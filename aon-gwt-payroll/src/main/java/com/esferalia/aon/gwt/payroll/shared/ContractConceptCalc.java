package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.view.client.ProvidesKey;

public class ContractConceptCalc implements Serializable {
	
	public enum ContractConceptCalcType {
		PAYMENT,
		DEDUCTION,
		COST,
		BONUS
	}

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Byte type;
	private Integer conceptId;
	private String code;
	private ContractConceptCalcType contractConceptCalcType;
	private String description;
	private String expression;
	private Date startDate;
	private Date endDate;
	private boolean hasChange;
	
	/**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<ContractConceptCalc> KEY_PROVIDER = new ProvidesKey<ContractConceptCalc>() {
      @Override
      public Object getKey(ContractConceptCalc item) {
        return item == null ? null : item.getId();
      }
    };
	
	public ContractConceptCalc() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public ContractConceptCalc setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Byte getType() {
		return type;
	}

	public ContractConceptCalc setType(Byte type) {
		this.type = type;
		return this;
	}

	public Integer getConceptId() {
		return conceptId;
	}

	public ContractConceptCalc setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
		return this;
	}

	public String getCode() {
		return code;
	}

	public ContractConceptCalc setCode(String code) {
		this.code = code;
		return this;
	}

	public ContractConceptCalcType getContractConceptCalcType() {
		return contractConceptCalcType;
	}

	public ContractConceptCalc setContractConceptCalcType(ContractConceptCalcType contractConceptCalcType) {
		this.contractConceptCalcType = contractConceptCalcType;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public ContractConceptCalc setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getExpression() {
		return expression;
	}

	public ContractConceptCalc setExpression(String expression) {
		this.expression = expression;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public ContractConceptCalc setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public ContractConceptCalc setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public boolean getHasChange() {
		return hasChange;
	}

	public ContractConceptCalc setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		return this;
	}
	
}
