package net.aonsolutions.occam.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.constants.InvoiceSource;
import net.aonsolutions.occam.api.invoicing.InvoiceDetail;
import net.aonsolutions.watson.client.util.AonCollectionUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class InvoiceDetailJSON {
	
	private InvoiceDetailJSON() {
	}
	
	public static List<InvoiceDetail> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AonJSONUtils.stream(array)
			.map(InvoiceDetailJSON::from)
			.toList();		
	}
	
	public static InvoiceDetail from(JSONObject json) {
		if (json == null) return null; 
		return new InvoiceDetail()
			.setId(AonJSONUtils.getInteger(json, AonNames.ID))
			.setItem(AonJSONUtils.getInteger(json, AonNames.ITEM))
			.setLine(AonJSONUtils.getInteger(json, AonNames.LINE))
			.setDescription(AonJSONUtils.getString(json, AonNames.DESCRIPTION))
			.setQuantity(AonJSONUtils.getDouble(json, AonNames.QUANTITY))
			.setPrice(AonJSONUtils.getDouble(json, AonNames.PRICE))
			.setDiscountExpression(AonJSONUtils.getString(json, AonNames.DISCOUNT))
			.setTaxableBase(AonJSONUtils.getDouble(json, AonNames.TAXABLE_BASE))
			.setPrepayment(AonJSONUtils.getBoolean(json, AonNames.PREPAYMENT))
			.setSource( InvoiceSource.safeValueOf(AonJSONUtils.getString(json, AonNames.SOURCE)).orElse(null) )
			.setSourceId(AonJSONUtils.getInteger(json, AonNames.SOURCE_ID))
			.setTaxes(InvoiceTaxJSON.from(AonJSONUtils.getArray(json, AonNames.TAXES)))
			.setAudit( AuditJSON.from(AonJSONUtils.getObject(json, AonNames.AUDIT)))
		;
	}
	
	public static JSONArray to(List<InvoiceDetail> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<InvoiceDetail> stream) {
		return stream
			.map(InvoiceDetailJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(InvoiceDetail invoice) {
		if (invoice == null) return null;
		return new JSONObject()
			.put(AonNames.ID, invoice.getId())
			.putOpt(AonNames.ITEM, invoice.getItem())
			.putOpt(AonNames.LINE, invoice.getLine())
			.putOpt(AonNames.DESCRIPTION, invoice.getDescription())
			.putOpt(AonNames.QUANTITY, invoice.getQuantity())
			.putOpt(AonNames.PRICE, invoice.getPrice())
			.putOpt(AonNames.DISCOUNT, invoice.getDiscountExpression())
			.putOpt(AonNames.TAXABLE_BASE, invoice.getTaxableBase())
			.putOpt(AonNames.PREPAYMENT, invoice.isPrepayment())
			.putOpt(AonNames.SOURCE, AonObjectUtils.ifNotNullGet(invoice.getSource(), Object::toString ))
			.putOpt(AonNames.SOURCE_ID, invoice.getSourceId())
			.putOpt(AonNames.TAXES, InvoiceTaxJSON.to(invoice.getTaxes().orElse(null)))
			.putOpt(AonNames.AUDIT, AuditJSON.to(invoice.getAudit().orElse(null)))
		;
	}
}
