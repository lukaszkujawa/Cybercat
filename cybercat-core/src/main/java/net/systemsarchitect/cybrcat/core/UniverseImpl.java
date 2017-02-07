package net.systemsarchitect.cybrcat.core;

import net.systemsarchitect.cybrcat.core.types.CCatValue;
import net.systemsarchitect.cybrcat.core.types.CCatValueList;
import net.systemsarchitect.cybrcat.core.types.CCatValueModule;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by lukasz on 04/02/2017.
 */
public class UniverseImpl {

    final static String SELF_REF = "this";

    PriorityQueue<Packet> packets = new PriorityQueue();

    CCatValueModule localContext;
    Map<String, CCatValue> variables = new HashMap<>();
    Map<String, CCatValue> localVariables = new HashMap<>();

    public void setVariable(String name, CCatValue value) {
        if(localContext != null) {
            localVariables.put(name, value);
        }
        else {
            variables.put(name, value);
        }
    }

    public CCatValue getVariable(String name) {
        if(localContext != null ) {
            if(name.equals(SELF_REF)) {
                return localContext;
            }
            else if(localVariables.containsKey(name)) {
                return localVariables.get(name);
            }
        }

        return variables.get(name);
    }

    public boolean hasVariable(String name) {
        if(localContext != null ) {
            return name.equals(SELF_REF) || localVariables.containsKey(name) || variables.containsKey(name);
        }
        else {
            return variables.containsKey(name);
        }
    }

    public void startLocalContext(CCatValueModule module) {
        localContext = module;
    }

    public void endLocalContext() {
        localContext = null;
        localVariables.clear();
    }

    public Packet nextPacket() {
        if(packets.isEmpty() == false) {
            return packets.poll();
        }

        return null;
    }

    public void addPacket(Packet packet) {
        packets.add(packet);
    }

    public void run() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Packet packet;

        while((packet = nextPacket()) != null) {

            for(CCatValueModule module : packet.getTargets()) {

                List<CCatValue> output = module.call(packet);

                if(output != null) {
                    for (CCatValue out : output) {
                        if (out != null) {
                            Packet p = createPacket(out, packet, module);
                            if(p.getTargets().size() > 0) {
                                addPacket(p);
                            }
                        }
                    }
                }

            }
        }
    }

    Packet createPacket(CCatValue out, Packet packet, CCatValueModule module) {
        CCatValue ccatTarget = module.getValue().getTarget();

        List<CCatValueModule> target = null;

        if(ccatTarget instanceof CCatValueList) {
            target = (List<CCatValueModule>) ccatTarget.getValue();
        }
        else if(ccatTarget instanceof CCatValueModule) {
            List<CCatValueModule> t = new ArrayList<CCatValueModule>();
            t.add((CCatValueModule)ccatTarget);

            target = t;
        }
        else {
            target = new ArrayList<CCatValueModule>();
        }

        Packet p = new Packet(out);
        p.setAge(packet.getAge());
        p.incAge(module.getValue().getAgeinc().getValue());

        for(CCatValueModule mod : target) {
            if(p.getAge() < mod.getValue().getMaxage().getValue()) {
                p.addTarget(mod);
            }
        }

        return p;
    }

}
