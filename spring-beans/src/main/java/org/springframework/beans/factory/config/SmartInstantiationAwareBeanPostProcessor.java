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

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

import java.lang.reflect.Constructor;

/**
 * Extension of the {@link InstantiationAwareBeanPostProcessor} interface,
 * InstantiationAwareBeanPostProcessor接口的扩展接口，添加一个回调来预测
 * adding a callback for predicting the eventual type of a processed bean.
 * 处理bean的最终类型。
 *
 * <p><b>NOTE:</b> This interface is a special purpose interface, mainly for
 * 注意：该接口用于特殊目的，主要框架内部使用。一般，应用提供的后处理应该实现
 * internal use within the framework. In general, application-provided
 * 原生的BeanPostProcessor接口或者继承InstantiationAwareBeanPostProcessorAdapter。
 * post-processors should simply implement the plain {@link BeanPostProcessor}
 * 即使在点版本中，新方法也可能添加到此接口中。
 * interface or derive from the {@link InstantiationAwareBeanPostProcessorAdapter}
 * class. New methods might be added to this interface even in point releases.
 *
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see InstantiationAwareBeanPostProcessorAdapter
 */
public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {

	/**
	 * Predict the type of the bean to be eventually returned from this
	 * 预测该后处理器的postProcessBeforeInstantiation回调返回的bean的最终类型。
	 * processor's {@link #postProcessBeforeInstantiation} callback.
	 * <p>The default implementation returns {@code null}.
	 * @param beanClass the raw class of the bean
	 * @param beanName the name of the bean
	 * @return the type of the bean, or {@code null} if not predictable
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	@Nullable
	default Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

	/**
	 * Determine the candidate constructors to use for the given bean.
	 * 确定用于给定bean的匹配的构造器。
	 * <p>The default implementation returns {@code null}.
	 * @param beanClass the raw class of the bean (never {@code null})
	 * @param beanName the name of the bean
	 * @return the candidate constructors, or {@code null} if none specified
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	@Nullable
	default Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName)
			throws BeansException {

		return null;
	}

	/**
	 * Obtain a reference for early access to the specified bean,
	 * 获取可以早期访问指定bean的引用，通常用于解决循环引用问题。
	 * typically for the purpose of resolving a circular reference.
	 * <p>This callback gives post-processors a chance to expose a wrapper
	 * 换句话说，在目标bean完全实例化之前，回调给了后处理器一个机会去过早暴露包装器。
	 * early - that is, before the target bean instance is fully initialized.
	 * 否则暴露出的对象应该和postProcessBeforeInitialization/postProcessAfterInitialization
	 * The exposed object should be equivalent to the what
	 * 暴露出的对象相同。注意：除非后处理器返回一个调用后处理回调返回的不同的包装器，否则
	 * {@link #postProcessBeforeInitialization} / {@link #postProcessAfterInitialization}
	 * 该方法返回的对象会被用作bean引用。换句话说：那些后处理回调要么最后暴露相同的引用
	 * would expose otherwise. Note that the object returned by this method will
	 * 要么有选择返回那些回调的原始bean实例（如果已经调用该方法构建了目标bean的包装器），
	 * be used as bean reference unless the post-processor returns a different
	 * 默认会将其作为最终的bean引用。
	 * wrapper from said post-process callbacks. In other words: Those post-process
	 * callbacks may either eventually expose the same reference or alternatively
	 * return the raw bean instance from those subsequent callbacks (if the wrapper
	 * for the affected bean has been built for a call to this method already,
	 * it will be exposes as final bean reference by default).
	 * <p>The default implementation returns the given {@code bean} as-is.
	 * @param bean the raw bean instance
	 * @param beanName the name of the bean
	 * @return the object to expose as bean reference
	 * (typically with the passed-in bean instance as default)
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	default Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
