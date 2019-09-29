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

import java.beans.PropertyDescriptor;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.lang.Nullable;

/**
 * Subinterface of {@link BeanPostProcessor} that adds a before-instantiation callback,
 * BeanPostProcessor的子接口，添加了实例化前回调，以及在实例化后但明确属性设置或者
 * and a callback after instantiation but before explicit properties are set or
 * 自动注入发生前的回调。
 * autowiring occurs.
 *
 * <p>Typically used to suppress default instantiation for specific target beans,
 * 通常用于阻止指定目标bean的默认实例化，比如：为指定目标对象创建代理（池对象，懒加载
 * for example to create proxies with special TargetSources (pooling targets,
 * 初始化对象等），或者对于字段注入，实现另外的注入策略。
 * lazily initializing targets, etc), or to implement additional injection strategies
 * such as field injection.
 *
 * <p><b>NOTE:</b> This interface is a special purpose interface, mainly for
 * 注意该接口是一个特殊用途的接口，主要框架内部使用。建议最好尽可能的实现原生的
 * internal use within the framework. It is recommended to implement the plain
 * BeanPostProcessor接口或者扩展InstantiationAwareBeanPostProcessorAdapter，
 * {@link BeanPostProcessor} interface as far as possible, or to derive from
 * 这样可以隔离该接口的扩展。
 * {@link InstantiationAwareBeanPostProcessorAdapter} in order to be shielded
 * from extensions to this interface.
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @since 1.2
 * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#setCustomTargetSourceCreators
 * @see org.springframework.aop.framework.autoproxy.target.LazyInitTargetSourceCreator
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

	/**
	 * Apply this BeanPostProcessor <i>before the target bean gets instantiated</i>.
	 * 在目标bean实例化前应用该BeanPostProcessor。返回的bean对象可能是替换目标bean的
	 * The returned bean object may be a proxy to use instead of the target bean,
	 * 代理对象，有效的阻止目标bean的默认实例化。
	 * effectively suppressing default instantiation of the target bean.
	 * <p>If a non-null object is returned by this method, the bean creation process
	 * 如果该方法返回非空对象，该bean的创建过程会被短路。要进一步处理仅能使用配置的
	 * will be short-circuited. The only further processing applied is the
	 * BeanPostProcessors的postProcessAfterInitialization回调。
	 * {@link #postProcessAfterInitialization} callback from the configured
	 * {@link BeanPostProcessor BeanPostProcessors}.
	 * <p>This callback will only be applied to bean definitions with a bean class.
	 * 该回调仅能应用于bean class的bean定义。特别的，该回调不能应用于"factory-method"
	 * In particular, it will not be applied to beans with a "factory-method".
	 * 创建的bean。
	 * <p>Post-processors may implement the extended
	 * Post-processors可以实现扩展SmartInstantiationAwareBeanPostProcessor
	 * {@link SmartInstantiationAwareBeanPostProcessor} interface in order
	 * 接口为了预测该回调将要返回的对象类型。
	 * to predict the type of the bean object that they are going to return here.
	 * <p>The default implementation returns {@code null}.
	 * 默认实现返回null。
	 * @param beanClass the class of the bean to be instantiated
	 * @param beanName the name of the bean
	 * @return the bean object to expose instead of a default instance of the target bean,
	 * 替换目标bean默认实例的bean对象，或是null表示执行默认的实例化。
	 * or {@code null} to proceed with default instantiation
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#hasBeanClass
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getFactoryMethodName
	 */
	@Nullable
	default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

	/**
	 * Perform operations after the bean has been instantiated, via a constructor or factory method,
	 * 通过构造器或者工厂方法，bean已经实例化后要执行的操作，但是要在Spring属性填充发生之前（明确的属性
	 * but before Spring property population (from explicit properties or autowiring) occurs.
	 * 或者自动注入）
	 * <p>This is the ideal callback for performing custom field injection on the given bean
	 * 这是对给定bean实例进行自定义字段注入的理想回调，发生在Spring的自动注入前。
	 * instance, right before Spring's autowiring kicks in.
	 * <p>The default implementation returns {@code true}.
	 * 默认实现返回true。
	 * @param bean the bean instance created, with properties not having been set yet
	 * @param beanName the name of the bean
	 * @return {@code true} if properties should be set on the bean; {@code false}
	 * 如果bean属性应该被设置返回true；如果属性填充应该跳过返回false。一般应该返回true。
	 * if property population should be skipped. Normal implementations should return {@code true}.
	 * 返回false会阻止将后面任何的InstantiationAwareBeanPostProcessor应用于该bean实例。
	 * Returning {@code false} will also prevent any subsequent InstantiationAwareBeanPostProcessor
	 * instances being invoked on this bean instance.
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		return true;
	}

	/**
	 * Post-process the given property values before the factory applies them
	 * 在容器将属性值填充到bean之前，后处理给定的属性值。允许检查是否所有的依赖都已经
	 * to the given bean. Allows for checking whether all dependencies have been
	 * 满足，如果在bean属性设置器上的"Required"注解。
	 * satisfied, for example based on a "Required" annotation on bean property setters.
	 * <p>Also allows for replacing the property values to apply, typically through
	 * 也允许替换要应用的属性值，通常通过基于原有的PropertyValues创建新的MutablePropertyValues，
	 * creating a new MutablePropertyValues instance based on the original PropertyValues,
	 * 添加或者移除特定的值。
	 * adding or removing specific values.
	 * <p>The default implementation returns the given {@code pvs} as-is.
	 * 默认实现返回给定pvs自身
	 * @param pvs the property values that the factory is about to apply (never {@code null})
	 *            容器将要应用的属性值。
	 * @param pds the relevant property descriptors for the target bean (with ignored
	 * 目标bean相关的属性描述器（忽略已经过滤出的依赖类型-容器特殊处理）
	 * dependency types - which the factory handles specifically - already filtered out)
	 * @param bean the bean instance created, but whose properties have not yet been set
	 *             要创建的bean实例，其属性还没有设置。
	 * @param beanName the name of the bean
	 * @return the actual property values to apply to the given bean
	 * 返回要应用于给定bean的实际属性值（可以是传入的PropertyValues实例），
	 * (can be the passed-in PropertyValues instance), or {@code null}
	 * 或者是null来跳过属性填充。
	 * to skip property population
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.MutablePropertyValues
	 */
	@Nullable
	default PropertyValues postProcessPropertyValues(
			PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {

		return pvs;
	}

}
