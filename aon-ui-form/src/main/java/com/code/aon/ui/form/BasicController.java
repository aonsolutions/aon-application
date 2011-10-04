package com.code.aon.ui.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.DataModelListener;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.TypeResolver;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Order;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.ast.ConstantExpression;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.ast.IdentExpression;
import com.code.aon.ql.ast.RelationalExpression;
import com.code.aon.ql.ast.RelationalType;
import com.code.aon.ql.ast.impl.ConstantExpressionImpl;
import com.code.aon.ql.ast.impl.RelationalExpressionImpl;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.ControllerListenerSupport;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.form.listener.ConfidentialityFilterListener;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller for Basic Structures.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 06-abr-2005
 */
public class BasicController extends AbstractPojoController implements IController,
		ICollectionProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicController.class);
	
	/** Default limit of rows to be load from de data source. */
    public static final int LIMIT = 20;	

	private Criteria criteria = new Criteria();

	private ITransferObject to;

	/** Represent the model of data that we are going to interact with */
	protected DataModel model;

	private boolean isNew;

	private boolean queryOnStartUP;

	/** Represent a manager of listeners */
	protected ControllerListenerSupport controllerListenerSupport;

	private int pageLimit = LIMIT;

	private int selectedIndex;

	private List<IControllerListener> listenerClasses;
	
	private List<IControllerListener> optionalListenerClasses;

	private List<DataModelListener> dataModelListeners;

	private boolean interfaceListenersFlag;

	private boolean saveState;

	/** The saved to id. */
	protected Serializable savedToId;

	private OrderByList orderList;

	private List<Expression> initExpressions;
	
	private String backAction;
	private String backActionListener;
	
	private String afterSearchAction; 

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
	 * Gets the back action.
	 *
	 * @return the back action
	 */
	public String backAction() {
		if ( this.backAction == null ) {
			return listAction();
		}
		return backAction;
	}

	/**
	 * Gets the back action.
	 *
	 * @return the back action
	 */
	public String afterSearchAction() {
		if ( this.afterSearchAction == null ) {
			return listAction();
		}
		return afterSearchAction;
	}

	/**
	 * On after search.
	 *
	 * @param event the event
	 */
	public void onAfterSearch( ActionEvent event ) {
		try {
			if ( getModel().getRowCount() == 1 ) {
				getModel().setRowIndex(0);
				onSelect(event);
				setAfterSearchAction(formAction());
			} else {
				setAfterSearchAction(null);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onAfterSearch",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}		
	}	
	
	/**
	 * Gets the after search action.
	 *
	 * @return the after search action
	 */
	public String getAfterSearchAction() {
		return afterSearchAction;
	}

	/**
	 * Sets the after search action.
	 *
	 * @param afterSearchAction the new after search action
	 */
	public void setAfterSearchAction(String afterSearchAction) {
		this.afterSearchAction = afterSearchAction;
	}

	/**
	 * Gets the back action.
	 * 
	 * @return the back action
	 */
	public String getBackAction() {
		return backAction;
	}

	/**
	 * Sets the back action.
	 *
	 * @param backAction the new back action
	 */
	public void setBackAction(String backAction) {
		this.backAction = backAction;
	}

	/**
	 * Gets the back action listener.
	 * 
	 * @return the back action listener
	 */
	public String getBackActionListener() {
		return backActionListener;
	}

	/**
	 * Sets the back action listener.
	 *
	 * @param expression the new back action listener
	 */
	public void setBackActionListener(String expression) {
		this.backActionListener = expression;
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
		addListeners( this.listenerClasses );
	}

	/**
	 * Return a list containing the optional listeners associated to controller.
	 * 
	 * @return List<IControllerListener>
	 */
	public List<IControllerListener> getOptionalListenerClasses() {
		return optionalListenerClasses;
	}

	/**
	 * Set a list containing the optional listeners associated to controller.
	 * 
	 * @param listenerClasses
	 */
	public void setOptionalListenerClasses(List<IControllerListener> listenerClasses) {
		this.optionalListenerClasses = listenerClasses;
		addListeners( this.optionalListenerClasses );
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
				this.model = new ExtendedPageDataModel(this);
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
			getManagerBean().restoreNullSubPOJOs(getTo());
			boolean updateModel = isNew();
			accept();
			if (updateModel) {
				initializeModel();
				synchronizeAddedPojo();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onAccept",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			try {
				getManagerBean().initializePOJO(this.to);
			} catch (ManagerBeanException e) {
				LOGGER.error(">>>> onAccept initializePOJO ",e);
				addMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);
			}
		}

	}

	/**
	 * Method that synchronizes current TO with its corresponding asset in the model, so that changes made in current TO will be reflected
	 * in the model too.
	 */
	@SuppressWarnings("unchecked")
	private void synchronizeAddedPojo() throws ManagerBeanException {
		if (getModel() instanceof ExtendedPageDataModel) {
			List<ITransferObject> list = (List<ITransferObject>)getModel().getWrappedData();
			for (ITransferObject to : list) {
				if (to.equals(this.getTo())) {
					this.to = to;
					break;
				}
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
				setNew(false);
				controllerListenerSupport.fireAfterBeanAdded(evt);
			} else {
				controllerListenerSupport.fireBeforeBeanUpdated(evt);
				this.to = update();
				controllerListenerSupport.fireAfterBeanUpdated(evt);
			}
		} catch (ControllerListenerException e) {
			LOGGER.error(">>>> onAccept ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onAccept ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public void onSearch(ActionEvent event) {
		try {
			resetBackProccess();
			ControllerEvent evt = new ControllerEvent(this);
			controllerListenerSupport.fireBeforeModelSearched(evt);
			controllerListenerSupport.fireBeforeBeanReset(evt);
			initializeModel();
			resetTo();
			controllerListenerSupport.fireAfterBeanReset(evt);
			controllerListenerSupport.fireAfterModelSearched(evt);
		} catch (ControllerListenerException e) {
			LOGGER.error(">>>> onSearch ",e);
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
			LOGGER.error(">>>> onRemove exception ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onRemove exception ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	

	@Override
	public void onBack(ActionEvent event) {
		cancel(event);
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
			LOGGER.error(">>>> onCancel ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onCancel ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public void onReset(ActionEvent event) {
		try {
			resetBackProccess();
			ControllerEvent evt = new ControllerEvent(this);
			setTo(getManagerBean().createNewTo());
			controllerListenerSupport.fireBeforeBeanCreated(evt);
			setNew(true);
			controllerListenerSupport.fireAfterBeanCreated(evt);
		} catch (ControllerListenerException e) {
			LOGGER.error(">>>> onReset ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onReset ",e);
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
			LOGGER.error(">>>> onEditSearch ", e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onEditSearch ", e);
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
				LOGGER.error(">>>> onSelectFirst exception: ", e);
				addMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);
			}
			select(event);
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
				LOGGER.error(">>>> onSelectFirst exception: ", e);
				addMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);
			}
			select(event);
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
				select(event);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectFirst exception: ", e);
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
				select(event);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectFirst exception: ", e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public void onSelect(ActionEvent event) {
		resetBackProccess();
		select(event);
	}

	/**
	 * Execute selection action.
	 *
	 * @param event the event
	 */
	public void select(ActionEvent event) {
		try {
			ControllerEvent evt = new ControllerEvent(this);
			controllerListenerSupport.fireBeforeBeanSelected(evt);
			selectedIndex = getSelectedTOIndex();
			LOGGER.debug(">>>> onSelect rowIndex: {}",selectedIndex);
			ITransferObject to = (ITransferObject) getSelectedTO();
			getManagerBean().initializePOJO(to);
			setTo(to);
			setNew(false);
			controllerListenerSupport.fireAfterBeanSelected(evt);
			saveState(to);
		} catch (ControllerListenerException e) {
			LOGGER.error(">>>> onSelect exception: ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelect exception: ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	@Override
	public ITransferObject getTo() {
		return this.to;
	}
	
	private boolean isOnlyTextExpression( Expression expression, String fieldName ) {
		if ( expression instanceof RelationalExpression ) {
			RelationalExpression re = (RelationalExpression) expression;
			if ( (re.getType() == RelationalType.EQUAL) &&  
				(re.getLeftExpression() instanceof IdentExpression) &&
				(re.getRightExpression() instanceof ConstantExpression) ) {
				TypeResolver typeResolver = new TypeResolver(getPojo());
				Type type = typeResolver.getType(fieldName);
				return typeResolver.isString(type);
			}
		}			
		return false;
	}
	
	private void updateTextExpression( Expression expression ) {
		RelationalExpressionImpl re = (RelationalExpressionImpl) expression;
		re.setType(RelationalType.LIKE);
		ConstantExpressionImpl ce = (ConstantExpressionImpl) re.getRightExpression();
		ce.setData( "%" + ce.getData().toString() + "%" );
	}

	@Override
	public void addExpression(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			String value = event.getNewValue().toString();
			if (! StringUtils.isBlank(value) ) {
				String fieldName = resolveAlias(event.getComponent().getId()); 
				try {
					Expression exp = ExpressionUtilities.getExpression(value, fieldName);
					if ( isOnlyTextExpression(exp, fieldName) ) {
						updateTextExpression(exp);
					}
					criteria.addExpression(exp);
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
			LOGGER.debug(">>>> before InitializeModel");
			if (model == null) {
				model = new ExtendedPageDataModel(this);
				addDataModelListeners();
			}
			((ExtendedPageDataModel) model).update( 0, getPageLimit() ); 
			selectedIndex = -1;
			LOGGER.debug("initializeModel RowCount {}",model.getRowCount());
			controllerListenerSupport.fireAfterModelInitialized(evt);
		} catch (ControllerListenerException e) {
			LOGGER.error(">>>> initializeModel ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> initializeModel ",e);
			addMessage(e.getMessage());
			try {
				clearCriteria();
			} catch (ManagerBeanException e1) {
				LOGGER.error(">>>> Unable to clear crtieria! ",e);
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
		LOGGER.debug("Adding Id: [{}]",getTo());
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
		LOGGER.debug("Setting Id: [{}]",getTo());
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
		LOGGER.debug("Removing Id: [{}]",getTo());
		getManagerBean().remove(getTo());
	}

	@Override
	public List<ITransferObject> search(int start, int count) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		LOGGER.info("search:[{},{},start={},count={}]", new Object[]{getBeanName(), ((criteria != null) ? criteria.toString() : null), start, count} );
		List<ITransferObject> list = getManagerBean().getList(criteria, start, count);
		return list;
	}

	/**
	 * Gets the row count.
	 * 
	 * @return the row count
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public int getRowCount() throws ManagerBeanException {
		Criteria criteria = getCriteria();
		LOGGER.debug("rowCount:[{}]", (criteria != null) ? criteria.toString() : null );
		return getManagerBean().getCount(criteria);
		
	}
	
	/**
	 * Add listener to controller.
	 * 
	 * @param listener
	 */
	public void addControllerListener(IControllerListener listener) {
		LOGGER.debug("Listener registered " + listener);
		controllerListenerSupport.addControllerListener(listener);
	}

	/**
	 * Remove listener from controller.
	 * 
	 * @param listener
	 */
	public void removeControllerListener(IControllerListener listener) {
		LOGGER.debug("Listener removed " + listener);
		controllerListenerSupport.removeControllerListener(listener);
	}

	/**
	 * Add all listeners from variable listenerClasses to controller.
	 * 
	 */
	private void addListeners( List<IControllerListener> listenerClasses ) {
		Iterator<IControllerListener> iter = listenerClasses.iterator();
		while (iter.hasNext()) {
			IControllerListener listener = iter.next();
			if (! listener.isDisabled() ) {
				listener.setController(this);
				this.addControllerListener(listener);				
			}
		}
	}

	/**
	 * Get a collection that contains current <code>ITransferObject</code>
	 * associated to controller. To use in reports.
	 * 
	 * @return Collection
	 */
	public Collection<ITransferObject> getCollection() {
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
	public Collection<ITransferObject> getCollection(boolean forceRefresh) throws ManagerBeanException {
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
				LOGGER.error(">>>> saveState ",e);
				addMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Sets the row data.
	 *
	 * @param to the new row data
	 * @throws ManagerBeanException the manager bean exception
	 */
	protected void setRowData(ITransferObject to) throws ManagerBeanException {
		if (getModel() instanceof ExtendedPageDataModel) {
			((ExtendedPageDataModel) getModel()).setRowData(getSelectedIndex(), to);
		} else {
			List<ITransferObject> list = getWrappedList();
			list.set(getSelectedIndex(), to);
		}
	}

	private void restoreState() throws ManagerBeanException {
		if (this.saveState) {
			if ((this.savedToId != null) && (getSelectedIndex() != -1)) {
				setTo( getManagerBean().get(this.savedToId) );
				setRowData( getTo() );
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
			OrderByList list = new OrderByList( orderList.getOrders() );
			this.criteria.setOrderByList( list );
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
				String name = resolveAlias(parts[0]);
				boolean ascending = true;
				if (parts.length == 2) {
					ascending = "ASC".equalsIgnoreCase(parts[1]);
				}
				IdentExpression identifier = ExpressionUtilities.getIdentifierExpression(name);
				Order order = new Order(identifier, ascending);
				this.orderList.add(order);
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
					String identifier = resolveAlias(entry.getKey());
					Object value = entry.getValue();
					if (value != null) {
						Expression expression = ExpressionUtilities.getExpression(value.toString(),
								identifier);
						this.initExpressions.add(expression);
					}
				} catch (ExpressionException e) {
					LOGGER.error("Error resolving expression {}: {}",entry.getValue(), e.getMessage());
				}
			}
			updateInitExpression();
		}
	}

	@Override
	public void setPojo(String bean) {
		super.setPojo(bean);
		addInterfaceListeners();
	}

	@SuppressWarnings("rawtypes")
	private void addInterfaceListeners() {
		if (!interfaceListenersFlag) {
			interfaceListenersFlag = true;

			List<IControllerListener> interfaceListeners = new LinkedList<IControllerListener>();
			try {
				Class clazz = Class.forName(getPojo());
				Class[] interfaces = clazz.getInterfaces();
				for (Class interfaz : interfaces) {
					if (IConfidentialable.class.equals(interfaz) && !AonUtil.getRoleManager().isConfidentiality()) {
						interfaceListeners.add(new ConfidentialityFilterListener());
					}
				}
				addListeners(interfaceListeners);
			} catch (ClassNotFoundException e) {
				LOGGER.error(">>>> ClassNotFoundException for pojo: " + getPojo(), e);
			}
		}
	}
	
	/**
	 * Refresh current TO.
	 * 
	 * @param event the event
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void refresh( ActionEvent event ) throws ManagerBeanException {
		setTo(getManagerBean().get(getManagerBean().getId(getTo())));
		getManagerBean().initializePOJO(getTo());
		selectedIndex = (getSelectedIndex() != -1) ? getSelectedIndex() : getSelectedTOIndex();
		if (getSelectedIndex() != -1) {
			setRowData(getTo());
		}
	}
	
	/**
	 * Select an ITransferObject.
	 * 
	 * @param event the event
	 * @param to the to
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void select( ActionEvent event, ITransferObject to ) throws ManagerBeanException {
		select( event, getManagerBean().getId(to) );
	}
	
	/**
	 * Select an ITransferObject.
	 * 
	 * @param event the event
	 * @param id the id of the TransferObject
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void select( ActionEvent event, Serializable id ) throws ManagerBeanException {
		clearCriteria();
		Criteria criteria = getCriteria();
		String alias = getIdAlias();
		criteria.addEqualExpression(alias, id);
		initializeModel();
		getModel().setRowIndex(0);
		select(event);		
	}	

	private void resetBackProccess() {
		setBackAction(null);
		setBackActionListener(null);
	}
	
    /**
     * Execute default or defined back action.
     * 
     * @param event
     */
	public void onBackActionListener(ActionEvent event) {
		if ( this.backActionListener == null ) {
			onCancel(event);
		} else if (! StringUtils.isEmpty(this.backActionListener) ) {
			String expression = "#{" + this.backActionListener + "}";
			AonUtil.actionListener(expression, event);
		}
	}	
	
	/**
	 * Form action.
	 *
	 * @return the string
	 */
	public String formAction() {
		return getBeanName()+FORM_SUFFIX;
	}

	/**
	 * List action.
	 *
	 * @return the string
	 */
	public String listAction() {
		return getBeanName()+LIST_SUFFIX;
	}

	/**
	 * Search action.
	 *
	 * @return the string
	 */
	public String searchAction() {
		return getBeanName()+SEARCH_SUFFIX;
	}
	
	/**
	 * Gets the initial action
	 * 
	 * @return the initial action
	 */
	public String initialAction(){
		try {
			if(getRowCount()==1){
				initializeModel();
				getModel().setRowIndex(0);
				select(null);
				return formAction();
			} else if(getRowCount()<LIMIT){
				initializeModel();
				return listAction();
			} else {
				return searchAction();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			return searchAction();
		}
	}

    /**
     * Execute load action when navigation comes from other controller.
     * 
	 * @param event the event
	 * @param id the id of the TransferObject
	 * @param backAction the backAction
	 * @param backActionListener the backActionListener
 	 * @throws ManagerBeanException the manager bean exception
    */
	public void onLoad(ActionEvent event, Serializable id, String backAction, String backActionListener) throws ManagerBeanException {
		load(event, id);
		setBackAction(backAction);
		setBackActionListener(backActionListener);
	}

    /**
     * Execute load action.
     * 
	 * @param event the event
	 * @param id the id of the TransferObject
 	 * @throws ManagerBeanException the manager bean exception
    */
	public void load(ActionEvent event, Serializable id) throws ManagerBeanException {
		clearCriteria();
		Criteria criteria = getCriteria();
		String alias = getIdAlias();
		criteria.addEqualExpression(alias, id);
		this.model = new ExtendedPageDataModel(this);
		getModel().setRowIndex(0);
		select(event);		
	}

}