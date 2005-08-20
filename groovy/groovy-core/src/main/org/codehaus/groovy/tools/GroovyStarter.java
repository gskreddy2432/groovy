/*
 $Id$

 Copyright 2003 (C) James Strachan and Bob Mcwhirter. All Rights Reserved.

 Redistribution and use of this software and associated documentation
 ("Software"), with or without modification, are permitted provided
 that the following conditions are met:

 1. Redistributions of source code must retain copyright
    statements and notices.  Redistributions must also contain a
    copy of this document.

 2. Redistributions in binary form must reproduce the
    above copyright notice, this list of conditions and the
    following disclaimer in the documentation and/or other
    materials provided with the distribution.

 3. The name "groovy" must not be used to endorse or promote
    products derived from this Software without prior written
    permission of The Codehaus.  For written permission,
    please contact info@codehaus.org.

 4. Products derived from this Software may not be called "groovy"
    nor may "groovy" appear in their names without prior written
    permission of The Codehaus. "groovy" is a registered
    trademark of The Codehaus.

 5. Due credit should be given to The Codehaus -
    http://groovy.codehaus.org/

 THIS SOFTWARE IS PROVIDED BY THE CODEHAUS AND CONTRIBUTORS
 ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 THE CODEHAUS OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package org.codehaus.groovy.tools;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import java.lang.reflect .*;

import groovy.ui.GroovyMain;
import groovy.ui.InteractiveShell;
import java.net.URL;
import java.io.File;
import java.io.FileInputStream;



/**
 * Helper class to help classworlds to load classes. 
 */
public class GroovyStarter {

    static void printUsage() {
        System.out.println("possible programs are 'groovyc','groovy','console','grok' and 'groovysh'");
        System.exit(1);
    }
    
    
    public static void rootLoader(String args[]) {
        String conf = System.getProperty("groovy.launcher.conf","conf/launcher.conf");
        LoaderConfiguration lc = new LoaderConfiguration();
        
        String tools = System.getProperty("tools.jar");
        if (tools!=null) lc.addFile(new File(tools));

        int argsOffset = 0;
        while (args.length-argsOffset>0) {
            if (args[argsOffset].equals("-classpath") || args[argsOffset].equals("-cp")) {
                if (args.length==argsOffset+1) {
                    System.err.println ("classpath parameter needs argument");
                    System.exit(1);
                }
                lc.addClassPath(args[argsOffset+1]);
                argsOffset+=2;
            } else if (args[argsOffset].equals("-main")) {
                if (args.length==argsOffset+1) {
                    System.err.println ("main parameter needs argument");
                    System.exit(1);
                }
                lc.setMainClass(args[argsOffset+1]);
                argsOffset+=2;
            } else {
                break;
            }            
        }

        String[] newArgs = new String[args.length-argsOffset];
        for (int i=0; i<args.length; i++) {
            newArgs[i] = args[i+argsOffset];
        }        
        
        try {
            lc.configure(new FileInputStream(conf));
        } catch (Exception e) {
            System.err.println("exception while configuring main class loader:");
            exit(e);
        }
        
        ClassLoader loader = new RootLoader(lc);
        Method m=null;
        try {
            Class c = loader.loadClass(lc.getMainClass());
            m = c.getMethod("main", new Class[]{String[].class});
        } catch (ClassNotFoundException e1) {
            exit(e1);
        } catch (SecurityException e2) {
            exit(e2);
        } catch (NoSuchMethodException e2) {
            exit(e2);
        }
        try {
            m.invoke(null, new Object[]{newArgs});
        } catch (IllegalArgumentException e3) {
            exit(e3);
        } catch (IllegalAccessException e3) {
            exit(e3);
        } catch (InvocationTargetException e3) {
            exit(e3);
        }
    }
    
    private static void exit(Exception e) {
        e.printStackTrace();
        System.exit(1);
    }
    
    // after migration from classworlds to the rootloader rename
    // the rootLoader method to main and remove this method as 
    // well as the classworlds method
    public static void main(String args[],ClassWorld classWorld ) {
        classworlds(args,classWorld);
    }

    public static void classworlds(String oldArgs[],ClassWorld classWorld ) {
        try {
            // Creates a realm with *just* the system classloader
            ClassRealm system = classWorld.newRealm("system");
     
            // Get the groovy realm
            ClassRealm groovy = classWorld.getRealm("groovy");
           
            // import everything from the system realm, because imports
            // are searched *first* in Classworlds
            groovy.importFrom("system", "");
            
            //add tools.jar to classpath
            String tools = System.getProperty("tools.jar");
            if (tools!=null) {
            	URL ref = (new File(tools)).toURI().toURL();
            	groovy.addConstituent(ref);
            }
        
            if (oldArgs.length==0) {
                printUsage();
                System.exit(1);
            }
            
            String program = oldArgs[0].toLowerCase();
            String[] args = new String[oldArgs.length-1];
            for (int i=0; i<args.length; i++) {
                args[i] = oldArgs[i+1];
            }
            
            if (program.equals("groovyc")) {
                org.codehaus.groovy.tools.FileSystemCompiler.main(args);
            } else if (program.equals("groovy")) {
                GroovyMain.main(args);
            } else if (program.equals("console")) {
                // work around needed, because the console is compiled after this files
                Class c = Class.forName("groovy.ui.Console");
                Method m= c.getMethod("main", new Class[]{String[].class});
                m.invoke(null, new Object[]{args});
            } else if (program.equals("groovysh")) {
                InteractiveShell.main(args);
             } else if (program.equals("grok")) {
                org.codehaus.groovy.tools.Grok.main(args);
            } else {
                System.out.println("unknown program "+program);
                printUsage();
                System.exit(1);
            }
        
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
    }
    
}
