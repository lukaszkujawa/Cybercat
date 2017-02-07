package net.systemsarchitect.cybrcat.module.output;

import net.systemsarchitect.cybrcat.core.Module;
import net.systemsarchitect.cybrcat.core.types.CCatValue;
import net.systemsarchitect.cybrcat.core.types.CCatValueList;
import net.systemsarchitect.cybrcat.core.types.CCatValueMap;

import java.util.List;

/**
 * Created by lukasz on 05/02/2017.
 */
public class Json extends Module {

    @Override
    public List<CCatValue> call(CCatValue input) {
        if(input instanceof CCatValueMap || input instanceof CCatValueList) {
            System.out.println(input.toString());
        }
        else {
            System.out.println("{\"value\": \"" + input.toString() + "\"}");
        }

        return null;
    }
}
