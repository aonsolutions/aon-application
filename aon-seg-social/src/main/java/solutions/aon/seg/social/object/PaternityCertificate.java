package solutions.aon.seg.social.object;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PaternityCertificate {
	//BUSINESS DATA
	private String ccc;
	private String postCode;
	private String address;
	private String province;
	private String municipality;
	//BENEFIT DATA
	private Date receptionDate;
	private Integer periodNumber;
	private Date startDate;
	private Date endDate;
	private Float parciality;
	//WORKER DATA
	private String workerName;
	private String workerNif;
	private String workerNaf;
	private String workerGroup;
	private Date workerDischargeDate;
	private Date workerWithdrawalDate;
	private String workerContractCode;
	private Float workerPartialTimeCoef;
	private String workerContractType;
	private ApplicantType workerApplicantType;
	private ReasonType reason;
	private Boolean isPublicEmployee;
	private ArrayList<PaternityDetail> paternityDetail;
	
	private boolean isFather;
	private Boolean canceled;
	
	public PaternityCertificate() {
		this.paternityDetail = new ArrayList<>();
	}
	
	private byte[] pdf;
	
	public String getCcc() {
		return ccc;
	}
	
	public boolean getCanceled() {
		return canceled;
	}
	
	public PaternityCertificate setCcc(String ccc) {
		this.ccc = ccc;
		return this;
	}
	public Optional<String> getPostCode() {
		return Optional.ofNullable(postCode);
	}
	public PaternityCertificate setPostCode(String postCode) {
		this.postCode = postCode;
		return this;
	}
	
	public Optional<String> getAddress() {
		return Optional.ofNullable(address);
	}
	
	public boolean getIsFather() {
		return isFather;
	}
	
	public PaternityCertificate setAddress(String address) {
		this.address = address;
		return this;
	}
	
	public Optional<String> getProvince() {
		return Optional.ofNullable(province);
	}
	
	public PaternityCertificate setProvince(String province) {
		this.province = province;
		return this;
	}
	
	public Optional<String> getMunicipality() {
		return Optional.ofNullable(municipality);
	}
	public PaternityCertificate setMunicipality(String municipality) {
		this.municipality = municipality;
		return this;
	}
	
	public Optional<ReasonType> getReason() {
		return Optional.ofNullable(reason);
	}
	
	public PaternityCertificate setReason(ReasonType reason) {
		this.reason = reason;
		return this;
	}
	public Date getReceptionDate() {
		return receptionDate;
	}
	public PaternityCertificate setReceptionDate(Date receptionDate) {
		this.receptionDate = receptionDate;
		return this;
	}
	public Optional<Integer> getPeriodNumber() {
		return Optional.ofNullable(periodNumber);
	}
	public PaternityCertificate setPeriodNumber(Integer periodNumber) {
		this.periodNumber = periodNumber;
		return this;
	}
	public Date getStartDate() {
		return startDate;
	}
	public PaternityCertificate setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	public Optional<Date> getEndDate() {
		return Optional.ofNullable(endDate);
	}
	public PaternityCertificate setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	public Optional<Float> getParciality() {
		return Optional.ofNullable(null!=parciality && parciality>0 ? parciality : parciality);
	}
	public PaternityCertificate setParciality(Float parciality) {
		this.parciality = parciality;
		return this;
	}
	public Optional<String> getWorkerName() {
		return Optional.ofNullable(workerName);
	}
	public PaternityCertificate setWorkerName(String workerName) {
		this.workerName = workerName;
		return this;
	}
	public Optional<String> getWorkerNif() {
		return Optional.ofNullable(workerNif);
	}
	public PaternityCertificate setWorkerNif(String workerNif) {
		this.workerNif = workerNif;
		return this;
	}
	public Optional<String> getWorkerNaf() {
		return Optional.ofNullable(workerNaf);
	}
	public PaternityCertificate setWorkerNaf(String workerNaf) {
		this.workerNaf = workerNaf;
		return this;
	}
	public Optional<String> getWorkerGroup() {
		return Optional.ofNullable(workerGroup);
	}
	public PaternityCertificate setWorkerGroup(String workerGroup) {
		this.workerGroup = workerGroup;
		return this;
	}
	public Date getWorkerDischargeDate() {
		return workerDischargeDate;
	}
	public PaternityCertificate setWorkerDischargeDate(Date workerDischargeDate) {
		this.workerDischargeDate = workerDischargeDate;
		return this;
	}
	public Date getWorkerWithdrawalDate() {
		return workerWithdrawalDate;
	}
	public PaternityCertificate setWorkerWithdrawalDate(Date workerWithdrawalDate) {
		this.workerWithdrawalDate = workerWithdrawalDate;
		return this;
	}
	public String getWorkerContractCode() {
		return workerContractCode;
	}
	public PaternityCertificate setWorkerContractCode(String workerContractCode) {
		this.workerContractCode = workerContractCode;
		return this;
	}
	public Optional<Float> getWorkerPartialTimeCoef() {
		return Optional.ofNullable(workerPartialTimeCoef);
	}
	public PaternityCertificate setWorkerPartialTimeCoef(Float workerPartialTimeCoef) {
		this.workerPartialTimeCoef = workerPartialTimeCoef;
		return this;
	}
	public String getWorkerContractType() {
		return workerContractType;
	}
	
	public Optional<ApplicantType> getWorkerApplicantType() {
		return Optional.ofNullable(workerApplicantType);
	}
	
	public PaternityCertificate setWorkerContractType(String workerContractType) {
		this.workerContractType = workerContractType;
		return this;
	}
	
	public PaternityCertificate setWorkerApplicantType(ApplicantType workerApplicantType) {
		this.workerApplicantType = workerApplicantType;
		return this;
	}
	
	public Boolean getIsPublicEmployee() {
		return isPublicEmployee;
	}
	
	public PaternityCertificate setIsPublicEmployee(Boolean isPublicEmployee) {
		this.isPublicEmployee = isPublicEmployee;
		return this;
	}
	
	public List<PaternityDetail> getPaternityDetail() {
		return paternityDetail;
	}
	
	public PaternityCertificate setPaternityDetail(ArrayList<PaternityDetail> paternityDetail) {
		this.paternityDetail = paternityDetail;
		return this;
	}
	
	public PaternityCertificate setCanceled(boolean canceled) {
		this.canceled = canceled;
		return this;
	}
	
	public PaternityCertificate setIsFather(boolean isFather) {
		this.isFather = isFather;
		return this;
	}
	
	public void addPaternityDetail(PaternityDetail paternityDetail) {
		this.paternityDetail.add(paternityDetail);
	}
	
	public PaternityCertificate setPartiality(Float parciality) {
		this.parciality = parciality;
		return this;
	}
	

	public byte[] getPdf() {
		return pdf;
	}
	
	public PaternityCertificate setPdf(byte[] pdf2) {
		this.pdf = pdf2;
		return this;
	}


	@Override
	public String toString() {
		return "PaternityCertificate {ccc=" + ccc + ", postCode=" + postCode + ", address=" + address + ", province="
				+ province + ", municipality=" + municipality + ", receptionDate=" + receptionDate + ", periodNumber=" + periodNumber + 
				", startDate=" + startDate + ", endDate=" + endDate
				+ ", parciality=" + parciality + ", workerName=" + workerName + ", workerNif=" + workerNif
				+ ", workerNaf=" + workerNaf + ", workerGroup=" + workerGroup + ", workerDischargeDate=" + workerDischargeDate
				+ ", workerWithdrawalDate=" + workerWithdrawalDate + ", workerContractCode=" + workerContractCode + ", workerPartialTimeCoef=" + workerPartialTimeCoef
				+ ", workerContractType=" + workerContractType + ", workerApplicantType=" + workerApplicantType + ", reason="
				+ reason + ", isPublicEmployee=" + isPublicEmployee + ", isFather=" + isFather + ", canceled=" + canceled+","
				+ "paternityDetail=[" + paternityDetail.toString() +"],"
				+ "}";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(workerNaf, ccc, startDate, endDate);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PaternityCertificate ) ) return false;
		
		PaternityCertificate certificate = (PaternityCertificate) obj;

		return Objects.equals(workerNaf, certificate.workerNaf)
				&& Objects.equals(ccc, certificate.ccc)
				&& Objects.equals(startDate, certificate.startDate)
				&& Objects.equals(endDate, certificate.endDate);
	}
	
	public enum ApplicantType{
		MADRE_BIOLOGICA("MADRE BIOL\u00f3GICA"), 
		OTRO_PROGENITOR("OTRO PROGRENITOR"),
		PRIMER_ADOPTANTE("PRIMER ADOPTANTE"), 
		SEGUNDO_ADOPTANTE("SEGUNDO ADOPTANTE");
	
		private String value;
		private ApplicantType(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public byte value(){
			return (byte) this.ordinal();
		}
		
		public static ApplicantType safeValueOf( Byte i ) {
			if (i == null) return null;
			return safeValueOf( i.intValue() ); 
		}
		
		public static ApplicantType safeValueOf( Integer i ) {
			if (i == null) return null;
			if (i < 0 || i >= ApplicantType.values().length) return null;
			return ApplicantType.values()[i];
		}
		
		public static ApplicantType safeValueOf( String i ) {
			for (ApplicantType rs : values()) {
				if(i.toUpperCase().equalsIgnoreCase(rs.getValue()))
					return rs;
			}
			return null;
		}
	}
	
	public enum ReasonType{
		NACIMIENTO_HIJO("Nacimiento de hijo"), 
		FALLECIMIENTO_MADRE("Fallecimiento de la madre"),
		OPCION_OTRO_PROGENITOR("Cesi\u00f3n/Opci\u00f3n en favor del otro progenitor"), 
		PARTO_MULTIPLE("Parto m\u00faltiple"),
		DESCANSO_ANTES_PARTO("Inicio del descanso antes del parto (solo para madre biol\u00f3gica ET)"),
		ADOPCION_TUTELA("Adopci\u00f3n/Tutela/Acogimiento");
	
		private String value;
		private ReasonType(String value) {
			this.value = value;
		}
		public String getValue() {
			return value;
		}
		
		public byte value(){
			return (byte) this.ordinal();
		}
		
		public static ReasonType safeValueOf( Byte i ) {
			if (i == null) return null;
			return safeValueOf( i.intValue() ); 
		}
		
		public static ReasonType safeValueOf( Integer i ) {
			if (i == null) return null;
			if (i < 0 || i >= ReasonType.values().length) return null;
			return ReasonType.values()[i];
		}
		
		public static ReasonType safeValueOf( String i ) {
			for (ReasonType rs : values()) {
				if(i.toLowerCase().equalsIgnoreCase(rs.getValue().toLowerCase()))
					return rs;
			}
			return null;
		}
	}
}
