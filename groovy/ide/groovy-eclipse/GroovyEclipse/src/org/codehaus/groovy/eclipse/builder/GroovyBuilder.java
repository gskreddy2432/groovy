package org.codehaus.groovy.eclipse.builder;

import java.util.Map;

import org.codehaus.groovy.eclipse.GroovyPlugin;
import org.codehaus.groovy.eclipse.model.GroovyModel;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * @see IncrementalProjectBuilder
 */

public class GroovyBuilder extends IncrementalProjectBuilder {
	
	private IJavaProject javaProject;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#startupOnInitialize()
	 */
	protected void startupOnInitialize() {
		super.startupOnInitialize();
		javaProject = JavaCore.create(getProject());
		GroovyPlugin.trace("GroovyBuilder.startupOnInitialize got a Java project : "+javaProject.getProject().getName());
	}

	
	/**
	 * @see IncrementalProjectBuilder#build
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
		throws CoreException {
			GroovyModel.getModel().buildGroovyContent(javaProject);
			return null;
		}
}
