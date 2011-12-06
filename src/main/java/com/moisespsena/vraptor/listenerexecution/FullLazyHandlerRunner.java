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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 16/09/2011
 */
public class FullLazyHandlerRunner<T> implements HandlerRunner<T> {
	private static final Logger logger = LoggerFactory
			.getLogger(ToInstantiateHandlerRunner.class);
	private T instance;

	private final ListenerInstanceResolver instanceResolver;
	private final boolean interception;
	private final Class<? extends T> type;

	public FullLazyHandlerRunner(
			final ListenerInstanceResolver instanceResolver,
			final Class<? extends T> type, final boolean interception) {
		this.instanceResolver = instanceResolver;
		this.type = type;
		this.interception = interception;
	}

	private T getInstance() throws ExecutionStackException {
		if (instance == null) {
			instance = instanceResolver.instanceForWithoutDependencies(type);
		}
		return instance;
	}

	@Override
	public boolean isInterception() {
		return interception;
	}

	@Override
	public void run(final ExecutionStack<T> stack,
			final ListenerExecutor<T> listenerExecutor)
			throws ExecutionStackException {
		final T instance = getInstance();

		if (listenerExecutor.accepts(instance)) {
			logger.debug("Invoking handler {}:{}", listenerExecutor.getClass()
					.getSimpleName(), instance.getClass().getSimpleName());

			listenerExecutor.execute(stack, instance);
		}
	}

	@Override
	public String toString() {
		return "LazyHandlerRunner for " + type.getName();
	}
}
