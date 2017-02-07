package net.systemsarchitect.cybrcat.module.parser;

import net.systemsarchitect.cybrcat.core.Module;
import net.systemsarchitect.cybrcat.core.anno.*;
import net.systemsarchitect.cybrcat.core.types.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukasz on 06/02/2017.
 */
public class JSoup extends Module {

    CCatValue selector;
    CCatValueFunction each;

    Element eachContext;

    @AllowString
    @AllowList
    //@AllowMap
    public void setSelector(CCatValue selector) {
        this.selector = selector;
    }

    @AllowFunction
    public void setEach(CCatValue each) {
        this.each = (CCatValueFunction) each;
    }

    @AllowCall
    public CCatValue select(CCatValue selector) {
        return new CCatValueString(eachContext.select(selector.toString()).text());

    }

    @AllowCall
    public CCatValue select(CCatValue selector, CCatValue attr) {
        return new CCatValueString(eachContext.select(selector.toString()).attr(attr.toString()));
    }

    public void parseWithStringSelector(CCatValue input) {
        CCatValueList list = new CCatValueList();
        Document doc = Jsoup.parse(input.toString());

        String sel = null;
        String attr = null;

        if(selector instanceof CCatValueList) {
            sel =  ((CCatValueList) selector).get(0).toString();
            attr =  ((CCatValueList) selector).get(1).toString();
        }
        else {
            sel = selector.toString();
        }

        for(Element el : doc.select(sel)) {
            eachContext = el;
            CCatValue hit = null;
            if(attr == null) {
                hit = new CCatValueString(el.text());
            }
            else {
                hit = new CCatValueString(el.attr(attr));
            }

            if(each != null) {
                CCatValue ret = each.execute(getParent(), hit);
                if(ret != null) {
                    list.add(ret);
                }
            } else {
                list.add(hit);
            }
        }

        if(list.getValue().size() > 0) {
            emit(list);
        }
    }

    public void parseWithMapSelector(CCatValue input) {

    }

    @Override
    public List<CCatValue> call(CCatValue input) {

        if(selector instanceof CCatValueMap) {
            parseWithMapSelector(input);
        }
        else {
            parseWithStringSelector(input);
        }

        return getOutput();
    }

}
