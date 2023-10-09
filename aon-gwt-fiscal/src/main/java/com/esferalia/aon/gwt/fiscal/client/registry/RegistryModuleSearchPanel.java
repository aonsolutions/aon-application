package com.esferalia.aon.gwt.fiscal.client.registry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.DocumentTypeListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;


public class RegistryModuleSearchPanel extends SimpleLayoutPanel implements Focusable, HasValueChangeHandlers<RegistryParams>{

	public static final double HEIGHT = 130;
	
	private DocumentTypeListBox documentTypeBox;
	private CountryListBox documentCountryBox;
	private TextBox documentBox;
	private TextBox nameBox;
	private TextBox aliasBox;
	private ListBox confidentialBox;
	private CheckBox activeBox;
	private CheckBox inactiveBox;
	private CheckBox blockedBox;
	
	private AonSearchPanelButton cleanButton;
	private AonSearchPanelButton refreshButton;

	public RegistryModuleSearchPanel(final RegistryModuleOptions opt) {
		
		setStyleName(AON.CSS.aonSearchPanel());
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());
		addStyleName(AON.CSS.aonMarginLeft());
		addStyleName(AON.CSS.aonMarginRight());
		addStyleName(AON.CSS.aonBlockCenter());

		documentTypeBox = new DocumentTypeListBox();
		documentTypeBox.setSelectedIndex(0);
		documentTypeBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				search(opt);
			}
		});
		documentCountryBox = new CountryListBox();
		documentCountryBox.addStyleName(AON.CSS.aonMarginLeftSep());
		documentCountryBox.setSelectedIndex(0);
		documentCountryBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				search(opt);
			}
		});

		documentBox = new AonTextBox();
		documentBox.addStyleName(AON.CSS.aonMarginLeftSep());
		documentBox.setVisibleLength(9);
		documentBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> arg0) {
				search(opt);
			}
		});

		nameBox = new AonTextBox();
		nameBox.setVisibleLength(30);
		nameBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> arg0) {
				search(opt);
			}
		});
		
		aliasBox = new AonTextBox();
		aliasBox.setVisibleLength(20);
		aliasBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> arg0) {
				search(opt);
			}
		});
		
		confidentialBox = new ListBox();
		confidentialBox.setWidth("100px");
		confidentialBox.addItem( "NO confidenciales" );
		confidentialBox.addItem( "Confidenciales" );
		confidentialBox.addItem(" Todos ");
		confidentialBox.setSelectedIndex(2);
		confidentialBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				search(opt);
			}
		});
		
		activeBox = new CheckBox( RegistryStatus.ACTIVE.getDescription()) ;
		activeBox.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				search(opt);
			}
		});
		
		inactiveBox = new CheckBox( RegistryStatus.INACTIVE.getDescription()) ;
		inactiveBox.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				search(opt);
			}
		});
		
		blockedBox = new CheckBox( RegistryStatus.BLOCKED.getDescription()) ;
		blockedBox.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				search(opt);
			}
		});
		
		cleanButton = new AonSearchPanelButton(AON.MSG.clean(), AON.CSS.aonIconClear());
		cleanButton.addStyleName(AON.CSS.aonMarginLeft());
		cleanButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				initialize(opt);
			}
		});

		refreshButton = new AonSearchPanelButton(AON.MSG.refresh(), AON.CSS.aonIconRefresh());
		refreshButton.addStyleName(AON.CSS.aonMarginLeft());
		refreshButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				search(opt);
			}
		});

		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.CSS.aonGrid());
		tab.setWidth("100%");
		
		tab.getColumnFormatter().setWidth(0, "110px");
		tab.getColumnFormatter().setWidth(1, "250px");
		tab.getColumnFormatter().setWidth(2, "110px");
		tab.getColumnFormatter().setWidth(3, "250px");
		tab.getColumnFormatter().setWidth(4, "110px");
		tab.getColumnFormatter().setWidth(5, "250px");
		tab.getColumnFormatter().setWidth(6, "auto");
		
		int row = 0;
		int col = 0;
		
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.document()));
		++col;
		FlowPanel documentPanel = new FlowPanel();
		documentPanel.setStyleName(AON.CSS.aonNowrap());
		documentPanel.add(documentTypeBox);
		documentPanel.add(documentCountryBox);
		documentPanel.add(documentBox);
		tab.setWidget(row, col, documentPanel);
		++col;


		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.name()));
		++col;
		tab.setWidget(row, col, nameBox);
		++col;
		
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.aliasAbbr()));
		++col;
		tab.setWidget(row, col, aliasBox);
		++col;
		
		tab.setWidget(row, col, new InlineLabel());
		
		++row;
		col = 0;

		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.status()));
		++col;
		FlowPanel statusPanel = new FlowPanel();
		statusPanel.add(activeBox);
		statusPanel.add(inactiveBox);
		statusPanel.add(blockedBox);
		tab.setWidget(row, col, statusPanel);
		++col;
		
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		if (opt.getConfiguration() != null && opt.getConfiguration().getUser() != null && opt.getConfiguration().getUser().hasConfidentialityRole()) {
			tab.setWidget(row, col, new Label(AON.MSG.show()));
			++col;
			tab.setWidget(row, col, confidentialBox);
			++col;
		} else {
			tab.setWidget(row, col, new InlineLabel());
			++col;
			tab.setWidget(row, col, new InlineLabel());
			++col;
		}
		++col;
		
		tab.setWidget(row, col, new InlineLabel());
		++col;
		tab.setWidget(row, col, new InlineLabel());
		++col;
		
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonNowrap());
		buttonsPanel.add( cleanButton );
		buttonsPanel.add( refreshButton );
		tab.setWidget(row, col, buttonsPanel);
		++col;
		setWidget(tab);
		
		initialize(opt);
	}
	
	@Override
	public int getTabIndex() {
		return nameBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		nameBox.setAccessKey(key);;
	}

	@Override
	public void setFocus(boolean focused) {
		nameBox.setFocus(true);
		nameBox.selectAll();
	}

	@Override
	public void setTabIndex(int index) {
		nameBox.setTabIndex(index);
	}
	
	private void search(final RegistryModuleOptions opt) {
		ValueChangeEvent.<RegistryParams>fire( RegistryModuleSearchPanel.this, getParams( opt ) ); 
	}

	public void initialize(final RegistryModuleOptions opt) {
		documentTypeBox.setSelectedIndex(0);
		documentCountryBox.setSelectedIndex(0);
		documentBox.setValue(null);
		nameBox.setValue(null);
		aliasBox.setValue(null);
		confidentialBox.setSelectedIndex(2);
		activeBox.setValue(true);
		inactiveBox.setValue(false);
		blockedBox.setValue(false);
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<RegistryParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	public RegistryParams getParams( final RegistryModuleOptions opt) {
		boolean confidentiality = opt.getConfiguration() != null 
				&& opt.getUser() != null 
				&& opt.getConfiguration().getUser().hasConfidentialityRole();
		return new RegistryParams()
			.setDomain(opt.getDomain())
			.setDocumentType(documentTypeBox.getValue())
			.setDocumentCountry(documentCountryBox.getValue())
			.setDocument(documentBox.getValue())
			.setName(nameBox.getValue())
			.setAlias(aliasBox.getValue())
			.setSecurityLevel(confidentialBox!=null?SecurityLevel.safeValueOf(confidentialBox.getSelectedIndex()):SecurityLevel.OFFICIAL)
			.setActive(activeBox.getValue())
			.setInactive(inactiveBox.getValue())
			.setBlocked(blockedBox.getValue())
			.setHasConfidentialityRole(confidentiality)
			;
	}
	
}
