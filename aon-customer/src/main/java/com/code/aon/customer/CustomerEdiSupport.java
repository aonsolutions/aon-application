package com.code.aon.customer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tag;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.enumeration.NoteType;
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerEdiSupport implements Serializable, IEdiSupport {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory
			.getLogger(CustomerEdiSupport.class);
	
	private Map<Integer, List<String>> addressCodes;
	private List<RegistryAddress> customerAddresses;
	private boolean enabled;
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public Map<Integer, List<String>> getAddressCodes(){
		return addressCodes;
	}
	
	public List<RegistryAddress> getCustomerAddresses() {
		return customerAddresses;
	}

	public void setCustomerAddresses(List<RegistryAddress> customerAddresses) {
		this.customerAddresses = customerAddresses;
	}

	public void init(Customer customer) throws ManagerBeanException {
		this.customerAddresses = this.getAddresses(customer);
		this.addressCodes = new HashMap<Integer, List<String>>();
		
		RegistryNote active = this.getRegistryNote(ACTIVE, customer.getId());
		enabled = active != null && new Boolean(active.getComments());
		
		this.customerAddresses.forEach(
				address -> {
					addressCodes.put(
							address.getId(),
							getAddressCodes(this.obtainRegistryNote(address.getId(),
									customer)));
				});
	}
	
	protected List<RegistryAddress> getAddresses(Customer customer) {
		try {
			IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID),
					customer.getId());
			criteria.addOrder(registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS));
			return (List) registryAddressBean.getList(criteria);
		} catch (ManagerBeanException e) {
			 LOGGER.info(e.getMessage());
		}
		return null;
	}
	
	protected List<String> getAddressCodes(RegistryNote registryNote) {
		String value = null;
		if (registryNote != null) {
			value = registryNote.getComments();
		}
		String[] values = new String[EDI_VALUES.length];
		Matcher m;
		
		Pattern p1 = Pattern.compile(EDI_CODES_PATTERN);
		int ediCodesCount=0;
		if (value != null && (m = p1.matcher(value)).find()) {
			for(ediCodesCount=0; ediCodesCount<m.groupCount(); ediCodesCount++)
				values[ediCodesCount] = m.group(ediCodesCount+1);
		}
		Pattern p2 = Pattern.compile(EDI_PACKING_PATTERN);
		if (value != null && (m = p2.matcher(value)).find()) {
			for(int i=0; i<m.groupCount(); i++)
				values[ediCodesCount + i] = m.group(i+1);
		}
		return Arrays.asList(values);
	}
	
	protected RegistryNote obtainRegistryNote(Integer addressId, Customer customer) {
		RegistryNote note = this.getRegistryNote(addressId.toString(), customer.getId());
		if (note == null) {
			note = getEmptyNote(customer.getRegistry(), addressId.toString());
		}
		return note;
	}

	protected RegistryNote getEmptyNote(Registry registry, String key) {
		RegistryNote note = new RegistryNote();
		note.setNoteDate(new Date());
		note.setRegistry(registry);
		note.setNotetype(NoteType.FACTURAE);
		note.setDescription(key);
		return note;
	}

	protected RegistryNote getRegistryNote(String key, Integer registryId) {
		RegistryNote note = null;
		try {
			IManagerBean bean = BeanManager.getManagerBean(RegistryNote.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_NOTE_REGISTRY_ID), registryId);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_NOTE_NOTETYPE),
					NoteType.FACTURAE);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_NOTE_DESCRIPTION), key);
			List<ITransferObject> list = bean.getList(criteria);
			if (!list.isEmpty()) {
				note = (RegistryNote) list.get(0);
			}
		} catch (ManagerBeanException e) {
			 LOGGER.info(e.getMessage());
		}
		return note;
	}
	
	public Map<String, String> getEdiCodes(Registry registry, RegistryAddress address){
		RegistryNote rnote = this.getRegistryNote(address.getId().toString(),
				registry.getId());
		String value = null;
		if (rnote != null) {
			value = rnote.getComments();
		}
		Map<String, String> values = new HashMap<>();
		Matcher m;
		Pattern p = Pattern.compile(EDI_CODES_PATTERN);
		if (value != null && (m = p.matcher(value)).find()) {
			for(int i=0; i<EDI_VALUES.length; i++)
				values.put(EDI_VALUES[i], m.groupCount()>i ? m.group(i+1) : null);
		}
		return values;
	}
	
	public Tag obtainPackingTag(Registry registry, RegistryAddress address) {
		return obtainPackingTag(registry, address, MEDIDA);
	}
	
	public Tag obtainPackingTagInvoice(Registry registry, RegistryAddress address) {
		Tag tag = obtainPackingTag(registry, address, MEDIDA_FACTURA);
		if(tag==null)
			tag = obtainPackingTag(registry, address, MEDIDA);
		return tag;
	}
	
	private Tag obtainPackingTag(Registry registry, RegistryAddress address, String packTag) {
		RegistryNote rNote = this.getRegistryNote(address.getId().toString(),
				registry.getId());
		String value = null;
		if (rNote != null) {
			value = rNote.getComments();
		}
		Matcher m;
		Pattern p = Pattern.compile(packTag + "=([^;]*);");
		try {
			if (value != null && (m = p.matcher(value)).find()) {
				IManagerBean tagBean = BeanManager.getManagerBean(Tag.class);
				return (Tag) tagBean.get(Integer.valueOf(m.group(1)));
			}
		} catch (ManagerBeanException e) {
			LOGGER.info(e.getMessage());
		} catch (NumberFormatException e) {
			LOGGER.info(e.getMessage());
		}
		return null;
	}
	
}
