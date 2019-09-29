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

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Post-processor callback interface for <i>merged</i> bean definitions at runtime.
 * 运行期后处理合并bean定义的后处理器回调接口。BeanPostProcessor的实现类可以实现该子接口
 * {@link BeanPostProcessor} implementations may implement this sub-interface in order
 * ，这样可以后处理合并bean定义（对原始bean定义进行复制），该bean定义Spring容器用来创建
 * to post-process the merged bean definition (a processed copy of the original bean
 * bean实例。
 * definition) that the Spring {@code BeanFactory} uses to create a bean instance.
 *
 * <p>The {@link #postProcessMergedBeanDefinition} method may for example introspect
 * 比如：postProcessMergedBeanDefinition方法可以拦截bean定义为了在后处理bean实际实例之前
 * the bean definition in order to prepare some cached metadata before post-processing
 * 准备某些缓存元数据。也允许修改bean定义但仅能修改实际上会并发修改的定义属性。本质上，
 * actual instances of a bean. It is also allowed to modify the bean definition but
 * 能应用于RootBeanDefinition中定义的操作而不能应用于基础类的属性。
 * <i>only</i> for definition properties which are actually intended for concurrent
 * modification. Essentially, this only applies to operations defined on the
 * {@link RootBeanDefinition} itself but not to the properties of its base classes.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#getMergedBeanDefinition
 */
public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {

	/**
	 * Post-process the given merged bean definition for the specified bean.
	 * 后处理指定bean的给定合并bean定义。
	 * @param beanDefinition the merged bean definition for the bean
	 * @param beanType the actual type of the managed bean instance
	 * @param beanName the name of the bean
	 */
	void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName);

}
