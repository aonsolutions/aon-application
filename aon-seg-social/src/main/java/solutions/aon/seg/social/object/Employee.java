package solutions.aon.seg.social.object;

import java.util.Date;
import java.util.Optional;

public class Employee {

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
	private Date frv;
	private Date feb;
	private String contract;
	private String coef; // EXAMPLE "530"
	private Double factor; //COEF EXAMPLE 0.53
	private String colec; // CONVENIO COLECTIVO
	private String epig;
	private String ocup;
	private String vinFam;
	private String profesCat;
	private String reducingCoefic;
	private Integer ident;
	private String mdCtz;
	private String rlce;
	private String collective; // COLECTIVO DEL TRABAJADOR
	private String cno; // CNO
	
	public Boolean quoteMonth;
	
	private String asociativeSA; //INDICATIVO (Situaciones adicionales de afiliacion) solo cuando tenga vacaciones
	//POSIBLES VALORES 001 – VACACIONES RETRIBUIDAS Y NO DISFRUTADAS, 015 – VACACIONES NO DISFRUTADAS, RETRIBUIDAS Y COTIZADAS DURANTE LA RELACION LABORAL

	private Employee() {}
	
	public void accept(Visitor visitor) {
		if(fra != null)				visitor.visitrFra(fra);
		if(fea != null)				visitor.visitrFea(fea);
		if(frb != null)				visitor.visitrFrb(frb);
		if(feb != null)				visitor.visitrFeb(feb);
		if (nss != null)			visitor.visitNSS(nss);
		if (name != null)			visitor.visitName(name);
		if(birthDate != null)		visitor.visitBirthDate(birthDate);
		if(ipf !=  null)			visitor.visitIpf(ipf);
		if(sex != null)				visitor.visitSex(sex);
		if(tlf != null)				visitor.visitTlf(tlf);
		if(ctaCti != null)			visitor.visitCtaCti(ctaCti);
		if(regime != null)			visitor.visitRegime(regime);
		if(companyId != null)		visitor.visitCompanyId(companyId);
		if(companyName != null)		visitor.visitCompanyName(companyName);
		if(situation != null)		visitor.visitSituation(situation);
		if(gc != null)				visitor.visitGc(gc);
		if(gcDesc != null)			visitor.visitGcDesc(gcDesc);
		if(agricultPromo != null)	visitor.visitAgricultPromo(agricultPromo);
		if(workTimeReduct != null)	visitor.visitWorkTimeReduct(workTimeReduct);
		if(contract != null)		visitor.visitContract(contract);
		if(coef != null)			visitor.visitCoef(coef);
		if(factor != null)			visitor.visitFactor(factor);
		if(rlce != null)			visitor.visitRlce(rlce);
		if(colec != null)			visitor.visitColec(colec);
		if(epig != null)			visitor.visitEpig(epig);
		if(ocup != null)			visitor.visitOcup(ocup);
		if(vinFam != null)			visitor.visitVinFam(vinFam);
		if(profesCat != null)		visitor.visitProfesCat(profesCat);
		if(reducingCoefic != null)	visitor.visitReducingcoefic(reducingCoefic);
		if(collective != null)	visitor.visitCollective(collective);
		if(cno != null) visitor.visitCno(cno);
		if(frv != null)	visitor.visitFrv(frv);
		if(mdCtz != null)	visitor.visitMdCtz(mdCtz);
		if(quoteMonth != null) visitor.visitQuoteMonth(quoteMonth);
		if(asociativeSA != null) visitor.visitAsociativeSA(asociativeSA);
	}
	
	public String getIpf() {return ipf;}
	public String getNss() {return nss;}
	public Date getFra() {return fra;}
	public String getSituacion() {return situation;}
	public String getRegime() {return regime;}
	public Integer getIdent() {return ident;}
	public String getOcup() {return ocup;}
	public Optional<String> getName() {return Optional.ofNullable(name);}
	public Optional<String> getGc() {return Optional.ofNullable(gc);}
	public Optional<Date> getFrb() {return Optional.ofNullable(frb);}
	public Optional<Date> getFrv() {return Optional.ofNullable(frv);} // FECHA DE VA
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
	
	public Optional<Double> getFactor(){return Optional.ofNullable(factor);}
	public Optional<String> getSex() {return Optional.ofNullable(sex);}
	public Optional<String> getColec() {return Optional.ofNullable(colec);}
	public Optional<String> getMdctz(){return Optional.ofNullable(mdCtz);}
	public Optional<String> getRlce() {return Optional.ofNullable(rlce);}
	public Optional<String> getCollective() {return Optional.ofNullable(collective);}
	public Optional<String> getCno() {return Optional.ofNullable(cno);}
	public Optional<Boolean> getQuoteMonth() {return Optional.ofNullable(quoteMonth);}
	public Optional<String> getAsociativeSA() {return Optional.ofNullable(asociativeSA);}
	
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append('{');
		accept(new Visitor() {
			@Override
			public void visitNSS(String nss) {stringBuffer.append(String.format(" nss : \"%s\" ", nss));}
			@Override
			public void visitName(String name) {stringBuffer.append(String.format(" name : \"%s\" ", name));}
			@Override
			public void visitBirthDate(Date birthDate) {stringBuffer.append(String.format(" birthDate : \"%s\" ", birthDate));}
			@Override
			public void visitIpf(String ipf) {stringBuffer.append(String.format(" ipf : \"%s\" ", ipf));}
			@Override
			public void visitSex(String sex) {stringBuffer.append(String.format(" sex : \"%s\" ", sex));}
			@Override
			public void visitTlf(String tlf) {stringBuffer.append(String.format(" movil : \"%s\" ", tlf));}
			@Override
			public void visitCtaCti(String ctaCti) {stringBuffer.append(String.format(" ctaCti : \"%s\" ", ctaCti));}
			@Override
			public void visitRegime(String regime) {stringBuffer.append(String.format(" regime : \"%s\" ", regime));}
			@Override
			public void visitCompanyId(String companyId) {stringBuffer.append(String.format(" companyId : \"%s\" ", companyId));}
			@Override
			public void visitCompanyName(String companyName) {stringBuffer.append(String.format(" companyName : \"%s\" ", companyName));}
			@Override
			public void visitSituation(String situation) {stringBuffer.append(String.format(" situation : \"%s\" ", situation));}
			@Override
			public void visitGc(String gc) {stringBuffer.append(String.format(" gc : \"%s\" ", gc));}
			@Override
			public void visitGcDesc(String gcDesc) {stringBuffer.append(String.format(" gcDesc : \"%s\" ", gcDesc));}
			@Override
			public void visitAgricultPromo(Boolean agriculturePromo) {stringBuffer.append(String.format(" agriculturePromo : \"%s\" ", agriculturePromo));}
			@Override
			public void visitWorkTimeReduct(Boolean workTimeReduct) {stringBuffer.append(String.format(" workTimeReduct : \"%s\" ", workTimeReduct));}
			@Override
			public void visitrFra(Date fra) {stringBuffer.append(String.format(" fra : \"%s\" ", fra));}
			@Override
			public void visitrFea(Date fea) {stringBuffer.append(String.format(" fea : \"%s\" ", fea));}
			@Override
			public void visitrFrb(Date frb) {stringBuffer.append(String.format(" frb : \"%s\" ", frb));}
			@Override
			public void visitrFeb(Date feb) {stringBuffer.append(String.format(" feb : \"%s\" ", feb));	}
			@Override
			public void visitFrv(Date frv) {stringBuffer.append(String.format(" Date holidays : \"%s\" ", frv));}
			@Override
			public void visitContract(String contract) {stringBuffer.append(String.format(" contract : \"%s\" ", contract));}
			@Override
			public void visitCoef(String coef) {stringBuffer.append(String.format(" coef : \"%s\" ", coef));}
			@Override
			public void visitFactor(Double factor) {stringBuffer.append(String.format(" factor : \"%s\" ", factor));}
			@Override
			public void visitRlce(String rlce) {stringBuffer.append(String.format(" rlce : \"%s\" ", rlce));}
			@Override
			public void visitColec(String colec) {stringBuffer.append(String.format(" colec : \"%s\" ", colec));}
			@Override
			public void visitEpig(String epig) {stringBuffer.append(String.format(" epig : \"%s\" ", epig));}
			@Override
			public void visitOcup(String ocup) {stringBuffer.append(String.format(" ocup : \"%s\" ", ocup));}
			@Override
			public void visitVinFam(String vimFam) {stringBuffer.append(String.format(" vinFam : \"%s\" ", vimFam));}
			@Override
			public void visitProfesCat(String profesCat) {stringBuffer.append(String.format(" profesCat : \"%s\" ", profesCat));}
			@Override
			public void visitReducingcoefic(String reducingCoefic) {stringBuffer.append(String.format(" reducingCoefic : \"%s\" ", reducingCoefic));}
			@Override
			public void visitCollective(String collective) {stringBuffer.append(String.format(" collective : \"%s\" ", collective));}
			@Override
			public void visitCno(String cno) {stringBuffer.append(String.format(" cno : \"%s\" ", cno));}
			@Override
			public void visitMdCtz(String mdCtz) {stringBuffer.append(String.format(" mdCtz : \"%s\" ", mdCtz));}
			@Override
			public void visitQuoteMonth(Boolean quoteMonth) {stringBuffer.append(String.format(" quoteMonth : \"%s\" ", quoteMonth));}
			@Override
			public void visitAsociativeSA(String asociativeSA) {stringBuffer.append(String.format(" asociativeSA : \"%s\" ", asociativeSA));}
		});
		stringBuffer.append('}');
		return stringBuffer.toString();
	}
	
	public static class EmployeeBuilder {
		
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
		private Date fra;// FECHA DE ALTA
		private Date fea;// FECHA ESTIMADA DE ALTA
		private Date frb;// FECHA DE BAJA
		private Date feb;// FECHA ESTIMADA DE ALTA
		private Date frv; // FECHA DE VACACIONES
		private String contract;
		private String coef;
		private Double factor;
		private String colec;
		private String epig;
		private String ocup;
		private String vinFam;
		private String profesCat;
		private String reducingCoefic;
		private Integer ident;
		private String mdCtz;
		private String rlce;
		private String collective;
		private String cno;
		private Boolean quoteMonth;
		private String asociativeSA;

		public EmployeeBuilder(){}		
		
		public EmployeeBuilder setNss(String nss) {
			if(nss != null && !nss.trim().equals("")) this.nss = nss.trim();
			else this.nss = null;
			return this;
		}
		
		public EmployeeBuilder setRlce(String rlce) {
			if(rlce != null && !rlce.trim().equals("")) this.rlce = rlce.trim();
			else this.rlce = null;
			return this;
		}

		public EmployeeBuilder setName(String name) {
			if(name != null && !name.trim().equals("")) this.name = name.trim();
			else this.name = null;
			return this;
		}
		
		public EmployeeBuilder setBirthDate(Date birthDate) {
			this.birthDate = birthDate;
			return this;
		}


		public EmployeeBuilder setIpf(String ipf) {
			if(ipf != null && !ipf.trim().equals("")) this.ipf = ipf.trim();
			else this.ipf = null;
			return this;
		}


		public EmployeeBuilder setSex(String sex) {
			if(sex != null && !sex.trim().equals("")) this.sex = sex.trim();
			else this.sex = null;
			return this;
		}
		
		public EmployeeBuilder setTlf(String tlf) {
			if(tlf != null && !tlf.trim().equals("")) this.tlf = tlf.trim();
			else this.tlf = null;
			return this;
		}

		public EmployeeBuilder setCtaCti(String ctaCti) {
			if(ctaCti != null && !ctaCti.trim().equals("")) this.ctaCti = ctaCti.trim();
			else this.ctaCti = null;
			return this;
		}

		public EmployeeBuilder setRegime(String regime) {
			if(regime != null && !regime.trim().equals("")) this.regime = regime.trim();
			else this.regime = null;
			return this;
		}

		public EmployeeBuilder setCompanyId(String companyId) {
			if(companyId != null && !companyId.trim().equals("")) this.companyId = companyId.trim();
			else this.companyId = null;
			return this;
		}

		public EmployeeBuilder setCompanyName(String companyName) {
			if(companyName != null && !companyName.trim().equals("")) this.companyName = companyName.trim();
			else this.companyName = null;
			return this;
		}

		public EmployeeBuilder setSituation(String situation) {
			if(situation != null && !situation.trim().equals("")) this.situation = situation.trim();
			else this.situation = null;
			return this;
		}

		public EmployeeBuilder setGc(String gc) {
			if(gc != null && !gc.trim().equals("")) this.gc = gc.trim();
			else this.gc = null;
			return this;
		}
		
		public EmployeeBuilder setGcDesc(String gcDesc) {
			if(gcDesc != null && !gcDesc.trim().equals("")) this.gcDesc = gcDesc.trim();
			else this.gcDesc = null;
			return this;
		}


		public EmployeeBuilder setAgricultPromo(Boolean agricultPromo) {
			this.agricultPromo = agricultPromo;
			return this;
		}


		public EmployeeBuilder setWorkTimeReduct(Boolean workTimeReduct) {
			this.workTimeReduct = workTimeReduct;
			return this;
		}
		
		public EmployeeBuilder setFra(Date fra) {
			this.fra = fra;
			return this;
		}

		public EmployeeBuilder setFea(Date fea) {
			this.fea = fea;
			return this;
		}

		public EmployeeBuilder setFrb(Date frb) {
			this.frb = frb;
			return this;
		}
		
		public EmployeeBuilder setFrv(Date frv) {
			this.frv = frv;
			return this;
		}


		public EmployeeBuilder setFeb(Date feb) {
			this.feb = feb;
			return this;
		}

		public EmployeeBuilder setContract(String contract) {
			if(contract != null && !contract.trim().equals("")) this.contract = contract.trim();
			else this.contract = null;
			return this;
		}

		 /**
		   * use setFactor
		    * @deprecated (use setFactor)
		    */
		 @Deprecated
		public EmployeeBuilder setCoef(String coef) {
			if(coef != null && !coef.trim().equals("")) this.coef = coef.trim();
			else this.coef = null;
			return this;
		}
		
		public EmployeeBuilder setFactor(Double factor) {
			this.factor = factor;
			return this;
		}

		public EmployeeBuilder setColec(String colec) {
			if(colec != null && !colec.trim().equals("")) this.colec = colec.trim();
			else this.colec = null;
			return this;
		}

		public EmployeeBuilder setEpig(String epig) {
			if(epig != null && !epig.trim().equals("")) this.epig = epig.trim();
			else this.epig = null;
			return this;
		}

		public EmployeeBuilder setOcup(String ocup) {
			if(ocup != null && !ocup.trim().equals("")) this.ocup = ocup.trim().toLowerCase();
			else this.ocup = null;
			return this;
		}

		public EmployeeBuilder setVinFam(String vinFam) {
			if(vinFam != null && !vinFam.trim().equals("")) this.vinFam = vinFam.trim();
			else this.vinFam = null;
			return this;
		}

		public EmployeeBuilder setProfesCat(String profesCat) {
			if(profesCat != null && !profesCat.trim().equals("")) this.profesCat = profesCat.trim();
			else this.profesCat = null;
			return this;
		}

		public EmployeeBuilder setReducingCoefic(String reducingCoefic) {
			if(reducingCoefic != null && !reducingCoefic.trim().equals("")) this.reducingCoefic = reducingCoefic.trim();
			else this.reducingCoefic = null;
			return this;
		}
		
		public EmployeeBuilder setIdent(Integer ident) {
			this.ident = ident;
			return this;
		}
		
		public EmployeeBuilder setMdctz(String mdCtz) {
			this.mdCtz = mdCtz;
			return this;
		}
		
		public EmployeeBuilder setQuoteMonth(Boolean quoteMonth) {
			this.quoteMonth = quoteMonth;
			return this;
		}
		
		public EmployeeBuilder setCollective(String collective) {
			this.collective = collective;
			return this;
		}
		
		public EmployeeBuilder setCno(String cno) {
			this.cno = cno;
			return this;
		}
		
		public EmployeeBuilder setAsociativeSA(String asociativeSA) {
			this.asociativeSA = asociativeSA;
			return this;
		}
		
		
		public Employee build(){
			Employee employee = new Employee();
			
			employee.nss = this.nss;
			employee.name = this.name;
			employee.birthDate = this.birthDate;
			employee.ipf = this.ipf;
			employee.sex = this.sex;
			employee.tlf = this.tlf;
			employee.ctaCti = this.ctaCti;
			employee.regime = this.regime;
			employee.companyId = this.companyId;
			employee.companyName = this.companyName;
			employee.situation = this.situation;
			employee.gc = this.gc;
			employee.gcDesc = this.gcDesc;
			employee.agricultPromo = this.agricultPromo;
			employee.workTimeReduct = this.workTimeReduct;
			employee.fra = this.fra;
			employee.fea = this.fea;
			employee.frb = this.frb;
			employee.feb = this.feb;
			employee.frv = this.frv;
			employee.contract = this.contract;
			employee.coef = this.coef;
			employee.factor = this.factor;
			employee.colec = this.colec;
			employee.epig = this.epig;
			employee.ocup = this.ocup;
			employee.vinFam = this.vinFam;
			employee.profesCat = this.profesCat;
			employee.reducingCoefic = this.reducingCoefic;
			employee.ident = this.ident;
			employee.mdCtz = this.mdCtz;
			employee.rlce = this.rlce;
			employee.collective = this.collective;
			employee.cno = this.cno;
			employee.quoteMonth = this.quoteMonth;
			employee.asociativeSA = this.asociativeSA;
			return employee;
		}

	}	
	
	public static interface Visitor{
		void visitrFra(Date fra);
		void visitQuoteMonth(Boolean quoteMonth);
		void visitFrv(Date frv);
		void visitrFea(Date fea);
		void visitrFrb(Date frb);
		void visitrFeb(Date feb);
		void visitNSS(String nss);
		void visitName(String name);
		void visitBirthDate(Date birthDate);
		void visitIpf(String ipf);
		void visitSex(String sex);
		void visitTlf(String tlf);
		void visitCtaCti(String ctaCti);
		void visitRegime(String regime);
		void visitCompanyId(String companyId);
		void visitCompanyName(String companyName);
		void visitSituation(String situaction);
		void visitGc(String gc);
		void visitGcDesc(String gcDesc);
		void visitAgricultPromo(Boolean agriculturePromo);
		void visitWorkTimeReduct(Boolean workTimeReduct);
		void visitContract(String contract);
		void visitCoef(String coef);
		void visitFactor(Double factor);
		void visitRlce(String rlce);
		void visitColec(String colec);
		void visitEpig(String epig);
		void visitOcup(String ocup);
		void visitVinFam(String vimFam);
		void visitProfesCat(String profesCat);
		void visitReducingcoefic(String reducingCoefic);	
		void visitCollective(String collective);	
		void visitCno(String cno);
		void visitMdCtz(String mdCtz);
		void visitAsociativeSA(String asociativeSA);
	}
	
	public static abstract class AbstractVisitor implements Visitor {
		@Override
		public void visitrFra(Date fra) {}
		@Override
		public void visitrFea(Date fea) {}
		@Override
		public void visitrFrb(Date frb) {}
		@Override
		public void visitrFeb(Date feb) {}
		@Override
		public void visitFrv(Date frv) {}
		@Override
		public void visitNSS(String nss) {}
		@Override
		public void visitName(String name) {}
		@Override
		public void visitBirthDate(Date birthDate) {}
		@Override
		public void visitIpf(String ipf) {}
		@Override
		public void visitSex(String sex) {}
		@Override
		public void visitTlf(String tlf) {}
		@Override
		public void visitCtaCti(String ctaCti) {}
		@Override
		public void visitRegime(String regime) {}
		@Override
		public void visitCompanyId(String companyId) {}
		@Override
		public void visitCompanyName(String companyName) {}
		@Override
		public void visitSituation(String situaction) {}
		@Override
		public void visitGc(String gc) {}
		@Override
		public void visitAgricultPromo(Boolean agriculturePromo) {}
		@Override
		public void visitWorkTimeReduct(Boolean workTimeReduct) {}
		@Override
		public void visitContract(String contract) {}
		@Override
		public void visitCoef(String coef) {}
		@Override
		public void visitFactor(Double factor) {}
		@Override
		public void visitRlce(String rlce) {}
		@Override
		public void visitColec(String colec) {}
		@Override
		public void visitEpig(String epig) {}
		@Override
		public void visitOcup(String ocup) {}
		@Override
		public void visitVinFam(String vimFam) {}
		@Override
		public void visitProfesCat(String profesCat) {}
		@Override
		public void visitReducingcoefic(String reducingCoefic) {}
		@Override
		public void visitCollective(String collective) {}
		@Override
		public void visitCno(String cno) {}
		@Override
		public void visitMdCtz(String mdCtz) {}
		@Override
		public void visitQuoteMonth(Boolean quoteMonth) {}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agricultPromo == null) ? 0 : agricultPromo.hashCode());
		result = prime * result + ((birthDate == null) ? 0 : birthDate.hashCode());
		result = prime * result + ((coef == null) ? 0 : coef.hashCode());
		result = prime * result + ((colec == null) ? 0 : colec.hashCode());
		result = prime * result + ((companyId == null) ? 0 : companyId.hashCode());
		result = prime * result + ((companyName == null) ? 0 : companyName.hashCode());
		result = prime * result + ((contract == null) ? 0 : contract.hashCode());
		result = prime * result + ((ctaCti == null) ? 0 : ctaCti.hashCode());
		result = prime * result + ((epig == null) ? 0 : epig.hashCode());
		result = prime * result + ((fea == null) ? 0 : fea.hashCode());
		result = prime * result + ((feb == null) ? 0 : feb.hashCode());
		result = prime * result + ((fra == null) ? 0 : fra.hashCode());
		result = prime * result + ((frb == null) ? 0 : frb.hashCode());
		result = prime * result + ((gc == null) ? 0 : gc.hashCode());
		result = prime * result + ((gcDesc == null) ? 0 : gcDesc.hashCode());
		result = prime * result + ((ident == null) ? 0 : ident.hashCode());
		result = prime * result + ((ipf == null) ? 0 : ipf.hashCode());
		result = prime * result + ((mdCtz == null) ? 0 : mdCtz.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nss == null) ? 0 : nss.hashCode());
		result = prime * result + ((ocup == null) ? 0 : ocup.hashCode());
		result = prime * result + ((profesCat == null) ? 0 : profesCat.hashCode());
		result = prime * result + ((reducingCoefic == null) ? 0 : reducingCoefic.hashCode());
		result = prime * result + ((regime == null) ? 0 : regime.hashCode());
		result = prime * result + ((rlce == null) ? 0 : rlce.hashCode());
		result = prime * result + ((sex == null) ? 0 : sex.hashCode());
		result = prime * result + ((situation == null) ? 0 : situation.hashCode());
		result = prime * result + ((tlf == null) ? 0 : tlf.hashCode());
		result = prime * result + ((vinFam == null) ? 0 : vinFam.hashCode());
		result = prime * result + ((workTimeReduct == null) ? 0 : workTimeReduct.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		if (agricultPromo == null) {
			if (other.agricultPromo != null)
				return false;
		} else if (!agricultPromo.equals(other.agricultPromo))
			return false;
		if (birthDate == null) {
			if (other.birthDate != null)
				return false;
		} else if (!birthDate.equals(other.birthDate))
			return false;
		if (coef == null) {
			if (other.coef != null)
				return false;
		} else if (!coef.equals(other.coef))
			return false;
		if (colec == null) {
			if (other.colec != null)
				return false;
		} else if (!colec.equals(other.colec))
			return false;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		if (companyName == null) {
			if (other.companyName != null)
				return false;
		} else if (!companyName.equals(other.companyName))
			return false;
		if (contract == null) {
			if (other.contract != null)
				return false;
		} else if (!contract.equals(other.contract))
			return false;
		if (ctaCti == null) {
			if (other.ctaCti != null)
				return false;
		} else if (!ctaCti.equals(other.ctaCti))
			return false;
		if (epig == null) {
			if (other.epig != null)
				return false;
		} else if (!epig.equals(other.epig))
			return false;
		if (fea == null) {
			if (other.fea != null)
				return false;
		} else if (!fea.equals(other.fea))
			return false;
		if (feb == null) {
			if (other.feb != null)
				return false;
		} else if (!feb.equals(other.feb))
			return false;
		if (fra == null) {
			if (other.fra != null)
				return false;
		} else if (!fra.equals(other.fra))
			return false;
		if (frb == null) {
			if (other.frb != null)
				return false;
		} else if (!frb.equals(other.frb))
			return false;
		if (gc == null) {
			if (other.gc != null)
				return false;
		} else if (!gc.equals(other.gc))
			return false;
		if (gcDesc == null) {
			if (other.gcDesc != null)
				return false;
		} else if (!gcDesc.equals(other.gcDesc))
			return false;
		if (ident == null) {
			if (other.ident != null)
				return false;
		} else if (!ident.equals(other.ident))
			return false;
		if (ipf == null) {
			if (other.ipf != null)
				return false;
		} else if (!ipf.equals(other.ipf))
			return false;
		if (mdCtz == null) {
			if (other.mdCtz != null)
				return false;
		} else if (!mdCtz.equals(other.mdCtz))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nss == null) {
			if (other.nss != null)
				return false;
		} else if (!nss.equals(other.nss))
			return false;
		if (ocup == null) {
			if (other.ocup != null)
				return false;
		} else if (!ocup.equals(other.ocup))
			return false;
		if (profesCat == null) {
			if (other.profesCat != null)
				return false;
		} else if (!profesCat.equals(other.profesCat))
			return false;
		if (reducingCoefic == null) {
			if (other.reducingCoefic != null)
				return false;
		} else if (!reducingCoefic.equals(other.reducingCoefic))
			return false;
		if (regime == null) {
			if (other.regime != null)
				return false;
		} else if (!regime.equals(other.regime))
			return false;
		if (rlce == null) {
			if (other.rlce != null)
				return false;
		} else if (!rlce.equals(other.rlce))
			return false;
		if (sex == null) {
			if (other.sex != null)
				return false;
		} else if (!sex.equals(other.sex))
			return false;
		if (situation == null) {
			if (other.situation != null)
				return false;
		} else if (!situation.equals(other.situation))
			return false;
		if (tlf == null) {
			if (other.tlf != null)
				return false;
		} else if (!tlf.equals(other.tlf))
			return false;
		if (vinFam == null) {
			if (other.vinFam != null)
				return false;
		} else if (!vinFam.equals(other.vinFam))
			return false;
		if (workTimeReduct == null) {
			if (other.workTimeReduct != null)
				return false;
		} else if (!workTimeReduct.equals(other.workTimeReduct))
			return false;
		return true;
	}
}


