package com.code.aon.ui.payroll.event;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.cotizacion.Porcentaje;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.Utils;
import com.code.aon.ui.util.AonUtil;

public class PorcentajeControllerListener extends ControllerAdapter {

	private BigDecimal pcttotTemp;

	@Override
	public void beforeBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Porcentaje p = (Porcentaje) event.getController().getTo();
		if ( p.getPctemp() == null )
			p.setPctemp( new BigDecimal( 0d ) );
		if ( p.getPcttra() == null )
			p.setPcttra( new BigDecimal( 0d ) );
		p.setPcttot( new BigDecimal( 0d ) );
		Calendar c = Calendar.getInstance();
		c.set( 9999, 11, 31);
		p.setFecfin( c.getTime() );
		pcttotTemp = p.getPcttot();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Porcentaje p = (Porcentaje) event.getController().getTo();
		pcttotTemp = p.getPcttot();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Porcentaje p = (Porcentaje) event.getController().getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean( Porcentaje.class );
			Criteria criteria = new Criteria();
			String fecini = bean.getFieldName( IPayrollAlias.PORCENTAJE_ID_FECINI );
			criteria.addEqualExpression( bean.getFieldName( IPayrollAlias.PORCENTAJE_FECFIN), p.getFecfin() );
			criteria.addEqualExpression( bean.getFieldName( IPayrollAlias.PORCENTAJE_ID_CDG), p.getId().getCdg() );
			Expression expression = ExpressionUtilities.getNotEqualExpression( fecini, p.getId().getFecini() );
			criteria.addExpression( expression );
			criteria.addLessThanOrEqualExpression( fecini, p.getId().getFecini() );
			List l = bean.getList(criteria);
			if ( l.size() > 0 ) {
				Porcentaje lp = (Porcentaje) l.get( 0 );
				Calendar c = Calendar.getInstance();
				c.setTime( p.getId().getFecini() );
				c.add( Calendar.DATE, -1 );
				lp.setFecfin( c.getTime() );
				bean.update( lp );
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e );
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Porcentaje porcentaje = (Porcentaje) event.getController().getTo();
		try {
			Iterator iter = ( (List) event.getController().getModel().getWrappedData() ).iterator();
			if ( Utils.hasOverlap( porcentaje.getId().getFecini(), porcentaje.getFecfin(), iter ) ) {
		    	FacesMessage fm = 
		    		AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_1480", null );
				throw new ControllerListenerException( fm.getSummary() );
			}

			checkBeforeAccept( porcentaje );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e );
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		checkBeforeAccept( (Porcentaje) event.getController().getTo() );
	}

	/**
	 * Checks if the add or update operation can proceed.
	 * 
	 * @param p
	 * @throws ControllerListenerException
	 */
	private void checkBeforeAccept(Porcentaje p) throws ControllerListenerException {
		if (p.getFecfin() == null) {
			Calendar c = Calendar.getInstance();
			c.set( 9999, 11, 31);
			p.setFecfin( c.getTime() );
		}
		if ( p.getPcttot().doubleValue() == pcttotTemp.doubleValue() ) {
			double d = p.getPctemp().doubleValue() + p.getPcttra().doubleValue();
			p.setPcttot( new BigDecimal( d ) );
		}
		Calendar cIni = Calendar.getInstance();
		cIni.setTime( p.getId().getFecini() );
		Calendar cFin = Calendar.getInstance();
		cFin.setTime( p.getFecfin() );
		if ( p.getId().getFecini() == null || cFin.before( cIni ) ) {
	    	FacesMessage fm = 
	    		AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_1405", null );
			throw new ControllerListenerException( fm.getSummary() );
		}
	}
}
