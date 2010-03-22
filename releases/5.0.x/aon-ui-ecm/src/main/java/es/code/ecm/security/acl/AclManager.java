package es.code.ecm.security.acl;

import java.util.List;

public interface AclManager {

	static final String EHCACHE = "resources/ehcache.xml";
	static final String ACL_CACHE_NAME = "es.code.ecm.repository.cache.aclCache";

	abstract List<AclEntry> getAcls(Object obj);

    abstract List<AclEntry> getAcls(String workspaceName, Object obj);

}
