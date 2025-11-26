package com.esferalia.aon.occam.api.model.fiscal.mod425;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.Address;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod4252025 extends Mod390  {

	private static final long serialVersionUID = -7615841313494334951L;

	public static class Mod425Detail implements Serializable {

		private static final long serialVersionUID = -7239795405873915332L;
		
		private Mod4252025DetailKey key;
		private double taxableBase;
		private double percent;
		private double quota;

		public Mod4252025DetailKey getKey() {
			return key;
		}
		public Mod425Detail setKey(Mod4252025DetailKey key) {
			this.key = key;
			return this;
		}
		public double getTaxableBase() {
			return taxableBase;
		}
		public Mod425Detail setTaxableBase(double taxableBase) {
			this.taxableBase = taxableBase;
			return this;
		}
		public double getPercent() {
			return percent;
		}
		public Mod425Detail setPercent(double percent) {
			this.percent = percent;
			return this;
		}
		public double getQuota() {
			return quota;
		}
		public Mod425Detail setQuota(double quota) {
			this.quota = quota;
			return this;
		}
		
		public String getTaxableBaseBox() {
			if (key == Mod4252025DetailKey.C074) {
				return "074";
			} else if (key == Mod4252025DetailKey.C018B) {
				return "016B";
			} else if (key == Mod4252025DetailKey.C066B) {
				return "064B";
			} else if (key == Mod4252025DetailKey.C071 ||
					   key == Mod4252025DetailKey.C073 ||
					   key == Mod4252025DetailKey.C076 ||
					   key == Mod4252025DetailKey.C078 ||
					   key == Mod4252025DetailKey.C081 ||
					   key == Mod4252025DetailKey.C083 ||
					   key == Mod4252025DetailKey.C085 ||
					   key == Mod4252025DetailKey.C087 ||
					   key == Mod4252025DetailKey.C089) {
				return AonNumberUtils.toString(key.getBox() - 1);
			} else {
				return AonNumberUtils.toString(key.getBox() - 2);
			}
		}
		
		public String getQuotaBox() {
			if (key == Mod4252025DetailKey.C018B) {
				return "018B";
			} else if (key == Mod4252025DetailKey.C066B) {
				return "066B";
			} else {
				return AonNumberUtils.toString(key.getBox());	
			}
		}

	}
	
	private String streetInitial;	// Tipo de vía (Calle, Avenida, Plaza, etc.)
	private String streetName; 		// Nombre de la vía
	private String streetNumber; 	// Número
	private String streetStair; 	// Escalera
	private String streetFloor; 	// Piso
	private String streetDoor; 		// Puerta
	private String town;	 		// Municipio 
	private String townCode; 	 	// Código del municipio	
	private String provinceCode;	// Código de Provincia
	private String zip;		 		// Código postal
	
	private boolean confidential;
	private boolean taxRefund;                     	// Registro de devolución mensual en algún período del ejercicio
	private boolean specialRegime; 					// Régimen especial del pequeño empresario o profesional
	private boolean replacementDueInsolvencyState; 	// Declaración sustitutiva por rectificación de cuotas en caso de concurso de acreedores
	private boolean mod415; 						// Está obligado a presentar el modelo 415 por realizar operaciones con terceras personas por importe superior a 3.005,06 euros
	private boolean accrualRegimeTarget; 			// Ha sido destinatario de operaciones a las que se aplica el régimen especial de criterio de caja
	
	private Activity425 mainActivity; 	// Actividades a las que se refiere la declaración - Actividad principal
	private Activity425 activity1; 		// Actividades a las que se refiere la declaración - Otras (1)
	private Activity425 activity2; 		// Actividades a las que se refiere la declaración - Otras (2)
	private Activity425 activity3; 		// Actividades a las que se refiere la declaración - Otras (3) 	 
	private Activity425 activity4; 		// Actividades a las que se refiere la declaración - Otras (4)
	private Activity425 activity5; 		// Actividades a las que se refiere la declaración - Otras (5)
	private String mergedDeclarationDocument; 	// Declaración de sujeto pasivo incluido en autoliquidaciones conjuntas - NIF
	private String mergedDeclarationName; 		// Declaración de sujeto pasivo incluido en autoliquidaciones conjuntas - Razón social
	
	private Address address; // Datos del representante - Personas físicas y Comunidades de bienes
	
	private LegalRepresentative legalRepr1; // Datos del representante - Personas jurídicas (1) 
	private LegalRepresentative legalRepr2; // Datos del representante - Personas jurídicas (2)
	private LegalRepresentative legalRepr3; // Datos del representante - Personas jurídicas (3)
	
	private Map<Mod4252025DetailKey,Mod425Detail> generalRegime; // Operaciones realizadas en régimen general
	
	// POR AHORA SOLO VOY A PONER 2 IGUAL QUE ESTA EN EL 390 DE LA AEAT
	private SimpliedRegimeActivity425 simpRegime1; // Operaciones realizadas en régimen simplificado (1)
	private SimpliedRegimeActivity425 simpRegime2; // Operaciones realizadas en régimen simplificado (2)
//	private SimpliedRegimeActivity425 simpRegime3; // Operaciones realizadas en régimen simplificado (3)
//	private SimpliedRegimeActivity425 simpRegime4; // Operaciones realizadas en régimen simplificado (4)
//	private SimpliedRegimeActivity425 simpRegime5; // Operaciones realizadas en régimen simplificado (5)
//	private SimpliedRegimeActivity425 simpRegime6; // Operaciones realizadas en régimen simplificado (6)
//	private SimpliedRegimeActivity425 simpRegime7; // Operaciones realizadas en régimen simplificado (7)
	
	private double box103; // 103 Total cuota anual derivada del régimen simplificado
	private double box104; // 104 Cuotas devengadas por entregas o transmisiones de activos fijos y por inversión del sujeto pasivo
	private double box105; // 105 Cuotas devengadas por arrendamientos de bienes inmuebles
	private double box106; // 106 Rectificación de cuotas impositivas repercutidas
	private double box107; // 107 Total cuotas
	private double box108; // 108 Cuotas deducibles por adquisición o importación de activos fijos
	private double box109; // 109 Cuotas deducibles por arrendamiento de bienes inmuebles
	private double box110; // 110 Total cuotas deducibles
	private double box111; // 111 Resultado régimen simplificado
	
	private double box112; // 112 Regularización cuotas artículo 22.8.5ª Ley 20/1991
	private double box113; // 113 Suma de resultados
	private double box114; // 114 Cuota de IGIC a compensar del ejercicio anterior
	private double box115; // 115 Resultado de la liquidación anual
	private double box116; // 116 Total de ingresos realizados en las autoliquidaciones por I.G.I.C. del ejercicio
	private double box117; // 117 Total devoluciones mensuales por I.G.I.C. a sujetos pasivos inscritos en el Registro de Devolución Mensual  
	private double box118; // 118 Resultado de la autoliquidación último del año A compensar
	private double box119; // 119 Resultado de la autoliquidación último del año A devolver
	
	// Operaciones específicas
	private double box120; // 120 Operaciones en régimen general
	private double box121; // 121 Operaciones a las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el artículo 18 Ley 20/1991
	private double box122; // 122 Exportaciones definitivas y operaciones asimiladas a la exportación
	private double box123; // 123 Operaciones relativas a áreas exentas
	private double box124; // 124 Operaciones interiores exentas por el artículo 25 de la Ley 19/1994 realizadas por el sujeto pasivo
	private double box125; // 125 Otras operaciones exentas con derecho a deducción
	private double box126; // 126 Operaciones exentas sin derecho a deducción
	private double box127; // 127 Operaciones en régimen simplificado
	private double box128; // 128 Operaciones no sujetas por reglas de localización o con inversión del sujeto pasivo
	private double box129; // 129 Operaciones en régimen especial de la agricultura, ganadería y pesca 
	private double box130; // 130 Operaciones en regímenes especiales de bienes usados, objetos de arte, antigüedades o colección
	private double box131; // 131 Operaciones en régimen especial de agencias de viajes
	private double box132; // 132 Entregas de bienes inmuebles y operaciones financieras no habituales
	private double box133; // 133 Entregas de bienes de inversión para el transmitente
	private double box134; // 134 Total volumen de operaciones
	private double box135; // 135 Importaciones de bienes de inversión exentos por el artículo 25 de la Ley 19/1994
	private double box136; // 136 Cuotas de I.G.I.C. soportado no deducible
	private double box137; // 137 Otras operaciones no sujetas con derecho a deducción (artículo 29.4.1ªg) Ley 20/1991)
	
	// Exclusivamente para aquellos sujetos pasivos acogidos al régimen especial de criterio de caja y para aquellos	que sean destinatarios de operaciones afectadas por el mismo
	private double box138; // 138 Importes de las entregas de bienes y prestaciones de servicios a las que Base Cuota habiéndoles aplicado el régimen especial de criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art. 18 de la Ley 20/1991 - Base
	private double box139; // 139 Importes de las entregas de bienes y prestaciones de servicios a las que Base Cuota habiéndoles aplicado el régimen especial de criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art. 18 de la Ley 20/1991 - Cuota
	private double box140; // 140 Importes de las adquisiciones de bienes y servicios a las que sea de aplicación o afecte el régimen especial del criterio de caja conforme a la regla general de devengo contenida en el art. 18 de la Ley 20/1991 - Base
	private double box141; // 141 Importes de las adquisiciones de bienes y servicios a las que sea de aplicación o afecte el régimen especial del criterio de caja conforme a la regla general de devengo contenida en el art. 18 de la Ley 20/1991 - Cuota

	// Declaración informativa del volumen de operaciones en el régimen especial del pequeño empresario o profesional
	private double box142; // 142 Importe de operaciones habituales u ocasionales sujetas al IGIC exentas por Régimen especial del pequeño empresario o profesional
	private double box143; // 143 Importe de operaciones sujetas al IGIC exentas por Régimen especial del comerciante minorista
	private double box144; // 144 Importe de entregas de bienes y prestaciones de servicios no sujetas al IGIC imputables a la sede de la actividad económica situada en Canarias
	private double box145; // 145 Importe de entregas de bienes y prestaciones de servicios no sujetas al IGIC imputables a otras sedes o establecimientos situados fuera de Canarias
	private double box146; // 146 Importe en el supuesto de transmisión de la totalidad o parte del patrimonio empresarial o profesional
	private double box147; // 147 Total volumen de operaciones en el REPEP

	public String getStreetInitial() {
		return streetInitial;
	}
	public void setStreetInitial(String streetInitial) {
		this.streetInitial = streetInitial;
	}
	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	public String getStreetNumber() {
		return streetNumber;
	}
	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}
	public String getStreetStair() {
		return streetStair;
	}
	public void setStreetStair(String streetStair) {
		this.streetStair = streetStair;
	}
	public String getStreetFloor() {
		return streetFloor;
	}
	public void setStreetFloor(String streetFloor) {
		this.streetFloor = streetFloor;
	}
	public String getStreetDoor() {
		return streetDoor;
	}
	public void setStreetDoor(String streetDoor) {
		this.streetDoor = streetDoor;
	}
	public String getTown() {
		return town;
	}
	public void setTown(String town) {
		this.town = town;
	}
	public String getTownCode() {
		return townCode;
	}
	public Mod4252025 setTownCode(String townCode) {
		this.townCode = townCode;
		return this;
	}	
	public String getProvinceCode() {
		return provinceCode;
	}
	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	public boolean isConfidential() {
		return confidential;
	}
	public void setConfidential(boolean confidential) {
		this.confidential = confidential;
	}
	public boolean isReplacementDueInsolvencyState() {
		return replacementDueInsolvencyState;
	}
	public void setReplacementDueInsolvencyState(boolean replacementDueInsolvencyState) {
		this.replacementDueInsolvencyState = replacementDueInsolvencyState;
	}
	public boolean isSpecialRegime() {
		return specialRegime;
	}
	public void setSpecialRegime(boolean specialRegime) {
		this.specialRegime = specialRegime;
	}
	
	public boolean isAccrualRegimeTarget() {
		return accrualRegimeTarget;
	}
	public void setAccrualRegimeTarget(boolean accrualRegimeTarget) {
		this.accrualRegimeTarget = accrualRegimeTarget;
	}
	public boolean isTaxRefund() {
		return taxRefund;
	}
	public void setTaxRefund(boolean taxRefund) {
		this.taxRefund = taxRefund;
	}
	public boolean isMod415() {
		return mod415;
	}
	public void setMod415(boolean mod415) {
		this.mod415 = mod415;
	}
	public String getMergedDeclarationDocument() {
		return mergedDeclarationDocument;
	}
	public void setMergedDeclarationDocument(String mergedDeclarationDocument) {
		this.mergedDeclarationDocument = mergedDeclarationDocument;
	}
	public String getMergedDeclarationName() {
		return mergedDeclarationName;
	}
	public void setMergedDeclarationName(String mergedDeclarationName) {
		this.mergedDeclarationName = mergedDeclarationName;
	}
	public Activity425 getMainActivity() {
		if (mainActivity == null) {
			setMainActivity(new Activity425());
		}
		return mainActivity;
	}
	public void setMainActivity(Activity425 mainActivity) {
		this.mainActivity = mainActivity;
	}
	public Activity425 getActivity1() {
		if (activity1 == null) {
			setActivity1(new Activity425());
		}
		return activity1;
	}
	public void setActivity1(Activity425 activity1) {
		this.activity1 = activity1;
	}
	public Activity425 getActivity2() {
		if (activity2 == null) {
			setActivity2(new Activity425());
		}
		return activity2;
	}
	public void setActivity2(Activity425 activity2) {
		this.activity2 = activity2;
	}
	public Activity425 getActivity3() {
		if (activity3 == null) {
			setActivity3(new Activity425());
		}
		return activity3;
	}
	public void setActivity3(Activity425 activity3) {
		this.activity3 = activity3;
	}
	public Activity425 getActivity4() {
		if (activity4 == null) {
			setActivity4(new Activity425());
		}
		return activity4;
	}
	public void setActivity4(Activity425 activity4) {
		this.activity4 = activity4;
	}
	public Activity425 getActivity5() {
		if (activity5 == null) {
			setActivity5(new Activity425());
		}
		return activity5;
	}
	public void setActivity5(Activity425 activity5) {
		this.activity5 = activity5;
	}
	public Address getAddress() {
		return address;
	}
	public Address ensureAddress() {
		if (address == null) setAddress(new Address());
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public LegalRepresentative getLegalRepr1() {
		return legalRepr1;
	}
	public LegalRepresentative ensureLegalRepr1() {
		if (legalRepr1 == null) setLegalRepr1(new LegalRepresentative());
		return legalRepr1;
	}
	public void setLegalRepr1(LegalRepresentative legalRepr1) {
		this.legalRepr1 = legalRepr1;
	}
	public LegalRepresentative getLegalRepr2() {
		return legalRepr2;
	}
	public LegalRepresentative ensureLegalRepr2() {
		if (legalRepr2 == null) setLegalRepr2(new LegalRepresentative());
		return legalRepr2;
	}
	public void setLegalRepr2(LegalRepresentative legalRepr2) {
		this.legalRepr2 = legalRepr2;
	}
	public LegalRepresentative getLegalRepr3() {
		return legalRepr3;
	}
	public LegalRepresentative ensureLegalRepr3() {
		if (legalRepr3 == null) setLegalRepr3(new LegalRepresentative());
		return legalRepr3;
	}
	public void setLegalRepr3(LegalRepresentative legalRepr3) {
		this.legalRepr3 = legalRepr3;
	}
	public Map<Mod4252025DetailKey, Mod425Detail> getGeneralRegime() {
		if (generalRegime == null) {
			generalRegime = new EnumMap<>(Mod4252025DetailKey.class);
		}
		return generalRegime;
	}
	public void setGeneralRegime(Map<Mod4252025DetailKey, Mod425Detail> generalRegime) {
		this.generalRegime = generalRegime;
	}
	public SimpliedRegimeActivity425 getSimpRegime1() {
		return simpRegime1;
	}
	public void setSimpRegime1(SimpliedRegimeActivity425 simpRegime1) {
		this.simpRegime1 = simpRegime1;
	}
	public SimpliedRegimeActivity425 getSimpRegime2() {
		return simpRegime2;
	}
	public void setSimpRegime2(SimpliedRegimeActivity425 simpRegime2) {
		this.simpRegime2 = simpRegime2;
	}
	
	public boolean isSimplifiedRegime() {
		return (
				(getSimpRegime1() != null && AonStringUtils.isNotEmpty(getSimpRegime1().getEpigrafe()))
			||  (getSimpRegime2() != null && AonStringUtils.isNotEmpty(getSimpRegime2().getEpigrafe()))
//			||  (getSimpRegime3() != null && AonStringUtils.isNotEmpty(getSimpRegime3().getEpigrafe()))
//			||  (getSimpRegime4() != null && AonStringUtils.isNotEmpty(getSimpRegime4().getEpigrafe()))
//			||  (getSimpRegime5() != null && AonStringUtils.isNotEmpty(getSimpRegime5().getEpigrafe()))
//			||  (getSimpRegime6() != null && AonStringUtils.isNotEmpty(getSimpRegime6().getEpigrafe()))
//			||  (getSimpRegime7() != null && AonStringUtils.isNotEmpty(getSimpRegime7().getEpigrafe()))
				); 
	}
	
	public double getBox103() {
		return box103;
	}
	public void setBox103(double box103) {
		this.box103 = box103;
	}
	public double getBox104() {
		return box104;
	}
	public void setBox104(double box104) {
		this.box104 = box104;
	}
	public double getBox105() {
		return box105;
	}
	public void setBox105(double box105) {
		this.box105 = box105;
	}
	public double getBox106() {
		return box106;
	}
	public void setBox106(double box106) {
		this.box106 = box106;
	}
	public double getBox107() {
		return box107;
	}
	public void setBox107(double box107) {
		this.box107 = box107;
	}
	public double getBox108() {
		return box108;
	}
	public void setBox108(double box108) {
		this.box108 = box108;
	}
	public double getBox109() {
		return box109;
	}
	public void setBox109(double box109) {
		this.box109 = box109;
	}
	public double getBox110() {
		return box110;
	}
	public void setBox110(double box110) {
		this.box110 = box110;
	}
	public double getBox111() {
		return box111;
	}
	public void setBox111(double box111) {
		this.box111 = box111;
	}
	public double getBox112() {
		return box112;
	}
	public void setBox112(double box112) {
		this.box112 = box112;
	}
	public double getBox113() {
		return box113;
	}
	public void setBox113(double box113) {
		this.box113 = box113;
	}
	public double getBox114() {
		return box114;
	}
	public void setBox114(double box114) {
		this.box114 = box114;
	}
	public double getBox115() {
		return box115;
	}
	public void setBox115(double box115) {
		this.box115 = box115;
	}
	public double getBox116() {
		return box116;
	}
	public void setBox116(double box116) {
		this.box116 = box116;
	}
	public double getBox117() {
		return box117;
	}
	public void setBox117(double box117) {
		this.box117 = box117;
	}
	public double getBox118() {
		return box118;
	}
	public void setBox118(double box118) {
		this.box118 = box118;
	}
	public double getBox119() {
		return box119;
	}
	public void setBox119(double box119) {
		this.box119 = box119;
	}
	public double getBox120() {
		return box120;
	}
	public void setBox120(double box120) {
		this.box120 = box120;
	}
	public double getBox121() {
		return box121;
	}
	public void setBox121(double box121) {
		this.box121 = box121;
	}
	public double getBox122() {
		return box122;
	}
	public void setBox122(double box122) {
		this.box122 = box122;
	}
	public double getBox123() {
		return box123;
	}
	public void setBox123(double box123) {
		this.box123 = box123;
	}
	public double getBox124() {
		return box124;
	}
	public void setBox124(double box124) {
		this.box124 = box124;
	}
	public double getBox125() {
		return box125;
	}
	public void setBox125(double box125) {
		this.box125 = box125;
	}
	public double getBox126() {
		return box126;
	}
	public void setBox126(double box126) {
		this.box126 = box126;
	}
	public double getBox127() {
		return box127;
	}
	public void setBox127(double box127) {
		this.box127 = box127;
	}
	public double getBox128() {
		return box128;
	}
	public void setBox128(double box128) {
		this.box128 = box128;
	}
	public double getBox129() {
		return box129;
	}
	public void setBox129(double box129) {
		this.box129 = box129;
	}
	public double getBox130() {
		return box130;
	}
	public void setBox130(double box130) {
		this.box130 = box130;
	}
	public double getBox131() {
		return box131;
	}
	public void setBox131(double box131) {
		this.box131 = box131;
	}
	public double getBox132() {
		return box132;
	}
	public void setBox132(double box132) {
		this.box132 = box132;
	}
	public double getBox133() {
		return box133;
	}
	public void setBox133(double box133) {
		this.box133 = box133;
	}
	public double getBox134() {
		return box134;
	}
	public void setBox134(double box134) {
		this.box134 = box134;
	}
	public double getBox135() {
		return box135;
	}
	public void setBox135(double box135) {
		this.box135 = box135;
	}
	public double getBox136() {
		return box136;
	}
	public void setBox136(double box136) {
		this.box136 = box136;
	}
	public double getBox137() {
		return box137;
	}
	public void setBox137(double box137) {
		this.box137 = box137;
	}
	public double getBox138() {
		return box138;
	}
	public void setBox138(double box138) {
		this.box138 = box138;
	}
	public double getBox139() {
		return box139;
	}
	public void setBox139(double box139) {
		this.box139 = box139;
	}
	public double getBox140() {
		return box140;
	}
	public void setBox140(double box140) {
		this.box140 = box140;
	}
	public double getBox141() {
		return box141;
	}
	public void setBox141(double box141) {
		this.box141 = box141;
	}
	public double getBox142() {
		return box142;
	}
	public void setBox142(double box142) {
		this.box142 = box142;
	}
	public double getBox143() {
		return box143;
	}
	public void setBox143(double box143) {
		this.box143 = box143;
	}
	public double getBox144() {
		return box144;
	}
	public void setBox144(double box144) {
		this.box144 = box144;
	}
	public double getBox145() {
		return box145;
	}
	public void setBox145(double box145) {
		this.box145 = box145;
	}
	public double getBox146() {
		return box146;
	}
	public void setBox146(double box146) {
		this.box146 = box146;
	}
	public double getBox147() {
		return box147;
	}
	public void setBox147(double box147) {
		this.box147 = box147;
	}	
	
	//	Casilla 74 Total bases IGIC: consigne el importe de la operación aritmética para obtener el Total
	//	bases IGIC: 01+04+07+10+13+16+16bis+19+22+25+28+31+34+37+40+43+46+49+52+55+58+61+64+64bis+67+70-72.	
	private static final Mod4252025DetailKey[] C074_FORMULA_ADD = {
		 Mod4252025DetailKey.C003
		,Mod4252025DetailKey.C006
		,Mod4252025DetailKey.C009
		,Mod4252025DetailKey.C012
		,Mod4252025DetailKey.C015
		,Mod4252025DetailKey.C018
		,Mod4252025DetailKey.C018B
		,Mod4252025DetailKey.C021
		,Mod4252025DetailKey.C024
		,Mod4252025DetailKey.C027
		,Mod4252025DetailKey.C030
		,Mod4252025DetailKey.C033
		,Mod4252025DetailKey.C036
		,Mod4252025DetailKey.C039
		,Mod4252025DetailKey.C042
		,Mod4252025DetailKey.C045
		,Mod4252025DetailKey.C048
		,Mod4252025DetailKey.C051
		,Mod4252025DetailKey.C054
		,Mod4252025DetailKey.C057
		,Mod4252025DetailKey.C060
		,Mod4252025DetailKey.C063
		,Mod4252025DetailKey.C066
		,Mod4252025DetailKey.C066B
		,Mod4252025DetailKey.C069
		,Mod4252025DetailKey.C071			
	};
	private static final Mod4252025DetailKey[] C074_FORMULA_SUBTRACT = {
		Mod4252025DetailKey.C073			
	};
	
	//	Casilla 79 Total cuotas devengadas: consigne el importe de la operación aritmética para obtener
	//	el Total de cuotas devengadas: 03+06+09+12+15+18+18bis+21+24+27+30+33+36+39+42+45+48+51+54+57+60+63+66+66bis+69+71-73+76-78
	private static final Mod4252025DetailKey[] C079_FORMULA_ADD = {
		 Mod4252025DetailKey.C003
		,Mod4252025DetailKey.C006
		,Mod4252025DetailKey.C009
		,Mod4252025DetailKey.C012
		,Mod4252025DetailKey.C015
		,Mod4252025DetailKey.C018
		,Mod4252025DetailKey.C018B
		,Mod4252025DetailKey.C021
		,Mod4252025DetailKey.C024
		,Mod4252025DetailKey.C027
		,Mod4252025DetailKey.C030
		,Mod4252025DetailKey.C033
		,Mod4252025DetailKey.C036
		,Mod4252025DetailKey.C039
		,Mod4252025DetailKey.C042
		,Mod4252025DetailKey.C045
		,Mod4252025DetailKey.C048
		,Mod4252025DetailKey.C051
		,Mod4252025DetailKey.C054
		,Mod4252025DetailKey.C057
		,Mod4252025DetailKey.C060
		,Mod4252025DetailKey.C063
		,Mod4252025DetailKey.C066
		,Mod4252025DetailKey.C066B
		,Mod4252025DetailKey.C069
		,Mod4252025DetailKey.C071
		,Mod4252025DetailKey.C076
	};
	private static final Mod4252025DetailKey[] C079_FORMULA_SUBTRACT = {
		 Mod4252025DetailKey.C073
		,Mod4252025DetailKey.C078
	};
	
	//	Casilla 94 Total cuotas deducibles: consigne el importe resultante de la siguiente operación
	//	aritmética con los importes de las casillas que se indican: 81+ 83 +85 +87+ 89+ 90+ 91+ 92+ 93
	private static final Mod4252025DetailKey[] C094_FORMULA_ADD = {
		 Mod4252025DetailKey.C081
		,Mod4252025DetailKey.C083
		,Mod4252025DetailKey.C085
		,Mod4252025DetailKey.C087
		,Mod4252025DetailKey.C089
		,Mod4252025DetailKey.C090
		,Mod4252025DetailKey.C091
		,Mod4252025DetailKey.C092
		,Mod4252025DetailKey.C093
	};
	
	//	Casilla 95 Resultado régimen general: consigne, con el signo que proceda, la diferencia entre
	//	el Total cuotas devengadas (casilla 79) y el Total cuotas deducibles (casilla 94), es decir, (79-94).
	private static final Mod4252025DetailKey[] C095_FORMULA_ADD = {
		Mod4252025DetailKey.C079
	};
	private static final Mod4252025DetailKey[] C095_FORMULA_SUBTRACT = {		 
		Mod4252025DetailKey.C094
	};
	
	//	Casilla 103 Total cuota anual derivada del régimen simplificado: consigne el importe resultante
	//	de la siguiente operación aritmética para obtener el Total cuota anual derivada del régimen
	//	simplificado: 96+97+98+99+100+101+102.
		
	//	Casilla 107 Total cuotas: consigne el importe resultante de la siguiente operación aritmética para
	//	obtener el Total de cuotas: 103+104+105+106.	
		
	//	Casilla 110 Total cuotas deducibles: consigne el importe resultante de la siguiente operación
	//	aritmética para obtener el Total cuotas deducibles: 108+109.
		
	//	Casilla 111 Resultado régimen simplificado: consigne, con el signo que proceda, el importe
	//	resultante de la siguiente operación aritmética para obtener el Resultado régimen simplificado:	107-110.	
		
	//	Casilla 113 Suma de resultados: consigne, con el signo que corresponda, el importe resultante de
	//	la siguiente operación aritmética para obtener la Suma de resultados: 95+111.
		
	//	Casilla 115 Resultado de la liquidación anual: consigne, con el signo que corresponda, el importe
	//	resultante de la siguiente operación aritmética para obtener el Resultado de la liquidación
	//	anual: 112+113-114.	
		
	//	Casilla 134 Total volumen de operaciones: consigne el importe resultante de la siguiente
	//	operación aritmética para obtener el Total volumen de operaciones: 120+121+122+123+124+125+126+127+128+129+130+131-132-133.	
		
	//	Casilla 147: Total volumen de operaciones en el REPEP: consigne el importe resultante de la
	//	siguiente operación aritmética para obtener el Total volumen de operaciones en el REPEP: 142+143+144+145+146.

	public void calculate() {
		calculate(Mod4252025DetailKey.C074, C074_FORMULA_ADD, C074_FORMULA_SUBTRACT);  	// Casilla 74 Total bases IGIC: 01+04+07+10+13+16+16bis+19+22+25+28+31+34+37+40+43+46+49+52+55+58+61+64+64bis+67+70-72.
		calculate(Mod4252025DetailKey.C079, C079_FORMULA_ADD, C079_FORMULA_SUBTRACT);  	// Casilla 79 Total cuotas devengadas: 03+06+09+12+15+18+18bis+21+24+27+30+33+36+39+42+45+48+51+54+57+60+63+66+66bis+69+71-73+76-78
		calculate(Mod4252025DetailKey.C094, C094_FORMULA_ADD); 							// Casilla 94 Total cuotas deducibles: 81+ 83 +85 +87+ 89+ 90+ 91+ 92+ 93
		calculate(Mod4252025DetailKey.C095, C095_FORMULA_ADD, C095_FORMULA_SUBTRACT);	// Casilla 95 Resultado régimen general: 79-94.
		box103 = calculateRS(simpRegime1, simpRegime2);  								// Casilla 103 Total cuota anual derivada del régimen simplificado: 96+97+98+99+100+101+102.
        box107 = AonMathUtils.round(box103+box104+box105+box106);     					// Casilla 107 Total cuotas: 103+104+105+106.	
        box110 = AonMathUtils.round(box108+box109);  									// Casilla 110 Total cuotas deducibles: 108+109.
        box111 = AonMathUtils.round(box107-box110); 									// Casilla 111 Resultado régimen simplificado: 107-110.	
        box113 = AonMathUtils.round(getGeneralRegime().get(Mod4252025DetailKey.C095).getQuota()+box111); // Casilla 113 Suma de resultados: 95+111.
        box115 = AonMathUtils.round(box112+box113-box114); 								// Casilla 115 Resultado de la liquidación anual: 112+113-114.	
        box134 = AonMathUtils.round(box120+box121+box122+box123+box124+box125+box126+box127+box128+box129+box130+box131-box132-box133); // Casilla 134 Total volumen de operaciones: 120+121+122+123+124+125+126+127+128+129+130+131-132-133.	
        box147 = AonMathUtils.round(box142+box143+box144+box145+box146); 				// Casilla 147: Total volumen de operaciones en el REPEP: 142+143+144+145+146.
	}
	
	private double calculateRS(SimpliedRegimeActivity425... simpRegimes) {
		double total = 0.0;
		for (SimpliedRegimeActivity425 simpRegime : simpRegimes) {
			if (simpRegime != null) {
				
				// D) Diferencia: consigne la diferencia entre el importe de la casilla A y el de la casilla B.
				// Exclusivamente para el supuesto de actividades de temporada, esta diferencia se multiplicará por
				// el importe consignado en la casilla C.
				simpRegime.setBoxD(AonMathUtils.round(simpRegime.getBoxA()-simpRegime.getBoxB()));
				if (AonMathUtils.isNotZero(simpRegime.getBoxC())) {
					simpRegime.setBoxD(AonMathUtils.round(simpRegime.getBoxD() * simpRegime.getBoxC()));
				}
				
				// F) Cuota mínima: consigne el resultado de aplicar el porcentaje que se ha expresado en la casilla
				// E por el importe consignado en la casilla A. Exclusivamente para el supuesto de actividades de
				// temporada este resultado se multiplicará por el índice consignado en la casilla C.
				simpRegime.setBoxF(AonMathUtils.round(simpRegime.getBoxA() * simpRegime.getBoxE() / 100.0));
				if (AonMathUtils.isNotZero(simpRegime.getBoxC())) {
					simpRegime.setBoxF(AonMathUtils.round(simpRegime.getBoxF() * simpRegime.getBoxC()));
				}
				
				// G) Cuota anual derivada del régimen simplificado (casillas 96 a 102): consigne por fila el
				// importe mayor de los consignados en las casillas D o F.
				simpRegime.setBoxG(AonMathUtils.round(Math.max(simpRegime.getBoxD(), simpRegime.getBoxF())));
				
				// Acumular total	
				total += simpRegime.getBoxG();
			}
		}
		return AonMathUtils.round(total);
	
	}
	public Mod425Detail ensure(Mod4252025DetailKey key) {
		Mod425Detail detail = getGeneralRegime().get(key);
		if (detail == null) {
			detail = new Mod425Detail();
			detail.setKey(key);
			detail.setPercent(key.getPercent());
			getGeneralRegime().put(key, detail);
		}
		return detail;
	}
	
	private Mod425Detail calculate(Mod4252025DetailKey key, Mod4252025DetailKey[] addKeys, Mod4252025DetailKey... subtractKeys) {
		Mod425Detail detail = ensure(key);
		detail.setTaxableBase(0.0);
		detail.setQuota(0.0);
		for (Mod4252025DetailKey k : addKeys) {
			Mod425Detail det = ensure(k);
			detail.setTaxableBase( AonMathUtils.round(detail.getTaxableBase() + det.getTaxableBase()));
			detail.setQuota( AonMathUtils.round(detail.getQuota() + det.getQuota()));
		}
		for (Mod4252025DetailKey k : subtractKeys) {
			Mod425Detail det = ensure(k);
			detail.setTaxableBase( AonMathUtils.round(detail.getTaxableBase() - det.getTaxableBase()));
			detail.setQuota( AonMathUtils.round(detail.getQuota() - det.getQuota()));
		}
		return detail;
	}
	
}

