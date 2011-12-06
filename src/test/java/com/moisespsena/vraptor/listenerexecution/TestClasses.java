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
import java.util.List;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.Lazy;

import com.moisespsena.vraptor.listenerexecution.topological.ListenerOrder;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 19/09/2011
 */
public class TestClasses {
	@ListenerOrder(after = { AOrderListener.class }, before = { BOrderListener.class })
	static class A2OrderListener extends AListener {
	}

	abstract static class AbstractListener implements MyListener {
		private String value = LISTENER_DEFAULT_VALUE;

		@Override
		public void execute(final ExecutionStack<MyListener> stack,
				final List<Class<? extends MyListener>> classes)
				throws ExecutionStackException {
			classes.add(getClass());
		}

		@Override
		public String getValue() {
			return value;
		}

		public void setValue(final String value) {
			this.value = value;
		}
	}

	@Lazy
	static class ALazyListener extends AListener {
		private ALazyListener(final String value) {
			super();
			setValue(value);
		}
	}

	static class AListener extends AbstractListener {
		@Override
		public boolean accept() {
			return true;
		}
	}

	static class ANotAcceptListener extends AListener {
		@Override
		public boolean accept() {
			return false;
		}
	}

	@ListenerOrder
	static class AOrderListener extends AListener {
	}

	@ListenerOrder(after = { COrderListener.class }, before = { C2OrderListener.class })
	static class B2OrderListener extends BListener {
	}

	@Lazy
	static class BLazyListener extends BListener {
	}

	static class BListener extends AbstractListener {
		@Override
		public boolean accept() {
			return true;
		}
	}

	static class BNotAcceptListener extends BListener {
		@Override
		public boolean accept() {
			return false;
		}
	}

	@ListenerOrder(after = { AOrderListener.class }, before = { BOrderListener.class })
	static class BOrderListener extends BListener {
	}

	@ListenerOrder(after = { COrderListener.class })
	static class C2OrderListener extends CListener {
	}

	@Lazy
	static class CLazyListener extends CListener {
	}

	static class CListener extends AbstractListener {
		@Override
		public boolean accept() {
			return true;
		}
	}

	static class CNotAcceptListener extends CListener {
		@Override
		public boolean accept() {
			return false;
		}
	}

	@ListenerOrder(after = { B2OrderListener.class }, before = { C2OrderListener.class })
	static class COrderListener extends CListener {
	}

	public static final String LISTENER_DEFAULT_VALUE = TestClasses.class
			.getName();

	public static <T> T createInstance(final Class<T> type,
			final Object... parameters) {
		final Constructor<?> constructor = type.getDeclaredConstructors()[0];
		final T instance = type.cast(new Mirror().on(type).invoke()
				.constructor(constructor).withArgs(parameters));
		return instance;
	}
}
