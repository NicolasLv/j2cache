/**
 * Copyright (c) 2015-2017.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.j2cache.hibernate4.util;

final class TimeProviderLoader {

    private static SlewClock.TimeProvider timeProvider = new SlewClock.TimeProvider() {
        public final long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    };

    private TimeProviderLoader() {
    }

    public static synchronized SlewClock.TimeProvider getTimeProvider() {
        return timeProvider;
    }

    public static synchronized void setTimeProvider(final SlewClock.TimeProvider timeProvider) {
        TimeProviderLoader.timeProvider = timeProvider;
    }
}