package com.esferalia.aon.occam.api.model.fiscal;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod369 implements IFiscalModel, HasAudit {

	private static final long serialVersionUID = 7141412199571798830L;
	
	private Integer id;
	private int domain;	
	private int year;
	private Period period;	
	private Administration administration;
	private String comments;
	private FiscalStatus status;  
	private boolean confidential;	
	private String document;
	private String name;
	private String domainName;
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	private Integer fsModel;
	private Mod369Regime regime;       // Régimen 
	private Mod369PayType payType;     // Tipo de Pago 
	private String nrc;                // NRC Pago
	private Double amountPaid;         // Importe pagado
	private Country country;           // Declarante - País
	private String operatorNumber;     // Número de operador en el régimen
	private boolean withoutActivity;   // Declaración sin actividad
	private boolean intermediary;      // Actúa a través de intermediario
	private String intermediaryNumber; // Número de identificación del intermediario
	private Date fromDate;             // Fecha desde 
	private Date toDate;               // Fecha hasta
	
// FALTA - POR AHORA VOY A PONER SOLO UN TIPO DE DETALLE, SEGUN EL REGIMEN PUEDE HABER MAS
//	
//	Régimen de la Unión:
//	3. Prestaciones de servicios desde el EMID (España) y desde establecimientos permanentes situados fuera de la UE. (details3)
//	4. Entregas de bienes expedidos o transportados desde EMID España. (details4)
//	5. Prestaciones de servicios desde establecimientos permanentes en otros EM distintos de España. (details5)
//	6. Entregas de bienes expedidos o transportados desde otros EM distintos de España. (details6)
//	7. Correcciones de declaraciones de períodos anteriores (máx. 3 años). (corrections) 
//	
//	Régimen Exterior a la Unión:
//	3. Prestaciones de servicios. (details3)
//	4. Correcciones de declaraciones de períodos anteriores (máx. 3 años). (corrections) 
//
//	Régimen de importación:	
//	3. Importaciones de bienes de menos de 150 euros. (details3) 
//	4. Correcciones de declaraciones de períodos anteriores (máx. 3 años). (corrections)
	
	private LinkedList<Mod369Detail> details3;
	private LinkedList<Mod369Detail> details4;              // Régimen de la Unión - 4. Entregas de bienes expedidos o transportados desde EMID España.
	private LinkedList<Mod369DetailOther> details5;         // Régimen de la Unión - 5. Prestaciones de servicios desde establecimientos permanentes en otros EM distintos de España.
	private LinkedList<Mod369DetailOther> details6;         // Régimen de la Unión - 6. Entregas de bienes expedidos o transportados desde otros EM distintos de España.
	private LinkedList<Mod369DetailCorrection> corrections; // Correcciones de declaraciones de períodos anteriores
	
	@Override
	public Integer getId() { 
		return id;
	}

	public Mod369 setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public boolean isNew() {
		return id==null;
	}

	@Override
	public int getDomain() {
		return domain;
	}

	public Mod369 setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	@Override
	public String getDomainName() {
		return domainName;
	}
	public Mod369 setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	@Override
	public int getYear() {
		return year;
	}

	public Mod369 setYear(int year) {
		this.year = year;
		return this;
	}
	
	@Override
	public Period getPeriod() {
		return period;
	}

	public Mod369 setPeriod(Period period) {
		this.period = period;
		return this;
	}

	@Override
	public Administration getAdministration() {
		return administration;
	}

	public Mod369 setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}

	public boolean isConfidential() {
		return confidential;
	}
	
	public Mod369 setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public Mod369 setComments(String comments) {
		this.comments = comments;
		return this;
	}

	@Override
	public String getDocument() {
		return document;
	}

	public Mod369 setDocument(String document) {
		this.document = document;
		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	public Mod369 setName(String name) {
		this.name = name;
		return this;
	}
	
	public FiscalStatus getStatus() {
		return status;
	}

	public Mod369 setStatus(FiscalStatus status) {
		this.status = status;
		return this;
	}
	
	@Override
	public String getFullName() {
		return name;
	}
	
	@Override
	public double getResult() {
		return 0;
	}
	
	@Override
	public IFiscalModelKey getDeclarationTypeKey() {
		return null;
	}
	
	@Override
	public String getSurname() {
		return null;
	}
	
	@Override
	public FiscalModelType getModel() {
		return FiscalModelType.M369;
	}
	
	// ---------------------------------------------------------- AUDIT
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Mod369 setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	public Mod369 setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Mod369 setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	public Mod369 setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	@Override
	public Double getDeclarationResult() {
		return null;
	}
	@Override
	public FiscalModelDeclarationType getDeclarationResultType() {
		return null;
	}

	public Integer getFsModel() {
		return fsModel;
	}

	public Mod369 setFsModel(Integer fsModel) {
		this.fsModel = fsModel;
		return this;
	}

	public Mod369Regime getRegime() {
		return regime;
	}

	public Mod369 setRegime(Mod369Regime regime) {
		this.regime = regime;
		return this;
	}

	public Mod369PayType getPayType() {
		return payType;
	}

	public Mod369 setPayType(Mod369PayType payType) {
		this.payType = payType;
		return this;
	}

	@Override
	public String getNrc() {
		return nrc;
	}

	@Override
	public Mod369 setNrc(String nrc) {
		this.nrc = nrc;
		return this;
	}

	public Double getAmountPaid() {
		return amountPaid;
	}

	public Mod369 setAmountPaid(Double amountPaid) {
		this.amountPaid = amountPaid;
		return this;
	}

	public Country getCountry() {
		return country;
	}

	public Mod369 setCountry(Country country) {
		this.country = country;
		return this;
	}

	public String getOperatorNumber() {
		return operatorNumber;
	}

	public Mod369 setOperatorNumber(String operatorNumber) {
		this.operatorNumber = operatorNumber;
		return this;
	}

	public boolean isWithoutActivity() {
		return withoutActivity;
	}

	public Mod369 setWithoutActivity(boolean withoutActivity) {
		this.withoutActivity = withoutActivity;
		return this;
	}

	public boolean isIntermediary() {
		return intermediary;
	}

	public Mod369 setIntermediary(boolean intermediary) {
		this.intermediary = intermediary;
		return this;
	}

	public String getIntermediaryNumber() {
		return intermediaryNumber;
	}

	public Mod369 setIntermediaryNumber(String intermediaryNumber) {
		this.intermediaryNumber = intermediaryNumber;
		return this;
	}

	@Override
	public boolean isReplacement() {		
		return false;
	}

	@Override
	public boolean isComplementary() {
		return false;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public Mod369 setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public Date getToDate() {
		return toDate;
	}

	public Mod369 setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}
	
	public LinkedList<Mod369Detail> getDetails3() {
		if (details3 == null) {
			details3 = new LinkedList<Mod369Detail>();
		}
		return details3;
	}
	
	public Mod369 setDetails3(LinkedList<Mod369Detail> details3) {
		this.details3 = details3;
		return this;
	}
	
	public LinkedList<Mod369Detail> getDetails4() {
		if (details4 == null) {
			details4 = new LinkedList<Mod369Detail>();
		}
		return details4;
	}
	
	public Mod369 setDetails4(LinkedList<Mod369Detail> details4) {
		this.details4 = details4;
		return this;
	}
	
	public LinkedList<Mod369DetailOther> getDetails5() {
		if (details5 == null) {
			details5 = new LinkedList<Mod369DetailOther>();
		}
		return details5;
	}
	
	public Mod369 setDetails5(LinkedList<Mod369DetailOther> details5) {
		this.details5 = details5;
		return this;
	}
	
	public LinkedList<Mod369DetailOther> getDetails6() {
		if (details6 == null) {
			details6 = new LinkedList<Mod369DetailOther>();
		}
		return details5;
	}
	
	public Mod369 setDetails6(LinkedList<Mod369DetailOther> details6) {
		this.details6 = details6;
		return this;
	}
	
	public LinkedList<Mod369DetailCorrection> getCorrections() {
		if (corrections == null) {
			corrections = new LinkedList<Mod369DetailCorrection>();
		}
		return corrections;
	}
	
	public Mod369 setCorrections(LinkedList<Mod369DetailCorrection> corrections) {
		this.corrections = corrections;
		return this;
	}

}
