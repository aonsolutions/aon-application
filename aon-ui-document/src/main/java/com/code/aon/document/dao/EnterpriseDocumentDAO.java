package com.code.aon.document.dao;

import static com.code.aon.document.EnterpriseDocumentAspect.ENTERPRISE_ID;
import static org.alfresco.webservice.util.Constants.NAMESPACE_CONTENT_MODEL;
import static org.alfresco.webservice.util.Constants.PROP_CREATED;
import static org.alfresco.webservice.util.Constants.PROP_DESCRIPTION;
import static org.alfresco.webservice.util.Constants.PROP_NAME;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Date;

import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.webservice.classification.AppliedCategory;
import org.alfresco.webservice.classification.ClassificationFault;
import org.alfresco.webservice.types.CMLAddAspect;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.Utils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.document.AlfrescoCategory;
import com.code.aon.document.AlfrescoCategoryManager;
import com.code.aon.document.EnterpriseDocument;
import com.code.aon.document.EnterpriseDocumentAspect;
import com.code.aon.document.IAlfrescoDocument;

/**
 * The Class LdapDAO.
 */
public class EnterpriseDocumentDAO extends AlfrescoDAO  {
	
	private AlfrescoCategoryManager categoryManager;

	public EnterpriseDocumentDAO( String user, String password, AlfrescoCategoryManager categoryManager ) {
		super( EnterpriseDocument.class, user, password ); 
		this.categoryManager = categoryManager;
	}

	private EnterpriseDocument newEnterpriseDocument() {
		EnterpriseDocument ed = new EnterpriseDocument();
		ed.setDao(this);
		return ed;
	}	
	
	@Override
	public ITransferObject newTo() throws DAOException {
		return newEnterpriseDocument();
	}

	@Override
	public Serializable getId(ITransferObject to) throws DAOException {
		EnterpriseDocument ed = (EnterpriseDocument) to;
		return ed.getId();
	}

	@Override
	public Object getValue( NamedValue nv ) {
		String name = nv.getName();
		if ( PROP_NAME.equals(name) ) {
			return nv.getValue();
		} else if ( PROP_DESCRIPTION.equals(name) ) {
			return nv.getValue();
		} else if ( PROP_CREATED.equals(name) ) {
			return ISO8601DateFormat.parse(nv.getValue());
		} else if ( ENTERPRISE_ID.equals(name) ) {
			return NumberUtils.toInt(nv.getValue());
		}
		return null;
	}
	
	private AlfrescoCategory[] getCategories( String[] values ) {
		AlfrescoCategory[] categories = null;
		if (! ArrayUtils.isEmpty(values)) {
			categories = new AlfrescoCategory[values.length];	
			for( int i = 0; i < values.length; i++ ) {
				String uuid = StringUtils.substringAfterLast(values[i], "/");
				categories[i] = categoryManager.getCategoryByUuid(uuid);
			}
		}
		return categories;
	}
		
	@Override
	protected ITransferObject convert( NamedValue[] values ) {
		EnterpriseDocument ed = newEnterpriseDocument();
		Reference reference = new Reference();
		reference.setStore(STORE);
		ed.setId(reference);
		for( NamedValue nv : values ) {
			String name = nv.getName();
			if ( PROP_DESCRIPTION.equals(name) ) {
				ed.setDescription(nv.getValue());
			} else if ( PROP_NAME.equals(name) ) {
				ed.setName(nv.getValue());
			} else if ( UUID.equals(name) ) {
				reference.setUuid(nv.getValue());
			} else if ( PATH.equals(name) ) {
				reference.setPath(nv.getValue());
			} else if ( PROP_CREATED.equals(name) ) {
				Date date = ISO8601DateFormat.parse(nv.getValue());
				ed.setCreated(date);
			} else if ( ENTERPRISE_ID.equals(name) ) {
				Integer id = NumberUtils.toInt(nv.getValue());
				ed.setEnterpriseId(id);
			} else if ( CATEGORIES.equals(name) ) {
				ed.setCategories(getCategories(nv.getValues()));
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

	@Override
	protected void afterInsert(IAlfrescoDocument ad) throws ClassificationFault, RemoteException {
		updateCategories( (EnterpriseDocument) ad );
	}
	
	@Override
	protected void afterUpdate(IAlfrescoDocument ad) throws Exception {
		updateCategories( (EnterpriseDocument) ad );
	}

	private void updateCategories( EnterpriseDocument ed ) throws ClassificationFault, RemoteException {
		AlfrescoCategory[] list = ed.getCategories();
		if (! ArrayUtils.isEmpty(list) ) {
			Predicate predicate = getPredicate(ed);
			Reference[] categories = new Reference[list.length];
			for( int i = 0; i < list.length; i++ ) {
				categories[i] = list[i].getId(); 
			}
			String classification = Constants.createQNameString(NAMESPACE_CONTENT_MODEL, "generalclassifiable");
			AppliedCategory ac = new AppliedCategory(classification, categories);
			getClassificationService().setCategories(predicate, new AppliedCategory[]{ac});
		}
	}
	
}
