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
package com.moisespsena.vraptor.listenerexecution.topological;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 16/09/2011
 */
public class DefaultTopologicalSortedListenersOrder<T> implements
		TopologicalSortedListenersOrder<T> {

	@Override
	public Class<?>[] afters(final Class<? extends T> type) {
		if (type.isAnnotationPresent(ListenerOrder.class)) {
			final ListenerOrder order = type.getAnnotation(ListenerOrder.class);
			return order.after();
		} else {
			return new Class<?>[0];
		}
	}

	@Override
	public Class<?>[] befores(final Class<? extends T> type) {
		if (type.isAnnotationPresent(ListenerOrder.class)) {
			final ListenerOrder order = type.getAnnotation(ListenerOrder.class);
			return order.before();
		} else {
			return new Class<?>[0];
		}
	}

	@Override
	public Class<? extends T> defaultFirst() {
		return null;
	}

	@Override
	public Class<? extends T> defaultLast() {
		return null;
	}

	@Override
	public Class<? extends T>[] defaults() {
		return null;
	}
}
