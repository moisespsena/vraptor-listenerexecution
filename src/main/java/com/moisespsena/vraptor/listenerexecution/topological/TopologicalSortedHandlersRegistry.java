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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.dag.CycleDetectedException;
import org.codehaus.plexus.util.dag.DAG;
import org.codehaus.plexus.util.dag.TopologicalSorter;

import com.moisespsena.vraptor.listenerexecution.HandlersRegistry;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 16/09/2011
 */
public class TopologicalSortedHandlersRegistry<T> implements
		HandlersRegistry<T> {
	static class TopologicalSortedHandlersRegistryException extends
			RuntimeException {
		private static final long serialVersionUID = -1693107310760346933L;

		/**
		 * @param cause
		 */
		public TopologicalSortedHandlersRegistryException(final Throwable cause) {
			super(cause);
		}

	}

	private final Map<String, Class<? extends T>> classMap = new HashMap<String, Class<? extends T>>();
	private boolean defaultsRegistred = false;
	private final ListenerOrderDefaultsResolver<T> defaultsResolver;
	private final DAG graph = new DAG();
	private final TopologicalSortedListenersOrder<T> listenerOrder;

	public TopologicalSortedHandlersRegistry() {
		this(new DefaultTopologicalSortedListenersOrder<T>(),
				new DefaultListenerOrderDefaultsResolver<T>());
	}

	public TopologicalSortedHandlersRegistry(
			final DefaultTopologicalSortedListenersOrder<T> listenersOrder) {
		this(listenersOrder, new DefaultListenerOrderDefaultsResolver<T>());
	}

	public TopologicalSortedHandlersRegistry(
			final ListenerOrderDefaultsResolver<T> defaultsResolver) {
		this(new DefaultTopologicalSortedListenersOrder<T>(), defaultsResolver);
	}

	public TopologicalSortedHandlersRegistry(
			final TopologicalSortedListenersOrder<T> listenerOrder,
			final ListenerOrderDefaultsResolver<T> defaultsResolver) {
		this.listenerOrder = listenerOrder;
		this.defaultsResolver = defaultsResolver;
	}

	private void addEdges(final Class<? extends T> listener) {
		final Class<? extends T>[] before = defaultsResolver.resolve(
				listenerOrder, listenerOrder.befores(listener));
		final Class<? extends T>[] after = defaultsResolver.resolve(
				listenerOrder, listenerOrder.afters(listener));

		final String listenerClassName = listener.getName();

		if (classMap.get(listenerClassName) == null) {
			classMap.put(listenerClassName, listener);
			graph.addVertex(listenerClassName);
		}

		try {
			for (final Class<? extends T> other : before) {
				final String name = other.getName();
				if (classMap.get(name) == null) {
					classMap.put(name, other);
					graph.addVertex(name);
				}
				graph.addEdge(name, listenerClassName);
			}

			for (final Class<? extends T> other : after) {
				final String name = other.getName();

				if (classMap.get(name) == null) {
					classMap.put(name, other);
					graph.addVertex(name);
				}

				graph.addEdge(listenerClassName, name);
			}
		} catch (final CycleDetectedException e) {
			throw new TopologicalSortedHandlersRegistryException(e);
		}
	}

	@Override
	public List<Class<? extends T>> all() {
		final List<Class<? extends T>> list = new ArrayList<Class<? extends T>>();
		for (@SuppressWarnings("unchecked")
		final Iterator<String> i = TopologicalSorter.sort(graph).iterator(); i
				.hasNext();) {
			final String id = i.next();
			final Class<? extends T> clazz = classMap.get(id);
			list.add(clazz);
		}

		final List<Class<? extends T>> sortedClasses = Collections
				.unmodifiableList(list);
		return sortedClasses;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends T>[] allArray() {
		return all().toArray(new Class[0]);
	}

	@Override
	public void register(final Class<? extends T>... listeners) {
		if (!defaultsRegistred) {
			defaultsRegistred = true;

			final Class<? extends T>[] defaults = listenerOrder.defaults();

			if (defaults != null) {
				register(listenerOrder.defaults());
			}
		}

		for (final Class<? extends T> listener : listeners) {
			addEdges(listener);
		}
	}

}