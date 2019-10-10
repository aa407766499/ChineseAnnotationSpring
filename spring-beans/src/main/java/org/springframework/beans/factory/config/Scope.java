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

package org.springframework.beans.factory.config;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.lang.Nullable;

/**
 * Strategy interface used by a {@link ConfigurableBeanFactory},
 * ConfigurableBeanFactory使用的策略接口，代表持有的bean所处的
 * representing a target scope to hold bean instances in.
 * 目标范围。
 * This allows for extending the BeanFactory's standard scopes
 * 允许自定义作用域扩展BeanFactory的标准作用域"singleton"和prototype"，
 * {@link ConfigurableBeanFactory#SCOPE_SINGLETON "singleton"} and
 * 调用ConfigurableBeanFactory的registerScope方法注册自定义作用域。
 * {@link ConfigurableBeanFactory#SCOPE_PROTOTYPE "prototype"}
 * with custom further scopes, registered for a
 * {@link ConfigurableBeanFactory#registerScope(String, Scope) specific key}.
 *
 * <p>{@link org.springframework.context.ApplicationContext} implementations
 * ApplicationContext的实现类比如WebApplicationContext会注册另外的标准作用域，
 * such as a {@link org.springframework.web.context.WebApplicationContext}
 * 比如基于该接口的"request"和"session"作用域。
 * may register additional standard scopes specific to their environment,
 * e.g. {@link org.springframework.web.context.WebApplicationContext#SCOPE_REQUEST "request"}
 * and {@link org.springframework.web.context.WebApplicationContext#SCOPE_SESSION "session"},
 * based on this Scope SPI.
 *
 * <p>Even if its primary use is for extended scopes in a web environment,
 * 即使该接口的主要用途是在web环境中扩展作用域，但该接口完全是通用的：
 * this SPI is completely generic: It provides the ability to get and put
 * 其提供了从任何底层存储机制获取和添加对象的能力，比如一个HTTP session或者
 * objects from any underlying storage mechanism, such as an HTTP session
 * 自定义的存储机制。传入该类的get和remove方法的名称能够区分当前作用域中的
 * or a custom conversation mechanism. The name passed into this class's
 * 目标对象。
 * {@code get} and {@code remove} methods will identify the
 * target object in the current scope.
 *
 * <p>{@code Scope} implementations are expected to be thread-safe.
 * Scope实现类要求是线程安全的。如果需要，一个Scope实例能够同时被多个
 * One {@code Scope} instance can be used with multiple bean factories
 * bean工厂使用，（除非其明确要求感知包含的BeanFactory），任意多个工厂并发
 * at the same time, if desired (unless it explicitly wants to be aware of
 * 地用多个线程访问该Scope。
 * the containing BeanFactory), with any number of threads accessing
 * the {@code Scope} concurrently from any number of factories.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2.0
 * @see ConfigurableBeanFactory#registerScope
 * @see CustomScopeConfigurer
 * @see org.springframework.aop.scope.ScopedProxyFactoryBean
 * @see org.springframework.web.context.request.RequestScope
 * @see org.springframework.web.context.request.SessionScope
 */
public interface Scope {

	/**
	 * Return the object with the given name from the underlying scope,
	 * 返回底层范围的给定名称的对象，如果在底层存储机制没有找到该对象
	 * {@link org.springframework.beans.factory.ObjectFactory#getObject() creating it}
	 * 那么用ObjectFactory的getObject()创建该对象，
	 * if not found in the underlying storage mechanism.
	 * <p>This is the central operation of a Scope, and the only operation
	 * 这是Scope接口的核心操作，这个操作是必须有的。
	 * that is absolutely required.
	 * @param name the name of the object to retrieve
	 * @param objectFactory the {@link ObjectFactory} to use to create the scoped
	 *                         ObjectFactory用于创建该作用域的对象
	 * object if it is not present in the underlying storage mechanism
	 * @return the desired object (never {@code null})
	 * @throws IllegalStateException if the underlying scope is not currently active
	 */
	Object get(String name, ObjectFactory<?> objectFactory);

	/**
	 * Remove the object with the given {@code name} from the underlying scope.
	 * 从底层范围移除给定名称的对象。
	 * <p>Returns {@code null} if no object was found; otherwise
	 * 没找到返回null；否则返回移除的对象。
	 * returns the removed {@code Object}.
	 * <p>Note that an implementation should also remove a registered destruction
	 * 注意：实现类也可以移除指定对象的已注册的销毁回调，如果有的话。然后它也可以
	 * callback for the specified object, if any. It does, however, <i>not</i>
	 * 不需要执行已注册的销毁回调，因为对象将被调用者销毁（如果合适的话）
	 * need to <i>execute</i> a registered destruction callback in this case,
	 * since the object will be destroyed by the caller (if appropriate).
	 * <p><b>Note: This is an optional operation.</b> Implementations may throw
	 * 注意：这是一个可选操作。实现类可以抛出UnsupportedOperationException，如果
	 * {@link UnsupportedOperationException} if they do not support explicitly
	 * 他们不支持明确的移除一个对象。
	 * removing an object.
	 * @param name the name of the object to remove
	 * @return the removed object, or {@code null} if no object was present
	 * @throws IllegalStateException if the underlying scope is not currently active
	 * @see #registerDestructionCallback
	 */
	@Nullable
	Object remove(String name);

	/**
	 * Register a callback to be executed on destruction of the specified
	 * 注册一个作用域中指定对象要被执行的销毁回调（或者整个作用域的销毁，
	 * object in the scope (or at destruction of the entire scope, if the
	 * 如果作用域不是销毁个别对象，那么就是要销毁整个作用域）。
	 * scope does not destroy individual objects but rather only terminates
	 * in its entirety).
	 * <p><b>Note: This is an optional operation.</b> This method will only
	 * 注意：这是个可选操作。该方法仅会被作用域中的配置了实际销毁的bean调用。
	 * be called for scoped beans with actual destruction configuration
	 * （DisposableBean，destroy-method，DestructionAwareBeanPostProcessor）
	 * (DisposableBean, destroy-method, DestructionAwareBeanPostProcessor).
	 * Implementations should do their best to execute a given callback
	 * 实现类可以尽其所能在合适的时候执行给定的回调。如果该回调完全不被底层
	 * at the appropriate time. If such a callback is not supported by the
	 * 的运行时环境所支持，该回调必须被忽略，相应的要打警告日志。
	 * underlying runtime environment at all, the callback <i>must be
	 * ignored and a corresponding warning should be logged</i>.
	 * <p>Note that 'destruction' refers to automatic destruction of
	 * 注意：'destruction'引用作为scope生命一部分的对象的自动销毁，
	 * the object as part of the scope's own lifecycle, not to the individual
	 * 而不是明确被应用移除的个别对象。
	 * scoped object having been explicitly removed by the application.
	 * If a scoped object gets removed via this facade's {@link #remove(String)}
	 * 如果通过facade的remove方法移除了作用域对象，那么任何注册的销毁回调也要被移除，
	 * method, any registered destruction callback should be removed as well,
	 * 假设被移除的对象会被重用或者手工销毁。
	 * assuming that the removed object will be reused or manually destroyed.
	 * @param name the name of the object to execute the destruction callback for
	 * @param callback the destruction callback to be executed.
	 *                 要执行的销毁回调。
	 * Note that the passed-in Runnable will never throw an exception,
	 *                 注意传入的Runnable决不会抛出异常，因此执行它是安全的
	 * so it can safely be executed without an enclosing try-catch block.
	 *                 不需要在周围加try-catch块。
	 * Furthermore, the Runnable will usually be serializable, provided
	 *                 此外， Runnable通常是可序列化的，提供它的目标对象也可以序列化。
	 * that its target object is serializable as well.
	 * @throws IllegalStateException if the underlying scope is not currently active
	 * @see org.springframework.beans.factory.DisposableBean
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getDestroyMethodName()
	 * @see DestructionAwareBeanPostProcessor
	 */
	void registerDestructionCallback(String name, Runnable callback);

	/**
	 * Resolve the contextual object for the given key, if any.
	 * 根据给定Key解析上下文对象，如果有的话。
	 * E.g. the HttpServletRequest object for key "request".
	 * 比如传递request，得到HttpServletRequest对象
	 * @param key the contextual key
	 * @return the corresponding object, or {@code null} if none found
	 * @throws IllegalStateException if the underlying scope is not currently active
	 */
	@Nullable
	Object resolveContextualObject(String key);

	/**
	 * Return the <em>conversation ID</em> for the current underlying scope, if any.
	 * <p>The exact meaning of the conversation ID depends on the underlying
	 * storage mechanism. In the case of session-scoped objects, the
	 * conversation ID would typically be equal to (or derived from) the
	 * {@link javax.servlet.http.HttpSession#getId() session ID}; in the
	 * case of a custom conversation that sits within the overall session,
	 * the specific ID for the current conversation would be appropriate.
	 * <p><b>Note: This is an optional operation.</b> It is perfectly valid to
	 * return {@code null} in an implementation of this method if the
	 * underlying storage mechanism has no obvious candidate for such an ID.
	 * @return the conversation ID, or {@code null} if there is no
	 * conversation ID for the current scope
	 * @throws IllegalStateException if the underlying scope is not currently active
	 */
	@Nullable
	String getConversationId();

}
