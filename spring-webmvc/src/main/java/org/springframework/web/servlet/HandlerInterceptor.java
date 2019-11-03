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

package org.springframework.web.servlet;

import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Workflow interface that allows for customized handler execution chains.
 * 工作流接口，该接口允许自定义处理器执行链。应用能将任意数量的已存在的或者自定义的
 * Applications can register any number of existing or custom interceptors
 * 拦截器注册到确定的处理器组中，在不需要修改每一个处理器实现的情况下，添加公共的
 * for certain groups of handlers, to add common preprocessing behavior
 * 预处理行为。
 * without needing to modify each handler implementation.
 *
 * <p>A HandlerInterceptor gets called before the appropriate HandlerAdapter
 * 在合适的HandlerAdapter触发执行处理器之前调用HandlerInterceptor。该机制能用于
 * triggers the execution of the handler itself. This mechanism can be used
 * 一长段的预处理切面，比如：权限校验，或者公共的处理器行为比如本地化或者主题改变。
 * for a large field of preprocessing aspects, e.g. for authorization checks,
 * 其主要目的是提取重复的处理器代码。
 * or common handler behavior like locale or theme changes. Its main purpose
 * is to allow for factoring out repetitive handler code.
 *
 * <p>In an asynchronous processing scenario, the handler may be executed in a
 * 在异步处理场景中，在主线程存在并且没有调用postHandle以及afterCompletion回调时
 * separate thread while the main thread exits without rendering or invoking the
 * 处理器可以在分开的线程中执行。在并发处理器执行完成时，为了执行发送模型转发回请求
 * {@code postHandle} and {@code afterCompletion} callbacks. When concurrent
 * 该协议中的所有方法被再次调用。获取更多细节参考AsyncHandlerInterceptor
 * handler execution completes, the request is dispatched back in order to
 * proceed with rendering the model and all methods of this contract are invoked
 * again. For further options and details see
 * {@code org.springframework.web.servlet.AsyncHandlerInterceptor}
 *
 * <p>Typically an interceptor chain is defined per HandlerMapping bean,
 * 通常每一个HandlerMapping bean都定义了一条拦截器链，共享其粒度。能够应用处理器
 * sharing its granularity. To be able to apply a certain interceptor chain
 * 组中的确定的拦截器链，需要通过一个HandlerMapping bean映射需要的处理器。
 * to a group of handlers, one needs to map the desired handlers via one
 * 拦截器自身被定义为应用上下文中的bean，被映射的bean定义的拦截器属性引用。
 * HandlerMapping bean. The interceptors themselves are defined as beans
 * in the application context, referenced by the mapping bean definition
 * via its "interceptors" property (in XML: a &lt;list&gt; of &lt;ref&gt;).
 *
 * <p>HandlerInterceptor is basically similar to a Servlet Filter, but in
 * HandlerInterceptor与Servlet Filter基本相似，但是与后者相反，前者仅允许自定义
 * contrast to the latter it just allows custom pre-processing with the option
 * 预处理，可以阻止执行处理器，而且自定义后置处理。Filter更加强大，比如他们允许修改
 * of prohibiting the execution of the handler itself, and custom post-processing.
 * 传递的请求对象和响应对象。注意过滤器是在web.xml中配置，HandlerInterceptor在应用上下
 * Filters are more powerful, for example they allow for exchanging the request
 * 文中配置。
 * and response objects that are handed down the chain. Note that a filter
 * gets configured in web.xml, a HandlerInterceptor in the application context.
 *
 * <p>As a basic guideline, fine-grained handler-related preprocessing tasks are
 * 作为基本准则，细粒度处理器相关的预处理任务是HandlerInterceptor实现类的候选对象，
 * candidates for HandlerInterceptor implementations, especially factored-out
 * 特别是提取公共的处理器代码以及权限校验。在其他方法，过滤器非常适合请求内容和
 * common handler code and authorization checks. On the other hand, a Filter
 * 视图内容处理，比如多文件表单和GZIP压缩。这通常出现在需要将过滤器映射到确定的
 * is well-suited for request content and view content handling, like multipart
 * 内容类型（比如图片），或者所有请求，
 * forms and GZIP compression. This typically shows when one needs to map the
 * filter to certain content types (e.g. images), or to all requests.
 *
 * @author Juergen Hoeller
 * @since 20.06.2003
 * @see HandlerExecutionChain#getInterceptors
 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter
 * @see org.springframework.web.servlet.handler.AbstractHandlerMapping#setInterceptors
 * @see org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor
 * @see org.springframework.web.servlet.i18n.LocaleChangeInterceptor
 * @see org.springframework.web.servlet.theme.ThemeChangeInterceptor
 * @see javax.servlet.Filter
 */
public interface HandlerInterceptor {

	/**
	 * Intercept the execution of a handler. Called after HandlerMapping determined
	 * 处理器执行的拦截。HandlerMapping确定一个合适的处理器对象之后，但是在
	 * an appropriate handler object, but before HandlerAdapter invokes the handler.
	 * HandlerAdapter调用处理器对象之前调用。
	 * <p>DispatcherServlet processes a handler in an execution chain, consisting
	 * DispatcherServlet处理一个执行链中的处理器，该链由任意数量的拦截器和末尾的处理器
	 * of any number of interceptors, with the handler itself at the end.
	 * 组成。调用该方法，每一个拦截器能决定阻止执行链，通常发送一个HTTP错误或者
	 * With this method, each interceptor can decide to abort the execution chain,
	 * 写一个自定义响应。
	 * typically sending a HTTP error or writing a custom response.
	 * <p><strong>Note:</strong> special considerations apply for asynchronous
	 * 注意：对于异步请求处理的特别考虑，获取更多细节参考AsyncHandlerInterceptor。
	 * request processing. For more details see
	 * {@link org.springframework.web.servlet.AsyncHandlerInterceptor}.
	 * <p>The default implementation returns {@code true}.
	 * 默认实现返回true。
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler chosen handler to execute, for type and/or instance evaluation
	 * @return {@code true} if the execution chain should proceed with the
	 * next interceptor or the handler itself. Else, DispatcherServlet assumes
	 * that this interceptor has already dealt with the response itself.
	 * @throws Exception in case of errors
	 */
	default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		return true;
	}

	/**
	 * Intercept the execution of a handler. Called after HandlerAdapter actually
	 * 拦截处理器执行。在HandlerAdapter实际调用处理器之后，DispatcherServlet发送
	 * invoked the handler, but before the DispatcherServlet renders the view.
	 * 视图之前调用。通过给定的ModelAndView可以暴露另外的模型对象给视图。
	 * Can expose additional model objects to the view via the given ModelAndView.
	 * <p>DispatcherServlet processes a handler in an execution chain, consisting
	 * DispatcherServlet处理一个执行链中的处理器，该链由任意数量的拦截器和末尾的
	 * of any number of interceptors, with the handler itself at the end.
	 * 处理器组成。使用该方法，每一个拦截器能后置处理一个执行，以执行链
	 * With this method, each interceptor can post-process an execution,
	 * 相反的顺序调用。
	 * getting applied in inverse order of the execution chain.
	 * <p><strong>Note:</strong> special considerations apply for asynchronous
	 * request processing. For more details see
	 * {@link org.springframework.web.servlet.AsyncHandlerInterceptor}.
	 * <p>The default implementation is empty.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler handler (or {@link HandlerMethod}) that started asynchronous
	 * execution, for type and/or instance examination
	 * @param modelAndView the {@code ModelAndView} that the handler returned
	 * (can also be {@code null})
	 * @throws Exception in case of errors
	 */
	default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable ModelAndView modelAndView) throws Exception {
	}

	/**
	 * Callback after completion of request processing, that is, after rendering
	 * 请求处理完成之后的回调，换句话说，在发送视图之后。将在处理器执行的任何结果
	 * the view. Will be called on any outcome of handler execution, thus allows
	 * 上调用，因此允许释放属性资源。
	 * for proper resource cleanup.
	 * <p>Note: Will only be called if this interceptor's {@code preHandle}
	 * 注意：只有在该拦截器preHandle方法成功完成并且返回 true时才调用该方法。
	 * method has successfully completed and returned {@code true}!
	 * <p>As with the {@code postHandle} method, the method will be invoked on each
	 * 和postHandle方法一样，以相反的顺序调用链中拦截器的该方法，因此第一个拦截器最后
	 * interceptor in the chain in reverse order, so the first interceptor will be
	 * 被调用。
	 * the last to be invoked.
	 * <p><strong>Note:</strong> special considerations apply for asynchronous
	 * request processing. For more details see
	 * {@link org.springframework.web.servlet.AsyncHandlerInterceptor}.
	 * <p>The default implementation is empty.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler handler (or {@link HandlerMethod}) that started asynchronous
	 * execution, for type and/or instance examination
	 * @param ex exception thrown on handler execution, if any
	 * @throws Exception in case of errors
	 */
	default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable Exception ex) throws Exception {
	}

}
