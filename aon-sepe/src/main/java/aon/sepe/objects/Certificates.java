package aon.sepe.objects;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Certificates {

	private String regimen;
	private String ctaCti;
	private String ipf;
	private String ipfManager;
	private String nameManager;
	private String surnameManager;
	private String lastSurnameManager;
	private String cargoManager;
	private String typeContract;
	private String gz;
	private TypeDuration typeDuration;
	private PublicPosition publicPosition;
	private String catProfessional;
	private String causeSuspension; //01 - 33
	private String officePublic;
	private Integer dedicationPer;
	private Date fAEd; // fecha de alta de empresa
	private Date fSTd; // fecha de extension 
	//data vacation
	private Integer daysCtzVc;
	private String bcccVc;
	private String bcdVc;
	List<Map<String, String>> dataCtz;
	private Integer durationContract;
	
	public Integer getDurationContract() {
		return durationContract;
	}

	public String getRegimen() {
		return regimen;
	}

	public String getCtaCti() {
		return ctaCti;
	}

	public String getIpf() {
		return ipf;
	}

	public String getIpfManager() {
		return ipfManager;
	}

	public String getNameManager() {
		return nameManager;
	}

	public String getSurnameManager() {
		return surnameManager;
	}

	public String getLastSurname() {
		return lastSurnameManager;
	}

	public String getCargoManager() {
		return cargoManager;
	}

	public String getTypeContract() {
		return typeContract;
	}

	public String getGz() {
		return gz;
	}

	public TypeDuration getTypeDuration() {
		return typeDuration;
	}

	public PublicPosition getPublicPosition() {
		return publicPosition;
	}

	public String getCatProfessional() {
		return catProfessional;
	}

	public String getCauseSuspension() {
		return causeSuspension;
	}

	public String getOfficePublic() {
		return officePublic;
	}

	public Integer getDedicationPer() {
		return dedicationPer;
	}

	public Date getfAEd() {
		return fAEd;
	}

	public Date getfSTd() {
		return fSTd;
	}

	public Integer getDaysCtzVc() {
		return daysCtzVc;
	}

	public String getBcccVc() {
		return bcccVc;
	}

	public String getBcdVc() {
		return bcdVc;
	}

	public List<Map<String, String>> getDataCtz() {
		return dataCtz;
	}
	
	private Certificates() {}
	public static class CertificatesBuilder {
		private String regimen;
		private String ctaCti;
		private String ipf;
		private String ipfManager;
		private String nameManager;
		private String surnameManager;
		private String lastSurnameManager;
		private String cargoManager;
		private String typeContract;
		private String gz;
		private TypeDuration typeDuration;
		private PublicPosition publicPosition;
		private String catProfessional;
		private String causeSuspension; //01 - 33
		private String officePublic;
		private Integer dedicationPer;
		private Date fAEd; // fecha de alta de empresa
		private Date fSTd; // fecha de extension 
		//data vacation
		private Integer daysCtzVc;
		private String bcccVc;
		private String bcdVc;
		List<Map<String, String>> dataCtz;
		private Integer durationContract;
		
		public CertificatesBuilder setDaysCtzVc(Integer daysCtzVc) {
			this.daysCtzVc = daysCtzVc;
			return this;
		}

		public CertificatesBuilder setDurationContract(Integer durationContract) {
			this.durationContract = durationContract;
			return this;
		}
		
		public CertificatesBuilder setRegimen(String regimen) {
			this.regimen = regimen;
			return this;
		}

		public CertificatesBuilder setCtaCti(String ctaCti) {
			this.ctaCti = ctaCti;
			return this;
		}

		public CertificatesBuilder setIpf(String ipf) {
			this.ipf = ipf;
			return this;
		}

		public CertificatesBuilder setIpfManager(String ipfManager) {
			this.ipfManager = ipfManager;
			return this;
		}

		public CertificatesBuilder setName(String nameManager) {
			this.nameManager = nameManager;
			return this;
		}

		public CertificatesBuilder setSurname(String surnameManager) {
			this.surnameManager = surnameManager;
			return this;
		}

		public CertificatesBuilder setLastSurname(String lastSurnameManager) {
			this.lastSurnameManager = lastSurnameManager;
			return this;
		}

		public CertificatesBuilder setCargo(String cargoManager) {
			this.cargoManager = cargoManager;
			return this;
		}

		public CertificatesBuilder setTypeContract(String typeContract) {
			this.typeContract = typeContract;
			return this;
		}

		public CertificatesBuilder setGz(String gz) {
			this.gz = gz;
			return this;
		}

		public CertificatesBuilder setTypeDuration(TypeDuration typeDuration) {
			this.typeDuration = typeDuration;
			return this;
		}

		public CertificatesBuilder setPublicPosition(PublicPosition publicPosition) {
			this.publicPosition = publicPosition;
			return this;
		}

		public CertificatesBuilder setCatProfessional(String catProfessional) {
			this.catProfessional = catProfessional;
			return this;
		}

		public CertificatesBuilder setCauseSuspension(String causeSuspension) {
			this.causeSuspension = causeSuspension;
			return this;
		}

		public CertificatesBuilder setOfficePublic(String officePublic) {
			this.officePublic = officePublic;
			return this;
		}

		public CertificatesBuilder setDedicationPer(Integer dedicationPer) {
			this.dedicationPer = dedicationPer;
			return this;
		}

		public CertificatesBuilder setfAEd(Date fAEd) {
			this.fAEd = fAEd;
			return this;
		}

		public CertificatesBuilder setfSTd(Date fSTd) {
			this.fSTd = fSTd;
			return this;
		}


		public CertificatesBuilder setBcccVc(String bcccVc) {
			this.bcccVc = bcccVc;
			return this;
		}

		public CertificatesBuilder setBcdVc(String bcdVc) {
			this.bcdVc = bcdVc;
			return this;
		}

		public CertificatesBuilder setDataCtz(List<Map<String, String>> dataCtz) {
			this.dataCtz = dataCtz;
			return this;
		}
		
		public CertificatesBuilder() {}
		
		public Certificates build() {
			Certificates ct = new Certificates();
			ct.regimen = this.regimen;
			ct.ctaCti = this.ctaCti;
			ct.ipf = this.ipf;
			ct.ipfManager = this.ipfManager;
			ct.nameManager  = this.nameManager;
			ct.surnameManager = this.surnameManager;
			ct.lastSurnameManager = this.lastSurnameManager;
			ct.cargoManager = this.cargoManager;
			ct.typeContract = this.typeContract;
			ct.gz = this.gz;
			ct.typeDuration = this.typeDuration;
			ct.publicPosition = this.publicPosition;
			ct.catProfessional = this.catProfessional;
			ct.causeSuspension = this.causeSuspension; 
			ct.officePublic = this.officePublic;
			ct.dedicationPer = this.dedicationPer;
			ct.fAEd = this.fAEd;
			ct.fSTd = this.fSTd; 
			ct.daysCtzVc = this.daysCtzVc;
			ct.bcccVc = this.bcccVc;
			ct.bcdVc = this.bcdVc;
			ct.dataCtz = this.dataCtz;
			ct.durationContract = this.durationContract;
			return ct;
		}
	}
	
	public enum TypeDuration {
		DIAS("D"), 
		MESES("M"),
		ANIOS("A");
		
		private String value;
		
		public String getValue() {
			return value;
		}
		
		private TypeDuration(String value) {
			this.value = value;
		}
	}
	/**
	 * 
	 * Cargo publico o sindical
	 *
	 */
	public enum PublicPosition {
		ALTO_CARGO_ADM(1), 
		CARGO_REPRESENTANTE(2),
		MIEMBRO_DE_CORPORACION(3);
		
		private Integer value;
		
		public Integer getValue() {
			return value;
		}
		
		private PublicPosition(Integer value) {
			this.value = value;
		}
	}
	
}
