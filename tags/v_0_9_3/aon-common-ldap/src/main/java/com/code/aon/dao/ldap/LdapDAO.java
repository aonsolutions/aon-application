package com.code.aon.dao.ldap;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.IDAO;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
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
	
	private String baseDN;

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
	
	public String getBaseDN() {
		return baseDN;
	}

	public void setBaseDN(String baseDN) {
		this.baseDN = baseDN; 
		try {
			String base = getLdapSession().getBaseDN();
			if (! StringUtils.isEmpty(base) ) {
				if ( StringUtils.isEmpty(this.baseDN) ) {
					this.baseDN = base;
				} else {
					this.baseDN = baseDN + "," + base;	
				}
			}
		} catch ( LdapException e ) {
			LOGGER.info( "Error obtaining base DN of the LDAP connection" );
		} finally {
			closeSession();
		}
	}

	private void setDN( ITransferObject to, DistinguishedName dn ) throws DAOException {
		try {
			BeanUtils.setProperty( to, metadata.getDnHolder(), dn.toString() );
		} catch (Exception e) {
			throw new DAOException( e );
		}
	}
	
	private String getDN( ITransferObject to ) throws DAOException {
		try {
			return BeanUtils.getProperty( to, metadata.getDnHolder() );
		} catch (Exception e) {
			throw new DAOException( e );
		}
	}
	
	public String calculateDN( ITransferObject to ) throws DAOException {
		StringBuffer dn = new StringBuffer( this.baseDN );
		try {
			Object value = getValue(to, metadata.getRDN());
			if ( value != null ) {
				dn.insert( 0, metadata.getRDN().getLdapName() + "=" + value + "," );
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}		
		return dn.toString();
	}

	public boolean exists( String id ) throws DAOException {
		return exists( id, this.metadata.getMainObjectClass() );
	}
	
	@Override
	public int getCount(Criteria criteria) throws DAOException {
		int count = 0;
		try {
			String dn = getDN(criteria);
			String filter = getFilter(criteria);
			LOGGER.info( "getCount, dn=" + dn + ",filter=" + filter );
			count = getLdapSession().getCount(dn.toString(), filter, Scope.SUBTREE_SCOPE );
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
	
	private IDAO getDAO( PropertyInfo info ) {
		LdapDAO dao = new LdapDAO( getProperties(), (Class<ITransferObject>) info.getPropertyClass() );
		if ( info.getBaseDN() != null ) {
			String baseDN = info.getBaseDN();
			if ( getBaseDN() != null ) {
				baseDN = baseDN.replace( "{this}", getBaseDN() );
				if ( baseDN.indexOf("{parent}") != -1 ) {
					DistinguishedName dn = new DistinguishedName( getBaseDN() );
					baseDN = baseDN.replace( "{parent}", dn.getParent().toString() );
				}
			}
			dao.setBaseDN(baseDN);
		}
		return dao;
	}

	private Object getValue( ITransferObject to, PropertyInfo info ) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, DAOException {
		Object value = PropertyUtils.getProperty(to, info.getAccesPath());
		if ( value != null ) {
			if ( info.isTransferObject() ) {
				IDAO dao = getDAO(info);
				value = dao.getId( (ITransferObject) value );
			} else if ( value instanceof Boolean ) {
				value = ((Boolean) value).booleanValue() ? LdapSession.TRUE_VALUE : LdapSession.FALSE_VALUE;
			} else if (! (value instanceof byte[]) ) {
				value = value.toString();
			}
			if ( value instanceof String ) {
				value = StringUtils.trimToNull( (String) value );
			}
		}
		return value;
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
			String dn = getDN(to);
			getLdapSession().delete(dn);
		} catch ( LdapException e ) {
			throw new DAOException( "Error in remove of " + metadata.getMainObjectClass(), e );
		} finally {
			closeSession();
		}
		return true;
	}

	private String getDN( Criteria criteria ) throws DAOException {
		StringBuffer dn = new StringBuffer( this.baseDN );
		if ( criteria != null ) {
		}
		return dn.toString();
	}

	private String getFilter( Criteria criteria ) throws DAOException {
		String expression = LdapSession.getObjectClass(metadata.getMainObjectClass()); 
		if ( criteria != null ) {
			LdapRenderer renderer = new LdapRenderer();
			renderer.visitCriteria(criteria);
			String filter = renderer.getExpression();
			expression = "(&" + expression + filter + ")";
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
					value = dao.get( (Serializable) value );
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
			String dn = getDN(criteria);
			String filter = getFilter(criteria);
			LOGGER.info( "getList, dn=" + dn + ",filter=" + filter );
			List<Entry> list = getLdapSession().search(dn.toString(), filter, Scope.SUBTREE_SCOPE );
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
		DistinguishedName dn = new DistinguishedName( (String) id );
		setDN(to, dn);
	}
	
	@Override
	public ITransferObject get(Serializable pk) throws DAOException {
		LOGGER.info( "Get: " + pk );
		ITransferObject to = null;
		try {
			LdapSession session = getLdapSession();
			String dn = (String) pk;
			String filter = LdapSession.getObjectClass(metadata.getMainObjectClass());
			if ( session.exists(dn, filter) ) {
				Entry entry = session.get(dn, filter);
				if ( entry != null ) {
					to = convert(entry);
				}
			}
		} catch ( LdapException e ) {
			throw new DAOException( "Error in get of " + metadata.getMainObjectClass(), e );
		} finally {
			closeSession();
		}
		return to;
	}
	
	@Override
	public ITransferObject update(ITransferObject to) throws DAOException {
		try {
			LdapSession session = getLdapSession();
			String dn = getDN(to);
			String filter = LdapSession.getObjectClass(metadata.getMainObjectClass());
			Entry entry = session.get(dn, filter);
			for( PropertyInfo info : metadata.getMappings() ) {
				try {
					Object value = getValue(to, info);
					String name = info.getLdapName();
					if ( entry.containsKey(name) ) {
						if ( value != null ) {
							Object entryValue = entry.getAsObject(name);
							if (! ObjectUtils.equals(value, entryValue) ) {
								session.replaceAttribute(dn, name, value);	
							}				
						} else {
							session.removeAttributes(dn, name);
						}						
					} else {
						if ( value != null ) {
							session.addAttribute(dn, name, value);						
						}						
					}
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
		String dn = getDN(to);
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
