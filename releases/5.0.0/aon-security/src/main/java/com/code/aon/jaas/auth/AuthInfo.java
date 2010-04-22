/*
 * Created on 25-feb-2005
 *
 */
package com.code.aon.jaas.auth;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IUser;

/**
 * Authentication and Authorization.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 25-feb-2005
 * @since 1.0
 *
 */
public class AuthInfo implements IAuthInfo {

	/** Security domain custom Domains. */
	Map<String, AuthDomain> domains = new LinkedHashMap<String, AuthDomain>();
	/** Application identifiers for each context relation. */
	Map<String, String> appId4Contexts = new LinkedHashMap<String, String>();

    /**
     * Authentication Constructor from an application list.
     * 
     * @param apps
     */
	public AuthInfo(Collection apps) {
		init(apps.iterator());
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.IAuthInfo#hasUser(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean hasUser(String domainName, String context, String name)
			throws AuthenticationLoginException {
		return getUserRelation(domainName, context, name) != null;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.IAuthInfo#getProfileRelation(java.lang.String, java.lang.String, java.lang.String)
	 */
    public IRelation getProfileRelation(String domainName, String context, String name) 
    		throws AuthenticationLoginException {
    	String appId = (String) appId4Contexts.get(context);
    	if ( !this.domains.containsKey( domainName ) )
    		throw new AuthenticationLoginException( "aon_login_err_2", domainName );

    	return this.domains.get(domainName).getProfileRelation(appId, name);
    }

    /* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.IAuthInfo#getUserRelation(java.lang.String, java.lang.String, java.lang.String)
	 */
    public IRelation getUserRelation(String domainName, String context, String name)
    		throws AuthenticationLoginException {
    	String appId = (String) appId4Contexts.get(context);
    	if ( !this.domains.containsKey( domainName ) )
    		throw new AuthenticationLoginException( "aon_login_err_2", domainName );
    	
        return this.domains.get(domainName).getUserRelation(appId, name);
    }

    /* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.IAuthInfo#getUserPassword(java.lang.String, java.lang.String)
	 */
    public String getUserPassword(String domainName, String name) 
    		throws AuthenticationLoginException {
    	if ( !this.domains.containsKey( domainName ) )
    		throw new AuthenticationLoginException( "aon_login_err_2", domainName );

    	IUser user = this.domains.get(domainName).getStandaloneUser(name);
        return user.getPasswd();
    }

    /* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.IAuthInfo#getAccessPolicy(java.lang.String)
	 */
    public IAccessPolicy getAccessPolicy(String domainName)
    		throws AuthenticationLoginException {
    	if ( !this.domains.containsKey( domainName ) )
    		throw new AuthenticationLoginException( "aon_login_err_2", domainName );

    	return this.domains.get(domainName).getAccessPolicy();
    }

    /**
     * Initialize Profiles and Users attributes that belongs to the list of domains inside each 
     * application. 
     * 
     * @param apps
     */
    private void init(Iterator apps) {
    	IApplication app = null;
    	while (apps.hasNext()) {
    		app = (IApplication) apps.next();
    		this.appId4Contexts.put( app.getContext(), app.getId() );
    		Iterator iter = app.domains().iterator();
    		while (iter.hasNext()) {
				IDomain domain = (IDomain) iter.next();
				AuthDomain authDomain = new AuthDomain( domain.getAccessPolicy() );
				if ( this.domains.containsKey( domain.getId() ) )
					authDomain = this.domains.get( domain.getId() );
				else 
					this.domains.put( domain.getId(), authDomain );
				Iterator dApps = domain.applications().iterator();
				while (dApps.hasNext()) {
					IDomainApplication element = (IDomainApplication) dApps.next();
					if ( element.getId().equals( app.getId() ) ) {
						Iterator profiles = element.profiles().iterator();
						while (profiles.hasNext()) {
							IRelation relation = (IRelation) profiles.next();
							authDomain.addProfile( element.getId(), relation );
						}
						Iterator users = element.users().iterator();
						while (users.hasNext()) {
							IRelation relation = (IRelation) users.next();
							authDomain.addUser( element.getId(), relation );
						}
					}
				}
				Iterator users = domain.standaloneUsers().values().iterator();
				while (users.hasNext()) {
					IUser user = (IUser) users.next();
					if ( authDomain.getStandaloneUser( user.getId() ) == null ) { 
						authDomain.addStandaloneUser(user);
					}
				}
    		}
		}
    }

    /**
     * This class manages Authentication and Authorization. 
     * 
     * @author Consulting & Development. Iñaki Ayerbe - 28-jul-2006
     * @since 1.0
     *
     */
    class AuthDomain {

    	/** Domain Access Policy */
    	IAccessPolicy accessPolicy;
    	/** Profiles and Roles bound to domain application. */
        private Map<String, Map<String, IRelation>> profiles = new LinkedHashMap<String, Map<String, IRelation>>();
    	/** Users and Profiles bound to domain application. */
        private Map<String, Map<String, IRelation>> users = new LinkedHashMap<String, Map<String, IRelation>>();
        /** Stand-Alone users for the domain. */
        private Map<String, IUser> standalone = new LinkedHashMap<String, IUser>();

        /**
         * Constructs an Authentication and Authorization domain with an access policy control.
         * 
         * @param accessPolicy
         */
        public AuthDomain(IAccessPolicy accessPolicy) {
        	this.accessPolicy = accessPolicy;
        }

		/**
		 * @return the accessPolicy
		 */
		public IAccessPolicy getAccessPolicy() {
			return accessPolicy;
		}

        /**
         * Add Profile relations to the domain application, if Profile identifier exits then adds all its 
         * Roles to that Profile relations.   
         * 
         * @param appId
         * @param relation
         */
		public void addProfile(String appId, IRelation relation) {
			Map<String, IRelation> _profiles = profiles.get(appId);
			if ( _profiles == null ) {
    			profiles.put( appId, _profiles = new HashMap<String, IRelation>() );
    		}
			if ( _profiles.containsKey(relation.getId()) ) {
    			IRelation profile = _profiles.get(relation.getId());
				profile.relations().addAll(relation.relations());
			} else {
				_profiles.put(relation.getId(), relation);
			}
		}

        /**
         * Return the Profile relations bound to domain application. 
         * 
         * @param appId
         * @param name
         * @return
         */
		public IRelation getProfileRelation(String appId, String name) {
			return profiles.get(appId).get(name);
		}

        /**
         * Add User relations to the domain application, if User identifier exits then adds all its 
         * Profiles to that User relations.   
         * 
         * @param appId
         * @param relation
         */
		public void addUser(String appId, IRelation relation) {
			Map<String, IRelation> _users = users.get(appId);
			if ( _users == null ) {
				users.put( appId, _users = new HashMap<String, IRelation>() );
			}
			if ( _users.containsKey(relation.getId()) ) {
				IRelation user = _users.get( relation.getId() );
				user.relations().addAll( relation.relations() );
			} else {
				_users.put( relation.getId(), relation) ;
			}
		}

        /**
         * Return the User relations bound to domain application. 
         * 
         * @param appId
         * @param name
         * @return
         */
		public IRelation getUserRelation(String appId, String name) {
			return users.get(appId).get(name);
		}

        /**
         * Add a standalone user.
         *  
         * @param user
         */
        public void addStandaloneUser(IUser user) {
			standalone.put(user.getId(), user);
        }

        /**
         * Return the <code>IUser</code>.
         * 
         * @param name
         * @return
         */
        public IUser getStandaloneUser(String name) {
        	return standalone.get(name);
        }

    }

}
