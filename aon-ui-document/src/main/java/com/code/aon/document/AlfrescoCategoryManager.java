package com.code.aon.document;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.webservice.classification.ClassificationFault;
import org.alfresco.webservice.types.Category;
import org.alfresco.webservice.types.Classification;
import org.alfresco.webservice.types.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.sql.DAOException;


/**
 * The Class LdapDAO.
 */
public class AlfrescoCategoryManager extends BasicAlfresco  {
	
	private static final String AON_CLASSIFICATION = "AON";

	private static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoCategoryManager.class);
	
	private Map<String, AlfrescoCategory> categoryMap;

	public AlfrescoCategoryManager( String user, String password ) {
		super( user, password ); 
		try {
			loadCategories();
		} catch (DAOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private Classification getClassifcation( String name ) throws ClassificationFault, RemoteException {
		// Get all the classifications
        Classification[] classifications = getClassificationService().getClassifications(STORE); 
        
        // Output some details
        LOGGER.info("All classifications:");
        for (Classification classification : classifications) {
            if ( name.equals(classification.getRootCategory().getTitle()) ) {
            	return classification;
            }
        }
		return null;
	}

	public void loadCategories() throws DAOException {
		this.categoryMap = new HashMap<String,AlfrescoCategory>();
		try {
			startSession();
            Classification classification = getClassifcation(AON_CLASSIFICATION);
            if ( classification != null ) {
            	Reference reference = classification.getRootCategory().getId();
                Category[] categories = getClassificationService().getChildCategories(reference);
                for (Category category : categories) {
                	AlfrescoCategory ac = new AlfrescoCategory(classification);
                	ac.setId(category.getId());
                	ac.setName(category.getTitle());
                	ac.setDescription(category.getDescription());
                	this.categoryMap.put(ac.getName(), ac);
                }            	
            }
		} catch ( Throwable e ) {
			throw new DAOException( "Error loading categories", e );
		} finally {
			endSession();
		}			
	}

	public Collection<AlfrescoCategory> getCategories() {
		return this.categoryMap.values();
	}
	
	public AlfrescoCategory getCategory( String name )  {
		return this.categoryMap.get(name);
	}

	public AlfrescoCategory getCategoryByUuid( String uuid )  {
		for( AlfrescoCategory category : getCategories() ) {
			if ( category.getId().getUuid().equals(uuid) ) {
				return category;
			}
		}
		return null;
	}
	
}