package com.code.aon.marketing.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum MailProcessType implements IResourceable {

    /**
     * AGENCY_NO_SHOW
     */
	AGENCY_NO_SHOW,

	/**
     * GUEST_RESERVATION 
     */
	GUEST_RESERVATION,
	
	/**
     * INVOICE - FACTURA -- Venta | Compra | Gasto
     */
	INVOICE,
	
	/**
     * ORDER - PEDIDO -- Compra | Venta
     */
	ORDER,
	
	/**
     * DELIVERY - ALBARAN -- Compra | Venta
     */
	DELIVERY,
	
	/**
     * FINANCE - VENCIMIENTOS ???? GESTION DE COBROS
     */
	FINANCE,
	
	/**
     * OFFER - PRESUPUESTO
     */
	OFFER;   
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_mail_process_type_";
    
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
