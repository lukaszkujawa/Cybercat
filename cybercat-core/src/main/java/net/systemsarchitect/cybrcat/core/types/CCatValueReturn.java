package net.systemsarchitect.cybrcat.core.types;

/**
 * Created by lukasz on 06/02/2017.
 */
public class CCatValueReturn implements CCatValue<CCatValue> {

    CCatValue value;

    public CCatValueReturn(CCatValue val) {
        setValue(val);
    }


    @Override
    public CCatValue getValue() {
        return value;
    }

    @Override
    public void setValue(CCatValue val) {
        value = val;
    }
}
