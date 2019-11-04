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

package org.springframework.web.bind.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.ui.Model;

import java.lang.annotation.*;

/**
 * Annotation that binds a method parameter or method return value
 * 该注解将一个方法参数或者方法返回值绑定到一个命名的模型属性，暴露给web
 * to a named model attribute, exposed to a web view. Supported
 * 视图。支持controller类的@RequestMapping注解方法。
 * for controller classes with {@link RequestMapping @RequestMapping}
 * methods.
 *
 * <p>Can be used to expose command objects to a web view, using
 * 用于将命令对象暴露给web视图，使用指定的属性名称，通过在RequestMapping
 * specific attribute names, through annotating corresponding
 * 方法对应的参数上添加注解。
 * parameters of an {@link RequestMapping @RequestMapping} method.
 *
 * <p>Can also be used to expose reference data to a web view
 * 也能用于暴露引用数据给web视图，通过在有RequestMapping方法的
 * through annotating accessor methods in a controller class with
 * controller类的访问器方法上添加注解。这样访问器方法允许有任何
 * {@link RequestMapping @RequestMapping} methods. Such accessor
 * RequestMapping方法支持的参数，返回要暴露的模型属性值。
 * methods are allowed to have any arguments that
 * {@link RequestMapping @RequestMapping} methods support, returning
 * the model attribute value to expose.
 *
 * <p>Note however that reference data and all other model content is
 * 注意：在请求处理结果出现Exception时web视图不能获取引用数据和所有其他模型
 * not available to web views when request processing results in an
 * 内容因为任何时候出现异常表示模型内容不可靠。因此@ExceptionHandler方法
 * {@code Exception} since the exception could be raised at any time
 * 不提供对Model参数的访问。
 * making the content of the model unreliable. For this reason
 * {@link ExceptionHandler @ExceptionHandler} methods do not provide
 * access to a {@link Model} argument.
 *
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 2.5
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModelAttribute {

	/**
	 * Alias for {@link #name}.
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * The name of the model attribute to bind to.
	 * <p>The default model attribute name is inferred from the declared
	 * attribute type (i.e. the method parameter type or method return type),
	 * based on the non-qualified class name:
	 * e.g. "orderAddress" for class "mypackage.OrderAddress",
	 * or "orderAddressList" for "List&lt;mypackage.OrderAddress&gt;".
	 * @since 4.3
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * Allows declaring data binding disabled directly on an {@code @ModelAttribute}
	 * 允许在ModelAttribute方法参数或者ModelAttribute方法返回的属性上直接声明取消数据绑定，
	 * method parameter or on the attribute returned from an {@code @ModelAttribute}
	 * 两者都阻止属性上的数据绑定。
	 * method, both of which would prevent data binding for that attribute.
	 * <p>By default this is set to {@code true} in which case data binding applies.
	 * 默认设置为true，表示应用数据绑定。设置false表示取消数据绑定。
	 * Set this to {@code false} to disable data binding.
	 * @since 4.3
	 */
	boolean binding() default true;

}
