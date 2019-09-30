/*
 * Copyright 2002-2011 the original author or authors.
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

package org.springframework.core.convert.support;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;

/**
 * Configuration interface to be implemented by most if not all {@link ConversionService}
 * 如果不是全部都是ConversionService类型，那么多数要实现的配置接口。合并ConversionService的
 * types. Consolidates the read-only operations exposed by {@link ConversionService} and
 * 只读操作和ConverterRegistry的可变操作，这样就可以便利的增加和移除Converter。
 * the mutating operations of {@link ConverterRegistry} to allow for convenient ad-hoc
 * 在应用上下文启动代码中有ConfigurableEnvironment时，ConverterRegistry很有用的。
 * addition and removal of {@link org.springframework.core.convert.converter.Converter
 * Converters} through. The latter is particularly useful when working against a
 * {@link org.springframework.core.env.ConfigurableEnvironment ConfigurableEnvironment}
 * instance in application context bootstrapping code.
 *
 * @author Chris Beams
 * @since 3.1
 * @see org.springframework.core.env.ConfigurablePropertyResolver#getConversionService()
 * @see org.springframework.core.env.ConfigurableEnvironment
 * @see org.springframework.context.ConfigurableApplicationContext#getEnvironment()
 */
public interface ConfigurableConversionService extends ConversionService, ConverterRegistry {

}
