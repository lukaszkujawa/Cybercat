package net.systemsarchitect.cybrcat.core.types;

/**
 * Created by lukasz on 05/02/2017.
 */
public class CCatValueDouble implements CCatValue<Double>, Addable, Subtractable, Multiplicable, Dividable {

    Double value;

    public CCatValueDouble(Double value) {
        this.value = value;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void setValue(Double val) {
        value = val;
    }

    @Override
    public CCatValue add(CCatValue val) {
        if(val.getValue() instanceof Integer) {
            return new CCatValueDouble(getValue() + (Integer) val.getValue());
        }
        else {
            return new CCatValueDouble(getValue() + (Double) val.getValue());
        }
    }


    @Override
    public CCatValue div(CCatValue val) {
        if(val.getValue() instanceof Integer) {
            return new CCatValueDouble(getValue() / (Integer) val.getValue());
        }
        else {
            return new CCatValueDouble(getValue() / (Double) val.getValue());
        }
    }

    @Override
    public CCatValue mul(CCatValue val) {
        if(val.getValue() instanceof Integer) {
            return new CCatValueDouble(getValue() * (Integer) val.getValue());
        }
        else {
            return new CCatValueDouble(getValue() * (Double) val.getValue());
        }
    }

    @Override
    public CCatValue sub(CCatValue val) {
        if(val.getValue() instanceof Integer) {
            return new CCatValueDouble(getValue() - (Integer) val.getValue());
        }
        else {
            return new CCatValueDouble(getValue() - (Double) val.getValue());
        }

    }

    @Override
    public String toString() {
        return value.toString();
    }
}
