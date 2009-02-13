package com.code.aon.ui.form;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.annotations.Cascade;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ILookupObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull;
import com.code.aon.ui.common.lookup.LookupUtils;
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
	 * Restores de null value in framework created subpojos.
	 * 
	 * @param to
	 * @throws ManagerBeanException
	 */
	protected void restoreNullSubPOJOs(ITransferObject to) throws ManagerBeanException {
		try {
			Class clazz = to.getClass();
			LOGGER.fine("Restoring null values on " + clazz.getName());
			PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(clazz);
			LOGGER.fine("Found " + pds.length + " properties");
			for (PropertyDescriptor pd : pds) {
				Class fieldClass = pd.getPropertyType();
				String name = pd.getName();
				if (ITransferObject.class.isAssignableFrom(fieldClass)) {
					if (!pd.getReadMethod().isAnnotationPresent(Cascade.class)) {
						LOGGER.fine("Initializing TO " + name + " property");
						ITransferObject childTO = (ITransferObject) PropertyUtils.getProperty(to, name);
						if (childTO != null ) {
							IManagerBean bean = BeanManager.getManagerBean(fieldClass);
							Serializable id = bean.getId(childTO);
							if (id == null) {
								if(!pd.getReadMethod().isAnnotationPresent(AonPOJOInitializationInvalidateRestoreNull.class)){
									PropertyUtils.setProperty(to, name, null);
									LOGGER.fine("Assigned NULL to " + fieldClass);
								}
							}
						}
					}
				}
			}
		} catch (SecurityException e) {
			throw new ManagerBeanException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ManagerBeanException(e.getMessage());
		} catch (InvocationTargetException e) {
			throw new ManagerBeanException(e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new ManagerBeanException(e.getMessage());
		}
	}

	/**
	 * Return lookups in XML Format.
	 * 
	 * @param lo
	 * @param ids
	 * @return String
	 */
	protected String getLookupsAsXML(ILookupObject lo, String ids) {
		Map<String, Object> map = lo.getLookups();
		customizeLookupMap(lo, map);
		return LookupUtils.getResponseXML(map, ids);
	}

	/**
	 * To redefine in childs when you want to customize your lookups.
	 * 
	 * @param ito
	 * @param map
	 */
	@SuppressWarnings("unused")
	protected void customizeLookupMap(ILookupObject ito, Map<String, Object> map) {
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
