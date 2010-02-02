package com.code.aon.ui.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;

/**
 * Controller for Select Windows
 */
@Deprecated
public class SelectWindowController extends AbstractPojoController implements ISearchable {

    private static final Logger LOGGER = Logger.getLogger(SelectWindowController.class.getName());

    private Criteria criteria = new Criteria();

    /** Represent the model of data that we are going to interact with */
    protected DataModel model;

    private int pageLimit = PageDataModel.LIMIT;

    private String pagePath;

    private String ids;

    private Map<String, Object> values;

    private Map<String, Expression> expressions;

    /**
     * Constructor. 
     */
    public SelectWindowController() {
        this.values = new HashMap<String, Object>();
        this.expressions = new HashMap<String, Expression>();
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
     * Return the model associated to controller. The model represents a list of <code>ITransferObject</code> 
     *  with which we will be able to interact.
     * 
     * @return DataModel
     * @throws ManagerBeanException
     */
    public DataModel getModel() throws ManagerBeanException {
        if (model == null) {
            initializeModel();
        }
        return model;
    }

    /**
     * Initialize the model associated to controller.
     * 
     * @throws ManagerBeanException
     */
    @SuppressWarnings("unchecked")
    public void initializeModel() throws ManagerBeanException {
        if (model == null) {
            int count = getManagerBean().getCount(getCriteria());
            model = new PageDataModel(this, count, getPageLimit());
        } else {
            PageDataModel pdm = (PageDataModel) model;
            pdm.setWrappedData(search(0, getPageLimit()));
            pdm.resize(getManagerBean().getCount(getCriteria()));
        }
        LOGGER.fine("initializeModel RowCount[" + model.getRowCount() + "]");
    }

    /**
     * Add a new expression to the criteria to condition the following searches.
     * 
     * @param key
     * @param value
     * @throws ManagerBeanException
     */
    public void addExpression(String key, String value) throws ManagerBeanException {
        try {
            if (value.length() > 0) {
                Expression expression = ExpressionUtilities.getExpression(value, key);
                this.expressions.put(key, expression);
            } else {
                this.expressions.remove(key);
            }
        } catch (ExpressionException e) {
            throw new ManagerBeanException(e.getMessage(), e);
        }
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.ISearchable#addExpression(javax.faces.event.ValueChangeEvent)
     */
    public void addExpression(ValueChangeEvent event) throws ManagerBeanException {
        String id = event.getComponent().getId();
        if (event.getNewValue() != null) {
            String value = event.getNewValue().toString();
            addExpression(getFieldName(id), value);
        } else {
            this.expressions.remove(id);
        }
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.ISearchable#addDirectExpression(javax.faces.event.ValueChangeEvent)
     */
    public void addDirectExpression(ValueChangeEvent event) throws ManagerBeanException {
        String id = event.getComponent().getId().replace('_', '.');
        if (event.getNewValue() != null) {
            String value = event.getNewValue().toString();
            addExpression(id, value);
        } else {
            this.expressions.remove(id);
        }
    }
    
    /**
     * Add a new expression to the criteria to condition the following searches.
     * 
     * @param key
     * @param expression
     */
    public void addExpression(String key, Expression expression) {
        this.expressions.put(key, expression);
    }

    /**
     * Add a new equal expression to the criteria to condition the following searches.
     * 
     * @param event
     * @throws ManagerBeanException
     */
    public void addEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
        String id = event.getComponent().getId();
        if (event.getNewValue() != null) {
            Expression expression = ExpressionUtilities.getEqualExpression(getFieldName(id), event.getNewValue());
            this.expressions.put(id, expression);
        } else {
            this.expressions.remove(id);
        }
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.ISearchable#clearCriteria()
     */
    public void clearCriteria() throws ManagerBeanException {
        this.criteria = new Criteria();
    }

    /* (non-Javadoc)
     * @see com.code.aon.common.ICriteriaProvider#getCriteria()
     */
    public Criteria getCriteria() throws ManagerBeanException {
        return this.criteria;
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.ISearchable#search(int, int)
     */
    public List<ITransferObject> search(int start, int count) throws ManagerBeanException {
        Criteria criteria = getCriteria();
        LOGGER.info("Searching Expression:[" + ((criteria != null) ? criteria.toString() : null) + ", " + start + ", " + count
                + "]");
        return getManagerBean().getList(criteria, start, count);
    }

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.ISearchable#setCriteria(com.code.aon.ql.Criteria)
     */
    public void setCriteria(Criteria criteria) throws ManagerBeanException {
        this.criteria = criteria;
    }

    /**
     * Return an object representing the data for the currently selected row index of the model associated to controller.
     * 
     * @return Object
     */
    protected Object getSelectedTO() {
        return this.model.getRowData();
    }

    /**
     * Return the zero-relative index of the currently selected row of the model associated to controller. 
     * 
     * @return int
     */
    protected int getSelectedTOIndex() {
        return this.model.getRowIndex();
    }

    /**
     * Return lookups in XML Format.
     * 
     * @return String
     */
    public String getLookupsAsXML() {
        int selectedIndex = getSelectedTOIndex();
        LOGGER.fine(">>>> getLookups rowIndex:[" + selectedIndex + "]");
        return getLookupsAsXML((ILookupObject) getSelectedTO(), this.ids);
    }

    /**
     * Executes search action.
     * 
     * @param event
     */
    @SuppressWarnings("unused")
    public void onSearch(ActionEvent event) {
        try {
            createCriteria();
            initializeModel();
        } catch (ManagerBeanException e) {
            LOGGER.severe(">>>> onSearch " + e.getMessage());
            addMessage(e.getMessage());
            try {
                clearCriteria();
            } catch (ManagerBeanException e1) {
                LOGGER.severe(">>>> Unable to clear crtieria! " + e.getMessage());
                addMessage(e.getMessage());
                throw new AbortProcessingException(e.getMessage(), e);
            }
        }
    }

    /**
     * Create the criteria.
     * 
     * @throws ManagerBeanException
     */
    private void createCriteria() throws ManagerBeanException {
        Criteria criteria = new Criteria();
        for (Map.Entry<String, Expression> entry : this.expressions.entrySet()) {
            if (entry.getValue() != null) {
                criteria.addExpression(entry.getValue());
            }
        }
        setCriteria(criteria);
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
     * Return values map.
     * 
     * @return Map<String, Object>
     */
    public Map<String, Object> getValues() {
        return values;
    }

    /**
     * Set values map.
     * 
     * @param values
     */
    public void setValues(Map<String, Object> values) {
        this.values = values;
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
                    this.values = new HashMap<String, Object>();
                    LOGGER.fine("Values resetted");
                }
            }
        }
        return false;
    }

    /**
     * Set involved parameters.
     * 
     * @param value
     */
    @SuppressWarnings("unused")
    public void setParameters(boolean value) {
    }

}
