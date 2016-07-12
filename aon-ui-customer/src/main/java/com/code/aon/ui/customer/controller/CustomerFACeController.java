package com.code.aon.ui.customer.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.facturae.FACeUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.enumeration.NoteType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerFACeController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(CustomerFACeController.class);
	
	private Map<String,RegistryNote> notes;
	
	private List<RegistryAddress> addresses;
	
	private List<SelectItem> addressList;
	
	private boolean enabled;
	
	public void onRecover( Customer customer ) throws ManagerBeanException {
		init( customer );			
		updateCustomerAddresses( customer );
	}
	
	private void init( Customer customer ) throws ManagerBeanException {
		this.notes = new HashMap<String, RegistryNote>();
		RegistryNote active = FACeUtil.getRegistryNote(FACeUtil.FACE_ENABLED, customer.getId());
		setEnabled(active != null && new Boolean(active.getComments()));
		for( String key : FACeUtil.FACE_REQUIRED_CONSTANTS ) {
			updateRegistryNote(key, customer);
		}
		if(isEnabled()){
			initFACe( customer );
		}
	}

	private void initFACe( Customer customer ) throws ManagerBeanException {
		if(this.notes == null)
			this.notes = new HashMap<String, RegistryNote>();
		for( String key : FACeUtil.FACE_CONSTANTS ) {
			updateRegistryNote(key, customer);
		}
	}
	
	private void clear( Customer customer ) {
		for( String key : FACeUtil.FACE_REQUIRED_CONSTANTS ) {
			RegistryNote note = getEmptyNote(customer.getRegistry(), key);
			setNote(key, note);
		}
		for( String key : FACeUtil.FACE_CONSTANTS ) {
   			RegistryNote note = getEmptyNote(customer.getRegistry(), key);
   	   		setNote(key, note);
   		}
	}
	
	private void save() throws ManagerBeanException {
		for( RegistryNote note : this.notes.values() ) {
			saveRegistryNote(note);
		}
	}

	public void onUpdate( Customer customer ) throws ManagerBeanException {
		if ( customer.isEInvoice() ) {
			RegistryNote note = getNote(FACeUtil.FACE_ENABLED);
			if (note != null) {
				note.setComments(String.valueOf(isEnabled()));
			}
			save();
		} else {
			onRemove(customer);
		}
	}
	
	public void onRemove( Customer customer ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryNote.class);
		Criteria criteria = new Criteria();
   		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_NOTE_REGISTRY_ID), customer.getId());
   		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_NOTE_NOTETYPE), NoteType.FACTURAE);
		for( ITransferObject to : bean.getList(criteria) ) {
			bean.remove(to);
		}
		clear(customer);
	}
	
	private RegistryNote getNote( String key ) {
		return this.notes.get(key);
	}

	private void setNote( String key, RegistryNote note ) {
		this.notes.put(key, note);
	}
	
	private RegistryAddress getAddress( String key ) {
		RegistryAddress address = null;
		RegistryNote rnote = getNote(key);
		if (! StringUtils.isEmpty(rnote.getComments()) ) {
			int id = NumberUtils.toInt(rnote.getComments());
			for( RegistryAddress ra : this.addresses ) {
				if ( ra.getId().equals(id) ) {
					address = ra;
					break;
				}
			}
		}
		return address;
	}	
	
	private void setAddress( String key, RegistryAddress address ) {
		String id = (address != null) ? address.getId().toString() : null;
		getNote(key).setComments(id);
	}	
	
	public RegistryNote getFiscal() {
		return getNote(FACeUtil.FACE_FISCAL_CENTRE_CODE);
	}

	public void setFiscal(RegistryNote fiscal) {
		setNote(FACeUtil.FACE_FISCAL_CENTRE_CODE, fiscal);
	}

	public RegistryAddress getFiscalAddress() {
		return getAddress(FACeUtil.FACE_FISCAL_ADDRESS);
	}

	public void setFiscalAddress(RegistryAddress address) {
		setAddress(FACeUtil.FACE_FISCAL_ADDRESS, address);
	}

	public RegistryNote getReceptor() {
		return getNote(FACeUtil.FACE_RECEPTOR_CENTRE_CODE);
	}

	public void setReceptor(RegistryNote receptor) {
		setNote(FACeUtil.FACE_RECEPTOR_CENTRE_CODE, receptor);
	}

	public RegistryAddress getReceptorAddress() {
		return getAddress(FACeUtil.FACE_RECEPTOR_ADDRESS);
	}

	public void setReceptorAddress(RegistryAddress address) {
		setAddress(FACeUtil.FACE_RECEPTOR_ADDRESS, address);
	}

	public RegistryNote getPagador() {
		return getNote(FACeUtil.FACE_PAGADOR_CENTRE_CODE);
	}

	public void setPagador(RegistryNote pagador) {
		setNote(FACeUtil.FACE_PAGADOR_CENTRE_CODE, pagador);
	}

	public RegistryAddress getPagadorAddress() {
		return getAddress(FACeUtil.FACE_PAGADOR_ADDRESS);
	}

	public void setPagadorAddress(RegistryAddress address) {
		setAddress(FACeUtil.FACE_PAGADOR_ADDRESS, address);
	}

	public RegistryNote getComprador() {
		return getNote(FACeUtil.FACE_COMPRADOR_CENTRE_CODE);
	}

	public void setComprador(RegistryNote comprador) {
		setNote(FACeUtil.FACE_COMPRADOR_CENTRE_CODE, comprador);
	}

	public RegistryAddress getCompradorAddress() {
		return getAddress(FACeUtil.FACE_COMPRADOR_ADDRESS);
	}

	public void setCompradorAddress(RegistryAddress address) {
		setAddress(FACeUtil.FACE_COMPRADOR_ADDRESS, address);
	}
	
	public RegistryNote getOrderNumber() {
		return getNote(FACeUtil.FACE_INVOICE_ISSUER_CONTRACT_REFERENCE);
	}
	
	public void setOrderNumber(RegistryNote orderNumber) {
		setNote(FACeUtil.FACE_INVOICE_ISSUER_CONTRACT_REFERENCE, orderNumber);
	}
	
	public RegistryNote getDeliveryNumber() {
		return getNote(FACeUtil.FACE_INVOICE_DELIVERY_NUMBER);
	}
	
	public void setDeliveryNumber(RegistryNote deliveryNumber) {
		setNote(FACeUtil.FACE_INVOICE_ISSUER_CONTRACT_REFERENCE, deliveryNumber);
	}
	
	public RegistryNote getCenterIdentification() {
		return getNote(FACeUtil.FACE_INVOICE_SEQUENCE_NUMBER);
	}
	
	public void setCenterIdentification(RegistryNote centerIdentification) {
		setNote(FACeUtil.FACE_INVOICE_ISSUER_CONTRACT_REFERENCE, centerIdentification);
	}

	private String getLabel( RegistryAddress address ) {
		String addressLabel = address.getFullAddress();
		addressLabel = ((addressLabel.length()>30)?addressLabel.substring(0,27)+"...":addressLabel);
		if ( address.getGeozone() != null ) {
			addressLabel += " - " + address.getGeozone().getName();
		}
		addressLabel = ((addressLabel.length()>48)?addressLabel.substring(0,45)+"...":addressLabel);
		return addressLabel;
	}

	private RegistryNote getEmptyNote( Registry registry, String key ) {
		RegistryNote note = new RegistryNote();
		note.setNoteDate(new Date());
		note.setRegistry(registry);
		note.setNotetype(NoteType.FACTURAE);
		note.setDescription(key);
		return note;
	}
	
	private void updateRegistryNote( String key, Customer customer ) throws ManagerBeanException {
    	RegistryNote note = FACeUtil.getRegistryNote(key, customer.getId());
    	if ( note == null ) {
   			note = getEmptyNote(customer.getRegistry(), key);
   		}
   		setNote(key, note);
    }
	
	private void saveRegistryNote( RegistryNote note ) throws ManagerBeanException {
   		IManagerBean bean = BeanManager.getManagerBean(RegistryNote.class);
   		if (! StringUtils.isEmpty(note.getComments()) ) {
   			note.setNoteDate(new Date());
   			bean.insertOrUpdate(note);
   		} else if ( note.getId() != null ) {
   			Registry registry = note.getRegistry();
   			String key = note.getDescription();
   			bean.remove(note);
   			setNote(key, getEmptyNote(registry, key));
   		}
	}
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private List<RegistryAddress> getAddresses( Customer customer ) {
    	try {
       		IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
       		Criteria criteria = new Criteria();
       		criteria.addEqualExpression(registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), customer.getId());
       		criteria.addOrder(registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS));
       		return (List) registryAddressBean.getList(criteria);    		
    	} catch ( ManagerBeanException e ) {
    		LOGGER.error(e.getMessage(), e);
    	}
    	return null;
    }
    
    public List<SelectItem> getCustomerAddresses() {
    	return this.addressList;
    }
    	
    private void updateCustomerAddresses( Customer customer ) {
    	if ( isEnabled() ) {
    		this.addresses = getAddresses(customer);
        	this.addressList = new LinkedList<SelectItem>();
       		for (RegistryAddress address : this.addresses) {
    			SelectItem item = new SelectItem(address, getLabel(address));
    			addressList.add(item);
        	}    		
    	}
    }

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void onEnabledChanged( ActionEvent event ) throws ManagerBeanException {
		if ( isEnabled() ) {
			CustomerController cc = (CustomerController) AonUtil.getRegisteredBean(ICustomerConstants.CUSTOMER_CONTROLLER_NAME);
			Customer customer = (Customer) cc.getTo();
			initFACe( customer );
			updateCustomerAddresses(customer);
		}					
	}

}
