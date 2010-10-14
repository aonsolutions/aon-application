package com.code.aon.ui.form.listener;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * ChildControllerListener is a listener that must be registered in <code>faces-bean-config.xml</code> asociated
 * with the child Controller.
 * It updates the parent object reference of the child object in a Master-Detail relation. 
 */
public class ChildControllerListener extends ControllerAdapter {

	/** The master bean. */
	private String masterBean;

	/** The master property name. */
	private String masterPropertyName;

	
	/**
	 * Gets the master property name.
	 * 
	 * @return the master property name
	 */
	public String getMasterPropertyName() {
		return masterPropertyName;
	}

	/**
	 * Sets the master property name.
	 * 
	 * @param masterPropertyName the master property name
	 */
	public void setMasterPropertyName(String masterPropertyName) {
		this.masterPropertyName = masterPropertyName;
	}

	/**
	 * Gets the master bean.
	 * 
	 * @return the master bean
	 */
	public String getMasterBean() {
		return this.masterBean;
	}

	/**
	 * Sets the master bean.
	 * 
	 * @param masterBean the master bean
	 */
	public void setMasterBean(String masterBean) {
		this.masterBean = masterBean;
	}

	/**
	 * Gets the master controller.
	 * 
	 * @return the master controller
	 */
	private IController getMasterController() {
		return FormUtil.getController( getMasterBean() );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanCreated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		try {
			BasicController childController = (BasicController) event
					.getController();
			ITransferObject child = childController.getTo();
			ITransferObject master = resolveMasterBean();
			Class<?> propertyClass = PropertyUtils.getPropertyType(child, getMasterPropertyName());
			if ( propertyClass.isAssignableFrom(master.getClass()) ) {
				PropertyUtils.setProperty(child, getMasterPropertyName(), master );
			} else {
				Serializable id = getMasterController().getManagerBean().getId( master );
				PropertyUtils.setProperty(child, getMasterPropertyName(), id );
			}
		} catch (IllegalAccessException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	/**
	 * Returns <code>getTo()</code> of the master Controller 
	 * 
	 * @return the I transfer object
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	public ITransferObject resolveMasterBean()
			throws ControllerListenerException {
		if (getMasterBean() == null) {
			throw new ControllerListenerException(
					"\"masterBean\" property can not be null");
		}
		if (getMasterBean().indexOf('.') == -1) {
			IController masterController = getMasterController();
			return masterController.getTo();
		}
		StringTokenizer st = new StringTokenizer(getMasterBean(), ".");
		ITransferObject to = null;
		for (int i = 0; st.hasMoreTokens();++i) {
			String token = st.nextToken();
			if (i == 0) {
				IController masterController = FormUtil.getController(token);
				to = masterController.getTo();
			} else {
				try {
					to = (ITransferObject) PropertyUtils.getProperty(to, token);
				} catch (IllegalAccessException e) {
					throw new ControllerListenerException(e.getMessage(), e);
				} catch (InvocationTargetException e) {
					throw new ControllerListenerException(e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					throw new ControllerListenerException(e.getMessage(), e);
				}
			}
		}
		return to;
	}

}
