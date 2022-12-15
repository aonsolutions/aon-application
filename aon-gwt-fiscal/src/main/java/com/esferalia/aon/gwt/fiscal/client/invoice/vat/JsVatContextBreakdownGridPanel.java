package com.esferalia.aon.gwt.fiscal.client.invoice.vat;

import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBooleanLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleLabel;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class JsVatContextBreakdownGridPanel extends FlowPanel implements HasSelectionHandlers<JsVatContext>{
	
	private final boolean showProrrate;
	private final Label title;
	private final Label subTitle;
	private final Label remarks;
	private final AonDisplayGrid grid;
	private double sumBase = 0.0;
	private double sumQuota = 0.0;
	private double sumSurchargeQuota = 0.0;
	private double sumDeductibleQuota = 0.0;
	private double sumProrratedQuota = 0.0;
	
	
	public JsVatContextBreakdownGridPanel() {
		this(false);
	}
	
	public JsVatContextBreakdownGridPanel(boolean showProrrate) {
		this.showProrrate = showProrrate;
		
		title = new Label();
		title.setStyleName(AON.CSS.aonMarginTop());
		title.addStyleName(AON.CSS.aonBold());
		title.addStyleName(AON.CSS.aonWidthAll());
		title.addStyleName(AON.CSS.aonTextCenter());
		title.addStyleName(AON.CSS.aonTextUppercase());
		title.addStyleName(AON.CSS.aonFontLarger());
		add( title );
		
		subTitle = new Label();	
		subTitle.setStyleName(AON.CSS.aonMarginTop());
		subTitle.addStyleName(AON.CSS.aonBold());
		subTitle.addStyleName(AON.CSS.aonWidthAll());
		subTitle.addStyleName(AON.CSS.aonTextCenter());
		subTitle.addStyleName(AON.CSS.aonFontLarger());
		add( subTitle );

		remarks = new Label();	
		remarks.setStyleName(AON.CSS.aonMarginTop());
		remarks.addStyleName(AON.CSS.aonColorRed());
		remarks.addStyleName(AON.CSS.aonBold());
		remarks.addStyleName(AON.CSS.aonWidthAll());
		remarks.addStyleName(AON.CSS.aonTextCenter());
		remarks.addStyleName(AON.CSS.aonFontLarger());
		remarks.setVisible(false);
		add( remarks );

		grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonMarginTop());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		paintHeader();
		add( grid );
	}
	
	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		this.title.setText(title);	
	}
	public void setSubTitle( String subTitle) {
		this.subTitle.setText(subTitle);	
	}
	public void setRemarks( String remarks) {
		this.remarks.setText(remarks);
		this.remarks.setVisible( AonStringUtils.isNotBlank(remarks) );
	}

	private void paintHeader() {
		Label serviceLabel = new Label("S");
		serviceLabel.setTitle(AON.MSG.service());
		Label investmentLabel = new Label("I");
		investmentLabel.setTitle(AON.MSG.investment());
		Label farmerLabel = new Label("A");
		farmerLabel.setTitle(AON.MSG.farmerRegime());
		Label rectifiedLabel = new Label("R");
		rectifiedLabel.setTitle(AON.MSG.rectified());
		Label accrualLabel = new Label("C");
		accrualLabel.setTitle(AON.MSG.vatAccrualPayment());
		Label importationLabel = new Label("M");
		importationLabel.setTitle(AON.MSG.vatImportationRegime());
		Label duaLabel = new Label("D");
		duaLabel.setTitle(AON.MSG.DUALinked());
		grid.addHeaderRow()
			.addCell(new Label("Tipo"),AON.CSS.aonWidth40())
			.addCell(new Label("Tran."),AON.CSS.aonWidth40())
			.addCell(serviceLabel,AON.CSS.aonWidth20())
			.addCell(investmentLabel,AON.CSS.aonWidth20())
			.addCell(farmerLabel,AON.CSS.aonWidth20())
			.addCell(rectifiedLabel,AON.CSS.aonWidth20())
			.addCell(accrualLabel,AON.CSS.aonWidth20())
			.addCell(importationLabel,AON.CSS.aonWidth20())
			.addCell(duaLabel,AON.CSS.aonWidth20())
			.addCell(new Label("Epigr."),AON.CSS.aonWidth80())
			.addCell(new Label("N\u00BA.Doc"),AON.CSS.aonWidth100(),AON.CSS.aonNowrap())
			.addCell(new Label("Doc.Tit."),AON.CSS.aonWidthAuto())
			.addCell(new Label("Nombre/raz\u00F3n social"),AON.CSS.aonWidthAuto())
			.addCell(new Label("Fec. Fac."),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCell(new Label("Fec. Imp."),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCell(new Label("Tipo IVA"),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCell(new Label("Base Imp."),AON.CSS.aonTextRight(),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())		
			.addCell(new Label("% IVA"),AON.CSS.aonTextRight(),AON.CSS.aonWidth40(),AON.CSS.aonNowrap())
			.addCell(new Label("Cuota"),AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
			.addCell(new Label("% RE"),AON.CSS.aonTextRight(),AON.CSS.aonWidth40(),AON.CSS.aonNowrap())
			.addCell(new Label("Cuota RE"),AON.CSS.aonTextRight(),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCell(new Label("% Ded."),AON.CSS.aonTextRight(),AON.CSS.aonWidth40(),AON.CSS.aonNowrap())
			.addCell(new Label("Cuota Ded"),AON.CSS.aonTextRight(),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCellIf(showProrrate, new Label("% Pror.."),AON.CSS.aonTextRight(),AON.CSS.aonWidth40(),AON.CSS.aonNowrap())
			.addCellIf(showProrrate, new Label("Cuota Pro."),AON.CSS.aonTextRight(),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCell(new Label("N\u00BA Referencia"),AON.CSS.aonWidth100(),AON.CSS.aonNowrap())
		;
	}
	
	private String ensure(Object nullable, Supplier<String>  supplier) {
		return ensure(nullable, supplier, "---");
	}
	private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
		return (nullable == null) 
			? defaultValue
			: supplier.get();
	}
	
	public void addRow(JsVatContext vc) {
		InvoiceType invoiceType = InvoiceType.safeValueOf(vc.getInvoiceType());
		InvoiceTransactionType invoiceTransactionType =  InvoiceTransactionType.safeValueOf(vc.getTransaction());
		VatDeductionType vatDeductionType = VatDeductionType.safeValueOf(vc.getVatDeductionType());
		AonDisplayGridRow row = grid.addRow();
		row.addClickHandler(event -> SelectionEvent.fire(this, vc));
		
		Label serviceLabel = new Label();
		serviceLabel.setStyleName(DEBUG_ID_PREFIX);
		
		String issueDate = ensure(vc.getIssueDate(), () -> AON.DATE_FORMAT.format(vc.getIssueDate()), AonStringUtils.EMPTY);
		Label issueDateLabel = new Label(issueDate);

		String taxDate = ensure(vc.getTaxDate(), () -> AON.DATE_FORMAT.format(vc.getTaxDate()), AonStringUtils.EMPTY);
		Label taxDateLabel = new Label(taxDate);
		if (!AonStringUtils.equals(issueDate, taxDate)) {
			taxDateLabel.addStyleName(AON.CSS.aonBackgroundHighlightedOrange());
		}
		AonDoubleLabel surchargePercentLabel = new AonDoubleLabel();
		AonDoubleLabel surchargeQuotaLabel = new AonDoubleLabel();
		if ( vc.isSurcharge()) {
			surchargePercentLabel.setValue(vc.getSurchargePercent());
			surchargeQuotaLabel.setValue(vc.getSurchargeQuota());
		}
		AonDoubleLabel deductiblePercentLabel = new AonDoubleLabel();
		AonDoubleLabel deductibleQuotaLabel = new AonDoubleLabel();
		if (!vc.isSales()) {
			deductiblePercentLabel.setValue(vc.getDeductiblePercent());
			deductibleQuotaLabel.setValue(vc.getDeductibleQuota());
		}
		AonDoubleLabel prorratePercentLabel = new AonDoubleLabel();
		AonDoubleLabel prorrateQuotaLabel = new AonDoubleLabel();
		
		if (showProrrate) {
			prorratePercentLabel.setValue(vc.getProrratePercent());
			if (vc.isProrrated()) {
				prorrateQuotaLabel.setValue(vc.getProrrateQuota());
			} else {
				prorrateQuotaLabel.setValue(vc.getDeductibleQuota());
			}
		}
		row
			.addCell(new Label(ensure(invoiceType,invoiceType::getAbbrDescription)))
			.addCell(new Label(ensure(invoiceTransactionType,invoiceTransactionType::getTediName)))
			.addCell(new AonBooleanLabel(vc.isService()			, "S", AON.MSG.service() ))	
			.addCell(new AonBooleanLabel(vc.isInvestment()		, "I", AON.MSG.investment() ))
			.addCell(new AonBooleanLabel(vc.isFarmerRegime()	, "A", AON.MSG.farmerRegime() ))
			.addCell(new AonBooleanLabel(vc.isRectification()	, "R", AON.MSG.rectifiedInvoice()))
			.addCell(new AonBooleanLabel(vc.isVatAccrualRegime(), "C", AON.MSG.vatAccrualPayment()))
			.addCell(new AonBooleanLabel(vc.isVatImportation()	, "M", AON.MSG.vatImportationRegime()))
			.addCell(new AonBooleanLabel(vc.hasDuaLinked()		, "D", AON.MSG.DUALinked()))
			.addCell(new Label(ensure(vc.getEpigraph(), vc::getEpigraph, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(vc.getDocumentNumber(), vc::getDocumentNumber, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(vc.getRegistryDocument(), vc::getRegistryDocument, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(vc.getRegistryName(), () -> AonStringUtils.abbreviate(vc.getRegistryName(),25), AonStringUtils.EMPTY)))
			.addCell(issueDateLabel)
			.addCell(taxDateLabel)
			.addCell(new Label(ensure(vatDeductionType.getAbbr(), vatDeductionType::getAbbr, AonStringUtils.EMPTY)))
			.addCell(new AonDoubleLabel(vc.getBase()),AON.CSS.aonTextRight())
			.addCell(new AonDoubleLabel(vc.getPercentage()),AON.CSS.aonTextRight())
			.addCell(new AonDoubleLabel(vc.getQuota()),AON.CSS.aonTextRight())
			.addCell(surchargePercentLabel ,AON.CSS.aonTextRight())
			.addCell(surchargeQuotaLabel   ,AON.CSS.aonTextRight())
			.addCell(deductiblePercentLabel,AON.CSS.aonTextRight())
			.addCell(deductibleQuotaLabel,AON.CSS.aonTextRight())
			.addCellIf(showProrrate, prorratePercentLabel,AON.CSS.aonTextRight(),AonMathUtils.isZero(vc.getProrratePercent())?AON.CSS.aonColorBlack():AON.CSS.aonColorBlue())
			.addCellIf(showProrrate, prorrateQuotaLabel,AON.CSS.aonTextRight(),AonMathUtils.isZero(vc.getProrratePercent())?AON.CSS.aonColorBlack():AON.CSS.aonColorBlue())
			.addCell(new Label(ensure(vc.getReferenceCode(), vc::getReferenceCode, AonStringUtils.EMPTY)))
		;

		sumBase += vc.getBase();
		sumQuota += vc.getQuota();
		sumSurchargeQuota += vc.getSurchargeQuota();
		sumDeductibleQuota += vc.getDeductibleQuota();
		sumProrratedQuota += vc.isProrrated()?vc.getProrrateQuota():vc.getDeductibleQuota();
	}

	public void addFooterRow() {
		grid.addFooterRow()
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label())	
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label(AON.MSG.total()),AON.CSS.aonBold())
			.addCell(new AonDoubleLabel(sumBase),AON.CSS.aonTextRight(),AON.CSS.aonBold())
			.addCell(new Label())
			.addCell(new AonDoubleLabel(sumQuota),AON.CSS.aonTextRight(),AON.CSS.aonBold())
			.addCell(new Label())
			.addCell(new AonDoubleLabel(sumSurchargeQuota, true),AON.CSS.aonTextRight(),AON.CSS.aonBold())
			.addCell(new Label())
			.addCell(new AonDoubleLabel(sumDeductibleQuota),AON.CSS.aonTextRight(),AON.CSS.aonBold())
			.addCellIf(showProrrate, new Label())
			.addCellIf(showProrrate, new AonDoubleLabel(sumProrratedQuota),AON.CSS.aonTextRight(),AON.CSS.aonColorBlue(),AON.CSS.aonBold())
			.addCell(new Label())
		;
	}
	public void setReportTitle( String title) {
		this.title.setText(title);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<JsVatContext> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
