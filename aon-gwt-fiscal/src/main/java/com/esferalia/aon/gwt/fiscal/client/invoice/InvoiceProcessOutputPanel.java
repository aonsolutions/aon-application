package com.esferalia.aon.gwt.fiscal.client.invoice;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCards;
import com.esferalia.aon.occam.api.model.finance.InvoiceProcessOutput;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class InvoiceProcessOutputPanel extends AonCards {

	public InvoiceProcessOutputPanel( InvoiceProcessOutput output ) {
		
		getContainer().getElement().getStyle().setProperty("justify-content", "center");
		output.getProcessErrorLevel()
			.ifPresent(el ->
				this.addCard( new AonCard(false, null, new Label("Level: " + InvoiceErrorLevel.name( el ))))
			)
		;
		if (AonStringUtils.isNotBlank(output.getProcessMessage())) {
			this.addCard( new AonCard(false, null, new Label("MSG: " + output.getProcessMessage())));
		}
		this.addCard( new AonCard(false, null, getPanel("Total facturas", AON.FMT_INT.format(output.getTotalCount() ))));
		this.addCard( new AonCard(false, null, getPanel("Importe total", AON.FMT.format(output.getTotalAmount() ))));
		
		this.breakRow();
		
		this.addCard( new AonCard(false, null, getPanel("Total IVA", AON.FMT.format(output.getTotalVAT() ))));
		this.addCard( new AonCard(false, null, getPanel("Total retenci\u00F3n IRPF", AON.FMT.format(output.getTotalRetention() ))));
		if (AonMathUtils.isNotZero( output.getTotalPrepaymentCount() )) {
			this.addCard( new AonCard(false, null, getPanel("Facturas con suplidos", AON.FMT_INT.format(output.getTotalPrepaymentCount() ))));
		}
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
