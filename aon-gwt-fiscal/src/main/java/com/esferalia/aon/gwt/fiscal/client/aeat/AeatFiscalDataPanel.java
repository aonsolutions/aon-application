package com.esferalia.aon.gwt.fiscal.client.aeat;

import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleLabel;
import com.esferalia.aon.occam.api.model.ddff.AeatFiscalData;
import com.esferalia.aon.occam.api.model.ddff.AeatTitular;
import com.esferalia.aon.watson.util.AonCollectionUtils;
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
		container.add( titulares(data) );
	}

	private Widget basicPanel(String msg) {
		FlowPanel basicPanel = new FlowPanel();
		basicPanel.setStyleName(AON.CSS.aonTextLeft());
		basicPanel.addStyleName(AON.CSS.aonMarginTop());
		basicPanel.addStyleName(AON.CSS.aonMarginLeft());
		Label basicLabel = new Label( msg );
		basicPanel.add(basicLabel);
		return basicPanel;
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
	
	private Widget titulares(AeatFiscalData data) {
		if ( AonCollectionUtils.isEmpty(data.getTitulares() )) {
			return basicPanel("No se han encontrado titulares.");
		}
		FlowPanel titularesPanel = new FlowPanel();
		titularesPanel.addStyleName(AON.CSS.aonMarginTop());
		int i = 1;
		for ( AeatTitular titular : data.getTitulares() ) {
			Label tabLabel = new Label("Titular n\u00BA: " + i);
			tabLabel.setStyleName(AON.CSS.aonBold());
			titularesPanel.add(  tabLabel );
			AonDisplayTable tab = new AonDisplayTable();
			tab.addStyleName(AON.CSS.aonWidthAlmostAll() );
			tab.addStyleName(AON.CSS.aonBlockCenter());
			tab.addStyleName(AON.CSS.aonMarginTop() );
			tab.addRow()
				.addCell(new Label("NIF") , AON.CSS.aonTableLabel(), AON.CSS.aonWidth300() )
				.addCell(new Label(titular.getNif()) , AON.CSS.aonWidthAuto())
			;
			tab.addRow()
				.addCell(new Label("Apellidos y Nombre"), AON.CSS.aonTableLabel())
				.addCell(new Label(titular.getApellidosNombre()))
			;
			tab.addRow()
				.addCell(new Label("Discapacidad-IRPF"), AON.CSS.aonTableLabel())
				.addCell(getEnumLabel(titular.getDiscapacidadIRPF(),  d -> d.getDescription() ) )
			;
			tab.addRow()
				.addCell(new Label("Discapacidad-990"), AON.CSS.aonTableLabel())
				.addCell(getEnumLabel(titular.getDiscapacidad990(),  d -> d.getDescription() ) )
				;
			tab.addRow()
				.addCell(new Label("Fecha de nacimiento"), AON.CSS.aonTableLabel())
				.addCell(getDateLabel(titular.getFechaNacimiento()))
				;
			tab.addRow()
				.addCell(new Label("Sexo"), AON.CSS.aonTableLabel())
				.addCell(getEnumLabel(titular.getSexo(),  d -> d.getDescription() ) )
				;
			tab.addRow()
				.addCell(new Label("Fecha de fallecimiento"), AON.CSS.aonTableLabel())
				.addCell(getDateLabel(titular.getFechaFallecimiento()))
				;
			tab.addRow()
				.addCell(new Label("Comunidad aut\u00F3noma"), AON.CSS.aonTableLabel())
				.addCell(getEnumLabel(titular.getComunidadAutonoma(),  d -> d.getDescription() ) )
				;
			tab.addRow()
				.addCell(new Label("IBAN"), AON.CSS.aonTableLabel())
				.addCell(new Label(titular.getIBAN()))
				;
			tab.addRow()
				.addCell(new Label("SWIFT"), AON.CSS.aonTableLabel())
				.addCell(new Label(titular.getSWIFT()))
				;
			tab.addRow()
				.addCell(new Label("Fecha adquisici\u00F3n vivienda habitual"), AON.CSS.aonTableLabel())
				.addCell(getDateLabel(titular.getFechaAdquisicionViviendaHabitual()))
				;
			tab.addRow()
				.addCell(new Label("N\u00FAmero de pr\u00E9stamo hipotecario"), AON.CSS.aonTableLabel())
				.addCell(new Label(titular.getNumeroPrestamoHipotecario()))
				;
			tab.addRow()
				.addCell(new Label("Porcentaje del pr\u00E9stamo destinado a la compra de la vivienda habitual"), AON.CSS.aonTableLabel())
				.addCell(new AonDoubleLabel(titular.getPorcentajePrestamo()))
				;
			tab.addRow()
				.addCell(new Label("Deducci\u00F3n en vivienda ejercicio anterior"), AON.CSS.aonTableLabel())
				.addCell(getBooleanLabel(titular.isDeduccionViviendaEjercicioAnterior()))
				;
			tab.addRow()
				.addCell(new Label("Asignaci\u00F3n a la iglesia cat\u00F3lica"), AON.CSS.aonTableLabel())
				.addCell(getBooleanLabel(titular.isIglesiaCatolica()))
				;
			tab.addRow()
				.addCell(new Label("Asignaci\u00F3n a otros fines sociales"), AON.CSS.aonTableLabel())
				.addCell(getBooleanLabel(titular.isFinesSociales()))
			;
			titularesPanel.add(tab);
		}
		return titularesPanel;
	}
	
	private <T> Label getEnumLabel( T enumObject , Function<? super T, String> func) {
		return new Label(Optional.ofNullable( enumObject ).map( func ).orElse(""));
	}
	
	private Label getDateLabel( Date date) {
		return new Label(date==null?"":AON.DATE_FORMAT.format( date));
	}
	private Label getBooleanLabel( boolean data) {
		return new Label(data?"SI":"NO");
	}
	
}
