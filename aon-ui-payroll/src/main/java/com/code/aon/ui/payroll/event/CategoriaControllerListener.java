package com.code.aon.ui.payroll.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.convenios.Categoria;
import com.code.aon.payroll.auxiliares.convenios.Convenio;
import com.code.aon.payroll.auxiliares.convenios.Nivel;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.CategoriaLinesController;
import com.code.aon.ui.payroll.controller.ConveniosColectivoController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.util.AonUtil;

public class CategoriaControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	
	@Override
	public void beforeBeanReset(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("CategoriaControllerListener -------> beforeBeanReset");
		
		CategoriaLinesController categoria = (CategoriaLinesController)event.getController();
		//CategoriaLinesController categoria  = (CategoriaLinesController)AonUtil.getController(IPayrollConstants.CATEGORIA_CONTROLLER_NAME);
		//Categoria to = (Categoria)categoria.getTo();
		
		if(categoria.getTo()==null) {
			System.out.println("to vacio");
			/*
			try {
				BeanManager.getManagerBean(Nivel.class);
				ConveniosColectivoController convenio = (ConveniosColectivoController)AonUtil.getController(IPayrollConstants.CONVENIO_CONTROLLER_NAME);
				LinesController nivel = (LinesController)AonUtil.getController(IPayrollConstants.NIVEL_CONTROLLER_NAME);
				
				categoria.onReset(null);
				
				Convenio con = (Convenio)convenio.getTo();
				((Categoria)(categoria.getTo())).setConvenio(con);
				((Categoria)(categoria.getTo())).setNivel(((Nivel)nivel.getTo()).getId().getCdg());
				//((Categoria)(categoria.getTo())).g
			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			
		} else
			System.out.println("to con algo "+categoria);
		
		
		
		
		
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		 beforeBeanReset(event);
	}
	
	
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("CategoriaControllerListener -------> afterBeanCanceled");
	}
	
	@Override
	public void beforeBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("CategoriaControllerListener -------> beforeBeanCanceled");
	}


}
