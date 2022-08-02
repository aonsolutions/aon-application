package com.esferalia.aon.occam.mod200.api.model.mod200_2020;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.mod200.api.model.BalanceType;
import com.esferalia.aon.occam.mod200.api.model.DoubleVariableEx;
import com.esferalia.aon.occam.mod200.api.model.EcpnType;
import com.esferalia.aon.occam.mod200.api.model.GroupEntitie;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.MinorEntity;
import com.esferalia.aon.occam.mod200.api.model.Mod200;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyAdministrator;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyParticipation;
import com.esferalia.aon.occam.mod200.api.model.Secretary;
import com.esferalia.aon.occam.mod200.api.model.UteBase;
import com.esferalia.aon.occam.mod200.api.model.UteForeign;
import com.esferalia.aon.occam.mod200.api.model.UteParticipation;
import com.esferalia.aon.watson.util.AonMathUtils;

//public class Mod2002020 implements IFiscalModel {
public class Mod2002020 extends Mod200 {
	
	private static final long serialVersionUID = -5669862778411873677L;

	private boolean initializedFromLastYear;
	
//	private Integer id;
//	private int domain;
//	private int year;
//	private Administration administration = Administration.COMMON_TERRITORY;
//	private FiscalStatus status;
//	private boolean complementary;
//	private String receipt;	
//	private String complementaryReceipt;
//	private String comments;
//	private String resultType;
	
	private String cnae;
	
	private int periodType;
	private Date periodStart;
	private Date periodEnd;
	
	private int enterprise;
//	private String enterpriseDocument;
//	private String enterpriseName;
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
	private LinkedList<Mod200CompanyAdministrator> administrators = new LinkedList<Mod200CompanyAdministrator>();    // A. Relación de administradores 
	private LinkedList<Mod200CompanyParticipation> participationsOut = new LinkedList<Mod200CompanyParticipation>(); // B.1. Participaciones de la declarante en otras entidades 
	private LinkedList<Mod200CompanyParticipation> participationsIn = new LinkedList<Mod200CompanyParticipation>();  // B.2. Participaciones de personas o entidades en la declarante

	private LinkedList<MinorEntity> minorEntities = new LinkedList<MinorEntity>();  // C. Entidades menores dependientes de diócesis, provincia religiosa o entidad eclesiástica integradas en la declaración, previamente autorizadas
	
	private LinkedList<UteBase> uteBases = new LinkedList<UteBase>();
	private LinkedList<UteParticipation> uteParticipations = new LinkedList<UteParticipation>();
	private LinkedList<UteForeign> uteForeign = new LinkedList<UteForeign>();
	
	private LinkedList<GroupEntitie> groupEntities = new LinkedList<GroupEntitie>();  // NIF de las entidades del grupo 
	private LinkedList<String> establishments = new LinkedList<String>();	// NIF de los establecimientos permanentes, en caso de entidad titular
	
	private String devType;
	private String payType;
//	private Double amount;
	private String iban;
	private String bic;

	private String nrsAnexoIII;
	private String justCanarias;
	private String nrsAnexoIV;
	private String nrsAnexoV;
	private String nrsAnexoVric;
	private String justActivos;
	
	private HashMap<IMod200Key,DoubleVariableEx> keysMap = new HashMap<IMod200Key,DoubleVariableEx>();
	private HashMap<IMod200Key,DoubleVariableEx> draftMap = new HashMap<IMod200Key,DoubleVariableEx>();
	private HashMap<IMod200Key,Boolean> visibleMap = new HashMap<IMod200Key,Boolean>();

//	private LinkedList<ValidationMessage2020> messages;

	public boolean isInitializedFromLastYear() {
		return initializedFromLastYear;
	}
	public void setInitializedFromLastYear(boolean initializedFromLastYear) {
		this.initializedFromLastYear = initializedFromLastYear;
	}
	
//	@Override
//	public Integer getId() {
//		return id;
//	}	
//	public void setId(Integer id) {
//		this.id = id;
//	}
//	@Override
//	public int getYear() {
//		return year;
//	}
//	public void setYear(int year) {
//		this.year = year;
//	}
//	@Override
//	public int getDomain() {
//		return domain;
//	}
//	public void setDomain(int domain) {
//		this.domain = domain;
//	}
	public int getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(int enterprise) {
		this.enterprise = enterprise;
	}
//	@Override
//	public Administration getAdministration() {
//		return administration;
//	}
//	public void setAdministration(Administration administration) {
//		this.administration = administration;
//	}
//	public String getReceipt() {
//		return receipt;
//	}
//	public void setReceipt(String receipt) {
//		this.receipt = receipt;
//	}
//	@Override
//	public boolean isComplementary() {
//		return complementary;
//	}
//	public void setComplementary(boolean complementary) {
//		this.complementary = complementary;
//	}
//	public String getComplementaryReceipt() {
//		return complementaryReceipt;
//	}
//	public void setComplementaryReceipt(String complementaryReceipt) {
//		this.complementaryReceipt = complementaryReceipt;
//	}
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
//	public String getEnterpriseDocument() {
//		return enterpriseDocument;
//	}
//	public void setEnterpriseDocument(String enterpriseDocument) {
//		this.enterpriseDocument = enterpriseDocument;
//	}
//	public String getEnterpriseName() {
//		return enterpriseName;
//	}
//	public void setEnterpriseName(String enterpriseName) {
//		this.enterpriseName = enterpriseName;
//	}
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
	public LinkedList<Mod200CompanyAdministrator> getAdministrators() {
		return administrators;
	}
	public void setAdministrators(LinkedList<Mod200CompanyAdministrator> administrators) {
		this.administrators = administrators;
	}
	public LinkedList<Mod200CompanyParticipation> getParticipationsIn() {
		return participationsIn;
	}
	public void setParticipationsIn(LinkedList<Mod200CompanyParticipation> participationsIn) {
		this.participationsIn = participationsIn;
	}
	public LinkedList<Mod200CompanyParticipation> getParticipationsOut() {
		return participationsOut;
	}
	public void setParticipationsOut(LinkedList<Mod200CompanyParticipation> participationsOut) {
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
//	public String getResultType() {
//		return resultType;
//	}
//	public void setResultType(String resultType) {
//		this.resultType = resultType;
//	}
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
//	public Double getAmount() {
//		return amount;
//	}
//	public void setAmount(Double amount) {
//		this.amount = amount;
//	}
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
//	public String getComments() {
//		return comments;
//	}
//	public void setComments(String comments) {
//		this.comments = comments;
//	}
//	public LinkedList<ValidationMessage2020> getMessages() {
//		return messages;
//	}
//	public void setMessages(LinkedList<ValidationMessage2020> messages) {
//		this.messages = messages;
//	}
	
	@Override
	public FiscalModelType getModel() {
		return FiscalModelType.M200;
	}
	@Override
	public Period getPeriod() {
		return Period.YEAR;
	}
//	@Override
//	public FiscalStatus getStatus() {
//		// TODO Soporte!!
//		return this.status;
//	}
//	public void setStatus(FiscalStatus status) {
//		this.status = status;
//	}
	@Override
	public boolean isReplacement() {
		return false;
	}
//	@Override
//	public String getDocument() {
//		return enterpriseDocument;
//	}
//	@Override
//	public String getName() {
//		return enterpriseName;
//	}
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
	public String getNrsAnexoVric() {
		return nrsAnexoVric;
	}
	public void setNrsAnexoVric(String nrsAnexoV) {
		this.nrsAnexoVric = nrsAnexoV;
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
	
	public HashMap<IMod200Key, DoubleVariableEx> getKeysMap() {
		return keysMap;
	}
	
	public HashMap<IMod200Key, DoubleVariableEx> getDraftMap() {
		return draftMap;
	}
	
	public HashMap<IMod200Key, Boolean> getVisibleMap() {
		return visibleMap;
	}
	
	public void addDraftVariable(DoubleVariableEx t) {
		draftMap.put( t.getKey(), t);
    }
	
	public void addVariable(DoubleVariableEx t) {
		keysMap.put( t.getKey(), t);
	}
	
	public DoubleVariableEx getVariable(IMod200Key key) {
		DoubleVariableEx var = getDraftMap().get(key);
		if (var == null ) {
			var = getKeysMap().get(key);
		}
		return var; 
	}
	
	public DoubleVariableEx getKey(IMod200Key key) {
		return keysMap.get(key);
	}
	
	public Double getDoubleValue(IMod200Key key) {
		if (!keysMap.containsKey(key)){
			return 0.0;
		}
		DoubleVariableEx v = keysMap.get(key);
		if (v==null) {
			return 0.0;
		}
		Object o = v.getValue();
		if (o != null && o instanceof Double) {
			return (Double) o;
		}
		return 0.0;
	}
	
	public boolean isChecked(IMod200Key key) {
		DoubleVariableEx dv = keysMap.get(key); 
		return dv != null && (AonMathUtils.round(dv.getValue()) == 1.0);
	}
	
	public boolean isNotChecked(IMod200Key key) {
		return !isChecked(key);
	}
	
	public boolean isCooperativa() {
		return isChecked(Mod2002020Key.C0017) || isChecked(Mod2002020Key.C0018) || isChecked(Mod2002020Key.C0019);
	}
	
//	@Override
//	public Double getDeclarationResult() {
//		return null;
//	}
//	@Override
//	public FiscalModelDeclarationType getDeclarationResultType() {
//		return null;
//	}
	
	
}
