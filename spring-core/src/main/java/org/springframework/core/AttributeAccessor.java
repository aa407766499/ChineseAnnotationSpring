/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.core;

import org.springframework.lang.Nullable;

/**
 * Interface defining a generic contract for attaching and accessing metadata
 * 该接口定义了通用的规则，给任意对象的元数据赋值以及访问任意对象元数据的值。
 * to/from arbitrary objects.
 *
 * @author Rob Harrop
 * @since 2.0
 */
public interface AttributeAccessor {

	/**
	 * Set the attribute defined by {@code name} to the supplied	{@code value}.
	 * 给定义为指定名称的属性赋值。
	 * If {@code value} is {@code null}, the attribute is {@link #removeAttribute removed}.
	 * 如果value为null，属性通过removeAttribute被移除。
	 * <p>In general, users should take care to prevent overlaps with other
	 * 通常，用户应该注意不要使用全限定名称造成与其他元数据属性的重叠，
	 * metadata attributes by using fully-qualified names, perhaps using
	 * 可以使用类或包名称作为前缀
	 * class or package names as prefix.
	 * @param name the unique attribute key
	 * @param value the attribute value to be attached
	 */
	void setAttribute(String name, @Nullable Object value);

	/**
	 * Get the value of the attribute identified by {@code name}.
	 * 获取指定名称属性的值。
	 * Return {@code null} if the attribute doesn't exist.
	 * 属性不存在返回null。
	 * @param name the unique attribute key
	 *             唯一属性键
	 * @return the current value of the attribute, if any
	 */
	@Nullable
	Object getAttribute(String name);

	/**
	 * Remove the attribute identified by {@code name} and return its value.
	 * 移除指定名称的属性，返回其值。
	 * Return {@code null} if no attribute under {@code name} is found.
	 * @param name the unique attribute key
	 * @return the last value of the attribute, if any
	 */
	@Nullable
	Object removeAttribute(String name);

	/**
	 * Return {@code true} if the attribute identified by {@code name} exists.
	 * 如果有指定的名称的属性存在返回true。
	 * Otherwise return {@code false}.
	 * @param name the unique attribute key
	 */
	boolean hasAttribute(String name);

	/**
	 * Return the names of all attributes.
	 * 返回所有属性的名称
	 */
	String[] attributeNames();

}
