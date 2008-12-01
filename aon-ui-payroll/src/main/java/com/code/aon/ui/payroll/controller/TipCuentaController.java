package com.code.aon.ui.payroll.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.cotizacion.Linepigr;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.Epigrafes;
import com.code.aon.payroll.enumeration.Tipcuenta;
import com.code.aon.ui.form.LinesController;

public class TipCuentaController extends LinesController {


	
	private List<SelectItem> listatiposcuentas;

	public List<SelectItem> getListaTipos() {
		if(listatiposcuentas==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			listatiposcuentas = new LinkedList<SelectItem>();
			for (Tipcuenta e : Tipcuenta.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				listatiposcuentas.add(item);
			}
		}
		return listatiposcuentas;
	}
	

	



	

}
