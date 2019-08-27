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

package org.springframework.beans.factory.wiring;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the BeanWiringInfo class.
 *
 * @author Rick Evans
 * @author Sam Brannen
 */
public class BeanWiringInfoTests {

	@Test(expected = IllegalArgumentException.class)
	public void ctorWithNullBeanName() {
		new BeanWiringInfo(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorWithWhitespacedBeanName() {
		new BeanWiringInfo("   \t");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorWithEmptyBeanName() {
		new BeanWiringInfo("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorWithNegativeIllegalAutowiringValue() {
		new BeanWiringInfo(-1, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorWithPositiveOutOfRangeAutowiringValue() {
		new BeanWiringInfo(123871, true);
	}

	@Test
	public void usingAutowireCtorIndicatesAutowiring() {
		BeanWiringInfo info = new BeanWiringInfo(BeanWiringInfo.AUTOWIRE_BY_NAME, true);
		assertTrue(info.indicatesAutowiring());
	}

	@Test
	public void usingBeanNameCtorDoesNotIndicateAutowiring() {
		BeanWiringInfo info = new BeanWiringInfo("fooService");
		assertFalse(info.indicatesAutowiring());
	}

	@Test
	public void noDependencyCheckValueIsPreserved() {
		BeanWiringInfo info = new BeanWiringInfo(BeanWiringInfo.AUTOWIRE_BY_NAME, true);
		assertTrue(info.getDependencyCheck());
	}

	@Test
	public void dependencyCheckValueIsPreserved() {
		BeanWiringInfo info = new BeanWiringInfo(BeanWiringInfo.AUTOWIRE_BY_TYPE, false);
		assertFalse(info.getDependencyCheck());
	}

}
