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

package org.springframework.core.annotation;

import org.springframework.core.DecoratingProxy;
import org.springframework.core.OrderComparator;
import org.springframework.lang.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@code AnnotationAwareOrderComparator} is an extension of
 * OrderComparator的扩展类AnnotationAwareOrderComparator
 * {@link OrderComparator} that supports Spring's
 * 该类支持Spring的Ordered接口也支持@Order和@Priority注解，Ordered
 * {@link org.springframework.core.Ordered} interface as well as the
 * 接口提供的排序值会覆盖静态定义的注解值（如果有的话）。
 * {@link Order @Order} and {@link javax.annotation.Priority @Priority}
 * annotations, with an order value provided by an {@code Ordered}
 * instance overriding a statically defined annotation value (if any).
 *
 * <p>Consult the Javadoc for {@link OrderComparator} for details on the
 * 参考OrderComparator的文档查看对无序对象进行排序的细节。
 * sort semantics for non-ordered objects.
 *
 * @author Juergen Hoeller
 * @author Oliver Gierke
 * @author Stephane Nicoll
 * @since 2.0.1
 * @see org.springframework.core.Ordered
 * @see org.springframework.core.annotation.Order
 * @see javax.annotation.Priority
 */
public class AnnotationAwareOrderComparator extends OrderComparator {

	/**
	 * Shared default instance of {@code AnnotationAwareOrderComparator}.
	 */
	public static final AnnotationAwareOrderComparator INSTANCE = new AnnotationAwareOrderComparator();


	/**
	 * This implementation checks for {@link Order @Order} or
	 * {@link javax.annotation.Priority @Priority} on various kinds of
	 * elements, in addition to the {@link org.springframework.core.Ordered}
	 * check in the superclass.
	 */
	@Override
	@Nullable
	protected Integer findOrder(Object obj) {
		// Check for regular Ordered interface
		Integer order = super.findOrder(obj);
		if (order != null) {
			return order;
		}

		// Check for @Order and @Priority on various kinds of elements
		if (obj instanceof Class) {
			return OrderUtils.getOrder((Class<?>) obj);
		}
		else if (obj instanceof Method) {
			Order ann = AnnotationUtils.findAnnotation((Method) obj, Order.class);
			if (ann != null) {
				return ann.value();
			}
		}
		else if (obj instanceof AnnotatedElement) {
			Order ann = AnnotationUtils.getAnnotation((AnnotatedElement) obj, Order.class);
			if (ann != null) {
				return ann.value();
			}
		}
		else {
			order = OrderUtils.getOrder(obj.getClass());
			if (order == null && obj instanceof DecoratingProxy) {
				order = OrderUtils.getOrder(((DecoratingProxy) obj).getDecoratedClass());
			}
		}

		return order;
	}

	/**
	 * This implementation retrieves an @{@link javax.annotation.Priority}
	 * value, allowing for additional semantics over the regular @{@link Order}
	 * annotation: typically, selecting one object over another in case of
	 * multiple matches but only one object to be returned.
	 */
	@Override
	@Nullable
	public Integer getPriority(Object obj) {
		if (obj instanceof Class) {
			return OrderUtils.getPriority((Class<?>) obj);
		}
		Integer priority = OrderUtils.getPriority(obj.getClass());
		if (priority == null && obj instanceof DecoratingProxy) {
			priority = OrderUtils.getOrder(((DecoratingProxy) obj).getDecoratedClass());
		}
		return priority;
	}


	/**
	 * Sort the given List with a default AnnotationAwareOrderComparator.
	 * 用默认的AnnotationAwareOrderComparator给给定列表排序。
	 * <p>Optimized to skip sorting for lists with size 0 or 1,
	 * 优化跳过大小为0或者1的列表，为了防止不必要的数组遍历。
	 * in order to avoid unnecessary array extraction.
	 * @param list the List to sort
	 * @see java.util.Collections#sort(java.util.List, java.util.Comparator)
	 */
	public static void sort(List<?> list) {
		if (list.size() > 1) {
			Collections.sort(list, INSTANCE);
		}
	}

	/**
	 * Sort the given array with a default AnnotationAwareOrderComparator.
	 * <p>Optimized to skip sorting for lists with size 0 or 1,
	 * in order to avoid unnecessary array extraction.
	 * @param array the array to sort
	 * @see java.util.Arrays#sort(Object[], java.util.Comparator)
	 */
	public static void sort(Object[] array) {
		if (array.length > 1) {
			Arrays.sort(array, INSTANCE);
		}
	}

	/**
	 * Sort the given array or List with a default AnnotationAwareOrderComparator,
	 * if necessary. Simply skips sorting when given any other value.
	 * <p>Optimized to skip sorting for lists with size 0 or 1,
	 * in order to avoid unnecessary array extraction.
	 * @param value the array or List to sort
	 * @see java.util.Arrays#sort(Object[], java.util.Comparator)
	 */
	public static void sortIfNecessary(Object value) {
		if (value instanceof Object[]) {
			sort((Object[]) value);
		}
		else if (value instanceof List) {
			sort((List<?>) value);
		}
	}

}
