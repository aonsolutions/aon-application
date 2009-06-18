package com.code.aon.ui.form.wizard;

import java.lang.reflect.InvocationTargetException;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;

/**
 * 
 *
 */
public abstract class AbstractWizard {

	private boolean visible;
	private String valueExpression;
	private String valueChangeListener;
	private String extendedAttributesValueExpression;
	private BasicController controller;

	/**
	 * 
	 */
	public AbstractWizard() {
		controller = new BasicController();
		controller.setPageLimit(15);
		controller.setPojo(getClazz().getName());
		controller.setQueryOnStartUP(false);
		controller.setSaveState(false);
	}

	/**
	 * @return BasicController
	 */
	public BasicController getController() {
		return controller;
	}
	
	/**
	 * @return boolean
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return String
	 */
	public String getValueExpression() {
		return valueExpression;
	}

	/**
	 * @return String
	 */
	public String getELValueExpression() {
		return "#{" + valueExpression + "}";
	}

	/**
	 * @param valueExpression
	 */
	public void setValueExpression(String valueExpression) {
		this.valueExpression = valueExpression;
	}

	/**
	 * @return String
	 */
	public String getValueChangeListener() {
		return valueChangeListener;
	}

	/**
	 * @return String
	 */
	public String getELValueChangeListener() {
		return "#{" + valueChangeListener + "}";
	}

	/**
	 * @param valueChangeListener
	 */
	public void setValueChangeListener(String valueChangeListener) {
		this.valueChangeListener = valueChangeListener;
	}

	/**
	 * @return String
	 */
	public String getExtendedAttributesValueExpression() {
		return extendedAttributesValueExpression;
	}

	/**
	 * @param extendedAttributesValueExpression
	 */
	public void setExtendedAttributesValueExpression(String extendedAttributesValueExpression) {
		this.extendedAttributesValueExpression = extendedAttributesValueExpression;
	}

	/**
	 * @return Criteria
	 * @throws ManagerBeanException
	 */
	public Criteria getCriteria() throws ManagerBeanException {
		return controller.getCriteria();
	}

	/**
	 * @param criteria
	 * @throws ManagerBeanException
	 */
	public void setCriteria(Criteria criteria) throws ManagerBeanException {
		controller.setCriteria(criteria);
	}

	/**
	 * @return DataModel
	 * @throws ManagerBeanException
	 */
	public DataModel getModel() throws ManagerBeanException {
		return controller.getModel();
	}

	/**
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void onShow(ActionEvent event) throws ManagerBeanException {
		setVisible(true);
		clear();
		controller.setModel(null);
	}

	/**
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void onClose(ActionEvent event) throws ManagerBeanException {
		controller.setModel(null);
		reset();
	}

	/**
	 * @throws ManagerBeanException
	 * 
	 */
	protected void reset() throws ManagerBeanException {
		setVisible(false);
		clear();
		controller.setModel(null);
	}

	/**
	 * @throws ManagerBeanException
	 * 
	 */
	protected void clear() throws ManagerBeanException {
		resetFields();
		setCriteria(new Criteria());
	}

	/**
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void onSearch(ActionEvent event) throws ManagerBeanException {
		try {
			setCriteria(new Criteria());
			fillCriteria(getCriteria());
			controller.onSearch(event);
		} catch (ExpressionException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(e.getMessage());
			context.addMessage(null, message);
		}
	}

	/**
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void onSelect(ActionEvent event) throws ManagerBeanException {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ELContext elctx = ctx.getELContext();
			ExpressionFactory factory = ctx.getApplication().getExpressionFactory();
			ValueExpression ve = factory.createValueExpression(elctx, getELValueExpression(),
					getClazz());
			Object newValue = controller.getModel().getRowData();
			Object oldValue = ve.getValue(elctx);
			ve.setValue(elctx, newValue);
			assignExtendedLookupAttributes(newValue);
			fireValueChangeListener(event, oldValue, newValue);
			reset();
		} catch (IllegalAccessException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(e.getMessage());
			context.addMessage(null, message);
		} catch (InvocationTargetException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(e.getMessage());
			context.addMessage(null, message);
		} catch (NoSuchMethodException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(e.getMessage());
			context.addMessage(null, message);
		}
	}

	/**
	 * @param event
	 */
	public void inputChanged(ActionEvent event) {
	}

	/**
	 * @param event
	 */
	public void onLookup(ActionEvent event) {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ELContext elctx = ctx.getELContext();
			ExpressionFactory factory = ctx.getApplication().getExpressionFactory();
			ValueExpression ve = factory.createValueExpression(elctx, getELValueExpression(),
					Object.class);
			Object newValue = ve.getValue(elctx);
			assignExtendedLookupAttributes(ve.getValue(elctx));
			fireValueChangeListener(event, null, newValue);
		} catch (IllegalAccessException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(e.getMessage());
			context.addMessage(null, message);
		} catch (InvocationTargetException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(e.getMessage());
			context.addMessage(null, message);
		} catch (NoSuchMethodException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(e.getMessage());
			context.addMessage(null, message);
		}
	}

	private void fireValueChangeListener(ActionEvent event, Object oldValue, Object newValue) {
		String expression = getELValueChangeListener();
		if (!"#{null}".equals(expression)) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ELContext elctx = ctx.getELContext();
			ExpressionFactory factory = ctx.getApplication().getExpressionFactory();
			MethodExpression me = factory.createMethodExpression(elctx, expression, Object.class,
					new Class[] { ValueChangeEvent.class });
			ValueChangeEvent changeEvent = new ValueChangeEvent(event.getComponent(), oldValue,
					newValue);
			me.invoke(elctx, new Object[] { changeEvent });
		}
	}

	/**
	 * @param selected
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	protected void assignExtendedLookupAttributes(Object selected) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		String exp = getExtendedAttributesValueExpression();
		if (!StringUtils.isEmpty(exp) && !("null".equals(exp))) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ELContext elctx = ctx.getELContext();
			ExpressionFactory factory = ctx.getApplication().getExpressionFactory();
			String[] expressions = exp.split(";");
			for (String expression : expressions) {
				String[] tokens = expression.split("=");
				Object property = BeanUtils.getProperty(selected, tokens[1]);
				ValueExpression ve = factory.createValueExpression(elctx, "#{" + tokens[0] + "}",
						Object.class);
				ve.setValue(elctx, property);
			}
		}
	}

	/**
	 * 
	 */
	protected abstract void resetFields();

	/**
	 * @param criteria
	 * @throws ExpressionException
	 */
	protected abstract void fillCriteria(Criteria criteria) throws ExpressionException;

	/**
	 * @return Class<? extends ITransferObject>
	 */
	protected abstract Class<? extends ITransferObject> getClazz();

}
