package com.code.aon.ui.payroll.event;




import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.RichLookupBean;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.principales.persona.Trabajador;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.payroll.controller.TrabajadorController;
import com.code.aon.ui.util.AonUtil;

public class ActividadLookUpListener  extends ControllerAdapter {

	
  @Override
	public void beforeModelInitialized(ControllerEvent event){
			
	  //FALTA FILTRAR CENTROS DE TRABAJO
		
	RichLookupBean ccosteLookup = (RichLookupBean)AonUtil.getRegisteredBean("emprccosLookup");
	try {
		Criteria c= ccosteLookup.getController().getCriteria();
		ccosteLookup.getController().clearCriteria();
		Integer codempresa= ((Trabajador)((TrabajadorController)AonUtil.getRegisteredBean("trabajador")).getTo()).getActividad().getCdg();
		
		String actividad=ccosteLookup.getController().getFieldName(IPayrollAlias.EMPRCCOS_ACTIVIDAD_CDG);
		ccosteLookup.getController().getCriteria().addEqualExpression(actividad,codempresa);
		System.out.println(ccosteLookup.getController().getCriteria());
	} catch (ManagerBeanException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
}
}



