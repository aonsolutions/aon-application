package com.esferalia.aon.occam.mod200.api.model.mod200_2023;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
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

public class Mod2002023 extends Mod200 {
	
	private static final long serialVersionUID = 3919318555240470290L;

	private boolean initializedFromLastYear;
	
	private String cnae;  
	
	private int periodType;
	private Date periodStart;
	private Date periodEnd;
	
	private int enterprise;
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
	
	private Secretary secretary = new Secretary();    // Secretario del Consejo de Administración, declarante o representante
	                                                  // FALTA - LA FECHA IRNR NO ESTA EN EL MODELO
	
	private LinkedList<LegalRepresentative> representatives = new LinkedList<LegalRepresentative>();                 // Representantes legales de la entidad
	private LinkedList<Mod200CompanyAdministrator> administrators = new LinkedList<Mod200CompanyAdministrator>();    // A. Relación de administradores 
	private LinkedList<Mod200CompanyParticipation> participationsOut = new LinkedList<Mod200CompanyParticipation>(); // B1. Participaciones directas de la declarante en otras sociedades a la fecha de cierre del período declarado
	private LinkedList<Mod200CompanyParticipation> participationsIn = new LinkedList<Mod200CompanyParticipation>();  // B2. Participaciones personas o entidades en la declarante a la fecha de cierre del período declarado
	private LinkedList<MinorEntity> minorEntities = new LinkedList<MinorEntity>();                                   // C. Entidades menores dependientes de diócesis, provincia religiosa o entidad eclesiástica integradas en la declaración, previamente autorizadas
	private LinkedList<UteForeign> uteForeign = new LinkedList<UteForeign>();                                        // D. Información de detalle de EP o UTE que operen en el extranjero y por participación en fórmula de colaboración análoga a UTE 
	private LinkedList<UteBase> uteBases = new LinkedList<UteBase>();                              // UTES - Deducción para evitar la doble imposición
	private LinkedList<UteParticipation> uteParticipations = new LinkedList<UteParticipation>();   // UTES - Relación de socios
	private LinkedList<GroupEntitie> groupEntities = new LinkedList<GroupEntitie>();               // Grupos de Sociedades- NIF de las entidades del grupo 
	private LinkedList<String> establishments = new LinkedList<String>();	                       // No residentes - NIF de los establecimientos permanentes, en caso de entidad titular
	private LinkedList<String> filmProductions = new LinkedList<String>();                         // Información adicional producciones cinematográficas españolas y espectáculos en vivo
	private LinkedList<String> sicav1 = new LinkedList<String>(); // E. Socios de SICAV en régimen especial de disolución y liquidación - NIF de la sociedad/es disuelta/s
	private LinkedList<String> sicav2 = new LinkedList<String>(); // E. Socios de SICAV en régimen especial de disolución y liquidación - NIF de la/las IIC donde reinvierte
	
	// FALTA - NUEVO APARTADO EN UTES - Partícipes de agrupaciones de interés económico y UTES
	
	private String devType;
	private String payType;
	private String iban;
	private String bic;

	private String nrsAnexoIII;
	private String justCanarias;
	private String nrsAnexoIV;
	private String nrsAnexoV;
	private String nrsAnexoVric;
	private String justActivos;
	// FALTA - NUEVAS CASILLAS PARA PRESENTACION DE DOCUMENTACION (HABRA QUE AÑADIR CAMPOS EN fs_model200
	// Documentación presentada por el Anexo VI (RIIB: Inversiones anticipadas)
	// Número de justificante identificativo de la declaración informativa de ayudas Régimen Económico y Fiscal de Illes Balears
	
	private HashMap<IMod200Key,DoubleVariableEx> keysMap = new HashMap<IMod200Key,DoubleVariableEx>();
	private HashMap<IMod200Key,DoubleVariableEx> draftMap = new HashMap<IMod200Key,DoubleVariableEx>();
	private HashMap<IMod200Key,Boolean> visibleMap = new HashMap<IMod200Key,Boolean>();

	public boolean isInitializedFromLastYear() {
		return initializedFromLastYear;
	}
	public void setInitializedFromLastYear(boolean initializedFromLastYear) {
		this.initializedFromLastYear = initializedFromLastYear;
	}
	public int getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(int enterprise) {
		this.enterprise = enterprise;
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
	public LinkedList<String> getFilmProductions() {
		return filmProductions;
	}
	public void setFilmProductions(LinkedList<String> filmProductions) {
		this.filmProductions = filmProductions;		
	}
	public LinkedList<String> getSicav1() {
		return sicav1;
	}
	public void setSicav1(LinkedList<String> sicav1) {
		this.sicav1 = sicav1;		
	}
	public LinkedList<String> getSicav2() {
		return sicav2;
	}
	public void setSicav2(LinkedList<String> sicav2) {
		this.sicav2 = sicav2;		
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
	
	@Override
	public FiscalModelType getModel() {
		return FiscalModelType.M200;
	}
	@Override
	public Period getPeriod() {
		return Period.YEAR;
	}
	@Override
	public boolean isReplacement() {
		return false;
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
		DoubleVariableEx v = getDraftMap().get(key);
		if (v == null ) {
			v = getKeysMap().get(key);
		}
		return v; 
	}
	
	public DoubleVariableEx getKey(IMod200Key key) {
		return keysMap.get(key);
	}
	
	public Double getDoubleValue(IMod200Key key) {
		if (!keysMap.containsKey(key)){
			return 0.0;
		}
		DoubleVariableEx v = keysMap.get(key);
		if (v == null) {
			return 0.0;
		}
		Object o = v.getValue();
		if (o != null && o instanceof Double) {
			return (Double) o;
		}
		return 0.0;
	}

	public void setDoubleValue(IMod200Key key, Double value) {
		if (value == null)
			value = 0.0;
		DoubleVariableEx dv = new DoubleVariableEx(key);
		dv.setValue(value);
		addVariable(dv);
	}
	
	public Boolean getBooleanValue(IMod200Key key) {
		return (getDoubleValue(key) == 1.0);
	}
	
	public void setBooleanValue(IMod200Key key, Boolean value) {
		if (value == null)
			value = false;
		double doubleValue = value?1.0:0.0;
		setDoubleValue(key, doubleValue);
	}
	
	public boolean isChecked(IMod200Key key) {
		DoubleVariableEx dv = keysMap.get(key); 
		return dv != null && (AonMathUtils.round(dv.getValue()) == 1.0);
	}
	
	public boolean isNotChecked(IMod200Key key) {
		return !isChecked(key);
	}
	
	public boolean isCooperativa() {
		return isChecked(Mod2002023Key.C0017) || isChecked(Mod2002023Key.C0018) || isChecked(Mod2002023Key.C0019);
	}
	
	@Override
	public boolean isStrictToDeposit() {
		return (canBeSent() || isSent()) && ("I".equals(getPayType()));
	}
	
}
