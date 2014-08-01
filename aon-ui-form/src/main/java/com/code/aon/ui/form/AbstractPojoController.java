package com.code.aon.ui.form;

import java.io.Serializable;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.dao.hibernate.TypeResolver;
import com.code.aon.ui.util.AonUtil;

/**
 * Abstract POJO Controller.
 * 
 * @author Consulting & Development.
 */
public class AbstractPojoController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPojoController.class);

	private IManagerBean managerBean;

	private String beanName;

	private String pojo;
	
	private boolean heritable;

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
		this.heritable = calculateHeritable();
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
				LOGGER.error(msg);
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
		LOGGER.debug("Getting field name for[{}]",alias);
		return getManagerBean().getFieldName(alias);
	}

	/**
	 * Gets the id alias.
	 * 
	 * @return the id alias
	 * @throws ManagerBeanException 
	 */
	protected String getIdAlias() throws ManagerBeanException {
		TypeResolver typeResolver = new TypeResolver(getPojo());
		ClassMetadata cmd = typeResolver.getClassMetdata();
		String id = getPojoShortName() + "_" + cmd.getIdentifierPropertyName();
		return getFieldName(id);
	}
	
	/**
	 * Resolves the alias.
	 * 
	 * @param alias
	 * @return Field path.
	 */
	public String resolveAlias(String alias) {
		String fieldName = StringUtils.substringBefore(alias, "-");
		try {
			fieldName = getFieldName(fieldName);
		} catch (ManagerBeanException e) {
			fieldName = fieldName.replace('_', '.');
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

	public boolean isHeritable() {
		return heritable;
	}
	
	@SuppressWarnings("unchecked")
	private boolean calculateHeritable() {
		try {
			Class<? extends ITransferObject> pojoClass = (Class<? extends ITransferObject>) Class.forName( getPojo() );
			return pojoClass.isAnnotationPresent(Heritable.class);
		} catch (ClassNotFoundException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return false;		
	}
	
}
