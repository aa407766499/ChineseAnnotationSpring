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

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link org.springframework.web.servlet.HandlerMapping}
 * HandlerMapping接口的实现类，该类将URL映射到以斜线("/")开头的bean名称，和Struts
 * interface that map from URLs to beans with names that start with a slash ("/"),
 * 将URL映射到action名称相似。
 * similar to how Struts maps URLs to action names.
 *
 * <p>This is the default implementation used by the
 * 这是DispatcherServlet使用的默认实现类，可以选择使用
 * {@link org.springframework.web.servlet.DispatcherServlet}, along with
 * RequestMappingHandlerMapping，SimpleUrlHandlerMapping允许以声明的方式自定义
 * {@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping}.
 * 一个处理器映射。
 * Alternatively, {@link SimpleUrlHandlerMapping} allows for customizing a
 * handler mapping declaratively.
 *
 * <p>The mapping is from URL to bean name. Thus an incoming URL "/foo" would map
 * 该映射是URL和bean名称之间的映射。因此输入URL"/foo"可以映射到一个命名为"/foo"的
 * to a handler named "/foo", or to "/foo /foo2" in case of multiple mappings to
 * 处理器，如果对于单个处理器有多个映射，也可以传"/foo /foo2"。注意：在XML定义中，
 * a single handler. Note: In XML definitions, you'll need to use an alias
 * 你需要在bean定义中使用一个别名"/foo"，但是XML id不能包含斜线。
 * name="/foo" in the bean definition, as the XML id may not contain slashes.
 *
 * <p>Supports direct matches (given "/test" -> registered "/test") and "*"
 * 支持直接匹配（给定的"/test"->注册的"/test"）也支持"*"匹配（给定的"/test"->注册的"/t*"）。
 * matches (given "/test" -> registered "/t*"). Note that the default is
 * 注意：默认是当前servlet映射中的map，如果可以应用的话；查看setAlwaysUseFullPath
 * to map within the current servlet mapping if applicable; see the
 * 属性获取更多细节。模式选择的更多细节，参考AntPathMatcher javadoc。
 * {@link #setAlwaysUseFullPath "alwaysUseFullPath"} property for details.
 * For details on the pattern options, see the
 * {@link org.springframework.util.AntPathMatcher} javadoc.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see SimpleUrlHandlerMapping
 */
public class BeanNameUrlHandlerMapping extends AbstractDetectingUrlHandlerMapping {

	/**
	 * Checks name and aliases of the given bean for URLs, starting with "/".
	 */
	@Override
	protected String[] determineUrlsForHandler(String beanName) {
		List<String> urls = new ArrayList<>();
		if (beanName.startsWith("/")) {
			urls.add(beanName);
		}
		String[] aliases = obtainApplicationContext().getAliases(beanName);
		for (String alias : aliases) {
			if (alias.startsWith("/")) {
				urls.add(alias);
			}
		}
		return StringUtils.toStringArray(urls);
	}

}
