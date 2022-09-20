package com.esferalia.aon.gwt.fiscal.client.console;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDomainTypeBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
 
public class ConsoleDomainFilterPanel extends SimpleLayoutPanel implements Focusable, HasValueChangeHandlers<DomainParams>{
	
	public static final double HEIGTH = 115;
	
	private static final String ALL = "-- TODOS --";
	
	private ListBox schemaBox;
	private AonTextBox queryBox;
	private AonDomainBox parentBox;
	private AonDomainTypeBox typeBox;
	private ListBox activeBox;
	private ListBox enableHeredityBox;
	private ListBox domainManagementBox;
	private AonDateBox fromLastAccessBox;
	private AonDateBox toLastAccessBox;
	private AonDateBox fromExpirationDateBox;
	private AonDateBox toExpirationDateBox;
	
	private AonSearchPanelButton cleanButton;
	private AonSearchPanelButton refreshButton;
	
	public ConsoleDomainFilterPanel(final ConsoleModuleOptions opt) {
		setStyleName(AON.CSS.aonSearchPanel());
			addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMargin());
		addStyleName(AON.CSS.aonBlockCenter());
		
		parentBox = new AonDomainBox(opt.getOccam());
		parentBox.addSelectionHandler(e -> fire(opt));
		parentBox.setEnabled(false);

		schemaBox = new ListBox();

		ConsoleModule.CONSOLE_SERVICE.getSchemas(opt.getOccam(), new AsyncCallback<String[]>() {
			
			@Override
			public void onSuccess(String[] schemas) {
				schemaBox.clear();
				schemaBox.addItem(AonStringUtils.EMPTY);
				for (String sch : schemas) {
					schemaBox.addItem(sch);
				}
				schemaBox.addChangeHandler(e -> {
					fire(opt);
					if ( AonStringUtils.isBlank(schemaBox.getSelectedValue())) {
						parentBox.setEnabled(false);	
					} else {
						parentBox.setEnabled(true);
						parentBox.setSchema( schemaBox.getSelectedValue() );
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("No se pueden leer los escquemas de la BD");
			}
		});
		
		
		
		queryBox = new AonTextBox();
		queryBox.setVisibleLength(40);
		queryBox.addValueChangeHandler(e -> fire(opt));
		
		typeBox = new AonDomainTypeBox() ;
		typeBox.addChangeHandler(e -> fire(opt));
		
		activeBox = new ListBox();
		activeBox.addItem(ALL);
		activeBox.addItem("Activos");
		activeBox.addItem("Inactivos");
		activeBox.addChangeHandler(e -> fire(opt));
		
		enableHeredityBox = new ListBox();
		enableHeredityBox.addItem(ALL);
		enableHeredityBox.addItem("Con");
		enableHeredityBox.addItem("Sin");
		enableHeredityBox.addChangeHandler(e -> fire(opt));
		
		domainManagementBox = new ListBox();
		domainManagementBox.addItem(ALL);
		domainManagementBox.addItem("Puede crear");
		domainManagementBox.addItem("No puede crear");		
		domainManagementBox.addChangeHandler(e -> fire(opt));

		fromLastAccessBox = new AonDateBox();
		fromLastAccessBox.addValueChangeHandler(e -> fire(opt));
		
		toLastAccessBox = new AonDateBox();
		toLastAccessBox.addValueChangeHandler(e -> fire(opt));
		
		fromExpirationDateBox = new AonDateBox();
		fromExpirationDateBox.addValueChangeHandler(e -> fire(opt));
		
		toExpirationDateBox = new AonDateBox();
		toExpirationDateBox.addValueChangeHandler(e -> fire(opt));
		
		FlowPanel mainTab = new FlowPanel();
		mainTab.setStyleName( AON.CSS.aonBlockCenter());
		mainTab.addStyleName( AON.CSS.aonWidthAlmostAll());
		
		cleanButton = new AonSearchPanelButton(AON.MSG.clean(),AON.CSS.aonIconClear());
		cleanButton.addClickHandler(event -> fire(opt));

		refreshButton = new AonSearchPanelButton(AON.MSG.refresh(),AON.CSS.aonIconRefresh());
		refreshButton.addClickHandler(event -> fire(opt));
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonNowrap());
		buttonsPanel.add( cleanButton );
		buttonsPanel.add( refreshButton );
		
		AonDisplayTable rowTable1 = new AonDisplayTable();
		rowTable1.addRow()
			.addCell(new Label("Esquema"),AON.CSS.aonSearchPanelLabel(),AON.CSS.aonWidth80())
			.addCell(schemaBox)
			.addCell(new Label(AON.MSG.description()),AON.CSS.aonSearchPanelLabel())
			.addCell(queryBox)
			.addCell(new Label("Dominio padre"),AON.CSS.aonSearchPanelLabel())
			.addCell(parentBox);
		mainTab.add(rowTable1);
		
		AonDisplayTable rowTable2 = new AonDisplayTable();
		rowTable2.addRow()
			.addCell(new Label(AON.MSG.type()),AON.CSS.aonSearchPanelLabel(),AON.CSS.aonWidth80())
			.addCell(typeBox)
			.addCell(new Label( "Estado" ),AON.CSS.aonSearchPanelLabel())
			.addCell(activeBox)
			.addCell(new Label("Herencia"),AON.CSS.aonSearchPanelLabel())
			.addCell(enableHeredityBox)
			.addCell(new Label("Crea dominios"),AON.CSS.aonSearchPanelLabel())
			.addCell(domainManagementBox);
		mainTab.add(rowTable2);
		
		AonDisplayTable rowTable3 = new AonDisplayTable();
		rowTable3.addRow()
			.addCell(new Label("\u00FAltimo acceso"),AON.CSS.aonSearchPanelLabel(),AON.CSS.aonWidth80())
			.addCell(fromLastAccessBox)
			.addCell(new Label(" hasta "))
			.addCell(toLastAccessBox)
			.addCell(new Label("Fecha expiraci\u00F3n"),AON.CSS.aonSearchPanelLabel())
			.addCell(fromExpirationDateBox)
			.addCell(new Label(" hasta "))
			.addCell(toExpirationDateBox)
			.addCell(buttonsPanel);
		mainTab.add(rowTable3);
		
		setWidget(mainTab);
	}

	private void fire(final ConsoleModuleOptions opt) {
		if (AonStringUtils.isEmpty( schemaBox.getSelectedValue() ) ) {
			Window.alert("Rellene el campo \"esquema\" para realizar una b\u00FAsqueda");
		} else {
			ValueChangeEvent.<DomainParams>fire( ConsoleDomainFilterPanel.this, getParams( opt ) ); 
		}
	}
	
	DomainParams getParams(ConsoleModuleOptions opt) {
		DomainParams params = new DomainParams()
			.setSchema(schemaBox.getSelectedValue())
			.setQuery(queryBox.getValue())
			.setFromLastAccess(fromLastAccessBox.getValue())
			.setToLastAccess(toLastAccessBox.getValue())
			.setFromExpirationDate(fromExpirationDateBox.getValue())
			.setToExpirationDate(toExpirationDateBox.getValue());
		
		if ( parentBox.getDomain() != null) {
			params.setParent(parentBox.getDomain().getId());
		}
		
		if ( typeBox.getValue() != null ) {
			params.setType(typeBox.getValue().ordinal());
		}
		
		if ( activeBox.getSelectedIndex() > 0 ) {
			params.setActive(activeBox.getSelectedIndex() == 1 
				?Boolean.valueOf(true)
				:Boolean.valueOf(false));
		}
		
		if ( enableHeredityBox.getSelectedIndex() > 0 ) {
			params.setEnableHeredity(enableHeredityBox.getSelectedIndex() == 1 
				?Boolean.valueOf(true)
				:Boolean.valueOf(false));
		}

		if ( domainManagementBox.getSelectedIndex() > 0 ) {
			params.setDomainManagement(domainManagementBox.getSelectedIndex() == 1 
				?Boolean.valueOf(true)
				:Boolean.valueOf(false));
		}
		return params;
	}

	@Override
	public int getTabIndex() {
		return schemaBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		schemaBox.setAccessKey(key);;
	}

	@Override
	public void setFocus(boolean focused) {
		schemaBox.setFocus(true);
	}

	@Override
	public void setTabIndex(int index) {
		schemaBox.setTabIndex(index);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DomainParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

}
