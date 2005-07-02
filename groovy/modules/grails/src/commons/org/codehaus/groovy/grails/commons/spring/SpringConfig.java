/*
 * Copyright 2004-2005 the original author or authors.
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
package org.codehaus.groovy.grails.commons.spring;

import java.util.Collection;

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsControllerClass;
import org.codehaus.groovy.grails.web.servlet.mvc.SimpleGrailsController;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springmodules.beans.factory.drivers.Bean;

/**
 * <p>Creates beans and bean references for a Grails application.
 * 
 * @author Steven Devijver
 * @since Jul 2, 2005
 */
public class SpringConfig {

	private GrailsApplication application = null;
	
	public SpringConfig(GrailsApplication application) {
		super();
		this.application = application;
	}

	public Collection getBeanReferences() {
		Collection beanReferences = null;
		
		Bean simpleGrailsController = SpringConfigUtils.createSingletonBean(SimpleGrailsController.class);
		simpleGrailsController.setAutowire("byType");
		beanReferences.add(SpringConfigUtils.createBeanReference("simpleGrailsController", simpleGrailsController));
		
		Bean internalResourceViewResolver = SpringConfigUtils.createSingletonBean(InternalResourceViewResolver.class);
		internalResourceViewResolver.setProperty("prefix", SpringConfigUtils.createLiteralValue("/WEB-INF/jsp/"));
		internalResourceViewResolver.setProperty("suffix", SpringConfigUtils.createLiteralValue(".jsp"));
		beanReferences.add(SpringConfigUtils.createBeanReference("jspViewResolver", internalResourceViewResolver));
		
		Bean simpleUrlHandlerMapping = SpringConfigUtils.createSingletonBean(SimpleUrlHandlerMapping.class);
		simpleUrlHandlerMapping.setProperty("mappings", SpringConfigUtils.createLiteralValue("/*=simpleGrailsController"));
		beanReferences.add(SpringConfigUtils.createBeanReference("handlerMapping", simpleUrlHandlerMapping));
		
		GrailsControllerClass[] simpleControllers = application.getControllers();
		for (int i = 0; i < simpleControllers.length; i++) {
			GrailsControllerClass simpleController = simpleControllers[i];
			if (!simpleController.getAvailable()) {
				continue;
			}
			String name = simpleController.getName().substring(0, 1).toLowerCase() + simpleController.getName().substring(1);
			Bean controllerClass = SpringConfigUtils.createSingletonBean(MethodInvokingFactoryBean.class);
			controllerClass.setProperty("targetObject", SpringConfigUtils.createBeanReference("grailsApplication"));
			controllerClass.setProperty("targetMethod", SpringConfigUtils.createLiteralValue("getController"));
			controllerClass.setProperty("arguments", SpringConfigUtils.createLiteralValue(name));
			beanReferences.add(SpringConfigUtils.createBeanReference(name + "ControllerClass", controllerClass));
			
			Bean controller = SpringConfigUtils.createSingletonBean();
			controller.setFactoryBean(SpringConfigUtils.createBeanReference(name + "ControllerClass"));
			controller.setFactoryMethod("newInstance");
			if (simpleController.byType()) {
				controller.setAutowire("byType");
			} else if (simpleController.byName()) {
				controller.setAutowire("byName");
			}
			beanReferences.add(SpringConfigUtils.createBeanReference(name + "Controller", controller));
		}
		
		return beanReferences;
	}
}
