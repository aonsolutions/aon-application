package com.code.aon.document;

import static com.code.aon.document.IAlfrescoConstants.ALFRESCO_ADMINISTRATORS;
import static com.code.aon.document.IAlfrescoConstants.GROUP_AUTHORITY_TYPE;
import static com.code.aon.document.IAlfrescoConstants.SCOPES_GROUP;
import static com.code.aon.document.IAlfrescoConstants.USER_AUTHORITY_TYPE;
import static org.alfresco.webservice.util.Constants.PROP_USER_FIRSTNAME;
import static org.alfresco.webservice.util.Constants.PROP_USER_HOMEFOLDER;
import static org.alfresco.webservice.util.Constants.PROP_USER_ORGID;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.alfresco.webservice.accesscontrol.ACE;
import org.alfresco.webservice.accesscontrol.ACL;
import org.alfresco.webservice.accesscontrol.AccessControlServiceSoapBindingStub;
import org.alfresco.webservice.accesscontrol.AccessStatus;
import org.alfresco.webservice.accesscontrol.NewAuthority;
import org.alfresco.webservice.accesscontrol.SiblingAuthorityFilter;
import org.alfresco.webservice.administration.AdministrationServiceSoapBindingStub;
import org.alfresco.webservice.administration.NewUserDetails;
import org.alfresco.webservice.administration.UserDetails;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.Utils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.EnterpriseUser;
import com.code.aon.document.dao.EnterpriseDocumentDAO;

/**
 * The Class LdapDAO.
 */
public class AlfrescoUserManager extends BasicAlfresco  {
	
	private Set<String> scopes = new TreeSet<String>();

	public AlfrescoUserManager( String user, String password ) throws DAOException {
		super( user, password ); 
		updateScopes(); 
	}

	private NamedValue[] getProperties( EnterpriseUser user ) {
		NamedValue[] values = new NamedValue[3];
		String home = STORE.getScheme() + "://" + STORE.getAddress() + "/";
		values[0] = Utils.createNamedValue(PROP_USER_HOMEFOLDER, home);
		values[1] = Utils.createNamedValue(PROP_USER_FIRSTNAME, user.getName());
		String id = String.valueOf( user.getEnterprise().getId() );
		values[2] = Utils.createNamedValue(PROP_USER_ORGID, id );
		return values;		
	}

	private NamedValue[] getUdateProperties( EnterpriseUser user ) {
		NamedValue[] values = new NamedValue[2];
		values[0] = Utils.createNamedValue(PROP_USER_FIRSTNAME, user.getName());
		String id = String.valueOf( user.getEnterprise().getId() );
		values[1] = Utils.createNamedValue(PROP_USER_ORGID, id );
		return values;		
	}
	
	public void addUserToGroup( String user, String group ) throws DAOException {
		try {
			startSession();
			String groupName = Constants.GROUP_PREFIX + group;
	        AccessControlServiceSoapBindingStub acs = getAccessControlService();
	        acs.addChildAuthorities(groupName, new String[]{user} );
		} catch ( Throwable e ) {
			throw new DAOException( "Error adding user " + user + " to group " + group, e );
		} finally {
			endSession();
		}				
	}

	public void removeUserFromGroup( String user, String group ) throws DAOException {
		try {
			startSession();
			String groupName = Constants.GROUP_PREFIX + group;
	        AccessControlServiceSoapBindingStub acs = getAccessControlService();
	        acs.removeChildAuthorities(groupName, new String[]{user} );
		} catch ( Throwable e ) {
			throw new DAOException( "Error removing user " + user + " from group " + group, e );
		} finally {
			endSession();
		}				
	}
	
	private void addUserToGroup( EnterpriseUser user ) throws DAOException {
		addUserToGroup( user.getLogin(), EnterpriseDocumentDAO.getName(user.getEnterprise()) );
	}	
	
	public void createUser( EnterpriseUser user ) throws DAOException {
		try {
			startSession();
			AdministrationServiceSoapBindingStub ad = getAdministrationService();
			NamedValue[] properties = getProperties(user);
            NewUserDetails dc[] = new NewUserDetails[]{new NewUserDetails(user.getLogin(), user.getPassword(), properties)};
            ad.createUsers(dc);  
		} catch ( Throwable e ) {
			throw new DAOException( "Error creating user " + user.getLogin(), e );
		} finally {
			endSession();
		}			
		addUserToGroup(user);
	}

	public void updateUser( EnterpriseUser user ) throws DAOException {
		try {
			startSession();
			AdministrationServiceSoapBindingStub ad = getAdministrationService();
			NamedValue[] properties = getUdateProperties(user);
            UserDetails uds[] = new UserDetails[]{new UserDetails(user.getLogin(), properties)};
            ad.updateUsers(uds);
		} catch ( Throwable e ) {
			throw new DAOException( "Error creating user " + user.getLogin(), e );
		} finally {
			endSession();
		}			
	}	
	
	public void deleteUser( String name ) throws DAOException {
		try {
			startSession();
			AdministrationServiceSoapBindingStub ad = getAdministrationService();
            ad.deleteUsers(new String[]{name});                			
		} catch ( Throwable e ) {
			throw new DAOException( "Error deleting user " + name, e );
		} finally {
			endSession();
		}					
	}

	/**
	 * Change password.
	 *
	 * @param user the user
	 * @param oldPassword Not needed if it changed by the administrator
	 * @param newPassword the new password
	 * @throws DAOException the dAO exception
	 */
	public void changePassword(String user, String oldPassword, String newPassword ) throws DAOException {
		try {
			startSession();
			AdministrationServiceSoapBindingStub ad = getAdministrationService();
            ad.changePassword(user, oldPassword, newPassword);                			
		} catch ( Throwable e ) {
			throw new DAOException( "Error in password change for user " + user, e );
		} finally {
			endSession();
		}					
	}
	
	public boolean userExists(String name) {
		boolean exists = false;
		try {
			UserDetails ud = getUser(name);
			exists = ( ud != null );
		} catch ( Throwable e ) {
			exists = false;
		}					
		return exists;
	}	
	
	private UserDetails getUser(String name) throws DAOException {
		UserDetails details = null;
		try {
			startSession();
			AdministrationServiceSoapBindingStub ad = getAdministrationService();
			details = ad.getUser(name);
		} catch ( Throwable e ) {
			throw new DAOException( "Error getting user " + name, e );
		} finally {
			endSession();
		}					
		return details;
	}

	public boolean isAlfrescoAdministrator( String user ) {
		return isUserInGroup(ALFRESCO_ADMINISTRATORS, user);
	}

	public boolean isUserInGroup( String group, String user ) {
		boolean inside = false;
		try {
			String[] users = getUsersInGroup(group);
			inside = ArrayUtils.contains(users, user);
		} catch ( Throwable e ) {
			inside = false;
		}					
		return inside;
	}
		
	public String[] getUsersInGroup( String group ) throws DAOException {
		String[] users = null;
		try {
			startSession();
	        AccessControlServiceSoapBindingStub acs = getAccessControlService();
	        SiblingAuthorityFilter saf = new SiblingAuthorityFilter();
	        saf.setAuthorityType(USER_AUTHORITY_TYPE);
	        users = acs.getChildAuthorities(Constants.GROUP_PREFIX + group, saf);			
		} catch ( Throwable e ) {
			throw new DAOException( "Error getting users of group " + group, e );
		} finally {
			endSession();
		}					
		return users;
	}	
	
	public List<String> getUserScopes( String name ) {
		List<String> list = new LinkedList<String>();
		for( String scope : scopes ) {
			if ( isUserInGroup(scope, name) ) {
				list.add(scope);
			}
		}
		return list;
	}

	public void setInheritPermission( Reference reference, boolean value ) throws DAOException {
		try {
			startSession();
			Predicate predicate = getPredicate(reference);  
	        getAccessControlService().setInheritPermission( predicate, value );
		} catch ( Throwable e ) {
			throw new DAOException( "Error setting inherit permission for " + reference.getPath(), e );
		} finally {
			endSession();
		}					
	}

	public void addGroupAccess( Reference reference, String group, String permission ) throws DAOException {
		try {
			startSession();
			Predicate predicate = getPredicate(reference);
			ACE ace = new ACE(Constants.GROUP_PREFIX + group, permission, AccessStatus.acepted);
	        getAccessControlService().addACEs(predicate, new ACE[]{ace});			
		} catch ( Throwable e ) {
			throw new DAOException( "Error adding group " + group + " access to " + reference.getPath(), e );
		} finally {
			endSession();
		}					
	}		

	public void removeGroupAccess( Reference reference, String group, String permission ) throws DAOException {
		try {
			startSession();
			Predicate predicate = getPredicate(reference);
			ACE ace = new ACE(Constants.GROUP_PREFIX + group, permission, AccessStatus.acepted);
			getAccessControlService().removeACEs(predicate, new ACE[]{ace});
		} catch ( Throwable e ) {
			throw new DAOException( "Error removing group " + group + " access to " + reference.getPath(), e );
		} finally {
			endSession();
		}					
	}	
	
	public void updateScopes() throws DAOException {
		this.scopes.clear();
		try {
			startSession();
	        AccessControlServiceSoapBindingStub acs = getAccessControlService();
	        SiblingAuthorityFilter saf = new SiblingAuthorityFilter();
	        saf.setAuthorityType(GROUP_AUTHORITY_TYPE);
	        String[] result = acs.getChildAuthorities(SCOPES_GROUP, saf);
	        if (! ArrayUtils.isEmpty(result) ) {
	        	for( String value : result ) {
	        		this.scopes.add( getGroupName(value) );
	        	}
	        }
		} catch ( Throwable e ) {
			throw new DAOException( "Error getting scopes", e );
		} finally {
			endSession();
		}					
	}	
	
	public Collection<String> getScopes() {
		return this.scopes;
	}	
	
	public void createGroup(String name, String parentAuthority) throws DAOException {
		try {
			startSession();
	        NewAuthority cpGrpAuth = new NewAuthority(GROUP_AUTHORITY_TYPE, name);
	        NewAuthority[] newAuthorities = {cpGrpAuth};
	        getAccessControlService().createAuthorities(parentAuthority, newAuthorities);
		} catch ( Throwable e ) {
			throw new DAOException( "Error creating group " + name + " in " + parentAuthority, e );			
		} finally {
			endSession();
		}
	}
	
	public void deleteGroup(String name, String parentAuthority) throws DAOException {
		try {
			startSession();
	        String[] authorities = new String[]{Constants.GROUP_PREFIX + name};
	        if ( parentAuthority != null ) {
		        getAccessControlService().removeChildAuthorities(parentAuthority, authorities);	
	        }
	        getAccessControlService().deleteAuthorities(authorities);
		} catch ( Throwable e ) {
			throw new DAOException( "Error deleting group " + name, e );
		} finally {
			endSession();
		}	
	}
	
	private String getGroupName( String value ) {
		return StringUtils.removeStart(value, Constants.GROUP_PREFIX);
	}
	
	public String getScope( Reference reference, String permission ) throws DAOException {
		try {
			startSession();
			Predicate predicate = getPredicate(reference);
			ACE ace = new ACE("", permission, AccessStatus.acepted);
	        ACL[] acls = getAccessControlService().getACLs(predicate, ace);
	        if (! ArrayUtils.isEmpty(acls) ) {
	        	for( ACL acl : acls ) {
	        		if (! ArrayUtils.isEmpty(acl.getAces()) ) {
		        		for( ACE _ace : acl.getAces() ) {
		        			String name = getGroupName(_ace.getAuthority());
		        			if ( scopes.contains(name) ) {
		        				return name;
		        			}
		        		}
	        		}
	        	}
	        }
		} catch ( Throwable e ) {
			throw new DAOException( "Error gettins groups for " + reference.getPath(), e );
		} finally {
			endSession();
		}					
		return null;
	}		

	public List<String> getUserScopes() throws DAOException {
		List<String> list = new LinkedList<String>();
		try {
			startSession();
	        String[] authorities = getAccessControlService().getAuthorities();
	        if (! ArrayUtils.isEmpty(authorities) ) {
	        	for( String authority : authorities ) {
	        		String name = getGroupName(authority);
	        		if ( this.scopes.contains(name) ) {
	        			list.add(name);
	        		}
	        	}
	        }
		} catch ( Throwable e ) {
			throw new DAOException( "Error gettins user scopes", e );
		} finally {
			endSession();
		}				
		return list;
	}		
	
}