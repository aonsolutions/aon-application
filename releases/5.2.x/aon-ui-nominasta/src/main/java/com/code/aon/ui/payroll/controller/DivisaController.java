package com.code.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;
import com.code.aon.payroll.divisa.LinDivisa;
import com.code.aon.ui.form.LinesController;

public class DivisaController extends LinesController {

LinDivisa d;
	
	@Override
	public void onAccept(ActionEvent event) {
	
	 
		
		System.out.println("111111111111111111111111111");
		
	    /*if  (((LinDivisa)this.getTo()).getId().getDivisaFinal().isEmpty())  {
		
				
		System.out.println("222222222222222222222222");	
		LinDivisa Div = (LinDivisa)event.getController().getTo();
		((LinDivisa)this.getTo()).setId();  setId().setId(setDivisaFinal((Divisa)this.getTo()).getCdg()));
		}
		DivisaMaestroController controller = (DivisaMaestroController)AonUtil.getController("divisaMaestro");
		LinDivisa Div = (LinDivisa)getTo();		
		Div.getId().setDivisaFinal(controller.getMaestro());
		System.out.println(" fffffffffffffffff               " + controller.getMaestro());
		
		System.out.println(Div.getId().getDivisaFinal());
		System.out.println(Div.getId().getCdg());
		System.out.println(Div.getId().getFecini());
		System.out.println(Div.getImporte());
		System.out.println(Div.getUnidades());
		System.out.println("111111111111111111111111111");
			
		*/
		super.onAccept(event);
	}

	public LinDivisa getD() {
		return d;
	}

	public void setD(LinDivisa d) {
		this.d = d;
	}

	
	
	
}
