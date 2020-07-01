package com.esferalia.aon.occam.api.model.fiscal.mod200_2019;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyAdministrator;
import com.esferalia.aon.occam.api.model.CompanyParticipation;
import com.esferalia.aon.occam.api.model.GroupEntitie;
import com.esferalia.aon.occam.api.model.UteBase;
import com.esferalia.aon.occam.api.model.UteForeign;
import com.esferalia.aon.occam.api.model.UteParticipation;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.MinorEntity;
import com.esferalia.aon.occam.api.model.fiscal.Secretary;
import com.esferalia.aon.occam.api.model.fiscal.mod200.IMod200Key;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod2002019 implements IFiscalModel {
	
	private static final long serialVersionUID = 2233757579695504428L;
	
	public static enum BalanceType implements Serializable {
		NORMAL,
		ABREVIADO,
		PYMES;
	}
	public static enum EcpnType implements Serializable {
		NORMAL,
		ABREVIADO,
		PYMES,
		NO_CONSTA;
	}	
	
	private boolean initializedFromLastYear;
	
	private Integer id;
	private int domain;
	private int year;
	private Administration administration = Administration.COMMON_TERRITORY;
	private FiscalStatus status;
	
	private String receipt;
	private boolean complementary;
	private String complementaryReceipt;
	private String cnae;
	
	private int periodType;
	private Date periodStart;
	private Date periodEnd;
	
	private int enterprise;
	private String enterpriseDocument;
	private String enterpriseName;
	private String enterprisePhone1;
	private String enterprisePhone2;
	
	private String fiscalGroup;                   // Grupo - Claves 00009 ó 00010 - Nº de grupo fiscal
	private String dominantDocument;              // Grupo - Claves 00009 ó 00010 - N.I.F. de la sociedad representante/dominante (incluida en el grupo fiscal)
	private String dominantIdentificationNumber;  // Grupo - Clave 00010 - Nº identificación de la sociedad dominante (en el caso de grupos constituidos solo por entidades depend.)
	
	private String ultimateDocument;            // Grupo - Clave 00081 - Datos de la sociedad matriz última: NIF o equivalente.
	private Country ultimateDocumentCountry; 	// Grupo - Clave 00081 - Datos de la sociedad matriz última: Código país
    private String ultimateName;				// Grupo - Clave 00081 - Datos de la sociedad matriz última: Nombre o razón social
    private Country ultimateCountry; 			// Grupo - Clave 00081 - Datos de la sociedad matriz última: País o jurisdicción
	
	private BalanceType balanceType;
	private EcpnType ecpnType;
	private BalanceType pygType;
	
	private Secretary secretary = new Secretary(); // Secretario del Consejo de Administración, declarante o representante
	private LinkedList<LegalRepresentative> representatives = new LinkedList<LegalRepresentative>();     // Representantes legales de la entidad
	private LinkedList<CompanyAdministrator> administrators = new LinkedList<CompanyAdministrator>();    // Administradores 
	private LinkedList<CompanyParticipation> participationsOut = new LinkedList<CompanyParticipation>(); // B.1. Participaciones declarante en otras entidades
	private LinkedList<CompanyParticipation> participationsIn = new LinkedList<CompanyParticipation>();  // B.2. Participaciones de personas o entidades en la declarante

	private LinkedList<MinorEntity> minorEntities = new LinkedList<MinorEntity>();  // C. Entidades menores dependientes de diócesis, provincia religiosa o entidad eclesiástica integradas en la declaración, previamente autorizadas
	
	private LinkedList<UteBase> uteBases = new LinkedList<UteBase>();
	private LinkedList<UteParticipation> uteParticipations = new LinkedList<UteParticipation>();
	private LinkedList<UteForeign> uteForeign = new LinkedList<UteForeign>();
	
	private LinkedList<GroupEntitie> groupEntities = new LinkedList<GroupEntitie>();  // NIF de las entidades del grupo 
	private LinkedList<String> establishments = new LinkedList<String>();	// NIF de los establecimientos permanentes, en caso de entidad titular
	
	private String resultType;
	private String devType;
	private String payType;
	private Double amount;
	private String iban;
	private String bic;

	private String comments;

	private String nrsAnexoIII;
	private String justCanarias;
	private String nrsAnexoIV;
	private String nrsAnexoV;
	private String justActivos;
	
//	private EnumMap<Mod2002019Key,DoubleVariable2019> keysMap = new EnumMap<Mod2002019Key,DoubleVariable2019>(Mod2002019Key.class);
//	private EnumMap<Mod2002019Key,DoubleVariable2019> draftMap = new EnumMap<Mod2002019Key,DoubleVariable2019>(Mod2002019Key.class);
//	private EnumMap<Mod2002019Key,Boolean> visibleMap = new EnumMap<Mod2002019Key,Boolean>(Mod2002019Key.class);
	
	private HashMap<IMod200Key,DoubleVariable2019> keysMap = new HashMap<IMod200Key,DoubleVariable2019>();
	private HashMap<IMod200Key,DoubleVariable2019> draftMap = new HashMap<IMod200Key,DoubleVariable2019>();
	private HashMap<IMod200Key,Boolean> visibleMap = new HashMap<IMod200Key,Boolean>();

	private LinkedList<ValidationMessage2019> messages;

	public boolean isInitializedFromLastYear() {
		return initializedFromLastYear;
	}
	public void setInitializedFromLastYear(boolean initializedFromLastYear) {
		this.initializedFromLastYear = initializedFromLastYear;
	}
	
	@Override
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Override
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	@Override
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public int getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(int enterprise) {
		this.enterprise = enterprise;
	}
	@Override
	public Administration getAdministration() {
		return administration;
	}
	public void setAdministration(Administration administration) {
		this.administration = administration;
	}
	public String getReceipt() {
		return receipt;
	}
	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}
	@Override
	public boolean isComplementary() {
		return complementary;
	}
	public void setComplementary(boolean complementary) {
		this.complementary = complementary;
	}
	public String getComplementaryReceipt() {
		return complementaryReceipt;
	}
	public void setComplementaryReceipt(String complementaryReceipt) {
		this.complementaryReceipt = complementaryReceipt;
	}
	public String getCnae() {
		return cnae;
	}
	public void setCnae(String cnae) {
		this.cnae = cnae;
	}
	public int getPeriodType() {
		return periodType;
	}
	public void setPeriodType(int periodType) {
		this.periodType = periodType;
	}
	public Date getPeriodStart() {
		return periodStart;
	}
	public void setPeriodStart(Date periodStart) {
		this.periodStart = periodStart;
	}
	public Date getPeriodEnd() {
		return periodEnd;
	}
	public void setPeriodEnd(Date periodEnd) {
		this.periodEnd = periodEnd;
	}
	public String getEnterpriseDocument() {
		return enterpriseDocument;
	}
	public void setEnterpriseDocument(String enterpriseDocument) {
		this.enterpriseDocument = enterpriseDocument;
	}
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}
	public String getEnterprisePhone1() {
		return enterprisePhone1;
	}
	public void setEnterprisePhone1(String enterprisePhone1) {
		this.enterprisePhone1 = enterprisePhone1;
	}
	public String getEnterprisePhone2() {
		return enterprisePhone2;
	}
	public void setEnterprisePhone2(String enterprisePhone2) {
		this.enterprisePhone2 = enterprisePhone2;
	}
	public String getFiscalGroup() {
		return fiscalGroup;
	}
	public void setFiscalGroup(String fiscalGroup) {
		this.fiscalGroup = fiscalGroup;
	}
	public String getDominantDocument() {
		return dominantDocument;
	}
	public void setDominantDocument(String dominantDocument) {
		this.dominantDocument = dominantDocument;
	}

	public BalanceType getBalanceType() {
		return balanceType;
	}
	public void setBalanceType(BalanceType balanceType) {
		this.balanceType = balanceType;
	}
	public void setBalanceType(int index) {
		this.balanceType = BalanceType.values()[index];
	}
	
	public EcpnType getEcpnType() {
		return ecpnType;
	}
	public void setEcpnType(EcpnType ecpnType) {
		this.ecpnType = ecpnType;
	}
	public void setEcpnType(int index) {
		this.ecpnType = EcpnType.values()[index];
	}
	
	public BalanceType getPygType() {
		return pygType;
	}
	public void setPygType(BalanceType pygType) {
		this.pygType = pygType;
	}
	public void setPygType(int index) {
		this.pygType = BalanceType.values()[index];
	}
	
	public Secretary getSecretary() {
		return secretary;
	}
	public void setSecretary(Secretary secretary) {
		this.secretary = secretary;
	}
	public LinkedList<LegalRepresentative> getRepresentatives() {
		return representatives;
	}
	public void setRepresentatives(LinkedList<LegalRepresentative> representatives) {
		this.representatives = representatives;
	}
	public LinkedList<CompanyAdministrator> getAdministrators() {
		return administrators;
	}
	public void setAdministrators(LinkedList<CompanyAdministrator> administrators) {
		this.administrators = administrators;
	}
	public LinkedList<CompanyParticipation> getParticipationsIn() {
		return participationsIn;
	}
	public void setParticipationsIn(LinkedList<CompanyParticipation> participationsIn) {
		this.participationsIn = participationsIn;
	}
	public LinkedList<CompanyParticipation> getParticipationsOut() {
		return participationsOut;
	}
	public void setParticipationsOut(LinkedList<CompanyParticipation> participationsOut) {
		this.participationsOut = participationsOut;
	}
	public LinkedList<MinorEntity> getMinorEntities() {
		return minorEntities;
	}
	public void setMinorEntities(LinkedList<MinorEntity> minorEntities) {
		this.minorEntities = minorEntities;
	}
	public LinkedList<UteBase> getUteBases() {
		return uteBases;
	}
	public void setUteBases(LinkedList<UteBase> uteBases) {
		this.uteBases = uteBases;
	}
	public LinkedList<UteParticipation> getUteParticipations() {
		return uteParticipations;
	}
	public void setUteParticipations(LinkedList<UteParticipation> uteParticipations) {
		this.uteParticipations = uteParticipations;
	}
	public LinkedList<UteForeign> getUteForeign() {
		return uteForeign;
	}
	public void setUteForeign(LinkedList<UteForeign> uteForeign) {
		this.uteForeign = uteForeign;
	}
	public LinkedList<GroupEntitie> getGroupEntities() {
		return groupEntities;
	}
	public void setGroupEntities(LinkedList<GroupEntitie> groupEntities) {
		this.groupEntities = groupEntities;
	}
	public LinkedList<String> getEstablishments() {
		return establishments;
	}
	public void setEstablishments(LinkedList<String> establishments) {
		this.establishments = establishments;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getDevType() {
		return devType;
	}
	public void setDevType(String devType) {
		this.devType = devType;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getIban() {
		return iban;
	}
	public void setIban(String iban) {
		this.iban = iban;
	}
	public String getBic() {
		return bic;
	}
	public void setBic(String bic) {
		this.bic = bic;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public LinkedList<ValidationMessage2019> getMessages() {
		return messages;
	}
	public void setMessages(LinkedList<ValidationMessage2019> messages) {
		this.messages = messages;
	}
	
	@Override
	public FiscalModelType getModel() {
		return FiscalModelType.M200;
	}
	@Override
	public Period getPeriod() {
		return Period.YEAR;
	}
	@Override
	public FiscalStatus getStatus() {
		// TODO Soporte!!
		return this.status;
	}
	public void setStatus(FiscalStatus status) {
		this.status = status;
	}
	@Override
	public boolean isReplacement() {
		return false;
	}
	@Override
	public String getDocument() {
		return enterpriseDocument;
	}
	@Override
	public String getName() {
		return enterpriseName;
	}
	@Override
	public String getFullName() {
		return getName();
	}
	@Override
	public String getSurname() {
		return null;
	}
	@Override
	public String getDomainName() {
		return null;
	}
	@Override
	public IFiscalModelKey getDeclarationTypeKey() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public double getResult() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String getDominantIdentificationNumber() {
		return dominantIdentificationNumber;
	}
	public void setDominantIdentificationNumber(String dominantIdentificationNumber) {
		this.dominantIdentificationNumber = dominantIdentificationNumber;
	}
	
	public String getNrsAnexoIII() {
		return nrsAnexoIII;
	}
	public void setNrsAnexoIII(String nrsAnexoIII) {
		this.nrsAnexoIII = nrsAnexoIII;
	}
	public String getJustCanarias() {
		return justCanarias;
	}
	public void setJustCanarias(String justCanarias) {
		this.justCanarias = justCanarias;
	}
	public String getNrsAnexoIV() {
		return nrsAnexoIV;
	}
	public void setNrsAnexoIV(String nrsAnexoIV) {
		this.nrsAnexoIV = nrsAnexoIV;
	}
	public String getNrsAnexoV() {
		return nrsAnexoV;
	}
	public void setNrsAnexoV(String nrsAnexoV) {
		this.nrsAnexoV = nrsAnexoV;
	}
	public String getJustActivos() {
		return justActivos;
	}
	public void setJustActivos(String justActivos) {
		this.justActivos = justActivos;
	}
	public String getUltimateDocument() {
		return ultimateDocument;
	}
	public void setUltimateDocument(String ultimateDocument) {
		this.ultimateDocument = ultimateDocument;
	}
	public Country getUltimateDocumentCountry() {
		return ultimateDocumentCountry;
	}
	public void setUltimateDocumentCountry(Country ultimateDocumentCountry) {
		this.ultimateDocumentCountry = ultimateDocumentCountry;
	}
	public String getUltimateName() {
		return ultimateName;
	}
	public void setUltimateName(String ultimateName) {
		this.ultimateName = ultimateName;
	}
	public Country getUltimateCountry() {
		return ultimateCountry;
	}
	public void setUltimateCountry(Country ultimateCountry) {
		this.ultimateCountry = ultimateCountry;
	}
	
//	public EnumMap<Mod2002019Key, DoubleVariable2019> getKeysMap() {
//	return keysMap;
//}
//public void setKeysMap(EnumMap<Mod2002019Key, DoubleVariable2019> keysMap) {
//	this.keysMap = keysMap;
//}
//public EnumMap<Mod2002019Key, DoubleVariable2019> getDraftMap() {
//	return draftMap;
//}
//public void setDraftMap(EnumMap<Mod2002019Key, DoubleVariable2019> draftMap) {
//	this.draftMap = draftMap;
//}
//public EnumMap<Mod2002019Key, Boolean> getVisibleMap() {
//	return visibleMap;
//}
//public void setVisibleMap(EnumMap<Mod2002019Key, Boolean> visibleMap) {
//	this.visibleMap = visibleMap;
//}
//	
//	public DoubleVariable2019 getKey(Mod2002019Key key) {
//		return keysMap.get(key);
//	}
//	public boolean isChecked(Mod2002019Key key) {
//		DoubleVariable2019 dv = keysMap.get(key); 
//		return dv != null && (AonMathUtils.round(dv.getValue()) == 1.0);
//	}
//	public boolean isNotChecked(Mod2002019Key key) {
//		return !isChecked(key);
//	}
//	public boolean isCooperativa() {
//		return isChecked(Mod2002019Key.C0017) || isChecked(Mod2002019Key.C0018) || isChecked(Mod2002019Key.C0019);
//	}
//	
//	public void addVariable(Mod2002019Key key, Number d) {
//		DoubleVariable2019 dv = new DoubleVariable2019(key);
//		dv.setValue(d==null?0:d.doubleValue());
//		keysMap.put( dv.getKey(),dv );
//	}
//
//	public void addVariable(DoubleVariable2019 t) {
//		keysMap.put( t.getKey(), t);
//	}
//	public void removeVariable(DoubleVariable2019 t) {
//		keysMap.remove(t.getKey());
//	}
//	
//	public void addDraftVariable(DoubleVariable2019 t) {
//		draftMap.put( t.getKey(), t);
//	}
//	public void removeDraftariable(DoubleVariable2019 t) {
//		draftMap.remove(t.getKey());
//	}
//
//	public DoubleVariable2019 getVariable(Mod2002019Key key) {
//		DoubleVariable2019 var = getDraftMap().get(key);
//		if (var == null ) {
//			var = getKeysMap().get(key);
//		}
//		return var; 
//	}
//
//	public Double getDoubleValue(Mod2002019Key key) {
//		if (!keysMap.containsKey(key)){
//			//return new Double(0);
//			return 0.0;
//		}
//		DoubleVariable2019 v = keysMap.get(key);
//		if (v==null) {
//			//return new Double(0);
//			return 0.0;
//		}
//		Object o = v.getValue();
//		if (o != null && o instanceof Double) {
//			return (Double) o;
//		}
//		//return new Double(0);
//		return 0.0;
//	}
//	public void listDraftVariables() {
//		for (DoubleVariable2019 dv : getDraftMap().values()) {
//			System.out.println(dv.getKey() + " --> " + dv.getValue() );
//		}
//		
//	}
	
	public HashMap<IMod200Key, DoubleVariable2019> getKeysMap() {
		return keysMap;
	}
	
	public HashMap<IMod200Key, DoubleVariable2019> getDraftMap() {
		return draftMap;
	}
	
	public HashMap<IMod200Key, Boolean> getVisibleMap() {
		return visibleMap;
	}
	
	public void addDraftVariable(DoubleVariable2019 t) {
		draftMap.put( t.getKey(), t);
    }
	
	public void addVariable(DoubleVariable2019 t) {
		keysMap.put( t.getKey(), t);
	}
	
	public DoubleVariable2019 getVariable(IMod200Key key) {
		DoubleVariable2019 var = getDraftMap().get(key);
		if (var == null ) {
			var = getKeysMap().get(key);
		}
		return var; 
	}
	
	public DoubleVariable2019 getKey(IMod200Key key) {
		return keysMap.get(key);
	}
	
	public Double getDoubleValue(IMod200Key key) {
		if (!keysMap.containsKey(key)){
			//return new Double(0);
			return 0.0;
		}
		DoubleVariable2019 v = keysMap.get(key);
		if (v==null) {
			//return new Double(0);
			return 0.0;
		}
		Object o = v.getValue();
		if (o != null && o instanceof Double) {
			return (Double) o;
		}
		//return new Double(0);
		return 0.0;
	}
	
	public boolean isChecked(IMod200Key key) {
		DoubleVariable2019 dv = keysMap.get(key); 
		return dv != null && (AonMathUtils.round(dv.getValue()) == 1.0);
	}
	
	public boolean isNotChecked(IMod200Key key) {
		return !isChecked(key);
	}
	
	public boolean isCooperativa() {
		return isChecked(Mod2002019Key.C0017) || isChecked(Mod2002019Key.C0018) || isChecked(Mod2002019Key.C0019);
	}
	
}
