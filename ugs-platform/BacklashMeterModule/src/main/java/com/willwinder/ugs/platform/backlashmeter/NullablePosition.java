package com.willwinder.ugs.platform.backlashmeter;

import com.willwinder.universalgcodesender.model.Axis;
import com.willwinder.universalgcodesender.model.Position;
import com.willwinder.universalgcodesender.model.UnitUtils;

import java.util.HashMap;

public class NullablePosition {
    private final Position pos;
    protected HashMap<Axis, Boolean> nullAxes = new HashMap<>() {{
        put(Axis.X, true);
        put(Axis.Y, true);
        put(Axis.Z, true);
        put(Axis.A, true);
        put(Axis.B, true);
        put(Axis.C, true);
    }};

    public void set(Axis axis, Double value) {
        if (value == null) {
            nullAxes.put(axis, true);
            return;
        }
        nullAxes.put(axis, false);
        pos.set(axis, value);
    }

    public Double get(Axis axis) {
        if (nullAxes.get(axis))
            return null;
        return pos.get(axis);
    }

    public NullablePosition(UnitUtils.Units units) {
        pos = new Position(units);
    }

    public NullablePosition(NullablePosition other) {
        nullAxes = new HashMap<>(other.nullAxes);
        pos = new Position(other.pos);
    }
}
