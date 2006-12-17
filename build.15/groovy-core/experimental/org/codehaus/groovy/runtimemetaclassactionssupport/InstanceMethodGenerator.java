package org.codehaus.groovy.runtimemetaclassactionssupport;

import java.lang.reflect.Method;

/*
 * Copyright 2005 John G. Wilson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/**
 * @author John Wilson
 *
 */

public class InstanceMethodGenerator extends MethodGenerator {
  public InstanceMethodGenerator(final Method method) {
    super(method);
  }
  
  protected void startCall(final StringBuffer code) {
    code.append("((").append(this.method.getDeclaringClass().getName()).append(")target).").append(this.method.getName()).append('(');
  }
}
