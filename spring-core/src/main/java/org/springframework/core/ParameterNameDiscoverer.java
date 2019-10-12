/*
 * Copyright 2002-2012 the original author or authors.
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

/**
 * Interface to discover parameter names for methods and constructors.
 * 该接口获取方法和构造器的参数名称。
 * <p>Parameter name discovery is not always possible, but various strategies are
 * 参数名称不是总是可以获取的，但是可以尝试不同的策略，比如查看debug信息，该信息会在编译时
 * available to try, such as looking for debug information that may have been
 * 发出，或者可选择从AspectJ注解方法查找参数名称注解值。
 * emitted at compile time, and looking for argname annotation values optionally
 * accompanying AspectJ annotated methods.
 *
 * @author Rod Johnson
 * @author Adrian Colyer
 * @since 2.0
 */
public interface ParameterNameDiscoverer {

	/**
	 * Return parameter names for this method,
	 * 返回该方法的参数名称。如果参数名称不能确定返回null。
	 * or {@code null} if they cannot be determined.
	 * @param method method to find parameter names for
	 * @return an array of parameter names if the names can be resolved,
	 * or {@code null} if they cannot
	 */
	@Nullable
	String[] getParameterNames(Method method);

	/**
	 * Return parameter names for this constructor,
	 * 返回该构造器的参数名称。
	 * or {@code null} if they cannot be determined.
	 * @param ctor constructor to find parameter names for
	 * @return an array of parameter names if the names can be resolved,
	 * or {@code null} if they cannot
	 */
	@Nullable
	String[] getParameterNames(Constructor<?> ctor);

}
