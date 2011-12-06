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

import com.moisespsena.vraptor.listenerexecution.TestClasses.AListener;
import com.moisespsena.vraptor.listenerexecution.topological.ListenerOrder;

/**
 * @author Moises P. Sena (http://moisespsena.com)
 * @since 1.0 19/09/2011
 */
public class ClassesForTest {

	@ListenerOrder
	static class C1 extends AListener {
	}

	@ListenerOrder(before = { C4.class }, after = { C5.class })
	static class C2 extends AListener {
	}

	@ListenerOrder(before = { C2.class }, after = { C1.class })
	static class C3 extends AListener {
	}

	@ListenerOrder(after = { C5.class })
	static class C4 extends AListener {
	}

	@ListenerOrder(before = { C4.class })
	static class C5 extends AListener {
	}

	@ListenerOrder(before = { C3.class }, after = { C1.class })
	static class C6 extends AListener {
	}

	@ListenerOrder(before = {}, after = {})
	static class C7 extends AListener {
	}
}
