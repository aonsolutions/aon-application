package net.aonsolutions.vat.change;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class VatTaxKey  {

	public static final Map<String,BiConsumer<Mod303,VatTaxDetail>> AEAT_KEY_MAP = new HashMap<String,BiConsumer<Mod303,VatTaxDetail>>();
	static {
		AEAT_KEY_MAP.put("A1", (mod,det) -> {	// A1=REGIMEN GENERAL
			if ( AonNumberUtils.equals(det.getPercent(), AEAT_2017_Declaration.PERCENT1 )) {
				addBase ( mod, Mod303Key.CT_C01, det);
				mod.ensureDetail(Mod303Key.CT_C02).setAmount(AEAT_2017_Declaration.PERCENT1);
				addQuota( mod, Mod303Key.CT_C03, det);
			}
			if ( AonNumberUtils.equals(det.getPercent(), AEAT_2017_Declaration.PERCENT2 )) {
				addBase ( mod, Mod303Key.CT_C04, det);
				mod.ensureDetail(Mod303Key.CT_C05).setAmount(AEAT_2017_Declaration.PERCENT2);
				addQuota( mod, Mod303Key.CT_C06, det);
			}
			if ( AonNumberUtils.equals(det.getPercent(), AEAT_2017_Declaration.PERCENT3 )) {
				addBase ( mod, Mod303Key.CT_C07, det);
				mod.ensureDetail(Mod303Key.CT_C08).setAmount(AEAT_2017_Declaration.PERCENT3);
				addQuota( mod, Mod303Key.CT_C09, det);
			}
			
		});	
		AEAT_KEY_MAP.put("A3", (mod,det) -> {	// A3=ADQUISIONES INTRACOMUNITARIAS DE BIENES
			addBase ( mod, Mod303Key.CT_C10, det);
			addQuota( mod, Mod303Key.CT_C11, det);
		});
		
		AEAT_KEY_MAP.put("A31", (mod,det) -> {	// A31=ADQUISIONES INTRACOMUNITARIAS DE SERVICIOS
			addBase ( mod, Mod303Key.CT_C10, det);
			addQuota( mod, Mod303Key.CT_C11, det);
		});
		
		AEAT_KEY_MAP.put("A4", (mod,det) -> {	// A4=INVERSION DE SUJETO PASIVO
				addBase ( mod, Mod303Key.CT_C12, det);
				addQuota( mod, Mod303Key.CT_C13, det);
		});
		AEAT_KEY_MAP.put("A5", (mod,det) -> {	// A5=MODIFICACION BASES Y CUOTAS
			addBase ( mod, Mod303Key.CT_C14, det);
			addQuota( mod, Mod303Key.CT_C15, det);
		});
		AEAT_KEY_MAP.put("A2", (mod,det) -> {	// A2=RECARGO EQUIVALENCIA
			if ( AonNumberUtils.equals(det.getPercent(), AEAT_2017_Declaration.SURCHARGE_PERCENT1 )) {
				addBase ( mod, Mod303Key.CT_C16, det);
				mod.ensureDetail(Mod303Key.CT_C17).setAmount(AEAT_2017_Declaration.SURCHARGE_PERCENT1);
				addQuota( mod, Mod303Key.CT_C18, det);
			}
			if ( AonNumberUtils.equals(det.getPercent(), AEAT_2017_Declaration.SURCHARGE_PERCENT2 )) {
				addBase ( mod, Mod303Key.CT_C19, det);
				mod.ensureDetail(Mod303Key.CT_C20).setAmount(AEAT_2017_Declaration.SURCHARGE_PERCENT2);
				addQuota( mod, Mod303Key.CT_C21, det);
			}
			if ( AonNumberUtils.equals(det.getPercent(), AEAT_2017_Declaration.SURCHARGE_PERCENT3 )) {
				addBase ( mod, Mod303Key.CT_C22, det);
				mod.ensureDetail(Mod303Key.CT_C23).setAmount(AEAT_2017_Declaration.SURCHARGE_PERCENT3);
				addQuota( mod, Mod303Key.CT_C24, det);
			}
		});
		AEAT_KEY_MAP.put("A21", (mod,det) -> {	// A21=MODIFICACION BASES Y CUOTAS RECARGO EQUIVALENCIA
			addBase ( mod, Mod303Key.CT_C25, det);
			addQuota( mod, Mod303Key.CT_C26, det);
		});
		AEAT_KEY_MAP.put("AT", (mod,det) -> {	// AT=TOTAL DEVENGADO
			addQuota( mod, Mod303Key.CT_C27, det);
		});
		AEAT_KEY_MAP.put("B1", (mod,det) -> {	// B1=OP. INTERIORES DE BIENES CORRIENTES
			addBase ( mod, Mod303Key.CT_C28, det);
			addQuota( mod, Mod303Key.CT_C29, det);
		});
		AEAT_KEY_MAP.put("B2", (mod,det) -> {	// B2=OP. INTERIORES DE BIENES DE INVERSION
			addBase ( mod, Mod303Key.CT_C30, det);
			addQuota( mod, Mod303Key.CT_C31, det);
		});
		AEAT_KEY_MAP.put("B3", (mod,det) -> {	// B3=OP. INTERIORES DE GASTOS
			addBase ( mod, Mod303Key.CT_C28, det);
			addQuota( mod, Mod303Key.CT_C29, det);
		});
		AEAT_KEY_MAP.put("BT", (mod,det) -> {	// BT=TOTAL OP. INTERIORES
			
		});
		AEAT_KEY_MAP.put("C1", (mod,det) -> {	// C1=IMPORTACIONES DE BIENES CORRIENTES
			addBase ( mod, Mod303Key.CT_C32, det);
			addQuota( mod, Mod303Key.CT_C33, det);
		});		
		AEAT_KEY_MAP.put("C2", (mod,det) -> {	// C2=IMPORTACIONES DE BIENES DE INVERSION
			addBase ( mod, Mod303Key.CT_C34, det);
			addQuota( mod, Mod303Key.CT_C35, det);
		});
		AEAT_KEY_MAP.put("CT", (mod,det) -> {	// CT=TOTAL IMPORTACIONES
			
		});
		AEAT_KEY_MAP.put("D1", (mod,det) -> {	// D1=ADQ. INTRACOM. DE BIENES CORRIENTES
			addBase ( mod, Mod303Key.CT_C36, det);
			addQuota( mod, Mod303Key.CT_C37, det);
		});
		AEAT_KEY_MAP.put("D2", (mod,det) -> {	// D2=ADQ. INTRACOM. DE BIENES DE INVERSION
			addBase ( mod, Mod303Key.CT_C38, det);
			addQuota( mod, Mod303Key.CT_C39, det);
		});
		AEAT_KEY_MAP.put("D3", (mod,det) -> {	// D3=TOTAL ADQ. INTRACOM. DE GASTOS
			addBase ( mod, Mod303Key.CT_C36, det);
			addQuota( mod, Mod303Key.CT_C37, det);
		});
		AEAT_KEY_MAP.put("DT", (mod,det) -> {	// DT=TOTAL ADQ. INTRACOM.
			
		});
		AEAT_KEY_MAP.put("RD", (mod,det) -> {	// RD=RECTIFICACION DE DEDUCCIONES
			addBase ( mod, Mod303Key.CT_C40, det);
			addQuota( mod, Mod303Key.CT_C41, det);
		});
		AEAT_KEY_MAP.put("ET", (mod,det) -> {	// ET=COMPENSACION REGIMEN ESPECIAL A,G Y P.
			addQuota( mod, Mod303Key.CT_C42, det);
		});
		AEAT_KEY_MAP.put("RI", (mod,det) -> {	// RI=REGULARIZACION DE INVERSIONES
			addQuota( mod, Mod303Key.CT_C43, det);
		});
		AEAT_KEY_MAP.put("RP", (mod,det) -> {	// RP=REGULARIZACION POR APLICACIÓN DEL PORCENTAJE DEFINITIVO DE PRORRATA
			addQuota( mod, Mod303Key.CT_C44, det);
		});
		AEAT_KEY_MAP.put("FT", (mod,det) -> {	// FT=TOTAL A DEDUCIR
			addQuota( mod, Mod303Key.CT_C45, det);
		});
		AEAT_KEY_MAP.put("DF", (mod,det) -> {	// DF=DIFERENCIA
			addQuota( mod, Mod303Key.CT_C46, det);
			addQuota( mod, Mod303Key.CT_C64, det);
		});
		AEAT_KEY_MAP.put("SP", (mod,det) -> {	// SP=LINEA EN BLANCO
		});
		AEAT_KEY_MAP.put("CP", (mod,det) -> {	// CP=COMPRAS DE BIENES CORRIENTES
		});
		AEAT_KEY_MAP.put("GT", (mod,det) -> {	// GT=GASTOS
		});
		AEAT_KEY_MAP.put("BI", (mod,det) -> {	// BI=BIENES DE INVERSION
		});
		AEAT_KEY_MAP.put("TD", (mod,det) -> {	// TD=TOTAL CUOTA DEDUCIBLE
		});
		AEAT_KEY_MAP.put("SP2", (mod,det) -> {	// SP2=LINEA EN BLANCO
		});
		AEAT_KEY_MAP.put("EI", (mod,det) -> {	// EI=Entregas Intracomunitarias
			addBase( mod, Mod303Key.CT_C59, det);
		});
		AEAT_KEY_MAP.put("EX1", (mod,det) -> {	// EX1=Exportaciones Definitivas
			addBase( mod, Mod303Key.CT_C60, det);
		});
		AEAT_KEY_MAP.put("EX2", (mod,det) -> {	// EX2=Exportaciones Envios Definitivos a Canarias,Ceuta y Melilla
			addBase( mod, Mod303Key.CT_C60, det);
		});
		AEAT_KEY_MAP.put("OO", (mod,det) -> {	// OO=Otras Operaciones no sujetas con derecho a deducción
			addBase( mod, Mod303Key.CT_C61, det);
		});
		AEAT_KEY_MAP.put("OS", (mod,det) -> {	// OS=Otras Op. no sujetas sin drcho. a deducción
			addBase( mod, Mod303Key.CT_C61, det);
		});
		AEAT_KEY_MAP.put("OI", (mod,det) -> {	// OI=Operaciones por inversión de sujet pasivo no incluídas.
			addBase( mod, Mod303Key.CT_C61, det);
		});
		AEAT_KEY_MAP.put("PS", (mod,det) -> {	// PS=Prestaciones intracomunitarias de servicios localizadas fuera del territorio de aplicación del impuesto
			addBase( mod, Mod303Key.CT_C59, det);
		});
		AEAT_KEY_MAP.put("EBI", (mod,det) -> {	// EBI=Importe de entregas de bienes de inversion.
			
		});
		AEAT_KEY_MAP.put("XO", (mod,det) -> {	// XO=Importe de entregas de bienes y servicios Rég. Caja
			addBase( mod, Mod303Key.CT_C62, det);
			addQuota( mod, Mod303Key.CT_C63, det);
		});
		AEAT_KEY_MAP.put("XI", (mod,det) -> {	// XI=Importe de adquisiciones de bienes y servicios Rég. Caja
			addBase( mod, Mod303Key.CT_C74, det);
			addQuota( mod, Mod303Key.CT_C75, det);
		});
	}

	public static final Map<String,BiConsumer<Mod303,VatTaxDetail>> BIZKAIA_KEY_MAP = new HashMap<String,BiConsumer<Mod303,VatTaxDetail>>();
	static {
		BIZKAIA_KEY_MAP.put("A1", (mod,det) -> {	// A1=REGIMEN GENERAL
		});	
		BIZKAIA_KEY_MAP.put("A3", (mod,det) -> {	// A3=ADQUISIONES INTRACOMUNITARIAS DE BIENES
			
		});
		BIZKAIA_KEY_MAP.put("A31", (mod,det) -> {	// A31=ADQUISIONES INTRACOMUNITARIAS DE SERVICIOS
			
		});
		BIZKAIA_KEY_MAP.put("A4", (mod,det) -> {	// A4=INVERSION DE SUJETO PASIVO
			
		});
		BIZKAIA_KEY_MAP.put("A5", (mod,det) -> {	// A5=MODIFICACION BASES Y CUOTAS
			
		});
		BIZKAIA_KEY_MAP.put("A2", (mod,det) -> {	// A2=RECARGO EQUIVALENCIA
			
		});
		BIZKAIA_KEY_MAP.put("A21", (mod,det) -> {	// A21=MODIFICACION BASES Y CUOTAS RECARGO EQUIVALENCIA
			
		});
		BIZKAIA_KEY_MAP.put("AT", (mod,det) -> {	// AT=TOTAL DEVENGADO
			
		});
		BIZKAIA_KEY_MAP.put("B1", (mod,det) -> {	// B1=OP. INTERIORES DE BIENES CORRIENTES
			
		});
		BIZKAIA_KEY_MAP.put("B2", (mod,det) -> {	// B2=OP. INTERIORES DE BIENES DE INVERSION
			
		});
		BIZKAIA_KEY_MAP.put("B3", (mod,det) -> {	// B3=OP. INTERIORES DE GASTOS
			
		});
		BIZKAIA_KEY_MAP.put("BT", (mod,det) -> {	// BT=TOTAL OP. INTERIORES
			
		});
		BIZKAIA_KEY_MAP.put("C1", (mod,det) -> {	// C1=IMPORTACIONES DE BIENES CORRIENTES
			
		});
		BIZKAIA_KEY_MAP.put("C2", (mod,det) -> {	// C2=IMPORTACIONES DE BIENES DE INVERSION
			
		});
		BIZKAIA_KEY_MAP.put("CT", (mod,det) -> {	// CT=TOTAL IMPORTACIONES
			
		});
		BIZKAIA_KEY_MAP.put("D1", (mod,det) -> {	// D1=ADQ. INTRACOM. DE BIENES CORRIENTES
			
		});
		BIZKAIA_KEY_MAP.put("D2", (mod,det) -> {	// D2=ADQ. INTRACOM. DE BIENES DE INVERSION
			
		});
		BIZKAIA_KEY_MAP.put("D3", (mod,det) -> {	// D3=TOTAL ADQ. INTRACOM. DE GASTOS
			
		});
		BIZKAIA_KEY_MAP.put("DT", (mod,det) -> {	// DT=TOTAL ADQ. INTRACOM.
			
		});
		BIZKAIA_KEY_MAP.put("RD", (mod,det) -> {	// RD=RECTIFICACION DE DEDUCCIONES
			
		});
		BIZKAIA_KEY_MAP.put("ET", (mod,det) -> {	// ET=COMPENSACION REGIMEN ESPECIAL A,G Y P.
			
		});
		BIZKAIA_KEY_MAP.put("RI", (mod,det) -> {	// RI=REGULARIZACION DE INVERSIONES
			
		});
		BIZKAIA_KEY_MAP.put("RP", (mod,det) -> {	// RP=REGULARIZACION POR APLICACIÓN DEL PORCENTAJE DEFINITIVO DE PRORRATA
			
		});
		BIZKAIA_KEY_MAP.put("FT", (mod,det) -> {	// FT=TOTAL A DEDUCIR
			
		});
		BIZKAIA_KEY_MAP.put("DF", (mod,det) -> {	// DF=DIFERENCIA
			
		});
		BIZKAIA_KEY_MAP.put("SP", (mod,det) -> {	// SP=LINEA EN BLANCO
		});
		BIZKAIA_KEY_MAP.put("CP", (mod,det) -> {	// CP=COMPRAS DE BIENES CORRIENTES
		});
		BIZKAIA_KEY_MAP.put("GT", (mod,det) -> {	// GT=GASTOS
		});
		BIZKAIA_KEY_MAP.put("BI", (mod,det) -> {	// BI=BIENES DE INVERSION
		});
		BIZKAIA_KEY_MAP.put("TD", (mod,det) -> {	// TD=TOTAL CUOTA DEDUCIBLE
		});
		BIZKAIA_KEY_MAP.put("SP2", (mod,det) -> {	// SP2=LINEA EN BLANCO
		});
		BIZKAIA_KEY_MAP.put("EI", (mod,det) -> {	// EI=Entregas Intracomunitarias
		});
		BIZKAIA_KEY_MAP.put("EX1", (mod,det) -> {	// EX1=Exportaciones Definitivas
		});
		BIZKAIA_KEY_MAP.put("EX2", (mod,det) -> {	// EX2=Exportaciones Envios Definitivos a Canarias,Ceuta y Melilla
		});
		BIZKAIA_KEY_MAP.put("OO", (mod,det) -> {	// OO=Otras Operaciones no sujetas con derecho a deducción
		});
		BIZKAIA_KEY_MAP.put("OS", (mod,det) -> {	// OS=Otras Op. no sujetas sin drcho. a deducción
		});
		BIZKAIA_KEY_MAP.put("OI", (mod,det) -> {	// OI=Operaciones por inversión de sujet pasivo no incluídas.
		});
		BIZKAIA_KEY_MAP.put("PS", (mod,det) -> {	// PS=Prestaciones intracomunitarias de servicios localizadas fuera del territorio de aplicación del impuesto
		});
		BIZKAIA_KEY_MAP.put("EBI", (mod,det) -> {	// EBI=Importe de entregas de bienes de inversion.
		});
		BIZKAIA_KEY_MAP.put("XO", (mod,det) -> {	// XO=Importe de entregas de bienes y servicios Rég. Caja
		});
		BIZKAIA_KEY_MAP.put("XI", (mod,det) -> {	// XI=Importe de adquisiciones de bienes y servicios Rég. Caja
		});
		
	}

	public static final Map<String,BiConsumer<Mod303,VatTaxDetail>> GIPUZKOA_KEY_MAP = new HashMap<String,BiConsumer<Mod303,VatTaxDetail>>();
	static {
		GIPUZKOA_KEY_MAP.put("A1", (mod,det) -> {	// A1=REGIMEN GENERAL
			if ( AonNumberUtils.equals(det.getPercent(), GIPUZKOA_2017_Declaration.PERCENT1 )) {
				addBase ( mod, Mod303Key.GP_C002, det);
				mod.ensureDetail(Mod303Key.GP_X002).setAmount(GIPUZKOA_2017_Declaration.PERCENT1);
				addQuota( mod, Mod303Key.GP_C003, det);
			}
			if ( AonNumberUtils.equals(det.getPercent(), GIPUZKOA_2017_Declaration.PERCENT2 )) {
				addBase ( mod, Mod303Key.GP_C004, det);
				mod.ensureDetail(Mod303Key.GP_X004).setAmount(GIPUZKOA_2017_Declaration.PERCENT2);
				addQuota( mod, Mod303Key.GP_C005, det);
			}
			if ( AonNumberUtils.equals(det.getPercent(), GIPUZKOA_2017_Declaration.PERCENT3 )) {
				addBase ( mod, Mod303Key.GP_C006, det);
				mod.ensureDetail(Mod303Key.GP_X006).setAmount(GIPUZKOA_2017_Declaration.PERCENT3);
				addQuota( mod, Mod303Key.GP_C007, det);
			}
		});	
		GIPUZKOA_KEY_MAP.put("A3", (mod,det) -> {	// A3=ADQUISIONES INTRACOMUNITARIAS DE BIENES
			addBase ( mod, Mod303Key.GP_C014, det);
			addQuota( mod, Mod303Key.GP_C015, det);
		});
		GIPUZKOA_KEY_MAP.put("A31", (mod,det) -> {	// A31=ADQUISIONES INTRACOMUNITARIAS DE SERVICIOS
			addBase ( mod, Mod303Key.GP_C014, det);
			addQuota( mod, Mod303Key.GP_C015, det);
		});
		GIPUZKOA_KEY_MAP.put("A4", (mod,det) -> {	// A4=INVERSION DE SUJETO PASIVO
			addBase ( mod, Mod303Key.GP_C043, det);
			addQuota( mod, Mod303Key.GP_C044, det);
		});
		GIPUZKOA_KEY_MAP.put("A5", (mod,det) -> {	// A5=MODIFICACION BASES Y CUOTAS
			addBase ( mod, Mod303Key.GP_C039, det);
			addQuota( mod, Mod303Key.GP_C040, det);
		});
		GIPUZKOA_KEY_MAP.put("A2", (mod,det) -> {	// A2=RECARGO EQUIVALENCIA
			if ( AonNumberUtils.equals(det.getPercent(), GIPUZKOA_2017_Declaration.SURCHARGE_PERCENT1 )) {
				addBase ( mod, Mod303Key.GP_C008, det);
				mod.ensureDetail(Mod303Key.GP_X008).setAmount(GIPUZKOA_2017_Declaration.SURCHARGE_PERCENT1);
				addQuota( mod, Mod303Key.GP_C009, det);
			}
			if ( AonNumberUtils.equals(det.getPercent(), GIPUZKOA_2017_Declaration.SURCHARGE_PERCENT2 )) {
				addBase ( mod, Mod303Key.GP_C010, det);
				mod.ensureDetail(Mod303Key.GP_X010).setAmount(GIPUZKOA_2017_Declaration.SURCHARGE_PERCENT2);
				addQuota( mod, Mod303Key.GP_C011, det);
			}
			if ( AonNumberUtils.equals(det.getPercent(), GIPUZKOA_2017_Declaration.SURCHARGE_PERCENT3 )) {
				addBase ( mod, Mod303Key.GP_C012, det);
				mod.ensureDetail(Mod303Key.GP_X012).setAmount(GIPUZKOA_2017_Declaration.SURCHARGE_PERCENT3);
				addQuota( mod, Mod303Key.GP_C013, det);
			}
		});
		GIPUZKOA_KEY_MAP.put("A21", (mod,det) -> {	// A21=MODIFICACION BASES Y CUOTAS RECARGO EQUIVALENCIA
			addBase ( mod, Mod303Key.GP_C041, det);
			addQuota( mod, Mod303Key.GP_C042, det);
		});
		GIPUZKOA_KEY_MAP.put("AT", (mod,det) -> {	// AT=TOTAL DEVENGADO
			addQuota( mod, Mod303Key.GP_C016, det);
		});
		
		GIPUZKOA_KEY_MAP.put("B1", (mod,det) -> {	// B1=OP. INTERIORES DE BIENES CORRIENTES
			addBase ( mod, Mod303Key.GP_C017, det);
			addQuota( mod, Mod303Key.GP_C018, det);
		});
		GIPUZKOA_KEY_MAP.put("B2", (mod,det) -> {	// B2=OP. INTERIORES DE BIENES DE INVERSION
			addBase ( mod, Mod303Key.GP_C017, det);
			addQuota( mod, Mod303Key.GP_C018, det);
		});
		GIPUZKOA_KEY_MAP.put("B3", (mod,det) -> {	// B3=OP. INTERIORES DE GASTOS
			addBase ( mod, Mod303Key.GP_C017, det);
			addQuota( mod, Mod303Key.GP_C018, det);
		});
		GIPUZKOA_KEY_MAP.put("BT", (mod,det) -> {	// BT=TOTAL OP. INTERIORES
			
		});
		GIPUZKOA_KEY_MAP.put("C1", (mod,det) -> {	// C1=IMPORTACIONES DE BIENES CORRIENTES
			addBase ( mod, Mod303Key.GP_C019, det);
			addQuota( mod, Mod303Key.GP_C020, det);
		});
		GIPUZKOA_KEY_MAP.put("C2", (mod,det) -> {	// C2=IMPORTACIONES DE BIENES DE INVERSION
			addBase ( mod, Mod303Key.GP_C019, det);
			addQuota( mod, Mod303Key.GP_C020, det);
		});
		GIPUZKOA_KEY_MAP.put("CT", (mod,det) -> {	// CT=TOTAL IMPORTACIONES
		});
		GIPUZKOA_KEY_MAP.put("D1", (mod,det) -> {	// D1=ADQ. INTRACOM. DE BIENES CORRIENTES
			addBase ( mod, Mod303Key.GP_C021, det);
			addQuota( mod, Mod303Key.GP_C022, det);
		});
		GIPUZKOA_KEY_MAP.put("D2", (mod,det) -> {	// D2=ADQ. INTRACOM. DE BIENES DE INVERSION
			addBase ( mod, Mod303Key.GP_C021, det);
			addQuota( mod, Mod303Key.GP_C022, det);
		});
		GIPUZKOA_KEY_MAP.put("D3", (mod,det) -> {	// D3=TOTAL ADQ. INTRACOM. DE GASTOS
			addBase ( mod, Mod303Key.GP_C021, det);
			addQuota( mod, Mod303Key.GP_C022, det);
		});
		GIPUZKOA_KEY_MAP.put("DT", (mod,det) -> {	// DT=TOTAL ADQ. INTRACOM.
			
		});
		GIPUZKOA_KEY_MAP.put("RD", (mod,det) -> {	// RD=RECTIFICACION DE DEDUCCIONES
			addBase ( mod, Mod303Key.GP_C045, det);
			addQuota( mod, Mod303Key.GP_C046, det);
		});
		GIPUZKOA_KEY_MAP.put("ET", (mod,det) -> {	// ET=COMPENSACION REGIMEN ESPECIAL A,G Y P.
			addQuota( mod, Mod303Key.GP_C023, det);
		});
		GIPUZKOA_KEY_MAP.put("RI", (mod,det) -> {	// RI=REGULARIZACION DE INVERSIONES
			addQuota( mod, Mod303Key.GP_C024, det);
		});
		GIPUZKOA_KEY_MAP.put("RP", (mod,det) -> {	// RP=REGULARIZACION POR APLICACIÓN DEL PORCENTAJE DEFINITIVO DE PRORRATA
			
		});
		GIPUZKOA_KEY_MAP.put("FT", (mod,det) -> {	// FT=TOTAL A DEDUCIR
			addQuota( mod, Mod303Key.GP_C025, det);
		});
		
		GIPUZKOA_KEY_MAP.put("DF", (mod,det) -> {	// DF=DIFERENCIA
			addQuota( mod, Mod303Key.GP_C026, det);
		});
		GIPUZKOA_KEY_MAP.put("SP", (mod,det) -> {	// SP=LINEA EN BLANCO
		});
		GIPUZKOA_KEY_MAP.put("CP", (mod,det) -> {	// CP=COMPRAS DE BIENES CORRIENTES
		});
		GIPUZKOA_KEY_MAP.put("GT", (mod,det) -> {	// GT=GASTOS
		});
		GIPUZKOA_KEY_MAP.put("BI", (mod,det) -> {	// BI=BIENES DE INVERSION
		});
		GIPUZKOA_KEY_MAP.put("TD", (mod,det) -> {	// TD=TOTAL CUOTA DEDUCIBLE
		});
		GIPUZKOA_KEY_MAP.put("SP2", (mod,det) -> {	// SP2=LINEA EN BLANCO
		});
		GIPUZKOA_KEY_MAP.put("EI", (mod,det) -> {	// EI=Entregas Intracomunitarias
			addBase( mod, Mod303Key.GP_C030, det);
		});
		GIPUZKOA_KEY_MAP.put("EX1", (mod,det) -> {	// EX1=Exportaciones Definitivas
			addBase( mod, Mod303Key.GP_C031, det);
		});
		GIPUZKOA_KEY_MAP.put("EX2", (mod,det) -> {	// EX2=Exportaciones Envios Definitivos a Canarias,Ceuta y Melilla
			addBase( mod, Mod303Key.GP_C031, det);
		});
		GIPUZKOA_KEY_MAP.put("OO", (mod,det) -> {	// OO=Otras Operaciones no sujetas con derecho a deducción
			addBase( mod, Mod303Key.GP_C032, det);
		});
		GIPUZKOA_KEY_MAP.put("OS", (mod,det) -> {	// OS=Otras Op. no sujetas sin drcho. a deducción
			addBase( mod, Mod303Key.GP_C032, det);
		});
		GIPUZKOA_KEY_MAP.put("OI", (mod,det) -> {	// OI=Operaciones por inversión de sujet pasivo no incluídas.
			addBase( mod, Mod303Key.GP_C032, det);
		});
		GIPUZKOA_KEY_MAP.put("PS", (mod,det) -> {	// PS=Prestaciones intracomunitarias de servicios localizadas fuera del territorio de aplicación del impuesto
			addBase( mod, Mod303Key.GP_C030, det);
		});
		GIPUZKOA_KEY_MAP.put("EBI", (mod,det) -> {	// EBI=Importe de entregas de bienes de inversion.
		});
		GIPUZKOA_KEY_MAP.put("XO", (mod,det) -> {	// XO=Importe de entregas de bienes y servicios Rég. Caja
			addBase( mod, Mod303Key.GP_C047, det);
			addQuota( mod, Mod303Key.GP_C048, det);
		});
		GIPUZKOA_KEY_MAP.put("XI", (mod,det) -> {	// XI=Importe de adquisiciones de bienes y servicios Rég. Caja
			addBase( mod, Mod303Key.GP_C049, det);
			addQuota( mod, Mod303Key.GP_C050, det);
		});
	}

	public static final Map<String,BiConsumer<Mod303,VatTaxDetail>> ARABA_KEY_MAP = new HashMap<String,BiConsumer<Mod303,VatTaxDetail>>();
	static {
		ARABA_KEY_MAP.put("A1", (mod,det) -> {	// A1=REGIMEN GENERAL
			if ( AonNumberUtils.equals(det.getPercent(), ARABA_2017_Declaration.PERCENT1 )) {
				addBase ( mod, Mod303Key.AR_C001, det);
				mod.ensureDetail(Mod303Key.AR_C002).setAmount(ARABA_2017_Declaration.PERCENT1);
				addQuota( mod, Mod303Key.AR_C003, det);
			}
			if ( AonNumberUtils.equals(det.getPercent(), ARABA_2017_Declaration.PERCENT2 )) {
				addBase ( mod, Mod303Key.AR_C204, det);
				mod.ensureDetail(Mod303Key.AR_C205).setAmount(ARABA_2017_Declaration.PERCENT2);
				addQuota( mod, Mod303Key.AR_C206, det);
			}
			if ( AonNumberUtils.equals(det.getPercent(), ARABA_2017_Declaration.PERCENT3 )) {
				addBase ( mod, Mod303Key.AR_C207, det);
				mod.ensureDetail(Mod303Key.AR_C208).setAmount(ARABA_2017_Declaration.PERCENT3);
				addQuota( mod, Mod303Key.AR_C209, det);
			}
			
		});	
		ARABA_KEY_MAP.put("A3", (mod,det) -> {	// A3=ADQUISIONES INTRACOMUNITARIAS DE BIENES
			if ( AonNumberUtils.equals(det.getPercent(), ARABA_2017_Declaration.PERCENT1 )) {
				addBase ( mod, Mod303Key.AR_C019, det);
				mod.ensureDetail(Mod303Key.AR_C020).setAmount(ARABA_2017_Declaration.PERCENT1);
				addQuota( mod, Mod303Key.AR_C021, det);
			}
			if ( AonNumberUtils.equals(det.getPercent(), ARABA_2017_Declaration.PERCENT2 )) {
				addBase ( mod, Mod303Key.AR_C222, det);
				mod.ensureDetail(Mod303Key.AR_C223).setAmount(ARABA_2017_Declaration.PERCENT2);
				addQuota( mod, Mod303Key.AR_C224, det);
			}
			if ( AonNumberUtils.equals(det.getPercent(), ARABA_2017_Declaration.PERCENT3 )) {
				addBase ( mod, Mod303Key.AR_C225, det);
				mod.ensureDetail(Mod303Key.AR_C226).setAmount(ARABA_2017_Declaration.PERCENT3);
				addQuota( mod, Mod303Key.AR_C227, det);
			}
		});
		ARABA_KEY_MAP.put("A31", (mod,det) -> {	// A31=ADQUISIONES INTRACOMUNITARIAS DE SERVICIOS
			if ( AonNumberUtils.equals(det.getPercent(), ARABA_2017_Declaration.PERCENT1 )) {
				addBase ( mod, Mod303Key.AR_C019, det);
				mod.ensureDetail(Mod303Key.AR_C020).setAmount(ARABA_2017_Declaration.PERCENT1);
				addQuota( mod, Mod303Key.AR_C021, det);
			}
			if ( AonNumberUtils.equals(det.getPercent(), ARABA_2017_Declaration.PERCENT2 )) {
				addBase ( mod, Mod303Key.AR_C222, det);
				mod.ensureDetail(Mod303Key.AR_C223).setAmount(ARABA_2017_Declaration.PERCENT2);
				addQuota( mod, Mod303Key.AR_C224, det);
			}
			if ( AonNumberUtils.equals(det.getPercent(), ARABA_2017_Declaration.PERCENT3 )) {
				addBase ( mod, Mod303Key.AR_C225, det);
				mod.ensureDetail(Mod303Key.AR_C226).setAmount(ARABA_2017_Declaration.PERCENT3);
				addQuota( mod, Mod303Key.AR_C227, det);
			}
		});
		ARABA_KEY_MAP.put("A4", (mod,det) -> { 	// A4=INVERSION DE SUJETO PASIVO
			addBase ( mod, Mod303Key.AR_C372, det);
			addQuota( mod, Mod303Key.AR_C373, det);
		});
		ARABA_KEY_MAP.put("A5", (mod,det) -> { 	// A5=MODIFICACION BASES Y CUOTAS
			addBase ( mod, Mod303Key.AR_C370, det);
			addQuota( mod, Mod303Key.AR_C371, det);
		});
		ARABA_KEY_MAP.put("A2", (mod,det) -> { 	// A2=RECARGO EQUIVALENCIA
			if ( AonNumberUtils.equals(det.getPercent(), ARABA_2017_Declaration.SURCHARGE_PERCENT1 )) {
				addBase ( mod, Mod303Key.AR_C010, det);
				mod.ensureDetail(Mod303Key.AR_C011).setAmount(ARABA_2017_Declaration.SURCHARGE_PERCENT1);
				addQuota( mod, Mod303Key.AR_C012, det);
			}
			if ( AonNumberUtils.equals(det.getPercent(), ARABA_2017_Declaration.SURCHARGE_PERCENT2 )) {
				addBase ( mod, Mod303Key.AR_C213, det);
				mod.ensureDetail(Mod303Key.AR_C214).setAmount(ARABA_2017_Declaration.SURCHARGE_PERCENT2);
				addQuota( mod, Mod303Key.AR_C215, det);
			}
			if ( AonNumberUtils.equals(det.getPercent(), ARABA_2017_Declaration.SURCHARGE_PERCENT3 )) {
				addBase ( mod, Mod303Key.AR_C216, det);
				mod.ensureDetail(Mod303Key.AR_C217).setAmount(ARABA_2017_Declaration.SURCHARGE_PERCENT3);
				addQuota( mod, Mod303Key.AR_C218, det);
			}
		});
		ARABA_KEY_MAP.put("A21", (mod,det) -> {	// A21=MODIFICACION BASES Y CUOTAS RECARGO EQUIVALENCIA
			addBase ( mod, Mod303Key.AR_C374, det);
			addQuota( mod, Mod303Key.AR_C375, det);
		});
		ARABA_KEY_MAP.put("AT", (mod,det) -> {// AT=TOTAL DEVENGADO
			addQuota( mod, Mod303Key.AR_C028, det);
		});
		
		
		ARABA_KEY_MAP.put("B1", (mod,det) -> {	// B1=OP. INTERIORES DE BIENES CORRIENTES
			addQuota( mod, Mod303Key.AR_C030, det);
		});
		ARABA_KEY_MAP.put("B2", (mod,det) -> {	// B2=OP. INTERIORES DE BIENES DE INVERSION
			addQuota( mod, Mod303Key.AR_C031, det);
		});
		ARABA_KEY_MAP.put("B3", (mod,det) -> {	// B3=OP. INTERIORES DE GASTOS
			addQuota( mod, Mod303Key.AR_C030, det);
		});
		ARABA_KEY_MAP.put("BT", (mod,det) -> {	// BT=TOTAL OP. INTERIORES
		
		});
		ARABA_KEY_MAP.put("C1", (mod,det) -> {	// C1=IMPORTACIONES DE BIENES CORRIENTES
			addQuota( mod, Mod303Key.AR_C032, det);
		});
		ARABA_KEY_MAP.put("C2", (mod,det) -> {	// C2=IMPORTACIONES DE BIENES DE INVERSION
			addQuota( mod, Mod303Key.AR_C033, det);
		});
		ARABA_KEY_MAP.put("CT", (mod,det) -> {	// CT=TOTAL IMPORTACIONES
			
		});
		ARABA_KEY_MAP.put("D1", (mod,det) -> {	// D1=ADQ. INTRACOM. DE BIENES CORRIENTES
			addQuota( mod, Mod303Key.AR_C034, det);
		});
		ARABA_KEY_MAP.put("D2", (mod,det) -> {	// D2=ADQ. INTRACOM. DE BIENES DE INVERSION
			addQuota( mod, Mod303Key.AR_C035, det);
		});
		ARABA_KEY_MAP.put("D3", (mod,det) -> {	// D3=TOTAL ADQ. INTRACOM. DE GASTOS
			addQuota( mod, Mod303Key.AR_C034, det);
		});
		ARABA_KEY_MAP.put("DT", (mod,det) -> {	// DT=TOTAL ADQ. INTRACOM.
			
		});
		ARABA_KEY_MAP.put("RD", (mod,det) -> {	// RD=RECTIFICACION DE DEDUCCIONES
			
		});
		ARABA_KEY_MAP.put("ET", (mod,det) -> {	// ET=COMPENSACION REGIMEN ESPECIAL A,G Y P.
			addQuota( mod, Mod303Key.AR_C036, det);
		});
		ARABA_KEY_MAP.put("RI", (mod,det) -> {	// RI=REGULARIZACION DE INVERSIONES
			addQuota( mod, Mod303Key.AR_C037, det);
		});
		ARABA_KEY_MAP.put("RP", (mod,det) -> {	// RP=REGULARIZACION POR APLICACIÓN DEL PORCENTAJE DEFINITIVO DE PRORRATA
			
		});
		ARABA_KEY_MAP.put("FT", (mod,det) -> {	// FT=TOTAL A DEDUCIR
			addQuota( mod, Mod303Key.AR_C038, det);
		});
		ARABA_KEY_MAP.put("DF", (mod,det) -> {	// DF=DIFERENCIA
			addQuota( mod, Mod303Key.AR_C039, det);
		});
		ARABA_KEY_MAP.put("SP", (mod,det) -> {	// SP=LINEA EN BLANCO
		});
		ARABA_KEY_MAP.put("CP", (mod,det) -> {	// CP=COMPRAS DE BIENES CORRIENTES
		});
		ARABA_KEY_MAP.put("GT", (mod,det) -> {	// GT=GASTOS
		});
		ARABA_KEY_MAP.put("BI", (mod,det) -> {	// BI=BIENES DE INVERSION
		});
		ARABA_KEY_MAP.put("TD", (mod,det) -> {	// TD=TOTAL CUOTA DEDUCIBLE
		});
		ARABA_KEY_MAP.put("SP2", (mod,det) -> {	// SP2=LINEA EN BLANCO
		});
		ARABA_KEY_MAP.put("EI", (mod,det) -> {	// EI=Entregas Intracomunitarias
			addBase( mod, Mod303Key.AR_C050, det);
		});
		ARABA_KEY_MAP.put("EX1", (mod,det) -> {	// EX1=Exportaciones Definitivas
			addBase( mod, Mod303Key.AR_C051, det);
		});
		ARABA_KEY_MAP.put("EX2", (mod,det) -> {	// EX2=Exportaciones Envios Definitivos a Canarias,Ceuta y Melilla
			addBase( mod, Mod303Key.AR_C051, det);
		});
		ARABA_KEY_MAP.put("OO", (mod,det) -> {	// OO=Otras Operaciones no sujetas con derecho a deducción
			addBase( mod, Mod303Key.AR_C052, det);
		});
		ARABA_KEY_MAP.put("OS", (mod,det) -> {	// OS=Otras Op. no sujetas sin drcho. a deducción
			addBase( mod, Mod303Key.AR_C052, det);
		});
		ARABA_KEY_MAP.put("OI", (mod,det) -> {	// OI=Operaciones por inversión de sujet pasivo no incluídas.
			addBase( mod, Mod303Key.AR_C052, det);
		});
		ARABA_KEY_MAP.put("PS", (mod,det) -> {	// PS=Prestaciones intracomunitarias de servicios localizadas fuera del territorio de aplicación del impuesto
			addBase( mod, Mod303Key.AR_C050, det);
		});
		ARABA_KEY_MAP.put("EBI", (mod,det) -> {	// EBI=Importe de entregas de bienes de inversion.
		});
		ARABA_KEY_MAP.put("XO", (mod,det) -> {	// XO=Importe de entregas de bienes y servicios Rég. Caja
			addBase( mod, Mod303Key.AR_C180, det);
			addQuota( mod, Mod303Key.AR_C181, det);
		});
		ARABA_KEY_MAP.put("XI", (mod,det) -> {	// XI=Importe de adquisiciones de bienes y servicios Rég. Caja
			addBase( mod, Mod303Key.AR_C182, det);
			addQuota( mod, Mod303Key.AR_C183, det);
		});
		
	}
	
	private static void addBase(Mod303 mod, Mod303Key key, VatTaxDetail tax) {
		FiscalModelDetail det = mod.ensureDetail(key);
		if (key.isDiffEnabled()) {
			det.addAccumulatedAmount(tax.getTaxableBaseAccumulated());
			det.addDeclaredAmount(tax.getTaxableBaseDeclared());
			det.addResultAmount(tax.getTaxableBaseResult());
		}
		det.addAdjustAmount(tax.getTaxableBaseAdjust());
		det.addAmount(tax.getTaxableBase());
	}

	private static void addQuota(Mod303 mod, Mod303Key key, VatTaxDetail tax) {
		FiscalModelDetail det = mod.ensureDetail(key);
		if (key.isDiffEnabled()) {
			det.addAccumulatedAmount(tax.getQuotaAccumulated());
			det.addDeclaredAmount(tax.getQuotaDeclared());
			det.addResultAmount(tax.getQuotaResult());
		}
		det.addAdjustAmount(tax.getQuotaAdjust());
		det.addAmount(tax.getQuota());
	}

}



// A1=REGIMEN GENERAL
// A3=ADQUISIONES INTRACOMUNITARIAS DE BIENES
// A31=ADQUISIONES INTRACOMUNITARIAS DE SERVICIOS
// A4=INVERSION DE SUJETO PASIVO
// A5=MODIFICACION BASES Y CUOTAS
// A2=RECARGO EQUIVALENCIA
// A21=MODIFICACION BASES Y CUOTAS RECARGO EQUIVALENCIA
// AT=TOTAL DEVENGADO
// B1=OP. INTERIORES DE BIENES CORRIENTES
// B2=OP. INTERIORES DE BIENES DE INVERSION
// B3=OP. INTERIORES DE GASTOS
// BT=TOTAL OP. INTERIORES
// C1=IMPORTACIONES DE BIENES CORRIENTES
// C2=IMPORTACIONES DE BIENES DE INVERSION
// CT=TOTAL IMPORTACIONES
// D1=ADQ. INTRACOM. DE BIENES CORRIENTES
// D2=ADQ. INTRACOM. DE BIENES DE INVERSION
// D3=TOTAL ADQ. INTRACOM. DE GASTOS
// DT=TOTAL ADQ. INTRACOM.
// RD=RECTIFICACION DE DEDUCCIONES
// ET=COMPENSACION REGIMEN ESPECIAL A,G Y P.
// RI=REGULARIZACION DE INVERSIONES
// RP=REGULARIZACION POR APLICACIÓN DEL PORCENTAJE DEFINITIVO DE PRORRATA
// FT=TOTAL A DEDUCIR
// DF=DIFERENCIA
// SP=LINEA EN BLANCO
// CP=COMPRAS DE BIENES CORRIENTES
// GT=GASTOS
// BI=BIENES DE INVERSION
// TD=TOTAL CUOTA DEDUCIBLE
// SP2=LINEA EN BLANCO
// EI=Entregas Intracomunitarias
// EX1=Exportaciones Definitivas
// EX2=Exportaciones Envios Definitivos a Canarias,Ceuta y Melilla
// OO=Otras Operaciones no sujetas con derecho a deducción
// OS=Otras Op. no sujetas sin drcho. a deducción
// OI=Operaciones por inversión de sujet pasivo no incluídas.
// PS=Prestaciones intracomunitarias de servicios localizadas fuera del territorio de aplicación del impuesto
// EBI=Importe de entregas de bienes de inversion.
// XO=Importe de entregas de bienes y servicios Rég. Caja
// XI=Importe de adquisiciones de bienes y servicios Rég. Caja
