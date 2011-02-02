package com.code.aon.desktop;

import java.util.Properties;

import javax.naming.Name;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;
import com.code.aon.ldap.IAonObjectClasses;

@EntryObject(mainObjectClass=IAonObjectClasses.DB_CONNECTION, objectClasses={IAonObjectClasses.TOP})
public class DBConnnection implements ITransferObject, Cloneable {

	private static final long serialVersionUID = -16395756416577198L;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DBConnnection.class);

	private Name id;
	
	private String commonName;
	
	private String driverClassName;
	
	private String labeledURI;
	
	private String uid;
	
	private byte[] userPassword;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Name getId() {
		return id;
	}

	public void setId(Name id) {
		this.id = id;
	}
	
	@RDN
	@Attribute(name="cn",nullable=false)
	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
	
	@Attribute(name="driverClassName",nullable=false)
	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	@Attribute(name="labeledURI",nullable=false)
	public String getLabeledURI() {
		return labeledURI;
	}

	public void setLabeledURI(String labeledURI) {
		this.labeledURI = labeledURI;
	}

	@Attribute(name="uid",length=256,nullable=false)
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@Attribute(name="userPassword",length=128,nullable=false)
	public byte[] getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(byte[] userPassword) {
		this.userPassword = userPassword;
	}

	public String getPassword() {
		return new String(userPassword);
	}
	
	public String getDBName() {
		String name = StringUtils.substringAfterLast(this.labeledURI, "/");
		return StringUtils.substringBefore(name, "?");
	}
	
	private Properties getHibernateProperties() {
		Properties hibernateProperties = new Properties();
		hibernateProperties.put(Environment.USER, getUid());
		hibernateProperties.put(Environment.PASS, getPassword());
		hibernateProperties.put(Environment.URL, getLabeledURI());
		hibernateProperties.put(Environment.DRIVER, getDriverClassName());
		hibernateProperties.put(Environment.SHOW_SQL, Boolean.TRUE);
		return hibernateProperties;
	}
	
	public void configure( Configuration configuration ) {
   		configuration.configure();
		configuration.addProperties( getHibernateProperties() );
   		Properties properties = configuration.getProperties();
   		properties.remove(Environment.CONNECTION_PROVIDER);
   		properties.remove(Environment.DATASOURCE);		
   		properties.remove(Environment.C3P0_MAX_SIZE);
	}
	
	@Override
	public Object clone() {
        Object obj=null;
        try {
            obj=super.clone();
        } catch (CloneNotSupportedException ex) {
        	LOGGER.error( "Error cloning DBConnection", ex );
        }
        return obj;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof DBConnnection) {
			DBConnnection o = (DBConnnection) obj;
			if (o.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.id != null) ? id.hashCode() : super.hashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	
	
}