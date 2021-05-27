package com.esferalia.aon.ui.payroll.event.contract;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class ContractSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Person person;
	private WorkPlace workPlace;
	private Date endDateFrom;
	private Date endDateTo;
	private boolean active;
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getEndDateFrom() {
		return endDateFrom;
	}

	public void setEndDateFrom(Date endDateFrom) {
		this.endDateFrom = endDateFrom;
	}

	public Date getEndDateTo() {
		return endDateTo;
	}

	public void setEndDateTo(Date endDateTo) {
		this.endDateTo = endDateTo;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		IManagerBean personBean = BeanManager.getManagerBean(Person.class);
		setPerson((Person) personBean.createNewTo());
		setWorkPlace(null);
		setEndDateTo(null);
		setActive(true);
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		criteria.addGreaterThanExpression(getFieldName(IEntityAlias.CONTRACT_ID), 0);			
		if ((getPerson() != null) && (getPerson().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.CONTRACT_PERSON_ID), getPerson().getId());			
		}
		if ((getWorkPlace() != null) && (getWorkPlace().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ID), getWorkPlace().getId());			
		}
		if(isActive()){
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_END_DATE), new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(getFieldName(IEntityAlias.CONTRACT_END_DATE));
			criteria.addExpression( ExpressionUtilities.getOrExpression(expr1, expr2) );
		} else {
			if(getEndDateFrom()!=null){
				criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_END_DATE), getEndDateFrom());			
			}
			if(getEndDateTo()!=null){
				criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_END_DATE), getEndDateTo());			
			}
		}
	}
	
	public List<SelectItem> getWorkPlaces() throws ManagerBeanException {
		PayrollUtils utils = PayrollUtils.getInstance();
		List<SelectItem> workPlaces = new LinkedList<SelectItem>();
		IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_ACTIVE), new Boolean(true));
		criteria.addEqualExpression(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_ENTERPRISE_ID), utils.getCurrentDomainEnterprise().getId());
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_SCOPE_ID));
		criteria.addOrder(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_DESCRIPTION));
		for(ITransferObject to: workPlaceBean.getList(criteria)){
			WorkPlace workPlace = (WorkPlace)to;
			workPlaces.add(new SelectItem(workPlace, workPlace.getDescription()));
		}
		return workPlaces;
	}	

}