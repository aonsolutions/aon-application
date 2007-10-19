package com.code.aon.ui.registry.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.geozone.GeoZone;
import com.code.aon.geozone.dao.IGeoZoneAlias;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Relationship;
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

	/** The geoZones list. */
	private List<SelectItem> geoZones;
	
    /**
     * Gets the address types.
     * 
     * @return the address types
     */
    public List<SelectItem> getAddressTypes() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    LinkedList<SelectItem> types = new LinkedList<SelectItem>();
        for( AddressType type : AddressType.values() ) {
            String name = type.getName(locale); 
            SelectItem item = new SelectItem(type, name);
            types.add( item );
        }
        return types;
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
     * Gets the geoZones.
     * 
     * @return the geoZones
     * 
     * @throws ManagerBeanException the manager bean exception
     */
    public List<SelectItem> getGeoZones() throws ManagerBeanException {
        if (geoZones == null) {
            geoZones = new LinkedList<SelectItem>();
            IManagerBean geozoneBean = BeanManager.getManagerBean(GeoZone.class);
            Criteria criteria = new Criteria();
            criteria.addOrder(geozoneBean.getFieldName(IGeoZoneAlias.GEO_ZONE_NAME));
            Iterator<ITransferObject> iter = geozoneBean.getList(criteria).iterator();
            while (iter.hasNext()){
                GeoZone geozone = (GeoZone) iter.next();
                SelectItem item = new SelectItem(geozone.getId(), geozone.getName());
                geoZones.add( item );
            }
        }
        return geoZones;
    }

    /**
     * Gets the media types.
     * 
     * @return the media types
     */
    @SuppressWarnings("unchecked")
    public List getMediaTypes() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    LinkedList<SelectItem> types = new LinkedList<SelectItem>();
        for( MediaType type : MediaType.values() ) {
            String name = type.getName(locale); 
            SelectItem item = new SelectItem(type, name);
            types.add( item );
        }
        return types;
    }
    
    /**
     * Gets the registry types.
     * 
     * @return the registry types
     */
    public List<SelectItem> getRegistryTypes() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    LinkedList<SelectItem> types = new LinkedList<SelectItem>();
        for( RegistryType type : RegistryType.values() ) {
            String name = type.getName(locale); 
            SelectItem item = new SelectItem(type, name);
            types.add( item );
        }
        return types;
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
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    LinkedList<SelectItem> genders = new LinkedList<SelectItem>();
        for( Gender gender : Gender.values() ) {
            String name = gender.getName(locale); 
            SelectItem item = new SelectItem(gender, name);
            genders.add( item );
        }
        return genders;
    }
    
    public List<SelectItem> getMaritalStatuses() {
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    LinkedList<SelectItem> maritalStatuses = new LinkedList<SelectItem>();
        for( MaritalStatus status : MaritalStatus.values() ) {
            String name = status.getName(locale); 
            SelectItem item = new SelectItem(status, name);
            maritalStatuses.add( item );
        }
        return maritalStatuses;
    }
    
    public List<SelectItem> getRegistryAttachmentTypes() {
    	Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    LinkedList<SelectItem> rAttachTypes = new LinkedList<SelectItem>();
        for( RegistryAttachmentType status : RegistryAttachmentType.values() ) {
            String name = status.getName(locale); 
            SelectItem item = new SelectItem(status, name);
            rAttachTypes.add( item );
        }
        return rAttachTypes;
    }
    
	public List<SelectItem> getNoteTypes() {
		List<SelectItem> noteTypes = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		NoteType[] types = NoteType.values();
		for (int i = 0; i < types.length; i++) {
			NoteType type = types[i];
			if (type.compareTo(NoteType.OBSERVATION)!=0){
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				noteTypes.add(item);
			}
		}
		return noteTypes;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getUsers() throws ManagerBeanException {
		List<SelectItem> users = new LinkedList<SelectItem>();
		IManagerBean userBean = BeanManager.getManagerBean(com.code.aon.config.User.class);
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
}