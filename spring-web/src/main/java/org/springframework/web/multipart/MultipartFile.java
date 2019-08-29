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

package org.springframework.web.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.InputStreamSource;
import org.springframework.lang.Nullable;

/**
 * A representation of an uploaded file received in a multipart request.
 * 表示在多部件请求中接收的上传文件。
 * <p>The file contents are either stored in memory or temporarily on disk.
 * 这个文件的内容可以存储在内存中或者临时存储在磁盘上。
 * In either case, the user is responsible for copying file contents to a
 * 在任何一种情况下，用户都负责根据需要将文件内容复制到会话级或持久存储区。
 * session-level or persistent store as and if desired. The temporary storage
 * 在请求处理的末尾会清除临时存储。
 * will be cleared at the end of request processing.
 *
 * @author Juergen Hoeller
 * @author Trevor D. Cook
 * @since 29.09.2003
 * @see org.springframework.web.multipart.MultipartHttpServletRequest
 * @see org.springframework.web.multipart.MultipartResolver
 */
public interface MultipartFile extends InputStreamSource {

	/**
	 * Return the name of the parameter in the multipart form.
	 * 返回多部件表单的参数名称
	 * @return the name of the parameter (never {@code null} or empty)
	 *           参数的名称（绝不是null或者空）
	 */
	String getName();

	/**
	 * Return the original filename in the client's filesystem.
	 * 返回客户端文件系统的原始文件名。
	 * <p>This may contain path information depending on the browser used,
	 * 取决于使用的浏览器，这会包含路径信息，但是通常情况下不会包含路径信息。
	 * but it typically will not with any other than Opera.
	 * @return the original filename, or the empty String if no file has been chosen
	 * 返回原始的文件名，或者如果没有在多部件表单中选择文件返回空字符串，或者如果没有
	 * in the multipart form, or {@code null} if not defined or not available
	 * 定义或者不能获得返回null。
	 * @see org.apache.commons.fileupload.FileItem#getName()
	 * @see org.springframework.web.multipart.commons.CommonsMultipartFile#setPreserveFilename
	 */
	@Nullable
	String getOriginalFilename();

	/**
	 * Return the content type of the file.
	 * 返回文件的内容类型。
	 * @return the content type, or {@code null} if not defined
	 * (or no file has been chosen in the multipart form)
	 */
	@Nullable
	String getContentType();

	/**
	 * Return whether the uploaded file is empty, that is, either no file has
	 * 返回上传文件是否为空，换句话说，在多部件表单中没有选择文件或者选择的文件没有内容。
	 * been chosen in the multipart form or the chosen file has no content.
	 */
	boolean isEmpty();

	/**
	 * Return the size of the file in bytes.
	 * 返回文件的字节大小
	 * @return the size of the file, or 0 if empty
	 */
	long getSize();

	/**
	 * Return the contents of the file as an array of bytes.
	 * 返回文件内容的字节数组
	 * @return the contents of the file as bytes, or an empty byte array if empty
	 * @throws IOException in case of access errors (if the temporary store fails)
	 *                      访问错误（如果临时存储失败）
	 */
	byte[] getBytes() throws IOException;

	/**
	 * Return an InputStream to read the contents of the file from.
	 * 返回读取文件内容的输入流。
	 * <p>The user is responsible for closing the returned stream.
	 * 用户应该负责关闭返回的输入流。
	 * @return the contents of the file as stream, or an empty stream if empty
	 * @throws IOException in case of access errors (if the temporary store fails)
	 */
	@Override
	InputStream getInputStream() throws IOException;

	/**
	 * Transfer the received file to the given destination file.
	 * 传输接收到的文件到指定的文件中。
	 * <p>This may either move the file in the filesystem, copy the file in the
	 * 这可以移动文件系统中的文件，复制文件，或者将内存中的文件内容保存到指定文件。
	 * filesystem, or save memory-held contents to the destination file. If the
	 * 如果文件已经存在，将先被删除。
	 * destination file already exists, it will be deleted first.
	 * <p>If the target file has been moved in the filesystem, this operation
	 * 如果目标文件已经被移动了，这个操作之后不能被再次调用。因此，调用这个方法是为了
	 * cannot be invoked again afterwards. Therefore, call this method just once
	 * 操作任何存储机制。
	 * in order to work with any storage mechanism.
	 * <p><b>NOTE:</b> Depending on the underlying provider, temporary storage
	 * 注意：依赖于底层提供器，临时存储可以是容器依赖，在这里包含指定相对路径的基础目录。
	 * may be container-dependent, including the base directory for relative
	 * （比如Servlet 3.0 多部件处理）
	 * destinations specified here (e.g. with Servlet 3.0 multipart handling).
	 * For absolute destinations, the target file may get renamed/moved from its
	 * 对于绝对路径，在临时路径下目标文件可以被重命名/移动或者重新复制，即使临时副本以及存在。
	 * temporary location or newly copied, even if a temporary copy already exists.
	 * @param dest the destination file (typically absolute)
	 *             文件路径（通常是绝对路径）
	 * @throws IOException in case of reading or writing errors
	 * @throws IllegalStateException if the file has already been moved
	 * 如果文件系统中的文件已经被移动，或者不能再通过其他管道获得。
	 * in the filesystem and is not available anymore for another transfer
	 * @see org.apache.commons.fileupload.FileItem#write(File)
	 * @see javax.servlet.http.Part#write(String)
	 */
	void transferTo(File dest) throws IOException, IllegalStateException;

}
