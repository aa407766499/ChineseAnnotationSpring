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

package org.springframework.core;

/**
 * Interface to be implemented by decorating proxies, in particular Spring AOP
 * 包装代理实现的接口，特别是Spring AOP代理，还有可能是有包装语义的自定义代理。
 * proxies but potentially also custom proxies with decorator semantics.
 *
 * <p>Note that this interface should just be implemented if the decorated class
 * 注意：如果被包装的类没有处于代理类层级的开头，那么仅实现该接口。特别地，一个目标类
 * is not within the hierarchy of the proxy class to begin with. In particular,
 * 代理比如Spring AOP CGLIB代理不应该实现该接口因为在目标类上的任何查找可以在代理类上
 * a "target-class" proxy such as a Spring AOP CGLIB proxy should not implement
 * 简单执行。
 * it since any lookup on the target class can simply be performed on the proxy
 * class there anyway.
 *
 * <p>Defined in the core module in order to allow
 * 在核心模块中定义该接口是允许AnnotationAwareOrderComparator使用该类用于内省目的，
 * #{@link org.springframework.core.annotation.AnnotationAwareOrderComparator}
 * 特别是注解查找。
 * (and potential other candidates without spring-aop dependencies) to use it
 * for introspection purposes, in particular annotation lookups.
 *
 * @author Juergen Hoeller
 * @since 4.3
 */
public interface DecoratingProxy {

	/**
	 * Return the (ultimate) decorated class behind this proxy.
	 * 返回该代理背后的被包装的类。
	 * <p>In case of an AOP proxy, this will be the ultimate target class,
	 * 如果是AOP代理，这将是最终的目标类，而不是最接近的目标（如果多层嵌套代理）
	 * not just the immediate target (in case of multiple nested proxies).
	 * @return the decorated class (never {@code null})
	 */
	Class<?> getDecoratedClass();

}
