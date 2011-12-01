package es.code.ecm.security.acl.cache;

import java.io.Serializable;
import java.util.List;

import es.code.ecm.security.acl.AclEntry;

public class AclEntryHolder implements Serializable {

	private static final long serialVersionUID = 5524672430085361321L;

	private List<AclEntry> entries;

	public AclEntryHolder(List<AclEntry> aclEntries) {
		entries = aclEntries;
	}

	public List<AclEntry> getEntries() {
		return entries;
	}

}
