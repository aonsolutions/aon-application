package com.code.aon.dao.ldap;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Name;
import javax.persistence.Id;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.BaseDN;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;
import com.code.aon.ldap.NameResolver;

/**
 * The Class EntityMetadata.
 */
public class EntityMetadata {

	private Class<? extends ITransferObject> pojoClass;
	
	private PropertyInfo rdn;
	
	private String dnHolder;
	
	private Map<String,String> fieldMap;
	
	private List<PropertyInfo> mappings;
	
	private String mainObjectClass;
	
	private String[] objectClasses;
	
	private Name baseDN;

	/**
	 * Instantiates a new Entity Metadata.
	 * 
	 * @param pojoClass the pojo class
	 */
	public EntityMetadata( Class<? extends ITransferObject> pojoClass ) {
		this.pojoClass = pojoClass;
		if ( this.pojoClass.isAnnotationPresent(EntryObject.class) ) {
			EntryObject entity = this.pojoClass.getAnnotation(EntryObject.class);
			this.baseDN = NameResolver.getName(entity.baseDN());
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
	
	private PropertyInfo getPropertyInfo( Method method, PropertyDescriptor pd ) {
		Attribute attribute = method.getAnnotation(Attribute.class);
		String accessPath = StringUtils.defaultIfEmpty(attribute.accessPath(), pd.getName());
		String ldapName = StringUtils.defaultIfEmpty(attribute.name(), pd.getName());
		PropertyInfo info = new PropertyInfo( accessPath, ldapName, pd.getPropertyType() );
		info.setLength( attribute.length() );
		info.setNullable( attribute.nullable() );
		info.setAlias( getAlias(accessPath) );
		if (! StringUtils.isEmpty(attribute.baseClass()) ) {
			try {
				Class<?> baseClass = ClassUtils.getClass(attribute.baseClass());
				info.setBaseClass( baseClass );
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException( e );
			}			
		}
		if ( method.isAnnotationPresent(BaseDN.class) ) {
			String name = method.getAnnotation(BaseDN.class).value();
			info.setBaseDN( name );
		}
		return info;
	}
	
	private void resolveMetaInfo() {
		this.fieldMap = new HashMap<String, String>();
		this.mappings = new ArrayList<PropertyInfo>();
		PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(this.pojoClass);
		for (PropertyDescriptor pd : pds) {
			Method method = pd.getReadMethod();
			if ( method != null ) {
				if ( method.isAnnotationPresent(RDN.class) ) {
					this.rdn = getPropertyInfo(method, pd);
					this.fieldMap.put( rdn.getAlias(), rdn.getLdapName() );
				} else if ( method.isAnnotationPresent(Attribute.class) ) {
					PropertyInfo info = getPropertyInfo(method, pd);
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

	/**
	 * Gets the pojo class.
	 * 
	 * @return the pojo class
	 */
	public Class<? extends ITransferObject> getPojoClass() {
		return pojoClass;
	}

	/**
	 * Gets the rDN.
	 * 
	 * @return the rDN
	 */
	public PropertyInfo getRDN() {
		return rdn;
	}

	/**
	 * Gets the dn holder.
	 * 
	 * @return the dn holder
	 */
	public String getDnHolder() {
		return dnHolder;
	}

	/**
	 * Gets the field map.
	 * 
	 * @return the field map
	 */
	public Map<String, String> getFieldMap() {
		return fieldMap;
	}

	/**
	 * Gets the mappings.
	 * 
	 * @return the mappings
	 */
	public List<PropertyInfo> getMappings() {
		return mappings;
	}

	/**
	 * Gets the main object class.
	 * 
	 * @return the main object class
	 */
	public String getMainObjectClass() {
		return mainObjectClass;
	}

	/**
	 * Gets the object classes.
	 * 
	 * @return the object classes
	 */
	public String[] getObjectClasses() {
		return objectClasses;
	}

	/**
	 * Gets the base dn.
	 * 
	 * @return the base dn
	 */
	public Name getBaseDN() {
		return baseDN;
	}
	
}
