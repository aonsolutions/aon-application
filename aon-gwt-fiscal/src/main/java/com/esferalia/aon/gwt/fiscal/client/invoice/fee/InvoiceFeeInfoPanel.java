package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCards;
import com.esferalia.aon.gwt.fiscal.client.invoice.fee.InvoiceFeeTable.InvoiceFeeTableInfo;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class InvoiceFeeInfoPanel extends AonCards {

	public InvoiceFeeInfoPanel( InvoiceFeeTableInfo info ) {
		
		getContainer().getElement().getStyle().setProperty("justify-content", "center");
		if (info.getErrorLevel() != null) {
			this.addCard( new AonCard(false, null, new Label("Level: " + InvoiceErrorLevel.name( info.getErrorLevel()))));
		}
		if (AonStringUtils.isNotBlank(info.getMessage())) {
			this.addCard( new AonCard(false, null, new Label("MSG: " + info.getMessage())));
		}
		this.addCard( new AonCard(false, null, getPanel("Total facturas", AON.FMT_INT.format(info.getTotalCount() ))));
		this.addCard( new AonCard(false, null, getPanel("Total IVA", AON.FMT.format(info.getTotalVAT() ))));
		this.addCard( new AonCard(false, null, getPanel("Total retenci\u00F3n IRPF", AON.FMT.format(info.getTotalRetention() ))));
		this.addCard( new AonCard(false, null, getPanel("Facturas con suplidos", AON.FMT_INT.format(info.getTotalPrepaymentCount() ))));
		this.addCard( new AonCard(false, null, getPanel("Importe total", AON.FMT.format(info.getTotalAmount() ))));
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
