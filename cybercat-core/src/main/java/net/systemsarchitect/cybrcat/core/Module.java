package net.systemsarchitect.cybrcat.core;

import net.systemsarchitect.cybrcat.core.anno.AllowCall;
import net.systemsarchitect.cybrcat.core.anno.AllowFunction;
import net.systemsarchitect.cybrcat.core.anno.AllowInteger;
import net.systemsarchitect.cybrcat.core.anno.AllowModule;
import net.systemsarchitect.cybrcat.core.types.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukasz on 04/02/2017.
 */
public class Module {

    List<CCatValue> output;

    CCatValueInteger maxAge = new CCatValueInteger(Integer.MAX_VALUE);
    CCatValueInteger ageInc = new CCatValueInteger(1);
    CCatValue target;
    CCatValueFunction before;
    CCatValueFunction after;
    CCatValueModule parent;

    public List<CCatValue> call(CCatValue input) {
        return null;
    }

    final public void clearOutput() {
        this.output = new ArrayList<>();
    }

    final public List<CCatValue> getOutput() {
        return output;
    }

    @AllowCall
    final public void emit(CCatValue val) {
        output.add(val);
    }

    @AllowInteger
    final public void setAgeinc(CCatValue ageInc) { this.ageInc = (CCatValueInteger) ageInc;  }

    public CCatValueInteger getAgeinc() { return ageInc; }

    @AllowInteger
    final public void setMaxage(CCatValue maxAge) { this.maxAge = (CCatValueInteger) maxAge;  }

    public CCatValueInteger getMaxage() { return maxAge; }

    @AllowFunction
    final public void setBefore(CCatValue before) {
        this.before = (CCatValueFunction) before;
    }

    @AllowFunction
    final public void setAfter(CCatValue after) {
        this.after = (CCatValueFunction) after;
    }

    @AllowModule
    final public void setTarget(CCatValue target) {
        this.target = target;
    }

    final public CCatValueFunction getBefore() { return before; }

    final public CCatValueFunction getAfter() { return after; }

    final public CCatValue getTarget() { return target; }

    final public void setParent(CCatValueModule parent) {
        this.parent = parent;
    }

    final public CCatValueModule getParent() { return parent; }

}
