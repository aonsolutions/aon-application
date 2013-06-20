package com.esferalia.aon.ui.payroll.controller.launcher;

import java.sql.SQLException;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.aeat.jaxb.TipoError;
import com.aeat.jaxb.TipoRetenedorError2011;
import com.aeat.jaxb.TipoRetenidoError2011;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.IrpfDraftController;

public class IrpfTestLauncher extends AbstractIrpfLauncher {
	
	
	@Override
	protected void execute(IrpfLauncherParams params) throws SalaryException,
			ExpressionException, SQLException {
	}
	
	
	@Override
	public void onWarn(TipoRetenedorError2011 retenedorError2011,
			TipoRetenidoError2011 retenidoError2011) {
		for (TipoError error : retenidoError2011.getError()) {
			WarnMessage warnMessage = 
				new WarnMessage(error.getDescripcion().substring(0, 80));
			warnMessage.setEmployeeName(retenidoError2011.getNif());
			warnMessage.setEnterpriseName(retenedorError2011.getNif());
			onMessage(warnMessage);
		}
		
	}

	
	private Integer contractId;
	
	public Integer getContractId() {
		return contractId;
	}
	
	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}
	
	public void onIrpfDraft(ActionEvent event) {
		try {
			IrpfDraftController controller = 
				(IrpfDraftController) FormUtil.getController(IPayrollConstants.IRPF_DRAFT_CONTROLLER_NAME);
			controller.onEditSearch(event);
			Date date = getParams().getDate();
			controller.setDate(date);

			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					controller.getManagerBean().
					getFieldName(IEntityAlias.CONTRACT_ID), 
					getContractId());
			controller.clearCriteria();
			controller.setCriteria(criteria);
			controller.onSearch(event);
			controller.getModel().setRowIndex(0);
			controller.onSelect(event);
			controller.setBackAction(
					IPayrollConstants.IRPF_TESTER_LAUNCHER_FORM);
		} catch (ManagerBeanException e) {
			String msg = "Error en el borrador del I.R.P.F.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	
}
