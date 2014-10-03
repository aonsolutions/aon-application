package com.code.aon.ui.config.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.lob.SerializableBlob;

import com.code.aon.AonVersion;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.Domain;
import com.code.aon.ui.util.AonUtil;

public class ParentDomainController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean resolved = false;
	private Integer domain;
	private String name;
	private String phone;
	private String email;
	private MimeType mimeType;

	public void resolve() {
		DomainSwitcher dm = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		domain = dm.getDomainId();
		
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(Domain.class.getName());
		String q = "SELECT d.parent FROM domain d WHERE d.id = ?";
		SQLQuery query = HibernateUtil.getSession(sessionFactoryName).createSQLQuery(q);
		query.setInteger(0, domain);
		query.addScalar("parent", Hibernate.INTEGER);
		setDomain( (Integer) query.uniqueResult() );
		HibernateUtil.closeSession(sessionFactoryName, false);

		sessionFactoryName = HibernateUtil.getSessionFactoryName(Domain.class.getName());
		Session session = HibernateUtil.getSession(sessionFactoryName);
		q = "SELECT r.name FROM company c, registry r"
				+ " WHERE c.domain = ? AND r.domain = ?"
				+ " AND c.registry = r.id LIMIT 1";
		query = session.createSQLQuery(q);
		query.setInteger(0, domain).setInteger(1, domain);
		query.addScalar("name", Hibernate.STRING);
		setName( (String) query.uniqueResult() );
		
		q = "SELECT r.value FROM company c, rmedia r"
				+ " WHERE c.domain = ? AND r.domain = ?"
				+ " AND c.registry = r.registry AND r.media = 1 LIMIT 1";
		query = session.createSQLQuery(q);
		query.setInteger(0, domain).setInteger(1, domain);
		query.addScalar("value", Hibernate.STRING);
		setPhone((String) query.uniqueResult() );

		q = "SELECT r.value FROM company c, rmedia r"
				+ " WHERE c.domain = ? AND r.domain = ?" 
				+ " AND c.registry = r.registry AND r.media = 4 LIMIT 1";
		query = session.createSQLQuery(q);
		query.setInteger(0, domain).setInteger(1, domain);
		query.addScalar("value", Hibernate.STRING);
		setEmail((String) query.uniqueResult() );
		HibernateUtil.closeSession(sessionFactoryName, false);
		resolved = true;
	}
	
	public void createLogoContent(OutputStream out, Object data) throws IOException, SQLException {
		if (resolved) {
			String sessionFactoryName = HibernateUtil.getSessionFactoryName(Domain.class.getName());
			String q = "SELECT r.data,r.mimetype FROM company c, rattach r"
					+ " WHERE c.domain = ? AND r.domain = ?" 
					+ " AND c.registry = r.registry"
					+ " AND r.type = 0";
			SQLQuery query = HibernateUtil.getSession(sessionFactoryName).createSQLQuery(q);
			query.setInteger(0, domain).setInteger(1, domain);
			List<?> queryList = query
					.addScalar("data", Hibernate.BLOB)
					.addScalar("mimetype", Hibernate.INTEGER)
					.list();
			Iterator<?> iterator = queryList.iterator();
			if (iterator.hasNext()) {
				Object[] tuple = (Object[]) iterator.next();
				SerializableBlob blob = (SerializableBlob) tuple[0];
				Integer mime = (Integer) tuple[1];
				setMimeType(mime!=null?MimeType.values()[mime]:null);
				if (blob != null) {
					IOUtils.copy(blob.getBinaryStream(), out);	
				}
			}
			HibernateUtil.closeSession(sessionFactoryName, false);
		}
	}
	
	

	public Integer getDomain() {
		if (!resolved) {
			resolve();
		}
		return domain;
	}

	public void setDomain(Integer domain) {
		this.domain = domain;
	}

	public String getName() {
		if (!resolved) {
			resolve();
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		if (!resolved) {
			resolve();
		}
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		if (!resolved) {
			resolve();
		}
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public MimeType getMimeType() {
		return mimeType;
	}
	public String getMimeTypeName() {
		return mimeType==null?null:getMimeType().getName();
	}

	public void setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
	}
	
	
}



