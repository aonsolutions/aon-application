package aon.sepe.objects;

import java.util.Date;
import java.util.Optional;

public class Contract {
	private String sepeId;
	private String cifEnterprise;
	private String regimen;
	private String ctaCti;
	private String nss;
	private String ipf;
	private String name;
	private SexType sex;
	private String surname;
	private String lastSurname;
	private Integer codNationality;
	private Integer codPaisDom;
	private String codMunDom;
	private Integer codFormativo;
	private String codOccupation;
	private Integer codPaisWork;
	private String codMunWork;
	private String codContract;
	private Date dateIniContract;
	private Date dateFinContract;
	private Date dateBirth;
	private Date dateComContract;
	private OfferType offer;
	private JndType jndType;
	private String durationTypeJndHour;
	private String durationTypeJndMin;
	private String durationTypeCvnHour;
	private String durationTypeCvnMin;
	private String interinidad;
	private String titulacion;

	private boolean discontinuo; // ¿Realiza trabajos fijos discontinuos o periódicos que se repiten en fechas ciertas?
	private boolean previsible; // ¿ El contrato tiene una duracion igual o inferior a 90 dias, situacion previsible ?
	private boolean certificateProfessional; // ¿ El trabajador tiene Certificado de profesionalidad?
	
	 // Para las transformaciones
	private DiscontinuoReason discontinuoReason; 
	private Date oldDateIniContract; 
	private Date oldDateFinContract;
	
	public String getSepeId() {
		return sepeId;
	}
	
	public String getCtaCti() {
		return ctaCti;
	}
	
	public Date getDateBirth() {
		return dateBirth;
	}
	
	public Date getDateComContract() {
		return dateComContract;
	}

	public String getNss() {
		return nss;
	}
	
	public String getCifEnterprise() {
		return cifEnterprise;
	}

	public String getCodContract() {
		return codContract;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getIpf() {
		return ipf;
	}

	public String getRegimen() {
		return regimen;
	}

	public String getLastSurname() {
		return lastSurname;
	}

	public Integer getCodNationality() {
		return codNationality;
	}

	public SexType getSex() {
		return sex;
	}

	public Integer getCodPaisDom() {
		return codPaisDom;
	}

	public String getCodMunDom() {
		return codMunDom;
	}

	public Integer getCodFormativo() {
		return codFormativo;
	}
	
	public Optional<String> getTitulacion() {
		return Optional.ofNullable(titulacion);
	}

	public String getCodOccupation() {
		return codOccupation;
	}

	public Integer getCodPaisWork() {
		return codPaisWork;
	}

	public String getCodMunWork() {
		return codMunWork;
	}

	public Date getDateIniContract() {
		return dateIniContract;
	}
	
	public Date getOldDateIniContract() {
		return oldDateIniContract;
	}
	
	public Date getOldDateFindContract() {
		return oldDateFinContract;
	}

	public Date getDateFinContract() {
		return dateFinContract;
	}

	public OfferType getOffer() {
		return offer;
	}

	public JndType getJndType() {
		return jndType;
	}

	public DiscontinuoReason getDiscontinuoReason() {
		return discontinuoReason;
	}

	public String getDurationTypeJndHour() {
		return durationTypeJndHour;
	}

	public String getDurationTypeJndMin() {
		return durationTypeJndMin;
	}

	public String getDurationTypeCvnHour() {
		return durationTypeCvnHour;
	}

	public String getDurationTypeCvnMin() {
		return durationTypeCvnMin;
	}
	
	public Optional<String> getInterinidad() {
		return Optional.ofNullable(interinidad);
	}
	
	public boolean getPrevisible() {
		return previsible;
	}
	
	public boolean getCertificateProfessional() {
		return certificateProfessional;
	}
	
	
	public boolean getDiscontinuo() {
		return discontinuo;
	}
	
	public void setDiscontinuo(boolean discontinuo) {
		this.discontinuo = discontinuo;
	}

	private Contract() {
	}

	public static class ContractBuilder {
		private String sepeId;
		private String cifEnterprise;
		private String regimen;
		private String ctaCti;
		private String nss;
		private String ipf;
		private String name;
		private String surname;
		private String lastSurname;
		private SexType sex;
		private Integer codNationality;
		private Integer codPaisDom;
		private String codMunDom;
		private Integer codFormativo;
		private String codOccupation;
		private Integer codPaisWork;
		private String codMunWork;
		private String codContract;
		private Date dateIniContract;
		private Date dateFinContract;
		private Date dateBirth;
		private Date dateComContract;
		private OfferType offer;
		private JndType jndType;
		private DiscontinuoReason discontinuoReason;
		private String durationTypeJndHour;
		private String durationTypeJndMin;
		private String durationTypeCvnHour;
		private String durationTypeCvnMin;
		private String interinidad;
		private String titulacion;
		
		private boolean discontinuo; // ¿Realiza trabajos fijos discontinuos o periódicos que se repiten en fechas ciertas?
		private boolean previsible; // ¿El contrato tiene una duracion igual o inferior a 90 dias, situacion previsible ?
		private boolean certificateProfessional; // ¿ El trabajador tiene Certificado de profesionalidad?
		
		// Para la transformacion
		private Date oldDateIniContract; 
		private Date oldDateFinContract;
		
		public ContractBuilder() { /* TODO document why this constructor is empty */ }
		
		public ContractBuilder setCifEnterprise(String cifEnterprise) {
			this.cifEnterprise = cifEnterprise;
			return this;
		}

		public ContractBuilder setCtaCti(String ctaCti) {
			this.ctaCti = ctaCti;
			return this;
		}
		
		public ContractBuilder setSepeId(String sepeId) {
			this.sepeId = sepeId;
			return this;
		}


		public ContractBuilder setNss(String nss) {
			this.nss = nss;
			return this;
		}

		public ContractBuilder setIpf(String ipf) {
			this.ipf = ipf;
			return this;
		}

		public ContractBuilder setSex(SexType sex) {
			this.sex = sex;
			return this;
		}

		public ContractBuilder setCodContract(String codContract) {
			this.codContract = codContract;
			return this;
		}

		public ContractBuilder setTitulacion(String titulacion) {
			this.titulacion = titulacion;
			return this;
		}
		
		public ContractBuilder setRegimen(String regimen) {
			this.regimen = regimen;
			return this;
		}

		public ContractBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public ContractBuilder setSurname(String surname) {
			this.surname = surname;
			return this;
		}

		public ContractBuilder setLastSurname(String lastSurname) {
			this.lastSurname = lastSurname;
			return this;
		}

		public ContractBuilder setCodNationality(Integer codNationality) {
			this.codNationality = codNationality;
			return this;
		}

		public ContractBuilder setCodPaisDom(Integer codPaisDom) {
			this.codPaisDom = codPaisDom;
			return this;
		}

		public ContractBuilder setCodMunDom(String codMunDom) {
			this.codMunDom = codMunDom;
			return this;
		}

		public ContractBuilder setCodFormativo(Integer codFormativo) {
			this.codFormativo = codFormativo;
			return this;
		}

		public ContractBuilder setCodOccupation(String codOccupation) {
			this.codOccupation = codOccupation;
			return this;
		}

		public ContractBuilder setCodPaisWork(Integer codPaisWork) {
			this.codPaisWork = codPaisWork;
			return this;
		}

		public ContractBuilder setCodMunWork(String codMunWork) {
			this.codMunWork = codMunWork;
			return this;
		}

		public ContractBuilder setDateIniContract(Date dateIniContract) {
			this.dateIniContract = dateIniContract;
			return this;
		}
		
		public ContractBuilder setOldDateIniContract(Date oldDateIniContract) {
			this.oldDateIniContract = oldDateIniContract;
			return this;
		}
		
		public ContractBuilder setOldDateFinContract(Date oldDateFinContract) {
			this.oldDateFinContract = oldDateFinContract;
			return this;
		}

		public ContractBuilder setDateFinContract(Date dateFinContract) {
			this.dateFinContract = dateFinContract;
			return this;
		}
		
		public ContractBuilder setDateBirth(Date dateBirth) {
			this.dateBirth = dateBirth;
			return this;
		}
		
		public ContractBuilder setDateComContract(Date dateComContract) {
			this.dateComContract = dateComContract;
			return this;
		}
	
		public ContractBuilder setOffer(OfferType offer) {
			this.offer = offer;
			return this;
		}

		public ContractBuilder setJndType(JndType jndType) {
			this.jndType = jndType;
			return this;
		}

		public ContractBuilder setDiscontinuoReason(DiscontinuoReason discontinuoReason) {
			this.discontinuoReason = discontinuoReason;
			return this;
		}

		public ContractBuilder setDurationTypeJndHour(String durationTypeJndHour) {
			this.durationTypeJndHour = durationTypeJndHour;
			return this;
		}

		public ContractBuilder setDurationTypeJndMin(String durationTypeJndMin) {
			this.durationTypeJndMin = durationTypeJndMin;
			return this;
		}

		public ContractBuilder setDurationTypeCvnHour(String durationTypeCvnHour) {
			this.durationTypeCvnHour = durationTypeCvnHour;
			return this;
		}

		public ContractBuilder setDurationTypeCvnMin(String durationTypeCvnMin) {
			this.durationTypeCvnMin = durationTypeCvnMin;
			return this;
		}
		
		public ContractBuilder setInterinidad(String interinidad) {
			this.interinidad = interinidad;
			return this;
		}
		
		public ContractBuilder setDiscontinuo(boolean discontinuo) {
			this.discontinuo = discontinuo;
			return this;
		}
		
		
		public ContractBuilder setPrevisible(boolean previsible) {
			this.previsible = previsible;
			return this;
		}
		
		public ContractBuilder setCertificateProfessional(boolean certificateProfessional) {
			this.certificateProfessional = certificateProfessional;
			return this;
		}

		public Contract build() {
			Contract contract = new Contract();
			contract.cifEnterprise = this.cifEnterprise;
			contract.regimen = this.regimen;
			contract.ctaCti = this.ctaCti;
			contract.nss = this.nss;
			contract.name = this.name;
			contract.dateBirth = this.dateBirth;
			contract.ipf = this.ipf;
			contract.surname = this.surname;
			contract.lastSurname = this.lastSurname;
			contract.sex = this.sex;
			contract.codNationality = this.codNationality;
			contract.codPaisDom = this.codPaisDom;
			contract.codMunDom = this.codMunDom;
			contract.codFormativo = this.codFormativo;
			contract.codOccupation = this.codOccupation;
			contract.codPaisWork = this.codPaisWork;
			contract.codMunWork = this.codMunWork;
			contract.dateIniContract = this.dateIniContract;
			contract.dateComContract = this.dateComContract;
			contract.dateFinContract = this.dateFinContract;
			contract.codContract = this.codContract;
			contract.offer = this.offer;
			contract.jndType = this.jndType;
			contract.discontinuoReason = this.discontinuoReason;
			contract.durationTypeJndHour = this.durationTypeJndHour;
			contract.durationTypeJndMin = this.durationTypeJndMin;
			contract.durationTypeCvnHour = this.durationTypeCvnHour;
			contract.durationTypeCvnMin = this.durationTypeCvnMin;
			contract.sepeId = this.sepeId;
			contract.interinidad = this.interinidad;
			contract.discontinuo = this.discontinuo;
			contract.previsible = this.previsible;
			contract.oldDateIniContract = this.oldDateIniContract;
			contract.oldDateFinContract = this.oldDateFinContract;
			contract.titulacion = this.titulacion;
			contract.certificateProfessional = this.certificateProfessional;
			return contract;
		}
	}
	
	public enum SexType {
		HOMBRE(1), 
		MUJER(2);
		private Integer value;
		
		private SexType(Integer value) {
			this.value = value;
		}
	
		public Integer getValue() {
			return value;
		}
	}
	
	  /**
	    * TIPO DE JORNADA
	  */
	public enum JndType {
		JORNADA_DIARIA("D"), 
		JORNADA_SEMANAL("S"),
		JORNADA_MENSUAL("M"), 
		JORNADA_ANUAL("A");
	
		private String value;
		private JndType(String value) {
			this.value = value;
		}
		public String getValue() {
			return value;
		}
		
		public static JndType safeValueOf( Byte i ) {
			if (i == null) return null;
			return safeValueOf( i.intValue() ); 
		}
		
		public static JndType safeValueOf( Integer i ) {
			if (i == null) return null;
			if (i < 0 || i >= JndType.values().length) return null;
			return JndType.values()[i];
		}
		
		public static JndType safeValueOf( String i ) {
			for (JndType rs : values()) {
				if(i.equalsIgnoreCase(rs.getValue()))
					return rs;
			}
			return null;
		}
		
		
		
	}
	
	  /**
	    * PROCEDE DE OFERTA DE EMPLEO?
	  */
	public enum OfferType{
		SI("S"), 
		NO("N");
		private String value;
		private OfferType(String value) {
			this.value = value;
		}
		public String getValue() {
			return value;
		}
	}
	
	public enum DiscontinuoReason{
		INCAPACIDAD_TRANSITORIA("I"), 
		PRORROGA_TACITA("P");
		private String value;
		private DiscontinuoReason(String value) {
			this.value = value;
		}
		public String getValue() {
			return value;
		}
	}


	@Override
	public String toString() {
		return "Contract [sepeId=" + sepeId + ", cifEnterprise=" + cifEnterprise + ", regimen=" + regimen + ", ctaCti="
				+ ctaCti + ", nss=" + nss + ", ipf=" + ipf + ", name=" + name + ", sex=" + sex + ", surname=" + surname
				+ ", lastSurname=" + lastSurname + ", codNationality=" + codNationality + ", codPaisDom=" + codPaisDom
				+ ", codMunDom=" + codMunDom + ", codFormativo=" + codFormativo + ", codOccupation=" + codOccupation
				+ ", codPaisWork=" + codPaisWork + ", codMunWork=" + codMunWork + ", codContract=" + codContract
				+ ", dateIniContract=" + dateIniContract + ", dateFinContract=" + dateFinContract 
				+ ", oldDateIniContract=" + oldDateIniContract 
				+ ", oldDateFinContract=" + oldDateFinContract 
				+ ", dateBirth="+ dateBirth 
				+ ", dateComContract=" + dateComContract 
				+ ", offer=" + offer 
				+ ", jndType=" + jndType 
				+ ", discontinuoReason=" + discontinuoReason
				+ ", durationTypeJndHour=" + durationTypeJndHour + ", durationTypeJndMin=" + durationTypeJndMin
				+ ", durationTypeCvnHour=" + durationTypeCvnHour + ", durationTypeCvnMin=" + durationTypeCvnMin +  ", interinidad=" + interinidad
				+  ", titulacion=" + titulacion
				+  ", certificateProfessional=" + certificateProfessional
				+  ", previsible=" + previsible+"]";
	}
	
	
}
