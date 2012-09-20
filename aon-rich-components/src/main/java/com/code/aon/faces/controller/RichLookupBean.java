package com.code.aon.faces.controller;

import static com.code.aon.faces.controller.IRichConstants.SEARCH_NO_RESULTS;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.component.richfaces.lookup.ILookupComponent;
import com.code.aon.faces.component.richfaces.lookup.button.HtmlLookupButton;
import com.code.aon.faces.component.richfaces.lookup.button.LookupButtonType;
import com.code.aon.faces.component.richfaces.lookup.inputText.HtmlLookupInputText;
import com.code.aon.faces.component.richfaces.lookup.suggestText.HtmlLookupSuggestText;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;


/**
 * LookupBean is the class used to implement a Lookup creating an SQL sentence
 * which will be executed to retrive the required data.
 */
public class RichLookupBean {

	private static final String LIST_ID = LookupButtonType.LIST.getName();

	private static final String NEW_ID = LookupButtonType.NEW.getName();

	private static final String SEARCH_ID = LookupButtonType.SEARCH.getName();
	
	private static final String DEFAULT_WINDOW_TITLE = "Select Window";
	
	private static final int DEFAULT_PAGE_LIMIT = 15;

	private final static Logger LOGGER = LoggerFactory.getLogger(RichLookupBean.class);

	/** The foreign controller. */
	private BasicController controller;

	/** The list page path. */
	private String listPagePath;

	/** The search page path. */
	private String searchPagePath;

	/** The new page path. */
	private String newPagePath;

	/** The value binding of foreign Pojo. */
	private ValueExpression sourcePojoBinding;

	private ILookupComponent component;
	
	/** The map of join value bindings. */
	// private Map<String,ValueBinding> joinBindingsMap;
	/** The show window. */
	private boolean showWindow;

	/** The show search buttons. */
	private boolean showSearchButtons;

	/** The selected panel. */
	private String selectedPanel;
	
	/** The window title. */
	private String windowTitle;
	
	/** The minimum width. */
	private String minWidth;
	
	/** The minimum height. */
	private String minHeight;

	/** The window close focus. */
	private String windowCloseFocus;
	
	private IControllerListener controllerListener;

	private ITransferObject suggestedTo;
	
	private String[] suggestAliases;

	/**
	 * The Constructor.
	 */
	public RichLookupBean() {
		this.controller = new BasicController();
		setPageLimit(DEFAULT_PAGE_LIMIT);
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

	/**
	 * Gets the join bindings map.
	 * 
	 * @param event
	 *            the event
	 * 
	 * @return the join bindings map
	 */
	public Map<String, ValueExpression> getJoinBindingsMap(UIComponent component) {
		Map<String, ValueExpression> joinBindingsMap = Collections.emptyMap();
		if (component instanceof HtmlLookupInputText) {
			HtmlLookupInputText lookupComponent = (HtmlLookupInputText) component;
			joinBindingsMap = lookupComponent.getJoinBindingsMap();
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
	 * Return the POJO class short name associated to controller.
	 * 
	 * @return String
	 */
	public String getPojoShortName() {
		return getController().getPojoShortName();
	}
	
	/**
	 * Return the limit of page in the model associated to controller.
	 * 
	 * @return int
	 */
	public int getPageLimit() {
		return getController().getPageLimit();
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
	 * Sets the init expressions.
	 * 
	 * @param expressions
	 *            the expressions
	 */
	public void setDefaultExpressions(Map<String, Object> expressions) {
		getController().setDefaultExpressions(expressions);
	}
	
	/**
	 * Sets the order list.
	 * 
	 * @param value
	 *            the new order list
	 */
	public void setDefaultOrder(String value) {
		getController().setDefaultOrder(value);
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
	 * The id component is managed as an alias to resolve the real property
	 * path.
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void addEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		getController().addEqualExpression(event);
	}
	
	/**
	 * Add a new expression to the criteria to condition the following searches.
	 * The id component is managed as an alias to resolve the real property
	 * path.
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void addIdEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		getController().addIdEqualExpression(event);
	}
	
	/**
	 * Add a new >= expression to the criteria to condition the following searches.
	 * The id component is managed as an alias to resolve the real property
	 * path.
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void addGreaterThanOrEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		getController().addGreaterThanOrEqualExpression(event);
	}

	/**
	 * Add a new <= expression to the criteria to condition the following searches.
	 * The id component is managed as an alias to resolve the real property
	 * path.
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void addLessThanOrEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		getController().addLessThanOrEqualExpression(event);
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
		if (! showPopup ) {
			setSelectedPanel(null);
		}
	}

	/**
	 * Gets the values map.
	 * 
	 * @return the values map
	 */
	private Map<String, Object> getValuesMap(Map<String, ValueExpression> joinBindingsMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		FacesContext ctx = FacesContext.getCurrentInstance();
		for (Entry<String, ValueExpression> entry : joinBindingsMap.entrySet()) {
			Object value = entry.getValue().getValue(ctx.getELContext());
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
	private void updateCriteria(Map<String, Object> valuesMap) throws ManagerBeanException {
		Criteria criteria = getController().getCriteria();
		IManagerBean bean = getController().getManagerBean();
		for (Entry<String, Object> entry : valuesMap.entrySet()) {
			String fieldName = bean.getFieldName(entry.getKey());
			if (entry.getValue() != null) {
				criteria.addEqualExpression(fieldName, entry.getValue());
			} else {
				criteria.addNullExpression(fieldName);
			}
		}
	}
	
	private void fireLookupChangeListener(UIComponent component, boolean resolved) {
		if ( getComponent().getLookupChangeListener() != null ) {
			LookupChangeEvent event = null;
			FacesContext ctx = FacesContext.getCurrentInstance();
			Object newValue = null;
			if ( resolved ) {
				if ( NEW_ID.equals(getSelectedPanel()) ) {
					newValue = getController().getTo();
				} else {
					try {
						newValue = getController().getModel().getRowData();
					} catch (ManagerBeanException e) {
						LOGGER.error( e.getMessage(), e );
					}
				}				
			}
			event = new LookupChangeEvent(component, newValue );
			try {
				getComponent().getLookupChangeListener().invoke(ctx.getELContext(), new Object[]{event});
			} catch (Throwable e) {
				LOGGER.error(">>>> fireLookupChangeListener ",e);
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);			
			}
		}
	}

	private Object getLookupValue() {
		Object value = getController().getTo();
		if (! StringUtils.isEmpty(getComponent().getLookupProperty()) ) {		
			try {
				value = PropertyUtils.getProperty( value, getComponent().getLookupProperty() );
			} catch (Throwable e) {
				LOGGER.error( e.getMessage(), e );
				value = null;
			}
		}
		return value; 
	}
	
	private void updateSourcePojo() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		sourcePojoBinding.setValue(ctx.getELContext(), getLookupValue());
	}
	
	/**
	 * Lookup changed.
	 * 
	 * @param event
	 *            the event
	 * @throws ManagerBeanException
	 */
	public void lookupChanged(ActionEvent event) throws ManagerBeanException {
		boolean resolved = false;
		UIComponent component = event.getComponent().getParent();
		setBindings( component );
		onEditSearch(event);
		Map<String, ValueExpression> joinBindingsMap = getJoinBindingsMap(component);
		Map<String, Object> valuesMap = getValuesMap(joinBindingsMap);
		updateCriteria(valuesMap);
		onSearch(event);
		if (getModel().getRowCount() == 1) {
			getController().getModel().setRowIndex(0);
			onSelect(event);
			resolved = true;
		} else {
			onReset(event);
		}
		fireLookupChangeListener(component, resolved);
		updateSourcePojo();
		removeControllerListener();
		if (! resolved) {
			String message = AonUtil.addErrorMessageFromBundle(SEARCH_NO_RESULTS);
			throw new AbortProcessingException(message);
		}
	}	

	private void setBindings(UIComponent component) {
		if (component instanceof ILookupComponent) {
			this.component = (ILookupComponent) component;
			this.sourcePojoBinding = this.component.getProperty();
			this.controllerListener = this.component.getControllerListener();
			if ( this.controllerListener != null ) {
				addControllerListener( this.controllerListener );
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
		updateWindowProperties();
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
		updateWindowProperties();
		setShowWindow(true);
		setSelectedPanel(SEARCH_ID);
		onEditSearch(event);
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
		updateWindowProperties();
		setShowWindow(true);
		setSelectedPanel(NEW_ID);
		onReset(event);
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
		fireLookupChangeListener(event.getComponent(), true);
		onSelect(null);
		updateSourcePojo();
		beforeCloseWindow();
	}

	/**
	 * Row selection.
	 * 
	 * @param event
	 *            the event
	 */
	public void onFormSelect(ActionEvent event) {
		LOGGER.info("onFormSelect: {}", getController().getTo());
		fireLookupChangeListener(event.getComponent(), true);
		updateSourcePojo();
		beforeCloseWindow();
	}

	/**
	 * On close window.
	 * 
	 * @param event
	 *            the event
	 */
	public void onCloseWindow(ActionEvent event) {
		beforeCloseWindow();
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

	public ILookupComponent getComponent() {
		return component;
	}

	public String getWindowTitle() {
		return windowTitle;
	}
	
	public String getMinWidth() {
		return minWidth;
	}

	public String getMinHeight() {
		return minHeight;
	}
	
	public String getWindowCloseFocus() {
		return windowCloseFocus;
	}

	private void updateWindowProperties() {
		if ( (this.component != null) && (this.component instanceof HtmlLookupButton) ) {
			HtmlLookupButton lookupButton = (HtmlLookupButton) this.component;
			this.windowTitle = lookupButton.getWindowTitle();
			this.windowCloseFocus = lookupButton.getWindowCloseFocus();
			this.minWidth = lookupButton.getMinWidth();
			this.minHeight = lookupButton.getMinHeight();
		}
		if ( StringUtils.isEmpty(this.windowTitle) ) {
			this.windowTitle = DEFAULT_WINDOW_TITLE;	
		}
	}
	
	private void removeControllerListener() {
		if ( this.controllerListener != null ) {
			removeControllerListener(this.controllerListener);
			this.controllerListener = null;
		}		
	}
	
	private void beforeCloseWindow() {
		setShowWindow(false);
		clearModel();
		removeControllerListener();
	}
	
	public boolean isResolved( ILookupComponent lookupComponent ) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Object to = lookupComponent.getProperty().getValue(ctx.getELContext());
		return isResolved( (ITransferObject) to );
	}	

	public static boolean isResolved( ITransferObject to ) {
		if ( to != null ) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(to.getClass());
				Serializable id = bean.getId(to); 
				return (id != null);
			} catch (ManagerBeanException e) {
				LOGGER.error( "Error getting id", e );
			}
		}
		return false;		
	}
	
	public void onClear( ActionEvent event ) {
		setBindings(event.getComponent());
		onReset(null);
		fireLookupChangeListener(event.getComponent(), false);
		updateSourcePojo();
		removeControllerListener();
	}

	public String[] getSuggestAliases() {
		if ( suggestAliases == null ) {
			OrderByList list = getController().getOrderList();
			if ( (list != null) && (!list.getOrders().isEmpty()) ) {
				this.suggestAliases = new String[ list.getOrders().size() ];
				for( int i = 0; i < this.suggestAliases.length; i++ )  {
					this.suggestAliases[i] = list.getOrders().get(i).getExpression().getName();
				}
			}
		}
		return suggestAliases;
	}

	public void setSuggestAlias(String alias) {
		String[] values = StringUtils.split(alias, ",");
		this.suggestAliases = new String[values.length];
		for( int i = 0; i < values.length; i++ ) {
			this.suggestAliases[i] = getController().resolveAlias(values[i]);	
		}
	}
	
	private String[] getSuggestAliases( HtmlLookupSuggestText st ) {
		if (! ArrayUtils.isEmpty(st.getSuggestAlias()) ) {
			String[] list = st.getSuggestAlias();
			String[] aliases = new String[list.length];
			for( int i = 0; i < list.length; i++ ) {
				aliases[i] = getController().resolveAlias(list[i]);	
			}
			return aliases;
		}
		return getSuggestAliases();
	}

	@SuppressWarnings("unchecked")
	public List<ITransferObject> autocomplete( Object value, UIComponent component ) {
		if ( value != null ) {
			String text = value.toString();
			if (! StringUtils.isBlank(text) ) {
				try {
					HtmlLookupSuggestText st = (HtmlLookupSuggestText) component.getParent();					
					setBindings( st );
					getController().onEditSearch(null);
					String search = (st.getMatchBeginOnly() ? "" : "%") + text + "%";
					for( String alias : getSuggestAliases(st) ) {
						Expression exp = ExpressionUtilities.getLikeExpression(alias, search);
						getController().getCriteria().addOrExpression(exp);						
					}
					onSearch(null);
					if (getModel().getRowCount() > 0) {
						return (List<ITransferObject>) getModel().getWrappedData();
					}					
		    	} catch (ManagerBeanException e) {
		    		LOGGER.error( "Error getting suggestion objects", e );
				}				
			}
		}
    	return Collections.emptyList();
    }	
	
	public ITransferObject getSuggestedTo() {
		return suggestedTo;
	}

	public void setSuggestedTo(ITransferObject suggestedTo) {
		this.suggestedTo = suggestedTo;
	}

	public void onSuggestSelect( ActionEvent event ) {
		UIComponent component = event.getComponent().getParent().getParent();
		setBindings( component );
		try {
			getController().select(event, this.suggestedTo);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSuggestSelect ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);			
		}
		fireLookupChangeListener(component, true);
		updateSourcePojo();
		removeControllerListener();
	}
		
	public String lookupAction() {
		String action = "";
		if ( (this.component != null) && (this.component instanceof HtmlLookupButton) ) {
			HtmlLookupButton lookupButton = (HtmlLookupButton) this.component;
			if ( lookupButton.getLookupAction() != null ) {
				try {
					FacesContext ctx = FacesContext.getCurrentInstance();
					action = (String) lookupButton.getLookupAction().invoke(ctx.getELContext(), new Object[]{});
				} catch (Throwable e) {
					LOGGER.error(">>>> lookupAction ",e);
					throw new AbortProcessingException(e.getMessage(), e);			
				}							
			}
		}
		return action;
	}

}