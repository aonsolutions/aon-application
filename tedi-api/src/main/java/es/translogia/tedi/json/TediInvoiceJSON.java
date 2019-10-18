package es.translogia.tedi.json;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import es.translogia.tedi.ewok.IConstants;
import es.translogia.tedi.ewok.TediComments;
import es.translogia.tedi.ewok.TediFinance;
import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceCategory;
import es.translogia.tedi.ewok.TediInvoiceDetail;
import es.translogia.tedi.ewok.TediInvoiceStatus;
import es.translogia.tedi.ewok.TediInvoiceTax;
import es.translogia.tedi.ewok.TediInvoiceTransaction;
import es.translogia.tedi.ewok.TediInvoiceType;
import es.translogia.tedi.json.FunctionalInterfaces.ITediInvoiceFromJSON;
import es.translogia.tedi.json.FunctionalInterfaces.ITediInvoiceToJSON;

public enum TediInvoiceJSON {

	UUID(
		(invoice, json) -> invoice.setUuid(json.optString(IConstants.UUID)),
		(invoice, json) -> json.put(IConstants.UUID, invoice.getUuid())
	),
	COMPANY(
		(invoice, json) -> invoice.setCompany(json.optString(IConstants.COMPANY)),
		(invoice, json) -> json.put(IConstants.COMPANY, invoice.getCompany())
	),
	TYPE(
		(invoice, json) -> invoice.setType(json.optEnum(TediInvoiceType.class, IConstants.TYPE)),
		(invoice, json) -> json.put(IConstants.TYPE, invoice.getType())
	),
	SERIES(
		(invoice, json) -> invoice.setSeries(json.optString(IConstants.SERIES)),
		(invoice, json) -> json.put(IConstants.SERIES, invoice.getSeries())
	),
	NUMBER(
		(invoice, json) -> invoice.setNumber(TediJSONUtils.optInteger(json, IConstants.NUMBER)),
		(invoice, json) -> json.put(IConstants.NUMBER, invoice.getNumber())
	),
	REFERENCE(
		(invoice, json) -> invoice.setReference(json.optString(IConstants.REFERENCE)),
		(invoice, json) -> json.put(IConstants.REFERENCE, invoice.getReference())
	),
	DATE(
		(invoice, json) -> invoice.setDate(TediJSONUtils.parseDate(json.optString(IConstants.DATE))),
		(invoice, json) -> TediJSONUtils.put(json, IConstants.DATE, invoice.getDate())
	),
	TRANSACTION(
		(invoice, json) -> invoice.setTransaction(json.optEnum(TediInvoiceTransaction.class, IConstants.TRANSACTION)),
		(invoice, json) -> json.put(IConstants.TRANSACTION, invoice.getTransaction())
	),
	CATEGORY(
		(invoice, json) -> invoice.setCategory(json.optEnum(TediInvoiceCategory.class, IConstants.CATEGORY)),
		(invoice, json) -> json.put(IConstants.CATEGORY, invoice.getCategory())
	),
	TOTAL(
		(invoice, json) -> invoice.setTotal(TediJSONUtils.optDouble(json, IConstants.TOTAL)),
		(invoice, json) -> json.put(IConstants.TOTAL, invoice.getTotal())
	),
	RDOCUMENT(
		(invoice, json) -> invoice.setRdocument(json.optString(IConstants.RDOCUMENT)),
		(invoice, json) -> json.put(IConstants.RDOCUMENT, invoice.getRdocument())
	),
	RNAME(
		(invoice, json) -> invoice.setRname(json.optString(IConstants.RNAME)),
		(invoice, json) -> json.put(IConstants.RNAME, invoice.getRname())
	),
	SENDER(
		(invoice, json) -> {
			JSONObject jsonSender = json.optJSONObject(IConstants.SENDER);
			return (jsonSender != null) ? invoice.setSender(TediRegistryJSON.fromJSON(jsonSender)) : invoice;
		}, 
		(invoice, json) -> (invoice.getSender() != null)
			? json.put(IConstants.SENDER, TediRegistryJSON.toJSON(invoice.getSender()))
			: json
	),
	RECEIVER(
		(invoice, json) -> {
			JSONObject jsonReceiver = json.optJSONObject(IConstants.RECEIVER);
			return (jsonReceiver != null) ? invoice.setReceiver(TediRegistryJSON.fromJSON(jsonReceiver)) : invoice;
		}, 
		(invoice, json) -> (invoice.getReceiver() != null)
			? json.put(IConstants.RECEIVER, TediRegistryJSON.toJSON(invoice.getReceiver()))
			: json
	),
	DETAILS(
		(invoice, json) -> {
			JSONArray details = json.optJSONArray(IConstants.DETAILS);
			if (details != null) {
				invoice.setDetails(new LinkedList<TediInvoiceDetail>());
				for (Object detail : details) {
					invoice.getDetails().add(TediInvoiceDetailJSON.fromJSON((JSONObject) detail));
				}
			}
			return invoice;
		}, 
		(invoice, json) -> invoice.getDetails() == null ? json
			: json.put(IConstants.DETAILS,(Collection<JSONObject>) 
						invoice.getDetails()
							.stream()
							.map(t -> TediInvoiceDetailJSON.toJSON(t))
							.collect(Collectors.toCollection(LinkedList::new)))
	),
	TAXES(
		(invoice, json) -> {
			JSONArray taxes = json.optJSONArray(IConstants.TAXES);
			if (taxes != null) {
				invoice.setTaxes(new LinkedList<TediInvoiceTax>());
				for (Object tax : taxes) {
					invoice.getTaxes().add(TediInvoiceTaxJSON.fromJSON((JSONObject) tax));
				}
			}
			return invoice;
		}, 
		(invoice, json) -> invoice.getTaxes() == null ? json
				: json.put(IConstants.TAXES, (Collection<JSONObject>) 
						invoice.getTaxes()
							.stream()
							.map(t -> TediInvoiceTaxJSON.toJSON(t))
							.collect(Collectors.toCollection(LinkedList::new)))),
	FINANCES(
		(invoice, json) -> {
			JSONArray finances = json.optJSONArray(IConstants.FINANCES);
			if (finances != null) {
				invoice.setFinances(new LinkedList<TediFinance>());
				for (Object finance : finances) {
					invoice.getFinances().add(TediFinanceJSON.fromJSON((JSONObject) finance));
				}
			}
			return invoice;
		}, 
		(invoice, json) -> invoice.getFinances() == null ? json
			: json.put(IConstants.FINANCES, (Collection<JSONObject>) 
					invoice.getFinances()
					.stream()
					.map(t -> TediFinanceJSON.toJSON(t))
					.collect(Collectors.toCollection(LinkedList::new)))
	),
	FILE(
		(invoice, json) -> {
			JSONObject jsonFile = json.optJSONObject(IConstants.FILE);
			return (jsonFile != null) ? invoice.setFile(TediInvoiceFileJSON.fromJSON(jsonFile)) : invoice;
		}, 
		(invoice, json) -> (invoice.getFile() != null) ? json.put(IConstants.FILE, TediInvoiceFileJSON.toJSON(invoice.getFile())) : json
	),
	STATUS(
		(invoice, json) -> invoice.setStatus(json.optEnum(TediInvoiceStatus.class, IConstants.STATUS)),
		(invoice, json) -> json.put(IConstants.STATUS, invoice.getStatus())
	),
	OLD_STATUS(
		(invoice, json) -> invoice.setOldStatus(json.optEnum(TediInvoiceStatus.class, IConstants.OLD_STATUS)),
		(invoice, json) -> json.put(IConstants.OLD_STATUS, invoice.getOldStatus())
	),
	SOURCE(
		(invoice, json) -> invoice.setSource(json.optString(IConstants.SOURCE)),
		(invoice, json) -> json.put(IConstants.SOURCE, invoice.getSource())
	),
	COMMENTS(
		(invoice, json) -> {
			JSONArray jsonComments = json.optJSONArray(IConstants.COMMENTS);
			if (jsonComments != null) {
				invoice.setComments(new LinkedList<TediComments>());
				for (Object jsonComment : jsonComments) {
					TediComments comment = TediCommentsJSON.fromJSON((JSONObject) jsonComment);
					invoice.getComments().add(comment);
				}
			}  
			return invoice;
		}, 
		(invoice, json) -> invoice.getComments() == null ? json
				: json.put(IConstants.COMMENTS, (Collection<JSONObject>) 
						invoice.getComments()
						.stream()
						.map(t -> TediCommentsJSON.toJSON(t))
						.collect(Collectors.toCollection(LinkedList::new)))
		),
	EMAIL(
		(invoice, json) -> {
			JSONObject jsonEmail = json.optJSONObject(IConstants.EMAIL);
			return (jsonEmail != null) ? invoice.setEmail(TediEmailInfoJSON.fromJSON(jsonEmail)) : invoice;
		}, 
		(invoice, json) -> (invoice.getEmail() != null)
			? json.put(IConstants.EMAIL, TediEmailInfoJSON.toJSON(invoice.getEmail()))
			: json
	);

	private ITediInvoiceFromJSON fromJSON;
	private ITediInvoiceToJSON toJSON;

	private TediInvoiceJSON(ITediInvoiceFromJSON fromJSON, ITediInvoiceToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}

	public static JSONObject toJSON(TediInvoice t) {
		JSONObject json = new JSONObject();
		for (TediInvoiceJSON p : TediInvoiceJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}

	public static TediInvoice fromJSON(JSONObject json) {
		TediInvoice emailInfo = new TediInvoice();
		if (json != null) {
			for (TediInvoiceJSON p : TediInvoiceJSON.values()) {
				p.fromJSON.from(emailInfo, json);
			}
		}
		return emailInfo;
	}
}
