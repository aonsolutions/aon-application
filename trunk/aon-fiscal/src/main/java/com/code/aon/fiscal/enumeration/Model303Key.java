package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different types of an Invoice.
 * 
 */
public enum Model303Key implements IResourceable {

	A1(true,true,true,false,false),		//	A1=REGIMEN GENERAL
	A2(true,true,true,false,false),		//	A2=RECARGO EQUIVALENCIA
	A12(false,false,false,true,false),	//	A12=TOTAL CUOTA DEVENGADA
	A3(false,false,false,true,false),	//	A3=ADQUISIONES INTRACOMUNITARIAS
	A4(false,false,false,true,false),	//	A4=INVERSION DE SUJETO PASIVO
	A5(false,false,false,true,false),	//	A5=MODIFICACION BASES Y CUOTAS
	AT(false,false,false,true,true),	//	AT=TOTAL DEVENGADO
	B1(false,false,false,false,false),	//	B1=OP. INTERIORES DE BIENES CORRIENTES
	B2(false,false,false,false,false),	//	B2=OP. INTERIORES DE BIENES CORRIENTES DE INVERSION
	BT(false,false,false,true,false),	//	BT=TOTAL OP. INTERIORES DE BIENES CORRIENTES
	C1(false,false,false,false,false),	//	C1=IMPORTACIONES DE BIENES CORRIENTES
	C2(false,false,false,false,false),	//	C2=IMPORTACIONES DE BIENES CORRIENTES DE INVERSION
	CT(false,false,false,true,false),	//	CT=TOTAL IMPORTACIONES DE BIENES CORRIENTES
	D1(false,false,false,false,false),	//	D1=ADQ. INTRACOM. DE BIENES CORRIENTES
	D2(false,false,false,false,false),	//	D2=ADQ. INTRACOM. DE BIENES CORRIENTES DE INVERSION
	DT(false,false,false,true,false),	//	DT=TOTAL ADQ. INTRACOM. DE BIENES CORRIENTES
	ET(false,false,false,true,false),	//	ET=COMPENSACION REGIMEN ESPECIAL A,G Y P.
	FT(false,false,false,true,true),	//	FT=TOTAL A DEDUCIR
	DF(false,false,false,true,true),	//	DF=DIFERENCIA
	SP(false,false,false,false,false),	//  SP=LINEA EN BLANCO
	CP(true,true,true,false,false),		//  CP=COMPRAS DE BIENES CORRIENTES
	GT(true,true,true,false,false),		//  GT=GASTOS
	BI(true,true,true,false,false),		//  BI=BIENES DE INVERSION
	TD(true,true,false,true,false),		//  TD=TOTAL CUOTA DEDUCIBLE
	SP2(false,false,false,false,false),	//  SP2=LINEA EN BLANCO
	EX(false,false,false,true,false),	//  Exportaciones y Operaciones Asimiladas
	EI(false,false,false,true,false),	//  Entregas Intracomunitarias
	OO(false,false,false,true,false),	//  Otras Operaciones no sujetas con derecho a deducción
	OI(false,false,false,true,false);	//  Operaciones por inversión de sujet pasivo no incluídas.

	private boolean detailed;
	private boolean percentVisible;
	private boolean deductibleQuotaVisible;
	private boolean subtotal;
	private boolean total;

	private Model303Key( boolean detailed,boolean percentVisible,boolean deductibleQuotaVisible,boolean subtotal,boolean total) {
		this.detailed = detailed;
		this.percentVisible = percentVisible;
		this.deductibleQuotaVisible = deductibleQuotaVisible;
		this.subtotal = subtotal;
		this.total = total;
	}
	
	public boolean isDetailed() {
		return detailed;
	}
	public boolean isPercentVisible() {
		return percentVisible;
	}
	public boolean isDeductibleQuotaVisible() {
		return deductibleQuotaVisible;
	}
	public boolean isSubtotal() {
		return subtotal;
	}
	public boolean isTotal() {
		return total;
	}

	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.fiscal.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_model303_keys_";

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