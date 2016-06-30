package com.code.aon.ui.customer.controller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.enumeration.NoteType;
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerEdiSupportController implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory
			.getLogger(CustomerEdiSupportController.class);

	public final static String ACTIVE = "EDI_ACTIVE";
	public final static String CABECERA = "EDI_CABECERA";
	public final static String PEDIDOS = "EDI_PEDIDOS";
	public final static String PTO_ENTREGA = "EDI_PTO_ENTREGA";
	public final static String FACTURA = "EDI_FACTURA";
	public final static String FINANCIERA = "EDI_FINANCIERA";
	public final static String ALBARANES = "EDI_ALBARANES";
	
	private final static String EDI_CODES_PATTERN = CABECERA + "=(.*);" + PEDIDOS
			+ "=(.*);" + PTO_ENTREGA + "=(.*);" + FACTURA + "=(.*);"
			+ FINANCIERA + "=(.*);" + ALBARANES + "=(.*);";

	private Map<Integer, List<String>> addressCodes;
	private List<RegistryAddress> customerAddresses;
	private boolean enabled;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Map<Integer, List<String>> getAddressCodes() {
		return addressCodes;
	}

	public List<RegistryAddress> getCustomerAddresses() {
		return customerAddresses;
	}

	public void onRecover(Customer customer) throws ManagerBeanException {
		init(customer);
	}

	private void init(Customer customer) throws ManagerBeanException {
		this.customerAddresses = getAddresses(customer);
		this.addressCodes = new HashMap<Integer, List<String>>();

		RegistryNote active = this.getRegistryNote(ACTIVE, customer.getId());
		setEnabled(active != null && new Boolean(active.getComments()));
		
		getAddresses(customer).forEach(
				address -> {
					addressCodes.put(
							address.getId(),
							getAddressCodes(obtainRegistryNote(address.getId(),
									customer)));
				});
	}

	private RegistryNote obtainRegistryNote(Integer addressId, Customer customer) {
		RegistryNote note = this.getRegistryNote(addressId.toString(),
				customer.getId());
		if (note == null) {
			note = getEmptyNote(customer.getRegistry(), addressId.toString());
		}
		return note;
	}

	
	private List<String> getAddressCodes(RegistryNote registryNote) {
		String value = null;
		if (registryNote != null) {
			value = registryNote.getComments();
		}
		String[] values = { "", "", "", "", "", "" };
		Matcher m;
		Pattern p = Pattern.compile(EDI_CODES_PATTERN);
		if (value != null && (m = p.matcher(value)).find()) {
			values[0] = m.group(1);
			values[1] = m.group(2);
			values[2] = m.group(3);
			values[3] = m.group(4);
			values[4] = m.group(5);
			values[5] = m.group(6);
		}
		return Arrays.asList(values);
	}

	private void clear(Customer customer) {
		getAddresses(customer).forEach(
				address -> {
					addressCodes.put(
							address.getId(),
							getAddressCodes(obtainRegistryNote(address.getId(),
									customer)));
				});
	}

	private void saveActiveParam(Customer customer) throws ManagerBeanException {
		RegistryNote active = this.getRegistryNote(ACTIVE, customer.getId());
		if (active == null) {
			active = getEmptyNote(customer.getRegistry(), ACTIVE);
		}
		active.setComments(String.valueOf(isEnabled()));
		saveRegistryNote(active);
	}

	private void save(Customer customer) throws ManagerBeanException {
		for (Integer addressId : this.addressCodes.keySet()) {
			RegistryNote note = this.getRegistryNote(addressId.toString(),
					customer.getId());
			if (note == null) {
				note = getEmptyNote(customer.getRegistry(),
						addressId.toString());
			}
			String format = CABECERA + "=%s;" + PEDIDOS + "=%s;" + PTO_ENTREGA
					+ "=%s;" + FACTURA + "=%s;" + FINANCIERA + "=%s;"
					+ ALBARANES + "=%s;";
			List<String> values = this.addressCodes.get(addressId);
			note.setComments(String.format(format, values.toArray()));
			saveRegistryNote(note);
		}
	}

	public void onUpdate(Customer customer) throws ManagerBeanException {
		if (isEnabled()) {
			saveActiveParam(customer);
			save(customer);
		} else {
			onRemove(customer);
		}
	}

	public void onRemove(Customer customer) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryNote.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				bean.getFieldName(IEntityAlias.REGISTRY_NOTE_REGISTRY_ID),
				customer.getId());
		criteria.addEqualExpression(
				bean.getFieldName(IEntityAlias.REGISTRY_NOTE_NOTETYPE),
				NoteType.FACTURAE);
		for (ITransferObject to : bean.getList(criteria)) {
			bean.remove(to);
		}
		clear(customer);
	}

	private RegistryNote getEmptyNote(Registry registry, String key) {
		RegistryNote note = new RegistryNote();
		note.setNoteDate(new Date());
		note.setRegistry(registry);
		note.setNotetype(NoteType.FACTURAE);
		note.setDescription(key);
		return note;
	}

	private void saveRegistryNote(RegistryNote note)
			throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryNote.class);
		if (!StringUtils.isEmpty(note.getComments())) {
			note.setNoteDate(new Date());
			bean.insertOrUpdate(note);
		} else if (note.getId() != null) {
			bean.remove(note);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<RegistryAddress> getAddresses(Customer customer) {
		try {
			IManagerBean registryAddressBean = BeanManager
					.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(registryAddressBean
					.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID),
					customer.getId());
			criteria.addOrder(registryAddressBean
					.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS));
			return (List) registryAddressBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	private RegistryNote getRegistryNote(String key, Integer registryId) {
		RegistryNote note = null;
		try {
			IManagerBean bean = BeanManager.getManagerBean(RegistryNote.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					bean.getFieldName(IEntityAlias.REGISTRY_NOTE_REGISTRY_ID),
					registryId);
			criteria.addEqualExpression(
					bean.getFieldName(IEntityAlias.REGISTRY_NOTE_NOTETYPE),
					NoteType.FACTURAE);
			criteria.addEqualExpression(
					bean.getFieldName(IEntityAlias.REGISTRY_NOTE_DESCRIPTION),
					key);
			List<ITransferObject> list = bean.getList(criteria);
			if (!list.isEmpty()) {
				note = (RegistryNote) list.get(0);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return note;
	}
	
	public Map<String, String> getEdiCodes(Customer customer, RegistryAddress address){
		RegistryNote rnote = this.getRegistryNote(address.getId().toString(),
				customer.getId());
		String value = null;
		if (rnote != null) {
			value = rnote.getComments();
		}
		Map<String, String> values = new HashMap<>();
		Matcher m;
		Pattern p = Pattern.compile(EDI_CODES_PATTERN);
		if (value != null && (m = p.matcher(value)).find()) {
			values.put(CABECERA, m.group(1));
			values.put(PEDIDOS, m.group(2));
			values.put(PTO_ENTREGA, m.group(3));
			values.put(FACTURA, m.group(4));
			values.put(FINANCIERA, m.group(5));
			values.put(ALBARANES, m.group(6));
		}
		return values;
	}

}
