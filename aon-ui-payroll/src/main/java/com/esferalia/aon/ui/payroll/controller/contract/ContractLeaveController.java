package com.esferalia.aon.ui.payroll.controller.contract;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractLeave;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.ui.payroll.utils.NumberValidation;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class ContractLeaveController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractLeaveController.class.getName());
	
	private final int RELAPSE_IT_LIST_SIZE = 10;
	
	private ContractLeaveDetail leave;
	private ContractLeaveDetail discharge;
	private boolean editBases;
	private Double dailyCgcBase;
	private Double dailyCgpBase;
	private Double dailyRegBase;
	
	private IControllerListener contractFilter;
	
	
	public Double getDailyCgcBase() {
		return dailyCgcBase;
	}

	public void setDailyCgcBase(Double dailyCgcBase) {
		this.dailyCgcBase = dailyCgcBase;
	}

	public Double getDailyCgpBase() {
		return dailyCgpBase;
	}

	public void setDailyCgpBase(Double dailyCgpBase) {
		this.dailyCgpBase = dailyCgpBase;
	}

	public Double getDailyRegBase() {
		return dailyRegBase;
	}

	public void setDailyRegBase(Double dailyRegBase) {
		this.dailyRegBase = dailyRegBase;
	}

	public boolean isEditBases() {
		return editBases;
	}

	public void setEditBases(boolean editBases) {
		this.editBases = editBases;
	}

	public ContractLeaveDetail getLeave() {
		return leave;
	}

	public void setLeave(ContractLeaveDetail leave) {
		this.leave = leave;
	}

	public ContractLeaveDetail getDischarge() {
		return discharge;
	}

	public void setDischarge(ContractLeaveDetail discharge) {
		this.discharge = discharge;
	}

	public List<ITransferObject> getLeaveList() {
		ContractLeave leave = (ContractLeave) this.getTo();
		if( leave.getContract()!=null && leave.getContract().getId()!=null){
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContractLeave.class);
				Criteria criteria = new Criteria();
				if(!this.isNew()){
					criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_ID), leave.getId());
				}
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_CONTRACT_ID), leave.getContract().getId());
				criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_START_DATE), false);
				return bean.getList(criteria);
			} catch (ManagerBeanException e) {
				String msg = "Imposible cargar los partes. [" + e.getMessage() + "]";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg, e);
			}
		}
		return null;
	}
	
	public Integer getItDays(){
		ContractLeave leave = (ContractLeave) this.getTo();
		if(leave!=null && leave.getId()!=null){
			int days = Integer.parseInt(String.valueOf(CommonUtil.getDaysBetweenDates(leave.getStartDate(), leave.getEndDate()!=null?leave.getEndDate():new Date(), false)));
			return days>=0?days+1:0;
		}
		return null;
	}
	
	public Date getTodayDate(){
		return new Date();
	}
	
	public Boolean getValidLeaveCollegeNumber() {
		return checkCollegeNumber(getLeave());
	}
	
	public Boolean getValidLeaveCias() {
		return checkCiasNumber(getLeave());
	}
	public Boolean getValidDischargeCollegeNumber() {
		return checkCollegeNumber(getDischarge());
	}
	
	public Boolean getValidDischargeCias() {
		return checkCiasNumber(getDischarge());
	}
	
	public Boolean checkCollegeNumber(ContractLeaveDetail detail){
		if (detail!=null && !StringUtils.isBlank(detail.getCollegeNumber())) {
			if (NumberValidation.validCollegeNumberPattern(detail.getCollegeNumber())
					&& NumberValidation.validCollegeNumberControlDigit(detail.getCollegeNumber())) {
				return true;
			} else {
				return false;
			}
		} else {
			return null;
		}
	}
	
	public Boolean checkCiasNumber(ContractLeaveDetail detail){
		if (detail!=null && !StringUtils.isBlank(detail.getCias())) {
			if (NumberValidation.validCiasPattern(detail.getCias())
					&& NumberValidation.validCiasControlDigit(detail .getCias())) {
				return true;
			} else {
				return false;
			}
		} else {
			return null;
		}
	}
	
	public List<SelectItem> getRelapseITList() {
		List<SelectItem> relapseITList = new LinkedList<SelectItem>();
		if(getLeaveList() !=null && !getLeaveList().isEmpty()){
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Iterator<ITransferObject> it = getLeaveList().iterator();
			ContractLeave leave;
			while(it.hasNext() && relapseITList.size() < RELAPSE_IT_LIST_SIZE){
				leave = (ContractLeave)it.next();
				String start = formatter.format(leave.getStartDate());
				String end = leave.getEndDate()!=null?formatter.format(leave.getEndDate()):"";
				String label = leave.getId().toString() + " (" + start +" - "+ end + ")";
				SelectItem item = new SelectItem(leave.getId(), label);
				relapseITList.add(item);
			}
		}
		return relapseITList;
	}
	
	public IControllerListener getContractFilter() {
		if ( this.contractFilter == null ) {
			this.contractFilter = new ControllerAdapter() {
				@Override
				public void beforeModelInitialized(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {
						String alias = controller.getFieldName(IEntityAlias.CONTRACT_END_DATE);
						Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(alias, new Date());
						Expression expr2 = ExpressionUtilities.getNullExpression(alias);
						controller.getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
						controller.getCriteria().addOrder(controller.getFieldName(IEntityAlias.CONTRACT_PERSON_FIRST_SURNAME));
						controller.getCriteria().addOrder(controller.getFieldName(IEntityAlias.CONTRACT_PERSON_SECOND_SURNAME));
						controller.getCriteria().addOrder(controller.getFieldName(IEntityAlias.CONTRACT_PERSON_REGISTRY_NAME));
					} catch (ManagerBeanException e) {
						LOGGER.error("Error filtering contracts by endDate", e);
					}
				}
			};
		}
		return this.contractFilter;
	}
	
	public void onContractChange(LookupChangeEvent event){
		calculateBases( (ContractLeave) this.getTo(), (Contract)event.getNewValue() );
	}
//	public void onEndDateChanged(ValueChangeEvent event){
//		Date endDate = (Date) event.getNewValue();
//		Date startDate = ((ContractLeave)this.getTo()).getStartDate();
//		if(endDate!=null && endDate.before(startDate)){
//			endDate = null;
//			AonUtil.addErrorMessage("La fecha de alta no puede ser anterior a la fecha de baja");
//			throw new AbortProcessingException("La fecha de alta no puede ser anterior a la fecha de baja");
//		}
//	}
//	public void onChangeEndDate(ActionEvent event){
//		Date endDate = ((ContractLeave)this.getTo()).getEndDate();
//		Date startDate = ((ContractLeave)this.getTo()).getStartDate();
//		if(endDate!=null && endDate.before(startDate)){
//			endDate = null;
//			AonUtil.addErrorMessage("La fecha de alta no puede ser anterior a la fecha de baja");
//			throw new AbortProcessingException("La fecha de alta no puede ser anterior a la fecha de baja");
//		}
//	}
	public void onStartDateChange(ActionEvent event){
		ContractLeave leave = (ContractLeave) this.getTo();
		if(leave!=null){
			calculateBases( leave, leave.getContract() );
		}
	}
	public void onReloadBases(ActionEvent event){
		ContractLeave leave = (ContractLeave) this.getTo();
		if(leave!=null){
			calculateBases( leave, leave.getContract() );
		}
	}
	
	public void calculateBases(ContractLeave leave, Contract contract) {
		// TODO obtener las bases del trabajador, 
//		las de la nomina del mes anterior dividido por 30, si el trabajador tiene salario mensual; 30, 31 ó 28, 29 si tiene salario diario)
//		el problema viene cuando no existe nomina anterior (cae de baja el primer mes)
		if(leave.getStartDate()!=null && contract!=null && contract.getId()!=null){
			ISalary salary = PayrollUtils.getInstance().getBeforeDateSalary(contract, leave.getStartDate());
			if(salary==null){
				try{
					salary = PayrollUtils.getInstance().calculateSalary(contract, leave.getStartDate());
				} catch(Exception e){
					// no se carga ninguna base
					salary = null;
				}
			}
			if(salary!=null){
				setDailyCgcBase( CommonUtil.round(salary.getCommonBase()/salary.getTimeUnits()) );
				setDailyCgpBase( CommonUtil.round(salary.getProfessionalBase()/salary.getTimeUnits()) );
				setDailyRegBase( CommonUtil.round(salary.getRawCommonBase()/salary.getTimeUnits()) );
			}
		} else {
			setDailyCgcBase( null );
			setDailyCgpBase( null );
			setDailyRegBase( null );
		}
		setEditBases(false);
	}
}
