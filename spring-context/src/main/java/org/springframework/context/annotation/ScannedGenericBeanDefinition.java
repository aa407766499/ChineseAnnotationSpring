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

package org.springframework.context.annotation;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Extension of the {@link org.springframework.beans.factory.support.GenericBeanDefinition}
 * GenericBeanDefinition类的扩展类，基于ASM类加载器，通过AnnotatedBeanDefinition接口暴露
 * class, based on an ASM ClassReader, with support for annotation metadata exposed
 * 注解元数据。
 * through the {@link AnnotatedBeanDefinition} interface.
 *
 * <p>This class does <i>not</i> load the bean {@code Class} early.
 * 该类不提前加载bean类。相反其通过".class"文件获取所有相关的元数据，该文件由
 * It rather retrieves all relevant metadata from the ".class" file itself,
 * ASM类加载器解析。它在功能上相当于，AnnotatedGenericBeanDefinition
 * parsed with the ASM ClassReader. It is functionally equivalent to
 * AnnotatedGenericBeanDefinition(AnnotationMetadata)，但区别于已扫描的bean类
 * {@link AnnotatedGenericBeanDefinition#AnnotatedGenericBeanDefinition(AnnotationMetadata)}
 * 型和以其他方式注册或检测的bean类型。
 * but distinguishes by type beans that have been <em>scanned</em> vs those that have
 * been otherwise registered or detected by other means.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 2.5
 * @see #getMetadata()
 * @see #getBeanClassName()
 * @see org.springframework.core.type.classreading.MetadataReaderFactory
 * @see AnnotatedGenericBeanDefinition
 */
@SuppressWarnings("serial")
public class ScannedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {

	private final AnnotationMetadata metadata;


	/**
	 * Create a new ScannedGenericBeanDefinition for the class that the
	 * given MetadataReader describes.
	 * @param metadataReader the MetadataReader for the scanned target class
	 */
	public ScannedGenericBeanDefinition(MetadataReader metadataReader) {
		Assert.notNull(metadataReader, "MetadataReader must not be null");
		this.metadata = metadataReader.getAnnotationMetadata();
		setBeanClassName(this.metadata.getClassName());
	}


	@Override
	public final AnnotationMetadata getMetadata() {
		return this.metadata;
	}

	@Override
	@Nullable
	public MethodMetadata getFactoryMethodMetadata() {
		return null;
	}

}
