/*
 * Created on 19-Jan-2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package org.codehaus.groovy.eclipse.test;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.groovy.eclipse.builder.GroovyBuilder;
import org.codehaus.groovy.eclipse.builder.GroovyNature;
import org.codehaus.groovy.eclipse.model.GroovyProject;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author MelamedZ
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class GroovyPluginTestCase extends EclipseTestCase {
	private static final List groovyRuntimeJars = new ArrayList();
	static{
		groovyRuntimeJars.add("commons-io-1.2.jar");
		groovyRuntimeJars.add("groovy-all-1.0-RC-01-SNAPSHOT.jar");
	};
	public void testNatureAddAndRemove() throws CoreException {
		IProject project = testProject.getProject();
		GroovyProject.addGroovyNature(project);

		assertTrue(hasGroovyNature());
		assertTrue(hasGroovyBuilder());

		GroovyProject.removeGroovyNature(project);

		assertFalse(hasGroovyNature());
		assertFalse(hasGroovyBuilder());
	}

	public void testManualAddGroovyRuntime() throws CoreException {

		List groovyRuntimeJars = verifyNoGroovyRuntime();

		plugin.addGroovyRuntime(testProject.getProject());
		GroovyProject.addGroovyNature( testProject.getProject() );
		for (Iterator iter = groovyRuntimeJars.iterator(); iter.hasNext();) {
			String jarName = (String) iter.next();
			assertTrue("expecting groovy runtime libs", testProject.hasJar(jarName));
		}
	}

	private List verifyNoGroovyRuntime() throws JavaModelException {
		
		for (Iterator iter = groovyRuntimeJars.iterator(); iter.hasNext();) {
			String jarName = (String) iter.next();
			assertFalse(
				"initialy none of the groovy runtime libs should be part of the project",
				testProject.hasJar(jarName));
		}

		return groovyRuntimeJars;
	}



	private boolean hasGroovyBuilder() throws CoreException {
		ICommand[] commands = testProject.getProject().getDescription().getBuildSpec();
		boolean found = false;
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(GroovyBuilder.GROOVY_BUILDER)) {
				found = true;
			}
		}
		return found;
	}

	private boolean hasGroovyNature() throws CoreException {
		return testProject.getProject().hasNature(GroovyNature.GROOVY_NATURE);
	}

}
