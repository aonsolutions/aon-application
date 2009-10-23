package com.code.aon.dao.ldap;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.Name;
import javax.naming.ldap.Rdn;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.IDAO;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ldap.Scope;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Order;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;

/**
 * The Class LdapDAO.
 */
public class LdapDAO extends BasicLdap implements IDAO  {

	/**
	 * Obtain a suitable <code>Logger</code>.
	 */
	private static final Logger LOGGER = Logger.getLogger(LdapDAO.class.getName());

	private EntityMetadata metadata;
	
	private Name baseDN;

	/**
	 * Instantiates a new ldap dao.
	 * 
	 * @param pojoClass the pojo class
	 */
	public LdapDAO( Class<? extends ITransferObject> pojoClass ) {
		this.metadata = EntityMetadataManager.getMetadata(pojoClass);
		setBaseDN( this.metadata.getBaseDN() );
	}
	
	/**
	 * Instantiates a new ldap dao.
	 * 
	 * @param properties the properties
	 * @param pojoClass the pojo class
	 */
	public LdapDAO( Properties properties, Class<? extends ITransferObject> pojoClass ) {
		super( properties );
		this.metadata = EntityMetadataManager.getMetadata(pojoClass);
		setBaseDN( this.metadata.getBaseDN() );			
	}
	
	/**
	 * Gets the base dn.
	 * 
	 * @return the base dn
	 */
	public Name getBaseDN() {
		return baseDN;
	}

	/**
	 * Sets the base dn.
	 * 
	 * @param baseDN the new base dn
	 */
	public void setBaseDN(Name baseDN) {
		this.baseDN = baseDN; 
		try {
			Name base = getLdapSession().getBaseDN();
			this.baseDN = NameResolver.getName(baseDN, base);
		} catch ( LdapException e ) {
			LOGGER.info( "Error obtaining base DN of the LDAP connection" );
		} finally {
			closeSession();
		}
	}

	private void setDN( ITransferObject to, Name dn ) throws DAOException {
		try {
			PropertyUtils.setProperty( to, metadata.getDnHolder(), dn );
		} catch (Exception e) {
			throw new DAOException( e );
		}
	}
	
	private Name getDN( ITransferObject to ) throws DAOException {
		try {
			String name = BeanUtils.getProperty( to, metadata.getDnHolder() );
			return NameResolver.getName(name);
		} catch (Exception e) {
			throw new DAOException( e );
		}
	}
	
	/**
	 * Calculate dn.
	 * 
	 * @param to the to
	 * 
	 * @return the string
	 * 
	 * @throws DAOException the DAO exception
	 */
	public Name calculateDN( ITransferObject to ) throws DAOException {
		Name dn = this.baseDN;
		try {
			Object value = getValue(to, metadata.getRDN());
			if ( value != null ) {
				Rdn rdn = NameResolver.getRdn(metadata.getRDN().getLdapName(), value);
				dn = NameResolver.getName( rdn, this.baseDN );
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}		
		return dn;
	}

	/**
	 * Exists.
	 * 
	 * @param id the id
	 * 
	 * @return true, if successful
	 * 
	 * @throws DAOException the DAO exception
	 */
	public boolean exists( Name id ) throws DAOException {
		return exists( id, this.metadata.getMainObjectClass() );
	}
	
	@Override
	public int getCount(Criteria criteria) throws DAOException {
		int count = 0;
		try {
			Name dn = getDN(criteria);
			String filter = getFilter(criteria);
			LOGGER.info( "getCount, dn=" + dn + ",filter=" + filter );
			count = getLdapSession().getCount(dn, filter, Scope.ONELEVEL_SCOPE );
		} catch ( LdapException e ) {
			throw new DAOException( "Error getting count of " + metadata.getMainObjectClass(), e );
		} finally {
			closeSession();
		}
		return count;
	}

	public Class<?> getPOJOClass() {
		return metadata.getPojoClass();
	}
	
	@SuppressWarnings("unchecked")
	private IDAO getDAO( PropertyInfo info ) {
		LdapDAO dao = new LdapDAO( getProperties(), (Class<ITransferObject>) info.getPropertyClass() );
		if ( info.getBaseDN() != null ) {
			String baseDN = info.getBaseDN();
			if (! StringUtils.isEmpty(baseDN) ) {
				baseDN = baseDN.replace( "{this}", getBaseDN().toString() );
				if ( baseDN.indexOf("{parent}") != -1 ) {
					Name parent = NameResolver.getParent(getBaseDN());
					baseDN = baseDN.replace( "{parent}", parent.toString() );
				}
			}
			dao.setBaseDN(NameResolver.getName(baseDN));
		}
		return dao;
	}

	private Object getValue( ITransferObject to, PropertyInfo info ) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, DAOException {
		Object value = PropertyUtils.getProperty(to, info.getAccesPath());
		return getValue(value, info);
	}

	private Object getValue( Object value, PropertyInfo info ) throws DAOException {
		Object result = value; 
		if ( result != null ) {
			if ( info.isTransferObject() && (value instanceof ITransferObject) ) {
				IDAO dao = getDAO(info);
				result = dao.getId( (ITransferObject) result );
			}
			if ( result instanceof Boolean ) {
				result = ((Boolean) result).booleanValue() ? LdapSession.TRUE_VALUE : LdapSession.FALSE_VALUE;
			} else if (! (result instanceof byte[]) ) {
				result = result.toString();
			}
			if ( result instanceof String ) {
				result = StringUtils.trimToNull( (String) result );
			}
		}
		return result;
	}
	
	private void setProperties( ITransferObject to, Entry entry ) throws DAOException {
		for( PropertyInfo info : metadata.getMappings()) {
			try {
				Object value = getValue(to, info);
				if ( value != null ) {
					entry.put( info.getLdapName(), value );						
				}
			} catch (Exception e) {
				throw new DAOException( e );
			}
		}		
	}

	@Override
	public String getFieldName(String alias) throws DAOException {
		String field = metadata.getFieldMap().get(alias);
		return field;
	}
	
	@Override
	public ITransferObject insert(ITransferObject to) throws DAOException {
		try {
			Entry entry = new Entry( calculateDN(to) );
			entry.addObjectClass(metadata.getMainObjectClass());
			entry.addObjectClasses(metadata.getObjectClasses());
			setProperties(to, entry);
			getLdapSession().add(entry);
			setDN( to, entry.getDN() );
		} catch ( LdapException e ) {
			throw new DAOException( "Error in insert of " + metadata.getMainObjectClass(), e );
		} finally {
			closeSession();
		}
		return to;
	}
	
	@Override
	public boolean remove(ITransferObject to) throws DAOException {
		try {
			Name dn = getDN(to);
			getLdapSession().delete(dn);
		} catch ( LdapException e ) {
			throw new DAOException( "Error in remove of " + metadata.getMainObjectClass(), e );
		} finally {
			closeSession();
		}
		return true;
	}

	private Name getDN( Criteria criteria ) throws DAOException {
		return this.baseDN;
	}

	private String getFilter( Criteria criteria ) throws DAOException {
		String expression = NameResolver.getObjectClass(metadata.getMainObjectClass()); 
		if ( criteria != null ) {
			LdapRenderer renderer = new LdapRenderer();
			renderer.visitCriteria(criteria);
			String filter = renderer.getExpression();
			expression = NameResolver.getAndExpression( expression, filter );
		}
		return expression;
	}
	
	private List<Entry> sortList( List<Entry> list, Criteria criteria ) {
		if ( (criteria != null) && (criteria.getOrderByList() != null) ) {
			List<Order> orderList = criteria.getOrderByList().getOrders();
			for( int i = orderList.size()-1; i >= 0; i-- ) {
				Order order = orderList.get(i);
				String attribute = order.getExpression().getName();
				EntryComparator comparator = new EntryComparator( attribute, order.isAscending() );
				LOGGER.info( "Sorting " + order );
				Collections.sort( list, comparator );
			}
		}
		return list;
	}

	private List<Entry> getSubList( List<Entry> list, int offset, int count ) {
		if ( offset >= 0 ) {
			int toIndex = Math.min( offset+count, list.size() );
			LOGGER.info( "SubList, offset=" + offset + ",toIndex=" + toIndex );
			return list.subList( offset, toIndex );	
		}
		return list;
	}
	
	private void setProperty( ITransferObject to, PropertyInfo info, Entry entry ) throws InstantiationException, IllegalAccessException, InvocationTargetException, DAOException {
		if ( entry.containsKey(info.getLdapName()) ) {
			Object value = entry.getAsObject( info.getLdapName() );
			if ( value != null ) {
				if ( info.isTransferObject() ) {
					IDAO dao = getDAO(info);
					Name id = NameResolver.getName( value.toString() );
					value = dao.get( (Serializable) id );
				}
				BeanUtils.setProperty(to, info.getAccesPath(), value);						
			}
		}
	}
	
	private ITransferObject convert( Entry entry ) throws DAOException {
		ITransferObject to = null;
		try {
			to = metadata.getPojoClass().newInstance();
			for( PropertyInfo info : metadata.getMappings()) {
				setProperty(to, info, entry);
			}		
			setProperty(to, metadata.getRDN(), entry);
			setDN( to, entry.getDN() );
		} catch (Exception e) {
			throw new DAOException( "Error in converting to ITransferObject " + entry.getDN(), e );
		}
		return to;
	}
	
	private List<ITransferObject> convertList( List<Entry> list ) throws DAOException {
		List<ITransferObject> tos = new ArrayList<ITransferObject>();
		for( Entry entry : list ) {
			ITransferObject to = convert(entry);
			tos.add(to);
		}
		return tos;
	}
	
	@Override
	public List<ITransferObject> getList(Criteria criteria, int offset,
			int count) throws DAOException {
		List<ITransferObject> tos = null;
		try {
			Name dn = getDN(criteria);
			String filter = getFilter(criteria);
			LOGGER.info( "getList, dn=" + dn + ",filter=" + filter );
			List<Entry> list = getLdapSession().search(dn, filter, Scope.ONELEVEL_SCOPE );
			list = sortList(list, criteria);
			list = getSubList(list, offset, count);
			tos = convertList(list);
		} catch ( LdapException e ) {
			throw new DAOException( "Error in getList of " + metadata.getMainObjectClass(), e );
		} finally {
			closeSession();
		}
		return tos;
	}

	@Override
	public List<ITransferObject> getList(Criteria criteria) throws DAOException {
		return getList(criteria, -1, -1);
	}

	@Override
	public Serializable getId(ITransferObject to) throws DAOException {
		return getDN(to);
	}
	
	@Override
	public void setId(ITransferObject to, Serializable id) throws DAOException {
		setDN(to, (Name) id);
	}
	
	@Override
	public ITransferObject get(Serializable pk) throws DAOException {
		LOGGER.info( "Get: " + pk );
		ITransferObject to = null;
		Name dn = (Name) pk;
		if ( exists(dn, metadata.getMainObjectClass()) ) {
			Entry entry = get(dn, metadata.getMainObjectClass());
			if ( entry != null ) {
				to = convert(entry);
			} else {
				throw new DAOException( "Error in get of " + metadata.getMainObjectClass() );	
			}
		}
		return to;
	}
	
	@Override
	public ITransferObject update(ITransferObject to) throws DAOException {
		try {
			LdapSession session = getLdapSession();
			Name dn = getDN(to);
			String filter = NameResolver.getObjectClass(metadata.getMainObjectClass());
			Entry entry = session.get(dn, filter);
			for( PropertyInfo info : metadata.getMappings() ) {
				try {
					Object newValue = getValue(to, info);
					String name = info.getLdapName();
					Object oldValue = null;
					if ( entry.containsKey(name) ) {
						oldValue = getValue(entry.getAsObject(name), info);
					}
					session.updateAttribute(dn, name, oldValue, newValue);
				} catch (Exception e) {
					throw new DAOException( e );
				}
			}		
		} catch ( LdapException e ) {
			throw new DAOException( "Error in update of " + metadata.getMainObjectClass(), e );
		} finally {
			closeSession();
		}
		return to;
	}

	public ITransferObject insertOrUpdate(ITransferObject to)
			throws DAOException {
		Name dn = getDN(to);
		if ( dn == null ) {
			insert(to);
		} else {
			update(to);
		}
		return to;
	}

	public void setProperty(ITransferObject to, String propertyName,
			Object value) throws DAOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not supported!");
	}
	
	public List getList(ProjectionList projectionList, Criteria criteria)
			throws DAOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not supported!");
	}

	public Object getUniqueResult(Projection projection, Criteria criteria)
			throws DAOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not supported!");
	}

}
