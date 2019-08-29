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

package org.springframework.core.io;

import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;

/**
 * 加载资源动作解释：类路径下或者文件系统中的文件存放在磁盘中，ResourceLoader
 * 通过其路径将其加载到内存中，在内存中的存在形式就是Resource（类比JDK中的File）。
 * Strategy interface for loading resources (e.. class path or file system
 * 策略接口（策略模式），用于加载资源（类路径资源或者文件系统资源）。
 * resources). An {@link org.springframework.context.ApplicationContext}
 * ApplicationContext需要提供这个功能，加上扩展的ResourcePatternResolver支持。
 * is required to provide this functionality, plus extended
 * {@link org.springframework.core.io.support.ResourcePatternResolver} support.
 *
 * <p>{@link DefaultResourceLoader} is a standalone implementation that is
 * DefaultResourceLoader是一个单独的实现其可以在ApplicationContext外部使用，
 * usable outside an ApplicationContext, also used by {@link ResourceEditor}.
 * 也能够被ResourceEditor使用。
 * <p>Bean properties of type Resource and Resource array can be populated
 * 在ApplicationContext中运行时，Resource和Resource类型数组的bean属性能够由字符串
 * from Strings when running in an ApplicationContext, using the particular
 * 填充，使用特定的上下文加载策略。
 * context's resource loading strategy.
 *
 * @author Juergen Hoeller
 * @since 10.03.2004
 * @see Resource
 * @see org.springframework.core.io.support.ResourcePatternResolver
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ResourceLoaderAware
 */
public interface ResourceLoader {

	/** Pseudo URL prefix for loading from the class path: "classpath:" */
	//从类路径加载的假URL前缀："classpath:"
	String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;


	/**
	 * Return a Resource handle for the specified resource location.
	 * 根据指定的资源路径返回资源句柄。
	 * <p>The handle should always be a reusable resource descriptor,
	 * 该句柄是可以重复使用的资源描述符，可以多次调用Resource#getInputStream()
	 * allowing for multiple {@link Resource#getInputStream()} calls.
	 * <p><ul>
	 * <li>Must support fully qualified URLs, e.g. "file:C:/test.dat".
	 * 必须支持完整确定的URL，比如"file:C:/test.dat"
	 * <li>Must support classpath pseudo-URLs, e.g. "classpath:test.dat".
	 * 必须支持类路径假URL，比如"classpath:test.dat"
	 * <li>Should support relative file paths, e.g. "WEB-INF/test.dat".
	 * 应该支持相对文件路径，比如"WEB-INF/test.dat"。
	 * (This will be implementation-specific, typically provided by an
	 * （有具体实现类实现，通常由ApplicationContext实现类实现。）
	 * ApplicationContext implementation.)
	 * </ul>
	 * <p>Note that a Resource handle does not imply an existing resource;
	 * 注意：一个资源句柄不能表明一个存在的资源；你需要调用Resource#exists检查是否存在；
	 * you need to invoke {@link Resource#exists} to check for existence.
	 * @param location the resource location
	 * @return a corresponding Resource handle (never {@code null})
	 * 返回相应资源句柄（不能为null）
	 * @see #CLASSPATH_URL_PREFIX
	 * @see Resource#exists()
	 * @see Resource#getInputStream()
	 */
	Resource getResource(String location);

	/**
	 * Expose the ClassLoader used by this ResourceLoader.
	 * 暴露该资源加载器使用的类加载器
	 * <p>Clients which need to access the ClassLoader directly can do so
	 * 需要直接访问类加载器的客户端可以通过资源加载器以统一的方式访问类加载器，
	 * in a uniform manner with the ResourceLoader, rather than relying
	 * 而不用依赖线程上下文类加载器。
	 * on the thread context ClassLoader.
	 * @return the ClassLoader
	 * @see org.springframework.util.ClassUtils#getDefaultClassLoader()
	 */
	@Nullable
	ClassLoader getClassLoader();

}
