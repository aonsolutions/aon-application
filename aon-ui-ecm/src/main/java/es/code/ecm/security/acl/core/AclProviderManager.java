package es.code.ecm.security.acl.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.code.ecm.repository.util.Path;
import es.code.ecm.security.acl.AclEntry;
import es.code.ecm.security.acl.AclManager;
import es.code.ecm.security.acl.AclObjectIdentity;
import es.code.ecm.security.acl.AclProvider;

public class AclProviderManager implements AclManager {

	/** AclProviderManager class Logger */
	static final Logger LOGGER = LoggerFactory.getLogger( AclProviderManager.class.getName() );

	CacheManager cacheManager;
	private List<AclProvider> providers;

	public AclProviderManager() throws IOException {
		InputStream is = Path.getResource( "", EHCACHE, "" ).openStream();
		try {
			cacheManager = new CacheManager( is );
		} finally {
			is.close();
		}
		this.providers = new ArrayList<AclProvider>();
	}

	public void add(AclProvider provider) {
		this.providers.add( provider );
	}

	public Cache getCache() {
		return cacheManager.getCache( ACL_CACHE_NAME );
	}

	public void registerChangedAclEntries(String workspaceName, AclObjectIdentity aoi, List<AclEntry> entries) {
		for(Iterator<AclProvider> iter = providers.iterator(); iter.hasNext();) {
			AclProvider provider = iter.next();
			if ( provider.supports( workspaceName, aoi ) 
					&& provider instanceof AclProviderImpl ) {
				if( LOGGER.isDebugEnabled())
					LOGGER.debug("ACL lookup using " + provider.getClass().getName());
				AclProviderImpl providerImpl = (AclProviderImpl) provider;
				providerImpl.getAclEntryCache().removeEntriesFromCache( aoi.getNodeType() );
				providerImpl.getAclEntryCache().putEntriesInCache( entries, aoi.getNodeType() );
			}
		}

	}
	@Override
	public List<AclEntry> getAcls(Object obj) {
		return getAcls( null, obj );
	}

	@Override
	public List<AclEntry> getAcls(String workspaceName, Object obj) {
		for(Iterator<AclProvider> iter = providers.iterator(); iter.hasNext();) {
			AclProvider provider = iter.next();
			if ( provider.supports( workspaceName, obj ) ) {
				if( LOGGER.isDebugEnabled())
					LOGGER.debug("ACL lookup using " + provider.getClass().getName());
				return provider.getAcls( obj );
			}
		}
		if( LOGGER.isDebugEnabled())
			LOGGER.debug("No AclProvider found for " + obj.toString());
		return null;
	}

}
