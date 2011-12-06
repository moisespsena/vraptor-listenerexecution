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

import java.lang.reflect.Constructor;

import net.vidageek.mirror.dsl.Mirror;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 16/09/2011
 */
public abstract class AbstractListenerInstanceResolver implements
		ListenerInstanceResolver {
	@Override
	public <T> T instanceForWithoutDependencies(final Class<T> type) {
		final Constructor<?> constructor = type.getDeclaredConstructors()[0];
		final int argsLength = constructor.getParameterTypes().length;
		final T instance = type.cast(new Mirror().on(type).invoke()
				.constructor(constructor).withArgs(new Object[argsLength]));
		return instance;
	}

}
