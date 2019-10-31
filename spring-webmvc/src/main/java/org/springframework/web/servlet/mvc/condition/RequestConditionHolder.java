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

package org.springframework.web.servlet.mvc.condition;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;

/**
 * A holder for a {@link RequestCondition} useful when the type of the request
 * 在请求条件的类型未知的情况下，RequestCondition使用的持有器。比如：自定义条件。因为
 * condition is not known ahead of time, e.g. custom condition. Since this
 * 该类也是RequestCondition的一个实现，它将持有的请求条件包装，允许组合请求条件以及
 * class is also an implementation of {@code RequestCondition}, effectively it
 * 和类型的其他请求条件比较，null安全。
 * decorates the held request condition and allows it to be combined and compared
 * with other request conditions in a type and null safe way.
 *
 * <p>When two {@code RequestConditionHolder} instances are combined or compared
 * 在组合两个RequestConditionHolder实例或者两者相互比较时，需要持有的条件是相同类型。
 * with each other, it is expected the conditions they hold are of the same type.
 * 如果不是相同类型，将引发ClassCastException。
 * If they are not, a {@link ClassCastException} is raised.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public final class RequestConditionHolder extends AbstractRequestCondition<RequestConditionHolder> {

	@Nullable
	private final RequestCondition<Object> condition;


	/**
	 * Create a new holder to wrap the given request condition.
	 * @param requestCondition the condition to hold, may be {@code null}
	 */
	@SuppressWarnings("unchecked")
	public RequestConditionHolder(@Nullable RequestCondition<?> requestCondition) {
		this.condition = (RequestCondition<Object>) requestCondition;
	}


	/**
	 * Return the held request condition, or {@code null} if not holding one.
	 */
	@Nullable
	public RequestCondition<?> getCondition() {
		return this.condition;
	}

	@Override
	protected Collection<?> getContent() {
		return (this.condition != null ? Collections.singleton(this.condition) : Collections.emptyList());
	}

	@Override
	protected String getToStringInfix() {
		return " ";
	}

	/**
	 * Combine the request conditions held by the two RequestConditionHolder
	 * instances after making sure the conditions are of the same type.
	 * Or if one holder is empty, the other holder is returned.
	 */
	@Override
	public RequestConditionHolder combine(RequestConditionHolder other) {
		if (this.condition == null && other.condition == null) {
			return this;
		}
		else if (this.condition == null) {
			return other;
		}
		else if (other.condition == null) {
			return this;
		}
		else {
			assertEqualConditionTypes(this.condition, other.condition);
			RequestCondition<?> combined = (RequestCondition<?>) this.condition.combine(other.condition);
			return new RequestConditionHolder(combined);
		}
	}

	/**
	 * Ensure the held request conditions are of the same type.
	 */
	private void assertEqualConditionTypes(RequestCondition<?> thisCondition, RequestCondition<?> otherCondition) {
		Class<?> clazz = thisCondition.getClass();
		Class<?> otherClazz = otherCondition.getClass();
		if (!clazz.equals(otherClazz)) {
			throw new ClassCastException("Incompatible request conditions: " + clazz + " and " + otherClazz);
		}
	}

	/**
	 * Get the matching condition for the held request condition wrap it in a
	 * 获取持有请求条件的匹配条件，将其包装一个新的RequestConditionHolder实例。
	 * new RequestConditionHolder instance. Or otherwise if this is an empty
	 * 或者如果是一个空的持有器，返回相同的持有器实例。
	 * holder, return the same holder instance.
	 */
	@Override
	@Nullable
	public RequestConditionHolder getMatchingCondition(HttpServletRequest request) {
		if (this.condition == null) {
			return this;
		}
		RequestCondition<?> match = (RequestCondition<?>) this.condition.getMatchingCondition(request);
		return (match != null ? new RequestConditionHolder(match) : null);
	}

	/**
	 * Compare the request conditions held by the two RequestConditionHolder
	 * instances after making sure the conditions are of the same type.
	 * Or if one holder is empty, the other holder is preferred.
	 */
	@Override
	public int compareTo(RequestConditionHolder other, HttpServletRequest request) {
		if (this.condition == null && other.condition == null) {
			return 0;
		}
		else if (this.condition == null) {
			return 1;
		}
		else if (other.condition == null) {
			return -1;
		}
		else {
			assertEqualConditionTypes(this.condition, other.condition);
			return this.condition.compareTo(other.condition, request);
		}
	}

}
