/* Copyright 2004-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.grails.web.taglib;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException;

/**
 * @author Graeme Rocher
 * @since 18-Jan-2006
 */
public class GroovyEachTag extends GroovySyntaxTag {
    public static final String TAG_NAME = "each";
    private static final String ATTRIBUTE_IN = "in";
    private static final String ATTRIBUTE_VAR = "var";

    public void doStartTag() {
        String in = (String) attributes.get(ATTRIBUTE_IN);
        String var = (String) attributes.get(ATTRIBUTE_VAR);

        if(StringUtils.isBlank(in))
            throw new GrailsTagException("Tag ["+TAG_NAME+"] missing required attribute ["+ATTRIBUTE_IN+"]");

        out.print(in);
        if(StringUtils.isBlank(var)) {
            out.println(".each {");
        }
        else {
            out.print(".each { ");
            out.print(var.substring(1,var.length() -1));
            out.println(" ->");
        }

    }

    public void doEndTag() {
        out.println("}");
    }

    public String getName() {
        return TAG_NAME;
    }

    public boolean isBufferWhiteSpace() {
        return false;
    }

    public boolean hasPrecedingContent() {
        return true;
    }
}