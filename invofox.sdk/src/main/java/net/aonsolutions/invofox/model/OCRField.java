package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OCRField implements Serializable {

	private static final long serialVersionUID = 5236239578347983071L;
	
	private String name;
	private String prefix;
	private Integer index;
	private Integer groupIndex;
	private Integer splitIndex;

	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}

	public OCRField setName(String name) {
		this.name = name;
		return this;
	}

	public Optional<String> getPrefix() {
		return Optional.ofNullable(prefix);
	}
	public OCRField setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public Optional<Integer> getIndex() {
		return Optional.ofNullable(index);
	}
	public OCRField setIndex(Integer index) {
		this.index = index;
		return this;
	}

	public Optional<Integer> getGroupIndex() {
		return Optional.ofNullable(groupIndex);
	}
	public OCRField setGroupIndex(Integer groupIndex) {
		this.groupIndex = groupIndex;
		return this;
	}

	public Optional<Integer> getSplitIndex() {
		return Optional.ofNullable(splitIndex);
	}
	public OCRField setSplitIndex(Integer splitIndex) {
		this.splitIndex = splitIndex;
		return this;
	}

	public Optional<String> getDescription() {
		return Optional.ofNullable(getFieldsDescriptionsMap().get(name));
	}

	private static Map<String, String> getFieldsDescriptionsMap() {
	    Map<String, String> fieldNamesDescriptionsMap = new HashMap<String, String>();
	    fieldNamesDescriptionsMap.put("currency", "Divisa");
	    fieldNamesDescriptionsMap.put("language", "Idioma");
	    fieldNamesDescriptionsMap.put("isCreditNote", "isCreditNote");
	    fieldNamesDescriptionsMap.put("numberFormat", "numberFormat");
	    fieldNamesDescriptionsMap.put("invoiceRef", "Núm. factura");
	    fieldNamesDescriptionsMap.put("seriesCode", "Núm. serie");
	    fieldNamesDescriptionsMap.put("taxClass", "Tipo impositivo");
	    fieldNamesDescriptionsMap.put("issuerCountry", "País emisor");
	    fieldNamesDescriptionsMap.put("recipientCountry", "País receptor");
	    fieldNamesDescriptionsMap.put("issuerAddressDetails", "issuerAddressDetails");
	    fieldNamesDescriptionsMap.put("recipientAddressDetails", "recipientAddressDetails");
	    fieldNamesDescriptionsMap.put("documentNumber", "Núm.factura");
	    fieldNamesDescriptionsMap.put("issueDate", "Fecha emisión");
	    fieldNamesDescriptionsMap.put("issuerName", "Nombre emisor");
	    fieldNamesDescriptionsMap.put("issuerTaxId", "CIF emisor");
	    fieldNamesDescriptionsMap.put("issuerAddress", "Dirección emisor");
	    fieldNamesDescriptionsMap.put("issuerEmail", "Email emisor");
	    fieldNamesDescriptionsMap.put("issuerPhoneNumber", "Teléfono emisor");
	    fieldNamesDescriptionsMap.put("issuerWebsite", "Web emisor");
	    fieldNamesDescriptionsMap.put("recipientName", "Nombbre receptor");
	    fieldNamesDescriptionsMap.put("recipientTaxId", "CIF receptor");
	    fieldNamesDescriptionsMap.put("recipientAddress", "Dirección receptor");
	    fieldNamesDescriptionsMap.put("shippingAddress", "Dirección envío receptor");
	    fieldNamesDescriptionsMap.put("recipientEmail", "Email receptor");
	    fieldNamesDescriptionsMap.put("recipientWebsite", "Web receptor");
	    fieldNamesDescriptionsMap.put("recipientPhoneNumber", "Teléfono receptor");
	    fieldNamesDescriptionsMap.put("paymentMethod", "Método de pago");
	    fieldNamesDescriptionsMap.put("IBAN", "IBAN");
	    fieldNamesDescriptionsMap.put("SWIFT", "SWIFT");
	    fieldNamesDescriptionsMap.put("clientCode", "Núm. cliente");
	    fieldNamesDescriptionsMap.put("deliveryNoteRef", "Núm. albarán");
	    fieldNamesDescriptionsMap.put("orderRef", "Núm. pedido");
	    fieldNamesDescriptionsMap.put("contractRef", "Contrato");
	    fieldNamesDescriptionsMap.put("incoterms", "InCoTerms");
	    fieldNamesDescriptionsMap.put("documentType", "Tipo de documento");
	    fieldNamesDescriptionsMap.put("additionalNotes", "Notas adicionales");
	    fieldNamesDescriptionsMap.put("legalNotes", "Texto legal");
	    fieldNamesDescriptionsMap.put("totalTaxAmount", "Cuota");
	    fieldNamesDescriptionsMap.put("totalTaxBaseAmount", "Base imponible");
	    fieldNamesDescriptionsMap.put("totalAmount", "Total");
	    fieldNamesDescriptionsMap.put("totalGrossAmount", "Total");
	    fieldNamesDescriptionsMap.put("totalDueAmount", "Total a pagar");
	    fieldNamesDescriptionsMap.put("withholdingTaxRate", "Tipo retención (%)");
	    fieldNamesDescriptionsMap.put("withholdingTaxAmount", "Retención");
	    fieldNamesDescriptionsMap.put("totalFeesAmount", "totalFeesAmount");
	    fieldNamesDescriptionsMap.put("totalDiscountAmount", "Descuento");
	    fieldNamesDescriptionsMap.put("reimbursableExpensesAmount", "reimbursableExpensesAmount");
	    fieldNamesDescriptionsMap.put("additionalChargesAmount", "Gastos adicionales");
	    fieldNamesDescriptionsMap.put("additionalDiscountsAmount", "Descuentos adicionales");
	    fieldNamesDescriptionsMap.put("serviceAddress", "serviceAddress");
	    fieldNamesDescriptionsMap.put("supplyNumber", "supplyNumber");
	    fieldNamesDescriptionsMap.put("meterNumber", "meterNumber");
	    fieldNamesDescriptionsMap.put("totalUsage", "totalUsage");
	    fieldNamesDescriptionsMap.put("usageUnitOfMeasurement", "usageUnitOfMeasurement");

	    // lines/breakdows
	    fieldNamesDescriptionsMap.put("taxRate", "Tipo (%)");
	    fieldNamesDescriptionsMap.put("taxAmount", "Cuota");
	    fieldNamesDescriptionsMap.put("taxBaseAmount", "Base imponible");

	    return fieldNamesDescriptionsMap;
	}

}
