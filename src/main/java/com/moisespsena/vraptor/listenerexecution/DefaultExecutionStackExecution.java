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

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 16/09/2011
 */
public class DefaultExecutionStackExecution<T> implements
		ExecutionStackExecution<T> {

	private final ExecutionStack<T> stack;

	/**
	 * 
	 */
	public DefaultExecutionStackExecution(final ExecutionStack<T> stack) {
		this.stack = stack;
	}

	@Override
	public void execute() throws ExecutionStackException {
		// faz a fila andar para o primeiro handler
		stack.next();

		while (true) {
			final HandlerRunner<T> lastHandlerRunner = stack
					.getLastHandlerRunner();

			// se o ultimo handler runner for um interceptador, ele tem que
			// disparar o next do stack. Se ele nao disparou, é porque ele
			// quer
			// que a fila pare de ser executada
			if ((lastHandlerRunner == null)
					|| lastHandlerRunner.isInterception()) {
				break;
			}
			// caso contrario, ele se comporta como uma fila. Dispara o next
			// do
			// stack para a fila andar
			else {
				stack.next();
			}
		}
	}
}
