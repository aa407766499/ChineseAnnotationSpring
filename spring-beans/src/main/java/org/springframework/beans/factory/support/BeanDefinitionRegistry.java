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

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.AliasRegistry;

/**
 * Interface for registries that hold bean definitions, for example RootBeanDefinition
 * 持有bean定义的注册表（个人理解：bean定义管理器）接口，比如RootBeanDefinition和ChildBeanDefinition实例。
 * and ChildBeanDefinition instances. Typically implemented by BeanFactories that
 * 通常由Bean容器实现，其内部与AbstractBeanDefinition层级协同工作
 * internally work with the AbstractBeanDefinition hierarchy.
 *
 * <p>This is the only interface in Spring's bean factory packages that encapsulates
 * 在Spring bean容器包中，这是唯一包装bean定义注册表的接口。标准的BeanFactory接口
 * <i>registration</i> of bean definitions. The standard BeanFactory interfaces
 * 仅覆盖对完整配置的容器实例的访问。
 * only cover access to a <i>fully configured factory instance</i>.
 *
 * <p>Spring's bean definition readers expect to work on an implementation of this
 * Spring的bean定义读取器要求与该接口的实现一起工作。在Spring core中知名的实现者有DefaultListableBeanFactory
 * interface. Known implementors within the Spring core are DefaultListableBeanFactory
 * 和GenericApplicationContext。
 * and GenericApplicationContext.
 *
 * @author Juergen Hoeller
 * @since 26.11.2003
 * @see org.springframework.beans.factory.config.BeanDefinition
 * @see AbstractBeanDefinition
 * @see RootBeanDefinition
 * @see ChildBeanDefinition
 * @see DefaultListableBeanFactory
 * @see org.springframework.context.support.GenericApplicationContext
 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 * @see PropertiesBeanDefinitionReader
 */
public interface BeanDefinitionRegistry extends AliasRegistry {

	/**
	 * Register a new bean definition with this registry.
	 * 将新的bean定义注册到该注册表中。必须支持RootBeanDefinition
	 * Must support RootBeanDefinition and ChildBeanDefinition.
	 * 和ChildBeanDefinition
	 * @param beanName the name of the bean instance to register
	 *                 注册的bean实例的名称
	 * @param beanDefinition definition of the bean instance to register
	 *                       注册的bean实例的定义
	 * @throws BeanDefinitionStoreException if the BeanDefinition is invalid
	 * 如果BeanDefinition无效或者已经有指定名称的BeanDefinition（不允许重写）
	 * or if there is already a BeanDefinition for the specified bean name
	 * (and we are not allowed to override it)
	 * @see RootBeanDefinition
	 * @see ChildBeanDefinition
	 */
	void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
			throws BeanDefinitionStoreException;

	/**
	 * Remove the BeanDefinition for the given name.
	 * 移除指定名称的BeanDefinition
	 * @param beanName the name of the bean instance to register
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 */
	void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * Return the BeanDefinition for the given bean name.
	 * 获取指定名称的BeanDefinition
	 * @param beanName name of the bean to find a definition for
	 * @return the BeanDefinition for the given name (never {@code null})
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 */
	BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * Check if this registry contains a bean definition with the given name.
	 * 确认该注册表是否指定名称的BeanDefinition
	 * @param beanName the name of the bean to look for
	 * @return if this registry contains a bean definition with the given name
	 */
	boolean containsBeanDefinition(String beanName);

	/**
	 * Return the names of all beans defined in this registry.
	 * 获取该注册表中所有定义的bean名称。
	 * @return the names of all beans defined in this registry,
	 * or an empty array if none defined
	 */
	String[] getBeanDefinitionNames();

	/**
	 * Return the number of beans defined in the registry.
	 * 返回该注册表中定义的bean的数量。
	 * @return the number of beans defined in the registry
	 */
	int getBeanDefinitionCount();

	/**
	 * Determine whether the given bean name is already in use within this registry,
	 * 确定该注册表中指定名称是否已经使用。
	 * i.e. whether there is a local bean or alias registered under this name.
	 * 该名称下是否已经注册了本地bean或者别名
	 * @param beanName the name to check
	 * @return whether the given bean name is already in use
	 */
	boolean isBeanNameInUse(String beanName);

}
