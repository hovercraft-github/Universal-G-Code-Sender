/*
 * Copyright (C) 2022 al
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.willwinder.ugs.platform.backlashmeter;

import static com.willwinder.universalgcodesender.model.WorkCoordinateSystem.G54;
import static com.willwinder.universalgcodesender.model.WorkCoordinateSystem.G55;
import static com.willwinder.universalgcodesender.model.WorkCoordinateSystem.G56;
import static com.willwinder.universalgcodesender.model.WorkCoordinateSystem.G57;
import static com.willwinder.universalgcodesender.model.WorkCoordinateSystem.G58;
import static com.willwinder.universalgcodesender.model.WorkCoordinateSystem.G59;

import com.swingstwo.tablemodel.SingleRowTableModel;
import com.swingstwo.tablemodel.SortableTableModel;
import com.swingstwo.uielements.TableHeaderWithHints;
import com.swingstwo.uielements.NumSpinnerCellEditor;
import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.universalgcodesender.Utils;
import com.willwinder.universalgcodesender.listeners.UGSEventListener;
import com.willwinder.universalgcodesender.model.Axis;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.model.UGSEvent;
import com.willwinder.universalgcodesender.model.WorkCoordinateSystem;
import com.willwinder.ugs.nbp.core.actions.HomingAction;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.modules.Places;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//com.willwinder.ugs.platform.backlashmeter//BM//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "BMTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Window", id = "com.willwinder.ugs.platform.backlashmeter.BMTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_BMAction",
        preferredID = "BMTopComponent"
)
@Messages({
    "CTL_BMAction=Backlash Meter",
    "CTL_BMTopComponent=Backlash Meter",
    "HINT_BMTopComponent=Backlash Meter"
})
public final class BMTopComponent extends TopComponent
        implements ActionListener, TableModelListener, ChangeListener, ItemListener, UGSEventListener {

    private static final Logger logger = Logger.getLogger(BMTopComponent.class.getName());
    private String m_machineId = "1";
    private Connection m_con;
    private final File m_userDir = Places.getUserDirectory();
    private SortableTableModel tblAxesModel = null;
    private SingleRowTableModel commonSettingsModel = null;
    private SortableTableModel tblActionsModel = null;
    private SortableTableModel tblResultsModel = null;
    private final BackendAPI backend = CentralLookup.getDefault().lookup(BackendAPI.class);
    private BMService service;

    public BMTopComponent() {
        initComponents();
        setName(Bundle.CTL_BMTopComponent());
        setToolTipText(Bundle.HINT_BMTopComponent());
        try {
            service = new BMService(backend, (state) -> {
                machineState.setText(Utils.getControllerStateText(backend.getControllerState()));
                machineState.setBackground(Utils.getControllerStateBackgroundColor(backend.getControllerState()));
            });
            String dbUrl = String.format("jdbc:hsqldb:%svar/db/machine_data/machine_data;shutdown=true",
                    m_userDir.getCanonicalFile().toURI());
            m_con = DriverManager.getConnection(
                    dbUrl, "SA", "");
            m_con.setAutoCommit(true);
            updateControls();
            backend.addUGSEventListener(this);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jtp = new javax.swing.JTabbedPane();
        settings = new javax.swing.JPanel();
        settingsUnits = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        settingsWorkCoordinate = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        settingsProbeDiameter = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        settingsSlowMeasureRate = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        settingsFastFindRate = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        settingsRetractAmount = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        settingsHint = new javax.swing.JTextArea();
        settingsNextButton = new javax.swing.JButton();
        leadUp = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        leadUpHint = new javax.swing.JTextArea();
        leadUpPrevButton = new javax.swing.JButton();
        leadUpNextButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        leadUpResetOffsets = new javax.swing.JCheckBox();
        doLeadUpButton = new javax.swing.JButton();
        leadUpZProbeAtX = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        leadUpZProbeAtY = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        leadUpXYProbeAtZ = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        chevron1 = new javax.swing.JToggleButton();
        chevron2 = new javax.swing.JToggleButton();
        chevron3 = new javax.swing.JToggleButton();
        selectAxes = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        selectAxesHint = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblAxes = new javax.swing.JTable();
        axesPrevButton = new javax.swing.JButton();
        axesNextButton = new javax.swing.JButton();
        probe = new javax.swing.JPanel();
        probePrevButton = new javax.swing.JButton();
        probeNextButton = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblActions = new javax.swing.JTable();
        probeButton = new javax.swing.JToggleButton();
        machineState = new javax.swing.JLabel();
        results = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblResults = new javax.swing.JTable();
        resultsPrevButton = new javax.swing.JButton();
        resultsNextButton = new javax.swing.JButton();

        setName("Form"); // NOI18N

        jtp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jtp.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jtp.setName("jtp"); // NOI18N
        jtp.addChangeListener(this);

        settings.setName("settings"); // NOI18N

        settingsUnits.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Millimeters", "Inches", " " }));
        settingsUnits.setName("settingsUnits"); // NOI18N
        settingsUnits.addActionListener(this);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        settingsWorkCoordinate.setModel(new DefaultComboBoxModel(new WorkCoordinateSystem[]{G54, G55, G56, G57, G58, G59}));
        settingsWorkCoordinate.setName("settingsWorkCoordinate"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        settingsProbeDiameter.setModel(new javax.swing.SpinnerNumberModel(2.0d, 0.0d, 100.0d, 0.1d));
        settingsProbeDiameter.setToolTipText(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.settingsProbeDiameter.toolTipText")); // NOI18N
        settingsProbeDiameter.setName("settingsProbeDiameter"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        settingsSlowMeasureRate.setModel(new javax.swing.SpinnerNumberModel(100.0d, 1.0d, 1000.0d, 1.0d));
        settingsSlowMeasureRate.setToolTipText(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.settingsSlowMeasureRate.toolTipText")); // NOI18N
        settingsSlowMeasureRate.setName("settingsSlowMeasureRate"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        settingsFastFindRate.setModel(new javax.swing.SpinnerNumberModel(250.0d, 1.0d, 1000.0d, 1.0d));
        settingsFastFindRate.setToolTipText(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.settingsFastFindRate.toolTipText")); // NOI18N
        settingsFastFindRate.setName("settingsFastFindRate"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        settingsRetractAmount.setModel(new javax.swing.SpinnerNumberModel(1.0d, 0.1d, 1000.0d, 0.1d));
        settingsRetractAmount.setToolTipText(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.settingsRetractAmount.toolTipText")); // NOI18N
        settingsRetractAmount.setName("settingsRetractAmount"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jScrollPane1.setHorizontalScrollBar(null);
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        settingsHint.setEditable(false);
        settingsHint.setColumns(20);
        settingsHint.setLineWrap(true);
        settingsHint.setRows(10);
        settingsHint.setText(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.settingsHint.text")); // NOI18N
        settingsHint.setWrapStyleWord(true);
        settingsHint.setName("settingsHint"); // NOI18N
        jScrollPane1.setViewportView(settingsHint);

        org.openide.awt.Mnemonics.setLocalizedText(settingsNextButton, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.settingsNextButton.text")); // NOI18N
        settingsNextButton.setName("settingsNextButton"); // NOI18N
        settingsNextButton.addActionListener(this);

        javax.swing.GroupLayout settingsLayout = new javax.swing.GroupLayout(settings);
        settings.setLayout(settingsLayout);
        settingsLayout.setHorizontalGroup(
            settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(settingsLayout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addGap(334, 334, 334))
                    .addGroup(settingsLayout.createSequentialGroup()
                        .addGroup(settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(settingsWorkCoordinate, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(settingsUnits, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(37, 37, 37)
                        .addGroup(settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(settingsSlowMeasureRate)
                            .addComponent(settingsProbeDiameter))
                        .addGap(28, 28, 28)
                        .addGroup(settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(settingsFastFindRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(settingsRetractAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(207, Short.MAX_VALUE))))
            .addGroup(settingsLayout.createSequentialGroup()
                .addGap(99, 99, 99)
                .addComponent(settingsNextButton)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        settingsLayout.setVerticalGroup(
            settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(settingsUnits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(settingsProbeDiameter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(settingsFastFindRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(settingsWorkCoordinate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(settingsSlowMeasureRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(settingsRetractAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(settingsNextButton)
                .addContainerGap())
        );

        jtp.addTab(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.settings.TabConstraints.tabTitle"), settings); // NOI18N

        leadUp.setName("leadUp"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        leadUpHint.setEditable(false);
        leadUpHint.setColumns(20);
        leadUpHint.setLineWrap(true);
        leadUpHint.setRows(5);
        leadUpHint.setText(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.leadUpHint.text")); // NOI18N
        leadUpHint.setWrapStyleWord(true);
        leadUpHint.setName("leadUpHint"); // NOI18N
        jScrollPane2.setViewportView(leadUpHint);

        org.openide.awt.Mnemonics.setLocalizedText(leadUpPrevButton, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.leadUpPrevButton.text")); // NOI18N
        leadUpPrevButton.setToolTipText(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.leadUpPrevButton.toolTipText")); // NOI18N
        leadUpPrevButton.setName("leadUpPrevButton"); // NOI18N
        leadUpPrevButton.addActionListener(this);

        leadUpNextButton.setLabel(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.leadUpNextButton.label")); // NOI18N
        leadUpNextButton.setName("leadUpNextButton"); // NOI18N
        leadUpNextButton.addActionListener(this);

        jButton1.setAction(new HomingAction());
        jButton1.setName("jButton1"); // NOI18N

        leadUpResetOffsets.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(leadUpResetOffsets, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.leadUpResetOffsets.text")); // NOI18N
        leadUpResetOffsets.setName("leadUpResetOffsets"); // NOI18N
        leadUpResetOffsets.addActionListener(this);

        doLeadUpButton.setLabel(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.doLeadUpButton.label")); // NOI18N
        doLeadUpButton.setName("doLeadUpButton"); // NOI18N
        doLeadUpButton.addActionListener(this);

        leadUpZProbeAtX.setModel(new javax.swing.SpinnerNumberModel(0.0d, null, null, 1.0d));
        leadUpZProbeAtX.setToolTipText(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.leadUpZProbeAtX.toolTipText")); // NOI18N
        leadUpZProbeAtX.setName("leadUpZProbeAtX"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        leadUpZProbeAtY.setModel(new javax.swing.SpinnerNumberModel(0.0d, null, null, 1.0d));
        leadUpZProbeAtY.setToolTipText(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.leadUpZProbeAtY.toolTipText")); // NOI18N
        leadUpZProbeAtY.setName("leadUpZProbeAtY"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        leadUpXYProbeAtZ.setModel(new javax.swing.SpinnerNumberModel(0.0d, null, null, 1.0d));
        leadUpXYProbeAtZ.setToolTipText(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.leadUpXYProbeAtZ.toolTipText")); // NOI18N
        leadUpXYProbeAtZ.setName("leadUpXYProbeAtZ"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(chevron1, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.chevron1.text")); // NOI18N
        chevron1.setName("chevron1"); // NOI18N
        chevron1.addItemListener(this);

        org.openide.awt.Mnemonics.setLocalizedText(chevron2, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.chevron2.text")); // NOI18N
        chevron2.setName("chevron2"); // NOI18N
        chevron2.addItemListener(this);

        org.openide.awt.Mnemonics.setLocalizedText(chevron3, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.chevron3.text")); // NOI18N
        chevron3.setName("chevron3"); // NOI18N
        chevron3.addItemListener(this);

        javax.swing.GroupLayout leadUpLayout = new javax.swing.GroupLayout(leadUp);
        leadUp.setLayout(leadUpLayout);
        leadUpLayout.setHorizontalGroup(
            leadUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leadUpLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(leadUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(leadUpLayout.createSequentialGroup()
                        .addComponent(leadUpPrevButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(leadUpNextButton))
                    .addGroup(leadUpLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(leadUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(leadUpResetOffsets)
                            .addGroup(leadUpLayout.createSequentialGroup()
                                .addGroup(leadUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(leadUpZProbeAtY)
                                    .addComponent(leadUpZProbeAtX, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(leadUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8)))
                            .addGroup(leadUpLayout.createSequentialGroup()
                                .addComponent(leadUpXYProbeAtZ, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9))
                            .addGroup(leadUpLayout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(doLeadUpButton)
                                .addGap(61, 61, 61)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(leadUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chevron3)
                            .addComponent(chevron2)
                            .addComponent(chevron1))))
                .addContainerGap(130, Short.MAX_VALUE))
        );
        leadUpLayout.setVerticalGroup(
            leadUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leadUpLayout.createSequentialGroup()
                .addGroup(leadUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(leadUpLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(leadUpResetOffsets)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(leadUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(doLeadUpButton)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(leadUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(leadUpZProbeAtX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(chevron1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(leadUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(leadUpZProbeAtY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(chevron2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(leadUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(leadUpXYProbeAtZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(chevron3)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(leadUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(leadUpPrevButton)
                    .addComponent(leadUpNextButton))
                .addContainerGap())
        );

        jtp.addTab(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.leadUp.TabConstraints.tabTitle"), leadUp); // NOI18N

        selectAxes.setName("selectAxes"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        selectAxesHint.setEditable(false);
        selectAxesHint.setColumns(20);
        selectAxesHint.setLineWrap(true);
        selectAxesHint.setRows(5);
        selectAxesHint.setText(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.selectAxesHint.text")); // NOI18N
        selectAxesHint.setWrapStyleWord(true);
        selectAxesHint.setName("selectAxesHint"); // NOI18N
        jScrollPane3.setViewportView(selectAxesHint);

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        tblAxes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"X",  new Boolean(true), null,  new Double(0.0),  new Double(-30.0),  new Double(2.0)},
                {"Y",  new Boolean(true), null,  new Double(0.0),  new Double(-30.0),  new Double(2.0)},
                {"Z",  new Boolean(true),  new Boolean(true),  new Double(0.0),  new Double(-30.0),  new Double(2.0)}
            },
            new String [] {
                "Axis", "Probe", "Zero to probed", "Start (machine coord)", "Dir/Distance", "Surf.offset"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblAxes.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblAxes.setName("tblAxes"); // NOI18N
        tblAxes.setRowSelectionAllowed(false);
        tblAxes.setShowGrid(true);
        tblAxes.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tblAxes);
        if (tblAxes.getColumnModel().getColumnCount() > 0) {
            tblAxes.getColumnModel().getColumn(0).setPreferredWidth(10);
            tblAxes.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.tblAxes.columnModel.title0")); // NOI18N
            tblAxes.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.tblAxes.columnModel.title1")); // NOI18N
            tblAxes.getColumnModel().getColumn(2).setHeaderValue(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.tblAxes.columnModel.title2")); // NOI18N
            tblAxes.getColumnModel().getColumn(3).setHeaderValue(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.tblAxes.columnModel.title5")); // NOI18N
            tblAxes.getColumnModel().getColumn(4).setHeaderValue(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.tblAxes.columnModel.title3")); // NOI18N
            tblAxes.getColumnModel().getColumn(5).setHeaderValue(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.tblAxes.columnModel.title4")); // NOI18N
        }

        axesPrevButton.setLabel(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.axesPrevButton.label")); // NOI18N
        axesPrevButton.setName("axesPrevButton"); // NOI18N
        axesPrevButton.addActionListener(this);

        axesNextButton.setLabel(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.axesNextButton.label")); // NOI18N
        axesNextButton.setName("axesNextButton"); // NOI18N
        axesNextButton.addActionListener(this);

        javax.swing.GroupLayout selectAxesLayout = new javax.swing.GroupLayout(selectAxes);
        selectAxes.setLayout(selectAxesLayout);
        selectAxesLayout.setHorizontalGroup(
            selectAxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(selectAxesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(selectAxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(selectAxesLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(selectAxesLayout.createSequentialGroup()
                        .addComponent(axesPrevButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(axesNextButton)))
                .addContainerGap(175, Short.MAX_VALUE))
        );
        selectAxesLayout.setVerticalGroup(
            selectAxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(selectAxesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(selectAxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(selectAxesLayout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 80, Short.MAX_VALUE))
                    .addComponent(jScrollPane3))
                .addGap(18, 18, 18)
                .addGroup(selectAxesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(axesPrevButton)
                    .addComponent(axesNextButton))
                .addContainerGap())
        );

        jtp.addTab(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.selectAxes.TabConstraints.tabTitle"), selectAxes); // NOI18N

        probe.setName("probe"); // NOI18N

        probePrevButton.setLabel(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.probePrevButton.label")); // NOI18N
        probePrevButton.setName("probePrevButton"); // NOI18N
        probePrevButton.addActionListener(this);

        org.openide.awt.Mnemonics.setLocalizedText(probeNextButton, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.probeNextButton.text_1")); // NOI18N
        probeNextButton.setName("probeNextButton"); // NOI18N
        probeNextButton.addActionListener(this);

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        tblActions.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                { new Integer(150), "G21 G54", "Set units to mm and User Work Coordinate System to G54"}
            },
            new String [] {
                "Label", "G-code", "Description"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblActions.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblActions.setName("tblActions"); // NOI18N
        tblActions.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblActions.setShowGrid(true);
        tblActions.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(tblActions);
        tblActions.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tblActions.getColumnModel().getColumnCount() > 0) {
            tblActions.getColumnModel().getColumn(0).setPreferredWidth(50);
            tblActions.getColumnModel().getColumn(0).setMaxWidth(50);
            tblActions.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.tblActions.columnModel.title0")); // NOI18N
            tblActions.getColumnModel().getColumn(1).setPreferredWidth(250);
            tblActions.getColumnModel().getColumn(1).setMaxWidth(250);
            tblActions.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.tblActions.columnModel.title1")); // NOI18N
            tblActions.getColumnModel().getColumn(2).setHeaderValue(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.tblActions.columnModel.title2")); // NOI18N
        }

        org.openide.awt.Mnemonics.setLocalizedText(probeButton, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.probeButton.text")); // NOI18N
        probeButton.setName("probeButton"); // NOI18N
        probeButton.addItemListener(this);

        org.openide.awt.Mnemonics.setLocalizedText(machineState, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.machineState.text")); // NOI18N
        machineState.setToolTipText(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.machineState.toolTipText")); // NOI18N
        machineState.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        machineState.setName("machineState"); // NOI18N

        javax.swing.GroupLayout probeLayout = new javax.swing.GroupLayout(probe);
        probe.setLayout(probeLayout);
        probeLayout.setHorizontalGroup(
            probeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(probeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(probeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 894, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(probeLayout.createSequentialGroup()
                        .addComponent(probePrevButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(probeNextButton)
                        .addGap(66, 66, 66)
                        .addComponent(probeButton)
                        .addGap(79, 79, 79)
                        .addComponent(machineState)))
                .addContainerGap(160, Short.MAX_VALUE))
        );
        probeLayout.setVerticalGroup(
            probeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, probeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(probeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(probePrevButton)
                    .addComponent(probeNextButton)
                    .addComponent(probeButton)
                    .addComponent(machineState))
                .addContainerGap())
        );

        jtp.addTab(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.probe.TabConstraints.tabTitle"), probe); // NOI18N

        results.setName("results"); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        tblResults.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblResults.setName("tblResults"); // NOI18N
        jScrollPane5.setViewportView(tblResults);
        if (tblResults.getColumnModel().getColumnCount() > 0) {
            tblResults.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.tblResults.columnModel.title0")); // NOI18N
            tblResults.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.tblResults.columnModel.title1")); // NOI18N
            tblResults.getColumnModel().getColumn(2).setHeaderValue(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.tblResults.columnModel.title2")); // NOI18N
            tblResults.getColumnModel().getColumn(3).setHeaderValue(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.tblResults.columnModel.title3")); // NOI18N
        }

        org.openide.awt.Mnemonics.setLocalizedText(resultsPrevButton, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.resultsPrevButton.text_1")); // NOI18N
        resultsPrevButton.setName("resultsPrevButton"); // NOI18N
        resultsPrevButton.addActionListener(this);

        org.openide.awt.Mnemonics.setLocalizedText(resultsNextButton, org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.resultsNextButton.text_1")); // NOI18N
        resultsNextButton.setName("resultsNextButton"); // NOI18N

        javax.swing.GroupLayout resultsLayout = new javax.swing.GroupLayout(results);
        results.setLayout(resultsLayout);
        resultsLayout.setHorizontalGroup(
            resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 830, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(resultsLayout.createSequentialGroup()
                        .addComponent(resultsPrevButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(resultsNextButton)))
                .addContainerGap(224, Short.MAX_VALUE))
        );
        resultsLayout.setVerticalGroup(
            resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addGroup(resultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resultsPrevButton)
                    .addComponent(resultsNextButton))
                .addContainerGap())
        );

        jtp.addTab(org.openide.util.NbBundle.getMessage(BMTopComponent.class, "BMTopComponent.results.TabConstraints.tabTitle"), results); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jtp)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jtp)
                .addContainerGap())
        );
    }

    // Code for dispatching events from components to event handlers.

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == settingsUnits) {
            BMTopComponent.this.settingsUnitsActionPerformed(evt);
        }
        else if (evt.getSource() == settingsNextButton) {
            BMTopComponent.this.settingsNextButtonActionPerformed(evt);
        }
        else if (evt.getSource() == leadUpPrevButton) {
            BMTopComponent.this.leadUpPrevButtonActionPerformed(evt);
        }
        else if (evt.getSource() == leadUpNextButton) {
            BMTopComponent.this.leadUpNextButtonActionPerformed(evt);
        }
        else if (evt.getSource() == leadUpResetOffsets) {
            BMTopComponent.this.leadUpResetOffsetsActionPerformed(evt);
        }
        else if (evt.getSource() == doLeadUpButton) {
            BMTopComponent.this.doLeadUpButtonActionPerformed(evt);
        }
        else if (evt.getSource() == axesPrevButton) {
            BMTopComponent.this.axesPrevButtonActionPerformed(evt);
        }
        else if (evt.getSource() == axesNextButton) {
            BMTopComponent.this.axesNextButtonActionPerformed(evt);
        }
        else if (evt.getSource() == probePrevButton) {
            BMTopComponent.this.probePrevButtonActionPerformed(evt);
        }
        else if (evt.getSource() == probeNextButton) {
            BMTopComponent.this.probeNextButtonActionPerformed(evt);
        }
        else if (evt.getSource() == resultsPrevButton) {
            BMTopComponent.this.resultsPrevButtonActionPerformed(evt);
        }
    }

    public void itemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getSource() == chevron1) {
            BMTopComponent.this.chevron1ItemStateChanged(evt);
        }
        else if (evt.getSource() == chevron2) {
            BMTopComponent.this.chevron2ItemStateChanged(evt);
        }
        else if (evt.getSource() == chevron3) {
            BMTopComponent.this.chevron3ItemStateChanged(evt);
        }
        else if (evt.getSource() == probeButton) {
            BMTopComponent.this.probeButtonItemStateChanged(evt);
        }
    }

    public void stateChanged(javax.swing.event.ChangeEvent evt) {
        if (evt.getSource() == jtp) {
            BMTopComponent.this.jtpStateChanged(evt);
        }
    }// </editor-fold>//GEN-END:initComponents

    private void settingsUnitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsUnitsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_settingsUnitsActionPerformed

    private void settingsNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsNextButtonActionPerformed
        jtp.setSelectedIndex(jtp.getSelectedIndex() + 1);
    }//GEN-LAST:event_settingsNextButtonActionPerformed

    private void leadUpPrevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leadUpPrevButtonActionPerformed
        jtp.setSelectedIndex(jtp.getSelectedIndex() - 1);
    }//GEN-LAST:event_leadUpPrevButtonActionPerformed

    private void leadUpNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leadUpNextButtonActionPerformed
        jtp.setSelectedIndex(jtp.getSelectedIndex() + 1);
    }//GEN-LAST:event_leadUpNextButtonActionPerformed

    private void axesPrevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_axesPrevButtonActionPerformed
        jtp.setSelectedIndex(jtp.getSelectedIndex() - 1);
    }//GEN-LAST:event_axesPrevButtonActionPerformed

    private void axesNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_axesNextButtonActionPerformed
        jtp.setSelectedIndex(jtp.getSelectedIndex() + 1);
    }//GEN-LAST:event_axesNextButtonActionPerformed

    private void leadUpResetOffsetsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leadUpResetOffsetsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_leadUpResetOffsetsActionPerformed

    private void probePrevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_probePrevButtonActionPerformed
        jtp.setSelectedIndex(jtp.getSelectedIndex() - 1);
    }//GEN-LAST:event_probePrevButtonActionPerformed

    private void probeNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_probeNextButtonActionPerformed
        jtp.setSelectedIndex(jtp.getSelectedIndex() + 1);
    }//GEN-LAST:event_probeNextButtonActionPerformed

    private void resultsPrevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultsPrevButtonActionPerformed
        jtp.setSelectedIndex(jtp.getSelectedIndex() - 1);
    }//GEN-LAST:event_resultsPrevButtonActionPerformed

    private void doLeadUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doLeadUpButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_doLeadUpButtonActionPerformed

    private void jtpStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jtpStateChanged
        String tabTitle = jtp.getTitleAt(jtp.getSelectedIndex());
        try {
            switch (tabTitle) {
                case "Probe":
                    tblActionsPopulate();
                    break;
                case "Lead-up":
                    SwingUtilities.invokeLater(() -> {
                        chevron1.setSelected(false);
                        chevron2.setSelected(false);
                        chevron3.setSelected(false);
                    });
                    break;
                case "Results":
                    tblResultsPopulate();
                    break;
            }
        } catch (Exception e) {
            String msg = String.format("When switching to tab %s:", tabTitle);
            logger.log(Level.SEVERE, msg, e);
            JOptionPane.showMessageDialog(null,
                    String.format("%s %s", msg, e.getMessage()));
        }
    }//GEN-LAST:event_jtpStateChanged

    private void chevron1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chevron1ItemStateChanged
        int state = evt.getStateChange();
        if (state == ItemEvent.SELECTED) {
            if (service.chevronOneEncode(commonSettingsModel.getRecordList(), tblAxesModel.getRecordList())
                    && storeAllChevronData()) {
                try {
                    commonSettingsPopulate();
                    tblAxesPopulate();
                } catch (Exception e) {
                    logger.log(Level.SEVERE,"chevron1ItemStateChanged: ", e);
                    JOptionPane.showMessageDialog(null,
                            String.format("Chevron operation failed: %s", e.getMessage()));
                }
            } else {
                /*System.out.print("\007");*/
                SwingUtilities.invokeLater(() -> chevron1.setSelected(false));
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }//GEN-LAST:event_chevron1ItemStateChanged

    private void chevron2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chevron2ItemStateChanged
        int state = evt.getStateChange();
        if (state == ItemEvent.SELECTED) {
            if (service.chevronTwoEncode(commonSettingsModel.getRecordList(), tblAxesModel.getRecordList())
                    && storeAllChevronData()) {
                try {
                    commonSettingsPopulate();
                    tblAxesPopulate();
                } catch (Exception e) {
                    logger.log(Level.SEVERE,"chevron2ItemStateChanged: ", e);
                    JOptionPane.showMessageDialog(null,
                            String.format("Chevron operation failed: %s", e.getMessage()));
                }
            } else {
                SwingUtilities.invokeLater(() -> chevron2.setSelected(false));
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }//GEN-LAST:event_chevron2ItemStateChanged

    private void chevron3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chevron3ItemStateChanged
        int state = evt.getStateChange();
        if (state == ItemEvent.SELECTED) {
            if (service.chevronThreeEncode(commonSettingsModel.getRecordList(), tblAxesModel.getRecordList())
                    && storeAllChevronData()) {
                try {
                    commonSettingsPopulate();
                    tblAxesPopulate();
                } catch (Exception e) {
                    logger.log(Level.SEVERE,"chevron3ItemStateChanged: ", e);
                    JOptionPane.showMessageDialog(null,
                            String.format("Chevron operation failed: %s", e.getMessage()));
                }
            } else {
                SwingUtilities.invokeLater(() -> chevron3.setSelected(false));
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }//GEN-LAST:event_chevron3ItemStateChanged

    private void probeButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_probeButtonItemStateChanged
        int state = evt.getStateChange();
        if (state == ItemEvent.SELECTED) {
            runBMJob();
        } else {
            if (jobInProgress) {
                service.finishJob(false, "Job killed");
                jobInProgress = false;
                SwingUtilities.invokeLater(() -> probeButton.setSelected(jobInProgress));
                logger.log(Level.SEVERE, "runBMJob hanged");
                JOptionPane.showMessageDialog(null,
                        "Looks like probe job hanged? Check the machine, " +
                                "then reconnect and repeat the homing cycle",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_probeButtonItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton axesNextButton;
    private javax.swing.JButton axesPrevButton;
    private javax.swing.JToggleButton chevron1;
    private javax.swing.JToggleButton chevron2;
    private javax.swing.JToggleButton chevron3;
    private javax.swing.JButton doLeadUpButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jtp;
    private javax.swing.JPanel leadUp;
    private javax.swing.JTextArea leadUpHint;
    private javax.swing.JButton leadUpNextButton;
    private javax.swing.JButton leadUpPrevButton;
    private javax.swing.JCheckBox leadUpResetOffsets;
    private javax.swing.JSpinner leadUpXYProbeAtZ;
    private javax.swing.JSpinner leadUpZProbeAtX;
    private javax.swing.JSpinner leadUpZProbeAtY;
    private javax.swing.JLabel machineState;
    private javax.swing.JPanel probe;
    private javax.swing.JToggleButton probeButton;
    private javax.swing.JButton probeNextButton;
    private javax.swing.JButton probePrevButton;
    private javax.swing.JPanel results;
    private javax.swing.JButton resultsNextButton;
    private javax.swing.JButton resultsPrevButton;
    private javax.swing.JPanel selectAxes;
    private javax.swing.JTextArea selectAxesHint;
    private javax.swing.JPanel settings;
    private javax.swing.JSpinner settingsFastFindRate;
    private javax.swing.JTextArea settingsHint;
    private javax.swing.JButton settingsNextButton;
    private javax.swing.JSpinner settingsProbeDiameter;
    private javax.swing.JSpinner settingsRetractAmount;
    private javax.swing.JSpinner settingsSlowMeasureRate;
    private javax.swing.JComboBox<String> settingsUnits;
    private javax.swing.JComboBox< com.willwinder.universalgcodesender.model.WorkCoordinateSystem> settingsWorkCoordinate;
    private javax.swing.JTable tblActions;
    private javax.swing.JTable tblAxes;
    private javax.swing.JTable tblResults;
    // End of variables declaration//GEN-END:variables

    @Override
    public void tableChanged(TableModelEvent e) {
        String sql;
        Object val;
        Object axis = null;
        int colIx = e.getColumn();
        try {
            if (e.getSource() == commonSettingsModel) {
                String colName = commonSettingsModel.getColumnShortName(colIx);
                val = commonSettingsModel.getInternalValue(0, colIx);
                if (val == null) {
                    commonSettingsPopulate();
                    return;
                }
                sql = String.format("update probe_common_settings set %s = ? where machine_id = '%s'",
                        colName, m_machineId);
            } else if (e.getSource() == tblAxesModel) {
                String colName = tblAxesModel.getColumnShortName(colIx);
                int rowIx = e.getFirstRow();
                val = tblAxesModel.getInternalValue(rowIx, colIx);
                if (val == null) {
                    tblAxesPopulate();
                    return;
                }
                axis = tblAxesModel.getInternalValue(rowIx, 0);
                sql = String.format("update probe_axes_settings set %s = ?\n" +
                                "where machine_id = '%s'\n" +
                                "   and axis = ?",
                        colName, m_machineId);
            } else {
                return;
            }
            PreparedStatement st = m_con.prepareStatement(sql);
            st.setObject(1, val);
            if (axis != null)
                st.setObject(2, axis);
            st.executeUpdate();
        } catch (Exception ex) {
            logger.log(Level.SEVERE,"tableChanged: ", ex);
            JOptionPane.showMessageDialog(null,
                    String.format("Unable to store settings: %s", ex.getMessage()));
        }
    }

    private boolean appendBacklashHistory(Double x, Double y, Double z) {
        String sql = "insert into backlash_history\n" +
                "(machine_id, x, y, z)\n" +
                " values (?, ?, ?, ?)";
        PreparedStatement st = null;
        boolean ret = false;
        try {
            st = m_con.prepareStatement(sql);
            st.setString(1, m_machineId);
            if (x != null) {
                st.setDouble(2, x);
            } else {
                st.setNull(2, Types.DOUBLE);
            }
            if (y != null) {
                st.setDouble(3, y);
            } else {
                st.setNull(3, Types.DOUBLE);
            }
            if (z != null) {
                st.setDouble(4, z);
            } else {
                st.setNull(4, Types.DOUBLE);
            }
            st.executeUpdate();
            ret = true;
        } catch (Exception e) {
            logger.log(Level.SEVERE,"appendBacklashHistory: ", e);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException e) {
                logger.warning(e.getMessage());
            }
        }
        return ret;
    }

    private boolean storeAllChevronData() {
        PreparedStatement st = null;
        boolean ret = false;
        try {
            m_con.setAutoCommit(false);
            String sql = "update probe_axes_settings set start_pos = ?, distance = ? \n" +
                    "where   machine_id = ? \n" +
                    "        and axis = ?";
            st = m_con.prepareStatement(sql);
            for (int row = 0; row < tblAxesModel.getRowCount(); ++row) {
                st.setObject(1, tblAxesModel.getInternalValue(row, "start_pos"));
                st.setObject(2, tblAxesModel.getInternalValue(row, "distance"));
                st.setString(3, m_machineId);
                st.setObject(4, tblAxesModel.getInternalValue(row, "axis"));
                if (st.executeUpdate() != 1)
                    throw new Exception(String.format("Failed to write into probe_axes_settings row %d", row));
            }
            sql = "update probe_common_settings set z_probe_at_x = ?, z_probe_at_y = ?, xy_probe_at_z = ? \n" +
                    "where   machine_id = ?";
            st = m_con.prepareStatement(sql);
            st.setObject(1, commonSettingsModel.getInternalValue(0, "z_probe_at_x"));
            st.setObject(2, commonSettingsModel.getInternalValue(0, "z_probe_at_y"));
            st.setObject(3, commonSettingsModel.getInternalValue(0, "xy_probe_at_z"));
            st.setString(4, m_machineId);
            if (st.executeUpdate() != 1)
                throw new Exception("Failed to write into probe_common_settings");
        } catch (Exception e) {
            logger.log(Level.SEVERE,"storeAllChevronData: ", e);
            JOptionPane.showMessageDialog(null,
                    String.format("Unable to lock the chevron: %s", e.getMessage()));
        } finally {
            try {
                m_con.commit();
                ret = true;
            } catch (SQLException e) {
                logger.warning(e.getMessage());
            }
            try {
                m_con.setAutoCommit(true);
                if (st != null) {
                    st.close();
                }
            } catch (SQLException e) {
                logger.warning(e.getMessage());
            }
        }
        return ret;
    }

    private void commonSettingsPopulate() throws Exception {
        if (commonSettingsModel == null) {
            commonSettingsModel = new SingleRowTableModel(m_con,
                    new Object[]{settingsWorkCoordinate, settingsUnits, settingsProbeDiameter,
                            settingsSlowMeasureRate, settingsFastFindRate, settingsRetractAmount,
                            leadUpResetOffsets, leadUpZProbeAtX, leadUpZProbeAtY, leadUpXYProbeAtZ},
                    new String[]{"work_coordinate", "units", "tool_diameter", "measure_rate",
                            "leadup_rate", "retract_amount", "reset_tool_offsets",
                            "z_probe_at_x", "z_probe_at_y", "xy_probe_at_z"}
            );
            commonSettingsModel.addTableModelListener(this);
        }
        commonSettingsModel.Populate(String.format("select work_coordinate, units, tool_diameter, " +
                "measure_rate, leadup_rate, retract_amount, reset_tool_offsets, " +
                "z_probe_at_x, z_probe_at_y, xy_probe_at_z\n" +
                "from   probe_common_settings\n" +
                "where  machine_id = '%s'", m_machineId));
    }

    private void tblAxesPopulate() throws Exception {
        if (tblAxesModel != null)
            tblAxesModel.removeTableModelListener(this);
        tblAxesModel = new SortableTableModel(m_con);
        tblAxesModel.Populate(String.format("select axis, probe, zero, start_pos, distance, surf_offset\n" +
                        "from probe_axes_settings\n" +
                        "where machine_id = '%s'\n" +
                        "order by axis", m_machineId),
                new Class<?>[] {String.class, Boolean.class, Boolean.class, Double.class, Double.class, Double.class},
                new String[] {"Axis", "Probe", "Zero to probed", "Start (machine coord)", "Dir/Distance", "Surf.offset"},
                new String[] {"axis", "probe", "zero", "start_pos", "distance", "surf_offset"},
                new int[] {0,1,2,3,4,5}, // Visible column indexes
                new int[] {1,2,3,4,5});  // Editable column indexes
        tblAxes.setModel(tblAxesModel);
        TableColumnModel colModel = tblAxes.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(50);
        colModel.getColumn(3).setCellEditor(new NumSpinnerCellEditor(1.0d, "#####,##0.00"));
        colModel.getColumn(4).setCellEditor(new NumSpinnerCellEditor(1.0d, "#####,##0.00"));
        colModel.getColumn(5).setCellEditor(new NumSpinnerCellEditor(0.1d, "#####,##0.00"));
        TableHeaderWithHints hdr = new TableHeaderWithHints(colModel, new String[]{null,"Measure the backlash",
                "Reset selected user coordinate system zero to the probed position",
                "Probe travel start position. Use machine coordinates!","Direction (the sign) and max probe travel",
                "Probed position offset caused by a target body size",});
        hdr.setResizingAllowed(true);
        tblAxes.setTableHeader(hdr);
        tblAxesModel.addTableModelListener(this);
    }

    private void tblActionsPopulate() throws Exception {
        if (tblActionsModel != null)
            tblActionsModel.removeTableModelListener(this);
        tblActionsModel = new SortableTableModel(m_con);
        String sql = ("select  PA.action_order, \n" +
                "   PA.action_name, UGS_SUBST_CONSTANTS(PA.g_code, '<machine_id>') g_code, \n" +
                "UGS_SUBST_CONSTANTS(PA.description , '<machine_id>') descr\n" +
                "from    probe_actions PA\n" +
                "where   POSITION_ARRAY(UGS_GET_PROBE_AXES('<machine_id>') in pa.PROBE_AXES) != 0\n" +
                "        and POSITION_ARRAY(UGS_GET_ZERO_AXES('<machine_id>') in pa.ZERO_AXES) != 0\n" +
                "order by pa.action_order").replace("<machine_id>", m_machineId);
        tblActionsModel.Populate(sql,
                new Class<?>[] {Integer.class, String.class, String.class, String.class},
                new String[] {"Label", "G-code", "Description"},
                new String[] {"action_order", "action_name", "g_code", "descr"},
                new int[] {0, 2, 3},
                new int[] {});
        tblActions.setModel(tblActionsModel);
        tblActions.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableColumnModel colModel = tblActions.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(50);
        colModel.getColumn(0).setMaxWidth(70);
        colModel.getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                cell.setForeground(Color.blue);
                this.setHorizontalAlignment(JLabel.TRAILING);
                return cell;
            }
        });
        colModel.getColumn(1).setPreferredWidth(250);
        colModel.getColumn(1).setMaxWidth(500);
        /*
        TableHeaderWithHints hdr = new TableHeaderWithHints(colModel, new String[]{"Line #",null,null,null});
        tblActions.setTableHeader(hdr);
        */
        tblActions.setColumnModel(colModel);
        tblActions.getTableHeader().setResizingAllowed(true);
        tblActionsModel.addTableModelListener(this);
    }

    private void tblResultsPopulate() throws Exception {
        tblResultsModel = new SortableTableModel(m_con);
        String sql = ("select  BH.probe_dt, BH.x, BH.y, BH.z\n" +
                "from    backlash_history BH\n" +
                "where   BH.machine_id = '<machine_id>'\n" +
                "order by BH.probe_dt desc").replace("<machine_id>", m_machineId);
        tblResultsModel.Populate(sql,
                new Class<?>[] {Timestamp.class, Double.class, Double.class, Double.class},
                new String[] {"Date/time", "X", "y", "Z"},
                new String[] {"probe_dt", "x", "y", "z"},
                new int[] {0, 1, 2, 3},
                new int[] {}
                );
        tblResults.setModel(tblResultsModel);
        tblResults.getTableHeader().setResizingAllowed(true);
        tblResults.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                if( value instanceof Date) {
                    value = f.format(value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
            }
        });
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
        try {
            backend.removeUGSEventListener(this);
            backend.removeUGSEventListener(service);
            m_con.close();
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
        try {
            Statement st = m_con.createStatement();
            st.executeUpdate("create table if not exists probe_common_settings (\n" +
                    "  machine_id varchar(256) not null primary key,\n" +
                    "  work_coordinate int not null,\n" +
                    "  units int not null,\n" +
                    "  tool_diameter decimal(10,3) not null,\n" +
                    "  measure_rate decimal(10,3) not null,\n" +
                    "  leadup_rate decimal(10,3) not null,\n" +
                    "  retract_amount decimal(10,3) not null,\n" +
                    "  reset_tool_offsets boolean not null,\n" +
                    "  z_probe_at_x decimal(10,3) not null,\n" +
                    "  z_probe_at_y decimal(10,3) not null,\n" +
                    "  xy_probe_at_z decimal(10,3) not null\n" +
                    ")");
            st.executeUpdate("create table if not exists probe_axes_settings (\n" +
                    "  machine_id varchar(256) not null ,\n" +
                    "  axis char(1) not null,\n" +
                    "  probe boolean not null,\n" +
                    "  zero boolean not null,\n" +
                    "  start_pos decimal(10,3) not null,\n" +
                    "  distance decimal(10,3) not null,\n" +
                    "  surf_offset decimal(10,3) not null,\n" +
                    "  constraint probe_axes_settings_pk primary key (machine_id, axis)\n" +
                    ")");
            st.executeUpdate("create table if not exists backlash_history (\n" +
                    "  machine_id varchar(256) not null, \n" +
                    "  probe_dt timestamp default localtimestamp,\n" +
                    "  x decimal(10,3) null,\n" +
                    "  y decimal(10,3) null,\n" +
                    "  z decimal(10,3) null,\n" +
                    "  constraint backlash_pk primary key (machine_id, probe_dt)\n" +
                    ")");
            ResultSet rs = st.executeQuery(String.format("select count(1) from probe_common_settings" +
                    " where machine_id = '%s'", m_machineId));
            int nRows = 0;
            if (rs.next()) {
                nRows = rs.getInt(1);
            }
            rs.close();
            if (nRows == 0) {
                String sql = String.format("insert into probe_common_settings\n" +
                        "(machine_id, work_coordinate, units, tool_diameter, measure_rate, " +
                        "leadup_rate, retract_amount, reset_tool_offsets, " +
                                "z_probe_at_x, z_probe_at_y, xy_probe_at_z)\n" +
                        "values('%s', %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                        m_machineId,
                        Integer.toString(settingsWorkCoordinate.getSelectedIndex()),
                        Integer.toString(settingsUnits.getSelectedIndex()),
                        Double.toString((double)settingsProbeDiameter.getModel().getValue()),
                        Double.toString((double)settingsSlowMeasureRate.getModel().getValue()),
                        Double.toString((double)settingsFastFindRate.getModel().getValue()),
                        Double.toString((double)settingsRetractAmount.getModel().getValue()),
                        Boolean.toString(leadUpResetOffsets.isSelected()),
                        Double.toString((double) leadUpZProbeAtX.getModel().getValue()),
                        Double.toString((double) leadUpZProbeAtY.getModel().getValue()),
                        Double.toString((double) leadUpXYProbeAtZ.getModel().getValue())
                        );
                st.executeUpdate(sql);
            }
            nRows = 0;
            rs = st.executeQuery(String.format("select count(1) from probe_axes_settings" +
                    " where machine_id = '%s'", m_machineId));
            if (rs.next()) {
                nRows = rs.getInt(1);
            }
            if (nRows < 3) {
                st.executeUpdate(String.format("delete from probe_axes_settings " +
                        " where machine_id = '%s'", m_machineId));
                PreparedStatement ps = m_con.prepareStatement("insert into probe_axes_settings\n" +
                        "(machine_id, axis, probe, zero, start_pos, distance, surf_offset)\n" +
                        "values(?, ?, ?, ?, ?, ?, ?)");
                ps.setString(1, m_machineId);
                ps.setString(2, "X");
                ps.setBoolean(3, false);
                ps.setBoolean(4, false);
                ps.setDouble(5, 0.0);
                ps.setDouble(6, 0.0);
                ps.setDouble(7, 1.0);
                ps.execute();
                ps.setString(1, m_machineId);
                ps.setString(2, "Y");
                ps.setBoolean(3, false);
                ps.setBoolean(4, false);
                ps.setDouble(5, 0.0);
                ps.setDouble(6, 0.0);
                ps.setDouble(7, 1.0);
                ps.execute();
                ps.setString(1, m_machineId);
                ps.setString(2, "Z");
                ps.setBoolean(3, true);
                ps.setBoolean(4, false);
                ps.setDouble(5, 0.0);
                ps.setDouble(6, 0.0);
                ps.setDouble(7, 1.0);
                ps.execute();
            }
            commonSettingsPopulate();
            tblAxesPopulate();
        } catch (Exception e) {
            logger.warning(e.toString());
        }
    }

    public void updateControls() {
        boolean enabled = backend.isIdle();
        doLeadUpButton.setEnabled(enabled);
        probeButton.setEnabled(enabled);
    }

    private boolean jobInProgress = false;
    private void runBMJob() {
        try {
            BMService.JobContext context = new BMService.JobContext(commonSettingsModel.getRecordList(),
                    tblAxesModel.getRecordList(),
                    tblActionsModel.getRecordList(),
                    (stepLabel, success, ctx) -> {
                        if (ctx != null) {
                            tblActions.changeSelection(ctx.stepNo, 0, false, false);
                            tblActions.scrollRectToVisible(new Rectangle(tblActions.getCellRect(ctx.stepNo, 0, true)));
                        }
                    },
                    (stepLabel, success, ctx) -> {
                        jobInProgress = false;
                        SwingUtilities.invokeLater(() -> probeButton.setSelected(false));
                        if (success && appendBacklashHistory(ctx.backlash.get(Axis.X),
                                ctx.backlash.get(Axis.Y),
                                ctx.backlash.get(Axis.Z))) {
                            JOptionPane.showMessageDialog(null,
                                    String.format("Backlash: X=%f, Y=%f, Z=%f",
                                            ctx.backlash.get(Axis.X),
                                            ctx.backlash.get(Axis.Y),
                                            ctx.backlash.get(Axis.Z)));
                        } else {
                            if (!success) {
                                JOptionPane.showMessageDialog(null,
                                        String.format("Backlash measurement failed: %s", ctx.msg),
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null,
                                        "Unable to store backlash history to DB",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
            tblActions.changeSelection(0, 0, false, false);
            tblActions.scrollRectToVisible(new Rectangle(tblActions.getCellRect(0, 0, true)));
            jobInProgress = true;
            service.startJob(context);
        } catch (Exception e) {
            logger.log(Level.SEVERE,"probeButtonAction: ", e);
            JOptionPane.showMessageDialog(null,
                    String.format("Job start failed: %s", e.getMessage()),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void UGSEvent(UGSEvent evt) {
        updateControls();
    }
}
