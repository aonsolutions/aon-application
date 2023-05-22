package com.code.aon.registry;

import java.util.StringTokenizer;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.config.BankAccount;
import com.code.aon.config.IPayMethod;
import com.esferalia.aon.entity.master.RegistryPayMethodDB;

@Entity
@Table(name="rpaymethod")
public class RegistryPayMethod extends RegistryPayMethodDB implements IPayMethod {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    private final String DELIM = " ";
    private int[] paymentDaysArray;
    
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

	@Transient
	public BankAccount getBankAccount() {
		return getRegistryBank()==null?null:getRegistryBank().getBankAccount();
	}

	@Transient
	public String getBankAlias() {
		return getRegistryBank()==null?null:getRegistryBank().getBankAlias();
	}

	@Transient
	public String getBic() {
		return getRegistryBank()==null?null:getRegistryBank().getBic();
	}

}