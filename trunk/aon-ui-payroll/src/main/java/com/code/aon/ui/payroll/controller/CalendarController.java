package com.code.aon.ui.payroll.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import com.code.aon.payroll.dao.IPayrollAlias;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import com.code.aon.payroll.avanzadas.gestel.dao.*;
import org.hibernate.Query;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.payroll.auxiliares.convenios.calendar.Anyo;
import com.code.aon.payroll.auxiliares.convenios.calendar.Calendario;
import com.code.aon.payroll.auxiliares.convenios.calendar.Mes;
import com.code.aon.payroll.enumeration.Tipdia;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;

public class CalendarController extends LinesController implements IPayrollConstants {

	private Empresa empresa;
	private Actividad actividad;
	private Domicilio domicilio;
	private Integer actual= 2009;
	List<ITransferObject> list;	
	Anyo year;
	
	
	Calendar now = new GregorianCalendar(2010,0,1);
	Integer dia = now.get(Calendar.DAY_OF_MONTH);
	Integer mes = now.get(Calendar.MONTH);
	Integer anio = now.get(Calendar.YEAR);
	
	String[] meses = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
			"Julio", "Agosto", "Septiembre", "Octubre", "Noviembre",
			"Diciembre" };
	String[] numsem = { "1", "2", "3", "4", "5" };
	


	
	

	public void load(ActionEvent ev) throws ManagerBeanException {

		year = new Anyo();
		List<String> listaDias = new ArrayList<String>();
		listaDias.add("L");
		listaDias.add("M");
		listaDias.add("M");
		listaDias.add("J");
		listaDias.add("V");
		listaDias.add("S");
		listaDias.add("D");

		List<String> listaMeses = new ArrayList<String>();
		listaMeses.add("Enero");
		listaMeses.add("Febrero");
		listaMeses.add("Marzo");
		listaMeses.add("Abril");
		listaMeses.add("Mayo");
		listaMeses.add("Junio");
		listaMeses.add("Julio");
		listaMeses.add("Agosto");
		listaMeses.add("Septiembre");
		listaMeses.add("Octubre");
		listaMeses.add("Noviembre");
		listaMeses.add("Diciembre");

	

		while (mes <= 11)

		{
			Mes m = new Mes();
			m.setCdg(mes);
			m.setDesc(listaMeses.get(mes));
			year.getListaMeses().add(m); // añade el mes a listaMeses
			int j = 0; // variable para meter dias en
			// blanco al principio del mes

			while (dia <= numDiasMes(mes, anio)) {

				if (j == 0) {

					int dayOfWeek = 0;
					dayOfWeek = now.get(Calendar.DAY_OF_WEEK) - 1;

					if (dayOfWeek == -1) {
						dayOfWeek = 6;

					}
					if (dayOfWeek == 0) {
						dayOfWeek = 7;

					}
					System.out.println(dayOfWeek);
					for (int i = 1; i < dayOfWeek; i++) { // Mete
						// blanks
						// hasta el
						// primer
						// dia del
						// mes
						Calendario d = new Calendario();
						d.setCdg(null);
						d.setFeccal(null);
						d.setTipdia(null);
						d.setEmpresa(null);
						d.setActividad(null);
						d.setDomicilio(null);
						m.addDia(d);

					}
				}
				j = 1;

				Calendario d = new Calendario();
				d.setCdg(dia);

				Calendar fecha = new GregorianCalendar(2009, mes, dia);
				fecha.get(Calendar.DAY_OF_MONTH);
				d.setFeccal(fecha.getTime());
				d.setTipdia(Tipdia.TIP4);
				d.setEmpresa(null);
				d.setActividad(null);
				d.setDomicilio(null);

				int dayOfWeek = 0;
				dayOfWeek = now.get(Calendar.DAY_OF_WEEK) - 1;

				if (dayOfWeek == 6) {
					d.setTipdia(Tipdia.TIP1);

				}
				if (dayOfWeek == 0) {
					d.setTipdia(Tipdia.TIP1);

				}

				m.addDia(d);
				dia++;
				now.add(Calendar.DAY_OF_MONTH, 1);
			}
			now.add(Calendar.MONTH, 1);
			mes++; // va al siguiente mes
			dia = 1; // se coloca en el primer dia del
			// mes

			j = 0; // inicializa variable para meter
			// Blanks al principo del mes
		}
		mes=0;
		dia=1;
		
	}

	public static int numDiasMes(int mes, int año) {
		int dias = 31;
		switch (mes) {
		case 1:
			if (bisiesto(año))
				dias = 29;
			else
				dias = 28;
			break;
		case 3:
		case 5:
		case 8:
		case 10:
			dias = 30;
			break;
		}
		return dias;
	}

	public static boolean bisiesto(int año) {
		return ((año % 4 == 0 && año % 100 != 0) || (año % 400 == 0));
	}


	
    public  List<ITransferObject> getSpecialDays() throws ManagerBeanException{
		
	    	
    	Calendar fecini = new GregorianCalendar(actual,1,1);
    	Calendar fecfin = new GregorianCalendar(actual,12,31);
    	Date ini= fecini.getTime();
    	Date fin= fecfin.getTime();
    	IManagerBean bean = BeanManager.getManagerBean(Calendario.class);
    	String emp = bean.getFieldName(IPayrollAlias.CALENDARIO_EMPRESA_CDG);
    	String act = bean.getFieldName(IPayrollAlias.CALENDARIO_ACTIVIDAD_CDG);
    	String dom = bean.getFieldName(IPayrollAlias.CALENDARIO_DOMICILIO_CDG);
    	String fec = bean.getFieldName(IPayrollAlias.CALENDARIO_FECCAL);
    	
    	Criteria criteria = new Criteria();	
    	
    	criteria.addEqualExpression(emp,empresa.getCdg());
    	criteria.addEqualExpression(act,actividad.getCdg());
    	criteria.addEqualExpression(dom,domicilio.getCdg());    	
    	criteria.addBetweenExpression(fec,ini,fin);
    	
    	list = bean.getList(criteria);
		   	
		
			return list;
	}
	
    
    public  Anyo  insertDays(ActionEvent ev) throws ManagerBeanException{
    	dia=1;
    	mes=0;
    	//anio=2009;
    	
    	getSpecialDays();
    	
    	for  (ITransferObject to : list) {
    		 Calendario c = (Calendario) to;
    		 
    			while (mes < year.getListaMeses().size()) {

	
				while (dia <  year.getListaMeses().get(mes).getlistaDias().size())
				
				{
					System.out.println(c.getFeccal());
					System.out.println(year.getListaMeses().get(mes).getlistaDias().get(dia).getFeccal());
					if (c.getFeccal() == year.getListaMeses().get(mes).getlistaDias().get(dia).getFeccal())

					{
						year.getListaMeses().get(mes).getlistaDias().get(dia).setTipdia(Tipdia.TIP2);
					//	System.out.println((Tipdia)year.getListaMeses().get(mes).getlistaDias().get(dia).getTipdia().getValue());
				
					}
					dia++;
				}
		    	dia=1;
				mes++;
			}
    			 
    		 }    		
		   			   
			return year;
	
}
    





	public Anyo getYear() {
		return year;
	}

	public void setYear(Anyo year) {
		this.year = year;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Actividad getActividad() {
		return actividad;
	}

	public void setActividad(Actividad actividad) {
		this.actividad = actividad;
	}

	public Domicilio getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}
	private List<SelectItem> listatipos;

	public List<SelectItem> getListatipos() {
		if (listatipos == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			listatipos = new LinkedList<SelectItem>();
			for (Tipdia e : Tipdia.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				listatipos.add(item);
			}
		}
		return listatipos;
	}

	public Integer getActual() {
		return actual;
	}

	public void setActual(Integer actual) {
		this.actual = actual;
	}
	
	
}
