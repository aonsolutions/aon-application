package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.IAccount;
import com.esferalia.aon.occam.api.model.IScopable;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryFull;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AonRegistryFullPanel<R extends RegistryFull<?>> extends DockLayoutPanel implements Focusable {
	public static final int MIN_WIDTH = 950;
	public static final int MIN_HEIGHT = 700;
	private RegistryServiceAsync service;

	private static final Logger LOGGER = Logger.getLogger(AonRegistryFullPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	public static interface AonRegistryFullPanelCallback<R extends RegistryFull<?>> {
		void onAccept(R registryFull);
		void onCancel();
		void onError(Throwable caught);
		void onDocumenthanged(R registryFull);
		void setFocus(boolean b);
	}

	private final FlowPanel rootPanel = new FlowPanel();
	private final AonTextBox nameText = new AonTextBox();
	private AonAccountBox accountBox;
	
	protected AonRegistryFullPanel(AonModuleOptions<?> options, R registryFull,AonRegistryFullPanelCallback<R> callback) {
		super(Unit.PX);
		
		this.setStyleName(AON.CSS.aonSelector());
		this.addStyleName(AON.CSS.aonScrollArea());
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
		addButtons(options, buttons, registryFull, callback);
		this.addSouth(buttons, AonToolbar.HEIGTH);
		
		this.addNorth(rootPanel, 350);

		addRegistry(options, registryFull,callback);
		addExtended(options, registryFull,callback);
		
		TabLayoutPanel tabLayoutPanel = new TabLayoutPanel(30, Unit.PX);
		tabLayoutPanel.addStyleName(AON.CSS.aonMarginTop());
		tabLayoutPanel.add(new AonRegistryAddressGrid(options, registryFull), AON.MSG.addresses());
		tabLayoutPanel.add(new AonRegistryMediaGrid(registryFull), AON.MSG.contacts());
		tabLayoutPanel.add(new AonRegistryBankGrid(registryFull), AON.MSG.banks());
		this.add(tabLayoutPanel);
	}
	
	protected FlowPanel getRootPanel() {
		return rootPanel;
	}
	
	protected AonDisplayTable getNewTab() {
		AonDisplayTable displayTab = new AonDisplayTable();
		displayTab.addStyleName(AON.CSS.aonWidthAlmostAll());
		displayTab.addStyleName(AON.CSS.aonBlockCenter());
		return displayTab;
	}

	@Override
	public int getTabIndex() {
		return nameText.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		nameText.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		nameText.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		nameText.setTabIndex(index);
	}
	
	private void addRegistry(AonModuleOptions<?> options, R registryFull, AonRegistryFullPanelCallback<R> callback) {
		AonDisplayTable displayTab = getNewTab();
		Registry registry = registryFull.getRegistry();
		LOGGER.info( "AonRegistryFullPanel --> addRegistry " + registry.getName() ); 
		getRootPanel().add(displayTab);
		// ***************************************************************** [FULL DOCUMENT]
		AonFullDocument fulldocument = new AonFullDocument();
		fulldocument.setValue(registry.getDocumentType(), registry.getDocumentCountry(), registry.getDocument());
		fulldocument.addTypeChangeHandler(event -> registry.setDocumentType(fulldocument.getType()));
		fulldocument.addCountryChangeHandler(event -> registry.setDocumentCountry(fulldocument.getCountry()));
		
		fulldocument.addDocumentChangeHandler(event -> {
			registry.setDocument(fulldocument.getDocument());
			callback.onDocumenthanged(registryFull);
		});
		
		// ***************************************************************** [NAME]		
		nameText.setValue(registry.getName());
		nameText.setVisibleLength(40);
		nameText.setMaxLength(64);
		nameText.addValueChangeHandler(event -> registry.setName(nameText.getValue()));

		// ***************************************************************** [ALIAS]
		AonTextBox aliasText = new AonTextBox();
		aliasText.setValue(registry.getAlias());
		aliasText.setVisibleLength(30);
		aliasText.setMaxLength(32);
		aliasText.addValueChangeHandler(event -> registry.setAlias(aliasText.getValue()));
					
		// ***************************************************************** [NATIONALITY]
		CountryListBox nationalityBox = new CountryListBox();
		nationalityBox.setValue(registry.getNationality());
		nationalityBox.addChangeHandler(event -> registry.setNationality(nationalityBox.getValue()));
		
		addBasicRow(displayTab,new InlineLabel(AON.MSG.document()),fulldocument);
		addBasicRow(displayTab,new InlineLabel(AON.MSG.name()),nameText);
		addBasicRow(displayTab,new InlineLabel(AON.MSG.alias()),aliasText);
		addBasicRow(displayTab,new InlineLabel(AON.MSG.nationality()),nationalityBox);
	}
	
	protected void addBasicRow(AonDisplayTable tab, Widget label, Widget widget) {
		AonDisplayTableRow row = new AonDisplayTableRow();
		tab.add(row);
		AonDisplayTableCell labelCell = row.addCell(AON.CSS.aonTableLabel());
		labelCell.setWidth("180px");
		labelCell.add(label);
		AonDisplayTableCell widgetCell = row.addCell(AON.CSS.aonWidthAuto());
		widgetCell.add(widget);
	}
	
	protected void addScopeRow(AonDisplayTable displayTab, AonModuleOptions<?> options, IScopable<?> scopable) {
		if ( options.getConfiguration().hasAvailableScopes()) {
			scopable.setScope( options.getConfiguration().getAvailableScopes().get(0)); 
			final ListBox scopeBox = new ListBox();
			for (Scope scope : options.getConfiguration().getAvailableScopes()) {
				scopeBox.addItem(scope.getDescription(),AonNumberUtils.toString(scope.getId()));
			}
			scopeBox.addChangeHandler(event -> {
				Integer scopeId = AonNumberUtils.toint(scopeBox.getSelectedValue());
				Scope scope = options.getConfiguration().getAvailableScopes().stream()
					.filter(f -> f.getId().equals(scopeId))
					.findFirst().orElse(new Scope());
				scopable.setScope(scope);
			});
			addBasicRow(displayTab, new InlineLabel(AON.MSG.scope()),scopeBox);				
		}
	}

	protected void addAccountRow(AonDisplayTable displayTab,AonModuleOptions<?> options, IAccount<?> account) {
		Account acc = account.getAccount();
		accountBox = new AonAccountBox(options.getDomainName(),options.getDomain(),options.getUser());
		accountBox.setAccount(acc);
		accountBox.addSelectionHandler(event -> {
				Account a = event.getSelectedItem();
				account.setAccount(a);
		});
		addBasicRow(displayTab, new InlineLabel(AON.MSG.account()), accountBox);				
	}
	
	protected void setAccountEnable(boolean enabled) {
		accountBox.setEnabled(false);			
	}

	protected void addExtended(AonModuleOptions<?> options, R registryFull, AonRegistryFullPanelCallback<R> callback) {
		// Empty method
	}

	protected RegistryServiceAsync getService() {
		if (service == null) {
			RegistryServiceAsync serviceRaw = GWT.create(RegistryService.class);
			service = new RegistryServiceAsyncDecorator(serviceRaw);
		}
		return service;
	}
	
	protected abstract void addButtons(AonModuleOptions<?> options, FlowPanel buttons, R registryFull,AonRegistryFullPanelCallback<R> callback);	
}
