package aon.sepe.objects;

import java.util.Date;
import java.util.Optional;

public class Contrata {

	private String nss;
	private String name;
	private Date birthDate;
	private String ipf;
	private String sex;
	private String tlf;
	private String ctaCti; 
	private String regime;
	private String companyId;
	private String companyName;
	private String situation;
	private String gc;
	private String gcDesc;
	private Boolean agricultPromo;
	private Boolean workTimeReduct;
	private Date fra;
	private Date fea;
	private Date frb;
	private Date feb;
	private String contract;
	private String coef;
	private String colec;
	private String epig;
	private String ocup;
	private String vinFam;
	private String profesCat;
	private String reducingCoefic;
	private Integer ident;
	private String md_ctz;
	
	private Contrata() {}
	
	
	public String getIpf() {return ipf;}
	public String getNss() {return nss;}
	public Date getFra() {return fra;}
	public String getSituacion() {return situation;}
	public String getRegime() {return regime;}
	public Optional<String> getName() {return Optional.ofNullable(name);}
	public Optional<String> getGc() {return Optional.ofNullable(gc);}
	public Optional<Date> getFrb() {return Optional.ofNullable(frb);}
	public Optional<String>  getCtaCti() {return Optional.ofNullable(ctaCti);}
	public Optional<String> getContract(){return Optional.ofNullable(contract);}	
	public Optional<Date> getBirthDate() {return Optional.ofNullable(birthDate);}
	public Optional<Double> getCoef() {
		try {
			return Optional.of(Double.parseDouble(coef));
		} catch ( Exception e) {
			return Optional.empty();
		}
	}	
	public Optional<String> getSex() {return Optional.of(sex);}
	public Integer getIdent() {return ident;}
	public String getColec() {return colec;}
	public String getOcup() {return ocup;}
	public Optional<String> getMdctz(){return Optional.ofNullable(md_ctz);}
	


	public static class ContrataBuilder {
		
		private String nss;
		private String name;
		private Date birthDate;
		private String ipf;
		private String sex;
		private String tlf;
		private String ctaCti;
		private String regime;
		private String companyId;
		private String companyName;
		private String situation;
		private String gc;
		private String gcDesc;
		private Boolean agricultPromo;
		private Boolean workTimeReduct;
		private Date fra;
		private Date fea;
		private Date frb;
		private Date feb;
		private String contract;
		private String coef;
		private String colec;
		private String epig;
		private String ocup;
		private String vinFam;
		private String profesCat;
		private String reducingCoefic;
		private Integer ident;
		private String md_ctz;
		
		public ContrataBuilder(){}		
		
		public ContrataBuilder setNss(String nss) {
			if(nss != null && !nss.trim().equals("")) this.nss = nss.trim();
			else this.nss = null;
			return this;
		}


		public ContrataBuilder setName(String name) {
			if(name != null && !name.trim().equals("")) this.name = name.trim();
			else this.name = null;
			return this;
		}
		
		public ContrataBuilder setBirthDate(Date birthDate) {
			this.birthDate = birthDate;
			return this;
		}


		public ContrataBuilder setIpf(String ipf) {
			if(ipf != null && !ipf.trim().equals("")) this.ipf = ipf.trim();
			else this.ipf = null;
			return this;
		}


		public ContrataBuilder setSex(String sex) {
			if(sex != null && !sex.trim().equals("")) this.sex = sex.trim();
			else this.sex = null;
			return this;
		}
		
		public ContrataBuilder setTlf(String tlf) {
			if(tlf != null && !tlf.trim().equals("")) this.tlf = tlf.trim();
			else this.tlf = null;
			return this;
		}

		public ContrataBuilder setCtaCti(String ctaCti) {
			if(ctaCti != null && !ctaCti.trim().equals("")) this.ctaCti = ctaCti.trim();
			else this.ctaCti = null;
			return this;
		}


		public ContrataBuilder setRegime(String regime) {
			if(regime != null && !regime.trim().equals("")) this.regime = regime.trim();
			else this.regime = null;
			return this;
		}


		public ContrataBuilder setCompanyId(String companyId) {
			if(companyId != null && !companyId.trim().equals("")) this.companyId = companyId.trim();
			else this.companyId = null;
			return this;
		}


		public ContrataBuilder setCompanyName(String companyName) {
			if(companyName != null && !companyName.trim().equals("")) this.companyName = companyName.trim();
			else this.companyName = null;
			return this;
		}


		public ContrataBuilder setSituation(String situation) {
			if(situation != null && !situation.trim().equals("")) this.situation = situation.trim();
			else this.situation = null;
			return this;
		}


		public ContrataBuilder setGc(String gc) {
			if(gc != null && !gc.trim().equals("")) this.gc = gc.trim();
			else this.gc = null;
			return this;
		}
		
		public ContrataBuilder setGcDesc(String gcDesc) {
			if(gcDesc != null && !gcDesc.trim().equals("")) this.gcDesc = gcDesc.trim();
			else this.gcDesc = null;
			return this;
		}


		public ContrataBuilder setAgricultPromo(Boolean agricultPromo) {
			this.agricultPromo = agricultPromo;
			return this;
		}


		public ContrataBuilder setWorkTimeReduct(Boolean workTimeReduct) {
			this.workTimeReduct = workTimeReduct;
			return this;
		}
		
		public ContrataBuilder setFra(Date fra) {
			this.fra = fra;
			return this;
		}

		public ContrataBuilder setFea(Date fea) {
			this.fea = fea;
			return this;
		}


		public ContrataBuilder setFrb(Date frb) {
			this.frb = frb;
			return this;
		}


		public ContrataBuilder setFeb(Date feb) {
			this.feb = feb;
			return this;
		}


		public ContrataBuilder setContract(String contract) {
			if(contract != null && !contract.trim().equals("")) this.contract = contract.trim();
			else this.contract = null;
			return this;
		}


		public ContrataBuilder setCoef(String coef) {
			if(coef != null && !coef.trim().equals("")) this.coef = coef.trim();
			else this.coef = null;
			return this;
		}


		public ContrataBuilder setColec(String colec) {
			if(colec != null && !colec.trim().equals("")) this.colec = colec.trim();
			else this.colec = null;
			return this;
		}


		public ContrataBuilder setEpig(String epig) {
			if(epig != null && !epig.trim().equals("")) this.epig = epig.trim();
			else this.epig = null;
			return this;
		}


		public ContrataBuilder setOcup(String ocup) {
			if(ocup != null && !ocup.trim().equals("")) this.ocup = ocup.trim();
			else this.ocup = null;
			return this;
		}


		public ContrataBuilder setVinFam(String vinFam) {
			if(vinFam != null && !vinFam.trim().equals("")) this.vinFam = vinFam.trim();
			else this.vinFam = null;
			return this;
		}


		public ContrataBuilder setProfesCat(String profesCat) {
			if(profesCat != null && !profesCat.trim().equals("")) this.profesCat = profesCat.trim();
			else this.profesCat = null;
			return this;
		}


		public ContrataBuilder setReducingCoefic(String reducingCoefic) {
			if(reducingCoefic != null && !reducingCoefic.trim().equals("")) this.reducingCoefic = reducingCoefic.trim();
			else this.reducingCoefic = null;
			return this;
		}
		
		public ContrataBuilder setIdent(Integer ident) {
			this.ident = ident;
			return this;
		}
		
		public ContrataBuilder setMdctz(String md_ctz) {
			this.md_ctz = md_ctz;
			return this;
		}
		
		public Contrata build(){
			Contrata contrata = new Contrata();
			
			contrata.nss = this.nss;
			contrata.name = this.name;
			contrata.birthDate = this.birthDate;
			contrata.ipf = this.ipf;
			contrata.sex = this.sex;
			contrata.tlf = this.tlf;
			contrata.ctaCti = this.ctaCti;
			contrata.regime = this.regime;
			contrata.companyId = this.companyId;
			contrata.companyName = this.companyName;
			contrata.situation = this.situation;
			contrata.gc = this.gc;
			contrata.gcDesc = this.gcDesc;
			contrata.agricultPromo = this.agricultPromo;
			contrata.workTimeReduct = this.workTimeReduct;
			contrata.fra = this.fra;
			contrata.fea = this.fea;
			contrata.frb = this.frb;
			contrata.feb = this.feb;
			contrata.contract = this.contract;
			contrata.coef = this.coef;
			contrata.colec = this.colec;
			contrata.epig = this.epig;
			contrata.ocup = this.ocup;
			contrata.vinFam = this.vinFam;
			contrata.profesCat = this.profesCat;
			contrata.reducingCoefic = this.reducingCoefic;
			contrata.ident = this.ident;
			contrata.md_ctz = this.md_ctz;
			return contrata;
		}

	}	
	

}


