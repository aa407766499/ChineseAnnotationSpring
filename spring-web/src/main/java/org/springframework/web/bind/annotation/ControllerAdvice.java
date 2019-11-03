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

package org.springframework.web.bind.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Specialization of {@link Component @Component} for classes that declare
 * 限定于@Component注解类，该类还声明了@ExceptionHandler注解的，@InitBinder注解的，
 * {@link ExceptionHandler @ExceptionHandler}, {@link InitBinder @InitBinder}, or
 * 或者@ModelAttribute注解的方法，这些方法在多个@Controller类之间进行共享。
 * {@link ModelAttribute @ModelAttribute} methods to be shared across
 * multiple {@code @Controller} classes.
 *
 * <p>Classes with {@code @ControllerAdvice} can be declared explicitly as Spring
 * ControllerAdvice注解的类能被明确声明为Spring的bean或者通过类路径扫描自动检索。
 * beans or auto-detected via classpath scanning. All such beans are sorted via
 * 所有这种bean通过AnnotationAwareOrderComparator进行排序，比如基于@Order
 * {@link org.springframework.core.annotation.AnnotationAwareOrderComparator
 * 以及Ordered接口，然后运行时以该顺序应用。对于处理器异常，会选择匹配异常
 * AnnotationAwareOrderComparator}, i.e. based on
 * 处理器方法的首个增强的@ExceptionHandler。对于模型属性和InitBinder初始化，
 * {@link org.springframework.core.annotation.Order @Order} and
 * ModelAttribute和InitBinder注解的方法也会遵循ControllerAdvice的顺序。
 * {@link org.springframework.core.Ordered Ordered}, and applied in that order
 * at runtime. For handling exceptions, an {@code @ExceptionHandler} will be
 * picked on the first advice with a matching exception handler method. For
 * model attributes and {@code InitBinder} initialization, {@code @ModelAttribute}
 * and {@code @InitBinder} methods will also follow {@code @ControllerAdvice} order.
 *
 * <p>Note: For {@code @ExceptionHandler} methods, a root exception match will be
 * 注意：对于ExceptionHandler注解的方法，根异常的匹配会优先于匹配当前异常的原因，
 * preferred to just matching a cause of the current exception, among the handler
 * 在特定增强bean的处理器方法之中。然而，高优先级增强的原因匹配仍然优先低优先级增强bean
 * methods of a particular advice bean. However, a cause match on a higher-priority
 * 的任何匹配。结果是，请在对应顺序的优先的增强bean中声明你的主要根异常映射！
 * advice will still be preferred to a any match (whether root or cause level)
 * on a lower-priority advice bean. As a consequence, please declare your primary
 * root exception mappings on a prioritized advice bean with a corresponding order!
 *
 * <p>By default the methods in an {@code @ControllerAdvice} apply globally to
 * 默认@ControllerAdvice中的方法应用于所有的Controllers。使用选择器 annotations()，
 * all Controllers. Use selectors {@link #annotations()},
 * basePackageClasses()，basePackages()方法去定义目标Controllers更具体的子集。
 * {@link #basePackageClasses()}, and {@link #basePackages()} (or its alias
 * 如果声明了多个选择器，或者应用逻辑，意味着选择的Controllers至少匹配一个
 * {@link #value()}) to define a more narrow subset of targeted Controllers.
 * 选择器。注意：运行时选择器检查的执行以及添加更多的选择器可能会影响性能以及
 * If multiple selectors are declared, OR logic is applied, meaning selected
 * 灵活性。
 * Controllers should match at least one selector. Note that selector checks
 * are performed at runtime and so adding many selectors may negatively impact
 * performance and add complexity.
 *
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @author Sam Brannen
 * @since 3.2
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ControllerAdvice {

	/**
	 * Alias for the {@link #basePackages} attribute.
	 * <p>Allows for more concise annotation declarations e.g.:
	 * {@code @ControllerAdvice("org.my.pkg")} is equivalent to
	 * {@code @ControllerAdvice(basePackages="org.my.pkg")}.
	 * @since 4.0
	 * @see #basePackages()
	 */
	@AliasFor("basePackages")
	String[] value() default {};

	/**
	 * Array of base packages.
	 * <p>Controllers that belong to those base packages or sub-packages thereof
	 * will be included, e.g.: {@code @ControllerAdvice(basePackages="org.my.pkg")}
	 * or {@code @ControllerAdvice(basePackages={"org.my.pkg", "org.my.other.pkg"})}.
	 * <p>{@link #value} is an alias for this attribute, simply allowing for
	 * more concise use of the annotation.
	 * <p>Also consider using {@link #basePackageClasses()} as a type-safe
	 * alternative to String-based package names.
	 * @since 4.0
	 */
	@AliasFor("value")
	String[] basePackages() default {};

	/**
	 * Type-safe alternative to {@link #value()} for specifying the packages
	 * to select Controllers to be assisted by the {@code @ControllerAdvice}
	 * annotated class.
	 * <p>Consider creating a special no-op marker class or interface in each package
	 * that serves no purpose other than being referenced by this attribute.
	 * @since 4.0
	 */
	Class<?>[] basePackageClasses() default {};

	/**
	 * Array of classes.
	 * <p>Controllers that are assignable to at least one of the given types
	 * will be assisted by the {@code @ControllerAdvice} annotated class.
	 * @since 4.0
	 */
	Class<?>[] assignableTypes() default {};

	/**
	 * Array of annotations.
	 * <p>Controllers that are annotated with this/one of those annotation(s)
	 * will be assisted by the {@code @ControllerAdvice} annotated class.
	 * <p>Consider creating a special annotation or use a predefined one,
	 * like {@link RestController @RestController}.
	 * @since 4.0
	 */
	Class<? extends Annotation>[] annotations() default {};

}
