package com.code.aon.facturae;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddInfo;
import com.esferalia.aon.entity.IEntityAlias;

import es.mityc.facturae32.AmountType;
import es.mityc.facturae32.Facturae;
import es.mityc.facturae32.InvoiceLineType;
import es.mityc.facturae32.InvoiceLineType.TaxesOutputs.Tax;
import es.mityc.facturae32.InvoiceType;
import es.mityc.facturae32.TaxType;

public class DecimalUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DecimalUtil.class);
	
	public static final int DEFAULT_DECIMALS = 2;
	private static final int MAX_DECIMALS = 6;	
	
	private static final String EQUIVALENCE_SURCHARGE_AMOUNT = "EquivalenceSurchargeAmount";
	private static final String TAX_AMOUNT = "TaxAmount";
	private static final String TAXABLE_BASE = "TaxableBase";
	private static final String TOTAL_AMOUNT = "TotalAmount";
	private static final String TAX_TYPE_CODE = "TaxTypeCode";
	private static final String ISSUER_TRANSACTION_REFERENCE = "IssuerTransactionReference";
	
	private DecimalFormat decimalFormatter;
	
	public DecimalUtil(int numberOfDecimals) {
		String pattern = StringUtils.rightPad("0.", numberOfDecimals+2, '0');
		this.decimalFormatter = new DecimalFormat(pattern);
	}

	public static int getNumberOfDecimals( Invoice invoice ) {
		int decimals = getRegistryDecimals(invoice.getRegistry());
		if ( decimals == -1 ) {
			Integer value = AppParamUtil.getValueAsInteger(AppParam.AON_FACTURAE_DECIMALS);
			decimals = (value != null) ? value : DEFAULT_DECIMALS; 
		}
		decimals = Math.min(Math.max(DEFAULT_DECIMALS, decimals), MAX_DECIMALS);
		return decimals;
	}
	
	public static int getRegistryDecimals( Registry registry ) {
		int value = -1;
		try {
			IManagerBean bean = BeanManager.getManagerBean(RegistryAddInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_REGISTRY_ID), registry.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), AppParam.AON_FACTURAE_DECIMALS.getValue());
			criteria.addNotNullExpression(bean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_VALUE));
			criteria.addOrder(bean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_VALUE_DATE), false);
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				RegistryAddInfo addInfo = (RegistryAddInfo) list.get(0);
				value = NumberUtils.toInt(addInfo.getValue(), -1);
			}
		} catch ( ManagerBeanException e ) {
			LOGGER.error(e.getMessage(), e);
		}
		return value;
	}
	
	private List<InvoiceLineType> getInvoiceLines( Facturae facturae ) {
		InvoiceType invoice = facturae.getInvoices().getInvoice().get(0);
		return invoice.getItems().getInvoiceLine();
	}

	@SuppressWarnings("unchecked")
	private List<Element> getInvoiceLines( Document document ) {
		return document.selectNodes("//InvoiceLine");
	}

	@SuppressWarnings("unchecked")
	private List<Element> getTaxesWithheld( Element element ) {
		return element.selectNodes("TaxesWithheld/Tax");
	}

	@SuppressWarnings("unchecked")
	private List<Element> getTaxesOutputs( Element element ) {
		return element.selectNodes("TaxesOutputs/Tax");
	}
	
	private String getValue( Element element, String name ) {
		Element e = element.element(name);
		return StringUtils.trim(e.getText());		
	}
	
	private String format( double value ) {
		return decimalFormatter.format(value).replace(',', '.');	
	}
	
	private void fix( AmountType amount, Element element ) {
		if ( element != null ) {
			Element amountElement = element.element(TOTAL_AMOUNT);
			String value = format( amount.getTotalAmount() );
			LOGGER.debug("Changing amount: {} -> {}", amountElement.getText(), value);
			amountElement.setText(value);			
		}
	}
	
	private void fixTax( Tax tax, Element taxElement ) {
		String ttc = getValue(taxElement, TAX_TYPE_CODE);
		if ( StringUtils.equals(ttc, tax.getTaxTypeCode()) ) {
			fix( tax.getTaxableBase(), taxElement.element(TAXABLE_BASE) );
			fix( tax.getTaxAmount(), taxElement.element(TAX_AMOUNT) );
			fix( tax.getEquivalenceSurchargeAmount(), taxElement.element(EQUIVALENCE_SURCHARGE_AMOUNT) );
		} else {
			LOGGER.error( "Element for Tax {} not found", tax.getTaxTypeCode() );
		}
	}

	private void fixTax( TaxType tax, Element taxElement ) {
		String ttc = getValue(taxElement, TAX_TYPE_CODE);
		if ( StringUtils.equals(ttc, tax.getTaxTypeCode()) ) {
			fix( tax.getTaxableBase(), taxElement.element(TAXABLE_BASE) );
			fix( tax.getTaxAmount(), taxElement.element(TAX_AMOUNT) );
		} else {
			LOGGER.error( "Element for Tax {} not found", tax.getTaxTypeCode() );
		}		
	}
	
	private Element getLineElement( List<Element> lines, InvoiceLineType line ) {
		int index = -1;
		for( int i = 0; i < lines.size(); i++ ) {
			Element element = lines.get(i);
			String itr = getValue(element, ISSUER_TRANSACTION_REFERENCE);
			if ( StringUtils.equals(itr, line.getIssuerTransactionReference()) ) {
				index = i;
				break;
			}
		}
		if ( index != -1 ) {
			return lines.remove(index);
		}
		return null;
	}
	
	private void fixDecimals( Facturae facturae, Document document ) {
		List<Element> lines = getInvoiceLines(document);
		for( InvoiceLineType line : getInvoiceLines(facturae) ) {
			Element lineElement = getLineElement(lines, line);
			if ( lineElement != null ) {
				if ( line.getTaxesOutputs() != null ) {
					List<Element> taxes = getTaxesOutputs(lineElement);
					int n = 0;
					for( Tax tax : line.getTaxesOutputs().getTax() ) {
						fixTax(tax, taxes.get(n++));
					}					
				}
				if ( line.getTaxesWithheld() != null ) {
					List<Element> taxes = getTaxesWithheld(lineElement);
					int n = 0;
					for( TaxType tax : line.getTaxesWithheld().getTax() ) {
						fixTax(tax, taxes.get(n++));
					}					
				}
			} else {
				LOGGER.error( "Element for InvoiceLine {} not found", line.getIssuerContractReference());
			}
		}
	}
	
	public void transform( Facturae facturae, String fileName ) throws DocumentException, IOException {
		SAXReader reader = new SAXReader();
		File file = new File(fileName);
		Document document = reader.read(file);
		fixDecimals(facturae, document);
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setNewLineAfterDeclaration(false);
		XmlWriter writer = new XmlWriter( new FileWriter(file), format );
		writer.write( document );		
		writer.close();
	}
	
}
