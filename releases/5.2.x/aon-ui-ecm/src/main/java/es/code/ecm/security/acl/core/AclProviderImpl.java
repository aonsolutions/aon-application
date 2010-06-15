package es.code.ecm.security.acl.core;

import java.util.List;

import net.sf.ehcache.Cache;
import es.code.ecm.ContentRepository;
import es.code.ecm.nodes.ECMNode;
import es.code.ecm.security.acl.AclEntry;
import es.code.ecm.security.acl.AclObjectIdentity;
import es.code.ecm.security.acl.AclProvider;
import es.code.ecm.security.acl.cache.AclEntryCache;
import es.code.ecm.security.acl.cache.EhCacheBasedAclEntryCache;

public class AclProviderImpl implements AclProvider {

	private String name;
    private AclEntryCache aclEntryCache;

	public AclProviderImpl(Cache cache) {
		this( ContentRepository.DEFAULT_WORKSPACE, cache );
	}

	public AclProviderImpl(String name, Cache cache) {
		this.name = name;
		aclEntryCache = new EhCacheBasedAclEntryCache( cache ); 
	}

	public AclEntryCache getAclEntryCache() {
		return aclEntryCache;
	}

	public void setAclEntryCache(AclEntryCache aclEntryCache) {
		this.aclEntryCache = aclEntryCache;
	}

	@Override
	public List<AclEntry> getAcls(Object obj) {
		if ( obj instanceof AclObjectIdentity ) {
	        AclObjectIdentity aclObjectIdentity = (AclObjectIdentity) obj;
			return aclEntryCache.getEntriesFromCache( aclObjectIdentity );
		}
		return null;
	}

	@Override
	public boolean supports(String name, Object obj) {
		if ( name == null || this.name.equals( name ) ) 
			return true;

		if ( obj instanceof AclObjectIdentity ) {
			AclObjectIdentity aoi = (AclObjectIdentity) obj;
			return aoi.getNodeType().equals( ECMNode.DOCUMENT_TYPE ) 
				|| aoi.getNodeType().equals( ECMNode.FOLDER_TYPE )
				|| aoi.getNodeType().equals( ECMNode.USER_TYPE );
		}
		return false;
	}

}
