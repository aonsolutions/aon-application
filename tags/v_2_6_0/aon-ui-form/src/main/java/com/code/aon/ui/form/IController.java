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

    /**
     * Return the manager of bean associated to controller.
     * 
     * @return IManagerBean
     * @throws ManagerBeanException
     */
    public IManagerBean getManagerBean() throws ManagerBeanException;

    /**
     * Initialize the model associated to controller.
     */
    public void initializeModel();

    /**
     * Return the model associated to controller. The model represents a list of <code>ITransferObject</code> 
     *  with which we will be able to interact.
     * 
     * @return DataModel
     * @throws ManagerBeanException
     */
    public DataModel getModel() throws ManagerBeanException;

    /**
     * Set the model associated to controller.
     * 
     * @param model
     */
    public void setModel(DataModel model);

    /**
     * Return the state of bean. True if bean is in state 'New', otherwise false.
     * 
     * @return boolean
     */
    public boolean isNew();

    /**
     * Sets the state of bean.
     * 
     * @param isNew
     */
    public void setNew(boolean isNew);

    /**
     * Execute insert or update action.
     * 
     * @param event
     */
    public void onAccept(ActionEvent event);

    /**
     * Execute search action.
     * 
     * @param event
     */
    public void onSearch(ActionEvent event);

    /**
     * Execute removal action.
     * 
     * @param event
     */
    public void onRemove(ActionEvent event);

    /**
     * Execute cancel action.
     * 
     * @param event
     */
    public void onCancel(ActionEvent event);

    /**
     * Execute reset action.
     * 
     * @param event
     */
    public void onReset(ActionEvent event);

    /**
     * Execute search edition action.
     * 
     * @param event
     */
    public void onEditSearch(ActionEvent event);

    /**
     * Execute selection action.
     * 
     * @param event
     */
    public void onSelect(ActionEvent event);

    /**
     * Return <code>ITransferObject</code> associated to controller.
     * 
     * @return ITransferObject.
     */
    public ITransferObject getTo();

}