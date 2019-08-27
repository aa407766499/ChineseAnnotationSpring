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

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditor;
import java.net.URI;

import org.junit.Test;

import org.springframework.util.ClassUtils;

import static org.junit.Assert.*;

/**
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 */
public class URIEditorTests {

	@Test
	public void standardURI() {
		doTestURI("mailto:juergen.hoeller@interface21.com");
	}

	@Test
	public void withNonExistentResource() {
		doTestURI("gonna:/freak/in/the/morning/freak/in/the.evening");
	}

	@Test
	public void standardURL() {
		doTestURI("http://www.springframework.org");
	}

	@Test
	public void standardURLWithFragment() {
		doTestURI("http://www.springframework.org#1");
	}

	@Test
	public void standardURLWithWhitespace() {
		PropertyEditor uriEditor = new URIEditor();
		uriEditor.setAsText("  http://www.springframework.org  ");
		Object value = uriEditor.getValue();
		assertTrue(value instanceof URI);
		URI uri = (URI) value;
		assertEquals("http://www.springframework.org", uri.toString());
	}

	@Test
	public void classpathURL() {
		PropertyEditor uriEditor = new URIEditor(getClass().getClassLoader());
		uriEditor.setAsText("classpath:" + ClassUtils.classPackageAsResourcePath(getClass()) +
				"/" + ClassUtils.getShortName(getClass()) + ".class");
		Object value = uriEditor.getValue();
		assertTrue(value instanceof URI);
		URI uri = (URI) value;
		assertEquals(uri.toString(), uriEditor.getAsText());
		assertTrue(!uri.getScheme().startsWith("classpath"));
	}

	@Test
	public void classpathURLWithWhitespace() {
		PropertyEditor uriEditor = new URIEditor(getClass().getClassLoader());
		uriEditor.setAsText("  classpath:" + ClassUtils.classPackageAsResourcePath(getClass()) +
				"/" + ClassUtils.getShortName(getClass()) + ".class  ");
		Object value = uriEditor.getValue();
		assertTrue(value instanceof URI);
		URI uri = (URI) value;
		assertEquals(uri.toString(), uriEditor.getAsText());
		assertTrue(!uri.getScheme().startsWith("classpath"));
	}

	@Test
	public void classpathURLAsIs() {
		PropertyEditor uriEditor = new URIEditor();
		uriEditor.setAsText("classpath:test.txt");
		Object value = uriEditor.getValue();
		assertTrue(value instanceof URI);
		URI uri = (URI) value;
		assertEquals(uri.toString(), uriEditor.getAsText());
		assertTrue(uri.getScheme().startsWith("classpath"));
	}

	@Test
	public void setAsTextWithNull() {
		PropertyEditor uriEditor = new URIEditor();
		uriEditor.setAsText(null);
		assertNull(uriEditor.getValue());
		assertEquals("", uriEditor.getAsText());
	}

	@Test
	public void getAsTextReturnsEmptyStringIfValueNotSet() {
		PropertyEditor uriEditor = new URIEditor();
		assertEquals("", uriEditor.getAsText());
	}

	@Test
	public void encodeURI() {
		PropertyEditor uriEditor = new URIEditor();
		uriEditor.setAsText("http://example.com/spaces and \u20AC");
		Object value = uriEditor.getValue();
		assertTrue(value instanceof URI);
		URI uri = (URI) value;
		assertEquals(uri.toString(), uriEditor.getAsText());
		assertEquals("http://example.com/spaces%20and%20%E2%82%AC", uri.toASCIIString());
	}

	@Test
	public void encodeAlreadyEncodedURI() {
		PropertyEditor uriEditor = new URIEditor(false);
		uriEditor.setAsText("http://example.com/spaces%20and%20%E2%82%AC");
		Object value = uriEditor.getValue();
		assertTrue(value instanceof URI);
		URI uri = (URI) value;
		assertEquals(uri.toString(), uriEditor.getAsText());
		assertEquals("http://example.com/spaces%20and%20%E2%82%AC", uri.toASCIIString());
	}


	private void doTestURI(String uriSpec) {
		PropertyEditor uriEditor = new URIEditor();
		uriEditor.setAsText(uriSpec);
		Object value = uriEditor.getValue();
		assertTrue(value instanceof URI);
		URI uri = (URI) value;
		assertEquals(uriSpec, uri.toString());
	}

}
