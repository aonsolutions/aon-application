package com.code.aon.jaas.ldap;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.ast.IRelation;

public class DomainApplication implements IDomainApplication {

    /** Obtiene un logger apropiado. */
	private static final Log LOGGER = LogFactory.getLog( DomainApplication.class.getName() );
	
	/** Domain identifier. */
	private String id;
	
	private String domain;
	
	private String dataSource;

	private SecurityLdap ldap;
	
	public DomainApplication(SecurityLdap ldap, String domain) {
		this.ldap = ldap;
		this.domain = domain;
	}
	
	/**
     * Domain application identifier.
     * 
     * @param string
     */
	public void setId(String string) {
		this.id = string;
	}
	
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public IDataSourceMetaData getDataSourceMetaData() {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public IRelation getProfile(String name) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public IRelation getUser(String name) {
		return ldap.getUserRelation(this.domain, this.id, name);
	}

	@Override
	public boolean isProfileInUsers(String profile) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public Collection<IRelation> profiles() {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void removeProfile(IRelation relation) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void removeUser(IRelation relation) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public IRelation updateProfile(IRelation relation) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public IRelation updateUser(IRelation relation) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public Collection<IRelation> users() {
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
