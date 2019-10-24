/*
 * Copyright 2002-2016 the original author or authors.
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

import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

/**
 * Class containing static methods used to obtain information about the current AOP invocation.
 * 该类包含的静态方法用于获取关于当前AOP调用的信息。
 * <p>The {@code currentProxy()} method is usable if the AOP framework is configured to
 * 如果AOP框架配置要暴露当前代理（非默认），那么currentProxy()方法很有用。该方法返回使用的AOP代理。
 * expose the current proxy (not the default). It returns the AOP proxy in use. Target objects
 * 目标对象或者增强能使用该AOP代理触发增强调用，和EJB中使用的getEJBObject()方法一样。他们也可以使用
 * or advice can use this to make advised calls, in the same way as {@code getEJBObject()}
 * AOP代理查找增强配置。
 * can be used in EJBs. They can also use it to find advice configuration.
 *
 * <p>Spring's AOP framework does not expose proxies by default, as there is a performance cost
 * Spring AOP框架默认不暴露代理，因为这样做有性能开销。
 * in doing so.
 *
 * <p>The functionality in this class might be used by a target object that needed access
 * 需要访问调用中资源的目标对象可以使用该类的功能。然而，在有合理选择的时候，该方法不应该使用，
 * to resources on the invocation. However, this approach should not be used when there is
 * 因为它使得应用代码依赖于AOP，特别是Spring AOP框架的使用。
 * a reasonable alternative, as it makes application code dependent on usage under AOP and
 * the Spring AOP framework in particular.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 13.03.2003
 */
public abstract class AopContext {

	/**
	 * ThreadLocal holder for AOP proxy associated with this thread.
	 * Will contain {@code null} unless the "exposeProxy" property on
	 * the controlling proxy configuration has been set to "true".
	 * @see ProxyConfig#setExposeProxy
	 */
	private static final ThreadLocal<Object> currentProxy = new NamedThreadLocal<>("Current AOP proxy");


	/**
	 * Try to return the current AOP proxy. This method is usable only if the
	 * 试着返回当前AOP 代理，只有该调用方法通过AOP调用并且设置了AOP框架暴露代理，该方法才有用。
	 * calling method has been invoked via AOP, and the AOP framework has been set
	 * 否则，该方法会抛出IllegalStateException。
	 * to expose proxies. Otherwise, this method will throw an IllegalStateException.
	 * @return Object the current AOP proxy (never returns {@code null})
	 * @throws IllegalStateException if the proxy cannot be found, because the
	 * method was invoked outside an AOP invocation context, or because the
	 * AOP framework has not been configured to expose the proxy
	 */
	public static Object currentProxy() throws IllegalStateException {
		Object proxy = currentProxy.get();
		if (proxy == null) {
			throw new IllegalStateException(
					"Cannot find current proxy: Set 'exposeProxy' property on Advised to 'true' to make it available.");
		}
		return proxy;
	}

	/**
	 * Make the given proxy available via the {@code currentProxy()} method.
	 * 使得currentProxy()方法能够获得给定代理。
	 * <p>Note that the caller should be careful to keep the old value as appropriate.
	 * 注意：调用者应该小心恰当地保留旧值。
	 * @param proxy the proxy to expose (or {@code null} to reset it)
	 * @return the old proxy, which may be {@code null} if none was bound
	 * @see #currentProxy()
	 */
	@Nullable
	static Object setCurrentProxy(@Nullable Object proxy) {
		Object old = currentProxy.get();
		if (proxy != null) {
			currentProxy.set(proxy);
		}
		else {
			currentProxy.remove();
		}
		return old;
	}

}
