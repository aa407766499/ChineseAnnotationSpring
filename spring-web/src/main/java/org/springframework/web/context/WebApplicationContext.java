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

package org.springframework.web.context;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;

/**
 * Interface to provide configuration for a web application. This is read-only while
 * 该接口用于对web应用提供配置。应用运行期处于只读状态，但是如果其实现支持可以重写加载
 * the application is running, but may be reloaded if the implementation supports this.
 *
 * <p>This interface adds a {@code getServletContext()} method to the generic
 * 相比于通用的ApplicationContext接口，该接口添加了getServletContext()方法，定义了著名的
 * ApplicationContext interface, and defines a well-known application attribute name
 * 应用属性名称，将顶层上下文绑定到启动过程中。
 * that the root context must be bound to in the bootstrap process.
 *
 * <p>Like generic application contexts, web application contexts are hierarchical.
 * 与通用的应用上下文一样，web应用上下文是层级的。每个应用只有一个顶层上下文，应用中每个servlet
 * There is a single root context per application, while each servlet in the application
 * 有其自己的子上下文（包括MVC框架的dispatcher servlet）。
 * (including a dispatcher servlet in the MVC framework) has its own child context.
 *
 * <p>In addition to standard application context lifecycle capabilities,
 * 除了标准的应用上下文生命周期功能之外，WebApplicationContext需要检测ServletContextAware
 * WebApplicationContext implementations need to detect {@link ServletContextAware}
 * bean 因此需要调用setServletContext方法
 * beans and invoke the {@code setServletContext} method accordingly.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since January 19, 2001
 * @see ServletContextAware#setServletContext
 */
public interface WebApplicationContext extends ApplicationContext {

	/**
	 * Context attribute to bind root WebApplicationContext to on successful startup.
	 * 成功启动后，将上下文属性绑定到顶层WebApplicationContext。
	 * <p>Note: If the startup of the root context fails, this attribute can contain
	 * 注意：如果顶层的上下文启动失败，这个属性会将异常或者错误作为值。使用WebApplicationContextUtils
	 * an exception or error as value. Use WebApplicationContextUtils for convenient
	 * 方便查找顶层WebApplicationContext。
	 * lookup of the root WebApplicationContext.
	 * @see org.springframework.web.context.support.WebApplicationContextUtils#getWebApplicationContext
	 * @see org.springframework.web.context.support.WebApplicationContextUtils#getRequiredWebApplicationContext
	 */
	String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = WebApplicationContext.class.getName() + ".ROOT";

	/**
	 * Scope identifier for request scope: "request".
	 * 请求作用域标识
	 * Supported in addition to the standard scopes "singleton" and "prototype".
	 * 除了标准作用域"singleton"以及"prototype"支持请求作用域。
	 */
	String SCOPE_REQUEST = "request";

	/**
	 * Scope identifier for session scope: "session".
	 * 会话作用域标识
	 * Supported in addition to the standard scopes "singleton" and "prototype".
	 * 除了标准作用域"singleton"以及"prototype"支持会话作用域。
	 */
	String SCOPE_SESSION = "session";

	/**
	 * Scope identifier for the global web application scope: "application".
	 * 全局web应用作用域标识
	 * Supported in addition to the standard scopes "singleton" and "prototype".
	 */
	String SCOPE_APPLICATION = "application";

	/**
	 * Name of the ServletContext environment bean in the factory.
	 * 容器中ServletContext环境bean名称
	 * @see javax.servlet.ServletContext
	 */
	String SERVLET_CONTEXT_BEAN_NAME = "servletContext";

	/**
	 * Name of the ServletContext/PortletContext init-params environment bean in the factory.
	 * 容器中ServletContext/PortletContext初始化参数环境bean名称。
	 * <p>Note: Possibly merged with ServletConfig/PortletConfig parameters.
	 * 注意：可能被覆盖ServletConfig/PortletConfig的参数。
	 * ServletConfig parameters override ServletContext parameters of the same name.
	 * ServletConfig参数会重写同名的ServletContext参数
	 * @see javax.servlet.ServletContext#getInitParameterNames()
	 * @see javax.servlet.ServletContext#getInitParameter(String)
	 * @see javax.servlet.ServletConfig#getInitParameterNames()
	 * @see javax.servlet.ServletConfig#getInitParameter(String)
	 */
	String CONTEXT_PARAMETERS_BEAN_NAME = "contextParameters";

	/**
	 * Name of the ServletContext/PortletContext attributes environment bean in the factory.
	 * 容器中ServletContext/PortletContext 属性环境bean名称
	 * @see javax.servlet.ServletContext#getAttributeNames()
	 * @see javax.servlet.ServletContext#getAttribute(String)
	 */
	String CONTEXT_ATTRIBUTES_BEAN_NAME = "contextAttributes";


	/**
	 * Return the standard Servlet API ServletContext for this application.
	 * 返回该容器标准的ServletAPI ServletContext
	 */
	@Nullable
	ServletContext getServletContext();

}
