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
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class JsVatContextBreakdownGridPanel extends FlowPanel implements HasSelectionHandlers<JsVatContext>{
	
	private static String[] COLUMN_LENGTHS = new String[] {
			 AON.CSS.aonWidth60()
			,AON.CSS.aonWidth60()
			,AON.CSS.aonWidth30()
			,AON.CSS.aonWidth30()	
			,AON.CSS.aonWidth30()	
			,AON.CSS.aonWidth30()	
			,AON.CSS.aonWidth30()	
			,AON.CSS.aonWidth30()	
			,AON.CSS.aonWidth30()	
			,AON.CSS.aonWidth80()
			,AON.CSS.aonWidth100()
			,AON.CSS.aonWidth120()
			,AON.CSS.aonWidthAuto()
			,AON.CSS.aonWidth80()
			,AON.CSS.aonWidth80()
			,AON.CSS.aonWidth80()
			,AON.CSS.aonWidth80()
			,AON.CSS.aonWidth40()
			,AON.CSS.aonWidth80()
			,AON.CSS.aonWidth40()
			,AON.CSS.aonWidth80()
			,AON.CSS.aonWidth40()
			,AON.CSS.aonWidth80()
			,AON.CSS.aonWidth40()
			,AON.CSS.aonWidth80()
			,AON.CSS.aonWidth100()
	};

	private static final int PAGE_SIZE = 500;
	
	private JsArray<JsVatContext> data;
	private FlowPanel moreDataPanel;
	private AonDisplayGrid footerTable;

	private boolean something;
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
	public HandlerRegistration addSelectionHandler(SelectionHandler<JsVatContext> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
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
		int i = 0;
		grid.addHeaderRow()
			.addCell(new Label("Tipo"),COLUMN_LENGTHS[i++])
			.addCell(new Label("Tran."),COLUMN_LENGTHS[i++])
			.addCell(serviceLabel,COLUMN_LENGTHS[i++])
			.addCell(investmentLabel,COLUMN_LENGTHS[i++])
			.addCell(farmerLabel,COLUMN_LENGTHS[i++])
			.addCell(rectifiedLabel,COLUMN_LENGTHS[i++])
			.addCell(accrualLabel,COLUMN_LENGTHS[i++])
			.addCell(importationLabel,COLUMN_LENGTHS[i++])
			.addCell(duaLabel,COLUMN_LENGTHS[i++])
			.addCell(new Label("Epigr."),COLUMN_LENGTHS[i++])
			.addCell(new Label("N\u00BA.Doc"),COLUMN_LENGTHS[i++],AON.CSS.aonNowrap())
			.addCell(new Label("Doc.Tit."),COLUMN_LENGTHS[i++],AON.CSS.aonNowrap())
			.addCell(new Label("Nombre/raz\u00F3n social"),COLUMN_LENGTHS[i++])
			.addCell(new Label("Fec. Fac."),COLUMN_LENGTHS[i++],AON.CSS.aonNowrap())
			.addCell(new Label("Fec. Imp."),COLUMN_LENGTHS[i++],AON.CSS.aonNowrap())
			.addCell(new Label("Tipo IVA"),COLUMN_LENGTHS[i++],AON.CSS.aonNowrap())
			.addCell(new Label("Base Imp."),AON.CSS.aonTextRight(),COLUMN_LENGTHS[i++],AON.CSS.aonNowrap())		
			.addCell(new Label("% IVA"),AON.CSS.aonTextRight(),COLUMN_LENGTHS[i++],AON.CSS.aonNowrap())
			.addCell(new Label("Cuota"),AON.CSS.aonTextRight(),COLUMN_LENGTHS[i++])
			.addCell(new Label("% RE"),AON.CSS.aonTextRight(),COLUMN_LENGTHS[i++],AON.CSS.aonNowrap())
			.addCell(new Label("Cuota RE"),AON.CSS.aonTextRight(),COLUMN_LENGTHS[i++],AON.CSS.aonNowrap())
			.addCell(new Label("% Ded."),AON.CSS.aonTextRight(),COLUMN_LENGTHS[i++],AON.CSS.aonNowrap())
			.addCell(new Label("Cuota Ded"),AON.CSS.aonTextRight(),COLUMN_LENGTHS[i++],AON.CSS.aonNowrap())
			.addCellIf(showProrrate, new Label("% Pror.."),AON.CSS.aonTextRight(),COLUMN_LENGTHS[i++],AON.CSS.aonNowrap())
			.addCellIf(showProrrate, new Label("Cuota Pro."),AON.CSS.aonTextRight(),COLUMN_LENGTHS[i++],AON.CSS.aonNowrap())
			.addCell(new Label("N\u00BA Referencia"),COLUMN_LENGTHS[i++],AON.CSS.aonNowrap())
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
	
	public AonDisplayGridRow addRow(JsVatContext vc) {
		something = true;
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
		int i = 0;
		row
			.addCell(new Label(ensure(invoiceType,invoiceType::getAbbrDescription)),COLUMN_LENGTHS[i++])
			.addCell(new Label(ensure(invoiceTransactionType,invoiceTransactionType::getTediName)),COLUMN_LENGTHS[i++])
			.addCell(new AonBooleanLabel(vc.isService()			, "S", AON.MSG.service() ),COLUMN_LENGTHS[i++])	
			.addCell(new AonBooleanLabel(vc.isInvestment()		, "I", AON.MSG.investment() ),COLUMN_LENGTHS[i++])
			.addCell(new AonBooleanLabel(vc.isFarmerRegime()	, "A", AON.MSG.farmerRegime() ),COLUMN_LENGTHS[i++])
			.addCell(new AonBooleanLabel(vc.isRectification()	, "R", AON.MSG.rectifiedInvoice()),COLUMN_LENGTHS[i++])
			.addCell(new AonBooleanLabel(vc.isVatAccrualRegime(), "C", AON.MSG.vatAccrualPayment()),COLUMN_LENGTHS[i++])
			.addCell(new AonBooleanLabel(vc.isVatImportation()	, "M", AON.MSG.vatImportationRegime()),COLUMN_LENGTHS[i++])
			.addCell(new AonBooleanLabel(vc.hasDuaLinked()		, "D", AON.MSG.DUALinked()),COLUMN_LENGTHS[i++])
			.addCell(new Label(ensure(vc.getEpigraph(), vc::getEpigraph, AonStringUtils.EMPTY)),COLUMN_LENGTHS[i++])
			.addCell(new Label(ensure(vc.getDocumentNumber(), vc::getDocumentNumber, AonStringUtils.EMPTY)),COLUMN_LENGTHS[i++])
			.addCell(new Label(ensure(vc.getRegistryDocument(), vc::getRegistryDocument, AonStringUtils.EMPTY)),COLUMN_LENGTHS[i++])
			.addCell(new Label(ensure(vc.getRegistryName(), () -> AonStringUtils.abbreviate(vc.getRegistryName(),25), AonStringUtils.EMPTY)),COLUMN_LENGTHS[i++])
			.addCell(issueDateLabel,COLUMN_LENGTHS[i++])
			.addCell(taxDateLabel,COLUMN_LENGTHS[i++])
			.addCell(new Label(ensure(vatDeductionType.getAbbr(), vatDeductionType::getAbbr, AonStringUtils.EMPTY)),COLUMN_LENGTHS[i++])
			.addCell(new AonDoubleLabel(vc.getBase()),AON.CSS.aonTextRight(),COLUMN_LENGTHS[i++])
			.addCell(new AonDoubleLabel(vc.getPercentage()),AON.CSS.aonTextRight(),COLUMN_LENGTHS[i++])
			.addCell(new AonDoubleLabel(vc.getQuota()),AON.CSS.aonTextRight(),COLUMN_LENGTHS[i++])
			.addCell(surchargePercentLabel ,AON.CSS.aonTextRight(),COLUMN_LENGTHS[i++])
			.addCell(surchargeQuotaLabel   ,AON.CSS.aonTextRight(),COLUMN_LENGTHS[i++])
			.addCell(deductiblePercentLabel,AON.CSS.aonTextRight(),COLUMN_LENGTHS[i++])
			.addCell(deductibleQuotaLabel,AON.CSS.aonTextRight(),COLUMN_LENGTHS[i++])
			.addCellIf(showProrrate, prorratePercentLabel,AON.CSS.aonTextRight(),AonMathUtils.isZero(vc.getProrratePercent())?AON.CSS.aonColorBlack():AON.CSS.aonColorBlue(),COLUMN_LENGTHS[i++])
			.addCellIf(showProrrate, prorrateQuotaLabel,AON.CSS.aonTextRight(),AonMathUtils.isZero(vc.getProrratePercent())?AON.CSS.aonColorBlack():AON.CSS.aonColorBlue(),COLUMN_LENGTHS[i++])
			.addCell(new Label(ensure(vc.getReferenceCode(), vc::getReferenceCode, AonStringUtils.EMPTY)),COLUMN_LENGTHS[i++])
		;
		return row;
	}

	public AonDisplayGrid getSummaryTable() {
		AonDisplayGrid tab = new AonDisplayGrid();
		
		Label sumBaseTitle = new Label( "TOTAL base imponible" );
		sumBaseTitle.setStyleName(AON.CSS.aonBold());
		sumBaseTitle.addStyleName(AON.CSS.aonBackgroundLigthGray());
		
		AonDoubleLabel sumBaseLabel = new AonDoubleLabel(sumBase);
		sumBaseLabel.addStyleName(AON.CSS.aonTextRight());
		sumBaseLabel.addStyleName(AON.CSS.aonBold());

		tab.addLabelWidgetRow(sumBaseTitle, sumBaseLabel);

		Label sumQuotaTitle = new Label("TOTAL cuota IVA");
		sumQuotaTitle.setStyleName(AON.CSS.aonBold());
		sumQuotaTitle.addStyleName(AON.CSS.aonBackgroundLigthGray());
		
		AonDoubleLabel sumQuotaLabel = new AonDoubleLabel(sumQuota);
		sumQuotaLabel.addStyleName(AON.CSS.aonTextRight());
		sumQuotaLabel.addStyleName(AON.CSS.aonBold());
		
		tab.addLabelWidgetRow(sumQuotaTitle, sumQuotaLabel);
		
		if ( AonMathUtils.isNotZero(sumSurchargeQuota)) {
			
			Label sumSurchargeQuotaTitle = new Label("TOTAL cuota recargo equivalencia");
			sumSurchargeQuotaTitle.setStyleName(AON.CSS.aonBold());
			sumSurchargeQuotaTitle.addStyleName(AON.CSS.aonBackgroundLigthGray());
			
			AonDoubleLabel sumSurchargeQuotaLabel = new AonDoubleLabel(sumSurchargeQuota);
			sumSurchargeQuotaLabel.addStyleName(AON.CSS.aonTextRight());
			sumSurchargeQuotaLabel.addStyleName(AON.CSS.aonBold());
			
			tab.addLabelWidgetRow(sumSurchargeQuotaTitle, sumSurchargeQuotaLabel);
			
			double sumSurcharge = AonMathUtils.round(sumQuota + sumSurchargeQuota);
			
			Label sumSurchargeTitle = new Label("TOTAL cuota IVA + cuota recargo equivalencia");
			sumSurchargeTitle.setStyleName(AON.CSS.aonBold());
			sumSurchargeTitle.addStyleName(AON.CSS.aonBackgroundLigthGray());
			
			AonDoubleLabel sumSurchargeLabel = new AonDoubleLabel(sumSurcharge);
			sumSurchargeLabel.addStyleName(AON.CSS.aonTextRight());
			sumSurchargeLabel.addStyleName(AON.CSS.aonBold());
			
			tab.addLabelWidgetRow(sumSurchargeTitle, sumSurchargeLabel);
			
		}
		
		if ( AonMathUtils.isNotZero(sumDeductibleQuota)) {

			Label sumDeductibleQuotaTitle = new Label("TOTAL cuota IVA deducible");
			sumDeductibleQuotaTitle.setStyleName(AON.CSS.aonBold());
			sumDeductibleQuotaTitle.addStyleName(AON.CSS.aonBackgroundLigthGray());
	
			AonDoubleLabel sumDeductibleQuotaLabel = new AonDoubleLabel(sumDeductibleQuota);
			sumDeductibleQuotaLabel.addStyleName(AON.CSS.aonTextRight());
			sumDeductibleQuotaLabel.addStyleName(AON.CSS.aonBold());
			
			tab.addLabelWidgetRow(sumDeductibleQuotaTitle, sumDeductibleQuotaLabel);
		}
		
		if (showProrrate) {
			AonDoubleLabel sumProrratedQuotaLabel = new AonDoubleLabel(sumProrratedQuota);
			sumProrratedQuotaLabel.addStyleName(AON.CSS.aonTextRight());
			sumProrratedQuotaLabel.addStyleName(AON.CSS.aonColorBlue());
			sumProrratedQuotaLabel.addStyleName(AON.CSS.aonBold());
			tab.addLabelWidgetRow("TOTAL cuota IVA prorrateada", sumProrratedQuotaLabel);
		}
		
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		return tab;
	}
	
	public void setReportTitle( String title) {
		this.title.setText(title);
	}
	
	public boolean hasSomething() {
		return something;
	}
	
	public void render(JsArray<JsVatContext> array) {
		this.data = array;
		if (data == null || data.length() == 0) {
			FlowPanel linePanel = new FlowPanel();
			linePanel.setStyleName(AON.CSS.aonMargin());
			Label noDataLabel = new Label(AON.MSG.noData());
			noDataLabel.setStyleName(AON.CSS.aonTextCenter());
			noDataLabel.addStyleName(AON.CSS.aonBold());
			linePanel.add(noDataLabel);
			add(linePanel);
		} else {
			for (int i = 0 ; i < data.length(); i++) {
				JsVatContext vc = data.get(i);
				sumBase += vc.getBase();
				sumQuota += vc.getQuota();
				sumSurchargeQuota += vc.getSurchargeQuota();
				sumDeductibleQuota += vc.isSales()?0.0:vc.getDeductibleQuota();
				sumProrratedQuota += vc.isProrrated()?vc.getProrrateQuota():vc.getDeductibleQuota();
			}
			paintRows( 0 );
		}
	}

	private void paintRows(int offset) {
		if ( offset > 0 ) {
			if (footerTable != null) footerTable.removeFromParent();
			if (moreDataPanel != null) moreDataPanel.removeFromParent();
		}
		int i = offset;
		int x = AonMathUtils.min(data.length(), (offset + PAGE_SIZE) );
		for (; i < x; i++) {
			addRow( data.get(i) );
		}
		if ( i < data.length()) {
			moreDataPanel = new FlowPanel();
			moreDataPanel.setStyleName(AON.CSS.aonMargin());
			moreDataPanel.addStyleName(AON.CSS.aonBorder());
			moreDataPanel.addStyleName(AON.CSS.aonBackgroundLigthYellow());
			Label moreDataLabel = new Label(" Se han mostrando " + i + " de " + data.length() + " filas. Click para mostrar m\u00E1s filas.");
			moreDataLabel.setStyleName(AON.CSS.aonMargin());
			moreDataLabel.addStyleName(AON.CSS.aonTextCenter());
			moreDataLabel.addStyleName(AON.CSS.aonBold());
			moreDataLabel.addStyleName(AON.CSS.aonClickable());
			int current = i;
			moreDataLabel.addClickHandler( e -> {
				paintRows( current );
			});
			moreDataPanel.add(moreDataLabel);
			add(moreDataPanel);
		}
		footerTable = getSummaryTable();
		add(footerTable);
	}
}
