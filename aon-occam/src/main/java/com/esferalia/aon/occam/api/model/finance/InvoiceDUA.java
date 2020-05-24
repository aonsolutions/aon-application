package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

public class InvoiceDUA implements Serializable {

	private static final long serialVersionUID = 942513078643891675L;
	
	private Integer id;
	private Integer internalInvoice;
	private Integer externalInvoice;
	
	private String code;
	
	/**
	 * Deberá incluirse la cantidad a incrementar y/o a deducir del valor facturado (casilla 42),
	 * expresada en euros, cuando proceda ajustar dicho valor para hallar el valor en aduana
	 * de la mercancía declarada. Las cantidades de que se trate deberán ir precedidas del
	 * signo "+" o "-" y corresponderse con los apartados B y C (casillas 18 y 23) de la
	 * Declaración de Valor en Aduana si ésta tuviera que presentarse.
	 */
	private double adjust;
	

}
