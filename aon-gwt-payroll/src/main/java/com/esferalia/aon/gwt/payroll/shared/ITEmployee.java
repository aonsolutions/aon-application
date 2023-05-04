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
	
}
