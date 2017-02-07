package net.systemsarchitect.cybrcat.core;

/**
 * Created by lukasz on 04/02/2017.
 */
public class CCatRuntimeException extends RuntimeException {

    public CCatRuntimeException(String msg) {
        super(msg);
    }

    public CCatRuntimeException(String msg, String location) {
        super("error at:\n" + location + "\n" + msg);
    }

}
