package net.systemsarchitect.cybrcat.core.types;

/**
 * Created by lukasz on 05/02/2017.
 */
public class CCatValueBoolean implements CCatValue<Boolean> {
    boolean value;

    public CCatValueBoolean(Boolean val) {
        value = val;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean val) {
        value = val;
    }
}
