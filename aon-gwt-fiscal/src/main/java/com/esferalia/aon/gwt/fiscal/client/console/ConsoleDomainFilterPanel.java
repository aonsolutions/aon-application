package com.esferalia.aon.gwt.fiscal.client.console;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDomainTypeBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonPasswordTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.console.ConsoleSchema;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
 
public class ConsoleDomainFilterPanel extends SimpleLayoutPanel implements Focusable, HasValueChangeHandlers<DomainParams>{
	
	public static final double HEIGTH = 115;
	
	private static final String ALL = "-- TODOS --";

	private static final int[] AMK_ARRAY = {52, 48, 110, 115, 48, 108, 117, 116, 49, 48, 110, 115};
	private static final String AMK = 
		AonCollectionUtils.stream(AMK_ARRAY)
			.mapToObj(i -> String.valueOf((char) i))
			.reduce("", String::concat);
	
	private ListBox schemaBox;
	private AonIntegerBox idBox;
	private AonTextBox queryBox;
	private AonDomainBox parentBox;
	private ListBox orphanBox;
	private AonDomainTypeBox typeBox;
	private ListBox activeBox;
	private ListBox enableHeredityBox;
	private ListBox domainManagementBox;
	private AonDateBox fromLastAccessBox;
	private AonDateBox toLastAccessBox;
	private AonDateBox fromExpirationDateBox;
	private AonDateBox toExpirationDateBox;
	private AonPasswordTextBox advancedModePassword;
	private InlineLabel advancedModeLabel;
	private boolean advancedMode;
	// private String[] schemas;
	private String select;
	
	private FlowPanel advancedButtonsPanel;
	
	public ConsoleDomainFilterPanel(final ConsoleModuleOptions opt) {
		setStyleName(AON.CSS.aonSearchPanel());
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMargin());
		addStyleName(AON.CSS.aonBlockCenter());
		
		defineFields( opt );

		FlowPanel searchingPanel = new FlowPanel();
		InlineLabel searchingLabel = new InlineLabel("Buscando esquemas, un momento por favor ....");
		searchingPanel.add(searchingLabel);
		setWidget(searchingPanel);
		
		schemaBox.clear();
		schemaBox.addItem(AonStringUtils.EMPTY);
		AonCollectionUtils.stream(ConsoleSchema.values())
			.forEach( s -> schemaBox.addItem(s.name(), s.getSchema()));
		
		paintFields( opt );

//		ConsoleModule.CONSOLE_SERVICE.getSchemas(opt.getOccam(), new AsyncCallback<String[]>() {
//			
//			@Override
//			public void onSuccess(String[] schemas) {
//				ConsoleDomainFilterPanel.this.schemas = schemas;
//				schemaBox.clear();
//				schemaBox.addItem(AonStringUtils.EMPTY);
//				for (String sch : schemas) {
//					schemaBox.addItem(sch);
//				}
//				schemaBox.addChangeHandler(e -> {
//					fire(opt);
//					if ( AonStringUtils.isBlank(schemaBox.getSelectedValue())) {
//						parentBox.setEnabled(false);	
//					} else {
//						parentBox.setEnabled(true);
//						parentBox.setSchema( schemaBox.getSelectedValue() );
//					}
//				});
//				paintFields( opt );
//			}
//			
//			@Override
//			public void onFailure(Throwable caught) {
//				FlowPanel searchingPanel = new FlowPanel();
//				InlineLabel searchingLabel = new InlineLabel("No se pueden leer los escquemas de la BD");
//				searchingPanel.add(searchingLabel);
//				setWidget(searchingPanel);
//			}
//		});
		
		
		
	}

	public String getSchema() {
		return schemaBox != null? schemaBox.getSelectedValue() : null;
	}


//	public String[]getSchemas() {
//		return this.schemas;
//	}

	private void defineFields(ConsoleModuleOptions opt) {
		schemaBox = new ListBox();
		
		idBox = new AonIntegerBox();
		idBox.setVisibleLength(6);
		idBox .addValueChangeHandler(e -> fire(opt));
		
		queryBox = new AonTextBox();
		queryBox.setVisibleLength(40);
		queryBox.addValueChangeHandler(e -> fire(opt));
		
		typeBox = new AonDomainTypeBox() ;
		typeBox.addChangeHandler(e -> fire(opt));
		
		parentBox = new AonDomainBox(opt.getOccam(), true);
		parentBox.addSelectionHandler(e -> fire(opt));
		parentBox.setEnabled(false);

		orphanBox = new ListBox();
		orphanBox.addItem(ALL);
		orphanBox.addItem("Con padre");
		orphanBox.addItem("Sin padre");
		orphanBox.addChangeHandler(e -> fire(opt));

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
		
		advancedModePassword = new AonPasswordTextBox();
		advancedModePassword.getElement().setAttribute("autocomplete","off");
		advancedModePassword.addStyleName(AON.CSS.aonMarginLeft());
		advancedModePassword.addValueChangeHandler(e -> checkAdvanced(opt));
		
		advancedModeLabel = new InlineLabel("Modo avanzado");
		advancedModeLabel.addMouseDownHandler( e -> {
			if (e.isControlKeyDown()) {
				advancedModePassword.setValue( AMK, false);
				checkAdvanced(opt);
			}
		});
		advancedModeLabel.addStyleName( AON.CSS.aonBold() );
	}

	private void paintFields(final ConsoleModuleOptions opt) {
		FlowPanel mainTab = new FlowPanel();
		mainTab.setStyleName( AON.CSS.aonBlockCenter());
		mainTab.addStyleName( AON.CSS.aonWidthAlmostAll());
		
		AonSearchPanelButton cleanButton = new AonSearchPanelButton(AON.MSG.clean(),AON.CSS.aonIconClear());
		cleanButton.addClickHandler(event -> clean(opt));

		AonSearchPanelButton refreshButton = new AonSearchPanelButton(AON.MSG.refresh(),AON.CSS.aonIconRefresh());
		refreshButton.addClickHandler(event -> fire(opt));
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonNowrap());
		buttonsPanel.add( cleanButton );
		buttonsPanel.add( refreshButton );
		
		advancedButtonsPanel = new FlowPanel();
		advancedButtonsPanel.setStyleName(AON.CSS.aonNowrap());
		advancedButtonsPanel.addStyleName(AON.CSS.aonBackgroundHighlightedGreen());
		advancedButtonsPanel.addStyleName(AON.CSS.aonPaddingLeft());
		advancedButtonsPanel.addStyleName(AON.CSS.aonPaddingRight());
		advancedButtonsPanel.addStyleName(AON.CSS.aonMarginRight());
		advancedButtonsPanel.addStyleName(AON.CSS.aonTextCenter());
		advancedButtonsPanel.setVisible( advancedMode );
		AonSearchPanelButton selectButton = new AonSearchPanelButton("Editar SELECT",AON.CSS.aonIconEdit());
		selectButton.addClickHandler(event -> onEditSelect(opt));
		advancedButtonsPanel.add( selectButton );
		
		FlowPanel advancedModelPanel = new FlowPanel();
		advancedModelPanel.add( advancedModeLabel );
		advancedModelPanel.add( advancedModePassword );
		
		
		AonDisplayTable rowTable1 = new AonDisplayTable();
		rowTable1.addRow()
			.addCell(new Label("Esquema"),AON.CSS.aonSearchPanelLabel(),AON.CSS.aonWidth80())
			.addCell(schemaBox)
			.addCell(new Label("ID"),AON.CSS.aonSearchPanelLabel(),AON.CSS.aonWidth30())
			.addCell(idBox)
			.addCell(new Label(AON.MSG.description()),AON.CSS.aonSearchPanelLabel())
			.addCell(queryBox)
			.addCell(new Label("Dominio padre"),AON.CSS.aonSearchPanelLabel())
			.addCell(parentBox)
			.addCell(new Label("Padre?"),AON.CSS.aonSearchPanelLabel())
			.addCell(orphanBox)
			;
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
		rowTable3.addStyleName(AON.CSS.aonWidthAlmostAll());
		rowTable3.addRow()
			.addCell(new Label("\u00FAltimo acceso"),AON.CSS.aonSearchPanelLabel(),AON.CSS.aonWidth80())
			.addCell(fromLastAccessBox)
			.addCell(new Label(" hasta "))
			.addCell(toLastAccessBox)
			.addCell(new Label("Fecha expiraci\u00F3n"),AON.CSS.aonSearchPanelLabel())
			.addCell(fromExpirationDateBox)
			.addCell(new Label(" hasta "))
			.addCell(toExpirationDateBox)
			.addCell(buttonsPanel)
			.addCell(advancedButtonsPanel)
			.addCell(advancedModelPanel, AON.CSS.aonTextRight() , AON.CSS.aonWidthAuto())
			;
		mainTab.add(rowTable3);
		
		setWidget(mainTab);
	}

	private void onEditSelect(ConsoleModuleOptions opt) {
		EditSelectPanel rrp = new EditSelectPanel(opt, new EditSelectPanelCallback() {
			
			@Override
			public void onAccept() {
				fire( opt );
			}
			
			@Override
			public String getSelect() {
				return ConsoleDomainFilterPanel.this.select;
			}
			@Override
			public void setSelect(String select) {
				ConsoleDomainFilterPanel.this.select = select;
			}
		});
		Scheduler.get().scheduleDeferred(() -> rrp.setFocus(true));
		rrp.center();
		rrp.show();
	}

	private void clean(ConsoleModuleOptions opt) {
		queryBox.setValue(null,false);
		fromLastAccessBox.setValue(null,false);
		toLastAccessBox.setValue(null,false);
		fromExpirationDateBox.setValue(null,false);
		toExpirationDateBox.setValue(null,false);
		parentBox.setValue(null,false);
		orphanBox.setSelectedIndex(0);
		typeBox.setSelectedIndex(0);
		activeBox.setSelectedIndex(0);
		enableHeredityBox.setSelectedIndex(0);
		domainManagementBox.setSelectedIndex(0);
		fire(opt);
	}
	
	private void checkAdvanced(final ConsoleModuleOptions opt) {
		advancedMode = (AMK.equals(advancedModePassword.getValue()));
		refreshAdvancedModePanel();
		fire(opt);
	}

	private void refreshAdvancedModePanel() {
		advancedButtonsPanel.setVisible( advancedMode );
		if (advancedMode) {
			advancedModeLabel.addStyleName( AON.CSS.aonColorGreen() );
			advancedModeLabel.removeStyleName( AON.CSS.aonColorRed() );
		} else {
			advancedModeLabel.addStyleName( AON.CSS.aonColorRed() );
			advancedModeLabel.removeStyleName( AON.CSS.aonColorGreen() );
		}
		
	}

	private void fire(final ConsoleModuleOptions opt) {
		ValueChangeEvent.<DomainParams>fire( ConsoleDomainFilterPanel.this, getParams( opt ) ); 
	}
	
	DomainParams getParams(ConsoleModuleOptions opt) {
		DomainParams params = new DomainParams()
			.setDbSchema(schemaBox.getSelectedValue())
			.setId(idBox.getValue())
			.setQuery(queryBox.getValue())
			.setFromLastAccess(fromLastAccessBox.getValue())
			.setToLastAccess(toLastAccessBox.getValue())
			.setFromExpirationDate(fromExpirationDateBox.getValue())
			.setToExpirationDate(toExpirationDateBox.getValue())
			.setAdvancedMode(advancedMode)
			.setSelect( select );
		
		if ( parentBox.getDomain() != null) {
			params.setParent(parentBox.getDomain().getId());
		}
		
		if ( orphanBox.getSelectedIndex() > 0 ) {
			params.setOrphan(orphanBox.getSelectedIndex() == 1 
				?Boolean.valueOf(false)
				:Boolean.valueOf(true));
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
		schemaBox.setAccessKey(key);
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

	public boolean isAdvancedMode() {
		return advancedMode;
	}

	static interface EditSelectPanelCallback {
		String getSelect();
		void setSelect( String reason);
		void onAccept();
		default void onCancel() {
		}
	}
	
	static class EditSelectPanel extends AonCustomDialog implements Focusable {
		
		final TextArea select;
				
		EditSelectPanel(ConsoleModuleOptions opt, EditSelectPanelCallback callback) {
			this.setCaption("Edit SELECT");
			FlowPanel reasonPanel = new FlowPanel();
			reasonPanel.setStyleName(AON.CSS.aonTextCenter());
			reasonPanel.addStyleName(AON.CSS.aonPadding());
			
			select = new TextArea();
			select.setWidth("800px");
			select.setHeight("300px");
			if (AonStringUtils.isNotEmpty( callback.getSelect() )) {
				select.setValue( callback.getSelect() );
			}
			select.addKeyUpHandler(event1 -> {
				if (event1.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					this.hide();
					callback.onCancel();
				}
			});
		
			FlowPanel buttons = new FlowPanel();
			buttons.setStyleName(AON.CSS.aonTextCenter());
			buttons.addStyleName(AON.CSS.aonMarginTop());
		
			final Button okButton = new Button();
			okButton.setStyleName(AON.CSS.aonOkButton());
			okButton.setText( AON.MSG.accept());
			okButton.addKeyUpHandler(event1 -> {
				if (event1.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					this.hide();
					callback.onCancel();
				}
			});
			
			okButton.addClickHandler(event1 -> {
				okButton.setEnabled(false);
				this.hide();
				callback.setSelect( select.getValue() );
				callback.onAccept();
			});
			buttons.add(okButton);
		
			final Button cancelButton = new Button();
			cancelButton.setStyleName(AON.CSS.aonCancelButton());
			cancelButton.addStyleName(AON.CSS.aonMarginLeft());
			cancelButton.setText( AON.MSG.cancelAction());
			cancelButton.addClickHandler(event1 -> {
				cancelButton.setEnabled(false);
				this.hide();
				callback.onCancel();
			});
			cancelButton.addKeyUpHandler(event1 -> {
				if (event1.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					this.hide();
					callback.onCancel();
				}
			});
			buttons.add(cancelButton);
			Label helpLabel1 = new Label();
			helpLabel1.setStyleName( AON.CSS. aonMarginTop() );
			helpLabel1.setText("Se debe realizar una SELECT correcta cuya columna de selecci\u00F3n sea \"domain\" solo. Por ejemplo: ");
			Label helpLabel2 = new Label();
			helpLabel2.setStyleName( AON.CSS.aonMargin() );
			helpLabel2.addStyleName( AON.CSS.aonItalic() );
			helpLabel2.addStyleName( AON.CSS.aonPaddingLeft() );
			helpLabel2.setText("select domain from registry where name like \"%GARCIA%\" group by domain having count(id) > 1" );
			ListBox recorded = new ListBox();
			recorded.addItem("---", "");
			recorded.addItem("SELECT B\u00E1sica"
				,"select domain from <TABLE> WHERE <CONDITION> group by domain having count(id) > 0" );
			recorded.addItem("Apuntes descuadrados"
				,"select domain from account_entry_detail aed group by domain having sum(aed.debit) <> sum(aed.credit)" );
			recorded.addChangeHandler( e -> {
				String v = recorded.getSelectedValue();
				if (v != null) select.setValue( recorded.getSelectedValue() );
			});
			
			reasonPanel.add(select);
			reasonPanel.add(helpLabel1);
			reasonPanel.add(helpLabel2);
			reasonPanel.add(recorded);
			reasonPanel.add(buttons);
			this.add(reasonPanel);
		}

		@Override
		public void setFocus(boolean focused) {
			select.setFocus( focused );
		}
		
		@Override public int getTabIndex() {return 0;}
		@Override public void setAccessKey(char arg0) {/*Nothing*/}
		@Override public void setTabIndex(int arg0) {/*Nothing*/}
	}


}
