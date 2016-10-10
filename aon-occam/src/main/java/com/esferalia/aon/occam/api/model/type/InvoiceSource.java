package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum InvoiceSource implements Serializable {

	DIRECT_EXPENSE 		/** DIRECT_EXPENSE. Facturas directas de gastos */
	,PURCHASE			/** PURCHASE. Facturacion de un pedido de compra */
	,SALES				/** SALES. Facturacion de un pedido de venta */
    ,DELIVERY			/** DELIVERY. Facturacion de un albaran de venta */
    ,INCOME				/** INCOME. Facturacion de un albaran de compra */
    ,FEE				/** FEE. Facturacion de una cuota */
    ,ACCOUNT			/** ACCOUNT. Facturacion desde un apunte contable */ 
    ,DIRECT_INVOICE		/** DIRECT INVOICE. Factura directa y punto */
    ,OFFER				/** OFFER. Facturacion de un presupuesto */
    ,RESERVATION		/** RESERVATION. Facturacion de una reserva PMS */
    ;

	public byte value() {
		return (byte) ordinal();
	}
}