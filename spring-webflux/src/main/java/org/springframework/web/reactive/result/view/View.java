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

package org.springframework.web.reactive.result.view;

import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Contract to render {@link HandlerResult} to the HTTP response.
 * 将HandlerResult发送到HTTP 响应。
 *
 * <p>In contrast to an {@link org.springframework.core.codec.Encoder Encoder}
 * 与单例的以及能对任何给定类型的对象编码的Encoder相比，通常通过名称选择的以及使用
 * which is a singleton and encodes any object of a given type, a {@code View}
 * ViewResolver解析的View，比如：该ViewResolver将View匹配成一个HTML模板。此外一个
 * is typically selected by name and resolved using a {@link ViewResolver}
 * 视图可以基于模型中包含的多个属性进行发送。
 * which may for example match it to an HTML template. Furthermore a {@code View}
 * may render based on multiple attributes contained in the model.
 *
 * <p>A {@code View} can also choose to select an attribute from the model use
 * 一个View也可以选择从模型中获取一个属性，使用任何存在的Encoder发送代替的媒体类型
 * any existing {@code Encoder} to render alternate media types.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public interface View {

	/**
	 * Return the list of media types this View supports, or an empty list.
	 */
	List<MediaType> getSupportedMediaTypes();

	/**
	 * Whether this View does rendering by performing a redirect.
	 */
	default boolean isRedirectView() {
		return false;
	}

	/**
	 * Render the view based on the given {@link HandlerResult}. Implementations
	 * 发送基于给定HandlerResult的视图。实现类能访问和使用模型或者模型中的特定属性。
	 * can access and use the model or only a specific attribute in it.
	 * @param model Map with name Strings as keys and corresponding model
	 * objects as values (Map can also be {@code null} in case of empty model)
	 * @param contentType the content type selected to render with which should
	 * match one of the {@link #getSupportedMediaTypes() supported media types}.
	 * @param exchange the current exchange
	 * @return {@code Mono} to represent when and if rendering succeeds
	 */
	Mono<Void> render(@Nullable Map<String, ?> model, @Nullable MediaType contentType, ServerWebExchange exchange);

}
