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
import com.code.aon.customer.Customer;
import com.code.aon.customer.CustomerEdiSupport;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.enumeration.NoteType;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.model.seres.SeresPath;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CustomerEdiSupportController extends CustomerEdiSupport implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean seresAutoCommitDelivery;
	private boolean seresInvoicingMainAddress;
	private boolean eci;
	
	private SeresPath receiveOrder;
	private SeresPath sendDesadv;
	private SeresPath sendInvoice;

	public boolean isEci(){
		return eci;
	}
	
	public void setEci(boolean eci) {
		this.eci = eci;
	}
	
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

	public SeresPath getReceiveOrder() {
		if(receiveOrder == null)
			return SeresPath.RECEPCION_ORDERS_D96A;
		return receiveOrder;
	}
	
	public void setReceiveOrder(SeresPath receiveOrder) {
		this.receiveOrder = receiveOrder;
	}
	
	public SeresPath getSendDesadv() {
		if(sendDesadv == null)
			return SeresPath.ENVIO_DESADV_D96A;
		return sendDesadv;
	}
	
	public void setSendDesadv(SeresPath sendDesadv) {
		this.sendDesadv = sendDesadv;
	}
	
	public SeresPath getSendInvoice() {
		if(sendDesadv == null)
			return SeresPath.ENVIO_INVOIC_D93A;
		return sendInvoice;
	}
	
	public void setSendInvoice(SeresPath sendInvoice) {
		this.sendInvoice = sendInvoice;
	}
	
	public List<SelectItem> getReceiveOrderOptions() {
		List<SelectItem> list = new LinkedList<>();
		list.add(new SelectItem(SeresPath.RECEPCION_ORDERS_D01B, "D01B"));
		list.add(new SelectItem(SeresPath.RECEPCION_ORDERS_D93A, "D93A"));
		list.add(new SelectItem(SeresPath.RECEPCION_ORDERS_D96A, "D96A"));
		return list;
	}
	
	public List<SelectItem> getSendDesadvOptions() {
		List<SelectItem> list = new LinkedList<>();
		list.add(new SelectItem(SeresPath.ENVIO_DESADV_D01B, "D01B"));
		list.add(new SelectItem(SeresPath.ENVIO_DESADV_D96A, "D96A"));
		return list;
	}
	
	public List<SelectItem> getSendInvoiceOptions() {
		List<SelectItem> list = new LinkedList<>();
		list.add(new SelectItem(SeresPath.ENVIO_INVOIC_D01B, "D01B"));
		list.add(new SelectItem(SeresPath.ENVIO_INVOIC_D93A, "D93A"));
		list.add(new SelectItem(SeresPath.ENVIO_INVOIC_D96A, "D96A"));
		return list;
	}
	
	public List<SelectItem> getPackingTypeTags() throws ManagerBeanException {
		IManagerBean tagBean = BeanManager.getManagerBean(Tag.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(tagBean.getFieldName(IEntityAlias.TAG_TYPE), TagType.PACKING);
		List<SelectItem> list = new LinkedList<>();
		for (ITransferObject to : tagBean.getList(criteria)) {
			Tag tag = (Tag) to;
			list.add(new SelectItem(tag.getId().toString(), tag.getName()));
		}
		return list;
	}

	public void onRecover(Customer customer) throws ManagerBeanException {
		setEci(isEci(customer.getRegistry()));
		init(customer);
		
		RegistryNote autoCommit = this.getRegistryNote(SERES_AUTO_COMMIT_DELIVERY, customer.getId());
		seresAutoCommitDelivery = autoCommit != null && !AonStringUtils.isBlank(autoCommit.getComments())
				&& Boolean.valueOf(autoCommit.getComments().trim());
		RegistryNote mainInvoicingAddress = this.getRegistryNote(SERES_INVOICING_MAIN_ADDRESS, customer.getId());
		seresInvoicingMainAddress = mainInvoicingAddress != null && !AonStringUtils.isBlank(mainInvoicingAddress.getComments())
				&& Boolean.valueOf(mainInvoicingAddress.getComments().trim());

		RegistryNote order = this.getRegistryNote(SERES_RECEIVE_ORDER, customer.getId());
		if(order != null) setReceiveOrder(SeresPath.safeValueOf(order.getComments()));
		
		RegistryNote desadv = this.getRegistryNote(SERES_SEND_DESADV, customer.getId());
		if(desadv != null) setSendDesadv(SeresPath.safeValueOf(desadv.getComments()));
		
		RegistryNote invoice = this.getRegistryNote(SERES_SEND_INVOICE, customer.getId());
		if(invoice != null) setSendInvoice(SeresPath.safeValueOf(invoice.getComments()));
		
	}

	private void clear(Customer customer) {
		setEci(isEci(customer.getRegistry()));
		getAddresses(customer).forEach(
				address -> 
					getAddressCodes().put(
							address.getId(),
							getAddressCodes(obtainRegistryNote(address.getId(),
									customer)))
				);
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
	
	
	private void saveReceiveOrderParam(Customer customer) throws ManagerBeanException {
		RegistryNote rNote = this.getRegistryNote(SERES_RECEIVE_ORDER, customer.getId());
		if (rNote == null) {
			rNote = getEmptyNote(customer.getRegistry(), SERES_RECEIVE_ORDER);
		}
		rNote.setComments(getReceiveOrder().name());
		saveRegistryNote(rNote);
	}
	
	private void saveSendDesadvParam(Customer customer) throws ManagerBeanException {
		RegistryNote rNote = this.getRegistryNote(SERES_SEND_DESADV, customer.getId());
		if (rNote == null) {
			rNote = getEmptyNote(customer.getRegistry(), SERES_SEND_DESADV);
		}
		rNote.setComments(getSendDesadv().name());
		saveRegistryNote(rNote);
	}
	
	private void saveSendInvoiceParam(Customer customer) throws ManagerBeanException {
		RegistryNote rNote = this.getRegistryNote(SERES_SEND_INVOICE, customer.getId());
		if (rNote == null) {
			rNote = getEmptyNote(customer.getRegistry(), SERES_SEND_INVOICE);
		}
		rNote.setComments(getSendInvoice().name());
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
			StringBuilder format = new StringBuilder();
			for(int i=0; i<EDI_VALUES.length; i++)
				format.append(EDI_VALUES[i] + "=%s;");
			
			List<String> values = this.getAddressCodes().get(addressId);
			note.setComments(String.format(format.toString(), values.toArray()));
			saveRegistryNote(note);
		}
	}

	public void onUpdate(Customer customer) throws ManagerBeanException {
		if (isEnabled()) {
			saveActiveParam(customer);
			saveSeresAutoCommitDeliveryParam(customer);
			saveSeresInvoicingMainAddressParam(customer);
			
			saveReceiveOrderParam(customer);
			saveSendDesadvParam(customer);
			saveSendInvoiceParam(customer);
			
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
	
	private boolean isEci(Registry registry) {
		return "A28017895".equalsIgnoreCase(registry.getDocument());
	}
}
