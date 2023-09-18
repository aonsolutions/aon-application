package com.code.aon.tas;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.commercial.Offer;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.project.IProject;
import com.code.aon.ql.Criteria;
import com.code.aon.tas.enumeration.ProjectStatus;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ProjectTasDB;

@Entity
@Table(name="project_tas")
public class ProjectTas extends ProjectTasDB implements IHeaderObject, IProject {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public ProjectTas() {
		setStatus( ProjectStatus.PENDING);
	}

    @Transient
    public String getReferenceCode() {
    	String referenceCode = StringUtils.leftPad(Integer.toString(getNumber()), 6, "0");
		if (!StringUtils.isEmpty(getSeries())) {
			referenceCode = getSeries() + "/" + referenceCode;
		}
    	return referenceCode;
    }

    @Transient
    public Date getDate() {
    	return getProject().getDate();
    }

    @Transient
    public SecurityLevel getSecurityLevel() {
    	return SecurityLevel.OFFICIAL;
    }

    @Override
	public void setSecurityLevel(SecurityLevel securityLevel) {
	}

	@Transient
    public boolean isOfferLinked() throws ManagerBeanException {
    	IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(offerBean.getFieldName(IEntityAlias.OFFER_PROJECT_ID), getProject().getId());
    	return (offerBean.getCount(criteria) > 0);
    }
	
}