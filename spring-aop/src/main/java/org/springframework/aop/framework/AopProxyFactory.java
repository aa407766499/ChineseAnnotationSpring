/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.aop.framework;

/**
 * Interface to be implemented by factories that are able to create
 * 工厂实现该接口就能够基于AdvisedSupport配置对象创建AOP代理。
 * AOP proxies based on {@link AdvisedSupport} configuration objects.
 *
 * <p>Proxies should observe the following contract:
 * 代理应该遵守以下协议：
 * <ul>
 * <li>They should implement all interfaces that the configuration
 * 实现配置表明的所有接口
 * indicates should be proxied.
 * <li>They should implement the {@link Advised} interface.
 * 实现Advised接口
 * <li>They should implement the equals method to compare proxied
 * 实现equals方法用于比较代理接口，增强以及目标
 * interfaces, advice, and target.
 * <li>They should be serializable if all advisors and target
 * 实现序列化如果所有的切面和目标可以序列化
 * are serializable.
 * <li>They should be thread-safe if advisors and target
 * 如果切面和目标是线程安全的，代理也应该是线程安全的
 * are thread-safe.
 * </ul>
 *
 * <p>Proxies may or may not allow advice changes to be made.
 * 代理可以允许改变增强也可以不允许改变增强。如果他们不允许增强改变
 * If they do not permit advice changes (for example, because
 * （比如，因为配置冻结），那么代理应该抛出AopConfigException
 * the configuration was frozen) a proxy should throw an
 * 在增强发生改变的时候。
 * {@link AopConfigException} on an attempted advice change.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface AopProxyFactory {

	/**
	 * Create an {@link AopProxy} for the given AOP configuration.
	 * 根据给定的AOP配置创建一个AopProxy
	 * @param config the AOP configuration in the form of an
	 * AdvisedSupport object
	 * @return the corresponding AOP proxy
	 * @throws AopConfigException if the configuration is invalid
	 */
	AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException;

}
