package com.code.aon.ui.fiscal.controller;



import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.fiscal.enumeration.FiscalModelStatus;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.InvoiceReportOrder;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.RetentionRegime;
import com.code.aon.fiscal.enumeration.VatExemptionCause;
import com.code.aon.fiscal.enumeration.VatRegime;
import com.code.aon.fiscal.enumeration.VatTaxDeclarationStatus;
import com.code.aon.fiscal.enumeration.VatTaxStatus;
import com.code.aon.fiscal.enumeration.VatType;
import com.code.aon.fiscal.enumeration.WithholdingStatus;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class FiscalCollectionsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<SelectItem> withholdingStatuses;
	private List<SelectItem> vatTaxStatuses;
	private List<SelectItem> vatTaxDeclarationStatuses;
	private List<SelectItem> vatTypes;
	private List<SelectItem> vatRegimes;
	private List<SelectItem> vatExemptionCauses;
	private List<SelectItem> retentionRegimes;
	private List<SelectItem> invoiceOrders;
	private List<SelectItem> periods;
	private List<SelectItem> quarterPeriods;
	private List<SelectItem> fiscalModelStatuses;

	public List<SelectItem> getWithholdingStatuses() {
		if (withholdingStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			withholdingStatuses = new LinkedList<>();
			for (WithholdingStatus status:WithholdingStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				withholdingStatuses.add(item);
			}
		}
		return withholdingStatuses;
	}

	public List<SelectItem> getVatTaxStatuses() {
		if (vatTaxStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			vatTaxStatuses = new LinkedList<>();
			for (VatTaxStatus status:VatTaxStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				vatTaxStatuses.add(item);
			}
		}
		return vatTaxStatuses;
	}

	public List<SelectItem> getVatTaxDeclarationStatuses() {
		if (vatTaxDeclarationStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			vatTaxDeclarationStatuses = new LinkedList<>();
			for (VatTaxDeclarationStatus declarationStatus:VatTaxDeclarationStatus.values()) {
				String name = declarationStatus.getName(locale);
				SelectItem item = new SelectItem(declarationStatus, name);
				vatTaxDeclarationStatuses.add(item);
			}
		}
		return vatTaxDeclarationStatuses;
	}

	public List<SelectItem> getVatTypes() {
		if (vatTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			vatTypes = new LinkedList<>();
			for (VatType type:VatType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				vatTypes.add(item);
			}
		}
		return vatTypes;
	}

	public List<SelectItem> getVatRegimes() {
		if (vatRegimes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			vatRegimes = new LinkedList<>();
			for (VatRegime regime : VatRegime.values()) {
				SelectItem item = new SelectItem(regime, regime.getName(locale));
				vatRegimes.add(item);
			}
		}
		return vatRegimes;
	}
	
	public List<SelectItem> getVatExemptionCauses() {
		if (vatExemptionCauses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			vatExemptionCauses = new LinkedList<>();
			for (VatExemptionCause exemptionCause : VatExemptionCause.values()) {
				SelectItem item = new SelectItem(exemptionCause, exemptionCause.getName(locale));
				vatExemptionCauses.add(item);
			}
		}
		return vatExemptionCauses;
	}

	public List<SelectItem> getRetentionRegimes() {
		if (retentionRegimes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			retentionRegimes = new LinkedList<>();
			for (RetentionRegime regime : RetentionRegime.values()) {
				SelectItem item = new SelectItem(regime, regime.getName(locale));
				retentionRegimes.add(item);
			}
		}
		return retentionRegimes;
	}

	public List<SelectItem> getInvoiceReportOrders() {
		if (invoiceOrders == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			invoiceOrders = new LinkedList<>();
			for (InvoiceReportOrder order:InvoiceReportOrder.values()) {
				String name = order.getName(locale);
				SelectItem item = new SelectItem(order, name);
				invoiceOrders.add(item);
			}
		}
		return invoiceOrders;
	}

	public List<SelectItem> getPeriods() {
		if (periods == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			periods = new LinkedList<>();
			for (Period period:Period.values()) {
				String name = period.getName(locale);
				SelectItem item = new SelectItem(period, name);
				periods.add(item);
			}
		}
		return periods;
	}
	
	public List<SelectItem> getQuarterPeriods() {
		if (quarterPeriods == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			quarterPeriods = new LinkedList<>();
			quarterPeriods.add( new SelectItem(Period.T1, Period.T1.getName(locale)) );
			quarterPeriods.add( new SelectItem(Period.T2, Period.T2.getName(locale)) );
			quarterPeriods.add( new SelectItem(Period.T3, Period.T3.getName(locale)) );
			quarterPeriods.add( new SelectItem(Period.T4, Period.T4.getName(locale)) );
		}
		return quarterPeriods;
	}

	public List<SelectItem> getFiscalModelStatuses() {
		if (fiscalModelStatuses == null) {
			fiscalModelStatuses = new LinkedList<>();
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			for (FiscalModelStatus status : FiscalModelStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				fiscalModelStatuses.add(item);
			}
		}
		return fiscalModelStatuses;
	}
	
	public List<SelectItem> getModelPayMethods() throws ManagerBeanException {
		List<SelectItem> payMethods = new LinkedList<>();
		IManagerBean payMethodBean = BeanManager.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(payMethodBean.getFieldName(IEntityAlias.PAY_METHOD_NAME));
		for (ITransferObject ito : payMethodBean.getList(criteria)) {
			PayMethod pMethod = (PayMethod)ito;
			if (pMethod.getType() == PayMethodType.NEGOTIABLE_DOCUMENT ||
				pMethod.getType() == PayMethodType.CASH_BASIS ) {
				SelectItem item = new SelectItem(pMethod, pMethod.getName());
				payMethods.add(item);
			}
		}
		return payMethods;
	}

	public FiscalModelType getModel111() {
		return FiscalModelType.M111;
	}
	public FiscalModelType getModel115() {
		return FiscalModelType.M115;
	}
	public FiscalModelType getModel123() {
		return FiscalModelType.M123;
	}
	public FiscalModelType getModel130() {
		return FiscalModelType.M130;
	}
	public FiscalModelType getModel303() {
		return FiscalModelType.M303;
	}
	public FiscalModelType getModel303AI() {
		return FiscalModelType.M303_AI;
	}
	public FiscalModelType getModel310() {
		return FiscalModelType.M310;
	}
	public FiscalModelType getModel311() {
		return FiscalModelType.M311;
	}
	public FiscalModelType getModel131() {
		return FiscalModelType.M131;
	}

	private static List<SelectItem> streetTypes = new LinkedList<>(); 
	static {
		streetTypes.add(new SelectItem("ACCE ","ACCES"));
		streetTypes.add(new SelectItem("ACCES","ACCESO"));
		streetTypes.add(new SelectItem("ACEQ ","ACEQUIA"));
		streetTypes.add(new SelectItem("ACERA","ACERA"));
		streetTypes.add(new SelectItem("ALAM ","ALAMEDA"));
		streetTypes.add(new SelectItem("ALDAP","ALDAPA"));
		streetTypes.add(new SelectItem("ALDEA","ALDEA"));
		streetTypes.add(new SelectItem("ALQUE","ALQUERIA"));
		streetTypes.add(new SelectItem("ALTO ","ALTO"));
		streetTypes.add(new SelectItem("ANDAD","ANDADOR"));
		streetTypes.add(new SelectItem("ANGTA","ANGOSTA"));
		streetTypes.add(new SelectItem("APTES","APARTAMENTS"));
		streetTypes.add(new SelectItem("APTOS","APARTAMENTOS"));
		streetTypes.add(new SelectItem("ARB  ","ARBOLEDA"));
		streetTypes.add(new SelectItem("ARRAL","ARRABAL"));
		streetTypes.add(new SelectItem("ARRY ","ARROYO"));
		streetTypes.add(new SelectItem("ASSEG","ASSEGADOR"));
		streetTypes.add(new SelectItem("ATAJO","ATAJO"));
		streetTypes.add(new SelectItem("ATAL ","ATALL"));
		streetTypes.add(new SelectItem("ATALL","ATALLO"));
		streetTypes.add(new SelectItem("ATZUC","ATZUCAT"));
		streetTypes.add(new SelectItem("AUTO ","AUTOPISTA"));
		streetTypes.add(new SelectItem("AUZO ","AUZO"));
		streetTypes.add(new SelectItem("AUZOT","AUZOTEGI"));
		streetTypes.add(new SelectItem("AUZUN","AUZUNEA"));
		streetTypes.add(new SelectItem("AV   ","AVINGUDA"));
		streetTypes.add(new SelectItem("AVDA ","AVENIDA"));
		streetTypes.add(new SelectItem("AVGDA","AVINGUDA"));
		streetTypes.add(new SelectItem("AVIA ","AUTOVIA"));
		streetTypes.add(new SelectItem("BARDA","BARRIADA"));
		streetTypes.add(new SelectItem("BARRI","BARRI"));
		streetTypes.add(new SelectItem("BARRO","BARRIO"));
		streetTypes.add(new SelectItem("BDA  ","BAIXADA"));
		streetTypes.add(new SelectItem("BELNA","BELENA"));
		streetTypes.add(new SelectItem("BIDE ","BIDE"));
		streetTypes.add(new SelectItem("BIDEB","BIDEBIETA"));
		streetTypes.add(new SelectItem("BJADA","BAJADA"));
		streetTypes.add(new SelectItem("BLQUE","BLOQUE"));
		streetTypes.add(new SelectItem("BRANC","BARRANCO"));
		streetTypes.add(new SelectItem("BRDLA","BARREDUELA"));
		streetTypes.add(new SelectItem("BRZAL","BRAZAL"));
		streetTypes.add(new SelectItem("BULEV","BULEVAR"));
		streetTypes.add(new SelectItem("BV   ","BULEVAR"));
		streetTypes.add(new SelectItem("C    ","CARRER"));
		streetTypes.add(new SelectItem("C.H. ","CMNO HONDO"));
		streetTypes.add(new SelectItem("C.N. ","CMNO NUEVO"));
		streetTypes.add(new SelectItem("C.V. ","CMNO VIEJO"));
		streetTypes.add(new SelectItem("C/   ","CARRER"));
		streetTypes.add(new SelectItem("CÑADA","CAÑADA"));
		streetTypes.add(new SelectItem("CALLE","CALLE"));
		streetTypes.add(new SelectItem("CALLI","CALLI"));
		streetTypes.add(new SelectItem("CAMI ","CAMI"));
		streetTypes.add(new SelectItem("CAMIN","CAMIN"));
		streetTypes.add(new SelectItem("CAMPA","CAMPA"));
		streetTypes.add(new SelectItem("CANÑO","CANTIÑO"));
		streetTypes.add(new SelectItem("CANAL","CANAL"));
		streetTypes.add(new SelectItem("CANT ","CANTON"));
		streetTypes.add(new SelectItem("CANTO","CANTO"));
		streetTypes.add(new SelectItem("CARRA","CARRERADA"));
		streetTypes.add(new SelectItem("CARRE","CARRER"));
		streetTypes.add(new SelectItem("CARRY","CARRERANY"));
		streetTypes.add(new SelectItem("CBTIZ","COBERTIZO"));
		streetTypes.add(new SelectItem("CELLA","CANELLA"));
		streetTypes.add(new SelectItem("CERRO","CERRO"));
		streetTypes.add(new SelectItem("CINT ","CINTURO"));
		streetTypes.add(new SelectItem("CINY ","CINYELL"));
		streetTypes.add(new SelectItem("CIRCU","CIRCUMVAL.LACIO"));
		streetTypes.add(new SelectItem("CJLA ","CALLEJUELA"));
		streetTypes.add(new SelectItem("CJTO ","CONJUNTO"));
		streetTypes.add(new SelectItem("CLEYA","CALEYA"));
		streetTypes.add(new SelectItem("CLLJA","CALLEJA"));
		streetTypes.add(new SelectItem("CLLJO","CALLEJO"));
		streetTypes.add(new SelectItem("CLLON","CALLEJON"));
		streetTypes.add(new SelectItem("CLLZO","CALLIZO"));
		streetTypes.add(new SelectItem("CLYON","CALEYON"));
		streetTypes.add(new SelectItem("CMÑO ","CAMIÑO"));
		streetTypes.add(new SelectItem("CMNO ","CAMINO"));
		streetTypes.add(new SelectItem("CNDA ","CANADA"));
		streetTypes.add(new SelectItem("COL  ","COLONIA"));
		streetTypes.add(new SelectItem("COMPJ","COMPLEJO"));
		streetTypes.add(new SelectItem("COMPX","COMPLEXO"));
		streetTypes.add(new SelectItem("COSTA","COSTA"));
		streetTypes.add(new SelectItem("CRA  ","CARRERA"));
		streetTypes.add(new SelectItem("CNLLA","CANELLA"));
		streetTypes.add(new SelectItem("CORDL","CORDEL"));
		streetTypes.add(new SelectItem("CRLLO","CORRILLO"));
		streetTypes.add(new SelectItem("CRO  ","CARRERO"));
		streetTypes.add(new SelectItem("CRRA ","CARRUA"));
		streetTypes.add(new SelectItem("CRRAL","CORRALO"));
		streetTypes.add(new SelectItem("CRRCI","CORREDORCILLO"));
		streetTypes.add(new SelectItem("CRRDA","CORREDOIRA"));
		streetTypes.add(new SelectItem("CRRDE","CORREDERA"));
		streetTypes.add(new SelectItem("CRRDO","CORREDOR"));
		streetTypes.add(new SelectItem("CRRIL","CARRIL"));
		streetTypes.add(new SelectItem("CRRLO","CORRALILLO"));
		streetTypes.add(new SelectItem("CRRO ","CARREIRO"));
		streetTypes.add(new SelectItem("CRROL","CORRIOL"));
		streetTypes.add(new SelectItem("CRTIL","CARRETIL"));
		streetTypes.add(new SelectItem("CRTJO","CORTIJO"));
		streetTypes.add(new SelectItem("CSRIO","CASERIO"));
		streetTypes.add(new SelectItem("CSTAN","COSTANILLA"));
		streetTypes.add(new SelectItem("CTRA ","CARRETERA"));
		streetTypes.add(new SelectItem("CTRIN","CARRETERIN"));
		streetTypes.add(new SelectItem("CUSTA","CUESTA"));
		streetTypes.add(new SelectItem("CXON ","CALEXON"));
		streetTypes.add(new SelectItem("CXTO ","CONXUNTO"));
		streetTypes.add(new SelectItem("CZADA","CALZADA"));
		streetTypes.add(new SelectItem("CZADS","CALZADAS"));
		streetTypes.add(new SelectItem("DEMAR","DEMARCACIO"));
		streetTypes.add(new SelectItem("DRERA","DRECERA"));
		streetTypes.add(new SelectItem("EIRAD","EIRADO"));
		streetTypes.add(new SelectItem("ENTD ","ENTRADA"));
		streetTypes.add(new SelectItem("EPTZA","ENPARANTZA"));
		streetTypes.add(new SelectItem("ERREB","ERREBAL"));
		streetTypes.add(new SelectItem("ERREK","ERREKA"));
		streetTypes.add(new SelectItem("ERREP","ERREPIDE"));
		streetTypes.add(new SelectItem("ERRIB","ERRIBERA"));
		streetTypes.add(new SelectItem("ESCA ","ESCALERA"));
		streetTypes.add(new SelectItem("ESCAL","ESCALINATA"));
		streetTypes.add(new SelectItem("ESCU ","ESCULLERA"));
		streetTypes.add(new SelectItem("ESLDA","ESPALDA"));
		streetTypes.add(new SelectItem("ESTDA","ESTRADA"));
		streetTypes.add(new SelectItem("ETDEA","ETORBIDE"));
		streetTypes.add(new SelectItem("ETXAD","ETXADI"));
		streetTypes.add(new SelectItem("ETXAR","ETXARTE"));
		streetTypes.add(new SelectItem("ETXAT","ETXATZE"));
		streetTypes.add(new SelectItem("EXPLA","EXPLANADA"));
		streetTypes.add(new SelectItem("EXTRM","EXTRAMUROS"));
		streetTypes.add(new SelectItem("EXTRR","EXTRARRADIO"));
		streetTypes.add(new SelectItem("FALDA","FALDA"));
		streetTypes.add(new SelectItem("FINCA","FINCA"));
		streetTypes.add(new SelectItem("G.V. ","GRAN VIA"));
		streetTypes.add(new SelectItem("GAIN ","GAIN"));
		streetTypes.add(new SelectItem("GALE ","GALERIA"));
		streetTypes.add(new SelectItem("GORAB","GORABIDE"));
		streetTypes.add(new SelectItem("GRUP ","GRUPO"));
		streetTypes.add(new SelectItem("GRUPO","GRUPO"));
		streetTypes.add(new SelectItem("GTA  ","GLORIETA"));
		streetTypes.add(new SelectItem("HEGI ","HEGI"));
		streetTypes.add(new SelectItem("HIRIB","HIRIBIDE"));
		streetTypes.add(new SelectItem("HONDA","HONDARTZA"));
		streetTypes.add(new SelectItem("HOYA ","HOYA"));
		streetTypes.add(new SelectItem("IBILB","IBILBIDE"));
		streetTypes.add(new SelectItem("ILLA ","ILLA"));
		streetTypes.add(new SelectItem("INDA ","INDA"));
		streetTypes.add(new SelectItem("JARD ","JARDI"));
		streetTypes.add(new SelectItem("JDIN ","JARDIN"));
		streetTypes.add(new SelectItem("JDINS","JARDINES"));
		streetTypes.add(new SelectItem("KAI  ","KAI"));
		streetTypes.add(new SelectItem("KALE ","KALE"));
		streetTypes.add(new SelectItem("KARIK","KARRIKA"));
		streetTypes.add(new SelectItem("KARRE","KARRERA"));
		streetTypes.add(new SelectItem("KARRI","KARRICA"));
		streetTypes.add(new SelectItem("KOSTA","KOSTA"));
		streetTypes.add(new SelectItem("KRRIL","KARRIL"));
		streetTypes.add(new SelectItem("LAGO ","LAGO"));
		streetTypes.add(new SelectItem("LASTE","LASTERBIDE"));
		streetTypes.add(new SelectItem("LDERA","LADERA"));
		streetTypes.add(new SelectItem("LEKU ","LEKU"));
		streetTypes.add(new SelectItem("LLOC ","LLOC"));
		streetTypes.add(new SelectItem("LOMA ","LOMA"));
		streetTypes.add(new SelectItem("LORAK","LORATEGIAK"));
		streetTypes.add(new SelectItem("LORAT","LORATEGI"));
		streetTypes.add(new SelectItem("LUGAR","LUGAR"));
		streetTypes.add(new SelectItem("MALEC","MALECON"));
		streetTypes.add(new SelectItem("MAZO ","MAZO"));
		streetTypes.add(new SelectItem("MENDI","MENDI"));
		streetTypes.add(new SelectItem("MIRAD","MIRADOR"));
		streetTypes.add(new SelectItem("MOLL ","MOLL"));
		streetTypes.add(new SelectItem("MONTE","MONTE"));
		streetTypes.add(new SelectItem("MUELL","MUELLE"));
		streetTypes.add(new SelectItem("ONDAR","ONDARTZA"));
		streetTypes.add(new SelectItem("PAGO ","PAGO"));
		streetTypes.add(new SelectItem("PARC ","PARQUE"));
		streetTypes.add(new SelectItem("PARKE","PARKE"));
		streetTypes.add(new SelectItem("PARTI","PARTICULAR"));
		streetTypes.add(new SelectItem("PAS  ","PAS"));
		streetTypes.add(new SelectItem("PASAI","PASAI"));
		streetTypes.add(new SelectItem("PASEA","PASEABIDE"));
		streetTypes.add(new SelectItem("PASEO","PASEO"));
		streetTypes.add(new SelectItem("PBDO ","POBLADO"));
		streetTypes.add(new SelectItem("PDA  ","PUJADA"));
		streetTypes.add(new SelectItem("PDIS ","PASSADIS"));
		streetTypes.add(new SelectItem("PG   ","PASSEIG"));
		streetTypes.add(new SelectItem("PINAR","PINAR"));
		streetTypes.add(new SelectItem("PISTA","PISTA"));
		streetTypes.add(new SelectItem("PJDA ","PUJADA,SUBIDA"));
		streetTypes.add(new SelectItem("PL   ","PLACA"));
		streetTypes.add(new SelectItem("PLA  ","PLA"));
		streetTypes.add(new SelectItem("PLAÇA","PLAÇA"));
		streetTypes.add(new SelectItem("PLAYA","PLAYA"));
		streetTypes.add(new SelectItem("PLAZA","PLAZA"));
		streetTypes.add(new SelectItem("PLCET","PLACETA"));
		streetTypes.add(new SelectItem("PLLO ","PASILLO"));
		streetTypes.add(new SelectItem("PLZLA","PLAZUELA"));
		streetTypes.add(new SelectItem("PNTE ","PUENTE"));
		streetTypes.add(new SelectItem("POLIG","POLIGONO"));
		streetTypes.add(new SelectItem("PONT ","PONT"));
		streetTypes.add(new SelectItem("PONTE","PONTE"));
		streetTypes.add(new SelectItem("PORT ","PORT"));
		streetTypes.add(new SelectItem("PQUE ","PARQUE"));
		streetTypes.add(new SelectItem("PRAÑA","PRACIÑA"));
		streetTypes.add(new SelectItem("PRAGE","PARATGE"));
		streetTypes.add(new SelectItem("PRAIA","PRAIA"));
		streetTypes.add(new SelectItem("PRAJE","PARAJE"));
		streetTypes.add(new SelectItem("PRAZA","PRAZA"));
		streetTypes.add(new SelectItem("PROL ","PORLONGACION"));
		streetTypes.add(new SelectItem("PSAJE","PASAJE"));
		streetTypes.add(new SelectItem("PSAXE","PARAXE"));
		streetTypes.add(new SelectItem("PTDA ","PARTIDA"));
		streetTypes.add(new SelectItem("PTGE ","PASSATGE"));
		streetTypes.add(new SelectItem("PTILO","PORTILLO"));
		streetTypes.add(new SelectItem("PTJA ","PLATJA"));
		streetTypes.add(new SelectItem("PTLLO","PORTILLO"));
		streetTypes.add(new SelectItem("PZO  ","PASADIZO"));
		streetTypes.add(new SelectItem("PZTA ","PLAZOLETA"));
		streetTypes.add(new SelectItem("RABAL","RABAL"));
		streetTypes.add(new SelectItem("RACDA","RACONADA"));
		streetTypes.add(new SelectItem("RACO ","RACO"));
		streetTypes.add(new SelectItem("RAMAL","RAMAL"));
		streetTypes.add(new SelectItem("RAMPA","RAMPA"));
		streetTypes.add(new SelectItem("RAMPS","RAMPAS"));
		streetTypes.add(new SelectItem("RAVAL","RAVAL"));
		streetTypes.add(new SelectItem("RBLA ","RAMBLA"));
		streetTypes.add(new SelectItem("RBRA ","RIBERA"));
		streetTypes.add(new SelectItem("RCDA ","RINCONADA"));
		streetTypes.add(new SelectItem("RCON ","RINCON"));
		streetTypes.add(new SelectItem("RENTO","RECANTO"));
		streetTypes.add(new SelectItem("RIERA","RIERA"));
		streetTypes.add(new SelectItem("RONDA","RONDA"));
		streetTypes.add(new SelectItem("RTDA ","ROTONDA"));
		streetTypes.add(new SelectItem("RUA  ","RUA"));
		streetTypes.add(new SelectItem("RUELA","RUELA"));
		streetTypes.add(new SelectItem("RUERO","RUEIRO"));
		streetTypes.add(new SelectItem("SARBI","SARBIDE"));
		streetTypes.add(new SelectItem("SBIDA","SUBIDA"));
		streetTypes.add(new SelectItem("SECT ","SECTOR"));
		streetTypes.add(new SelectItem("SEDER","SENDER"));
		streetTypes.add(new SelectItem("SEDRA","SENDERA"));
		streetTypes.add(new SelectItem("SEKT ","SEKTORE"));
		streetTypes.add(new SelectItem("SEND ","SENDERO"));
		streetTypes.add(new SelectItem("SENDA","SENDA"));
		streetTypes.add(new SelectItem("SVTIA","SERVENTIA"));
		streetTypes.add(new SelectItem("TALDE","TALDE"));
		streetTypes.add(new SelectItem("TOKI ","TOKI"));
		streetTypes.add(new SelectItem("TRANS","TRANSITO"));
		streetTypes.add(new SelectItem("TRAS ","TRASERA"));
		streetTypes.add(new SelectItem("TRAV ","TRAVESSERA"));
		streetTypes.add(new SelectItem("TRRNT","TORRENT"));
		streetTypes.add(new SelectItem("TRSSI","TRAVESSIA"));
		streetTypes.add(new SelectItem("TRVA ","TRAVESIA"));
		streetTypes.add(new SelectItem("TRVAL","TRANSVERSAL"));
		streetTypes.add(new SelectItem("TRVSA","TRAVESA"));
		streetTypes.add(new SelectItem("TUNEL","TUNEL"));
		streetTypes.add(new SelectItem("URB  ","URBANIZACION"));
		streetTypes.add(new SelectItem("URBAT","URBANITZACIO"));
		streetTypes.add(new SelectItem("URBAZ","URBANIZAZIO"));
		streetTypes.add(new SelectItem("VALLE","VALLE"));
		streetTypes.add(new SelectItem("VCTO ","VIADUCTO"));
		streetTypes.add(new SelectItem("VEGA ","VEGA"));
		streetTypes.add(new SelectItem("VENAT","VEINAT"));
		streetTypes.add(new SelectItem("VENLA","VENELA"));
		streetTypes.add(new SelectItem("VIA  ","VIA"));
		streetTypes.add(new SelectItem("VIAL ","VIAL"));
		streetTypes.add(new SelectItem("VIANY","VIARANY"));
		streetTypes.add(new SelectItem("VREDA","VEREDA"));
		streetTypes.add(new SelectItem("XDIN ","XARDIN"));
		streetTypes.add(new SelectItem("ZEHAR","ZEARKALETA"));
		streetTypes.add(new SelectItem("ZONA ","ZONA"));
		streetTypes.add(new SelectItem("ZUBI ","ZUBI"));
		streetTypes.add(new SelectItem("ZUHAI","ZUHAIZTI"));
		streetTypes.add(new SelectItem("ZUMAR","ZUMARDI"));
	}
	
	public List<SelectItem> getStreetTypes() {
		return FiscalCollectionsController.streetTypes;
	}
}