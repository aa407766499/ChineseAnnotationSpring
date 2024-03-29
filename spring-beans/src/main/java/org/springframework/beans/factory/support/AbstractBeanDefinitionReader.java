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

import java.io.IOException;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Abstract base class for bean definition readers which implement
 * 实现BeanDefinitionReader接口的bean定义读取器抽象基础类
 * the {@link BeanDefinitionReader} interface.
 *
 * <p>Provides common properties like the bean factory to work on
 * 提供要处理的bean容器和用于加载bean类的类加载器等常见属性。
 * and the class loader to use for loading bean classes.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 11.12.2003
 * @see BeanDefinitionReaderUtils
 */
public abstract class AbstractBeanDefinitionReader implements EnvironmentCapable, BeanDefinitionReader {

	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	private final BeanDefinitionRegistry registry;

	@Nullable
	private ResourceLoader resourceLoader;

	@Nullable
	private ClassLoader beanClassLoader;

	private Environment environment;

	private BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();


	/**
	 * Create a new AbstractBeanDefinitionReader for the given bean factory.
	 * 根据给定bean容器创建新的AbstractBeanDefinitionReader
	 * <p>If the passed-in bean factory does not only implement the BeanDefinitionRegistry
	 * 如果传入的bean容器不仅实现了BeanDefinitionRegistry而且实现了ResourceLoader接口，
	 * interface but also the ResourceLoader interface, it will be used as default
	 * 那么该容器将作为默认的资源加载器。通常情况下是ApplicationContext的实现。
	 * ResourceLoader as well. This will usually be the case for
	 * {@link org.springframework.context.ApplicationContext} implementations.
	 * <p>If given a plain BeanDefinitionRegistry, the default ResourceLoader will be a
	 * 如果给的是一个普通的BeanDefinitionRegistry，那么默认的ResourceLoader会是
	 * {@link org.springframework.core.io.support.PathMatchingResourcePatternResolver}.
	 * PathMatchingResourcePatternResolver。
	 * <p>If the passed-in bean factory also implements {@link EnvironmentCapable} its
	 * 如果传入的bean容器也实现了EnvironmentCapable接口，那么该容器的环境会被该读取器使用。
	 * environment will be used by this reader.  Otherwise, the reader will initialize and
	 * 否则，读取器会初始化以及使用StandardEnvironment。所有ApplicationContext实现都是
	 * use a {@link StandardEnvironment}. All ApplicationContext implementations are
	 * EnvironmentCapable，而正常BeanFactory实现不是EnvironmentCapable。
	 * EnvironmentCapable, while normal BeanFactory implementations are not.
	 * @param registry the BeanFactory to load bean definitions into,
	 * in the form of a BeanDefinitionRegistry
	 * @see #setResourceLoader
	 * @see #setEnvironment
	 */
	protected AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		this.registry = registry;

		// Determine ResourceLoader to use.
		// 确定要使用的资源加载器
		if (this.registry instanceof ResourceLoader) {
			this.resourceLoader = (ResourceLoader) this.registry;
		}
		else {
			this.resourceLoader = new PathMatchingResourcePatternResolver();
		}

		// Inherit Environment if possible
		// 如果可能，继承环境
		if (this.registry instanceof EnvironmentCapable) {
			this.environment = ((EnvironmentCapable) this.registry).getEnvironment();
		}
		else {
			this.environment = new StandardEnvironment();
		}
	}


	public final BeanDefinitionRegistry getBeanFactory() {
		return this.registry;
	}

	@Override
	public final BeanDefinitionRegistry getRegistry() {
		return this.registry;
	}

	/**
	 * Set the ResourceLoader to use for resource locations.
	 * 设置用于资源路径的资源加载器。如果指定ResourcePatternResolver，
	 * If specifying a ResourcePatternResolver, the bean definition reader
	 * 那么bean定义读取器能够将资源模式解析成资源数组。
	 * will be capable of resolving resource patterns to Resource arrays.
	 * <p>Default is PathMatchingResourcePatternResolver, also capable of
	 * 默认是PathMatchingResourcePatternResolver，也可以通过ResourcePatternResolver
	 * resource pattern resolving through the ResourcePatternResolver interface.
	 * 接口解析资源模式。
	 * <p>Setting this to {@code null} suggests that absolute resource loading
	 * 将此设置为 NULL 意味着绝对资源加载对于此 bean 定义读取器不可用。
	 * is not available for this bean definition reader.
	 * @see org.springframework.core.io.support.ResourcePatternResolver
	 * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver
	 */
	public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	@Nullable
	public ResourceLoader getResourceLoader() {
		return this.resourceLoader;
	}

	/**
	 * Set the ClassLoader to use for bean classes.
	 * <p>Default is {@code null}, which suggests to not load bean classes
	 * eagerly but rather to just register bean definitions with class names,
	 * with the corresponding Classes to be resolved later (or never).
	 * @see Thread#getContextClassLoader()
	 */
	public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
		this.beanClassLoader = beanClassLoader;
	}

	@Override
	@Nullable
	public ClassLoader getBeanClassLoader() {
		return this.beanClassLoader;
	}

	/**
	 * Set the Environment to use when reading bean definitions. Most often used
	 * 在读取bean定义时设置使用的环境。通常该环境用于解析概述信息，确定哪些bean定义
	 * for evaluating profile information to determine which bean definitions
	 * 应该被读取，哪些应该被忽略。
	 * should be read and which should be omitted.
	 */
	public void setEnvironment(Environment environment) {
		Assert.notNull(environment, "Environment must not be null");
		this.environment = environment;
	}

	@Override
	public Environment getEnvironment() {
		return this.environment;
	}

	/**
	 * Set the BeanNameGenerator to use for anonymous beans
	 * (without explicit bean name specified).
	 * <p>Default is a {@link DefaultBeanNameGenerator}.
	 */
	public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
		this.beanNameGenerator = (beanNameGenerator != null ? beanNameGenerator : new DefaultBeanNameGenerator());
	}

	@Override
	public BeanNameGenerator getBeanNameGenerator() {
		return this.beanNameGenerator;
	}


	@Override
	public int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException {
		Assert.notNull(resources, "Resource array must not be null");
		int counter = 0;
		for (Resource resource : resources) {
			counter += loadBeanDefinitions(resource);
		}
		return counter;
	}
	//重载方法，调用下面的loadBeanDefinitions(String, Set<Resource>);方法
	@Override
	public int loadBeanDefinitions(String location) throws BeanDefinitionStoreException {
		return loadBeanDefinitions(location, null);
	}

	/**
	 * Load bean definitions from the specified resource location.
	 * 从指定资源路径下加载bean定义。
	 * <p>The location can also be a location pattern, provided that the
	 * 该路径可以使路径模式串，该bean定义读取器提供的ResourceLoader是
	 * ResourceLoader of this bean definition reader is a ResourcePatternResolver.
	 * ResourcePatternResolver
	 * @param location the resource location, to be loaded with the ResourceLoader
	 * (or ResourcePatternResolver) of this bean definition reader
	 * @param actualResources a Set to be filled with the actual Resource objects
	 *                        在加载处理期间已经被解析的实际资源对象集合。可以是null
	 * that have been resolved during the loading process. May be {@code null}
	 *                        表明调用者对那些资源对象不感兴趣。
	 * to indicate that the caller is not interested in those Resource objects.
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 * @see #getResourceLoader()
	 * @see #loadBeanDefinitions(org.springframework.core.io.Resource)
	 * @see #loadBeanDefinitions(org.springframework.core.io.Resource[])
	 */
	public int loadBeanDefinitions(String location, @Nullable Set<Resource> actualResources) throws BeanDefinitionStoreException {
		//获取在IoC容器初始化过程中设置的资源加载器
		ResourceLoader resourceLoader = getResourceLoader();
		if (resourceLoader == null) {
			throw new BeanDefinitionStoreException(
					"Cannot import bean definitions from location [" + location + "]: no ResourceLoader available");
		}

		if (resourceLoader instanceof ResourcePatternResolver) {
			// Resource pattern matching available.
			// 可以匹配资源模式串
			try {
				//将指定位置的Bean定义资源文件解析为Spring IOC容器封装的资源
				//加载多个指定位置的Bean定义资源文件
				Resource[] resources = ((ResourcePatternResolver) resourceLoader).getResources(location);
				//委派调用其子类XmlBeanDefinitionReader的方法，实现加载功能
				int loadCount = loadBeanDefinitions(resources);
				if (actualResources != null) {
					for (Resource resource : resources) {
						actualResources.add(resource);
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Loaded " + loadCount + " bean definitions from location pattern [" + location + "]");
				}
				return loadCount;
			}
			catch (IOException ex) {
				throw new BeanDefinitionStoreException(
						"Could not resolve bean definition resource pattern [" + location + "]", ex);
			}
		}
		else {
			// Can only load single resources by absolute URL.
			//将指定位置的Bean定义资源文件解析为Spring IOC容器封装的资源
			//加载单个指定位置的Bean定义资源文件
			Resource resource = resourceLoader.getResource(location);
			//委派调用其子类XmlBeanDefinitionReader的方法，实现加载功能
			int loadCount = loadBeanDefinitions(resource);
			if (actualResources != null) {
				actualResources.add(resource);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Loaded " + loadCount + " bean definitions from location [" + location + "]");
			}
			return loadCount;
		}
	}

	//重载方法，调用loadBeanDefinitions(String);
	@Override
	public int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException {
		Assert.notNull(locations, "Location array must not be null");
		int counter = 0;
		for (String location : locations) {
			counter += loadBeanDefinitions(location);
		}
		return counter;
	}

}
