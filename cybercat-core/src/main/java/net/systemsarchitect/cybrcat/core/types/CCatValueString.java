package net.systemsarchitect.cybrcat.core.types;


import net.systemsarchitect.cybrcat.core.anno.AllowCall;
import net.systemsarchitect.cybrcat.core.anno.AllowString;

import java.util.regex.Pattern;

/**
 * Created by lukasz on 04/02/2017.
 */
public class CCatValueString implements CCatValue<String>, Addable {

    static final Pattern doubleQuoteEsc = Pattern.compile("\\\\\"");
    static final Pattern backsladhEsc = Pattern.compile("\\\\\\\\");

    String value;

    public CCatValueString(String val) {
        setValue(val);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String val) {
        value = val;
    }

    @Override
    public CCatValue add(CCatValue val) {
        return new CCatValueString(getValue() + val.getValue().toString());
    }

    @Override
    public String toString() {
        return value;
    }

    @AllowCall
    @AllowString
    public CCatValue replace(CCatValue find, CCatValue replace) {
        return new CCatValueString(value.replaceAll(find.toString(), replace.toString()));
    }

    static public String parseString(String str) {
        str = str.substring(1,str.length()-1);
        str = doubleQuoteEsc.matcher(str).replaceAll("\"");
        str = backsladhEsc.matcher(str).replaceAll("\\\\");
        return str;
    }
}
