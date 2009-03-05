package com.code.aon.ui.payroll.controller;


import java.sql.Date;
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
import com.code.aon.payroll.resultados.nomina.Nomdto;
import com.code.aon.payroll.resultados.nomina.Nomina;
import com.code.aon.payroll.resultados.nomina.Nominadev;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;

public class NominadtoController extends LinesController {


	
	
	
	
    
	public void generateCdg() {

		Integer cdg = ((Nomina) (FormUtil
				.getController(IPayrollConstants.NOMINA_CONTROLLER_NAME))
				.getTo()).getCdg();

		((Nomdto) getTo()).getId().setLinea((Integer.parseInt(Utils.maxCode("Nomdto", "id.linea")) + 1));
		((Nomdto) getTo()).getId().setCdg(cdg);

		
		Date d= new Date(1,1 ,2009);
		
		((Nomdto) getTo()).setFecnew(d);
		((Nomdto) getTo()).setFecmod(d);
		((Nomdto) getTo()).setHornew(d);
		((Nomdto) getTo()).setHormod(d);
		
	}

}
