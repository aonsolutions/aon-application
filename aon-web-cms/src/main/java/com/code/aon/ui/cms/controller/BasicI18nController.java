package com.code.aon.ui.cms.controller;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.event.I18NControllerListener;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class BasicI18nController extends BasicController implements I18NControllerListener, IController, ICollectionProvider {

    private static final Logger LOGGER = Logger.getLogger(BasicI18nController.class.getName());

	private IManagerBean managerBeanI18n;

	private String beanI18nName;

	private String pojoI18n;

    private ITransferObject toI18n;
    
    private String language_alias = "";

    private String join_alias = "";

    /**
     * Constructor.
     * 
     */
	public BasicI18nController(){
        super();
        ControllerUtil.getI18NController().addListener(this);
	}

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onReset(javax.faces.event.ActionEvent)
     */
    public void onReset(ActionEvent event) {
        try {
            ControllerEvent evt = new ControllerEvent(this);
            setTo(getManagerBean().createNewTo());
            setToI18n(getManagerBeanI18n().createNewTo());
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
     * @see com.code.aon.ui.form.IController#onAccept(javax.faces.event.ActionEvent)
     */
    public void onAccept(ActionEvent event) {
        accept(event);
        resetTo();
    }

    /**
     * Reset the <code>ITransferObject</code> value.
     * 
     */
    protected void resetTo() {
        super.setTo(null);
    	setToI18n(null);
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
    	addI18n(inserted);
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
    	updateI18n(updated);
        saveState(updated);
        return updated;
    }


    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onRemove(javax.faces.event.ActionEvent)
     */
    public void onRemoveI18n(ActionEvent event) {
        removeI18n(event);
    	setToI18n(null);
    }

    /**
     * Action to remove the current row.
     * 
     * @param event
     */
    @SuppressWarnings("unused")
    public void removeI18n(ActionEvent event) {
        try {
            removeI18n();
        } catch (ManagerBeanException e) {
            LOGGER.severe(">>>> onRemove exception[" + e.getMessage() + "]");
            addMessage(e.getMessage());
            throw new AbortProcessingException(e.getMessage(), e);
        }
    }

    /**
     * Remove current <code>ITransferObject</code>.
     * 
     * @throws ManagerBeanException
     */
    protected void removeI18n() throws ManagerBeanException {
        LOGGER.fine("Removing Id:[" + getToI18n() + "]");
        getManagerBeanI18n().remove(getToI18n());
    }

	/**
	 * Return the manager of bean associated to controller.
	 * 
	 * @return IManagerBean
	 * @throws ManagerBeanException
	 */
	public IManagerBean getManagerBeanI18n() throws ManagerBeanException {
		if (this.managerBeanI18n == null) {
			this.managerBeanI18n = BeanManager.getManagerBean(getPojoI18n());
			if (this.managerBeanI18n == null) {
				String msg = "Unknown IManagerBeanI18n for " + getPojoI18n();
				LOGGER.severe(msg);
				throw new ManagerBeanException(msg);
			}
		}
		return this.managerBeanI18n;
	}

	public void languajeChanged() {
		loadCurrentLanguage();
	}

	public void loadCurrentLanguage() {
		try {
			if (getTo() != null) {
				IManagerBean beanI18n = getManagerBeanI18n();
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(beanI18n.getFieldName(language_alias), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanI18n.getFieldName(join_alias), getManagerBean().getId(getTo()));
				List<ITransferObject> list = beanI18n.getList(criteria);
				if (list.size() > 0) {
					setToI18n(list.get(0));
				}
				else {
					setToI18n(beanI18n.createNewTo());
				}
			}
		} catch (Exception e) {
		}
	}

	/* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#getTo()
     */
    public ITransferObject getToI18n() {
        return this.toI18n;
    }

    /**
     * Set the current <code>ITransferObject</code> of the <code>DataModel</code> to the controller.
     *
     * @param value
     */
    protected void setToI18n(ITransferObject value) {
        this.toI18n = value;
    }

	/**
	 * Return the POJO associated to controller.
	 * 
	 * @return String
	 */
	public String getPojoI18n() {
		return pojoI18n;
	}

	/**
	 * Set the POJO associated to controller.
	 * 
	 * @param beanI18n
	 */
	public void setPojoI18n(String beanI18n) {
		this.pojoI18n = beanI18n;
	}

	/**
	 * Return name of the bean associated to controller.
	 * 
	 * @return String
	 */
	public String getBeanI18nName() {
		return beanI18nName;
	}

	/**
	 * Set the name of the bean associated to controller.
	 * 
	 * @param beanI18nName
	 */
	public void setBeanI18nName(String beanI18nName) {
		this.beanI18nName = beanI18nName;
	}

	public String getLanguageAlias() {
		return language_alias;
	}

	public void setLanguageAlias(String language_alias) {
		this.language_alias = language_alias;
	}

	public String getJoinAlias() {
		return join_alias;
	}

	public void setJoinAlias(String join_alias) {
		this.join_alias = join_alias;
	}

    @SuppressWarnings("unchecked")
	protected ITransferObject addI18n(ITransferObject insertedTo) throws ManagerBeanException {
		DAOConstantsEntry entry = DAOConstants.getDAOConstant( getPojoI18n() );
    	Map hibernates = entry.getHibernateMap();
		String joinHibernateAlias = hibernates.get(getJoinAlias()).toString();
		String languageHibernateAlias = hibernates.get(getLanguageAlias()).toString();

    	getManagerBeanI18n().setProperty(getToI18n(), getPropertyName(joinHibernateAlias), insertedTo);
    	getManagerBeanI18n().setProperty(getToI18n(), getPropertyName(languageHibernateAlias), ControllerUtil.getCurrentLanguage());
    	ITransferObject inserted = getManagerBeanI18n().insert(getToI18n());
        return inserted;
    }


    @SuppressWarnings("unchecked")
	protected ITransferObject updateI18n(ITransferObject updatedTo) throws ManagerBeanException {
		DAOConstantsEntry entry = DAOConstants.getDAOConstant( getPojoI18n() );
    	Map hibernates = entry.getHibernateMap();
		String joinHibernateAlias = hibernates.get(getJoinAlias()).toString();
		String languageHibernateAlias = hibernates.get(getLanguageAlias()).toString();
		
    	ITransferObject updated;
    	if (getManagerBeanI18n().getId(getToI18n()) != null) {
        	updated = getManagerBeanI18n().update(getToI18n());
    	}
    	else {
        	getManagerBeanI18n().setProperty(getToI18n(), getPropertyName(joinHibernateAlias), updatedTo);
        	getManagerBeanI18n().setProperty(getToI18n(), getPropertyName(languageHibernateAlias), ControllerUtil.getCurrentLanguage());
        	updated = getManagerBeanI18n().insert(getToI18n());
    	}
        return updated;
    }

	private String getPropertyName(String hibernateProperty) {
		StringTokenizer st = new StringTokenizer(hibernateProperty, ".");
		st.nextToken();
		return st.nextToken();
	}

	public void addFromDateExpression(ValueChangeEvent event) throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			String id = event.getComponent().getId();
			id = id.replaceAll("_from", "");
			c.addGreaterThanOrEqualExpression(getFieldName(id), value);
			setCriteria(c);
		}
	}
	
	public void addToDateExpression(ValueChangeEvent event) throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			String id = event.getComponent().getId();
			id = id.replaceAll("_to", "");
			c.addLessThanOrEqualExpression(getFieldName(id), value);
			setCriteria(c);
		}
	}

	public ITransferObject getModelRowdataI18n() {
		try {
			if (this.model.getRowData() != null) {
				IManagerBean beanI18n = getManagerBeanI18n();
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(beanI18n.getFieldName(language_alias), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanI18n.getFieldName(join_alias), getManagerBean().getId((ITransferObject)this.model.getRowData()));
				List<ITransferObject> list = beanI18n.getList(criteria);
				if (!list.isEmpty()) {
					return list.get(0);
				}
			}
		} catch (Exception e) {
		}
		return null;
	}


}
