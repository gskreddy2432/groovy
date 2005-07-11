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
package org.codehaus.groovy.grails.orm.hibernate;

import org.hibernate.cfg.Configuration;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

/**
 * @author Graeme Rocher
 * @since 07-Jul-2005
 */
public class ConfigurableLocalsSessionFactoryBean extends
		LocalSessionFactoryBean {

	private Configuration configuration;
		
	/**
	 * 
	 */
	public ConfigurableLocalsSessionFactoryBean() {
		super();		
	}
	
	/**
	 * Overrides default behaviour to allow for a configurable configuration class 
	 */
	protected Configuration newConfiguration() {
		return this.configuration;
	}

	/**
	 * @return Returns the configuration.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configurationClass The configuration to set.
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
}