package net.systemsarchitect.cybrcat.core.types;

/**
 * Created by lukasz on 04/02/2017.
 */
public class CCatValueInteger implements CCatValue<Integer>, Addable, Subtractable, Multiplicable, Dividable {

    Integer value;

    public CCatValueInteger(int val) {
        setValue(val);
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(Integer val) {
        value = val;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public CCatValue add(CCatValue val) {
        return new CCatValueInteger(getValue() + (Integer) val.getValue());
    }


    @Override
    public CCatValue div(CCatValue val) {
        return new CCatValueInteger(getValue() / (Integer) val.getValue());
    }

    @Override
    public CCatValue mul(CCatValue val) {
        return new CCatValueInteger(getValue() * (Integer) val.getValue());
    }

    @Override
    public CCatValue sub(CCatValue val) {
        return new CCatValueInteger(getValue() - (Integer) val.getValue());
    }
}
