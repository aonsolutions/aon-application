package com.esferalia.aon.ui.payroll.event.agreement;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.payroll.AgreementExtra;
import com.esferalia.aon.payroll.AgreementPayment;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.controller.agreement.AgreementPaymentController;

public class AgreementPaymentControllerListener extends ControllerAdapter{
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		AgreementPaymentController controller = (AgreementPaymentController) event.getController();
		AgreementPayment ap = (AgreementPayment) controller.getTo();
		try {
			if(ap.getSalaryType()!=SalaryType.EXTRA ){
				removeAgreementExtra(ap);
				controller.setAgreementExtra(null);
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible borrar los datos de paga extra.";
			throw new ControllerListenerException(msg,e);
		}
	}
	
	private void removeAgreementExtra(AgreementPayment ap) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(AgreementExtra.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IEntityAlias.AGREEMENT_EXTRA_AGREEMENT_PAYMENT_ID), ap.getId());
		List<ITransferObject> list = bean.getList(c);
		for (ITransferObject to : list) {
			bean.remove(to);
		}
	}
	
	
}
