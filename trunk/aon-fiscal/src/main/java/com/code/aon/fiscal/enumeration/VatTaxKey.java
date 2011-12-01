package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum VatTaxKey implements IResourceable, IStringEnum   {
	// ***************************************************************
	// NO DESORDENAR LA LISTA, ESTA TABULADA AUNQUE PAREZCA QUE NO!!
	// ***************************************************************
	
	// ---------------------------------------------------------------------
	// key		,T.B.	,Prct.	,Quota	,Ded.Q.	,SubT	,Total
	// ---------------------------------------------------------------------
	// A1=REGIMEN GENERAL
	A1	("A1"	,true	,true	,true	,true	,false	,false	,null,null),
	// A2=RECARGO EQUIVALENCIA
	A2	("A2"	,true	,true	,true	,true	,false	,false	,null,null),
	// A3=ADQUISIONES INTRACOMUNITARIAS
	A3	("A3"	,true	,true	,true	,false	,false	,false	,null,null),
	// A4=INVERSION DE SUJETO PASIVO
	A4	("A4"	,false	,false	,true	,false	,false	,false	,null,null),
	// A5=MODIFICACION BASES Y CUOTAS
	A5	("A5"	,false	,false	,true	,false	,false	,false	,null,null),
	// AT=TOTAL DEVENGADO
	AT	("AT"	,false	,false	,true	,false	,true	,true	,new VatTaxKey[]{VatTaxKey.A1,VatTaxKey.A2,VatTaxKey.A3,VatTaxKey.A4,VatTaxKey.A5},null),
	// B1=OP. INTERIORES DE BIENES CORRIENTES
	B1	("B1"	,false	,false	,true	,false	,false	,false	,null,null),
	// B2=OP. INTERIORES DE BIENES DE INVERSION
	B2	("B2"	,false	,false	,true	,false	,false	,false	,null,null),
	// B3=OP. INTERIORES DE GASTOS
	B3	("B3"	,false	,false	,true	,false	,false	,false	,null,null),
	// BT=TOTAL OP. INTERIORES
	BT	("BT"	,false	,false	,true	,false	,true	,false	,new VatTaxKey[]{VatTaxKey.B1,VatTaxKey.B2,VatTaxKey.B3},null),
	// C1=IMPORTACIONES DE BIENES CORRIENTES
	C1	("C1"	,false	,false	,true	,false	,false	,false	,null,null),
	// C2=IMPORTACIONES DE BIENES DE INVERSION
	C2	("C2"	,false	,false	,true	,false	,false	,false	,null,null),
	// CT=TOTAL IMPORTACIONES
	CT	("CT"	,false	,false	,true	,false	,true	,false	,new VatTaxKey[]{VatTaxKey.C1,VatTaxKey.C2},null),
	// D1=ADQ. INTRACOM. DE BIENES CORRIENTES
	D1	("D1"	,false	,false	,true	,false	,false	,false	,null,null),
	// D2=ADQ. INTRACOM. DE BIENES DE INVERSION
	D2	("D2"	,false	,false	,true	,false	,false	,false	,null,null),
	// D3=TOTAL ADQ. INTRACOM. DE GASTOS
	D3	("D3"	,false	,false	,true	,false	,false	,false	,null,null),
	// DT=TOTAL ADQ. INTRACOM.
	DT	("DT"	,false	,false	,true	,false	,true	,false	,new VatTaxKey[]{VatTaxKey.D1,VatTaxKey.D2,VatTaxKey.D3},null),
	// ET=COMPENSACION REGIMEN ESPECIAL A,G Y P.
	ET	("ET"	,false	,false	,true	,false	,false	,false	,null,null),
	// RI=REGULARIZACION DE INVERSIONES
	RI	("RI"	,false	,false	,true	,false	,false	,false	,null,null),
	// FT=TOTAL A DEDUCIR
	FT	("FT"	,false	,false	,true	,false	,true	,true	,new VatTaxKey[]{VatTaxKey.BT,VatTaxKey.CT,VatTaxKey.DT,VatTaxKey.ET,VatTaxKey.RI},null),
	// DF=DIFERENCIA
	DF	("DF"	,false	,false	,true	,false	,true	,true	,new VatTaxKey[]{VatTaxKey.AT},new VatTaxKey[]{VatTaxKey.FT}),
	// SP=LINEA EN BLANCO
	SP	("SP"	,false	,false	,false	,false	,false	,false	,null,null),
	// CP=COMPRAS DE BIENES CORRIENTES
	CP	("CP"	,true	,true	,true	,true	,false	,false	,null,null),
	// GT=GASTOS
	GT	("GT"	,true	,true	,true	,true	,false	,false	,null,null),
	// BI=BIENES DE INVERSION
	BI	("BI"	,true	,true	,true	,true	,false	,false	,null,null),
	// TD=TOTAL CUOTA DEDUCIBLE
	TD	("TD"	,true	,false	,true	,false	,true	,false	,new VatTaxKey[]{VatTaxKey.CP,VatTaxKey.GT,VatTaxKey.BI},null),
	// SP2=LINEA EN BLANCO
	SP2	("SP2"	,false	,false	,false	,false	,false	,false	,null,null),
	// Entregas Intracomunitarias
	EI	("EI"	,true	,false	,false	,false	,false	,false	,null,null),
	// Exportaciones Definitivas
	EX1	("EX1"	,true	,false	,false	,false	,false	,false	,null,null),
	// Exportaciones Envios Definitivos a Canarias,Ceuta y Melilla
	EX2	("EX2"	,true	,false	,false	,false	,false	,false	,null,null),
	// Otras Operaciones no sujetas con derecho a deducción
	OO	("OO"	,true	,false	,false	,false	,false	,false	,null,null),
	// Otras Op. no sujetas sin drcho. a deducción
	OS	("OS"	,true	,false	,false	,false	,false	,false	,null,null),
	// Operaciones por inversión de sujet pasivo no incluídas.
	OI	("OI"	,true	,false	,false	,false	,false	,false	,null,null);

	private String key;
	private boolean taxableBaseVisible;
	private boolean percentVisible;
	private boolean quotaVisible;
	private boolean deductibleQuotaVisible;
	private boolean subtotal;
	private boolean total;
	private VatTaxKey[] positiveAffectedKeys;
	private VatTaxKey[] negativeAffectedKeys;

	private VatTaxKey( String key, boolean taxableBaseVisible, boolean percentVisible,
			boolean  quotaVisible, boolean deductibleQuotaVisible,boolean subtotal,
			boolean total,VatTaxKey[] positiveAffectedKeys,VatTaxKey[] negativeAffectedKeys) {
		this.key = key;
		this.taxableBaseVisible = taxableBaseVisible;
		this.percentVisible = percentVisible;
		this.quotaVisible = quotaVisible;
		this.deductibleQuotaVisible = deductibleQuotaVisible;
		this.subtotal = subtotal;
		this.total = total;
		this.positiveAffectedKeys = positiveAffectedKeys;
		this.negativeAffectedKeys = negativeAffectedKeys;
	}
	
	public String getKey() {
		return key;
	}
	@Override
	public String getValue() {
		return getKey();
	}
	public boolean isTaxableBaseVisible() {
		return taxableBaseVisible;
	}
	public boolean isPercentVisible() {
		return percentVisible;
	}
	public boolean isQuotaVisible() {
		return quotaVisible;
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