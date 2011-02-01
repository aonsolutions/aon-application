package es.code.ecm.security.acl.cache;

import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.code.ecm.security.acl.AclEntry;
import es.code.ecm.security.acl.AclObjectIdentity;

public class EhCacheBasedAclEntryCache implements AclEntryCache {

	/** EhCacheBasedAclEntryCache class Logger */
	static final Logger LOGGER = LoggerFactory.getLogger( EhCacheBasedAclEntryCache.class.getName() );

	private Cache cache;

	public EhCacheBasedAclEntryCache(Cache cache) {
		this.cache = cache;
	}

	@Override
	public List<AclEntry> getEntriesFromCache(AclObjectIdentity aclObjectIdentity) {
		Element element = cache.get( aclObjectIdentity.getNodeType() ); // throw CacheException
		if ( element == null ) {
			if ( LOGGER.isDebugEnabled() )
				LOGGER.debug("Cache miss: " + aclObjectIdentity);
			return null;
		}
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Cache hit: " + (element != null) + "; object: " + aclObjectIdentity);
		AclEntryHolder holder = (AclEntryHolder)element.getValue();
		return holder.getEntries();
	}

	@Override
	public void putEntriesInCache(List<AclEntry> entries, String entryNodeType) {
		AclEntryHolder holder = new AclEntryHolder( entries );
		Element element = new Element( entryNodeType, holder );
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Cache put: " + element.getKey());
		cache.put(element);
	}

	@Override
	public boolean removeEntriesFromCache(String entryNodeType) {
		return cache.remove( entryNodeType );
	}

}
