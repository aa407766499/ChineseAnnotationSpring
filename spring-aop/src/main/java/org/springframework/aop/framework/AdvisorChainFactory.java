/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.aop.framework;

import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Factory interface for advisor chains.
 * 切面链的工厂接口。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface AdvisorChainFactory {

	/**
	 * Determine a list of {@link org.aopalliance.intercept.MethodInterceptor} objects
	 * 确定给定切面链配置的MethodInterceptor对象列表。
	 * for the given advisor chain configuration.
	 * @param config the AOP configuration in the form of an Advised object
	 * @param method the proxied method
	 * @param targetClass the target class (may be {@code null} to indicate a proxy without
	 * target object, in which case the method's declaring class is the next best option)
	 * @return List of MethodInterceptors (may also include InterceptorAndDynamicMethodMatchers)
	 */
	List<Object> getInterceptorsAndDynamicInterceptionAdvice(Advised config, Method method, @Nullable Class<?> targetClass);

}
