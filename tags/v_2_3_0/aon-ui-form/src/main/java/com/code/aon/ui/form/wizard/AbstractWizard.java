package com.code.aon.ui.form.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;

/**
 * 
 *
 */
public abstract class AbstractWizard {

	private boolean visible;
	private String valueExpression;
	private String valueChangeListener;	
	private String extendedAttributesValueExpression;
	private DataModel model;
	private Criteria criteria;
	private int offset;
	private int rows = 15;
	private int count;
	private List<ITransferObject> list;

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
	 * @return int 
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * @return int 
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @param rows
	 */
	/**
	 * @param rows
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * @return int
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return Criteria 
	 */
	public Criteria getCriteria() {
		return criteria;
	}

	/**
	 * @param criteria
	 */
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	/**
	 * @return List<ITransferObject>
	 */
	public List<ITransferObject> getList() {
		return list;
	}

	/**
	 * @param list
	 */
	public void setList(List<ITransferObject> list) {
		this.list = list;
		model = null;
	}

	/**
	 * @return DataModel 
	 */
	public DataModel getModel() {
		if (model == null) {
			model = new ListDataModel(getList());
		}
		return model;
	}

	/**
	 * @param event
	 */
	public void onShow(ActionEvent event) {
		setVisible(true);
		clear();
		setList(null);
		model = null;
	}

	/**
	 * @param event
	 */
	public void onClose(ActionEvent event) {
		reset();
	}

	/**
	 * 
	 */
	protected void reset() {
		setVisible(false);
		clear();
		setList(null);
	}

	/**
	 * 
	 */
	protected void clear() {
		resetFields();
		criteria = new Criteria();
		offset = 0;
		count = -1;
	}

	/**
	 * @param event
	 */
	public void onSearch(ActionEvent event) {
		try {
			criteria = new Criteria();
			offset = 0;
			count = -1;
			fillCriteria(criteria);
			onSearch(criteria);
		} catch (ExpressionException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(e.getMessage());
			context.addMessage(null, message);
		}
	}

	/**
	 * @param criteria
	 */
	public void onSearch(Criteria criteria) {
		try {
			IManagerBean bean = getManagerBean();
			if (count == -1) {
				count = bean.getCount(criteria);
			}
			List<ITransferObject> list = null;
			if (count > 0) {
				list = bean.getList(criteria, offset, rows);
				offset += list.size();
			} else {
				list = new LinkedList<ITransferObject>();
			}
			setList(list);
			model = null;
		} catch (ManagerBeanException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(e.getMessage());
			context.addMessage(null, message);
		}
	}

	/**
	 * @return IManagerBean
	 * @throws ManagerBeanException
	 */
	private IManagerBean getManagerBean() throws ManagerBeanException {
		return BeanManager.getManagerBean( getClazz() );
	}

	/**
	 * @param event
	 */
	public void nextPage(ActionEvent event) {
		onSearch(criteria);
	}

	/**
	 * @param event
	 */
	public void previousPage(ActionEvent event) {
		if (offset > 0) {
			offset -= getList().size();
			offset -= rows;
		}
		if (offset < 0) {
			offset = 0;
		}
		onSearch(criteria);
	}

	/**
	 * @return boolean 
	 */
	public boolean isPreviousPageAvailable() {
		return (offset > rows);
	}

	/**
	 * @return boolean 
	 */
	public boolean isNextPageAvailable() {
		return (count > 0 && offset < count);
	}

	/**
	 * @param event
	 */
	public void onSelect(ActionEvent event) {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ELContext elctx = ctx.getELContext();
			ExpressionFactory factory = ctx.getApplication().getExpressionFactory();
			ValueExpression ve = factory.createValueExpression(elctx, getELValueExpression(),
					getClazz());
			Object newValue = model.getRowData();
			Object oldValue = ve.getValue(elctx);
			ve.setValue(elctx, newValue);
			assignExtendedLookupAttributes(newValue);
			fireValueChangeListener(event,oldValue,newValue);
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
			fireValueChangeListener(event,null,newValue);
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

	private void fireValueChangeListener(ActionEvent event,Object oldValue, Object newValue) {
		String expression = getELValueChangeListener();
		if (!"#{null}".equals(expression)) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ELContext elctx = ctx.getELContext();
			ExpressionFactory factory = ctx.getApplication().getExpressionFactory();
			MethodExpression me = 
				factory.createMethodExpression(elctx, expression, Object.class, new Class[]{ValueChangeEvent.class} );
			ValueChangeEvent changeEvent = new ValueChangeEvent(event.getComponent(),oldValue,newValue); 
			me.invoke(elctx, new Object[]{changeEvent});
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
