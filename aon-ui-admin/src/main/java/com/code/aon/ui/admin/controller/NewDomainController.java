package com.code.aon.ui.admin.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.Domain;
import com.code.aon.config.User;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.controller.LoggedUser;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DataSourceUtil;

public class NewDomainController {

	private static final String EQUALS = "="; 
	private static final String DOMAIN_TYPE = " --domain-type=Child";
	private static final String DOMAIN_USER = " --domain-user";
	private static final String DOMAIN_PASSWORD = " --domain-password";
	private static final String DOMAIN_NAME = " --domain-name";
	private static final String DOMAIN_DESCRIPTION = " --domain-description";
	private static final String PARENT_DOMAIN_ID = " --domain-parent-id";
	private static final String LOAD_DEFAULTS_FROM_PARENT = " -f";
	private static final String DB_HOST = " --host";
	private static final String DB_NAME = " --db";
	private static final String DB_USER = " --user";
	private static final String DB_PASSWORD = " --passwd";
	
	private static final String CONNECTION_USER_PROPERTY = "hibernate.connection.username";
	private static final String CONNECTION_PASSWORD_PROPERTY = "hibernate.connection.password";
	private static final String CONNECTION_URL_PROPERTY = "hibernate.connection.url";
		
	
	private String domainName;
	private String domainFinalName;
	private String domainDescription;
	private String domainSuffix;
	private String password;
	private boolean success;
	private boolean loadDefaultValuesEnabled;

	private String output;
	
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	public String getDomainFinalName() {
		return domainFinalName;
	}
	public void setDomainFinalName(String domainFinalName) {
		this.domainFinalName = domainFinalName;
	}
	
	public String getDomainDescription() {
		return domainDescription;
	}
	public void setDomainDescription(String domainDescription) {
		this.domainDescription = domainDescription;
	}

	public String getDomainSuffix() {
		return domainSuffix;
	}
	public void setDomainSuffix(String domainSuffix) {
		this.domainSuffix = domainSuffix;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public boolean isLoadDefaultValuesEnabled() {
		return loadDefaultValuesEnabled;
	}
	public void setLoadDefaultValuesEnabled(boolean loadDefaultValuesEnabled) {
		this.loadDefaultValuesEnabled = loadDefaultValuesEnabled;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public void onInit( ActionEvent event) {
		setDomainName(null);
		setDomainFinalName(null);
		setDomainDescription(null);
		setPassword(null);
		setSuccess(false);
		setOutput(null);
		setLoadDefaultValuesEnabled(true);
		
		try {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			Domain domain = (Domain) bean.get(DomainManager.getCurrentDomain());
			if (StringUtils.isNotBlank( domain.getSubDomainSuffix() )) {
				setDomainSuffix( domain.getSubDomainSuffix() );	
			} else {
				setDomainSuffix( StringUtils.substringAfter(domain.getName(), "." ));
			}
			if (!StringUtils.startsWith(getDomainSuffix(), ".")) {
				setDomainSuffix( "." + getDomainSuffix());
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se pudo recuperar el sufijo del dominio padre. Escriba el nombre completo.");
		}
		
	}
	
	public void onSave( ActionEvent event) {
		setDomainFinalName(  getDomainName() + getDomainSuffix() ); 
		Pattern p = Pattern.compile("[A-Z\\d][A-Z\\d.-]{1,61}[A-Z\\d]$",Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(getDomainFinalName());
		if (!m.matches() ) {
			String message = "El formato del nombre del dominio no es válido, debe comenzar y terminar por una letra o número. Solo puede contener letras, números y los caracteres '-' (guión) o '.' (punto).";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}
		
		try {
			StringBuffer buf = new StringBuffer();
			buf.append(DOMAIN_TYPE);
			
			buf.append(PARENT_DOMAIN_ID);
			buf.append(EQUALS);
			buf.append(DomainManager.getCurrentDomain());
			
			Properties properties = DataSourceUtil.getDBProperties();
			String user = properties.getProperty(CONNECTION_USER_PROPERTY);
			if (StringUtils.isNotBlank(user)) {
				buf.append(DB_USER);
				buf.append(EQUALS);
				buf.append(user);
			}
			
			String password = properties.getProperty(CONNECTION_PASSWORD_PROPERTY);
			if (StringUtils.isNotBlank(password)) {
				buf.append(DB_PASSWORD);
				buf.append(EQUALS);
				buf.append(password);
			}
			
			String url = properties.getProperty(CONNECTION_URL_PROPERTY);
			String[] tokens = StringUtils.splitByWholeSeparator(url,"://");
			String[] hosts = StringUtils.split(tokens[1],"/");
			String[] hostsItems = StringUtils.split(hosts[0],",:");
			String host = hostsItems[0];
			String dbname =StringUtils.substringBefore(StringUtils.split(tokens[1],"/")[1],"?");
			if (StringUtils.isNotBlank(host)) {
				buf.append(DB_HOST);
				buf.append(EQUALS);
				buf.append(host);
			}
			if (StringUtils.isNotBlank(dbname)) {
				buf.append(DB_NAME);
				buf.append(EQUALS);
				buf.append(dbname);
			}
			
			LoggedUser lu = (LoggedUser) AonUtil.getRegisteredBean(ICommonConstants.LOGGED_USER_CONTROLLER_NAME);
			AuthPrincipal principal = lu.getPrincipal(); 
			Integer userId = principal.getUserId();
			IManagerBean bean = BeanManager.getManagerBean(User.class);  
			User appUser = (User) bean.get(userId);
			
			buf.append(DOMAIN_USER);
			buf.append(EQUALS);
			buf.append(appUser.getLogin());

			buf.append(DOMAIN_PASSWORD);
			buf.append(EQUALS);
			buf.append(getPassword());

			buf.append(DOMAIN_NAME);
			buf.append(EQUALS);
			buf.append(getDomainFinalName());

			if (StringUtils.isNotBlank(getDomainDescription())) {
				buf.append(DOMAIN_DESCRIPTION);
				buf.append(EQUALS);
				buf.append('\'');
				buf.append(getDomainDescription());
				buf.append('\'');
			}
			
			if (!isLoadDefaultValuesEnabled()) {
				buf.append(LOAD_DEFAULTS_FROM_PARENT);
			}
			
			String command = "new_domain.py -v " + buf.toString();
			System.out.println( command);
			Runtime r = Runtime.getRuntime();
			Process process = r.exec(command);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line = "";
			StringWriter sw = new StringWriter();
			String bef = "<div>";
			String aft = "</div>";
			while ((line = stdInput.readLine()) != null) {
				sw.append(bef);
				sw.append(line);
				sw.append(aft);
			}
			while ((line = stdError.readLine()) != null) {
				sw.append(bef);
				sw.append(line);
				sw.append(aft);
			}
			int exitVal = process.waitFor();
			sw.append(bef);
			sw.append("Process return code ..: " + exitVal);
			sw.append(aft);
			output =  sw.toString();
			if (exitVal == 0) {
				success = true;
				
				onConfigure(event);
				// TODO
				// Ñapa para modificar la descripcion del dominio recien creado
				// Al llamar al script python se corta por el primer espacio
				// 
				// Cuando podamos utilizar python 2.7, cambiar optparse, que 
				// esta deprecado por argparse. en los script de python
				//
				//
				
				String sessionFactoryName = HibernateUtil.getSessionFactoryName(Domain.class.getName());
				// fin
				
			} else {
				AonUtil.addErrorMessage("Se ha producido un error durante la creación del dominio.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void onConfigure(ActionEvent event) {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(Domain.class.getName());
		String q = "SELECT d.id,d.description FROM domain d"
				+ " WHERE d.name = '" + getDomainFinalName() + "'"
				+ " AND d.active = 1";
		SQLQuery query = HibernateUtil.getSession(sessionFactoryName).createSQLQuery(q);
		List<?> queryList = query
				.addScalar("id", Hibernate.INTEGER)
				.addScalar("description", Hibernate.STRING)
				.list();
		Iterator<?> iterator = queryList.iterator();
		if (iterator.hasNext()) {
			Object[] array = (Object[]) iterator.next(); 
			Integer id = (Integer) array[0];
			String description = (String) array[1];
			
			// Nos aseguramos de que la descripción este correctamente grabada.
			if (!StringUtils.equals(description, getDomainDescription())) {
				Session session = HibernateUtil.getSession(sessionFactoryName); 
				org.hibernate.Transaction tx = session.beginTransaction();
				q = "UPDATE domain SET description=:description"
						+ " WHERE id = :id"
						+ " AND active = 1";
				query = session.createSQLQuery(q);
				query.setString("description", getDomainDescription());
				query.setInteger("id", id);
				query.executeUpdate();
				tx.commit();
			}
			DomainSwitcher switcher = (DomainSwitcher) AonUtil.getRegisteredBean(IAdminConstants.DOMAIN_SWITCHER_CONTROLLER_NAME);
			switcher.select(id, getDomainDescription() );
		}
	}
	
}
