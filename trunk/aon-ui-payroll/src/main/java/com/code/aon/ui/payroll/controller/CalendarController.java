package com.code.aon.ui.payroll.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.convenios.calendar.Calendario;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.Tipdia;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;

public class CalendarController extends LinesController implements
		IPayrollConstants {

	private Empresa empresa;
	private Actividad actividad;
	private Domicilio domicilio;
	private Integer actual = 2009;
	List<ITransferObject> selectlist;
	List<ITransferObject> selectDefaultList;
	// Date fecdia;
	// Tipdia tipodia;

	int[] listBlanks;

	Anyo year;
	String[] meses = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
			"Julio", "Agosto", "Septiembre", "Octubre", "Noviembre",
			"Diciembre" };
	String[] numsem = { "1", "2", "3", "4", "5" };

	public void load(ActionEvent event) throws ManagerBeanException {

		yearModel = null;
		daysModel = null;
		dias = null;
		Calendar now = new GregorianCalendar(actual, 0, 1);
		Integer dia = now.get(Calendar.DAY_OF_MONTH);
		Integer mes = now.get(Calendar.MONTH);
		Integer anio = now.get(Calendar.YEAR);

		String[] diasem = { "L", "M", "M", "J", "V", "S", "D" };

		if (dias == null) {
			dias = new ArrayList<MyCalendario>();
			j = 0;
			for (int i = 0; i < 40; i++) {

				MyCalendario d = new MyCalendario();
				d.setCdg(i);
				d.setDiasem(diasem[j]);
				dias.add(d);
				j++;
				if (j == 7) {
					j = 0;
				}
			}
		}

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

		mes = 0;
		dia = 1;
		listBlanks = new int[12];
		year = new Anyo();

		while (mes <= 11)

		{
			Mes m = new Mes();
			m.setCdg(mes);
			m.setDesc(listaMeses.get(mes));

			year.getListaMeses().add(m); // añade el mes a listaMeses
			int j = 0; // variable para meter dias en
			// blanco al principio del mes

			while (dia <= numDiasMes(mes, actual)) {

				if (j == 0) {

					int dayOfWeek = 0;
					dayOfWeek = now.get(Calendar.DAY_OF_WEEK) - 1;

					if (dayOfWeek == 6) {
						dayOfWeek = 6;

					}
					if (dayOfWeek == 0) {
						dayOfWeek = 7;

					}
					System.out.println(dayOfWeek - 1);
					listBlanks[mes] = dayOfWeek - 1;
					for (int i = 1; i < dayOfWeek; i++) { // Mete

						MyCalendario d = new MyCalendario();
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

				MyCalendario d = new MyCalendario();
				d.setCdg(dia);

				Calendar fecha = new GregorianCalendar(actual, mes, dia);
				fecha.get(Calendar.DAY_OF_MONTH);
				d.setFeccal(fecha.getTime());
				d.setTipdia(Tipdia.TIP4);
				d.setEmpresa(null);
				d.setActividad(null);
				d.setDomicilio(null);

				int dayOfWeek = 0;
				dayOfWeek = now.get(Calendar.DAY_OF_WEEK) - 1;

				if (dayOfWeek == 6) {
					d.setTipdia(Tipdia.TIP2);

				}
				if (dayOfWeek == 0) {
					d.setTipdia(Tipdia.TIP2);
				}

				m.addDia(d);
				dia++;
				now.add(Calendar.DAY_OF_MONTH, 1);

			}

			now.add(Calendar.MONTH, 1);
			mes++; // va al siguiente mes
			dia = 1; // se coloca en el primer dia del mes
			j = 0; // inicializa variable para meter Blanks al principo del mes

		}
		loadDefaultDays(); // carga dias del año para todas las
		// empresas,actividades y centros de trabajo
	}

	int row1;
	int column1;
	int j = 0;

	public void onChangeDay(ActionEvent event) {
		Mes mes = (Mes) getYearModel().getRowData();
		UIComponent c = event.getComponent().getParent();
		HtmlOutputText o = (HtmlOutputText) c;
		int idx = (Integer) o.getValue();
		MyCalendario cal = mes.getDays()[(idx + listBlanks[mes.getCdg()]) - 1];

		int row = mes.getCdg();
		int col = idx;
		if (row == row1 && col == column1) {

			if (j == 1) {
				cal.setTipdia(Tipdia.TIP2);
				j = 2;
			} else if (j == 2) {
				cal.setTipdia(Tipdia.TIP3);
				j = 3;
			} else if (j == 3) {
				cal.setTipdia(Tipdia.TIP4);
				j = 0;
			} else if (j == 0) {
				cal.setTipdia(Tipdia.TIP1);
				j = 1;
			}

		} else {
			row1 = row;
			column1 = col;
			cal.setTipdia(Tipdia.TIP1);
			j = 1;
			;
		}

		System.out.println(mes.getCdg());
		System.out.println(idx);
		// System.out.println(listBlanks[mes.getCdg()]);

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

	public List<ITransferObject> getSpecialDays() throws ManagerBeanException {

		Calendar fecini = new GregorianCalendar(actual, 0, 1);
		Calendar fecfin = new GregorianCalendar(actual, 11, 31);
		Date ini = fecini.getTime();
		Date fin = fecfin.getTime();
		IManagerBean bean = BeanManager.getManagerBean(Calendario.class);
		String emp = bean.getFieldName(IPayrollAlias.CALENDARIO_EMPRESA_CDG);
		String act = bean.getFieldName(IPayrollAlias.CALENDARIO_ACTIVIDAD_CDG);
		String dom = bean.getFieldName(IPayrollAlias.CALENDARIO_DOMICILIO_CDG);
		String fec = bean.getFieldName(IPayrollAlias.CALENDARIO_FECCAL);

		Criteria criteria = new Criteria();

		criteria.addEqualExpression(emp, empresa.getCdg());
		criteria.addEqualExpression(act, actividad.getCdg());
		criteria.addEqualExpression(dom, domicilio.getCdg());
		criteria.addBetweenExpression(fec, ini, fin);

		Criteria criteria2 = new Criteria();
		criteria2.addEqualExpression(emp, null);
		criteria2.addEqualExpression(act, null);
		criteria2.addEqualExpression(dom, null);
		criteria2.addBetweenExpression(fec, ini, fin);

		selectlist = bean.getList(criteria);

		return selectlist;
	}

	public List<ITransferObject> getDefaultDays() throws ManagerBeanException {

		Calendar fecini = new GregorianCalendar(actual, 0, 1);
		Calendar fecfin = new GregorianCalendar(actual, 11, 31);
		Date ini = fecini.getTime();
		Date fin = fecfin.getTime();
		IManagerBean bean = BeanManager.getManagerBean(Calendario.class);
		String emp = bean.getFieldName(IPayrollAlias.CALENDARIO_EMPRESA_CDG);
		String act = bean.getFieldName(IPayrollAlias.CALENDARIO_ACTIVIDAD_CDG);
		String dom = bean.getFieldName(IPayrollAlias.CALENDARIO_DOMICILIO_CDG);
		String fec = bean.getFieldName(IPayrollAlias.CALENDARIO_FECCAL);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(emp, null);
		criteria.addEqualExpression(act, null);
		criteria.addEqualExpression(dom, null);
		criteria.addBetweenExpression(fec, ini, fin);

		selectDefaultList = bean.getList(criteria);

		return selectDefaultList;
	}

	public Anyo loadDefaultDays() throws ManagerBeanException {
		int dia = 1;
		int mes = 0;

		getDefaultDays();

		for (ITransferObject to : selectDefaultList) {
			Calendario c = (Calendario) to;

			while (mes < year.getListaMeses().size()) {

				while (dia < year.getListaMeses().get(mes).getlistaDias()
						.size())

				{

					if (c.getFeccal() == year.getListaMeses().get(mes)
							.getlistaDias().get(dia).getFeccal())

					{
						year.getListaMeses().get(mes).getlistaDias().get(dia)
								.setTipdia(
										c.updateDay(c.getTipdia().getValue()));

					}
					dia++;
				}
				dia = 1;
				mes++;
			}

		}

		return year;

	}

	public Anyo loadDays(ActionEvent event) throws ManagerBeanException {
		int dia = 1;
		int mes = 0;

		getSpecialDays();

		for (ITransferObject to : selectlist) {
			Calendario c = (Calendario) to;

			while (mes < year.getListaMeses().size()) {

				while (dia < year.getListaMeses().get(mes).getlistaDias().size())
				{
					if (c.getFeccal() == year.getListaMeses().get(mes).getlistaDias().get(dia).getFeccal())
					{
						year.getListaMeses().get(mes).getlistaDias().get(dia).setTipdia(c.updateDay(c.getTipdia().getValue()));
					}
					dia++;
				}
				dia = 1;
				mes++;
			}
		   /* 	yearModel = new ListDataModel(year.getListaMeses());
		    	private MyCalendario[] days;
		    	days = new MyCalendario[year.getListaMeses().listaDias.size()];
				int i = 0;
				for (MyCalendario c : listaDias) {
					days[i] = c;
					i++;
				}
		    	*/
		    	
		}

		return year;

	}

	public void saveModelDays(ActionEvent event) throws ManagerBeanException {
		int dia = 1;
		int mes = 0;
		// anio=2009;

		while (mes < year.getListaMeses().size()) {

			while (dia < year.getListaMeses().get(mes).getlistaDias().size())

			{
				if (year.getListaMeses().get(mes).getlistaDias().get(dia)
						.getTipdia().getValue() == "F")

				{
//IMAnagerBean calendari0= BeanManager
					year.getListaMeses().get(mes).getlistaDias().get(dia);
				}

				else if (year.getListaMeses().get(mes).getlistaDias().get(dia)
						.getTipdia().getValue() == "Z")

				{
					year.getListaMeses().get(mes).getlistaDias().get(dia);
				}

				else if (year.getListaMeses().get(mes).getlistaDias().get(dia)
						.getTipdia().getValue() == "W")

				{
					year.getListaMeses().get(mes).getlistaDias().get(dia);
				}
				dia++;
			}
			dia = 1;
			mes++;
		}

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

	private DataModel yearModel;

	public DataModel getYearModel() {
		if (yearModel == null) {
			yearModel = new ListDataModel(year.getListaMeses());
		}
		return yearModel;
	}

	private DataModel daysModel;

	public DataModel getDaysModel() {

		return daysModel;
	}

	public class Anyo {

		private List<Mes> listaMeses;
		private DataModel monthsModel;

		public Anyo() {
			listaMeses = new ArrayList<Mes>();
		}

		public List<Mes> getListaMeses() {
			return listaMeses;
		}

		public void setListaMeses(List<Mes> lista) {
			listaMeses = lista;
		}

		public DataModel getMonthsModel() {
			if (monthsModel == null) {
				monthsModel = new ListDataModel(listaMeses);
			}
			return monthsModel;
		}

	}

	private ArrayList<MyCalendario> dias = new ArrayList<MyCalendario>();

	public ArrayList<MyCalendario> getDias() {

		return dias;
	}

	public class Mes {

		private List<MyCalendario> listaDias;
		private Integer cdg;
		private String desc;
		private MyCalendario[] days;

		public MyCalendario[] getDays() {
			if (days == null) {
				days = new MyCalendario[listaDias.size()];
				int i = 0;
				for (MyCalendario c : listaDias) {
					days[i] = c;
					i++;
				}
			}
			return days;
		}

		public Integer getCdg() {
			return cdg;
		}

		public void setCdg(Integer cdg) {
			this.cdg = cdg;
		}

		public List<MyCalendario> getlistaDias() {
			return listaDias;
		}

		public void setlistaDias(List<MyCalendario> lista) {
			this.listaDias = lista;
		}

		public void addDia(MyCalendario d)

		{
			this.listaDias.add(d);
		}

		public Mes() {

			listaDias = new ArrayList<MyCalendario>();

		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public void setDays(MyCalendario[] days) {
			this.days = days;
		}

	}

	public class Dias {

		private String dia;

		public String getDia() {
			return dia;
		}

		public void setDia(String dia) {
			this.dia = dia;
		}

	}

	public class MyCalendario extends Calendario {

		private String diasem;

		public String getColor() {
			if (getTipdia() == Tipdia.TIP1) {
				return "#CC3333";
			}
			if (getTipdia() == Tipdia.TIP2) {
				return "#99CC66";
			}
			if (getTipdia() == Tipdia.TIP3) {
				return "#0099FF";
			}
			if (getTipdia() == Tipdia.TIP4) {
				return "white";
			}
			return "transparent";
		}

		public String getDiasem() {
			return diasem;
		}

		public void setDiasem(String diasem) {
			this.diasem = diasem;
		}

		public Tipdia updateDay(String tipo) {
			if (tipo == "F") {
				return Tipdia.TIP1;
			}

			if (tipo == "Z") {
				return Tipdia.TIP2;
			}

			if (tipo == "W") {
				return Tipdia.TIP3;
			}

			if (tipo == "L") {
				return Tipdia.TIP4;
			}
			return null;

		}
	}

}
