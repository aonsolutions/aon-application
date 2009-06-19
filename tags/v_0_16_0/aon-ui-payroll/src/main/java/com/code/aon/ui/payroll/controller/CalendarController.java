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
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.payroll.auxiliares.convenios.calendar.Calendario;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.Tipdia;
import com.code.aon.payroll.enumeration.Tipdom;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.principales.empresa.Emprdom;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class CalendarController extends LinesController implements
		IPayrollConstants {

	private Empresa empresa;
	private Actividad actividad;
	private Domicilio domicilio;
	private Integer actual = 2009;
	List<ITransferObject> selectlist;
	List<ITransferObject> selectDefaultList;
	int[] listBlanks;
	Anyo year;
	String[] meses = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
			"Julio", "Agosto", "Septiembre", "Octubre", "Noviembre",
			"Diciembre" };
	String[] diasem = { "L", "M", "M", "J", "V", "S", "D" };
	
    private Integer numFestivos = 0;
	private Integer numNolabo = 0;
	private Integer numOtros = 0;
	private Integer numLaborables = 0;
	private Integer codempresa;
	private Integer codactividad;
	private Integer codcentro;
	private List<SelectItem> empresas;
	private List<SelectItem> actividades;
	private List<SelectItem> domicilios;
	
	
	public void load(ActionEvent event) throws ManagerBeanException {
	  
		
		codempresa =null;
		codactividad=null;
		codcentro= null;
		empresas=null;
		domicilios=null;
		numFestivos = 0;
		numNolabo = 0;
		numOtros = 0;
		numLaborables = 0;
		yearModel = null;
		dias = null;
		int[] huecos = {0,0,0,0,0,0,0,0,0,0,0,0};
		Calendar now = new GregorianCalendar(actual, 0, 1);
		Integer dia = now.get(Calendar.DAY_OF_MONTH);
		Integer mes = now.get(Calendar.MONTH);
	
		int ji=0;	
	
	
	
	while( ji<=11)
	{
		Calendar a= new GregorianCalendar(actual, ji, 1);
		huecos[ji]= a.get(Calendar.DAY_OF_WEEK);
		ji++;
	}
		
		
		if (dias == null) {
			dias = new ArrayList<MyCalendario>();
			j = 0;
			for (int i = 0; i < 38; i++) {

				MyCalendario d = new MyCalendario();
				d.setCdg(i);
				d.setDiasem(diasem[j]);
				dias.add(d);
				if (diasem[j]=="S" || diasem[j]=="D"){
					d.setFinde(true);
				}				
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

		while (mes <= 11) {
			Mes m = new Mes();
			m.setCdg(mes);
			m.setDesc(listaMeses.get(mes));

			year.getListaMeses().add(m); // añade el mes a listaMeses
			int j = 0; // variable para meter dias en
			// blanco al principio del mes

			while (dia <= numDiasMes(mes, actual)) {

				if (j == 0) {

					
					
				
						if (huecos[mes] == 1) {						
							huecos[mes] = 8;
						}
						
						
						listBlanks[mes] = huecos[mes] - 2;
						

			
			
					listBlanks[mes] = huecos[mes] - 2;
					for (int i = 1; i <  huecos[mes]-1; i++) { // Mete blankos antes del primer dia de mes

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

		/*	    int dayOfWeek = 0;
				dayOfWeek = now.get(Calendar.DAY_OF_WEEK);

				if (dayOfWeek == 7) {
					d.setTipdia(Tipdia.TIP5);

				}
				if (dayOfWeek == 1) {
					d.setTipdia(Tipdia.TIP5);
				}
*/
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

		} else if (cal.getTipdia() == Tipdia.TIP1) {
			cal.setTipdia(Tipdia.TIP2);
			row1 = row;
			column1 = col;
			j = 2;
		} else {

			row1 = row;
			column1 = col;
			cal.setTipdia(Tipdia.TIP1);
			j = 1;
			;
		}
		
		countDays();
		System.out.println(mes.getCdg());
		System.out.println(idx);
	}
	
	public void clearFilter(ActionEvent event){
		setEmpresa(null);
		setActividad(null);
		setDomicilio(null);
		
	}
	
	public void setActualYear(ActionEvent event){
		Calendar actualyear = Calendar.getInstance();
		
		setActual(actualyear.getTime().getYear());
			
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
		String emp = bean.getFieldName(IPayrollAlias.CALENDARIO_EMPRESA);
		String act = bean.getFieldName(IPayrollAlias.CALENDARIO_ACTIVIDAD);
		String dom = bean.getFieldName(IPayrollAlias.CALENDARIO_DOMICILIO);
		String fec = bean.getFieldName(IPayrollAlias.CALENDARIO_FECCAL);

		Criteria criteria = new Criteria();

		criteria.addEqualExpression(emp,codempresa);
		criteria.addEqualExpression(act,codactividad);
		criteria.addEqualExpression(dom,codcentro);
		criteria.addBetweenExpression(fec, ini, fin);

		selectlist = bean.getList(criteria);

		return selectlist;
	}

	public List<ITransferObject> getDefaultDays() throws ManagerBeanException {

		Calendar fecini = new GregorianCalendar(actual, 0, 1);
		Calendar fecfin = new GregorianCalendar(actual, 11, 31);
		Date ini = fecini.getTime();
		Date fin = fecfin.getTime();
		IManagerBean bean = BeanManager.getManagerBean(Calendario.class);
		String emp = bean.getFieldName(IPayrollAlias.CALENDARIO_EMPRESA);
		String act = bean.getFieldName(IPayrollAlias.CALENDARIO_ACTIVIDAD);
		String dom = bean.getFieldName(IPayrollAlias.CALENDARIO_DOMICILIO);
		String fec = bean.getFieldName(IPayrollAlias.CALENDARIO_FECCAL);

		Criteria criteria = new Criteria();
		criteria.addNullExpression(emp);
		criteria.addNullExpression(act);
		criteria.addNullExpression(dom);
		
		criteria.addBetweenExpression(fec, ini, fin);

		selectDefaultList = bean.getList(criteria);

		return selectDefaultList;
	}

	public void clearYear() throws ManagerBeanException {
		int dia = 1;
		int mes = 0;
		
			while (mes < year.getListaMeses().size()) {

			while (dia < year.getListaMeses().get(mes).getlistaDias().size()) {

				year.getListaMeses().get(mes).getlistaDias().get(dia)
						.setTipdia(Tipdia.TIP4);

				dia++;
			}
			dia = 1;
			mes++;

		}
			loadDefaultDays();
	}
			
			
	public void loadDefaultDays() throws ManagerBeanException {
		int dia = 1;
		int mes = 0;

		getDefaultDays();
		
			while (mes < year.getListaMeses().size()) {

				while (dia < year.getListaMeses().get(mes).getlistaDias().size())
				{
					
				// Comprobamos si el dia del modelo es = que el de la select y si lo es se le cambia el tipo de dia	
				for (ITransferObject to : selectDefaultList) {
					Calendario c = (Calendario) to;

					if (c.getFeccal().equals(year.getListaMeses().get(mes).getlistaDias().get(dia).getFeccal()))
					{year.getListaMeses().get(mes).getlistaDias().get(dia).setTipdia(c.updateDay(c.getTipdia().getValue()));
					
					}
				}
				
					dia++;
				}
				dia = 1;
				mes++;
			   }
			
			
	    countDays(); //Cuenta los dias de cada tipo
		mes=0;		
		yearModel = new ListDataModel(year.getListaMeses());		 	
		for (int i=0; i<=11;i++)
	 	{year.getListaMeses().get(i).days=null;}

	}
	
	public void loadDays(ActionEvent event) throws ManagerBeanException {
		int dia = 1;
		int mes = 0;
		numFestivos = 0;
		numNolabo = 0;
		numOtros = 0;
		numLaborables = 0;
		clearYear();
	
		getSpecialDays();


			while (mes < year.getListaMeses().size()) {

				while (dia < year.getListaMeses().get(mes).getlistaDias().size())
				{
					// Comprobamos si el dia del modelo es = que el de la select y si lo es se le cambia el tipo de dia	
					for (ITransferObject to : selectlist) {
						Calendario c = (Calendario) to;
						
					if (c.getFeccal().equals(year.getListaMeses().get(mes).getlistaDias().get(dia).getFeccal()))
					{
						year.getListaMeses().get(mes).getlistaDias().get(dia).setTipdia(c.updateDay(c.getTipdia().getValue()));
					    
					}
					}
					
					
					dia++;
				}
				dia = 1;
				mes++;
			 		 		
		       }
			mes=0;
			
		countDays(); //Cuenta los dias de cada tipo
		
		yearModel = new ListDataModel(year.getListaMeses());// recargamos modelo 	
		for (int i=0; i<=11;i++)
	 	{year.getListaMeses().get(i).days=null;	}

	}

	public void generateCdg() {
		((Calendario) getTo()).setCdg(Integer.parseInt(Utils.maxCode(
				"Httrabajador", "cdg")) + 1);
	}
		
	
	
	public void saveModelDays(ActionEvent event) throws ManagerBeanException {
		int dia = 1;
		int mes = 0;
		numFestivos = 0;
		numNolabo = 0;
		numOtros = 0;
		numLaborables = 0;
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {  HibernateUtil.setBeginTransaction(false);
			       HibernateUtil.setCloseSession(false);
			       HibernateUtil.beginTransaction(sessionName);
								
			       while (mes < year.getListaMeses().size()) {

						while (dia < year.getListaMeses().get(mes).getlistaDias().size())

						{
							if ((year.getListaMeses().get(mes).getlistaDias().get(dia).getTipdia()== Tipdia.TIP1)
								|| (year.getListaMeses().get(mes).getlistaDias().get(dia).getTipdia()== Tipdia.TIP2)
									|| (year.getListaMeses().get(mes).getlistaDias().get(dia).getTipdia()== Tipdia.TIP3))

							{
							 
			                 IManagerBean calendario= BeanManager.getManagerBean(Calendario.class);
			                 ITransferObject to = null;
			                 Calendario  cal=new Calendario();
			                 Integer cdg  = Integer.parseInt(Utils.maxCode("Calendario", "cdg")) + 1;
			                 cal.setCdg(cdg);
			                 cal.setEmpresa(codempresa);
			                 cal.setActividad(codactividad);
			                 cal.setDomicilio(codcentro);
			                 cal.setTipdia(year.getListaMeses().get(mes).getDays()[dia].getTipdia());
			                 cal.setFeccal(year.getListaMeses().get(mes).getDays()[dia].getFeccal());
			                 
			                 calendario.insertOrUpdate(cal);
			                               
							}
							else if (year.getListaMeses().get(mes).getlistaDias().get(dia).getTipdia()== Tipdia.TIP4){
								
								
							}				
						
							
							dia++;
						}
						dia = 1;
						mes++;
					}
					countDays(); //Cuenta los dias de cada tipo
				
					HibernateUtil.getSession(sessionName).flush();
					HibernateUtil.commitTransaction(sessionName);
				} catch (Exception e) {
					try {
						HibernateUtil.rollbackTransaction(sessionName);
					} catch (DAOException daoe) {
						String msg = "Unable to rollback transaction!";
						//Logger.log(Level.SEVERE, msg, e);
					}
					String msg = "Error al guardar el Calendario:  " + e.getMessage() ;
					//LOGGER.log(Level.SEVERE, msg, e);
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				} finally {
					HibernateUtil.closeSession(sessionName);
				}
			} finally {
				HibernateUtil.setCloseSession(mustCloseSession);
				HibernateUtil.setBeginTransaction(mustBeginTransaction);
			}
		}
		
	
	
	
	public void countDays(){
		int dia = 1;
		int mes = 0;
		numFestivos = 0;
		numNolabo = 0;
		numOtros = 0;
		numLaborables = 0;
		
		while (mes < year.getListaMeses().size()) {

			while (dia < year.getListaMeses().get(mes).getlistaDias().size())

			{
		
		         if (year.getListaMeses().get(mes).getlistaDias().get(dia).getTipdia() == Tipdia.TIP1) {
		          	setNumFestivos();
		          } else if (year.getListaMeses().get(mes).getlistaDias().get(dia).getTipdia() == Tipdia.TIP2) {
		        	setNumNolabo();
		           } else if (year.getListaMeses().get(mes).getlistaDias().get(dia).getTipdia() == Tipdia.TIP3) {
		        	setNumOtros();
		          } else if (year.getListaMeses().get(mes).getlistaDias().get(dia).getTipdia() == Tipdia.TIP4) {
		           setNumLaborables();
		}	
		dia++;
	     }
    	dia=1;
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


	public class MyCalendario extends Calendario {

		private String diasem;
        private boolean finde;
        
        
		public boolean getFinde() {
			return finde;
		}

		public void setFinde(boolean finde) {
			this.finde = finde;
		}

		public String getWeekend() {
			
			  String color;
	          color= "#000000"; 
	      
	          
			if (getFinde() == true) {
				color= "#00C957";					
			if (   getTipdia()==Tipdia.TIP1 ||getTipdia()==Tipdia.TIP2 || getTipdia()==Tipdia.TIP3) {
				color= "#FFFFFF";
			}
			
			
			}
			
			return color;
			
		}
		
		
		public String getColor() {
			if (getTipdia() == Tipdia.TIP1) {
				return "#FF8247";
			}
			if (getTipdia() == Tipdia.TIP2) {
				return "#EEC900";
			}
			if (getTipdia() == Tipdia.TIP3) {
				return "#5CACEE";
			}
			if (getTipdia() == Tipdia.TIP5) {
				return "#A2CD5A";
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


	}

	public int getNumFestivos() {
		return numFestivos;
	}

	public void setNumFestivos() {
		this.numFestivos++;
	}

	public int getNumNolabo() {
		return numNolabo;
	}

	public void setNumNolabo() {
		this.numNolabo++;
	}

	public int getNumOtros() {
		return numOtros;
	}

	public void setNumOtros() {
		this.numOtros++;
	}

	public int getNumLaborables() {
		return numLaborables;
	}

	public void setNumLaborables() {
		this.numLaborables++;
	}

	
  
    
    
  
	public List<SelectItem> getListaemp() throws ManagerBeanException  {
		if(empresas==null){
			
		empresas = new LinkedList<SelectItem>();
		EmpresaController controller = (EmpresaController)FormUtil.getController(IPayrollConstants.EMPRESA_CONTROLLER_NAME);
		List<ITransferObject> empresa  =  controller.getManagerBean().getList(controller.getCriteria());
		for (ITransferObject to: empresa) {
			Empresa e = (Empresa) to;	
			SelectItem item = new SelectItem(e.getCdg(),""+e.getCdg()+" "+e.getDescripcion());
			empresas.add(item);
		}
		}
		
		return empresas;
	}	
	
	
	public void refreshListaAct(ValueChangeEvent event) throws ManagerBeanException  {
		clearYear();
		Integer newCodEmpresa = (Integer) event.getNewValue();	
		codempresa=newCodEmpresa;
		if (newCodEmpresa != null) {
			IManagerBean bean = BeanManager.getManagerBean(Actividad.class);
			String emp = bean.getFieldName(IPayrollAlias.ACTIVIDAD_EMPRESA_CDG);
			Criteria cri1 = new Criteria();
			cri1.addEqualExpression(emp, newCodEmpresa);
			List<ITransferObject> filtroact;
			filtroact = bean.getList(cri1);
			actividades.clear();
			domicilios.clear();
			for (ITransferObject to : filtroact) {
				Actividad a = (Actividad) to;
				SelectItem item = new SelectItem(a.getCdg(),""+a.getCdg()+": "+a.getDescripcion());
				actividades.add(item);
			}
		
		}        
	}
	
	public void refreshListaDom(ValueChangeEvent event) throws ManagerBeanException  {
		clearYear();
		Integer newCodActividad = (Integer) event.getNewValue();
		codactividad= newCodActividad;
		if (newCodActividad != null) {

			IManagerBean bean = BeanManager.getManagerBean(Emprdom.class);

			String act = bean.getFieldName(IPayrollAlias.EMPRDOM_ACTIVIDAD_CDG);
			String tipdom = bean.getFieldName(IPayrollAlias.EMPRDOM_TIPDOM);

			Criteria cri1 = new Criteria();
			cri1.addEqualExpression(act, newCodActividad);
			cri1.addEqualExpression(tipdom, Tipdom.CENTROSTRABAJO);

			List<ITransferObject> filtroact;
			filtroact = bean.getList(cri1);
			domicilios.clear();
			for (ITransferObject to : filtroact) {
				Emprdom a = (Emprdom) to;
				SelectItem item = new SelectItem(a.getDomicilio().getCdg()," "+a.getDomicilio().getCdg()+" "+a.getDomicilio().getNomvia());
				domicilios.add(item);
			}

			
		}
	}

	public void refreshDomicilios(ValueChangeEvent event) throws ManagerBeanException  {
		Integer newCodCentro = (Integer) event.getNewValue();
		codcentro= newCodCentro;
		clearYear();
		
	}
	
	public Integer getCodempresa() {
		return codempresa;
	}

	public void setCodempresa(Integer codempresa) {		
		this.codempresa = codempresa;
			
	}

	public Integer getCodactividad() {
		return codactividad;
	}

	public void setCodactividad(Integer codactividad) {
		this.codactividad = codactividad;
	}

	public Integer getCodcentro() {
		return codcentro;
	}

	public void setCodcentro(Integer codcentro) {
		this.codcentro = codcentro;
	}

	public List<SelectItem> getActividades() {
		
		if(actividades==null){						
			actividades = new LinkedList<SelectItem>();
			actividades.add( new SelectItem( IPayrollConstants.EMPTY_STRING, IPayrollConstants.EMPTY_STRING ) );		
		}
			
		return actividades;
	}

	public void setActividades(List<SelectItem> actividades) {
		this.actividades = actividades;
	}

	public List<SelectItem> getDomicilios() {
		if(domicilios==null){						
			domicilios = new LinkedList<SelectItem>();
			domicilios.add( new SelectItem( IPayrollConstants.EMPTY_STRING, IPayrollConstants.EMPTY_STRING ) );		
		}
			
		return domicilios;
	}

	public void setDomicilios(List<SelectItem> domicilios) {
		this.domicilios = domicilios;
	}
		
}
