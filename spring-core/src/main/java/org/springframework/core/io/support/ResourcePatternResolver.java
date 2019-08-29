/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.core.io.support;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Strategy interface for resolving a location pattern (for example,
 * 策略接口（策略模式），用于解析资源对象中路径模式（比如，Ant风格的路径模式）
 * an Ant-style path pattern) into Resource objects.
 *
 * <p>This is an extension to the {@link org.springframework.core.io.ResourceLoader}
 * 这是ResourceLoader的扩展接口。能够检查传入的资源加载器
 * interface. A passed-in ResourceLoader (for example, an
 * 是否实现了此扩展接口（比如在上下文运行中，通过ResourceLoaderAware传入
 * {@link org.springframework.context.ApplicationContext} passed in via
 * 一个ApplicationContext）
 * {@link org.springframework.context.ResourceLoaderAware} when running in a context)
 * can be checked whether it implements this extended interface too.
 *
 * <p>{@link PathMatchingResourcePatternResolver} is a standalone implementation
 * PathMatchingResourcePatternResolver是一个单独的实现，能够独立于ApplicationContext使用，
 * that is usable outside an ApplicationContext, also used by
 * 也能够被ResourceArrayPropertyEditor用于填充Resource数组bean属性。
 * {@link ResourceArrayPropertyEditor} for populating Resource array bean properties.
 *
 * <p>Can be used with any sort of location pattern (e.g. "/WEB-INF/*-context.xml"):
 * 可以和任何路径模式一起使用（比如："/WEB-INF/*-context.xml"）：
 * Input patterns have to match the strategy implementation. This interface just
 * 输入模式必须匹配策略实现。该接口仅定义了转换方法而没有定义特定模式的格式化方法。
 * specifies the conversion method rather than a specific pattern format.
 *
 * <p>This interface also suggests a new resource prefix "classpath*:" for all
 * 该接口要求对于所有匹配类路径的资源使用新的资源前缀"classpath*:"。注意：资源路径
 * matching resources from the class path. Note that the resource location is
 * 不要有占位符（比如："/beans.xml"）；jar文件或者类文件目录可以包含多个相同名称的文件。
 * expected to be a path without placeholders in this case (e.g. "/beans.xml");
 * JAR files or classes directories can contain multiple files of the same name.
 *
 * @author Juergen Hoeller
 * @since 1.0.2
 * @see org.springframework.core.io.Resource
 * @see org.springframework.core.io.ResourceLoader
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ResourceLoaderAware
 */
public interface ResourcePatternResolver extends ResourceLoader {

	/**
	 * Pseudo URL prefix for all matching resources from the class path: "classpath*:"
	 * 所有匹配的类路径资源的假URL前缀："classpath*:"。
	 * This differs from ResourceLoader's classpath URL prefix in that it
	 * 跟ResourceLoader类路径URL的区别在于它提取所有匹配给定名称的资源（比如："/beans.xml"），
	 * retrieves all matching resources for a given name (e.g. "/beans.xml"),
	 * 比如所有部署jar文件的根路径。
	 * for example in the root of all deployed JAR files.
	 * @see org.springframework.core.io.ResourceLoader#CLASSPATH_URL_PREFIX
	 */
	String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

	/**
	 * Resolve the given location pattern into Resource objects.
	 * 将给定的资源模式解析成资源对象数组。
	 * <p>Overlapping resource entries that point to the same physical
	 * 要尽可能防止生成相同物理资源的重叠的资源项。结果应该有这个语义。
	 * resource should be avoided, as far as possible. The result should
	 * have set semantics.
	 * @param locationPattern the location pattern to resolve
	 * @return the corresponding Resource objects
	 * @throws IOException in case of I/O errors
	 */
	Resource[] getResources(String locationPattern) throws IOException;

}
