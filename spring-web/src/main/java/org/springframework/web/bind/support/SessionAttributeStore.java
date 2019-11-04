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

package org.springframework.web.bind.support;

import org.springframework.lang.Nullable;
import org.springframework.web.context.request.WebRequest;

/**
 * Strategy interface for storing model attributes in a backend session.
 * 存储到后端session中的模型属性的策略接口。
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.web.bind.annotation.SessionAttributes
 */
public interface SessionAttributeStore {

	/**
	 * Store the supplied attribute in the backend session.
	 * <p>Can be called for new attributes as well as for existing attributes.
	 * In the latter case, this signals that the attribute value may have been modified.
	 * @param request the current request
	 * @param attributeName the name of the attribute
	 * @param attributeValue the attribute value to store
	 */
	void storeAttribute(WebRequest request, String attributeName, Object attributeValue);

	/**
	 * Retrieve the specified attribute from the backend session.
	 * 从后端session中获取指定属性。
	 * <p>This will typically be called with the expectation that the
	 * 通常需要属性已经存在才调用该方法，如果该方法返回null，抛出异常。
	 * attribute is already present, with an exception to be thrown
	 * if this method returns {@code null}.
	 * @param request the current request
	 * @param attributeName the name of the attribute
	 * @return the current attribute value, or {@code null} if none
	 */
	@Nullable
	Object retrieveAttribute(WebRequest request, String attributeName);

	/**
	 * Clean up the specified attribute in the backend session.
	 * <p>Indicates that the attribute name will not be used anymore.
	 * @param request the current request
	 * @param attributeName the name of the attribute
	 */
	void cleanupAttribute(WebRequest request, String attributeName);

}
