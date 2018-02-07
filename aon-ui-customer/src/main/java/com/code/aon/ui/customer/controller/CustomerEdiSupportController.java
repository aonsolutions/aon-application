package com.code.aon.ui.customer.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tag;
import com.code.aon.config.enumeration.TagType;
import com.code.aon.customer.CustomerEdiSupport;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.enumeration.NoteType;
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerEdiSupportController extends CustomerEdiSupport implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean seresAutoCommitDelivery;
	private boolean seresInvoicingMainAddress;
	
	public boolean isSeresAutoCommitDelivery() {
		return seresAutoCommitDelivery;
	}

	public void setSeresAutoCommitDelivery(boolean seresAutoCommitDelivery) {
		this.seresAutoCommitDelivery = seresAutoCommitDelivery;
	}
	
	public boolean isSeresInvoicingMainAddress() {
		return seresInvoicingMainAddress;
	}

	public void setSeresInvoicingMainAddress(boolean seresInvoicingMainAddress) {
		this.seresInvoicingMainAddress = seresInvoicingMainAddress;
	}

	public List<SelectItem> getPackingTypeTags() throws ManagerBeanException {
		IManagerBean tagBean = BeanManager.getManagerBean(Tag.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tagBean.getFieldName(IEntityAlias.TAG_TYPE), TagType.PACKING);
		List<SelectItem> list = new LinkedList<SelectItem>();
		for (ITransferObject to : tagBean.getList(criteria)) {
			Tag tag = (Tag) to;
			list.add(new SelectItem(tag.getId().toString(), tag.getName()));
		}
		return list;
	}

	public void onRecover(Customer customer) throws ManagerBeanException {
		init(customer);
		
		RegistryNote autoCommit = this.getRegistryNote(SERES_AUTO_COMMIT_DELIVERY, customer.getId());
		seresAutoCommitDelivery = autoCommit != null && new Boolean(autoCommit.getComments());
		
		RegistryNote mainInvoicingAddress = this.getRegistryNote(SERES_INVOICING_MAIN_ADDRESS, customer.getId());
		seresInvoicingMainAddress = mainInvoicingAddress != null && new Boolean(mainInvoicingAddress.getComments());
	}

	private void clear(Customer customer) {
		getAddresses(customer).forEach(
				address -> {
					getAddressCodes().put(
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

	private void saveSeresAutoCommitDeliveryParam(Customer customer) throws ManagerBeanException {
		RegistryNote autoCommitDelivery = this.getRegistryNote(SERES_AUTO_COMMIT_DELIVERY, customer.getId());
		if (autoCommitDelivery == null) {
			autoCommitDelivery = getEmptyNote(customer.getRegistry(), SERES_AUTO_COMMIT_DELIVERY);
		}
		autoCommitDelivery.setComments(String.valueOf(isSeresAutoCommitDelivery()));
		saveRegistryNote(autoCommitDelivery);
	}
	
	private void saveSeresInvoicingMainAddressParam(Customer customer) throws ManagerBeanException {
		RegistryNote rNote = this.getRegistryNote(SERES_INVOICING_MAIN_ADDRESS, customer.getId());
		if (rNote == null) {
			rNote = getEmptyNote(customer.getRegistry(), SERES_INVOICING_MAIN_ADDRESS);
		}
		rNote.setComments(String.valueOf(isSeresInvoicingMainAddress()));
		saveRegistryNote(rNote);
	}

	private void save(Customer customer) throws ManagerBeanException {
		for (Integer addressId : this.getAddressCodes().keySet()) {
			RegistryNote note = this.getRegistryNote(addressId.toString(),
					customer.getId());
			if (note == null) {
				note = getEmptyNote(customer.getRegistry(),
						addressId.toString());
			}
			String format = "";
			for(int i=0; i<EDI_VALUES.length; i++)
				format += EDI_VALUES[i] + "=%s;";
			List<String> values = this.getAddressCodes().get(addressId);
			note.setComments(String.format(format, values.toArray()));
			saveRegistryNote(note);
		}
	}

	public void onUpdate(Customer customer) throws ManagerBeanException {
		if (isEnabled()) {
			saveActiveParam(customer);
			saveSeresAutoCommitDeliveryParam(customer);
			saveSeresInvoicingMainAddressParam(customer);
			save(customer);
		} else {
			onRemove(customer);
		}
	}

	public void onRemove(Customer customer) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryNote.class);

		String ediCommentsPattern = CABECERA + "=%;" + PEDIDOS + "=%;" + PTO_ENTREGA
				+ "=%;" + FACTURA + "=%;" + FINANCIERA + "=%;"
				+ ALBARANES + "=%;"
				;
		Expression ediTypeExp = ExpressionUtilities.getOrExpression(
				ExpressionUtilities.getLikeExpression(
						bean.getFieldName(IEntityAlias.REGISTRY_NOTE_COMMENTS),
						ediCommentsPattern),
						ExpressionUtilities.getEqualExpression(bean
								.getFieldName(IEntityAlias.REGISTRY_NOTE_DESCRIPTION),
								ACTIVE));
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				bean.getFieldName(IEntityAlias.REGISTRY_NOTE_REGISTRY_ID),
				customer.getId());
		criteria.addEqualExpression(
				bean.getFieldName(IEntityAlias.REGISTRY_NOTE_NOTETYPE),
				NoteType.FACTURAE);
		criteria.addExpression(ediTypeExp);
		for (ITransferObject to : bean.getList(criteria)) {
			bean.remove(to);
		}
		clear(customer);
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


}
