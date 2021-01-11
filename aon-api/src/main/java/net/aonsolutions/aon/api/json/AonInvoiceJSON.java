package net.aonsolutions.aon.api.json;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.api.json.FunctionalInterfaces.IAonInvoiceFromJSON;
import net.aonsolutions.aon.api.json.FunctionalInterfaces.IAonInvoiceToJSON;

public enum AonInvoiceJSON {

	ID(
		(invoice, json) -> invoice.setId(json.optInt(IConstants.ID)),
		(invoice, company, json) -> json.put(IConstants.ID, invoice.getId())
	),
	DOMAIN(
		(invoice, json) -> json.opt(IConstants.DOMAIN) != null ? invoice.setDomain(json.optInt(IConstants.DOMAIN)) : invoice,
		(invoice, company, json) -> json.put(IConstants.COMPANY, invoice.getDomain())
	),
	TYPE(
		(invoice, json) -> json.opt(IConstants.TYPE) != null ? invoice.setType(json.optEnum(InvoiceType.class, IConstants.TYPE)): invoice,
		(invoice, company, json) -> json.put(IConstants.TYPE, invoice.getType().name())
	),
	SERIES(
		(invoice, json) -> json.opt(IConstants.SERIES) != null ? invoice.setSeries(json.optString(IConstants.SERIES)): invoice,
		(invoice, company, json) -> json.put(IConstants.SERIES, invoice.getSeries())
	),
	NUMBER(
		(invoice, json) -> json.opt(IConstants.NUMBER) != null ? invoice.setNumber(json.optInt(IConstants.NUMBER)) : invoice,
		(invoice, company, json) -> json.put(IConstants.NUMBER, invoice.getNumber())
	),
	REFERENCE(
		(invoice, json) ->  json.opt(IConstants.REFERENCE) != null ? invoice.setReferenceCode(json.optString(IConstants.REFERENCE)) : invoice,
		(invoice, company, json) -> json.put(IConstants.REFERENCE, invoice.getReferenceCode())
	),
	DATE(
		(invoice, json) ->  json.opt(IConstants.DATE) != null ? invoice.setIssueDate(AonDateUtils.dateTimeParse(json.optString(IConstants.DATE))) : invoice,
		(invoice, company, json) -> json.put(IConstants.DATE, AonDateUtils.dateTimeFormat(invoice.getIssueDate()))
	),
	TRANSACTION(
		(invoice, json) ->  json.opt(IConstants.TRANSACTION) != null ? invoice.setTransaction(json.optEnum(InvoiceTransactionType.class, IConstants.TRANSACTION)) : invoice,
		(invoice, company, json) -> json.put(IConstants.TRANSACTION, invoice.getTransaction().name())
	),
	CATEGORY(
		(invoice, json) -> invoice,
		(invoice, company, json) -> json.put(IConstants.CATEGORY, invoice.getDetails().get(0).getAccount())
	),
	TOTAL(
		(invoice, json) ->  json.opt(IConstants.TOTAL) != null ? invoice.setTotal(json.optDouble(IConstants.TOTAL)) : invoice,
		(invoice, company, json) -> json.put(IConstants.TOTAL, invoice.getTotal())
	),
	SENDER(
		(invoice, json) -> {
			InvoiceType  invoiceType = json.optEnum(InvoiceType.class, IConstants.TYPE);
			JSONObject jsonSender = json.optJSONObject(IConstants.SENDER);
			if(!InvoiceType.SALES.equals(invoiceType) && jsonSender != null) {
				Registry registry = AonRegistryJSON.fromJSON(jsonSender);
				invoice
				.setRegistry(registry.getId())
				.setRegistryDocument(registry.getDocument())
				.setRegistryDocumentCountry(registry.getDocumentCountry())
				.setRegistryDocumentType(registry.getDocumentType())
				.setRegistryName(registry.getName())
				.setRegistryAddress(registry.getMainAddress().getId());
			}
			return invoice;
		},
		(invoice, company, json) -> {
			InvoiceType  invoiceType = json.optEnum(InvoiceType.class, IConstants.TYPE);
			if(!InvoiceType.SALES.equals(invoiceType)) {
				Registry registry = new Registry()
					.setId(invoice.getRegistry())
					.setDocument(invoice.getRegistryDocument())
					.setName(invoice.getRegistryName())
					.setDocumentCountry(invoice.getRegistryDocumentCountry())
					.setDocumentType(invoice.getRegistryDocumentType())
					.setMainAddress(new RAddress()
							.setAddress(invoice.getAddress())
							.setCity(invoice.getAddressTown())
							.setGeozone(invoice.getAddressGeozone())
							.setGeozoneCode(invoice.getAddressProvinceCode())
							.setGeozoneName(invoice.getAddressProvince())
							.setZip(invoice.getAddressZIP())
							.setStreet_type(invoice.getAddressStreetType() != null ? invoice.getAddressStreetType().getAeatCode(): null)
							.setNumber(invoice.getAddressNumber())
							.setCountry(invoice.getRegistryDocumentCountry()));
				return json.put(IConstants.SENDER, AonRegistryJSON.toJSON(registry));
			} else {
				return json.put(IConstants.SENDER, AonRegistryJSON.toJSON(company));
			}
		}
	),
	RECEIVER(
		(invoice, json) -> {
			InvoiceType  invoiceType = json.optEnum(InvoiceType.class, IConstants.TYPE);
			JSONObject jsonReceiver = json.optJSONObject(IConstants.RECEIVER);
			if(InvoiceType.SALES.equals(invoiceType) && jsonReceiver != null) {
				Registry registry = AonRegistryJSON.fromJSON(jsonReceiver);
				invoice
					.setRegistry(registry.getId())
					.setRegistryDocument(registry.getDocument())
					.setRegistryDocumentCountry(registry.getDocumentCountry())
					.setRegistryDocumentType(registry.getDocumentType())
					.setRegistryName(registry.getName())
					.setRegistryAddress(registry.getMainAddress().getId());
			}
			return invoice;
		},
		(invoice, company, json) -> {
			InvoiceType  invoiceType = json.optEnum(InvoiceType.class, IConstants.TYPE);
			if(InvoiceType.SALES.equals(invoiceType)) {
				Registry registry = new Registry()
					.setId(invoice.getId())
					.setDocument(invoice.getRegistryDocument())
					.setName(invoice.getRegistryName())
					.setDocumentCountry(invoice.getRegistryDocumentCountry())
					.setDocumentType(invoice.getRegistryDocumentType())
					.setMainAddress(new RAddress()
						.setAddress(invoice.getAddress())
						.setCity(invoice.getAddressTown())
						.setGeozone(invoice.getAddressGeozone())
						.setGeozoneCode(invoice.getAddressProvinceCode())
						.setGeozoneName(invoice.getAddressProvince())
						.setZip(invoice.getAddressZIP())
						.setStreet_type(invoice.getAddressStreetType() != null ? invoice.getAddressStreetType().getAeatCode(): null)
						.setNumber(invoice.getAddressNumber())
						.setCountry(invoice.getRegistryDocumentCountry()));
				return json.put(IConstants.RECEIVER, AonRegistryJSON.toJSON(registry));
			} else {
				return json.put(IConstants.RECEIVER, AonRegistryJSON.toJSON(company));
			}
		}
	),
	DETAILS(
		(invoice, json) -> {
			JSONArray details = json.optJSONArray(IConstants.DETAILS);
			if (details != null) {
				invoice.setDetails(new LinkedList<InvoiceDetail>());
				for (Object detail : details) {
					invoice.getDetails().add(AonInvoiceDetailJSON.fromJSON((JSONObject) detail , json.optString(IConstants.CATEGORY)));
				}
			}
			return invoice;
		},
		(invoice, company, json) -> invoice.getDetails() == null ? json
			: json.put(IConstants.DETAILS,(Collection<JSONObject>)
						invoice.getDetails()
							.stream()
							.map(t -> AonInvoiceDetailJSON.toJSON(t))
							.collect(Collectors.toCollection(LinkedList::new)))
	),
	TAXES(
		(invoice, json) -> {
			JSONArray taxes = json.optJSONArray(IConstants.TAXES);
			if (taxes != null) {
				invoice.setBreakdown(new LinkedList<InvoiceBreakdown>());
				for (Object tax : taxes) {
					invoice.getBreakdown().add(AonInvoiceTaxJSON.fromJSON((JSONObject) tax));
				}
			}
			return invoice;
		},
		(invoice, company, json) -> invoice.getBreakdown() == null ? json
				: json.put(IConstants.TAXES, (Collection<JSONObject>)
						invoice.getBreakdown()
							.stream()
							.map(t -> AonInvoiceTaxJSON.toJSON(t))
							.collect(Collectors.toCollection(LinkedList::new)))),
	FINANCES(
		(invoice, json) -> {
			JSONArray finances = json.optJSONArray(IConstants.FINANCES);
			if (finances != null) {
				invoice.setFinances(new LinkedList<Finance>());
				for (Object finance : finances) {
					invoice.getFinances().add(AonFinanceJSON.fromJSON((JSONObject) finance));
				}
			}
			return invoice;
		},
		(invoice, company, json) -> invoice.getFinances() == null ? json
			: json.put(IConstants.FINANCES, (Collection<JSONObject>)
					invoice.getFinances()
					.stream()
					.map(t -> AonFinanceJSON.toJSON(t))
					.collect(Collectors.toCollection(LinkedList::new)))
	),
//	FILE(
//		(aonCtx, invoice, json) -> {
//			JSONObject jsonFile = json.optJSONObject(IConstants.FILE);
//			return (jsonFile != null) ? invoice.setFile(TediInvoiceFileJSON.fromJSON(jsonFile)) : invoice;
//		},
//		(aonCtx, invoice, json) -> (invoice.getFile() != null) ? json.put(IConstants.FILE, TediInvoiceFileJSON.toJSON(invoice.getFile())) : json
//	),
	STATUS(
		(invoice, json) -> json.opt(IConstants.STATUS) != null ? invoice.setStatus(json.optEnum(InvoiceStatus.class, IConstants.STATUS).value()) : invoice,
		(invoice, company, json) -> json.put(IConstants.STATUS, InvoiceStatus.safeValueOf(invoice.getStatus()))
	),
	COMMENTS(
		(invoice, json) -> {
			JSONArray jsonComments = json.optJSONArray(IConstants.COMMENTS);
			if (jsonComments != null) {
				invoice.setComments(jsonComments.toString());
			}
			return invoice;
		},
		(invoice, company, json) -> json
//			invoice.getComments() == null ? json : json.put(IConstants.COMMENTS, new JSONArray(invoice.getComments()))
		);

	private IAonInvoiceFromJSON fromJSON;
	private IAonInvoiceToJSON toJSON;

	private AonInvoiceJSON(IAonInvoiceFromJSON fromJSON, IAonInvoiceToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}

	public static JSONObject toJSON(Invoice t, Company company) {
		JSONObject json = new JSONObject();
		for (AonInvoiceJSON p : AonInvoiceJSON.values()) {
			p.toJSON.to(t, company, json);
		}
		return json;
	}

	public static Invoice fromJSON(JSONObject json) {
		return fromJSON(json, null);
	}

	public static Invoice fromJSON(JSONObject json, Invoice invoice) {
		if(invoice == null) {
			invoice = new Invoice();
		}
		if (json != null) {
			for (AonInvoiceJSON p : AonInvoiceJSON.values()) {
				p.fromJSON.from(invoice, json);
			}
		}
		return invoice;
	}
}
