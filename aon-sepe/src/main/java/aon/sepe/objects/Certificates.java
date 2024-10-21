package aon.sepe.objects;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
	private String causeSuspension; // 01 - 33
	private String officePublic;
	private Integer dedicationPer;
	private Date fAEd; // Fecha de alta
	private Date fSTd; // Fecha de suspensión o extinción
	// DATA VACATION
	private Integer daysCtzVc; // Dias cotizados vacaciones
	private Double bcccVc; // Base cotizacion vacaciones 0.00
	private Double bcdVc; // 0.00
	
	// ERE
	private String ereCode;
	private Date ereEnd;
	private String ereCoef;

	// DATA EMPLOYEE
	private String employeeName;
	private String employeeSurname;
	private String employeeSecondSurname;
	private String naf;

	private List<QuoteData> quoteData;

	private Integer durationContract; // Duracion contrato (horas)

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

	public Optional<String> getLastSurnameManager() {
		return Optional.ofNullable(lastSurnameManager);
	}

	public Optional<String> getCargoManager() {
		return Optional.ofNullable(cargoManager);
	}

	public Optional<String> getEmployeeName() {
		return Optional.ofNullable(employeeName);
	}

	public Optional<String> getEmployeeSurname() {
		return Optional.ofNullable(employeeSurname);
	}

	public Optional<String> getEmployeeSecondSurname() {
		return Optional.ofNullable(employeeSecondSurname);
	}

	public Optional<String> getNaf() {
		return Optional.ofNullable(naf);
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

	public Optional<String> getOfficePublic() {
		return Optional.ofNullable(officePublic);
	}

	public Optional<Integer> getDedicationPer() {
		return Optional.ofNullable(dedicationPer);
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

	public Optional<Double> getBcccVc() {
		return Optional.ofNullable(bcccVc);
	}

	public Optional<Double> getBcdVc() {
		return Optional.ofNullable(bcdVc);
	}

	public List<QuoteData> getQuoteData() {
		return quoteData;
	}

	public String getEreCode() {
		return ereCode;
	}

	public Date getEreEnd() {
		return ereEnd;
	}

	public String getEreCoef() {
		return ereCoef;
	}

	@Override
	public String toString() {
		return "Certificates [\n\tregimen=" + regimen + ",\n\tctaCti=" + ctaCti + ",\n\tipf=" + ipf + ",\n\tipfManager="
				+ ipfManager + ",\n\tnameManager=" + nameManager + ",\n\tsurnameManager=" + surnameManager
				+ ",\n\tlastSurnameManager=" + lastSurnameManager + ",\n\temployeeName=" + employeeName
				+ ",\n\temployeeSurname=" + employeeSurname + ",\n\temployeeSecondSurname=" + employeeSecondSurname
				+ ",\n\tnaf=" + naf + ",\n\tcargoManager=" + cargoManager + ",\n\ttypeContract=" + typeContract
				+ ",\n\tgz=" + gz + ",\n\ttypeDuration=" + typeDuration + ",\n\tpublicPosition=" + publicPosition
				+ ",\n\tcatProfessional=" + catProfessional + ",\n\tcauseSuspension=" + causeSuspension
				+ ",\n\tofficePublic=" + officePublic + ",\n\tdedicationPer=" + dedicationPer + ",\n\tfAEd=" + fAEd
				+ ",\n\tfSTd=" + fSTd + ",\n\tdaysCtzVc=" + daysCtzVc + ",\n\tbcccVc=" + bcccVc + ",\n\tbcdVc=" + bcdVc
				+ ",\n\tdurationContract=" + durationContract + ", quoteData=" + quoteData + "\n]";
	}

	private Certificates() {
	}

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
		private String causeSuspension; // 01 - 33
		private String officePublic;
		private Integer dedicationPer;
		private Date fAEd; // fecha de alta de empresa
		private Date fSTd; // fecha de extension
		// data vacation
		private Integer daysCtzVc;
		private Double bcccVc;
		private Double bcdVc;
		private Integer durationContract;
		private List<QuoteData> quoteData;
		
		// ERE
		private String ereCode;
		private Date ereEnd;
		private String ereCoef;

		// DATA EMPLOYEE
		private String employeeName;
		private String employeeSurname;
		private String employeeSecondSurname;
		private String naf;

		public CertificatesBuilder setQuoteData(List<QuoteData> quoteData) {
			this.quoteData = quoteData;
			return this;
		}

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

		public CertificatesBuilder setEmployeeName(String employeeName) {
			this.employeeName = employeeName;
			return this;
		}

		public CertificatesBuilder setEmployeeSurname(String employeeSurname) {
			this.employeeSurname = employeeSurname;
			return this;
		}

		public CertificatesBuilder setEmployeeSecondSurname(String employeeSecondSurname) {
			this.employeeSecondSurname = employeeSecondSurname;
			return this;
		}

		public CertificatesBuilder setNaf(String naf) {
			this.naf = naf;
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

		public CertificatesBuilder setBcccVc(Double bcccVc) {
			this.bcccVc = bcccVc;
			return this;
		}

		public CertificatesBuilder setBcdVc(Double bcdVc) {
			this.bcdVc = bcdVc;
			return this;
		}

		public CertificatesBuilder setEreCode(String ereCode) {
			this.ereCode = ereCode;
			return this;
		}

		public CertificatesBuilder setEreEnd(Date ereEnd) {
			this.ereEnd = ereEnd;
			return this;
		}

		public CertificatesBuilder setEreCoef(String ereCoef) {
			this.ereCoef = ereCoef;
			return this;
		}

		@Deprecated
		public CertificatesBuilder setDataCtz(List<Map<String, String>> dataCtz) {
			List<QuoteData> quoteDatas = new LinkedList<>();
			for (Map<String, String> ctz : dataCtz) {
				quoteDatas.add(new QuoteData().setAnio(Integer.parseInt(ctz.get("anioCtz")))
						.setMonth(Integer.parseInt(ctz.get("monthCtz"))).setDays(Integer.parseInt(ctz.get("daysCtz")))
						.setBccc(Double.parseDouble(ctz.get("bccc"))).setBcd(Double.parseDouble(ctz.get("bcd"))));
			}
			this.setQuoteData(quoteDatas);
			return this;
		}

		public CertificatesBuilder() {
		}

		public Certificates build() {
			Certificates ct = new Certificates();
			ct.regimen = this.regimen;
			ct.ctaCti = this.ctaCti;
			ct.ipf = this.ipf;
			ct.ipfManager = this.ipfManager;
			ct.nameManager = this.nameManager;
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
			ct.quoteData = this.quoteData;
			ct.durationContract = this.durationContract;
			ct.employeeName = this.employeeName;
			ct.employeeSurname = this.employeeSurname;
			ct.employeeSecondSurname = this.employeeSecondSurname;
			ct.naf = this.naf;
			ct.ereCode = this.ereCode;
			ct.ereEnd = this.ereEnd;
			ct.ereCoef = this.ereCoef;
			return ct;
		}
	}

	public enum TypeDuration {
		DIAS("D"), MESES("M"), ANIOS("A");

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
		ALTO_CARGO_ADM(1), CARGO_REPRESENTANTE(2), MIEMBRO_DE_CORPORACION(3);

		private Integer value;

		public Integer getValue() {
			return value;
		}

		private PublicPosition(Integer value) {
			this.value = value;
		}
	}

}
