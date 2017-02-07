package net.systemsarchitect.cybrcat.core.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukasz on 05/02/2017.
 */
public class CCatValueList implements CCatValue<List<CCatValue>> {

    List<CCatValue> value = new ArrayList<>();

    @Override
    public List<CCatValue> getValue() {
        return value;
    }

    @Override
    public void setValue(List<CCatValue> val) {
        value = val;
    }

    public void add(CCatValue val) {
        value.add(val);
    }

    public CCatValue get(int index) { return value.get(index); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        String join = "";
        for(CCatValue val : value) {

            sb.append(join);
            join = ",";

            if(val instanceof CCatValueMap || val instanceof CCatValueList) {
                sb.append(val.toString());
            }
            else {
                sb.append('"');
                sb.append(val.toString());
                sb.append('"');
            }

        }


        sb.append("]");
        return sb.toString();
    }

}
