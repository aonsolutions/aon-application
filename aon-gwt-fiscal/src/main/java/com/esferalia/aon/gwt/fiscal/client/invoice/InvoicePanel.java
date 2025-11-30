package com.esferalia.aon.gwt.fiscal.client.invoice;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.InvoiceRegistryDocumentBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceRegistryNameBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceTransactionListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonActivityBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonInvestAssetBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonInvoiceSeriesListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class InvoicePanel extends SimpleLayoutPanel implements HasValueChangeHandlers<Invoice>{
	
	public static interface InvoicePanelCallback {
		void onNatureChange( Invoice invoice );
		void onRegistryChange(Invoice invoice);
	}

	private static final String EMPTY_VALUE = "-----";
	private final boolean readOnly;
	
	private InvoiceRegistryDocumentBox registryDocumentBox;
	private InvoiceRegistryNameBox registryNameBox;
	
	
	public InvoicePanel(InvoiceModuleOptions options, Invoice invoice) {
		this(options, invoice, null, false);
	}
	public InvoicePanel(InvoiceModuleOptions options, Invoice invoice, InvoicePanelCallback callback) {
		this(options, invoice, callback, false);
	}
	public InvoicePanel(InvoiceModuleOptions options, Invoice invoice, InvoicePanelCallback callback, boolean readOnly) {
		super();
		this.readOnly = readOnly;
		setWidget( getInvoice(options, invoice, callback) );
	}
	
	private boolean isReadOnly() {
		return readOnly;
	}
	private boolean isNotReadOnly() {
		return !isReadOnly();
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Invoice> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
	private void fire( Invoice invoice ) {
		ValueChangeEvent.<Invoice>fire(InvoicePanel.this, invoice );
	}
	private Widget getInvoice(InvoiceModuleOptions options, Invoice invoice, InvoicePanelCallback callback) {
		DockLayoutPanel dock = new DockLayoutPanel(Unit.PX);
		Widget header = getHeader(options, invoice, callback);
		dock.addNorth(header, 150);

		
		dock.addSouth(getFooter(invoice), 160);

		Widget details = getDetails(invoice);
		dock.add(details);

		return dock;
		
	}
	private Widget getContainer( String title, Widget widget ) {
		AonDisplayTable headerTab = new AonDisplayTable();
		headerTab.setHeight("100%");
		AonDisplayTableRow headerRow = headerTab.addRow();
		AonDisplayTableCell labelCell = headerRow.addCell();
		labelCell.addStyleName(AON.CSS.aonTextVerticalContainer());
		labelCell.getElement().getStyle().setWidth(40, Unit.PX);
		labelCell.getElement().getStyle().setBackgroundColor("AliceBlue");
		labelCell.getElement().getStyle().setBorderColor("CornflowerBlue");
		labelCell.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		labelCell.getElement().getStyle().setBorderWidth(1, Unit.PX);
		labelCell.getElement().getStyle().setProperty("border-radius", 10, Unit.PCT);
		Label headerDescription = new Label( title);
		headerDescription.setStyleName(AON.CSS.aonTextVertical());
		headerDescription.addStyleName(AON.CSS.aonBold());
		labelCell.add(headerDescription);

		FlowPanel headerPanel = new FlowPanel();
		headerPanel.setStyleName(AON.CSS.aonDisplayFlex());
		headerPanel.addStyleName(AON.CSS.aonWidthAlmostAll());
		headerPanel.addStyleName(AON.CSS.aonBlockCenter());
		headerPanel.addStyleName(AON.CSS.aonAlignItemsCenter());
		headerPanel.addStyleName(AON.CSS.aonFlexWrap());
		headerPanel.getElement().getStyle().setProperty("gap", "8px");
		AonDisplayTableCell cell = headerRow.addCell();
		cell.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
		cell.add(widget);
		
		return headerTab; 
	}
	
	private Widget getHeader(InvoiceModuleOptions options, Invoice invoice, InvoicePanelCallback callback) {
		ScrollPanel headerScoll = new ScrollPanel();

		FlowPanel headerPanel = new FlowPanel();
		headerPanel.setStyleName(AON.CSS.aonDisplayFlex());
		headerPanel.addStyleName(AON.CSS.aonWidthAlmostAll());
		headerPanel.addStyleName(AON.CSS.aonBlockCenter());
		headerPanel.addStyleName(AON.CSS.aonAlignItemsCenter());
		headerPanel.addStyleName(AON.CSS.aonFlexWrap());
		headerPanel.getElement().getStyle().setProperty("gap", "8px");

		Widget headerContainer = getContainer( "DATOS FACTURA", headerPanel );
		headerScoll.setWidget(headerContainer);
		
		resolveType(invoice, callback);
		headerPanel.add( createIdBox( invoice) );
		headerPanel.add( createSeriesBox( options, invoice) );
		headerPanel.add( createNumberBox( invoice) );
		headerPanel.add( createReferenceCodeBox( invoice) );
		headerPanel.add( createIssueDateBox( invoice) );
		headerPanel.add( createTaxDateBox( invoice) );
		headerPanel.add( createSecurityLevelBox( invoice) );
		if (options.getConfiguration().hasActivities()) {
			headerPanel.add( createActivityBox( options, invoice) );
		}
		
		FlowPanel registryPanel = new FlowPanel();
		registryPanel.setStyleName(AON.CSS.aonNowrap());
		registryPanel.addStyleName(AON.CSS.aonDisplayFlexStart());
		registryPanel.addStyleName(AON.CSS.aonFlexGrow1());
		registryPanel.addStyleName(AON.CSS.aonWidthAll());
		registryPanel.add( createRegistryBox( options, invoice, callback) );
		registryPanel.add( createRegistryNameBox( options, invoice, callback) );
		headerPanel.add( registryPanel);
		
		headerPanel.add( createTransactionBox( invoice ) );
		headerPanel.add( createSurcharge( invoice ) );
		headerPanel.add( createWithholding( invoice ) );
		headerPanel.add( createWithholdingFarmer( invoice ) );
		headerPanel.add( createVatAccrualPayment( invoice ) );
		headerPanel.add( createInvestment( invoice ) );
		headerPanel.add( createService( invoice ) );
		
		if (options.getConfiguration().isInvestAssetsAvailable()) {
			headerPanel.add( createInvestAssetBox( options, invoice) );
		}
		return headerScoll;
		
	}
	
	private Widget wrapWidget(Widget widget) {
		FlowPanel wrapper = new FlowPanel();
		wrapper.add(widget);
		return wrapper;
	}
	private Widget wrapWidget(String label, Widget widget) {
		FlowPanel wrapperContainer = new FlowPanel();
		wrapperContainer.addStyleName(AON.CSS.aonFlexBlockInline());
		wrapperContainer.addStyleName(AON.CSS.aonNowrap());

		InlineLabel titleLabel = new InlineLabel(AonStringUtils.defaultIfBlank(label) );
		titleLabel.addStyleName(AON.CSS.aonInnerLabel()); 
		titleLabel.addStyleName(AON.CSS.aonMarginRight());
		wrapperContainer.add(titleLabel);
		
		widget.addStyleName(AON.CSS.aonNoPadding());
		wrapperContainer.add(widget);
		
		return wrapWidget(wrapperContainer);
	}
	
	// ------------------------------------------------------- [TYPE]
	private void resolveType(Invoice invoice, InvoicePanelCallback callback) {
		if (callback != null) {
			callback.onNatureChange( invoice );
		}
	}
	
	// --------------------------------------------------------- [ID]
	private Widget createIdBox(Invoice invoice) {
		String id = invoice.getId() == null ? EMPTY_VALUE : AON.FMT_INT.format(invoice.getId());
		Label idLabel = new Label(id);
		idLabel.setStyleName(AON.CSS.aonTextCenter());
		return wrapWidget(idLabel);
	}
	
	// ----------------------------------------------------- [SERIES]
	private Widget createSeriesBox(InvoiceModuleOptions options, Invoice invoice) {
		AonInvoiceSeriesListBox seriesBox = new AonInvoiceSeriesListBox(options, invoice.isRectifier() );
		seriesBox.setEnabled( isNotReadOnly() );
		seriesBox.setSeries( invoice.getSeries() , false);
		if (isNotReadOnly()) {
			seriesBox.addSelectionHandler(e -> {
				invoice.setSeries( e.getSelectedItem() );
				fire( invoice );
			});			
		}
		return wrapWidget(AON.MSG.series(), seriesBox);
	}
	// ----------------------------------------------------- [NUMBER]
	private Widget createNumberBox(Invoice invoice) {
		if ( invoice.isProforma()) {
			Label proformaLabel = new Label( AON.MSG.proforma() );
			proformaLabel.setStyleName(AON.CSS.aonTextCenter());
			return wrapWidget(AON.MSG.number(), proformaLabel);
		} else {
			AonIntegerBox numberBox = new AonIntegerBox();
			numberBox.setEnabled( isNotReadOnly() );
			numberBox.setValue( invoice.getNumber() );
			if (isNotReadOnly()) {
				numberBox.addValueChangeHandler(e -> {
					invoice.setNumber( e.getValue() );
					fire( invoice );
				});			
			}
			return wrapWidget(AON.MSG.number(),numberBox);
		}
	}
	// --------------------------------------------- [REFERENCE CODE]
	private Widget createReferenceCodeBox(Invoice invoice) {
		AonTextBox referenceCodeBox = new AonTextBox();
		referenceCodeBox.setEnabled( isNotReadOnly() );
		referenceCodeBox.setValue( invoice.getReferenceCode() );
		if (isNotReadOnly()) {
			referenceCodeBox.addValueChangeHandler(e -> {
				invoice.setReferenceCode( e.getValue() );
				fire( invoice );
			});			
		}
		return wrapWidget(AON.MSG.reference(),referenceCodeBox);
	}
	// ------------------------------------------------- [ISSUE DATE]
	private Widget createIssueDateBox(Invoice invoice) {
		AonDateBox issueDateBox = new AonDateBox();
		issueDateBox.setEnabled( isNotReadOnly() );
		issueDateBox.setValue( invoice.getIssueDate() );
		if (isNotReadOnly()) {
			issueDateBox.addValueChangeHandler(e -> {
				invoice.setIssueDate( e.getValue() );
				fire( invoice );
			});			
		}
		return wrapWidget(AON.MSG.issueDate(),issueDateBox);
	}
	// --------------------------------------------------- [TAX DATE]
	private Widget createTaxDateBox(Invoice invoice) {
		AonDateBox taxDateBox = new AonDateBox();
		taxDateBox.setEnabled( isNotReadOnly() );
		taxDateBox.setValue( invoice.getTaxDate() );
		if (isNotReadOnly()) {
			taxDateBox.addValueChangeHandler(e -> {
				invoice.setTaxDate( e.getValue() );
				fire( invoice );
			});			
		}
		return wrapWidget(AON.MSG.taxDate(),taxDateBox);
	}
	// --------------------------------------------- [SECURITY LEVEL]
	private Widget createSecurityLevelBox(Invoice invoice) {
		CheckBox securityLevelBox = new CheckBox( AON.MSG.confidential() );
		securityLevelBox.setStyleName(AON.CSS.aonCheckBox());
		securityLevelBox.setEnabled( isNotReadOnly() );
		securityLevelBox.setValue( invoice.isConfidential() );
		if (isNotReadOnly()) {
			securityLevelBox.addValueChangeHandler(e -> {
				invoice.setConfidential( e.getValue() );
				fire( invoice );
			});			
		}
		return wrapWidget("",securityLevelBox);
	}

	// --------------------------------------------------- [ACTIVITY]
	private Widget createActivityBox(InvoiceModuleOptions options, Invoice invoice) {
		AonActivityBox activityBox = new AonActivityBox(options);
		activityBox.setEnabled( isNotReadOnly() );
		activityBox.setActivity( invoice.optActivity().orElse(null) );
		if (isNotReadOnly()) {
			activityBox.addSelectionHandler(e -> {
				invoice.setActivity( e.getSelectedItem() );
				fire( invoice );
			});			
		}
		return wrapWidget(AON.MSG.activity(), activityBox);
	}
	
	// --------------------------------------------------- [REGISTRY]
	// ------------------------------------- [REGISTRY DOCUMENT TYPE]
	// ---------------------------------- [REGISTRY DOCUMENT COUNTRY]
	// ------------------------------------------ [REGISTRY DOCUMENT]
	public Widget createRegistryBox(InvoiceModuleOptions options, Invoice invoice, InvoicePanelCallback callback) {
		registryDocumentBox = new InvoiceRegistryDocumentBox(options);
		registryDocumentBox.setEnabled( isNotReadOnly() );
		registryDocumentBox.setValue( InvoiceRegistry.from(invoice) );
		if (isNotReadOnly()) {
			registryDocumentBox.addSelectionHandler(e -> {
				InvoiceRegistry ir = e.getSelectedItem();
				InvoiceRegistry.copy( ir, invoice);
				registryNameBox.setValue(ir, false);
				if (callback != null) {
					callback.onRegistryChange( invoice );
				}
				fire( invoice );
			});			
		}
		return wrapWidget(AON.MSG.recipient(),registryDocumentBox);
	}
	
	// ---------------------------------------------- [REGISTRY NAME]
	public Widget createRegistryNameBox(InvoiceModuleOptions options, Invoice invoice, InvoicePanelCallback callback) {
		registryNameBox = new InvoiceRegistryNameBox(options);
		registryNameBox.setEnabled( isNotReadOnly() );
		registryNameBox.setValue( InvoiceRegistry.from(invoice) );
		if (isNotReadOnly()) {
			registryNameBox.addSelectionHandler(e -> {
				InvoiceRegistry ir = e.getSelectedItem();
				InvoiceRegistry.copy( ir, invoice);
				registryDocumentBox.setValue( ir, false );
				if (callback != null) {
					callback.onRegistryChange( invoice );
				}
				fire( invoice );
			});			
		}
		return wrapWidget(AON.MSG.name(),registryNameBox);
	}
	
	// ------------------------------------------------ [TRANSACTION]
	private Widget createTransactionBox(Invoice invoice) {
		InvoiceTransactionListBox transactionBox = new InvoiceTransactionListBox();
		transactionBox.setEnabled( isNotReadOnly() );
		transactionBox.setValue( invoice.getTransaction() );
		if (isNotReadOnly()) {
			transactionBox.addChangeHandler(e -> {
				invoice.setTransaction( transactionBox.getValue() );
				fire( invoice );
			});
		}
		return wrapWidget(AON.MSG.transaction(),transactionBox);
	}
	
	// -------------------------------------------------- [SURCHARGE]
	private Widget createSurcharge( Invoice invoice ) {
		CheckBox surchargeBox = new CheckBox( AON.MSG.surcharge() );
		surchargeBox.setStyleName(AON.CSS.aonCheckBox());
		surchargeBox.setEnabled( isNotReadOnly() );
		surchargeBox.setValue( invoice.isSurcharge() );
		if (isNotReadOnly()) {
			surchargeBox.addValueChangeHandler(e -> {
				invoice.setSurcharge( e.getValue() );
				fire( invoice );
			});			
		}
		return wrapWidget(surchargeBox);
	}
	
	// ------------------------------------------------ [WITHHOLDING]
	private Widget createWithholding( Invoice invoice ) {
		CheckBox withholdingBox = new CheckBox( AON.MSG.withholding() );
		withholdingBox.setStyleName(AON.CSS.aonCheckBox());
		withholdingBox.setEnabled( isNotReadOnly() );
		withholdingBox.setValue( invoice.isWithholding() );
		if (isNotReadOnly()) {
			withholdingBox.addValueChangeHandler(e -> {
				invoice.setWithholding(e.getValue() );
				fire( invoice );
			});			
		}
		return wrapWidget(withholdingBox);
	}
	
	// ----------------------------------------- [WITHHOLDING FARMER]
	private Widget createWithholdingFarmer( Invoice invoice ) {
		CheckBox withholdingFarmerBox = new CheckBox( AON.MSG.withholdingFarmer() );
		withholdingFarmerBox.setStyleName(AON.CSS.aonCheckBox());
		withholdingFarmerBox.setEnabled( isNotReadOnly() );
		withholdingFarmerBox.setValue( invoice.isWithholdingFarmer() );
		if (isNotReadOnly()) {
			withholdingFarmerBox.addValueChangeHandler(e -> {
				invoice.setWithholdingFarmer( e.getValue() );
				fire( invoice );
			});			
		}
		return wrapWidget(withholdingFarmerBox);
	}
	
	// ---------------------------------------- [VAT ACCRUAL PAYMENT]
	private Widget createVatAccrualPayment( Invoice invoice ) {
		CheckBox vatAccrualPaymentBox = new CheckBox( AON.MSG.vatAccrualPayment() );
		vatAccrualPaymentBox.setStyleName(AON.CSS.aonCheckBox());
		vatAccrualPaymentBox.setEnabled( isNotReadOnly() );
		vatAccrualPaymentBox.setValue( invoice.isVatAccrualPayment() );
		if (isNotReadOnly()) {
			vatAccrualPaymentBox.addValueChangeHandler(e -> {
				invoice.setVatAccrualPayment( e.getValue() );
				fire( invoice );
			});			
		}
		return wrapWidget(vatAccrualPaymentBox);
	}
	
	// ------------------------------------------------- [INVESTMENT]
	private Widget createInvestment( Invoice invoice ) {
		CheckBox investmentBox = new CheckBox( AON.MSG.investment() );
		investmentBox.setStyleName(AON.CSS.aonCheckBox());
		investmentBox.setEnabled( isNotReadOnly() );
		investmentBox.setValue( invoice.isInvestment() );
		if (isNotReadOnly()) {
			investmentBox.addValueChangeHandler(e -> {
				invoice.setInvestment( e.getValue() );
				fire( invoice );
			});			
		}
		return wrapWidget(investmentBox);
	}
	
	// ---------------------------------------------------- [SERVICE]
	private Widget createService( Invoice invoice ) {
		CheckBox serviceBox = new CheckBox( AON.MSG.service() );
		serviceBox.setStyleName(AON.CSS.aonCheckBox());
		serviceBox.setEnabled( isNotReadOnly() );
		serviceBox.setValue( invoice.isService() );
		if (isNotReadOnly()) {
			serviceBox.addValueChangeHandler(e -> {
				invoice.setService( e.getValue() );
				fire( invoice );
			});			
		}
		return wrapWidget(serviceBox);
	}
	
	// ----------------------------------------------- [INVEST ASSET]
	private Widget createInvestAssetBox(InvoiceModuleOptions options, Invoice invoice) {
		AonInvestAssetBox investAssetBox = new AonInvestAssetBox(options);
		investAssetBox.setEnabled( isNotReadOnly() );
		investAssetBox.setInvestAsset( invoice.getInvestAsset() );
		if (isNotReadOnly()) {
			investAssetBox.addSelectionHandler(e -> {
				InvestAsset ia = e.getSelectedItem();
				invoice.setInvestAsset( ia==null?null:ia.getId() );
				fire( invoice );
			});
		}
		return wrapWidget(investAssetBox);
	}
	// ---------------------------------------------------- [PROJECT]
	// ----------------------------------------- [RECTIFICATION TYPE]
	// ------------------------------------------- [REGISTRY ADDRESS]
	// ---------------------------------------------------- [ADDRESS]
	// -------------------------------------- [RECTIFICATION INVOICE]
	// ------------------------------- [RECTIFICATION INVOICE SERIES]
	// ---------------------------- [RECTIFICATION INVOICE REFERENCE]
	// ------------------------------- [RECTIFICATION INVOICE NUMBER]
	// --------------------------------- [RECTIFICATION INVOICE DATE]
	// ------------------------------------------- [REGISTRY ACCOUNT]
	// ------------------------------------------------------ [SCOPE]
	// --------------------------------------------------- [RECORDED]
	
	
	// ---------------------------------------------------- [ADVANCE]
	// ----------------------------------------------------- [SIGNED]
	// --------------------------------------------------- [ANNULLED]
	// ----------------------------------------------- [TAXABLE BASE]
	// -------------------------------------------------- [VAT QUOTA]
	// -------------------------------------------- [RETENTION QUOTA]
	// ------------------------------------------------------ [TOTAL]
	// ----------------------------------------------------- [SELLER]
	// ------------------------------------------------ [SELLER NAME]
	// --------------------------------------------------- [COMMENTS]
	// ---------------------------------------------------- [REMARKS]

	// -------------------------------------------------------------
	// --------------------------------------------------- [DETAILS]
	// -------------------------------------------------------------
	private Widget getDetails(Invoice invoice) {
		ScrollPanel detailsScroll = new ScrollPanel();
		detailsScroll.setStyleName(AON.CSS.aonWidthAlmostAll());

		FlowPanel detailsPanel = new FlowPanel();
		detailsPanel.addStyleName(AON.CSS.aonWidthAlmostAll());
		detailsPanel.addStyleName(AON.CSS.aonBlockCenter());
		
		Widget headerContainer = getContainer( "DETALLE FACTURA", detailsPanel );
		detailsScroll.setWidget(headerContainer);
		
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
		invoice.detailStream()
			.forEach(detail -> {
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
			});
		detailsPanel.add(tab);
		return detailsScroll;
	}
	
	
	// -------------------------------------------------------------
	// ---------------------------------------------------- [FOOTER]
	// -------------------------------------------------------------
	private Widget getFooter(Invoice invoice) {
		ScrollPanel footerScroll = new ScrollPanel();
		FlowPanel footerPanel = new FlowPanel();
		footerPanel.setStyleName(AON.CSS.aonWidthAlmostAll());
		footerPanel.addStyleName(AON.CSS.aonBlockCenter());
		footerPanel.addStyleName(AON.CSS.aonDisplayFlex());
		
		Widget finances = getFinances(invoice);
		finances.setWidth("50%");
		footerPanel.add(finances);
		
		Widget breakdown = getBreakdown(invoice);
		breakdown.setWidth("50%");
		footerPanel.add(breakdown);
		
		footerScroll.setWidget(footerPanel);
		return footerScroll;
	}
	
	private Widget getBreakdown(Invoice invoice) {
		ScrollPanel breakdownScroll = new ScrollPanel();
		breakdownScroll.setStyleName(AON.CSS.aonBorderBottom());
		breakdownScroll.addStyleName(AON.CSS.aonWidthAlmostAll());

		FlowPanel breakdownContainer = new FlowPanel();
		Widget headerContainer = getContainer( "TOTALES", breakdownContainer );
		breakdownScroll.setWidget(headerContainer);
		
		AonDisplayGrid tab = new AonDisplayGrid();
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addHeaderRow()
			.addCell(new Label( ), AON.CSS.aonWidth100())
			.addCell(new Label( AON.MSG.taxableBase() ), AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
			.addCell(new Label( AON.MSG.percent() ), AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
			.addCell(new Label( AON.MSG.quota() ), AON.CSS.aonTextRight(),AON.CSS.aonWidth120())
		;
		invoice.getTaxBreakdown()
			.ifPresent(tb -> 
				tb.stream()
					.forEach(br -> {
						String typeLabel = br.getTaxType().getName();
						if (br.getTaxType() == TaxType.RETENTION && br.getWithholdingType() != null) {
							typeLabel = typeLabel + " (" + br.getWithholdingType().getAbbreviatedDescription() + ")";
						} 
						String percent = AON.FMT.format( br.getPercentage())+"%";
						double quota = br.getQuota(); 
						if (invoice.isSurcharge() && br.getTaxType() == TaxType.VAT && AonMathUtils.isNotZero( br.getSurcharge() )) {
							typeLabel = typeLabel + " + R.E.";
							percent = percent + " + " + AON.FMT.format( br.getSurcharge())+"%";
							quota = AonMathUtils.round( quota + br.getSurchargeQuota() );	
						}
						tab.addRow()
							.addCell(new Label( typeLabel ))
							.addCell(new Label( AON.FMT.format( br.getBase()) ), AON.CSS.aonTextRight())
							.addCell(new Label( percent ), AON.CSS.aonTextRight())
							.addCell(new Label( AON.FMT.format( quota )), AON.CSS.aonTextRight())
						;
					})
				);
		breakdownContainer.add(tab);
		return breakdownScroll;
	}

	private Widget getFinances(Invoice invoice) {
		ScrollPanel financesScroll = new ScrollPanel();
		financesScroll.setStyleName(AON.CSS.aonBorderTop());
		financesScroll.addStyleName(AON.CSS.aonWidthAlmostAll());

		FlowPanel financesContainer = new FlowPanel();
		Widget headerContainer = getContainer( "PAGOS", financesContainer );
		financesScroll.setWidget(headerContainer);

		AonDisplayGrid tab = new AonDisplayGrid();
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addHeaderRow()
			.addCell(new Label( AON.MSG.dueDate()), AON.CSS.aonWidth150())
			.addCell(new Label( AON.MSG.payMethod()), AON.CSS.aonWidth150())
			.addCell(new Label( AON.MSG.bankAccount()), AON.CSS.aonWidthAuto())
			.addCell(new Label( AON.MSG.amount()), AON.CSS.aonWidth150(),AON.CSS.aonTextRight())
			.addCell(new Label( AON.MSG.status()), AON.CSS.aonWidth150())
		;
		invoice.financeStream()
			.forEach(finance -> 
				tab.addRow()
					.addCell(new Label( AON.DATE_FORMAT.format(finance.getDueDate())))
					.addCell(new Label( finance.getPayMethodName()))
					.addCell(new Label( finance.getBankAccount()!=null?finance.getBankAccount().getIban():""))
					.addCell(new Label( AON.FMT.format( finance.getAmount())),AON.CSS.aonTextRight())
					.addCell(new FinanceStatusLabel(finance.getFinanceStatus()),AON.CSS.aonTextCenter())
			);
		financesContainer.add(tab);
		return financesScroll;
	}
	
}
