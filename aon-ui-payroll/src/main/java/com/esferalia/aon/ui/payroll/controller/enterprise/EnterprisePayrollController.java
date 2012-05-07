package com.esferalia.aon.ui.payroll.controller.enterprise;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.esferalia.aon.payroll.EnterpriseActivity;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.EnterpriseActivityType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class EnterprisePayrollController {
	
	private boolean showActivityNode;
	private EnterpriseActivity activity;	
	private EnterpriseCCC ccc;
	
	
	public boolean isShowActivityNode() {
		return showActivityNode;
	}

	public EnterpriseActivity getActivity() {
		if(activity==null){
			try {
				loadMainActivity();
			} catch (ManagerBeanException e) {
				// NADA, la actividad se queda nula
			}
		}
		return activity;
	}

	public void setActivity(EnterpriseActivity activity) {
		this.activity = activity;
	}
	
	public EnterpriseCCC getCcc() {
		return ccc;
	}

	public void setCcc(EnterpriseCCC ccc) {
		this.ccc = ccc;
	}
    
    /**
     * Gets the CCCs of the enterprise.
     * 
     * @return the CCCs of the enterprise
     * @throws ManagerBeanException 
     */
    public List<SelectItem> getCCCs() throws ManagerBeanException {
    	LinkedList<SelectItem> cccs = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID), getEnterprise().getId());
		criteria.addOrder(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_CCC));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			EnterpriseCCC ccc = (EnterpriseCCC)to;
			cccs.add(new SelectItem(ccc, ccc.getCcc()));
		}
    	return cccs;
    }	    
    
    /**
     * Gets the Activities of the enterprise.
     * 
     * @return the Activities of the enterprise
     * @throws ManagerBeanException 
     */
    public List<SelectItem> getActivities() throws ManagerBeanException {
    	LinkedList<SelectItem> list = new LinkedList<SelectItem>();
    	IManagerBean bean = BeanManager.getManagerBean(EnterpriseActivity.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_ENTERPRISE_ID), getEnterprise().getId());
    	criteria.addOrder(bean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_TYPE));
    	for (ITransferObject to : bean.getList(criteria)) {
    		EnterpriseActivity a = (EnterpriseActivity)to;
    		String label = a.getDescription();
    		list.add(new SelectItem(a, label));
    	}
    	return list;
    }	    

	private void loadMainActivity() throws ManagerBeanException {
		IManagerBean activityBean = BeanManager.getManagerBean(EnterpriseActivity.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_ENTERPRISE_ID), getEnterprise().getId());
		criteria.addEqualExpression(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_TYPE), EnterpriseActivityType.PRINCIPAL);
		List<ITransferObject> activities = activityBean.getList(criteria);
		if (! activities.isEmpty() ) {
			setActivity( (EnterpriseActivity) activities.get(0) );
			IManagerBean cccBean = BeanManager.getManagerBean(EnterpriseCCC.class);
			Criteria cccCriteria = new Criteria();
			cccCriteria.addEqualExpression(cccBean.getFieldName(IEntityAlias.ENTERPRISE_CCC_ACTIVITY_ID), getActivity().getId());
			cccCriteria.addEqualExpression(cccBean.getFieldName(IEntityAlias.ENTERPRISE_CCC_TYPE), CCCType.PRINCIPAL);
			List<ITransferObject> cccs = cccBean.getList(cccCriteria);
			if (! cccs.isEmpty() ) {
				setCcc( (EnterpriseCCC) cccs.get(0) );
			}
			this.showActivityNode = (activities.size() > 1) || (cccs.size() > 1);
		}
	}    
	
	public void reset() {
    	this.showActivityNode = false;
    	setActivity(null);
    	setCcc(null);
	}
    
    public void initMainActiviy() throws ManagerBeanException {
    	IController controller = FormUtil.getController(IPayrollConstants.ENTERPRISE_CONTROLLER);
    	if (! controller.isNew() ) {
    		loadMainActivity();
    	}
    }
    
    public Enterprise getEnterprise() {
    	IController controller = FormUtil.getController(IPayrollConstants.ENTERPRISE_CONTROLLER);
    	return (Enterprise) controller.getTo();
    }
    
	
}