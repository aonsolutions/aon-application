package com.code.aon.ui.finance.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Department;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.Pos;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.Shift;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class PosFinanceSearchListener extends ControllerSearchListener {

	private WorkPlace workPlace;
	private Department department;
	private Pos pos;
	private Shift shift;
	private PayMethod payMethod;

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

	public Pos getPos() {
		return pos;
	}

	public void setPos(Pos pos) {
		this.pos = pos;
	}
	
	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;
	}

	public PayMethod getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setWorkPlace((WorkPlace)BeanManager.getManagerBean(WorkPlace.class).createNewTo());
		setDepartment((Department)BeanManager.getManagerBean(Department.class).createNewTo());
		setPos((Pos)BeanManager.getManagerBean(Pos.class).createNewTo());
		setShift(null);
		setPayMethod((PayMethod)BeanManager.getManagerBean(PayMethod.class).createNewTo());
	}

	public List<SelectItem> getWorkPlacePos() throws ManagerBeanException {
		List<SelectItem> posList = new LinkedList<SelectItem>();
		if (getWorkPlace() != null && getWorkPlace().getId() != null) {
			IManagerBean posBean = BeanManager.getManagerBean(Pos.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(posBean.getFieldName(IEntityAlias.POS_WORK_PLACE_ID), getWorkPlace().getId());
			if (getDepartment() != null && getDepartment().getId() != null) {
				criteria.addEqualExpression(posBean.getFieldName(IEntityAlias.POS_DEPARTMENT_ID), getDepartment().getId());
			}
			criteria.addEqualExpression(posBean.getFieldName(IEntityAlias.POS_ACTIVE), new Boolean(true));
			criteria.addOrder(posBean.getFieldName(IEntityAlias.POS_NAME));
			for (ITransferObject ito : posBean.getList(criteria)) {
				Pos pos = (Pos)ito;
				SelectItem posItem = new SelectItem(pos, pos.getName());
				posList.add(posItem);
			}
		}
		return posList;
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_PAYMENT), false);
		criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
		if (getPos() != null && getPos().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_INVOICE_POS_SHIFT_POS_ID), getPos().getId());			
		} else {
			if (getWorkPlace() != null && getWorkPlace().getId() != null) {
				criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_INVOICE_POS_SHIFT_POS_WORK_PLACE_ID), getWorkPlace().getId());			
			}
			if (getDepartment() != null && getDepartment().getId() != null) {
				criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_INVOICE_POS_SHIFT_POS_DEPARTMENT_ID), getDepartment().getId());			
			}
		}
		if (getShift() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_INVOICE_POS_SHIFT_SHIFT), getShift());			
		}
		if (getPayMethod() != null && getPayMethod().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_PAY_METHOD_ID), getPayMethod().getId());
		}
	}

}