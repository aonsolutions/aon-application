package es.code.ecm.security.acl;

import java.io.Serializable;

public interface AclObjectIdentity extends Serializable {

	abstract String getNodeType();

	abstract boolean equals(Object obj);

	abstract int hashCode();

}
