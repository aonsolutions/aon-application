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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.console.ConsoleTableField;
import com.esferalia.aon.occam.api.model.console.ConsoleTableFieldType;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

class ConsoleRowQueryViewer extends DockLayoutPanel  {
	
	protected static final int KEY_PLUS = 171;
	
	public interface AonConsoleRowViewerCallback {
		void onLink( ConsoleTableRow row );

		void deleted(ConsoleTableRow tableRow);
	}

	private static final String MODIFICAR_EL_DATO_EN_BD = "Modificar el dato en BD?";
	private static final String MODIFICACION = "MODIFICACI\u00D3n";
	
	ConsoleRowQueryViewer(ConsoleTableRow tableRow) {
		this(tableRow, null);
	}

	ConsoleRowQueryViewer(ConsoleTableRow tableRow,AonConsoleRowViewerCallback callback) {
		super(Unit.PX);
		
		AonToolbar toolbar = new AonToolbar( "Tabla: " + tableRow.getTable() );
		AonToolbarButton deleteButton = new AonToolbarButton( AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(e -> delete(tableRow, callback));
		toolbar.add(deleteButton);
		
		addNorth(toolbar, AonToolbar.HEIGTH);
		
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		FlowPanel container = new FlowPanel();
		container.setStyleName(AON.CSS.aonMarginBottom());
		scroll.setWidget( container );
		container.add(getGrid(tableRow, callback));
		add( scroll);
		
	}
	
	private void delete(ConsoleTableRow tableRow, AonConsoleRowViewerCallback callback) {
		AonConfirmDialog.showConfirm(MODIFICACION
				,AON.MSG.confirmDeleteAction()
				, () -> ConsoleModule.CONSOLE_SERVICE.delete(tableRow, new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						callback.deleted( tableRow );
					}
					
					@Override
					public void onFailure(Throwable caught) {
						AonMessageDialog.error(caught.getMessage());
					}
				}
		));
	}

	private Widget getGrid(ConsoleTableRow tableRow, AonConsoleRowViewerCallback callback) {
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
				.addCell( getTextBoxForForm(tableRow, callback, field))
		);
		return grid;
	}
	
	static Widget getTextBoxForForm(ConsoleTableRow tr, AonConsoleRowViewerCallback cbk, ConsoleTableField field ) {
		return getTextBox(tr, cbk, field, true);	
	}
	static Widget getTextBoxForList(ConsoleTableRow tr, AonConsoleRowViewerCallback cbk, ConsoleTableField field ) {
		return getTextBox(tr, cbk, field, false);
	}
	static Widget getTextBox(ConsoleTableRow tr, AonConsoleRowViewerCallback cbk, ConsoleTableField field , boolean isForm) {
		FlowPanel valuePanel = new FlowPanel();
		valuePanel.setStyleName(AON.CSS.aonDisplayFlex());
		
		final Label response = new Label("");
		response.setStyleName(AON.CSS.aonIconLabel());
		response.addStyleName(AON.CSS.aonMarginLeft());
		
		Widget valueWidget = field.getType().visit( new ConsoleTableFieldType.Visitor<Widget>() {

			private void change( String newValue, Consumer<String> onFailureCallback ) {
				AonConfirmDialog.showConfirm(MODIFICACION
						,MODIFICAR_EL_DATO_EN_BD
						, () -> ConsoleModule.CONSOLE_SERVICE.update(tr, field.setNewValue( newValue ) , new AsyncCallback<ConsoleTableRow>() {
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
				if (isForm) {
					w.addStyleName( AON.CSS.aonWidthAll());
					w.addStyleName( AON.CSS.aonBorderNone());
					w.addStyleName( AON.CSS.aonFlexGrow1());
				} 
			}
			
			@Override
			public Widget visitString() {
				AonTextBox text = new AonTextBox();
//				text.setEnabled( !field.isPrimaryKey() );
				field.getLength()
					.filter(l -> l>0 )
					.ifPresent( l -> text.setVisibleLength( l>40?40:l ) );
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
//				text.setEnabled( !field.isPrimaryKey() );
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
//				text.setEnabled( !field.isPrimaryKey() );
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
//				text.setEnabled( !field.isPrimaryKey() );
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
//				text.setEnabled( !field.isPrimaryKey() );
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
			valueWidget.addStyleName(AON.CSS.aonInputKey() );
//			final Label idLabel = new Label();
//			idLabel.setStyleName(AON.CSS.aonIconLabel() );
//			idLabel.addStyleName(AON.CSS.aonIconKey() );
//			valuePanel.add( idLabel );	
		} 
		if (field.isForeignKey()) {
			valueWidget.setTitle("Clave refenrencial: " + field.getForeignTable() + "." + field.getForeignColumn());
			valueWidget.addStyleName(AON.CSS.aonInputLink() );
			if (cbk != null) {
				if (valueWidget instanceof HasAllKeyHandlers) {
					HasAllKeyHandlers keyWidget = (HasAllKeyHandlers) valueWidget;
					keyWidget.addKeyUpHandler( new KeyUpHandler() {
						@Override
						public void onKeyUp(KeyUpEvent event) {
							if ((event.isControlKeyDown() && event.getNativeKeyCode() == KeyCodes.KEY_F3)
									|| event.getNativeKeyCode() == KeyCodes.KEY_NUM_PLUS
									|| (!event.isShiftKeyDown() && event.getNativeKeyCode() == KEY_PLUS)) {
								cbk.onLink( 
										new ConsoleTableRow()
										.setSchema( tr.getSchema() )
										.setTable( field.getForeignTable() )
										.setId( AonNumberUtils.toInteger(field.getValue() ))
										);					
							}
						}
					});
				}
			}
		}
		
		return valuePanel;
	}
	
}
