package com.esferalia.aon.ui.sepe.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.sepe.utils.SEPEConnectionProvider;

public class SepeAppParamsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public final static String DEVELOPMENT_MODE = "PAY_dev_mode_PAY";
	
	public final static String CONTRATA_USER = "PAY_contrata_user_PAY";
	public final static String CONTRATA_PASSWORD = "PAY_contrata_passwd_PAY";
	public final static String CONTRATA_TEST_ENVIRONMENT_ACTIVE = "PAY_contrata_test_env_active_PAY";
	public final static String CONTRATA_SSL_ENVIRONMENT_ACTIVE = "PAY_contrata_ssl_env_active_PAY";

	public final static String CERTIFICA2_USER = "PAY_certifica2_user_PAY";
	public final static String CERTIFICA2_PASSWORD = "PAY_certifica2_passwd_PAY";
	public final static String CERTIFICA2_TEST_ENVIRONMENT_ACTIVE = "PAY_certifica2_test_env_PAY";
	public final static String CERTIFICA2_SSL_ENVIRONMENT_ACTIVE = "PAY_certifica2_ssl_env_PAY";

	private Boolean developmentMode;

	private String contrataUser;
	private String contrataPassword;
	private Boolean validContrataLogin;
	private Boolean contrataTestEnviroment;
	private Boolean contrataSSLEnviroment;

	private String certifica2User;
	private String certifica2Password;
	private Boolean validCertifica2Login;
	private Boolean certifica2TestEnviroment;
	private Boolean certifica2SSLEnviroment;
	
	
	private Map<String, ApplicationParameter> parameters;

	private Map<String, String> defaultParameters;
	
	public Boolean getDevelopmentMode() {
		if(developmentMode==null){
			initDevelopmentMode();
		}
		return developmentMode;
	}
	
	public void setDevelopmentMode(Boolean developmentMode) {
		this.developmentMode = developmentMode;
	}
	
	private void initDevelopmentMode() {
		try {
			if(getParameter(DEVELOPMENT_MODE).getValue()!=null){
				setDevelopmentMode(new Boolean(getParameter(DEVELOPMENT_MODE).getValue()));
			} else {
				setDevelopmentMode(false);
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}
	
	public Boolean getValidContrataLogin() {
		return validContrataLogin;
	}

	public void setValidContrataLogin(Boolean validContrataLogin) {
		this.validContrataLogin = validContrataLogin;
	}

	public boolean isContrataLoginChecked() {
		return validContrataLogin != null;
	}

	public String getContrataUser() {
		if(contrataUser==null){
			initContrataUser();
		}
		return contrataUser;
	}

	public void setContrataUser(String contrataUser) {
		this.contrataUser = contrataUser;
	}
	
	private void initContrataUser() {
		try {
			if(getParameter(CONTRATA_USER).getValue()!=null){
				setContrataUser(getParameter(CONTRATA_USER).getValue());
			} else {
				setContrataUser("");
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}

	public String getContrataPassword() {
		if(contrataPassword==null){
			initContrataPassword();
		}
		return contrataPassword;
	}

	public void setContrataPassword(String contrataPassword) {
		this.contrataPassword = contrataPassword;
	}
	
	private void initContrataPassword() {
		try {
			if(getParameter(CONTRATA_PASSWORD).getValue()!=null){
				setContrataPassword(getParameter(CONTRATA_PASSWORD).getValue());
			} else {
				setContrataPassword("");
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}
	
	public Boolean getContrataTestEnviroment() {
		if(contrataTestEnviroment==null){
			initContrataTestEnviroment();
		}
		return contrataTestEnviroment;
	}
	
	public void setContrataTestEnviroment(Boolean contrataTestEnviroment) {
		this.contrataTestEnviroment = contrataTestEnviroment;
	}
	
	private void initContrataTestEnviroment() {
		try {
			if(getParameter(CONTRATA_TEST_ENVIRONMENT_ACTIVE).getValue()!=null){
				setContrataTestEnviroment(new Boolean(getParameter(CONTRATA_TEST_ENVIRONMENT_ACTIVE).getValue()));
			} else {
				setContrataTestEnviroment(true);
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}
	
	public Boolean getContrataSSLEnviroment() {
		if(contrataSSLEnviroment==null){
			initContrataSSLEnviroment();
		}
		return contrataSSLEnviroment;
	}
	
	public void setContrataSSLEnviroment(Boolean contrataSSLEnviroment) {
		this.contrataSSLEnviroment = contrataSSLEnviroment;
	}
	
	private void initContrataSSLEnviroment() {
		try {
			if(getParameter(CONTRATA_SSL_ENVIRONMENT_ACTIVE).getValue()!=null){
				setContrataSSLEnviroment(new Boolean(getParameter(CONTRATA_SSL_ENVIRONMENT_ACTIVE).getValue()));
			} else {
				setContrataSSLEnviroment(true);
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}
	
	public Boolean getValidCertifica2Login() {
		return validCertifica2Login;
	}
	
	public void setValidCertifica2Login(Boolean validCertifica2Login) {
		this.validCertifica2Login = validCertifica2Login;
	}
	
	public boolean isCertifica2LoginChecked() {
		return validCertifica2Login != null;
	}
	
	public String getCertifica2User() {
		if(certifica2User==null){
			initCertifica2User();
		}
		return certifica2User;
	}
	
	public void setCertifica2User(String certifica2User) {
		this.certifica2User = certifica2User;
	}
	
	private void initCertifica2User() {
		try {
			if(getParameter(CERTIFICA2_USER).getValue()!=null){
				setCertifica2User(getParameter(CERTIFICA2_USER).getValue());
			} else {
				setCertifica2User("");
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}
	
	public String getCertifica2Password() {
		if(certifica2Password==null){
			initCertifica2Password();
		}
		return certifica2Password;
	}
	
	public void setCertifica2Password(String certifica2Password) {
		this.certifica2Password = certifica2Password;
	}
	
	private void initCertifica2Password() {
		try {
			if(getParameter(CERTIFICA2_PASSWORD).getValue()!=null){
				setCertifica2Password(getParameter(CERTIFICA2_PASSWORD).getValue());
			} else {
				setCertifica2Password("");
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}
	
	public Boolean getCertifica2TestEnviroment() {
		if(certifica2TestEnviroment==null){
			initCertifica2TestEnviroment();
		}
		return certifica2TestEnviroment;
	}
	
	public void setCertifica2TestEnviroment(Boolean certifica2TestEnviroment) {
		this.certifica2TestEnviroment = certifica2TestEnviroment;
	}
	
	private void initCertifica2TestEnviroment() {
		try {
			if(getParameter(CERTIFICA2_TEST_ENVIRONMENT_ACTIVE).getValue()!=null){
				setCertifica2TestEnviroment(new Boolean(getParameter(CERTIFICA2_TEST_ENVIRONMENT_ACTIVE).getValue()));
			} else {
				setCertifica2TestEnviroment(true);
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}
	
	public Boolean getCertifica2SSLEnviroment() {
		if(certifica2SSLEnviroment==null){
			initCertifica2SSLEnviroment();
		}
		return certifica2SSLEnviroment;
	}
	
	public void setCertifica2SSLEnviroment(Boolean certifica2SSLEnviroment) {
		this.certifica2SSLEnviroment = certifica2SSLEnviroment;
	}
	
	private void initCertifica2SSLEnviroment() {
		try {
			if(getParameter(CERTIFICA2_SSL_ENVIRONMENT_ACTIVE).getValue()!=null){
				setCertifica2SSLEnviroment(new Boolean(getParameter(CERTIFICA2_SSL_ENVIRONMENT_ACTIVE).getValue()));
			} else {
				setCertifica2SSLEnviroment(true);
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}
	
	public Map<String, ApplicationParameter> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, ApplicationParameter> parameters) {
		this.parameters = parameters;
	}

	public Map<String, String> getDefaultParameters() {
		return defaultParameters;
	}

	public void setDefaultParameters(Map<String, String> defaultParameters) {
		this.defaultParameters = defaultParameters;
		if (defaultParameters != null) {
			for (String key : defaultParameters.keySet()) {
				String value = defaultParameters.get(key);
				if ("[NULL]".equals(value)) {
					defaultParameters.put(key,null);	
				}
			}
		}
	}

	public void onAccept(ActionEvent event) throws ManagerBeanException{
		accept();
	}
	
	public void accept() throws ManagerBeanException{
		IManagerBean managerBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Collection<ApplicationParameter>params = parameters.values();
		beforeBeanUpdate();
		for(ApplicationParameter param : params){
			managerBean.update(param);
		}
	}

	public void onLoad(ActionEvent event) {
		try {
			loadParameters();
		} catch (ManagerBeanException e) {
			String msg = "Unable to load defaultParameters";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void loadParameters() throws ManagerBeanException{
		
		setDevelopmentMode(null);

		setContrataUser(null);
		setContrataPassword(null);
		setValidContrataLogin(null);
		setContrataTestEnviroment(null);
		
		setCertifica2User(null);
		setCertifica2Password(null);
		setValidCertifica2Login(null);
		setCertifica2TestEnviroment(null);
		
		List<String> keyList = new LinkedList<String>();
		for(String key: defaultParameters.keySet()){
			keyList.add(key);
		}
		parameters = new TreeMap<String, ApplicationParameter>();
		IManagerBean managerBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addInExpression(managerBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), keyList);
		List<ITransferObject> list = managerBean.getList(criteria);
		Iterator<ITransferObject> iter = list.iterator();
		while (iter.hasNext()) {
			ApplicationParameter appParam = (ApplicationParameter) iter.next();
			parameters.put(appParam.getName(), appParam);
		}
		Set<String> keys = defaultParameters.keySet();
		for (String key : keys) {
			if (!parameters.containsKey(key)) {
				ApplicationParameter p = new ApplicationParameter();
				p.setName(key);
				p.setValue(defaultParameters.get(key));
				p = (ApplicationParameter) managerBean.insert(p);
				parameters.put(p.getName(), p);
			}
		}
	}
	
	public ApplicationParameter getParameter(String key) throws ManagerBeanException {
		if (parameters == null || parameters.isEmpty()) {
			loadParameters();	
		}
		return parameters.get(key); 		
	}
	
	private void beforeBeanUpdate() throws ManagerBeanException {
	
		getParameter(DEVELOPMENT_MODE).setValue(getDevelopmentMode().toString());

		// CONTRATA PARAMS
		getParameter(CONTRATA_USER).setValue(getContrataUser());
		getParameter(CONTRATA_PASSWORD).setValue(getContrataPassword());
		getParameter(CONTRATA_TEST_ENVIRONMENT_ACTIVE).setValue(getContrataTestEnviroment().toString());
		getParameter(CONTRATA_SSL_ENVIRONMENT_ACTIVE).setValue(getContrataSSLEnviroment().toString());
		
		// CERTIFICA2 PARAMS
		getParameter(CERTIFICA2_USER).setValue(getCertifica2User());
		getParameter(CERTIFICA2_PASSWORD).setValue(getCertifica2Password());
		getParameter(CERTIFICA2_TEST_ENVIRONMENT_ACTIVE).setValue(getCertifica2TestEnviroment().toString());
		getParameter(CERTIFICA2_SSL_ENVIRONMENT_ACTIVE).setValue(getCertifica2SSLEnviroment().toString());

	}
	
	public void validateContrataLogin(ActionEvent event){
		setValidContrataLogin( SEPEConnectionProvider.validateContrataLogin(getContrataSSLEnviroment(), getContrataTestEnviroment(), getContrataUser(), getContrataUser(), getContrataPassword()) );
	}
	
	public void validateCertifica2Login(ActionEvent event){
		setValidCertifica2Login( SEPEConnectionProvider.validateCertifica2Login(getCertifica2SSLEnviroment(), getCertifica2TestEnviroment(), getCertifica2User(), getCertifica2User(), getCertifica2Password()) );
	}
	
}