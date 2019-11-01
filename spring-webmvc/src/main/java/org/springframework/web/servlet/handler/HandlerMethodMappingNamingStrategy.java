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

package org.springframework.web.servlet.handler;

import org.springframework.web.method.HandlerMethod;

/**
 * A strategy for assigning a name to a handler method's mapping.
 * 给一个处理器方法的映射分配一个名称的策略。
 * <p>The strategy can be configured on
 * 该策略在AbstractHandlerMethodMapping上配置。分配一个名称给每一个注册的处理器方法
 * {@link org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
 * 的映射。该名称通过AbstractHandlerMethodMapping的getHandlerMethodsForMappingName()
 * AbstractHandlerMethodMapping}. It is used to assign a name to the mapping of
 * 方法使用。
 * every registered handler method. The names can then be queried via
 * {@link org.springframework.web.servlet.handler.AbstractHandlerMethodMapping#getHandlerMethodsForMappingName(String)
 * AbstractHandlerMethodMapping#getHandlerMethodsForMappingName}.
 *
 * <p>Applications can build a URL to a controller method by name with the help
 * 应用能使用MvcUriComponentsBuilder的fromMappingName()方法通过名称建立URL到controller
 * of the static method
 * 或者在JSP中通过Spring标签库注册的"mvcUrl"功能。
 * {@link org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder#fromMappingName(String)
 * MvcUriComponentsBuilder#fromMappingName} or in JSPs through the "mvcUrl"
 * function registered by the Spring tag library.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 */
@FunctionalInterface
public interface HandlerMethodMappingNamingStrategy<T> {

	/**
	 * Determine the name for the given HandlerMethod and mapping.
	 * 确定给定处理器方法和映射的名称。
	 * @param handlerMethod the handler method
	 * @param mapping the mapping
	 * @return the name
	 */
	String getName(HandlerMethod handlerMethod, T mapping);

}
