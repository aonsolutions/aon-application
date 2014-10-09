package com.code.aon.ui.form;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;

/**
 * Interface to define data processing methods for the controllers.
 * 
 * @author Consulting & Development.
 */
public interface IController extends ISearchable {
	
	/** The form suffix for template action. */
	String FORM_SUFFIX = "_form";
	
	/** The list suffix for template action. */
	String LIST_SUFFIX = "_list";
	
	/** The search suffix for template action. */
	String SEARCH_SUFFIX = "_search";	

    /**
     * Return the manager of bean associated to controller.
     * 
     * @return IManagerBean
     * @throws ManagerBeanException
     */
    IManagerBean getManagerBean() throws ManagerBeanException;

    /**
     * Initialize the model associated to controller.
     */
    void initializeModel();

    /**
     * Return the model associated to controller. The model represents a list of <code>ITransferObject</code> 
     *  with which we will be able to interact.
     * 
     * @return DataModel
     * @throws ManagerBeanException
     */
    DataModel getModel() throws ManagerBeanException;

    /**
     * Set the model associated to controller.
     * 
     * @param model
     */
    void setModel(DataModel model);

    /**
     * Return the state of bean. True if bean is in state 'New', otherwise false.
     * 
     * @return boolean
     */
    boolean isNevv();

    /**
     * Sets the state of bean.
     * 
     * @param isNevv
     */
    void setNew(boolean isNevv);

    /**
     * Execute insert or update action.
     * 
     * @param event
     */
    void onAccept(ActionEvent event);

    /**
     * Execute search action.
     * 
     * @param event
     */
    void onSearch(ActionEvent event);

    /**
     * Execute removal action.
     * 
     * @param event
     */
    void onRemove(ActionEvent event);

    /**
     * Execute back action.
     * 
     * @param event
     */
    void onBack(ActionEvent event);
    
    /**
     * Execute cancel action.
     * 
     * @param event
     */
    void onCancel(ActionEvent event);

    /**
     * Execute reset action.
     * 
     * @param event
     */
    void onReset(ActionEvent event);

    /**
     * Execute search edition action.
     * 
     * @param event
     */
    void onEditSearch(ActionEvent event);

    /**
     * Execute selection action.
     * 
     * @param event
     */
    void onSelect(ActionEvent event);

    /**
     * Return <code>ITransferObject</code> associated to controller.
     * 
     * @return ITransferObject.
     */
    ITransferObject getTo();

}