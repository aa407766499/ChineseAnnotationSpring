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

package org.aopalliance.intercept;

import java.lang.reflect.AccessibleObject;

/**
 * This interface represents a generic runtime joinpoint (in the AOP
 * 该接口代表一个一般的运行时连接点（AOP术语）
 * terminology).
 *
 * <p>A runtime joinpoint is an <i>event</i> that occurs on a static
 * 一个运行时连接点是一个事件，该事件出现在一个静态连接点上（比如程序中
 * joinpoint (i.e. a location in a the program). For instance, an
 * 的一个位置）。比如，一次调用就是一个方法（静态连接点）上的运行时连接点
 * invocation is the runtime joinpoint on a method (static joinpoint).
 * 给定连接点的静态部分通常可以调用getStaticPart()方法获得
 * The static part of a given joinpoint can be generically retrieved
 * using the {@link #getStaticPart()} method.
 *
 * <p>In the context of an interception framework, a runtime joinpoint
 * 在拦截框架的上下文中，一个运行时连接点是访问一个可访问对象（方法，构造器，
 * is then the reification of an access to an accessible object (a
 * 字段）的具体化，比如一个连接点的静态部分。它被传递到安装在静态连接点上的拦截器
 * method, a constructor, a field), i.e. the static part of the
 * joinpoint. It is passed to the interceptors that are installed on
 * the static joinpoint.
 *
 * @author Rod Johnson
 * @see Interceptor
 */
public interface Joinpoint {

	/**
	 * Proceed to the next interceptor in the chain.
	 * 执行拦截器链中的下一个拦截器。
	 * <p>The implementation and the semantics of this method depends
	 * 该方法的实现和语义取决于实际的连接点类型（查看子接口）
	 * on the actual joinpoint type (see the children interfaces).
	 * @return see the children interfaces' proceed definition
	 * @throws Throwable if the joinpoint throws an exception
	 */
	Object proceed() throws Throwable;

	/**
	 * Return the object that holds the current joinpoint's static part.
	 * 返回持有当前连接点静态部分的对象。
	 * <p>For instance, the target object for an invocation.
	 * 比如，调用的目标对象。
	 * @return the object (can be null if the accessible object is static)
	 */
	Object getThis();

	/**
	 * Return the static part of this joinpoint.
	 * 返回该连接点的静态部分。
	 * <p>The static part is an accessible object on which a chain of
	 * 静态部分是一个可访问对象，拦截器链安装在该对象上。
	 * interceptors are installed.
	 */
	AccessibleObject getStaticPart();

}
