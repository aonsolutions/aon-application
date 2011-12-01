package com.code.aon.document.dao;

import static com.code.aon.document.IAlfrescoConstants.NAME_SHORT;
import static com.code.aon.document.IAlfrescoConstants.PATH_LONG;
import static com.code.aon.document.IAlfrescoConstants.TYPE_CATEGORY;
import static com.code.aon.document.IAlfrescoConstants.UUID_LONG;
import static org.alfresco.webservice.util.Constants.PROP_DESCRIPTION;
import static org.alfresco.webservice.util.Constants.PROP_NAME;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.ResultSetRow;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.document.AlfrescoCategory;
import com.code.aon.document.EnterpriseDocument;
import com.code.aon.ql.Criteria;

/**
 * The Class LdapDAO.
 */
public class AlfrescoCategoryDAO extends AlfrescoDAO  {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoCategoryDAO.class);
	
	public static final AlfrescoCategory EMPTY_CATEGORY = new AlfrescoCategory();
	
    private final String CATEGORY = "category"; // the propertyname of subcategories
    private final String CATEGORY_Q = Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, CATEGORY);
    private final String SUBCATEGORIES = "subcategories"; // the propertyname of subcategories
    private final String SUBCATEGORIES_Q = Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, SUBCATEGORIES);
	
	public AlfrescoCategoryDAO( String user, String password ) {
		super( AlfrescoCategory.class, user, password ); 
		setPath(AlfrescoCategory.AON_CLASIFICATION);
	}
	
	public ParentReference getReferenceToParent(Reference  ref) {
		ParentReference parent = new ParentReference(STORE,
				ref.getUuid(), ref.getPath(), SUBCATEGORIES_Q, null );
		return parent;
	}

	private AlfrescoCategory newAlfrescoCategory() {
		return new AlfrescoCategory();
	}	
	
	@Override
	public ITransferObject newTo() throws DAOException {
		return newAlfrescoCategory();
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
		}
		return null;
	}
	
	@Override
	protected String getType() {
		return CATEGORY_Q;
	}

	@Override
	protected String getQueryPath() {
		return getQueryPath( getParentReference().getPath() );
	}

	private String getQueryPath( String path ) {
		return "TYPE:\"" + TYPE_CATEGORY + "\" AND PATH:\"" + path + "/*\"";
	}
	
	@SuppressWarnings("unchecked")
	private List<AlfrescoCategory> getSubCategories( Reference reference ) throws DAOException {
		String path = AlfrescoCategory.getSearhPath(reference);
		String expression = getQueryPath(path);
		LOGGER.debug( "getList, expression={}", expression );
		try {
			List<ResultSetRow> list = getList(expression);
			if (! list.isEmpty() ) {
				Criteria criteria = new Criteria();
				criteria.addOrder(getFieldName(NAME_SHORT));
				list = sortList(list, criteria);
				return (List) convertList(list);	
			}		
		} catch ( Throwable e ) {
			throw new DAOException( "Error in getSubcategories of " + reference, e );
		}
		return Collections.emptyList();
	}
	
	@Override
	protected ITransferObject convert( NamedValue[] values ) throws DAOException {
		AlfrescoCategory ac = newAlfrescoCategory();
		Reference reference = new Reference();
		reference.setStore(STORE);
		ac.setId(reference);
		for( NamedValue nv : values ) {
			String name = nv.getName();
			if ( PROP_DESCRIPTION.equals(name) ) {
				ac.setDescription(nv.getValue());
			} else if ( PROP_NAME.equals(name) ) {
				ac.setName(nv.getValue());
			} else if ( UUID_LONG.equals(name) ) {
				reference.setUuid(nv.getValue());
			} else if ( PATH_LONG.equals(name) ) {
				reference.setPath(nv.getValue());
			}
		}		
		ac.setCategories(getSubCategories(reference));
		return ac;
	}

	@Override
	protected NamedValue[] updateValues(ITransferObject to) {
		NamedValue[] values = new NamedValue[2];
		AlfrescoCategory ac = (AlfrescoCategory) to;
		values[0] = Utils.createNamedValue(PROP_DESCRIPTION, ac.getDescription());
		values[1] = Utils.createNamedValue(PROP_NAME, ac.getName());
		return values;
	}
	
	public static void main(String[] args) throws DAOException {
		AlfrescoCategoryDAO bc = new AlfrescoCategoryDAO("admin", "admin");
		Criteria criteria = new Criteria();
		List<ITransferObject> tos = bc.getList(criteria);
		for( ITransferObject to : tos ) {
			LOGGER.info( "{}", to );
		}
	}
	
}
