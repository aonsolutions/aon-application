package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.concurrent.atomic.AtomicInteger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFinanceStatusLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerLabel;
import com.esferalia.aon.occam.api.model.finance.FinanceUtil;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class InvoiceViewer extends DockLayoutPanel  {

	public InvoiceViewer (Invoice invoice) {
		super( Unit.PX);
		
		ScrollPanel headerScroll = new ScrollPanel();
		FlowPanel headerContainer = new FlowPanel();
		headerContainer.add( getTitlePanel(invoice));
		headerContainer.add( getHeaderPanel(invoice) );
		headerScroll.setWidget(headerContainer);
		this.addNorth(headerScroll, 200);
		
		ScrollPanel footerScroll = new ScrollPanel();
		footerScroll.setWidget(getFooterPanel(invoice));
		this.addSouth(footerScroll, 180);
		
		add( getDetailsPanel(invoice));
	}
	
	private Widget getTitlePanel(Invoice invoice) {
		FlowPanel titleContainer = new FlowPanel();
		titleContainer.setStyleName(AON.CSS.aonMarginTop());
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addRow()
			.addCell( getInvoiceTypeLabel( invoice )
					,AON.CSS.aonBold(),AON.CSS.aonTextCenter()
					,AON.CSS.aonFontXLarger(),AON.CSS.aonWidth300())
			.addCell( getIdTypeLabel( invoice ) 
					,AON.CSS.aonBold(),AON.CSS.aonTextCenter()
					,AON.CSS.aonFontSmall(),AON.CSS.aonWidthAuto())
			.addCell( getSourceLabel( invoice )
					,AON.CSS.aonWidth200(),AON.CSS.aonColorGreen(),AON.CSS.aonTextCenter()
					,AON.CSS.aonFontLarger(),AON.CSS.aonNowrap())
		;
		titleContainer.add(tab);
		return titleContainer;
	}
	
	private Widget getAttributesPanel(Invoice invoice) {
		FlowPanel attributesContainer = new FlowPanel();
		attributesContainer.setStyleName(AON.CSS.aonWidthAlmostAll());
		attributesContainer.addStyleName(AON.CSS.aonBlockCenter());
		attributesContainer.addStyleName(AON.CSS.aonTextRight());
		attributesContainer.addStyleName(AON.CSS.aonMarginRight());
		attributesContainer.add( getAttributes( invoice ) );
		return attributesContainer;
	}
	
	private Label getSourceLabel(Invoice invoice) {
		return new Label( 
			invoice.getUniqueSource()
			.map( s -> "M\u00F3dulo origen: " + s.getDescription())
			.orElse("M\u00F3dulo origen: M\u00FAltiple"));
	}

	private Widget getIdTypeLabel(Invoice invoice) {
		FlowPanel idPanel = new FlowPanel();
		idPanel.setStyleName(AON.CSS.aonNowrap());
		Label docLabel = new InlineLabel( FinanceUtil.getDocumentNumber(invoice) );
		idPanel.add(docLabel);
		Label idLabel = new InlineLabel( (invoice.getId()==null?"":" ("+ invoice.getId()+ ") "));
		idLabel.setStyleName(AON.CSS.aonMarginLeft());
		idLabel.getElement().getStyle().setFontSize(0.9, Unit.EM);
		idPanel.add(idLabel);
		return idPanel;
	}
	
	private Label getInvoiceTypeLabel(Invoice invoice) {
		return new Label("Factura de " + invoice.getType().getDescription());
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
	
	private Widget getHeaderPanel(Invoice invoice) {
		FlowPanel headerContainer = new FlowPanel();
		headerContainer.setStyleName(AON.CSS.aonMarginTop());
		headerContainer.addStyleName(AON.CSS.aonPadding());
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addRow()
			.addCell( getInvoiceInfoTable(invoice) , AON.CSS.aonWidthHalf())
			.addCell( getRegistryInfoTable(invoice) , AON.CSS.aonWidthHalf())
			.addCell( getAttributesPanel(invoice) , AON.CSS.aonWidth120())
		;
		headerContainer.add(tab);
		return headerContainer;
	}
	
	private Widget getInvoiceInfoTable( Invoice invoice ) {
		FlowPanel infoContainer = new FlowPanel();
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		String numberLabel = invoice.isSales()?"N/Fra":"S/Fra";
		String numberValue = invoice.isSales()
			? FinanceUtil.getSalesReferenceCode(invoice)
			:(invoice.getReferenceCode());
		tab
			.addLabelWidgetRow( AON.MSG.issueDate(), new AonDateLabel(invoice.getIssueDate())) 
			.addLabelWidgetRow( AON.MSG.taxDate(), new AonDateLabel(invoice.getTaxDate()))
			.addLabelWidgetRow( numberLabel , new Label( numberValue ))
			.addLabelWidgetRow( AON.MSG.document(), new Label( FinanceUtil.getDocumentNumber(invoice)))
			.addLabelWidgetRow( AON.MSG.invoiceTotal(), new Label(AON.FMT.format( invoice.getTotal())))
		;
		infoContainer.add(tab);
		return infoContainer;
	}
	
	private Widget getRegistryInfoTable( Invoice invoice ) {
		FlowPanel infoContainer = new FlowPanel();
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		String registryDocumentType = invoice.getRegistryDocumentType() == null ? "" : invoice.getRegistryDocumentType().getDescription(); 		
		String document = ((invoice.getRegistryDocumentCountry() != Country.ES)
				?(invoice.getRegistryDocumentCountry().getIso2() + "/"):"") 
			+ invoice.getRegistryDocument();
		tab
			.addLabelWidgetRow( AON.MSG.titular(), new Label(invoice.getRegistryName()))
			.addLabelWidgetRow( registryDocumentType ,new Label(document))
		;
		RegistryAddress addr = invoice.getAddress();
		if (addr != null && !addr.isEmpty()) {
			tab
				.addLabelWidgetRow( AON.MSG.address(), new Label(addr.getFullAddress()))
				.addLabelWidgetRow( AON.MSG.address(), new Label(addr.getAddress()))
				.addLabelWidgetRow( AON.MSG.zip(), new Label(addr.getZip()))
				.addLabelWidgetRow( AON.MSG.city(), new Label(addr.getCity()))
				.addLabelWidgetRow( AON.MSG.province(), new Label(addr.getProvince()))
				.addLabelWidgetRow( AON.MSG.country(), new Label(addr.getCountry() != null? addr.getCountry().getName() : ""))
			;
		}
		infoContainer.add(tab);
		return infoContainer;
	}

	private Widget getDetailsPanel(Invoice invoice) {
		ScrollPanel detailScroll = new ScrollPanel();
		FlowPanel detailsContainer = new FlowPanel();
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
		AtomicInteger lineCounter = new AtomicInteger(1);
		invoice.detailStream()
			.forEach( detail -> {
				Label descriptionLabel = new Label( AonStringUtils.abbreviate(detail.getDescription(), 50));
				descriptionLabel.setTitle(detail.getDescription());
				tab.addRow()
					.addCell(new AonIntegerLabel( lineCounter.getAndIncrement() ))
					.addCell(new Label(detail.getItem() != null?detail.getItem().getProduct().getCode():""))
					.addCell(descriptionLabel)
					.addCell(new AonDoubleLabel( detail.getQuantity()))
					.addCell(new AonDoubleLabel( detail.getPrice()))
					.addCell(new AonDoubleLabel( detail.getDiscount()))
					.addCell(new AonDoubleLabel( detail.getTaxableBase()))
			;
		});
		detailsContainer.add(tab);
		detailScroll.setWidget(detailsContainer);
		return detailScroll;
	}

	
	private Widget getFooterPanel(Invoice invoice) {
		FlowPanel footerContainer = new FlowPanel();
		footerContainer.setStyleName(AON.CSS.aonMarginTop());
		footerContainer.addStyleName(AON.CSS.aonPadding());
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addRow()
			.addCell( getFinancesPanel(invoice) , AON.CSS.aonWidthAuto())
			.addCell( getBreakDownPanel(invoice) , AON.CSS.aonWidth600())
		;
		footerContainer.add(tab);
		return footerContainer;
	}
	
	private Widget getBreakDownPanel(Invoice invoice) {
		FlowPanel breakdownContainer = new FlowPanel();
		breakdownContainer.setStyleName(AON.CSS.aonMarginTop());
		breakdownContainer.addStyleName(AON.CSS.aonPadding());
		invoice.getTaxBreakdown()
			.ifPresent( tb -> {
				AonDisplayGrid tab = new AonDisplayGrid();
				tab.addStyleName(AON.CSS.aonBlockRight());
				tab.addHeaderRow()
					.addCell(new Label( ), AON.CSS.aonWidth100())
					.addCell(new Label( AON.MSG.taxableBase() ), AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
					.addCell(new Label( AON.MSG.percent() ), AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
					.addCell(new Label( AON.MSG.quota() ), AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
					.addCellIf(invoice.isSurcharge(), new Label( AON.MSG.surchargePercent() ), AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
					.addCellIf(invoice.isSurcharge(), new Label( AON.MSG.surchargeQuota() ), AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
					.addCellIf(invoice.isSurcharge(), new Label( AON.MSG.totalVat() ), AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
				;
				tb.stream()
					.forEach( detail  -> {
						String typeLabel = detail.getTaxType().getName();
						if (detail.getTaxType() == TaxType.RETENTION && detail.getWithholdingType() != null) {
							typeLabel = typeLabel + " (" + detail.getWithholdingType().getAbbreviatedDescription() + ")";
						} 
						String percent = AON.FMT.format( detail.getPercentage())+"%";
						boolean hasSurcharge = invoice.isSurcharge() && detail.getTaxType() == TaxType.VAT && AonMathUtils.isNotZero( detail.getSurcharge() );
						String surcharge = AON.FMT.format( detail.getSurcharge())+"%";
						double quota = AonMathUtils.round( detail.getQuota() + detail.getSurchargeQuota() );	
						tab.addRow()
							.addCell(new Label( typeLabel ))
							.addCell(new AonDoubleLabel( detail.getBase()))
							.addCell(new Label( percent ), AON.CSS.aonTextRight())
							.addCell(new AonDoubleLabel( detail.getQuota() ))
							.addCellIf( hasSurcharge, new Label( surcharge ), AON.CSS.aonTextRight())
							.addCellIf( hasSurcharge, new AonDoubleLabel( detail.getSurchargeQuota() ))
							.addCellIf( hasSurcharge, new AonDoubleLabel( quota ))
						;
					}
				);
				breakdownContainer.add(tab);
			});
		 return breakdownContainer;
	}

	private Widget getFinancesPanel(Invoice invoice) {
		FlowPanel financesContainer = new FlowPanel();
		financesContainer.setStyleName(AON.CSS.aonMarginTop());
		financesContainer.addStyleName(AON.CSS.aonPadding());
		if (invoice.hasFinances()) {
			AonDisplayGrid tab = new AonDisplayGrid();
			tab.addHeaderRow()
				.addCell(new Label( AON.MSG.dueDate()), AON.CSS.aonWidth150())
				.addCell(new Label( AON.MSG.payMethod()), AON.CSS.aonWidth150())
				.addCell(new Label( AON.MSG.bankAccount()), AON.CSS.aonWidthAuto())
				.addCell(new Label( AON.MSG.amount()), AON.CSS.aonWidth150(),AON.CSS.aonTextRight())
				.addCell(new Label( AON.MSG.status()), AON.CSS.aonWidth150())
				;
			invoice.financeStream()
				.forEach( f -> {
					tab.addRow()
						.addCell(new AonDateLabel( f.getDueDate()))
						.addCell(new Label( f.getPayMethodName()))
						.addCell(new Label( f.getBankAccount()!=null?f.getBankAccount().getIban():""))
						.addCell(new AonDoubleLabel( f.getAmount()))
						.addCell(new AonFinanceStatusLabel( f.getFinanceStatus() ),AON.CSS.aonTextCenter())
						;
				});
			financesContainer.add(tab);
		}		
		return financesContainer;
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
