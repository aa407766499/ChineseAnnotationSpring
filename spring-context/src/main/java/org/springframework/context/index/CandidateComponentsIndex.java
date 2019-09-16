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

package org.springframework.context.index;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Provide access to the candidates that are defined in {@code META-INF/spring.components}.
 * 提供访问META-INF/spring.components定义的合格者。
 * <p>An arbitrary number of stereotypes can be registered (and queried) on the index: a
 * 任意数量的类型能用索引注册（查找）：一个典型的例子是一个标识确定用途类的注解的全限定类名。
 * typical example is the fully qualified name of an annotation that flags the class for
 * 下面调用返回基础包及其子包下的所有的@Component匹配类型
 * a certain use case. The following call returns all the {@code @Component}
 * <b>candidate</b> types for the {@code com.example} package (and its sub-packages):
 * <pre class="code">
 * Set&lt;String&gt; candidates = index.getCandidateTypes(
 *         "com.example", "org.springframework.stereotype.Component");
 * </pre>
 *
 * <p>The {@code type} is usually the fully qualified name of a class, though this is
 * 虽然不是一个规则，通常type是一个类的全限定名称。同样地，stereotype通常是一个目标类型的
 * not a rule. Similarly, the {@code stereotype} is usually the fully qualified name of
 * 全限定名称但是它确实可以是任何标记。
 * a target type but it can be any marker really.
 *
 * @author Stephane Nicoll
 * @since 5.0
 */
public class CandidateComponentsIndex {

	private final static AntPathMatcher pathMatcher = new AntPathMatcher(".");

	private final MultiValueMap<String, Entry> index;


	CandidateComponentsIndex(List<Properties> content) {
		this.index = parseIndex(content);
	}


	/**
	 * Return the candidate types that are associated with the specified stereotype.
	 * 返回指定stereotype相关的匹配类型。
	 * @param basePackage the package to check for candidates
	 * @param stereotype the stereotype to use
	 * @return the candidate types associated with the specified {@code stereotype}
	 * or an empty set if none has been found for the specified {@code basePackage}
	 */
	public Set<String> getCandidateTypes(String basePackage, String stereotype) {
		//先查找所有匹配的类型，然后看匹配的类型是不是指定包下的。
		List<Entry> candidates = this.index.get(stereotype);
		if (candidates != null) {
			return candidates.parallelStream()
					.filter(t -> t.match(basePackage))
					.map(t -> t.type)
					.collect(Collectors.toSet());
		}
		return Collections.emptySet();
	}

	private static MultiValueMap<String, Entry> parseIndex(List<Properties> content) {
		MultiValueMap<String, Entry> index = new LinkedMultiValueMap<>();
		for (Properties entry : content) {
			entry.forEach((type, values) -> {
				String[] stereotypes = ((String) values).split(",");
				for (String stereotype : stereotypes) {
					index.add(stereotype, new Entry((String) type));
				}
			});
		}
		return index;
	}

	private static class Entry {
		private final String type;
		private final String packageName;

		Entry(String type) {
			this.type = type;
			this.packageName = ClassUtils.getPackageName(type);
		}

		public boolean match(String basePackage) {
			if (pathMatcher.isPattern(basePackage)) {
				return pathMatcher.match(basePackage, this.packageName);
			}
			else {
				return this.type.startsWith(basePackage);
			}
		}

	}

}
