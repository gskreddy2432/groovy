package org.codehaus.groovy.grails.web.taglib;

import javax.servlet.ServletRequest;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * @author Graeme Rocher
 * @since 11-Jan-2006
 */
public interface GrailsTagContext {


    Writer getOut() throws IOException ;

    ServletRequest getRequest();

    Map getAttributes();
}