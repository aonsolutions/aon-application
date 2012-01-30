package com.code.aon.document.dao;

import static com.code.aon.document.IAlfrescoConstants.ALFRESCO_ADMINISTRATORS;
import static com.code.aon.document.IAlfrescoConstants.EMAIL_CONTRIBUTORS;
import static com.code.aon.document.IAlfrescoConstants.ENTERPRISE_PREFFIX;
import static com.code.aon.document.IAlfrescoConstants.GROUP_AUTHORITY_TYPE;
import static com.code.aon.document.IAlfrescoConstants.INSERT_ERROR;
import static com.code.aon.document.IAlfrescoConstants.REMOVE_ERROR;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.alfresco.webservice.accesscontrol.AuthorityFilter;
import org.alfresco.webservice.accesscontrol.NewAuthority;
import org.alfresco.webservice.repository.RepositoryFault;
import org.alfresco.webservice.util.Constants;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.IDAO;
import com.code.aon.common.dao.hibernate.ReplicationMode;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.document.AlfrescoGroup;
import com.code.aon.document.BasicAlfresco;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;

/**
 * The Class LdapDAO.
 */
public class AlfrescoGroupDAO extends BasicAlfresco implements IDAO  {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoGroupDAO.class);
	
	private ResourceBundle bundle;
	
	public AlfrescoGroupDAO( String user, String password ) {
		super( user, password );
		this.bundle = ResourceBundle.getBundle(AlfrescoDAO.BASE_NAME);
	}

	@Override
	public Serializable getId(ITransferObject to) throws DAOException {
		AlfrescoGroup ag = (AlfrescoGroup) to;
		return ag.getName();
	}
	
	private String[] getGroups() throws DAOException {
		String[] groups = null;
		try {
			startSession();
			AuthorityFilter filter = new AuthorityFilter();
			filter.setAuthorityType(GROUP_AUTHORITY_TYPE);
			filter.setRootOnly(true);
	        groups = getAccessControlService().getAllAuthorities(filter);			
		} catch ( Throwable e ) {
			throw new DAOException( "Error getting user groups", e );
		} finally {
			endSession();
		}					
		return groups;
	}		
	
	private List<String> filterGroups( String[] groups ) {
		List<String> list = new LinkedList<String>();
		for( String group : groups ) {
			String name = StringUtils.removeStart(group, Constants.GROUP_PREFIX);
			if (! (StringUtils.startsWith(name, ALFRESCO_ADMINISTRATORS) || 
				StringUtils.startsWith(name, EMAIL_CONTRIBUTORS) ||
				StringUtils.startsWith(name, ENTERPRISE_PREFFIX)) ) {
				list.add(name);
			}
		}
		return list;
	}
	
	@Override
	public int getCount(Criteria criteria) throws DAOException {
		int count = 0;
		try {
			startSession();
			String[] groups = getGroups();
			if (! ArrayUtils.isEmpty(groups) ) {
				count = filterGroups(groups).size();
			}			
		} catch ( Throwable e ) {
			String message = getErrorMessage( "Error getting count", e );
			throw new DAOException( message, e );
		} finally {
			endSession();
		}
		return count;		
	}

	@Override
	public String getFieldName(String alias) throws DAOException {
		return StringUtils.replace(alias, "_", ":" );
	}
	
	@Override
	public ITransferObject insert(ITransferObject to) throws DAOException {
		try {
			startSession();
			String name = (String) getId(to);
	        NewAuthority cpGrpAuth = new NewAuthority(GROUP_AUTHORITY_TYPE, name);
	        NewAuthority[] newAuthorities = {cpGrpAuth};
	        getAccessControlService().createAuthorities(null, newAuthorities);			
		} catch ( Throwable e ) {
			String message = getErrorMessage(to, e, INSERT_ERROR);
			throw new DAOException( message, e );			
		} finally {
			endSession();
		}
		return to;
	}
	
	@Override
	public boolean remove(ITransferObject to) throws DAOException {
		boolean removed = false;
		try {
			startSession();
			String name = (String) getId(to);
	        String groupName = Constants.GROUP_PREFIX + name;
	        getAccessControlService().deleteAuthorities(new String[]{groupName});
			removed = true;
		} catch ( Throwable e ) {
			String message = getErrorMessage(to, e, REMOVE_ERROR);
			throw new DAOException( message, e );
		} finally {
			endSession();
		}	
		return removed;
	}
	
	@Override
	public List<ITransferObject> getList(Criteria criteria, int offset,
			int count) throws DAOException {
		List<ITransferObject> tos = new LinkedList<ITransferObject>();
		try {
			startSession();
			String[] groups = getGroups();
			if (! ArrayUtils.isEmpty(groups) ) {
				for( String name : filterGroups(groups) ) {
					tos.add(new AlfrescoGroup(name));
				}
			}
		} catch ( Throwable e ) {
			throw new DAOException( "Error in getList", e );
		} finally {
			endSession();
		}
		return tos;			
	}

	@Override
	public List<ITransferObject> getList(Criteria criteria) throws DAOException {
		return getList(criteria, -1, -1);
	}
	
	@Override
	public void setId(ITransferObject to, Serializable id) throws DAOException {
		throw new UnsupportedOperationException("Not supported!");		
	}
	
	@Override
	public ITransferObject get(Serializable pk) throws DAOException {
		throw new UnsupportedOperationException("Not supported!");
	}
	
	@Override
	public ITransferObject update(ITransferObject to) throws DAOException {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public ITransferObject insertOrUpdate(ITransferObject to)
			throws DAOException {
		throw new UnsupportedOperationException("Not supported!");		
	}

	@Override
	public ITransferObject replicate(ITransferObject to, ReplicationMode mode)
			throws DAOException {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void setProperty(ITransferObject to, String propertyName,
			Object value) throws DAOException {
		throw new UnsupportedOperationException("Not supported!");
	}
	
	@Override
	public List<?> getList(ProjectionList projectionList, Criteria criteria)
			throws DAOException {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public Object getUniqueResult(Projection projection, Criteria criteria)
			throws DAOException {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public Class<? extends ITransferObject> getPOJOClass() {
		return AlfrescoGroup.class;
	}

	private String getErrorMessage( ITransferObject to, Throwable t, String key  ) {
		String name = ((AlfrescoGroup)to).getName();
		String errorMessage = t.getMessage();
		if ( StringUtils.isBlank(errorMessage) ) {
			if ( t instanceof RepositoryFault ) {
				errorMessage = StringUtils.substringAfter( ((RepositoryFault) t).getMessage1(), ": " );
			}
		}
		return MessageFormat.format(bundle.getString(key), name, errorMessage);
	}

	private String getErrorMessage( String message, Throwable t ) {
		String errorMessage = t.getMessage();
		if ( StringUtils.isBlank(errorMessage) ) {
			if ( t instanceof RepositoryFault ) {
				errorMessage = StringUtils.substringAfterLast( ((RepositoryFault) t).getMessage1(), ": " );
			}
		}
		return message + ". " + errorMessage;
	}

	@Override
	public ITransferObject newTo() throws DAOException {
		return new AlfrescoGroup();
	}
	
}
