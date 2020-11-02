package solutions.aon.seg.social.objects;

import java.util.Date;

import solutions.aon.seg.social.objects.SecondaryUser.SecondaryUserBuilder.Visitor;

public class SecondaryUser {

	private String authoritation;
	private String authoritation_entity;
	private String main_user_name;
	private String main_user_ipf;
	private String main_user_naf;
	
	private String name;
	private String province;
	private String ipf;
	private String naf;
	private String situation;
	private Date situation_date;
	private String telephone;
	private String fax;
	private String mobile;
	private String mail;
	
	private SecondaryUser() {}

	public void setAuthoritation(String authoritation) {this.authoritation = authoritation;}
	public void setAuthoritation_entity(String authoritation_entity) {this.authoritation_entity = authoritation_entity;}
	public void setMain_user_name(String main_user_name) {this.main_user_name = main_user_name;}
	public void setMain_user_ipf(String main_user_ipf) {this.main_user_ipf = main_user_ipf;}
	public void setMain_user_naf(String main_user_naf) {this.main_user_naf = main_user_naf;}
	public void setName(String name) {this.name = name;}
	public void setProvince(String province) {this.province = province;}
	public void setIpf(String ipf) {this.ipf = ipf;}
	public void setNaf(String naf) {this.naf = naf;}
	public void setSituation(String situation) {this.situation = situation;}
	public void setSituation_date(Date situation_date) {this.situation_date = situation_date;}
	public void setTelephone(String telephone) {this.telephone = telephone;}
	public void setFax(String fax) {this.fax = fax;}
	public void setMobile(String mobile) {this.mobile = mobile;}
	public void setMail(String mail) {this.mail = mail;}
		
	
	public void accept(Visitor visitor) {
		if(authoritation != null) visitor.visitAuthoritation(authoritation);
		if(authoritation_entity != null) visitor.visitAuthoritation_entity(authoritation_entity);
		if(main_user_name != null) visitor.visitMain_user_name(main_user_name);
		if(main_user_ipf != null) visitor.visitMain_user_ipf(main_user_ipf);
		if(main_user_naf != null) visitor.visitMain_user_naf(main_user_naf);
		if(name != null) visitor.visitName(name);
		if(province != null) visitor.visitProvince(province);
		if(ipf != null) visitor.visitIpf(ipf);
		if(naf != null) visitor.visitNaf(naf);
		if(situation != null) visitor.visitSituation(situation);
		if(situation_date != null) visitor.visitSituation_date(situation_date);
		if(telephone != null) visitor.visitTelephone(telephone);
		if(fax != null) visitor.visitFax(fax);
		if(mobile != null) visitor.visitMobile(mobile);
		if(mail != null) visitor.visitMail(mail);
	}
	
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append('{');
		
		accept(new Visitor() {
			
			@Override
			public void visitTelephone(String telephone) {stringBuffer.append(String.format(" telephone : \"%s\" ", telephone));}
			@Override
			public void visitSituation_date(Date situation_date) {stringBuffer.append(String.format(" situation_date : \"%s\" ", situation_date));}
			@Override
			public void visitSituation(String situation) {stringBuffer.append(String.format(" situation : \"%s\" ", situation));}
			@Override
			public void visitProvince(String provinces) {stringBuffer.append(String.format(" provinces : \"%s\" ", provinces));}
			@Override
			public void visitName(String name) {stringBuffer.append(String.format(" name : \"%s\" ", name));}
			@Override
			public void visitNaf(String naf) {stringBuffer.append(String.format(" naf : \"%s\" ", naf));}
			@Override
			public void visitMobile(String mobile) {stringBuffer.append(String.format(" mobile : \"%s\" ", mobile));}
			@Override
			public void visitMain_user_name(String main_user_name) {stringBuffer.append(String.format(" main_user_name : \"%s\" ", main_user_name));}
			@Override
			public void visitMain_user_naf(String main_user_naf) {stringBuffer.append(String.format(" main_user_naf : \"%s\" ", main_user_naf));}
			@Override
			public void visitMain_user_ipf(String main_user_ipf) {stringBuffer.append(String.format(" main_user_ipf : \"%s\" ", main_user_ipf));}
			@Override
			public void visitMail(String mail) {stringBuffer.append(String.format(" mail : \"%s\" ", mail));}
			@Override
			public void visitIpf(String ipf) {stringBuffer.append(String.format(" ipf : \"%s\" ", ipf));}
			@Override
			public void visitFax(String fax) {stringBuffer.append(String.format(" fax : \"%s\" ", fax));}
			@Override
			public void visitAuthoritation_entity(String authoritation_entity) {stringBuffer.append(String.format(" authoritation_entity : \"%s\" ", authoritation_entity));}
			@Override
			public void visitAuthoritation(String authoritation) {stringBuffer.append(String.format(" authoritation : \"%s\" ", authoritation));}
		}); 
		
		stringBuffer.append('}');
		return stringBuffer.toString();
	}
	
	
	public static class SecondaryUserBuilder{
		
		private String authoritation;
		private String authoritation_entity;
		private String main_user_name;
		private String main_user_ipf;
		private String main_user_naf;
		
		private String name;
		private String province;
		private String ipf;
		private String naf;
		private String situation;
		private Date situation_date;
		private String telephone;
		private String fax;
		private String mobile;
		private String mail;
		
		public SecondaryUserBuilder setAuthoritation(String authoritation) {
			if(authoritation != null && !authoritation.trim().equals("")) this.authoritation = authoritation.trim();
			else this.authoritation = null;
			return this;
		}
		public SecondaryUserBuilder setAuthoritation_entity(String authoritation_entity) {
			if(authoritation_entity != null && !authoritation_entity.trim().equals("")) this.authoritation_entity = authoritation_entity.trim();
			else this.authoritation_entity = null;
			return this;
		}
		public SecondaryUserBuilder setMain_user_name(String main_user_name) {
			if(main_user_name != null && !main_user_name.trim().equals("")) this.main_user_name = main_user_name.trim();
			else this.main_user_name = null;
			return this;
		}
		public SecondaryUserBuilder setMain_user_ipf(String main_user_ipf) {
			if(main_user_ipf != null && !main_user_ipf.trim().equals("")) this.main_user_ipf = main_user_ipf.trim();
			else this.main_user_ipf = null; {
				// TODO Auto-generated method stub
				
			}
			
			return this;
		}
		public SecondaryUserBuilder setMain_user_naf(String main_user_naf) {
			if(main_user_naf != null && !main_user_naf.trim().equals("")) this.main_user_naf = main_user_naf.trim();
			else this.main_user_naf = null;
			return this;
		}
		public SecondaryUserBuilder setName(String name) {
			if(name != null && !name.trim().equals("")) this.name = name.trim();
			else this.name = null;
			return this;
		}
		public SecondaryUserBuilder setProvince(String province) {
			if(province != null && !province.trim().equals("")) this.province = province.trim();
			else this.province = null;
			return this;
		}
		public SecondaryUserBuilder setIpf(String ipf) {
			if(ipf != null && !ipf.trim().equals("")) this.ipf = ipf.trim();
			else this.ipf = null;
			return this;
		}
		public SecondaryUserBuilder setNaf(String naf) {
			if(naf != null && !naf.trim().equals("")) this.naf = naf.trim();
			else this.naf = null;
			return this;
		}
		public SecondaryUserBuilder setSituation(String situation) {
			if(situation != null && !situation.trim().equals("")) this.situation = situation.trim();
			else this.situation = null;
			return this;
		}
		public SecondaryUserBuilder setSituation_date(Date situation_date) {
			this.situation_date = situation_date;
			return this;
		}
		public SecondaryUserBuilder setTelephone(String telephone) {
			if(telephone != null && !telephone.trim().equals("")) this.telephone = telephone.trim();
			else this.telephone = null;
			return this;
		}
		public SecondaryUserBuilder setFax(String fax) {
			if(fax != null && !fax.trim().equals("")) this.fax = fax.trim();
			else this.fax = null;
			return this;
		}
		public SecondaryUserBuilder setMobile(String mobile) {
			if(mobile != null && !mobile.trim().equals("")) this.mobile = mobile.trim();
			else this.mobile = null;
			return this;
		}
		public SecondaryUserBuilder setMail(String mail) {
			if(mail != null && !mail.trim().equals("")) this.mail = mail.trim();
			else this.mail = null;
			return this;
		}
		
		public SecondaryUser build() {
			SecondaryUser user = new SecondaryUser();
			
			user.authoritation = this.authoritation;
			user.authoritation_entity = this.authoritation_entity;
			user.main_user_name = this.main_user_name;
			user.main_user_ipf = this.main_user_ipf;
			user.main_user_naf = this.main_user_naf;
			user.name = this.name;
			user.province = this.province;
			user.ipf = this.ipf;
			user.naf = this.naf;
			user.situation = this.situation;
			user.situation_date = this.situation_date;
			user.telephone = this.telephone;
			user.fax = this.fax;
			user.mobile = this.mobile;
			user.mail = this.mail;
			
			return user;
		}
		
		public interface Visitor{
			void visitAuthoritation(String authoritation);
			void visitAuthoritation_entity(String authoritation_entity);
			void visitMain_user_name(String main_user_name);
			void visitMain_user_ipf(String main_user_ipf);
			void visitMain_user_naf(String main_user_naf);
			void visitName(String name);
			void visitProvince(String provinces);
			void visitIpf(String ipf);
			void visitNaf(String naf);
			void visitSituation(String situation);
			void visitSituation_date(Date situation_date);
			void visitTelephone(String telephone);
			void visitFax(String fax);
			void visitMobile(String mobile);
			void visitMail(String mail);
		}
				
		
		public static class AbstractVisitor implements Visitor {
			@Override
			public void visitAuthoritation(String authoritation) {}
			@Override
			public void visitAuthoritation_entity(String authoritation_entity) {}
			@Override
			public void visitMain_user_name(String main_user_name) {}
			@Override
			public void visitMain_user_ipf(String main_user_ipf) {}
			@Override
			public void visitMain_user_naf(String main_user_naf) {}
			@Override
			public void visitName(String name) {}
			@Override
			public void visitProvince(String provinces) {}
			@Override
			public void visitIpf(String ipf) {}
			@Override
			public void visitNaf(String naf) {}
			@Override
			public void visitSituation(String situation) {}
			@Override
			public void visitSituation_date(Date situation_date) {}
			@Override
			public void visitTelephone(String telephone) {}
			@Override
			public void visitFax(String fax) {}
			@Override
			public void visitMobile(String mobile) {}
			@Override
			public void visitMail(String mail) {}
		}
		
		
	}
		
	
}
