/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.core;

/**
 * Extension of the {@link Ordered} interface, expressing a <em>priority</em>
 * Ordered的扩展接口，表达优先排序的意思。PriorityOrdered对象表达的顺序值
 * ordering: order values expressed by {@code PriorityOrdered} objects
 * 总是在Ordered对象表达的相同顺序值之前。
 * always apply before same order values expressed by <em>plain</em>
 * {@link Ordered} objects.
 *
 * <p>This is primarily a special-purpose interface, used for objects where
 * 这是一个主要用于特殊用途的接口，在多个对象中识别出优先的对象，即使没有获得剩余对象。
 * it is particularly important to recognize <em>prioritized</em> objects
 * 典型的例子：在Spring的ApplicationContext中的优先的后置处理器
 * first, without even obtaining the remaining objects. A typical example:
 * prioritized post-processors in a Spring
 * {@link org.springframework.context.ApplicationContext}.
 *
 * <p>Note: {@code PriorityOrdered} post-processor beans are initialized in
 * 注意：PriorityOrdered后置处理器bean是在特定阶段初始化，在其他后置处理器bean之前。
 * a special phase, ahead of other post-processor beans. This subtly
 * 这影响了他们的自动注入行为：他们会被自动注入到那些不需要为了类型匹配而饿汉式初始化的bean中。
 * affects their autowiring behavior: they will only be autowired against
 * beans which do not require eager initialization for type matching.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.beans.factory.config.PropertyOverrideConfigurer
 * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
 */
public interface PriorityOrdered extends Ordered {

}
