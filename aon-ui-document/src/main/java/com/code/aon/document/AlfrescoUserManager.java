package com.code.aon.document;

import static com.code.aon.document.IAlfrescoConstants.ALFRESCO_ADMINISTRATORS;
import static com.code.aon.document.IAlfrescoConstants.GROUP_AUTHORITY_TYPE;
import static com.code.aon.document.IAlfrescoConstants.USER_AUTHORITY_TYPE;
import static org.alfresco.webservice.util.Constants.PROP_USER_FIRSTNAME;
import static org.alfresco.webservice.util.Constants.PROP_USER_HOMEFOLDER;
import static org.alfresco.webservice.util.Constants.PROP_USER_ORGID;

import org.alfresco.webservice.accesscontrol.ACE;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.EnterpriseUser;
import com.code.aon.document.dao.EnterpriseDocumentDAO;


/**
 * The Class LdapDAO.
 */
public class AlfrescoUserManager extends BasicAlfresco  {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoUserManager.class);

	public AlfrescoUserManager( String user, String password ) {
		super( user, password ); 
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
	        String[] result = acs.addChildAuthorities(groupName, new String[]{user} );
	        LOGGER.info( "Result {}", ArrayUtils.toString(result));
		} catch ( Throwable e ) {
			throw new DAOException( "Error adding user " + user + " to group " + group, e );
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
	
	public void createGroup( String name ) throws DAOException {
		try {
			startSession();
	        NewAuthority cpGrpAuth = new NewAuthority(GROUP_AUTHORITY_TYPE, name);
	        NewAuthority[] newAuthorities = {cpGrpAuth};
	        getAccessControlService().createAuthorities(null, newAuthorities);
		} catch ( Throwable e ) {
			throw new DAOException( "Error creating group " + name, e );
		} finally {
			endSession();
		}					
	}
	
	public void deleteGroup(String name) throws DAOException {
		try {
			startSession();
	        String groupName = Constants.GROUP_PREFIX + name;
	        getAccessControlService().deleteAuthorities(new String[]{groupName});
		} catch ( Throwable e ) {
			throw new DAOException( "Error deleting group " + name, e );
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
			if ( details != null ) {
		    	for( NamedValue nv : details.getProperties() ) {
		    		LOGGER.info( "Name: {}, Value: {}", nv.getName(), nv.getIsMultiValue() ? nv.getValues() : nv.getValue() );
		    	}				
			}
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

	public String[] addGroupAccess( Reference reference, String group, String permission ) throws DAOException {
		String[] users = null;
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
		return users;
	}		
	
}