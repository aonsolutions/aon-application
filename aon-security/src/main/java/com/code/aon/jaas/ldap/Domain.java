package com.code.aon.jaas.ldap;

import java.util.Collection;
import java.util.Map;

import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.UserAlreadyExistException;
import com.code.aon.ldap.Entry;

public class Domain implements IDomain {
	
	private static final long serialVersionUID = -8630396330009341954L;

	/** Domain identifier. */
	private String id;

	private SecurityLdap ldap;
	
	public Domain(SecurityLdap ldap) {
		this.ldap = ldap;
	}
	
    /**
     * Assigns domain identifier.
     * 
     * @param string
     */
	public void setId(String string) {
		this.id = string;
	}
	
	@Override
	public void add(IUser user) throws UserAlreadyExistException {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public Collection<IDomainApplication> applications() {
		return this.ldap.getDomainApplications(this.id);
	}

	@Override
	public IAccessPolicy getAccessPolicy() {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public IDataSourceMetaData getDataSourceMetaData() {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public IDomainApplication getDomainApplication(String name) {
		Entry domainApplication = ldap.getDomainApplication(this.id, name);
		if ( domainApplication != null ) {
			return ldap.getDomainApplication(domainApplication, this.id);
		}
		return null;
	}

	@Override
	public IUser getStandaloneUser(String name) {
		Entry user = ldap.getUser(this.id, name);
		if ( user != null ) {
			return ldap.getUser(user);
		}
		return null;
	}

	@Override
	public IDomainApplication remove(String appName) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void remove(IUser user) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void setAccessPolicy(IAccessPolicy access) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void setDataSourceMetaData(IDataSourceMetaData dsmt) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public Map<String, IUser> standaloneUsers() {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public IUser update(IUser user, String oldUserId)
			throws UserAlreadyExistException {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void accept(INodeVisitor visitor) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public String getId() {
		return this.id;
	}

}
