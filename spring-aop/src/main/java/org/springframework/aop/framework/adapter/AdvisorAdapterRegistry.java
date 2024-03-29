/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.aop.framework.adapter;

import org.aopalliance.intercept.MethodInterceptor;

import org.springframework.aop.Advisor;

/**
 * Interface for registries of Advisor adapters.
 * 该接口用于注册切面适配器
 * <p><i>This is an SPI interface, not to be implemented by any Spring user.</i>
 * 这是一个SPI接口，不需要被任何Spring用户实现，
 *
 * @author Rod Johnson
 * @author Rob Harrop
 */
public interface AdvisorAdapterRegistry {

	/**
	 * Return an Advisor wrapping the given advice.
	 * 返回包装给定增强的切面。
	 * <p>Should by default at least support
	 * 默认至少要支持MethodInterceptor，MethodBeforeAdvice，
	 * {@link org.aopalliance.intercept.MethodInterceptor},
	 * AfterReturningAdvice，ThrowsAdvice。
	 * {@link org.springframework.aop.MethodBeforeAdvice},
	 * {@link org.springframework.aop.AfterReturningAdvice},
	 * {@link org.springframework.aop.ThrowsAdvice}.
	 * @param advice object that should be an advice
	 * @return an Advisor wrapping the given advice. Never returns {@code null}.
	 * If the advice parameter is an Advisor, return it.
	 * @throws UnknownAdviceTypeException if no registered advisor adapter
	 * can wrap the supposed advice
	 */
	Advisor wrap(Object advice) throws UnknownAdviceTypeException;

	/**
	 * Return an array of AOP Alliance MethodInterceptors to allow use of the
	 * 返回一个AOP联盟MethodInterceptors数组，允许在一个基于拦截的框架中使用
	 * given Advisor in an interception-based framework.
	 * 给定的切面。
	 * <p>Don't worry about the pointcut associated with the Advisor,
	 * 不用关心和切面想关联的切点，如果该切面是一个PointcutAdvisor：
	 * if it's a PointcutAdvisor: just return an interceptor.
	 * 仅返回一个拦截器。
	 * @param advisor Advisor to find an interceptor for
	 * @return an array of MethodInterceptors to expose this Advisor's behavior
	 * @throws UnknownAdviceTypeException if the Advisor type is
	 * not understood by any registered AdvisorAdapter.
	 */
	MethodInterceptor[] getInterceptors(Advisor advisor) throws UnknownAdviceTypeException;

	/**
	 * Register the given AdvisorAdapter. Note that it is not necessary to register
	 * adapters for an AOP Alliance Interceptors or Spring Advices: these must be
	 * automatically recognized by an AdvisorAdapterRegistry implementation.
	 * @param adapter AdvisorAdapter that understands a particular Advisor
	 * or Advice types
	 */
	void registerAdvisorAdapter(AdvisorAdapter adapter);

}
