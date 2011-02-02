package es.code.ecm.security.acl;

import java.io.Serializable;

public interface AclEntry extends Serializable {
	
	abstract void setAclObjectIdentity(AclObjectIdentity aclobjectidentity);

	abstract AclObjectIdentity getAclObjectIdentity();

	abstract void setAclPermission(AclPermission aclPermission);

	abstract AclPermission getAclPermission();
}
