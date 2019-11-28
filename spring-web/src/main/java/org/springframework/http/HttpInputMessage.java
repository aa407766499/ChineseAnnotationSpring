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

package org.springframework.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents an HTTP input message, consisting of {@linkplain #getHeaders() headers}
 * 代表一个HTTP输入消息，由getHeaders()的headers以及可读的getBody()的body组成，
 * and a readable {@linkplain #getBody() body}.
 *
 * <p>Typically implemented by an HTTP request handle on the server side,
 * 通常由服务器端的HTTP请求处理实现，或者客户端的HTTP响应处理实现
 * or an HTTP response handle on the client side.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public interface HttpInputMessage extends HttpMessage {

	/**
	 * Return the body of the message as an input stream.
	 * @return the input stream body (never {@code null})
	 * @throws IOException in case of I/O Errors
	 */
	InputStream getBody() throws IOException;

}
