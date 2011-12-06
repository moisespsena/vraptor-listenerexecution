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
import java.util.List;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 19/09/2011
 */
public class DefaultListenerOrderDefaultsResolver<T> implements
		ListenerOrderDefaultsResolver<T> {

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends T>[] resolve(
			final TopologicalSortedListenersOrder<T> listenerOrder,
			final Class<?>[] classes) {
		final List<Class<T>> tClasses = new ArrayList<Class<T>>();

		for (Class<?> clazz : classes) {
			if (clazz.equals(DefaultFirst.class)) {
				clazz = listenerOrder.defaultFirst();
			} else if (clazz.equals(DefaultLast.class)) {
				clazz = listenerOrder.defaultLast();
			}

			if (clazz != null) {
				tClasses.add((Class<T>) clazz);
			}
		}

		final Class<?>[] tClassesArr = tClasses.toArray(new Class[0]);

		return (Class<T>[]) tClassesArr;
	}

}
