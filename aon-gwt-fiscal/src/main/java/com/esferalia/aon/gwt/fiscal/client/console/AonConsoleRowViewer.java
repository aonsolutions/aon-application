package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Objects;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessage;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageFixType;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageType;
import com.esferalia.aon.occam.api.model.console.ConsoleTableField;
import com.esferalia.aon.occam.api.model.console.ConsoleTableFieldType;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

class AonConsoleRowViewer extends AonCustomPopup {
	
	private final ConsoleTableRow tableRow;
	
	AonConsoleRowViewer(ConsoleTableRow tableRow) {
		super(false);
		this.tableRow = tableRow;
		
		setWidth("600px");
		setHeight("600px");
		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel( Unit.PX );
		add( dockLayoutPanel );
		dockLayoutPanel.addNorth( getToolbar(), AonToolbar.HEIGTH);			
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		dockLayoutPanel.add( scroll);
		FlowPanel container = new FlowPanel();
		container.setStyleName(AON.CSS.aonMarginBottom());
		scroll.setWidget( container );
		container.add(getGrid());
	}
	
	private AonToolbar getToolbar() {
		AonToolbar toolbar = new AonToolbar("Tabla: " + tableRow.getTable() + " ID: " + tableRow.getId());
		
		AonToolbarButton backButton = new AonToolbarButton(AON.MSG.cancelAction(),AON.CSS.aonIconBack());
		backButton.setText(AON.MSG.backAction());
		backButton.addClickHandler( e -> hide());
		toolbar.add( backButton );
		
		return toolbar;
	}

	private Widget getGrid() {
		AonDisplayGrid grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonBlockCenter());
		grid.addStyleName(AON.CSS.aonWidthAlmostAll());
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
		
		if (field.getType() == ConsoleTableFieldType.STRING) {
			AonTextBox text = new AonTextBox();
			text.setEnabled( !field.isPrimaryKey() );
			text.addStyleName( AON.CSS.aonWidthAll());
			text.addStyleName( AON.CSS.aonBorderNone());
			text.addStyleName( AON.CSS.aonFlexGrow1());
			text.setValue( Objects.toString(field.getValue(), null));
			valuePanel.add( text );
			text.addValueChangeHandler(e -> AonConfirmDialog.showConfirm("MODIFICACI\u00D3n"
					,"Modificar el dato en BD?"
					, () -> saveColumn(field.setNewValue( text.getValue() ) , new AsyncCallback<Boolean>() {
						@Override
						public void onSuccess(Boolean result) {
							response.addStyleName(AON.CSS.aonIconValid());
							new Timer() {
								@Override
								public void run() {
									response.removeStyleName(AON.CSS.aonIconValid());
								}
							}.schedule(1000);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							text.setValue( Objects.toString(field.getValue(), null));
							AonMessageDialog.error(caught.getMessage());
						}
					}
			)));
		} else  if (field.getType() == ConsoleTableFieldType.NUMBER) {
			AonIntegerBox text = new AonIntegerBox();
			text.setEnabled( !field.isPrimaryKey() );
			text.addStyleName( AON.CSS.aonWidthAll());
			text.addStyleName( AON.CSS.aonBorderNone());
			text.addStyleName( AON.CSS.aonFlexGrow1());
			text.setValue( AonNumberUtils.toInteger( field.getValue() ) );
			valuePanel.add( text );
			text.addValueChangeHandler(e -> AonConfirmDialog.showConfirm("MODIFICACI\u00D3n"
					,"Modificar el dato en BD?"
					, () -> saveColumn(field.setNewValue( AonNumberUtils.toString(text.getValue() ))
						, new AsyncCallback<Boolean>() {
						
						@Override
						public void onSuccess(Boolean result) {
							response.addStyleName(AON.CSS.aonIconValid());
							new Timer() {
								@Override
								public void run() {
									response.removeStyleName(AON.CSS.aonIconValid());
								}
							}.schedule(1000);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							text.setValue( AonNumberUtils.toInteger( field.getValue() ) );
							AonMessageDialog.error(caught.getMessage());
						}
					}
				)));
		} else  if (field.getType() == ConsoleTableFieldType.DECIMAL) {
			AonDoubleBox text = new AonDoubleBox();
			text.setEnabled( !field.isPrimaryKey() );
			text.addStyleName( AON.CSS.aonWidthAll());
			text.addStyleName( AON.CSS.aonBorderNone());
			text.addStyleName( AON.CSS.aonFlexGrow1());
			text.setValue( AonNumberUtils.toDouble( field.getValue() ) );
			valuePanel.add( text );
			text.addValueChangeHandler(e -> AonConfirmDialog.showConfirm("MODIFICACI\u00D3n"
					,"Modificar el dato en BD?"
					, () -> saveColumn(field.setNewValue( AonNumberUtils.toString(text.getValue() )), new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						response.addStyleName(AON.CSS.aonIconValid());
						new Timer() {
							@Override
							public void run() {
								response.removeStyleName(AON.CSS.aonIconValid());
							}
						}.schedule(1000);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						text.setValue( AonNumberUtils.toDouble( field.getValue() ) );
						AonMessageDialog.error(caught.getMessage());
					}
				}
			)));

		} else {
			InlineLabel text = new InlineLabel( Objects.toString( field.getValue(), "") );
			valuePanel.add( text );
		}
			
		valuePanel.add( response );
		
		if (field.isPrimaryKey()) {
			final Label idLabel = new Label();
			idLabel.setStyleName(AON.CSS.aonIconLabel() );
			idLabel.addStyleName(AON.CSS.aonIconKey() );
			valuePanel.add( idLabel );	
		} 
		if (field.isForeignKey()) {
			final Label fkLabel = new Label();
			fkLabel.setTitle( "Clave refenrencial: " + field.getForeignTable() + "." + field.getForeignColumn());
			fkLabel.setStyleName(AON.CSS.aonIconLabel() );
			fkLabel.addStyleName(AON.CSS.aonIconRedo() );
			valuePanel.add( fkLabel );	
		}
		
		return valuePanel;
	}
	
	
	private <T extends Object> void saveColumn( ConsoleTableField field, AsyncCallback<Boolean> cbk) {
		ConsoleDomainMessage cdm = new ConsoleDomainMessage()
				.setSchema( tableRow.getSchema() )
				.setTable(tableRow.getTable())
				.setPkId(tableRow.getId())
				.setFkColumn( field.getColumn() )
				.setType(ConsoleDomainMessageType.INTEGRITY)
				.setFixType(ConsoleDomainMessageFixType.NEW_VALUE)
				.setField( field );
		ConsoleModule.CONSOLE_SERVICE.fix( cdm, cbk);
	}
	
}
