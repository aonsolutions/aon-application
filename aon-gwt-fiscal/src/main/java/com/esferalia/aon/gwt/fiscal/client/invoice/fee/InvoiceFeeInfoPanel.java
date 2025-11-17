package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCards;
import com.esferalia.aon.gwt.fiscal.client.invoice.fee.InvoiceFeeTable.InvoiceFeeTableInfo;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class InvoiceFeeInfoPanel extends AonCards {

	public InvoiceFeeInfoPanel( InvoiceFeeTableInfo info ) {
		
		getContainer().getElement().getStyle().setProperty("justify-content", "center");
		this.addCard( new AonCard(false, null
			, getPanel("Total facturas", AON.FMT_INT.format(info.getTotalCount() ))));
		this.addCard( new AonCard(false, null
			, getPanel("Total IVA", AON.FMT.format(info.getTotalVAT() ))));
		
		if (AonMathUtils.isNotZero( info.getTotalRetention() )) {
			this.addCard( new AonCard(false, null
				, getPanel("Total retenci\u00F3n", AON.FMT.format(info.getTotalRetention() ))));
		}

		if (AonMathUtils.isNotZero( info.getTotalPrepaymentCount() )) {
			this.addCard( new AonCard(false, null
					, getPanel("Facturas con suplidos", AON.FMT_INT.format(info.getTotalPrepaymentCount() ))));
		}
		
		this.addCard( new AonCard(false, null
			, getPanel("Importe total", AON.FMT.format(info.getTotalAmount() ))));
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
