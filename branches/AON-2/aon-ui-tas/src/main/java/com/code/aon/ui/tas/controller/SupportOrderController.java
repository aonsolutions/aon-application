package com.code.aon.ui.tas.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.tas.SupportOrder;
import com.code.aon.tas.TasItem;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.tas.enumeration.SupportOrderStatus;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.report.OutputFormat;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller for Support Order.
 * 
 * @author Consulting & Development.
 */
public class SupportOrderController   extends BasicController {
	
	/**
	 * Phone linked to the target of the support order
	 */
	private RegistryMedia phone;

	/**
	 * cellular linked to the target of the support order
	 */
	private RegistryMedia cellular;

	/**
	 * the target of the support order
	 */
	private Target target;

	/**
	 * Notifies if the target changed during the same support order
	 */
	private boolean targetDirty;

	/**
	 * Tas Item linked to the support order
	 */
	private TasItem tasItem;

	/**
	 * Notifies if the tas item changed during the same support order
	 */
	private boolean tasItemDirty;
	
    /**
     * Default constructor.
     * 
     * @throws ManagerBeanException
     */
    public SupportOrderController() throws ManagerBeanException {
        super();
    }

	/**
	 * Is the target dirty
	 * 
	 * @return is target dirty.
	 */
	public boolean isTargetDirty() {
		return targetDirty;
	}


	/**
	 * Returns the target
	 * 
	 * @return the target
	 */
	public Target getTarget() {
		return target;
	}

	/**
	 * Assigns the target
	 * 
	 * @param target the target to set
	 */
	public void setTarget(Target target) {
		this.target = target;
	}

	/**
	 * Return all the employee info as a string
	 * 
	 * @return the employee
	 */
	public String getEmployee() {
		try{
			SupportOrder so = (SupportOrder)this.getTo();
			String name = so.getEmployee().getRegistry().getName()==null?"":so.getEmployee().getRegistry().getName(); 
			String surname = so.getEmployee().getRegistry().getSurname()==null?"":so.getEmployee().getRegistry().getSurname();
			return name +
				" "+
				surname;
		}catch (Exception e) {
		}
		return "";
	}

	/**
	 * Retrns the tas item
	 * 
	 * @return the tasItem
	 */
	public TasItem getTasItem() {
		return tasItem;
	}

	/**
	 * Assigns the tas item
	 * 
	 * @param tasItem the tasItem to set
	 */
	public void setTasItem(TasItem tasItem) {
		this.tasItem = tasItem;
	}

	/**
	 * notifies if the tas item is dirty
	 * 
	 * @return is tas item dirty?
	 */
	public boolean isTasItemDirty() {
		return tasItemDirty;
	}

	/**
	 * Returns the phone
	 * 
	 * @return the phone.
	 */
	public RegistryMedia getPhone() {
		return phone;
	}



	/**
	 * Assigns the phone
	 * 
	 * @param phone The phone to set.
	 */
	public void setPhone(RegistryMedia phone) {
		this.phone = phone;
	}


	/**
	 * Returns the cellular
	 * 
	 * @return the cellular
	 */
	public RegistryMedia getCellular() {
		return cellular;
	}

	/**
	 * Assigns the cellular
	 * 
	 * @param movile the cellular to set
	 */
	public void setCellular(RegistryMedia cellular) {
		this.cellular = cellular;
	}

	/**
	 * accepts the transfer object and reset dirty tags
	 * 
	 * @param action event
	 * @see com.code.aon.ui.form.BasicController#onAccept(javax.faces.event.ActionEvent)
	 */
	public void onAccept(ActionEvent event) {
		accept(event);
		resetDirty();
	}
	
	/**
	 *  reset dirty tags
	 */
	public void resetDirty(){
		targetDirty = false;
		tasItemDirty = false;
	}

	
	/**
	 * Notify that target changed
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unused")
	public void targetChanged(ValueChangeEvent event) throws ManagerBeanException {
		targetDirty = true;
	}

	/**
	 * Notify that tas item changed
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unused")
	public void tasItemChanged(ValueChangeEvent event) throws ManagerBeanException {
		tasItemDirty = true;
	}

	/**
	 * Resets the controller, new support order
	 * 
	 * @param event
	 */
	@SuppressWarnings("unused")
	public void onNewSupportOrder(MenuEvent event){
		onReset(null);
	}

	/**
	 * Returns if support order is finished
	 * 
	 * @return is finished
	 */
	public boolean isFinished(){
		SupportOrder so = (SupportOrder)this.getTo();
		return SupportOrderStatus.FINISHED == so.getStatus();
	}
	
	/**
	 * Returns if support order is pending
	 * 
	 * @return is pending
	 */
	public boolean isPending(){
		SupportOrder so = (SupportOrder)this.getTo();
		return SupportOrderStatus.PENDING == so.getStatus();
	}
	
	/**
	 * adds equal condition to criteria if needed
	 * 
	 * @param event
	 * @throws ManagerBeanException
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
	 * adds start date greater than or equal condition to criteria if needed
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void addStartGreaterThanExpression(ValueChangeEvent event)
		throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addGreaterThanOrEqualExpression(getFieldName(ITASAlias.SUPPORT_ORDER_START_DATE), value);
			setCriteria(c);
		}
	}

	/**
	 * adds start date less than or equal condition to criteria if needed
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void addStartLessThanExpression(ValueChangeEvent event)
		throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addLessThanOrEqualExpression(getFieldName(ITASAlias.SUPPORT_ORDER_START_DATE), value);
			setCriteria(c);
		}
	}

	/**
	 * adds end date greater than or equal condition to criteria if needed
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void addEndGreaterThanExpression(ValueChangeEvent event)
		throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addGreaterThanOrEqualExpression(getFieldName(ITASAlias.SUPPORT_ORDER_FINAL_DATE), value);
			setCriteria(c);
		}
	}
	
	/**
	 * adds end date less than or equal condition to criteria if needed
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void addEndLessThanExpression(ValueChangeEvent event)
		throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addLessThanOrEqualExpression(getFieldName(ITASAlias.SUPPORT_ORDER_FINAL_DATE), value);
			setCriteria(c);
		}
	}
	
	/**
	 * Adds to criteria all support order for this target
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 * @throws ExpressionException
	 */
	@SuppressWarnings("unchecked")
	public void addTargetExpression(ValueChangeEvent event)
		throws ManagerBeanException, ExpressionException {
	    if ((event.getNewValue() != null)
	    		&& (!"".equals(event.getNewValue().toString().trim()))
	    		) {
			Criteria criteria = new Criteria();
			IManagerBean bean = BeanManager.getManagerBean(Target.class);
			String identifier = bean.getFieldName(ICommercialAlias.TARGET_REGISTRY_DOCUMENT);
			criteria.addExpression(identifier, event.getNewValue().toString());
			List list = bean.getList(criteria);
			Iterator iter = list.iterator();
	    	Criteria c = getCriteria();
	    	if (iter.hasNext()){
	    		String values = null;
				while (iter.hasNext()){
					if (values==null){
						values="";
					}else{
						values+="|";
					}
					Target t = (Target)iter.next();
					values += t.getId();
				}
				c.addExpression(getFieldName(ITASAlias.SUPPORT_ORDER_TARGET_ID), values);
				setCriteria(c);
	    	}else{
				Object value = new Integer(-1); 
				c.addEqualExpression(getFieldName(ITASAlias.SUPPORT_ORDER_TARGET_ID), value);
				setCriteria(c);
	    	}
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
        manager.setReportKey("SupportOrderForm");
        manager.setOutputFormat(OutputFormat.PDF);
    }
    
	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException{
		if (event.getPhaseId() == PhaseId.ANY_PHASE) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION );
			event.queue();
		}
		if (event.getPhaseId() == PhaseId.INVOKE_APPLICATION) {
			int number = obtainMaxNumber((String)event.getNewValue());
			if(this.getTo() != null){
				((SupportOrder)this.getTo()).setNumber(number);	
			}
		}
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
		IManagerBean supportOrderBean = BeanManager.getManagerBean(SupportOrder.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(supportOrderBean.getFieldName(ITASAlias.SUPPORT_ORDER_SERIES), seriesId);
		Projection projection = Projection.max(supportOrderBean.getFieldName(ITASAlias.SUPPORT_ORDER_NUMBER));
		Object value = supportOrderBean.getUniqueResult(projection, criteria);
		if(value != null){
			return ((Integer)value).intValue() + 1;
		}
		return 1;
	}
	
	@SuppressWarnings("unchecked")
	public void reloadMedias(ActionEvent event) throws ManagerBeanException{
		this.setPhone(new RegistryMedia());
		this.getPhone().setRegistry(this.getTarget().getRegistry());
		this.getPhone().setMediaType(MediaType.FIXED_PHONE);
		this.setCellular(new RegistryMedia());
		this.getCellular().setRegistry(this.getTarget().getRegistry());
		this.getCellular().setMediaType(MediaType.CELLULAR);
		Criteria criteriaMedia = new Criteria();
		IManagerBean beanMedia = BeanManager.getManagerBean( RegistryMedia.class);
		String registryIdFieldName = beanMedia.getFieldName( IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID);
		criteriaMedia.addEqualExpression(registryIdFieldName,target.getId() );
		List mediaList = beanMedia.getList(  criteriaMedia );
		
		Iterator mediaIter = mediaList.iterator();
		while (mediaIter.hasNext()){
			RegistryMedia rmedia = (RegistryMedia)mediaIter.next();
			if (MediaType.FIXED_PHONE == rmedia.getMediaType()){
				this.setPhone(rmedia); 
			}else if (MediaType.CELLULAR == rmedia.getMediaType()){
				this.setCellular(rmedia); 
			}
		}
	}
}