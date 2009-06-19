package com.code.aon.ui.payroll.event;

import java.util.Iterator;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.cotizacion.Porcentaje;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.irpfforal.CuotaRetencionAlava;
import com.code.aon.payroll.irpfforal.MinoracionesAlava;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.Utils;
import com.code.aon.ui.util.AonUtil;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MinoracionesAlavaListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		overLap(event);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		overLap(event);
	}

	private void overLap(ControllerEvent event) throws ControllerListenerException{

		IController c = event.getController();
		MinoracionesAlava n = (MinoracionesAlava) c.getTo();

		Date fecini = n.getId().getFecini();
		Date fecfin = n.getFecfin();

		try {

			IManagerBean bean = BeanManager
					.getManagerBean(MinoracionesAlava.class);

			Criteria criteria = new Criteria();
			String alias = bean
					.getFieldName(IPayrollAlias.MINORACIONES_ALAVA_ID_NUM_TRAMO);
			criteria.addEqualExpression(alias, n.getId().getnumTramo());

			List<ITransferObject> lista = bean.getList(criteria);
			Iterator iter = lista.iterator();
			while (iter.hasNext()) {
				MinoracionesAlava cra = (MinoracionesAlava) iter.next();
				if ((fecini.after(cra.getId().getFecini()) && fecfin.before(cra.getFecfin()))
				|| (fecini.after(cra.getId().getFecini()) && fecfin.after(cra.getFecfin()))
				|| (fecini.before(cra.getId().getFecini()) && fecfin.before(cra.getFecfin())))

				{
					
					FacesMessage fm = 
			    		AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_1480", null );
					throw new ControllerListenerException( fm.getSummary() );
			}}

		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
