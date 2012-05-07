package com.esferalia.aon.payroll;


import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.PaymentConceptDB;

@Entity
@Table(name="payment_concept")
public class PaymentConcept extends PaymentConceptDB {
	
	private static final long serialVersionUID = 1L;

}
