package com.esferalia.aon.ui.payroll.controller.contract;


import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

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
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractLeave;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.enumeration.ContractLeaveStatus;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.utils.NumberValidation;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class ContractLeaveController extends BasicController {

	private final int AVAILABLE_IT_LIST_SIZE = 10;
	private final int RELAPSE_IT_LIST_SIZE = 10;
	
	private Contract contract;
	private List<ITransferObject> leaveList;
	private DataModel leaveModel;
	private Integer selectedLeaveIndex;

	public PayrollUtils getUtils() {
		return PayrollUtils.getInstance();
	}
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	public Integer getSelectedLeaveIndex() {
		return selectedLeaveIndex;
	}
	public void setSelectedLeaveIndex(Integer selectedLeaveIndex) {
		this.selectedLeaveIndex = selectedLeaveIndex;
	}
	public List<ITransferObject> getLeaveList() {
		if( leaveList == null ){
			buildLeaveList();
		}
		return leaveList;
	}
	public void setLeaveList(List<ITransferObject> leaveList) {
		this.leaveList = leaveList;
	}
	public DataModel getLeaveModel() {
		if ( leaveModel == null ){
			leaveModel = new ListDataModel(getLeaveList());
		}
		return leaveModel;
	}
	public void setLeaveModel(DataModel leaveModel) {
		this.leaveModel = leaveModel;
	}
	public Boolean getValidCollegeNumber() {
		return checkCollegeNumber((ContractLeaveDetail) getTo());
	}
	public Boolean getValidCias() {
		return checkCiasNumber((ContractLeaveDetail) getTo());
	}
	
	public ContractLeaveDetail getLastLeave(){
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractLeaveDetail.class);
			Criteria criteria = new Criteria();
			ContractLeave leave = null;
			if( getLeaveList()!=null && !getLeaveList().isEmpty() ){
				leave = (ContractLeave) getLeaveList().get(0);
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE_ID), leave.getId());
				criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_DATE), false);
				List<ITransferObject> list = bean.getList(criteria);
				if( !list.isEmpty() ){
					return (ContractLeaveDetail) list.get(0);
				}
			}
		} catch (ManagerBeanException e) {
			// NADA, no se sugiere ninguna informacion
		}
		return null;
	}

	public boolean isShowDetail() {
		if(getSelectedLeaveIndex()==null || getSelectedLeaveIndex().equals(-1)){
			return false;
		}
		return getSelectedLeaveIndex().intValue()==getLeaveModel().getRowIndex();
	}
	
	public List<SelectItem> getAvailableITList() {
		List<SelectItem> availableITList = new LinkedList<SelectItem>();
		if(getLeaveList() !=null && !getLeaveList().isEmpty()){
			Iterator<ITransferObject> it = getLeaveList().iterator();
			ContractLeave leave;
			while(it.hasNext() && availableITList.size() < AVAILABLE_IT_LIST_SIZE){
				leave = (ContractLeave)it.next();
				String label = leave.getId().toString();
				SelectItem item = new SelectItem(leave.getId(), label);
				availableITList.add(item);
			}
		}
		return availableITList;
	}
	
	public List<SelectItem> getRelapseITList() {
		List<SelectItem> relapseITList = new LinkedList<SelectItem>();
		if(getLeaveList() !=null && !getLeaveList().isEmpty()){
			Iterator<ITransferObject> it = getLeaveList().iterator();
			ContractLeave leave;
			while(it.hasNext() && relapseITList.size() < RELAPSE_IT_LIST_SIZE){
				leave = (ContractLeave)it.next();
				String label = leave.getId().toString();
				SelectItem item = new SelectItem(leave.getId(), label);
				relapseITList.add(item);
			}
		}
		return relapseITList;
	}
	
	private void buildLeaveList() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractLeave.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_CONTRACT_ID), getContract().getId());
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_START_DATE), false);
			setLeaveList(bean.getList(criteria));
			setLeaveModel(null);
			setSelectedLeaveIndex(0);
			buildLeaveDetailList();
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar los partes. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	private void buildLeaveDetailList() {
		try {
			if(!getLeaveList().isEmpty() && getSelectedLeaveIndex()>-1){
				ContractLeave leave = (ContractLeave) getLeaveList().get(getSelectedLeaveIndex());
				this.clearCriteria();
				this.getCriteria().addEqualExpression(this.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE_ID), leave.getId());
				this.getCriteria().addOrder(this.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_DATE), false);
				this.onSearch(null);
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar los partes. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void initialize() {
		setLeaveList(null);
		setLeaveModel(null);
	}
	
	public void createLeaveReportSuggest(){
		ContractLeaveDetail lastLeave = getLastLeave();
		ContractLeaveDetail detail = (ContractLeaveDetail) this.getTo();
		if( detail!=null ){
			if(lastLeave!=null && lastLeave.getType()!=LeaveReportType.DISCHARGE){
				createNewConfirmReport(detail, lastLeave);
			} else {
				createNewLeaveReport(detail, lastLeave);
			}
		}
	}
	
	private void createNewConfirmReport(ContractLeaveDetail detail, ContractLeaveDetail lastLeave){
		detail.setType(LeaveReportType.CONFIRM);
		detail.setStatus(ContractLeaveStatus.PENDING);
		if(lastLeave!=null && lastLeave.getId()!=null){
			detail.setConfirmOrder(lastLeave.getConfirmOrder()==null?1:lastLeave.getConfirmOrder()+1);
			detail.setContractLeave(lastLeave.getContractLeave());
			detail.getContractLeave().setParent(detail.getContractLeave().getParent()==null?new ContractLeave():detail.getContractLeave().getParent());
			detail.setCias(lastLeave.getCias());
			detail.setCollegeNumber(lastLeave.getCollegeNumber());
			detail.setDate(getConfirmSuggestedDate(detail.getContractLeave().getStartDate(),lastLeave.getConfirmOrder()));
		} else {
			detail.getContractLeave().setParent(new ContractLeave());
			detail.setDate(new Date());
		}
	}
	
	private void createNewLeaveReport(ContractLeaveDetail detail, ContractLeaveDetail lastLeave){
		detail.setType(LeaveReportType.LEAVE);
		detail.setContractLeave(new ContractLeave());
		detail.getContractLeave().setParent(new ContractLeave());
		if(lastLeave!=null && lastLeave.getId()!=null){
			detail.setCias(lastLeave!=null?lastLeave.getCias():null);
			detail.setCollegeNumber(lastLeave!=null?lastLeave.getCollegeNumber():null);
		}
		detail.setStatus(ContractLeaveStatus.PENDING);
		detail.setDate(new Date());
		calculateBases(detail.getContractLeave());
	}
	
	public void calculateBases(ContractLeave leave) {
		// TODO obtener las bases del trabajador, 
//		las de la nomina del mes anterior dividido por 30
//		el problema viene cuando no existe nomina anterior (cae de baja el primer mes)
		ISalary salary = getUtils().getBeforeDateSalary(getContract(), leave.getStartDate());
		if(salary==null){
			salary = getUtils().calculateSalary(getContract(), leave.getStartDate()!=null?leave.getStartDate():new Date());
		}
		if(salary!=null){
			leave.setDailyCgcBase( CommonUtil.round(salary.getCommonBase()/salary.getTimeUnits()) );
			leave.setDailyCgpBase( CommonUtil.round(salary.getProfessionalBase()/salary.getTimeUnits()) );
			leave.setDailyRegBase( CommonUtil.round(salary.getRawCommonBase()/salary.getTimeUnits()) );
		}
	}
	
	/*
	 * ACTION LISTENER
	 */
	
	public void onInit(ActionEvent event) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			setContract((Contract) bean.createNewTo());
		} catch (ManagerBeanException e) {
			String msg = "No se puede inicializar el lookup.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		initialize();
	}
	
	public void onContractChange(LookupChangeEvent event) {
		if(event.getNewValue()!=null){
			setContract((Contract) event.getNewValue());
			onReset(null);
			initialize();
			createLeaveReportSuggest();
		} else {
			try {
				IManagerBean bean = BeanManager.getManagerBean(Contract.class);
				setContract((Contract) bean.createNewTo());
			} catch (ManagerBeanException e) {
				String msg = "No se puede inicializar el lookup.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}
		initialize();
	}
	
	public void onLeaveDateChange(ActionEvent event) {
		ContractLeaveDetail detail = (ContractLeaveDetail) getTo();
		if(existLeave(getContract(), detail) && detail.getType()==LeaveReportType.LEAVE){
			String msg = "Ya existe una incidencia de IT en la fecha indicada";
			AonUtil.addErrorMessage(msg);
			detail.setDate(new Date());
		}
	}
	
	public void onSearchContract(ActionEvent event) {
		// TODO este metodo sobra si el mnto se mantiene sin search ni list
		ContractController controller = ((ContractController)AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_CONTROLLER));
		try {
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(controller.getFieldName(IEntityAlias.CONTRACT_END_DATE), new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(controller.getFieldName(IEntityAlias.CONTRACT_END_DATE));
			controller.getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		} catch (ManagerBeanException e) {
			String msg = "Error al buscar contratos.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		controller.onSearch(event);

	}
	
	public void onSelectContract(ActionEvent event) {
		ContractController controller = ((ContractController)AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_CONTROLLER));
		controller.onSelect(event);
		setContract((Contract) controller.getTo());
		initialize();
		onSelectLeave(null);
	}

	public void onSelectCurrentContract(ActionEvent event) {
		ContractController controller = ((ContractController)AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_CONTROLLER));
		setContract((Contract) controller.getTo());
		initialize();
	}
	
	public void onSelectLeave(ActionEvent event) {
		if(getSelectedLeaveIndex().intValue()==getLeaveModel().getRowIndex()){
			setSelectedLeaveIndex(-1);
		} else {
			setSelectedLeaveIndex(getLeaveModel().getRowIndex());
			buildLeaveDetailList();
		}
	}
	
	public Date getConfirmSuggestedDate(Date startDate, Integer confirmReportNumber) {
		if(startDate==null){
			return new Date();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DAY_OF_YEAR, 3 + (((confirmReportNumber==null?0:confirmReportNumber)  * 7)));
		return cal.getTime();
	}
	
	public void onChangeConfirmOrder(ActionEvent event) {
		ContractLeaveDetail detail = (ContractLeaveDetail) this.getTo();
		detail.setDate(getConfirmSuggestedDate(detail.getContractLeave().getStartDate(), detail.getConfirmOrder()-1));
		
	}
	
	public void onChangeType(ActionEvent event) {
		ContractLeaveDetail lastLeave = getLastLeave();
		ContractLeaveDetail detail = (ContractLeaveDetail) this.getTo();
		if( detail!=null ){
			if(detail.getType()==LeaveReportType.CONFIRM){
				createNewConfirmReport(detail, lastLeave);
			} else if(detail.getType()==LeaveReportType.LEAVE){
				createNewLeaveReport(detail, lastLeave);
			} 
		}
	}
	
	public void onChangeCollegeNumber(ActionEvent event) {
		checkCollegeNumber((ContractLeaveDetail) this.getTo());
	}
	
	public void onChangeCias(ActionEvent event) {
		checkCiasNumber((ContractLeaveDetail) this.getTo());
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
	
	public boolean existLeave(Contract contract, ContractLeaveDetail leaveDetail) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractLeave.class);
			Criteria criteria = new Criteria();
			criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_ID), leaveDetail.getContractLeave().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_CONTRACT_ID), contract.getId());
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_START_DATE), leaveDetail.getDate());
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_END_DATE), leaveDetail.getDate());
			Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_END_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			return bean.getCount(criteria)>0;
		} catch (ManagerBeanException e) {
			String msg = "Error al comprobar IT existente.";
			AonUtil.addErrorMessage(msg);
		}
		return false;
	}
	
	@Override
	public void accept(ActionEvent event) {
		ContractLeaveDetail detail = (ContractLeaveDetail) this.getTo();
		try {
			if(detail.getContractLeave()!=null && detail.getContractLeave().getId()==null){
				if(detail.getType()==LeaveReportType.LEAVE){
					detail.getContractLeave().setContract(getContract());
					detail.getContractLeave().setStartDate(detail.getDate());
					IManagerBean bean = BeanManager.getManagerBean(ContractLeave.class);
					bean.restoreNullSubPOJOs(detail.getContractLeave());
					detail.setContractLeave((ContractLeave) bean.insert(detail.getContractLeave()));
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al buscar los partes.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		super.accept(event);
	}
	
}
