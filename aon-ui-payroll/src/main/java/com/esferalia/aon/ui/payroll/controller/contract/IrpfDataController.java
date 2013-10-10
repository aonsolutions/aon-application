package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.payroll.IrpfDataAscendants;
import com.esferalia.aon.payroll.IrpfDataDescendients;
import com.esferalia.aon.payroll.enumeration.DeductHomeLoan;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class IrpfDataController extends LinesController {

	private static final Logger LOGGER = LoggerFactory.getLogger(IrpfDataController.class.getName());
	
	private static final char[] DNI_LETTERS = { 'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D',
		'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E' };
	
	private DisabilityLevel disabilityLevel;

	public DisabilityLevel getDisabilityLevel() {
		return disabilityLevel;
	}
	public void setDisabilityLevel(DisabilityLevel disabilityLevel) {
		this.disabilityLevel = disabilityLevel;
	}
	public DeductHomeLoan getDeductHomeLoanAfter() {
		return DeductHomeLoan.AFTER_01_01_2001;
	}
	public DeductHomeLoan getDeductHomeLoanBefore() {
		return DeductHomeLoan.BEFORE_01_01_2001;
	}
	
	public Integer getDescendientLinesCount() {
		try {
			IrpfData data = (IrpfData) getTo();
			IManagerBean bean = BeanManager.getManagerBean(IrpfDataDescendients.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.IRPF_DATA_DESCENDIENTS_IRPF_DATA_ID), data.getId());
			List<ITransferObject> list = bean.getList(criteria);
			return list.size();
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
	
	
	public void onSelectContract(ActionEvent event){
		IController controller = FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		controller.onSelect(event);
		Contract contract = (Contract) controller.getTo();
		try {
			this.getCriteria().addEqualExpression(this.getFieldName(IEntityAlias.IRPF_DATA_CONTRACT_ID), contract.getId());
			this.getCriteria().addOrder(this.getFieldName(IEntityAlias.IRPF_DATA_START_DATE), false);
			this.onSearch(event);
			if(!(this.getRowCount()>0)){
				this.onReset(event);
			} else {
				this.onSelectFirst(event);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener el modelo 145";
			LOGGER.error(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	
	//TODO para la impresion del modelo 145, revisar
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
