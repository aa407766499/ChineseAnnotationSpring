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

/**
 * Interface to be implemented by a reloading-aware ClassLoader
 * 重加载感知的类加载器实现的接口（比如：基于Groovy的类加载器）。
 * (e.g. a Groovy-based ClassLoader). Detected for example by
 * 比如：有缓存功能的Spring的CGLIB代理工厂的检测。
 * Spring's CGLIB proxy factory for making a caching decision.
 *
 * <p>If a ClassLoader does <i>not</i> implement this interface,
 * 如果一个类加载器不实现该接口，那么从该类加载器获取的所有类应该被认为
 * then all of the classes obtained from it should be considered
 * 不能进行重新加载（比如：缓存）
 * as not reloadable (i.e. cacheable).
 *
 * @author Juergen Hoeller
 * @since 2.5.1
 */
public interface SmartClassLoader {

	/**
	 * Determine whether the given class is reloadable (in this ClassLoader).
	 * 确定给定类是否是可重加载的（在该类加载器中）。
	 * <p>Typically used to check whether the result may be cached (for this
	 * 通常用于检查结果是否可以被缓存（对于该类加载器）或者是否该类每次都应该重新
	 * ClassLoader) or whether it should be reobtained every time.
	 * 获取。
	 * @param clazz the class to check (usually loaded from this ClassLoader)
	 * @return whether the class should be expected to appear in a reloaded
	 * version (with a different {@code Class} object) later on
	 */
	boolean isClassReloadable(Class<?> clazz);

}
