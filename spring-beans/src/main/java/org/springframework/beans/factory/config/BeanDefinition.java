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

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.AttributeAccessor;
import org.springframework.lang.Nullable;

/**
 * A BeanDefinition describes a bean instance, which has property values,
 * 一个BeanDefinition描述一个bean实例，该实例中有属性值，构造器参数值，
 * constructor argument values, and further information supplied by
 * 更多的信息由具体实现提供。
 * concrete implementations.
 *
 * <p>This is just a minimal interface: The main intention is to allow a
 * 这是一个最小接口：其目的是允许BeanFactoryPostProcessor比如PropertyPlaceholderConfigurer
 * {@link BeanFactoryPostProcessor} such as {@link PropertyPlaceholderConfigurer}
 * 拦截并修改属性值和其他bean元数据。
 * to introspect and modify property values and other bean metadata.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 19.03.2004
 * @see ConfigurableListableBeanFactory#getBeanDefinition
 * @see org.springframework.beans.factory.support.RootBeanDefinition
 * @see org.springframework.beans.factory.support.ChildBeanDefinition
 */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {

	/**
	 * Scope identifier for the standard singleton scope: "singleton".
	 * <p>Note that extended bean factories might support further scopes.
	 * @see #setScope
	 */
	String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

	/**
	 * Scope identifier for the standard prototype scope: "prototype".
	 * <p>Note that extended bean factories might support further scopes.
	 * @see #setScope
	 */
	String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;


	/**
	 * Role hint indicating that a {@code BeanDefinition} is a major part
	 * 角色提示，该参数表明一个BeanDefinition是应用的主要部分。通常对应于
	 * of the application. Typically corresponds to a user-defined bean.
	 * 用户自定义的bean
	 */
	int ROLE_APPLICATION = 0;

	/**
	 * Role hint indicating that a {@code BeanDefinition} is a supporting
	 * 该参数表明一个bean定义是某些大配置的支持部分，通常
	 * part of some larger configuration, typically an outer
	 * 在仔细查看特定的ComponentDefinition时，需要看该ComponentDefinition
	 * {@link org.springframework.beans.factory.parsing.ComponentDefinition}.
	 * 最重要的支持bean，而不是查看应用的全部配置。
	 * {@code SUPPORT} beans are considered important enough to be aware
	 * of when looking more closely at a particular
	 * {@link org.springframework.beans.factory.parsing.ComponentDefinition},
	 * but not when looking at the overall configuration of an application.
	 */
	int ROLE_SUPPORT = 1;

	/**
	 * Role hint indicating that a {@code BeanDefinition} is providing an
	 * 该参数表明，该BeanDefinition完全是一个后台角色，与终端用户没有关联。该
	 * entirely background role and has no relevance to the end-user. This hint is
	 * 参数在注册ComponentDefinition内部操作bean时使用。
	 * used when registering beans that are completely part of the internal workings
	 * of a {@link org.springframework.beans.factory.parsing.ComponentDefinition}.
	 */
	int ROLE_INFRASTRUCTURE = 2;


	// Modifiable attributes
	// 可修改的属性

	/**
	 * Set the name of the parent definition of this bean definition, if any.
	 * 如果有的话设置该bean定义的父bean定义的名称。
	 */
	void setParentName(@Nullable String parentName);

	/**
	 * Return the name of the parent definition of this bean definition, if any.
	 * 如果有的话返回该bean定义的父bean定义的名称。
	 */
	@Nullable
	String getParentName();

	/**
	 * Specify the bean class name of this bean definition.
	 * 指定该bean定义的bean类名。
	 * <p>The class name can be modified during bean factory post-processing,
	 * 在bean工厂后置处理期间可以修改该类名，通常用解析的类名替换原来的类名。
	 * typically replacing the original class name with a parsed variant of it.
	 * @see #setParentName
	 * @see #setFactoryBeanName
	 * @see #setFactoryMethodName
	 */
	void setBeanClassName(@Nullable String beanClassName);

	/**
	 * Return the current bean class name of this bean definition.
	 * 返回该bean定义的当前bean类名。
	 * <p>Note that this does not have to be the actual class name used at runtime, in
	 * 注意：在运行期并不是要使用实际的类名，子bean定义会重写/继承父bean定义的类名。
	 * case of a child definition overriding/inheriting the class name from its parent.
	 * 这也可以是调用工厂方法生成的类，或者调用工厂bean引用的方法时为空。因此，这不是要在
	 * Also, this may just be the class that a factory method is called on, or it may
	 * 运行期确定bean的类型，而是在个别bean定义等级仅用于解析目的。
	 * even be empty in case of a factory bean reference that a method is called on.
	 * Hence, do <i>not</i> consider this to be the definitive bean type at runtime but
	 * rather only use it for parsing purposes at the individual bean definition level.
	 * @see #getParentName()
	 * @see #getFactoryBeanName()
	 * @see #getFactoryMethodName()
	 */
	@Nullable
	String getBeanClassName();

	/**
	 * Override the target scope of this bean, specifying a new scope name.
	 * 重写该bean的目标作用域，指定新的作用域名称。
	 * @see #SCOPE_SINGLETON
	 * @see #SCOPE_PROTOTYPE
	 */
	void setScope(@Nullable String scope);

	/**
	 * Return the name of the current target scope for this bean,
	 * 返回当前该bean的目标作用域名称，如果没有则为null。
	 * or {@code null} if not known yet.
	 */
	@Nullable
	String getScope();

	/**
	 * Set whether this bean should be lazily initialized.
	 * 设置该bean是否懒初始化。
	 * <p>If {@code false}, the bean will get instantiated on startup by bean
	 * 如果设置false，该bean将在容器启动执行单例饿汉式初始化时进行初始化。
	 * factories that perform eager initialization of singletons.
	 */
	void setLazyInit(boolean lazyInit);

	/**
	 * Return whether this bean should be lazily initialized, i.e. not
	 * 返回该bean是否懒初始化，启动时不进行饿汉式初始化。仅用于单例bean。
	 * eagerly instantiated on startup. Only applicable to a singleton bean.
	 */
	boolean isLazyInit();

	/**
	 * Set the names of the beans that this bean depends on being initialized.
	 * 设置要被初始化的该bean依赖的bean的名称。容器会保证这些bean先初始化。
	 * The bean factory will guarantee that these beans get initialized first.
	 */
	void setDependsOn(@Nullable String... dependsOn);

	/**
	 * Return the bean names that this bean depends on.
	 * 返回该bean依赖的bean名称。
	 */
	@Nullable
	String[] getDependsOn();

	/**
	 * Set whether this bean is a candidate for getting autowired into some other bean.
	 * 设置该bean是否适合自动注入到其他bean中。
	 * <p>Note that this flag is designed to only affect type-based autowiring.
	 * 注意:这个标志被设计为仅影响基于类型的自动注入。其不会影响基于名称的自动注入，
	 * It does not affect explicit references by name, which will get resolved even
	 * 即使该bean没有被标记为自动注入合格者，其名称也会被解析。所以，如果名称匹配，
	 * if the specified bean is not marked as an autowire candidate. As a consequence,
	 * 就会根据名称自动注入一个bean。
	 * autowiring by name will nevertheless inject a bean if the name matches.
	 */
	void setAutowireCandidate(boolean autowireCandidate);

	/**
	 * Return whether this bean is a candidate for getting autowired into some other bean.
	 * 返回该bean是否适合自动注入到其他bean中。
	 */
	boolean isAutowireCandidate();

	/**
	 * Set whether this bean is a primary autowire candidate.
	 * 设置该bean是否优先注入。
	 * <p>If this value is {@code true} for exactly one bean among multiple
	 * 如果该值为true，则会从多个匹配的候选者中精确选择一个bean，其将作为一个断路器
	 * matching candidates, it will serve as a tie-breaker.
	 */
	void setPrimary(boolean primary);

	/**
	 * Return whether this bean is a primary autowire candidate.
	 * 返回该bean是否优先注入。
	 */
	boolean isPrimary();

	/**
	 * Specify the factory bean to use, if any.
	 * 如果有的话，指定使用的工厂bean。
	 * This the name of the bean to call the specified factory method on.
	 * 该bean名称回去调用指定的工厂方法。
	 * @see #setFactoryMethodName
	 */
	void setFactoryBeanName(@Nullable String factoryBeanName);

	/**
	 * Return the factory bean name, if any.
	 * 如果有的话，返回工厂bean名称。
	 */
	@Nullable
	String getFactoryBeanName();

	/**
	 * Specify a factory method, if any. This method will be invoked with
	 * 如果有的话，指定工厂方法。调用该方法会传入构造器参数，或者如果没有
	 * constructor arguments, or with no arguments if none are specified.
	 * 传参调用无参构造。如果有的话，该方法由特定的工厂bean调用，或者
	 * The method will be invoked on the specified factory bean, if any,
	 * 作为一个静态方法由本地bean类调用。
	 * or otherwise as a static method on the local bean class.
	 * @see #setFactoryBeanName
	 * @see #setBeanClassName
	 */
	void setFactoryMethodName(@Nullable String factoryMethodName);

	/**
	 * Return a factory method, if any.
	 * 如果有的话，返回工厂方法。
	 */
	@Nullable
	String getFactoryMethodName();

	/**
	 * Return the constructor argument values for this bean.
	 * 返回该bean的构造器参数值。
	 * <p>The returned instance can be modified during bean factory post-processing.
	 * 在bean容器后置处理期间可以修改返回的bean实例。
	 * @return the ConstructorArgumentValues object (never {@code null})
	 */
	ConstructorArgumentValues getConstructorArgumentValues();

	/**
	 * Return if there are constructor argument values defined for this bean.
	 * 返回是否有该bean定义的构造器参数值。
	 * @since 5.0.2
	 */
	default boolean hasConstructorArgumentValues() {
		return !getConstructorArgumentValues().isEmpty();
	}

	/**
	 * Return the property values to be applied to a new instance of the bean.
	 * 返回应用于该bean新实例的属性值。
	 * <p>The returned instance can be modified during bean factory post-processing.
	 * 在bean容器后置处理期间可以修改返回的bean实例。
	 * @return the MutablePropertyValues object (never {@code null})
	 */
	MutablePropertyValues getPropertyValues();

	/**
	 * Return if there are property values values defined for this bean.
	 * 返回该bean定义是否有属性值。
	 * @since 5.0.2
	 */
	default boolean hasPropertyValues() {
		return !getPropertyValues().isEmpty();
	}


	// Read-only attributes
	// 只读属性

	/**
	 * Return whether this a <b>Singleton</b>, with a single, shared instance
	 * 返回是否是单例，每次调用都返回单个共享的实例
	 * returned on all calls.
	 * @see #SCOPE_SINGLETON
	 */
	boolean isSingleton();

	/**
	 * Return whether this a <b>Prototype</b>, with an independent instance
	 * 返回是否是原型，每次调用都返回独立的实例。
	 * returned for each call.
	 * @since 3.0
	 * @see #SCOPE_PROTOTYPE
	 */
	boolean isPrototype();

	/**
	 * Return whether this bean is "abstract", that is, not meant to be instantiated.
	 * 返回该bean是否是抽象，换句话说，不能被实例化。
	 */
	boolean isAbstract();

	/**
	 * Get the role hint for this {@code BeanDefinition}. The role hint
	 * 获取该bean定义的角色参数。该角色参数表明一个特定的BeanDefinition
	 * provides the frameworks as well as tools with an indication of
	 * 对于框架和工具的重要性和角色象征。
	 * the role and importance of a particular {@code BeanDefinition}.
	 * @see #ROLE_APPLICATION
	 * @see #ROLE_SUPPORT
	 * @see #ROLE_INFRASTRUCTURE
	 */
	int getRole();

	/**
	 * Return a human-readable description of this bean definition.
	 * 返回该bean定义易读的描述。
	 */
	@Nullable
	String getDescription();

	/**
	 * Return a description of the resource that this bean definition
	 * 返回该bean定义的资源的描述（为了发生错误的时候展示上下文）
	 * came from (for the purpose of showing context in case of errors).
	 */
	@Nullable
	String getResourceDescription();

	/**
	 * Return the originating BeanDefinition, or {@code null} if none.
	 * 返回原始的BeanDefinition，没有返回null。如果有的话，允许提取
	 * Allows for retrieving the decorated bean definition, if any.
	 * 包装过的bean定义。
	 * <p>Note that this method returns the immediate originator. Iterate through the
	 * 注意：该方法返回当前原始bean定义。通过迭代原始bean定义链来查找用户定义的
	 * originator chain to find the original BeanDefinition as defined by the user.
	 * 原始bean定义。
	 */
	@Nullable
	BeanDefinition getOriginatingBeanDefinition();

}
