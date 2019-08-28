/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.web.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.Nullable;

/**
 * Interface to be implemented by configurable web application contexts.
 * 该接口用于配置web应用上下文。
 * Supported by {@link ContextLoader} and
 * 由ContextLoader和FrameworkServlet支持。
 * {@link org.springframework.web.servlet.FrameworkServlet}.
 *
 * <p>Note: The setters of this interface need to be called before an
 * 注意：该接口的setters方法需要在从ConfigurableApplicationContext
 * invocation of the {@link #refresh} method inherited from
 * 继承来的refresh方法调用之前调用
 * {@link org.springframework.context.ConfigurableApplicationContext}.
 * They do not cause an initialization of the context on their own.
 * 这些setters没有导致上下文初始化。
 *
 * @author Juergen Hoeller
 * @since 05.12.2003
 * @see #refresh
 * @see ContextLoader#createWebApplicationContext
 * @see org.springframework.web.servlet.FrameworkServlet#createWebApplicationContext
 */
public interface ConfigurableWebApplicationContext extends WebApplicationContext, ConfigurableApplicationContext {

	/**
	 * Prefix for ApplicationContext ids that refer to context path and/or servlet name.
	 * 参考上下文路径或者servlet名称，ApplicationContext id前缀
	 */
	String APPLICATION_CONTEXT_ID_PREFIX = WebApplicationContext.class.getName() + ":";

	/**
	 * Name of the ServletConfig environment bean in the factory.
	 * 容器中ServletConfig环境bean名称
	 * @see javax.servlet.ServletConfig
	 */
	String SERVLET_CONFIG_BEAN_NAME = "servletConfig";


	/**
	 * Set the ServletContext for this web application context.
	 * 给该web应用上下文设置ServletContext
	 * <p>Does not cause an initialization of the context: refresh needs to be
	 * 不会造成该上下文的初始化，所有配置属性配置之后需要调用refresh方法。
	 * called after the setting of all configuration properties.
	 * @see #refresh()
	 */
	void setServletContext(@Nullable ServletContext servletContext);

	/**
	 * Set the ServletConfig for this web application context.
	 * 给该web应用上下文设置ServletConfig
	 * Only called for a WebApplicationContext that belongs to a specific Servlet.
	 * 该方法仅会被属于特定Servlet的WebApplicationContext调用。
	 * @see #refresh()
	 */
	void setServletConfig(@Nullable ServletConfig servletConfig);

	/**
	 * Return the ServletConfig for this web application context, if any.
	 * 如果有的话，返回web应用上下文的ServletConfig
	 */
	@Nullable
	ServletConfig getServletConfig();

	/**
	 * Set the namespace for this web application context,
	 * 给该web应用上下文设置命名空间，该命名空间被用于构建默认的
	 * to be used for building a default context config location.
	 * 上下文配置路径。
	 * The root web application context does not have a namespace.
	 * 顶层web应用上下文没有命名空间。
	 */
	void setNamespace(@Nullable String namespace);

	/**
	 * Return the namespace for this web application context, if any.
	 * 如果有的话，返回该web应用上下文的命名空间
	 */
	@Nullable
	String getNamespace();

	/**
	 * Set the config locations for this web application context in init-param style,
	 * 使用初始化参数方式设置该web应用上下文的配置路径，该路径由逗号，分号或者空格分隔。
	 * i.e. with distinct locations separated by commas, semicolons or whitespace.
	 * <p>If not set, the implementation is supposed to use a default for the
	 * 如果没有设置，该实现应该使用给定的默认命名空间或者视情况而定，使用顶层web
	 * given namespace or the root web application context, as appropriate.
	 * 应用上下文
	 */
	void setConfigLocation(String configLocation);

	/**
	 * Set the config locations for this web application context.
	 * <p>If not set, the implementation is supposed to use a default for the
	 * given namespace or the root web application context, as appropriate.
	 */
	void setConfigLocations(String... configLocations);

	/**
	 * Return the config locations for this web application context,
	 * 返回该web应用上下文的配置路径,没有指定则为null。
	 * or {@code null} if none specified.
	 */
	@Nullable
	String[] getConfigLocations();

}
