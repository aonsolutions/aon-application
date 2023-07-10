package com.code.aon.ui.registry.controller;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.User;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.registry.Category;
import com.code.aon.registry.Question;
import com.code.aon.registry.QuestionValue;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddInfo;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.Relationship;
import com.code.aon.registry.Segment;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.CategoryType;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.NoteType;
import com.code.aon.registry.enumeration.QuestionType;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.registry.enumeration.RegistryItemStatus;
import com.code.aon.registry.enumeration.RegistrySellerStatus;
import com.code.aon.registry.enumeration.RegistrySellerType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.registry.enumeration.TaxRegime;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Controller used to get Collections related with clasess in <code>com.code.aon.registry</code>.
 * 
 */
public class RegistryCollectionsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<SelectItem> addressTypes;
	private List<SelectItem> streetTypes;
	private List<SelectItem> mediaTypes;
	private List<SelectItem> registryTypes;
	private List<SelectItem> genders;
	private List<SelectItem> maritalStatuses;
	private List<SelectItem> registryAttachmentTypes;
	private List<SelectItem> noteTypes;
	private List<SelectItem> documentTypes;
	private List<SelectItem> taxRegimes;
	private List<SelectItem> registryItemStatuses;
	private List<SelectItem> registrySellerStatuses;
	private List<SelectItem> registrySellerTypes;
	private List<SelectItem> questionTypes;
	private Category emptyCategory;
	private RegistryBank rBank; // No Borrar. Euke.
								// Se utiliza como selector 
								// en la pantalla de alta de vencimientos.
	
    public List<SelectItem> getAddressTypes() {
    	if ( addressTypes == null ) {
    		Locale locale = AonUtil.getCurrentLocale();
    		addressTypes = new LinkedList<SelectItem>();
    		for( AddressType type : AddressType.values() ) {
	            String name = type.getName(locale); 
	            SelectItem item = new SelectItem(type, name);
	            addressTypes.add( item );
    		}
        }
        return addressTypes;
    }

    public List<SelectItem> getStreetTypes() {
    	if ( streetTypes == null ) {
    		Locale locale = AonUtil.getCurrentLocale();
	        streetTypes = new LinkedList<SelectItem>();
	        for( StreetType type : StreetType.values() ) {
	            String name = type.getName(locale); 
	            SelectItem item = new SelectItem(type, name);
	            streetTypes.add( item );
	        }
    	}
        return streetTypes;
    }

    public List<SelectItem> getMediaTypes() {
    	if ( mediaTypes == null ) {
    		Locale locale = AonUtil.getCurrentLocale();
	        mediaTypes = new LinkedList<SelectItem>();
	        for( MediaType type : MediaType.values() ) {
	            String name = type.getName(locale); 
	            SelectItem item = new SelectItem(type, name);
	            mediaTypes.add( item );
	        }
    	}
        return mediaTypes;
    }
    
    public List<SelectItem> getRegistryTypes() {
    	if ( registryTypes == null) {
    		Locale locale = AonUtil.getCurrentLocale();
	        registryTypes = new LinkedList<SelectItem>();
	        for( RegistryType type : RegistryType.values() ) {
	            String name = type.getName(locale); 
	            SelectItem item = new SelectItem(type, name);
	            registryTypes.add( item );
	        }
    	}
        return registryTypes;
    }
    
    public List<SelectItem> getRelationships() throws ManagerBeanException{
    	List<SelectItem> relationships = new LinkedList<SelectItem>();
    	IManagerBean relationshipBean = BeanManager.getManagerBean(Relationship.class);
    	Criteria criteria = new Criteria();
    	criteria.addOrder(relationshipBean.getFieldName(IEntityAlias.RELATIONSHIP_DESCRIPTION));
    	Iterator<?> iter = relationshipBean.getList(criteria).iterator();
    	while(iter.hasNext()){
    		Relationship relationship = (Relationship)iter.next();
    		SelectItem item = new SelectItem(relationship.getId(), relationship.getDescription());
    		relationships.add(item);
    	}
    	return relationships;
    }
    
    public List<SelectItem> getGenders() {
    	if ( genders == null ) {
    		Locale locale = AonUtil.getCurrentLocale();
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
    		Locale locale = AonUtil.getCurrentLocale();
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
    		Locale locale = AonUtil.getCurrentLocale();
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
			Locale locale = AonUtil.getCurrentLocale();
			noteTypes = new LinkedList<SelectItem>();
			for( NoteType type : NoteType.values() ) {
				if ((type != NoteType.OBSERVATION) && (type != NoteType.OBSERVATION)) {
					String name = type.getName(locale);
					SelectItem item = new SelectItem(type, name);
					noteTypes.add(item);
				}
			}
		}
		return noteTypes;
	}
	
	public List<SelectItem> getDocumentTypes() {
		if ( documentTypes == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			documentTypes = new LinkedList<SelectItem>();
			for( DocumentType type : DocumentType.values() ) {
					String name = type.getName(locale);
					SelectItem item = new SelectItem(type, name);
					documentTypes.add(item);
			}
		}
		return documentTypes;
	}
	
	public List<SelectItem> getTaxRegimes() {
		if ( taxRegimes == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			taxRegimes = new LinkedList<SelectItem>();
			for( TaxRegime taxRegime : TaxRegime.values() ) {
					String name = taxRegime.getName(locale);
					SelectItem item = new SelectItem(taxRegime, name);
					taxRegimes.add(item);
			}
		}
		return taxRegimes;
	}

	public List<SelectItem> getSegments() throws ManagerBeanException{
    	return getSegments(false);
    }

    public List<SelectItem> getSegmentIds() throws ManagerBeanException{
    	return getSegments(true);
    }
    
    private List<SelectItem> getSegments( boolean onlyId ) throws ManagerBeanException{
    	List<SelectItem> segments = new LinkedList<SelectItem>();
    	IManagerBean segmentBean = BeanManager.getManagerBean(Segment.class);
    	Criteria criteria = new Criteria();
    	criteria.addOrder(segmentBean.getFieldName(IEntityAlias.SEGMENT_NAME));
    	Iterator<?> iter = segmentBean.getList(criteria).iterator();
    	while(iter.hasNext()){
    		Segment segment = (Segment)iter.next();
    		if ( onlyId ) {
    			segments.add(new SelectItem(segment.getId(), segment.getName()));
    		} else {
    			segments.add(new SelectItem(segment, segment.getName()));	
    		}
    	}
    	return segments;
    }
    
	public List<SelectItem> getUsers() throws ManagerBeanException {
		List<SelectItem> users = new LinkedList<SelectItem>();
		IManagerBean userBean = BeanManager.getManagerBean(User.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(userBean.getFieldName(IEntityAlias.USER_NAME));
		Iterator<?> iter = userBean.getList(criteria).iterator();
		while(iter.hasNext()){
			User user = (User)iter.next();
			SelectItem item = new SelectItem(user.getId(), user.getName());
			users.add(item);
		}
		return users;
	}

	public List<SelectItem> getAllRegistryBanks(Registry registry) throws ManagerBeanException {
		List<SelectItem> rBanks = new LinkedList<SelectItem>();
		IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_REGISTRY_ID), registry.getId());
		Iterator<?> iter = rBankBean.getList(criteria).iterator();
		while(iter.hasNext()){
			RegistryBank rBank = (RegistryBank)iter.next();
			SelectItem item = new SelectItem(rBank, rBank.getFullName());
			rBanks.add(item);
		}
		return rBanks;
	}

	public List<SelectItem> getActiveRegistryBanks(Registry registry) throws ManagerBeanException {
		List<SelectItem> rBanks = new LinkedList<SelectItem>();
		IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_REGISTRY_ID), registry.getId());
		criteria.addEqualExpression(rBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_ACTIVE), true);
		Iterator<?> iter = rBankBean.getList(criteria).iterator();
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<SelectItem> getCategories() throws ManagerBeanException {
		IManagerBean categoryBean = BeanManager.getManagerBean(Category.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(categoryBean.getFieldName(IEntityAlias.CATEGORY_TYPE), CategoryType.REGISTRY_ATTACHMENT);
		criteria.addOrder(categoryBean.getFieldName(IEntityAlias.CATEGORY_NAME));
		return getCategoryList( (List) categoryBean.getList(criteria));
	}
	
	public static List<SelectItem> getCategoryList( List<Category> categories ) {
		List<SelectItem> list = new LinkedList<SelectItem>();
		for (Category category : categories) {
			list.add( AonUtil.getSelectItem(category, category.getName()));
		}
		return list;
	}	
	
	public Category getCategory() {
		return null;
	}

	public void setCategory( Category category ) {
	}	

    public List<String> getAddInfoAttributes() throws ManagerBeanException{
    	List<String> addInfos = new LinkedList<String>();
    	IManagerBean addInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
    	Criteria criteria = new Criteria();
    	criteria.addOrder(addInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE));
		Projection projection = Projection.group(addInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE));
		Iterator<?> iter = addInfoBean.getList(new ProjectionList(projection), criteria).iterator();
    	while(iter.hasNext()){
    		String addInfo = (String)iter.next();
    		addInfos.add(addInfo);
    	}
    	return addInfos;
    }
 
    public CategoryType[] getCategoryTypes() {
    	return CategoryType.values();
    }
 
	/**
	 * Gets the registry item statuses.
	 * 
	 * @return the registry item statuses
	 */
	public List<SelectItem> getRegistryItemStatuses() {
		if ( registryItemStatuses == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			registryItemStatuses = new LinkedList<SelectItem>();
			for (RegistryItemStatus status : RegistryItemStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				registryItemStatuses.add(item);
			}
		}
		return registryItemStatuses;
	}

	/**
	 * Gets the registry item statuses.
	 * 
	 * @return the registry item statuses
	 */
	public List<SelectItem> getRegistrySellerStatuses() {
		if ( registrySellerStatuses == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			registrySellerStatuses = new LinkedList<SelectItem>();
			for (RegistrySellerStatus status : RegistrySellerStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				registrySellerStatuses.add(item);
			}
		}
		return registrySellerStatuses;
	}
	
	/**
	 * Gets the registry item types.
	 * 
	 * @return the registry item types
	 */
	public List<SelectItem> getRegistrySellerTypes() {
		if ( registrySellerTypes == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			registrySellerTypes = new LinkedList<SelectItem>();
			for (RegistrySellerType status : RegistrySellerType.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				registrySellerTypes.add(item);
			}
		}
		return registrySellerTypes;
	}
	
	public static List<SelectItem> getQuestionValues( Question question ) throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(QuestionValue.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.QUESTION_VALUE_QUESTION_ID), question.getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			QuestionValue questionValue = (QuestionValue) to;
			Object value = questionValue.getValue( question.getType() );
			SelectItem item = new SelectItem(questionValue.getId(), ObjectUtils.toString(value));
			list.add(item);
		}
		return list;
	}	
 
	/**
	 * Gets the question types.
	 * 
	 * @return the question types.
	 */
	public List<SelectItem> getQuestionTypes() {
		if ( questionTypes == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			questionTypes = new LinkedList<SelectItem>();
			for (QuestionType auditLevel : QuestionType.values()) {
				String name = auditLevel.getName(locale);
				SelectItem item = new SelectItem(auditLevel, name);
				questionTypes.add(item);
			}
		}
		return questionTypes;
	}	

	public Category getEmptyCategory() {
		if ( this.emptyCategory == null ) {
			this.emptyCategory = new Category();
			this.emptyCategory.setName("-");
			this.emptyCategory.setDomain(DomainManager.getCurrentDomain());			
		}
		return this.emptyCategory;
	}
	
}