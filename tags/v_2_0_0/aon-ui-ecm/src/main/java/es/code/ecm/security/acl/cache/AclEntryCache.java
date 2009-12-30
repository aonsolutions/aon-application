package es.code.ecm.security.acl.cache;

import java.util.List;

import es.code.ecm.security.acl.AclEntry;
import es.code.ecm.security.acl.AclObjectIdentity;

public interface AclEntryCache {

    abstract List<AclEntry> getEntriesFromCache(AclObjectIdentity aclObjectIdentity);

    abstract void putEntriesInCache(List<AclEntry> entries, String entryNodeType);

    abstract boolean removeEntriesFromCache(String entryNodeType);

}
