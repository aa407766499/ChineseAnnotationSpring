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

package org.springframework.beans;

import org.springframework.core.ResolvableType;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.security.*;

/**
 * Default {@link BeanWrapper} implementation that should be sufficient
 * BeanWrapper的默认实现，可以满足所有使用情况。缓存有效的拦截结果。
 * for all typical use cases. Caches introspection results for efficiency.
 *
 * <p>Note: Auto-registers default property editors from the
 * 注意：除了应用JDK标准的属性编辑器，还应用自动注册的来自propertyeditors包
 * {@code org.springframework.beans.propertyeditors} package, which apply
 * 的默认属性编辑器。应用能调用registerCustomEditor方法来注册特定实例的编辑器
 * in addition to the JDK's standard PropertyEditors. Applications can call
 * （比如：这些编辑器不是应用内共享的）。查看PropertyEditorRegistrySupport基础类的细节。
 * the {@link #registerCustomEditor(Class, java.beans.PropertyEditor)} method
 * to register an editor for a particular instance (i.e. they are not shared
 * across the application). See the base class
 * {@link PropertyEditorRegistrySupport} for details.
 *
 * <p><b>NOTE: As of Spring 2.5, this is - for almost all purposes - an
 * 注意：Spring2.5版本，这是一个用于所有用途的内部类。标记为public是为了让其他
 * internal class.</b> It is just public in order to allow for access from
 * 框架包访问。对于标准应用访问目的，使用PropertyAccessorFactory的forBeanPropertyAccess
 * other framework packages. For standard application access purposes, use the
 * 工厂方法代替。
 * {@link PropertyAccessorFactory#forBeanPropertyAccess} factory method instead.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Stephane Nicoll
 * @since 15 April 2001
 * @see #registerCustomEditor
 * @see #setPropertyValues
 * @see #setPropertyValue
 * @see #getPropertyValue
 * @see #getPropertyType
 * @see BeanWrapper
 * @see PropertyEditorRegistrySupport
 */
public class BeanWrapperImpl extends AbstractNestablePropertyAccessor implements BeanWrapper {

	/**
	 * Cached introspections results for this object, to prevent encountering
	 * the cost of JavaBeans introspection every time.
	 */
	@Nullable
	private CachedIntrospectionResults cachedIntrospectionResults;

	/**
	 * The security context used for invoking the property methods
	 */
	@Nullable
	private AccessControlContext acc;


	/**
	 * Create a new empty BeanWrapperImpl. Wrapped instance needs to be set afterwards.
	 * Registers default editors.
	 * @see #setWrappedInstance
	 */
	public BeanWrapperImpl() {
		this(true);
	}

	/**
	 * Create a new empty BeanWrapperImpl. Wrapped instance needs to be set afterwards.
	 * @param registerDefaultEditors whether to register default editors
	 * (can be suppressed if the BeanWrapper won't need any type conversion)
	 * @see #setWrappedInstance
	 */
	public BeanWrapperImpl(boolean registerDefaultEditors) {
		super(registerDefaultEditors);
	}

	/**
	 * Create a new BeanWrapperImpl for the given object.
	 * @param object object wrapped by this BeanWrapper
	 */
	public BeanWrapperImpl(Object object) {
		super(object);
	}

	/**
	 * Create a new BeanWrapperImpl, wrapping a new instance of the specified class.
	 * @param clazz class to instantiate and wrap
	 */
	public BeanWrapperImpl(Class<?> clazz) {
		super(clazz);
	}

	/**
	 * Create a new BeanWrapperImpl for the given object,
	 * registering a nested path that the object is in.
	 * @param object object wrapped by this BeanWrapper
	 * @param nestedPath the nested path of the object
	 * @param rootObject the root object at the top of the path
	 */
	public BeanWrapperImpl(Object object, String nestedPath, Object rootObject) {
		super(object, nestedPath, rootObject);
	}

	/**
	 * Create a new BeanWrapperImpl for the given object,
	 * registering a nested path that the object is in.
	 * @param object object wrapped by this BeanWrapper
	 * @param nestedPath the nested path of the object
	 * @param parent the containing BeanWrapper (must not be {@code null})
	 */
	private BeanWrapperImpl(Object object, String nestedPath, BeanWrapperImpl parent) {
		super(object, nestedPath, parent);
		setSecurityContext(parent.acc);
	}


	/**
	 * Set a bean instance to hold, without any unwrapping of {@link java.util.Optional}.
	 * @param object the actual target object
	 * @since 4.3
	 * @see #setWrappedInstance(Object)
	 */
	public void setBeanInstance(Object object) {
		this.wrappedObject = object;
		this.rootObject = object;
		this.typeConverterDelegate = new TypeConverterDelegate(this, this.wrappedObject);
		setIntrospectionClass(object.getClass());
	}

	@Override
	public void setWrappedInstance(Object object, @Nullable String nestedPath, @Nullable Object rootObject) {
		super.setWrappedInstance(object, nestedPath, rootObject);
		setIntrospectionClass(getWrappedClass());
	}

	/**
	 * Set the class to introspect.
	 * Needs to be called when the target object changes.
	 * @param clazz the class to introspect
	 */
	protected void setIntrospectionClass(Class<?> clazz) {
		if (this.cachedIntrospectionResults != null && this.cachedIntrospectionResults.getBeanClass() != clazz) {
			this.cachedIntrospectionResults = null;
		}
	}

	/**
	 * Obtain a lazily initializted CachedIntrospectionResults instance
	 * for the wrapped object.
	 */
	private CachedIntrospectionResults getCachedIntrospectionResults() {
		if (this.cachedIntrospectionResults == null) {
			this.cachedIntrospectionResults = CachedIntrospectionResults.forClass(getWrappedClass());
		}
		return this.cachedIntrospectionResults;
	}

	/**
	 * Set the security context used during the invocation of the wrapped instance methods.
	 * Can be null.
	 */
	public void setSecurityContext(@Nullable AccessControlContext acc) {
		this.acc = acc;
	}

	/**
	 * Return the security context used during the invocation of the wrapped instance methods.
	 * Can be null.
	 */
	@Nullable
	public AccessControlContext getSecurityContext() {
		return this.acc;
	}


	/**
	 * Convert the given value for the specified property to the latter's type.
	 * 将给定值转换为后者类型的指定属性。
	 * <p>This method is only intended for optimizations in a BeanFactory.
	 * 该方法仅在BeanFactory中用于优化。使用convertIfNecessary方法来编程转换。
	 * Use the {@code convertIfNecessary} methods for programmatic conversion.
	 * @param value the value to convert
	 * @param propertyName the target property
	 * (note that nested or indexed properties are not supported here)
	 * @return the new value, possibly the result of type conversion
	 * @throws TypeMismatchException if type conversion failed
	 */
	@Nullable
	public Object convertForProperty(@Nullable Object value, String propertyName) throws TypeMismatchException {
		CachedIntrospectionResults cachedIntrospectionResults = getCachedIntrospectionResults();
		PropertyDescriptor pd = cachedIntrospectionResults.getPropertyDescriptor(propertyName);
		if (pd == null) {
			throw new InvalidPropertyException(getRootClass(), getNestedPath() + propertyName,
					"No property '" + propertyName + "' found");
		}
		TypeDescriptor td = cachedIntrospectionResults.getTypeDescriptor(pd);
		if (td == null) {
			td = cachedIntrospectionResults.addTypeDescriptor(pd, new TypeDescriptor(property(pd)));
		}
		return convertForProperty(propertyName, null, value, td);
	}

	private Property property(PropertyDescriptor pd) {
		GenericTypeAwarePropertyDescriptor gpd = (GenericTypeAwarePropertyDescriptor) pd;
		return new Property(gpd.getBeanClass(), gpd.getReadMethod(), gpd.getWriteMethod(), gpd.getName());
	}

	@Override
	@Nullable
	protected BeanPropertyHandler getLocalPropertyHandler(String propertyName) {
		PropertyDescriptor pd = getCachedIntrospectionResults().getPropertyDescriptor(propertyName);
		if (pd != null) {
			return new BeanPropertyHandler(pd);
		}
		return null;
	}

	@Override
	protected BeanWrapperImpl newNestedPropertyAccessor(Object object, String nestedPath) {
		return new BeanWrapperImpl(object, nestedPath, this);
	}

	@Override
	protected NotWritablePropertyException createNotWritablePropertyException(String propertyName) {
		PropertyMatches matches = PropertyMatches.forProperty(propertyName, getRootClass());
		throw new NotWritablePropertyException(
				getRootClass(), getNestedPath() + propertyName,
				matches.buildErrorMessage(), matches.getPossibleMatches());
	}

	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		return getCachedIntrospectionResults().getPropertyDescriptors();
	}

	@Override
	public PropertyDescriptor getPropertyDescriptor(String propertyName) throws InvalidPropertyException {
		BeanWrapperImpl nestedBw = (BeanWrapperImpl) getPropertyAccessorForPropertyPath(propertyName);
		String finalPath = getFinalPath(nestedBw, propertyName);
		PropertyDescriptor pd = nestedBw.getCachedIntrospectionResults().getPropertyDescriptor(finalPath);
		if (pd == null) {
			throw new InvalidPropertyException(getRootClass(), getNestedPath() + propertyName,
					"No property '" + propertyName + "' found");
		}
		return pd;
	}


	//Bean属性处理器，调用属性的getter、setter进行读写
	private class BeanPropertyHandler extends PropertyHandler {

		private final PropertyDescriptor pd;

		public BeanPropertyHandler(PropertyDescriptor pd) {
			super(pd.getPropertyType(), pd.getReadMethod() != null, pd.getWriteMethod() != null);
			this.pd = pd;
		}

		@Override
		public ResolvableType getResolvableType() {
			return ResolvableType.forMethodReturnType(this.pd.getReadMethod());
		}

		@Override
		public TypeDescriptor toTypeDescriptor() {
			return new TypeDescriptor(property(this.pd));
		}

		@Override
		@Nullable
		public TypeDescriptor nested(int level) {
			return TypeDescriptor.nested(property(pd), level);
		}

		//利用反射机制根据属性的getter方法获取属性值
		@Override
		@Nullable
		public Object getValue() throws Exception {
			final Method readMethod = this.pd.getReadMethod();
			if (System.getSecurityManager() != null) {
				AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
					ReflectionUtils.makeAccessible(readMethod);
					return null;
				});
				try {
					return AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () ->
							readMethod.invoke(getWrappedInstance(), (Object[]) null), acc);
				}
				catch (PrivilegedActionException pae) {
					throw pae.getException();
				}
			}
			else {
				ReflectionUtils.makeAccessible(readMethod);
				return readMethod.invoke(getWrappedInstance(), (Object[]) null);
			}
		}

		//调用被包装对象的属性setter方法给属性赋值。
		@Override
		public void setValue(final @Nullable Object value) throws Exception {
			final Method writeMethod = (this.pd instanceof GenericTypeAwarePropertyDescriptor ?
					((GenericTypeAwarePropertyDescriptor) this.pd).getWriteMethodForActualAccess() :
					this.pd.getWriteMethod());
			if (System.getSecurityManager() != null) {
				AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
					ReflectionUtils.makeAccessible(writeMethod);
					return null;
				});
				try {
					AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () ->
							writeMethod.invoke(getWrappedInstance(), value), acc);
				}
				catch (PrivilegedActionException ex) {
					throw ex.getException();
				}
			}
			else {
				ReflectionUtils.makeAccessible(writeMethod);
				writeMethod.invoke(getWrappedInstance(), value);
			}
		}
	}

}
