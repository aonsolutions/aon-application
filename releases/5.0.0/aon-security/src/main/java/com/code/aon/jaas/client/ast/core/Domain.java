package com.code.aon.jaas.client.ast.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.UserAlreadyExistException;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 21-jul-2006
 * @since 1.0
 *  
 */

public class Domain implements IDomain {

	private static final long serialVersionUID = 7839221957928230669L;

	/** Domain identifier. */
	private String id;

	/** Domain access policy. */
	private IAccessPolicy accessPolicy;

    /** Domain DataSource meta data. */
	private IDataSourceMetaData metadata;

	/** Applications that domain belongs to. */
	private Map<String, IDomainApplication> applications = new HashMap<String, IDomainApplication>();

    /** Domain stand-alone users. */
	private Map<String, IUser> standalone = new HashMap<String, IUser>();

    /**
     * Assigns domain identifier.
     * 
     * @param string
     */
	public void setId(String string) {
		this.id = string;
	}

	/**
	 * Añade una Aplicación en la que está registrado.
	 * 
	 * @param application
	 */
	public void add(IDomainApplication application) {
		if ( !this.applications.containsKey(application.getId()) ) {
			this.applications.put( application.getId(), application );
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INode#accept(com.code.aon.jaas.client.ast.INodeVisitor)
	 */
	public void accept(INodeVisitor visitor) {
		visitor.visitDomain(this);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INode#getId()
	 */
	public String getId() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDomain#setAccessPolicy(com.code.aon.jaas.client.ast.IAccessPolicy)
	 */
	public void setAccessPolicy(IAccessPolicy accessPolicy) {
		this.accessPolicy = accessPolicy;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDomain#getAccessPolicy()
	 */
	public IAccessPolicy getAccessPolicy() {
		return this.accessPolicy;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDomain#setDataSourceMetaData(com.code.aon.jaas.client.ast.IDataSourceMetaData)
	 */
	@Override
	public void setDataSourceMetaData(IDataSourceMetaData dsmt) {
		this.metadata = dsmt;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDomain#getDataSourceMetaData()
	 */
	public IDataSourceMetaData getDataSourceMetaData() {
		return metadata;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDomain#applications()
	 */
	public Collection<IDomainApplication> applications() {
		return Collections.unmodifiableCollection( this.applications.values() );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDomain#getDomainApplication(java.lang.String)
	 */
	public IDomainApplication getDomainApplication(String name) {
		return this.applications.get(name);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDomain#remove(com.code.aon.jaas.client.ast.IDomainApplication)
	 */
	public IDomainApplication remove(String appName) {
		return this.applications.remove(appName);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IEntity#standaloneUsers()
	 */
	public Map<String, IUser> standaloneUsers() {
        return Collections.unmodifiableMap(this.standalone);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IEntity#getStandaloneUser(java.lang.String)
	 */
	public IUser getStandaloneUser(String name) {
        return this.standalone.get(name);
	}

    /* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDomain#add(com.code.aon.jaas.client.ast.IUser)
	 */
	public void add(IUser user) throws UserAlreadyExistException {
        if ( this.standalone.containsKey(user.getId()) )
        	throw new UserAlreadyExistException( "aon_security_user_exist", user.getId() );

        this.standalone.put( user.getId(), user );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IEntity#update(com.code.aon.jaas.client.ast.IUser, java.lang.String)
	 */
	public IUser update(IUser user, String oldUserId) throws UserAlreadyExistException {
		if ( oldUserId == null ) {
			return this.standalone.put( user.getId(), user );
		}
        if ( this.standalone.containsKey(user.getId()) )
        	throw new UserAlreadyExistException( "aon_security_user_exist", user.getId() );

		Iterator<IDomainApplication> iter = this.applications.values().iterator();
		while (iter.hasNext()) {
			IDomainApplication app = iter.next();
			Relation relation = (Relation) app.getUser( oldUserId );
			if ( relation != null ) {
				app.removeUser( relation );
				relation.setId( user.getId() );
				app.updateUser( relation );
			}
		}
		User _user = (User) this.standalone.get( oldUserId );
		_user.setId( user.getId() );
		return _user;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IEntity#remove(com.code.aon.jaas.client.ast.IUser)
	 */
	public void remove(IUser user) {
		Iterator<IDomainApplication> iter = this.applications.values().iterator();
		while (iter.hasNext()) {
			IDomainApplication app = iter.next();
			Relation relation = new Relation( user.getId() );
			if ( relation != null ) {
				app.removeUser(relation);
			}
		}
    	this.standalone.remove( user.getId() );
	}

	/**
	 * Create a default Domain instance.
	 * 
	 * @param appName
	 * @return
	 */
    public static final Domain getInstance(String appName) {
    	Domain domain = new Domain();
    	domain.setId( IDomain.DEFAULT_DOMAIN_NAME );
    	AccessPolicy access = new AccessPolicy();
    	access.setId( IAccessPolicy.ACCESS_POLICY[0] );
    	domain.setAccessPolicy( access );
    	DomainApplication app = new DomainApplication();
    	app.setId( appName );
    	app.setDataSourceMetaData( new DataSourceMetaData() );
    	domain.add( app );
    	return domain;
    }
}
