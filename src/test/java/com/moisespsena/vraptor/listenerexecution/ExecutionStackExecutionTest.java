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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.moisespsena.vraptor.listenerexecution.ClassesForTest.C1;
import com.moisespsena.vraptor.listenerexecution.ClassesForTest.C2;
import com.moisespsena.vraptor.listenerexecution.ClassesForTest.C3;
import com.moisespsena.vraptor.listenerexecution.ClassesForTest.C4;
import com.moisespsena.vraptor.listenerexecution.ClassesForTest.C5;
import com.moisespsena.vraptor.listenerexecution.TestClasses.ALazyListener;
import com.moisespsena.vraptor.listenerexecution.TestClasses.AListener;
import com.moisespsena.vraptor.listenerexecution.TestClasses.BListener;
import com.moisespsena.vraptor.listenerexecution.TestClasses.BNotAcceptListener;
import com.moisespsena.vraptor.listenerexecution.TestClasses.CListener;
import com.moisespsena.vraptor.listenerexecution.TestClassesIntercepts.BI;
import com.moisespsena.vraptor.listenerexecution.topological.DefaultListenerOrderDefaultsResolver;
import com.moisespsena.vraptor.listenerexecution.topological.DefaultTopologicalSortedListenersOrder;
import com.moisespsena.vraptor.listenerexecution.topological.TopologicalSortedHandlersRegistry;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 16/09/2011
 */
public class ExecutionStackExecutionTest {
	private static ListenerExecutor<MyListener> createDefaultListenerExecutor(
			final List<Class<? extends MyListener>> classes) {
		return new ListenerExecutor<MyListener>() {
			@Override
			public boolean accepts(final MyListener instance) {
				return instance.accept();
			}

			@Override
			public void execute(final ExecutionStack<MyListener> stack,
					final MyListener instance) throws ExecutionStackException {
				instance.execute(stack, classes);
			}
		};
	}

	private static HandlersRunnerFactory<MyListener> createRunnerFactory() {
		return new HandlersRunnerFactory<MyListener>(
				new AbstractListenerInstanceResolver() {
					@Override
					public <T> T instanceFor(final Class<T> type)
							throws ExecutionStackException {
						try {
							final T instance = TestClasses.createInstance(type);
							return instance;
						} catch (final Exception e) {
							ExecutionStackException.throwFrom(e);
						}
						return null;
					}
				});
	}

	static void executeStack(
			final ListenerExecutor<MyListener> listenerExecutor,
			final HandlersRunnerFactory<MyListener> handlersRunnerFactory,
			final Class<? extends MyListener>... classes)
			throws ExecutionStackException {
		final HandlersRegistry<MyListener> registry = new TopologicalSortedHandlersRegistry<MyListener>();
		registry.register(classes);

		final ExecutionStack<MyListener> stack = new DefaultExecutionStack<MyListener>(
				listenerExecutor, handlersRunnerFactory, registry.allArray());

		final DefaultExecutionStackExecution<MyListener> stackExecution = new DefaultExecutionStackExecution<MyListener>(
				stack);

		stackExecution.execute();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void executeWithLazyAcceptAll() throws ExecutionStackException {
		final String A_VALUE = "A_VALUE";
		final List<Class<? extends MyListener>> classes = new ArrayList<Class<? extends MyListener>>();
		final ListenerExecutor<MyListener> listenerExecutor = createDefaultListenerExecutor(classes);

		executeStack(
				listenerExecutor,
				new HandlersRunnerFactory<MyListener>(
						new AbstractListenerInstanceResolver() {
							@Override
							public <T> T instanceFor(final Class<T> type)
									throws ExecutionStackException {
								try {
									T instance = null;
									if (type.equals(ALazyListener.class)) {
										instance = TestClasses.createInstance(
												type, A_VALUE);

										Assert.assertEquals(A_VALUE,
												((MyListener) instance)
														.getValue());
									} else {
										instance = TestClasses
												.createInstance(type);
										Assert.assertEquals(
												TestClasses.LISTENER_DEFAULT_VALUE,
												((MyListener) instance)
														.getValue());
									}
									return instance;
								} catch (final Exception e) {
									ExecutionStackException.throwFrom(e);
								}
								return null;
							}

							@Override
							public <T> T instanceForWithoutDependencies(
									final Class<T> type) {
								final T instance = super
										.instanceForWithoutDependencies(type);

								Assert.assertEquals(ALazyListener.class, type);
								Assert.assertNull(((MyListener) instance)
										.getValue());

								return instance;
							}
						}), AListener.class, ALazyListener.class,
				BListener.class,
				CListener.class);

		final Object[] expecteds = new Object[] { AListener.class,
				ALazyListener.class, BListener.class, CListener.class };
		final Object[] actuals = classes.toArray();

		Assert.assertArrayEquals(expecteds, actuals);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void executeWithOutLazyAcceptAC() throws ExecutionStackException {
		final List<Class<? extends MyListener>> classes = new ArrayList<Class<? extends MyListener>>();
		final ListenerExecutor<MyListener> listenerExecutor = createDefaultListenerExecutor(classes);

		executeStack(listenerExecutor, createRunnerFactory(), AListener.class,
				BNotAcceptListener.class, CListener.class);

		final Object[] expecteds = new Object[] { AListener.class,
				CListener.class };
		final Object[] actuals = classes.toArray();

		Assert.assertArrayEquals(expecteds, actuals);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void executeWithOutLazyAcceptAll() throws ExecutionStackException {
		final List<Class<? extends MyListener>> classes = new ArrayList<Class<? extends MyListener>>();
		final ListenerExecutor<MyListener> listenerExecutor = new ListenerExecutor<MyListener>() {
			@Override
			public boolean accepts(final MyListener instance) {
				return true;
			}

			@Override
			public void execute(final ExecutionStack<MyListener> stack,
					final MyListener instance) throws ExecutionStackException {
				instance.execute(stack, classes);
			}
		};

		executeStack(listenerExecutor, createRunnerFactory(), AListener.class,
				BNotAcceptListener.class, CListener.class);

		final Object[] expecteds = new Object[] { AListener.class,
				BNotAcceptListener.class, CListener.class };
		final Object[] actuals = classes.toArray();

		Assert.assertArrayEquals(expecteds, actuals);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void executeWithOutLazyCheckOrder() throws Exception {
		final List<Class<? extends MyListener>> classes = new ArrayList<Class<? extends MyListener>>();
		final ListenerExecutor<MyListener> listenerExecutor = createDefaultListenerExecutor(classes);
		final HandlersRunnerFactory<MyListener> runnerFactory = createRunnerFactory();
		executeStack(listenerExecutor, runnerFactory, C1.class, C2.class,
				C3.class, C4.class, C5.class, C5.class);

		final Class<? extends MyListener>[] actuals = classes
				.toArray(new Class[0]);

		final OrderCheck<MyListener> orderCheck = new OrderCheck<MyListener>(
				new DefaultListenerOrderDefaultsResolver<MyListener>(),
				new DefaultTopologicalSortedListenersOrder<MyListener>());

		orderCheck.check(actuals);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void executeWithOutLazyIntercepts() throws ExecutionStackException {
		final List<Class<? extends MyListener>> classes = new ArrayList<Class<? extends MyListener>>();
		final ListenerExecutor<MyListener> listenerExecutor = createDefaultListenerExecutor(classes);

		executeStack(listenerExecutor, createRunnerFactory(), AListener.class,
				BI.class, CListener.class);

		final Object[] expecteds = new Object[] { AListener.class, BI.class,
				CListener.class };
		final Object[] actuals = classes.toArray();

		Assert.assertArrayEquals(expecteds, actuals);
	}
}
