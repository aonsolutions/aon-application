package com.code.aon.registry;

import java.util.StringTokenizer;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.IPayMethod;
import com.code.aon.config.PayMethod;
import com.esferalia.aon.entity.master.RegistrySupplierDB;

@Entity
@Table(name="rsupplier")
public class RegistrySupplier extends RegistrySupplierDB implements IBankAccountContainer, IPayMethod {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    private static final String DELIM = " ";

    private int[] paymentDaysArray;

    public RegistrySupplier() {
    	setNumberOfPayments(1);
    }

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
	public PayMethod getPayment() {
		return getPayMethod();
	}

}