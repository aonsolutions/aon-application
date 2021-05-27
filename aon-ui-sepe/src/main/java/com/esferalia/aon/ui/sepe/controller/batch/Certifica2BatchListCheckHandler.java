package com.esferalia.aon.ui.sepe.controller.batch;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.enumeration.SuspensionCause;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;


public class Certifica2BatchListCheckHandler extends BatchListCheckHandler {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public Certifica2BatchListCheckHandler(IController controller) {
		super(controller);
	}
	
	@Override
	public void setRowChecked(boolean rowChecked) {
		try {
			super.setRowChecked(rowChecked);
			Certifica2ListController controller = (Certifica2ListController) this.getController();
			Contract contract = (Contract) getController().getModel().getRowData();
			if(rowChecked){
				if( !controller.getBatchDetailList().containsKey(contract.getId()) ){
					Certifica2BatchDetail detail = new Certifica2BatchDetail();
					detail.setContract(contract);
					detail.setSuspensionCause(obtainSuspensionCause(contract));
					controller.getBatchDetailList().put(contract.getId(), detail);
				} else {
					controller.getBatchDetailList().get(contract.getId()).setSuspensionCause(obtainSuspensionCause(contract));
				}
			} else {
				if( controller.getBatchDetailList().containsKey(contract.getId()) ){
					controller.getBatchDetailList().remove(contract.getId());
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		super.checkAll(event);
		Certifica2ListController controller = (Certifica2ListController) this.getController();
		for(Object o: getCheckedList()){
			Contract contract = (Contract) o;
			if( !controller.getBatchDetailList().containsKey(contract.getId()) ){
				Certifica2BatchDetail detail = new Certifica2BatchDetail();
				detail.setContract(contract);
				detail.setSuspensionCause(obtainSuspensionCause(contract));
				controller.getBatchDetailList().put(contract.getId(), detail);
			}
		}
	}
	
	@Override
	public void clearCheckedList() {
		super.clearCheckedList();
		Certifica2ListController controller = (Certifica2ListController) AonUtil.getRegisteredBean(ISepeConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
		controller.getBatchDetailList().clear();
	}
	
	@Override
	public boolean isRowCheckeable(Object o) {
		Certifica2ListController controller = (Certifica2ListController) AonUtil.getRegisteredBean(ISepeConstants.CERTIFICA2_LIST_CONTROLLER_NAME);
		try {
			if(controller.isRowDisabled((Contract) o)){
				return false;
			}
		} catch (ManagerBeanException e) {
			
		}
		return super.isRowCheckeable(o);
	}
	
	private SuspensionCause obtainSuspensionCause(Contract contract) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(SalaryData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression("SalaryData.salary.contract.id", contract.getId());
			criteria.addEqualExpression("SalaryData.salary.type", SalaryType.SETTLE);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_DATA_NAME), "COD_CESE");
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_DATA_DOMAIN), contract.getDomain());
			criteria.setSkipDomainFilter(true);
			List<ITransferObject> list = bean.getList(criteria);
			if( !list.isEmpty() ){
				Integer suspensionCauseCode = Integer.parseInt(((SalaryData)list.get(0)).getExpression());
				for( SuspensionCause o : SuspensionCause.values() ) {
					if ( Integer.parseInt(o.getValue()) == suspensionCauseCode ) {
						return o;
					}
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se ha podido obtener la causa de suspensión de " + contract.getPerson().getFullName());
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		return null;
	}
	
}
