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

package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Simple interface for objects that are sources for an {@link InputStream}.
 * 对象的简单接口，这些对象是输入流的来源
 * <p>This is the base interface for Spring's more extensive {@link Resource} interface.
 * 这个是扩展接口Resource接口的基础接口
 * <p>For single-use streams, {@link InputStreamResource} can be used for any
 * 对于单使用流来说，InputStreamResource可以被用作任何给定的输入流。Spring的
 * given {@code InputStream}. Spring's {@link ByteArrayResource} or any
 * ByteArrayResource或者任何基于文件Resource实现都能用作具体的实例，其都可以
 * file-based {@code Resource} implementation can be used as a concrete
 * 多次读取底层内容流。比如，该接口用作邮件附件的抽象内容来源。
 * instance, allowing one to read the underlying content stream multiple times.
 * This makes this interface useful as an abstract content source for mail
 * attachments, for example.
 *
 * @author Juergen Hoeller
 * @since 20.01.2004
 * @see java.io.InputStream
 * @see Resource
 * @see InputStreamResource
 * @see ByteArrayResource
 */
public interface InputStreamSource {

	/**
	 * Return an {@link InputStream} for the content of an underlying resource.
	 * 返回一个底层资源内容的输入流。每次调用时都创建一个流。
	 * <p>It is expected that each call creates a <i>fresh</i> stream.
	 * <p>This requirement is particularly important when you consider an API such
	 * 在你思考JavaMail的API时，这个需求很重要，因为在创建邮件附件时需要多次读取流。
	 * as JavaMail, which needs to be able to read the stream multiple times when
	 * 所以需要每次调用返回一个新的流。
	 * creating mail attachments. For such a use case, it is <i>required</i>
	 * that each {@code getInputStream()} call returns a fresh stream.
	 * @return the input stream for the underlying resource (must not be {@code null})
	 * 返回底层资源的输入流（必须不为null）
	 * @throws java.io.FileNotFoundException if the underlying resource doesn't exist
	 * 如果底层资源不存在
	 * @throws IOException if the content stream could not be opened
	 * 如果内容流不能被打开
	 */
	InputStream getInputStream() throws IOException;

}
