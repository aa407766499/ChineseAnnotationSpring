/*
 * Copyright 2002-2013 the original author or authors.
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

/**
 * Simple implementation of the {@link TypeConverter} interface that does not operate on
 * TypeConverter接口的简单实现，不操作指定目标对象。可以选择使用一个任意类型转换需要的成熟的
 * a specific target object. This is an alternative to using a full-blown BeanWrapperImpl
 * BeanWrapperImpl实例，而使用非常相同的转换算法（包括委派给PropertyEditor以及ConversionService）。
 * instance for arbitrary type conversion needs, while using the very same conversion
 * algorithm (including delegation to {@link java.beans.PropertyEditor} and
 * {@link org.springframework.core.convert.ConversionService}) underneath.
 *
 * <p><b>Note:</b> Due to its reliance on {@link java.beans.PropertyEditor PropertyEditors},
 * 注意：由于该类依赖于PropertyEditors，所以SimpleTypeConverter不是线程安全的。每一个线程使
 * SimpleTypeConverter is <em>not</em> thread-safe. Use a separate instance for each thread.
 * 用单独的实例。
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see BeanWrapperImpl
 */
public class SimpleTypeConverter extends TypeConverterSupport {

	public SimpleTypeConverter() {
		this.typeConverterDelegate = new TypeConverterDelegate(this);
		registerDefaultEditors();
	}

}
