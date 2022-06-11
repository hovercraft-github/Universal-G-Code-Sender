package com.willwinder.ugs.platform.backlashmeter;

import com.swingstwo.tablemodel.RecordListBase;
import com.swingstwo.tablemodel.TableRecord;
import com.willwinder.universalgcodesender.Utils;
import com.willwinder.universalgcodesender.listeners.ControllerState;
import com.willwinder.universalgcodesender.listeners.UGSEventListener;
import com.willwinder.universalgcodesender.model.*;
import com.willwinder.universalgcodesender.model.events.ControllerStateEvent;
import com.willwinder.universalgcodesender.model.events.ProbeEvent;

import javax.swing.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.abs;

public class BMService implements UGSEventListener {
    private static final Logger logger = Logger.getLogger(BMService.class.getName());
    private final BackendAPI backend;
    private Continuation continuation = null;
    private final Set<WaitForEvent> waitForEvents = new HashSet<>();
    private Position endPosition = null;
    private Position probePosition = null;
    private JobContext currentJobContext = null;
    private final OnMachineStateChanged onMachineStateChanged;
    private Timer gearPusher = null;

    public BMService(BackendAPI backend, OnMachineStateChanged onMachineStateChanged) {
        this.backend = backend;
        this.backend.addUGSEventListener(this);
        this.onMachineStateChanged = onMachineStateChanged;
    }

    private enum WaitForEvent {
        IDLE,
        PROBE
    }

    @FunctionalInterface
    public interface OnMachineStateChanged {
        void send(ControllerState newState);
    }

    @FunctionalInterface
    public interface JobSignal {
        void send(Integer stepLabel, boolean success, JobContext context);
    }

    public static class JobContext {
        public final RecordListBase commonSettings;
        public final RecordListBase axesSetting;
        public final RecordListBase job;
        public final JobSignal onStepDone;
        public final JobSignal onJobComplete;
        protected Iterator<TableRecord> it;
        public int stepNo = 0;
        private TableRecord lastTask = null;
        public Position machinePos = null;
        public Position forwardProbePosition = null;
        public Position backwardProbePosition = null;
        public Position wcsRefPoint = null;
        public NullablePosition backlash = null;
        public UnitUtils.Units units;
        public WorkCoordinateSystem wcs;
        public String msg = "";

        public JobContext(RecordListBase commonSettings, RecordListBase axesSetting, RecordListBase job,
                          JobSignal onStepDone, JobSignal onJobComplete) throws CloneNotSupportedException {
            this.commonSettings = (RecordListBase)commonSettings.clone();
            this.axesSetting = (RecordListBase)axesSetting.clone();
            this.job = (RecordListBase)job.clone();
            this.onStepDone = onStepDone;
            this.onJobComplete = onJobComplete;
            units = getUnits((Integer) commonSettings.getInternalCellValue(0, "units"));
            wcs = getWCS((Integer) commonSettings.getInternalCellValue(0, "work_coordinate"));
            it = job.iterator();
        }
    }

    public void startJob(JobContext context) {
        if (!backend.isIdle())
            throw new IllegalStateException(String.format("Machine is %s", backend.getControllerState().name()));
        //backend.addUGSEventListener(this);
        continuation = null;
        waitForEvents.clear();
        context.lastTask = null;
        context.wcsRefPoint = new Position(context.units);
        context.forwardProbePosition = new Position(context.units);
        context.backwardProbePosition = new Position(context.units);
        context.backlash = new NullablePosition(context.units);
        currentJobContext = context;
        jobStep(currentJobContext);
    }

    public void finishJob(boolean success, String msg) {
        //backend.removeUGSEventListener(this);
        continuation = null;
        waitForEvents.clear();
        if (currentJobContext != null) {
            currentJobContext.msg = msg;
            currentJobContext.onJobComplete.send(null, success, currentJobContext);
        }
        currentJobContext = null;
    }

    protected void jobStep(JobContext context) {
        try {
            if (context.lastTask != null) {
                if (gearPusher != null)
                    gearPusher.stop();
                context.stepNo += 1;
                context.machinePos = endPosition;
                /*Do a known things after certain machine actions on the application side:*/
                String taskName = (String)context.lastTask.getFieldValue("action_name");
                if (taskName.matches("probe_[a-zA-Z]+_pass_2_forward")) {
                    String axis = taskName.replaceAll("[^_]*_([a-zA-Z]+)_.*", "$1").toUpperCase();
                    double val = probePosition.get(Axis.valueOf(axis));
                    context.forwardProbePosition.set(Axis.valueOf(axis), val);
                } else if (taskName.matches("probe_[a-zA-Z]+_pass_2_backward")) {
                    String axis = taskName.replaceAll("[^_]*_([a-zA-Z]+)_.*", "$1").toUpperCase();
                    double val = probePosition.get(Axis.valueOf(axis));
                    context.backwardProbePosition.set(Axis.valueOf(axis), val);
                    double backlash = abs(context.backwardProbePosition.get(Axis.valueOf(axis))
                            - context.forwardProbePosition.get(Axis.valueOf(axis)));
                    context.backlash.set(Axis.valueOf(axis), backlash);
                } else if (taskName.matches("reset_wcs_.*")) {
                    String axis = taskName.replaceAll("reset_wcs_([a-zA-Z]+).*", "$1").toUpperCase();
                    double offset = (Double) getAxisSettings(context.axesSetting, axis).getFieldValue("surf_offset");
                    //double pos = context.machinePos.get(Axis.valueOf(axis));
                    double pos = backend.getMachinePosition().getPositionIn(context.units).get(Axis.valueOf(axis));
                    double probedPos = context.forwardProbePosition.get(Axis.valueOf(axis));
                    double target = pos - probedPos - offset;
                    updateWCS(context.wcs, axis, target);
                    context.wcsRefPoint.set(Axis.valueOf(axis), pos);
                }
                context.msg = "";
                context.onStepDone.send(
                        (Integer) context.lastTask.getFieldValue("action_order"),
                        true, context);
            }
            if (!context.it.hasNext()) {
                finishJob(true, "Job complete");
                return;
            }
            context.lastTask = context.it.next();
            continuation = () -> jobStep(context);
            String gCode = (String)context.lastTask.getFieldValue("g_code");
            String taskName = (String)context.lastTask.getFieldValue("action_name");
            if (taskName.matches("probe_[a-zA-Z]+_pass_.*")) {
                waitForEvents.add(WaitForEvent.PROBE);
            }
            if ((gCode != null) && (!gCode.isEmpty())) {
                backend.sendGcodeCommand(true, gCode);
                backend.getController().requestStatusReport();
            }
            waitForEvents.add(WaitForEvent.IDLE);
            if (gearPusher == null) {
                gearPusher = new Timer(200,
                        e -> UGSEvent(new ControllerStateEvent(ControllerState.IDLE, ControllerState.RUN)));
                gearPusher.setRepeats(false);
            }
            gearPusher.start();
        } catch (Exception e) {
            finishJob(false, e.getMessage());
        }
    }

    @FunctionalInterface
    private interface Continuation {
        void execute() throws Exception;
    }

    private void updateWCS(WorkCoordinateSystem wcs, String axis, double value) throws Exception {
        String gCode = String.format("G10 L20 P%d %s%s", wcs.getPValue(), axis, Utils.formatter.format(value));
        backend.sendGcodeCommand(true, gCode);
    }

    private static UnitUtils.Units getUnits(int unitId) {
        return unitId == 0 ? UnitUtils.Units.MM : UnitUtils.Units.INCH;
    }

    /**
     * Get WCS index, as it is stored in the database, and returns corresponding UGS enum type.
     * @param index selected item index in the GUI element (combo-box)
     * @return WorkCoordinateSystem enum value
     */
    private static WorkCoordinateSystem getWCS(int index) {
        return WorkCoordinateSystem.fromPValue(index + 1);
    }

    /**
     * Extracts specific axis settings from the table of axes.
     * @param axesSettings table of axes
     * @param axis axis name to extract settings
     * @return Collection of settings for specified axis
     */
    private static TableRecord getAxisSettings(RecordListBase axesSettings, String axis) {
        for (TableRecord axisRecord: axesSettings) {
            if (axisRecord.getFieldValue("axis").equals(axis))
                return axisRecord;
        }
        throw new NoSuchElementException(String.format("Axis %s settings not found", axis));
    }

    private static void calcMaxDistances(RecordListBase commonSettings, RecordListBase axesSettings) {
        double zDistance = (Double) commonSettings.getInternalCellValue(0, "xy_probe_at_z")
                - (Double) getAxisSettings(axesSettings, "Z").getFieldValue("start_pos");
        double xDistance = (Double) commonSettings.getInternalCellValue(0, "z_probe_at_x")
                - (Double) getAxisSettings(axesSettings, "X").getFieldValue("start_pos");
        double yDistance = (Double) commonSettings.getInternalCellValue(0, "z_probe_at_y")
                - (Double) getAxisSettings(axesSettings, "Y").getFieldValue("start_pos");
        getAxisSettings(axesSettings, "X").setFieldValue("distance", xDistance);
        getAxisSettings(axesSettings, "Y").setFieldValue("distance", yDistance);
        getAxisSettings(axesSettings, "Z").setFieldValue("distance", zDistance);
    }

    public boolean chevronOneEncode(RecordListBase commonSettings, RecordListBase axesSettings) {
        if (!backend.isIdle())
            return false;
        UnitUtils.Units units = getUnits((Integer) commonSettings.getInternalCellValue(0, "units"));
        Position machinePos = backend.getMachinePosition().getPositionIn(units);
        commonSettings.setInternalCellValue(machinePos.x, 0, "z_probe_at_x");
        commonSettings.setInternalCellValue(machinePos.y, 0, "z_probe_at_y");
        getAxisSettings(axesSettings, "Z").setFieldValue("start_pos", machinePos.z);
        calcMaxDistances(commonSettings, axesSettings);
        return true;
    }

    public boolean chevronTwoEncode(RecordListBase commonSettings, RecordListBase axesSettings) {
        if (!backend.isIdle())
            return false;
        UnitUtils.Units units = getUnits((Integer) commonSettings.getInternalCellValue(0, "units"));
        Position machinePos = backend.getMachinePosition().getPositionIn(units);
        commonSettings.setInternalCellValue(machinePos.z, 0, "xy_probe_at_z");
        getAxisSettings(axesSettings, "X").setFieldValue("start_pos", machinePos.x);
        calcMaxDistances(commonSettings, axesSettings);
        return true;
    }

    /*https://stargate.fandom.com/wiki/Chevron*/
    public boolean chevronThreeEncode(RecordListBase commonSettings, RecordListBase axesSettings) {
        if (!backend.isIdle())
            return false;
        UnitUtils.Units units = getUnits((Integer) commonSettings.getInternalCellValue(0, "units"));
        Position machinePos = backend.getMachinePosition().getPositionIn(units);
        commonSettings.setInternalCellValue(machinePos.z, 0, "xy_probe_at_z");
        getAxisSettings(axesSettings, "Y").setFieldValue("start_pos", machinePos.y);
        calcMaxDistances(commonSettings, axesSettings);
        return true;
    }

    @Override
    public void UGSEvent(UGSEvent evt) {
        //if (waitForEvents.isEmpty()) return;
        if (evt instanceof ControllerStateEvent) {
            ControllerState state = backend.getControllerState();
                    //((ControllerStateEvent) evt).getState();
            if (onMachineStateChanged != null)
                onMachineStateChanged.send(state);
            if (state == ControllerState.DISCONNECTED) {
                finishJob(false, ControllerState.DISCONNECTED.name());
            } else if (state == ControllerState.RUN) {
                if (gearPusher != null)
                    gearPusher.stop();
            } else if (state == ControllerState.IDLE) {
                endPosition = this.backend.getMachinePosition();
                if (waitForEvents.contains(WaitForEvent.IDLE)) {
                    waitForEvents.remove(WaitForEvent.IDLE);
                    try {
                        if ((continuation != null) && waitForEvents.isEmpty())
                            continuation.execute();
                    } catch (Exception e) {
                        logger.log(Level.SEVERE,
                                "Failed to proceed after " + WaitForEvent.IDLE + " state.", e);
                        finishJob(false, e.getMessage());
                    }
                }
            }
        } else if (evt instanceof ProbeEvent) {
            if (waitForEvents.contains(WaitForEvent.PROBE)) {
                probePosition = ((ProbeEvent)evt).getProbePosition();
                waitForEvents.remove(WaitForEvent.PROBE);
                try {
                    if ((continuation != null) && waitForEvents.isEmpty())
                        continuation.execute();
                } catch (Exception e) {
                    logger.log(Level.SEVERE,
                            "Failed to proceed after " + WaitForEvent.PROBE + " state.", e);
                    finishJob(false, e.getMessage());
                }
            }
        }
    }
}
