package com.code.aon.ui.purchase.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Department;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.WorkplaceDepartment;
import com.code.aon.purchase.enumeration.ProposalStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ProposalSearchListener extends ControllerSearchListener {
	
	private WorkPlace workPlace;
	private Department department;
	private ProposalStatus[] proposalStatuses;
			
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}
	
	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
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
		setDepartment(new Department());
		ProposalStatus[] defaultProposalStatus = {ProposalStatus.PENDING, ProposalStatus.PARTIAL_PROCESSED};
		setProposalStatuses(defaultProposalStatus);
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		CompanyCollectionsController controller = (CompanyCollectionsController) AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		super.completeCriteria( criteria );		
		if (getWorkPlace() != null && getWorkPlace().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROPOSAL_WORK_PLACE_ID), getWorkPlace().getId());			
		} else {
			criteria.addInExpression(getFieldName(IEntityAlias.PROPOSAL_WORK_PLACE_ID), controller.getCurrentUserWorkPlacesIds());
		}
		if (getDepartment() != null && getDepartment().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROPOSAL_DEPARTMENT_ID), getDepartment().getId());			
		}
		if (!ArrayUtils.isEmpty(getProposalStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.PROPOSAL_STATUS);
			addEnumToCriteria(criteria, status, getProposalStatuses());
		}
	}	
	
	public List<SelectItem> getDepartments() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		try {
			List<Integer> deptartmentIds = new LinkedList<Integer>();
			if(getWorkPlace()!=null && getWorkPlace().getId()!=null){
				for (ITransferObject ito : getWorkplaceDepartments()) {
					WorkplaceDepartment wd = (WorkplaceDepartment)ito;
					deptartmentIds.add(wd.getDepartment().getId());
				}
				if(!deptartmentIds.isEmpty()){
					IManagerBean dBean = BeanManager.getManagerBean(Department.class);
					Criteria dCriteria = new Criteria();
					dCriteria.addInExpression(dBean.getFieldName(IEntityAlias.DEPARTMENT_ID), deptartmentIds);
					dCriteria.addOrder(dBean.getFieldName(IEntityAlias.DEPARTMENT_NAME));
					for (ITransferObject ito : dBean.getList(dCriteria)) {
						Department d = (Department)ito;
						SelectItem item = new SelectItem(d, d.getName());
						list.add(item);
					}
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los departamentos";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		return list;
	}
	
	private List<ITransferObject> getWorkplaceDepartments() throws ManagerBeanException {
		IManagerBean wdBean = BeanManager.getManagerBean(WorkplaceDepartment.class);
		Criteria wdCriteria = new Criteria();
		wdCriteria.addEqualExpression(wdBean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_WORK_PLACE_ID), getWorkPlace().getId());
		wdCriteria.addEqualExpression(wdBean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_ACTIVE), Boolean.TRUE);
		return wdBean.getList(wdCriteria);
	}
	
	

}