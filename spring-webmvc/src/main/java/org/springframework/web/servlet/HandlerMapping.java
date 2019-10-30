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

package org.springframework.web.servlet;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface to be implemented by objects that define a mapping between
 * 实现该接口的对象在请求和处理器对象之间定义了一种映射
 * requests and handler objects.
 *
 * <p>This class can be implemented by application developers, although this is not
 * 应用开发者可以实现该类，虽然并不需要这么做，框架内包含了BeanNameUrlHandlerMapping和
 * necessary, as {@link org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping}
 * RequestMappingHandlerMapping。如果在应用上下文中没有注册HandlerMapping，那么前者将作为默认值。
 * and {@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping}
 * are included in the framework. The former is the default if no
 * HandlerMapping bean is registered in the application context.
 *
 * <p>HandlerMapping implementations can support mapped interceptors but do not
 * HandlerMapping实现类可以支持映射拦截器但是并不必须这么做。一个处理器将总是被包装进
 * have to. A handler will always be wrapped in a {@link HandlerExecutionChain}
 * 一个HandlerExecutionChain实例中，可以选择将一些HandlerInterceptor实例一起包装。
 * instance, optionally accompanied by some {@link HandlerInterceptor} instances.
 * DispatcherServlet会首先以给定的顺序调用每一个HandlerInterceptor的preHandle方法，
 * The DispatcherServlet will first call each HandlerInterceptor's
 * 如果所有的preHandle方法都返回true，那么最后会调用handler。
 * {@code preHandle} method in the given order, finally invoking the handler
 * itself if all {@code preHandle} methods have returned {@code true}.
 *
 * <p>The ability to parameterize this mapping is a powerful and unusual
 * 将映射参数化是该MVC框架的有力而且独特的能力。比如，可能基于session状态，cookie
 * capability of this MVC framework. For example, it is possible to write
 * 状态或者许多其他变量写一个自定义的映射。其他的MVC框架都没有这么灵活。
 * a custom mapping based on session state, cookie state or many other
 * variables. No other MVC framework seems to be equally flexible.
 *
 * <p>Note: Implementations can implement the {@link org.springframework.core.Ordered}
 * 注意：实现类可以实现Ordered接口，这样可以指定一个排序顺序，因此DispatcherServlet
 * interface to be able to specify a sorting order and thus a priority for getting
 * 可以优先应用。没有实现Ordered接口的实例优先级更低。
 * applied by DispatcherServlet. Non-Ordered instances get treated as lowest priority.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see org.springframework.core.Ordered
 * @see org.springframework.web.servlet.handler.AbstractHandlerMapping
 * @see org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
 */
public interface HandlerMapping {

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains the path
	 * 包含处理器映射中路径的HttpServletRequest属性名称，如果有一个模式匹配，
	 * within the handler mapping, in case of a pattern match, or the full
	 * 或者全部的相关URI（通常在DispatcherServlet的映射中）。
	 * relevant URI (typically within the DispatcherServlet's mapping) else.
	 * <p>Note: This attribute is not required to be supported by all
	 * 注意：该属性不需要被所有的HandlerMapping实现支持。基于URL的HandlerMapping
	 * HandlerMapping implementations. URL-based HandlerMappings will
	 * 通常会支持该属性，但是处理器并不需要在所有的场景中都有该请求属性。
	 * typically support it, but handlers should not necessarily expect
	 * this request attribute to be present in all scenarios.
	 */
	String PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE = HandlerMapping.class.getName() + ".pathWithinHandlerMapping";

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains the
	 * 包含处理器映射中最匹配模式的HttpServletRequest属性的名称。
	 * best matching pattern within the handler mapping.
	 * <p>Note: This attribute is not required to be supported by all
	 * 注意：该属性不需要被所有的HandlerMapping实现支持。基于URL的HandlerMapping
	 * HandlerMapping implementations. URL-based HandlerMappings will
	 * 通常会支持该属性，但是处理器并不需要在所有的场景中都有该请求属性。
	 * typically support it, but handlers should not necessarily expect
	 * this request attribute to be present in all scenarios.
	 */
	String BEST_MATCHING_PATTERN_ATTRIBUTE = HandlerMapping.class.getName() + ".bestMatchingPattern";

	/**
	 * Name of the boolean {@link HttpServletRequest} attribute that indicates
	 * 表示是否检查类级别映射的boolean HttpServletRequest属性的名称
	 * whether type-level mappings should be inspected.
	 *
	 * <p>Note: This attribute is not required to be supported by all
	 * 注意：该属性不需要被所有的HandlerMapping实现支持。
	 * HandlerMapping implementations.
	 */
	String INTROSPECT_TYPE_LEVEL_MAPPING = HandlerMapping.class.getName() + ".introspectTypeLevelMapping";

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains the URI
	 * 包含URI模板映射，将变量名称映射给值的HttpServletRequest属性名称。
	 * templates map, mapping variable names to values.
	 * <p>Note: This attribute is not required to be supported by all
	 * 注意：该属性不需要被所有的HandlerMapping实现支持。基于URL的HandlerMapping
	 * HandlerMapping implementations. URL-based HandlerMappings will
	 * 通常会支持该属性，但是处理器并不需要在所有的场景中都有该请求属性。
	 * typically support it, but handlers should not necessarily expect
	 * this request attribute to be present in all scenarios.
	 */
	String URI_TEMPLATE_VARIABLES_ATTRIBUTE = HandlerMapping.class.getName() + ".uriTemplateVariables";

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains a map with
	 * 包含URI变量名称和URI矩阵变量对应的MultiValueMap映射的HttpServletRequest
	 * URI variable names and a corresponding MultiValueMap of URI matrix
	 * 属性名称。
	 * variables for each.
	 * <p>Note: This attribute is not required to be supported by all
	 * 注意：该属性不需要被所有的HandlerMapping实现支持并且不一定存在，这取决于
	 * HandlerMapping implementations and may also not be present depending on
	 * HandlerMapping是否被配置持有矩阵变量内容。
	 * whether the HandlerMapping is configured to keep matrix variable content
	 */
	String MATRIX_VARIABLES_ATTRIBUTE = HandlerMapping.class.getName() + ".matrixVariables";

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains the set of
	 * 包含应用到映射的处理器的生产媒体类型的集合的HttpServletRequest属性名称。
	 * producible MediaTypes applicable to the mapped handler.
	 * <p>Note: This attribute is not required to be supported by all
	 * 注意：该属性不需要被所有的HandlerMapping实现支持。处理器并不需要在所有
	 * HandlerMapping implementations. Handlers should not necessarily expect
	 * 的场景中都有该请求属性。
	 * this request attribute to be present in all scenarios.
	 */
	String PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE = HandlerMapping.class.getName() + ".producibleMediaTypes";

	/**
	 * Return a handler and any interceptors for this request. The choice may be made
	 * 返回该请求的处理器和任何拦截器。可以通过请求URL，session状态，或者任何实现类选择
	 * on request URL, session state, or any factor the implementing class chooses.
	 * 的因素进行该操作。
	 * <p>The returned HandlerExecutionChain contains a handler Object, rather than
	 * 返回的HandlerExecutionChain包含一个处理器对象，而不是标记接口，因此处理器
	 * even a tag interface, so that handlers are not constrained in any way.
	 * 不受任何约束。比如，可以写一个处理器适配器来允许使用其他框架的处理器对象。
	 * For example, a HandlerAdapter could be written to allow another framework's
	 * handler objects to be used.
	 * <p>Returns {@code null} if no match was found. This is not an error.
	 * 如果没有匹配的，返回null。这不是一个错误。DispatcherServlet会查询所有注册的
	 * The DispatcherServlet will query all registered HandlerMapping beans to find
	 * HandlerMapping bean来查找一个匹配，如果没有查找到一个处理器，那么仅能确定有
	 * a match, and only decide there is an error if none can find a handler.
	 * 一个错误。
	 * @param request current HTTP request
	 * @return a HandlerExecutionChain instance containing handler object and
	 * any interceptors, or {@code null} if no mapping found
	 * @throws Exception if there is an internal error
	 */
	@Nullable
	HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;

}
