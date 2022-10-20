package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Arrays;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.console.ConsoleTableFieldType;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

class ConsoleRowQueryFilter extends SimpleLayoutPanel implements HasValueChangeHandlers<ConsoleTableRow>{
	
	public static final double WIDTH = 400;
	private final DomainParams params;
	
	private final ListBox tableBox;
	private final AonDisplayTable tableTab;
	private final AonDisplayTable tab;
	private ConsoleTableRow rowMetadata;	
	
	ConsoleRowQueryFilter(DomainParams params) {
		this.params = params;
		
		setStyleName(AON.CSS.aonSearchPanel());
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMargin());
		addStyleName(AON.CSS.aonBlockCenter());

		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		setWidget( scroll);
		FlowPanel container = new FlowPanel();
		scroll.setWidget( container );

		Label schemaLabel = new Label( params.getSchema() );
		schemaLabel.setStyleName( AON.CSS.aonBold());
		schemaLabel.addStyleName( AON.CSS.aonTextCenter());
		schemaLabel.addStyleName( AON.CSS.aonTextUnderline());
		container.add( schemaLabel );
		
		tableTab = new AonDisplayTable();
		container.add( tableTab );
		tableBox = new ListBox();
		tableBox.addChangeHandler( e -> tableChanged());
		tableBox.addItem("");
		tableTab.addLabelWidgetRow("Tabla", tableBox);
		
		FlowPanel buttons = new FlowPanel();
		buttons.setStyleName(AON.CSS.aonTextCenter());
		buttons.addStyleName(AON.CSS.aonMarginTop());
		buttons.addStyleName(AON.CSS.aonMarginBottom());
		container.add( buttons );
		
		final Button okButton = new Button();
		okButton.setStyleName(AON.CSS.aonOkButton());
		okButton.setText( AON.MSG.searchAction());
		okButton.addClickHandler(event -> fire());
		buttons.add(okButton);

		tab = new AonDisplayTable();
		container.add( tab );
	
		ConsoleModule.CONSOLE_SERVICE.getAonTables(new AsyncCallback<String[]>() {

			@Override
			public void onFailure(Throwable caught) {
				AonMessageDialog.error(caught.getMessage());
			}

			@Override
			public void onSuccess(String[] tables) {
				tableBox.clear();
				tableBox.addItem("");
				Arrays.stream(tables).forEach( tableBox::addItem );
			}
			
		});
	}
	
	private ConsoleTableRow getRowMetadata() {
		return this.rowMetadata;
	}
	private void setRowMetadata(ConsoleTableRow rowMetadata) {
		this.rowMetadata = rowMetadata;
	}
	
	private void tableChanged() {
		tab.clear();
		ConsoleTableRow ctr = new ConsoleTableRow()
			.setSchema( params.getSchema() )
			.setTable( tableBox.getSelectedValue() )
			.setDomain( params.getId());
		ConsoleModule.CONSOLE_SERVICE.getTableRowMetadata(ctr, new AsyncCallback<ConsoleTableRow>() {

			@Override
			public void onFailure(Throwable caught) {
				AonMessageDialog.error(caught.getMessage());				
			}

			@Override
			public void onSuccess(ConsoleTableRow meta) {
				setRowMetadata( meta );
				if (meta.getFields() != null) {
					meta.getFields().keySet().stream()
						.filter( key -> AonStringUtils.notEquals("domain",key))
						.filter( key -> getRowMetadata().getField(key).getType() != ConsoleTableFieldType.BINARY)
						.forEach( key -> {
							AonTextBox box = new AonTextBox();
							box.addValueChangeHandler( e -> getRowMetadata().getField(key).setQueryValue( box.getValue()) );
							tab.addLabelWidgetRow(key, box);
						});
				}
			}
			
		});
	}

	private void fire() {
		if ( AonStringUtils.isEmpty( tableBox.getSelectedValue() ) ) {
			tableBox.setFocus(true);
			tableBox.addStyleName(AON.CSS.aonInputError());
			new Timer() {
				@Override
				public void run() {
					tableBox.removeStyleName(AON.CSS.aonInputError());
				}
			}.schedule(1000);
		} else {
			ValueChangeEvent.<ConsoleTableRow>fire( ConsoleRowQueryFilter.this, getRowMetadata() );
		}
	}


	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ConsoleTableRow> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
}
