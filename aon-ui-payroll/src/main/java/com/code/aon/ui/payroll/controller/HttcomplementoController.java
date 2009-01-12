package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.auxiliares.convenios.Categoria;
import com.code.aon.payroll.auxiliares.convenios.Complemento;
import com.code.aon.payroll.enumeration.EstadoCivil;
import com.code.aon.payroll.enumeration.Timecont;
import com.code.aon.payroll.enumeration.TipIrpf;
import com.code.aon.payroll.enumeration.Tipccc;
import com.code.aon.payroll.enumeration.Prorateo;
import com.code.aon.ui.form.LinesController;


public class HttcomplementoController extends LinesController {
	
    private Complemento complemento;

	public Complemento getComplemento() {
		return complemento;
	}

	public void setComplemento(Complemento complemento) {
		this.complemento = complemento;
	}
    
	
}


