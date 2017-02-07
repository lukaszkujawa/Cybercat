package net.systemsarchitect.cybrcat.core;

import net.systemsarchitect.Cybercat.CybercatBaseVisitor;
import net.systemsarchitect.Cybercat.CybercatParser;
import net.systemsarchitect.cybrcat.core.anno.AllowCall;
import net.systemsarchitect.cybrcat.core.types.*;
import org.antlr.v4.runtime.ParserRuleContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by lukasz on 04/02/2017.
 */
public class Visitor extends CybercatBaseVisitor {

    UniverseImpl universe;

    public Visitor(UniverseImpl universe) {
        this.universe = universe;
    }

    public UniverseImpl getUniverse() {
        return universe;
    }

    CCatValue methodChainReference = null;

    @Override
    public Object visitStatement(CybercatParser.StatementContext ctx) {
        int children = ctx.stmt().size();

        for(int i = 0 ; i < children ; i++) {

            //System.out.println("> " + i + " " + ctx.stmt().get(i).getText());
            Object o = visit(ctx.stmt().get(i));
            if(o != null) {
                //System.out.println(" > " + ((CCatValue) o).toString());
            }
        }

        return null;
    }

    @Override
    public Object visitDouble(CybercatParser.DoubleContext ctx) {
        return new CCatValueDouble(Double.valueOf(ctx.getText()));
    }

    @Override
    public Object visitBoolean(CybercatParser.BooleanContext ctx) {
        if(ctx.BOOLEAN().getText().equals("true")) {
            return new CCatValueBoolean(true);
        }
        else {
            return new CCatValueBoolean(false);
        }
    }

    @Override
    public Object visitInteger(CybercatParser.IntegerContext ctx) {
        return new CCatValueInteger(Integer.valueOf(ctx.getText()));
    }

    @Override
    public Object visitString(CybercatParser.StringContext ctx) {
        return new CCatValueString(CCatValueString.parseString(ctx.getText()));
    }

    @Override
    public Object visitReturnValue(CybercatParser.ReturnValueContext ctx) {
        return new CCatValueReturn((CCatValue) visit(ctx.value()));
    }


    /**
     ------------- Arithmetic ---------------------
     */


    @Override
    public Object visitAdd(CybercatParser.AddContext ctx) {
        CCatValue left = (CCatValue) visit(ctx.expr(0));
        CCatValue right = (CCatValue) visit(ctx.expr(1));

        if(left instanceof Addable && right instanceof Addable) {
            return ((Addable) left).add(right);
        }
        else {
            throw new CCatRuntimeException("Trying to add not addable types", ctx.getText());
        }
    }

    @Override public Object visitMul(CybercatParser.MulContext ctx) {
        CCatValue left = (CCatValue) visit(ctx.expr(0));
        CCatValue right = (CCatValue) visit(ctx.expr(1));

        if(left instanceof Multiplicable && right instanceof Multiplicable) {
            return ((Multiplicable) left).mul(right);
        }
        else {
            throw new CCatRuntimeException("Trying to multiply not multiplicable types", ctx.getText());
        }
    }

    @Override public Object visitSub(CybercatParser.SubContext ctx) {
        CCatValue left = (CCatValue) visit(ctx.expr(0));
        CCatValue right = (CCatValue) visit(ctx.expr(1));

        if(left instanceof Subtractable && right instanceof Subtractable) {
            return ((Subtractable) left).sub(right);
        }
        else {
            throw new CCatRuntimeException("Trying to subtract not subtractable types", ctx.getText());
        }
    }

    @Override public Object visitDiv(CybercatParser.DivContext ctx) {
        CCatValue left = (CCatValue) visit(ctx.expr(0));
        CCatValue right = (CCatValue) visit(ctx.expr(1));

        if(left instanceof Dividable && right instanceof Dividable) {
            return ((Dividable) left).div(right);
        }
        else {
            throw new CCatRuntimeException("Trying to divide not dividable types", ctx.getText());
        }
    }

    @Override
    public Object visitNested(CybercatParser.NestedContext ctx) { return visit(ctx.expr()); }

    /**
     ------------- Variable ---------------------
     */

    @Override
    public Object visitVariable(CybercatParser.VariableContext ctx) {
        String id = ctx.ID().getText();
        if(universe.hasVariable(id) == false) {
            throw new CCatRuntimeException("Undefined variable \"" + id + "\"", ctx.getText());
        }

        return universe.getVariable(id);
    }


    @Override
    public Object visitAssign(CybercatParser.AssignContext ctx) {
        String id = ctx.ID().getText();

        Object obj = visit(ctx.expr());

        universe.setVariable(id, (CCatValue) obj);

        return obj;
    }

    @Override
    public Object visitAssignMapValue(CybercatParser.AssignMapValueContext ctx) {
        CybercatParser.MapValueContext ctxx = ((CybercatParser.MapValueContext)ctx.map_value());

        CCatValue ref = (CCatValue) visit(ctxx.ret_variable());

        int ids = ctxx.ID().size();

        for(int i = 0 ; i < ids - 1 ; i++) {
            String id = ctxx.ID(i).getText();
            ref = getMapValueByName(ref, id, ctx);
        }

        String id = ctxx.ID(ids - 1).getText();

        if(ref instanceof CCatValueMap) {
            ((CCatValueMap) ref).put(id, (CCatValue) visit(ctx.expr()));
        }
        else if(ref instanceof CCatValueModule) {
            try {
                ((CCatValueModule) ref).setValue(id, (CCatValue) visit(ctx.expr()));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new CCatRuntimeException(ref.getClass().getName() + " doesn't support \"" + id + "\"", ctx.getText());
            }
        }
        else {
            throw new CCatRuntimeException("Can't assing value to " + ref.getClass().getName(), ctx.getText());
        }


        return ref;
    }

    CCatValue getMapValueByName(CCatValue ref, String name, ParserRuleContext ctx) {
        if(ref instanceof CCatValueModule) {
            try {
                return ((CCatValueModule) ref).get(name);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new CCatRuntimeException(e.toString(), ctx.getText());
            }
        }
        else if(ref instanceof  CCatValueMap) {
            return ((CCatValueMap) ref).get(name);
        }
        else {
            throw new CCatRuntimeException("Unexpected object type " + ref.getClass().getName(), ctx.getText());
        }
    }

    @Override
    public Object visitMapValue(CybercatParser.MapValueContext ctx) {
        int ids = ctx.ID().size();

        CCatValue ref = (CCatValue) visit(ctx.ret_variable());

        for(int i = 0 ; i < ids ; i++) {
            String id = ctx.ID(i).getText();
            ref = getMapValueByName(ref, id, ctx);
        }

        return ref;
    }

    /**
     ------------- Map ---------------------
     */

    @Override
    public Object visitMap(CybercatParser.MapContext ctx) {
        CCatValueMap map = new CCatValueMap();

        int attrSize = ctx.attrdef().size();

        for(int i = 0 ; i < attrSize ; i++) {
            String attrName = null;

            if(ctx.attrdef(i).attr_name().ID() != null) {
                attrName = ctx.attrdef(i).attr_name().ID().getText();
            }
            else {
                attrName = ((CCatValueString)visit(ctx.attrdef(i).attr_name().string_def())).toString();
            }

            CCatValue attrValue = (CCatValue) visit(ctx.attrdef(i).attr_value().expr());

            map.put(attrName, attrValue);
        }

        return map;
    }

    /**
     ------------- Function ---------------------
     */

    @Override
    public Object visitFunctionDefinition(CybercatParser.FunctionDefinitionContext ctx) {
        CCatValueFunction fn = new CCatValueFunction(ctx, this);

        return fn;
    }


    public void invokeModule(String moduleName, CCatValue args, ParserRuleContext ctx) {
        if(universe.hasVariable(moduleName) == false) {
            throw new CCatRuntimeException("Undefined module \"" + moduleName + "\"", ctx.getText());
        }

        try {
            CCatValue var = universe.getVariable(moduleName);

            if(var instanceof CCatValueModule == false) {
                throw new CCatRuntimeException("\"" + moduleName + "\" is not a module instance", ctx.getText());
            }

            Packet packet = new Packet(args);
            packet.setTarget(var);

            universe.addPacket(packet);
            universe.run();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new CCatRuntimeException(e.getMessage(), ctx.getText());
        }
    }

    @Override
    public Object visitInvokeByReferenceArgs(CybercatParser.InvokeByReferenceArgsContext ctx) {
        String moduleName = ctx.ID().getText();

        invokeModule(moduleName, (CCatValue) visit(ctx.value(0)), ctx);

        return null;
    }

    @Override
    public Object visitInvokeByReference(CybercatParser.InvokeByReferenceContext ctx) {
        String moduleName = ctx.ID().getText();

        invokeModule(moduleName, null, ctx);

        return null;
    }



    /**
     ------------- Module ---------------------
     */

    @Override
    public Object visitModuleDefinition(CybercatParser.ModuleDefinitionContext ctx) {
        String className = CCatValueString.parseString(ctx.STRING().getText());

        try {

            if(Class.forName(className).isInstance(Module.class)) {
                throw new CCatRuntimeException("Module \"" + className + "\" doesn't extend CCat Module class");
            }

            Module module = (Module) Class.forName(className).newInstance();

            CCatValueMap map = (CCatValueMap) visit(ctx.mapdef());

            CCatValueModule ccatModule = new CCatValueModule(module);
            ccatModule.populate(map);

            universe.setVariable(ctx.ID().getText(), (CCatValue) ccatModule);

            return ccatModule;

        } catch (IllegalArgumentException | InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException  e) {

            throw new CCatRuntimeException(e.toString(), ctx.getText());
        }
    }

    @Override
    public Object visitPrint(CybercatParser.PrintContext ctx) {
        CCatValue value = (CCatValue)visit(ctx.expr());
        System.out.println(value.toString());
        return null;
    }

    CCatValue invokeModuleMethod(Object obj, String methodName, Class[] clz, CCatValue[] cv, ParserRuleContext ctx) {
        try {
            CCatValue ret = null;
            Method method = null;

            if(obj instanceof CCatValueModule) {
                obj = ((CCatValueModule) obj).getValue();
            }

            method = obj.getClass().getMethod(methodName, clz);

            if(method.getAnnotation(AllowCall.class) == null) {
                throw new CCatRuntimeException("Illegal Access to \"" + method.getName() + "\"", ctx.getText());
            }

            ret = (CCatValue) method.invoke(obj, cv);

            return ret;

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new CCatRuntimeException(e.toString(), ctx.getText());
        }
    }

    @Override
    public Object visitModuleMethod(CybercatParser.ModuleMethodContext ctx) {
        int ids = ctx.ID().size();
        CCatValue ref = null;
        String methodName = null;
        if(methodChainReference == null) {
            ref = (CCatValue) visit(ctx.ret_variable());

            for(int i = 0 ; i < ids - 1 ; i++) {
                if(ref instanceof CCatValueMap == false) {
                    throw new CCatRuntimeException("Unexpected object type \"" + ref.getClass().getName() + "\", expecting CCatValueMap", ctx.getText());
                }

                String id = ctx.ID(i).getText();
                ref = getMapValueByName(ref, id, ctx);
            }

            methodName = ctx.ID(ids - 1).getText();

        }
        else {
            /**
             * Chaining methods
             * string.replace("a","").replace("b","")
             */
            ref = methodChainReference;
            String id = ((CybercatParser.VariableContext)ctx.ret_variable()).ID().getText();

            if(ids > 0) {
                ref = getMapValueByName(ref, id, ctx);
            }

            for(int i = 0 ; i < ids - 1 ; i++) {
                if(ref instanceof CCatValueMap == false) {
                    throw new CCatRuntimeException("Unexpected object type \"" + ref.getClass().getName() + "\", expecting CCatValueMap", ctx.getText());
                }

                id = ctx.ID(i).getText();
                ref = getMapValueByName(ref, id, ctx);
            }

            if(ids > 0) {
                methodName = ctx.ID(ids - 1).getText();
            }

            methodName = id;
        }


        return new Object[]{ref, methodName};
    }

    @Override public
    Object visitInvokeMethodChain(CybercatParser.InvokeMethodChainContext ctx) {
        int size = ctx.callMethod().size();
        CCatValue value = null;

        for(int i = 0 ; i < size ; i++ ) {
            if(i > 0) {
                methodChainReference = value;
            }

            value = (CCatValue) visit(ctx.callMethod(i));
        }

        methodChainReference = null;

        return value;
    }


    @Override
    public Object visitInvokeModuleMethod(CybercatParser.InvokeModuleMethodContext ctx) {
        Object[] obj = (Object[]) visit(ctx.module_method());

        CCatValue module = (CCatValue) obj[0];
        String methodName = (String) obj[1];

        return invokeModuleMethod(module, methodName, new Class[0], new CCatValue[0], ctx);
    }

    @Override
    public Object visitInvokeModuleMethodArgs(CybercatParser.InvokeModuleMethodArgsContext ctx) {
        Object[] obj = (Object[]) visit(ctx.module_method());

        CCatValue module = (CCatValue) obj[0];
        String methodName = (String) obj[1];

        Class[] clz = new Class[ctx.value().size()];
        CCatValue[] cv = new CCatValue[ctx.value().size()];

        for(int i = 0 ; i < ctx.value().size() ; i++) {
            clz[i] = CCatValue.class;
            cv[i] = (CCatValue)visit(ctx.value(i));
        }

        return invokeModuleMethod(module, methodName, clz, cv, ctx);
    }

    @Override
    public Object visitNewline(CybercatParser.NewlineContext ctx) { return null; }

    @Override
    public Object visitList(CybercatParser.ListContext ctx) {
        CCatValueList list = new CCatValueList();
        for(CybercatParser.ValueContext val : ctx.value()) {
            list.add((CCatValue)visit(val));
        }

        return list;
    }

}
