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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.moisespsena.vraptor.listenerexecution.topological.ListenerOrderDefaultsResolver;
import com.moisespsena.vraptor.listenerexecution.topological.TopologicalSortedListenersOrder;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 19/09/2011
 */
public class OrderCheck<T> {

	static class Checker<F> {
		private static <T> Checker<T> createActuals(
				final Class<? extends T> item, final Class<? extends T>[] items) {
			final Set<Class<? extends T>> befores = new HashSet<Class<? extends T>>();
			final Set<Class<? extends T>> afters = new HashSet<Class<? extends T>>();

			boolean itemChecked = false;
			for (final Class<? extends T> ci : items) {
				if (!itemChecked) {
					if (item.equals(ci)) {
						itemChecked = true;
					} else {
						befores.add(ci);
					}
				} else {
					afters.add(ci);
				}
			}

			final Checker<T> checker = new Checker<T>(item, befores, afters);
			return checker;
		}

		private static <T> Checker<T> createExpected(
				final Class<? extends T> actual,
				final ListenerOrderDefaultsResolver<T> defaultsResolver,
				final TopologicalSortedListenersOrder<T> listenersOrder) {
			final Class<? extends T>[] befores = defaultsResolver.resolve(
					listenersOrder, listenersOrder.befores(actual));
			final Class<? extends T>[] afters = defaultsResolver.resolve(
					listenersOrder, listenersOrder.afters(actual));

			final Set<Class<? extends T>> beforeSet = new HashSet<Class<? extends T>>();
			final Set<Class<? extends T>> afterSet = new HashSet<Class<? extends T>>();

			beforeSet.addAll(Arrays.asList(befores));
			afterSet.addAll(Arrays.asList(afters));

			final Checker<T> checker = new Checker<T>(actual, beforeSet,
					afterSet);
			return checker;
		}

		private final Set<Class<? extends F>> afters;
		private final Set<Class<? extends F>> befores;

		private final Class<? extends F> item;

		/**
		 * @param item
		 * @param befores2
		 * @param afters2
		 */
		public Checker(final Class<? extends F> item,
				final Set<Class<? extends F>> befores,
				final Set<Class<? extends F>> afters) {
			super();
			this.item = item;
			this.befores = befores;
			this.afters = afters;
		}

		private void check(final Checker<F> actualChecker)
				throws CheckerException {

			// itera sobre os que sao esperados depois do atual
			for (final Class<? extends F> beforeThen : this.befores) {
				// o atual deve estar antes destes
				if (!actualChecker.afters.contains(beforeThen)) {
					throw new CheckerException("expected " + actualChecker.item
							+ " before " + beforeThen);
				}
			}

			// itera sobre os que sao esperados antes do atual
			for (final Class<? extends F> afterThen : this.afters) {
				// o atual deve estar depois destes
				if (!actualChecker.befores.contains(afterThen)) {
					throw new CheckerException("expected " + actualChecker.item
							+ " after " + afterThen);
				}
			}
		}
	}

	public static class CheckerException extends Exception {
		private static final long serialVersionUID = -1128927121150332253L;

		public CheckerException() {
			super();
		}

		public CheckerException(final String message) {
			super(message);
		}

		public CheckerException(final String message, final Throwable cause) {
			super(message, cause);
		}

		public CheckerException(final Throwable cause) {
			super(cause);
		}

	}

	private final ListenerOrderDefaultsResolver<T> defaultsResolver;

	private final TopologicalSortedListenersOrder<T> listenersOrder;

	public OrderCheck(final ListenerOrderDefaultsResolver<T> defaultsResolver,
			final TopologicalSortedListenersOrder<T> listenersOrder) {
		this.defaultsResolver = defaultsResolver;
		this.listenersOrder = listenersOrder;
	}

	public void check(final Class<? extends T>... actuals)
			throws CheckerException {
		for (final Class<? extends T> actual : actuals) {
			final Checker<T> expectedChecker = Checker.createExpected(actual,
					defaultsResolver, listenersOrder);
			final Checker<T> actualChecker = Checker.createActuals(actual,
					actuals);

			expectedChecker.check(actualChecker);
		}
	}
}
