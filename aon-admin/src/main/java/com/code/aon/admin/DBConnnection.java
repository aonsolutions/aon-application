package com.code.aon.admin;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public class DBConnnection {

	private String driverClassName;
	
	private String labeledURI;
	
	private String uid;
	
	private String userPassword;
	
	public DBConnnection( Properties properties ) {
		setUid( properties.getProperty(Environment.USER) );
		setUserPassword( properties.getProperty(Environment.PASS) );
		setLabeledURI( properties.getProperty(Environment.URL) );
		setDriverClassName( properties.getProperty(Environment.DRIVER) );
	}	
	
	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getLabeledURI() {
		return labeledURI;
	}

	public void setLabeledURI(String labeledURI) {
		this.labeledURI = labeledURI;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	
	public String getDBName() {
		String name = StringUtils.substringAfterLast(this.labeledURI, "/");
		return StringUtils.substringBefore(name, "?");
	}
	
	private Properties getHibernateProperties() {
		Properties hibernateProperties = new Properties();
		hibernateProperties.put(Environment.USER, getUid());
		hibernateProperties.put(Environment.PASS, getUserPassword());
		hibernateProperties.put(Environment.URL, getLabeledURI());
		hibernateProperties.put(Environment.DRIVER, getDriverClassName());
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
	public boolean equals(Object obj) {
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