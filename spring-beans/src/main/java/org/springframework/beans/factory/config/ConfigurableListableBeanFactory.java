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

package org.springframework.beans.factory.config;

import java.util.Iterator;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.lang.Nullable;

/**
 * Configuration interface to be implemented by most listable bean factories.
 * 许多bean集合容器需要实现的配置接口。除了ConfigurableBeanFactory以外，该接口
 * In addition to {@link ConfigurableBeanFactory}, it provides facilities to
 * 提供工具去分析和修改bean定义，以及预实例化单例。
 * analyze and modify bean definitions, and to pre-instantiate singletons.
 *
 * <p>This subinterface of {@link org.springframework.beans.factory.BeanFactory}
 * 该接口为BeanFactory的子接口，在正常应用代码中不会使用。该接口和
 * is not meant to be used in normal application code: Stick to
 * BeanFactory或者ListableBeanFactory一起使用。这个扩展接口仅供于框架内部的插件使用，
 * {@link org.springframework.beans.factory.BeanFactory} or
 * {@link org.springframework.beans.factory.ListableBeanFactory} for typical
 * use cases. This interface is just meant to allow for framework-internal
 * plug'n'play even when needing access to bean factory configuration methods.
 *
 * @author Juergen Hoeller
 * @since 03.11.2003
 * @see org.springframework.context.support.AbstractApplicationContext#getBeanFactory()
 */
public interface ConfigurableListableBeanFactory
		extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

	/**
	 * Ignore the given dependency type for autowiring:
	 * 自动织入时忽略给定依赖类型
	 * for example, String. Default is none.
	 * @param type the dependency type to ignore
	 */
	void ignoreDependencyType(Class<?> type);

	/**
	 * Ignore the given dependency interface for autowiring.
	 * 自动织入时忽略给定依赖接口。
	 * <p>This will typically be used by application contexts to register
	 * 通常该方法会被注册了使用其他方式解析的依赖的应用上下文使用，比如通过BeanFactoryAware
	 * dependencies that are resolved in other ways, like BeanFactory through
	 * 注册BeanFactory或者通过ApplicationContextAware注册ApplicationContext。
	 * BeanFactoryAware or ApplicationContext through ApplicationContextAware.
	 * <p>By default, only the BeanFactoryAware interface is ignored.
	 * 默认情况下，仅会忽略BeanFactoryAware接口。
	 * For further types to ignore, invoke this method for each type.
	 * 对于要忽略的其他类型，请为每种类型调用此方法
	 * @param ifc the dependency interface to ignore
	 * @see org.springframework.beans.factory.BeanFactoryAware
	 * @see org.springframework.context.ApplicationContextAware
	 */
	void ignoreDependencyInterface(Class<?> ifc);

	/**
	 * Register a special dependency type with corresponding autowired value.
	 * 给相应的被自动织入的值注册特定的依赖类型。
	 * <p>This is intended for factory/context references that are supposed
	 * 这适用于工厂/上下文引用，该引用应该能够自动织入但是在容器中没有定义：
	 * to be autowirable but are not defined as beans in the factory:
	 * 比如
	 * e.g. a dependency of type ApplicationContext resolved to the
	 * ApplicationContext依赖类型被解析为bean所在的ApplicationContext实例。
	 * ApplicationContext instance that the bean is living in.
	 * <p>Note: There are no such default types registered in a plain BeanFactory,
	 * 请注意：在普通BeanFactory中没有注册这种默认类型，即使BeanFactory自身也没有。
	 * not even for the BeanFactory interface itself.
	 * @param dependencyType the dependency type to register. This will typically
	 *          			   注册的依赖类型。通常是一个基础接口比如BeanFactory，
	 * be a base interface such as BeanFactory, with extensions of it resolved
	 * 如果被声明为自动织入依赖，BeanFactory的扩展接口也能解析（比如ListableBeanFactory），
	 * as well if declared as an autowiring dependency (e.g. ListableBeanFactory),
	 * 主要给定值实现了扩展接口。
	 * as long as the given value actually implements the extended interface.
	 * @param autowiredValue the corresponding autowired value. This may also be an
	 *                       对应被自动织入的值。该值也可以是ObjectFactory接口的实现，该接口
	 * implementation of the {@link org.springframework.beans.factory.ObjectFactory}
	 *                       允许对实际目标值进行懒解析。
	 * interface, which allows for lazy resolution of the actual target value.
	 */
	void registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue);

	/**
	 * Determine whether the specified bean qualifies as an autowire candidate,
	 * 确定指定的bean是否符合自动织入的条件，将其注入到声明了匹配类型依赖的其他bean中。
	 * to be injected into other beans which declare a dependency of matching type.
	 * <p>This method checks ancestor factories as well.
	 * 这个方法会也检查父容器。
	 * @param beanName the name of the bean to check
	 * @param descriptor the descriptor of the dependency to resolve
	 * @return whether the bean should be considered as autowire candidate
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 */
	boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor)
			throws NoSuchBeanDefinitionException;

	/**
	 * Return the registered BeanDefinition for the specified bean, allowing access
	 * 返回指定bean注册的bean定义，可以访问其属性值以及构造器参数值（在bean容器后置
	 * to its property values and constructor argument value (which can be
	 * 处理中可以改变这些值）
	 * modified during bean factory post-processing).
	 * <p>A returned BeanDefinition object should not be a copy but the original
	 * 返回的BeanDefinition对象不是副本而是容器中注册的原定义对象。这使得在必要时
	 * definition object as registered in the factory. This means that it should
	 * 可以将它转换成更加专门的实现类型。
	 * be castable to a more specific implementation type, if necessary.
	 * <p><b>NOTE:</b> This method does <i>not</i> consider ancestor factories.
	 * 注意这个方法没有考虑父容器，它仅仅访问本地容器中的bean定义
	 * It is only meant for accessing local bean definitions of this factory.
	 * @param beanName the name of the bean
	 * @return the registered BeanDefinition
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * defined in this factory
	 */
	BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * Return a unified view over all bean names managed by this factory.
	 * 返回该容器中所有bean名称的一致性视图。，
	 * <p>Includes bean definition names as well as names of manually registered
	 * 包括bean定义的名称还有手工注册的单例实例的名称，bean定义名称排在前面
	 * singleton instances, with bean definition names consistently coming first,
	 * 类似于bean名称的类型/注解特殊检索的工作方式。
	 * analogous to how type/annotation specific retrieval of bean names works.
	 * @return the composite iterator for the bean names view
	 * @since 4.1.2
	 * @see #containsBeanDefinition
	 * @see #registerSingleton
	 * @see #getBeanNamesForType
	 * @see #getBeanNamesForAnnotation
	 */
	Iterator<String> getBeanNamesIterator();

	/**
	 * Clear the merged bean definition cache, removing entries for beans
	 * 清楚已覆盖的bean定义缓存，移除所有不适合全部元数据缓存的bean。
	 * which are not considered eligible for full metadata caching yet.
	 * <p>Typically triggered after changes to the original bean definitions,
	 * 通常在原bean定义改变之后出发，比如应用BeanFactoryPostProcessor之后。
	 * e.g. after applying a {@link BeanFactoryPostProcessor}. Note that metadata
	 * 注意：在这里已经被创建的bean的元数据会被保存。
	 * for beans which have already been created at this point will be kept around.
	 * @since 4.2
	 * @see #getBeanDefinition
	 * @see #getMergedBeanDefinition
	 */
	void clearMetadataCache();

	/**
	 * Freeze all bean definitions, signalling that the registered bean definitions
	 * 冻结所有bean定义，通知已注册的bean定义将不被修改或者进一步后置处理，
	 * will not be modified or post-processed any further.
	 * <p>This allows the factory to aggressively cache bean definition metadata.
	 * 这使得容器积极地缓存bean定义元数据。
	 *
	 */
	void freezeConfiguration();

	/**
	 * Return whether this factory's bean definitions are frozen,
	 * 返回该容器中的bean定义是否被冻结。
	 * i.e. are not supposed to be modified or post-processed any further.
	 * @return {@code true} if the factory's configuration is considered frozen
	 */
	boolean isConfigurationFrozen();

	/**
	 * Ensure that all non-lazy-init singletons are instantiated, also considering
	 * 确保所有非懒加载的单例被实例化，FactoryBeans也考虑在内。
	 * {@link org.springframework.beans.factory.FactoryBean FactoryBeans}.
	 * Typically invoked at the end of factory setup, if desired.
	 * 如果要用的话，通常在容器创建完成是调用。
	 * @throws BeansException if one of the singleton beans could not be created.
	 * Note: This may have left the factory with some beans already initialized!
	 * 注意：这会使得工厂和有些bean都被初始化。这种情况下完全清理需要调用destroySingletons()
	 * Call {@link #destroySingletons()} for full cleanup in this case.
	 * @see #destroySingletons()
	 */
	void preInstantiateSingletons() throws BeansException;

}
