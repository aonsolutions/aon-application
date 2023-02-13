package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;

import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class JsIRPFBreakdownInvoiceGridPanel extends FlowPanel implements HasSelectionHandlers<JsIRPFBreakdown>{
	
	private final Label title;
	private final Label subTitle;
	private final AonDisplayGrid grid;
	private double sumBase = 0.0;
	private double sumQuota = 0.0;
	
	public JsIRPFBreakdownInvoiceGridPanel() {
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
		grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonMarginTop());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		paintHeader();
		add( grid );
	}
	
	@Override
	public void setTitle( String title) {
		super.setTitle(title);
		this.title.setText(title);	
	}
	public void setSubTitle( String subTitle) {
		this.subTitle.setText(subTitle);	
	}
	
	private void paintHeader() {
		grid.addHeaderRow()
			.addCell(new Label("Tipo"),AON.CSS.aonWidth40())
			.addCell(new Label("Tipo Ret."),AON.CSS.aonWidth100(), AON.CSS.aonNowrap())
			.addCell(new Label("Epigr."),AON.CSS.aonWidth80())
			.addCell(new Label("N\u00BA Documento"),AON.CSS.aonWidth100(), AON.CSS.aonNowrap())
			.addCell(new Label("Doc.Tit."),AON.CSS.aonWidthAuto(), AON.CSS.aonNowrap())
			.addCell(new Label("Nombre/raz\u00F3n social"),AON.CSS.aonWidthAuto())
			.addCell(new Label("Fec. Fac."),AON.CSS.aonWidth80(), AON.CSS.aonNowrap())
			.addCell(new Label("Fec. Imp."),AON.CSS.aonWidth80(), AON.CSS.aonNowrap())
			.addCell(new Label("Base Imp."),AON.CSS.aonTextRight(),AON.CSS.aonWidth80(), AON.CSS.aonNowrap())		
			.addCell(new Label("% IRPF"),AON.CSS.aonTextRight(),AON.CSS.aonWidth40(), AON.CSS.aonNowrap())
			.addCell(new Label("Cuota"),AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
			.addCell(new Label("N\u00BA Referencia"),AON.CSS.aonWidth100(), AON.CSS.aonNowrap())
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
	
	
	public final native boolean hasInvoiceId(int invoice ) /*-{
		return !isNaN(invoice) && invoice != 0;
	}-*/;
	
	public void addRow(JsIRPFBreakdown br) {
		InvoiceType invoiceType = InvoiceType.safeValueOf(br.getInvoiceType());
		WithholdingType withholdingType = WithholdingType.safeValueOf(br.getWithholdingType());
		AonDisplayGridRow row = grid.addRow();
		if ( hasInvoiceId( br.getInvoice() ) ) {
			row.addClickHandler(event -> SelectionEvent.fire(this, br));
		}
		row
			.addCell(new Label(ensure(invoiceType,invoiceType::getAbbrDescription)))
			.addCell(new Label(ensure(withholdingType,() -> withholdingType.getAbbreviatedDescription())))
			.addCell(new Label(ensure(br.getEpigraph(), br::getEpigraph, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(br.getDocumentNumber(), br::getDocumentNumber, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(br.getRegistryDocument(), br::getRegistryDocument, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(br.getRegistryName(), () -> AonStringUtils.abbreviate(br.getRegistryName(),25), AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(br.getIssueDate(), () -> AON.DATE_FORMAT.format(br.getIssueDate()), AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(br.getTaxDate(), () -> AON.DATE_FORMAT.format(br.getTaxDate()), AonStringUtils.EMPTY)))
			.addCell(new Label(AON.CURRENCY_FORMAT.format(br.getBase())),AON.CSS.aonTextRight())
			.addCell(new Label(AonMathUtils.isLessThanZero( br.getPercent())
					?"------" 
					:AON.CURRENCY_FORMAT.format(br.getPercent()) + "%"),AON.CSS.aonTextRight())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(br.getQuota())),AON.CSS.aonTextRight())
			.addCell(new Label(ensure(br.getReferenceCode(), br::getReferenceCode, AonStringUtils.EMPTY)))
		;
		sumBase += br.getBase();
		sumQuota += br.getQuota();
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
			.addCell(new Label(AON.MSG.total()),AON.CSS.aonTextRight(),AON.CSS.aonBold(),AON.CSS.aonTextUppercase())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(sumBase)),AON.CSS.aonTextRight(),AON.CSS.aonBold())
			.addCell(new Label())
			.addCell(new Label(AON.CURRENCY_FORMAT.format(sumQuota)),AON.CSS.aonTextRight(),AON.CSS.aonBold())
			.addCell(new Label())
		;
	}
	public void setReportTitle( String title) {
		this.title.setText(title);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<JsIRPFBreakdown> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
