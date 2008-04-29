package com.code.aon.dao.ldap;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Id;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.IDAO;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.DNModifier;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;
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
public class LdapDAO implements IDAO  {

	/**
	 * Obtain a suitable <code>Logger</code>.
	 */
	private static final Logger LOGGER = Logger.getLogger(LdapDAO.class.getName());

	private Properties properties;
	
	private Class<? extends ITransferObject> pojoClass;
	
	private PropertyInfo rdn;
	
	private String dnHolder;
	
	private Map<String,String> fieldMap;
	
	private List<PropertyInfo> mappings;
	
	private List<DNModifier> dnModifiers;
	
	private String mainObjectClass;
	
	private String[] objectClasses;
	
	private String baseDN;
	
	/**
	 * Instantiates a new ldap dao.
	 * 
	 * @param properties the properties
	 * @param pojoClass the pojo class
	 */
	public LdapDAO( Properties properties, Class<? extends ITransferObject> pojoClass ) {
		this.properties = properties;
		this.pojoClass = pojoClass;
		if ( this.pojoClass.isAnnotationPresent(EntryObject.class) ) {
			EntryObject entity = this.pojoClass.getAnnotation(EntryObject.class);
			this.baseDN = entity.baseDN();
			this.mainObjectClass = entity.mainObjectClass();
			this.objectClasses = entity.objectClasses();
		} else {
			throw new IllegalArgumentException( "EntryObject annotation is mandatory" );
		}
		resolveMetaInfo();
	}
	
	private String getAlias( String accessPath ) {
		String preffix = ClassUtils.getShortClassName(this.pojoClass) + "_";
		return preffix + accessPath.replace('.', '_');		
	}
	
	private PropertyInfo getPropertyInfo( Attribute attribute, PropertyDescriptor pd ) {
		String accessPath = StringUtils.defaultIfEmpty(attribute.accessPath(), pd.getName());
		String ldapName = StringUtils.defaultIfEmpty(attribute.name(), pd.getName());
		PropertyInfo info = new PropertyInfo( accessPath, ldapName );
		info.setPropertyClass( pd.getPropertyType() );
		info.setLength( attribute.length() );
		info.setNullable( attribute.nullable() );
		info.setAlias( getAlias(accessPath) );
		return info;
	}
	
	private void resolveMetaInfo() {
		this.fieldMap = new HashMap<String, String>();
		this.mappings = new ArrayList<PropertyInfo>();
		this.dnModifiers = new ArrayList<DNModifier>();
		PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(this.pojoClass);
		for (PropertyDescriptor pd : pds) {
			Method method = pd.getReadMethod();
			if ( method != null ) {
				if ( method.isAnnotationPresent(DNModifier.class) ) {
					Attribute attribute = method.getAnnotation(Attribute.class);
					PropertyInfo info = getPropertyInfo(attribute, pd);
					this.mappings.add(info);
					DNModifier dnModifier = pd.getReadMethod().getAnnotation(DNModifier.class);
					dnModifiers.add( dnModifier.order(), dnModifier );
					this.fieldMap.put( info.getAlias(), info.getLdapName() );
				} else if ( method.isAnnotationPresent(RDN.class) ) {
					Attribute attribute = method.getAnnotation(Attribute.class);
					this.rdn = getPropertyInfo(attribute, pd);
					this.fieldMap.put( rdn.getAlias(), rdn.getLdapName() );
				} else if ( method.isAnnotationPresent(Attribute.class) ) {
					Attribute attribute = method.getAnnotation(Attribute.class);
					PropertyInfo info = getPropertyInfo(attribute, pd);
					this.mappings.add(info);
					this.fieldMap.put( info.getAlias(), info.getLdapName() );
				} else if ( method.isAnnotationPresent(Id.class) ) {
					dnHolder = pd.getName();
					this.fieldMap.put( getAlias(dnHolder), dnHolder );
				}
			}
		}		
		if ( this.rdn == null ) {
			throw new IllegalArgumentException( RDN.class + " annotation is mandatory" );
		}
		if ( this.dnHolder == null ) {
			throw new IllegalArgumentException( Id.class + " annotation is mandatory" );
		}
	}
	
	public String getBaseDN() {
		return baseDN;
	}

	public void setBaseDN(String baseDN) {
		this.baseDN = baseDN;
	}

	private LdapSession getLdapSession() throws LdapException {
		LdapSession session = new LdapSession();
		session.open(this.properties);
		return session;
	}
	
	private void closeSession( LdapSession session ) {
		try {
			if ( session != null ) {
				session.close();
			}
		} catch (LdapException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}
	}
	
	private String replaceValues( ITransferObject to, String string ) throws DAOException {
		StringBuffer result = new StringBuffer( string );
		int end = result.indexOf("}");
		while ( end != -1 ) {
			int start = result.lastIndexOf("{", end);
			if ( start != -1 ) {
				String expression = result.substring(start+1, end);
				result.delete(start, end+1);
				try {
					Object value = PropertyUtils.getProperty(to, expression);
					if ( value != null ) {
						result.insert( start, value.toString() );
					}
				} catch (Exception e) {
					throw new DAOException( e );
				}
			}
			end = result.lastIndexOf("}", Math.max(start, end-1));
		}
		return result.toString();
	}

	private void setDN( ITransferObject to, DistinguishedName dn ) throws DAOException {
		try {
			BeanUtils.setProperty( to, this.dnHolder, dn.toString() );
		} catch (Exception e) {
			throw new DAOException( e );
		}
	}
	
	private String getDN( ITransferObject to ) throws DAOException {
		try {
			return BeanUtils.getProperty( to, this.dnHolder );
		} catch (Exception e) {
			throw new DAOException( e );
		}
	}
	
	private String calculateDN( ITransferObject to ) throws DAOException {
		StringBuffer dn = new StringBuffer( this.baseDN );
		for( DNModifier dnModifier : this.dnModifiers ) {
			String value = dnModifier.dn();
			String modifiedValue = replaceValues(to, value);
			dn.insert(0, modifiedValue + "," );
		}
		try {
			Object value = getValue(to, rdn);
			if ( value != null ) {
				dn.insert( 0, rdn.getLdapName() + "=" + value + "," );
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}		
		return dn.toString();
	}
	
	@Override
	public int getCount(Criteria criteria) throws DAOException {
		LdapSession session = null;
		int count = 0;
		try {
			session = getLdapSession();
			String dn = getDN(criteria);
			String filter = getFilter(criteria);
			count = session.getCount(dn.toString(), filter, Scope.SUBTREE_SCOPE );
		} catch ( LdapException e ) {
			throw new DAOException( "Error getting count of " + mainObjectClass, e );
		} finally {
			closeSession(session);
		}
		return count;
	}

	public Class<?> getPOJOClass() {
		return this.pojoClass;
	}

	private Object getValue( ITransferObject to, PropertyInfo info ) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object value = PropertyUtils.getProperty(to, info.getAccesPath());
		if ( value != null ) {
			if (! (value instanceof byte[]) ) {
				value = value.toString();
			}
			if ( value instanceof String ) {
				value = StringUtils.trimToNull( (String) value );
			}
		}
		return value;
	}
	
	private void setProperties( ITransferObject to, Entry entry ) throws DAOException {
		for( PropertyInfo info : this.mappings ) {
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
		String field = this.fieldMap.get(alias);
		return field;
	}
	
	@Override
	public ITransferObject insert(ITransferObject to) throws DAOException {
		LdapSession session = null;
		try {
			session = getLdapSession();
			Entry entry = new Entry( calculateDN(to) );
			entry.addObjectClass(mainObjectClass);
			entry.addObjectClasses(objectClasses);
			setProperties(to, entry);
			session.add(entry);
			setDN( to, entry.getDN() );
		} catch ( LdapException e ) {
			throw new DAOException( "Error in insert of " + mainObjectClass, e );
		} finally {
			closeSession(session);
		}
		return to;
	}
	
	@Override
	public boolean remove(ITransferObject to) throws DAOException {
		LdapSession session = null;
		try {
			session = getLdapSession();
			String dn = getDN(to);
			session.delete(dn);
		} catch ( LdapException e ) {
			throw new DAOException( "Error in remove of " + mainObjectClass, e );
		} finally {
			closeSession(session);
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
		String expression = LdapSession.getObjectClass(this.mainObjectClass); 
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
	
	private void setProperty( ITransferObject to, PropertyInfo info, Entry entry ) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		if ( entry.containsKey(info.getLdapName()) ) {
			Object value = entry.getAsObject( info.getLdapName() );
			if ( value != null ) {
				if ( info.isTransferObject() ) {
					Object innerTo = info.getPropertyClass().newInstance();
					BeanUtils.setProperty(to, info.getToAccessPath(), innerTo);
				}
				BeanUtils.setProperty(to, info.getAccesPath(), value);						
			}
		}
	}
	
	private ITransferObject convert( Entry entry ) throws DAOException {
		ITransferObject to = null;
		try {
			to = this.pojoClass.newInstance();
			for( PropertyInfo info : this.mappings ) {
				setProperty(to, info, entry);
			}		
			setProperty(to, rdn, entry);
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
		LdapSession session = null;
		List<ITransferObject> tos = null;
		try {
			session = getLdapSession();
			String dn = getDN(criteria);
			String filter = getFilter(criteria);
			LOGGER.info( "getList, dn=" + dn + ",filter=" + filter );
			List<Entry> list = session.search(dn.toString(), filter, Scope.SUBTREE_SCOPE );
			list = sortList(list, criteria);
			list = getSubList(list, offset, count);
			tos = convertList(list);
		} catch ( LdapException e ) {
			throw new DAOException( "Error in getList of " + mainObjectClass, e );
		} finally {
			closeSession(session);
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
		LdapSession session = null;
		ITransferObject to = null;
		try {
			session = getLdapSession();
			String dn = (String) pk;
			String filter = LdapSession.getObjectClass(this.mainObjectClass);
			Entry entry = session.get(dn, filter);
			if ( entry != null ) {
				to = convert(entry);
			}
		} catch ( LdapException e ) {
			throw new DAOException( "Error in get of " + mainObjectClass, e );
		} finally {
			closeSession(session);
		}
		return to;
	}
	
	@Override
	public ITransferObject update(ITransferObject to) throws DAOException {
		LdapSession session = null;
		try {
			session = getLdapSession();
			String dn = getDN(to);
			String filter = LdapSession.getObjectClass(this.mainObjectClass);
			Entry entry = session.get(dn, filter);
			for( PropertyInfo info : this.mappings ) {
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
			throw new DAOException( "Error in update of " + mainObjectClass, e );
		} finally {
			closeSession(session);
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
