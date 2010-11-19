package com.code.aon.ui.employee.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.employee.Salary;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;

public class SalaryPrintController {
	
	private SalaryController controller;
	
	private Enterprise enterprise;
	
	private List<SelectItem> workPlaces;
	
	private boolean showWorkPlaces;
	
	private WorkPlace workPlace;
	
	private Date fromDate;
	
	private Date toDate;
	
	private Set<Integer> checks = new HashSet<Integer>();
	
	public SalaryPrintController() {
		controller = (SalaryController) AonUtil.getRegisteredBean(ICompanyConstants.SALARY_CONTROLLER_NAME);
	}

	public void onInit( ActionEvent event ) throws ManagerBeanException {
		EnterpriseController ec = (EnterpriseController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
		this.enterprise = (Enterprise) ec.getTo();		
		loadWorkPlaces( enterprise );
		clearFilters();
		resetCriteria();
		controller.initializeModel();
	}

	public void onClearFilter( ActionEvent event ) throws ManagerBeanException {
		clearFilters();
		resetCriteria();
		controller.initializeModel();
	}
	
	public void onFilter( ActionEvent event ) throws ManagerBeanException {
		resetCriteria();
		Criteria criteria = controller.getCriteria();
		if ( fromDate != null ) {
			String alias = controller.getFieldName(IEmployeeAlias.SALARY_START_DATE);
			criteria.addGreaterThanOrEqualExpression(alias, fromDate);
		}
		if ( toDate != null ) {
			String alias = controller.getFieldName(IEmployeeAlias.SALARY_START_DATE);
			criteria.addLessThanOrEqualExpression(alias, toDate);
		}
		if ((getWorkPlace() != null) && (getWorkPlace().getId() != null)) {
			String alias = controller.getFieldName(IEmployeeAlias.SALARY_CONTRACT_WORK_PLACE_ID);
			criteria.addEqualExpression(alias, getWorkPlace().getId());			
		}		
		controller.initializeModel();
	}
	
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		String id = controller.getFieldName(IEmployeeAlias.SALARY_ID);
		ProjectionList pl = new ProjectionList( Projection.property(id) );
		List<Integer> list = controller.getManagerBean().getList(pl, controller.getCriteria());
		checks.clear();
		checks.addAll( list );
	}

	public void checkNone(ActionEvent event) {
		clearChecked();
	}

	private void clearFilters() {
		this.fromDate = null;
		this.toDate = null;
		this.workPlace = new WorkPlace();
	}
	
	private void resetCriteria() throws ManagerBeanException {
		controller.clearCriteria();
		Criteria criteria = controller.getCriteria();
		String alias = controller.getFieldName(IEmployeeAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID);
		criteria.addEqualExpression(alias, enterprise.getId());		
	}

	public boolean isRowChecked() throws ManagerBeanException {
		Salary to = (Salary) controller.getModel().getRowData();
		return checks.contains(to.getId());
	}

	public void setRowChecked(boolean rowChecked) throws ManagerBeanException {
		Integer id = ((Salary) controller.getModel().getRowData()).getId();		
		if (rowChecked) {
			if (!checks.contains(id)) {
				checks.add(id);
			}
		} else {
			if (checks.contains(id)) {
				checks.remove(id);
			}
		}
	}

	public Set<Integer> getChecked() {
		return checks;
	}

	public void clearChecked() {
		checks = new HashSet<Integer>();
	}
	
	public boolean isShowWorkPlaces() {
		return showWorkPlaces;
	}

	public List<SelectItem> getWorkPlaces() {
		return workPlaces;
	}
	
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public void loadWorkPlaces( Enterprise enterprise) throws ManagerBeanException {
		this.workPlaces = null;
		this.showWorkPlaces = false;
		IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
		Criteria criteria = new Criteria();
		String enterpriseId = bean.getFieldName(ICompanyAlias.WORK_PLACE_ENTERPRISE_ID);
		criteria.addEqualExpression(enterpriseId, enterprise.getId());
		criteria.addOrder(bean.getFieldName(ICompanyAlias.WORK_PLACE_DESCRIPTION));
		if ( bean.getCount(criteria) > 1 ) {
			List<ITransferObject> list = bean.getList(criteria);
			this.workPlaces = new LinkedList<SelectItem>();
			for (ITransferObject to : list) {
				WorkPlace workPlace = (WorkPlace)to;
				workPlaces.add(new SelectItem(workPlace, workPlace.getDescription()));
			}
			this.showWorkPlaces = true;
		}
	}	
	
}