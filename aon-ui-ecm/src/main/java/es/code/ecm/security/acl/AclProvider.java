package es.code.ecm.security.acl;

import java.util.List;

public interface AclProvider {

	abstract List<AclEntry> getAcls(Object obj);

	abstract boolean supports(String name, Object obj);

}
