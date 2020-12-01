package com.esferalia.aon.in.payroll.tgss.report;

import java.util.Date;

public class Employee {

	private String nss;
	private String name;
	private String ipf;

	private String ctaCti;
	private String regime;
	private String companyName;

	private String situation;
	private String gc;
	private Date fra;
	private Date fea;
	private Date frb;
	private Date feb;
	private String tc;
	private Float ctp;
	private String ep;
	private Float at;
	private Float ims;
	private Float total;
	private Integer cotDays;


	private Employee() {}
	
	public void accept(Visitor visitor) {
		
		if (nss != null)			visitor.visitNSS(nss);
		if (name != null)			visitor.visitName(name);
		if(ipf !=  null)			visitor.visitIpf(ipf);
		if(ctaCti != null)			visitor.visitCtaCti(ctaCti);
		if(regime != null)			visitor.visitRegime(regime);
		if(companyName != null)		visitor.visitCompanyName(companyName);
		if(situation != null)		visitor.visitSituation(situation);
		if(gc != null)				visitor.visitGc(gc);
		if(fra != null)				visitor.visitFra(fra);
		if(fea != null)				visitor.visitFea(fea);
		if(frb != null)				visitor.visitFrb(frb);
		if(feb != null)				visitor.visitFeb(feb);
		if(tc != null)				visitor.visitTc(tc);
		if(ctp != null)				visitor.visitCtp(ctp);
		if(ep != null)				visitor.visitEp(ep);
		if(at != null)				visitor.visitAt(at);
		if(ims != null)				visitor.visitIms(ims);
		if(total != null)			visitor.visitTotal(total);
		if(cotDays != null)			visitor.visitCotDays(cotDays);
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("{\n");
		accept(new Visitor() {
			@Override
			public void visitNSS(String nss) {stringBuffer.append(String.format(" nss : \"%s\" \n", nss));}
			@Override
			public void visitName(String name) {stringBuffer.append(String.format(" name : \"%s\" \n", name));}
			@Override
			public void visitIpf(String ipf) {stringBuffer.append(String.format(" ipf : \"%s\" \n", ipf));}
			@Override
			public void visitCtaCti(String ctaCti) {stringBuffer.append(String.format(" ctaCti : \"%s\" \n", ctaCti));}
			@Override
			public void visitRegime(String regime) {stringBuffer.append(String.format(" regime : \"%s\" \n", regime));}
			@Override
			public void visitCompanyName(String companyName) {stringBuffer.append(String.format(" companyName : \"%s\" \n", companyName));}
			@Override
			public void visitSituation(String situation) {stringBuffer.append(String.format(" situation : \"%s\" \n", situation));}
			@Override
			public void visitGc(String gc) {stringBuffer.append(String.format(" gc : \"%s\" \n", gc));}
			@Override
			public void visitFra(Date fra) {stringBuffer.append(String.format(" fra : \"%s\" \n", fra));}
			@Override
			public void visitFea(Date fea) {stringBuffer.append(String.format(" fea : \"%s\" \n", fea));}
			@Override
			public void visitFrb(Date frb) {stringBuffer.append(String.format(" frb : \"%s\" \n", frb));}
			@Override
			public void visitFeb(Date feb) {stringBuffer.append(String.format(" feb : \"%s\" \n", feb));}
			@Override
			public void visitTc(String tc) {stringBuffer.append(String.format(" tc : \"%s\" \n", tc));}
			@Override
			public void visitCtp(float ctp) {stringBuffer.append(String.format(" ctp : \"%s\" \n", ctp));	}
			@Override
			public void visitEp(String ep) {stringBuffer.append(String.format(" ep : \"%s\" \n", ep));	}
			@Override
			public void visitAt(float at) {stringBuffer.append(String.format(" at : \"%s\" \n", at));	}
			@Override
			public void visitIms(float ims) {stringBuffer.append(String.format(" ims : \"%s\" \n", ims));	}
			@Override
			public void visitTotal(float total) {stringBuffer.append(String.format(" total : \"%s\" \n", total));	}
			@Override
			public void visitCotDays(int cotDays) {stringBuffer.append(String.format(" cotDays : \"%s\" \n", cotDays));	}
		});
		stringBuffer.append('}');
		return stringBuffer.toString();
	}
	
	public static class EmployeeBuilder {
		
		private String nss;
		private String name;
		private String ipf;
		private String ctaCti;
		private String regime;
		private String companyName;
		private String situation;
		private String gc;
		private Date fra;
		private Date fea;
		private Date frb;
		private Date feb;
		private String tc;
		private float ctp;
		private String ep;
		private float at;
		private float ims;
		private float total;
		private int cotDays;

		public EmployeeBuilder(){}		
		
	
		public EmployeeBuilder setNss(String nss) {
			if(nss != null && !nss.trim().equals("")) this.nss = nss.trim();
			else this.nss = null;
			return this;
		}

		public EmployeeBuilder setIpf(String ipf) {
			if(ipf != null && !ipf.trim().equals("")) this.ipf = ipf.trim();
			else this.ipf = null;
			return this;
		}

		public EmployeeBuilder setName(String name) {
			if(name != null && !name.trim().equals("")) this.name = name.trim();
			else this.name = null;
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

		public EmployeeBuilder setTc(String tc) {
			this.tc = tc;
			return this;
		}

		public EmployeeBuilder setCtp(float ctp) {
			this.ctp = ctp;
			return this;
		}

		public EmployeeBuilder setEp(String ep) {
			this.ep = ep;
			return this;
		}

		public EmployeeBuilder setAt(float at) {
			this.at = at;
			return this;
		}

		public EmployeeBuilder setIms(float ims) {
			this.ims = ims;
			return this;
		}

		public EmployeeBuilder setTotal(float total) {
			this.total = total;
			return this;
		}

		public EmployeeBuilder setCotDays(int cotDays) {
			this.cotDays = cotDays;
			return this;
		}

		public Employee build(){
			Employee employee = new Employee();
			
			employee.nss = this.nss;
			employee.name = this.name;
			employee.ipf = this.ipf;
			employee.ctaCti = this.ctaCti;
			employee.regime = this.regime;
			employee.companyName = this.companyName;
			employee.situation = this.situation;
			employee.gc = this.gc;
			employee.fra = this.fra;
			employee.fea = this.fea;
			employee.frb = this.frb;
			employee.feb = this.feb;
			employee.tc = this.tc;
			employee.ctp = this.ctp;
			employee.ep = this.ep;
			employee.at = this.at;
			employee.ims = this.ims;
			employee.total = this.total;
			employee.cotDays = this.cotDays;

			return employee;
		}

	}	
	
	public interface Visitor{
		void visitNSS(String nss);
		void visitName(String name);
		void visitIpf(String ipf);
		void visitCtaCti(String ctaCti);
		void visitRegime(String regime);
		void visitCompanyName(String companyName);
		void visitSituation(String situaction);
		void visitGc(String gc);
		void visitFra(Date fra);
		void visitFea(Date fea);
		void visitFrb(Date frb);
		void visitFeb(Date feb);
		void visitTc(String tc);
		void visitCtp(float ctp);
		void visitEp(String ep);
		void visitAt(float at);
		void visitIms(float ims);
		void visitTotal(float total);
		void visitCotDays(int cotDays);
	}
	
	public static  abstract class AbstractVisitor implements Visitor {

		@Override
		public void visitNSS(String nss) {}
		@Override
		public void visitName(String name) {}
		@Override
		public void visitIpf(String ipf) {}
		@Override
		public void visitCtaCti(String ctaCti) {}
		@Override
		public void visitRegime(String regime) {}
		@Override
		public void visitCompanyName(String companyName) {}
		@Override
		public void visitSituation(String situation) {}
		@Override
		public void visitGc(String gc) {}
		@Override
		public void visitFra(Date fra) {}
		@Override
		public void visitFea(Date fea) {}
		@Override
		public void visitFrb(Date frb) {}
		@Override
		public void visitFeb(Date feb) {}
		@Override
		public void visitTc(String tc) {}
		@Override
		public void visitCtp(float ctp) {}
		@Override
		public void visitEp(String ep) {}
		@Override
		public void visitAt(float at) {}
		@Override
		public void visitIms(float ims) {}
		@Override
		public void visitTotal(float total) {}
		@Override
		public void visitCotDays(int cotDays) {}
	}
	
}


