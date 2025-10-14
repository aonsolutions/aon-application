package com.esferalia.aon.occam.api.model.fiscal.mod425;

import java.io.Serializable;
import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.Address;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.SimpliedRegimeActivity;
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

		public int getTaxableBaseBox() {
			// Unica excepcion en el modelo.	
//			if (key == Mod4252025DetailKey.C0062) return 639;
			return (key.getBox() - 1);
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

		public int getBox() {
			return key.getBox();
		}

		public double getQuota() {
			return quota;
		}

		public Mod425Detail setQuota(double quota) {
			this.quota = quota;
			return this;
		}

	}
	
	private boolean confidential;
	private boolean taxRefund; // Registro de devolución mensual en algún período del ejercicio
	private boolean specialRegime; // FALTA - Régimen especial del pequeño empresario o profesional
	private boolean replacementDueInsolvencyState; // Declaración sustitutiva por rectificación de cuotas en caso de concurso de acreedores
	private boolean mod347; // Está obligado a presentar el modelo 415 por realizar operaciones con terceras personas por importe superior a 3.005,06 euros
	
//	private boolean insolvencyDeclarations;
//	private boolean insolvencyStateThisYear;
//	private boolean insolvencyStateLastPeriod;
//	private boolean accrualRegime;
	private boolean accrualRegimeTarget; // Ha sido destinatario de operaciones a las que se aplica el régimen especial de criterio de caja
//	private boolean specialGroupRegime; 
//	private String groupNumber; 
//	private boolean groupDependent; 
//	private boolean groupRegimeType; 
//	private String groupDocument;
//	private boolean groupDeclarations;
//	
	// FALTA - CADA ACTIVIDAD TIENEN MAS DATOS QUE LAS QUE LLEVA EL 390 AEAT
	private Activity425 mainActivity; // Actividades a las que se refiere la declaración - Actividad principal
	private Activity425 activity1; // Actividades a las que se refiere la declaración - Otras
	private Activity425 activity2; // Actividades a las que se refiere la declaración - Otras
	private Activity425 activity3; // Actividades a las que se refiere la declaración - Otras
	private Activity425 activity4; // Actividades a las que se refiere la declaración - Otras
	private Activity425 activity5; // Actividades a las que se refiere la declaración - Otras // FALTA - ESTA NO ESTA EN EL IMPRESO PERO IGUAL LA SOPORTA EL FICHERO
	private String mergedDeclarationDocument; // Declaración de sujeto pasivo incluido en autoliquidaciones conjuntas - NIF
	private String mergedDeclarationName; // Declaración de sujeto pasivo incluido en autoliquidaciones conjuntas - Razón social
	
	private Address address; // Datos del representante - Personas físicas y Comunidades de bienes
	
	private LegalRepresentative legalRepr1; // Datos del representante - Personas jurídicas 
	private LegalRepresentative legalRepr2; // Datos del representante - Personas jurídicas
	private LegalRepresentative legalRepr3; // Datos del representante - Personas jurídicas
	
	// FALTA - EL MODELO 425 TIENEN OTRAS CLAVES EN LAS CASILLAS
	private Map<Mod4252025DetailKey,Mod425Detail> generalRegime; // Operaciones realizadas en régimen general

	// FALTA - REVISAR CASILLAS DE CADA ACTIVIDAD EN REGIMEN SIMPLIFICADO TIENEN ALGUNAS DISTINTAS DEL 390 DE LA AEAT
	private SimpliedRegimeActivity simpRegime1; // Operaciones realizadas en régimen simplificado (1)
	private SimpliedRegimeActivity simpRegime2; // Operaciones realizadas en régimen simplificado (2)
	private SimpliedRegimeActivity simpRegime3; // Operaciones realizadas en régimen simplificado (3)
	private SimpliedRegimeActivity simpRegime4; // Operaciones realizadas en régimen simplificado (4)
	private SimpliedRegimeActivity simpRegime5; // Operaciones realizadas en régimen simplificado (5)
	private SimpliedRegimeActivity simpRegime6; // Operaciones realizadas en régimen simplificado (6)
	private SimpliedRegimeActivity simpRegime7; // Operaciones realizadas en régimen simplificado (7)
	
//	private FarmerRegimeActivity farmerRegime1;
//	private FarmerRegimeActivity farmerRegime2;
//	private FarmerRegimeActivity farmerRegime3;
//	private FarmerRegimeActivity farmerRegime4;
//	private FarmerRegimeActivity farmerRegime5;
//	
	private double box103; // 103 Total cuota anual derivada del régimen simplificado

	private double box104; // 104 Cuotas devengadas por entregas o transmisiones de activos fijos y por inversión del sujeto pasivo
	private double box105; // 105 Cuotas devengadas por arrendamientos de bienes inmuebles
	private double box106; // 106 Rectificación de cuotas impositivas repercutidas
	private double box107; // 107 Total cuotas
	private double box108; // 108 Cuotas deducibles por adquisición o importación de activos fijos
	private double box109; // 109 Cuotas deducibles por arrendamiento de bienes inmuebles
	private double box110; // 110 Total cuotas deducibles
	private double box111; // 111 Resultado régimen simplificado
	
//	private double box83;  
	
	private double box112; // 112 Regularización cuotas artículo 22.8.5ª Ley 20/1991
	private double box113; // 113 Suma de resultados
	private double box114;  // 114 Cuota de IGIC a compensar del ejercicio anterior
	private double box115;  // 115 Resultado de la liquidación anual
	private double box116;  // 116 Total de ingresos realizados en las autoliquidaciones por I.G.I.C. del ejercicio
	private double box117;  // 117 Total devoluciones mensuales por I.G.I.C. a sujetos pasivos inscritos en el Registro de Devolución Mensual  
	private double box118;  // 118 Resultado de la autoliquidación último del año A compensar
	private double box119;  // 119 Resultado de la autoliquidación último del año A devolver
	
//	private double box90;
//	private double box91;
//	private double box92;
//	private double box93;
//	private double box94;
//	private double box95;
//	private double box96;
//	private double box524;
//	private double box97;
//	private double box98;
//	private double box662;
//	private double box525;
//	private double box526;

	// Operaciones esecíficas
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
	
//	Exclusivamente para aquellos sujetos pasivos acogidos al régimen especial de criterio de caja y para aquellos	que sean destinatarios de operaciones afectadas por el mismo
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

//	private double box657;
//	
//	private LinkedList<Prorrata> prorratas = new LinkedList<>();
//	
//	private DeductionRegime regime1;
//	private DeductionRegime regime2;
//	private DeductionRegime regime3;
	
	public boolean isConfidential() {
		return confidential;
	}
	public void setConfidential(boolean confidential) {
		this.confidential = confidential;
	}
	public boolean isReplacementDueInsolvencyState() {
		return replacementDueInsolvencyState;
	}
	public void setReplacementDueInsolvencyState(
			boolean replacementDueInsolvencyState) {
		this.replacementDueInsolvencyState = replacementDueInsolvencyState;
	}
//	public boolean isInsolvencyDeclarations() {
//		return insolvencyDeclarations;
//	}
//	public void setInsolvencyDeclarations(boolean insolvencyDeclarations) {
//		this.insolvencyDeclarations = insolvencyDeclarations;
//	}
//	
//	public boolean isInsolvencyStateThisYear() {
//		return insolvencyStateThisYear;
//	}
//	public void setInsolvencyStateThisYear(boolean insolvencyStateThisYear) {
//		this.insolvencyStateThisYear = insolvencyStateThisYear;
//	}
//	public boolean isInsolvencyStateLastPeriod() {
//		return insolvencyStateLastPeriod;
//	}
//	public void setInsolvencyStateLastPeriod(boolean insolvencyStateLastPeriod) {
//		this.insolvencyStateLastPeriod = insolvencyStateLastPeriod;
//	}
//	public boolean isAccrualRegime() {
//		return accrualRegime;
//	}
//	public void setAccrualRegime(boolean accrualRegime) {
//		this.accrualRegime = accrualRegime;
//	}
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
//	public boolean isSpecialGroupRegime() {
//		return specialGroupRegime;
//	}
//	public void setSpecialGroupRegime(boolean specialGroupRegime) {
//		this.specialGroupRegime = specialGroupRegime;
//	}
//	public String getGroupNumber() {
//		return groupNumber;
//	}
//	public void setGroupNumber(String groupNumber) {
//		this.groupNumber = groupNumber;
//	}
//	public boolean isGroupDependent() {
//		return groupDependent;
//	}
//	public void setGroupDependent(boolean groupDependent) {
//		this.groupDependent = groupDependent;
//	}
//	public boolean isGroupRegimeType() {
//		return groupRegimeType;
//	}
//	public void setGroupRegimeType(boolean groupRegimeType) {
//		this.groupRegimeType = groupRegimeType;
//	}
//	public String getGroupDocument() {
//		return groupDocument;
//	}
//	public void setGroupDocument(String groupDocument) {
//		this.groupDocument = groupDocument;
//	}
//	public boolean isGroupDeclarations() {
//		return groupDeclarations;
//	}
//	public void setGroupDeclarations(boolean groupDeclarations) {
//		this.groupDeclarations = groupDeclarations;
//	}
	public boolean isMod347() {
		return mod347;
	}
	public void setMod347(boolean mod347) {
		this.mod347 = mod347;
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
		return mainActivity;
	}
	public void setMainActivity(Activity425 mainActivity) {
		this.mainActivity = mainActivity;
	}
	public Activity425 getActivity1() {
		return activity1;
	}
	public void setActivity1(Activity425 activity1) {
		this.activity1 = activity1;
	}
	public Activity425 getActivity2() {
		return activity2;
	}
	public void setActivity2(Activity425 activity2) {
		this.activity2 = activity2;
	}
	public Activity425 getActivity3() {
		return activity3;
	}
	public void setActivity3(Activity425 activity3) {
		this.activity3 = activity3;
	}
	public Activity425 getActivity4() {
		return activity4;
	}
	public void setActivity4(Activity425 activity4) {
		this.activity4 = activity4;
	}
	public Activity425 getActivity5() {
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
		return generalRegime;
	}
	public void setGeneralRegime(Map<Mod4252025DetailKey, Mod425Detail> generalRegime) {
		this.generalRegime = generalRegime;
	}
	public SimpliedRegimeActivity getSimpRegime1() {
		return simpRegime1;
	}
	public void setSimpRegime1(SimpliedRegimeActivity simpRegime1) {
		this.simpRegime1 = simpRegime1;
	}
	public SimpliedRegimeActivity getSimpRegime2() {
		return simpRegime2;
	}
	public void setSimpRegime2(SimpliedRegimeActivity simpRegime2) {
		this.simpRegime2 = simpRegime2;
	}
	public boolean isSimplifiedRegime() {
		return (
				(getSimpRegime1() != null && AonStringUtils.isNotEmpty(getSimpRegime1().getEpigrafe()))
			||  (getSimpRegime2() != null && AonStringUtils.isNotEmpty(getSimpRegime2().getEpigrafe()))
			||  (getSimpRegime3() != null && AonStringUtils.isNotEmpty(getSimpRegime3().getEpigrafe()))
			||  (getSimpRegime4() != null && AonStringUtils.isNotEmpty(getSimpRegime4().getEpigrafe()))
			||  (getSimpRegime5() != null && AonStringUtils.isNotEmpty(getSimpRegime5().getEpigrafe()))
			||  (getSimpRegime6() != null && AonStringUtils.isNotEmpty(getSimpRegime6().getEpigrafe()))
			||  (getSimpRegime7() != null && AonStringUtils.isNotEmpty(getSimpRegime7().getEpigrafe()))
				); 
	}
//	public FarmerRegimeActivity getFarmerRegime1() {
//		return farmerRegime1;
//	}
//	public FarmerRegimeActivity ensureFarmerRegime1() {
//		if (farmerRegime1 == null) setFarmerRegime1(new FarmerRegimeActivity());
//		return farmerRegime1;
//	}
//	public void setFarmerRegime1(FarmerRegimeActivity farmerRegime1) {
//		this.farmerRegime1 = farmerRegime1;
//	}
//	public FarmerRegimeActivity getFarmerRegime2() {
//		return farmerRegime2;
//	}
//	public FarmerRegimeActivity ensureFarmerRegime2() {
//		if (farmerRegime2 == null) setFarmerRegime2(new FarmerRegimeActivity());
//		return farmerRegime2;
//	}
//	public void setFarmerRegime2(FarmerRegimeActivity farmerRegime2) {
//		this.farmerRegime2 = farmerRegime2;
//	}
//	public FarmerRegimeActivity getFarmerRegime3() {
//		return farmerRegime3;
//	}
//	public FarmerRegimeActivity ensureFarmerRegime3() {
//		if (farmerRegime3 == null) setFarmerRegime3(new FarmerRegimeActivity());
//		return farmerRegime3;
//	}
//	public void setFarmerRegime3(FarmerRegimeActivity farmerRegime3) {
//		this.farmerRegime3 = farmerRegime3;
//	}
//	public FarmerRegimeActivity getFarmerRegime4() {
//		return farmerRegime4;
//	}
//	public FarmerRegimeActivity ensureFarmerRegime4() {
//		if (farmerRegime4 == null) setFarmerRegime4(new FarmerRegimeActivity());
//		return farmerRegime4;
//	}
//	public void setFarmerRegime4(FarmerRegimeActivity farmerRegime4) {
//		this.farmerRegime4 = farmerRegime4;
//	}
//	public FarmerRegimeActivity getFarmerRegime5() {
//		return farmerRegime5;
//	}
//	public FarmerRegimeActivity ensureFarmerRegime5() {
//		if (farmerRegime5 == null) setFarmerRegime5(new FarmerRegimeActivity());
//		return farmerRegime5;
//	}
//	public void setFarmerRegime5(FarmerRegimeActivity farmerRegime5) {
//		this.farmerRegime5 = farmerRegime5;
//	}
//	public double getBox74() {
//		return box74;
//	}
//	public void setBox74(double box74) {
//		this.box74 = box74;
//	}
//	public double getBox75() {
//		return box75;
//	}
//	public void setBox75(double box75) {
//		this.box75 = box75;
//	}
//	public double getBox76() {
//		return box76;
//	}
//	public void setBox76(double box76) {
//		this.box76 = box76;
//	}
//	public double getBox77() {
//		return box77;
//	}
//	public void setBox77(double box77) {
//		this.box77 = box77;
//	}
//	public double getBox78() {
//		return box78;
//	}
//	public void setBox78(double box78) {
//		this.box78 = box78;
//	}
//	public double getBox79() {
//		return box79;
//	}
//	public void setBox79(double box79) {
//		this.box79 = box79;
//	}
//	public double getBox80() {
//		return box80;
//	}
//	public void setBox80(double box80) {
//		this.box80 = box80;
//	}
//	public double getBox81() {
//		return box81;
//	}
//	public void setBox81(double box81) {
//		this.box81 = box81;
//	}
//	public double getBox82() {
//		return box82;
//	}
//	public void setBox82(double box82) {
//		this.box82 = box82;
//	}
//	public double getBox83() {
//		return box83;
//	}
//	public void setBox83(double box83) {
//		this.box83 = box83;
//	}
//	public double getBox658() {
//		return box658;
//	}
//	public void setBox658(double box658) {
//		this.box658 = box658;
//	}
//	public double getBox659() {
//		return box659;
//	}
//	public void setBox659(double box659) {
//		this.box659 = box659;
//	}
//	public double getBox84() {
//		return box84;
//	}
//	public void setBox84(double box84) {
//		this.box84 = box84;
//	}
//	public double getBox85() {
//		return box85;
//	}
//	public void setBox85(double box85) {
//		this.box85 = box85;
//	}
//	public double getBox86() {
//		return box86;
//	}
//	public void setBox86(double box86) {
//		this.box86 = box86;
//	}
//	public double getBox87() {
//		return box87;
//	}
//	public void setBox87(double box87) {
//		this.box87 = box87;
//	}
//	public double getBox88() {
//		return box88;
//	}
//	public void setBox88(double box88) {
//		this.box88 = box88;
//	}
//	public double getBox89() {
//		return box89;
//	}
//	public void setBox89(double box89) {
//		this.box89 = box89;
//	}
//	public double getBox90() {
//		return box90;
//	}
//	public void setBox90(double box90) {
//		this.box90 = box90;
//	}
//	public double getBox91() {
//		return box91;
//	}
//	public void setBox91(double box91) {
//		this.box91 = box91;
//	}
//	public double getBox92() {
//		return box92;
//	}
//	public void setBox92(double box92) {
//		this.box92 = box92;
//	}
//	public double getBox93() {
//		return box93;
//	}
//	public void setBox93(double box93) {
//		this.box93 = box93;
//	}
//	public double getBox94() {
//		return box94;
//	}
//	public void setBox94(double box94) {
//		this.box94 = box94;
//	}
//	public double getBox95() {
//		return box95;
//	}
//	public void setBox95(double box95) {
//		this.box95 = box95;
//	}
//	public double getBox96() {
//		return box96;
//	}
//	public void setBox96(double box96) {
//		this.box96 = box96;
//	}
//	public double getBox524() {
//		return box524;
//	}
//	public void setBox524(double box524) {
//		this.box524 = box524;
//	}
//	public double getBox97() {
//		return box97;
//	}
//	public void setBox97(double box97) {
//		this.box97 = box97;
//	}
//	public double getBox98() {
//		return box98;
//	}
//	public void setBox98(double box98) {
//		this.box98 = box98;
//	}
//	public double getBox662() {
//		return box662;
//	}
//	public void setBox662(double box662) {
//		this.box662 = box662;
//	}
//	public double getBox525() {
//		return box525;
//	}
//	public void setBox525(double box525) {
//		this.box525 = box525;
//	}
//	public double getBox526() {
//		return box526;
//	}
//	public void setBox526(double box526) {
//		this.box526 = box526;
//	}
//	public double getBox99() {
//		return box99;
//	}
//	public void setBox99(double box99) {
//		this.box99 = box99;
//	}
//	public double getBox653() {
//		return box653;
//	}
//	public void setBox653(double box653) {
//		this.box653 = box653;
//	}
//	public double getBox103() {
//		return box103;
//	}
//	public void setBox103(double box103) {
//		this.box103 = box103;
//	}
//	public double getBox104() {
//		return box104;
//	}
//	public void setBox104(double box104) {
//		this.box104 = box104;
//	}
//	public double getBox105() {
//		return box105;
//	}
//	public void setBox105(double box105) {
//		this.box105 = box105;
//	}
//	public double getBox110() {
//		return box110;
//	}
//	public void setBox110(double box110) {
//		this.box110 = box110;
//	}
//	public double getBox125() {
//		return box125;
//	}
//	public void setBox125(double box125) {
//		this.box125 = box125;
//	}
//	public double getBox126() {
//		return box126;
//	}
//	public void setBox126(double box126) {
//		this.box126 = box126;
//	}
//	public double getBox127() {
//		return box127;
//	}
//	public void setBox127(double box127) {
//		this.box127 = box127;
//	}
//	public double getBox128() {
//		return box128;
//	}
//	public void setBox128(double box128) {
//		this.box128 = box128;
//	}
//	public double getBox100() {
//		return box100;
//	}
//	public void setBox100(double box100) {
//		this.box100 = box100;
//	}
//	public double getBox101() {
//		return box101;
//	}
//	public void setBox101(double box101) {
//		this.box101 = box101;
//	}
//	public double getBox102() {
//		return box102;
//	}
//	public void setBox102(double box102) {
//		this.box102 = box102;
//	}
//	public double getBox227() {
//		return box227;
//	}
//	public void setBox227(double box227) {
//		this.box227 = box227;
//	}
//	public double getBox228() {
//		return box228;
//	}
//	public void setBox228(double box228) {
//		this.box228 = box228;
//	}
//	public double getBox106() {
//		return box106;
//	}
//	public void setBox106(double box106) {
//		this.box106 = box106;
//	}
//	public double getBox107() {
//		return box107;
//	}
//	public void setBox107(double box107) {
//		this.box107 = box107;
//	}
//	public double getBox108() {
//		return box108;
//	}
//	public void setBox108(double box108) {
//		this.box108 = box108;
//	}
//	public double getBox230() {
//		return box230;
//	}
//	public void setBox230(double box230) {
//		this.box230 = box230;
//	}
//	public double getBox109() {
//		return box109;
//	}
//	public void setBox109(double box109) {
//		this.box109 = box109;
//	}
//	public double getBox231() {
//		return box231;
//	}
//	public void setBox231(double box231) {
//		this.box231 = box231;
//	}
//	public double getBox232() {
//		return box232;
//	}
//	public void setBox232(double box232) {
//		this.box232 = box232;
//	}
//	public double getBox111() {
//		return box111;
//	}
//	public void setBox111(double box111) {
//		this.box111 = box111;
//	}
//	public double getBox113() {
//		return box113;
//	}
//	public void setBox113(double box113) {
//		this.box113 = box113;
//	}
//	public double getBox523() {
//		return box523;
//	}
//	public void setBox523(double box523) {
//		this.box523 = box523;
//	}
//	public double getBox654() {
//		return box654;
//	}
//	public void setBox654(double box654) {
//		this.box654 = box654;
//	}
//	public double getBox655() {
//		return box655;
//	}
//	public void setBox655(double box655) {
//		this.box655 = box655;
//	}
//	public double getBox656() {
//		return box656;
//	}
//	public void setBox656(double box656) {
//		this.box656 = box656;
//	}
//	public double getBox657() {
//		return box657;
//	}
//	public void setBox657(double box657) {
//		this.box657 = box657;
//	}
//	public LinkedList<Prorrata> getProrratas() {
//		return prorratas;
//	}
//	public void setProrratas(LinkedList<Prorrata> prorratas) {
//		this.prorratas = prorratas;
//	}
//	
//	public Prorrata getProrrata(int index) {
//		return (prorratas!=null && index < prorratas.size()) ? prorratas.get(index) :null;
//	}
//	
//	public DeductionRegime getRegime1() {
//		return regime1;
//	}
//	public DeductionRegime ensureRegime1() {
//		if (regime1 == null) setRegime1(new DeductionRegime());
//		return regime1;
//	}
//	public void setRegime1(DeductionRegime regime1) {
//		this.regime1 = regime1;
//	}
//	public DeductionRegime getRegime2() {
//		return regime2;
//	}
//	public DeductionRegime ensureRegime2() {
//		if (regime2 == null) setRegime2(new DeductionRegime());
//		return regime2;
//	}
//	public void setRegime2(DeductionRegime regime2) {
//		this.regime2 = regime2;
//	}
//	public DeductionRegime getRegime3() {
//		return regime3;
//	}
//	public DeductionRegime ensureRegime3() {
//		if (regime3 == null) setRegime3(new DeductionRegime());
//		return regime3;
//	}
//	public void setRegime3(DeductionRegime regime3) {
//		this.regime3 = regime3;
//	}

//	private static final Mod4252025DetailKey[] C0034_FORMULA = new Mod4252025DetailKey[] { 
//	     Mod4252025DetailKey.C0701,Mod4252025DetailKey.C0668,Mod4252025DetailKey.C0002,Mod4252025DetailKey.C0703,Mod4252025DetailKey.C0670,Mod4252025DetailKey.C0004,Mod4252025DetailKey.C0006
//	    ,Mod4252025DetailKey.C0705,Mod4252025DetailKey.C0672,Mod4252025DetailKey.C0501,Mod4252025DetailKey.C0707,Mod4252025DetailKey.C0674,Mod4252025DetailKey.C0503,Mod4252025DetailKey.C0505
//	    ,Mod4252025DetailKey.C0709,Mod4252025DetailKey.C0676,Mod4252025DetailKey.C0644,Mod4252025DetailKey.C0711,Mod4252025DetailKey.C0678,Mod4252025DetailKey.C0646,Mod4252025DetailKey.C0648
//	    ,Mod4252025DetailKey.C0713,Mod4252025DetailKey.C0680,Mod4252025DetailKey.C0008,Mod4252025DetailKey.C0715,Mod4252025DetailKey.C0682,Mod4252025DetailKey.C0010,Mod4252025DetailKey.C0012
//	    ,Mod4252025DetailKey.C0014
//	    ,Mod4252025DetailKey.C0717,Mod4252025DetailKey.C0684,Mod4252025DetailKey.C0022,Mod4252025DetailKey.C0719,Mod4252025DetailKey.C0686,Mod4252025DetailKey.C0024,Mod4252025DetailKey.C0026
//	    ,Mod4252025DetailKey.C0721,Mod4252025DetailKey.C0688,Mod4252025DetailKey.C0546,Mod4252025DetailKey.C0723,Mod4252025DetailKey.C0690,Mod4252025DetailKey.C0548,Mod4252025DetailKey.C0552
//	    ,Mod4252025DetailKey.C0028,Mod4252025DetailKey.C0030,Mod4252025DetailKey.C0650,Mod4252025DetailKey.C0032
//	};
//
//	private static final Mod4252025DetailKey[] C0047_FORMULA = new Mod4252025DetailKey[] { 
//		 Mod4252025DetailKey.C0034
//		,Mod4252025DetailKey.C0664
//		,Mod4252025DetailKey.C0692
//		,Mod4252025DetailKey.C0036
//		,Mod4252025DetailKey.C0666
//		,Mod4252025DetailKey.C0694
//		,Mod4252025DetailKey.C0600
//		,Mod4252025DetailKey.C0602
//		,Mod4252025DetailKey.C0042
//		,Mod4252025DetailKey.C0044
//		,Mod4252025DetailKey.C0046
//	};
//
//	private static final Mod4252025DetailKey[] C0049_FORMULA = {
//		 Mod4252025DetailKey.C0696
//		,Mod4252025DetailKey.C0191
//		,Mod4252025DetailKey.C0725
//		,Mod4252025DetailKey.C0698
//		,Mod4252025DetailKey.C0604
//		,Mod4252025DetailKey.C0606 
//	};
//
//	private static final Mod4252025DetailKey[] C0513_FORMULA = {
//		 Mod4252025DetailKey.C0746			
//		,Mod4252025DetailKey.C0507
//		,Mod4252025DetailKey.C0727
//		,Mod4252025DetailKey.C0748
//		,Mod4252025DetailKey.C0608
//		,Mod4252025DetailKey.C0610
//	};
//	
//	private static final Mod4252025DetailKey[] C0051_FORMULA = {
//		 Mod4252025DetailKey.C0750
//		,Mod4252025DetailKey.C0197
//		,Mod4252025DetailKey.C0729
//		,Mod4252025DetailKey.C0752
//		,Mod4252025DetailKey.C0612
//		,Mod4252025DetailKey.C0614
//	};
//	
//	private static final Mod4252025DetailKey[] C0521_FORMULA = {
//		 Mod4252025DetailKey.C0754
//		,Mod4252025DetailKey.C0515
//		,Mod4252025DetailKey.C0731
//		,Mod4252025DetailKey.C0756
//		,Mod4252025DetailKey.C0616
//		,Mod4252025DetailKey.C0618
//	};
//	
//	private static final Mod4252025DetailKey[] C0053_FORMULA = {
//		 Mod4252025DetailKey.C0758
//		,Mod4252025DetailKey.C0203
//		,Mod4252025DetailKey.C0733
//		,Mod4252025DetailKey.C0760
//		,Mod4252025DetailKey.C0620
//		,Mod4252025DetailKey.C0622
//	};
//	
//	private static final Mod4252025DetailKey[] C0055_FORMULA = {
//		 Mod4252025DetailKey.C0762
//		,Mod4252025DetailKey.C0209
//		,Mod4252025DetailKey.C0735
//		,Mod4252025DetailKey.C0764
//		,Mod4252025DetailKey.C0624
//		,Mod4252025DetailKey.C0626
//	};
//	
//	private static final Mod4252025DetailKey[] C0057_FORMULA = {
//		 Mod4252025DetailKey.C0766
//		,Mod4252025DetailKey.C0215
//		,Mod4252025DetailKey.C0737
//		,Mod4252025DetailKey.C0768
//		,Mod4252025DetailKey.C0628
//		,Mod4252025DetailKey.C0630
//	};
//	
//	private static final Mod4252025DetailKey[] C0059_FORMULA = {
//		 Mod4252025DetailKey.C0770
//		,Mod4252025DetailKey.C0221
//		,Mod4252025DetailKey.C0739
//		,Mod4252025DetailKey.C0772
//		,Mod4252025DetailKey.C0632
//		,Mod4252025DetailKey.C0634
//	};
//
//	private static final Mod4252025DetailKey[] C0598_FORMULA = {
//		 Mod4252025DetailKey.C0774
//		,Mod4252025DetailKey.C0588
//		,Mod4252025DetailKey.C0741
//		,Mod4252025DetailKey.C0776
//		,Mod4252025DetailKey.C0636
//		,Mod4252025DetailKey.C0638
//	};	
//
//	private static final Mod4252025DetailKey[] C0064_FORMULA = {
//		 Mod4252025DetailKey.C0049,Mod4252025DetailKey.C0513
//		,Mod4252025DetailKey.C0051,Mod4252025DetailKey.C0521
//		,Mod4252025DetailKey.C0053,Mod4252025DetailKey.C0055
//		,Mod4252025DetailKey.C0057,Mod4252025DetailKey.C0059
//		,Mod4252025DetailKey.C0598,Mod4252025DetailKey.C0061
//		,Mod4252025DetailKey.C0661,Mod4252025DetailKey.C0062
//		,Mod4252025DetailKey.C0652,Mod4252025DetailKey.C0063
//		,Mod4252025DetailKey.C0522};
//
//	public void calculate() {
//		double k37Quota = 0;
//		calculate(Mod4252025DetailKey.C0034, C0034_FORMULA);
//		Mod425Detail k13 = calculate(Mod4252025DetailKey.C0047, C0047_FORMULA);
//		calculate(Mod4252025DetailKey.C0049, C0049_FORMULA);
//		calculate(Mod4252025DetailKey.C0513, C0513_FORMULA);
//		calculate(Mod4252025DetailKey.C0051, C0051_FORMULA);
//		calculate(Mod4252025DetailKey.C0521, C0521_FORMULA);
//		calculate(Mod4252025DetailKey.C0053, C0053_FORMULA);
//		calculate(Mod4252025DetailKey.C0055, C0055_FORMULA);
//		calculate(Mod4252025DetailKey.C0057, C0057_FORMULA);
//		calculate(Mod4252025DetailKey.C0059, C0059_FORMULA);
//		calculate(Mod4252025DetailKey.C0598, C0598_FORMULA);
//			
//		Mod425Detail k36 = calculate(Mod4252025DetailKey.C0064, C0064_FORMULA);
//		Mod425Detail k37 = ensure(Mod4252025DetailKey.C0065);
//		k37Quota = AonMathUtils.round(k13.getQuota() - k36.getQuota());
//		k37.setQuota( k37Quota );
//		
//		if (isSimplifiedRegime()) {
//			box74 = AonMathUtils.round(
//					(getSimpRegime1()==null?0:getSimpRegime1().getBoxJ()) 
//				  + (getSimpRegime2()==null?0:getSimpRegime2().getBoxJ()));
//			box75 = AonMathUtils.round(
//					  (getFarmerRegime1()!=null?getFarmerRegime1().getQuota():0)
//					+ (getFarmerRegime2()!=null?getFarmerRegime2().getQuota():0)
//					+ (getFarmerRegime3()!=null?getFarmerRegime3().getQuota():0)
//					+ (getFarmerRegime4()!=null?getFarmerRegime4().getQuota():0)
//					+ (getFarmerRegime5()!=null?getFarmerRegime5().getQuota():0)
//					);
//			box79 = AonMathUtils.round(box74 + box75 + box76 + box77 + box78 );
//			box82 = AonMathUtils.round(box80 + box81);
//			box83 = AonMathUtils.round(box79 - box82);
//		} else {
//			box74 = 0;
//			box75 = 0;
//			box79 = 0;
//			box82 = 0;
//			box83 = 0;
//		}
//		box84 = AonMathUtils.round(k37Quota + box83);
//		box86 = AonMathUtils.round(box84 + box659 - box85);
//		box92 = AonMathUtils.round(box84 * box87 / 100);
//		box94 = AonMathUtils.round(box92 + box659 - box93);
//		box108 = AonMathUtils.round(box99+box653+box103+box104+box105
//				+box110+box125+box126+box127
//				+box128+box100+box101+box102
//				+box227+box228-box106-box107);
//	}
	
//	public Mod425Detail ensure(Mod4252025DetailKey key) {
//		Mod425Detail detail = getGeneralRegime().get(key);
//		if (detail == null) {
//			detail = new Mod425Detail();
//			detail.setKey(key);
//			detail.setPercent(key.getPercent());
//			getGeneralRegime().put(key, detail);
//		}
//		return detail;
//	}
	
//	private Mod425Detail calculate(Mod4252025DetailKey key, Mod4252025DetailKey ... keys) {
//		Mod425Detail detail = ensure(key);
//		detail.setTaxableBase(0.0);
//		detail.setQuota(0.0);
//		for (Mod4252025DetailKey k : keys) {
//			Mod425Detail det = ensure(k);
//			detail.setTaxableBase( AonMathUtils.round(detail.getTaxableBase() + det.getTaxableBase()));
//			detail.setQuota( AonMathUtils.round(detail.getQuota() + det.getQuota()));
//		}
//		return detail;
//	}
	
	public SimpliedRegimeActivity getSimpRegime3() {
		return simpRegime3;
	}
	public void setSimpRegime3(SimpliedRegimeActivity simpRegime3) {
		this.simpRegime3 = simpRegime3;
	}
	public SimpliedRegimeActivity getSimpRegime4() {
		return simpRegime4;
	}
	public void setSimpRegime4(SimpliedRegimeActivity simpRegime4) {
		this.simpRegime4 = simpRegime4;
	}
	public SimpliedRegimeActivity getSimpRegime5() {
		return simpRegime5;
	}
	public void setSimpRegime5(SimpliedRegimeActivity simpRegime5) {
		this.simpRegime5 = simpRegime5;
	}
	public SimpliedRegimeActivity getSimpRegime6() {
		return simpRegime6;
	}
	public void setSimpRegime6(SimpliedRegimeActivity simpRegime6) {
		this.simpRegime6 = simpRegime6;
	}
	public SimpliedRegimeActivity getSimpRegime7() {
		return simpRegime7;
	}
	public void setSimpRegime7(SimpliedRegimeActivity simpRegime7) {
		this.simpRegime7 = simpRegime7;
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
	
}

