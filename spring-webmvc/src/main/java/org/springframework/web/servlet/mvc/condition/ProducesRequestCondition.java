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

import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition.HeaderExpression;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * A logical disjunction (' || ') request condition to match a request's 'Accept' header
 * 逻辑解析或('||')请求条件，将请求的Accept头与媒体类型表达式的列表进行匹配。
 * to a list of media type expressions. Two kinds of media type expressions are
 * 支持两种媒体类型表达式，由RequestMapping的produces()方法和RequestMapping
 * supported, which are described in {@link RequestMapping#produces()} and
 * 的headers()方法进行描述，头名称要是Accept。忽略要使用的语法，也忽略
 * {@link RequestMapping#headers()} where the header name is 'Accept'.
 * 相同的语义。
 * Regardless of which syntax is used, the semantics are the same.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public final class ProducesRequestCondition extends AbstractRequestCondition<ProducesRequestCondition> {

	private final static ProducesRequestCondition PRE_FLIGHT_MATCH = new ProducesRequestCondition();

	private static final ProducesRequestCondition EMPTY_CONDITION = new ProducesRequestCondition();


	private final List<ProduceMediaTypeExpression> MEDIA_TYPE_ALL_LIST =
			Collections.singletonList(new ProduceMediaTypeExpression("*/*"));

	private final List<ProduceMediaTypeExpression> expressions;

	private final ContentNegotiationManager contentNegotiationManager;


	/**
	 * Creates a new instance from "produces" expressions. If 0 expressions
	 * are provided in total, this condition will match to any request.
	 * @param produces expressions with syntax defined by {@link RequestMapping#produces()}
	 */
	public ProducesRequestCondition(String... produces) {
		this(produces, null, null);
	}

	/**
	 * Creates a new instance with "produces" and "header" expressions. "Header"
	 * expressions where the header name is not 'Accept' or have no header value
	 * defined are ignored. If 0 expressions are provided in total, this condition
	 * will match to any request.
	 * @param produces expressions with syntax defined by {@link RequestMapping#produces()}
	 * @param headers expressions with syntax defined by {@link RequestMapping#headers()}
	 */
	public ProducesRequestCondition(String[] produces, @Nullable String[] headers) {
		this(produces, headers, null);
	}

	/**
	 * Same as {@link #ProducesRequestCondition(String[], String[])} but also
	 * accepting a {@link ContentNegotiationManager}.
	 * @param produces expressions with syntax defined by {@link RequestMapping#produces()}
	 * @param headers expressions with syntax defined by {@link RequestMapping#headers()}
	 * @param manager used to determine requested media types
	 */
	public ProducesRequestCondition(String[] produces, @Nullable String[] headers,
			@Nullable ContentNegotiationManager manager) {

		this.expressions = new ArrayList<>(parseExpressions(produces, headers));
		Collections.sort(this.expressions);
		this.contentNegotiationManager = (manager != null ? manager : new ContentNegotiationManager());
	}

	/**
	 * Private constructor with already parsed media type expressions.
	 */
	private ProducesRequestCondition(Collection<ProduceMediaTypeExpression> expressions,
			@Nullable ContentNegotiationManager manager) {

		this.expressions = new ArrayList<>(expressions);
		Collections.sort(this.expressions);
		this.contentNegotiationManager = (manager != null ? manager : new ContentNegotiationManager());
	}


	private Set<ProduceMediaTypeExpression> parseExpressions(String[] produces, @Nullable String[] headers) {
		Set<ProduceMediaTypeExpression> result = new LinkedHashSet<>();
		if (headers != null) {
			for (String header : headers) {
				HeaderExpression expr = new HeaderExpression(header);
				if ("Accept".equalsIgnoreCase(expr.name) && expr.value != null) {
					for (MediaType mediaType : MediaType.parseMediaTypes(expr.value)) {
						result.add(new ProduceMediaTypeExpression(mediaType, expr.isNegated));
					}
				}
			}
		}
		for (String produce : produces) {
			result.add(new ProduceMediaTypeExpression(produce));
		}
		return result;
	}

	/**
	 * Return the contained "produces" expressions.
	 */
	public Set<MediaTypeExpression> getExpressions() {
		return new LinkedHashSet<>(this.expressions);
	}

	/**
	 * Return the contained producible media types excluding negated expressions.
	 */
	public Set<MediaType> getProducibleMediaTypes() {
		Set<MediaType> result = new LinkedHashSet<>();
		for (ProduceMediaTypeExpression expression : this.expressions) {
			if (!expression.isNegated()) {
				result.add(expression.getMediaType());
			}
		}
		return result;
	}

	/**
	 * Whether the condition has any media type expressions.
	 */
	public boolean isEmpty() {
		return this.expressions.isEmpty();
	}

	@Override
	protected List<ProduceMediaTypeExpression> getContent() {
		return this.expressions;
	}

	@Override
	protected String getToStringInfix() {
		return " || ";
	}

	/**
	 * Returns the "other" instance if it has any expressions; returns "this"
	 * instance otherwise. Practically that means a method-level "produces"
	 * overrides a type-level "produces" condition.
	 */
	@Override
	public ProducesRequestCondition combine(ProducesRequestCondition other) {
		return (!other.expressions.isEmpty() ? other : this);
	}

	/**
	 * Checks if any of the contained media type expressions match the given
	 * 检查是否有任何包含的媒体类型表达式匹配给定的请求的'Content-Type'头，然后
	 * request 'Content-Type' header and returns an instance that is guaranteed
	 * 返回一个实例，该实例保证仅包含匹配表达式、通过MediaType的isCompatibleWith(MediaType)
	 * to contain matching expressions only. The match is performed via
	 * 方法进行匹配。
	 * {@link MediaType#isCompatibleWith(MediaType)}.
	 * @param request the current request
	 * @return the same instance if there are no expressions;
	 * or a new condition with matching expressions;
	 * or {@code null} if no expressions match.
	 */
	@Override
	@Nullable
	public ProducesRequestCondition getMatchingCondition(HttpServletRequest request) {
		if (CorsUtils.isPreFlightRequest(request)) {
			return PRE_FLIGHT_MATCH;
		}
		if (isEmpty()) {
			return this;
		}
		List<MediaType> acceptedMediaTypes;
		try {
			acceptedMediaTypes = getAcceptedMediaTypes(request);
		}
		catch (HttpMediaTypeException ex) {
			return null;
		}
		Set<ProduceMediaTypeExpression> result = new LinkedHashSet<>(expressions);
		for (Iterator<ProduceMediaTypeExpression> iterator = result.iterator(); iterator.hasNext();) {
			ProduceMediaTypeExpression expression = iterator.next();
			if (!expression.match(acceptedMediaTypes)) {
				iterator.remove();
			}
		}
		if (!result.isEmpty()) {
			return new ProducesRequestCondition(result, this.contentNegotiationManager);
		}
		else if (acceptedMediaTypes.contains(MediaType.ALL)) {
			return EMPTY_CONDITION;
		}
		else {
			return null;
		}
	}

	/**
	 * Compares this and another "produces" condition as follows:
	 * <ol>
	 * <li>Sort 'Accept' header media types by quality value via
	 * {@link MediaType#sortByQualityValue(List)} and iterate the list.
	 * <li>Get the first index of matching media types in each "produces"
	 * condition first matching with {@link MediaType#equals(Object)} and
	 * then with {@link MediaType#includes(MediaType)}.
	 * <li>If a lower index is found, the condition at that index wins.
	 * <li>If both indexes are equal, the media types at the index are
	 * compared further with {@link MediaType#SPECIFICITY_COMPARATOR}.
	 * </ol>
	 * <p>It is assumed that both instances have been obtained via
	 * {@link #getMatchingCondition(HttpServletRequest)} and each instance
	 * contains the matching producible media type expression only or
	 * is otherwise empty.
	 */
	@Override
	public int compareTo(ProducesRequestCondition other, HttpServletRequest request) {
		try {
			List<MediaType> acceptedMediaTypes = getAcceptedMediaTypes(request);
			for (MediaType acceptedMediaType : acceptedMediaTypes) {
				int thisIndex = this.indexOfEqualMediaType(acceptedMediaType);
				int otherIndex = other.indexOfEqualMediaType(acceptedMediaType);
				int result = compareMatchingMediaTypes(this, thisIndex, other, otherIndex);
				if (result != 0) {
					return result;
				}
				thisIndex = this.indexOfIncludedMediaType(acceptedMediaType);
				otherIndex = other.indexOfIncludedMediaType(acceptedMediaType);
				result = compareMatchingMediaTypes(this, thisIndex, other, otherIndex);
				if (result != 0) {
					return result;
				}
			}
			return 0;
		}
		catch (HttpMediaTypeNotAcceptableException ex) {
			// should never happen
			throw new IllegalStateException("Cannot compare without having any requested media types", ex);
		}
	}

	private List<MediaType> getAcceptedMediaTypes(HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
		List<MediaType> mediaTypes = this.contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request));
		return mediaTypes.isEmpty() ? Collections.singletonList(MediaType.ALL) : mediaTypes;
	}

	private int indexOfEqualMediaType(MediaType mediaType) {
		for (int i = 0; i < getExpressionsToCompare().size(); i++) {
			MediaType currentMediaType = getExpressionsToCompare().get(i).getMediaType();
			if (mediaType.getType().equalsIgnoreCase(currentMediaType.getType()) &&
					mediaType.getSubtype().equalsIgnoreCase(currentMediaType.getSubtype())) {
				return i;
			}
		}
		return -1;
	}

	private int indexOfIncludedMediaType(MediaType mediaType) {
		for (int i = 0; i < getExpressionsToCompare().size(); i++) {
			if (mediaType.includes(getExpressionsToCompare().get(i).getMediaType())) {
				return i;
			}
		}
		return -1;
	}

	private int compareMatchingMediaTypes(ProducesRequestCondition condition1, int index1,
			ProducesRequestCondition condition2, int index2) {

		int result = 0;
		if (index1 != index2) {
			result = index2 - index1;
		}
		else if (index1 != -1) {
			ProduceMediaTypeExpression expr1 = condition1.getExpressionsToCompare().get(index1);
			ProduceMediaTypeExpression expr2 = condition2.getExpressionsToCompare().get(index2);
			result = expr1.compareTo(expr2);
			result = (result != 0) ? result : expr1.getMediaType().compareTo(expr2.getMediaType());
		}
		return result;
	}

	/**
	 * Return the contained "produces" expressions or if that's empty, a list
	 * with a {@code MediaType_ALL} expression.
	 */
	private List<ProduceMediaTypeExpression> getExpressionsToCompare() {
		return (this.expressions.isEmpty() ? MEDIA_TYPE_ALL_LIST : this.expressions);
	}


	/**
	 * Parses and matches a single media type expression to a request's 'Accept' header.
	 */
	class ProduceMediaTypeExpression extends AbstractMediaTypeExpression {

		ProduceMediaTypeExpression(MediaType mediaType, boolean negated) {
			super(mediaType, negated);
		}

		ProduceMediaTypeExpression(String expression) {
			super(expression);
		}

		public final boolean match(List<MediaType> acceptedMediaTypes) {
			boolean match = matchMediaType(acceptedMediaTypes);
			return (!isNegated() ? match : !match);
		}

		private boolean matchMediaType(List<MediaType> acceptedMediaTypes) {
			for (MediaType acceptedMediaType : acceptedMediaTypes) {
				if (getMediaType().isCompatibleWith(acceptedMediaType)) {
					return true;
				}
			}
			return false;
		}
	}

}
