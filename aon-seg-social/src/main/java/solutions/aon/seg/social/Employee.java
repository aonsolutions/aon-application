package solutions.aon.seg.social;

import java.util.Date;

public class Employee {

	private String nss;
	private String name;
	private Date startDate;
	private Date birthDate;
	private String ipf;
	private String sex;
	private String ctaCti;
	private String regime;
	private String companyId;
	private String companyName;
	private String situation;
	private String gc;
	private String agricultPromo;
	private String workTimeReduct;
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
	private Employee() {}
	
	public void accept(Visitor visitor) {
		
		if (nss != null)			visitor.visitNSS(nss);
		if (name != null)			visitor.visitName(name);
		if(startDate != null)		visitor.visitStartDate(startDate);
		if(birthDate != null)		visitor.visitBirthDate(birthDate);
		if(ipf !=  null)			visitor.visitIpf(ipf);
		if(sex != null)				visitor.visitSex(sex);
		if(ctaCti != null)			visitor.visitCtaCti(ctaCti);
		if(regime != null)			visitor.visitRegime(regime);
		if(companyId != null)		visitor.visitCompanyId(companyId);
		if(companyName != null)		visitor.visitCompanyName(companyName);
		if(situation != null)		visitor.visitSituation(situation);
		if(gc != null)				visitor.visitGc(gc);
		if(agricultPromo != null)	visitor.visitAgricultPromo(agricultPromo);
		if(workTimeReduct != null)	visitor.visitWorkTimeReduct(workTimeReduct);
		if(fra != null)				visitor.visitrFra(fra);
		if(fea != null)				visitor.visitrFea(fea);
		if(frb != null)				visitor.visitrFrb(frb);
		if(feb != null)				visitor.visitrFeb(feb);
		if(contract != null)		visitor.visitContract(contract);
		if(coef != null)			visitor.visitCoef(coef);
		if(colec != null)			visitor.visitColec(colec);
		if(epig != null)			visitor.visitEpig(epig);
		if(ocup != null)			visitor.visitOcup(ocup);
		if(vinFam != null)			visitor.visitVinFam(vinFam);
		if(profesCat != null)		visitor.visitProfesCat(profesCat);
		if(reducingCoefic != null)	visitor.visitReducingcoefic(reducingCoefic);
	
	}
	
	//TO_DO Generate getters y toString
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append('{');
		accept(new Visitor() {
			@Override
			public void visitNSS(String nss) {
				stringBuffer.append(String.format("nss : \"%s\"", nss));
			}

			@Override
			public void visitName(String name) {
				stringBuffer.append(String.format("name : \"%s\"", name));
			}

			@Override
			public void visitStartDate(Date startDate) {
				stringBuffer.append(String.format("startDate : \"%s\"", startDate));
				
			}

			@Override
			public void visitBirthDate(Date birthDate) {
				stringBuffer.append(String.format("birthDate : \"%s\"", birthDate));				
			}

			@Override
			public void visitIpf(String ipf) {
				stringBuffer.append(String.format("ipf : \"%s\"", ipf));	
			}

			@Override
			public void visitSex(String sex) {
				stringBuffer.append(String.format("sex : \"%s\"", sex));	
			}

			@Override
			public void visitCtaCti(String ctaCti) {
				stringBuffer.append(String.format("ctaCti : \"%s\"", ctaCti));
			}

			@Override
			public void visitRegime(String regime) {
				stringBuffer.append(String.format("regime : \"%s\"", regime));
			}

			@Override
			public void visitCompanyId(String companyId) {
				stringBuffer.append(String.format("companyId : \"%s\"", companyId));
			}

			@Override
			public void visitCompanyName(String companyName) {
				stringBuffer.append(String.format("companyName : \"%s\"", companyName));				
			}

			@Override
			public void visitSituation(String situation) {
				stringBuffer.append(String.format("situation : \"%s\"", situation));
			}

			@Override
			public void visitGc(String gc) {
				stringBuffer.append(String.format("gc : \"%s\"", gc));
			}

			@Override
			public void visitAgricultPromo(String agriculturePromo) {
				stringBuffer.append(String.format("agriculturePromo : \"%s\"", agriculturePromo));
			}

			@Override
			public void visitWorkTimeReduct(String workTimeReduct) {
				stringBuffer.append(String.format("workTimeReduct : \"%s\"", workTimeReduct));				
			}

			@Override
			public void visitrFra(Date fra) {
				stringBuffer.append(String.format("fra : \"%s\"", fra));
			}

			@Override
			public void visitrFea(Date fea) {
				stringBuffer.append(String.format("fea : \"%s\"", fea));
			}

			@Override
			public void visitrFrb(Date frb) {
				stringBuffer.append(String.format("frb : \"%s\"", frb));
			}

			@Override
			public void visitrFeb(Date feb) {
				stringBuffer.append(String.format("feb : \"%s\"", feb));	
			}

			@Override
			public void visitContract(String contract) {
				stringBuffer.append(String.format("contract : \"%s\"", contract));
			}

			@Override
			public void visitCoef(String coef) {
				stringBuffer.append(String.format("coef : \"%s\"", coef));
			}

			@Override
			public void visitColec(String colec) {
				stringBuffer.append(String.format("colec : \"%s\"", colec));
			}

			@Override
			public void visitEpig(String epig) {
				stringBuffer.append(String.format("epig : \"%s\"", epig));
			}

			@Override
			public void visitOcup(String ocup) {
				stringBuffer.append(String.format("ocup : \"%s\"", ocup));
			}

			@Override
			public void visitVinFam(String vimFam) {
				stringBuffer.append(String.format("vinFam : \"%s\"", vimFam));
			}

			@Override
			public void visitProfesCat(String profesCat) {
				stringBuffer.append(String.format("profesCat : \"%s\"", profesCat));		
			}

			@Override
			public void visitReducingcoefic(String reducingCoefic) {
				stringBuffer.append(String.format("reducingCoefic : \"%s\"", reducingCoefic));
			}

		});
		stringBuffer.append('}');
		return stringBuffer.toString();
	}
	
	public static class EmployeeBuilder {
		
		private String nss;
		private String name;
		private Date startDate;
		private Date birthDate;
		private String ipf;
		private String sex;
		private String ctaCti;
		private String regime;
		private String companyId;
		private String companyName;
		private String situation;
		private String gc;
		private String agricultPromo;
		private String workTimeReduct;
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
		
		public EmployeeBuilder(){}		
		
	
		public EmployeeBuilder setNss(String nss) {
			if(nss != null && !nss.trim().equals("")) this.nss = nss.trim();
			else this.nss = null;
			return this;
		}


		public EmployeeBuilder setName(String name) {
			if(name != null && !name.trim().equals("")) this.name = name.trim();
			else this.name = null;
			return this;
		}
		
		public EmployeeBuilder setStartDate(Date startDate) {
			this.startDate = startDate;
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


		public EmployeeBuilder setAgricultPromo(String agricultPromo) {
			if(agricultPromo != null && !agricultPromo.trim().equals("")) this.agricultPromo = agricultPromo.trim();
			else this.agricultPromo = null;
			return this;
		}


		public EmployeeBuilder setWorkTimeReduct(String workTimeReduct) {
			if(workTimeReduct != null && !workTimeReduct.trim().equals("")) this.workTimeReduct = workTimeReduct.trim();
			else this.workTimeReduct = null;
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


		public EmployeeBuilder setFeb(Date feb) {
			this.feb = feb;
			return this;
		}


		public EmployeeBuilder setContract(String contract) {
			if(contract != null && !contract.trim().equals("")) this.contract = contract.trim();
			else this.contract = null;
			return this;
		}


		public EmployeeBuilder setCoef(String coef) {
			if(coef != null && !coef.trim().equals("")) this.coef = coef.trim();
			else this.coef = null;
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
			if(ocup != null && !ocup.trim().equals("")) this.ocup = ocup.trim();
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
		
		
		public Employee build(){
			Employee employee = new Employee();
			
			employee.nss = this.nss;
			employee.name = this.name;
			employee.startDate = this.startDate;
			employee.birthDate = this.birthDate;
			employee.ipf = this.ipf;
			employee.sex = this.sex;
			employee.ctaCti = this.ctaCti;
			employee.regime = this.regime;
			employee.companyId = this.companyId;
			employee.companyName = this.companyName;
			employee.situation = this.situation;
			employee.gc = this.gc;
			employee.agricultPromo = this.agricultPromo;
			employee.workTimeReduct = this.workTimeReduct;
			employee.fra = this.fra;
			employee.fea = this.fea;
			employee.frb = this.frb;
			employee.feb = this.feb;
			employee.contract = this.contract;
			employee.coef = this.coef;
			employee.colec = this.colec;
			employee.epig = this.epig;
			employee.ocup = this.ocup;
			employee.vinFam = this.vinFam;
			employee.profesCat = this.profesCat;
			employee.reducingCoefic = this.reducingCoefic;
			
			return employee;
		}
	}	
	
	public static interface Visitor{
		void visitNSS(String nss);
		void visitName(String name);
		void visitStartDate(Date startDate);
		void visitBirthDate(Date birthDate);
		void visitIpf(String ipf);
		void visitSex(String sex);
		void visitCtaCti(String ctaCti);
		void visitRegime(String regime);
		void visitCompanyId(String companyId);
		void visitCompanyName(String companyName);
		void visitSituation(String situaction);
		void visitGc(String gc);
		void visitAgricultPromo(String agriculturePromo);
		void visitWorkTimeReduct(String workTimeReduct);
		void visitrFra(Date fra);
		void visitrFea(Date fea);
		void visitrFrb(Date frb);
		void visitrFeb(Date feb);
		void visitContract(String contract);
		void visitCoef(String coef);
		void visitColec(String colec);
		void visitEpig(String epig);
		void visitOcup(String ocup);
		void visitVinFam(String vimFam);
		void visitProfesCat(String profesCat);
		void visitReducingcoefic(String reducingCoefic);	
	}
}


