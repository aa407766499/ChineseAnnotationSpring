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

package org.springframework.aop;

import org.aopalliance.aop.Advice;

/**
 * Common marker interface for before advice, such as {@link MethodBeforeAdvice}.
 * 前置增强的公共标记接口，比如MethodBeforeAdvice。
 * <p>Spring supports only method before advice. Although this is unlikely to change,
 * Spring仅支持方法前置增强。虽然这不能改变，如果需要的话，该API被设计在未来允许字段增强。
 * this API is designed to allow field advice in future if desired.
 *
 * @author Rod Johnson
 * @see AfterAdvice
 */
public interface BeforeAdvice extends Advice {

}
