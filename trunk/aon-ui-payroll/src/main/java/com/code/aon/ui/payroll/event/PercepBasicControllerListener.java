package com.code.aon.ui.payroll.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.payroll.auxiliares.convenios.Complemento;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.principales.personas.Percep;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.PercepBasicController;
import com.code.aon.ui.payroll.controller.Utils;

public class PercepBasicControllerListener extends ControllerAdapter implements
		IPayrollConstants {

	@Override
	public void beforeBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		PercepBasicController controller = (PercepBasicController) event.getController();
		controller.refreshComplementos();
	}

	@Override
	public void beforeBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		PercepBasicController controller = (PercepBasicController) event.getController();
		controller.refreshComplementos();
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {

		//((PercepBasicController) getController()).generarNumero(null);

	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		
		int numero = ((Percep)this.getController().getTo()).getTrabajador().getCdg();
		int cdg = Integer.parseInt(Utils.maxCode("Percep", "id.cdg", "id.numero="+numero));
		
		((Percep)this.getController().getTo()).getId().setCdg(cdg+1);
		((Percep)this.getController().getTo()).getId().setNumero(numero);
		
//		PercepBasicController percepBasic = (PercepBasicController)this.getController();
//		
//		if (percepBasic.getComplemento1()==null || StringUtils.isEmpty(percepBasic.getComplemento1().getCdg()) || percepBasic.getComplemento1().getCdg() == null) {
//			percepBasic.setComplemento1(new Complemento());
//		}
		
	}
}
