/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.core.io;

/**
 * Extended interface for a resource that is loaded from an enclosing
 * 扩展接口，从封装的上下文加载的资源，比如从ServletContext，但是也可以是类路径
 * 'context', e.g. from a {@link javax.servlet.ServletContext} but also
 * 或者文件系统相对路径（指定的路径没有明确的前缀，因此应用本地ResourceLoader上下文的相对路径）
 * from plain classpath paths or relative file system paths (specified
 * without an explicit prefix, hence applying relative to the local
 * {@link ResourceLoader}'s context).
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.web.context.support.ServletContextResource
 */
public interface ContextResource extends Resource {

	/**
	 * Return the path within the enclosing 'context'.
	 * 返回封装的上下文中的路径
	 * <p>This is typically path relative to a context-specific root directory,
	 * 通常是上下文指定根目录的相对路径。比如ServletContext根路径或者PortletContext根路径
	 * e.g. a ServletContext root or a PortletContext root.
	 */
	String getPathWithinContext();

}
