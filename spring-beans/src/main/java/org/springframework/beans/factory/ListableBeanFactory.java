/*
 * Copyright 2002-2016 the original author or authors.
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

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * Extension of the {@link BeanFactory} interface to be implemented by bean factories
 * 这是BeanFactory的扩展接口，实现该接口的bean factory能够枚举其所有的bean实例，而不是
 * that can enumerate all their bean instances, rather than attempting bean lookup
 * 根据客户端的请求一个一个按照名称去进行bean查找。需要预加载所有bean definition的
 * by name one by one as requested by clients. BeanFactory implementations that
 * BeanFactory的实现类（比如基于XML的容器）可以实现这个接口
 * preload all their bean definitions (such as XML-based factories) may implement
 * this interface.
 *
 * <p>If this is a {@link HierarchicalBeanFactory}, the return values will <i>not</i>
 * 如果这是一个HierarchicalBeanFactory，返回值将不考虑任何BeanFactory层次结构，而只与
 * take any BeanFactory hierarchy into account, but will relate only to the beans
 * 当前工厂中定义的bean相关。也可以使用BeanFactoryUtils辅助类去使用父容器中的bean
 * defined in the current factory. Use the {@link BeanFactoryUtils} helper class
 * to consider beans in ancestor factories too.
 *
 * <p>The methods in this interface will just respect bean definitions of this factory.
 * 这个接口中的方法只关心该容器中的bean定义。
 * They will ignore any singleton beans that have been registered by other means like
 * 他们会忽略像通过ConfigurableBeanFactory的registerSingleton等其他方式注册的任何单例bean，
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}'s
 * getBeanNamesOfType以及getBeansOfType的异常也会手动检查这些已注册的单例。
 * {@code registerSingleton} method, with the exception of
 * 当然，BeanFactory的getBean也会透明地访问这种特殊的bean。然而，无论如何，在典型情况下
 * {@code getBeanNamesOfType} and {@code getBeansOfType} which will check
 * 所有的bean都是通过外部bean定义定义的，因此许多应用不需要关心这种区别
 * such manually registered singletons too. Of course, BeanFactory's {@code getBean}
 * does allow transparent access to such special beans as well. However, in typical
 * scenarios, all beans will be defined by external bean definitions anyway, so most
 * applications don't need to worry about this differentiation.
 *
 * <p><b>NOTE:</b> With the exception of {@code getBeanDefinitionCount}
 * 注意：getBeanDefinitionCount以及containsBeanDefinition的异常，
 * and {@code containsBeanDefinition}, the methods in this interface
 * 这个接口中的方法并没有被设计成频繁调用。实现可能变慢
 * are not designed for frequent invocation. Implementations may be slow.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 16 April 2001
 * @see HierarchicalBeanFactory
 * @see BeanFactoryUtils
 */
public interface ListableBeanFactory extends BeanFactory {

	/**
	 * Check if this bean factory contains a bean definition with the given name.
	 * 检查这个容器是否包含给定名称的bean定义
	 * <p>Does not consider any hierarchy this factory may participate in,
	 * 不会考虑该容器参与的层次结构，而且除了bean定义以外，忽略通过其他方式注册的单例bean
	 * and ignores any singleton beans that have been registered by
	 * other means than bean definitions.
	 * @param beanName the name of the bean to look for
	 * @return if this bean factory contains a bean definition with the given name
	 * @see #containsBean
	 */
	boolean containsBeanDefinition(String beanName);

	/**
	 * Return the number of beans defined in the factory.
	 * 返回该容器中已定义的bean的数量
	 * <p>Does not consider any hierarchy this factory may participate in,
	 * and ignores any singleton beans that have been registered by
	 * other means than bean definitions.
	 * @return the number of beans defined in the factory
	 */
	int getBeanDefinitionCount();

	/**
	 * Return the names of all beans defined in this factory.
	 * 返回该容器中所有已定义的bean的名称
	 * <p>Does not consider any hierarchy this factory may participate in,
	 * and ignores any singleton beans that have been registered by
	 * other means than bean definitions.
	 * @return the names of all beans defined in this factory,
	 * or an empty array if none defined
	 */
	String[] getBeanDefinitionNames();

	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * 通过分析bean definitions或者FactoryBeans的getObjectType的值，返回匹配给定类型（
	 * judging from either bean definitions or the value of {@code getObjectType}
	 * 包括子类型）的bean的名称。
	 * in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * 注意：这个方法只内省高级别的bean。它并不会检查可能匹配指定类型的内嵌的bean
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * 会考虑FactoryBeans创建的对象，这意味着要对FactoryBeans进行初始化。如果FactoryBean
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * 创建的对象不匹配，会用FactoryBean本身进行匹配。
	 * the raw FactoryBean itself will be matched against the type.
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * 不考虑该容器关联的层级结构，使用BeanFactoryUtils的beanNamesForTypeIncludingAncestors
	 * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
	 * 包含父容器中的bean。
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * 注意：除了bean定义以外，不会忽略通过其他方式注册的单例bean
	 * by other means than bean definitions.
	 * <p>This version of {@code getBeanNamesForType} matches all kinds of beans,
	 * getBeanNamesForType的版本匹配所有种类的bean，包括单例，原型，或者工厂bean。
	 * be it singletons, prototypes, or FactoryBeans. In most implementations, the
	 * 在许多实现中getBeanNamesForType(type, true, true)的结果是相同的。
	 * result will be the same as for {@code getBeanNamesForType(type, true, true)}.
	 * <p>Bean names returned by this method should always return bean names <i>in the
	 * 这个方法返回bean名称是尽可能按后端的配置的bean定义顺序返回的。
	 * order of definition</i> in the backend configuration, as far as possible.
	 * @param type the generically typed class or interface to match
	 *             需要匹配的通用类型类或者接口
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * 返回匹配给定对象类型（包括子类型）的bean（或者FactoryBeans创建的对象）的名称，
	 * the given object type (including subclasses), or an empty array if none
	 * 没有则返回空数组
	 * @since 4.2
	 * @see #isTypeMatch(String, ResolvableType)
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, ResolvableType)
	 */
	String[] getBeanNamesForType(ResolvableType type);

	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * judging from either bean definitions or the value of {@code getObjectType}
	 * in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>This version of {@code getBeanNamesForType} matches all kinds of beans,
	 * be it singletons, prototypes, or FactoryBeans. In most implementations, the
	 * result will be the same as for {@code getBeanNamesForType(type, true, true)}.
	 * <p>Bean names returned by this method should always return bean names <i>in the
	 * order of definition</i> in the backend configuration, as far as possible.
	 * @param type the class or interface to match, or {@code null} for all bean names
	 *                                               或者传null获取所有bean名称
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class)
	 */
	String[] getBeanNamesForType(@Nullable Class<?> type);

	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * judging from either bean definitions or the value of {@code getObjectType}
	 * in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
	 * 如果设置了allowEagerInit会考虑FactoryBeans创建的对象，这意味着FactoryBeans会初始化。
	 * which means that FactoryBeans will get initialized. If the object created by the
	 * 工厂生成的对象不能匹配则对工厂本身进行匹配。
	 * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
	 * 如果allowEagerInit没有设置，则仅仅对工厂本身进行匹配（这样并不需要对每个工厂进行初始化）
	 * (which doesn't require initialization of each FactoryBean).
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>Bean names returned by this method should always return bean names <i>in the
	 * order of definition</i> in the backend configuration, as far as possible.
	 * @param type the class or interface to match, or {@code null} for all bean names
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 *                                是否包含原型作用域或者其他作用域的bean，或者仅仅包含
	 * or just singletons (also applies to FactoryBeans)
	 *                             单例bean（也应用于FactoryBeans）
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 *                       是否初始化懒加载的单例以及工厂生成的对象（或者是factory-bean引用
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 *                       指定的工厂方法生成的对象）
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 *                       注意FactoryBeans需要饿汉式初始化来确定他们的类型：因此要知道这个
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 *                       标志设为true将会初始化FactoryBeans以及factory-bean引用
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit);

	/**
	 * Return the bean instances that match the given object type (including
	 * 返回匹配给定对象类型的bean实例
	 * subclasses), judging from either bean definitions or the value of
	 * {@code getObjectType} in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beansOfTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>This version of getBeansOfType matches all kinds of beans, be it
	 * singletons, prototypes, or FactoryBeans. In most implementations, the
	 * result will be the same as for {@code getBeansOfType(type, true, true)}.
	 * <p>The Map returned by this method should always return bean names and
	 * 这个方法返回的map应该总是返回bean names以及对应的bean实例
	 * corresponding bean instances <i>in the order of definition</i> in the
	 * backend configuration, as far as possible.
	 * @param type the class or interface to match, or {@code null} for all concrete beans
	 *              传null获取所有实例
	 * @return a Map with the matching beans, containing the bean names as
	 * 包含匹配bean的map，该map bean name做key，相应的bean实例做值
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @since 1.1.2
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class)
	 */
	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException;

	/**
	 * Return the bean instances that match the given object type (including
	 * subclasses), judging from either bean definitions or the value of
	 * {@code getObjectType} in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
	 * which means that FactoryBeans will get initialized. If the object created by the
	 * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
	 * (which doesn't require initialization of each FactoryBean).
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beansOfTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>The Map returned by this method should always return bean names and
	 * corresponding bean instances <i>in the order of definition</i> in the
	 * backend configuration, as far as possible.
	 * @param type the class or interface to match, or {@code null} for all concrete beans
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @return a Map with the matching beans, containing the bean names as
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException;

	/**
	 * Find all names of beans whose {@code Class} has the supplied {@link Annotation}
	 * 在当前还没有创建任何bean实例的时候，查找bean的Class有对应注解类型的bean名称
	 * type, without creating any bean instances yet.
	 * @param annotationType the type of annotation to look for
	 * @return the names of all matching beans
	 * @since 4.0
	 */
	String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType);

	/**
	 * Find all beans whose {@code Class} has the supplied {@link Annotation} type,
	 * 查找bean的Class有对应注解类型的bean，返回一个map包含bean name及对应的bean实例
	 * returning a Map of bean names with corresponding bean instances.
	 * @param annotationType the type of annotation to look for
	 * @return a Map with the matching beans, containing the bean names as
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @since 3.0
	 */
	Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException;

	/**
	 * Find an {@link Annotation} of {@code annotationType} on the specified
	 * 在指定bean上查找指定注解类型的注解，如果没找到则去父类及其接口中查找
	 * bean, traversing its interfaces and super classes if no annotation can be
	 * found on the given class itself.
	 * @param beanName the name of the bean to look for annotations on
	 * @param annotationType the annotation class to look for
	 * @return the annotation of the given type if found, or {@code null} otherwise
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 3.0
	 */
	@Nullable
	<A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
			throws NoSuchBeanDefinitionException;

}
