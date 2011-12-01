package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractEmbargo;
import com.esferalia.aon.payroll.SalaryEmbargo;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class ContractEmbargoController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContractEmbargoController.class.getName());

	private boolean modalPanelVisible;
	private boolean searchCurrent;
	private Date inactiveDate;
	
	public Date getInactiveDate() {
		return inactiveDate;
	}
	public void setInactiveDate(Date inactiveDate) {
		this.inactiveDate = inactiveDate;
	}
	public boolean isSearchCurrent() {
		return searchCurrent;
	}
	public void setSearchCurrent(boolean searchCurrent) {
		this.searchCurrent = searchCurrent;
	}
	public boolean isModalPanelVisible() {
		return modalPanelVisible;
	}
	public void setModalPanelVisible(boolean modalPanelVisible) {
		this.modalPanelVisible = modalPanelVisible;
	}

	public void onEdit(ActionEvent event) {
		super.onSelect(event);
		reset(true);
	}

	public void onReset(ActionEvent event) {
		super.onReset(event);
		reset(true);
	}
	
	public void onSave(ActionEvent event) {
		IController master = FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		Contract contract = (Contract) master.getTo();
		ContractEmbargo ce  = (ContractEmbargo) getTo();
		ce.setContract(contract);
		super.onAccept(event);
		reset(false);
	}

	@Override
	public void onCancel(ActionEvent event) {
		super.onCancel(event);
		reset(false);
	}
	
	public void reset(boolean panelVisible) {
		setModalPanelVisible(panelVisible);
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		completeCiteria();
		super.onSearch(event);
	}
	
	protected void completeCiteria() {
		try {
			if(isSearchCurrent()){
				Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IPayrollAlias.CONTRACT_EMBARGO_END_DATE), new Date());
				Expression expr2 = ExpressionUtilities.getNullExpression(getFieldName(IPayrollAlias.CONTRACT_EMBARGO_END_DATE));
				getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
			} else {
				if(getInactiveDate()!=null){
					Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IPayrollAlias.CONTRACT_EMBARGO_END_DATE), getInactiveDate());
					Expression expr2 = ExpressionUtilities.getNullExpression(getFieldName(IPayrollAlias.CONTRACT_EMBARGO_END_DATE));
					getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible inicializar los embargos";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public double getEmbargedAmount(){
		Double amount = new Double(0);
		try {
			ContractEmbargo embargo = (ContractEmbargo) this.getTo();
			if(embargo==null){
				embargo = (ContractEmbargo) this.getModel().getRowData();
			}
			IManagerBean bean = BeanManager.getManagerBean(SalaryEmbargo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_EMBARGO_CONTRACT_EMBARGO_ID), embargo.getId());
			List<ITransferObject> list = bean.getList(criteria);
			for(ITransferObject to: list){
				SalaryEmbargo se = (SalaryEmbargo) to;
				amount += se.getAmount();
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible calcular el importe descontado del embargo (" + e.getMessage() +")";
			LOGGER.error(msg);
		}
		
		return amount;
	}
	
	public double getPendingAmount(){
		ContractEmbargo embargo = null;
		try {
			embargo = (ContractEmbargo) this.getTo();
			if(embargo==null){
				embargo = (ContractEmbargo) this.getModel().getRowData();
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible calcular el importe pendiente del embargo (" + e.getMessage() +")";
			LOGGER.error(msg);
		}
		return embargo.getAmount()-getEmbargedAmount();
	}
	
	public Date getEstimatedEndDate(){
		try {
			ContractEmbargo embargo = (ContractEmbargo) this.getTo();
			if(embargo==null && this.getModel().isRowAvailable()){
				embargo = (ContractEmbargo) this.getModel().getRowData();
				IManagerBean bean = BeanManager.getManagerBean(SalaryEmbargo.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_EMBARGO_CONTRACT_EMBARGO_ID), embargo.getId());
				List<ITransferObject> list = bean.getList(criteria);
				Double media = getEmbargedAmount()/list.size();
				Double month = getPendingAmount()/media;
				Calendar cal = Calendar.getInstance();
				cal.setTime(embargo.getStartDate());
				cal.add(Calendar.MONTH, cal.get(Calendar.MONTH)+(int) (Math.floor(month)));
				return cal.getTime();
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible calcular la fecha final estimada del embargo (" + e.getMessage() +")";
			LOGGER.error(msg);
		}
		return null;
	}
}
