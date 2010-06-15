package com.code.aon.ui.payroll.event;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.avanzadas.kartel.Linpercepcion;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.Utils;
import com.code.aon.ui.util.AonUtil;

public class LinpercepcionControllerListener extends ControllerAdapter {

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
	public void beforeBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println("beforeBeanSelected");
		super.beforeBeanSelected(event);
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println("beforeBeanAdded");
		
		((Linpercepcion)getController().getTo()).getId().setCdg(((Linpercepcion)getController().getTo()).getPercepcion().getCdg());
		comprobarNulos();
		comprobarFechas();
		overLap(event);
		
		super.beforeBeanAdded(event);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println("beforeBeanUpdated");
		comprobarNulos();
		comprobarFechas();
		overLap(event);
		
		super.beforeBeanUpdated(event);
	}
	
	
	/**
	 * Comprueba que fecfin sea posterior a fecini
	 * @throws ControllerListenerException 
	 */
	private void comprobarFechas() throws ControllerListenerException{
		Date fecini = ((Linpercepcion)getController().getTo()).getId().getFecini();
		Date fecfin = ((Linpercepcion)getController().getTo()).getFecfin();
		
		if(Utils.startEndDateschecker(fecini, fecfin)){
			FacesMessage fm = AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_1405", null );
			throw new ControllerListenerException( fm.getSummary() );
		}
	}
	
	/**
	 * Comprueba los campos nulos para darles valor por defecto
	 * @throws ControllerListenerException 
	 */
	private void comprobarNulos() {
		if(((Linpercepcion)getController().getTo()).getFecfin() == null){
			Calendar c = Calendar.getInstance(); 
			// sin aclarar: metodo set incrementa el año en uno
			c.set(9998, 12, 31);
			((Linpercepcion)getController().getTo()).setFecfin(c.getTime());
		}
		if(((Linpercepcion)getController().getTo()).getImporte() == null)
			((Linpercepcion)getController().getTo()).setImporte(new BigDecimal(0));
		if(((Linpercepcion)getController().getTo()).getNocturno() == null)
			((Linpercepcion)getController().getTo()).setNocturno(new BigDecimal(0));
		if(((Linpercepcion)getController().getTo()).getEmpresa() == null)
			((Linpercepcion)getController().getTo()).setEmpresa(new BigDecimal(0));
	}
	
	/**
	 * Controla que las fechas no tengan solapes entre si
	 * @param event
	 * @throws ControllerListenerException
	 */
	private void overLap(ControllerEvent event) throws ControllerListenerException{

		Linpercepcion linp = (Linpercepcion)getController().getTo();

		Date fecini = linp.getId().getFecini();
		Date fecfin = linp.getFecfin();

		try {
			IManagerBean bean = BeanManager.getManagerBean(Linpercepcion.class);
			String alias = bean.getFieldName(IPayrollAlias.LINPERCEPCION_ID_CDG);
			
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(alias, linp.getId().getCdg());
			// alias = bean.getFieldName(IPayrollAlias.LINPERCEPCION_ID_FECINI);
			// criteria.addEqualExpression(alias, linp.getId().getFecini());


			List<ITransferObject> lista = getController().getManagerBean().getList(criteria);
			Iterator<ITransferObject> iter = lista.iterator();
			
			while (iter.hasNext()) {
				Linpercepcion l = (Linpercepcion) iter.next();
				if(!(l.getId().equals(linp.getId()))){
					if ((fecini.before(l.getFecfin()) && fecini.after(l.getId().getFecini()))
							|| (fecfin.before(l.getFecfin()) && fecfin.after(l.getId().getFecini()))) {
						FacesMessage fm = AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_1480", null );
						throw new ControllerListenerException( fm.getSummary() );
					}
				}
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
