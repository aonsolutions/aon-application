package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.payroll.IrpfDataAscendants;
import com.esferalia.aon.payroll.IrpfDataDescendients;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.DeductHomeLoan;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;
import com.esferalia.aon.ui.payroll.utils.ContractUtils;

public class IrpfDataController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(IrpfDataController.class.getName());
	
	private static final char[] DNI_LETTERS = { 'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D',
		'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E' };
	
	private DisabilityLevel disabilityLevel;
	private boolean irpfCustomPercent;
	private Double irpfPercent;
		
	public DisabilityLevel getDisabilityLevel() {
		return disabilityLevel;
	}
	public void setDisabilityLevel(DisabilityLevel disabilityLevel) {
		this.disabilityLevel = disabilityLevel;
	}
	public boolean getIrpfCustomPercent() {
		return irpfCustomPercent;
	}
	public void setIrpfCustomPercent(boolean irpfCustomPercent) {
		this.irpfCustomPercent = irpfCustomPercent;
	}
	public Double getIrpfPercent() {
		return irpfPercent;
	}
	public void setIrpfPercent(Double irpfPercent) {
		this.irpfPercent = irpfPercent;
	}
	public boolean isDeductHomeLoan() {
		IrpfData irpfData = (IrpfData) this.getTo();
		return irpfData!=null && irpfData.getDeductHomeLoan()==DeductHomeLoan.BEFORE_01_01_2001;
	}
	public void setDeductHomeLoan(boolean deductHomeLoan) {
		IrpfData irpfData = (IrpfData) this.getTo();
		if(deductHomeLoan && irpfData!=null){
			irpfData.setDeductHomeLoan(DeductHomeLoan.BEFORE_01_01_2001);
		} else {
			irpfData.setDeductHomeLoan(null);
		}
	}
	public Integer getCurrentYear(){
		return CommonUtil.getYear(new Date());
	}
	
	public String getRowContractCode(){
		try {
			IrpfData irpfData = (IrpfData) getModel().getRowData();
			String code = ContractUtils.getInstance().getDataCurrentValue(irpfData.getContract(), ContextVariable.TC2.getName());
			ContractCode contractCode = ContractCode.getContractCodeByValue(code);
			return contractCode!=null?code + " - " + contractCode.getName(FacesContext.getCurrentInstance().getViewRoot().getLocale()):"";
		} catch (ManagerBeanException e) {
			LOGGER.error("no se ha podido recuperar el codigo del contrato");
		}
		return null;
	}
	
	public Integer getDescendientLinesCount() {
		try {
			IrpfData data = (IrpfData) getTo();
			IManagerBean bean = BeanManager.getManagerBean(IrpfDataDescendients.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.IRPF_DATA_DESCENDIENTS_IRPF_DATA_ID), data.getId());
			return bean.getCount(criteria);
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los descendientes";
			LOGGER.error(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public boolean isValidNIF() {
		IrpfData m = (IrpfData) getTo();
		if (m.getSpouseDocument() == null || m.getSpouseDocument().length() == 0) {
			return false;
		}
		char[] doc = m.getSpouseDocument().toCharArray();
		if (doc == null || doc.length == 0) {
			return false;
		}
		doc[0] = (doc[0] == 'K' || doc[0] == 'L' || doc[0] == 'M') ? '0' : doc[0];
		String numbers = new String(doc, 0, 8);
		if (!StringUtils.isNumeric(numbers)) {
			return false;
		}
		return (doc[8] == DNI_LETTERS[(Integer.parseInt(numbers) % 23)]);
	}
	
	public void init() {
		disabilityLevel = null;
		irpfCustomPercent = false;
		irpfPercent = null;	
	}
	
	public void onContractChange(LookupChangeEvent event){
		Contract contract = (Contract) event.getNewValue();
		IrpfData irpfData = (IrpfData) getTo();
		if(contract==null || contract.getId()==null){
			irpfData.setStartDate(null);
		} else {
			irpfData.setStartDate(contract.getStartDate());
		}
	}
	
	public void onChangeFiscalExclusion(ActionEvent event){
		IrpfData irpfData = (IrpfData) getTo();
		if(irpfData.isFiscalExclusion()){
			setIrpfCustomPercent(false);
			setIrpfPercent(0.0);
		}
	}
	
	// para la impresion del modelo 145
	public List<ITransferObject> getDescentants(){
		BasicController controller = (BasicController) FormUtil.getController("irpfDataDescendients");
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		list.addAll(controller.getWrappedList());
		list.add(new IrpfDataDescendients());
		list.add(new IrpfDataDescendients());
		list.add(new IrpfDataDescendients());
		list.add(new IrpfDataDescendients());
		return list;
	}
	public List<ITransferObject> getAscendants(){
		BasicController controller = (BasicController) FormUtil.getController("irpfDataAscendants");
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		list.addAll(controller.getWrappedList());
		list.add(new IrpfDataAscendants());
		list.add(new IrpfDataAscendants());
		return list;
	}
	
}
