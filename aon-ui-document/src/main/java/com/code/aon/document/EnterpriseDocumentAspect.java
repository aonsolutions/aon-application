package com.code.aon.document;

import static com.code.aon.document.IAlfrescoConstants.ASPECT_NAME;
import static com.code.aon.document.IAlfrescoConstants.ENTERPRISE_ID_LONG;
import static com.code.aon.document.IAlfrescoConstants.PROJECT_ID_LONG;
import static com.code.aon.document.IAlfrescoConstants.REFERENCE_DATE_LONG;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.webservice.types.CMLAddAspect;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.Utils;

public class EnterpriseDocumentAspect {
	
	private Integer enterpriseId;

	private Integer projectId;
	
	private Date referenceDate;
	
	public EnterpriseDocumentAspect(EnterpriseDocument ed) {
		this.enterpriseId = ed.getEnterprise().getId();
		if ( ed.getProject() != null ) {
			this.projectId = ed.getProject().getId();
		}
		this.referenceDate = ed.getReferenceDate();
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Date getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(Date referenceDate) {
		this.referenceDate = referenceDate;
	}

	public Integer getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Integer enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	
	public CMLAddAspect getAspect( ParentReference parent ) {
		CMLAddAspect aspect = new CMLAddAspect();
		aspect.setAspect(ASPECT_NAME);
		List<NamedValue> list = new LinkedList<NamedValue>();
		list.add( Utils.createNamedValue(ENTERPRISE_ID_LONG, enterpriseId.toString()) );
		if ( projectId != null ) {
			list.add( Utils.createNamedValue(PROJECT_ID_LONG, projectId.toString()) );
		}
		if ( referenceDate != null ) {
			String date = ISO8601DateFormat.format(referenceDate);
			list.add( Utils.createNamedValue(REFERENCE_DATE_LONG, date) );
		}
		aspect.setProperty(list.toArray(new NamedValue[list.size()]));
		aspect.setWhere(new Predicate(new Reference[] { parent }, null, null));
		aspect.setWhere_id("1");		
		return aspect;
	}
	
}
