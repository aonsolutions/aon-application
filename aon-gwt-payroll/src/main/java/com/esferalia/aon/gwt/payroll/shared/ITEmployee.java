package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.view.client.ProvidesKey;

public class ITEmployee extends EmployeeContractInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Byte status;
	private List<IT> its;
	
	/**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<ITEmployee> KEY_PROVIDER = new ProvidesKey<ITEmployee>() {
      @Override
      public Object getKey(ITEmployee item) {
        return item == null ? null : item.getContractInfo().getContractId();
      }
    };
	
	public ITEmployee() {
		super();
		this.its = new ArrayList<IT>();
	}

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public List<IT> getIts() {
		return its;
	}

	public void setIts(List<IT> its) {
		this.its = its;
	}
	
	public void addIT(IT it) {
		this.its.add(it);
	}

	public String getContractSuggestion() {
		return this.getEmployeeInfo().getFullName() + " (" + formatDate(this.getContractInfo().getStartDate()) + 
				(null == this.getContractInfo().getEndDate() ? ")" : (" - " + formatDate(this.getContractInfo().getEndDate()) + ")"));
	}
	
	private static String formatDate(Date date) {
		if(null == date) return "";
		
        // Obtener día, mes y año a partir de la fecha
        int day = date.getDate(); // Deprecado, pero permitido para este caso
        int month = date.getMonth() + 1; // Los meses comienzan en 0, así que se suma 1
        int year = date.getYear() + 1900; // Se suma 1900 al año

        // Formatear los valores en "dd/mm/aaaa"
        String dayStr = (day < 10) ? "0" + day : String.valueOf(day);
        String monthStr = (month < 10) ? "0" + month : String.valueOf(month);

        return dayStr + "/" + monthStr + "/" + year;
    }
	
}
