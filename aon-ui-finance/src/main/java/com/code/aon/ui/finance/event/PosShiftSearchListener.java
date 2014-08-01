package com.code.aon.ui.finance.event;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Department;
import com.code.aon.company.WorkPlace;
import com.code.aon.finance.Pos;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosShiftSearchListener extends ControllerSearchListener {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private WorkPlace workPlace;
	private Department department;
	private Pos pos;
	private Date startTimeFrom;
	private Date startTimeTo;
	private Date endTimeFrom;
	private Date endTimeTo;

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
	
	public Date getStartTimeFrom() {
		return startTimeFrom;
	}

	public void setStartTimeFrom(Date startTimeFrom) {
		this.startTimeFrom = startTimeFrom;
	}

	public Date getStartTimeTo() {
		return startTimeTo;
	}

	public void setStartTimeTo(Date startTimeTo) {
		this.startTimeTo = startTimeTo;
	}

	public Date getEndTimeFrom() {
		return endTimeFrom;
	}

	public void setEndTimeFrom(Date endTimeFrom) {
		this.endTimeFrom = endTimeFrom;
	}

	public Date getEndTimeTo() {
		return endTimeTo;
	}

	public void setEndTimeTo(Date endTimeTo) {
		this.endTimeTo = endTimeTo;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setWorkPlace((WorkPlace)BeanManager.getManagerBean(WorkPlace.class).createNewTo());
		setDepartment((Department)BeanManager.getManagerBean(Department.class).createNewTo());
		setPos((Pos)BeanManager.getManagerBean(Pos.class).createNewTo());
		setStartTimeFrom(null);
		setStartTimeTo(null);
		setEndTimeFrom(null);
		setEndTimeTo(null);
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
			criteria.addEqualExpression(posBean.getFieldName(IEntityAlias.POS_ACTIVE), Boolean.TRUE);
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
		if (getWorkPlace() != null && getWorkPlace().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_POS_WORK_PLACE_ID), getWorkPlace().getId());			
		}
		if (getDepartment() != null && getDepartment().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_POS_DEPARTMENT_ID), getDepartment().getId());			
		}
		if (getPos() != null && getPos().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_POS_ID), getPos().getId());			
		}
		if (!AonUtil.getRoleManager().isConfig() && !AonUtil.getRoleManager().isSaleOperator()) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_USERNAME), UserUtils.getInstance().getLoggedUser().getLogin());			
		}
		if (getStartTimeFrom() != null) {
			criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_START_TIME), getStartTimeFrom());
		}
		if (getStartTimeTo() != null) {
			int millisFullDay = (int)(DateUtils.MILLIS_PER_DAY - DateUtils.MILLIS_PER_SECOND);
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_START_TIME), DateUtils.addMilliseconds(getStartTimeTo(), millisFullDay));
		}
		if (getEndTimeFrom() != null) {
			criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_END_TIME), getEndTimeFrom());
		}
		if (getEndTimeTo() != null) {
			int millisFullDay = (int)(DateUtils.MILLIS_PER_DAY - DateUtils.MILLIS_PER_SECOND);
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_END_TIME), DateUtils.addMilliseconds(getEndTimeTo(), millisFullDay));
		}
	}

}