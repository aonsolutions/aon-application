package com.code.aon.finance.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different sources of an Invoice.
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 * @version 1.0
 */
public enum InvoiceSource implements IResourceable {

	/** DIRECT_EXPENSE. */
	DIRECT_EXPENSE,

	/** DIRECT PURCHASE. Facturas directas de compra que generan albaranes y pedidos */
	DIRECT_PURCHASE,
	
	/** DIRECT SALES. Facturas directas de venta que generan albaranes y pedidos */
	DIRECT_SALES,

    /** DELIVERY. Facturacion de un albaran de venta */
    DELIVERY,
    
    /** INCOME. Facturacion de un albaran de compra */
    INCOME,

    /** FEE. Facturacion de una cuota */
    FEE,
    
    /** ACCOUNT. Facturacion desde un apunte contable */
    ACCOUNT, 
    
    /** DIRECT INVOICE. Factura directa y punto */
    DIRECT_INVOICE,

    /** OFFER. Facturacion de un presupuesto */
    OFFER;

	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.finance.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_invoice_source_";

    /**
     * Returns a <code>String</code> with the transalation <code>Locale</code>
     * for the locale.
     * 
     * @param locale Required Locale.
     * 
     * @return String a <code>String</code>.
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}