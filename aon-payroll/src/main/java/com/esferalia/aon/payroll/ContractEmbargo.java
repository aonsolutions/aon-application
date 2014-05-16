package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.math.NumberUtils;

import com.esferalia.aon.entity.master.ContractEmbargoDB;

@Entity
@Table(name="contract_embargo")
public class ContractEmbargo extends ContractEmbargoDB {
	
	private static final long serialVersionUID = 1L;
	
	private boolean fixedAmount;
	
	@Transient
	public boolean isFixedAmount() {
		if(NumberUtils.isNumber(getExpression())){
			fixedAmount = true;
		}
		return fixedAmount;
	}
	public void setFixedAmount(boolean fixedAmount) {
		this.fixedAmount = fixedAmount;
		if( !fixedAmount ){
			setExpression("MAX_EMBARGABLE()");
		}
	}
	
	@Transient
	public Double getFee(){
		if(NumberUtils.isNumber(getExpression())){
			return NumberUtils.toDouble(getExpression());
		}
		return null;
	}

	public void setFee(Double fee){
		setExpression(fee.toString());
	}


}
