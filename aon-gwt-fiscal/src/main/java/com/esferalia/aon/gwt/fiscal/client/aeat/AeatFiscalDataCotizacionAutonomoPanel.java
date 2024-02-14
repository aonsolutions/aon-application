package com.esferalia.aon.gwt.fiscal.client.aeat;

import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonGroupPanel;
import com.esferalia.aon.occam.api.model.ddff.AeatFiscalData;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.Label;

public class AeatFiscalDataCotizacionAutonomoPanel extends AonGroupPanel {
	
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	public AeatFiscalDataCotizacionAutonomoPanel( AeatFiscalData data ) {
		setHeaderLabel("Cotizaciones de aut\u00F3nomo");
		
		if ( AonCollectionUtils.isEmpty(data.getTitulares() )) {
			Label basicLabel = new Label( "No se han encontrado cotizaciones de aut\u00F3nomo." );
			addContent( basicLabel );
		} else {
			AonDisplayGrid tab = new AonDisplayGrid();
//			tab.addStyleName(AON.CSS.aonWidthAlmostAll() );
//			tab.addStyleName(AON.CSS.aonBlockCenter());
//			tab.addStyleName(AON.CSS.aonMarginTop() );
			tab.addHeaderRow()
				.addCell( new Label("N\u00FAmero de afiliaci\u00F3n") )
				.addCell( new Label("Reg de cotizaci\u00F3n") )
				.addCell( new Label("Importe") );
			AonCollectionUtils.stream( data.getCotizacionesAutonomo())
			.forEach( ct -> {
				tab.addRow()
					.addCell( new Label( AonStringUtils.defaultIfBlank(ct.getNumeroAfiliacion()) ))
					.addCell( new Label( ct.getRegCotizacion() == null? "" : ct.getRegCotizacion().getDescription()))
					.addCell( new AonDoubleLabel( ct.getImporte() ) );
			});
			addContent(tab);
		}
	}
	
}
