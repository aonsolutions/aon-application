package com.code.aon.commercial;

import java.util.StringTokenizer;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.IPayMethod;
import com.code.aon.config.PayMethod;
import com.esferalia.aon.entity.master.TargetSupplierDB;

@Entity
@Table(name="target_supplier")
public class TargetSupplier extends TargetSupplierDB implements IBankAccountContainer, IPayMethod {

	private static final long serialVersionUID = -9181775363113298274L;

    private static final String DELIM = " ";

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
	public PayMethod getPayment() {
		return getPayMethod();
	}

}