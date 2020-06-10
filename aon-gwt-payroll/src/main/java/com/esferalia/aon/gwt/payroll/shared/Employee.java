package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.NoSuchElementException;

import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.gwt.common.shared.HasId;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Employee implements Serializable, HasId<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2517702556287850026L;

	public static enum Occupation implements HasDescription {

		a("Personal en trabajos exclusivos de oficina"), 
		b("Representantes Comercio"), 
		d("Personal de oficios en instalaciones y reparaciones en edificios, obras y trabajos de construcci\u00F3n en general"), 
		f("Conductores de veh\u00EDculo autom\u00F3vil de transporte de mercanc\u00EDas que tenga una capacidad de carga \u00FAtil superior a 3,5 Tm"), 
		g("Personal de limpieza en general. Limpieza de edificios y de todo tipo de establecimientos. Limpieza de calles"), 
		h("Vigilantes, guardas, guardas jurados y personal de seguridad");

		private String description;

		private Occupation(String desscription) {
			this.description = desscription;
		}

		public String getDescription() {
			return description;
		};
	}
	

	public static enum Dismissal implements HasDescription {
		 UNFAIR("Despido Improcedente"),
		 OBJECTIVE("Despido por Causas Objetivas"),
		 WORK_END("Fin Contrato Fijo de Obra"),
		 TEMP_END("Fin Contrato Temporal"),
		 DEFINITE_END("Fin Contrato Duraci\u00F3n Determinada"),
		 CONDITIONS_CHANGE("Baja Voluntaria Modificaci\u00F3n Condiciones"),
		;

		private String description;

		private Dismissal(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		};
	} 
	
	public enum TC2 implements HasDescription{

		_100 (100 ,"Indefinido, Tiempo Completo, Ordinario"),
		_109 (109 ,"Indefinido, Tiempo Completo, Fomento Contrataci\u00F3n Indefinida"),
		_130 (130 ,"Indefinido, Tiempo Completo, Personas con Discapacidad"),
		_139 (139 ,"Indefinido, Tiempo Completo, Personas con Discapacidad"),
		_150 (150 ,"Indefinido, Tiempo Completo, Fomento Contrataci\u00F3n Indefinida"),
		_189 (189 ,"Indefinido, Tiempo Completo"),
		
		_200 (200 ,"Indefinido, Tiempo Parcial, Ordinario"),
		_209 (209 ,"Indefinido, Tiempo Parcial, Fomento Contrataci\u00F3n Indefinida"),
		_230 (230 ,"Indefinido, Tiempo Parcial, Personas con Discapacidad"),
		_239 (239 ,"Indefinido, Tiempo Parcial, Personas con Discapacidad"),
		_250 (250 ,"Indefinido, Tiempo Parcial, Fomento Contrataci\u00F3n Indefinida"),
		_289 (289 ,"Indefinido, Tiempo Parcial"),
		
		_300 (300 ,"Indefinido, Fijo Discontinuo"),
		_309 (309 ,"Indefinido, Fijo Discontinuo, Fomento Contrataci\u00F3n Indefinida"),
		_330 (330 ,"Indefinido, Fijo Discontinuo, Personas con Discapacidad"),
		_339 (339 ,"Indefinido, Fijo Discontinuo, Personas con Discapacidad"),
		_350 (350 ,"Indefinido, Fijo Discontinuo, Fomento Contrataci\u00F3n Indefinida"),
		_389 (389 ,"Indefinido, Fijo Discontinuo"),
		
		_401 (401 ,"Duraci\u00F3 Determinada, Tiempo Completo, Obra o Servicio Determinado"),
		_402 (402 ,"Duraci\u00F3 Determinada, Tiempo Completo, Eventual Circunstancias de la producci\u00F3n"),
		_403 (403 ,"Duraci\u00F3 Determinada, Tiempo Completo, Inserci\u00F3"),
		_408 (408 ,"Temporal, Tiempo Completo"),
		_410 (410 ,"Duraci\u00F3 Determinada, Tiempo Completo, Interinidad"),
		_418 (418 ,"Duraci\u00F3 Determinada, Tiempo Completo, Interinidad"),
		_420 (420 ,"Temporal, Tiempo Completo, Pr\u00E1cticas"),
		_421 (421 ,"Temporal, Tiempo Completo, Formaci\u00F3n y Aprendizaje"),
		_430 (430 ,"Temporal, Tiempo Completo, Personas con Discapacidad"),
		_441 (441 ,"Temporal, Tiempo Completo, Relevo"),
		_450 (450 ,"Temporal, Tiempo Completo, Fomento Contrataci\u00F3n Indefinida"),
		_452 (452 ,"Temporal, Tiempo Completo, Empresas de Inserci\u00F3"),
		
		_501 (501 ,"Duraci\u00F3 Determinada, Tiempo Parcial, Obra o Servicio Determinado"),
		_502 (502 ,"Duraci\u00F3 Determinada, Tiempo Parcial, Eventual Circunstancias de la producci\u00F3n"),
		_503 (503 ,"Duraci\u00F3 Determinada, Tiempo Parcial, Inserci\u00F3"),
		_508 (508 ,"Temporal, Tiempo Parcial"),
		_510 (510 ,"Duraci\u00F3 Determinada, Tiempo Parcial, Interinidad"),
		_518 (518 ,"Duraci\u00F3 Determinada, Tiempo Parcial, Interinidad"),
		_520 (520 ,"Temporal, Tiempo Parcial, Pr\u00E1cticas"),
		_530 (530 ,"Temporal, Tiempo Parcial, Personas con Discapacidad"),
		_540 (540 ,"Temporal, Tiempo Parcial, Jubilado Parcial"),
		_541 (541 ,"Temporal, Tiempo Parcial, Relevo"),
		_550 (550 ,"Temporal, Tiempo Parcial, Fomento Contrataci\u00F3n Indefinida"),
		_552 (552 ,"Temporal, Tiempo Parcial, Empresas de Inserci\u00F3");
		
		private int code;
		private String description;
		
		private TC2( int code, String description){
			this.code = code;
			this.description = description;
			
		}
		
		public String getCode() {
			return String.valueOf(code);
		}
		
		@Override
		public String getDescription() {
			return code + " " + description;
		}
		
		
		
		public static String [] getCodes() {
			TC2 [] tc2s = TC2.values();
			String [] codes = new String [tc2s.length];
			for (int i = 0; i < tc2s.length; i++)
				codes[i] = tc2s[i].getCode();
			return codes;
		}
		
		public static String [] getDescriptions() {
			TC2 [] tc2s = TC2.values();
			String [] descriptions = new String [tc2s.length];
			for (int i = 0; i < tc2s.length; i++)
				descriptions[i] = tc2s[i].getDescription();
			return descriptions;
		}
		
		public static String getDescriptionByCode(String code) {
			for (TC2 tc2: TC2.values() )
				if ( tc2.getCode().equals(code))
					return tc2.getDescription();
				
			throw new NoSuchElementException();
		}
	}
	
	private int id;

	private int person;

	private String name;
	private String firstSurname;
	private String secondSurname;
	private String socialSecurity;

	private Date startDate;
	private Date endDate;

	private Date seniorityDate;

	private String document;
	
	private Category category;
	
	private Boolean hasSalaries;
	
	private String contractType;

	public Integer getId() {
		return id;
	}

	public Employee setId(int id) {
		this.id = id;
		return this;
	}

	public int getPerson() {
		return person;
	}

	public Employee setPerson(int personId) {
		this.person = personId;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public Employee setDocument(String document) {
		this.document = document;
		return this;
	}
	
	public Employee setSocialSecurity(String pSocialSecurity) {
		socialSecurity = pSocialSecurity;
		return this;
	}
	
	public String getSocialSecurity() {
		return socialSecurity;
	}

	public String getName() {
		return name;
	}

	public Employee setName(String name) {
		this.name = name;
		return this;
	}

	public String getFirstSurname() {
		return firstSurname;
	}

	public Employee setFirstSurname(String firstSurname) {
		this.firstSurname = firstSurname;
		return this;
	}

	public String getSecondSurName() {
		return secondSurname;
	}

	public Employee setSecondSurName(String secondSurName) {
		this.secondSurname = secondSurName;
		return this;
	}

	public String getFullname() {
		StringBuffer sb = new StringBuffer();

		if (AonStringUtils.isNotBlank(firstSurname)) {
			sb.append(firstSurname.trim());
		}
		if (AonStringUtils.isNotBlank(secondSurname)) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(secondSurname.trim());
		}
		if (sb.length() > 0) {
			sb.append(", ");
		}
		sb.append(getName());

		return sb.toString();
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public Employee setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Employee setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public Date getSeniorityDate() {
		return seniorityDate;
	}

	public Employee setSeniorityDate(Date seniorityDate) {
		this.seniorityDate = seniorityDate;
		return this;
	}

	public Category getCategory() {
		return category;
	}
	
	public Employee setCategory(Category category) {
		this.category = category;
		return this;
	}
	
	public Boolean hasSalries() {
		return this.hasSalaries;
	}
	
	public Employee setHasSalaries(Boolean hasSalaries) {
		this.hasSalaries = hasSalaries;
		return this;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj != null) && (obj instanceof Employee)
				&& (id == ((Employee) obj).id);
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	

}
