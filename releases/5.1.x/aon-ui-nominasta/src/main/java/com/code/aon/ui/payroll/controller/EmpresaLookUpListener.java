package com.code.aon.ui.payroll.controller;




import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.RichLookupBean;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.principales.persona.Trabajador;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.payroll.controller.TrabajadorController;
import com.code.aon.ui.util.AonUtil;

public class EmpresaLookUpListener  extends ControllerAdapter {

	
  @Override
	public void beforeModelInitialized(ControllerEvent event){
			
	System.out.println("--------------------");
		
	RichLookupBean actividadLookup = (RichLookupBean)AonUtil.getRegisteredBean("actividadLookup");
	try {
		Criteria c= actividadLookup.getController().getCriteria();
		actividadLookup.getController().clearCriteria();
		Integer codempresa= ((Trabajador)((TrabajadorController)AonUtil.getRegisteredBean("trabajador")).getTo()).getEmpresa().getCdg();
		
		String empresa=actividadLookup.getController().getFieldName(IPayrollAlias.ACTIVIDAD_EMPRESA_CDG);
		actividadLookup.getController().getCriteria().addEqualExpression(empresa,codempresa);
		System.out.println(actividadLookup.getController().getCriteria());
	} catch (ManagerBeanException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
}
}



