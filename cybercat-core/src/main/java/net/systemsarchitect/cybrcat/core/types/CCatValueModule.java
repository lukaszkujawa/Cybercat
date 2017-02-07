package net.systemsarchitect.cybrcat.core.types;

import net.systemsarchitect.cybrcat.core.Module;
import net.systemsarchitect.cybrcat.core.Packet;
import net.systemsarchitect.cybrcat.core.anno.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by lukasz on 04/02/2017.
 */
public class CCatValueModule implements CCatValue<Module> {

    Module value;

    public CCatValueModule(Module module) {
        module.setParent(this);
        value = module;
    }

    public static String attrToSetter(String attrName) {
        String s1 = attrName.substring(0,1).toUpperCase();
        String sTitle = s1 + attrName.substring(1);
        return "set" + sTitle;
    }

    public static String attrToGetter(String attrName) {
        String s1 = attrName.substring(0,1).toUpperCase();
        String sTitle = s1 + attrName.substring(1);
        return "get" + sTitle;
    }

    @Override
    public Module getValue() {
        return value;
    }

    @Override
    public void setValue(Module val) {
        value = val;
    }

    public CCatValue get(String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = value.getClass().getMethod(attrToGetter(name));
        if(method.getReturnType().isInstance(CCatValue.class)) {
            throw new IllegalAccessException("Attribute " + name + " is doesn't return CCatValue" );
        }

        return (CCatValue) method.invoke(value);
    }

    public void populate(CCatValueMap settings) throws NoSuchMethodException, IllegalArgumentException, InvocationTargetException, IllegalAccessException {
        for(Map.Entry<String, CCatValue> attr : settings.getValue().entrySet()) {
            setValue(attr.getKey(), attr.getValue());
        }
    }

    public void setValue(String attrName, CCatValue val) throws NoSuchMethodException, IllegalArgumentException, InvocationTargetException, IllegalAccessException {
        Method method = value.getClass().getMethod(attrToSetter(attrName), CCatValue.class);
        if((val instanceof CCatValueString && method.getAnnotation(AllowString.class) == null) ||
            (val instanceof CCatValueFunction && method.getAnnotation(AllowFunction.class) == null) ||
            (val instanceof CCatValueInteger && method.getAnnotation(AllowInteger.class) == null) ||
            (val instanceof CCatValueArray && method.getAnnotation(AllowList.class) == null) ||
            (val instanceof CCatValueModule && method.getAnnotation(AllowModule.class) == null) ) {
                throw new IllegalArgumentException(value.getClass().getName() + "." + method.getName() + " doesn't accept " + val.getClass().getName());
        }

        method.invoke(value, val);
    }

    public List<CCatValue> call(Packet packet) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        value.clearOutput();

        if(value.getBefore() != null) {
            value.getBefore().execute(this);
        }

        List<CCatValue> output = value.call(packet.getContent());

        if(value.getAfter() != null) {
            value.getAfter().execute(this);
        }

        return output;
    }

}
