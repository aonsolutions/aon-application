package aon.sepe.objects;

import java.util.Date;

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
	private Integer codOccupation;
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

	public Integer getCodOccupation() {
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

	public Date getDateFinContract() {
		return dateFinContract;
	}

	public OfferType getOffer() {
		return offer;
	}

	public JndType getJndType() {
		return jndType;
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
		private Integer codOccupation;
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

		public ContractBuilder setCodOccupation(Integer codOccupation) {
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

		public ContractBuilder() {
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
			contract.durationTypeJndHour = this.durationTypeJndHour;
			contract.durationTypeJndMin = this.durationTypeJndMin;
			contract.durationTypeCvnHour = this.durationTypeCvnHour;
			contract.durationTypeCvnMin = this.durationTypeCvnMin;
			contract.sepeId = this.sepeId;
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
}
