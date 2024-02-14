package com.esferalia.aon.gwt.fiscal.client.aeat;

import java.util.function.BiConsumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonGroupPanel;
import com.esferalia.aon.occam.api.model.ddff.AeatFiscalData;
import com.esferalia.aon.occam.api.model.ddff.AeatTitular;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class AeatFiscalDataTitularesPanel extends AonGroupPanel {
	
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	public AeatFiscalDataTitularesPanel( AeatFiscalData data ) {
		setHeaderLabel("Titulares");
		
		if ( AonCollectionUtils.isEmpty(data.getTitulares() )) {
			Label basicLabel = new Label( "No se han encontrado titulares." );
			addContent( basicLabel );
		} else {
			int i = 1;
			for ( AeatTitular titular : data.getTitulares() ) {
				FlowPanel titularPanel = new FlowPanel();
				titularPanel.setStyleName(AON.CSS.aonPaddingLeft());
				if (AonCollectionUtils.size(data.getTitulares()) > 1) {
					Label tabLabel = new Label("Titular n\u00BA: " + i++);
					tabLabel.setStyleName(AON.CSS.aonBold());
					tabLabel.addStyleName(AON.CSS.aonMarginTop() );
					titularPanel.add(  tabLabel );
				} else {
					setHeaderLabel("Titular");
				}
				
				AonDisplayGrid tab = new AonDisplayGrid();
				tab.addStyleName(AON.CSS.aonWidthAlmostAll() );
				tab.addStyleName(AON.CSS.aonBlockCenter());
				tab.addStyleName(AON.CSS.aonMarginTop() );
				
				TITULAR_NIF
				.andThen(TITULAR_APELLIDOS_NOMBRE)
				.andThen(TITULAR_DISCAPACIDAD_IRPF)
				.andThen(TITULAR_DISCAPACIDAD_990)
				.andThen(TITULAR_FECHA_NACIMIENTO)
				.andThen(TITULAR_SEXO)
				.andThen(TITULAR_FECHA_FALLECIMIENTO)
				.andThen(TITULAR_COMUNIDAD_AUTONOMA)
				.andThen(TITULAR_IBAN)
				.andThen(TITULAR_SWIFT)
				.andThen(TITULAR_FECHA_ADQUISICION_VIVIENDA_HABITUAL)
				.andThen(TITULAR_NUMERO_PRESTAMO_HIPOTECARIO)
				.andThen(TITULAR_PORCENTAJE_PRESTAMO)
				.andThen(TITULAR_DEDUCCION_VIVIENDA_EJERCICIO_ANTERIOR)
				.andThen(TITULAR_IGLESIA_CATOLICA)
				.andThen(TITULAR_FINES_SOCIALES)
				.accept(titular, tab);
				
				titularPanel.add(tab);
				addContent(titularPanel);
			}
		}
	}
	
	private static final BiConsumer<AeatTitular,AonDisplayGrid> TITULAR_NIF = (tit,tab) -> {
		if (AonStringUtils.isNotBlank(tit.getNif())) {
			tab.addRow()
				.addCell(new Label("NIF") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(tit.getNif()) , AON.CSS.aonWidthAuto());
		}
	};
	
	private static final BiConsumer<AeatTitular,AonDisplayGrid> TITULAR_APELLIDOS_NOMBRE = (tit, tab) -> {
		if (AonStringUtils.isNotBlank(tit.getApellidosNombre())) {
			tab.addRow()
				.addCell(new Label("Apellidos y Nombre"), AON.CSS.aonTableLabel())
				.addCell(new Label(tit.getApellidosNombre()));
		}
	};
	
	private static final BiConsumer<AeatTitular,AonDisplayGrid> TITULAR_DISCAPACIDAD_IRPF = (tit,tab) -> {
	if (tit.getDiscapacidadIRPF() != null) {
			tab.addRow()
				.addCell(new Label("Discapacidad-IRPF"), AON.CSS.aonTableLabel())
				.addCell(AeatFiscalDataPanel.getEnumLabel(tit.getDiscapacidadIRPF(), d -> d.getDescription()));
		}
	};
	
	private static final BiConsumer<AeatTitular,AonDisplayGrid> TITULAR_DISCAPACIDAD_990 = (tit, tab) -> {
		if (tit.getDiscapacidad990() != null) {
			tab.addRow()
				.addCell(new Label("Discapacidad-990"), AON.CSS.aonTableLabel())
				.addCell(AeatFiscalDataPanel.getEnumLabel(tit.getDiscapacidad990(), d -> d.getDescription()));
		}
	};
	
	private static final BiConsumer<AeatTitular,AonDisplayGrid> TITULAR_FECHA_NACIMIENTO = (tit,tab) -> {
		if (tit.getFechaNacimiento() != null ) {
			tab.addRow()
				.addCell(new Label("Fecha de nacimiento"), AON.CSS.aonTableLabel())
				.addCell(AeatFiscalDataPanel.getDateLabel(tit.getFechaNacimiento()));
		}
	};
	
	private static final BiConsumer<AeatTitular,AonDisplayGrid> TITULAR_SEXO = (tit, tab) -> {
		if (tit.getSexo() != null ) {
			tab.addRow()
				.addCell(new Label("Sexo"), AON.CSS.aonTableLabel())
				.addCell(AeatFiscalDataPanel.getEnumLabel(tit.getSexo(), d -> d.getDescription()));
		}
	};

	private static final BiConsumer<AeatTitular,AonDisplayGrid> TITULAR_FECHA_FALLECIMIENTO = (tit, tab) -> {
		if (tit.getFechaFallecimiento() != null) {
			tab.addRow()
				.addCell(new Label("Fecha de fallecimiento"), AON.CSS.aonTableLabel())
				.addCell(AeatFiscalDataPanel.getDateLabel(tit.getFechaFallecimiento()));
		}
	};

	private static final BiConsumer<AeatTitular,AonDisplayGrid> TITULAR_COMUNIDAD_AUTONOMA = (tit, tab) -> {
		if (tit.getComunidadAutonoma() != null) {
			tab.addRow()
				.addCell(new Label("Comunidad aut\u00F3noma"), AON.CSS.aonTableLabel())
				.addCell(AeatFiscalDataPanel.getEnumLabel(tit.getComunidadAutonoma(), d -> d.getDescription()));
		}
	};

	private static final BiConsumer<AeatTitular,AonDisplayGrid> TITULAR_IBAN = (tit, tab) -> {
		if (AonStringUtils.isNotBlank(tit.getIBAN())) {
			tab.addRow()
				.addCell(new Label("IBAN"), AON.CSS.aonTableLabel())
				.addCell(new Label(tit.getIBAN()));
		}
	};

	private static final BiConsumer<AeatTitular,AonDisplayGrid> TITULAR_SWIFT = (tit,tab) -> {
		if (AonStringUtils.isNotBlank(tit.getSWIFT())) {
			tab.addRow()
				.addCell(new Label("SWIFT"), AON.CSS.aonTableLabel())
				.addCell(new Label(tit.getSWIFT()));
			}
	};
	
	private static final BiConsumer<AeatTitular,AonDisplayGrid> TITULAR_FECHA_ADQUISICION_VIVIENDA_HABITUAL = (tit,tab) -> {
		if (tit.getFechaAdquisicionViviendaHabitual() != null) {
			tab.addRow()
				.addCell(new Label("Fecha adquisici\u00F3n vivienda habitual"), AON.CSS.aonTableLabel())
				.addCell(AeatFiscalDataPanel.getDateLabel(tit.getFechaAdquisicionViviendaHabitual()));
		}
	};

	private static final BiConsumer<AeatTitular,AonDisplayGrid> TITULAR_NUMERO_PRESTAMO_HIPOTECARIO = (tit,tab) -> {
		if (AonStringUtils.isNotBlank(tit.getNumeroPrestamoHipotecario())) {
			tab.addRow()
				.addCell(new Label("N\u00FAmero de pr\u00E9stamo hipotecario"), AON.CSS.aonTableLabel())
				.addCell(new Label(tit.getNumeroPrestamoHipotecario()))	;
		}
	};

	private static final BiConsumer<AeatTitular,AonDisplayGrid> TITULAR_PORCENTAJE_PRESTAMO = (tit,tab) -> {
		if (AonMathUtils.isNotZero(tit.getPorcentajePrestamo())) {
			tab.addRow()
				.addCell(new Label("Porcentaje del pr\u00E9stamo destinado a la compra de la vivienda habitual"), AON.CSS.aonTableLabel())
				.addCell(new AonDoubleLabel(tit.getPorcentajePrestamo()))	;
		}
	};

	private static final BiConsumer<AeatTitular,AonDisplayGrid> TITULAR_DEDUCCION_VIVIENDA_EJERCICIO_ANTERIOR = (tit,tab) -> {
		tab.addRow()
			.addCell(new Label("Deducci\u00F3n en vivienda ejercicio anterior"), AON.CSS.aonTableLabel())
			.addCell(AeatFiscalDataPanel.getBooleanLabel(tit.isDeduccionViviendaEjercicioAnterior()))	;
	};
	private static final BiConsumer<AeatTitular,AonDisplayGrid> TITULAR_IGLESIA_CATOLICA = (tit,tab) -> {
		tab.addRow()
			.addCell(new Label("Asignaci\u00F3n a la iglesia cat\u00F3lica"), AON.CSS.aonTableLabel())
			.addCell(AeatFiscalDataPanel.getBooleanLabel(tit.isIglesiaCatolica()))	;
	};
	
	private static final BiConsumer<AeatTitular,AonDisplayGrid> TITULAR_FINES_SOCIALES = (tit,tab) -> {
		tab.addRow()
			.addCell(new Label("Asignaci\u00F3n a otros fines sociales"), AON.CSS.aonTableLabel())
			.addCell(AeatFiscalDataPanel.getBooleanLabel(tit.isFinesSociales()));
	};
	
}
