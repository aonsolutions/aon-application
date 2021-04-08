package solutions.aon.seg.social.object;

import java.util.Date;

import solutions.aon.seg.social.object.SecondaryUser.SecondaryUserBuilder.Visitor;

public class SecondaryUser {

	private String authoritation;
	private String authoritationEntity;
	private String mainUserName;
	private String mainUserIpf;
	private String mainUserNaf;

	private String name;
	private String province;
	private String ipf;
	private String naf;
	private String situation;
	private Date situationDate;
	private String telephone;
	private String fax;
	private String mobile;
	private String mail;

	private SecondaryUser() {
	}

	public void setAuthoritation(String authoritation) {
		this.authoritation = authoritation;
	}

	public void setAuthoritationEntity(String authoritationEntity) {
		this.authoritationEntity = authoritationEntity;
	}

	public void setMainUserName(String mainUserName) {
		this.mainUserName = mainUserName;
	}

	public void setMainUserIpf(String mainUserIpf) {
		this.mainUserIpf = mainUserIpf;
	}

	public void setMainUserNaf(String mainUserNaf) {
		this.mainUserNaf = mainUserNaf;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public void setIpf(String ipf) {
		this.ipf = ipf;
	}

	public void setNaf(String naf) {
		this.naf = naf;
	}

	public void setSituation(String situation) {
		this.situation = situation;
	}

	public void setSituationDate(Date situationDate) {
		this.situationDate = situationDate;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getAuthoritation() {
		return this.authoritation;
	}

	public String getAuthoritationEntity() {
		return this.authoritationEntity;
	}

	public String getMainUserName() {
		return this.mainUserName;
	}

	public String getMainUserIpf() {
		return this.mainUserIpf;
	}

	public String getMainUserNaf() {
		return this.mainUserNaf;
	}

	public String getName() {
		return this.name;
	}

	public String getProvince() {
		return this.province;
	}

	public String getIpf() {
		return this.ipf;
	}

	public String getNaf() {
		return this.naf;
	}

	public String getSituation() {
		return this.situation;
	}

	public Date getSituationDate() {
		return this.situationDate;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public String getFax() {
		return this.fax;
	}

	public String getMobile() {
		return this.mobile;
	}

	public String getMail() {
		return this.mail;
	}

	public void accept(Visitor visitor) {
		if (authoritation != null)
			visitor.visitAuthoritation(authoritation);
		if (authoritationEntity != null)
			visitor.visitAuthoritationEntity(authoritationEntity);
		if (mainUserName != null)
			visitor.visitMainUserName(mainUserName);
		if (mainUserIpf != null)
			visitor.visitMainUserIpf(mainUserIpf);
		if (mainUserNaf != null)
			visitor.visitMainUserNaf(mainUserNaf);
		if (name != null)
			visitor.visitName(name);
		if (province != null)
			visitor.visitProvince(province);
		if (ipf != null)
			visitor.visitIpf(ipf);
		if (naf != null)
			visitor.visitNaf(naf);
		if (situation != null)
			visitor.visitSituation(situation);
		if (situationDate != null)
			visitor.visitSituationDate(situationDate);
		if (telephone != null)
			visitor.visitTelephone(telephone);
		if (fax != null)
			visitor.visitFax(fax);
		if (mobile != null)
			visitor.visitMobile(mobile);
		if (mail != null)
			visitor.visitMail(mail);
	}

	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append('{');

		accept(new Visitor() {

			@Override
			public void visitTelephone(String telephone) {
				stringBuffer.append(String.format(" telephone : \"%s\" ", telephone));
			}

			@Override
			public void visitSituationDate(Date situationDate) {
				stringBuffer.append(String.format(" situation date : \"%s\" ", situationDate));
			}

			@Override
			public void visitSituation(String situation) {
				stringBuffer.append(String.format(" situation : \"%s\" ", situation));
			}

			@Override
			public void visitProvince(String provinces) {
				stringBuffer.append(String.format(" provinces : \"%s\" ", provinces));
			}

			@Override
			public void visitName(String name) {
				stringBuffer.append(String.format(" name : \"%s\" ", name));
			}

			@Override
			public void visitNaf(String naf) {
				stringBuffer.append(String.format(" naf : \"%s\" ", naf));
			}

			@Override
			public void visitMobile(String mobile) {
				stringBuffer.append(String.format(" mobile : \"%s\" ", mobile));
			}

			@Override
			public void visitMainUserName(String mainUuserName) {
				stringBuffer.append(String.format(" main user name : \"%s\" ", mainUuserName));
			}

			@Override
			public void visitMainUserNaf(String mainUserNaf) {
				stringBuffer.append(String.format(" main user naf : \"%s\" ", mainUserNaf));
			}

			@Override
			public void visitMainUserIpf(String mainUserIpf) {
				stringBuffer.append(String.format(" main user ipf : \"%s\" ", mainUserIpf));
			}

			@Override
			public void visitMail(String mail) {
				stringBuffer.append(String.format(" mail : \"%s\" ", mail));
			}

			@Override
			public void visitIpf(String ipf) {
				stringBuffer.append(String.format(" ipf : \"%s\" ", ipf));
			}

			@Override
			public void visitFax(String fax) {
				stringBuffer.append(String.format(" fax : \"%s\" ", fax));
			}

			@Override
			public void visitAuthoritationEntity(String authoritationEntity) {
				stringBuffer.append(String.format(" authoritation entity : \"%s\" ", authoritationEntity));
			}

			@Override
			public void visitAuthoritation(String authoritation) {
				stringBuffer.append(String.format(" authoritation : \"%s\" ", authoritation));
			}
		});

		stringBuffer.append('}');
		return stringBuffer.toString();
	}

	public static class SecondaryUserBuilder {

		private String authoritation;
		private String authoritationEntity;
		private String mainUserName;
		private String mainUserIpf;
		private String mainUserNaf;

		private String name;
		private String province;
		private String ipf;
		private String naf;
		private String situation;
		private Date situationDate;
		private String telephone;
		private String fax;
		private String mobile;
		private String mail;

		public SecondaryUserBuilder setAuthoritation(String authoritation) {
			if (authoritation != null && !authoritation.trim().equals(""))
				this.authoritation = authoritation.trim();
			else
				this.authoritation = null;
			return this;
		}

		public SecondaryUserBuilder setAuthoritationEntity(String authoritationEntity) {
			if (authoritationEntity != null && !authoritationEntity.trim().equals(""))
				this.authoritationEntity = authoritationEntity.trim();
			else
				this.authoritationEntity = null;
			return this;
		}

		public SecondaryUserBuilder setMainUserName(String mainUserName) {
			if (mainUserName != null && !mainUserName.trim().equals(""))
				this.mainUserName = mainUserName.trim();
			else
				this.mainUserName = null;
			return this;
		}

		public SecondaryUserBuilder setMainUserIpf(String mainUserIpf) {
			if (mainUserIpf != null && !mainUserIpf.trim().equals(""))
				this.mainUserIpf = mainUserIpf.trim();
			else
				this.mainUserIpf = null;

			return this;
		}

		public SecondaryUserBuilder setMainUserNaf(String mainUserNaf) {
			if (mainUserNaf != null && !mainUserNaf.trim().equals(""))
				this.mainUserNaf = mainUserNaf.trim();
			else
				this.mainUserNaf = null;
			return this;
		}

		public SecondaryUserBuilder setName(String name) {
			if (name != null && !name.trim().equals(""))
				this.name = name.trim();
			else
				this.name = null;
			return this;
		}

		public SecondaryUserBuilder setProvince(String province) {
			if (province != null && !province.trim().equals(""))
				this.province = province.trim();
			else
				this.province = null;
			return this;
		}

		public SecondaryUserBuilder setIpf(String ipf) {
			if (ipf != null && !ipf.trim().equals(""))
				this.ipf = ipf.trim();
			else
				this.ipf = null;
			return this;
		}

		public SecondaryUserBuilder setNaf(String naf) {
			if (naf != null && !naf.trim().equals(""))
				this.naf = naf.trim();
			else
				this.naf = null;
			return this;
		}

		public SecondaryUserBuilder setSituation(String situation) {
			if (situation != null && !situation.trim().equals(""))
				this.situation = situation.trim();
			else
				this.situation = null;
			return this;
		}

		public SecondaryUserBuilder setSituationDate(Date situationDate) {
			this.situationDate = situationDate;
			return this;
		}

		public SecondaryUserBuilder setTelephone(String telephone) {
			if (telephone != null && !telephone.trim().equals(""))
				this.telephone = telephone.trim();
			else
				this.telephone = null;
			return this;
		}

		public SecondaryUserBuilder setFax(String fax) {
			if (fax != null && !fax.trim().equals(""))
				this.fax = fax.trim();
			else
				this.fax = null;
			return this;
		}

		public SecondaryUserBuilder setMobile(String mobile) {
			if (mobile != null && !mobile.trim().equals(""))
				this.mobile = mobile.trim();
			else
				this.mobile = null;
			return this;
		}

		public SecondaryUserBuilder setMail(String mail) {
			if (mail != null && !mail.trim().equals(""))
				this.mail = mail.trim();
			else
				this.mail = null;
			return this;
		}

		public SecondaryUser build() {
			SecondaryUser user = new SecondaryUser();

			user.authoritation = this.authoritation;
			user.authoritationEntity = this.authoritationEntity;
			user.mainUserName = this.mainUserName;
			user.mainUserIpf = this.mainUserIpf;
			user.mainUserNaf = this.mainUserNaf;
			user.name = this.name;
			user.province = this.province;
			user.ipf = this.ipf;
			user.naf = this.naf;
			user.situation = this.situation;
			user.situationDate = this.situationDate;
			user.telephone = this.telephone;
			user.fax = this.fax;
			user.mobile = this.mobile;
			user.mail = this.mail;

			return user;
		}

		public interface Visitor {
			void visitAuthoritation(String authoritation);

			void visitAuthoritationEntity(String authoritationEntity);

			void visitMainUserName(String mainUserName);

			void visitMainUserIpf(String mainUserIpf);

			void visitMainUserNaf(String mainUserNaf);

			void visitName(String name);

			void visitProvince(String provinces);

			void visitIpf(String ipf);

			void visitNaf(String naf);

			void visitSituation(String situation);

			void visitSituationDate(Date situationDate);

			void visitTelephone(String telephone);

			void visitFax(String fax);

			void visitMobile(String mobile);

			void visitMail(String mail);
		}

		public static class AbstractVisitor implements Visitor {
			@Override
			public void visitAuthoritation(String authoritation) {
			}

			@Override
			public void visitAuthoritationEntity(String authoritationEntity) {
			}

			@Override
			public void visitMainUserName(String mainUserName) {
			}

			@Override
			public void visitMainUserIpf(String mainUserIpf) {
			}

			@Override
			public void visitMainUserNaf(String mainUserNaf) {
			}

			@Override
			public void visitName(String name) {
			}

			@Override
			public void visitProvince(String provinces) {
			}

			@Override
			public void visitIpf(String ipf) {
			}

			@Override
			public void visitNaf(String naf) {
			}

			@Override
			public void visitSituation(String situation) {
			}

			@Override
			public void visitSituationDate(Date situationDate) {
			}

			@Override
			public void visitTelephone(String telephone) {
			}

			@Override
			public void visitFax(String fax) {
			}

			@Override
			public void visitMobile(String mobile) {
			}

			@Override
			public void visitMail(String mail) {
			}
		}

	}

}
