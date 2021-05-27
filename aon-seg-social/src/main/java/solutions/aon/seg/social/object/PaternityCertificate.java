package solutions.aon.seg.social.object;

import java.util.Collection;
import java.util.Date;

import solutions.aon.seg.social.object.SituacionEmpresa.SituacionEmpresaBuilder;

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
	private Collection<String[]> registry;
	private byte[] pdf;
	public String getCcc() {
		return ccc;
	}
	public void setCcc(String ccc) {
		this.ccc = ccc;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getMunicipality() {
		return municipality;
	}
	public void setMunicipality(String municipality) {
		this.municipality = municipality;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Date getReceptionDate() {
		return receptionDate;
	}
	public void setReceptionDate(Date receptionDate) {
		this.receptionDate = receptionDate;
	}
	public Integer getPeriodNumber() {
		return periodNumber;
	}
	public void setPeriodNumber(Integer periodNumber) {
		this.periodNumber = periodNumber;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getParciality() {
		return parciality;
	}
	public void setParciality(String parciality) {
		this.parciality = parciality;
	}
	public String getWorkerName() {
		return workerName;
	}
	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}
	public String getWorkerNif() {
		return workerNif;
	}
	public void setWorkerNif(String workerNif) {
		this.workerNif = workerNif;
	}
	public String getWorkerNaf() {
		return workerNaf;
	}
	public void setWorkerNaf(String workerNaf) {
		this.workerNaf = workerNaf;
	}
	public String getWorkerGroup() {
		return workerGroup;
	}
	public void setWorkerGroup(String workerGroup) {
		this.workerGroup = workerGroup;
	}
	public Date getWorkerDischargeDate() {
		return workerDischargeDate;
	}
	public void setWorkerDischargeDate(Date workerDischargeDate) {
		this.workerDischargeDate = workerDischargeDate;
	}
	public Date getWorkerWithdrawalDate() {
		return workerWithdrawalDate;
	}
	public void setWorkerWithdrawalDate(Date workerWithdrawalDate) {
		this.workerWithdrawalDate = workerWithdrawalDate;
	}
	public String getWorkerContractCode() {
		return workerContractCode;
	}
	public void setWorkerContractCode(String workerContractCode) {
		this.workerContractCode = workerContractCode;
	}
	public Float getWorkerPartialTimeCoef() {
		return workerPartialTimeCoef;
	}
	public void setWorkerPartialTimeCoef(Float workerPartialTimeCoef) {
		this.workerPartialTimeCoef = workerPartialTimeCoef;
	}
	public String getWorkerContractType() {
		return workerContractType;
	}
	public void setWorkerContractType(String workerContractType) {
		this.workerContractType = workerContractType;
	}
	public Boolean getIsPublicEmployee() {
		return isPublicEmployee;
	}
	public void setIsPublicEmployee(Boolean isPublicEmployee) {
		this.isPublicEmployee = isPublicEmployee;
	}
	public Collection<String[]> getRegistry() {
		return registry;
	}
	public void setRegistry(Collection<String[]> registry) {
		this.registry = registry;
	}
	public byte[] getPdf() {
		return pdf;
	}
	public void setPdf(byte[] pdf2) {
		this.pdf = pdf2;
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
		if(registry!=null)
			visitor.visitRegistry(this.registry);
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
				if(pdf!=null) {
					if(pdf.length>0) {
						stringBuffer.append(String.format("\tPDF DOWNLOADED"));
					}
				}
			}
			
			

		});
		return stringBuffer.toString();
	}
	
	
	public static class PaternityCertificateBuilder{
		
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
		private Collection<String[]> registry;
		private byte[] pdf;
		
		public PaternityCertificateBuilder setCcc(String ccc) {
			if(!((ccc.equals(""))||(ccc==null)))
				this.ccc = ccc;
			else
				this.ccc=null;
			return this;
		}
		
		public PaternityCertificateBuilder setPostCode(String postCode) {
			if(!((postCode.equals(""))||(postCode==null)))
				this.postCode = postCode;
			else
				this.postCode=null;
			return this;
		}
		
		public PaternityCertificateBuilder setAddress(String address) {
			if(!((address.equals(""))||(address==null)))
				this.address = address;
			else
				this.address=null;
			return this;
		}
		
		public PaternityCertificateBuilder setProvince(String province) {
			if(!((province.equals(""))||(province==null)))
				this.province = province;
			else
				this.province=null;
			return this;
		}
		
		public PaternityCertificateBuilder setMunicipality(String municipality) {
			if(!((municipality.equals(""))||(municipality==null)))
				this.municipality = municipality;
			else
				this.municipality=null;
			return this;
		}
		
		public PaternityCertificateBuilder setReason(String reason) {
			if(!((reason.equals(""))||(reason==null)))
				this.reason = reason;
			else
				this.reason=null;
			return this;
		} 
		
		public PaternityCertificateBuilder setReceptionDate(Date receptionDate) {
			this.receptionDate = receptionDate;
			return this;
		}
		
		public PaternityCertificateBuilder setPeriodNumber(Integer periodNumber) {
			this.periodNumber = periodNumber;
			return this;
		}
		
		public PaternityCertificateBuilder setStartDate(Date startDate) {
			this.startDate = startDate;
			return this;
		}
		
		public PaternityCertificateBuilder setEndDate(Date endDate) {
			this.endDate = endDate;
			return this;
		}
		
		public PaternityCertificateBuilder setPartiality(String parciality) {
			if(!((parciality.equals(""))||(parciality==null)))
				this.parciality = parciality;
			else
				this.parciality=null;
			return this;
		}
		
		public PaternityCertificateBuilder setWorkerName(String workerName) {
			if(!((workerName.equals(""))||(workerName==null)))
				this.workerName = workerName;
			else
				this.workerName=null;
			return this;
		}
		
		public PaternityCertificateBuilder setWorkerNif(String workerNif) {
			if(!((workerNif.equals(""))||(workerNif==null)))
				this.workerNif = workerNif;
			else
				this.workerNif=null;
			return this;
		}
		
		public PaternityCertificateBuilder setWorkerNaf(String workerNaf) {
			if(!((workerNaf.equals(""))||(workerNaf==null)))
				this.workerNaf = workerNaf;
			else
				this.workerNaf=null;
			return this;
		}
		
		public PaternityCertificateBuilder setWorkerGroup(String workerGroup) {
			if(!((workerGroup.equals(""))||(workerGroup==null)))
				this.workerGroup = workerGroup;
			else
				this.workerGroup=null;
			return this;
		}
		
		public PaternityCertificateBuilder setWorkerDischargeDate(Date workerDischargDate) {
			this.workerDischargeDate = workerDischargDate;
			return this;
		}
		
		public PaternityCertificateBuilder setWorkerWithdrawalDate(Date workerWithdrawalDate) {
			this.workerWithdrawalDate = workerWithdrawalDate;
			return this;
		}
		
		public PaternityCertificateBuilder setWorkerContractCode(String workerContractCode) {
			if(!((workerContractCode.equals(""))||(workerContractCode==null)))
				this.workerContractCode = workerContractCode;
			else
				this.workerContractCode=null;
			return this;
		}
		
		public PaternityCertificateBuilder setWorkerPartialTimeCoef(Float workerPartialTimeCoef) {
			this.workerPartialTimeCoef = workerPartialTimeCoef;
			return this;
		}
		
		public PaternityCertificateBuilder setWorkerContractType(String workerContractType) {
			if(!((workerContractType.equals(""))||(workerContractType==null)))
				this.workerContractType = workerContractType;
			else
				this.workerContractType=null;
			return this;
		}
		
		public PaternityCertificateBuilder setIsPublicEmployee(Boolean isPublicEmployee) {
			this.isPublicEmployee = isPublicEmployee;
			return this;
		}
		
		public PaternityCertificateBuilder setRegistry(Collection<String[]> registry) {
			this.registry = registry;
			return this;
		}
		
		public PaternityCertificateBuilder setPdf(byte[] pdf2) {
			this.pdf = pdf2;
			return this;
		}
		
		public PaternityCertificate build() {
			PaternityCertificate p=new PaternityCertificate();
			p.setCcc(this.ccc);
			p.setPostCode(this.postCode);
			p.setAddress(this.address);
			p.setProvince(this.province);
			p.setMunicipality(this.municipality);
			p.setReason(this.reason);
			p.setReceptionDate(this.receptionDate);
			p.setPeriodNumber(this.periodNumber);
			p.setStartDate(this.startDate);
			p.setEndDate(this.endDate);
			p.setParciality(this.parciality);
			p.setWorkerName(this.workerName);
			p.setWorkerNif(this.workerNif);
			p.setWorkerNaf(this.workerNaf);
			p.setWorkerGroup(this.workerGroup);
			p.setWorkerContractType(this.workerContractType);
			p.setWorkerDischargeDate(this.workerDischargeDate);
			p.setWorkerWithdrawalDate(this.workerWithdrawalDate);
			p.setWorkerContractCode(this.workerContractCode);
			p.setWorkerPartialTimeCoef(this.workerPartialTimeCoef);
			p.setIsPublicEmployee(this.isPublicEmployee);
			p.setRegistry(this.registry);
			p.setPdf(this.pdf);
			return p;
			
		}
		
	}
	
	
}
