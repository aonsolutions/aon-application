package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.view.client.ProvidesKey;

public class ContractDeduction implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private byte type;
	private String description;
	private String expression;
	private Date startDate;
	private Date endDate;
	private boolean hasChange;
	
	/**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<ContractDeduction> KEY_PROVIDER = new ProvidesKey<ContractDeduction>() {
      @Override
      public Object getKey(ContractDeduction item) {
        return item == null ? null : item.getId();
      }
    };
	
	public ContractDeduction() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public ContractDeduction setId(Integer id) {
		this.id = id;
		return this;
	}

	public byte getType() {
		return type;
	}

	public ContractDeduction setType(byte type) {
		this.type = type;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public ContractDeduction setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getExpression() {
		return expression;
	}

	public ContractDeduction setExpression(String expression) {
		this.expression = expression;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public ContractDeduction setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public ContractDeduction setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public boolean getHasChange() {
		return hasChange;
	}

	public ContractDeduction setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		return this;
	}
	
}
