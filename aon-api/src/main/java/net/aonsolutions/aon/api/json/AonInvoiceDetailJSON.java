package net.aonsolutions.aon.api.json;

import java.util.regex.Pattern;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.util.AonMathUtils;

import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.api.json.FunctionalInterfaces.IAonInvoiceDetailFromJSON;
import net.aonsolutions.aon.api.json.FunctionalInterfaces.IAonInvoiceDetailToJSON;

public enum AonInvoiceDetailJSON {

	ID(
		(detail, json) -> detail.setId(json.optInt(IConstants.ID)),
		(detail, json) -> json.put(IConstants.ID, detail.getId())
	),
	DESCRIPTION(
		(detail, json) -> detail.setDescription(json.optString(IConstants.DESCRIPTION)),
		(detail, json) -> json.put(IConstants.DESCRIPTION, detail.getDescription())
	),
	QUANTITY(
		(detail, json) -> detail.setQuantity(json.optDouble(IConstants.QUANTITY)),
		(detail, json) -> json.put(IConstants.QUANTITY, detail.getQuantity())
	),
	PRICE(
		(detail, json) -> detail.setPrice(json.optDouble(IConstants.PRICE)),
		(detail, json) -> json.put(IConstants.PRICE, detail.getPrice())
	),
	DISCOUNT(
		(detail, json) -> detail.setDiscountExpression(json.optString(IConstants.DISCOUNT)),
		(detail, json) -> json.put(IConstants.DISCOUNT, AonInvoiceDetailJSON.calculateDiscountExpression(detail))
	),
	AMOUNT(
		(detail, json) -> detail.setTaxableBase(json.optDouble(IConstants.AMOUNT)),
		(detail, json) -> json.put(IConstants.AMOUNT, detail.getTaxableBase())
	),
	VAT(
		(detail, json) -> {
			Boolean prepayment = json.optBoolean(IConstants.PREPAYMENT);
			if(!prepayment) {
				Double amount = json.optDouble(IConstants.AMOUNT);
				Double percentage = json.optDouble(IConstants.VAT);
				detail.addInvoiceTax(new InvoiceTax()
						.setTaxType(TaxType.VAT)
						.setBase(amount)
						.setPercentage(percentage)
						.setQuota(AonMathUtils.round(amount * percentage / 100)))
						// TODO Rellenar los datos que faltan
//						.setSurcharge(vat.getSurcharge())
//						.setSurchargeQuota(vat.getSurchargeQuota())
//						.setVatDeductionType(vat.getVatDeductionType())
//						.setDeductiblePercent(vat.getDeductiblePercent())
//						.setDeductibleQuota(vat.getDeductibleQuota())
//						.setAccount(accInvoice.isSales() ? vat.getOutputAccountId() : vat.getInputAccountId() )
				;
			}
			return detail;
		},
		(detail, json) -> {
			InvoiceTax it =  detail.getInvoiceTaxes() != null
					? detail.getInvoiceTaxes().stream().filter(f -> f.getTaxType() != null && TaxType.VAT.equals(f.getTaxType())).findFirst().orElse(new InvoiceTax())
					: new InvoiceTax();
			return json.put(IConstants.VAT, it.getPercentage());
		}
	),
	SURCHARGE(
		(detail, json) -> detail.setSurcharge(json.optDouble(IConstants.SURCHARGE)),
		(detail, json) -> json.put(IConstants.SURCHARGE, detail.getSurcharge())
	),
	ACCOUNT(
		(detail, json) -> {
			Account account = AonAccountJSON.fromJSON(json.optJSONObject(IConstants.ACCOUNT));
			detail.setAccount(account.getId());
			detail.setAccountCode(account.getCode());
			detail.setAccountDescription(account.getDescription());
			return detail;
		},
		(detail, json) -> json.put(IConstants.SURCHARGE, detail.getSurcharge())
	),
	PREPAYMENT(
		(detail, json) -> detail.setPrepayment(json.optBoolean(IConstants.PREPAYMENT)),
		(detail, json) -> json.put(IConstants.PREPAYMENT, detail.isPrepayment())
	),
	WITHHOLDING(
		(detail, json) -> {
			Boolean prepayment = json.optBoolean(IConstants.PREPAYMENT);
			Boolean withholding = json.optBoolean(IConstants.WITHHOLDING);

			if(!prepayment && withholding) {
				Double amount = json.optDouble(IConstants.AMOUNT);
				Double percentage = json.optDouble(IConstants.RETENTION);
				detail.addInvoiceTax(new InvoiceTax()
						.setTaxType(TaxType.RETENTION)
						.setBase(amount)
						.setPercentage(percentage)
						.setQuota(AonMathUtils.round(amount * percentage / 100)))
						// TODO Rellenar los datos que faltan
//						.setWithholdingType(accInvoice.getWithholdingData().getWithholdingType())
//						.setAccount(accInvoice.getWithholdingData().getAccountId()))
				;
			}
			return detail;
		},
		(detail, json) -> {
			Boolean  withholding = detail.getInvoiceTaxes() != null 
					? detail.getInvoiceTaxes().stream().filter(f -> TaxType.RETENTION.equals(f.getTaxType())).count() > 0 : false;
			return json.put(IConstants.WITHHOLDING, withholding);
		}
	);

	private IAonInvoiceDetailFromJSON fromJSON;
	private IAonInvoiceDetailToJSON toJSON;

	private AonInvoiceDetailJSON(IAonInvoiceDetailFromJSON fromJSON, IAonInvoiceDetailToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}


	public static JSONObject toJSON(InvoiceDetail t) {
		JSONObject json = new JSONObject();
		for (AonInvoiceDetailJSON p : AonInvoiceDetailJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}

	public static InvoiceDetail fromJSON(JSONObject json, String category) {
		InvoiceDetail detail = new InvoiceDetail();
		if (json != null) {
			for (AonInvoiceDetailJSON p : AonInvoiceDetailJSON.values()) {
				p.fromJSON.from(detail, json);
			}
		}
		return detail;	
	}
	
	public static Double calculateDiscountExpression(InvoiceDetail id) {
		Pattern PATTERN = Pattern.compile("\\+");
		String[] arr = PATTERN.split(id.getDiscountExpression());
    	Double discount = 0.0;
		for (int i = 0; i < arr.length; i++) {
        	discount =  discount + Double.parseDouble(arr[i].trim());
    	}
		return AonMathUtils.round(discount);
	}
}
