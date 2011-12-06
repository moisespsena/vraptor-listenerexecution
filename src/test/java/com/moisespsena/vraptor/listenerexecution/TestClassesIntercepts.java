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

import java.util.List;

import com.moisespsena.vraptor.listenerexecution.TestClasses.BListener;
import com.moisespsena.vraptor.listenerexecution.TestClasses.CListener;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 19/09/2011
 */
public class TestClassesIntercepts {
	@Interception
	static class BI extends BListener {
		@Override
		public void execute(final ExecutionStack<MyListener> stack,
				final List<Class<? extends MyListener>> classes)
				throws ExecutionStackException {
			classes.add(getClass());

			stack.next();
		}
	}

	@Interception
	static class CI extends CListener {
		@Override
		public void execute(final ExecutionStack<MyListener> stack,
				final List<Class<? extends MyListener>> classes)
				throws ExecutionStackException {
			classes.add(getClass());

			stack.next();
		}
	}
}
