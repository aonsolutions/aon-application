package com.code.aon.ui.tas.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.tas.Appraiser;
import com.code.aon.tas.Make;
import com.code.aon.tas.Model;
import com.code.aon.tas.SupportOrder;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.tas.enumeration.SupportOrderOperation;
import com.code.aon.tas.enumeration.SupportOrderStatus;

/**
 * This class provides this packages collection
 * 
 * @author Consulting & Development. igayarre - 28-ago-2006
 * 
 */
public class TASCollectionsController {

	/**
	 * Makes collection
	 */
	private List<SelectItem> makes;

	/**
	 * Returns make´s collection
	 * It is recovered once and stay in memory
	 * 
	 * @return makes 
	 * @throws ManagerBeanException
	 */
	public List<SelectItem> getMakes() throws ManagerBeanException {
		if (makes == null) {
			makes = new LinkedList<SelectItem>();
			IManagerBean makeBean = BeanManager.getManagerBean(Make.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(makeBean.getFieldName(ITASAlias.MAKE_NAME));
			Iterator<ITransferObject> iter = makeBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				Make make = (Make) iter.next();
				SelectItem item = new SelectItem(make.getId(), make.getName());
				makes.add(item);
			}
		}
		return makes;
	}

	/**
	 * Models collection
	 */
	private List<SelectItem> models;

	/**
	 * Returns model´s collection
	 * It is recovered once and stay in memory
	 * 
	 * @return models 
	 * @throws ManagerBeanException
	 */
	public List<SelectItem> getModels() throws ManagerBeanException {
		if (models == null) {
			models = new LinkedList<SelectItem>();
			IManagerBean modelBean = BeanManager.getManagerBean(Model.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(modelBean.getFieldName(ITASAlias.MODEL_NAME));
			List<ITransferObject> c = modelBean.getList(criteria);
			Iterator<ITransferObject> iter = c.iterator();
			while (iter.hasNext()) {
				Model model = (Model) iter.next();
				SelectItem item = new SelectItem(model.getId(), model.getName());
				models.add(item);
			}
		}
		return models;
	}

    /**
     * Returns a support order status list
     * 
     * @return support order statuses
     */
    public List<SelectItem> getSupportOrderStatuses() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        LinkedList<SelectItem> types = new LinkedList<SelectItem>();
        SupportOrderStatus[] cStatuses = SupportOrderStatus.values();
        for (int i = 0; i < cStatuses.length; i++) {
            SupportOrderStatus status = cStatuses[i];
            String name = status.getName(locale);
            SelectItem item = new SelectItem(status, name);
            types.add(item);
        }
        return types;
    }
    
    /**
     * Returns a support order operations list
     * 
     * @return support order operations
     */
    public List<SelectItem> getSupportOrderOperations() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        LinkedList<SelectItem> operationList = new LinkedList<SelectItem>();
        SupportOrderOperation[] operations = SupportOrderOperation.values();
        for (int i = 0; i < operations.length; i++) {
            SupportOrderOperation operation = operations[i];
            String name = operation.getName(locale);
            SelectItem item = new SelectItem(operation, name);
            operationList.add(item);
        }
        return operationList;
    }
    
	/**
	 * Recovers all support order with SupportOrderStatus.PENDING
	 * 
	 * @return pending support orders
	 * @throws ManagerBeanException
	 */
    @SuppressWarnings("unchecked")
	public List<SelectItem> getPendingSupportOrders() throws ManagerBeanException {
		LinkedList<SelectItem> supportOrders = new LinkedList<SelectItem>();
		Criteria criteria = new Criteria();
		IManagerBean supportOrderBean = BeanManager.getManagerBean(SupportOrder.class);
		criteria.addEqualExpression(supportOrderBean.getFieldName(ITASAlias.SUPPORT_ORDER_STATUS), SupportOrderStatus.PENDING);
		Iterator iter = supportOrderBean.getList(criteria).iterator();
		while(iter.hasNext()){
			SupportOrder supportOrder = (SupportOrder)iter.next();
			SelectItem item = new SelectItem(supportOrder.getId(), (supportOrder.getSeries()==null?"":supportOrder.getSeries()+"/")+supportOrder.getNumber()+ ": " +supportOrder.getTasItem().getPublicCode() + " / " + supportOrder.getTasItem().getModel().getMake().getName()+ " " + supportOrder.getTasItem().getModel().getName());
			supportOrders.add(item);
		}
		return supportOrders;
	}

	/**
	 * Recovers all support order with SupportOrderStatus.PENDING OR SupportOrderStatus.ACTIVE 
	 * 
	 * @return pending or active support orders
	 * @throws ManagerBeanException
	 */
    @SuppressWarnings("unchecked")
	public List<SelectItem> getPendingOrActiveSupportOrders() throws ManagerBeanException {
		LinkedList<SelectItem> supportOrders = new LinkedList<SelectItem>();
		Criteria criteria = new Criteria();
		IManagerBean supportOrderBean = BeanManager.getManagerBean(SupportOrder.class);
		Expression pendingExp = ExpressionUtilities.getEqualExpression(supportOrderBean.getFieldName(ITASAlias.SUPPORT_ORDER_STATUS), SupportOrderStatus.PENDING);
		Expression activeExp = ExpressionUtilities.getEqualExpression(supportOrderBean.getFieldName(ITASAlias.SUPPORT_ORDER_STATUS), SupportOrderStatus.ACTIVE);
		criteria.addExpression(ExpressionUtilities.getOrExpression(pendingExp, activeExp));
		Iterator iter = supportOrderBean.getList(criteria).iterator();
		while(iter.hasNext()){
			SupportOrder supportOrder = (SupportOrder)iter.next();
			SelectItem item = new SelectItem(supportOrder.getId(), (supportOrder.getSeries()==null?"":supportOrder.getSeries()+"/")+supportOrder.getNumber()+ ": " +supportOrder.getTasItem().getPublicCode() + " / " + supportOrder.getTasItem().getModel().getMake().getName()+ " " + supportOrder.getTasItem().getModel().getName());
			supportOrders.add(item);
		}
		return supportOrders;
	}

    @SuppressWarnings("unchecked")
    public List<SelectItem> getAppraisers() throws ManagerBeanException{
    	List<SelectItem> appraisers = new LinkedList<SelectItem>();
    	IManagerBean appraiserBean = BeanManager.getManagerBean(Appraiser.class);
    	Iterator iter = appraiserBean.getList(null).iterator();
    	while(iter.hasNext()){
    		Appraiser appraiser = (Appraiser)iter.next();
    		SelectItem item = new SelectItem(appraiser.getId(), (appraiser.getRegistry().getSurname()!=null?appraiser.getRegistry().getSurname() + " ":"") + (appraiser.getRegistry().getName()!=null?appraiser.getRegistry().getName():""));
    		appraisers.add(item);
    	}
    	return appraisers;
    }
}