package com.code.aon.desktop;

import static com.code.aon.ldap.IAonObjectClasses.DB_CONNECTION;
import static com.code.aon.ldap.IAonObjectClasses.TOP;
import static com.code.aon.ldap.ILdapConstants.COMMON_NAME_ATTRIBUTE;
import static com.code.aon.ldap.ILdapConstants.DRIVER_CLASS_NAME_ATTRIBUTE;
import static com.code.aon.ldap.ILdapConstants.LABELED_URI_ATTRIBUTE;
import static com.code.aon.ldap.ILdapConstants.USER_ID_ATTRIBUTE;
import static com.code.aon.ldap.ILdapConstants.USER_PASSWORD_ATTRIBUTE;

import java.util.Properties;

import javax.naming.Name;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;

@EntryObject(mainObjectClass=DB_CONNECTION, objectClasses={TOP})
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
	@Attribute(name=COMMON_NAME_ATTRIBUTE,nullable=false)
	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
	
	@Attribute(name=DRIVER_CLASS_NAME_ATTRIBUTE,nullable=false)
	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	@Attribute(name=LABELED_URI_ATTRIBUTE,nullable=false)
	public String getLabeledURI() {
		return labeledURI;
	}

	public void setLabeledURI(String labeledURI) {
		this.labeledURI = labeledURI;
	}

	@Attribute(name=USER_ID_ATTRIBUTE,length=256,nullable=false)
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@Attribute(name=USER_PASSWORD_ATTRIBUTE,length=128,nullable=false)
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

	public boolean equalsDB(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final DBConnnection o = (DBConnnection) obj;
		return new EqualsBuilder()
			.append(this.driverClassName, o.driverClassName)
			.append(this.labeledURI, o.labeledURI)				
			.append(this.uid, o.uid)
			.append(this.userPassword, o.userPassword)				
			.isEquals();
	}	
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(commonName)
			.append(driverClassName)
			.append(labeledURI)	
			.append(uid)			
			.append(userPassword)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}		
	
}