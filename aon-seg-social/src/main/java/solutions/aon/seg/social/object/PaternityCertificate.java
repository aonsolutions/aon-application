package solutions.aon.seg.social.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class PaternityCertificate {
	//BUSINESS DATA
	private String ccc;
	private String postCode;
	private String address;
	private String province;
	private String municipality;
	//BENEFIT DATA
	private String reason;
	private Date receptionDate;
	private Integer periodNumber;
	private Date startDate;
	private Date endDate;
	private String parciality;
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
	private Boolean isPublicEmployee;
	private ArrayList<PaternityDetail> paternityDetail;
	
	public PaternityCertificate() {
		this.paternityDetail = new ArrayList<>();
	}
	
	private byte[] pdf;
	
	public String getCcc() {
		return ccc;
	}
	public PaternityCertificate setCcc(String ccc) {
		this.ccc = ccc;
		return this;
	}
	public String getPostCode() {
		return postCode;
	}
	public PaternityCertificate setPostCode(String postCode) {
		this.postCode = postCode;
		return this;
	}
	public String getAddress() {
		return address;
	}
	public PaternityCertificate setAddress(String address) {
		this.address = address;
		return this;
	}
	public String getProvince() {
		return province;
	}
	public PaternityCertificate setProvince(String province) {
		this.province = province;
		return this;
	}
	public String getMunicipality() {
		return municipality;
	}
	public PaternityCertificate setMunicipality(String municipality) {
		this.municipality = municipality;
		return this;
	}
	public String getReason() {
		return reason;
	}
	public PaternityCertificate setReason(String reason) {
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
	public Integer getPeriodNumber() {
		return periodNumber;
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
	public Date getEndDate() {
		return endDate;
	}
	public PaternityCertificate setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	public String getParciality() {
		return parciality;
	}
	public PaternityCertificate setParciality(String parciality) {
		this.parciality = parciality;
		return this;
	}
	public String getWorkerName() {
		return workerName;
	}
	public PaternityCertificate setWorkerName(String workerName) {
		this.workerName = workerName;
		return this;
	}
	public String getWorkerNif() {
		return workerNif;
	}
	public PaternityCertificate setWorkerNif(String workerNif) {
		this.workerNif = workerNif;
		return this;
	}
	public String getWorkerNaf() {
		return workerNaf;
	}
	public PaternityCertificate setWorkerNaf(String workerNaf) {
		this.workerNaf = workerNaf;
		return this;
	}
	public String getWorkerGroup() {
		return workerGroup;
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
	public Float getWorkerPartialTimeCoef() {
		return workerPartialTimeCoef;
	}
	public PaternityCertificate setWorkerPartialTimeCoef(Float workerPartialTimeCoef) {
		this.workerPartialTimeCoef = workerPartialTimeCoef;
		return this;
	}
	public String getWorkerContractType() {
		return workerContractType;
	}
	public PaternityCertificate setWorkerContractType(String workerContractType) {
		this.workerContractType = workerContractType;
		return this;
	}
	public Boolean getIsPublicEmployee() {
		return isPublicEmployee;
	}
	public PaternityCertificate setIsPublicEmployee(Boolean isPublicEmployee) {
		this.isPublicEmployee = isPublicEmployee;
		return this;
	}
	public ArrayList<PaternityDetail> getPaternityDetail() {
		return paternityDetail;
	}
	
	public PaternityCertificate setPaternityDetail(ArrayList<PaternityDetail> paternityDetail) {
		this.paternityDetail = paternityDetail;
		return this;
	}
	
	public void addPaternityDetail(PaternityDetail paternityDetail) {
		this.paternityDetail.add(paternityDetail);
	}
	
	public PaternityCertificate setPartiality(String parciality) {
		if(!((parciality.equals(""))||(parciality==null)))
			this.parciality = parciality;
		else
			this.parciality=null;
		return this;
	}
	

	public byte[] getPdf() {
		return pdf;
	}
	
	public PaternityCertificate setPdf(byte[] pdf2) {
		this.pdf = pdf2;
		return this;
	}
	
	public static interface Visitor{
		public void visitCcc(String ccc);
		public void visitPostCode(String postCode);
		public void visitAddress(String address);
		public void visitProvince(String province);
		public void visitMunicipality(String municipality);
		//BENEFIT DATA
		public void visitReason(String reason);
		public void visitReceptionDate(Date receptionDate);
		public void visitPeriodNumber(Integer periodNumber);
		public void visitStartDate(Date startDate);
		public void visitEndDate(Date endDate);
		public void visitParciality(String parciality);
		//WORKER DATA
		public void visitWorkerName(String workerName);
		public void visitWorkerNif(String workerNif);
		public void visitWorkerNaf(String workerNaf);
		public void visitWorkerGroup(String workerGroup);
		public void visitWorkerDischargeDate(Date workerDischargeDate);
		public void visitWorkerWithdrawalDate(Date workerWithdrawalDate);
		public void visitWorkerContractCode(String workerContractCode);
		public void visitWorkerPartialTimeCoef(Float workerPartialTimeCoef);
		public void visitWorkerContractType(String workerContractType);
		public void visitIsPublicEmployee(Boolean isPublicEmployee);
		public void visitRegistry(Collection<String[]> registry);
		public void visitPdf(byte[] pdf);
	}
	
	public void accept(Visitor visitor) {
		if(ccc!=null)
			visitor.visitCcc(this.ccc);
		if(postCode!=null)
			visitor.visitPostCode(this.postCode);
		if(address!=null)
			visitor.visitAddress(this.address);
		if(province!=null)
			visitor.visitProvince(this.province);
		if(municipality!=null)
			visitor.visitMunicipality(this.municipality);
		if(reason!=null)
			visitor.visitReason(this.reason);
		if(receptionDate!=null)
			visitor.visitReceptionDate(this.receptionDate);
		if(periodNumber!=null)
			visitor.visitPeriodNumber(this.periodNumber);
		if(startDate!=null)
			visitor.visitStartDate(this.startDate);
		if(endDate!=null)
			visitor.visitEndDate(this.endDate);
		if(parciality!=null)
			visitor.visitParciality(this.parciality);
		if(workerName!=null)
			visitor.visitWorkerName(this.workerName);
		if(workerNif!=null)
			visitor.visitWorkerNif(this.workerNif);
		if(workerNaf!=null)
			visitor.visitWorkerNaf(this.workerNaf);
		if(workerGroup!=null)
			visitor.visitWorkerGroup(this.workerGroup);
		if(workerDischargeDate!=null)
			visitor.visitWorkerDischargeDate(this.workerDischargeDate);
		if(workerWithdrawalDate!=null)
			visitor.visitWorkerWithdrawalDate(this.workerWithdrawalDate);
		if(workerContractCode!=null)
			visitor.visitWorkerContractCode(this.workerContractCode);
		if(workerPartialTimeCoef!=null)
			visitor.visitWorkerPartialTimeCoef(this.workerPartialTimeCoef);
		if(workerContractType!=null)
			visitor.visitWorkerContractType(this.workerContractType);
		if(isPublicEmployee!=null)
			visitor.visitIsPublicEmployee(this.isPublicEmployee);
		if(pdf!=null)
			visitor.visitPdf(this.pdf);
	}
	
	@Override
	public String toString() {
		
		StringBuffer stringBuffer=new StringBuffer();
		stringBuffer.append("Certificate data: \n");
		
		accept(new Visitor() {

			@Override
			public void visitCcc(String ccc) {
				stringBuffer.append(String.format("\tCCC: \"%S\"\n", ccc));
			}

			@Override
			public void visitPostCode(String postCode) {
				stringBuffer.append(String.format("\tCP: \"%S\"\n", postCode));
			}

			@Override
			public void visitAddress(String address) {
				stringBuffer.append(String.format("\tAddress: \"%S\"\n", address));
			}

			@Override
			public void visitProvince(String province) {
				stringBuffer.append(String.format("\tProvince: \"%S\"\n", province));
			}

			@Override
			public void visitMunicipality(String municipality) {
				stringBuffer.append(String.format("\tMunicipality: \"%S\"\n", municipality));
			}

			@Override
			public void visitReason(String reason) {
				stringBuffer.append(String.format("\tReason: \"%S\"\n", reason));
			}

			@Override
			public void visitReceptionDate(Date receptionDate) {
				stringBuffer.append(String.format("\tReception date: \"%S\"\n", receptionDate));
			}

			@Override
			public void visitPeriodNumber(Integer periodNumber) {
				stringBuffer.append(String.format("\tPeriod number: \"%S\"\n", periodNumber));
			}

			@Override
			public void visitStartDate(Date startDate) {
				stringBuffer.append(String.format("\tStart date: \"%S\"\n", startDate));
			}

			@Override
			public void visitEndDate(Date endDate) {
				stringBuffer.append(String.format("\tEnd date: \"%S\"\n", endDate));
			}

			@Override
			public void visitParciality(String parciality) {
				stringBuffer.append(String.format("\tPartiality: \"%S\"\n", parciality));
			}

			@Override
			public void visitWorkerName(String workerName) {
				stringBuffer.append(String.format("\tWorker name: \"%S\"\n", workerName));
			}

			@Override
			public void visitWorkerNif(String workerNif) {
				stringBuffer.append(String.format("\tWorker NIF: \"%S\"\n", workerNif));
			}

			@Override
			public void visitWorkerNaf(String workerNaf) {
				stringBuffer.append(String.format("\tWorker NAF: \"%S\"\n", workerNaf));
			}

			@Override
			public void visitWorkerGroup(String workerGroup) {
				stringBuffer.append(String.format("\tWorker group: \"%S\"\n", workerGroup));
			}

			@Override
			public void visitWorkerDischargeDate(Date workerDischargeDate) {
				stringBuffer.append(String.format("\tWorker's discharge date: \"%S\"\n", workerDischargeDate));
			}

			@Override
			public void visitWorkerWithdrawalDate(Date workerWithdrawalDate) {
				stringBuffer.append(String.format("\tWorker's withdrawal date: \"%S\"\n", workerWithdrawalDate));
			}

			@Override
			public void visitWorkerContractCode(String workerContractCode) {
				stringBuffer.append(String.format("\tWorker's contract code: \"%S\"\n", workerContractCode));
			}

			@Override
			public void visitWorkerPartialTimeCoef(Float workerPartialTimeCoef) {
				stringBuffer.append(String.format("\tWorker's partial time coef.: \"%S\"\n", workerPartialTimeCoef));
			}

			@Override
			public void visitWorkerContractType(String workerContractType) {
				stringBuffer.append(String.format("\tWorker's contract type: \"%S\"\n", workerContractType));
			}

			@Override
			public void visitIsPublicEmployee(Boolean isPublicEmployee) {
				stringBuffer.append(String.format("\tIs public employee?: \"%S\"\n", isPublicEmployee));
			}

			@Override
			public void visitRegistry(Collection<String[]> registry) {
				stringBuffer.append(String.format("\tREGISTROS:\n"));
				stringBuffer.append(String.format("\tNúmero\tAño/mes\t\tBase CC\t\tBase CP\t\tDías\n"));
				for (String[] strings : registry) {
					for(int i=0;i<strings.length;i++) {
						stringBuffer.append(String.format("\t \"%S\"", strings[i]));
					}
					stringBuffer.append(String.format("\n"));
				}
				
			}
			@Override
			public void visitPdf(byte[] pdf) {
				if(pdf!=null && pdf.length>0) 
					stringBuffer.append(String.format("\tPDF DOWNLOADED"));
			}
		});
		return stringBuffer.toString();
	}
}
