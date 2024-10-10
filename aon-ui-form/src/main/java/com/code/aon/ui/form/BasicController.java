package com.code.aon.ui.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.DataModelListener;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.TypeResolver;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.domain.IDomain;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Order;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.ast.IdentExpression;
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
		ICollectionProvider, ITemplateController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicController.class);
    public static final int DEFAULT_PAGE_LIMIT = 20;
	private Criteria criteria = new Criteria();
	private Criteria backupCriteria = null;

	private ITransferObject to;

	/** Represent the model of data that we are going to interact with */
	protected DataModel model;

	private boolean isNevv;

	private boolean queryOnStartUP;

	private boolean lookup;

	/** Represent a manager of listeners */
	private ControllerListenerSupport controllerListenerSupport;

	private int page;
	
	private Integer pageLimit;

	private int selectedIndex;

	private List<DataModelListener> dataModelListeners;

	private boolean interfaceListenersFlag;

	private boolean saveState;

	/** The saved to id. */
	protected Serializable savedToId;

	private OrderByList orderList;

	private List<Expression> initExpressions;
	
	private String afterSearchAction;

	private boolean onlySearchNewValues;
	private Serializable searchNewValuesIndex;
	
	private String backAction;
	private String backActionListener;
	
	/** A list that contains the selected objects of the model. */
	private Set<Serializable> checkList;	

	private int scroll;
	
	/**
	 * Constructor.
	 * 
	 */
	public BasicController() {
		this.controllerListenerSupport = new ControllerListenerSupport();
		this.selectedIndex = -1;
		this.saveState = true;
		this.checkList = new HashSet<Serializable>();
		setPage(1);
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
	 * Return if controller is a lookup
	 * 
	 * @return lookup
	 */
	public boolean isLookup() {
		return lookup;
	}

	/**
	 * Set if controller is a lookup
	 * 
	 * @param lookup
	 */
	public void setLookup(boolean lookup) {
		this.lookup = lookup;
	}

	/**
	 * Return the limit of page in the model associated to controller.
	 * 
	 * @return int
	 */
	public Integer getPageLimit() {
		try {
			if ( pageLimit == null ) {
				return getDefaultPageLimit();
			}
			return pageLimit;
		} catch (Exception e) {
			e.printStackTrace();
			return DEFAULT_PAGE_LIMIT;
		}
	}
	
	protected int getDefaultPageLimit() {
		return AonUtil.getConfigurationController().getPageLimit();
	}

	/**
	 * Set the limit of page in the model associated to controller.
	 * 
	 * @param pageLimit
	 */
	public void setPageLimit(Integer pageLimit) {
		if ( pageLimit == null ) {
			pageLimit = getDefaultPageLimit();
		}
		this.pageLimit = pageLimit;
	}
	
	/**
	 * Gets the back action.
	 *
	 * @return the back action
	 */
	public String backAction() {
		if ( StringUtils.isEmpty(this.backAction) ) {
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
		if (backAction == null || !backAction.equals(formAction())) {
			this.backAction = StringUtils.trimToNull(backAction);
		}
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
		this.backActionListener = StringUtils.trimToNull(expression);
	}

	/**
	 * Set a list containing the listeners associated to controller.
	 * 
	 * @param listenerClasses
	 */
	public void setListenerClasses(List<IControllerListener> listenerClasses) {
		addListeners( listenerClasses );
	}

	/**
	 * Set a list containing the optional listeners associated to controller.
	 * 
	 * @param listenerClasses
	 */
	public void setOptionalListenerClasses(List<IControllerListener> listenerClasses) {
		addListeners( listenerClasses );
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
	public Object getSelectedTO() {
		if ( this.model.isRowAvailable() ) {
			return this.model.getRowData();	
		}
		return null;
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
	public boolean isNevv() {
		return isNevv;
	}

	@Override
	public void setNevv(boolean isNevv) {
		if (isNevv) {
			this.selectedIndex = -1;
			if (this.model != null) {
				this.model.setRowIndex(this.selectedIndex);
			}
		}
		this.isNevv = isNevv;
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
		return count>0 && (count - 1)==getSelectedIndex();
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
			boolean updateModel = isNevv();
			accept();
			if (updateModel) {
				resetBackProccess();
				if (!isQueryOnStartUP() && !(this instanceof LinesController)) {
					if (!onlySearchNewValues) {
						searchNewValuesIndex = getManagerBean().getId(getTo());
						onlySearchNewValues = true;
					}
					clearCriteria();
					Criteria criteria = getCriteria();
					criteria.addGreaterThanOrEqualExpression(getIdAlias(), searchNewValuesIndex);
				}
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
	protected void synchronizeAddedPojo() throws ManagerBeanException {
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

	private void resetSearchNewValues() {
		onlySearchNewValues = false;
	}

	/**
	 * Pure accept without POJO modificactions. Usefull in the use of
	 * transactions.
	 */
	protected void accept() {
		// SE COMENTA ESTE CODIGO POR DAR UN NonUniqueObjectException.
		//String sessionName = HibernateUtil.getSessionFactoryName(getPojo());
		//boolean mustCloseSession = HibernateUtil.mustCloseSession();
		//boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		try {
			//HibernateUtil.setCloseSession( false );
			//HibernateUtil.setBeginTransaction( false  );
			//HibernateUtil.startSession(sessionName);
			//HibernateUtil.beginTransaction(sessionName);
		
			ControllerEvent evt = new ControllerEvent(this);
			if (isNevv) {
				controllerListenerSupport.fireBeforeBeanAdded(evt);
				this.to = add();
				setNevv(false);
				controllerListenerSupport.fireAfterBeanAdded(evt);
			} else {
				controllerListenerSupport.fireBeforeBeanUpdated(evt);
				this.to = update();
				controllerListenerSupport.fireAfterBeanUpdated(evt);
			}
			//HibernateUtil.commitTransaction(sessionName);
		} catch (Throwable e) {
			//try {
				//HibernateUtil.rollbackTransaction(sessionName);
			//} catch (DAOException e1) {
				//String msg = "Unable to rollback transaction!";
				//LOGGER.error(msg, e);
			//}
			LOGGER.error(">>>> onAccept ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		//} finally {
			//HibernateUtil.closeSession(sessionName);
			//HibernateUtil.setCloseSession( mustCloseSession );
			//HibernateUtil.setBeginTransaction( mustBeginTransaction );
		}
	}

	@Override
	public void onSearch(ActionEvent event) {
		try {
			resetSearchNewValues();
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
		//String sessionName = HibernateUtil.getSessionFactoryName(getPojo());
		//boolean mustCloseSession = HibernateUtil.mustCloseSession();
		//boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		try {
			//HibernateUtil.setCloseSession( false );
			//HibernateUtil.setBeginTransaction( false  );
			//HibernateUtil.startSession(sessionName);
			//HibernateUtil.beginTransaction(sessionName);
		
			ControllerEvent evt = new ControllerEvent(this);
			controllerListenerSupport.fireBeforeBeanRemoved(evt);
			remove();
			initializeModel();
			controllerListenerSupport.fireAfterBeanRemoved(evt);
			
			//HibernateUtil.commitTransaction(sessionName);
		} catch (Throwable e) {
			//try {
				//HibernateUtil.rollbackTransaction(sessionName);
			//} catch (DAOException e1) {
				//String msg = "Unable to rollback transaction!";
				//LOGGER.error(msg, e);
			//}
			LOGGER.error(">>>> onRemove exception ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		//} finally {
			//HibernateUtil.closeSession(sessionName);
			//HibernateUtil.setCloseSession( mustCloseSession );
			//HibernateUtil.setBeginTransaction( mustBeginTransaction );
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
			setNevv(true);
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
				LOGGER.error(">>>> onSelectPrevious exception: ", e);
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
			if ( index!=-1 && index<(model.getRowCount() - 1) ) {
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
			if ( getModel().getRowCount()>0 && !isInLast() ) {
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
			setNevv(false);
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

	public void addExpression( Criteria criteria, String id, String value ) throws ManagerBeanException {
		Expression expression = FormUtil.getExpression(criteria, getPojo(), resolveAlias(id), value);
		if ( expression != null ) {
			criteria.addExpression(expression);
		}
	}
	
	@Override
	public void addExpression(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			String value = event.getNewValue().toString();
			if (! StringUtils.isBlank(value)) {
				addExpression(criteria, event.getComponent().getId(), value);
			}
		}
	}
	
	@Override
	public void addTrimExpression(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			String value = event.getNewValue().toString();
			if (! StringUtils.isBlank(value)) {
				addExpression(criteria, event.getComponent().getId(), value.trim());
			}
		}
	}

	@Override
	public void addEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		Object value = event.getNewValue();
		if (value != null) {
			if (! value.getClass().equals(String.class) || ! StringUtils.isBlank(value.toString())) {
				String fieldName = resolveAlias(event.getComponent().getId());
				criteria.addEqualExpression(fieldName, value);
			}
		}
	}	

	@Override
	public void addGreaterThanOrEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		Object value = event.getNewValue();
		if (value != null) {
			if (! value.getClass().equals(String.class) || ! StringUtils.isBlank(value.toString())) {
				String fieldName = resolveAlias(event.getComponent().getId());
				criteria.addGreaterThanOrEqualExpression(fieldName, value);
			}
		}
	}	

	@Override
	public void addLessThanOrEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		Object value = event.getNewValue();
		if (value != null) {
			if (! value.getClass().equals(String.class) || ! StringUtils.isBlank(value.toString())) {
				String fieldName = resolveAlias(event.getComponent().getId());
				criteria.addLessThanOrEqualExpression(fieldName, value);
			}
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

	public Criteria getBackupCriteria() throws ManagerBeanException {
		return backupCriteria;
	}

	public void setBackupCriteria(Criteria backupCriteria) throws ManagerBeanException {
		this.backupCriteria = backupCriteria;
	}

	public void saveBackupCriteria(ActionEvent event) throws ManagerBeanException {
		setBackupCriteria(getCriteria());
	}

	public void restoreBackupCriteria(ActionEvent event) throws ManagerBeanException {
		if (getBackupCriteria() != null) {
			setCriteria(getBackupCriteria());
		}
	}

	@Override
	public void initializeModel() {
		try {
			this.checkList.clear();
			ControllerEvent evt = new ControllerEvent(this);
			controllerListenerSupport.fireBeforeModelInitialized(evt);
			LOGGER.debug(">>>> before InitializeModel");
			if (model == null) {
				model = new ExtendedPageDataModel(this);
				addDataModelListeners();
			}
			((ExtendedPageDataModel) model).update( 0, getPageLimit() ); 
			selectedIndex = -1;
			setPage(1);
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
		setNevv(false);
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
		saveBackupCriteria(null);
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
	protected void addListeners( List<IControllerListener> listenerClasses ) {
		Iterator<IControllerListener> iter = listenerClasses.iterator();
		while (iter.hasNext()) {
			IControllerListener listener = iter.next();
			if (! listener.isDisabled() ) {
				listener.setController(this);
				this.addControllerListener(listener);				
			}
		}
	}
	
	public List<IControllerListener> getListeners() {
		return controllerListenerSupport.getListeners();
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
			if (this.savedToId != null && getSelectedIndex()!=-1) {
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
	
	public void setInterfaceListenersFlag(boolean interfaceListenersFlag) {
		this.interfaceListenersFlag = interfaceListenersFlag;
	}

	@SuppressWarnings("rawtypes")
	private void addInterfaceListeners() {
		if (!interfaceListenersFlag) {
			interfaceListenersFlag = true;

			List<IControllerListener> interfaceListeners = new LinkedList<IControllerListener>();
			try {
				Class clazz = Class.forName(getPojo());
				List interfaces = ClassUtils.getAllInterfaces(clazz);
				for (Object interfaz : interfaces) {
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
		if (!StringUtils.isEmpty(this.backActionListener) ) {
			String expression = "#{" + this.backActionListener + "}";
			AonUtil.actionListener(expression, event);
		} else if (!StringUtils.isEmpty(this.backAction) ) {
			onBack(event);
		} else {
			onCancel(event);
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
			initializeModel();
			if (getRowCount()==0) {
				onReset(null);
				return formAction();
			} else if (getRowCount()==1) {
				getModel().setRowIndex(0);
				select(null);
				return formAction();
			} else if ( getPageLimit()==-1 || getRowCount()<getPageLimit() ) {
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

	private Serializable getCurrentId() throws ManagerBeanException {
		ITransferObject to = (ITransferObject) model.getRowData();
		return getManagerBean().getId(to);
	}
	
	/**
	 * Gets the if the selected row is checked.
	 * 
	 * @return the row checked
	 * @throws ManagerBeanException 
	 */
	public boolean getRowChecked() throws ManagerBeanException {
		return checkList.contains( getCurrentId() );
	}

	/**
	 * Sets the selected row checked.
	 * 
	 * @param rowChecked
	 *            the row checked
	 * @throws ManagerBeanException 
	 */
	public void setRowChecked(boolean rowChecked) throws ManagerBeanException {
		Serializable id = getCurrentId();
		if (rowChecked) {
			if (!checkList.contains(id)) {
				checkList.add(id);
			}
		} else {
			if (checkList.contains(id)) {
				checkList.remove(id);
			}
		}
	}

	/**
	 * Gets the check list.
	 * 
	 * @return the check list
	 */
	public Collection<Serializable> getCheckList() {
		return checkList;
	}

	/**
	 * Clears the selected list.
	 * 
	 * @param event the event
	 */
	public void checkNone(ActionEvent event) {
		this.checkList.clear();
	}
	
	/**
	 * Check all.
	 * 
	 * @param event the event
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unchecked")
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		ProjectionList projectList = new ProjectionList(Projection.property(getIdAlias()));
		List<Serializable> list = getManagerBean().getList(projectList, getCriteria());
		this.checkList.clear();
		this.checkList.addAll(list);
	}	

	private boolean isCurrentDomainTo( Object object ) {
		if ( isHeritable() ) {
			return ((IDomain) object).getDomain() == DomainManager.getCurrentDomain();
		}
		return true;
	}
	
	/**
	 * Return true if an object representing the data for the currently selected row
	 * index of the model associated to controller is editable.
	 * 
	 * @return boolean
	 * @throws ManagerBeanException 
	 */
	public boolean isEditableSelectedTo() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			return isCurrentDomainTo(getModel().getRowData());
		}
		return true;
	}

	/**
	 * Return true if an object representing the data for the currently selected row
	 * index of the model associated to controller is editable.
	 * 
	 * @return boolean
	 */
	public boolean isEditableTo() {
		return isNevv() || isCurrentDomainTo(getTo());
	}

	@Override
	public int getPage() {
		return page;
	}

	@Override
	public void setPage(int page) {
		this.page = page;
	}
	
	public int getScroll() {
		return scroll;
	}
	
	public void setScroll(int scroll) {
		this.scroll = scroll;
	}
	
	public void onScroll(ActionEvent event) throws ManagerBeanException  {
		if ( scroll == 0 ) {
			onScrollTop();
		}
		else {
			onScrollBottom();
		}
	}

	private void onScrollTop() throws ManagerBeanException  {
		ExtendedPageDataModel pageModel = (ExtendedPageDataModel) getModel();
		int prevPage =  Math.max(getPage() - 1  , 1 );
		//pageModel.update(prevPage, getPageLimit());
		setPage(prevPage);
	}

	private void onScrollBottom() throws ManagerBeanException  {
		ExtendedPageDataModel pageModel = (ExtendedPageDataModel) getModel();
		int lastPage = ( getRowCount() / getPageLimit() )  - 1;
		int nextPage =  Math.min(getPage() + 1 , lastPage );
		//pageModel.update(nextPage, getPageLimit());
		setPage(nextPage);
	}
	
	
	public void addFilterExpression(ValueChangeEvent event) throws ManagerBeanException {
		

		clearCriteria();
		Object newValue = event.getNewValue();

		if (newValue != null && newValue.toString().trim().length() > 0   
				&& this.model != null && this.model instanceof ExtendedPageDataModel extendedPageDataModel) {
			
			TypeResolver typeResolver = new TypeResolver(getPojo());

			Optional<Expression> filterExpression = 
			extendedPageDataModel.getVisibleFields().stream()
			.map(BasicController::getProperty)
			.filter(alias -> isString(typeResolver,alias))
			.map( fieldName -> ExpressionUtilities.getLikeExpression(fieldName, String.format("%%%s%%",newValue)))
			.map(e -> (Expression) e ).reduce(ExpressionUtilities::getOrExpression);
			
			filterExpression.ifPresent( getCriteria()::addExpression);
			
		}

	}
	
	
	private static boolean isString(TypeResolver typeResolver, String alias ) {
		try {
			return typeResolver.isString(typeResolver.getType(alias));
		} catch (Exception e ) {
		}
		return false;
	}
	
	private static String getProperty (String valueELExpr) {
		try {
			Matcher matcher = Pattern.compile("\\.(?<property>to\\.[\\w\\.]*)", Pattern.CASE_INSENSITIVE).matcher(valueELExpr);
			return matcher.find()  ? matcher.group("property") : valueELExpr ;
		} catch (Exception e ) {
			return valueELExpr;
		}
	}
	
	
	
	
}