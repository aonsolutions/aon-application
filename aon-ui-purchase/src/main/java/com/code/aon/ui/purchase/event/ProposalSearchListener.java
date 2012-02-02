package com.code.aon.ui.purchase.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.WorkplaceDepartment;
import com.code.aon.purchase.enumeration.ProposalStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class ProposalSearchListener extends ControllerSearchListener {
	
	private WorkPlace workPlace;
	private WorkplaceDepartment workplaceDepartment;
	private ProposalStatus[] proposalStatuses;
			
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}
	
	public WorkplaceDepartment getWorkplaceDepartment() {
		return workplaceDepartment;
	}

	public void setWorkplaceDepartment(WorkplaceDepartment workplaceDepartment) {
		this.workplaceDepartment = workplaceDepartment;
	}

	public ProposalStatus[] getProposalStatuses() {
		return proposalStatuses;
	}

	public void setProposalStatuses(ProposalStatus[] proposalStatuses) {
		this.proposalStatuses = proposalStatuses;
	}

	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setWorkPlace(new WorkPlace());
		setWorkplaceDepartment(new WorkplaceDepartment());
		ProposalStatus[] defaultProposalStatus = {ProposalStatus.PENDING};
		setProposalStatuses(defaultProposalStatus);
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		super.completeCriteria( criteria );		
		if (getWorkPlace() != null && getWorkPlace().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROPOSAL_WORK_PLACE_ID), getWorkPlace().getId());			
		}
		if (getWorkplaceDepartment() != null && getWorkplaceDepartment().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROPOSAL_WORKPLACE_DEPARTMENT_ID), getWorkplaceDepartment().getId());			
		}
		if (!ArrayUtils.isEmpty(getProposalStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.PROPOSAL_STATUS);
			addEnumToCriteria(criteria, status, getProposalStatuses());
		}
	}	
	
	public List<SelectItem> getWorkplaceDepartments() throws ManagerBeanException{
		List<SelectItem> list = new LinkedList<SelectItem>();
		if(getWorkPlace()!=null && getWorkPlace().getId()!=null){
			IManagerBean bean = BeanManager.getManagerBean(WorkplaceDepartment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_WORK_PLACE_ID), getWorkPlace().getId());
			for (ITransferObject ito : bean.getList(criteria)) {
				WorkplaceDepartment wd = (WorkplaceDepartment)ito;
				SelectItem item = new SelectItem(wd, wd.getDepartment().getName());
				list.add(item);
			}
		}
		return list;
	}

}