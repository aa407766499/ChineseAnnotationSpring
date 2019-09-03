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

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * (粗略理解为查单个bean)
 * The root interface for accessing a Spring bean container.
 * 这个顶层接口用于访问spring bean 容器（bean可以理解为组件）
 * This is the basic client view of a bean container;
 * 这是一个bean容器的客户端基础视图
 * further interfaces such as {@link ListableBeanFactory} and
 * 更多的接口比如ListableBeanFactory以及ConfigurableBeanFactory被用于特定的用途
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}
 * are available for specific purposes.
 *
 * <p>This interface is implemented by objects that hold a number of bean definitions,
 * 这个接口由持有一定数量的bean定义的对象实现,每一个bean定义由一个String类型名称唯一指定。
 * each uniquely identified by a String name. Depending on the bean definition,
 * 根据bean定义,工厂要么返回一个所包含对象的独立实例（原型设计模式），要么生成返回
 * the factory will return either an independent instance of a contained object
 * 一个单例的共享实例（更好是选择单例设计模式，这样实例在容器中是一个单例）
 * (the Prototype design pattern), or a single shared instance (a superior
 * 实例的类型取决于bean工厂的配置:API是相同的,从spring2.0开始，根据具体的
 * alternative to the Singleton design pattern, in which the instance is a
 * 应用上下文，可以使用更多的作用域（比如在 web 环境下有request作用域以及session作用域）
 * singleton in the scope of the factory). Which type of instance will be returned
 * 这个方法的目的是为了让BeanFactory成为应用组件的注册中心，将应用组件的配置集中起来
 * depends on the bean factory configuration: the API is the same. Since Spring
 * （比如不再需要每个对象都去读取属性文件）,参见4到11章"Expert One-on-One J2EE Design and
 * 2.0, further scopes are available depending on the concrete application
 * Development"有关于这个方法益处的讨论。
 * context (e.g. "request" and "session" scopes in a web environment).
 * <p>The point of this approach is that the BeanFactory is a central registry
 * of application components, and centralizes configuration of application
 * components (no more do individual objects need to read properties files,
 * for example). See chapters 4 and 11 of "Expert One-on-One J2EE Design and
 * Development" for a discussion of the benefits of this approach.
 *
 * <p>Note that it is generally better to rely on Dependency Injection
 * 注意容器通常最好是通过依赖注入（推送配置），通过设置器方法和构造器方法来
 * ("push" configuration) to configure application objects through setters
 * 配置应用对象,而不是使用像容器查找的方式来拉去配置。
 * or constructors, rather than use any form of "pull" configuration like a
 * 使用BeanFactory接口及其子接口可以用来实现spring依赖注入的功能
 * BeanFactory lookup. Spring's Dependency Injection functionality is
 * implemented using this BeanFactory interface and its subinterfaces.
 *
 * <p>Normally a BeanFactory will load bean definitions stored in a configuration
 * 通常BeanFactory会从配置资源（比如xml文档）中加载bean定义,以及使用beans包配置beans。
 * source (such as an XML document), and use the {@code org.springframework.beans}
 * 然而，在java代码中，一个实现在必要时也可以直接简单地返回java对象。对于bean定义如何
 * package to configure the beans. However, an implementation could simply return
 * 存储没有进行限制，可以存储在LDAP,RDBMS,XML,属性文件中等。鼓励实现支持beans中的引用。
 * Java objects it creates as necessary directly in Java code. There are no
 * constraints on how the definitions could be stored: LDAP, RDBMS, XML,
 * properties file, etc. Implementations are encouraged to support references
 * amongst beans (Dependency Injection).
 *
 * <p>In contrast to the methods in {@link ListableBeanFactory}, all of the
 * 相对于ListableBeanFactory的方法，如果是HierarchicalBeanFactory，接口的所有操作
 * operations in this interface will also check parent factories if this is a
 * 也会去检查父容器。如果bean在这个容器中没有找到，就会去访问最近的父容器。
 * {@link HierarchicalBeanFactory}. If a bean is not found in this factory instance,
 * 该容器的Beans应该重写任何父容器中相同名称的beans
 * the immediate parent factory will be asked. Beans in this factory instance
 * are supposed to override beans of the same name in any parent factory.
 *
 * <p>Bean factory implementations should support the standard bean lifecycle interfaces
 * 容器的实现类应该尽可能的支持标准的bean生命周期接口。所有的初始化方法以及他们的顺序如下：
 * as far as possible. The full set of initialization methods and their standard order is:
 * <ol>
 * <li>BeanNameAware's {@code setBeanName}
 * 设置bean名称
 * <li>BeanClassLoaderAware's {@code setBeanClassLoader}
 * 设置bean类加载器
 * <li>BeanFactoryAware's {@code setBeanFactory}
 * 设置容器
 * <li>EnvironmentAware's {@code setEnvironment}
 * 设置环境
 * <li>EmbeddedValueResolverAware's {@code setEmbeddedValueResolver}
 * 设置内嵌值解析器
 * <li>ResourceLoaderAware's {@code setResourceLoader}
 * 设置资源加载器
 * (only applicable when running in an application context)
 * <li>ApplicationEventPublisherAware's {@code setApplicationEventPublisher}
 * 在应用上下文中设置容器事件推送器
 * (only applicable when running in an application context)
 * <li>MessageSourceAware's {@code setMessageSource}
 * 设置信息源
 * (only applicable when running in an application context)
 * <li>ApplicationContextAware's {@code setApplicationContext}
 * 设置容器
 * (only applicable when running in an application context)
 * <li>ServletContextAware's {@code setServletContext}
 * 设置servlet上下文
 * (only applicable when running in a web application context)
 * <li>{@code postProcessBeforeInitialization} methods of BeanPostProcessors
 * 在web应用容器中调用BeanPostProcessor的postProcessBeforeInitialization方法
 * <li>InitializingBean's {@code afterPropertiesSet}
 * 调用InitializingBean的afterPropertiesSet方法
 * <li>a custom init-method definition
 * 自定义初始化方法的定义
 * <li>{@code postProcessAfterInitialization} methods of BeanPostProcessors
 * </ol>
 * BeanPostProcessors的postProcessAfterInitialization方法
 * <p>On shutdown of a bean factory, the following lifecycle methods apply:
 * 在容器关闭时下面的生命周期方法要调用
 * <ol>
 * <li>{@code postProcessBeforeDestruction} methods of DestructionAwareBeanPostProcessors
 * DestructionAwareBeanPostProcessors的postProcessBeforeDestruction方法
 * <li>DisposableBean's {@code destroy}
 * DisposableBean的destroy方法
 * <li>a custom destroy-method definition
 * 自定义销毁方法的定义
 * </ol>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 13 April 2001
 * @see BeanNameAware#setBeanName
 * @see BeanClassLoaderAware#setBeanClassLoader
 * @see BeanFactoryAware#setBeanFactory
 * @see org.springframework.context.ResourceLoaderAware#setResourceLoader
 * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher
 * @see org.springframework.context.MessageSourceAware#setMessageSource
 * @see org.springframework.context.ApplicationContextAware#setApplicationContext
 * @see org.springframework.web.context.ServletContextAware#setServletContext
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization
 * @see InitializingBean#afterPropertiesSet
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getInitMethodName
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization
 * @see DisposableBean#destroy
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getDestroyMethodName
 */
public interface BeanFactory {

	/**
	 * Used to dereference a {@link FactoryBean} instance and distinguish it from
	 * "&"被用来间接引用FactoryBean以及与FactoryBean生成的bean相区别,比如被命名为
	 * beans <i>created</i> by the FactoryBean. For example, if the bean named
	 * myJndiObject的bean是一个FactoryBean,通过&myJndiObject获取可以获取工厂本身，
	 * {@code myJndiObject} is a FactoryBean, getting {@code &myJndiObject}
	 * 而不是工厂生成的实例
	 * will return the factory, not the instance returned by the factory.
	 */
	//对FactoryBean的转义定义，因为如果使用bean的名字检索FactoryBean得到的对象是工厂生成的对象，
	//如果需要得到工厂本身，需要转义
	String FACTORY_BEAN_PREFIX = "&";


	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * 返回一个指定bean的实例，这个实例可以使共享的或者是独立的。
	 * <p>This method allows a Spring BeanFactory to be used as a replacement for the
	 * 这个方法允许spring BeanFactory 作为单例或者原型模式的替代品。调用者可以持有返回
	 * Singleton or Prototype design pattern. Callers may retain references to
	 * 的单例对象的引用。
	 * returned objects in the case of Singleton beans.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * 将别名翻译成相应标准的bean名称。如果在当前容器中没有找到则去父容器中查找
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to retrieve
	 *             检索的bean的名称
	 * @return an instance of the bean
	 * 	        bean的实例
	 * @throws NoSuchBeanDefinitionException if there is no bean definition
	 * with the specified name
	 * 没有指定名称的bean
	 * @throws BeansException if the bean could not be obtained
	 * bean不能获取
	 */
	//根据bean的名字，获取在IOC容器中得到bean实例
	Object getBean(String name) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * <p>Behaves the same as {@link #getBean(String)}, but provides a measure of type
	 * 功能和getBean(String)一样，但是如果bean不是要求的类型,将通过抛出
	 * safety by throwing a BeanNotOfRequiredTypeException if the bean is not of the
	 * BeanNotOfRequiredTypeException来提供一种类型安全的措施。
	 * required type. This means that ClassCastException can't be thrown on casting
	 * 这意味着结果转换正确将不会抛出ClassCastException，而ClassCastException会发生在
	 * the result correctly, as can happen with {@link #getBean(String)}.
	 * getBean(String)
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to retrieve
	 * @param requiredType type the bean must match. Can be an interface or superclass
	 *                      bean的类型必须匹配，可能是实际类的接口、父类或者任何能匹配的类型。
	 * of the actual class, or {@code null} for any match. For example, if the value
	 * 比如，如果该参数是Object.class,无论返回实例的类型是什么这个方法都会成功执行
	 * is {@code Object.class}, this method will succeed whatever the class of the
	 * returned instance.
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
	 * @throws BeansException if the bean could not be created
	 */
	//根据bean的名字和Class类型来得到bean实例，增加了类型安全验证机制。
	<T> T getBean(String name, @Nullable Class<T> requiredType) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * <p>Allows for specifying explicit constructor arguments / factory method arguments,
	 * 允许指定明确的构造器参数/工厂方法参数重写bean definition中指定的默认参数（如果有的话）
	 * overriding the specified default arguments (if any) in the bean definition.
	 * @param name the name of the bean to retrieve
	 * @param args arguments to use when creating a bean instance using explicit arguments
	 *             使用明确的参数创建实例的时候要使用这些参数（仅在创建新实例时应用，而不是检索现有实例。）
	 * (only applied when creating a new instance as opposed to retrieving an existing one)
	 *
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanDefinitionStoreException if arguments have been given but
	 * 给定了参数但是创建的bean不是原型作用域
	 * the affected bean isn't a prototype
	 * @throws BeansException if the bean could not be created
	 * @since 2.5
	 */
	Object getBean(String name, Object... args) throws BeansException;

	/**
	 * Return the bean instance that uniquely matches the given object type, if any.
	 * 如果有的话，返回唯一匹配给定对象类型的实例。
	 * <p>This method goes into {@link ListableBeanFactory} by-type lookup territory
	 * 这个方法可以进入ListableBeanFactory中进行类型范围查找，也可以根据给定类型的名称
	 * but may also be translated into a conventional by-name lookup based on the name
	 * 变成传统的按名称查找。使用ListableBeanFactory或者 BeanFactoryUtils,在beans集合中
	 * of the given type. For more extensive retrieval operations across sets of beans
	 * 进行更多的检索操作。
	 * use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
	 * @param requiredType type the bean must match; can be an interface or superclass.
	 *                     类型必须匹配；可以使接口或者父类。不允许为NULL。
	 * {@code null} is disallowed.
	 * @return an instance of the single bean matching the required type
	 * 返回匹配给定类型的单例bean的实例
	 * @throws NoSuchBeanDefinitionException if no bean of the given type was found
	 * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
	 * @throws BeansException if the bean could not be created
	 * @since 3.0
	 * @see ListableBeanFactory
	 */
	<T> T getBean(Class<T> requiredType) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * <p>Allows for specifying explicit constructor arguments / factory method arguments,
	 * overriding the specified default arguments (if any) in the bean definition.
	 * <p>This method goes into {@link ListableBeanFactory} by-type lookup territory
	 * but may also be translated into a conventional by-name lookup based on the name
	 * of the given type. For more extensive retrieval operations across sets of beans,
	 * use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
	 * @param requiredType type the bean must match; can be an interface or superclass.
	 * {@code null} is disallowed.
	 * @param args arguments to use when creating a bean instance using explicit arguments
	 * (only applied when creating a new instance as opposed to retrieving an existing one)
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanDefinitionStoreException if arguments have been given but
	 * the affected bean isn't a prototype
	 * @throws BeansException if the bean could not be created
	 * @since 4.1
	 */
	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;


	/**
	 * Does this bean factory contain a bean definition or externally registered singleton
	 * 这个bean容器是否包含给定名称的Bean definition或者在外部注册了给定名称的单例实例
	 * instance with the given name?
	 * <p>If the given name is an alias, it will be translated back to the corresponding
	 * 如果给定的名称是别名，将会把别名翻译为相应的标准bean name。
	 * canonical bean name.
	 * <p>If this factory is hierarchical, will ask any parent factory if the bean cannot
	 * 如果这个容器是分层的，如果在该容器中未找到bean 将访问父容器
	 * be found in this factory instance.
	 * <p>If a bean definition or singleton instance matching the given name is found,
	 * 如果找到了匹配给定名称的bean definition或者单例实例，这个方法在以下情况下都会返回true:
	 * this method will return {@code true} whether the named bean definition is concrete
	 * 命名的bean definition是具体的还是抽象的，懒汉式或者饿汉式，在作用域或者不在作用域。因此
	 * or abstract, lazy or eager, in scope or not. Therefore, note that a {@code true}
	 * 注意这个方法返回true并不意味着getBean将能够获取相同名称的实例。
	 * return value from this method does not necessarily indicate that {@link #getBean}
	 * will be able to obtain an instance for the same name.
	 * @param name the name of the bean to query
	 * @return whether a bean with the given name is present
	 */
	//提供对bean的检索，看看是否在IOC容器有这个名字的bean
	boolean containsBean(String name);

	/**
	 * Is this bean a shared singleton? That is, will {@link #getBean} always
	 * 该bean是否是共享单例bean？换句话说，是否总是返回相同的实例？
	 * return the same instance?
	 * <p>Note: This method returning {@code false} does not clearly indicate
	 * 注意：这个方法返回false并没有明确表明是原型作用域。这只是表明不是一个单例，也可能对应于
	 * independent instances. It indicates non-singleton instances, which may correspond
	 * 一个作用域bean。使用isPrototype操作来明确这是原型作用域bean
	 * to a scoped bean as well. Use the {@link #isPrototype} operation to explicitly
	 * check for independent instances.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @return whether this bean corresponds to a singleton instance
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @see #getBean
	 * @see #isPrototype
	 */
	//根据bean名字得到bean实例，并同时判断这个bean是不是单例
	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Is this bean a prototype? That is, will {@link #getBean} always return
	 * independent instances?
	 * <p>Note: This method returning {@code false} does not clearly indicate
	 * a singleton object. It indicates non-independent instances, which may correspond
	 * to a scoped bean as well. Use the {@link #isSingleton} operation to explicitly
	 * check for a shared singleton instance.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @return whether this bean will always deliver independent instances
	 * 该Bean总是生成独立的实例
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.0.3
	 * @see #getBean
	 * @see #isSingleton
	 */
	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Check whether the bean with the given name matches the specified type.
	 * 检查给定名称的bean是否匹配指定的类型
	 * More specifically, check whether a {@link #getBean} call for the given name
	 * 更具体的说，检查给定名称的getBean调用返回的对象是否匹配指定目标类型
	 * would return an object that is assignable to the specified target type.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @param typeToMatch the type to match against (as a {@code ResolvableType})
	 * 匹配的类型是ResolvableType类型及其子类型
	 * @return {@code true} if the bean type matches,
	 * {@code false} if it doesn't match or cannot be determined yet
	 * 如果不能匹配或者无法确定则返回false
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 4.2
	 * @see #getBean
	 * @see #getType
	 */
	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * Check whether the bean with the given name matches the specified type.
	 * More specifically, check whether a {@link #getBean} call for the given name
	 * would return an object that is assignable to the specified target type.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @param typeToMatch the type to match against (as a {@code Class})
	 *                    类型匹配class类型及其子类型
	 * @return {@code true} if the bean type matches,
	 * {@code false} if it doesn't match or cannot be determined yet
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.0.1
	 * @see #getBean
	 * @see #getType
	 */
	boolean isTypeMatch(String name, @Nullable Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * Determine the type of the bean with the given name. More specifically,
	 * 确定给定名称bean的类型。更具体的说，确定给定名称调用getBean返回的对象类型
	 * determine the type of object that {@link #getBean} would return for the given name.
	 * <p>For a {@link FactoryBean}, return the type of object that the FactoryBean creates,
	 * 对于一个FactoryBean，会返回FactoryBean生产的对象的类型，和FactoryBean#getObjectType()的功能一样
	 * as exposed by {@link FactoryBean#getObjectType()}.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @return the type of the bean, or {@code null} if not determinable
	 * 返回对象的类型，或者如果无法确定返回null
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 1.1.2
	 * @see #getBean
	 * @see #isTypeMatch
	 */
	//得到bean实例的Class类型
	@Nullable
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Return the aliases for the given bean name, if any.
	 * 如果有的话，返回给定bean name的别名
	 * All of those aliases point to the same bean when used in a {@link #getBean} call.
	 * 在调用getBean的时候，会使用相同bean的所有别名
	 * <p>If the given name is an alias, the corresponding original bean name
	 * 如果给定的名称是别名，则相应原bean名称以及其他别名（如果有的话）将会返回，
	 * and other aliases (if any) will be returned, with the original bean name
	 * 原bean名称将是数组的第一个元素。
	 * being the first element in the array.
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the bean name to check for aliases
	 * @return the aliases, or an empty array if none
	 * @see #getBean
	 */
	//得到bean的别名，如果根据别名检索，那么其原名也会被检索出来
	String[] getAliases(String name);

}
