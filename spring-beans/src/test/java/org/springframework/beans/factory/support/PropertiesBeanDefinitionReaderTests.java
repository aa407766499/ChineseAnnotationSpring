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

package org.springframework.beans.factory.support;

import org.junit.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;

/**
 * @author Rob Harrop
 */
public class PropertiesBeanDefinitionReaderTests {

	private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

	private final PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(
			beanFactory);


	@Test
	public void withSimpleConstructorArg() {
		this.reader.loadBeanDefinitions(new ClassPathResource("simpleConstructorArg.properties", getClass()));
		TestBean bean = (TestBean)this.beanFactory.getBean("testBean");
		assertEquals("Rob Harrop", bean.getName());
	}

	@Test
	public void withConstructorArgRef() {
		this.reader.loadBeanDefinitions(new ClassPathResource("refConstructorArg.properties", getClass()));
		TestBean rob = (TestBean)this.beanFactory.getBean("rob");
		TestBean sally = (TestBean)this.beanFactory.getBean("sally");
		assertEquals(sally, rob.getSpouse());
	}

	@Test
	public void withMultipleConstructorsArgs() {
		this.reader.loadBeanDefinitions(new ClassPathResource("multiConstructorArgs.properties", getClass()));
		TestBean bean = (TestBean)this.beanFactory.getBean("testBean");
		assertEquals("Rob Harrop", bean.getName());
		assertEquals(23, bean.getAge());
	}

}
