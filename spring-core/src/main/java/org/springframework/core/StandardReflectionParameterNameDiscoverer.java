/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.core;

import org.springframework.lang.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * {@link ParameterNameDiscoverer} implementation which uses JDK 8's reflection facilities
 * ParameterNameDiscoverer实现类，使用JDK8的反射功能内省参数名称。（基于-parameters编译参数）
 * for introspecting parameter names (based on the "-parameters" compiler flag).
 *
 * @author Juergen Hoeller
 * @since 4.0
 * @see java.lang.reflect.Method#getParameters()
 * @see java.lang.reflect.Parameter#getName()
 */
public class StandardReflectionParameterNameDiscoverer implements ParameterNameDiscoverer {

	@Override
	@Nullable
	public String[] getParameterNames(Method method) {
		return getParameterNames(method.getParameters());
	}

	@Override
	@Nullable
	public String[] getParameterNames(Constructor<?> ctor) {
		return getParameterNames(ctor.getParameters());
	}

	@Nullable
	private String[] getParameterNames(Parameter[] parameters) {
		String[] parameterNames = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			Parameter param = parameters[i];
			if (!param.isNamePresent()) {
				return null;
			}
			parameterNames[i] = param.getName();
		}
		return parameterNames;
	}

}
