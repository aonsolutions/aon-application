package com.code.aon.ui.payroll.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.cotizacion.CnaeMaestro;
import com.code.aon.payroll.cotizacion.Ocupacion;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;


public class CnaeMaestroControllerListener extends ControllerAdapter {

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
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		
		CnaeMaestro cm = (CnaeMaestro)event.getController().getTo();
		String ocupaciones = cm.getOcupacion();
		
		try {
			Criteria criteria = new Criteria();
			
			IManagerBean ocupacionBean = BeanManager.getManagerBean( Ocupacion.class );
			IController ocupacion = AonUtil.getController("ocupacion");
			
			if(ocupaciones!=null){
				for (int i = 0; i < ocupaciones.length(); i++) {
					criteria.addOrExpression(ocupacionBean.getFieldName(IPayrollAlias.OCUPACION_OCUPACION_MAESTRO_CDG), ocupaciones.substring(i, i + 1));
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
