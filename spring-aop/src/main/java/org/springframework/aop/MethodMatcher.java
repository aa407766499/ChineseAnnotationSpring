/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.aop;

import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

/**
 * Part of a {@link Pointcut}: Checks whether the target method is eligible for advice.
 * Pointcut的组成部分：检查目标方法是否适合被增强。
 * <p>A MethodMatcher may be evaluated <b>statically</b> or at <b>runtime</b> (dynamically).
 * 方法匹配器可以被静态解析或者动态解析。静态匹配涉及方法和（可能）方法参数。动态匹配能够获得
 * Static matching involves method and (possibly) method attributes. Dynamic matching
 * 特定调用的参数，还能获得应用于连接点的前置增强的任何影响。
 * also makes arguments for a particular call available, and any effects of running
 * previous advice applying to the joinpoint.
 *
 * <p>If an implementation returns {@code false} from its {@link #isRuntime()}
 * 如果isRuntime()方法返回false，那么解析就是静态解析，该方法的所有调用的结果将是
 * method, evaluation can be performed statically, and the result will be the same
 * 相同的，无论传入的参数是什么。这意味着如果isRuntime()方法返回false，那么3个
 * for all invocations of this method, whatever their arguments. This means that
 * 参数的matches(java.lang.reflect.Method, Class, Object[])方法将绝不会被调用。
 * if the {@link #isRuntime()} method returns {@code false}, the 3-arg
 * {@link #matches(java.lang.reflect.Method, Class, Object[])} method will never be invoked.
 *
 * <p>If an implementation returns {@code true} from its 2-arg
 * 如果实现类其两个参数的matches(java.lang.reflect.Method, Class)方法返回true而且其isRuntime()方法
 * {@link #matches(java.lang.reflect.Method, Class)} method and its {@link #isRuntime()} method
 * 也返回true，在每一个相关的增强执行之前，3个参数的matches(java.lang.reflect.Method, Class, Object[])
 * returns {@code true}, the 3-arg {@link #matches(java.lang.reflect.Method, Class, Object[])}
 * 方法都会被调用，用来确定该增强是否应该运行。所有的前置增强，比如在拦截器链中的早期拦截器会运行，
 * method will be invoked <i>immediately before each potential execution of the related advice</i>,
 * 因此在解析的时候，他们在参数中产生的任何的状态改变或者ThreadLocal状态都能够获得。
 * to decide whether the advice should run. All previous advice, such as earlier interceptors
 * in an interceptor chain, will have run, so any state changes they have produced in
 * parameters or ThreadLocal state will be available at the time of evaluation.
 *
 * @author Rod Johnson
 * @since 11.11.2003
 * @see Pointcut
 * @see ClassFilter
 */
public interface MethodMatcher {

	/**
	 * Perform static checking whether the given method matches. If this
	 * 执行静态检查，检查给定方法是否匹配。如果返回false或者如果isRuntime()
	 * returns {@code false} or if the {@link #isRuntime()} method
	 * 返回false，没有进行运行时检查(比如matches(java.lang.reflect.Method, Class, Object[])调用)
	 * returns {@code false}, no runtime check (i.e. no.
	 * {@link #matches(java.lang.reflect.Method, Class, Object[])} call) will be made.
	 * @param method the candidate method
	 * @param targetClass the target class (may be {@code null}, in which case
	 * the candidate class must be taken to be the method's declaring class)
	 * @return whether or not this method matches statically
	 */
	boolean matches(Method method, @Nullable Class<?> targetClass);

	/**
	 * Is this MethodMatcher dynamic, that is, must a final call be made on the
	 * 该方法匹配器是动态的吗，换句话说，即使两个参数的matches方法返回true，
	 * {@link #matches(java.lang.reflect.Method, Class, Object[])} method at
	 * 也必须在最后调用matches(java.lang.reflect.Method, Class, Object[]) 方法
	 * runtime even if the 2-arg matches method returns {@code true}?
	 * <p>Can be invoked when an AOP proxy is created, and need not be invoked
	 * 在AOP代理被创建的时候会调用该方法，而在每一个方法调用前不需要调用该方法。
	 * again before each method invocation,
	 * @return whether or not a runtime match via the 3-arg
	 * {@link #matches(java.lang.reflect.Method, Class, Object[])} method
	 * is required if static matching passed
	 */
	boolean isRuntime();

	/**
	 * Check whether there a runtime (dynamic) match for this method,
	 * 检查该方法是否有运行时（动态）匹配，该方法必须已经静态匹配。
	 * which must have matched statically.
	 * <p>This method is invoked only if the 2-arg matches method returns
	 * 如果isRuntime()方法返回true，只有对于给定的方法和目标类两个参数的
	 * {@code true} for the given method and target class, and if the
	 * matches方法返回true，那么该方法才会被调用。在增强链中任何早期的增强
	 * {@link #isRuntime()} method returns {@code true}. Invoked
	 * 已经运行后，在可能要运行的增强之前会调用该方法。
	 * immediately before potential running of the advice, after any
	 * advice earlier in the advice chain has run.
	 * @param method the candidate method
	 * @param targetClass the target class (may be {@code null}, in which case
	 * the candidate class must be taken to be the method's declaring class)
	 * @param args arguments to the method
	 * @return whether there's a runtime match
	 * @see MethodMatcher#matches(Method, Class)
	 */
	boolean matches(Method method, @Nullable Class<?> targetClass, Object... args);


	/**
	 * Canonical instance that matches all methods.
	 * 匹配所有方法的标准实例。
	 */
	MethodMatcher TRUE = TrueMethodMatcher.INSTANCE;

}
