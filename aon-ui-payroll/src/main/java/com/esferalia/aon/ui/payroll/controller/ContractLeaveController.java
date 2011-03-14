package com.esferalia.aon.ui.payroll.controller;


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
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractLeave;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;
import com.esferalia.aon.ui.payroll.utils.NumberValidation;

public class ContractLeaveController extends BasicController {

	private final int AVAILABLE_IT_LIST_SIZE = 10;
	private final int RELAPSE_IT_LIST_SIZE = 10;
	
	private Contract contract;
	private List<ITransferObject> leaveList;
	private List<ITransferObject> leaveDetailList;
	private DataModel leaveModel;
	private DataModel leaveDetailModel;
	private LeaveReportType leaveReportType;
	private ContractLeaveDetail report;
	private Boolean validCollegeNumber;
	private Boolean validCias;
	private Integer selectedLeaveIndex;
	
	
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
		return leaveList;
	}
	public void setLeaveList(List<ITransferObject> leaveList) {
		this.leaveList = leaveList;
	}
	public DataModel getLeaveModel() {
		return leaveModel;
	}
	public void setLeaveModel(DataModel leaveModel) {
		this.leaveModel = leaveModel;
	}
	public DataModel getLeaveDetailModel() {
		return leaveDetailModel;
	}
	public void setLeaveDetailModel(DataModel leaveDetailModel) {
		this.leaveDetailModel = leaveDetailModel;
	}
	public List<ITransferObject> getLeaveDetailList() {
		return leaveDetailList;
	}
	public void setLeaveDetailList(List<ITransferObject> leaveDetailList) {
		this.leaveDetailList = leaveDetailList;
	}
	public LeaveReportType getLeaveReportType() {
		return leaveReportType;
	}
	public void setLeaveReportType(LeaveReportType leaveReportType) {
		this.leaveReportType = leaveReportType;
	}
	public Boolean getValidCollegeNumber() {
		return validCollegeNumber;
	}
	public void setValidCollegeNumber(Boolean validCollegeNumber) {
		this.validCollegeNumber = validCollegeNumber;
	}
	public Boolean getValidCias() {
		return validCias;
	}
	public void setValidCias(Boolean validCias) {
		this.validCias = validCias;
	}
	public ContractLeaveDetail getReport() {
		return report;
	}
	public void setReport(ContractLeaveDetail report) {
		this.report = report;
	}
	
	public ContractLeaveDetail getLastLeave(){
		if(!getLeaveDetailList().isEmpty()){
			return (ContractLeaveDetail) getLeaveDetailList().get(0);
		} else {
			return null;
		}
	}

	public boolean isShowDetail() {
		if(getSelectedLeaveIndex().equals(-1)){
			return false;
		}
		return getSelectedLeaveIndex().intValue()==getLeaveModel().getRowIndex();
	}
	
	public boolean isLeaveSelected(){
		if(getReport()==null){
			return false;
		}
		return getReport().getType()==LeaveReportType.LEAVE;
	}
	
	public boolean isDischargeSelected(){
		if(getReport()==null){
			return false;
		}
		return getReport().getType()==LeaveReportType.DISCHARGE;
	}
	
	public boolean isConfirmSelected(){
		if(getReport()==null){
			return false;
		}
		return getReport().getType()==LeaveReportType.CONFIRM;
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
	
	private void buildLeaveList() throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(ContractLeave.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_LEAVE_CONTRACT_ID), getContract().getId());
		criteria.addOrder(bean.getFieldName(IPayrollAlias.CONTRACT_LEAVE_START_DATE), false);
		setLeaveList(bean.getList(criteria));
		setLeaveModel(new ListDataModel(getLeaveList()));
	}
	
	private void buildLeaveDetailList() throws ManagerBeanException{
		if(getSelectedLeaveIndex()==null){
			setSelectedLeaveIndex(0);
		}
		if(!getLeaveList().isEmpty()){
			IManagerBean bean = BeanManager.getManagerBean(ContractLeaveDetail.class);
			Criteria criteria = new Criteria();
			ContractLeave leave = null;
			leave = (ContractLeave) getLeaveList().get(getSelectedLeaveIndex());
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE_ID), leave.getId());
			criteria.addOrder(bean.getFieldName(IPayrollAlias.CONTRACT_LEAVE_DETAIL_DATE), false);
			setLeaveDetailList(bean.getList(criteria));
			setLeaveDetailModel(new ListDataModel(getLeaveDetailList()));
		}
	}

	public void buildLeaveReport(boolean newReport) {
		if(newReport){
			if(getLeaveList().isEmpty()){
				setReport(new ContractLeaveDetail());
			} else {
				ContractLeaveDetail detail = new ContractLeaveDetail();
				ContractLeaveDetail lastLeave = getLastLeave();
				if(lastLeave!=null && lastLeave.getType()!=LeaveReportType.DISCHARGE){
					detail.setType(LeaveReportType.CONFIRM);
					detail.setContractLeave(lastLeave.getContractLeave());
					detail.setCias(lastLeave.getCias());
					detail.setCollegeNumber(lastLeave.getCollegeNumber());
					detail.setProcessed(lastLeave.isProcessed());
					detail.setDate(getConfirmSuggestedDate(detail.getContractLeave().getStartDate()));
				} else {
					detail.setType(LeaveReportType.LEAVE);
					detail.setContractLeave(new ContractLeave());
					detail.getContractLeave().setParent(new ContractLeave());
					detail.setCias(null);
					detail.setCollegeNumber(null);
					detail.setProcessed(false);
					detail.setDate(null);
				}
				setReport(detail);
			}
		} else {
			setReport((ContractLeaveDetail) this.getTo());
		}
		checkCiasNumber();
		checkCollegeNumber();
	}
	
	public void initialize() {
		this.onReset(null);
		setContract((Contract) ((ContractController)AonUtil.getRegisteredBean("contract")).getTo());
		try {
			buildLeaveList();
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar los partes. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		try {
			buildLeaveDetailList();
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar los partes. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		buildLeaveReport(true);
	}
	
	public void calculateBases(){
		// TODO obtener las bases del trabajador, las del mes anterior?
		getReport().getContractLeave().setDailyCgcBase(null);
		getReport().getContractLeave().setDailyCgpBase(null);
		getReport().getContractLeave().setDailyRegBase(null);
	}
	
	
	/*
	 * ACTION LISTENER
	 */
	public void onSelectContract(ActionEvent event) {
		ContractController controller = ((ContractController)AonUtil.getRegisteredBean("contract"));
		if(controller.getTo()==null){
			controller.onSelect(event);
		}
//		this.onReset(event);
		initialize();
		buildLeaveReport(true);
		checkCollegeNumber();
		checkCiasNumber();
	}
	
	public void onSelectLeave(ActionEvent event) {
		if(getSelectedLeaveIndex().intValue()==getLeaveModel().getRowIndex()){
			setSelectedLeaveIndex(-1);
			setLeaveDetailList(null);
			setLeaveDetailModel(null);
		} else {
			setSelectedLeaveIndex(getLeaveModel().getRowIndex());
			try {
				buildLeaveDetailList();
			} catch (ManagerBeanException e) {
				String msg = "Imposible cargar los partes. [" + e.getMessage() + "]";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg, e);
			}
		}
	}
	
	public void onSelectDetail(ActionEvent event) {
		this.onCancel(event);
		ContractLeaveDetail detail = (ContractLeaveDetail) getLeaveDetailModel().getRowData();
		try {
			this.select(event, detail);
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar el parte. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		buildLeaveReport(false);
	}
	
	
	private Date getConfirmSuggestedDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, 3 + ((getConfirmReportNumber()  * 7)));
		return cal.getTime();
	}
	
	private int getConfirmReportNumber() {
		return getLeaveDetailList().size()-1;
	}
	
	public void onChangeType(ActionEvent event) {
		
	}
	
	public void onChangeCollegeNumber(ActionEvent event) {
		checkCollegeNumber();
	}
	
	public void onChangeCias(ActionEvent event) {
		checkCiasNumber();
	}
	
	private void checkCollegeNumber(){
		// El nº colegiado debe cumplir una mascara
		// El nº colegiado corresponder con su digito de control
		if (!StringUtils.isBlank(getReport().getCollegeNumber())) {
			if (NumberValidation
					.validCollegeNumberPattern(getReport().getCollegeNumber())
					&& NumberValidation
					.validCollegeNumberControlDigit(getReport().getCollegeNumber())) {
				setValidCollegeNumber(true);
			} else {
				setValidCollegeNumber(false);
			}
		} else {
			setValidCollegeNumber(null);
		}
	}
	
	private void checkCiasNumber(){
		// El CIAS debe cumplir una mascara
		// El CIAS debe corresponder con su digito de control
		if (!StringUtils.isBlank(getReport().getCias())) {
			if (NumberValidation.validCiasPattern(getReport().getCias())
					&& NumberValidation.validCiasControlDigit(getReport()
							.getCias())) {
				setValidCias(true);
			} else {
				setValidCias(false);
			}
		} else {
			setValidCias(null);
		}
	}

}
