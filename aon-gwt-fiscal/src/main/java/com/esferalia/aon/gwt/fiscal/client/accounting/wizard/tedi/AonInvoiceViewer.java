package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFinanceStatusVisitor;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

/**
 * @deprecated This class is deprecated and will be removed in future versions. 
 * @use com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceViewer instead.
 * 	
 */
@Deprecated
public class AonInvoiceViewer extends SimpleLayoutPanel {

	private FlowPanel container;
	
	public AonInvoiceViewer (Invoice invoice) {
		super();
		ScrollPanel scroll = new ScrollPanel();
		container = new FlowPanel();
		scroll.setWidget(container);
		setWidget(scroll);
		paintInvoice(invoice);
	}
	
	private void paintInvoice(Invoice invoice) {
		paintTitle(invoice);
		paintHeader(invoice);
		paintDetails(invoice);
		paintBreakDown(invoice);
		paintFinances(invoice);
	}
	
	private void paintTitle(Invoice invoice) {
		InvoiceSource source = invoice
			.getDetails()
			.stream()
			.map( id -> id.getSource())
			.findFirst()
			.orElse(null);
		
		FlowPanel titleContainer = new FlowPanel();
		titleContainer.setStyleName(AON.CSS.aonMarginTop());
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		String sourceDesciption = "M\u00F3dulo origen: " 
			+ ( source == InvoiceSource.TEDI
				?"PORTAL"
				:AonStringUtils.upperCase( source.getDescription() ));
		tab.addRow()
			.addCell( new Label("Factura de " + invoice.getType().getDescription())
					,AON.CSS.aonBold(),AON.CSS.aonTextCenter()
					,AON.CSS.aonFontXLarger(),AON.CSS.aonWidth300())
			.addCell( new Label(" (" + invoice.getId() + ") ")
					,AON.CSS.aonBold(),AON.CSS.aonTextCenter()
					,AON.CSS.aonFontSmall(),AON.CSS.aonWidth100())
			.addCell( getAttributes( invoice )
					,AON.CSS.aonWidthAuto(),AON.CSS.aonTextCenter())
			.addCellIf( source != null , new Label( sourceDesciption )
					,AON.CSS.aonWidth200(),AON.CSS.aonColorGreen(),AON.CSS.aonTextCenter()
					,AON.CSS.aonFontLarger(),AON.CSS.aonNowrap())
		;
		titleContainer.add(tab);
		container.add(titleContainer);
	}
	
	
	private FlowPanel getAttributes(Invoice invoice) {
		FlowPanel attributesPanel = new FlowPanel();
		attributesPanel.add(new AttributeLabel(invoice.getTransaction().getDescription()));
		
		if (invoice.isService()) {
			attributesPanel.add(new AttributeLabel(AON.MSG.service()));
		}
		
		if (invoice.getRectificationType() != null && invoice.getRectificationType() != RectificationType.NONE) {
			attributesPanel.add(new AttributeLabel(invoice.getRectificationType().getDescription()));
		}
		
		if (invoice.isVatAccrualPayment()) {
			attributesPanel.add(new AttributeLabel(AON.MSG.vatAccrualPayment()));
		}
		
		if (invoice.isWithholdingFarmer()) {
			attributesPanel.add(new AttributeLabel(AON.MSG.withholdingFarmer()));
		}
		
		if (invoice.isInvestment()) {
			attributesPanel.add(new AttributeLabel(AON.MSG.investAsset()));
		}
		
		if (invoice.isVatUnion()) {
			attributesPanel.add(new AttributeLabel(AON.MSG.vatUnionRegime()));
		}
		if (invoice.isVatUnionExternal()) {
			attributesPanel.add(new AttributeLabel(AON.MSG.vatUnionExternalRegime()));
		}
		if (invoice.isVatImportation()) {
			attributesPanel.add(new AttributeLabel(AON.MSG.vatImportationRegime()));
		}
		
		return attributesPanel;
	}
	
	private void paintHeader(Invoice invoice) {
		FlowPanel headerContainer = new FlowPanel();
		headerContainer.setStyleName(AON.CSS.aonMarginTop());
		headerContainer.addStyleName(AON.CSS.aonPadding());
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		String document = ((invoice.getRegistryDocumentCountry() != Country.ES)
				?(invoice.getRegistryDocumentCountry().getIso2() + "/"):"") 
			+ invoice.getRegistryDocument();
		String numberLabel = invoice.isSales()?"N/Fra":"S/Fra";
		String numberValue = invoice.isSales()
				?(invoice.getSeries() == null ? "" + invoice.getNumber() : invoice.getSeries() + "/" + invoice.getNumber())
				:(invoice.getReferenceCode());
		tab.addRow()
			.addCell( new Label(AON.MSG.titular()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth120())
			.addCell( new Label(invoice.getRegistryName()),AON.CSS.aonWidth300())
			.addCell( new Label(invoice.getRegistryDocumentType() == null ? "" : invoice.getRegistryDocumentType().getDescription()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth120())
			.addCell( new Label(document),AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.issueDate()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth120())
			.addCell( new Label(AON.DATE_FORMAT.format(invoice.getIssueDate())),AON.CSS.aonWidth150())
			.addCell( new Label() , AON.CSS.aonWidthAuto())
		;
		tab.addRow()
			.addCell( new Label( numberLabel ),AON.CSS.aonBold(),AON.CSS.aonBorderBottom())
			.addCell( new Label( numberValue ))
			.addCell( new Label(AON.MSG.document()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom())
			.addCell( new Label( invoice.getDocumentNumber()))
			.addCell( new Label(AON.MSG.taxDate()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth120())
			.addCell( new Label(AON.DATE_FORMAT.format(invoice.getTaxDate())),AON.CSS.aonWidth150())
			.addCell( new Label() , AON.CSS.aonWidthAuto())
		;
		tab.addRow()
			.addCell( new Label())
			.addCell( new Label())
			.addCell( new Label())
			.addCell( new Label())
			.addCell( new Label(AON.MSG.invoiceTotal()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom())
			.addCell( new Label(AON.FMT.format( invoice.getTotal())),AON.CSS.aonBold(),AON.CSS.aonFontLarger())
			.addCell( new Label() , AON.CSS.aonWidthAuto())
	;
		headerContainer.add(tab);
		container.add(headerContainer);
	}
	
	// *****************************************************************
	// *****************************************************************	
	private void paintDetails(Invoice invoice) {
		FlowPanel detailsContainer = new FlowPanel();
		detailsContainer.setStyleName(AON.CSS.aonBorderBottom());
		detailsContainer.addStyleName(AON.CSS.aonMarginTop());
		detailsContainer.addStyleName(AON.CSS.aonPadding());
		AonDisplayGrid tab = new AonDisplayGrid();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addHeaderRow()
			.addCell(new Label( "#" ), AON.CSS.aonWidth40(), AON.CSS.aonTextCenter())
			.addCell(new Label( AON.MSG.product()), AON.CSS.aonWidth100())
			.addCell(new Label( AON.MSG.description()), AON.CSS.aonWidthAuto())
			.addCell(new Label( "Cant." ), AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
			.addCell(new Label( AON.MSG.price() ), AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
			.addCell(new Label( "Dto." ), AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
			.addCell(new Label( AON.MSG.amount() ), AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
		;
		for (InvoiceDetail detail : invoice.getDetails()) {
			Label descriptionLabel = new Label();
			String desc = detail.getDescription();
			if (AonStringUtils.length(desc) > 50) {
				desc = AonStringUtils.abbreviate(desc, 50);
				descriptionLabel.setTitle(detail.getDescription());
			}
			descriptionLabel.setText(desc);
			tab.addRow()
				.addCell(new Label( "" + detail.getLine() ), AON.CSS.aonTextCenter())
				.addCell(new Label(detail.getItem() != null?detail.getItem().getProduct().getCode():""))
				.addCell(descriptionLabel)
				.addCell(new Label( AON.FMT.format( detail.getQuantity())), AON.CSS.aonTextRight())
				.addCell(new Label( AON.FMT.format( detail.getPrice()))   , AON.CSS.aonTextRight())
				.addCell(new Label( AON.FMT.format( detail.getDiscount())), AON.CSS.aonTextRight())
				.addCell(new Label( AON.FMT.format( detail.getTaxableBase())), AON.CSS.aonTextRight())
			;
		}
		detailsContainer.add(tab);
		container.add(detailsContainer);
	}

	private void paintBreakDown(Invoice invoice) {
		FlowPanel breakdownContainer = new FlowPanel();
		breakdownContainer.setStyleName(AON.CSS.aonMarginTop());
		breakdownContainer.addStyleName(AON.CSS.aonBorderBottom());
		breakdownContainer.addStyleName(AON.CSS.aonPadding());
		if (!invoice.getBreakdown().isEmpty()) {
			AonDisplayGrid tab = new AonDisplayGrid();
			tab.addStyleName(AON.CSS.aonBlockRight());
			tab.addHeaderRow()
				.addCell(new Label( ), AON.CSS.aonWidth100())
				.addCell(new Label( AON.MSG.taxableBase() ), AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
				.addCell(new Label( AON.MSG.percent() ), AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
				.addCell(new Label( AON.MSG.quota() ), AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
			;
			for (InvoiceBreakdown detail : invoice.getBreakdown()) {
				String typeLabel = detail.getTaxType().getName();
				if (detail.getTaxType() == TaxType.RETENTION && detail.getWithholdingType() != null) {
					typeLabel = typeLabel + " (" + detail.getWithholdingType().getAbbreviatedDescription() + ")";
				} 
				String percent = AON.FMT.format( detail.getPercentage())+"%";
				double quota = detail.getQuota(); 
				if (invoice.isSurcharge() && detail.getTaxType() == TaxType.VAT && AonMathUtils.isNotZero( detail.getSurcharge() )) {
					typeLabel = typeLabel + " + R.E.";
					percent = percent + " + " + AON.FMT.format( detail.getSurcharge())+"%";
					quota = AonMathUtils.round( quota + detail.getSurchargeQuota() );	
				}
				tab.addRow()
					.addCell(new Label( typeLabel ))
					.addCell(new Label( AON.FMT.format( detail.getBase()) ), AON.CSS.aonTextRight())
					.addCell(new Label( percent ), AON.CSS.aonTextRight())
					.addCell(new Label( AON.FMT.format( quota )), AON.CSS.aonTextRight())
				;
				
			}
			breakdownContainer.add(tab);
		}
		container.add(breakdownContainer);
	}

	private void paintFinances(Invoice invoice) {
		FlowPanel financesContainer = new FlowPanel();
		financesContainer.setStyleName(AON.CSS.aonMarginTop());
		financesContainer.addStyleName(AON.CSS.aonBorderBottom());
		financesContainer.addStyleName(AON.CSS.aonPadding());
		if (invoice.getFinances() != null && !invoice.getFinances().isEmpty()) {
			AonDisplayGrid tab = new AonDisplayGrid();
			tab.addStyleName(AON.CSS.aonBlockRight());
			tab.addHeaderRow()
				.addCell(new Label( AON.MSG.dueDate()), AON.CSS.aonWidth150())
				.addCell(new Label( AON.MSG.payMethod()), AON.CSS.aonWidth150())
				.addCell(new Label( AON.MSG.bankAccount()), AON.CSS.aonWidthAuto())
				.addCell(new Label( AON.MSG.amount()), AON.CSS.aonWidth150(),AON.CSS.aonTextRight())
				.addCell(new Label( AON.MSG.status()), AON.CSS.aonWidth150())
			;
			for (Finance finance : invoice.getFinances()) {
				Label statusLabel = new Label( finance.getFinanceStatus() == null? "??" : finance.getFinanceStatus().getDescription());				
				if (finance.getFinanceStatus() != null) {
					finance.getFinanceStatus().visit( new IFinanceStatusVisitor() {
						@Override
						public void visitSettled() {
							statusLabel.setStyleName(AON.CSS.aonColorBlue());
						}
						
						@Override
						public void visitReturned() {
							statusLabel.setStyleName(AON.CSS.aonColorRed());
							statusLabel.addStyleName(AON.CSS.aonBold());
						}
						
						@Override
						public void visitPending() {
							statusLabel.setStyleName(AON.CSS.aonColorRed());
						}
						
						@Override
						public void visitPaid() {
							statusLabel.setStyleName(AON.CSS.aonColorGreen());
						}
						
						@Override
						public void visitBatched() {
							statusLabel.setStyleName(AON.CSS.aonColorGreen());
						}
					});
				}
				tab.addRow()
					.addCell(new Label( AON.DATE_FORMAT.format(finance.getDueDate())))
					.addCell(new Label( finance.getPayMethodName()))
					.addCell(new Label( finance.getBankAccount()!=null?finance.getBankAccount().getIban():""))
					.addCell(new Label( AON.FMT.format( finance.getAmount())),AON.CSS.aonTextRight())
					.addCell(statusLabel,AON.CSS.aonTextCenter())
					;
			}
			
			financesContainer.add(tab);
		}		
		container.add(financesContainer);
	}
	
	private static class AttributeLabel extends InlineLabel {

		public AttributeLabel(String text) {
			super(text);
			this.setStyleName(AON.CSS.aonMarginLeft());
			this.addStyleName(AON.CSS.aonBorder());
			this.addStyleName(AON.CSS.aonBackgroundLigthGray());
			this.addStyleName(AON.CSS.aonNowrap());
			this.getElement().getStyle().setPadding(5.0, Unit.PX);
		}

	}
}
