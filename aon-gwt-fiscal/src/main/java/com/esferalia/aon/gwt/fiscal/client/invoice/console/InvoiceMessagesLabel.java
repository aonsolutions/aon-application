package com.esferalia.aon.gwt.fiscal.client.invoice.console;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel.InvoiceErrorLevelVisitor;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class InvoiceMessagesLabel extends AonTableButton {
	
	public InvoiceMessagesLabel( Invoice invoice ) {
		super( "" );
		if ( !invoice.isRecorded()) {
			addStyleName(AON.CSS.aonClickableLabel());
			if ( invoice.hasMessages()) {
				invoice.getMoreSeriousLevel()
					.map( l -> { setTitle( l.getLabel() );return l;} ) 
					.map( l -> l.visit(new BackgroundErrorLevelVisitor()) )
					.ifPresent(s -> addStyleName(s))
				;
				addClickHandler(event -> {
					final PopupPanel infoPanel = new PopupPanel( true, true );
					infoPanel.setWidth( "600px");
					infoPanel.setHeight("400px");
					infoPanel.add(new InvoiceRecorderMessagesPanel( invoice ));
					infoPanel.center();
					infoPanel.show();
					event.stopPropagation();
				});
			} else {
				addStyleName(AON.CSS.aonIconCircleGreen());
			}
		} else {
		}
	}

	private static class BackgroundErrorLevelVisitor implements InvoiceErrorLevelVisitor<String> {
		@Override public String visitINF() {return AON.CSS.aonIconCircleYellow();}
		@Override public String visitWRN() {return AON.CSS.aonIconCircleOrange();}
		@Override public String visitERR() {return AON.CSS.aonIconCircleRed();}
	}		

	private static class InvoiceRecorderMessagesPanel extends FlowPanel {
		public InvoiceRecorderMessagesPanel( Invoice invoice) {
			setStyleName(AON.CSS.aonWidthAll());
			addStyleName(AON.CSS.aonPadding());
			
			AonDisplayGrid errors = new AonDisplayGrid();
			errors.addStyleName(AON.CSS.aonWidthAlmostAll());
			errors.addStyleName(AON.CSS.aonBlockCenter());
			errors.addHeaderRow()
				.addCell(new Label(""), AON.CSS.aonWidth30())
				.addCell(new Label(AON.MSG.message()), AON.CSS.aonWidthAuto())
			;
			AonCollectionUtils.stream(invoice.getMessages())
				.forEach( e -> {
					Label errorIcon = new Label("");
					errorIcon.setStyleName(AON.CSS.aonIconLabel());
					errorIcon.addStyleName( e.getLevel().visit(new BackgroundErrorLevelVisitor())  );
					
					Label errorMsg = new Label(AonStringUtils.abbreviate(e.getMessage(), 40));
					errorMsg.setTitle(e.getMessage());
					errors.addRow()
						.addCell(errorIcon, AON.CSS.aonWidth20())
						.addCell(errorMsg, AON.CSS.aonWidthAuto());
				})
			;
			this.add(errors);
		}
	}
	
}

