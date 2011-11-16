package com.code.aon.document.dao;

import static com.code.aon.document.EnterpriseDocumentAspect.ENTERPRISE_ID;
import static com.code.aon.document.EnterpriseDocumentAspect.ENTERPRISE_ID_LONG;
import static org.alfresco.webservice.util.Constants.NAMESPACE_CONTENT_MODEL;
import static org.alfresco.webservice.util.Constants.PROP_CREATED;
import static org.alfresco.webservice.util.Constants.PROP_DESCRIPTION;
import static org.alfresco.webservice.util.Constants.PROP_NAME;
import static org.alfresco.webservice.util.Constants.PROP_TITLE;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Date;

import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.webservice.classification.AppliedCategory;
import org.alfresco.webservice.classification.ClassificationFault;
import org.alfresco.webservice.content.ContentFault;
import org.alfresco.webservice.types.CMLAddAspect;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.Utils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.MimeResolver;
import com.code.aon.company.Enterprise;
import com.code.aon.document.AlfrescoCategory;
import com.code.aon.document.EnterpriseDocument;
import com.code.aon.document.EnterpriseDocumentAspect;

/**
 * The Class LdapDAO.
 */
public class EnterpriseDocumentDAO extends AlfrescoDAO  {
	
    private static final String DEFAULT_PATH = "/app:company_home/cm:AON";
	
	private AlfrescoDAO categoryDAO;

	public EnterpriseDocumentDAO( String user, String password, AlfrescoDAO categoryDAO ) {
		super( EnterpriseDocument.class, user, password );		
		this.categoryDAO = categoryDAO;
		setPath(DEFAULT_PATH);
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
	
	private AlfrescoCategory[] getCategories( String[] values ) throws DAOException {
		AlfrescoCategory[] categories = null;
		if (! ArrayUtils.isEmpty(values)) {
			categories = new AlfrescoCategory[values.length];	
			for( int i = 0; i < values.length; i++ ) {
				String uuid = StringUtils.substringAfterLast(values[i], "/");
				categories[i] = (AlfrescoCategory) categoryDAO.get(uuid);
			}
		}
		return categories;
	}
		
	@Override
	protected ITransferObject convert( NamedValue[] values ) throws DAOException {
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
				MimeType type = MimeResolver.getMimeTypeByExtension(ed.getName());
				ed.setMimeType(type);
			} else if ( PROP_TITLE.equals(name) ) {
				ed.setTitle(nv.getValue());
			} else if ( UUID_LONG.equals(name) ) {
				reference.setUuid(nv.getValue());
			} else if ( PATH_LONG.equals(name) ) {
				reference.setPath(nv.getValue());
			} else if ( PROP_CREATED.equals(name) ) {
				Date date = ISO8601DateFormat.parse(nv.getValue());
				ed.setCreated(date);
			} else if ( ENTERPRISE_ID_LONG.equals(name) ) {
				Integer id = NumberUtils.toInt(nv.getValue());
				IManagerBean bean;
				try {
					bean = BeanManager.getManagerBean(Enterprise.class);
					Enterprise enterprise = (Enterprise) bean.get(id);
					ed.setEnterprise(enterprise);
				} catch (ManagerBeanException e) {
					throw new DAOException(e);
				}
			} else if ( CATEGORIES_LONG.equals(name) ) {
				ed.setCategories(getCategories(nv.getValues()));
			}
		}		
		return ed;
	}

	@Override
	protected NamedValue[] updateValues(ITransferObject to) {
		NamedValue[] values = new NamedValue[3];
		EnterpriseDocument ed = (EnterpriseDocument) to;
		values[0] = Utils.createNamedValue(PROP_DESCRIPTION, ed.getDescription());
		values[1] = Utils.createNamedValue(PROP_NAME, ed.getName());
		values[2] = Utils.createNamedValue(PROP_TITLE, ed.getTitle());
		return values;
	}

	@Override
	protected CMLAddAspect[] getAddAspects(ITransferObject to) {
		EnterpriseDocument ed = (EnterpriseDocument) to;
		
		NamedValue[] values = new NamedValue[] {
				Utils.createNamedValue(Constants.PROP_TITLE, ed.getTitle()) 
		};
		CMLAddAspect title = new CMLAddAspect(Constants.ASPECT_TITLED, values, null, "1");
		
		EnterpriseDocumentAspect eda = new EnterpriseDocumentAspect(ed.getEnterprise().getId());
		CMLAddAspect aspect = eda.getAspect(getParentReference());
		return new CMLAddAspect[]{ title, aspect };
	}

	@Override
	protected void afterInsert(ITransferObject to) throws ClassificationFault, RemoteException {
		insertContent( (EnterpriseDocument) to );
		updateCategories( (EnterpriseDocument) to );
	}
	
	@Override
	protected void afterUpdate(ITransferObject to) throws Exception {
		updateCategories( (EnterpriseDocument) to );
	}
	
	private void insertContent( EnterpriseDocument ed ) throws ContentFault, RemoteException {
		ContentFormat contentFormat = new ContentFormat(ed.getMimeType().getName(), "UTF-8");
		getContentService().write(ed.getId(), Constants.PROP_CONTENT, ed.getData(), contentFormat);		
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
