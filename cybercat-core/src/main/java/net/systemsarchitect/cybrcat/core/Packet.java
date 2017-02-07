package net.systemsarchitect.cybrcat.core;

import net.systemsarchitect.cybrcat.core.types.CCatValue;
import net.systemsarchitect.cybrcat.core.types.CCatValueList;
import net.systemsarchitect.cybrcat.core.types.CCatValueModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukasz on 05/02/2017.
 */
public class Packet implements Comparable<Packet> {

    List<CCatValueModule> target = new ArrayList<>();
    CCatValue content;
    int age = 0;

    public Packet(CCatValue content) {
        this.content = content;
    }

    public CCatValue getContent() {
        return content;
    }

    public void addTarget(CCatValueModule target) {
        this.target.add(target);
    }

    public void setTarget(CCatValue target) {
        if (target instanceof CCatValueList) {
            this.target = (List<CCatValueModule>) target.getValue();
        } else if (target instanceof CCatValueModule) {
            List<CCatValueModule> t = new ArrayList<CCatValueModule>();
            t.add((CCatValueModule) target);

            this.target = t;
        } else {
            this.target = new ArrayList<CCatValueModule>();
        }
    }

    public List<CCatValueModule> getTargets() {
        return target;
    }

    public void incAge(int i) {
        age += i;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    @Override
    public int compareTo(Packet o) {
        return o.getAge() - getAge();
    }
}
