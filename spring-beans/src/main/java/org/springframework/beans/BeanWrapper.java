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

package org.springframework.beans;

import java.beans.PropertyDescriptor;

/**
 * The central interface of Spring's low-level JavaBeans infrastructure.
 * 低级的JavaBean基础设施中心接口。通常不直接使用，而是通过 Bean Factory
 * <p>Typically not used directly but rather implicitly via a
 * 和DataBinder隐式使用。
 * {@link org.springframework.beans.factory.BeanFactory} or a
 * {@link org.springframework.validation.DataBinder}.
 *
 * <p>Provides operations to analyze and manipulate standard JavaBeans:
 * 提供分析和操作标准JavaBeans的操作：能够获取和设置属性值（单个操作或者批量操作），
 * the ability to get and set property values (individually or in bulk),
 * 获取属性描述器，查找可读/可写的属性。
 * get property descriptors, and query the readability/writability of properties.
 *
 * <p>This interface supports <b>nested properties</b> enabling the setting
 * 在子属性没有限制深度时，该接口直接能够设置属性的内嵌属性。
 * of properties on subproperties to an unlimited depth.
 *
 * <p>A BeanWrapper's default for the "extractOldValueForEditor" setting
 * BeanWrapper的extractOldValueForEditor的默认设定为false，防止getter方法
 * is "false", to avoid side effects caused by getter method invocations.
 * 调用造成的副作用。设置成true将目前的属性值暴露给自定义编辑器。
 * Turn this to "true" to expose present property values to custom editors.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 13 April 2001
 * @see PropertyAccessor
 * @see PropertyEditorRegistry
 * @see PropertyAccessorFactory#forBeanPropertyAccess
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.validation.BeanPropertyBindingResult
 * @see org.springframework.validation.DataBinder#initBeanPropertyAccess()
 */
public interface BeanWrapper extends ConfigurablePropertyAccessor {

	/**
	 * Specify a limit for array and collection auto-growing.
	 * <p>Default is unlimited on a plain BeanWrapper.
	 * @since 4.1
	 */
	void setAutoGrowCollectionLimit(int autoGrowCollectionLimit);

	/**
	 * Return the limit for array and collection auto-growing.
	 * @since 4.1
	 */
	int getAutoGrowCollectionLimit();

	/**
	 * Return the bean instance wrapped by this object.
	 */
	Object getWrappedInstance();

	/**
	 * Return the type of the wrapped bean instance.
	 */
	Class<?> getWrappedClass();

	/**
	 * Obtain the PropertyDescriptors for the wrapped object
	 * (as determined by standard JavaBeans introspection).
	 * @return the PropertyDescriptors for the wrapped object
	 */
	PropertyDescriptor[] getPropertyDescriptors();

	/**
	 * Obtain the property descriptor for a specific property
	 * of the wrapped object.
	 * @param propertyName the property to obtain the descriptor for
	 * (may be a nested path, but no indexed/mapped property)
	 * @return the property descriptor for the specified property
	 * @throws InvalidPropertyException if there is no such property
	 */
	PropertyDescriptor getPropertyDescriptor(String propertyName) throws InvalidPropertyException;

}
