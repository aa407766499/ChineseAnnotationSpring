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

import org.aopalliance.aop.Advice;

/**
 * This interface represents a generic interceptor.
 * 该接口代表一般的拦截器。
 * <p>A generic interceptor can intercept runtime events that occur
 * 一个一般拦截器能拦截发生在基础程序中的运行时事件。那些事件被连接点具体化。
 * within a base program. Those events are materialized by (reified
 * 运行时连接点可以是调用，字段访问，异常...
 * in) joinpoints. Runtime joinpoints can be invocations, field
 * access, exceptions... 
 *
 * <p>This interface is not used directly. Use the sub-interfaces
 * 该接口不能直接使用。使用子接口拦截指定事件。比如说，以下类实现某些
 * to intercept specific events. For instance, the following class
 * 指定拦截器，目的是实现一个调试器。
 * implements some specific interceptors in order to implement a
 * debugger:
 *
 * <pre class=code>
 * class DebuggingInterceptor implements MethodInterceptor, 
 *     ConstructorInterceptor, FieldInterceptor {
 *
 *   Object invoke(MethodInvocation i) throws Throwable {
 *     debug(i.getMethod(), i.getThis(), i.getArgs());
 *     return i.proceed();
 *   }
 *
 *   Object construct(ConstructorInvocation i) throws Throwable {
 *     debug(i.getConstructor(), i.getThis(), i.getArgs());
 *     return i.proceed();
 *   }
 * 
 *   Object get(FieldAccess fa) throws Throwable {
 *     debug(fa.getField(), fa.getThis(), null);
 *     return fa.proceed();
 *   }
 *
 *   Object set(FieldAccess fa) throws Throwable {
 *     debug(fa.getField(), fa.getThis(), fa.getValueToSet());
 *     return fa.proceed();
 *   }
 *
 *   void debug(AccessibleObject ao, Object this, Object value) {
 *     ...
 *   }
 * }
 * </pre>
 *
 * @author Rod Johnson
 * @see Joinpoint
 */
public interface Interceptor extends Advice {

}
