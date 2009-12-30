package com.code.aon.ui.form;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;

import com.code.aon.common.ILookupObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.ControllerListenerSupport;
import com.code.aon.ui.form.event.IControllerListener;

/**
 * Controller for New Object Creation Windows.
 */
@Deprecated
public class NewWindowController extends AbstractPojoController implements IController {

    private static final Logger LOGGER = Logger.getLogger(NewWindowController.class.getName());

    private ITransferObject to;

    private boolean isNew;

    /** Represent a manager of listeners */
    protected ControllerListenerSupport controllerListenerSupport;

    private List<IControllerListener> listenerClasses;

    private String pagePath;

    private String ids;

    /**
     * Constructor.
     */
    public NewWindowController() {
        controllerListenerSupport = new ControllerListenerSupport();
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
     * Add all listeners from variable listenerClasses to controller.
     * 
     */
    private void addListeners() {
        Iterator<IControllerListener> iter = listenerClasses.iterator();
        while (iter.hasNext()) {
            IControllerListener listener = iter.next();
            this.addControllerListener(listener);
        }
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

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#getTo()
     */
    public ITransferObject getTo() {
        return this.to;
    }

    /**
     * Set the current <code>ITransferObject</code> of the <code>DataModel</code> to the controller.
     *
     * @param value
     */
    protected void setTo(ITransferObject value) {
        this.to = value;
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#isNew()
     */
    public boolean isNew() {
        return isNew;
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#setNew(boolean)
     */
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    /**
     * Return lookups in XML Format.
     * 
     * @return String
     */
    public String getToLookupsAsXML() {
        ILookupObject lo = (ILookupObject) getTo();
        if (lo != null) {
            return getLookupsAsXML(lo, this.ids);
        }
        return "";
    }

    /**
     * Add current <code>ITransferObject</code>.
     * 
     * @return ITransferObject
     * @throws ManagerBeanException
     */
    protected ITransferObject add() throws ManagerBeanException {
        LOGGER.fine("Adding Id:[" + getTo() + "]");
        return getManagerBean().insert(getTo());
    }

    /**
     * Update current <code>ITransferObject</code>.
     *
     * @return ITransferObject
     * @throws ManagerBeanException
     */
    protected ITransferObject update() throws ManagerBeanException {
        LOGGER.fine("Setting Id:[" + getTo() + "]");
        return getManagerBean().update(getTo());
    }

    /**
     * Action to insert or to update the current row depending on the value of the variable isNew.
     * 
     * @param event
     */
    @SuppressWarnings("unused")
    public void accept(ActionEvent event) {
        try {
            ControllerEvent evt = new ControllerEvent(this);
            if (isNew) {
                controllerListenerSupport.fireBeforeBeanAdded(evt);
                this.to = add();
                controllerListenerSupport.fireAfterBeanAdded(evt);
                setNew(false);
            } else {
                controllerListenerSupport.fireBeforeBeanUpdated(evt);
                to = update();
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

    /**
     * Reset the <code>ITransferObject</code> value.
     * 
     */
    protected void resetTo() {
        this.to = null;
        setNew(false);
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onCancel(javax.faces.event.ActionEvent)
     */
    public void onCancel(ActionEvent event) {
        try {
            ControllerEvent evt = new ControllerEvent(this);
            controllerListenerSupport.fireBeforeBeanCanceled(evt);
            resetTo();
            controllerListenerSupport.fireAfterBeanCanceled(evt);
        } catch (ControllerListenerException e) {
            LOGGER.severe(">>>> onCancel " + e.getMessage());
            addMessage(e.getMessage());
            throw new AbortProcessingException(e.getMessage(), e);
        }
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.ISearchable#addExpression(javax.faces.event.ValueChangeEvent)
     */
    public void addExpression(ValueChangeEvent event) throws ManagerBeanException {
        throw new NoSuchMethodError("Not implemented");
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.ISearchable#addDirectExpression(javax.faces.event.ValueChangeEvent)
     */
    public void addDirectExpression(ValueChangeEvent event) throws ManagerBeanException {
        throw new NoSuchMethodError("Not implemented");
    }
    
    /* (non-Javadoc)
     * @see com.code.aon.ui.form.ISearchable#clearCriteria()
     */
    public void clearCriteria() throws ManagerBeanException {
        throw new NoSuchMethodError("Not implemented");
    }

    /* (non-Javadoc)
     * @see com.code.aon.common.ICriteriaProvider#getCriteria()
     */
    public Criteria getCriteria() throws ManagerBeanException {
        throw new NoSuchMethodError("Not implemented");
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.ISearchable#search(int, int)
     */
    public List<ITransferObject> search(int start, int count) throws ManagerBeanException {
        throw new NoSuchMethodError("Not implemented");
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.ISearchable#setCriteria(com.code.aon.ql.Criteria)
     */
    public void setCriteria(Criteria criteria) throws ManagerBeanException {
        throw new NoSuchMethodError("Not implemented");
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#getModel()
     */
    public DataModel getModel() throws ManagerBeanException {
        throw new NoSuchMethodError("Not implemented");
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#initializeModel()
     */
    public void initializeModel() {
        throw new NoSuchMethodError("Not implemented");
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onAccept(javax.faces.event.ActionEvent)
     */
    public void onAccept(ActionEvent event) {
        throw new NoSuchMethodError("Not implemented");
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onEditSearch(javax.faces.event.ActionEvent)
     */
    public void onEditSearch(ActionEvent event) {
        throw new NoSuchMethodError("Not implemented");
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onRemove(javax.faces.event.ActionEvent)
     */
    public void onRemove(ActionEvent event) {
        throw new NoSuchMethodError("Not implemented");
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onReset(javax.faces.event.ActionEvent)
     */
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

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onSearch(javax.faces.event.ActionEvent)
     */
    public void onSearch(ActionEvent event) {
        throw new NoSuchMethodError("Not implemented");
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onSelect(javax.faces.event.ActionEvent)
     */
    public void onSelect(ActionEvent event) {
        throw new NoSuchMethodError("Not implemented");
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#setModel(javax.faces.model.DataModel)
     */
    public void setModel(DataModel model) {
        throw new NoSuchMethodError("Not implemented");
    }

    /**
     * Return path of requested page.
     * 
     * @return String
     */
    public String getPagePath() {
        return pagePath;
    }

    /**
     * Set path of requested page.
     * 
     * @param pagePath
     */
    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    /**
     * Return path encoded of requested page.
     * 
     * @return String
     */
    public String getEncodedPagePath() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext ec = ctx.getExternalContext();
        String url = ec.getRequestContextPath() + this.pagePath;
        url = ec.encodeActionURL(url);
        return url;
    }

    /**
     * Get involved parameters. 
     * 
     * @return boolean
     */
    public boolean getParameters() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        String idsValue = (String) ec.getRequestParameterMap().get("ids");
        String resetValue = (String) ec.getRequestParameterMap().get("reset");
        boolean hasParameters = (idsValue != null) || (resetValue != null);
        if (hasParameters) {
            this.ids = idsValue;
            LOGGER.fine("ids:[" + ids + "]");
            if (resetValue != null) {
                if (Boolean.valueOf(resetValue).booleanValue()) {
                    onReset(null);
                    LOGGER.fine("Values resetted");
                }
            }
        }
        return false;
    }

}
