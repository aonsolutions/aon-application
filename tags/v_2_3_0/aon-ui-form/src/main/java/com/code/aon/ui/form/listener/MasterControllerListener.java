package com.code.aon.ui.form.listener;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * MasterControllerListener is a listener that must be registered in
 * <code>faces-bean-config.xml</code> asociated with the master Controller. It
 * handles the relation between master and child controllers.
 */
public class MasterControllerListener extends ControllerAdapter {

	/** The child bean. */
	private String childBean;

	/** The alias map. */
	private Map<String, String> aliasMap;

	/**
	 * Sets the child bean.
	 * 
	 * @param childBean
	 *            the child bean
	 */
	public void setChildBean(String childBean) {
		this.childBean = childBean;
	}

	/**
	 * Sets the alias map.
	 * 
	 * @param aliasMap
	 *            the alias map
	 */
	public void setAliasMap(Map<String, String> aliasMap) {
		this.aliasMap = aliasMap;
	}

	/**
	 * Gets the detail controller.
	 * 
	 * @return the detail controller
	 */
	public IController getDetailController() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Application app = ctx.getApplication();
		ValueBinding vb = app.createValueBinding("#{" + this.childBean + "}");
		return (IController) vb.getValue(ctx);
	}

	/**
	 * Updates detail controller criteria to load only the objects related with
	 * the <code>to</code> of the <code>masterController</code>.
	 * 
	 * @param reset
	 *            the reset
	 * @param master
	 *            the master
	 * 
	 * @throws ControllerListenerException
	 *             the controller listener exception
	 */
	protected void updateDetailCriteria(IController master, boolean reset)
			throws ControllerListenerException {
		IController detail = getDetailController();
		ITransferObject to = master.getTo();
		// Criteria criteria = new Criteria();
		try {
			detail.clearCriteria();
			Criteria criteria = detail.getCriteria();
			for (Map.Entry<String, String> entry : this.aliasMap.entrySet()) {
				String alias = entry.getKey();
				String reg = detail.getFieldName(alias);
				if (reset) {
					criteria.addNullExpression(reg);
				} else {
					String propertyName = entry.getValue();
					Object value = PropertyUtils.getProperty(to, propertyName);
					criteria.addEqualExpression(reg, value);
				}
			}
			detail.setCriteria(criteria);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	/**
	 * Inits the detail model.
	 * 
	 * @param reset
	 *            the reset
	 * @param master
	 *            the master
	 * 
	 * @throws ControllerListenerException
	 *             the controller listener exception
	 */
	private void initDetailModel(IController master, boolean reset)
			throws ControllerListenerException {
		updateDetailCriteria(master, reset);
		getDetailController().initializeModel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanSelected(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		initDetailModel(event.getController(), false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanCreated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		initDetailModel(event.getController(), true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		initDetailModel(event.getController(), false);
	}

}
