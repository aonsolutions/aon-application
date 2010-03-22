package com.code.aon.jaas.client.ast.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.code.aon.jaas.auth.util.Util;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.ast.IRole;

import com.code.aon.jaas.deployment.ast.IMethod;
import com.code.aon.jaas.deployment.ast.IPermission;
import com.code.aon.jaas.deployment.ast.IResource;
import com.code.aon.jaas.deployment.core.SubDeployerVisitor;
import com.code.aon.jaas.deployment.event.SubDeployerEvent;

import com.code.aon.jaas.vendor.deployment.ast.IVendorDescriptor;

/**
 * Application deployed in the application server. This information is retrieved from 
 * <b>aon.workspace/deployed.xml</b> file.  
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 17-may-2004
 * @since 1.0
 *  
 */
public class Application implements IApplication {

    private static final long serialVersionUID = 4114656730648447890L;

    /** Application identifier. */
    private String id;

    /** Application description. */
    private String description;

    /** Application context. */
    private String context;

	/** Security domain used for Authentication and Authorization. */
	String securityDomain = "aon.security";

    /** 
     * Hash algorithm used to password generation. Default null.
     * SHA-1 (Secure Hash Algorithm 1) is slower than MD5, but the message digest is larger, 
     * which makes it more resistant to brute force attacks. Therefore, it is recommended 
     * that Secure Hash Algorithm is preferred to MD5 for all of your digest needs. Note, 
     * SHA-1 now has even higher strength brothers, SHA-256, SHA-384, and SHA-512 for 256, 
     * 384 and 512-bit digests respectively.
     */
	protected String hashAlgorithm = null;

    /** Hash encoding format. */
	protected String hashEncoding;

    /** Application roles with its access and execution permissions. */
    private Map<String, IRole> roles = new LinkedHashMap<String, IRole>();

    /** Application registered domains. */
    private Map<String, IDomain> domains = new LinkedHashMap<String, IDomain>();

    /**
     * Construct a new instance of this class.
     * 
     * @param name
     * @param isFile
     * @return IApplication
     */
    public static final IApplication createApplication(String name, boolean isFile) {
    	Application app = new Application(name);
		app.setContext( "/" + ( (isFile)? resolveContex(name): name ) );
        return app;
    }

    /**
     * Constructor.
     */
    public Application() {
    }

    /**
     * Constructor.
     * 
     * @param id String
     */
    public Application(String id) {
        this.id = id;
    }

    /**
     * Assign application identifier.
     * 
     * @param string
     */
    public void setId(String string) {
        id = string;
    }

    /**
     * Assign application description.
     * 
     * @param string
     */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
     * Assign application context.
     * 
     * @param string.
     */
    public void setContext(String context) {
        this.context = context;
    }

	/**
     * Assign application security domain.
     * 
	 * @param securityDomain The security domain to set.
	 */
	public void setSecurityDomain(String securityDomain) {
		this.securityDomain = securityDomain;
	}

	/**
     * Assign application hash algorithm.
     * 
	 * @param hashAlgorithm The Hash Algorithm to set.
	 */
	public void setHashAlgorithm(String hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}

	/**
     * Assign application hash encoding.
     * 
	 * @param hashEncoding The Hash Encoding to set.
	 */
	public void setHashEncoding(String hashEncoding) {
		this.hashEncoding = hashEncoding;
	}

    /**
     * Assign a domain to the application.
     * 
     * @param domain IDomain
     */
	public void addDomain(IDomain domain) {
		if ( !this.domains.containsKey(domain.getId()) ) {
			this.domains.put( domain.getId(), domain );
		}
	}

	/**
     * Actualize application from deployed application attributes.
     * 
     * @param app
     */
	public void actualize(IApplication app) {
		setContext( app.getContext() );
    	if ( app.getSecurityDomain() != null )
    		setSecurityDomain( app.getSecurityDomain() );
    	setHashAlgorithm( app.getHashAlgorithm() );
    	setHashEncoding( app.getHashEncoding() );
    	this.roles.clear();
    	Iterator<IRole> iter = app.roles().iterator();
    	while (iter.hasNext()) {
			IRole role = iter.next();
			this.roles.put( role.getId(), role );
		}
	}

    /**
     * Sustituye en el caso de existir un dominio con el mismo nombre por la nueva y 
     * devuelve el dominio antiguo. En caso de no existir la añade.
     *  
     * @param name
     * @param domain
     * @return
     */
	public IDomain replaceDomain(String name, IDomain domain) {
		return this.domains.put( name, domain );
	}

	/* (non-Javadoc)
     * @see com.code.aon.jaas.client.ast.INode#getId()
     */
    public String getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.code.aon.jaas.client.ast.INode#accept(com.code.aon.jaas.client.ast.INodeVisitor)
     */
    public void accept(INodeVisitor visitor) {
        visitor.visitApplication(this);
    }

	/* (non-Javadoc)
     * @see com.aon.jaas.client.ast.IApplication#getDescription()
     */
	public String getDescription() {
		return this.description;
	}

	/* (non-Javadoc)
     * @see com.aon.jaas.client.ast.IApplication#getContext()
     */
    public String getContext() {
        return this.context;
    }

    /* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IApplication#getSecurityDomain()
	 */
	public String getSecurityDomain() {
		return this.securityDomain;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IApplication#getHashAlgorithm()
	 */
	public String getHashAlgorithm() {
		return this.hashAlgorithm;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IApplication#getHashEncoding()
	 */
	public String getHashEncoding() {
		return this.hashEncoding;
	}

	/* (non-Javadoc)
     * @see com.code.aon.jaas.client.ast.IApplication#roles()
     */
    public Collection<IRole> roles() {
        return Collections.unmodifiableCollection(roles.values());
    }

    /* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IApplication#getRoles()
	 */
	public String getRoles() {
		List<String> r = new ArrayList<String>();
		Iterator<IRole> iter = this.roles.values().iterator();
		while (iter.hasNext()) {
			IRole role = iter.next();
			r.add( role.getId() );
		}
		return r.toString();
	}

	/* (non-Javadoc)
     * @see com.code.aon.jaas.client.ast.IApplication#getRole(java.lang.String)
     */
    public IRole getRole(String name) {
        return this.roles.get(name);
    }

    /* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IApplication#entities()
	 */
	public Collection<IDomain> domains() {
        return Collections.unmodifiableCollection(this.domains.values());
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IApplication#getDomain(java.lang.String)
	 */
	public IDomain getDomain(String name) {
		return this.domains.get(name);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IApplication#remove(com.code.aon.jaas.client.ast.IDomain)
	 */
	public IDomain remove(IDomain domain) {
		this.domains.get( domain.getId() ).remove( this.id );
		return this.domains.remove( domain.getId() );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.deployment.event.ISubDeployerListener#roleFound(com.code.aon.jaas.deployment.event.SubDeployerEvent)
	 */
    public void roleFound(SubDeployerEvent event) {
        SubDeployerVisitor visitor = (SubDeployerVisitor) event.getSource();
        String roleName = visitor.getActiveRole();
        IRole role = getRole(roleName);
        if (role == null) {
            role = Role.createInstance(roleName);
        }
		if ( !this.roles.containsKey(role.getId()) ) {
			this.roles.put( role.getId(), role );
		}
    }

    /* (non-Javadoc)
     * @see com.code.aon.jaas.deployment.event.ISubDeployerListener#methodPermissionFound(com.code.aon.jaas.deployment.event.SubDeployerEvent)
     */
    public void methodPermissionFound(SubDeployerEvent event) {
        SubDeployerVisitor visitor = (SubDeployerVisitor) event.getSource();
        IMethod method = (IMethod) event.getPermission();
        IRole role = (IRole) getRole(visitor.getActiveRole());
        boolean anonimous = visitor.getActiveRole().equals(IPermission.ANONIMOUS);
        role = (role == null && anonimous) ? Role.createInstance(IPermission.ANONIMOUS) : role;
        ExecutePermission execute = (ExecutePermission) role.getPermission(method.getName());
        if (execute == null) {
            execute = new ExecutePermission();
            execute.setId(method.getName());
            execute.setDescription(method.getDescription());
            ((Role) role).addPermission(execute);
        }
        execute.addMethod(method.getMethod());
    }

    /* (non-Javadoc)
     * @see com.code.aon.jaas.deployment.event.ISubDeployerListener#securityPermissionFound(com.code.aon.jaas.deployment.event.SubDeployerEvent)
     */
    public void securityPermissionFound(SubDeployerEvent event) {
        SubDeployerVisitor visitor = (SubDeployerVisitor) event.getSource();
        IResource resource = (IResource) event.getPermission();
        AccessPermission access = 
        	new AccessPermission(resource.getName(), resource.getDescription());
        Iterator<String> iter = resource.patterns().iterator();
        while (iter.hasNext()) {
            String urlPattern = iter.next();
            access.addPattern(urlPattern);
        }
        iter = resource.methods().iterator();
        while (iter.hasNext()) {
            String method = iter.next();
            access.addMethod(method);
        }
        IRole role = (IRole) getRole(visitor.getActiveRole());
        if (role == null) {
            role = Role.createInstance(visitor.getActiveRole());
    		if ( !this.roles.containsKey(role) ) {
    			this.roles.put( role.getId(), role );
    		}
        }
        ((Role) role).addPermission(access);
    }

    /* (non-Javadoc)
     * @see com.code.aon.jaas.deployment.event.ISubDeployerListener#vendorDescriptorFound(com.code.aon.jaas.deployment.event.SubDeployerEvent)
     */
    public void vendorDescriptorFound(SubDeployerEvent event) {
        IVendorDescriptor ivj = (IVendorDescriptor) event.getSource();
        this.context = ivj.getContext();
        this.securityDomain = ivj.getSecurityDomain();
    }

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.deployment.event.ISubDeployerListener#domainFound(com.code.aon.jaas.deployment.event.SubDeployerEvent)
	 */
	public void domainFound(SubDeployerEvent event) {
        Domain domain = (Domain) event.getSource();
        AccessPolicy ap = (AccessPolicy) domain.getAccessPolicy();
        if ( ap == null ) {
        	domain.setAccessPolicy( AccessPolicy.getInstance() );
        }
        addDomain(domain);
	}

	/**
	 * Resolve default application context from application identifier.
	 * 
	 * @param appId
	 * @return
	 */
	private static String resolveContex(String appId) {
		int lastDotIndex = appId.lastIndexOf('.');
		return (lastDotIndex > -1)? appId.substring( 0, lastDotIndex ): appId;
	}
}