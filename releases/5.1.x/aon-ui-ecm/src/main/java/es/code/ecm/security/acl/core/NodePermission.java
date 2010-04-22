package es.code.ecm.security.acl.core;

import es.code.ecm.security.acl.AclPermission;

public class NodePermission implements AclPermission {

	private Long accessLevel;
	private String deleteEntries;
	private String readEntries;
	private String writeEntries;

	public NodePermission(Long accessLevel) {
		this.accessLevel = accessLevel;
	}

	@Override
	public Long getAccessLevel() {
		return accessLevel;
	}

	@Override
	public String getAclDeleteEntries() {
		return deleteEntries;
	}

	@Override
	public String getAclReadEntries() {
		return readEntries;
	}

	@Override
	public String getAclWriteEntries() {
		return writeEntries;
	}

}
