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

package org.springframework.context.annotation;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.*;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.PatternMatchUtils;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A bean definition scanner that detects bean candidates on the classpath,
 * bean定义扫描器能够探测类路径上的匹配bean，将相应的bean定义注册到给定的注册表（BeanFactory
 * registering corresponding bean definitions with a given registry ({@code BeanFactory}
 * 或者ApplicationContext）。
 * or {@code ApplicationContext}).
 *
 * <p>Candidate classes are detected through configurable type filters. The
 * 通过配置的类型过滤器探测匹配的类。默认的过滤器会过滤出被@Component注解的类，
 * default filters include classes that are annotated with Spring's
 * Repository注解的类，Service注解的类，或者Controller注解的类。
 * {@link org.springframework.stereotype.Component @Component},
 * {@link org.springframework.stereotype.Repository @Repository},
 * {@link org.springframework.stereotype.Service @Service}, or
 * {@link org.springframework.stereotype.Controller @Controller} stereotype.
 *
 * <p>Also supports Java EE 6's {@link javax.annotation.ManagedBean} and
 * 也支持Java EE 6 的@ManagedBean和@Named
 * JSR-330's {@link javax.inject.Named} annotations, if available.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 2.5
 * @see AnnotationConfigApplicationContext#scan
 * @see org.springframework.stereotype.Component
 * @see org.springframework.stereotype.Repository
 * @see org.springframework.stereotype.Service
 * @see org.springframework.stereotype.Controller
 */
public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {

	private final BeanDefinitionRegistry registry;

	private BeanDefinitionDefaults beanDefinitionDefaults = new BeanDefinitionDefaults();

	@Nullable
	private String[] autowireCandidatePatterns;

	private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

	private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

	private boolean includeAnnotationConfig = true;


	/**
	 * Create a new {@code ClassPathBeanDefinitionScanner} for the given bean factory.
	 * @param registry the {@code BeanFactory} to load bean definitions into, in the form
	 * of a {@code BeanDefinitionRegistry}
	 */
	//创建一个类路径Bean定义扫描器
	public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
		this(registry, true);
	}

	/**
	 * Create a new {@code ClassPathBeanDefinitionScanner} for the given bean factory.
	 * 根据给定bean容器创建新的ClassPathBeanDefinitionScanner。
	 * <p>If the passed-in bean factory does not only implement the
	 * 如果传入的bean容器不仅实现了BeanDefinitionRegistry接口还实现了ResourceLoader接口
	 * {@code BeanDefinitionRegistry} interface but also the {@code ResourceLoader}
	 * 那么该容器就会被用作默认的ResourceLoader。通常，该容器是ApplicationContext实现类。
	 * interface, it will be used as default {@code ResourceLoader} as well. This will
	 * usually be the case for {@link org.springframework.context.ApplicationContext}
	 * implementations.
	 * <p>If given a plain {@code BeanDefinitionRegistry}, the default {@code ResourceLoader}
	 * 如果给定一个普通的BeanDefinitionRegistry，默认的ResourceLoader会是PathMatchingResourcePatternResolver
	 * will be a {@link org.springframework.core.io.support.PathMatchingResourcePatternResolver}.
	 * <p>If the passed-in bean factory also implements {@link EnvironmentCapable} its
	 * 如果传入的bean容器实现了EnvironmentCapable，那么该读取器就会使用该容器环境。否则，该读取器
	 * environment will be used by this reader.  Otherwise, the reader will initialize and
	 * 会初始化和使用StandardEnvironment。所有的ApplicationContext实现都是EnvironmentCapable，
	 * use a {@link org.springframework.core.env.StandardEnvironment}. All
	 * 而正常的BeanFactory实现不是。
	 * {@code ApplicationContext} implementations are {@code EnvironmentCapable}, while
	 * normal {@code BeanFactory} implementations are not.
	 * @param registry the {@code BeanFactory} to load bean definitions into, in the form
	 * of a {@code BeanDefinitionRegistry}
	 * @param useDefaultFilters whether to include the default filters for the
	 * {@link org.springframework.stereotype.Component @Component},
	 * {@link org.springframework.stereotype.Repository @Repository},
	 * {@link org.springframework.stereotype.Service @Service}, and
	 * {@link org.springframework.stereotype.Controller @Controller} stereotype annotations
	 * @see #setResourceLoader
	 * @see #setEnvironment
	 */
	//为容器创建一个类路径Bean定义扫描器，并指定是否使用默认的扫描过滤规则。
	//即Spring默认扫描配置：@Component、@Repository、@Service、@Controller
	//注解的Bean，同时也支持JavaEE6的@ManagedBean和JSR-330的@Named注解
	public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
		this(registry, useDefaultFilters, getOrCreateEnvironment(registry));
	}

	/**
	 * Create a new {@code ClassPathBeanDefinitionScanner} for the given bean factory and
	 * using the given {@link Environment} when evaluating bean definition profile metadata.
	 * <p>If the passed-in bean factory does not only implement the {@code
	 * BeanDefinitionRegistry} interface but also the {@link ResourceLoader} interface, it
	 * will be used as default {@code ResourceLoader} as well. This will usually be the
	 * case for {@link org.springframework.context.ApplicationContext} implementations.
	 * <p>If given a plain {@code BeanDefinitionRegistry}, the default {@code ResourceLoader}
	 * will be a {@link org.springframework.core.io.support.PathMatchingResourcePatternResolver}.
	 * @param registry the {@code BeanFactory} to load bean definitions into, in the form
	 * of a {@code BeanDefinitionRegistry}
	 * @param useDefaultFilters whether to include the default filters for the
	 * {@link org.springframework.stereotype.Component @Component},
	 * {@link org.springframework.stereotype.Repository @Repository},
	 * {@link org.springframework.stereotype.Service @Service}, and
	 * {@link org.springframework.stereotype.Controller @Controller} stereotype annotations
	 * @param environment the Spring {@link Environment} to use when evaluating bean
	 * definition profile metadata
	 * @since 3.1
	 * @see #setResourceLoader
	 */
	public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
			Environment environment) {

		this(registry, useDefaultFilters, environment,
				(registry instanceof ResourceLoader ? (ResourceLoader) registry : null));
	}

	/**
	 * Create a new {@code ClassPathBeanDefinitionScanner} for the given bean factory and
	 * 根据给定的bean容器创建新的ClassPathBeanDefinitionScanner，在解析bean定义概述元信息时
	 * using the given {@link Environment} when evaluating bean definition profile metadata.
	 * 使用给定Environment
	 * @param registry the {@code BeanFactory} to load bean definitions into, in the form
	 * of a {@code BeanDefinitionRegistry}
	 * @param useDefaultFilters whether to include the default filters for the
	 * {@link org.springframework.stereotype.Component @Component},
	 * {@link org.springframework.stereotype.Repository @Repository},
	 * {@link org.springframework.stereotype.Service @Service}, and
	 * {@link org.springframework.stereotype.Controller @Controller} stereotype annotations
	 * @param environment the Spring {@link Environment} to use when evaluating bean
	 * definition profile metadata
	 * @param resourceLoader the {@link ResourceLoader} to use
	 * @since 4.3.6
	 */
	public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
			Environment environment, @Nullable ResourceLoader resourceLoader) {

		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		//为扫描器设置注册Bean定义的注册器
		this.registry = registry;

		if (useDefaultFilters) {
			registerDefaultFilters();
		}
		setEnvironment(environment);
		//为扫描器设置资源加载器
		setResourceLoader(resourceLoader);
	}


	/**
	 * Return the BeanDefinitionRegistry that this scanner operates on.
	 */
	@Override
	public final BeanDefinitionRegistry getRegistry() {
		return this.registry;
	}

	/**
	 * Set the defaults to use for detected beans.
	 * @see BeanDefinitionDefaults
	 */
	public void setBeanDefinitionDefaults(@Nullable BeanDefinitionDefaults beanDefinitionDefaults) {
		this.beanDefinitionDefaults =
				(beanDefinitionDefaults != null ? beanDefinitionDefaults : new BeanDefinitionDefaults());
	}

	/**
	 * Return the defaults to use for detected beans (never {@code null}).
	 * @since 4.1
	 */
	public BeanDefinitionDefaults getBeanDefinitionDefaults() {
		return this.beanDefinitionDefaults;
	}

	/**
	 * Set the name-matching patterns for determining autowire candidates.
	 * @param autowireCandidatePatterns the patterns to match against
	 */
	public void setAutowireCandidatePatterns(@Nullable String... autowireCandidatePatterns) {
		this.autowireCandidatePatterns = autowireCandidatePatterns;
	}

	/**
	 * Set the BeanNameGenerator to use for detected bean classes.
	 * <p>Default is a {@link AnnotationBeanNameGenerator}.
	 */
	public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
		this.beanNameGenerator = (beanNameGenerator != null ? beanNameGenerator : new AnnotationBeanNameGenerator());
	}

	/**
	 * Set the ScopeMetadataResolver to use for detected bean classes.
	 * Note that this will override any custom "scopedProxyMode" setting.
	 * <p>The default is an {@link AnnotationScopeMetadataResolver}.
	 * @see #setScopedProxyMode
	 */
	public void setScopeMetadataResolver(@Nullable ScopeMetadataResolver scopeMetadataResolver) {
		this.scopeMetadataResolver =
				(scopeMetadataResolver != null ? scopeMetadataResolver : new AnnotationScopeMetadataResolver());
	}

	/**
	 * Specify the proxy behavior for non-singleton scoped beans.
	 * Note that this will override any custom "scopeMetadataResolver" setting.
	 * <p>The default is {@link ScopedProxyMode#NO}.
	 * @see #setScopeMetadataResolver
	 */
	public void setScopedProxyMode(ScopedProxyMode scopedProxyMode) {
		this.scopeMetadataResolver = new AnnotationScopeMetadataResolver(scopedProxyMode);
	}

	/**
	 * Specify whether to register annotation config post-processors.
	 * <p>The default is to register the post-processors. Turn this off
	 * to be able to ignore the annotations or to process them differently.
	 */
	public void setIncludeAnnotationConfig(boolean includeAnnotationConfig) {
		this.includeAnnotationConfig = includeAnnotationConfig;
	}


	/**
	 * Perform a scan within the specified base packages.
	 * 执行对指定基础包的扫描。
	 * @param basePackages the packages to check for annotated classes
	 * @return number of beans registered
	 */
	//调用类路径Bean定义扫描器入口方法
	public int scan(String... basePackages) {
		//获取容器中已经注册的Bean个数
		int beanCountAtScanStart = this.registry.getBeanDefinitionCount();

		//启动扫描器扫描给定包
		doScan(basePackages);

		// Register annotation config processors, if necessary.
		// 如果需要，注册注解配置处理器。
		//注册注解配置(Annotation config)处理器
		if (this.includeAnnotationConfig) {
			AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
		}

		//返回注册的Bean个数
		return (this.registry.getBeanDefinitionCount() - beanCountAtScanStart);
	}

	/**
	 * Perform a scan within the specified base packages,
	 * 执行对指定基础包的扫描，返回注册的bean定义。
	 * returning the registered bean definitions.
	 * <p>This method does <i>not</i> register an annotation config processor
	 * 该方法没有注册一个注解配置处理器而是将该处理器留给调用者。
	 * but rather leaves this up to the caller.
	 * @param basePackages the packages to check for annotated classes
	 * @return set of beans registered if any for tooling registration purposes (never {@code null})
	 */
	//类路径Bean定义扫描器扫描给定包及其子包
	protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Assert.notEmpty(basePackages, "At least one base package must be specified");
		//创建一个集合，存放扫描到的Bean定义的封装类
		Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();
		//遍历扫描所有给定的包
		for (String basePackage : basePackages) {
			//调用父类ClassPathScanningCandidateComponentProvider的方法
			//扫描给定类路径，获取符合条件的Bean定义
			Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
			//遍历扫描到的Bean定义
			for (BeanDefinition candidate : candidates) {
				//获取Bean定义类中@Scope注解的值，即获取Bean的作用域
				ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
				//为Bean设置注解配置的作用域
				candidate.setScope(scopeMetadata.getScopeName());
				//为Bean生成名称
				String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);
				//如果扫描到的Bean不是Spring的注解Bean，则为Bean设置默认值，
				//设置Bean的自动依赖注入装配属性等
				if (candidate instanceof AbstractBeanDefinition) {
					postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
				}
				//如果扫描到的Bean是Spring的注解Bean，则处理其通用的Spring注解
				if (candidate instanceof AnnotatedBeanDefinition) {
					//处理注解Bean中通用的注解，在分析注解Bean定义类读取器时已经分析过
					AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
				}
				//根据Bean名称检查指定的Bean是否需要在容器中注册，或者在容器中冲突
				if (checkCandidate(beanName, candidate)) {
					BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
					//根据注解中配置的作用域，为Bean应用相应的代理模式
					definitionHolder =
							AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
					beanDefinitions.add(definitionHolder);
					//向容器注册扫描到的Bean
					registerBeanDefinition(definitionHolder, this.registry);
				}
			}
		}
		return beanDefinitions;
	}

	/**
	 * Apply further settings to the given bean definition,
	 * 基于从扫描的组件类中获取的内容，对给定bean定义进行更多设置。
	 * beyond the contents retrieved from scanning the component class.
	 * @param beanDefinition the scanned bean definition
	 * @param beanName the generated bean name for the given bean
	 */
	protected void postProcessBeanDefinition(AbstractBeanDefinition beanDefinition, String beanName) {
		beanDefinition.applyDefaults(this.beanDefinitionDefaults);
		if (this.autowireCandidatePatterns != null) {
			beanDefinition.setAutowireCandidate(PatternMatchUtils.simpleMatch(this.autowireCandidatePatterns, beanName));
		}
	}

	/**
	 * Register the specified bean with the given registry.
	 * <p>Can be overridden in subclasses, e.g. to adapt the registration
	 * process or to register further bean definitions for each scanned bean.
	 * @param definitionHolder the bean definition plus bean name for the bean
	 * @param registry the BeanDefinitionRegistry to register the bean with
	 */
	protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
		BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
	}


	/**
	 * Check the given candidate's bean name, determining whether the corresponding
	 * 检查给定合格者的bean名称，确定相应的bean定义是否需要被注册或者和已存在的定义冲突。
	 * bean definition needs to be registered or conflicts with an existing definition.
	 * @param beanName the suggested name for the bean
	 * @param beanDefinition the corresponding bean definition
	 * @return {@code true} if the bean can be registered as-is;
	 * {@code false} if it should be skipped because there is an
	 * existing, compatible bean definition for the specified name
	 * @throws ConflictingBeanDefinitionException if an existing, incompatible
	 * bean definition has been found for the specified name
	 */
	protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
		if (!this.registry.containsBeanDefinition(beanName)) {
			return true;
		}
		BeanDefinition existingDef = this.registry.getBeanDefinition(beanName);
		BeanDefinition originatingDef = existingDef.getOriginatingBeanDefinition();
		if (originatingDef != null) {
			existingDef = originatingDef;
		}
		if (isCompatible(beanDefinition, existingDef)) {
			return false;
		}
		throw new ConflictingBeanDefinitionException("Annotation-specified bean name '" + beanName +
				"' for bean class [" + beanDefinition.getBeanClassName() + "] conflicts with existing, " +
				"non-compatible bean definition of same name and class [" + existingDef.getBeanClassName() + "]");
	}

	/**
	 * Determine whether the given new bean definition is compatible with
	 * 确定给定的新的bean定义和给定已存在的bean定义是否兼容。
	 * the given existing bean definition.
	 * <p>The default implementation considers them as compatible when the existing
	 * 默认实现认为是兼容的，在已经在的bean定义是来自相同的源或者来自非扫描源的时候。
	 * bean definition comes from the same source or from a non-scanning source.
	 * @param newDefinition the new bean definition, originated from scanning
	 * @param existingDefinition the existing bean definition, potentially an
	 * explicitly defined one or a previously generated one from scanning
	 * @return whether the definitions are considered as compatible, with the
	 * new definition to be skipped in favor of the existing definition
	 */
	protected boolean isCompatible(BeanDefinition newDefinition, BeanDefinition existingDefinition) {
		return (!(existingDefinition instanceof ScannedGenericBeanDefinition) ||  // explicitly registered overriding bean
				(newDefinition.getSource() != null && newDefinition.getSource().equals(existingDefinition.getSource())) ||  // scanned same file twice
				newDefinition.equals(existingDefinition));  // scanned equivalent class twice
	}


	/**
	 * Get the Environment from the given registry if possible, otherwise return a new
	 * StandardEnvironment.
	 */
	private static Environment getOrCreateEnvironment(BeanDefinitionRegistry registry) {
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		if (registry instanceof EnvironmentCapable) {
			return ((EnvironmentCapable) registry).getEnvironment();
		}
		return new StandardEnvironment();
	}

}
