package com.code.aon.registry;

import java.util.StringTokenizer;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.IPayMethod;
import com.esferalia.aon.entity.master.RegistryPayMethodDB;

@Entity
@Table(name="rpaymethod")
public class RegistryPayMethod extends RegistryPayMethodDB implements IPayMethod {

	private static final long serialVersionUID = 1L;

    private int[] paymentDaysArray;
    /** The DELIM. */
    private final String DELIM = " ";
    
    public void setPaymentDays(String paymentDays) {
        super.setPaymentDays(paymentDays);
        StringTokenizer strTknzr = new StringTokenizer(getPaymentDays(),DELIM);
    	int[] values = new int[strTknzr.countTokens()];
    	for (int i = 0; i < values.length; i++){
    		values[i] = Integer.parseInt(strTknzr.nextToken());
    	}    	
        this.paymentDaysArray = values;
    }

    @Transient
    public int[] getPaymentDaysArray() {
    	return paymentDaysArray;
    }

	@Override
	@Transient
	public Bank getBank() {
		return getRegistryBank()==null?null:getRegistryBank().getBank();
	}

	@Transient
	@Override
	public BankAccount getBankAccount() {
		return getRegistryBank()==null?null:getRegistryBank().getBankAccount();
	}

	
}