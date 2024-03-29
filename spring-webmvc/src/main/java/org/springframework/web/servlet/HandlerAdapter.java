/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.web.servlet;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * MVC framework SPI, allowing parameterization of the core MVC workflow.
 * MVC框架SPI，允许参数化核心MVC工作流。
 * <p>Interface that must be implemented for each handler type to handle a request.
 * 处理请求的每一个处理器类型都要实现该接口。该接口允许DispatcherServlet无限地扩展。
 * This interface is used to allow the {@link DispatcherServlet} to be indefinitely
 * DispatcherServlet通过该接口访问所有安装的处理器，这意味着它不包含任何处理器类型
 * extensible. The {@code DispatcherServlet} accesses all installed handlers through
 * 特定的代码。
 * this interface, meaning that it does not contain code specific to any handler type.
 *
 * <p>Note that a handler can be of type {@code Object}. This is to enable
 * 注意：一个处理器可以是Object类型。这样使得无须自定义代码就可以将其他框架的
 * handlers from other frameworks to be integrated with this framework without
 * 处理器整合到该框架中，也允许注解驱动的处理器对象不用遵循任何特定的Java接口。
 * custom coding, as well as to allow for annotation-driven handler objects that
 * do not obey any specific Java interface.
 *
 * <p>This interface is not intended for application developers. It is available
 * 此接口不适合应用程序开发人员。想要开发自己的web工作流的开发者可以获得该接口。
 * to handlers who want to develop their own web workflow.
 *
 * <p>Note: {@code HandlerAdapter} implementors may implement the {@link
 * org.springframework.core.Ordered} interface to be able to specify a sorting
 * 注意：HandlerAdapter实现类可以实现Ordered接口，这样就可以让DispatcherServlet
 * order (and thus a priority) for getting applied by the {@code DispatcherServlet}.
 * 以指定的顺序（因此有优先权）进行应用。非Ordered实例优先级更低。
 * Non-Ordered instances get treated as lowest priority.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter
 * @see org.springframework.web.servlet.handler.SimpleServletHandlerAdapter
 */
public interface HandlerAdapter {

	/**
	 * Given a handler instance, return whether or not this {@code HandlerAdapter}
	 * 给定一个处理器实例，返回该HandlerAdapter是否支持该处理器。通常HandlerAdapters
	 * can support it. Typical HandlerAdapters will base the decision on the handler
	 * 会基于在处理器类型上的该决定。通常每一个HandlerAdapters仅支持一种处理器类型。
	 * type. HandlerAdapters will usually only support one handler type each.
	 * <p>A typical implementation:
	 * 通常实现：
	 * <p>{@code
	 * return (handler instanceof MyHandler);
	 * }
	 * @param handler handler object to check
	 * @return whether or not this object can use the given handler
	 */
	boolean supports(Object handler);

	/**
	 * Use the given handler to handle this request.
	 * 使用给定的处理器处理该请求。所需的工作流可能差异很大。
	 * The workflow that is required may vary widely.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler handler to use. This object must have previously been passed
	 * to the {@code supports} method of this interface, which must have
	 * returned {@code true}.
	 * @throws Exception in case of errors
	 * @return ModelAndView object with the name of the view and the required
	 * model data, or {@code null} if the request has been handled directly
	 */
	@Nullable
	ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

	/**
	 * Same contract as for HttpServlet's {@code getLastModified} method.
	 * 至于HttpServlet的getLastModified方法有相同的协议。如果不支持该
	 * Can simply return -1 if there's no support in the handler class.
	 * 处理器类，可以简单返回-1.
	 * @param request current HTTP request
	 * @param handler handler to use
	 * @return the lastModified value for the given handler
	 * @see javax.servlet.http.HttpServlet#getLastModified
	 * @see org.springframework.web.servlet.mvc.LastModified#getLastModified
	 */
	long getLastModified(HttpServletRequest request, Object handler);

}
