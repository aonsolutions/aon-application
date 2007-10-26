package com.code.aon.ui.form;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.common.lookup.LookupUtils;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.form.lookup.HibernateLookupBean;

/**
 * @author atellitu
 *
 */
@Deprecated
public class LookupController extends HibernateLookupBean {

    private static final Logger LOGGER = Logger.getLogger(LookupController.class.getName());	
	
    private String pagePath;
    
    private String newPagePath;  
    
    private String searchPagePath;

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
     * Return the state of bean. True if bean is in state 'New', otherwise false.
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
        getController().setNew( isNew );
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
     * Action to insert or to update the current row depending on the value of the variable isNew.
     * 
     * @param event
     */
    public void accept(ActionEvent event) {
    	getController().accept(event);
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
     * Add a new expression to the criteria to condition the following searches.
     * The id component is managed as an alias to resolve the real property path.
     * 
     * @param event
     * @throws ManagerBeanException
     */
    public void addExpression(ValueChangeEvent event) throws ManagerBeanException {
    	getController().addExpression( event );
    }

    /**
     * Add a new expression to the criteria to condition the following searches.
     * The id component is managed as the property path, replacing <code>_</code>
     * with <code>.</code>. 
     * 
     * @param event
     * @throws ManagerBeanException
     */
    public void addDirectExpression(ValueChangeEvent event) throws ManagerBeanException {
    	getController().addDirectExpression( event );
    }
    
    /**
     * Execute search edition action.
     * 
     * @param event
     */
    public void onEditSearch(ActionEvent event) {
    	getController().onEditSearch( event );    	
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
     * Return path of the select window page.
     * 
     * @return String
     */
    public String getPagePath() {
        return this.pagePath;
    }

    /**
     * Set path of the select window page.
     * 
     * @param pagePath
     */
    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    private String getEncodedPagePath( String path ) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext ec = ctx.getExternalContext();
        String url = ec.getRequestContextPath() + path;
        url = ec.encodeActionURL(url);
        return url;
    }
    
    /**
     * Return path encoded of the new requested page.
     * 
     * @return String
     */
    public String getEncodedNewPagePath() {
    	return getEncodedPagePath(this.newPagePath);
    }

    /**
     * Return path encoded of the search requested page.
     * 
     * @return String
     */
    public String getEncodedSearchPagePath() {
    	return getEncodedPagePath(this.searchPagePath);
    }

    /**
     * Return path encoded of the select window page.
     * 
     * @return String
     */
    public String getEncodedPagePath() {
    	return getEncodedPagePath(this.pagePath);
    }
    
    /**
     * Sets the foreign bean.
     * 
     * @param foreignBean the foreign bean
     */
    public void setForeignBean( BasicController foreignBean ) {
    	setForeignPojo( foreignBean.getPojo() );
    	setListenerClasses( foreignBean.getListenerClasses() );
    }
    
	/**
	 * Gets the lookups as XML.
	 * 
	 * @return the lookups as XML
	 */
	@SuppressWarnings("unchecked")
	public String getTOLookupsAsXML() {
        ITransferObject to = getController().getTo();
		try {		
	        if (to != null) {			
	        	Map<String, Object> map = getToMap(to, getAlias());
	        	return LookupUtils.getResponseXML( map, getIdsMap() );
	        }
		} catch (Throwable th) {
			LOGGER.severe(th.getMessage());
		}
		return "";
	}
	
    
}
