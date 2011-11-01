package com.code.aon.ui.document.controller;

import static com.code.aon.ui.document.controller.EnterpriseDocumentAspect.ENTERPRISE_ID;
import static org.alfresco.webservice.util.Constants.PROP_DESCRIPTION;
import static org.alfresco.webservice.util.Constants.PROP_NAME;

import java.io.Serializable;
import java.util.Date;

import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.webservice.types.CMLAddAspect;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.Utils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ui.document.AlfrescoDAO;
import com.code.aon.ui.document.EnterpriseDocument;

/**
 * The Class LdapDAO.
 */
public class EnterpriseDocumentDAO extends AlfrescoDAO  {

	/**
	 * Obtain a suitable <code>Logger</code>.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseDocumentDAO.class);
	
	public EnterpriseDocumentDAO( String user, String password ) {
		super( EnterpriseDocument.class, user, password ); 
	}

	@Override
	protected ParentReference getParentReference() {
		return getReferenceToParent(getCompanyHome());
	}

	@Override
	public Serializable getId(ITransferObject to) throws DAOException {
		EnterpriseDocument ed = (EnterpriseDocument) to;
		return ed.getId();
	}

	@Override
	protected ITransferObject convert( NamedValue[] values ) {
		EnterpriseDocument ed = new EnterpriseDocument();
		for( NamedValue nv : values ) {
			String name = nv.getName();
			if ( Constants.PROP_DESCRIPTION.equals(name) ) {
				ed.setDescription(nv.getValue());
			} else if ( Constants.PROP_NAME.equals(name) ) {
				ed.setName(nv.getValue());
			} else if ( UUID.equals(name) ) {
				ed.setPath(nv.getValue());
			} else if ( PATH.equals(name) ) {
				ed.setPath(nv.getValue());
			} else if ( Constants.PROP_CREATED.equals(name) ) {
				Date date = ISO8601DateFormat.parse(nv.getValue());
				ed.setCreated(date);
			} else if ( ENTERPRISE_ID.equals(name) ) {
				Integer id = NumberUtils.toInt(nv.getValue());
				ed.setEnterpriseId(id);
			}
		}		
		return ed;
	}

	@Override
	protected NamedValue[] insertValues(ITransferObject to) {
		NamedValue[] values = new NamedValue[1];
		EnterpriseDocument ed = (EnterpriseDocument) to;
		values[0] = Utils.createNamedValue(PROP_DESCRIPTION, ed.getDescription());
		return values;
	}

	@Override
	protected NamedValue[] updateValues(ITransferObject to) {
		NamedValue[] values = new NamedValue[2];
		EnterpriseDocument ed = (EnterpriseDocument) to;
		values[0] = Utils.createNamedValue(PROP_DESCRIPTION, ed.getDescription());
		values[1] = Utils.createNamedValue(PROP_NAME, ed.getName());
		return values;
	}

	@Override
	protected CMLAddAspect getAddAspect(ITransferObject to) {
		EnterpriseDocument ed = (EnterpriseDocument) to;
		EnterpriseDocumentAspect eda = new EnterpriseDocumentAspect(ed.getEnterpriseId());
		return eda.getAspect(getParentReference());
	}
	
}
