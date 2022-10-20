package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.console.ConsoleTableField;
import com.esferalia.aon.occam.api.model.console.ConsoleTableFieldType;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class ConsoleRowQueryViewer extends SimpleLayoutPanel {
	
	public interface AonConsoleRowViewerCallback {
		void onLink( ConsoleTableRow row );
	}

	private static final String MODIFICAR_EL_DATO_EN_BD = "Modificar el dato en BD?";
	private static final String MODIFICACION = "MODIFICACI\u00D3n";
	private final ConsoleTableRow tableRow;
	private final AonConsoleRowViewerCallback callback;
	
	ConsoleRowQueryViewer(ConsoleTableRow tableRow) {
		this(tableRow, null);
	}

	ConsoleRowQueryViewer(ConsoleTableRow tableRow,AonConsoleRowViewerCallback callback) {
		this.tableRow = tableRow;
		this.callback = callback;
		
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		setWidget( scroll);
		FlowPanel container = new FlowPanel();
		container.setStyleName(AON.CSS.aonMarginBottom());
		scroll.setWidget( container );
		
		FlowPanel header = new FlowPanel();
		header.setStyleName(AON.CSS.aonMarginBottom());
		String msg = "Tabla: " + tableRow.getTable();
		Label headerLabel = new Label(msg);
		headerLabel.setStyleName(AON.CSS.aonBold());
		headerLabel.addStyleName(AON.CSS.aonFontLarger());
		headerLabel.addStyleName(AON.CSS.aonTextCenter());
		headerLabel.addStyleName(AON.CSS.aonBorderBottom());
		header.add(headerLabel);
		
		container.add(header);
		container.add(getGrid());
	}
	
	private Widget getGrid() {
		AonDisplayGrid grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonBlockCenter());
		grid.getElement().getStyle().setProperty("min-width", "400px");
		grid.addHeaderRow()
			.addCell( new Label("Columna") , AON.CSS.aonWidth120())
			.addCell( new Label("Valor") , AON.CSS.aonWidthAuto());
		tableRow.getFields()
			.values()
			.stream()
			.forEach( field -> grid.addRow()
				.addCell( new Label(field.getColumn()) , AON.CSS.aonTableLabel())
				.addCell( getTextBox(field))
		);
		return grid;
	}
	
	private Widget getTextBox(ConsoleTableField field ) {
		FlowPanel valuePanel = new FlowPanel();
		valuePanel.setStyleName(AON.CSS.aonDisplayFlex());
		
		final Label response = new Label("");
		response.setStyleName(AON.CSS.aonIconLabel());
		response.addStyleName(AON.CSS.aonMarginLeft());
		
		Widget valueWidget = field.getType().visit( new ConsoleTableFieldType.Visitor<Widget>() {

			private void change( String newValue, Consumer<String> onFailureCallback ) {
				AonConfirmDialog.showConfirm(MODIFICACION
						,MODIFICAR_EL_DATO_EN_BD
						, () -> ConsoleModule.CONSOLE_SERVICE.update(tableRow, field.setNewValue( newValue ) , new AsyncCallback<ConsoleTableRow>() {
							@Override
							public void onSuccess(ConsoleTableRow result) {
								String style;
								if (result == null) {
									style = AON.CSS.aonIconInvalid();
									if (onFailureCallback != null)  onFailureCallback.accept(field.getValue());
								} else {
									style = AON.CSS.aonIconValid();
								}
								response.addStyleName(style);
								new Timer() {
									@Override
									public void run() {
										response.removeStyleName(style);
									}
								}.schedule(1000);
							}
							
							@Override
							public void onFailure(Throwable caught) {
								if (onFailureCallback != null)  onFailureCallback.accept(field.getValue());
								AonMessageDialog.error(caught.getMessage());
							}
						}
				));
			}
			private void styleWidget(Widget w) {
				w.addStyleName( AON.CSS.aonWidthAll());
				w.addStyleName( AON.CSS.aonBorderNone());
				w.addStyleName( AON.CSS.aonFlexGrow1());
			}
			
			@Override
			public Widget visitString() {
				AonTextBox text = new AonTextBox();
				text.setEnabled( !field.isPrimaryKey() );
				styleWidget(text);
				text.setValue( Objects.toString(field.getValue(), null));
				text.addValueChangeHandler(e -> change(text.getValue(), v -> text.setValue( Objects.toString(v , null))));
				return text;
			}

			@Override public Widget visitByte() { return visitInteger(); }
			@Override public Widget visitShort() { return visitInteger(); }
			
			@Override 
			public Widget visitInteger() {
				AonIntegerBox text = new AonIntegerBox();
				text.setEnabled( !field.isPrimaryKey() );
				styleWidget(text);
				text.setValue( AonNumberUtils.toInteger( field.getValue() ) );
				text.addValueChangeHandler(e -> change( AonNumberUtils.toString(text.getValue()),AonNumberUtils::toInteger) );
				return text;
			}

			@Override
			public Widget visitDecimal() {
				return visitDouble();
			}
			
			@Override
			public Widget visitDouble() {
				AonDoubleBox text = new AonDoubleBox();
				text.setEnabled( !field.isPrimaryKey() );
				styleWidget(text);
				text.setValue( AonNumberUtils.toDouble( field.getValue() ) );
				text.addValueChangeHandler(e -> change( AonNumberUtils.toString(text.getValue()),AonNumberUtils::toInteger) );
				return text;
			}

			private Date parse( String dateAsString ) {
				return AonStringUtils.isBlank( dateAsString ) ? null : AonDateUtils.parseDate( dateAsString );
			}
			private String format( Date date ) {
				return date == null ? null : AonDateUtils.formatDate( date );
			}
			
			@Override
			public Widget visitDate() {
				AonDateBox text = new AonDateBox();
				text.setEnabled( !field.isPrimaryKey() );
				styleWidget(text);
				text.setValue( parse(field.getValue()));
				text.addValueChangeHandler(e -> change( format( text.getValue()),this::parse));
				return text;
			}

			private Date parseDateTime( String dateAsString ) {
				return AonStringUtils.isBlank( dateAsString ) ? null : AonDateUtils.parseDateTime( dateAsString );
			}
			private String formatDateTime( Date date ) {
				return date == null ? null : AonDateUtils.formatDateTime( date );
			}

			@Override
			public Widget visitTimestamp() {
				AonDateBox text = new AonDateBox();
				text.setEnabled( !field.isPrimaryKey() );
				styleWidget(text);
				text.setValue( parseDateTime(field.getValue()));
				text.addValueChangeHandler(e -> change( formatDateTime( text.getValue()),this::parseDateTime));
				return text;
			}

			@Override
			public Widget visitBinary() {
				return new InlineLabel( Objects.toString( field.getValue(), "") );
			}
		});
		
		valuePanel.add( valueWidget );		
		
		valuePanel.add( response );
		
		if (field.isPrimaryKey()) {
			final Label idLabel = new Label();
			idLabel.setStyleName(AON.CSS.aonIconLabel() );
			idLabel.addStyleName(AON.CSS.aonIconKey() );
			valuePanel.add( idLabel );	
		} 
		if (field.isForeignKey()) {
			String title = "Clave refenrencial: " + field.getForeignTable() + "." + field.getForeignColumn();
			AonTableButton fkButton = new AonTableButton(title, AON.CSS.aonIconRedo());
			if (callback != null) {
				fkButton.addClickHandler( e ->
					callback.onLink( 
						new ConsoleTableRow()
							.setSchema( tableRow.getSchema() )
							.setTable( field.getForeignTable() )
							.setId( AonNumberUtils.toInteger(field.getValue() ))
					));
			}
			
			valuePanel.add( fkButton );
			
			
		}
		
		return valuePanel;
	}
	
}
