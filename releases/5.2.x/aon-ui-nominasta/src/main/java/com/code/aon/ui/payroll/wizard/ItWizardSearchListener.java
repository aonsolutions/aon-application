package com.code.aon.ui.payroll.wizard;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class ItWizardSearchListener extends ControllerSearchListener{

	private Empresa empresa;
	
	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setEmpresa(new Empresa());
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException,
			ExpressionException {
		
		Criteria criteria = getController().getCriteria();
		if ((getEmpresa() != null) && (getEmpresa().getCdg() != null)) {
			criteria.addEqualExpression(getController().getFieldName(IPayrollAlias.TRABAJADOR_EMPRESA_CDG), getEmpresa().getCdg());			
		}
	}	
	
	
}
