package com.esferalia.aon.gwt.fiscal.client.accounting;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class PrintReportDialog extends CustomDialog {
	
	public interface IPrintReportDialogCallback {
		public void onAccept(ReportMetadata metadata);
		public void onCancel();
		public void onError(String msg);
	};

	private TextBox title = new TextBox();
	private TextBox subject = new TextBox();
	private CheckBox showCover = new CheckBox();
	private IntegerBox pageOffset = new IntegerBox();
	private TextBox pageOffsetText = new TextBox();
	private CheckBox hideFilter = new CheckBox();
	private TextBox headerText = new TextBox();
	private CheckBox hideDateTimeOnFooter = new CheckBox();
	private TextBox footerText = new TextBox();

	public PrintReportDialog(final ReportMetadata metadata,final IPrintReportDialogCallback callback) {
		setCaption("Impresi\u00F3n PDF");
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		title.setStyleName(AON.AON_CSS.aonInputText());
		title.setVisibleLength(50);
		subject.setStyleName(AON.AON_CSS.aonInputText());
		subject.setVisibleLength(40);
		pageOffset.setVisibleLength(5);
		pageOffsetText.setStyleName(AON.AON_CSS.aonInputText());
		pageOffsetText.setVisibleLength(40);
		headerText.setStyleName(AON.AON_CSS.aonInputText());
		headerText.setVisibleLength(40);
		footerText.setStyleName(AON.AON_CSS.aonInputText());
		footerText.setVisibleLength(40);
		
		FlexTable tab = new FlexTable();

		FlowPanel rootPanel = new FlowPanel(); 
		tab.setCellPadding(0);
		tab.setCellSpacing(0);
		tab.setStyleName(AON.AON_CSS.aonMarginTop());
		tab.addStyleName(AON.AON_CSS.aonMarginBottom());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		ColumnFormatter cf = tab.getColumnFormatter();
		cf.setWidth(0, "130px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		cf.setWidth(1, "250px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );

		int row = 0;
		
		// TITLE
		title.setValue(metadata.getTitle());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label("T\u00EDtulo"));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 1, title);
		row++;
		
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.appendHtmlConstant("&nbsp;");
		
		
		tab.setWidget(row, 0, new HTML( builder.toSafeHtml()));
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonMarginTop());
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		row++;
		
		// SHOW COVER
		showCover.setValue(metadata.isShowCover());
		showCover.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				subject.setEnabled(showCover.getValue());
			}
		});
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label("Mostrar portada"));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 1, showCover);
		row++;

		// SUBJECT
		subject.setValue(metadata.getSubject());
		subject.setEnabled(metadata.isShowCover());
		tab.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeftImportant());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextRight());
		tab.setWidget(row, 0, new Label("Subt\u00EDtulo (portada)"));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 1, subject);
		row++;

		tab.setWidget(row, 0, new HTML( builder.toSafeHtml()));
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonMarginTop());
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		row++;
		
		// PAGE OFFSET
		pageOffset.setValue(metadata.getPageOffset());
		pageOffset.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				pageOffsetText.setEnabled(pageOffset.getValue() > 0);
			}
		});
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label("Contador de p\u00E1gina a partir de "));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 1, pageOffset);
		row++;

		// PAGE OFFSET TEXT
		pageOffsetText.setValue(metadata.getPageOffsetText());
		pageOffsetText.setEnabled(metadata.getPageOffset() > 0);
		tab.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeftImportant());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextRight());
		tab.setWidget(row, 0, new Label("Texto para p\u00E1gina global"));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 1, pageOffsetText);
		row++;
		
		tab.setWidget(row, 0, new HTML( builder.toSafeHtml()));
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonMarginTop());
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		row++;
		
		// HIDE FILTER
		hideFilter.setValue(metadata.isHideFilter());
		hideFilter.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				headerText.setEnabled(hideFilter.getValue());
			}
		});
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label("Ocultar datos de filtro en cabecera"));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 1, hideFilter);
		row++;
		
		// HEADER TEXT
		headerText.setEnabled(metadata.isHideFilter());
		headerText.setValue(metadata.getHeaderText());
		tab.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeftImportant());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextRight());
		tab.setWidget(row, 0, new Label("Texto alternativo cabecera"));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 1, headerText);
		row++;
		
		tab.setWidget(row, 0, new HTML( builder.toSafeHtml()));
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonMarginTop());
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		row++;
		
		// HIDE DATE TIME ON FOOTER
		hideDateTimeOnFooter.setValue(metadata.isHideDateTimeOnFooter());
		hideDateTimeOnFooter.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				footerText.setEnabled(hideDateTimeOnFooter.getValue());
			}
		});
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label("Ocultar fecha/hora en pie de p\u00E1gina"));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 1, hideDateTimeOnFooter);
		row++;
		
		// FOOTER TEXT
		footerText.setValue(metadata.getFooterText());
		footerText.setEnabled(metadata.isHideDateTimeOnFooter());
		tab.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeftImportant());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextRight());
		tab.setWidget(row, 0, new Label("Texto alternativo pie de p\u00E1g."));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 1, footerText);
		row++;
		
		rootPanel.add(tab);
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.AON_CSS.aonPadding());
		buttonsPanel.addStyleName(AON.AON_CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.AON_CSS.aonConfirmDialogOkButton());
		acceptButton.setText( AON.MSG.accept());
		
		acceptButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
				callback.onAccept(metadata
					.setTitle(title.getValue())
					.setSubject(subject.getValue())
					.setShowCover(showCover.getValue())
					.setPageOffset(pageOffset.getValue())
					.setPageOffsetText(pageOffsetText.getValue())
					.setHideFilter(hideFilter.getValue())
					.setHeaderText(headerText.getValue())
					.setHideDateTimeOnFooter(hideDateTimeOnFooter.getValue())
					.setFooterText(footerText.getValue())
				);
			}
		});
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.AON_CSS.aonConfirmDialogCancelButton());
    	cancelButton.addStyleName(AON.AON_CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
				callback.onCancel();
			}
			
		});
		buttonsPanel.add(cancelButton);
		rootPanel.add(buttonsPanel);
		add(rootPanel);
	}

}
