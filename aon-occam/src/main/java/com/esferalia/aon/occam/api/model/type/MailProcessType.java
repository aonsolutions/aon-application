package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum MailProcessType  implements Serializable {
	
	 /**
     * AGENCY_NO_SHOW
     */
	AGENCY_NO_SHOW("Email de No Show de reserva a Agencias"),

	/**
     * GUEST_RESERVATION 
     */
	GUEST_RESERVATION("Email a Huéspedes en reservas"),
	
	/**
     * INVOICE - FACTURA -- Venta | Compra | Gasto
     */
	INVOICE("Email de Facturas"),
	
	/**
     * ORDER - PEDIDO -- Compra | Venta
     */
	ORDER("Email de Pedidos"),
	
	/**
     * DELIVERY - ALBARAN -- Compra | Venta
     */
	DELIVERY("Email de Albaranes"),
	
	/**
     * FINANCE - VENCIMIENTOS ???? GESTION DE COBROS
     */
	FINANCE("Email de Vencimientos"),
	
	/**
     * OFFER - PRESUPUESTO
     */
	OFFER("Email de Presupuestos");  
	
	
	String name;
	
	private MailProcessType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Byte value() {
		return (byte) ordinal();
	}
	
}