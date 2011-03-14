package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum VatTaxKey implements IResourceable, IStringEnum   {

	// key,detailed,percentVisible,deductibleQuotaVisible,subtotal,total
	
	// A1=REGIMEN GENERAL
	A1("A1", true, true, true, false, false),
	// A2=RECARGO EQUIVALENCIA
	A2("A2", true, true, true, false, false),
	// A3=ADQUISIONES INTRACOMUNITARIAS
	A3("A3", true, true, false, false, false),
	// A4=INVERSION DE SUJETO PASIVO
	A4("A4", false, false, false, true, false),
	// A5=MODIFICACION BASES Y CUOTAS
	A5("A5", false, false, false, true, false),
	// AT=TOTAL DEVENGADO
	AT("AT", false, false, false, true, true,new VatTaxKey[]{VatTaxKey.A1,VatTaxKey.A2,VatTaxKey.A3,VatTaxKey.A4,VatTaxKey.A5}),
	// B1=OP. INTERIORES DE BIENES CORRIENTES
	B1("B1", false, false, false, false, false),
	// B2=OP. INTERIORES DE BIENES DE INVERSION
	B2("B2", false, false, false, false, false),
	// B3=OP. INTERIORES DE GASTOS
	B3("B3", false, false, false, false, false),
	// BT=TOTAL OP. INTERIORES
	BT("BT", false, false, false, true, false,new VatTaxKey[]{VatTaxKey.B1,VatTaxKey.B2,VatTaxKey.B3}),
	// C1=IMPORTACIONES DE BIENES CORRIENTES
	C1("C1", false, false, false, false, false),
	// C2=IMPORTACIONES DE BIENES DE INVERSION
	C2("C2", false, false, false, false, false),
	// C3=IMPORTACIONES DE GASTOS
	C3("C3", false, false, false, false, false),
	// CT=TOTAL IMPORTACIONES
	CT("CT", false, false, false, true, false,new VatTaxKey[]{VatTaxKey.C1,VatTaxKey.C2,VatTaxKey.C3}),
	// D1=ADQ. INTRACOM. DE BIENES CORRIENTES
	D1("D1", false, false, false, false, false),
	// D2=ADQ. INTRACOM. DE BIENES DE INVERSION
	D2("D2", false, false, false, false, false),
	// D3=TOTAL ADQ. INTRACOM. DE GASTOS
	D3("D3", false, false, false, false, false),
	// DT=TOTAL ADQ. INTRACOM.
	DT("DT", false, false, false, true, false,new VatTaxKey[]{VatTaxKey.D1,VatTaxKey.D2,VatTaxKey.D3}),
	// ET=COMPENSACION REGIMEN ESPECIAL A,G Y P.
	ET("ET", false, false, false, false, false),
	// FT=TOTAL A DEDUCIR
	FT("FT", false, false, false, true, true,new VatTaxKey[]{VatTaxKey.BT,VatTaxKey.CT,VatTaxKey.DT,VatTaxKey.ET}),
	// DF=DIFERENCIA
	DF("DF", false, false, false, true, true,new VatTaxKey[]{VatTaxKey.AT},new VatTaxKey[]{VatTaxKey.FT}),
	// SP=LINEA EN BLANCO
	SP("SP", false, false, false, false, false,new VatTaxKey[]{}),
	// CP=COMPRAS DE BIENES CORRIENTES
	CP("CP", true, true, true, false, false,new VatTaxKey[]{}),
	// GT=GASTOS
	GT("GT", true, true, true, false, false,new VatTaxKey[]{}),
	// BI=BIENES DE INVERSION
	BI("BI", true, true, true, false, false,new VatTaxKey[]{}),
	// TD=TOTAL CUOTA DEDUCIBLE
	TD("TD", true, false, false, true, false,new VatTaxKey[]{VatTaxKey.CP,VatTaxKey.GT,VatTaxKey.BI}),
	// SP2=LINEA EN BLANCO
	SP2("SP2", false, false, false, false, false,new VatTaxKey[]{}),
	// Entregas Intracomunitarias
	EI("EI", false, false, false, true, false,new VatTaxKey[]{}),
	// Exportaciones Definitivas
	EX1("EX1", false, false, false, true, false,new VatTaxKey[]{}),
	// Exportaciones Envios Definitivos a Canarias,Ceuta y Melilla
	EX2("EX2", false, false, false, true, false,new VatTaxKey[]{}),
	// Otras Operaciones no sujetas con derecho a deducción
	OO("OO", false, false, false, true, false,new VatTaxKey[]{}),
	// Otras Op. no sujetas sin drcho. a deducción
	OS("OS", false, false, false, true, false,new VatTaxKey[]{}),
	// Operaciones por inversión de sujet pasivo no incluídas.
	OI("OI", false, false, false, true, false,new VatTaxKey[]{});

	private String key;
	private boolean detailed;
	private boolean percentVisible;
	private boolean deductibleQuotaVisible;
	private boolean subtotal;
	private boolean total;
	private VatTaxKey[] positiveAffectedKeys;
	private VatTaxKey[] negativeAffectedKeys;

	private VatTaxKey( String key, boolean detailed,boolean percentVisible,boolean deductibleQuotaVisible,boolean subtotal,boolean total,VatTaxKey[] positiveAffectedKeys,VatTaxKey[] negativeAffectedKeys) {
		this.key = key;
		this.detailed = detailed;
		this.percentVisible = percentVisible;
		this.deductibleQuotaVisible = deductibleQuotaVisible;
		this.subtotal = subtotal;
		this.total = total;
		this.positiveAffectedKeys = positiveAffectedKeys;
		this.negativeAffectedKeys = negativeAffectedKeys;
	}
	
	private VatTaxKey( String key, boolean detailed,boolean percentVisible,boolean deductibleQuotaVisible,boolean subtotal,boolean total,VatTaxKey[] affectedKeys) {
		this(key, detailed, percentVisible, deductibleQuotaVisible, subtotal, total,affectedKeys,null);
	}
	private VatTaxKey( String key, boolean detailed,boolean percentVisible,boolean deductibleQuotaVisible,boolean subtotal,boolean total) {
		this(key, detailed, percentVisible, deductibleQuotaVisible, subtotal, total,null,null);
	}
	
	public String getKey() {
		return key;
	}
	@Override
	public String getValue() {
		return getKey();
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
	public VatTaxKey[] getPositiveAffectedKeys() {
		return positiveAffectedKeys;
	}
	public VatTaxKey[] getNegativeAffectedKeys() {
		return negativeAffectedKeys;
	}
	public boolean isBlank() {
		return (this == VatTaxKey.SP || this == VatTaxKey.SP2); 
	}

	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.fiscal.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_vat_tax_keys_";

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