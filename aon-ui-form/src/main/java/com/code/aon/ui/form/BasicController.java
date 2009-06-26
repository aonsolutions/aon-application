package com.code.aon.ui.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.DataModelListener;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Order;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.ast.IdentExpression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.ControllerListenerSupport;
import com.code.aon.ui.form.event.IControllerListener;

/**
 * Controller for Basic Structures.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 06-abr-2005
 */
public class BasicController extends AbstractPojoController implements IController,
		ICollectionProvider {

	private static final Logger LOGGER = Logger.getLogger(BasicController.class.getName());

	private Criteria criteria = new Criteria();

	private ITransferObject to;

	/** Represent the model of data that we are going to interact with */
	protected DataModel model;

	private boolean isNew;

	private boolean queryOnStartUP;

	/** Represent a manager of listeners */
	protected ControllerListenerSupport controllerListenerSupport;

	private int pageLimit = PageDataModel.LIMIT;

	private int selectedIndex;

	private List<IControllerListener> listenerClasses;

	private List<DataModelListener> dataModelListeners;

	private boolean saveState;

	/** The saved to id. */
	protected Serializable savedToId;

	private OrderByList orderList;

	private List<Expression> initExpressions;

	/**
	 * Constructor.
	 * 
	 */
	public BasicController() {
		this.controllerListenerSupport = new ControllerListenerSupport();
		this.selectedIndex = -1;
		this.saveState = true;
	}

	/**
	 * Return if a query will be executed on the model associated to controller
	 * when starting up.
	 * 
	 * @return queryOnStartUP
	 */
	public boolean isQueryOnStartUP() {
		return queryOnStartUP;
	}

	/**
	 * Set if a query will be executed on the model associated to controller
	 * when starting up.
	 * 
	 * @param queryOnStartUP
	 */
	public void setQueryOnStartUP(boolean queryOnStartUP) {
		this.queryOnStartUP = queryOnStartUP;
	}

	/**
	 * Return the limit of page in the model associated to controller.
	 * 
	 * @return int
	 */
	public int getPageLimit() {
		return pageLimit;
	}

	/**
	 * Set the limit of page in the model associated to controller.
	 * 
	 * @param pageLimit
	 */
	public void setPageLimit(int pageLimit) {
		this.pageLimit = pageLimit;
	}

	/**
	 * Return a list containing the listeners associated to controller.
	 * 
	 * @return List<IControllerListener>
	 */
	public List<IControllerListener> getListenerClasses() {
		return listenerClasses;
	}

	/**
	 * Set a list containing the listeners associated to controller.
	 * 
	 * @param listenerClasses
	 */
	public void setListenerClasses(List<IControllerListener> listenerClasses) {
		this.listenerClasses = listenerClasses;
		addListeners();
	}

	/**
	 * Gets the list of <code>javax.faces.model.DataModelListener</code>
	 * registered to the implicit Model.
	 * 
	 * @return The list of <code>javax.faces.model.DataModelListener</code>
	 *         registered to the implicit Model.
	 */
	public List<DataModelListener> getDataModelListeners() {
		return dataModelListeners;
	}

	/**
	 * Adds the given DataModelListener to the list of listeners
	 * 
	 * @param listener
	 */
	public void addDataModelListener(DataModelListener listener) {
		if (dataModelListeners == null) {
			dataModelListeners = new LinkedList<DataModelListener>();
		}
		dataModelListeners.add(listener);
	}

	@Override
	public DataModel getModel() throws ManagerBeanException {
		if (model == null) {
			if (isQueryOnStartUP()) {
				initializeModel();
			} else {
				this.model = new PageDataModel(this, getPageLimit());
				addDataModelListeners();
			}
			
		}
		return model;
	}

	@Override
	public void setModel(DataModel model) {
		this.model = model;
	}

	/**
	 * Return an object representing the data for the currently selected row
	 * index of the model associated to controller.
	 * 
	 * @return Object
	 */
	protected Object getSelectedTO() {
		return this.model.getRowData();
	}

	/**
	 * Return the zero-relative index of the currently selected row of the model
	 * associated to controller.
	 * 
	 * @return int
	 */
	protected int getSelectedTOIndex() {
		return this.model.getRowIndex();
	}

	/**
	 * Return the zero-relative index of the currently selected row of the model
	 * associated to controller.
	 * 
	 * @return int
	 */
	public int getSelectedIndex() {
		return this.selectedIndex;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	@Override
	public void setNew(boolean isNew) {
		if (isNew) {
			this.selectedIndex = -1;
			if (this.model != null) {
				this.model.setRowIndex(this.selectedIndex);
			}
		}
		this.isNew = isNew;
	}

	/**
	 * Return the state of bean. True if bean is positioned in the first
	 * Transfer Object, otherwise false.
	 * 
	 * @return boolean
	 */
	public boolean isInFirst() {
		return getSelectedIndex() == 0;
	}

	/**
	 * Return the state of bean. True if bean is positioned in the last Transfer
	 * Object, otherwise false.
	 * 
	 * @return boolean
	 * @throws ManagerBeanException
	 */
	public boolean isInLast() throws ManagerBeanException {
		int count = getModel().getRowCount();
		return (count > 0) && ((count - 1) == getSelectedIndex());
	}

	@Override
	public void onAccept(ActionEvent event) {
		accept(event);
		resetTo();
	}

	/**
	 * Action to insert or to update the current row depending on the value of
	 * the variable isNew.
	 * 
	 * @param event
	 */
	public void accept(ActionEvent event) {
		try {
			restoreNullSubPOJOs(getTo());
			accept();
			if (isNew()) {
				initializeModel();
				setNew(false);
			}
		} catch (ManagerBeanException e) {
			LOGGER.severe(">>>> onAccept " + e.getMessage());
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			try {
				getManagerBean().initializePOJO(this.to);
			} catch (ManagerBeanException e) {
				LOGGER.severe(">>>> onAccept initializePOJO " + e.getMessage());
				addMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);
			}
		}

	}

	/**
	 * Pure accept without POJO modificactions. Usefull in the use of
	 * transactions.
	 */
	protected void accept() {
		try {
			ControllerEvent evt = new ControllerEvent(this);
			if (isNew) {
				controllerListenerSupport.fireBeforeBeanAdded(evt);
				this.to = add();
				controllerListenerSupport.fireAfterBeanAdded(evt);
			} else {
				controllerListenerSupport.fireBeforeBeanUpdated(evt);
				this.to = update();
				controllerListenerSupport.fireAfterBeanUpdated(evt);
			}
		} catch (ControllerListenerException e) {
			LOGGER.severe(">>>> onAccept " + e.getMessage());
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.severe(">>>> onAccept " + e.getMessage());
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public void onSearch(ActionEvent event) {
		try {
			ControllerEvent evt = new ControllerEvent(this);
			controllerListenerSupport.fireBeforeModelSearched(evt);
			controllerListenerSupport.fireBeforeBeanReset(evt);
			initializeModel();
			resetTo();
			controllerListenerSupport.fireAfterBeanReset(evt);
			controllerListenerSupport.fireAfterModelSearched(evt);
		} catch (ControllerListenerException e) {
			LOGGER.severe(">>>> onSearch " + e.getMessage());
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public void onRemove(ActionEvent event) {
		remove(event);
		resetTo();
	}

	/**
	 * Action to remove the current row.
	 * 
	 * @param event
	 */
	public void remove(ActionEvent event) {
		try {
			ControllerEvent evt = new ControllerEvent(this);
			controllerListenerSupport.fireBeforeBeanRemoved(evt);
			remove();
			initializeModel();
			controllerListenerSupport.fireAfterBeanRemoved(evt);
		} catch (ControllerListenerException e) {
			LOGGER.severe(">>>> onRemove exception[" + e.getMessage() + "]");
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.severe(">>>> onRemove exception[" + e.getMessage() + "]");
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public void onCancel(ActionEvent event) {
		cancel(event);
		resetTo();
	}

	/**
	 * @param event
	 */
	public void cancel(ActionEvent event) {
		try {
			ControllerEvent evt = new ControllerEvent(this);
			controllerListenerSupport.fireBeforeBeanCanceled(evt);
			restoreState();
			controllerListenerSupport.fireAfterBeanCanceled(evt);
		} catch (ControllerListenerException e) {
			LOGGER.severe(">>>> onCancel " + e.getMessage());
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.severe(">>>> onCancel " + e.getMessage());
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public void onReset(ActionEvent event) {
		try {
			ControllerEvent evt = new ControllerEvent(this);
			setTo(getManagerBean().createNewTo());
			controllerListenerSupport.fireBeforeBeanCreated(evt);
			setNew(true);
			controllerListenerSupport.fireAfterBeanCreated(evt);
		} catch (ControllerListenerException e) {
			LOGGER.severe(">>>> onReset " + e.getMessage());
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.severe(">>>> onReset " + e.getMessage());
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		try {
			ControllerEvent evt = new ControllerEvent(this);
			controllerListenerSupport.fireBeforeEditSearch(evt);
			controllerListenerSupport.fireBeforeBeanReset(evt);
			clearCriteria();
			setTo(getManagerBean().createNewTo());
			controllerListenerSupport.fireAfterBeanReset(evt);
			controllerListenerSupport.fireAfterEditSearch(evt);
		} catch (ControllerListenerException e) {
			LOGGER.severe(">>>> onEditSearch " + e.getMessage());
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.severe(">>>> onEditSearch " + e.getMessage());
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	/**
	 * Execute selection action of the first element of the data model.
	 * 
	 * @param event
	 */
	public void onSelectFirst(ActionEvent event) {
		if (!isInFirst()) {
			try {
				getModel().setRowIndex(0);
			} catch (ManagerBeanException e) {
				LOGGER.severe(">>>> onSelectFirst exception:[" + e.getMessage() + "]");
				addMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);
			}
			onSelect(event);
		}
	}

	/**
	 * Execute selection action of the previous element of the data model.
	 * 
	 * @param event
	 */
	public void onSelectPrevious(ActionEvent event) {
		if (getSelectedIndex() > 0) {
			try {
				getModel().setRowIndex(getSelectedIndex() - 1);
			} catch (ManagerBeanException e) {
				LOGGER.severe(">>>> onSelectFirst exception:[" + e.getMessage() + "]");
				addMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);
			}
			onSelect(event);
		}
	}

	/**
	 * Execute selection action of the next element of the data model.
	 * 
	 * @param event
	 */
	public void onSelectNext(ActionEvent event) {
		try {
			DataModel model = getModel();
			int index = getSelectedIndex();
			if ((index != -1) && (index < (model.getRowCount() - 1))) {
				getModel().setRowIndex(index + 1);
				onSelect(event);
			}
		} catch (ManagerBeanException e) {
			LOGGER.severe(">>>> onSelectFirst exception:[" + e.getMessage() + "]");
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	/**
	 * Execute selection action of the last element of the data model.
	 * 
	 * @param event
	 */
	public void onSelectLast(ActionEvent event) {
		try {
			if ((getModel().getRowCount() > 0) && (!isInLast())) {
				getModel().setRowIndex(getModel().getRowCount() - 1);
				onSelect(event);
			}
		} catch (ManagerBeanException e) {
			LOGGER.severe(">>>> onSelectFirst exception:[" + e.getMessage() + "]");
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public void onSelect(ActionEvent event) {
		try {
			ControllerEvent evt = new ControllerEvent(this);
			controllerListenerSupport.fireBeforeBeanSelected(evt);
			selectedIndex = getSelectedTOIndex();
			LOGGER.fine(">>>> onSelect rowIndex:[" + selectedIndex + "]");
			ITransferObject to = (ITransferObject) getSelectedTO();
			getManagerBean().initializePOJO(to);
			setTo(to);
			setNew(false);
			controllerListenerSupport.fireAfterBeanSelected(evt);
			saveState(to);
		} catch (ControllerListenerException e) {
			LOGGER.severe(">>>> onSelect exception:[" + e.getMessage() + "]");
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.severe(">>>> onSelect exception:[" + e.getMessage() + "]");
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public ITransferObject getTo() {
		return this.to;
	}

	@Override
	public void addExpression(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			String value = event.getNewValue().toString();
			if (! StringUtils.isBlank(value) ) {
				String fieldName = resolveAlias(event.getComponent().getId()); 
				try {
					criteria.addExpression(fieldName, value);
				} catch (ExpressionException e) {
					throw new ManagerBeanException(e.getMessage(), e);
				}
			}
		}
	}
	
	@Override
	public void addEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			String fieldName = resolveAlias(event.getComponent().getId());
			criteria.addEqualExpression(fieldName, event.getNewValue());
		}
	}	

	@Override
	public void addGreaterThanOrEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			String fieldName = resolveAlias(event.getComponent().getId());
			criteria.addGreaterThanOrEqualExpression(fieldName, event.getNewValue());
		}
	}	

	@Override
	public void addLessThanOrEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			String fieldName = resolveAlias(event.getComponent().getId());
			criteria.addLessThanOrEqualExpression(fieldName, event.getNewValue());
		}
	}	
	
	@Override
	public void addIdEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			ITransferObject to = (ITransferObject) event.getNewValue();
			String fieldName = resolveAlias(event.getComponent().getId());
			IManagerBean bean = BeanManager.getManagerBean(to.getClass());
			Serializable id = bean.getId(to);
			criteria.addEqualExpression(fieldName, id);
		}
	}	
	
	/**
	 * Resolves the alias.
	 * 
	 * @param alias
	 * @return Field path.
	 */
	protected String resolveAlias( String alias ) {
		String fieldName = null;
		try {
			fieldName = getFieldName(alias);
		} catch (ManagerBeanException e) {
			fieldName = alias.replace('_', '.');
			fieldName = StringUtils.substringBefore(fieldName, "-");
		}
		return fieldName;
	}
	
	@Override
	public void clearCriteria() throws ManagerBeanException {
		this.criteria = new Criteria();
		updateOrderList();
		updateInitExpression();
	}

	@Override
	public Criteria getCriteria() throws ManagerBeanException {
		return criteria;
	}

	@Override
	public void setCriteria(Criteria criteria) throws ManagerBeanException {
		this.criteria = criteria;
	}

	@Override
	public void initializeModel() {
		try {
			ControllerEvent evt = new ControllerEvent(this);
			controllerListenerSupport.fireBeforeModelInitialized(evt);
			LOGGER.fine(">>>> before InitializeModel");
			if (model == null) {
				int count = getManagerBean().getCount(getCriteria());
				model = new PageDataModel(this, count, getPageLimit());
				addDataModelListeners();
			} else {
				PageDataModel pdm = (PageDataModel) model;
				pdm.setWrappedData(search(0, getPageLimit()));
				pdm.resize(getManagerBean().getCount(getCriteria()));
			}
			selectedIndex = -1;
			LOGGER.fine("initializeModel RowCount[" + model.getRowCount() + "]");
			controllerListenerSupport.fireAfterModelInitialized(evt);
		} catch (ControllerListenerException e) {
			LOGGER.severe(">>>> initializeModel " + e.getMessage());
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.severe(">>>> initializeModel " + e.getMessage());
			addMessage(e.getMessage());
			try {
				clearCriteria();
			} catch (ManagerBeanException e1) {
				LOGGER.severe(">>>> Unable to clear crtieria! " + e.getMessage());
				addMessage(e.getMessage());
			}
			throw new AbortProcessingException(e.getMessage(), e);
		}

	}

	/**
	 * Adds the defined listeners to the model instance.
	 */
	private void addDataModelListeners() {
		List<DataModelListener> list = getDataModelListeners();
		if (list != null && !list.isEmpty()) {
			for (DataModelListener listener: list) {
				this.model.addDataModelListener(listener);
			}
		}
	}

	/**
	 * Set the current <code>ITransferObject</code> of the
	 * <code>DataModel</code> to the controller.
	 * 
	 * @param value
	 */
	protected void setTo(ITransferObject value) {
		this.to = value;
	}

	/**
	 * Reset the <code>ITransferObject</code> value.
	 * 
	 */
	protected void resetTo() {
		this.to = null;
		setNew(false);
	}

	/**
	 * Add current <code>ITransferObject</code>.
	 * 
	 * @return ITransferObject
	 * @throws ManagerBeanException
	 */
	protected ITransferObject add() throws ManagerBeanException {
		LOGGER.fine("Adding Id:[" + getTo() + "]");
		ITransferObject inserted = getManagerBean().insert(getTo());
		saveState(inserted);
		return inserted;
	}

	/**
	 * Update current <code>ITransferObject</code>.
	 * 
	 * @return ITransferObject
	 * @throws ManagerBeanException
	 */
	protected ITransferObject update() throws ManagerBeanException {
		LOGGER.fine("Setting Id:[" + getTo() + "]");
		ITransferObject updated = getManagerBean().update(getTo());
		saveState(updated);
		return updated;
	}

	/**
	 * Remove current <code>ITransferObject</code>.
	 * 
	 * @throws ManagerBeanException
	 */
	protected void remove() throws ManagerBeanException {
		LOGGER.fine("Removing Id:[" + getTo() + "]");
		getManagerBean().remove(getTo());
	}

	@Override
	public List<ITransferObject> search(int start, int count) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		LOGGER.info("Searching Expression:[" + ((criteria != null) ? criteria.toString() : null)
				+ ",start=" + start + ",count=" + count + "]");
		List<ITransferObject> list = getManagerBean().getList(criteria, start, count);
		return list;
	}

	/**
	 * Add listener to controller.
	 * 
	 * @param listener
	 */
	public void addControllerListener(IControllerListener listener) {
		LOGGER.info("Listener registered " + listener);
		controllerListenerSupport.addControllerListener(listener);
	}

	/**
	 * Remove listener from controller.
	 * 
	 * @param listener
	 */
	public void removeControllerListener(IControllerListener listener) {
		LOGGER.info("Listener removed " + listener);
		controllerListenerSupport.removeControllerListener(listener);
	}

	/**
	 * Add all listeners from variable listenerClasses to controller.
	 * 
	 */
	private void addListeners() {
		Iterator<IControllerListener> iter = listenerClasses.iterator();
		while (iter.hasNext()) {
			IControllerListener listener = iter.next();
			listener.setController(this);
			this.addControllerListener(listener);
		}
	}

	/**
	 * Get a collection that contains current <code>ITransferObject</code>
	 * associated to controller. To use in reports.
	 * 
	 * @return Collection
	 */
	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		if (this.getTo() != null) {
			List<ITransferObject> l = new LinkedList<ITransferObject>();
			l.add(getTo());
			return l;
		}
		return null;
	}

	/**
	 * Get a collection that contains current <code>ITransferObject</code>
	 * associated to controller. To use in reports.
	 * 
	 * @return Collection
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unchecked")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		if (!forceRefresh) {
			return this.getCollection();
		}
		if (this.getTo() != null) {
			List<ITransferObject> l = new LinkedList<ITransferObject>();
			ITransferObject refreshed = getManagerBean().get(this.savedToId);
			l.add(refreshed);
			return l;
		}
		return null;
	}

	/**
	 * Get the <code>List<ITransferObject></code> wrapped by the model
	 * associated to controller.
	 * 
	 * @return List<ITransferObject>
	 */
	@SuppressWarnings("unchecked")
	public List<ITransferObject> getWrappedList() {
		if (this.model != null) {
			return (List<ITransferObject>) this.model.getWrappedData();
		}
		return Collections.emptyList();
	}

	/**
	 * Sets the save state.
	 * 
	 * @param value
	 *            the value
	 */
	public void setSaveState(boolean value) {
		this.saveState = value;
	}

	/**
	 * Save state.
	 * 
	 * @param to
	 *            the to
	 */
	protected void saveState(ITransferObject to) {
		if (this.saveState) {
			try {
				Serializable id = getManagerBean().getId(to);
				this.savedToId = (Serializable) SerializationUtils.clone(id);
			} catch (Throwable e) {
				LOGGER.severe(">>>> saveState " + e.getMessage());
				addMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);
			}
		}
	}

	private void setRowData(ITransferObject to) throws ManagerBeanException {
		if (getModel() instanceof PageDataModel) {
			((PageDataModel) getModel()).setRowData(getSelectedIndex(), to);
		} else {
			List<ITransferObject> list = getWrappedList();
			list.set(getSelectedIndex(), to);
		}
	}

	private void restoreState() throws ManagerBeanException {
		if (this.saveState) {
			if ((this.savedToId != null) && (getSelectedIndex() != -1)) {
				ITransferObject to = getManagerBean().get(this.savedToId);
				setRowData(to);
			}
			this.savedToId = null;
		}
	}

	/**
	 * Gets the order list.
	 * 
	 * @return the order list
	 */
	public OrderByList getOrderList() {
		return orderList;
	}

	/**
	 * Sets the order list.
	 * 
	 * @param orderList
	 *            the new order list
	 */
	public void setOrderList(OrderByList orderList) {
		this.orderList = orderList;
	}

	private void updateOrderList() {
		if (orderList != null) {
			this.criteria.setOrderByList(orderList);
		}
	}

	/**
	 * Sets the order list.
	 * 
	 * @param value
	 *            the new order list
	 */
	public void setDefaultOrder(String value) {
		String[] values = StringUtils.split(value, ',');
		if (!ArrayUtils.isEmpty(values)) {
			this.orderList = new OrderByList();
			for (String part : StringUtils.split(value, ',')) {
				String[] parts = StringUtils.split(part);
				try {
					String name = getFieldName(parts[0]);
					boolean ascending = true;
					if (parts.length == 2) {
						ascending = "ASC".equalsIgnoreCase(parts[1]);
					}
					IdentExpression identifier = ExpressionUtilities.getIdentifierExpression(name);
					Order order = new Order(identifier, ascending);
					this.orderList.addOrder(order);
				} catch (ManagerBeanException e) {
					LOGGER.log(Level.SEVERE, "Error resolving alias " + parts[0], e);
				}
			}
			updateOrderList();
		}
	}

	private void updateInitExpression() {
		if (initExpressions != null) {
			for (Expression expression : initExpressions) {
				this.criteria.addExpression(expression);
			}
		}
	}

	/**
	 * Gets the inits the expressions.
	 * 
	 * @return the inits the expressions
	 */
	public List<Expression> getInitExpressions() {
		return initExpressions;
	}

	/**
	 * Sets the inits the expressions.
	 * 
	 * @param initExpressions
	 *            the new inits the expressions
	 */
	public void setInitExpressions(List<Expression> initExpressions) {
		this.initExpressions = initExpressions;
	}

	/**
	 * Sets the init expressions.
	 * 
	 * @param expressions
	 *            the expressions
	 */
	public void setDefaultExpressions(Map<String, Object> expressions) {
		if (!expressions.isEmpty()) {
			this.initExpressions = new ArrayList<Expression>();
			for (Map.Entry<String, Object> entry : expressions.entrySet()) {
				try {
					String identifier = getFieldName(entry.getKey());
					Object value = entry.getValue();
					if (value != null) {
						Expression expression = ExpressionUtilities.getExpression(value.toString(),
								identifier);
						this.initExpressions.add(expression);
					}
				} catch (ManagerBeanException e) {
					LOGGER.log(Level.SEVERE, "Error resolving alias " + entry.getKey(), e);
				} catch (ExpressionException e) {
					LOGGER.log(Level.SEVERE, "Error resolving expression " + entry.getValue(), e);
				}
			}
			updateInitExpression();
		}
	}

}