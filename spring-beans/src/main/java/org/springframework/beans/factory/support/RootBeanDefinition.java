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

package org.springframework.beans.factory.support;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A root bean definition represents the merged bean definition that backs
 * 根Bean定义用于表示在运行时在SpringBean容器中备份特定Bean的合并Bean定义。
 * a specific bean in a Spring BeanFactory at runtime. It might have been created
 * 它可能是从相互继承的多个原始bean定义中创建的，通常注册为GenericBeanDefinitions。
 * from multiple original bean definitions that inherit from each other,
 * 根bean定义本质上是运行期统一的bean定义视图。
 * typically registered as {@link GenericBeanDefinition GenericBeanDefinitions}.
 * A root bean definition is essentially the 'unified' bean definition view at runtime.
 *
 * <p>Root bean definitions may also be used for registering individual bean definitions
 * 在配置阶段，根bean定义也可以被用于注册单个bean定义。然而，自从Spring 2.5，最好是通过编程
 * in the configuration phase. However, since Spring 2.5, the preferred way to register
 * 的方式将bean定义注册为GenericBeanDefinition类。和根bean定义'hard-coding'角色不同，
 * bean definitions programmatically is the {@link GenericBeanDefinition} class.
 * GenericBeanDefinition有其优势，其能够动态定义父依赖。
 * GenericBeanDefinition has the advantage that it allows to dynamically define
 * parent dependencies, not 'hard-coding' the role as a root bean definition.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see GenericBeanDefinition
 * @see ChildBeanDefinition
 */
@SuppressWarnings("serial")
public class RootBeanDefinition extends AbstractBeanDefinition {

	@Nullable
	private BeanDefinitionHolder decoratedDefinition;

	@Nullable
	private AnnotatedElement qualifiedElement;

	boolean allowCaching = true;

	boolean isFactoryMethodUnique = false;

	@Nullable
	volatile ResolvableType targetType;

	/** Package-visible field for caching the determined Class of a given bean definition */
	@Nullable
	volatile Class<?> resolvedTargetType;

	/** Package-visible field for caching the return type of a generically typed factory method */
	@Nullable
	volatile ResolvableType factoryMethodReturnType;

	/** Common lock for the four constructor fields below */
	/*以下4个构造器字段的公共锁*/
	final Object constructorArgumentLock = new Object();

	/** Package-visible field for caching the resolved constructor or factory method */
	/*包可见字段，缓存了已解析的构造器或者工厂方法*/
	@Nullable
	Executable resolvedConstructorOrFactoryMethod;

	/** Package-visible field that marks the constructor arguments as resolved */
	/*包可见字段，标记构造器参数已解析*/
	boolean constructorArgumentsResolved = false;

	/** Package-visible field for caching fully resolved constructor arguments */
	@Nullable
	Object[] resolvedConstructorArguments;

	/** Package-visible field for caching partly prepared constructor arguments */
	@Nullable
	Object[] preparedConstructorArguments;

	/** Common lock for the two post-processing fields below */
	/*下面两个后置处理字段的公共锁*/
	final Object postProcessingLock = new Object();

	/** Package-visible field that indicates MergedBeanDefinitionPostProcessor having been applied */
	/*包可见字段，该字段表示已经应用了MergedBeanDefinitionPostProcessor*/
	boolean postProcessed = false;

	/** Package-visible field that indicates a before-instantiation post-processor having kicked in */
	/*包可见字段，该字段指示启用实例化前后处理器。*/
	@Nullable
	volatile Boolean beforeInstantiationResolved;

	@Nullable
	private Set<Member> externallyManagedConfigMembers;

	@Nullable
	private Set<String> externallyManagedInitMethods;

	@Nullable
	private Set<String> externallyManagedDestroyMethods;


	/**
	 * Create a new RootBeanDefinition, to be configured through its bean
	 * properties and configuration methods.
	 * @see #setBeanClass
	 * @see #setScope
	 * @see #setConstructorArgumentValues
	 * @see #setPropertyValues
	 */
	public RootBeanDefinition() {
		super();
	}

	/**
	 * Create a new RootBeanDefinition for a singleton.
	 * @param beanClass the class of the bean to instantiate
	 * @see #setBeanClass
	 */
	public RootBeanDefinition(@Nullable Class<?> beanClass) {
		super();
		setBeanClass(beanClass);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton bean, constructing each instance
	 * through calling the given supplier (possibly a lambda or method reference).
	 * @param beanClass the class of the bean to instantiate
	 * @param instanceSupplier the supplier to construct a bean instance,
	 * as an alternative to a declaratively specified factory method
	 * @since 5.0
	 * @see #setInstanceSupplier
	 */
	public <T> RootBeanDefinition(@Nullable Class<T> beanClass, @Nullable Supplier<T> instanceSupplier) {
		super();
		setBeanClass(beanClass);
		setInstanceSupplier(instanceSupplier);
	}

	/**
	 * Create a new RootBeanDefinition for a scoped bean, constructing each instance
	 * through calling the given supplier (possibly a lambda or method reference).
	 * @param beanClass the class of the bean to instantiate
	 * @param scope the name of the corresponding scope
	 * @param instanceSupplier the supplier to construct a bean instance,
	 * as an alternative to a declaratively specified factory method
	 * @since 5.0
	 * @see #setInstanceSupplier
	 */
	public <T> RootBeanDefinition(@Nullable Class<T> beanClass, String scope, @Nullable Supplier<T> instanceSupplier) {
		super();
		setBeanClass(beanClass);
		setScope(scope);
		setInstanceSupplier(instanceSupplier);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * using the given autowire mode.
	 * @param beanClass the class of the bean to instantiate
	 * @param autowireMode by name or type, using the constants in this interface
	 * @param dependencyCheck whether to perform a dependency check for objects
	 * (not applicable to autowiring a constructor, thus ignored there)
	 */
	public RootBeanDefinition(@Nullable Class<?> beanClass, int autowireMode, boolean dependencyCheck) {
		super();
		setBeanClass(beanClass);
		setAutowireMode(autowireMode);
		if (dependencyCheck && getResolvedAutowireMode() != AUTOWIRE_CONSTRUCTOR) {
			setDependencyCheck(DEPENDENCY_CHECK_OBJECTS);
		}
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * providing constructor arguments and property values.
	 * @param beanClass the class of the bean to instantiate
	 * @param cargs the constructor argument values to apply
	 * @param pvs the property values to apply
	 */
	public RootBeanDefinition(@Nullable Class<?> beanClass, @Nullable ConstructorArgumentValues cargs,
			@Nullable MutablePropertyValues pvs) {

		super(cargs, pvs);
		setBeanClass(beanClass);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * providing constructor arguments and property values.
	 * <p>Takes a bean class name to avoid eager loading of the bean class.
	 * @param beanClassName the name of the class to instantiate
	 */
	public RootBeanDefinition(String beanClassName) {
		setBeanClassName(beanClassName);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,
	 * providing constructor arguments and property values.
	 * <p>Takes a bean class name to avoid eager loading of the bean class.
	 * @param beanClassName the name of the class to instantiate
	 * @param cargs the constructor argument values to apply
	 * @param pvs the property values to apply
	 */
	public RootBeanDefinition(String beanClassName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
		super(cargs, pvs);
		setBeanClassName(beanClassName);
	}

	/**
	 * Create a new RootBeanDefinition as deep copy of the given
	 * 对给定bean定义进行深拷贝创建新的RootBeanDefinition。
	 * bean definition.
	 * @param original the original bean definition to copy from
	 */
	public RootBeanDefinition(RootBeanDefinition original) {
		super(original);
		this.decoratedDefinition = original.decoratedDefinition;
		this.qualifiedElement = original.qualifiedElement;
		this.allowCaching = original.allowCaching;
		this.isFactoryMethodUnique = original.isFactoryMethodUnique;
		this.targetType = original.targetType;
	}

	/**
	 * Create a new RootBeanDefinition as deep copy of the given
	 * bean definition.
	 * @param original the original bean definition to copy from
	 */
	RootBeanDefinition(BeanDefinition original) {
		super(original);
	}


	@Override
	public String getParentName() {
		return null;
	}

	@Override
	public void setParentName(@Nullable String parentName) {
		if (parentName != null) {
			throw new IllegalArgumentException("Root bean cannot be changed into a child bean with parent reference");
		}
	}

	/**
	 * Register a target definition that is being decorated by this bean definition.
	 */
	public void setDecoratedDefinition(@Nullable BeanDefinitionHolder decoratedDefinition) {
		this.decoratedDefinition = decoratedDefinition;
	}

	/**
	 * Return the target definition that is being decorated by this bean definition, if any.
	 */
	@Nullable
	public BeanDefinitionHolder getDecoratedDefinition() {
		return this.decoratedDefinition;
	}

	/**
	 * Specify the {@link AnnotatedElement} defining qualifiers,
	 * to be used instead of the target class or factory method.
	 * @since 4.3.3
	 * @see #setTargetType(ResolvableType)
	 * @see #getResolvedFactoryMethod()
	 */
	public void setQualifiedElement(@Nullable AnnotatedElement qualifiedElement) {
		this.qualifiedElement = qualifiedElement;
	}

	/**
	 * Return the {@link AnnotatedElement} defining qualifiers, if any.
	 * Otherwise, the factory method and target class will be checked.
	 * @since 4.3.3
	 */
	@Nullable
	public AnnotatedElement getQualifiedElement() {
		return this.qualifiedElement;
	}

	/**
	 * Specify a generics-containing target type of this bean definition, if known in advance.
	 * @since 4.3.3
	 */
	public void setTargetType(ResolvableType targetType) {
		this.targetType = targetType;
	}

	/**
	 * Specify the target type of this bean definition, if known in advance.
	 * @since 3.2.2
	 */
	public void setTargetType(@Nullable Class<?> targetType) {
		this.targetType = (targetType != null ? ResolvableType.forClass(targetType) : null);
	}

	/**
	 * Return the target type of this bean definition, if known
	 * 如果有返回该bean定义的目标类型，（要么提前指定要么首次实例化时解析）
	 * (either specified in advance or resolved on first instantiation).
	 * @since 3.2.2
	 */
	@Nullable
	public Class<?> getTargetType() {
		if (this.resolvedTargetType != null) {
			return this.resolvedTargetType;
		}
		ResolvableType targetType = this.targetType;
		return (targetType != null ? targetType.resolve() : null);
	}

	/**
	 * Specify a factory method name that refers to a non-overloaded method.
	 */
	public void setUniqueFactoryMethodName(String name) {
		Assert.hasText(name, "Factory method name must not be empty");
		setFactoryMethodName(name);
		this.isFactoryMethodUnique = true;
	}

	/**
	 * Check whether the given candidate qualifies as a factory method.
	 */
	public boolean isFactoryMethod(Method candidate) {
		return candidate.getName().equals(getFactoryMethodName());
	}

	/**
	 * Return the resolved factory method as a Java Method object, if available.
	 * @return the factory method, or {@code null} if not found or not resolved yet
	 */
	@Nullable
	public Method getResolvedFactoryMethod() {
		synchronized (this.constructorArgumentLock) {
			Executable candidate = this.resolvedConstructorOrFactoryMethod;
			return (candidate instanceof Method ? (Method) candidate : null);
		}
	}

	public void registerExternallyManagedConfigMember(Member configMember) {
		synchronized (this.postProcessingLock) {
			if (this.externallyManagedConfigMembers == null) {
				this.externallyManagedConfigMembers = new HashSet<>(1);
			}
			this.externallyManagedConfigMembers.add(configMember);
		}
	}

	public boolean isExternallyManagedConfigMember(Member configMember) {
		synchronized (this.postProcessingLock) {
			return (this.externallyManagedConfigMembers != null &&
					this.externallyManagedConfigMembers.contains(configMember));
		}
	}

	public void registerExternallyManagedInitMethod(String initMethod) {
		synchronized (this.postProcessingLock) {
			if (this.externallyManagedInitMethods == null) {
				this.externallyManagedInitMethods = new HashSet<>(1);
			}
			this.externallyManagedInitMethods.add(initMethod);
		}
	}

	public boolean isExternallyManagedInitMethod(String initMethod) {
		synchronized (this.postProcessingLock) {
			return (this.externallyManagedInitMethods != null &&
					this.externallyManagedInitMethods.contains(initMethod));
		}
	}

	public void registerExternallyManagedDestroyMethod(String destroyMethod) {
		synchronized (this.postProcessingLock) {
			if (this.externallyManagedDestroyMethods == null) {
				this.externallyManagedDestroyMethods = new HashSet<>(1);
			}
			this.externallyManagedDestroyMethods.add(destroyMethod);
		}
	}

	public boolean isExternallyManagedDestroyMethod(String destroyMethod) {
		synchronized (this.postProcessingLock) {
			return (this.externallyManagedDestroyMethods != null &&
					this.externallyManagedDestroyMethods.contains(destroyMethod));
		}
	}


	@Override
	public RootBeanDefinition cloneBeanDefinition() {
		return new RootBeanDefinition(this);
	}

	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof RootBeanDefinition && super.equals(other)));
	}

	@Override
	public String toString() {
		return "Root bean: " + super.toString();
	}

}
