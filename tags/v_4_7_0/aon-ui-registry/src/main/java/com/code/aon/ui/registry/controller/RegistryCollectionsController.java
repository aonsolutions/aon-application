package com.code.aon.ui.registry.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.registry.Category;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddInfo;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.Relationship;
import com.code.aon.registry.Segment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.NoteType;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.registry.enumeration.StreetType;

/**
 * Controller used to get Collections related with clasess in <code>com.code.aon.registry</code>.
 * 
 */
public class RegistryCollectionsController {

	private List<SelectItem> addressTypes;
	
	private List<SelectItem> streetTypes;
	
	private List<SelectItem> registryTypes;
	
	private List<SelectItem> genders;
	
	private List<SelectItem> maritalStatuses;
	
	private List<SelectItem> registryAttachmentTypes;
	
	private List<SelectItem> noteTypes;
	
	private RegistryBank rBank; // No Borrar. Euke.
								// Se utiliza como selector 
								// en la pantalla de alta de vencimientos.
	
	/**
     * Gets the address types.
     * 
     * @return the address types
     */
    public List<SelectItem> getAddressTypes() {
    	if ( addressTypes == null ) {
    		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
    		addressTypes = new LinkedList<SelectItem>();
    		for( AddressType type : AddressType.values() ) {
	            String name = type.getName(locale); 
	            SelectItem item = new SelectItem(type, name);
	            addressTypes.add( item );
    		}
        }
        return addressTypes;
    }

    /**
     * Gets the street types.
     * 
     * @return the street types
     */
    public List<SelectItem> getStreetTypes() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    LinkedList<SelectItem> types = new LinkedList<SelectItem>();
        for( StreetType type : StreetType.values() ) {
            String name = type.getName(locale); 
            SelectItem item = new SelectItem(type, name);
            types.add( item );
        }
        return types;
    }

    /**
     * Gets the media types.
     * 
     * @return the media types
     */
    @SuppressWarnings("unchecked")
    public List getMediaTypes() {
    	if ( streetTypes == null ) {
	        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	        streetTypes = new LinkedList<SelectItem>();
	        for( MediaType type : MediaType.values() ) {
	            String name = type.getName(locale); 
	            SelectItem item = new SelectItem(type, name);
	            streetTypes.add( item );
	        }
    	}
        return streetTypes;
    }
    
    /**
     * Gets the registry types.
     * 
     * @return the registry types
     */
    public List<SelectItem> getRegistryTypes() {
    	if ( registryTypes == null) {
	        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	        registryTypes = new LinkedList<SelectItem>();
	        for( RegistryType type : RegistryType.values() ) {
	            String name = type.getName(locale); 
	            SelectItem item = new SelectItem(type, name);
	            registryTypes.add( item );
	        }
    	}
        return registryTypes;
    }
    
    /**
     * Gets the Relationships.
     * 
     * @return the Relationships
     * @throws ManagerBeanException 
     */
    @SuppressWarnings("unchecked")
    public List<SelectItem> getRelationships() throws ManagerBeanException{
    	List<SelectItem> relationships = new LinkedList<SelectItem>();
    	IManagerBean relationshipBean = BeanManager.getManagerBean(Relationship.class);
    	Criteria criteria = new Criteria();
    	criteria.addOrder(relationshipBean.getFieldName(IRegistryAlias.RELATIONSHIP_DESCRIPTION));
    	Iterator iter = relationshipBean.getList(criteria).iterator();
    	while(iter.hasNext()){
    		Relationship relationship = (Relationship)iter.next();
    		SelectItem item = new SelectItem(relationship.getId(), relationship.getDescription());
    		relationships.add(item);
    	}
    	return relationships;
    }
    
    public List<SelectItem> getGenders() {
    	if ( genders == null ) {
	        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	        genders = new LinkedList<SelectItem>();
	        for( Gender gender : Gender.values() ) {
	            String name = gender.getName(locale); 
	            SelectItem item = new SelectItem(gender, name);
	            genders.add( item );
	        }
    	}
        return genders;
    }
    
    public List<SelectItem> getMaritalStatuses() {
    	if ( maritalStatuses == null ) {
	    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		    maritalStatuses = new LinkedList<SelectItem>();
	        for( MaritalStatus status : MaritalStatus.values() ) {
	            String name = status.getName(locale); 
	            SelectItem item = new SelectItem(status, name);
	            maritalStatuses.add( item );
	        }
    	}
        return maritalStatuses;
    }
    
    public List<SelectItem> getRegistryAttachmentTypes() {
    	if ( registryAttachmentTypes == null ) {
	    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    	registryAttachmentTypes = new LinkedList<SelectItem>();
	        for( RegistryAttachmentType status : RegistryAttachmentType.values() ) {
	            String name = status.getName(locale); 
	            SelectItem item = new SelectItem(status, name);
	            registryAttachmentTypes.add( item );
	        }
    	}
        return registryAttachmentTypes;
    }
    
	public List<SelectItem> getNoteTypes() {
		if ( noteTypes == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			noteTypes = new LinkedList<SelectItem>();
			for( NoteType type : NoteType.values() ) {
				if (type.compareTo(NoteType.OBSERVATION)!=0){
					String name = type.getName(locale);
					SelectItem item = new SelectItem(type, name);
					noteTypes.add(item);
				}
			}
		}
		return noteTypes;
	}
	
    @SuppressWarnings("unchecked")
    public List<SelectItem> getSegments() throws ManagerBeanException{
    	List<SelectItem> segments = new LinkedList<SelectItem>();
    	IManagerBean segmentBean = BeanManager.getManagerBean(Segment.class);
    	Criteria criteria = new Criteria();
    	criteria.addOrder(segmentBean.getFieldName(IRegistryAlias.SEGMENT_NAME));
    	Iterator iter = segmentBean.getList(criteria).iterator();
    	while(iter.hasNext()){
    		Segment segment = (Segment)iter.next();
    		SelectItem item = new SelectItem(segment.getId(), segment.getName());
    		segments.add(item);
    	}
    	return segments;
    }
    
	@SuppressWarnings("unchecked")
	public List<SelectItem> getUsers() throws ManagerBeanException {
		List<SelectItem> users = new LinkedList<SelectItem>();
		IManagerBean userBean = BeanManager.getManagerBean(User.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(userBean.getFieldName(IConfigAlias.USER_NAME));
		Iterator iter = userBean.getList(criteria).iterator();
		while(iter.hasNext()){
			User user = (User)iter.next();
			SelectItem item = new SelectItem(user.getId(), user.getName());
			users.add(item);
		}
		return users;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getRegistryBanks(Registry registry) throws ManagerBeanException {
		List<SelectItem> rBanks = new LinkedList<SelectItem>();
		IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_REGISTRY_ID), registry.getId());
		Iterator iter = rBankBean.getList(criteria).iterator();
		while(iter.hasNext()){
			RegistryBank rBank = (RegistryBank)iter.next();
			SelectItem item = new SelectItem(rBank, rBank.getFullName());
			rBanks.add(item);
		}
		return rBanks;
	}

	public RegistryBank getRegistryBank() {
		return rBank;
	}

	public void setRegistryBank(RegistryBank bank) {
		// void
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getCategories() throws ManagerBeanException {
		List<SelectItem> users = new LinkedList<SelectItem>();
		IManagerBean categoryBean = BeanManager.getManagerBean(Category.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(categoryBean.getFieldName(IRegistryAlias.CATEGORY_NAME));
		Iterator iter = categoryBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Category category = (Category) iter.next();
			SelectItem item = new SelectItem(category, category.getName());
			users.add(item);
		}
		return users;
	}
	
	public Category getCategory() {
		return null;
	}

	public void setCategory( Category category ) {
	}	

    @SuppressWarnings("unchecked")
    public List<String> getAddInfoAttributes() throws ManagerBeanException{
    	List<String> addInfos = new LinkedList<String>();
    	IManagerBean addInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
    	Criteria criteria = new Criteria();
    	criteria.addOrder(addInfoBean.getFieldName(IRegistryAlias.REGISTRY_ADD_INFO_ATTRIBUTE));
		Projection projection = Projection.group(addInfoBean.getFieldName(IRegistryAlias.REGISTRY_ADD_INFO_ATTRIBUTE));
		Iterator iter = addInfoBean.getList(new ProjectionList(projection), criteria).iterator();
    	while(iter.hasNext()){
    		String addInfo = (String)iter.next();
    		addInfos.add(addInfo);
    	}
    	return addInfos;
    }
    
}