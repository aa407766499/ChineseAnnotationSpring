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

package org.springframework.context;

import java.io.Closeable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.lang.Nullable;

/**
 * SPI interface to be implemented by most if not all application contexts.
 * 大多数（非全部）应用上下文要实现该SPI(服务提供接口)接口。除了在ApplicationContext
 * Provides facilities to configure an application context in addition
 * 接口中的客户端方法之外，提供了配置一个应用上下文的方法。
 * to the application context client methods in the
 * {@link org.springframework.context.ApplicationContext} interface.
 *
 * <p>Configuration and lifecycle methods are encapsulated here to avoid
 * 将配置方法和生命周期方法放在这里是为了对ApplicationContext客户端代码隐藏。
 * making them obvious to ApplicationContext client code. The present
 * 当前这些方法应该只被用在启动和关闭代码中。
 * methods should only be used by startup and shutdown code.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 03.11.2003
 */
public interface ConfigurableApplicationContext extends ApplicationContext, Lifecycle, Closeable {

	/**
	 * Any number of these characters are considered delimiters between
	 * 在单个字符串类型值中，可以使用这些字符作为多个上下文配置路径的分隔符。
	 * multiple context config paths in a single String value.
	 * @see org.springframework.context.support.AbstractXmlApplicationContext#setConfigLocation
	 * @see org.springframework.web.context.ContextLoader#CONFIG_LOCATION_PARAM
	 * @see org.springframework.web.servlet.FrameworkServlet#setContextConfigLocation
	 */
	String CONFIG_LOCATION_DELIMITERS = ",; \t\n";

	/**
	 * Name of the ConversionService bean in the factory.
	 * 容器中ConversionService的名称。如果没有提供，使用默认值。
	 * If none is supplied, default conversion rules apply.
	 * @see org.springframework.core.convert.ConversionService
	 * @since 3.0
	 */
	String CONVERSION_SERVICE_BEAN_NAME = "conversionService";

	/**
	 * Name of the LoadTimeWeaver bean in the factory. If such a bean is supplied,
	 * 容器中LoadTimeWeaver bean名称。如果提供了该bean，上下文将使用临时类加载器用于
	 * the context will use a temporary ClassLoader for type matching, in order
	 * 类型匹配，这是为了能用LoadTimeWeaver处理所有bean类。
	 * to allow the LoadTimeWeaver to process all actual bean classes.
	 * @since 2.5
	 * @see org.springframework.instrument.classloading.LoadTimeWeaver
	 */
	String LOAD_TIME_WEAVER_BEAN_NAME = "loadTimeWeaver";

	/**
	 * Name of the {@link Environment} bean in the factory.
	 * 容器中Environment bean名称
	 * @since 3.1
	 */
	String ENVIRONMENT_BEAN_NAME = "environment";

	/**
	 * Name of the System properties bean in the factory.
	 * 容器中System属性bean名称
	 * @see java.lang.System#getProperties()
	 */
	String SYSTEM_PROPERTIES_BEAN_NAME = "systemProperties";

	/**
	 * Name of the System environment bean in the factory.
	 * 容器中环境bean名称
	 * @see java.lang.System#getenv()
	 */
	String SYSTEM_ENVIRONMENT_BEAN_NAME = "systemEnvironment";


	/**
	 * Set the unique id of this application context.
	 * 设置该应用上下文id。
	 * @since 3.0
	 */
	void setId(String id);

	/**
	 * Set the parent of this application context.
	 * 设置该应用上下文父容器。
	 * <p>Note that the parent shouldn't be changed: It should only be set outside
	 * 注意：父容器不能被更改。在该类的实例创建的时候如果不能获得父容器，则父容器只能
	 * a constructor if it isn't available when an object of this class is created,
	 * 通过外部构造器设置，比如WebApplicationContext的创建。
	 * for example in case of WebApplicationContext setup.
	 * @param parent the parent context
	 * @see org.springframework.web.context.ConfigurableWebApplicationContext
	 */
	void setParent(@Nullable ApplicationContext parent);

	/**
	 * Set the {@code Environment} for this application context.
	 * 设置应用上下文的环境
	 * @param environment the new environment
	 * @since 3.1
	 */
	void setEnvironment(ConfigurableEnvironment environment);

	/**
	 * Return the {@code Environment} for this application context in configurable
	 * 返回应用上下文的可配置环境，允许进一步定制
	 * form, allowing for further customization.
	 * @since 3.1
	 */
	@Override
	ConfigurableEnvironment getEnvironment();

	/**
	 * Add a new BeanFactoryPostProcessor that will get applied to the internal
	 * 在解析任何bean定义之前，添加新的BeanFactoryPostProcessor，在应用上下文刷新
	 * bean factory of this application context on refresh, before any of the
	 * 的时候将其应用于内部bean容器。
	 * bean definitions get evaluated. To be invoked during context configuration.
	 * @param postProcessor the factory processor to register
	 */
	void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);

	/**
	 * Add a new ApplicationListener that will be notified on context events
	 * 添加一个新的应用监听器，当发生容器事件（容器刷新和关闭）的时候得到通知。
	 * such as context refresh and context shutdown.
	 * <p>Note that any ApplicationListener registered here will be applied
	 * 注意：如果上下文没有启动，则这里注册的所有ApplicationListener都会监听刷新事件，
	 * on refresh if the context is not active yet, or on the fly with the
	 * 如果上下文已经处于活动状态，则使用当前事件传播器。
	 * current event multicaster in case of a context that is already active.
	 *
	 * @param listener the ApplicationListener to register
	 * @see org.springframework.context.event.ContextRefreshedEvent
	 * @see org.springframework.context.event.ContextClosedEvent
	 */
	void addApplicationListener(ApplicationListener<?> listener);

	/**
	 * Register the given protocol resolver with this application context,
	 * 给该应用上下文注册给定的协议解析器，这样可以处理另外的资源协议。
	 * allowing for additional resource protocols to be handled.
	 * <p>Any such resolver will be invoked ahead of this context's standard
	 * 在上下文进行标准解析之前会调用这种解析器。因此该解析器也可以重写默认规则
	 * resolution rules. It may therefore also override any default rules.
	 * @since 4.3
	 */
	void addProtocolResolver(ProtocolResolver resolver);

	/**
	 * Load or refresh the persistent representation of the configuration,
	 * 加载或者刷新配置的持久化表示，可以使XML文件，属性文件，或者关系型数据库表。
	 * which might an XML file, properties file, or relational database schema.
	 * <p>As this is a startup method, it should destroy already created singletons
	 * 这是一个启动方法，如果该方法失败应该销毁已创建的单例，这样防止使用不稳定的资源。
	 * if it fails, to avoid dangling resources. In other words, after invocation
	 * 换句话说，调用这个方法之后，要么所有的单例被初始化，要么没有单例被初始化。
	 * of that method, either all or no singletons at all should be instantiated.
	 * @throws BeansException if the bean factory could not be initialized
	 * @throws IllegalStateException if already initialized and multiple refresh
	 * 如果已经初始化，以及不支持多次尝试刷新。
	 * attempts are not supported
	 */
	void refresh() throws BeansException, IllegalStateException;

	/**
	 * Register a shutdown hook with the JVM runtime, closing this context
	 * JVM运行期注册关闭钩子，除非上下文已经关闭否则在JVM关闭时关闭上下文。
	 * on JVM shutdown unless it has already been closed at that time.
	 * <p>This method can be called multiple times. Only one shutdown hook
	 * 这个方法可以被多次调用。每个上下文实例（至多）注册一个关闭钩子。
	 * (at max) will be registered for each context instance.
	 * @see java.lang.Runtime#addShutdownHook
	 * @see #close()
	 */
	void registerShutdownHook();

	/**
	 * Close this application context, releasing all resources and locks that the
	 * 关闭该应用上下文，释放该实例持有的所有的资源和锁。这还包括销毁所有缓存的单例bean。
	 * implementation might hold. This includes destroying all cached singleton beans.
	 * <p>Note: Does <i>not</i> invoke {@code close} on a parent context;
	 * 注意：不要在父容器中调用该方法；父容器有其自己独立的生命周期。
	 * parent contexts have their own, independent lifecycle.
	 * <p>This method can be called multiple times without side effects: Subsequent
	 * 多次调用该方法没有副作用：对已经关闭的上下文频繁调用close会被忽略掉。
	 * {@code close} calls on an already closed context will be ignored.
	 */
	@Override
	void close();

	/**
	 * Determine whether this application context is active, that is,
	 * 确定该应用上下文是否活跃，换句话说，确认其是否被至少刷新过一次或者目前没有关闭。
	 * whether it has been refreshed at least once and has not been closed yet.
	 * @return whether the context is still active
	 * @see #refresh()
	 * @see #close()
	 * @see #getBeanFactory()
	 */
	boolean isActive();

	/**
	 * Return the internal bean factory of this application context.
	 * 返回该应用上下文的内部bean容器。
	 * Can be used to access specific functionality of the underlying factory.
	 * 该方法专门用于访问底层的容器。
	 * <p>Note: Do not use this to post-process the bean factory; singletons
	 * 不要使用该方法去后置处理bean容器；该方法调用之前所有的单例bean已经被初始化了。
	 * will already have been instantiated before. Use a BeanFactoryPostProcessor
	 * 在接触bean之前，使用BeanFactoryPostProcessor拦截BeanFactory的创建过程。
	 * to intercept the BeanFactory setup process before beans get touched.
	 * <p>Generally, this internal factory will only be accessible while the context
	 * 通常，这个内部容器只有在上下文活跃的时候才能被访问，换句话说，在refresh()调用
	 * is active, that is, inbetween {@link #refresh()} and {@link #close()}.
	 * 之后，close()方法调用之前。
	 * The {@link #isActive()} flag can be used to check whether the context
	 * isActive()标志能够用来检查上下文是否处于合适的状态。
	 * is in an appropriate state.
	 * @return the underlying bean factory
	 * @throws IllegalStateException if the context does not hold an internal
	 * 如果目前refresh()没有被调用或者close()已经被调用，该上文就没有持有内部bean容器
	 * bean factory (usually if {@link #refresh()} hasn't been called yet or
	 * if {@link #close()} has already been called)
	 * @see #isActive()
	 * @see #refresh()
	 * @see #close()
	 * @see #addBeanFactoryPostProcessor
	 */
	ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

}
