package com.code.aon.jaas.ldap;

import java.util.Collection;

import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.ast.IRole;
import com.code.aon.jaas.deployment.event.SubDeployerEvent;

public class Application implements IApplication {

	private static final long serialVersionUID = -7774786273060605086L;

	/** Domain identifier. */
	private String id;

    /** Application description. */
    private String description;
    
    /** Application context. */
    private String context;
	
	private SecurityLdap ldap;
	
	public Application(SecurityLdap ldap) {
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
	public Collection<IDomain> domains() {
		throw new UnsupportedOperationException("Not supported!");
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public void setContext(String context) {
		this.context = context;
	}

	@Override
	public String getContext() {
		return ( this.context != null ) ? this.context : "/" + this.id;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public IDomain getDomain(String name) {
		return this.ldap.getDomain(name);
	}

	@Override
	public String getHashAlgorithm() {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public String getHashEncoding() {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public IRole getRole(String name) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public String getRoles() {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public String getSecurityDomain() {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public IDomain remove(IDomain domain) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public Collection<IRole> roles() {
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

	@Override
	public void domainFound(SubDeployerEvent event) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void methodPermissionFound(SubDeployerEvent event) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void roleFound(SubDeployerEvent event) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void securityPermissionFound(SubDeployerEvent event) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void vendorDescriptorFound(SubDeployerEvent event) {
		throw new UnsupportedOperationException("Not supported!");
	}

}
