package com.code.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.contratos.ContratosTc2;
import com.code.aon.payroll.dao.IPayrollAlias;


public class ContratosTc2Controller extends PayrollBasicController {

	private ContratosTc2Controller contratosTc2Print;

	public ContratosTc2Controller getContratosTc2Print() {
		return contratosTc2Print;
	}

	public void setContratosTc2Print(ContratosTc2Controller contratosTc2Print) {
		this.contratosTc2Print = contratosTc2Print;
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		// TODO Auto-generated method stub
		super.onSelect(event);
		if(getTo()!=null){
			try {
				String id = BeanManager.getManagerBean(ContratosTc2.class).getFieldName(IPayrollAlias.CONTRATOS_TC2_CDG);
				String cdg = ((ContratosTc2)getTo()).getCdg();
				contratosTc2Print = new ContratosTc2Controller();
				contratosTc2Print = this;
				contratosTc2Print.clearCriteria();
				contratosTc2Print.getCriteria().addEqualExpression(id, cdg);
			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onSearch(ActionEvent event) {
//		try {
//			this.clearCriteria();
//		} catch (ManagerBeanException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		super.onSearch(event);
	}
	
	

}
