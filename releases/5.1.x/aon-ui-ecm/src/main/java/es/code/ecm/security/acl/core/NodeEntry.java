package es.code.ecm.security.acl.core;

import es.code.ecm.security.acl.AclEntry;
import es.code.ecm.security.acl.AclObjectIdentity;
import es.code.ecm.security.acl.AclPermission;

public class NodeEntry implements AclEntry {

	private AclObjectIdentity aclObjectIdentity;
	private AclPermission aclPermission;

	@Override
	public AclObjectIdentity getAclObjectIdentity() {
		return aclObjectIdentity;
	}

	@Override
	public AclPermission getAclPermission() {
		return aclPermission;
	}

	@Override
	public void setAclObjectIdentity(AclObjectIdentity aclobjectidentity) {
		this.aclObjectIdentity = aclobjectidentity;
	}

	@Override
	public void setAclPermission(AclPermission aclPermission) {
		this.aclPermission = aclPermission;
	}

}
