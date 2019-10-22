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

package org.springframework.aop.framework;

import org.springframework.lang.Nullable;

/**
 * Delegate interface for a configured AOP proxy, allowing for the creation
 * 可配置AOP代理的委派接口，允许创建实际的代理对象。
 * of actual proxy objects.
 *
 * <p>Out-of-the-box implementations are available for JDK dynamic proxies
 * 可获得JDK动态代理和CGLIB代理的开箱即用的实现类，DefaultAopProxyFactory使用该类。
 * and for CGLIB proxies, as applied by {@link DefaultAopProxyFactory}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see DefaultAopProxyFactory
 */
public interface AopProxy {

	/**
	 * Create a new proxy object.
	 * 创建一个新的代理对象。
	 * <p>Uses the AopProxy's default class loader (if necessary for proxy creation):
	 * 使用AopProxy默认的类加载器（如果需要创建代理）：通常是线程上下文类加载器。
	 * usually, the thread context class loader.
	 * @return the new proxy object (never {@code null})
	 * @see Thread#getContextClassLoader()
	 */
	Object getProxy();

	/**
	 * Create a new proxy object.
	 * <p>Uses the given class loader (if necessary for proxy creation).
	 * {@code null} will simply be passed down and thus lead to the low-level
	 * 简单地传入null，这会造成低级别的代理功能缺省，这通常和AopProxy实现类的getProxy()
	 * proxy facility's default, which is usually different from the default chosen
	 * 方法选择的缺省不同。
	 * by the AopProxy implementation's {@link #getProxy()} method.
	 * @param classLoader the class loader to create the proxy with
	 * (or {@code null} for the low-level proxy facility's default)
	 * @return the new proxy object (never {@code null})
	 */
	Object getProxy(@Nullable ClassLoader classLoader);

}
