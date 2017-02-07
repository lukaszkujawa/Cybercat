package net.systemsarchitect.cybrcat.core;

import net.systemsarchitect.cybrcat.core.types.CCatValue;

/**
 * Created by lukasz on 04/02/2017.
 */
public class Variable {

    String name;
    CCatValue value;

    public Variable(String name, CCatValue value) {
        this.name = name;
        this.value = value;
    }


}
