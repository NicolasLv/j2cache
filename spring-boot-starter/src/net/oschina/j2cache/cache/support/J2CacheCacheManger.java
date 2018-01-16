package net.oschina.j2cache.cache.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.util.CollectionUtils;


/**
 * {@link CacheManager} implementation for J2Cache.
 * @author zhangsaizz
 *
 */
public class J2CacheCacheManger extends AbstractTransactionSupportingCacheManager{
	
	private boolean allowNullValues = true;
	
	private Collection<String> cacheNames;
	
	private boolean dynamic = true;
	
	@Override
	protected Collection<? extends Cache> loadCaches() {
		Collection<Cache> caches = new LinkedHashSet<>(cacheNames.size());
		for (String name : cacheNames) {
			J2CacheCache cache = new J2CacheCache(name, allowNullValues);
			caches.add(cache);
		}
		return caches;
	}
	
	
	public boolean isAllowNullValues() {
		return allowNullValues;
	}

	public void setAllowNullValues(boolean allowNullValues) {
		this.allowNullValues = allowNullValues;
	}
	
	@Override
	protected Cache getMissingCache(String name) {
		return this.dynamic ? new J2CacheCache(name, allowNullValues) : null;
	}
	
	
	public void setCacheNames(Collection<String> cacheNames) {
		Set<String> newCacheNames = CollectionUtils.isEmpty(cacheNames) ? Collections.<String> emptySet()
				: new HashSet<String>(cacheNames);
		this.cacheNames = newCacheNames;
		this.dynamic = newCacheNames.isEmpty();
	}

}
