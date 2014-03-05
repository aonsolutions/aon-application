package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_POS_OPENED;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Department;
import com.code.aon.company.WorkPlace;
import com.code.aon.finance.Pos;
import com.code.aon.finance.PosShift;
import com.code.aon.finance.enumeration.Shift;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.finance.util.PosUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosOpeningController {

	private PosShift posShift;
	private WorkPlace workPlace;
	private Department department;
	private CashCalculator calculator;

	public PosShift getPosShift() {
		return posShift;
	}

	public void setPosShift(PosShift posShift) {
		this.posShift = posShift;
	}
	
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

	public CashCalculator getCalculator() {
		if (calculator == null) {
			calculator = new CashCalculator();
		}
		return calculator;
	}

	public void setCalculator(CashCalculator calculator) {
		this.calculator = calculator;
	}

	public void onReset(ActionEvent event) {
		if (PosUtils.isUserPosShiftOpened()) {
			String msg = "Actualmente ya hay una Caja abierta por el Usuario.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		setPosShift(new PosShift());
		setWorkPlace(null);
		setDepartment(null);
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

	public List<SelectItem> getShifts() throws ManagerBeanException {
		List<SelectItem> shiftList = new LinkedList<SelectItem>();
		if (getPosShift().getPos() != null && getPosShift().getPos().getId() != null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			for (Shift shift : Shift.values()) {
				SelectItem item = new SelectItem(shift, shift.getName(locale));
				shiftList.add(item);
			}

			IManagerBean posShiftBean = BeanManager.getManagerBean(PosShift.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_POS_ID), getPosShift().getPos().getId());
			criteria.addNullExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_END_TIME));
			for (ITransferObject ito : posShiftBean.getList(criteria)) {
				PosShift posShift = (PosShift)ito;
				for (SelectItem item : shiftList) {
					if ((item.getValue().equals(posShift.getShift()))) {
						String label = " (" + AonUtil.getMessage(FINANCE_POS_OPENED) + ")";
						item.setLabel(item.getLabel() + label);
						item.setDisabled(true);
					}
				}
			}
		}
		return shiftList;
	}

	public void onAccept(ActionEvent event) {
		if (getPosShift().getId() == null && validateOpening(getPosShift().getPos(), getPosShift().getShift())) {
			getPosShift().setStartTime(new Date());
			getPosShift().setUsername(UserUtils.getInstance().getLoggedUser().getLogin());
		}

		try {
			getPosShift().setInitialAmount(getPosShift().getAmount());
			setPosShift((PosShift)BeanManager.getManagerBean(PosShift.class).insertOrUpdate(getPosShift()));
		} catch (ManagerBeanException ex) {
			String msg = "Error en el proceso de Apertura de Caja.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private boolean validateOpening(Pos pos, Shift shift) {
		if (PosUtils.isUserPosShiftOpened()) {
			String msg = "Actualmente ya hay una Caja abierta por el Usuario.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		if (PosUtils.isPosShiftAlreadyOpened(pos, shift)) {
			String msg = "No es posible abrir la Caja. Ya ha sido abierta por otro Usuario.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		return true;
	}

	public void onShowCalculatorWindow(ActionEvent event) {
		getCalculator().initialize();
	}

	public void onAcceptCalculatorWindow(ActionEvent event) {
	}

}
