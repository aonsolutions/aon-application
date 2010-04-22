package com.code.aon.ui.payroll.event;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.cotizacion.Cnae2009Maestro;
import com.code.aon.payroll.cotizacion.Ocupacion;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.PayrollBasicController;


public class Cnae2009MaestroControllerListener extends ControllerAdapter implements IPayrollConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println("afterBeanCreated");
		super.afterBeanCreated(event);
	}

	@Override
	public void afterBeanReset(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println("afterBeanReset");
		super.afterBeanReset(event);
	}

	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println("afterModelInitialized");
		super.afterModelInitialized(event);
	}
	
	@Override
	public void beforeEditSearch(ControllerEvent event)
			throws ControllerListenerException {
		
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		PayrollBasicController cnae2009Maestro = (PayrollBasicController) event.getController();
		Cnae2009Maestro cm = (Cnae2009Maestro)event.getController().getTo();
		String ocupaciones = cm.getOcupacion();
		
		try {
			Criteria criteria = new Criteria();
			
			IManagerBean ocupacionBean = BeanManager.getManagerBean( Ocupacion.class );
			IController ocupacion = FormUtil.getController(OCUPACION_CONTROLLER_NAME);
			
			if(ocupaciones!=null){
				IManagerBean bean = BeanManager.getManagerBean( Ocupacion.class );
				List<Ocupacion> o = new ArrayList<Ocupacion>();
				for (int i = 0; i < ocupaciones.length(); i++) {
					criteria.addOrExpression(ocupacionBean.getFieldName(IPayrollAlias.OCUPACION_OCUPACION_MAESTRO_CDG), ocupaciones.substring(i, i + 1));
					 List l = bean.getList( criteria );
					 if ( l != null ) {
						 o.add( (Ocupacion)l.iterator().next() );
					 }
				}
				
				criteria.addEqualExpression(ocupacionBean.getFieldName(IPayrollAlias.OCUPACION_OCUPACION_MAESTRO_EXCLUSIVO), "S");
			} else {
				criteria.addEqualExpression(ocupacionBean.getFieldName(IPayrollAlias.OCUPACION_ID_CDG), "");
			}
			
			ocupacion.setCriteria(criteria);
			ocupacion.onSearch(null);
			
		} catch (ManagerBeanException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}