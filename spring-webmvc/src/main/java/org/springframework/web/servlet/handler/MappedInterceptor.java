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

package org.springframework.web.servlet.handler;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Contains and delegates calls to a {@link HandlerInterceptor} along with
 * 包含以及委派调用给该处理器拦截器，该处理器拦截器包含（以及可能排除）拦截器要使用的路径模式、
 * include (and optionally exclude) path patterns to which the interceptor should apply.
 * 如果拦截器应用于给定的请求路径，也提供匹配逻辑用于测试。
 * Also provides matching logic to test if the interceptor applies to a given request path.
 *
 * <p>A MappedInterceptor can be registered directly with any
 * 一个MappedInterceptor能直接和任何的AbstractHandlerMethodMapping一起注册。
 * {@link org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
 * 此外，MappedInterceptor类型的bean会被AbstractHandlerMethodMapping（包括父级ApplicationContext）
 * AbstractHandlerMethodMapping}. Furthermore, beans of type MappedInterceptor
 * 自动检测，这实际上意味着拦截器对于所有的处理器映射是全局的。
 * are automatically detected by {@code AbstractHandlerMethodMapping} (including
 * ancestor ApplicationContext's) which effectively means the interceptor is
 * registered "globally" with all handler mappings.
 *
 * @author Keith Donald
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @since 3.0
 */
public final class MappedInterceptor implements HandlerInterceptor {

	@Nullable
	private final String[] includePatterns;

	@Nullable
	private final String[] excludePatterns;

	private final HandlerInterceptor interceptor;

	@Nullable
	private PathMatcher pathMatcher;


	/**
	 * Create a new MappedInterceptor instance.
	 * @param includePatterns the path patterns to map with a {@code null} value matching to all paths
	 * @param interceptor the HandlerInterceptor instance to map to the given patterns
	 */
	public MappedInterceptor(@Nullable String[] includePatterns, HandlerInterceptor interceptor) {
		this(includePatterns, null, interceptor);
	}

	/**
	 * Create a new MappedInterceptor instance.
	 * @param includePatterns the path patterns to map with a {@code null} value matching to all paths
	 * @param excludePatterns the path patterns to exclude
	 * @param interceptor the HandlerInterceptor instance to map to the given patterns
	 */
	public MappedInterceptor(@Nullable String[] includePatterns, @Nullable String[] excludePatterns,
			HandlerInterceptor interceptor) {

		this.includePatterns = includePatterns;
		this.excludePatterns = excludePatterns;
		this.interceptor = interceptor;
	}


	/**
	 * Create a new MappedInterceptor instance.
	 * @param includePatterns the path patterns to map with a {@code null} value matching to all paths
	 * @param interceptor the WebRequestInterceptor instance to map to the given patterns
	 */
	public MappedInterceptor(@Nullable String[] includePatterns, WebRequestInterceptor interceptor) {
		this(includePatterns, null, interceptor);
	}

	/**
	 * Create a new MappedInterceptor instance.
	 * @param includePatterns the path patterns to map with a {@code null} value matching to all paths
	 * @param interceptor the WebRequestInterceptor instance to map to the given patterns
	 */
	public MappedInterceptor(@Nullable String[] includePatterns, @Nullable String[] excludePatterns,
			WebRequestInterceptor interceptor) {

		this(includePatterns, excludePatterns, new WebRequestHandlerInterceptorAdapter(interceptor));
	}


	/**
	 * Configure a PathMatcher to use with this MappedInterceptor instead of the
	 * one passed by default to the {@link #matches(String, org.springframework.util.PathMatcher)}
	 * method. This is an advanced property that is only required when using custom
	 * PathMatcher implementations that support mapping metadata other than the
	 * Ant-style path patterns supported by default.
	 */
	public void setPathMatcher(@Nullable PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

	/**
	 * The configured PathMatcher, or {@code null} if none.
	 */
	@Nullable
	public PathMatcher getPathMatcher() {
		return this.pathMatcher;
	}

	/**
	 * The path into the application the interceptor is mapped to.
	 */
	@Nullable
	public String[] getPathPatterns() {
		return this.includePatterns;
	}

	/**
	 * The actual Interceptor reference.
	 * 实际的拦截器引用。
	 */
	public HandlerInterceptor getInterceptor() {
		return this.interceptor;
	}


	/**
	 * Returns {@code true} if the interceptor applies to the given request path.
	 * 如果拦截器能应用到给定的路径，返回true。
	 * @param lookupPath the current request path
	 * @param pathMatcher a path matcher for path pattern matching
	 */
	public boolean matches(String lookupPath, PathMatcher pathMatcher) {
		PathMatcher pathMatcherToUse = (this.pathMatcher != null) ? this.pathMatcher : pathMatcher;
		if (this.excludePatterns != null) {
			for (String pattern : this.excludePatterns) {
				if (pathMatcherToUse.match(pattern, lookupPath)) {
					return false;
				}
			}
		}
		if (ObjectUtils.isEmpty(this.includePatterns)) {
			return true;
		}
		else {
			for (String pattern : this.includePatterns) {
				if (pathMatcherToUse.match(pattern, lookupPath)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		return this.interceptor.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable ModelAndView modelAndView) throws Exception {

		this.interceptor.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable Exception ex) throws Exception {

		this.interceptor.afterCompletion(request, response, handler, ex);
	}

}
