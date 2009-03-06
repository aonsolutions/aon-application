package com.code.aon.ui.payroll.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.Afectados;
import com.code.aon.payroll.principales.persona.Trabajador;
import com.code.aon.ui.form.BasicController;

public class EmbargoBasicController extends BasicController {

	private List<SelectItem> afectados;

	/**
	 * Recupera los tipos de retribuciones
	 * 
	 * @return
	 */
	public List<SelectItem> getListaAfectados() {
		if (afectados == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			afectados = new LinkedList<SelectItem>();
			for (Afectados p : Afectados.values()) {
				String name = p.getName(locale);
				SelectItem item = new SelectItem(p, name);
				afectados.add(item);
			}
		}
		return afectados;
	}
	
	private Date fecha;
	
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	private Trabajador trabajador;

	public Trabajador getTrabajador() {
		return trabajador;
	}

	public void setTrabajador(Trabajador trabajador) {
		this.trabajador = trabajador;
	}

	@Override
	public void onEditSearch(ActionEvent arg0) {
		
		setTrabajador (new Trabajador());
		
		super.onEditSearch(arg0);
	}

	@Override
	public void onSearch(ActionEvent event) {

		try {
			/*
			 * Búsqueda por campos lookup
			 */
			if ((trabajador.getCdg() != null) && (! StringUtils.isEmpty(trabajador.getCdg().toString()))) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMBARGO_TRABAJADOR_CDG), trabajador.getCdg());
			}
			/*
			 * Búsqueda por campos Date
			 */
			if (fecha != null) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.EMBARGO_ID_FECHA), fecha);
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.onSearch(event);
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		
		// meter en el criteria el cdg de embargo
		/*
		LinesController ebc = (LinesController)FormUtil.getController(IPayrollConstants.EMBARGO_LINES_CONTROLLER_NAME);
		
		try {
			this.setCriteria(ebc.getCriteria());
			super.onSelect(event);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		super.onSelect(event);
	}
	
	
}
