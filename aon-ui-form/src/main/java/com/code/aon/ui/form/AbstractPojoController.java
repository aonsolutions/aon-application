package com.code.aon.ui.form;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull;
import com.code.aon.ui.util.AonUtil;

/**
 * Abstract POJO Controller.
 * 
 * @author Consulting & Development.
 */
public class AbstractPojoController {

	private static final Logger LOGGER = Logger.getLogger(AbstractPojoController.class.getName());

	/** Clave para identificar el mensaje de error. (El valor es ""aon_error"") */
	public static final String AON_ERROR = "aon_error";

	private IManagerBean managerBean;

	private String beanName;

	private String pojo;

	/**
	 * Empty constructor.
	 * 
	 */
	public AbstractPojoController() {
	}

	/**
	 * Return the POJO associated to controller.
	 * 
	 * @return String
	 */
	public String getPojo() {
		return pojo;
	}

	/**
	 * Return the POJO class short name associated to controller.
	 * 
	 * @return String
	 */
	public String getPojoShortName() {
		return ClassUtils.getShortClassName(pojo);
	}
	
	/**
	 * Set the POJO associated to controller.
	 * 
	 * @param bean
	 */
	public void setPojo(String bean) {
		this.pojo = bean;
	}

	/**
	 * Return name of the bean associated to controller.
	 * 
	 * @return String
	 */
	public String getBeanName() {
		return beanName;
	}

	/**
	 * Set the name of the bean associated to controller.
	 * 
	 * @param beanName
	 */
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	/**
	 * Return the manager of bean associated to controller.
	 * 
	 * @return IManagerBean
	 * @throws ManagerBeanException
	 */
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if (this.managerBean == null) {
			this.managerBean = BeanManager.getManagerBean(getPojo());
			if (this.managerBean == null) {
				String msg = "Unknown IManagerBean for " + getPojo();
				LOGGER.severe(msg);
				throw new ManagerBeanException(msg);
			}
		}
		return this.managerBean;
	}

	/**
	 * Return the name of the field that corresponds to the parameter alias.
	 * 
	 * @param alias
	 * @return String
	 * @throws ManagerBeanException
	 */
	public String getFieldName(String alias) throws ManagerBeanException {
		LOGGER.fine("Getting field name for[" + alias + "]");
		return getManagerBean().getFieldName(alias);
	}

	/**
	 * Resolves the alias.
	 * 
	 * @param alias
	 * @return Field path.
	 */
	public String resolveAlias( String alias ) {
		String fieldName = null;
		try {
			fieldName = getFieldName(alias);
		} catch (ManagerBeanException e) {
			fieldName = alias.replace('_', '.');
			fieldName = StringUtils.substringBefore(fieldName, "-");
		}
		return fieldName;
	}
		
	/**
	 * Add message to the collection of messages.
	 * 
	 * @param message
	 */
	protected void addMessage(String message) {
		AonUtil.addErrorMessage(message);
	}

}
