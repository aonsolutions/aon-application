package com.esferalia.aon.in.payroll.pdf.maker.form.bean;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.registry.RegistryAddress;

public class ModificationForm implements Serializable{
	private static final long serialVersionUID = -8544100969659072382L;
	
	private String modificationTitle;
	private String sepeId;//CONTRACT_DATA -> "IDE"
	private CompanyData companyData;
	private ClientData clientData;
	private Date contractStartDate;
	private Date modificationDate;
	private String clauses;
	
	public ModificationForm() {
		
	}

	public String getModificationTitle() {
		return modificationTitle;
	}

	public ModificationForm setModificationTitle(String modificationTitle) {
		this.modificationTitle = modificationTitle;
		return this;
	}

	public String getSepeId() {
		return sepeId;
	}

	public ModificationForm setSepeId(String sepeId) {
		this.sepeId = sepeId;
		return this;
	}

	public CompanyData getCompanyData() {
		return companyData;
	}

	public ModificationForm setCompanyData(CompanyData companyData) {
		this.companyData = companyData;
		return this;
	}

	public ClientData getClientData() {
		return clientData;
	}

	public ModificationForm setClientData(ClientData clientData) {
		this.clientData = clientData;
		return this;
	}

	public Date getContractStartDate() {
		return contractStartDate;
	}

	public ModificationForm setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
		return this;
	}
	
	public Date getModificationDate() {
		return modificationDate;
	}

	public ModificationForm setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public String getClauses() {
		return clauses;
	}
	
	public ModificationForm setClauses(String clauses) {
		this.clauses = clauses;
		return this;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	public static class CompanyData {
		private String name;
		private String ccc;
		private String document;
		private RegistryAddress address;
		private byte[] logo;
		private byte[] signature;
		
		public String getName() {
			return name;
		}
		public CompanyData setName(String name) {
			this.name = name;
			return this;
		}
		public String getCcc() {
			return ccc;
		}
		public CompanyData setCcc(String ccc) {
			this.ccc = ccc;
			return this;
		}
		public String getDocument() {
			return document;
		}
		public CompanyData setDocument(String document) {
			this.document = document;
			return this;
		}
		public RegistryAddress getAddress() {
			return address;
		}
		public CompanyData setAddress(RegistryAddress address) {
			this.address = address;
			return this;
		}
		public byte[] getLogo() {
			return logo;
		}
		public CompanyData setLogo(byte[] logo) {
			this.logo = logo;
			return this;
		}
		public byte[] getSignature() {
			return signature;
		}
		public CompanyData setSignature(byte[] signature) {
			this.signature = signature;
			return this;
		}
	}
	
	public static class ClientData {
		private String name;
		private String document;
		private String socialSecurityNum;
		private String professionalGroup;
		private String quoteGroup;
		private Date seniorityDate;
		
		public String getName() {
			return name;
		}
		public ClientData setName(String name) {
			this.name = name;
			return this;
		}
		public String getDocument() {
			return document;
		}
		public ClientData setDocument(String document) {
			this.document = document;
			return this;
		}
		public String getSocialSecurityNum() {
			return socialSecurityNum;
		}
		public ClientData setSocialSecurityNum(String socialSecurityNum) {
			this.socialSecurityNum = socialSecurityNum;
			return this;
		}
		public String getProfessionalGroup() {
			return professionalGroup;
		}
		public ClientData setProfessionalGroup(String professionalGroup) {
			this.professionalGroup = professionalGroup;
			return this;
		}
		public String getQuoteGroup() {
			return quoteGroup;
		}
		public ClientData setQuoteGroup(String quoteGroup) {
			this.quoteGroup = quoteGroup;
			return this;
		}
		public Date getSeniorityDate() {
			return seniorityDate;
		}
		public ClientData setSeniorityDate(Date seniorityDate) {
			this.seniorityDate = seniorityDate;
			return this;
		}
	}

}
