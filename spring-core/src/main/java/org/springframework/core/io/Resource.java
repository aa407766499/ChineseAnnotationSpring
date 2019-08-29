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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.springframework.lang.Nullable;

/**
 * Interface for a resource descriptor that abstracts from the actual
 * 资源描述器接口，其抽象了底层资源的实际类型，比如文件或者类路径资源。
 * type of underlying resource, such as a file or class path resource.
 *
 * <p>An InputStream can be opened for every resource if it exists in
 * 如果资源存在物理形态（磁盘中的文件），那么资源就能被打开成一个输入流，
 * physical form, but a URL or File handle can just be returned for
 * 但是对于确定的资源可以仅返回URL或者File句柄。具体行为由实现类指定。
 * certain resources. The actual behavior is implementation-specific.
 *
 * @author Juergen Hoeller
 * @since 28.12.2003
 * @see #getInputStream()
 * @see #getURL()
 * @see #getURI()
 * @see #getFile()
 * @see WritableResource
 * @see ContextResource
 * @see UrlResource
 * @see ClassPathResource
 * @see FileSystemResource
 * @see PathResource
 * @see ByteArrayResource
 * @see InputStreamResource
 */
public interface Resource extends InputStreamSource {

	/**
	 * Determine whether this resource actually exists in physical form.
	 * 确定资源是否存在物理形态。
	 * <p>This method performs a definitive existence check, whereas the
	 * 这个方法执行一次明确的存在检查，然而资源句柄的存在仅能保证一个有效的
	 * existence of a {@code Resource} handle only guarantees a valid
	 * 描述符句柄
	 * descriptor handle.
	 */
	boolean exists();

	/**
	 * Indicate whether the contents of this resource can be read via
	 * 该方法确定资源的内容能否通过getInputStream()读取。
	 * {@link #getInputStream()}.
	 * <p>Will be {@code true} for typical resource descriptors;
	 * 通常资源描述符将返回true；
	 * note that actual content reading may still fail when attempted.
	 * 注意：在尝试读取内容的时候可能会一直失败。
	 * However, a value of {@code false} is a definitive indication
	 * 然而，该方法返回false明确表明资源内容不可读取。
	 * that the resource content cannot be read.
	 * @see #getInputStream()
	 */
	default boolean isReadable() {
		return true;
	}

	/**
	 * Indicate whether this resource represents a handle with an open stream.
	 * 确定资源能否由一个打开流句柄表示。
	 * If {@code true}, the InputStream cannot be read multiple times,
	 * 如果为true，InputStream不能被多次读取，而且必须被读取以及关闭以防止
	 * and must be read and closed to avoid resource leaks.
	 * 资源泄露。
	 * <p>Will be {@code false} for typical resource descriptors.
	 * 通常资源描述符返回false
	 */
	default boolean isOpen() {
		return false;
	}

	/**
	 * Determine whether this resource represents a file in a file system.
	 * 确定该资源是否是文件系统中的文件。
	 * A value of {@code true} strongly suggests (but does not guarantee)
	 * 返回true明确表明（但不一定保证）getFile()将执行成功
	 * that a {@link #getFile()} call will succeed.
	 * <p>This is conservatively {@code false} by default.
	 * 保守默认返回false。
	 * @since 5.0
	 * @see #getFile()
	 */
	default boolean isFile() {
		return false;
	}

	/**
	 * Return a URL handle for this resource.
	 * 返回该资源的URL句柄。
	 * @throws IOException if the resource cannot be resolved as URL,
	 * 如果该资源不能被解析成URL（统一资源定位符）
	 * i.e. if the resource is not available as descriptor
	 * 如果该资源不能获取到描述符。
	 */
	URL getURL() throws IOException;

	/**
	 * Return a URI handle for this resource.
	 * 返回该资源的URI（统一资源标识符）
	 * @throws IOException if the resource cannot be resolved as URI,
	 * i.e. if the resource is not available as descriptor
	 * @since 2.5
	 */
	URI getURI() throws IOException;

	/**
	 * Return a File handle for this resource.
	 * 返回该资源的文件句柄。
	 * @throws java.io.FileNotFoundException if the resource cannot be resolved as
	 * 如果该资源不能被解析成绝对文件路径，比如该资源不能从文件系统获得。
	 * absolute file path, i.e. if the resource is not available in a file system
	 * @throws IOException in case of general resolution/reading failures
	 * 通常解析/读取失败时抛出
	 * @see #getInputStream()
	 */
	File getFile() throws IOException;

	/**
	 * Return a {@link ReadableByteChannel}.
	 * 返回读取字节通道。
	 * <p>It is expected that each call creates a <i>fresh</i> channel.
	 * 每次调用都会创建新的通道
	 * <p>The default implementation returns {@link Channels#newChannel(InputStream)}
	 * with the result of {@link #getInputStream()}.
	 * @return the byte channel for the underlying resource (must not be {@code null})
	 * 返回底层资源的字节通道，必须不为null
	 * @throws java.io.FileNotFoundException if the underlying resource doesn't exist
	 * @throws IOException if the content channel could not be opened
	 * @since 5.0
	 * @see #getInputStream()
	 */
	default ReadableByteChannel readableChannel() throws IOException {
		return Channels.newChannel(getInputStream());
	}

	/**
	 * Determine the content length for this resource.
	 * 确定资源的内容长度
	 * @throws IOException if the resource cannot be resolved
	 * (in the file system or as some other known physical resource type)
	 */
	long contentLength() throws IOException;

	/**
	 * Determine the last-modified timestamp for this resource.
	 * 确定该资源最近一次被修改的时间戳
	 * @throws IOException if the resource cannot be resolved
	 * (in the file system or as some other known physical resource type)
	 */
	long lastModified() throws IOException;

	/**
	 * Create a resource relative to this resource.
	 * 根据相对路径创建资源。
	 * @param relativePath the relative path (relative to this resource)
	 * @return the resource handle for the relative resource
	 * @throws IOException if the relative resource cannot be determined
	 */
	Resource createRelative(String relativePath) throws IOException;

	/**
	 * Determine a filename for this resource, i.e. typically the last
	 * 确定该资源的文件名称，通常是路径的最后部分，比如："myfile.txt"。
	 * part of the path: for example, "myfile.txt".
	 * <p>Returns {@code null} if this type of resource does not
	 * 如果该资源的类型没有名称，则返回null
	 * have a filename.
	 */
	@Nullable
	String getFilename();

	/**
	 * Return a description for this resource,
	 * 返回该资源的描述，在操作该资源时用于错误输出流。
	 * to be used for error output when working with the resource.
	 * <p>Implementations are also encouraged to return this value
	 * 提倡实现返回toString方法的值。
	 * from their {@code toString} method.
	 * @see Object#toString()
	 */
	String getDescription();

}
