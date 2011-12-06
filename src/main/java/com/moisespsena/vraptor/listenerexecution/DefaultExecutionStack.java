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
public class DefaultExecutionStack<T> implements ExecutionStack<T> {
	private static final Logger logger = LoggerFactory
			.getLogger(DefaultExecutionStack.class);

	private int currentPosition = -1;
	private final HandlersRunnerFactory<T> handlersRunnerFactory;

	private HandlerRunner<T> lastHandlerRunner;
	private final ListenerExecutor<T> listenerExecutor;

	private final Class<? extends T>[] runnersTypes;
	private boolean stopped = false;

	public DefaultExecutionStack(final ListenerExecutor<T> listenerExecutor,
			final HandlersRunnerFactory<T> handlersRunnerFactory,
			final Class<? extends T>[] runnersTypes) {
		this.handlersRunnerFactory = handlersRunnerFactory;
		this.runnersTypes = runnersTypes;
		this.listenerExecutor = listenerExecutor;
	}

	@Override
	public HandlerRunner<T> getLastHandlerRunner() {
		return lastHandlerRunner;
	}

	@Override
	public void next() throws ExecutionStackException {
		synchronized (this) {
			// reseta o ultimo handlerRunner
			lastHandlerRunner = null;

			if (stopped) {
				logger.debug("Listener Execution is stopped. End of Listener Execution.");
				return;
			}

			currentPosition++;
			if (currentPosition == runnersTypes.length) {
				logger.debug("All registered runners have been called. End of Listener Execution.");
				return;
			}

			final Class<? extends T> runnerType = runnersTypes[currentPosition];
			lastHandlerRunner = handlersRunnerFactory.handlerFor(runnerType);

			lastHandlerRunner.run(this, listenerExecutor);
		}
	}

	@Override
	public void stop() {
		synchronized (this) {
			stopped = true;
		}
	}

}
