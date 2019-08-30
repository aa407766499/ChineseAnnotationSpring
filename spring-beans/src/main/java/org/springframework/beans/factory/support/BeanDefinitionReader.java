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
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

/**
 * Simple interface for bean definition readers.
 * bean定义读取器的简单接口。
 * Specifies load methods with Resource and String location parameters.
 * 指定资源和字符串路径参数的加载方法
 * <p>Concrete bean definition readers can of course add additional
 * 当然对于特定的bean定义格式，具体的bean定义读取器可以添加另外的
 * load and register methods for bean definitions, specific to
 * bean定义的加载和注册方法。
 * their bean definition format.
 *
 * <p>Note that a bean definition reader does not have to implement
 * 注意:bean定义读取器不是必须实现该接口。这只是对想要遵循标准命名传统
 * this interface. It only serves as suggestion for bean definition
 * 的bean定义读取器的一个建议。
 * readers that want to follow standard naming conventions.
 *
 * @author Juergen Hoeller
 * @since 1.1
 * @see org.springframework.core.io.Resource
 */
public interface BeanDefinitionReader {

	/**
	 * Return the bean factory to register the bean definitions with.
	 * 返回注册了bean定义的bean容器。
	 * <p>The factory is exposed through the BeanDefinitionRegistry interface,
	 * 通过BeanDefinitionRegistry接口暴露容器，封装与bean定义处理相关的方法。
	 * encapsulating the methods that are relevant for bean definition handling.
	 */
	BeanDefinitionRegistry getRegistry();

	/**
	 * Return the resource loader to use for resource locations.
	 * 返回用于资源路径的资源加载器。因此，可以由ResourcePatternResolver接口检查和转换，
	 * Can be checked for the <b>ResourcePatternResolver</b> interface and cast
	 * 根据给定的资源模式加载多个资源。
	 * accordingly, for loading multiple resources for a given resource pattern.
	 * <p>A {@code null} return value suggests that absolute resource loading
	 * 返回null表示该bean定义读取器不能获得绝对路径资源加载。
	 * is not available for this bean definition reader.
	 * <p>This is mainly meant to be used for importing further resources
	 * 这主要被用于从bean定义资源中导入更多的资源，比如通过XML的"import"标签。
	 * from within a bean definition resource, for example via the "import"
	 * 然而，建议使用这种方式导入相关的定义资源；只有明确完整的资源路径会触发
	 * tag in XML bean definitions. It is recommended, however, to apply
	 * 绝对路径资源加载。
	 * such imports relative to the defining resource; only explicit full
	 * resource locations will trigger absolute resource loading.
	 * <p>There is also a {@code loadBeanDefinitions(String)} method available,
	 * 还有一个loadBeanDefinitions(String)方法，用于加载资源路径下（或者路径模式下）的
	 * for loading bean definitions from a resource location (or location pattern).
	 * bean定义。这样方便，不需要明确的ResourceLoader的处理。
	 * This is a convenience to avoid explicit ResourceLoader handling.
	 * @see #loadBeanDefinitions(String)
	 * @see org.springframework.core.io.support.ResourcePatternResolver
	 */
	@Nullable
	ResourceLoader getResourceLoader();

	/**
	 * Return the class loader to use for bean classes.
	 * 返回用于bean类的类加载器。
	 * <p>{@code null} suggests to not load bean classes eagerly
	 * 返回null表示不能饿汉式加载bean类，而仅能注册类名称的bean定义，
	 * but rather to just register bean definitions with class names,
	 * 相应的类以后解析（或者不解析）
	 * with the corresponding Classes to be resolved later (or never).
	 */
	@Nullable
	ClassLoader getBeanClassLoader();

	/**
	 * Return the BeanNameGenerator to use for anonymous beans
	 * 返回用于无名bean（没有指定明确的bean名称）的BeanNameGenerator
	 * (without explicit bean name specified).
	 */
	BeanNameGenerator getBeanNameGenerator();


	/**
	 * Load bean definitions from the specified resource.
	 * 加载指定资源的bean定义。
	 * @param resource the resource descriptor
	 *                 资源描述符
	 * @return the number of bean definitions found
	 * 查找到的bean定义数量
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 *                                       加载或者解析错误
	 */
	int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException;

	/**
	 * Load bean definitions from the specified resources.
	 * @param resources the resource descriptors
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException;

	/**
	 * Load bean definitions from the specified resource location.
	 * 从指定资源路径加载bean定义。
	 * <p>The location can also be a location pattern, provided that the
	 * 路径也可以是路径模式，该bean定义读取器提供的资源加载器是ResourcePatternResolver。
	 * ResourceLoader of this bean definition reader is a ResourcePatternResolver.
	 * @param location the resource location, to be loaded with the ResourceLoader
	 *                 资源路径，能够被该bean定义读取器的资源加载器加载
	 * (or ResourcePatternResolver) of this bean definition reader
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 * @see #getResourceLoader()
	 * @see #loadBeanDefinitions(org.springframework.core.io.Resource)
	 * @see #loadBeanDefinitions(org.springframework.core.io.Resource[])
	 */
	int loadBeanDefinitions(String location) throws BeanDefinitionStoreException;

	/**
	 * Load bean definitions from the specified resource locations.
	 * @param locations the resource locations, to be loaded with the ResourceLoader
	 * (or ResourcePatternResolver) of this bean definition reader
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException;

}
