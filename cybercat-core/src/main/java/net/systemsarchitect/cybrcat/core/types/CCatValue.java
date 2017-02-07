package net.systemsarchitect.cybrcat.core.types;

/**
 * Created by lukasz on 04/02/2017.
 */
public interface CCatValue<T> {

    public T getValue();

    public void setValue(T val);

}
