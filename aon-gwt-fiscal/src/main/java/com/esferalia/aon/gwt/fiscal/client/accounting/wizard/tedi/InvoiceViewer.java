package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

/**
 * @deprecated use AonInvoiceViewer
 *
 */
@Deprecated
public class InvoiceViewer extends SimpleLayoutPanel {

	private static final String DATE_PATTERN = "dd/MM/yyyy";
	private static NumberFormat FORMATTER = NumberFormat.getDecimalFormat();  

	public  InvoiceViewer (Invoice invoice) {
		super();
		ScrollPanel scroll = new ScrollPanel();
		
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		tab.addStyleName(AON.AON_CSS.aonWidth90Percent());
		
		tab.getColumnFormatter().setWidth(0, "50px");
		tab.getColumnFormatter().setWidth(1, "auto");
		
		tab.getColumnFormatter().setWidth(2, "100px");
		tab.getColumnFormatter().setWidth(3, "150px");
		
		tab.getColumnFormatter().setWidth(4, "100px");
		tab.getColumnFormatter().setWidth(5, "150px");
		
		int row = 0;
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFontBig());
		tab.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextLeft());
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.setWidget(row, 0, new Label("Factura de " + invoice.getType().getDescription()));
		
		String attributes = "[" + invoice.getTransaction().getDescription() + "]";
		attributes = attributes + (invoice.isService()? "[" + AON.MSG.service()+ "]":" ");
		attributes = attributes + (invoice.getRectificationType() != null && invoice.getRectificationType() != RectificationType.NONE 
				? "[" + invoice.getRectificationType().getDescription() + "]":" ");
		attributes = attributes + (invoice.isVatAccrualPayment()? "[" + AON.MSG.vatAccrualPayment() + "]":" ");
		attributes = attributes + (invoice.isWithholdingFarmer()? "[" + AON.MSG.withholdingFarmer() + "]":" ");
		attributes = attributes + (invoice.isInvestment()? "[" + AON.MSG.investAsset() + "]":" ");
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().setColSpan(row, 1, 2);
		tab.setWidget(row, 1, new Label(attributes));
		
		tab.getCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonColorGreen());
		tab.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonFontBig());
		tab.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextRight());
		tab.getFlexCellFormatter().setColSpan(row, 2, 2);
		tab.setWidget(row, 2, new Label(AON.MSG.managmentInvoice()));
		row++;
		
		tab.setWidget(row, 0, new Label(""));
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPaddingTop());
		row++;
		
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.titular()));

		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 1, new Label(invoice.getRegistryName()));
		
		
		tab.getCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 2, new Label(invoice.getRegistryDocumentType() == null
				?""
				:invoice.getRegistryDocumentType().getDescription())
		);

		String document = "";
		if (invoice.getRegistryDocumentCountry() != Country.ES) {
			document = invoice.getRegistryDocumentCountry().getIso2() + "/"; 
		}
		document = document + invoice.getRegistryDocument();
		tab.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 3, new Label(document));
		
		tab.getCellFormatter().setStyleName(row, 4, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 4, new Label(AON.MSG.issueDate()));
		
		tab.getCellFormatter().setStyleName(row, 5, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 5, new Label(DateTimeFormat.getFormat(DATE_PATTERN).format(invoice.getIssueDate())));
		row++;

		
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		if (!invoice.isSales()) {
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
		tab.setWidget(row, 5, new Label(FORMATTER.format( invoice.getTotal())));
		row++;

		tab.setWidget(row, 0, new Label(""));
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPaddingTop());
		row++;
		
		FlexTable detTab = new FlexTable();
		detTab.getColumnFormatter().setWidth(0, "20px");
		detTab.getColumnFormatter().setWidth(1, "140px");
		detTab.getColumnFormatter().setWidth(2, "auto");
		detTab.getColumnFormatter().setWidth(3, "50px");
		detTab.getColumnFormatter().setWidth(4, "60px");
		detTab.getColumnFormatter().setWidth(5, "50px");
		detTab.getColumnFormatter().setWidth(6, "70px");
		
		detTab.setStyleName(AON.AON_CSS.aonBlockCenter());
		detTab.addStyleName(AON.AON_CSS.aonPanelGrid());
		detTab.addStyleName(AON.AON_CSS.aonWidthAll());
		tab.getFlexCellFormatter().setColSpan(row, 0, 6);
		tab.setWidget(row, 0, detTab);
		
		detTab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonDataTableHeader());
		detTab.setWidget(row, 0, new Label( "#" ));
		detTab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonDataTableHeader());
		detTab.setWidget(row, 1, new Label( AON.MSG.product() ));
		detTab.getCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonDataTableHeader());
		detTab.setWidget(row, 2, new Label( AON.MSG.description() ));
		detTab.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonDataTableHeader());
		detTab.getCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextRight());
		detTab.setWidget(row, 3, new Label( "Cant." ));
		detTab.getCellFormatter().setStyleName(row, 4, AON.AON_CSS.aonDataTableHeader());
		detTab.getCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextRight());
		detTab.setWidget(row, 4, new Label( AON.MSG.price() ));
		detTab.getCellFormatter().setStyleName(row, 5, AON.AON_CSS.aonDataTableHeader());
		detTab.getCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextRight());
		detTab.setWidget(row, 5, new Label( "Dto." ));
		detTab.getCellFormatter().setStyleName(row, 6, AON.AON_CSS.aonDataTableHeader());
		detTab.getCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonTextRight());
		detTab.setWidget(row, 6, new Label( AON.MSG.amount() ));
		row++;
		
		for (InvoiceDetail detail : invoice.getDetails()) {
			detTab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
			detTab.setWidget(row, 0, new Label( "" + detail.getLine() ));
			
			detTab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			detTab.setWidget(row, 1, new Label(detail.getItem() != null?detail.getItem().getProduct().getCode():""));

			detTab.getCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonPanelGridEven());
			detTab.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonNowrap());
			Label descriptionLabel = new Label();
			String desc = detail.getDescription();
			if (AonStringUtils.length(desc) > 50) {
				desc = AonStringUtils.abbreviate(desc, 50);
				descriptionLabel.setTitle(detail.getDescription());
			}
			descriptionLabel.setText(desc);
			detTab.setWidget(row, 2, descriptionLabel );
			
			detTab.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonPanelGridEven());
			detTab.getCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextRight());
			detTab.setWidget(row, 3, new Label(FORMATTER.format( detail.getQuantity())));
			
			detTab.getCellFormatter().setStyleName(row, 4, AON.AON_CSS.aonPanelGridEven());
			detTab.getCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextRight());
			detTab.setWidget(row, 4, new Label(FORMATTER.format( detail.getPrice())));
			
			detTab.getCellFormatter().setStyleName(row, 5, AON.AON_CSS.aonPanelGridEven());
			detTab.getCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextRight());
			detTab.setWidget(row, 5, new Label(detail.getDiscountExpression()));
			
			detTab.getCellFormatter().setStyleName(row, 6, AON.AON_CSS.aonPanelGridEven());
			detTab.getCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonTextRight());
			detTab.setWidget(row, 6, new Label(FORMATTER.format( detail.getTaxableBase())));
			row++;
		}
		
		tab.setWidget(row, 0, new Label(""));
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPaddingTop());
		row++;
		if (!invoice.getBreakdown().isEmpty()) {
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
			
			for (InvoiceBreakdown detail : invoice.getBreakdown()) {
				taxTab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
				String typeLabel = detail.getTaxType().getName();
				String percent = FORMATTER.format( detail.getPercentage())+"%";
				double quota = detail.getQuota(); 
				if (invoice.isSurcharge() && detail.getTaxType() == TaxType.VAT && AonMathUtils.isNotZero( detail.getSurcharge() )) {
					typeLabel = typeLabel + " + R.E.";
					percent = percent + " + " + FORMATTER.format( detail.getSurcharge())+"%";
					quota = AonMathUtils.round( quota + detail.getSurchargeQuota() );	
				}
				taxTab.setWidget(row, 0, new Label( typeLabel ));	
				taxTab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
				taxTab.getCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextRight());
				taxTab.setWidget(row, 1, new Label(FORMATTER.format( detail.getBase())));
				taxTab.getCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonPanelGridEven());
				taxTab.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextRight());
				taxTab.setWidget(row, 2, new Label(percent));
				taxTab.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonPanelGridEven());
				taxTab.getCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextRight());
				taxTab.setWidget(row, 3, new Label(FORMATTER.format( quota )));
				row++;
			}
		}
		
		tab.setWidget(row, 0, new Label(""));
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPaddingTop());
		row++;
		if (invoice.getFinances() != null && !invoice.getFinances().isEmpty()) {
			FlexTable financeTab = new FlexTable();
			financeTab.setStyleName(AON.AON_CSS.aonPanelGrid());
			financeTab.addStyleName(AON.AON_CSS.aonBlockRight());
			
			financeTab.getColumnFormatter().setWidth(0, "150px"); // Fecha
			financeTab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonDataTableHeader());
			financeTab.setWidget(0, 0, new Label(AON.MSG.dueDate()));
			
			financeTab.getColumnFormatter().setWidth(1, "150px"); // paymethod
			financeTab.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonDataTableHeader());
			financeTab.setWidget(0, 1, new Label(AON.MSG.payMethod()));
			
			financeTab.getColumnFormatter().setWidth(2, "250px"); // bank
			financeTab.getCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonDataTableHeader());
			financeTab.setWidget(0, 2, new Label(AON.MSG.bankAccount()));
			
			financeTab.getColumnFormatter().setWidth(3, "150px"); // amount
			financeTab.getCellFormatter().setStyleName(0, 3, AON.AON_CSS.aonDataTableHeader());
			financeTab.getCellFormatter().addStyleName(0, 3, AON.AON_CSS.aonTextRight());
			financeTab.setWidget(0, 3, new Label(AON.MSG.amount()));
			
			tab.getFlexCellFormatter().setColSpan(row, 0, 6);
			tab.setWidget(row, 0, financeTab);

			for (Finance finance : invoice.getFinances()) {
				financeTab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
				financeTab.setWidget(row, 0, new Label(DateTimeFormat.getFormat(DATE_PATTERN).format(finance.getDueDate())));
				
				financeTab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
				financeTab.setWidget(row, 1, new Label(finance.getPayMethodName()));
	
				financeTab.getCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonPanelGridEven());
				financeTab.setWidget(row, 2, new Label(finance.getBankAccount()!=null?finance.getBankAccount().getIban():""));
	
				financeTab.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonPanelGridEven());
				financeTab.getCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextRight());
				financeTab.setWidget(row, 3, new Label(FORMATTER.format( finance.getAmount())));
				
				row++;
			}
		}

		scroll.setWidget(tab);
		setWidget(scroll);
	}
	
}
