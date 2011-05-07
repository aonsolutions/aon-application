package com.esferalia.aon.payroll;

import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.esferalia.aon.payroll.enumeration.EmbargableType;

/**
 * Transfer Object that represents the contract embargo.
 * 
 */
@Entity
@Table(name="contract_embargo")
public class ContractEmbargo implements ITransferObject {
	
	private static final long serialVersionUID = -6948493684689853167L;
	private static String EXPRESSION_PATTERN = "{0} ? (( PENDIENTE > EMBARGABLE ) ? EMBARGABLE : PENDIENTE ) : 0.00";
	private static String SALARY = "NOMINA";
	private static String EXTRA = "PAGA_EXTRA";
	private static String BOTH = "( NOMINA || PAGA_EXTRA )";
	private static String DELAY = "ATRASOS";

	private Integer id;
	private Contract contract;
	private Date startDate;	
	private Date endDate;
	private double amount;
	private String expression;
	private String description;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name = "contract", nullable = false, updatable = false)
	@ForeignKey(name = "FK_EMBARGO_CONTRACT")
	@Index(name = "IDX_EMBARGO_CONTRACT")
	public Contract getContract() {
		return contract;
	}
	
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	@Column(length = 64)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(length = 128)
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Temporal(TemporalType.DATE)
	@Column( name = "start_date", nullable = false )
    public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.DATE)
	@Column( name = "end_date" )
    public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}	
	
	@Column(precision = 15, scale = 3)
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ContractEmbargo o = (ContractEmbargo) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.contract, o.contract)
				.append(this.description, o.description)
				.append(this.expression, o.expression)
				.append(this.startDate, o.startDate)
				.append(this.endDate, o.endDate)
				.isEquals();	
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(contract)
			.append(description)
			.append(expression)
			.append(startDate)
			.append(endDate)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
	@Transient
	public EmbargableType getEmbargableType(){
		return expressionToType();
	}
	@Transient
	public void setEmbargableType(EmbargableType type){
		typeToExpression(type);
	}

	private EmbargableType expressionToType(){
		if(getExpression()!=null){
			String s = (String) ((new MessageFormat(EXPRESSION_PATTERN)).parse(getExpression(),new ParsePosition(0))[0]);
			if(s.equals(SALARY)){
				return EmbargableType.SALARY;
			} else if(s.equals(EXTRA)){
				return EmbargableType.EXTRA;
			} else if(s.equals(BOTH)){
				return EmbargableType.BOTH;
			} else if(s.equals(DELAY)){
				return EmbargableType.DELAY;
			}
		}
		return null;
	}
	private void typeToExpression(EmbargableType type){
		if(type==EmbargableType.SALARY){
			setExpression(MessageFormat.format(EXPRESSION_PATTERN, SALARY));
		} else if(type==EmbargableType.EXTRA){
			setExpression(MessageFormat.format(EXPRESSION_PATTERN, EXTRA));
		} else if(type==EmbargableType.BOTH){
			setExpression(MessageFormat.format(EXPRESSION_PATTERN, BOTH));
		} else if(type==EmbargableType.DELAY){
			setExpression(MessageFormat.format(EXPRESSION_PATTERN, DELAY));
		}
	}
	
}
