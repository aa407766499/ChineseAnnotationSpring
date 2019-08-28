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

package org.springframework.beans.factory.config;

import java.beans.PropertyEditor;
import java.security.AccessControlContext;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/**
 * Configuration interface to be implemented by most bean factories. Provides
 * 许多bean容器都要实现配置接口。除了在BeanFactory的bean容器客户端方法外，提供工具
 * facilities to configure a bean factory, in addition to the bean factory
 * 去配置bean容器。
 * client methods in the {@link org.springframework.beans.factory.BeanFactory}
 * interface.
 *
 * <p>This bean factory interface is not meant to be used in normal application
 * 这个bean容器接口不适合在正常的应用代码中使用：通常使用的时候，该接口和
 * code: Stick to {@link org.springframework.beans.factory.BeanFactory} or
 * BeanFactory或者ListableBeanFactory一起使用。这个扩展接口仅供于框架内部的插件使用，
 * {@link org.springframework.beans.factory.ListableBeanFactory} for typical
 * 以及专用于访问bean容器配置方法。
 * needs. This extended interface is just meant to allow for framework-internal
 * plug'n'play and for special access to bean factory configuration methods.
 *
 * @author Juergen Hoeller
 * @since 03.11.2003
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.beans.factory.ListableBeanFactory
 * @see ConfigurableListableBeanFactory
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

	/**
	 * Scope identifier for the standard singleton scope: "singleton".
	 * 标准单例作用域标识
	 * Custom scopes can be added via {@code registerScope}.
	 * 通过registerScope添加自定义作用域
	 * @see #registerScope
	 */
	String SCOPE_SINGLETON = "singleton";

	/**
	 * Scope identifier for the standard prototype scope: "prototype".
	 * 标准原型作用域标识
	 * Custom scopes can be added via {@code registerScope}.
	 * @see #registerScope
	 */
	String SCOPE_PROTOTYPE = "prototype";


	/**
	 * Set the parent of this bean factory.
	 * 设置该容器的父容器
	 * <p>Note that the parent cannot be changed: It should only be set outside
	 * 注意父容器不能被改变：如果在容器实例化的时候不能够获得父容器，那应该仅通过外部
	 * a constructor if it isn't available at the time of factory instantiation.
	 * 构造器设置父容器。
	 * @param parentBeanFactory the parent BeanFactory
	 * @throws IllegalStateException if this factory is already associated with
	 * a parent BeanFactory
	 * @see #getParentBeanFactory()
	 */
	void setParentBeanFactory(BeanFactory parentBeanFactory) throws IllegalStateException;

	/**
	 * Set the class loader to use for loading bean classes.
	 * 设置用于加载bean类的类加载器
	 * Default is the thread context class loader.
	 * 默认是线程上下文类加载器
	 * <p>Note that this class loader will only apply to bean definitions
	 * 请注意，这个类加载器只适用于未解析bean类的bean定义。这种情况默认直到
	 * that do not carry a resolved bean class yet. This is the case as of
	 * Spring 2.0：bean定义中有bean类名，类名在容器处理bean定义的时候使用
	 * Spring 2.0 by default: Bean definitions only carry bean class names,
	 * to be resolved once the factory processes the bean definition.
	 * @param beanClassLoader the class loader to use,
	 * or {@code null} to suggest the default class loader
	 *                        传null使用默认的类加载器
	 */
	void setBeanClassLoader(@Nullable ClassLoader beanClassLoader);

	/**
	 * Return this factory's class loader for loading bean classes.
	 * 返回容器加载bean类的类加载器
	 */
	@Nullable
	ClassLoader getBeanClassLoader();

	/**
	 * Specify a temporary ClassLoader to use for type matching purposes.
	 * 指定用于类型匹配的一个临时的类加载器。默认没有，仅使用标准的的bean类加载器
	 * Default is none, simply using the standard bean ClassLoader.
	 * <p>A temporary ClassLoader is usually just specified if
	 * 如果进行类加载期织入，通常要设置一个临时类加载器保证实际的bean类尽可能的懒加载。
	 * <i>load-time weaving</i> is involved, to make sure that actual bean
	 * classes are loaded as lazily as possible. The temporary loader is
	 * 一旦BeanFactory完成启动就要删除临时类加载器
	 * then removed once the BeanFactory completes its bootstrap phase.
	 * @since 2.5
	 */
	void setTempClassLoader(@Nullable ClassLoader tempClassLoader);

	/**
	 * Return the temporary ClassLoader to use for type matching purposes,
	 * 如果有的话，返回用于类型匹配的临时类加载器
	 * if any.
	 * @since 2.5
	 */
	@Nullable
	ClassLoader getTempClassLoader();

	/**
	 * Set whether to cache bean metadata such as given bean definitions
	 * 设置是否缓存bean元数据比如bean定义（使用覆盖的方式）以及已解析的bean类。
	 * (in merged fashion) and resolved bean classes. Default is on.
	 * 默认开启。
	 * <p>Turn this flag off to enable hot-refreshing of bean definition objects
	 * 将这个标志置为off开启bean定义对象特别是bean类的热刷新。如果这个标志为off，任何
	 * and in particular bean classes. If this flag is off, any creation of a bean
	 * bean实例的创建都会重新查找新解析类的bean类加载器。
	 * instance will re-query the bean class loader for newly resolved classes.
	 */
	void setCacheBeanMetadata(boolean cacheBeanMetadata);

	/**
	 * Return whether to cache bean metadata such as given bean definitions
	 * 返回是否缓存bean元数据比如bean定义（使用覆盖的方式）以及已解析的bean类。
	 * (in merged fashion) and resolved bean classes.
	 */
	boolean isCacheBeanMetadata();

	/**
	 * Specify the resolution strategy for expressions in bean definition values.
	 * 指定bean定义值表达式的解析策略。
	 * <p>There is no expression support active in a BeanFactory by default.
	 * 默认BeanFactory不支持表达式
	 * An ApplicationContext will typically set a standard expression strategy
	 * ApplicationContext通常会设置一个标准的表达式策略，使用统一的EL兼容风格
	 * here, supporting "#{...}" expressions in a Unified EL compatible style.
	 * 支持"#{...}"表达式
	 * @since 3.0
	 */
	void setBeanExpressionResolver(@Nullable BeanExpressionResolver resolver);

	/**
	 * Return the resolution strategy for expressions in bean definition values.
	 * 返回bean定义值表达式的解析策略
	 * @since 3.0
	 */
	@Nullable
	BeanExpressionResolver getBeanExpressionResolver();

	/**
	 * Specify a Spring 3.0 ConversionService to use for converting
	 * 指定一个用于转换属性值的Spring 3.0 转换服务，比如选择
	 * property values, as an alternative to JavaBeans PropertyEditors.
	 * JavaBeans的属性编辑器
	 * @since 3.0
	 */
	void setConversionService(@Nullable ConversionService conversionService);

	/**
	 * Return the associated ConversionService, if any.
	 * 如果有的话，返回关联的转换服务
	 * @since 3.0
	 */
	@Nullable
	ConversionService getConversionService();

	/**
	 * Add a PropertyEditorRegistrar to be applied to all bean creation processes.
	 * 添加一个属性编辑器注册器，将其应用于所有bean的创建处理
	 * <p>Such a registrar creates new PropertyEditor instances and registers them
	 * 这样的注册器创建新的属性编辑器实例，然后将其注册到给定的注册表中，每次进行bean
	 * on the given registry, fresh for each bean creation attempt. This avoids
	 * 的创建都是新的实例。这不需要对自定义编辑器进行同步。因此，通常最好使用这个方法代替
	 * the need for synchronization on custom editors; hence, it is generally
	 * registerCustomEditor方法。
	 * preferable to use this method instead of {@link #registerCustomEditor}.
	 * @param registrar the PropertyEditorRegistrar to register
	 */
	void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);

	/**
	 * Register the given custom property editor for all properties of the
	 * 对给定类型所有属性注册指定的自定义属性编辑器。容器配置时调用。
	 * given type. To be invoked during factory configuration.
	 * <p>Note that this method will register a shared custom editor instance;
	 * 请注意：这个方法会注册一个共享的自定义编辑器实例；要想访问实例时线程安全需要同步。
	 * access to that instance will be synchronized for thread-safety. It is
	 * 通常最好用addPropertyEditorRegistrar代替这个方法，以防自定义编辑器需要同步。
	 * generally preferable to use {@link #addPropertyEditorRegistrar} instead
	 * of this method, to avoid for the need for synchronization on custom editors.
	 * @param requiredType type of the property
	 * @param propertyEditorClass the {@link PropertyEditor} class to register
	 */
	void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass);

	/**
	 * Initialize the given PropertyEditorRegistry with the custom editors
	 * 初始化已在容器中注册过的自定义编辑器的给定属性编辑器注册表
	 * that have been registered with this BeanFactory.
	 * @param registry the PropertyEditorRegistry to initialize
	 */
	void copyRegisteredEditorsTo(PropertyEditorRegistry registry);

	/**
	 * Set a custom type converter that this BeanFactory should use for converting
	 * 设置容器用于转换bean属性值、构造器参数值等的类型转换器。
	 * bean property values, constructor argument values, etc.
	 * <p>This will override the default PropertyEditor mechanism and hence make
	 * 这会重写默认的属性编辑器机制，因此会使自定义的编辑器或者自定义的编辑器注册器失效
	 * any custom editors or custom editor registrars irrelevant.
	 * @see #addPropertyEditorRegistrar
	 * @see #registerCustomEditor
	 * @since 2.5
	 */
	void setTypeConverter(TypeConverter typeConverter);

	/**
	 * Obtain a type converter as used by this BeanFactory. This may be a fresh
	 * 获取容器使用的类型转换器。每次调用都是新的实例，因为类型转换器通常都是非线程安全的。
	 * instance for each call, since TypeConverters are usually <i>not</i> thread-safe.
	 * <p>If the default PropertyEditor mechanism is active, the returned
	 * 如果默认的属性编辑器机制，返回的类型转换器将能够感知所有已经注册的自定义编辑器
	 * TypeConverter will be aware of all custom editors that have been registered.
	 * @since 2.5
	 */
	TypeConverter getTypeConverter();

	/**
	 * Add a String resolver for embedded values such as annotation attributes.
	 * 添加内嵌值字符串解析器比如注解属性
	 * @param valueResolver the String resolver to apply to embedded values
	 * @since 3.0
	 */
	void addEmbeddedValueResolver(StringValueResolver valueResolver);

	/**
	 * Determine whether an embedded value resolver has been registered with this
	 * 确定容器中是否注册过内嵌值解析器，resolveEmbeddedValue(String)将使用内嵌值解析器
	 * bean factory, to be applied through {@link #resolveEmbeddedValue(String)}.
	 * @since 4.3
	 */
	boolean hasEmbeddedValueResolver();

	/**
	 * Resolve the given embedded value, e.g. an annotation attribute.
	 * 解析内嵌值，比如注解属性
	 * @param value the value to resolve
	 * @return the resolved value (may be the original value as-is)
	 * 解析值（可以是原值）
	 * @since 3.0
	 */
	@Nullable
	String resolveEmbeddedValue(String value);

	/**
	 * Add a new BeanPostProcessor that will get applied to beans created
	 * 添加bean后置处理器，将其应用于容器创建的bean。容器配置期间调用。
	 * by this factory. To be invoked during factory configuration.
	 * <p>Note: Post-processors submitted here will be applied in the order of
	 * 请注意：这里添加的后置处理器将按照注册的顺序应用；通过实现Ordered接口的
	 * registration; any ordering semantics expressed through implementing the
	 * 任何排序语义将被忽略。
	 * {@link org.springframework.core.Ordered} interface will be ignored. Note
	 * 注意：自动检测的后置处理器将在以编程方式注册的后置处理器之后应用。
	 * that autodetected post-processors (e.g. as beans in an ApplicationContext)
	 * will always be applied after programmatically registered ones.
	 * @param beanPostProcessor the post-processor to register
	 */
	void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

	/**
	 * Return the current number of registered BeanPostProcessors, if any.
	 * 如果有的话，返回已注册的bean后置处理器的数量
	 */
	int getBeanPostProcessorCount();

	/**
	 * Register the given scope, backed by the given Scope implementation.
	 * 注册给定的作用域，该作用域要是Scope接口的实现
	 * @param scopeName the scope identifier
	 * @param scope the backing Scope implementation
	 */
	void registerScope(String scopeName, Scope scope);

	/**
	 * Return the names of all currently registered scopes.
	 * 获取当前所有注册的作用域的名称。
	 * <p>This will only return the names of explicitly registered scopes.
	 * 这仅会明确地返回注册的作用域名称。
	 * Built-in scopes such as "singleton" and "prototype" won't be exposed.
	 * 内建的比如"singleton"和"prototype"不会暴露出来
	 * @return the array of scope names, or an empty array if none
	 * @see #registerScope
	 */
	String[] getRegisteredScopeNames();

	/**
	 * Return the Scope implementation for the given scope name, if any.
	 * 如果有的话，返回给定作用域名称的Scope接口实现。
	 * <p>This will only return explicitly registered scopes.
	 * Built-in scopes such as "singleton" and "prototype" won't be exposed.
	 * @param scopeName the name of the scope
	 * @return the registered Scope implementation, or {@code null} if none
	 * @see #registerScope
	 */
	@Nullable
	Scope getRegisteredScope(String scopeName);

	/**
	 * Provides a security access control context relevant to this factory.
	 * 返回与该容器相关的安全访问控制上下文
	 * @return the applicable AccessControlContext (never {@code null})
	 * @since 3.0
	 */
	AccessControlContext getAccessControlContext();

	/**
	 * Copy all relevant configuration from the given other factory.
	 * 从其他容器中复制所有相关配置。
	 * <p>Should include all standard configuration settings as well as
	 * 应该包括所有标准配置设定、bean后置处理器、作用域、以及容器专用的内部设定。
	 * BeanPostProcessors, Scopes, and factory-specific internal settings.
	 * Should not include any metadata of actual bean definitions,
	 * 不应该包括任何实际bean定义的元数据，比如的BeanDefinition对象以及bean名称别名
	 * such as BeanDefinition objects and bean name aliases.
	 * @param otherFactory the other BeanFactory to copy from
	 */
	void copyConfigurationFrom(ConfigurableBeanFactory otherFactory);

	/**
	 * Given a bean name, create an alias. We typically use this method to
	 * 给定bean名称，创建别名。通常我们将这个方法用于支持对于XML非法地名称
	 * support names that are illegal within XML ids (used for bean names).
	 * （用于bean的名称）。
	 * <p>Typically invoked during factory configuration, but can also be
	 * 通常在容器配置期间调用，也可以用于在运行期注册别名。因此，容器实现
	 * used for runtime registration of aliases. Therefore, a factory
	 * 应该同步别名的访问
	 * implementation should synchronize alias access.
	 * @param beanName the canonical name of the target bean
	 * @param alias the alias to be registered for the bean
	 * @throws BeanDefinitionStoreException if the alias is already in use
	 */
	void registerAlias(String beanName, String alias) throws BeanDefinitionStoreException;

	/**
	 * Resolve all alias target names and aliases registered in this
	 * 应用字符串值解析器解析目标名称的所有别名，以及容器中注册的别名。
	 * factory, applying the given StringValueResolver to them.
	 * <p>The value resolver may for example resolve placeholders
	 * 比如，该字符串值解析器解析目标bean名称甚至是别名中的占位符。
	 * in target bean names and even in alias names.
	 * @param valueResolver the StringValueResolver to apply
	 * @since 2.5
	 */
	void resolveAliases(StringValueResolver valueResolver);

	/**
	 * Return a merged BeanDefinition for the given bean name,
	 * 返回给定bean名称的已被覆盖的BeanDefinition，必要时用父bean定义覆盖
	 * merging a child bean definition with its parent if necessary.
	 * 子bean定义。
	 * Considers bean definitions in ancestor factories as well.
	 * 也考虑父容器中的bean定义
	 * @param beanName the name of the bean to retrieve the merged definition for
	 * @return a (potentially merged) BeanDefinition for the given bean
	 * @throws NoSuchBeanDefinitionException if there is no bean definition with the given name
	 * @since 2.5
	 */
	BeanDefinition getMergedBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * Determine whether the bean with the given name is a FactoryBean.
	 * 确定给定名称的bean是否是工厂bean。
	 * @param name the name of the bean to check
	 * @return whether the bean is a FactoryBean
	 * ({@code false} means the bean exists but is not a FactoryBean)
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.5
	 */
	boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Explicitly control the current in-creation status of the specified bean.
	 * 显式控制指定bean的当前创建状态。
	 * For container-internal use only.
	 * @param beanName the name of the bean
	 * @param inCreation whether the bean is currently in creation
	 *                   bean当前是否处于创建中
	 * @since 3.1
	 */
	void setCurrentlyInCreation(String beanName, boolean inCreation);

	/**
	 * Determine whether the specified bean is currently in creation.
	 * 确定bean当前是否处于创建中
	 * @param beanName the name of the bean
	 * @return whether the bean is currently in creation
	 * @since 2.5
	 */
	boolean isCurrentlyInCreation(String beanName);

	/**
	 * Register a dependent bean for the given bean,
	 * 注册指定bean的依赖bean
	 * to be destroyed before the given bean is destroyed.
	 * @param beanName the name of the bean
	 * @param dependentBeanName the name of the dependent bean
	 * @since 2.5
	 */
	void registerDependentBean(String beanName, String dependentBeanName);

	/**
	 * Return the names of all beans which depend on the specified bean, if any.
	 * 如果有的话，返回依赖于指定bean的所有bean名称。
	 * @param beanName the name of the bean
	 * @return the array of dependent bean names, or an empty array if none
	 * @since 2.5
	 */
	String[] getDependentBeans(String beanName);

	/**
	 * Return the names of all beans that the specified bean depends on, if any.
	 * 如果有的话，返回指定bean依赖的所有bean名称。
	 * @param beanName the name of the bean
	 * @return the array of names of beans which the bean depends on,
	 * or an empty array if none
	 * @since 2.5
	 */
	String[] getDependenciesForBean(String beanName);

	/**
	 * Destroy the given bean instance (usually a prototype instance
	 * 根据bean定义销毁给定bean实例（通常是从容器中获取的原型实例）
	 * obtained from this factory) according to its bean definition.
	 * <p>Any exception that arises during destruction should be caught
	 * 销毁期间发生的异常应该被捕获和记录，而不是传递给方法的调用者。
	 * and logged instead of propagated to the caller of this method.
	 * @param beanName the name of the bean definition
	 * @param beanInstance the bean instance to destroy
	 */
	void destroyBean(String beanName, Object beanInstance);

	/**
	 * Destroy the specified scoped bean in the current target scope, if any.
	 * 如果有的话，销毁当前目标作用域中指定的作用域bean
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 * @param beanName the name of the scoped bean
	 */
	void destroyScopedBean(String beanName);

	/**
	 * Destroy all singleton beans in this factory, including inner beans that have
	 * 销毁所有容器中的单例bean，包括已被注册为disposable的内部bean。容器关闭时会调用。
	 * been registered as disposable. To be called on shutdown of a factory.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 */
	void destroySingletons();

}
