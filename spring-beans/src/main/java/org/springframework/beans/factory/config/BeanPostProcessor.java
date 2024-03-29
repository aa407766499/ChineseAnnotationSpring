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

/**
 * Factory hook that allows for custom modification of new bean instances,
 * 容器钩子，允许对新bean实例进行自定义修改。比如标记接口或者将其包装成代理。
 * e.g. checking for marker interfaces or wrapping them with proxies.
 *
 * <p>ApplicationContexts can autodetect BeanPostProcessor beans in their
 * ApplicationContexts能够在其bean定义中自动探测BeanPostProcessor然后将其
 * bean definitions and apply them to any beans subsequently created.
 * 应用到其后任何创建的bean。原生的bean容器允许以编程的方式注册后处理器，
 * Plain bean factories allow for programmatic registration of post-processors,
 * 然后将其应用于该容器创建的所有bean。
 * applying to all beans created through this factory.
 *
 * <p>Typically, post-processors that populate beans via marker interfaces
 * 通常，通过接口或者相似的方式填充bean的后处理器会实现postProcessBeforeInitialization方法。
 * or the like will implement {@link #postProcessBeforeInitialization},
 * 而将bean包装成代理的后处理器通常会实现postProcessAfterInitialization方法。
 * while post-processors that wrap beans with proxies will normally
 * implement {@link #postProcessAfterInitialization}.
 *
 * @author Juergen Hoeller
 * @since 10.10.2003
 * @see InstantiationAwareBeanPostProcessor
 * @see DestructionAwareBeanPostProcessor
 * @see ConfigurableBeanFactory#addBeanPostProcessor
 * @see BeanFactoryPostProcessor
 */
public interface BeanPostProcessor {

	/**
	 * Apply this BeanPostProcessor to the given new bean instance <i>before</i> any bean
	 * 在任何bean实例化回调之前（比如InitializingBean的afterPropertiesSet或者自定义的初始化
	 * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
	 * 方法）将该BeanPostProcessor应用于给定的新bean实例。该bean已经被填充属性值。
	 * or a custom init-method). The bean will already be populated with property values.
	 * 返回的bean实例可能是原始bean的包装器。
	 * The returned bean instance may be a wrapper around the original.
	 * <p>The default implementation returns the given {@code bean} as-is.
	 * 默认实现返回给定bean自身。
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one;
	 * if {@code null}, no subsequent BeanPostProcessors will be invoked
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 */
	//为在Bean的初始化前提供回调入口
	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * Apply this BeanPostProcessor to the given new bean instance <i>after</i> any bean
	 * 在任何bean实例化回调之后（比如InitializingBean的afterPropertiesSet或者自定义的初始化
	 * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
	 * 方法）将该BeanPostProcessor应用于给定的新bean实例。该bean已经被填充属性值。
	 * or a custom init-method). The bean will already be populated with property values.
	 * 返回的bean实例可能是原始bean的包装器。
	 * The returned bean instance may be a wrapper around the original.
	 * <p>In case of a FactoryBean, this callback will be invoked for both the FactoryBean
	 * 如果是一个FactoryBean，该回调会被FactoryBean以及FactoryBean创建的对象调用。该后处理器
	 * instance and the objects created by the FactoryBean (as of Spring 2.0). The
	 * 会决定是否应用于FactoryBean或者FactoryBean创建的对象或者两者都应用，通过检查相应的
	 * post-processor can decide whether to apply to either the FactoryBean or created
	 * FactoryBean的bean实例。
	 * objects or both through corresponding {@code bean instanceof FactoryBean} checks.
	 * <p>This callback will also be invoked after a short-circuiting triggered by a
	 * 与其他所有的BeanPostProcessor回调相反，InstantiationAwareBeanPostProcessor的
	 * {@link InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation} method,
	 * postProcessBeforeInstantiation方法触发短路之后，会调用该回调。
	 * in contrast to all other BeanPostProcessor callbacks.
	 * <p>The default implementation returns the given {@code bean} as-is.
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one;
	 * if {@code null}, no subsequent BeanPostProcessors will be invoked
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 * @see org.springframework.beans.factory.FactoryBean
	 */
	//为在Bean的初始化之后提供回调入口
	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
