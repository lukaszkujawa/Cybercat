package net.systemsarchitect.cybrcat.core.types;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by lukasz on 04/02/2017.
 */
public class CCatValueMap implements CCatValue<Map<String, CCatValue>> {

    Pattern doubleQuoteEsc = Pattern.compile("\"");
    Pattern backslash = Pattern.compile("\\\\");

    Map<String, CCatValue> value = new HashMap<>();

    @Override
    public Map<String, CCatValue> getValue() {
        return value;
    }

    @Override
    public void setValue(Map<String, CCatValue> val) {
        value = val;
    }

    public CCatValue get(String name) {
        return value.get(name);
    }

    public void put(String name, CCatValue val) {
        value.put(name, val);
    }

    public boolean containsKey(String key) {
        return value.containsKey(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean comma = false;
        for(Map.Entry<String,CCatValue> e : value.entrySet()) {
            if(comma) {
                sb.append(", ");
            }

            String val = e.getValue().toString();

            if(e.getValue() instanceof CCatValueString) {
                val = backslash.matcher(val).replaceAll("\\\\\\\\");
                val = doubleQuoteEsc.matcher(val).replaceAll("\\\\\"");
            }

            if(e.getValue() instanceof CCatValueMap) {
                sb.append("\"" + e.getKey() + "\": "  +val);
            }
            else {
                sb.append("\"" + e.getKey() + "\": \"" + val + "\"");
            }

            comma = true;
        }
        sb.append("}");
        return sb.toString();
    }

}
