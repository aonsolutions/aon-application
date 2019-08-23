package com.esferalia.aon.gwt.fiscal.client.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class TediInvoiceViewer extends SimpleLayoutPanel {

	private static final String DATE_PATTERN = "dd/MM/yyyy";
	private FlexTable tab = new FlexTable();
	
	public  TediInvoiceViewer (Invoice invoice) {
		super();
		ScrollPanel scroll = new ScrollPanel();
		tab = new FlexTable();	
		tab.setStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		tab.addStyleName(AON.AON_CSS.aonWidth90Percent());
		
		tab.getColumnFormatter().setWidth(0, "100px");
		tab.getColumnFormatter().setWidth(1, "100px");
		
		tab.getColumnFormatter().setWidth(2, "150px");
		tab.getColumnFormatter().setWidth(3, "100px");
		
		tab.getColumnFormatter().setWidth(4, "100px");
		tab.getColumnFormatter().setWidth(5, "auto");
		
		
		int row = 0;
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFontBig());
		tab.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextLeft());
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.setWidget(row, 0, new Label("Factura de " + invoice.getType().getDescription()));
		
		String attributes = "[" + (invoice.getTransaction() != null ? invoice.getTransaction().getDescription() :"") + "]";
		attributes = attributes + (invoice.isService()? "[" + AON.MSG.service()+ "]":" ");
		attributes = attributes + (invoice.getRectificationType() != null && invoice.getRectificationType() != RectificationType.NONE 
				? "[" + invoice.getRectificationType().getDescription() + "]":" ");
		attributes = attributes + (invoice.isVatAccrualPayment()? "[" + AON.MSG.vatAccrualPayment() + "]":" ");
		attributes = attributes + (invoice.isWithholdingFarmer()? "[" + AON.MSG.withholdingFarmer() + "]":" ");
		attributes = attributes + (invoice.isInvestment()? "[" + AON.MSG.investAsset() + "]":" ");
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().setColSpan(row, 1, 4);
		tab.setWidget(row, 1, new Label(attributes));
		row++;
		
		tab.setWidget(row, 0, new Label(""));
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPaddingTop());
		row++;
		
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.titular()));

		String registry = "";
		if (invoice.getRegistryDocumentCountry()!= null && invoice.getRegistryDocumentCountry() != Country.ES) {
			registry = invoice.getRegistryDocumentCountry().getIso2() + "/"; 
		}
		registry = registry + invoice.getRegistryDocument();
		registry = registry + " - " + invoice.getRegistryName();
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.getFlexCellFormatter().setColSpan(row, 1, 3);
		tab.setWidget(row, 1, new Label(registry));
		
		tab.getCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 2, new Label(AON.MSG.issueDate()));
		
		tab.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 3, new Label(invoice.getIssueDate()==null?"SIN FECHA":DateTimeFormat.getFormat(DATE_PATTERN).format(invoice.getIssueDate())));
		row++;

		
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		if (invoice.isSales()) {
			tab.setWidget(row, 0, new Label( "N/Fra"));
			tab.setWidget(row, 1, new Label( invoice.getSeries() + "/" + invoice.getNumber() ));
		} else {
			tab.setWidget(row, 0, new Label( "S/Fra"));
			tab.setWidget(row, 1, new Label(invoice.getReferenceCode()));
		}
		
		tab.getCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 2, new Label(AON.MSG.document()));
		
		tab.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 3, new Label(invoice.getDocumentNumber()));
		
		tab.getCellFormatter().setStyleName(row, 4, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 4, new Label(AON.MSG.invoiceTotal()));
		
		tab.getCellFormatter().setStyleName(row, 5, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextRight());
		tab.getCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBold());
		tab.setWidget(row, 5, new Label(AON.FMT.format( invoice.getTotal())));
		row++;

		tab.setWidget(row, 0, new Label(""));
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPaddingTop());
		row++;
		
		FlexTable detTab = new FlexTable();
		detTab.getColumnFormatter().setWidth(0, "30px");
		detTab.getColumnFormatter().setWidth(1, "auto");
		detTab.getColumnFormatter().setWidth(2, "80px");
		detTab.getColumnFormatter().setWidth(3, "80px");
		detTab.getColumnFormatter().setWidth(4, "80px");
		detTab.getColumnFormatter().setWidth(5, "80px");
		detTab.getColumnFormatter().setWidth(6, "100px");
		
		detTab.setStyleName(AON.AON_CSS.aonBlockCenter());
		detTab.addStyleName(AON.AON_CSS.aonPanelGrid());
		detTab.addStyleName(AON.AON_CSS.aonWidthAll());
		tab.getFlexCellFormatter().setColSpan(row, 0, 6);
		tab.setWidget(row, 0, detTab);
		
		detTab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonDataTableHeader());
		detTab.setWidget(row, 0, new Label( "#" ));
		
		detTab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonDataTableHeader());
		detTab.setWidget(row, 1, new Label( AON.MSG.description() ));
		
		detTab.getCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonDataTableHeader());
		detTab.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextRight());
		detTab.setWidget(row, 2, new Label( AON.MSG.quantity() ));
		
		detTab.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonDataTableHeader());
		detTab.getCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextRight());
		detTab.setWidget(row, 3, new Label( AON.MSG.price() ));
		
		detTab.getCellFormatter().setStyleName(row, 4, AON.AON_CSS.aonDataTableHeader());
		detTab.getCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextRight());
		detTab.setWidget(row, 4, new Label( AON.MSG.discount() ));
		
		detTab.getCellFormatter().setStyleName(row, 5, AON.AON_CSS.aonDataTableHeader());
		detTab.getCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextRight());
		detTab.setWidget(row, 5, new Label( "IVA" ));
		
		detTab.getCellFormatter().setStyleName(row, 6, AON.AON_CSS.aonDataTableHeader());
		detTab.getCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonTextRight());
		detTab.setWidget(row, 6, new Label( AON.MSG.amount() ));
		row++;
		if (invoice.getDetails() != null && !invoice.getDetails().isEmpty()) {
			for (InvoiceDetail detail : invoice.getDetails()) {
				detTab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
				detTab.setWidget(row, 0, new Label( "" + detail.getLine() ));
				
				detTab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
				detTab.setWidget(row, 1, new Label(detail.getDescription()));
				
				detTab.getCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonPanelGridEven());
				detTab.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextRight());
				detTab.setWidget(row, 2, new Label(AON.FMT.format( detail.getQuantity())));
				
				detTab.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonPanelGridEven());
				detTab.getCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextRight());
				detTab.setWidget(row, 3, new Label(AON.FMT.format( detail.getPrice())));
				
				detTab.getCellFormatter().setStyleName(row, 4, AON.AON_CSS.aonPanelGridEven());
				detTab.getCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextRight());
				detTab.setWidget(row, 4, new Label(detail.getDiscountExpression()));
				
				detTab.getCellFormatter().setStyleName(row, 5, AON.AON_CSS.aonPanelGridEven());
				detTab.getCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextRight());
				String vat = "";
				if (detail.getInvoiceTaxes() != null && !detail.getInvoiceTaxes().isEmpty()) {
					InvoiceTax tax = detail.getInvoiceTaxes().get(0);
					if (tax.getTaxType() == TaxType.VAT) {
						vat = AON.FMT.format( tax.getPercentage()) + "%";
					}
				}
				detTab.setWidget(row, 5, new Label(vat));

				detTab.getCellFormatter().setStyleName(row, 6, AON.AON_CSS.aonPanelGridEven());
				detTab.getCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonTextRight());
				detTab.setWidget(row, 6, new Label(AON.FMT.format( detail.getTaxableBase())));
				row++;
			}
		} else {
			detTab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
			detTab.getFlexCellFormatter().setColSpan(row, 0, 6);
			detTab.setWidget(row, 0, new Label( AON.MSG.noData()));
		}
		
		tab.setWidget(row, 0, new Label(""));
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPaddingTop());
		row++;
		
		FlexTable taxTab = new FlexTable();
		taxTab.setStyleName(AON.AON_CSS.aonPanelGrid());
		taxTab.addStyleName(AON.AON_CSS.aonBlockRight());
		
		taxTab.getColumnFormatter().setWidth(0, "150px");
		taxTab.getColumnFormatter().setWidth(1, "150px");
		taxTab.getColumnFormatter().setWidth(2, "100px");
		taxTab.getColumnFormatter().setWidth(3, "150px");
		
		tab.getFlexCellFormatter().setColSpan(row, 0, 6);
		tab.setWidget(row, 0, taxTab);

		taxTab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonDataTableHeader());
		taxTab.setWidget(row, 0, new Label());
		
		taxTab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonDataTableHeader());
		taxTab.getCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextRight());
		taxTab.setWidget(row, 1, new Label(AON.MSG.taxableBase()));
		
		taxTab.getCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonDataTableHeader());
		taxTab.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextRight());		
		taxTab.setWidget(row, 2, new Label(AON.MSG.percent()));
		taxTab.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonDataTableHeader());
		taxTab.getCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextRight());
		taxTab.setWidget(row, 3, new Label(AON.MSG.quota()));
		row++;
		
		if (invoice.getBreakdown() != null && !invoice.getBreakdown().isEmpty()) {
			for (InvoiceBreakdown detail : invoice.getBreakdown()) {
				taxTab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
				String typeLabel = detail.getTaxType().getName();
				String percent = AON.FMT.format( detail.getPercentage())+"%";
				double quota = detail.getQuota(); 
				if (invoice.isSurcharge() && detail.getTaxType() == TaxType.VAT && AonMathUtils.isNotZero( detail.getSurcharge() )) {
					typeLabel = typeLabel + " + R.E.";
					percent = percent + " + " + AON.FMT.format( detail.getSurcharge())+"%";
					quota = AonMathUtils.round( quota + detail.getSurchargeQuota() );	
				}
				taxTab.setWidget(row, 0, new Label( typeLabel ));	
				taxTab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
				taxTab.getCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextRight());
				taxTab.setWidget(row, 1, new Label(AON.FMT.format( detail.getBase())));
				taxTab.getCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonPanelGridEven());
				taxTab.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextRight());
				taxTab.setWidget(row, 2, new Label(percent));
				taxTab.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonPanelGridEven());
				taxTab.getCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextRight());
				taxTab.setWidget(row, 3, new Label(AON.FMT.format( quota )));
				row++;
			}
		} else {
			taxTab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
			taxTab.getFlexCellFormatter().setColSpan(row, 0, 4);
			taxTab.setWidget(row, 0, new Label( AON.MSG.noData()));
		}
		scroll.setWidget(tab);
		setWidget(scroll);
	}
}
