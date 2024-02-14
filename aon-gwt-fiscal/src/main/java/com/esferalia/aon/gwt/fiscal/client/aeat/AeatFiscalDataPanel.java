package com.esferalia.aon.gwt.fiscal.client.aeat;

import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.ddff.AeatFiscalData;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AeatFiscalDataPanel extends SimpleLayoutPanel {
	
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	public AeatFiscalDataPanel( AeatFiscalData data ) {
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.CSS.aonScrollArea());
		setWidget(scrollPanel);
		
		FlowPanel container = new FlowPanel();
		scrollPanel.setWidget(container);
		container.add( title(data) );
		container.add( new AeatFiscalDataDatosGeneralesPanel(data) );
		container.add( new AeatFiscalDataTitularesPanel(data) );
		container.add( new AeatFiscalDataDomicilioPanel(data) );
	}

	private Widget title(AeatFiscalData data) {
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.CSS.aonTextCenter());
		Label title = new Label( " Datos Fiscales " );
		title.setStyleName(AON.CSS.aonBold());
		title.addStyleName(AON.CSS.aonTextUnderline());
		title.addStyleName(AON.CSS.aonFontXLarger());
		titlePanel.add(title);
		if (data.getDate() != null) {
			Label subTitle = new Label( " ( Fecha de proceso " + AON.DATE_FORMAT.format( data.getDate()) +" )");
			subTitle.setStyleName(AON.CSS.aonBold());
			subTitle.addStyleName(AON.CSS.aonTextUnderline());
			subTitle.addStyleName(AON.CSS.aonFontMedium());
			titlePanel.add(subTitle);
		}
		return titlePanel;
	}
	
	static <T> Label getEnumLabel( T enumObject , Function<? super T, String> func) {
		return new Label(Optional.ofNullable( enumObject ).map( func ).orElse(""));
	}
	
	static Label getDateLabel( Date date) {
		return new Label(date==null?"":AON.DATE_FORMAT.format( date));
	}
	static Label getBooleanLabel( boolean data) {
		return new Label(data?"SI":"NO");
	}
	
}
