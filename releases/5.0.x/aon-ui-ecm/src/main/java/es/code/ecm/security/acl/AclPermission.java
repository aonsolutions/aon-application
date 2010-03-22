package es.code.ecm.security.acl;

import java.io.Serializable;

public interface AclPermission extends Serializable {

	Long getAccessLevel();

	String getAclReadEntries();

	String getAclWriteEntries();

	String getAclDeleteEntries();
}
