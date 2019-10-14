/*
 * Copyright 2002-2014 the original author or authors.
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

/**
 * Callback interface triggered at the end of the singleton pre-instantiation phase
 * 在容器启动期间单例预实例化阶段的末尾触发的回调接口。为了在常规的单例实例化后执行某些初始化，
 * during {@link BeanFactory} bootstrap. This interface can be implemented by
 * 单例bean要实现该接口，防止意外初始化的副作用。（比如ListableBeanFactory的getBeansOfType调用）
 * singleton beans in order to perform some initialization after the regular
 * 在这个意义上，可选择实现InitializingBean接口，该接口可以在本地bean构造（初始化）阶段触发。
 * singleton instantiation algorithm, avoiding side effects with accidental early
 * initialization (e.g. from {@link ListableBeanFactory#getBeansOfType} calls).
 * In that sense, it is an alternative to {@link InitializingBean} which gets
 * triggered right at the end of a bean's local construction phase.
 *
 * <p>This callback variant is somewhat similar to
 * 该回调变体与ContextRefreshedEvent有些相似，但不要求实现ApplicationListener，
 * {@link org.springframework.context.event.ContextRefreshedEvent} but doesn't
 * 不需要通过上下文层级过滤上下文引用。仅对beans包有依赖，且依赖很少，单独的ListableBeanFactory实现
 * require an implementation of {@link org.springframework.context.ApplicationListener},
 * 类使用该类。不仅是在ApplicationContext环境中。
 * with no need to filter context references across a context hierarchy etc.
 * It also implies a more minimal dependency on just the {@code beans} package
 * and is being honored by standalone {@link ListableBeanFactory} implementations,
 * not just in an {@link org.springframework.context.ApplicationContext} environment.
 *
 * <p><b>NOTE:</b> If you intend to start/manage asynchronous tasks, preferably
 * 如果你想要启动或者管理异步任务，最好实现Lifecycle接口，该接口提供了丰富的模型用于运行时
 * implement {@link org.springframework.context.Lifecycle} instead which offers
 * 管理，以及允许阶段的启动或者关闭。
 * a richer model for runtime management and allows for phased startup/shutdown.
 *
 * @author Juergen Hoeller
 * @since 4.1
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory#preInstantiateSingletons()
 */
public interface SmartInitializingSingleton {

	/**
	 * Invoked right at the end of the singleton pre-instantiation phase,
	 * 单例预实例化阶段的末尾调用，保证所有的常规单例bean已经被创建。
	 * with a guarantee that all regular singleton beans have been created
	 * already. {@link ListableBeanFactory#getBeansOfType} calls within
	 * 在启动期间ListableBeanFactory的getBeansOfType调用不会触发副作用。
	 * this method won't trigger accidental side effects during bootstrap.
	 * <p><b>NOTE:</b> This callback won't be triggered for singleton beans
	 * 注意，在BeanFactory启动后对于需要懒加载初始化的单例bean不会触发该回调，
	 * lazily initialized on demand after {@link BeanFactory} bootstrap,
	 * 任何其他bean作用域也是。仅在想要启动语义时，小心使用该接口。
	 * and not for any other bean scope either. Carefully use it for beans
	 * with the intended bootstrap semantics only.
	 */
	void afterSingletonsInstantiated();

}
