package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.convenios.Convenio;
import com.code.aon.payroll.auxiliares.organismosyentidades.Mutua;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httbonificacion;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httrabajador;
import com.code.aon.payroll.avanzadas.simulacion.Costes;
import com.code.aon.payroll.avanzadas.simulacion.Lbonifica;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.EnvioSS2;
import com.code.aon.payroll.enumeration.Inddias;
import com.code.aon.payroll.enumeration.Representantes;
import com.code.aon.payroll.enumeration.Tipccc;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.principales.empresa.Emprccc;
import com.code.aon.payroll.principales.empresa.Emprccos;
import com.code.aon.payroll.principales.empresa.Emprctra;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;

public class LbonificaController extends LinesController {


	
	
	
	
    
	/**
	 * genera un cdg siguiendo al maximo 
	 */
public void generateCdg(){
		
		Integer cdg= ((Costes)(FormUtil.getController(IPayrollConstants.COSTES_CONTROLLER_NAME)).getTo()).getId().getCdg();
		Integer numero= Integer.parseInt(Utils.maxCode("Lbonifica","id.numero","id.cdg="+cdg));
		
		((Lbonifica)getTo()).getId().setCdg(((Httrabajador)(FormUtil.getController(IPayrollConstants.COSTES_CONTROLLER_NAME)).getTo()).getCdg());
		((Lbonifica)getTo()).getId().setNumero(numero +1);
	}
	
	
	
}
