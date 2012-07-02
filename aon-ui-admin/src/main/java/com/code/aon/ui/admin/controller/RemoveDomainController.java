package com.code.aon.ui.admin.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Domain;
import com.code.aon.config.User;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.controller.LoggedUser;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DataSourceUtil;

public class RemoveDomainController {

	private static final String EQUALS = "="; 
	private static final String DOMAIN_USER = " --domain-user";
	private static final String DOMAIN_PASSWORD = " --domain-password";
	private static final String DOMAIN_NAME = " --domain-name";
	
	private static final String DB_HOST = " --host";
	private static final String DB_NAME = " --db";
	private static final String DB_USER = " --user";
	private static final String DB_PASSWORD = " --passwd";
	
	private static final String CONNECTION_USER_PROPERTY = "hibernate.connection.username";
	private static final String CONNECTION_PASSWORD_PROPERTY = "hibernate.connection.password";
	private static final String CONNECTION_URL_PROPERTY = "hibernate.connection.url";
		
	
	private Domain domain;
	private String domainName;
	private String password;
	private boolean success;

	private String output;
	
	public Domain getDomain() {
		return domain;
	}
	public void setDomain(Domain domain) {
		this.domain = domain;
	}
	
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
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
		setDomain(null);
		setDomainName(null);
		setPassword(null);
		setSuccess(false);
		setOutput(null);
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getDomains() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean( IAdminConstants.DOMAIN_SWITCHER_CONTROLLER_NAME);
		List<Domain> domains = (List<Domain>) ds.getModel().getWrappedData();
		List<SelectItem> items = new LinkedList<SelectItem>();
		for (Domain domain : domains) {
			items.add(new SelectItem(domain,domain.getDescription()));
		}
		return items;
	}
	
	public void onRemove( ActionEvent event) {
		try {
			LoggedUser lu = (LoggedUser) AonUtil.getRegisteredBean(ICommonConstants.LOGGED_USER_CONTROLLER_NAME);
			AuthPrincipal principal = lu.getPrincipal(); 
			Integer userId = principal.getUserId();
			IManagerBean bean = BeanManager.getManagerBean(User.class);  
			User appUser = (User) bean.get(userId);

			String saved_passwd = appUser.getPassword();
			String sent_passwd  = AdminUtil.encodeSHA(getPassword());
			if (!StringUtils.equals(saved_passwd, sent_passwd)) {
				String msg = "La contraseña no es correcta.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			
			if (!StringUtils.equals(getDomain().getName() , getDomainName())) {
				String msg = "El Nombre del dominio de la empresa no coincide con el de la empresa seleccionada";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}

			StringBuffer buf = new StringBuffer();
			
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
			
			buf.append(DOMAIN_USER);
			buf.append(EQUALS);
			buf.append(appUser.getLogin());

			buf.append(DOMAIN_PASSWORD);
			buf.append(EQUALS);
			buf.append(getPassword());

			buf.append(DOMAIN_NAME);
			buf.append(EQUALS);
			buf.append(getDomainName());

			String command = "remove_domain.py --no-prompt " + buf.toString();
			System.out.println( command);
			Runtime r = Runtime.getRuntime();
			Process process = r.exec(command);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
//			BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line = "";
			StringWriter sw = new StringWriter();
			String bef = "<div>";
			String aft = "</div>";
//			while ((line = stdError.readLine()) != null) {
//				System.out.println(line);
//				sw.append(bef);
//				sw.append(parseLine(line));
//				sw.append(aft);
//			}
			while ((line = stdInput.readLine()) != null) {
				System.out.println(line);
				sw.append(bef);
				sw.append(parseLine(line));
				sw.append(aft);
			}
			int exitVal = process.waitFor();
			sw.append(bef);
			sw.append("Process return code ..: " + exitVal);
			sw.append(aft);
			output =  sw.toString();
			if (exitVal == 0) {
				success = true;
				sw.append(bef);
				sw.append("Empresa borrada correctamente");
				sw.append(aft);
			} else {
				AonUtil.addErrorMessage("Se ha producido un error durante la creación del dominio.");
			}
			setDomain(null);
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean( IAdminConstants.DOMAIN_SWITCHER_CONTROLLER_NAME);
			ds.onEditSearch(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	private String parseLine(String line) {
		line = StringUtils.replace(line, "[91m", "<p style='color: red;'>");
		line = StringUtils.replace(line, "[32m", "<p style='color: green;'>");
		line = StringUtils.replace(line, "[1m", "<p style='font-weight: bold;'>");
		line = StringUtils.replace(line, "[0m", "</p>");
		return line;
	}
	
}
