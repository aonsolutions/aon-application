package com.code.aon.dao.ldap;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.Id;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.BaseDN;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;

public class EntityMetadata {

	private Class<? extends ITransferObject> pojoClass;
	
	private Properties ldapProperties;
	
	private PropertyInfo rdn;
	
	private String dnHolder;
	
	private Map<String,String> fieldMap;
	
	private List<PropertyInfo> mappings;
	
	private String mainObjectClass;
	
	private String[] objectClasses;
	
	private String baseDN;

	/**
	 * Instantiates a new Entity Metadata.
	 * 
	 * @param pojoClass the pojo class
	 */
	public EntityMetadata( Class<? extends ITransferObject> pojoClass ) {
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
	
	private PropertyInfo getPropertyInfo( Method method, PropertyDescriptor pd ) {
		Attribute attribute = method.getAnnotation(Attribute.class);
		String accessPath = StringUtils.defaultIfEmpty(attribute.accessPath(), pd.getName());
		String ldapName = StringUtils.defaultIfEmpty(attribute.name(), pd.getName());
		PropertyInfo info = new PropertyInfo( accessPath, ldapName );
		info.setPropertyClass( pd.getPropertyType() );
		info.setLength( attribute.length() );
		info.setNullable( attribute.nullable() );
		info.setAlias( getAlias(accessPath) );
		if ( method.isAnnotationPresent(BaseDN.class) ) {
			info.setBaseDN( method.getAnnotation(BaseDN.class).value() );
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

	public Class<? extends ITransferObject> getPojoClass() {
		return pojoClass;
	}

	public PropertyInfo getRDN() {
		return rdn;
	}

	public String getDnHolder() {
		return dnHolder;
	}

	public Map<String, String> getFieldMap() {
		return fieldMap;
	}

	public List<PropertyInfo> getMappings() {
		return mappings;
	}

	public String getMainObjectClass() {
		return mainObjectClass;
	}

	public String[] getObjectClasses() {
		return objectClasses;
	}

	public String getBaseDN() {
		return baseDN;
	}
	
}
