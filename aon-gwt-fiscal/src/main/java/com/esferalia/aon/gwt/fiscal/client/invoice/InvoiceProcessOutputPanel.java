package com.esferalia.aon.gwt.fiscal.client.invoice;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCards;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCards.AonCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.occam.api.model.finance.InvoiceProcessOutput;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel.InvoiceErrorLevelVisitor;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class InvoiceProcessOutputPanel extends DockLayoutPanel {

	
	public InvoiceProcessOutputPanel( InvoiceProcessOutput output ) {
		super(Unit.PX);
		
		AonCards cards = new AonCards();
		cards.getElement().getStyle().setProperty("justify-content", "center");
		InvoiceErrorLevel errLevel = output.getProcessErrorLevel().orElse( null);
		if (AonStringUtils.isNotBlank(output.getProcessMessage())) {
			AonToolbar toolbar = new AonToolbar();
			addNorth( toolbar , AonToolbar.HEIGTH );
			String message = output.getProcessMessage();
			if (errLevel == null) {
				toolbar.showErrorMessage( message );
			} else {
				errLevel.visit( new InvoiceErrorLevelVisitor<Void>() {
					@Override public Void visitINF() { toolbar.showInfoMessage( message ); return null; }
					@Override public Void visitERR() { toolbar.showErrorMessage( message ); return null; }
					@Override public Void visitWRN() { return visitERR(); }
				});
			}
		}
		cards.addCard( new AonCard(false, null, getPanel("Total facturas", AON.FMT_INT.format(output.getInvoicesInfo().getTotalCount() ))));
		cards.addCard( new AonCard(false, null, getPanel("Importe total", AON.FMT.format(output.getInvoicesInfo().getTotalAmount() ))));
		cards.addCard( new AonCard(false, null, getPanel("Total I.V.A.", AON.FMT.format(output.getInvoicesInfo().getTotalVAT() ))));
		cards.addCard( new AonCard(false, null, getPanel("Total I.R.P.F.", AON.FMT.format(output.getInvoicesInfo().getTotalRetention() ))));
		if (AonMathUtils.isNotZero( output.getInvoicesInfo().getTotalPrepaymentCount() )) {
			cards.addCard( new AonCard(false, null, getPanel("Facturas con suplidos", AON.FMT_INT.format(output.getInvoicesInfo().getTotalPrepaymentCount() ))));
		}
		cards.breakRow();
		cards.addCard( new AonCard(false, null, getPanel("Total facturas", AON.FMT_INT.format(output.getProformasInfo().getTotalCount() ))));
		cards.addCard( new AonCard(false, null, getPanel("Importe total", AON.FMT.format(output.getProformasInfo().getTotalAmount() ))));
		cards.addCard( new AonCard(false, null, getPanel("Total I.V.A.", AON.FMT.format(output.getProformasInfo().getTotalVAT() ))));
		cards.addCard( new AonCard(false, null, getPanel("Total I.R.P.F.", AON.FMT.format(output.getProformasInfo().getTotalRetention() ))));
		if (AonMathUtils.isNotZero( output.getProformasInfo().getTotalPrepaymentCount() )) {
			cards.addCard( new AonCard(false, null, getPanel("Fras. Suplidos", AON.FMT_INT.format(output.getProformasInfo().getTotalPrepaymentCount() ))));
		}
		add( cards );
	}

	private FlowPanel getPanel( String title, String value ) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName( AON.CSS.aonFontXLarger() ); 
		panel.addStyleName( AON.CSS.aonTextCenter() );
		Label titleLabel = new Label(title);
		panel.add( titleLabel );
		Label valueLabel = new Label( value );
		valueLabel.addStyleName( AON.CSS.aonBold() );
		valueLabel.getElement().getStyle().setFontSize( 1.3, Unit.EM );
		panel.add( valueLabel );
		return panel;
	}
		
}
