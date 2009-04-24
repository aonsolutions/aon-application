package com.code.aon.faces.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.AliasEntry;
import com.code.aon.common.dao.DAOConstantsResolver;
import com.code.aon.faces.component.richfaces.lookup.ILookupComponent;
import com.code.aon.faces.component.richfaces.lookup.inputText.HtmlLookupInputText;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.IControllerListener;


/**
 * LookupBean is the class used to implement a Lookup creating an SQL sentence
 * which will be executed to retrive the required data.
 */
public class RichLookupBean {

	private static final String LIST_ID = "list";

	private static final String FORM_ID = "form";

	private static final String SEARCH_ID = "search";

	private static final Logger LOGGER = Logger.getLogger(RichLookupBean.class.getName());

	/** The foreign controller. */
	private BasicController controller;

	/** The list page path. */
	private String listPagePath;

	/** The search page path. */
	private String searchPagePath;

	/** The new page path. */
	private String newPagePath;

	/** The value binding of foreign Pojo. */
	private ValueBinding sourcePojoBinding;

	private ILookupComponent component;
	
	/** The map of join value bindings. */
	// private Map<String,ValueBinding> joinBindingsMap;
	/** The show window. */
	private boolean showWindow;

	/** The show search buttons. */
	private boolean showSearchButtons;

	/** The selected panel. */
	private String selectedPanel;

	/**
	 * The Constructor.
	 */
	public RichLookupBean() {
		this.controller = new BasicController();
	}

	/**
	 * Return path of the list requested page.
	 * 
	 * @return String
	 */
	public String getListPagePath() {
		return listPagePath;
	}

	/**
	 * Set path of the list requested page.
	 * 
	 * @param listPagePath
	 */
	public void setListPagePath(String listPagePath) {
		this.listPagePath = listPagePath;
	}

	/**
	 * Return path of the new requested page.
	 * 
	 * @return String
	 */
	public String getNewPagePath() {
		return newPagePath;
	}

	/**
	 * Set path of the new requested page.
	 * 
	 * @param pagePath
	 */
	public void setNewPagePath(String pagePath) {
		this.newPagePath = pagePath;
	}

	/**
	 * Return path of the search requested page.
	 * 
	 * @return String
	 */
	public String getSearchPagePath() {
		return this.searchPagePath;
	}

	/**
	 * Set path of the search requested page.
	 * 
	 * @param pagePath
	 */
	public void setSearchPagePath(String pagePath) {
		this.searchPagePath = pagePath;
	}

	private Map<String, ValueBinding> calculateJoinBindings() {
		Map<String, ValueBinding> joinBindingsMap = new HashMap<String, ValueBinding>();
		DAOConstantsResolver resolver = new DAOConstantsResolver();
		String expression = this.sourcePojoBinding.getExpressionString();
		Application app = FacesContext.getCurrentInstance().getApplication();
		for (AliasEntry entry : resolver.getIdentifierAliasEntryList(getController().getPojo())) {
			String value = FaceletUtil.appendExpression(expression, entry.getAccessPath());
			joinBindingsMap.put(entry.getAlias(), app.createValueBinding(value));
		}
		return joinBindingsMap;
	}

	/**
	 * Gets the join bindings map.
	 * 
	 * @param event
	 *            the event
	 * 
	 * @return the join bindings map
	 */
	public Map<String, ValueBinding> getJoinBindingsMap(ValueChangeEvent event) {
		Map<String, ValueBinding> joinBindingsMap = Collections.emptyMap();
		if (event.getComponent() instanceof HtmlLookupInputText) {
			HtmlLookupInputText lookupComponent = (HtmlLookupInputText) event.getComponent();
			joinBindingsMap = lookupComponent.getJoinBindingsMap();
		}
		if (joinBindingsMap.isEmpty()) {
			joinBindingsMap = calculateJoinBindings();
		}
		return joinBindingsMap;
	}

	/**
	 * Gets the controller.
	 * 
	 * @return the controller
	 */
	public BasicController getController() {
		return controller;
	}

	/**
	 * Sets the foreign pojo.
	 * 
	 * @param foreignPojo
	 *            the foreign pojo
	 */
	public void setForeignPojo(String foreignPojo) {
		getController().setPojo(foreignPojo);
	}

	/**
	 * Return name of the bean associated to controller.
	 * 
	 * @return String
	 */
	public String getBeanName() {
		return getController().getBeanName();
	}

	/**
	 * Set the name of the bean associated to controller.
	 * 
	 * @param beanName
	 */
	public void setBeanName(String beanName) {
		getController().setBeanName(beanName);
	}

	/**
	 * Set the limit of page in the model associated to controller.
	 * 
	 * @param pageLimit
	 *            the page limit
	 */
	public void setPageLimit(int pageLimit) {
		getController().setPageLimit(pageLimit);
	}

	/**
	 * Set if a query will be executed on the model associated to controller
	 * when starting up.
	 * 
	 * @param queryOnStartUP
	 *            the query on start UP
	 */
	public void setQueryOnStartUP(boolean queryOnStartUP) {
		getController().setQueryOnStartUP(queryOnStartUP);
	}

	/**
	 * Return the model associated to controller. The model represents a list of
	 * <code>ITransferObject</code> with which we will be able to interact.
	 * 
	 * @return DataModel
	 * 
	 * @throws ManagerBeanException
	 *             the manager bean exception
	 */
	public DataModel getModel() throws ManagerBeanException {
		return getController().getModel();
	}

	/**
	 * Add listener to controller.
	 * 
	 * @param listener
	 */
	public void addControllerListener(IControllerListener listener) {
		getController().addControllerListener(listener);
	}

	/**
	 * Remove listener from controller.
	 * 
	 * @param listener
	 */
	public void removeControllerListener(IControllerListener listener) {
		getController().removeControllerListener(listener);
	}

	/**
	 * Set a list containing the listeners associated to controller.
	 * 
	 * @param listenerClasses
	 */
	public void setListenerClasses(List<IControllerListener> listenerClasses) {
		getController().setListenerClasses(listenerClasses);
	}

	/**
	 * Add a new expression to the criteria to condition the following searches.
	 * The id component is managed as an alias to resolve the real property
	 * path.
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void addExpression(ValueChangeEvent event) throws ManagerBeanException {
		getController().addExpression(event);
	}

	/**
	 * Add a new expression to the criteria to condition the following searches.
	 * The id component is managed as the property path, replacing
	 * <code>_</code> with <code>.</code>.
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void addDirectExpression(ValueChangeEvent event) throws ManagerBeanException {
		getController().addDirectExpression(event);
	}

	/**
	 * Execute cancel action.
	 * 
	 * @param event
	 */
	public void onCancel(ActionEvent event) {
		getController().onCancel(event);
	}

	/**
	 * Execute search action.
	 * 
	 * @param event
	 *            the event
	 */
	public void onSearch(ActionEvent event) {
		getController().onSearch(event);
	}

	/**
	 * Execute reset action.
	 * 
	 * @param event
	 */
	public void onReset(ActionEvent event) {
		getController().onReset(event);
	}

	/**
	 * Execute selection action.
	 * 
	 * @param event
	 */
	public void onSelect(ActionEvent event) {
		getController().onSelect(event);
	}

	/**
	 * Execute search edition action.
	 * 
	 * @param event
	 */
	public void onEditSearch(ActionEvent event) {
		getController().onEditSearch(event);
	}

	/**
	 * Return the state of bean. True if bean is in state 'New', otherwise
	 * false.
	 * 
	 * @return boolean
	 */
	public boolean isNew() {
		return getController().isNew();
	}

	/**
	 * Sets the state of bean.
	 * 
	 * @param isNew
	 */
	public void setNew(boolean isNew) {
		getController().setNew(isNew);
	}

	/**
	 * Action to insert or to update the current row depending on the value of
	 * the variable isNew.
	 * 
	 * @param event
	 */
	public void accept(ActionEvent event) {
		getController().accept(event);
	}

	/**
	 * Return <code>ITransferObject</code> associated to controller.
	 * 
	 * @return ITransferObject.
	 */
	public ITransferObject getTo() {
		return getController().getTo();
	}

	/**
	 * Checks if is show window.
	 * 
	 * @return true, if is show window
	 */
	public boolean isShowWindow() {
		return showWindow;
	}

	/**
	 * Sets the show window.
	 * 
	 * @param showPopup
	 *            the show popup
	 */
	public void setShowWindow(boolean showPopup) {
		this.showWindow = showPopup;
	}

	/**
	 * Gets the values map.
	 * 
	 * @return the values map
	 */
	private Map<String, Object> getValuesMap(Map<String, ValueBinding> joinBindingsMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		FacesContext ctx = FacesContext.getCurrentInstance();
		for (Entry<String, ValueBinding> entry : joinBindingsMap.entrySet()) {
			Object value = entry.getValue().getValue(ctx);
			map.put(entry.getKey(), value);
		}
		return map;
	}

	/**
	 * Gets the criteria.
	 * 
	 * @return the criteria
	 * @throws ManagerBeanException
	 */
	private Criteria getCriteria(Map<String, Object> valuesMap) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		IManagerBean bean = getController().getManagerBean();
		for (Entry<String, Object> entry : valuesMap.entrySet()) {
			String fieldName = bean.getFieldName(entry.getKey());
			if (entry.getValue() != null) {
				criteria.addEqualExpression(fieldName, entry.getValue());
			} else {
				criteria.addNullExpression(fieldName);
			}
		}
		return criteria;
	}

	private void restoreValues(Map<String, ValueBinding> joinBindingsMap, Map<String, Object> valuesMap) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		for (Entry<String, ValueBinding> entry : joinBindingsMap.entrySet()) {
			Object value = valuesMap.get(entry.getKey());
			entry.getValue().setValue(ctx, value);
		}
	}
	
	private Object getCurrentSourcePojo() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		try {
			return this.sourcePojoBinding.getValue(ctx);
		} catch ( Throwable th ) {
			LOGGER.fine( this.sourcePojoBinding + " is possibly null" );
		}
		return null;
	}
	
	private void fireValueChangeListener(UIComponent component) {
		if ( getButtonValueChangeListener() != null ) {
			ValueChangeEvent event = null;
			FacesContext ctx = FacesContext.getCurrentInstance();
			if ( getController().isNew() ) {
				event = new ValueChangeEvent(component, null, getController().getTo() );
			} else {
				Object newValue = null;
				try {
					newValue = getController().getModel().getRowData();
				} catch (ManagerBeanException e) {
					LOGGER.severe( e.getMessage() );
				}
				Object oldValue = getCurrentSourcePojo();
				event = new ValueChangeEvent(component, oldValue, newValue );
			}
			getButtonValueChangeListener().invoke(ctx, new Object[]{event});
		}
	}

	private void updateSourcePojo() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		sourcePojoBinding.setValue(ctx, getController().getTo());
	}
	
	/**
	 * Lookup changed.
	 * 
	 * @param event
	 *            the event
	 * @throws ManagerBeanException
	 */
	public void lookupChanged(ValueChangeEvent event) throws ManagerBeanException {
		LOGGER.info("lookupChanged: " + event.getNewValue() + " old: " + event.getOldValue());
		boolean restoreValues = false;
		setBindings(event.getComponent());
		Map<String, ValueBinding> joinBindingsMap = getJoinBindingsMap(event);
		Map<String, Object> valuesMap = getValuesMap(joinBindingsMap);
		Criteria criteria = getCriteria(valuesMap);
		getController().setCriteria(criteria);
		onSearch(null);
		if (getModel().getRowCount() == 1) {
			getController().getModel().setRowIndex(0);
			onSelect(null);
		} else {
			onReset(null);
			restoreValues = true;
		}
		updateSourcePojo();
		if (restoreValues) {
			restoreValues(joinBindingsMap, valuesMap);
		}
	}

	/**
	 * Gets the parent binding.
	 * 
	 * @param vb
	 *            the vb
	 * @param ctx
	 *            the ctx
	 * 
	 * @return the parent binding
	 */
	private ValueBinding getParentBinding(FacesContext ctx, ValueBinding vb) {
		String parentExpression = vb.getExpressionString();
		int pos = parentExpression.lastIndexOf('.');
		if (pos != -1) {
			String expression = parentExpression.substring(0, pos) + "}";
			return ctx.getApplication().createValueBinding(expression);
		}
		return null;
	}

	/**
	 * Gets the foreign binding.
	 * 
	 * @param event
	 *            the event
	 * 
	 * @return the foreign binding
	 */
	private ValueBinding getSourcePojoBinding(UIComponent component) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ValueBinding vb = component.getValueBinding("value");
		while (vb != null) {
			String type = vb.getType(ctx).getName();
			if (type.equals(this.controller.getPojo())) {
				return vb;
			} else {
				vb = getParentBinding(ctx, vb);
			}
		}
		return null;
	}

	private void setBindings(UIComponent component) {
		if (component instanceof ILookupComponent) {
			this.component = (ILookupComponent) component;
			this.sourcePojoBinding = this.component.getProperty();
			if (this.sourcePojoBinding == null) {
				this.sourcePojoBinding = getSourcePojoBinding(component);
			}
		}
	}

	/**
	 * On show lookup list window.
	 * 
	 * @param event
	 *            the event
	 * @throws ManagerBeanException
	 */
	public void onShowListWindow(ActionEvent event) throws ManagerBeanException {
		setBindings(event.getComponent());
		setShowWindow(true);
		setSelectedPanel(LIST_ID);
		getController().clearCriteria();
		onSearch(null);
		this.showSearchButtons = false;
	}

	/**
	 * On show lookup search window.
	 * 
	 * @param event
	 *            the event
	 */
	public void onShowSearchWindow(ActionEvent event) {
		setBindings(event.getComponent());
		setShowWindow(true);
		setSelectedPanel(SEARCH_ID);
		onEditSearch(null);
		this.showSearchButtons = true;
	}

	/**
	 * On show lookup form window.
	 * 
	 * @param event
	 *            the event
	 */
	public void onShowNewWindow(ActionEvent event) {
		setBindings(event.getComponent());
		setShowWindow(true);
		setSelectedPanel(FORM_ID);
		onReset(null);
	}

	/**
	 * On filter search.
	 * 
	 * @param event
	 *            the event
	 */
	public void onFilterSearch(ActionEvent event) {
		setSelectedPanel(LIST_ID);
		onSearch(event);
	}

	/**
	 * On list cancel.
	 * 
	 * @param event
	 *            the event
	 */
	public void onListCancel(ActionEvent event) {
		setSelectedPanel(SEARCH_ID);
		onEditSearch(event);
	}

	/**
	 * Row selection.
	 * 
	 * @param event
	 *            the event
	 */
	public void onListSelect(ActionEvent event) {
		fireValueChangeListener(event.getComponent());
		onSelect(null);
		updateSourcePojo();
		setShowWindow(false);
		clearModel();
	}

	/**
	 * Row selection.
	 * 
	 * @param event
	 *            the event
	 */
	public void onFormSelect(ActionEvent event) {
		LOGGER.info("onFormSelect: " + getController().getTo());
		fireValueChangeListener(event.getComponent());
		updateSourcePojo();
		setShowWindow(false);
		clearModel();
	}

	/**
	 * On close window.
	 * 
	 * @param event
	 *            the event
	 */
	public void onCloseWindow(ActionEvent event) {
		setShowWindow(false);
		clearModel();
	}

	/**
	 * Gets the selected panel.
	 * 
	 * @return the selected panel
	 */
	public String getSelectedPanel() {
		return selectedPanel;
	}

	/**
	 * Sets the selected panel.
	 * 
	 * @param selectedPanel
	 *            the selected panel
	 */
	public void setSelectedPanel(String selectedPanel) {
		this.selectedPanel = selectedPanel;
	}

	/**
	 * Checks if is show search buttons.
	 * 
	 * @return true, if is show search buttons
	 */
	public boolean isShowSearchButtons() {
		return showSearchButtons;
	}

	private void clearModel() {
		getController().setModel(null);
	}

	public MethodBinding getButtonValueChangeListener() {
		return this.component.getValueChangeListener();
	}

	public ILookupComponent getComponent() {
		return component;
	}
	
}