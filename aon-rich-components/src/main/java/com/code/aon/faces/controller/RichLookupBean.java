package com.code.aon.faces.controller;

import static com.code.aon.ui.common.ICommonMessages.SEARCH_NO_RESULTS;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
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

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.component.richfaces.lookup.ILookupComponent;
import com.code.aon.faces.component.richfaces.lookup.ILookupWindowComponent;
import com.code.aon.faces.component.richfaces.lookup.button.HtmlLookupButton;
import com.code.aon.faces.component.richfaces.lookup.button.LookupButtonType;
import com.code.aon.faces.component.richfaces.lookup.inputText.HtmlLookupInputText;
import com.code.aon.faces.component.richfaces.lookup.inputText.JoinProperty;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.ITemplateController;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;


/**
 * LookupBean is the class used to implement a Lookup creating an SQL sentence
 * which will be executed to retrive the required data.
 */
public class RichLookupBean implements ITemplateController, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final String LIST_ID = LookupButtonType.LIST.getName();

	private static final String NEW_ID = LookupButtonType.NEW.getName();

	private static final String SEARCH_ID = LookupButtonType.SEARCH.getName();
	
	private static final String DEFAULT_WINDOW_TITLE = "Select Window";
	
	private static final int DEFAULT_PAGE_LIMIT = 15;

	private final static Logger LOGGER = LoggerFactory.getLogger(RichLookupBean.class);

	/** The foreign controller. */
	private RichLookupController controller;

	/** The list page path. */
	private String listPagePath;

	/** The search page path. */
	private String searchPagePath;

	/** The new page path. */
	private String newPagePath;

	/** The value binding of foreign Pojo. */
	private ValueExpression sourcePojoBinding;

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
	
	private MethodExpression lookupAction;
	
	private String lookupProperty;
	
	private MethodExpression lookupChangeListener;
	
	private String selectReRender;

	/**
	 * The Constructor.
	 */
	public RichLookupBean() {
		this.controller = new RichLookupController();
		this.controller.setLookup(true);
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
	 * Gets the join properties list.
	 * 
	 * @param event
	 *            the event
	 * 
	 * @return the join properties list
	 */
	public List<JoinProperty> getJoinBindingsMap(ILookupComponent component) {
		List<JoinProperty> joinProperties = Collections.emptyList();
		if (component instanceof HtmlLookupInputText) {
			HtmlLookupInputText lookupComponent = (HtmlLookupInputText) component;
			joinProperties = lookupComponent.getJoinProperties();
		}
		return joinProperties;
	}

	/**
	 * Gets the controller.
	 * 
	 * @return the controller
	 */
	public RichLookupController getController() {
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
	public Integer getPageLimit() {
		return getController().getPageLimit();
	}	
	
	/**
	 * Set the limit of page in the model associated to controller.
	 * 
	 * @param pageLimit
	 *            the page limit
	 */
	public void setPageLimit(Integer pageLimit) {
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
		if ( listener != null ) {
			this.controllerListener = listener;
			getController().addControllerListener(this.controllerListener);	
		}
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
	public boolean isNevv() {
		return getController().isNevv();
	}

	/**
	 * Sets the state of bean.
	 * 
	 * @param isNew
	 */
	public void setNevv(boolean isNevv) {
		getController().setNevv(isNevv);
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
	
	@Override
	public int getPage() {
		return getController().getPage();
	}

	@Override
	public void setPage(int page) {
		getController().setPage(page);
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
	 * Gets the criteria.
	 * 
	 * @return the criteria
	 * @throws ManagerBeanException
	 */
	protected void updateCriteria(List<JoinProperty> joinProperties) throws ManagerBeanException {
		Criteria criteria = getController().getCriteria();
		FacesContext ctx = FacesContext.getCurrentInstance();
		for (JoinProperty jp : joinProperties) {
			String fieldName = getController().resolveAlias(jp.getAlias());
			Object value = jp.getValue(ctx);
			Expression expression = null;
			if (value != null) {
				expression = ExpressionUtilities.getEqualExpression(fieldName, value);
			} else {
				expression = ExpressionUtilities.getNullExpression(fieldName);
			}
			if ( jp.isOrExpression() ) {
				criteria.addOrExpression(expression);
			} else {
				criteria.addExpression(expression);
			}
		}
	}
	
	private void fireLookupChangeListener(UIComponent component, boolean resolved) {
		if ( this.lookupChangeListener != null ) {
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
				this.lookupChangeListener.invoke(ctx.getELContext(), new Object[]{event});
			} catch (Throwable e) {
				LOGGER.error(">>>> fireLookupChangeListener ",e);
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);			
			}
		}
	}

	private Object getLookupValue() {
		Object value = getController().getTo();
		if (! StringUtils.isEmpty(this.lookupProperty) ) {		
			try {
				value = PropertyUtils.getProperty( value, this.lookupProperty );
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
		ILookupComponent component = (ILookupComponent) event.getComponent().getParent();
		if ( component != null ) {
			setBindings( component );
			onEditSearch(event);
			List<JoinProperty> joinProperties = getJoinBindingsMap(component);
			updateCriteria(joinProperties);
			onSearch(event);
			int count = getModel().getRowCount();
			if (count == 1) {
				getController().getModel().setRowIndex(0);
				onSelect(event);
			} else {
				onReset(event);
				if ( count > 1) {
					showListWindow(event, component, false);	
				}
			}
			fireLookupChangeListener((UIComponent)component, count==1);
			updateSourcePojo();
			removeControllerListener();
			if (count==0) {
				String message = AonUtil.addErrorMessageFromBundle(SEARCH_NO_RESULTS);
				throw new AbortProcessingException(message);
			}
		}
	}	

	private void setBindings(ILookupComponent component) {
		this.sourcePojoBinding = component.getProperty();
		this.lookupProperty = component.getLookupProperty();
		this.lookupChangeListener = component.getLookupChangeListener();
		addControllerListener(component.getControllerListener());
		if ( component instanceof HtmlLookupButton ) {
			this.lookupAction = ((HtmlLookupButton)component).getLookupAction();
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
		showListWindow(event, (ILookupComponent) event.getComponent(), true);
	}

	private void showListWindow(ActionEvent event, ILookupComponent component, boolean search) throws ManagerBeanException {
		if ( component != null ) {
			setBindings(component);
			updateWindowProperties(component);
			setShowWindow(true);
			setSelectedPanel(LIST_ID);
			if ( search ) {
				getController().clearCriteria();
				onSearch(null);			
			}
			this.showSearchButtons = false;			
		}
	}
	
	
	/**
	 * On show lookup search window.
	 * 
	 * @param event
	 *            the event
	 */
	public void onShowSearchWindow(ActionEvent event) {
		ILookupComponent component = (ILookupComponent) event.getComponent();
		if ( component != null ) {
			setBindings(component);
			updateWindowProperties(component);
			setShowWindow(true);
			setSelectedPanel(SEARCH_ID);
			onEditSearch(event);
			this.showSearchButtons = true;
		}
	}

	/**
	 * On show lookup form window.
	 * 
	 * @param event
	 *            the event
	 */
	public void onShowNewWindow(ActionEvent event) {
		ILookupComponent component = (ILookupComponent) event.getComponent();
		if ( component != null ) {
			setBindings(component);
			updateWindowProperties(component);
			setShowWindow(true);
			setSelectedPanel(NEW_ID);
			onReset(event);
		}
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
	
	public String getSelectReRender() {
		return selectReRender;
	}

	private void updateWindowProperties( ILookupComponent component ) {
		if ( component instanceof ILookupWindowComponent ) {
			ILookupWindowComponent lwComponent = (ILookupWindowComponent) component;
			this.windowTitle = lwComponent.getWindowTitle();
			this.windowCloseFocus = lwComponent.getWindowCloseFocus();
			this.minWidth = lwComponent.getMinWidth();
			this.minHeight = lwComponent.getMinHeight();
			this.selectReRender = lwComponent.getSelectReRender();
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
		ILookupComponent component = (ILookupComponent) event.getComponent();
		if ( component != null ) {
			setBindings(component);
			onReset(null);
			fireLookupChangeListener(event.getComponent(), false);
			updateSourcePojo();
			removeControllerListener();
		}
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
	
	private String[] getSuggestAliases( String value ) {
		if (! StringUtils.isEmpty(value) ) {
			String[] list = StringUtils.split(value, ",");
			String[] aliases = new String[list.length];
			for( int i = 0; i < list.length; i++ ) {
				aliases[i] = getController().resolveAlias(list[i]);	
			}
			return aliases;
		}
		return getSuggestAliases();
	}	

	@SuppressWarnings("unchecked")
	public List<ITransferObject> autocomplete( Object value, Object[] properties ) {
		if ( value != null ) {
			String text = value.toString();
			if (! StringUtils.isBlank(text) ) {
				try {
					ValueExpression ve = (ValueExpression) properties[2];
					if ( ve != null ) {
						FacesContext ctx = FacesContext.getCurrentInstance();
						 addControllerListener((IControllerListener) ve.getValue(ctx.getELContext()));
					}
					getController().onEditSearch(null);
					String[] aliases = getSuggestAliases((String) properties[0]);
					if (! ArrayUtils.isEmpty(aliases) ) {
						Expression expr = null;
						boolean matchBeginOnly = (Boolean) properties[1];
						String search = (matchBeginOnly ? "" : "%") + text + "%";
						for( String alias : aliases ) {
							if ( expr == null ) {
								expr = ExpressionUtilities.getLikeExpression(alias, search);	
							} else {
								Expression expr2 = ExpressionUtilities.getLikeExpression(alias, search);
								expr = ExpressionUtilities.getOrExpression(expr, expr2);
							}						
						}	
						getController().getCriteria().addExpression(expr);
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
		ILookupComponent component = (ILookupComponent) event.getComponent().getParent().getParent();
		if ( component != null ) {
			setBindings(component);
			try {
				getController().select(event, this.suggestedTo);
			} catch (ManagerBeanException e) {
				LOGGER.error(">>>> onSuggestSelect ",e);
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);			
			}
			fireLookupChangeListener((UIComponent) component, true);
			updateSourcePojo();
			removeControllerListener();
		}
	}
		
	public String lookupAction() {
		String action = "";
		if ( this.lookupAction != null ) {
			try {
				FacesContext ctx = FacesContext.getCurrentInstance();
				action = (String) this.lookupAction.invoke(ctx.getELContext(), new Object[]{});
			} catch (Throwable e) {
				LOGGER.error(">>>> lookupAction ",e);
				throw new AbortProcessingException(e.getMessage(), e);			
			}							
		}
		return action;
	}

}