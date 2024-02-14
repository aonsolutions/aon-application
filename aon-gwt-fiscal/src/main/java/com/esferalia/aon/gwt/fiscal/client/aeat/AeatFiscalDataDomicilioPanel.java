package com.esferalia.aon.gwt.fiscal.client.aeat;

import java.util.function.BiConsumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonGroupPanel;
import com.esferalia.aon.occam.api.model.ddff.AeatDomicilio;
import com.esferalia.aon.occam.api.model.ddff.AeatFiscalData;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class AeatFiscalDataDomicilioPanel extends AonGroupPanel {
	
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	public AeatFiscalDataDomicilioPanel( AeatFiscalData data ) {
		setHeaderLabel( "Domicilios" );
		
		if ( AonCollectionUtils.isEmpty(data.getTitulares() )) {
			Label basicLabel = new Label( "No se han encontrado domicilios." );
			addContent(basicLabel);
		} else {
			int i = 1;
			for ( AeatDomicilio dom : data.getDomicilios() ) {
				FlowPanel domicilioPanel = new FlowPanel();
				domicilioPanel.setStyleName(AON.CSS.aonPaddingLeft());
				if (AonCollectionUtils.size(data.getDomicilios()) > 1) {
					Label tabLabel = new Label("Domicilio n\u00BA: " + i++);
					tabLabel.setStyleName(AON.CSS.aonBold());
					tabLabel.addStyleName(AON.CSS.aonMarginTop() );
					domicilioPanel.add(  tabLabel );
				} else {
					setHeaderLabel("Domicilio");
				}
				
				AonDisplayGrid tab = new AonDisplayGrid();
				tab.addStyleName(AON.CSS.aonWidthAlmostAll() );
				tab.addStyleName(AON.CSS.aonBlockCenter());
				tab.addStyleName(AON.CSS.aonMarginTop() );
				
				TIPO_VIA
					.andThen(COD_VIA)
					.andThen(NOMBRE_LARGO)
					.andThen(NOMBRE_CORTO)
					.andThen(NUMERACION)
					.andThen(NUMERO)
					.andThen(CALIFICADOR_NUMERO)
					.andThen(BLOQUE)
					.andThen(PORTAL)
					.andThen(ESCALERA)
					.andThen(PLANTA)
					.andThen(PUERTA)
					.andThen(DATOS_COMPLEMENTARIOS)	
					.andThen(POBLACION)
					.andThen(CODIGO_POSTAL)	
					.andThen(CODIGO_MUNICIPIO)
					.andThen(MUNICIPIO)
					.andThen(CODIGO_PROVINCIA)
					.andThen(PROVINCIA)
					.andThen(REFERENCIA_CATASTRAL)
					.andThen(FECHA_MODIF)
				.accept(dom, tab);
				
				domicilioPanel.add(tab);
				addContent(domicilioPanel);
			}
		}
	}
	
	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> TIPO_VIA = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getTipoVia())) {
			tab.addRow()
				.addCell(new Label("Tipo de v\u00EDa") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getTipoVia() ) , AON.CSS.aonWidthAuto());
		}
	};
	
	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> COD_VIA  = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getCodVia())) {
			tab.addRow()
				.addCell(new Label("Codigo de v\u00EDa") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getCodVia() ) , AON.CSS.aonWidthAuto());
		}
	};
	
	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> NOMBRE_LARGO  = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getNombreLargo())) {
			tab.addRow()
				.addCell(new Label("Nombre largo de la v\u00EDa") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getNombreLargo() ) , AON.CSS.aonWidthAuto());
		}
	};

	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> NOMBRE_CORTO = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getNombreCorto())) {
			tab.addRow()
				.addCell(new Label("Nombre corto de la v\u00EDa") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getNombreCorto() ) , AON.CSS.aonWidthAuto());
		}
	};

	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> NUMERACION = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getNumeracion())) {
			tab.addRow()
				.addCell(new Label("Tipo de numeraci\u00F3n") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getNumeracion() ) , AON.CSS.aonWidthAuto());
		}
	};

	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> NUMERO = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getNumero())) {
			tab.addRow()
				.addCell(new Label("N\u00FAmero") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getNumero() ) , AON.CSS.aonWidthAuto());
		}
	};
	

	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> CALIFICADOR_NUMERO = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getCalificadorNumero())) {
			tab.addRow()
				.addCell(new Label("Calificador numero") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getCalificadorNumero() ) , AON.CSS.aonWidthAuto());
		}
	};
	
	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> BLOQUE = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getBloque())) {
			tab.addRow()
				.addCell(new Label("Bloque") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getBloque() ) , AON.CSS.aonWidthAuto());
		}
	};
	
	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> PORTAL = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getPortal())) {
			tab.addRow()
				.addCell(new Label("Portal") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getPortal() ) , AON.CSS.aonWidthAuto());
		}
	};
		
	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> ESCALERA = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getEscalera())) {
			tab.addRow()
				.addCell(new Label("Escalera") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getEscalera() ) , AON.CSS.aonWidthAuto());
		}
	};
		
	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> PLANTA = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getPlanta())) {
			tab.addRow()
				.addCell(new Label("Planta-piso") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getPlanta() ) , AON.CSS.aonWidthAuto());
		}
	};	
		
	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> PUERTA = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getPuerta())) {
			tab.addRow()
				.addCell(new Label("Puerta") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getPuerta() ) , AON.CSS.aonWidthAuto());
		}
	};
		
	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> DATOS_COMPLEMENTARIOS = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getDatosComplementarios())) {
			tab.addRow()
				.addCell(new Label("Datos complementarios") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getDatosComplementarios() ) , AON.CSS.aonWidthAuto());
		}
	};	
		
	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> POBLACION = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getPoblacion())) {
			tab.addRow()
				.addCell(new Label("Poblacion") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getPoblacion() ) , AON.CSS.aonWidthAuto());
		}
	};
		
	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> CODIGO_POSTAL = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getCodigoPostal())) {
			tab.addRow()
				.addCell(new Label("Codigo postal") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getCodigoPostal() ) , AON.CSS.aonWidthAuto());
		}
	};	
		
	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> CODIGO_MUNICIPIO = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getCodigoMunicipio())) {
			tab.addRow()
				.addCell(new Label("Codigo de municipio INE") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getCodigoMunicipio() ) , AON.CSS.aonWidthAuto());
		}
	};
		
	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> MUNICIPIO = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getMunicipio())) {
			tab.addRow()
				.addCell(new Label("Municipio") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getMunicipio() ) , AON.CSS.aonWidthAuto());
		}
	};
		
	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> CODIGO_PROVINCIA = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getCodigoProvincia())) {
			tab.addRow()
				.addCell(new Label("Codigo de provincia") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getCodigoProvincia() ) , AON.CSS.aonWidthAuto());
		}
	};
		
	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> PROVINCIA = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getProvincia())) {
			tab.addRow()
				.addCell(new Label("Provincia") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getProvincia() ) , AON.CSS.aonWidthAuto());
		}
	};
		
	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> REFERENCIA_CATASTRAL = (dom,tab) -> {
		if (AonStringUtils.isNotBlank(dom.getReferenciaCatastral())) {
			tab.addRow()
				.addCell(new Label("Referencia catastral") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(dom.getReferenciaCatastral() ) , AON.CSS.aonWidthAuto());
		}
	};
		
	private static final BiConsumer<AeatDomicilio,AonDisplayGrid> FECHA_MODIF = (dom,tab) -> {
		if (dom.getFechaModif() != null) {
			tab.addRow()
				.addCell(new Label("Fecha de modificaci\u00F3n") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(AeatFiscalDataPanel.getDateLabel(dom.getFechaModif()) , AON.CSS.aonWidthAuto());
		}
	};
	
}
