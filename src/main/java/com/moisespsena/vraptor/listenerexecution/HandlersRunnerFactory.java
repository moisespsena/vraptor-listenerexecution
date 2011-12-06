/***
 * Copyright (c) 2011 Moises P. Sena - www.moisespsena.com
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package com.moisespsena.vraptor.listenerexecution;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 16/09/2011
 */
public class HandlersRunnerFactory<T> {
	private final ConcurrentMap<Class<? extends T>, HandlerRunner<T>> cachedHandlers = new MapMaker()
			.makeMap();

	private final HandlersRunnerFactoryFor<T> factoryFor;
	private final ListenerInstanceResolver instanceResolver;

	public HandlersRunnerFactory(final ListenerInstanceResolver instanceResolver) {
		this(instanceResolver, new DefaultHandlersRunnerFactoryFor<T>());
	}

	public HandlersRunnerFactory(
			final ListenerInstanceResolver instanceResolver,
			final HandlersRunnerFactoryFor<T> factoryFor) {
		this.instanceResolver = instanceResolver;
		this.factoryFor = factoryFor;
	}

	public HandlerRunner<T> handlerFor(final Class<? extends T> type) {
		final boolean isInterception = factoryFor.isInterception(type);

		if (factoryFor.isFullLazy(type)) {
			final HandlerRunner<T> handler = cachedHandlers.get(type);

			if (handler == null) {
				final HandlerRunner<T> value = new FullLazyHandlerRunner<T>(
						instanceResolver, type, isInterception);
				cachedHandlers.putIfAbsent(type, value);
				return value;
			} else {
				return handler;
			}
		} else if (factoryFor.isLazy(type)) {
			final HandlerRunner<T> handler = cachedHandlers.get(type);

			if (handler == null) {
				final HandlerRunner<T> value = new LazyHandlerRunner<T>(
						instanceResolver, type, isInterception);
				cachedHandlers.putIfAbsent(type, value);
				return value;
			} else {
				return handler;
			}
		}

		return new ToInstantiateHandlerRunner<T>(instanceResolver, type,
				isInterception);
	}
}
