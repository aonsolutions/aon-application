package com.code.aon.document.dao;

import static com.code.aon.document.IAlfrescoConstants.SCOPES_GROUP;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.IDAO;
import com.code.aon.common.dao.hibernate.ReplicationMode;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.document.AlfrescoGroup;
import com.code.aon.document.AlfrescoUserManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;

/**
 * The Class LdapDAO.
 */
public class AlfrescoScopeDAO implements IDAO  {

	private AlfrescoUserManager userManager;
	
	public AlfrescoScopeDAO( AlfrescoUserManager userManager ) {
		this.userManager = userManager;
	}

	@Override
	public Serializable getId(ITransferObject to) throws DAOException {
		AlfrescoGroup ag = (AlfrescoGroup) to;
		return ag.getName();
	}
	
	@Override
	public int getCount(Criteria criteria) throws DAOException {
		return userManager.getScopes().size();
	}

	@Override
	public String getFieldName(String alias) throws DAOException {
		return StringUtils.replace(alias, "_", ":" );
	}
	
	@Override
	public ITransferObject insert(ITransferObject to) throws DAOException {
		String name = (String) getId(to);
		this.userManager.createGroup(name, SCOPES_GROUP);
		this.userManager.updateScopes();
		return to;
	}
	
	@Override
	public boolean remove(ITransferObject to) throws DAOException {
		String name = (String) getId(to);
		this.userManager.deleteGroup(name, SCOPES_GROUP);
		this.userManager.updateScopes();
		return true;
	}
	
	@Override
	public List<ITransferObject> getList(Criteria criteria, int offset,
			int count) throws DAOException {
		List<ITransferObject> tos = new LinkedList<ITransferObject>();
		Collection<String> scopes = userManager.getScopes();
		if (! scopes.isEmpty() ) {
			for( String name : scopes ) {
				tos.add(new AlfrescoGroup(name));
			}
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
	public Object getUniqueResult(ProjectionList projectionList,
			Criteria criteria) throws DAOException {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public Class<? extends ITransferObject> getPOJOClass() {
		return AlfrescoGroup.class;
	}

	@Override
	public ITransferObject newTo() throws DAOException {
		return new AlfrescoGroup();
	}
	
}
