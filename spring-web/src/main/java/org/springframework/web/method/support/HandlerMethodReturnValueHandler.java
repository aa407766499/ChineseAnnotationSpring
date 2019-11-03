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

package org.springframework.web.method.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Strategy interface to handle the value returned from the invocation of a
 * 策略接口，该接口处理调用处理器方法的返回值。
 * handler method .
 *
 * @author Arjen Poutsma
 * @since 3.1
 * @see HandlerMethodArgumentResolver
 */
public interface HandlerMethodReturnValueHandler {

	/**
	 * Whether the given {@linkplain MethodParameter method return type} is
	 * 该处理器是否支持给定MethodParameter方法返回类型。
	 * supported by this handler.
	 * @param returnType the method return type to check
	 * @return {@code true} if this handler supports the supplied return type;
	 * {@code false} otherwise
	 */
	boolean supportsReturnType(MethodParameter returnType);

	/**
	 * Handle the given return value by adding attributes to the model and
	 * 通过添加属性到模型以及设置视图或者ModelAndViewContainer的setRequestHandled
	 * setting a view or setting the
	 * 方法设置标识true标识响应已经被直接处理，处理给定的返回类型。
	 * {@link ModelAndViewContainer#setRequestHandled} flag to {@code true}
	 * to indicate the response has been handled directly.
	 * @param returnValue the value returned from the handler method
	 * @param returnType the type of the return value. This type must have
	 * previously been passed to {@link #supportsReturnType} which must
	 * have returned {@code true}.
	 * @param mavContainer the ModelAndViewContainer for the current request
	 * @param webRequest the current request
	 * @throws Exception if the return value handling results in an error
	 */
	void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception;

}
