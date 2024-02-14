package com.esferalia.aon.gwt.fiscal.client.aeat;

import java.util.function.BiConsumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonGroupPanel;
import com.esferalia.aon.occam.api.model.ddff.AeatDatosGenerales;
import com.esferalia.aon.occam.api.model.ddff.AeatFiscalData;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class AeatFiscalDataDatosGeneralesPanel extends AonGroupPanel {
	
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	public AeatFiscalDataDatosGeneralesPanel( AeatFiscalData data ) {
		setHeaderLabel("Datos generales");
		
		if ( AonCollectionUtils.isEmpty(data.getTitulares() )) {
			Label basicLabel = new Label( "No se han encontrado datos generales." );
			addContent( basicLabel );
		} else {
			int i = 1;
			for ( AeatDatosGenerales dat : data.getDatosGenerales() ) {
				FlowPanel titularPanel = new FlowPanel();
				titularPanel.setStyleName(AON.CSS.aonPaddingLeft());
				if (AonCollectionUtils.size(data.getDatosGenerales()) > 1) {
					Label tabLabel = new Label("Datos Generales n\u00BA: " + i++);
					tabLabel.setStyleName(AON.CSS.aonBold());
					tabLabel.addStyleName(AON.CSS.aonMarginTop() );
					titularPanel.add(  tabLabel );
				} 
				
				AonDisplayGrid tab = new AonDisplayGrid();
				tab.addStyleName(AON.CSS.aonWidthAlmostAll() );
				tab.addStyleName(AON.CSS.aonBlockCenter());
				tab.addStyleName(AON.CSS.aonMarginTop() );
				

				ESTADO_CIVIL
				.andThen(CONYUGE_NO_RESIDENTE)
				.andThen(CONYUGE_NO_RESIDENTE_UE)
				.accept(dat, tab);
				
				titularPanel.add(tab);
				addContent(titularPanel);
			}
		}
	}
	
	public static final BiConsumer<AeatDatosGenerales,AonDisplayGrid> ESTADO_CIVIL = (dat, tab) -> {
		if (dat.getEstadoCivil() != null ) {
			tab.addRow()
				.addCell(new Label("Estado_Civil"), AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(AeatFiscalDataPanel.getEnumLabel(dat.getEstadoCivil(), d -> d.getDescription()) , AON.CSS.aonWidthAuto());
		}
	};

	public static final BiConsumer<AeatDatosGenerales,AonDisplayGrid> CONYUGE_NO_RESIDENTE = (tit,tab) -> {
		tab.addRow()
			.addCell(new Label("C\u00F3nyuge no residente"), AON.CSS.aonTableLabel())
			.addCell(AeatFiscalDataPanel.getBooleanLabel(tit.isConyugeNoResidente()));
	};

	public static final BiConsumer<AeatDatosGenerales,AonDisplayGrid> CONYUGE_NO_RESIDENTE_UE = (tit,tab) -> {
		tab.addRow()
			.addCell(new Label("C\u00F3nyuge no residente que residee en UE/EE"), AON.CSS.aonTableLabel())
			.addCell(AeatFiscalDataPanel.getBooleanLabel(tit.isConyugeNoResidenteUE()));
	};
	
}
