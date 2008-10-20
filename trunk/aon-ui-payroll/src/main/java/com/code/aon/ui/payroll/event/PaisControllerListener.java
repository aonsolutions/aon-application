package com.code.aon.ui.payroll.event;


import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.cotizacion.Ocupacion;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.geograficas.Comunidad;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class PaisControllerListener extends ControllerAdapter {

	@Override
		public void afterBeanSelected(ControllerEvent event)
				throws ControllerListenerException {
		/*
		BasicController comunidad = (BasicController)AonUtil.getController(Comunidad.class.getName());
		BasicController provincia= (BasicController)AonUtil.getController(Provincia.class.getName());
		
		comunidad.initializeModel();
		provincia.initializeModel();
		*/
		
		}	
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		BasicController comunidad = (BasicController)AonUtil.getController(Comunidad.class.getName());
		BasicController provincia= (BasicController)AonUtil.getController(Provincia.class.getName());
		
		comunidad.initializeModel();
		provincia.initializeModel();
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		IController comunidad = AonUtil.getController("comunidad");
		IController provincia= AonUtil.getController("provincia");
		try {
			IManagerBean comunidadBean = BeanManager.getManagerBean( Comunidad.class );
			IManagerBean provinciaBean = BeanManager.getManagerBean( Provincia.class );
			
			//if(!event.getController().isNew()){
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(comunidadBean.getFieldName(IPayrollAlias.COMUNIDAD_CDG), "");
				comunidad.setCriteria(criteria);
				criteria = new Criteria();
				criteria.addEqualExpression(provinciaBean.getFieldName(IPayrollAlias.PROVINCIA_CDG), "");
				provincia.setCriteria(criteria);
			//}
		} catch (ManagerBeanException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/*
		comunidad.initializeModel();
		provincia.initializeModel();
		*/
		try {
			System.out.println("COMUNIDAD------------------------"+comunidad.getModel().getRowCount());
			System.out.println("PROVINCIA------------------------"+provincia.getModel().getRowCount());
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		try {
			IManagerBean comu = BeanManager.getManagerBean(Comunidad.class);
			
			comu.initializePOJO(null);
			
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
	}
}
