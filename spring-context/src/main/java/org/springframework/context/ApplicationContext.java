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

package org.springframework.context;

import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;

/**
 * Central interface to provide configuration for an application.
 * 给应用提供配置的中心接口。应用运行期间处于只读状态，但是如果实现
 * This is read-only while the application is running, but may be
 * 支持这个接口可以重新加载。
 * reloaded if the implementation supports this.
 *
 * <p>An ApplicationContext provides:
 * ApplicationContext提供如下功能：
 * <ul>
 * <li>Bean factory methods for accessing application components.
 * bean工厂方法去访问应用组件。继承自ListableBeanFactory。
 * Inherited from {@link org.springframework.beans.factory.ListableBeanFactory}.
 * <li>The ability to load file resources in a generic fashion.
 * 能够使用通用的方式加载文件资源。继承自ResourceLoader接口
 * Inherited from the {@link org.springframework.core.io.ResourceLoader} interface.
 * <li>The ability to publish events to registered listeners.
 * 能够推送事件给注册的监听器。继承自ApplicationEventPublisher接口。
 * Inherited from the {@link ApplicationEventPublisher} interface.
 * <li>The ability to resolve messages, supporting internationalization.
 * 能够解析消息，支持国际化。继承自MessageSource接口
 * Inherited from the {@link MessageSource} interface.
 * <li>Inheritance from a parent context. Definitions in a descendant context
 * 继承父容器。子容器中的定义有优先权。举个例子，这使得单一的父容器能够被整个web应用
 * will always take priority. This means, for example, that a single parent、
 * 使用，在每一个servlet有其自己的子容器，该子容器独立于其他的servlet
 * context can be used by an entire web application, while each servlet has
 * its own child context that is independent of that of any other servlet.
 * </ul>
 *
 * <p>In addition to standard {@link org.springframework.beans.factory.BeanFactory}
 * 除了标准的BeanFactory生命周期能力，ApplicationContext 实现检测和调用
 * lifecycle capabilities, ApplicationContext implementations detect and invoke
 * ApplicationContextAware的bean以及ResourceLoaderAware，ApplicationEventPublisherAware，
 * {@link ApplicationContextAware} beans as well as {@link ResourceLoaderAware},
 * MessageSourceAware的bean。
 * {@link ApplicationEventPublisherAware} and {@link MessageSourceAware} beans.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see ConfigurableApplicationContext
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.core.io.ResourceLoader
 */
public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
		MessageSource, ApplicationEventPublisher, ResourcePatternResolver {

	/**
	 * Return the unique id of this application context.
	 * 返回应用上下文的唯一id
	 * @return the unique id of the context, or {@code null} if none
	 */
	@Nullable
	String getId();

	/**
	 * Return a name for the deployed application that this context belongs to.
	 * 返回这个上下文归属的部署应用的名称
	 * @return a name for the deployed application, or the empty String by default
	 */
	String getApplicationName();

	/**
	 * Return a friendly name for this context.
	 * 返回上下文友好名称
	 * @return a display name for this context (never {@code null})
	 */
	String getDisplayName();

	/**
	 * Return the timestamp when this context was first loaded.
	 * 返回上下文第一次被加载时的时间戳
	 * @return the timestamp (ms) when this context was first loaded
	 */
	long getStartupDate();

	/**
	 * Return the parent context, or {@code null} if there is no parent
	 * 返回父容器，如果没有父容器或者该容器是顶层容器返回null
	 * and this is the root of the context hierarchy.
	 * @return the parent context, or {@code null} if there is no parent
	 */
	@Nullable
	ApplicationContext getParent();

	/**
	 * Expose AutowireCapableBeanFactory functionality for this context.
	 * 暴露该容器的AutowireCapableBeanFactory功能。
	 * <p>This is not typically used by application code, except for the purpose of
	 * 通常不会再应用代码中使用，除了为了初始化不受应用上下文管理的bean实例，对这些
	 * initializing bean instances that live outside of the application context,
	 * bean实例应用（全部或者部分）Spring bean 生命周期。
	 * applying the Spring bean lifecycle (fully or partly) to them.
	 * <p>Alternatively, the internal BeanFactory exposed by the
	 * 或者，通过ConfigurableApplicationContext接口暴露内部BeanFactory也提供对
	 * {@link ConfigurableApplicationContext} interface offers access to the
	 * AutowireCapableBeanFactory的访问。当前方法主要作为ApplicationContext接口的
	 * {@link AutowireCapableBeanFactory} interface too. The present method mainly
	 * 一个便利专用的功能。
	 * serves as a convenient, specific facility on the ApplicationContext interface.
	 * <p><b>NOTE: As of 4.2, this method will consistently throw IllegalStateException
	 * 注意：4.2版本以后，这个方法在应用上下文关闭以后都会抛出IllegalStateException。
	 * after the application context has been closed.</b> In current Spring Framework
	 * 在当前的spring版本中，只有可刷新应用上下文有这个功能；4.2版本以后，所有的应用上下文
	 * versions, only refreshable application contexts behave that way; as of 4.2,
	 * 都要遵从这个规则。
	 * all application context implementations will be required to comply.
	 * @return the AutowireCapableBeanFactory for this context
	 * @throws IllegalStateException if the context does not support the
	 * 如果该上下文不支持AutowireCapableBeanFactory接口，或者没有持有
	 * {@link AutowireCapableBeanFactory} interface, or does not hold an
	 * AutowireCapableBeanFactory接口的引用（比如refresh()没有调用过），或者
	 * autowire-capable bean factory yet (e.g. if {@code refresh()} has
	 * 上下文已经关闭了。
	 * never been called), or if the context has been closed already
	 * @see ConfigurableApplicationContext#refresh()
	 * @see ConfigurableApplicationContext#getBeanFactory()
	 */
	AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;

}
