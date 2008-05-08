package com.code.aon.ui.commercial.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.PageDataModel;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.report.OutputFormat;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the offer maintenance.
 */
public class OfferController extends BasicController {
	
	/** The id of the support order related with the current offer. */
	private Integer supportOrderId;

    /**
     * Description
     */
    private String description;
	
	/**
	 * Gets the support order id.
	 * 
	 * @return the support order id
	 */
	public Integer getSupportOrderId() {
		return supportOrderId;
	}

	/**
	 * Sets the support order id.
	 * 
	 * @param supportOrderId the support order id
	 */
	public void setSupportOrderId(Integer supportOrderId) {
		this.supportOrderId = supportOrderId;
	}
	
    /**
     * Returns the desciption
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Assigns the desciption
     * 
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

	/**
	 * Checks if the current offer is editable or not.
	 * 
	 * @return true, if the current offer editable
	 */
	public boolean isEditable(){
		if((Offer)this.getTo() != null && ((Offer)this.getTo()).getStatus() != null){
			if(((Offer)this.getTo()).getStatus().equals(OfferStatus.PROCESSED)){
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * On reset. Method launched by the menu
	 * 
	 * @param event the menu event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unused")
	public void onReset(MenuEvent event) throws ManagerBeanException{
		this.setModel(new PageDataModel(this,0,20));
		this.clearCriteria();
		super.onReset(null);
	}
	
	/**
	 * Adds a greaterThan expression of the field issueDate to the <code>criteria</code>.
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void addIssueDateGreaterThanExpression(ValueChangeEvent event)
		throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addGreaterThanOrEqualExpression(getFieldName(ICommercialAlias.OFFER_ISSUE_DATE), value);
			setCriteria(c);
		}
	}
	
	/**
	 * Adds a lessThan expression of the field issueDate to the <code>criteria</code>.
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void addIssueDateLessThanExpression(ValueChangeEvent event)
		throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addLessThanOrEqualExpression(getFieldName(ICommercialAlias.OFFER_ISSUE_DATE), value);
			setCriteria(c);
		}
	}
	
	/**
	 * Adds an equal expression of the field target to the <code>criteria</code>.
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 * @throws ExpressionException the expression exception
	 */
	public void addTargetExpression(ValueChangeEvent event)
		throws ManagerBeanException, ExpressionException {
	    if ((event.getNewValue() != null)
	    		&& (!"".equals(event.getNewValue().toString().trim()))
	    		) {
			Criteria criteria = new Criteria();
			IManagerBean bean = BeanManager.getManagerBean(Target.class);
			String identifier = bean.getFieldName(ICommercialAlias.TARGET_REGISTRY_DOCUMENT);
			criteria.addEqualExpression(identifier, event.getNewValue());
			List list = bean.getList(criteria);
			Iterator iter = list.iterator();
	    	Criteria c = getCriteria();
	    	if (iter.hasNext()){
				while (iter.hasNext()){
					Target t = (Target)iter.next();
					Object value = t.getId(); 
					c.addEqualExpression(getFieldName(ICommercialAlias.OFFER_TARGET_ID), value);
					setCriteria(c);
				}
	    	}else{
				Object value = new Integer(-1); 
				c.addEqualExpression(getFieldName(ICommercialAlias.OFFER_TARGET_ID), value);
				setCriteria(c);
	    	}
		}
	}
	
	/**
	 * Adds an equal expression to the <code>criteria</code> retrieving the field name from the <code>event</code>.
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void addEqualExpression(ValueChangeEvent event)
		throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addEqualExpression(getFieldName(event.getComponent().getId()), value);
			setCriteria(c);
		}
	}

	/**
     * Sets default parameters to report.
     * 
     * @param event that launched report
     */
    @SuppressWarnings("unused")
    public void onReport(ActionEvent event) {
        ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("offer");
        manager.setOutputFormat(OutputFormat.PDF);
    }
}