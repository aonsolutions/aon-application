package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.gwt.common.shared.HasId;


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
	
	private int id;

	private int person;

	private String name;
	private String firstSurname;
	private String secondSurname;
	private String socialSecurity;

	private Date startDate;
	private Date endDate;

	private String document;
	
	private Category category;

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPerson() {
		return person;
	}

	public void setPerson(int personId) {
		this.person = personId;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}
	
	public void setSocialSecurity(String pSocialSecurity) {
		socialSecurity = pSocialSecurity;
	}
	
	public String getSocialSecurity() {
		return socialSecurity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstSurname() {
		return firstSurname;
	}

	public void setFirstSurname(String firstSurname) {
		this.firstSurname = firstSurname;
	}

	public String getSecondSurName() {
		return secondSurname;
	}

	public void setSecondSurName(String secondSurName) {
		this.secondSurname = secondSurName;
	}

	public String getFullname() {
		StringBuffer sb = new StringBuffer();

		if (firstSurname != null) {
			sb.append(firstSurname.trim());
		}
		if (secondSurname != null) {
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

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public Category getCategory() {
		return category;
	}
	
	public void setCategory(Category category) {
		this.category = category;
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

}
