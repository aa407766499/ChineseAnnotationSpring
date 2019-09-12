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

package org.springframework.context.annotation;

import org.springframework.beans.factory.config.BeanDefinition;

/**
 * Strategy interface for resolving the scope of bean definitions.
 * 解析bean定义作用域的策略接口。
 * @author Mark Fisher
 * @since 2.5
 * @see org.springframework.context.annotation.Scope
 */
@FunctionalInterface
public interface ScopeMetadataResolver {

	/**
	 * Resolve the {@link ScopeMetadata} appropriate to the supplied
	 * 解析bean定义的ScopeMetadata。
	 * bean {@code definition}.
	 * <p>Implementations can of course use any strategy they like to
	 * 当然实现类可以使用任何他们喜欢的策略确定作用域元数据，但是一些spring
	 * determine the scope metadata, but some implementations that spring
	 * 的实现类可以使用类级别上的元注解，或者使用字段级别的元数据。
	 * immediately to mind might be to use source level annotations
	 * present on {@link BeanDefinition#getBeanClassName() the class} of the
	 * supplied {@code definition}, or to use metadata present in the
	 * {@link BeanDefinition#attributeNames()} of the supplied {@code definition}.
	 * @param definition the target bean definition
	 * @return the relevant scope metadata; never {@code null}
	 */
	ScopeMetadata resolveScopeMetadata(BeanDefinition definition);

}
