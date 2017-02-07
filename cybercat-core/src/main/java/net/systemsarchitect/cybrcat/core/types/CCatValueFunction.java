package net.systemsarchitect.cybrcat.core.types;

import net.systemsarchitect.Cybercat.CybercatParser;
import net.systemsarchitect.cybrcat.core.CCatRuntimeException;
import net.systemsarchitect.cybrcat.core.Visitor;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lukasz on 04/02/2017.
 */
public class CCatValueFunction implements CCatValue<List<CybercatParser.StmtContext>> {

    List<CybercatParser.StmtContext> value;
    List<String> arguments = new LinkedList<>();
    Visitor visitor;
    String text;

    public CCatValueFunction(CybercatParser.FunctionDefinitionContext val, Visitor visitor) {
        setValue(val.stmt());

        for(int i = 0 ; i < val.funcattrib().ID().size() ; i++) {
            arguments.add(val.funcattrib().ID(i).getText());
        }

        text = val.getText();
        this.visitor = visitor;
    }

    @Override
    public List<CybercatParser.StmtContext> getValue() {
        return value;
    }

    public CCatValue execute(CCatValueModule module, CCatValue... args) {
        CCatValue ret = null;
        visitor.getUniverse().startLocalContext(module);

        int i = 0;
        for(CCatValue arg : args) {
            visitor.getUniverse().setVariable(arguments.get(i), arg);
            i++;
        }

        for(CybercatParser.StmtContext ctx : value) {
            Object o = visitor.visit(ctx);
            if(o instanceof CCatValueReturn) {
                ret = ((CCatValueReturn) o).getValue();
                break;
            }
        }

        visitor.getUniverse().endLocalContext();

        return ret;
    }

    @Override
    public void setValue(List<CybercatParser.StmtContext> val) {
        value = val;
    }

    @Override
    public String toString() {
        return text;
    }

}
