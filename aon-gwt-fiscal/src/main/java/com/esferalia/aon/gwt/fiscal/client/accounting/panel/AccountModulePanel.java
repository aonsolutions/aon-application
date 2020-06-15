package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.occam.api.model.AccountParams;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;


public class AccountModulePanel extends DockLayoutPanel implements Focusable, HasAccountEntrySelectionHandlers{

	private String currentDomainName;
	private String currentUser;
	private Integer currentDomainId;
	
	private SimpleLayoutPanel northPanel;
	private SimpleLayoutPanel centerPanel;
	
	private FlexTable tab;
	
	private IntegerBox id;
	private TextBox code;
	private TextBox description;
	private TextBox alias;
	private ListBox active;
	private ListBox level;
	private TextBox costCenter;

	private Button cleanButton;
	private Button refreshButton;

	public AccountModulePanel(String domainName, int domainId,String user) {
		super(Unit.PX);
		this.currentDomainName = domainName;
		this.currentUser = user;
		this.currentDomainId = domainId;
		addStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonMarginBottom());
		northPanel = new SimpleLayoutPanel();
		
		id = new IntegerBox();
		id.setVisibleLength(8);
		id.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				onSearch();
			}
		});
		
		code= new TextBox();
		code.setVisibleLength(9);
		code.setMaxLength(9);
		code.setStyleName(AON.AON_CSS.aonInputText());
		code.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				onSearch();
			}
		});
		
		description= new TextBox();
		description.setVisibleLength(20);
		description.setStyleName(AON.AON_CSS.aonInputText());
		description.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				onSearch();
			}
		});
		
		alias = new TextBox();
		alias.setVisibleLength(10);
		alias.setStyleName(AON.AON_CSS.aonInputText());
		alias.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				onSearch();
			}
		});
		
		level = new ListBox();
		level.setWidth("50px");
		level.addItem( "---" );
		level.addItem( "1 d\u00EDgito" );
		level.addItem( "2 d\u00EDgitos" );
		level.addItem( "3 d\u00EDgitos" );
		level.addItem( "4 d\u00EDgitos" );
		level.addItem( "9 d\u00EDgitos" );
		level.setSelectedIndex(0);
		level.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onSearch();
			}
		});

		active = new ListBox();
		active.setWidth("100px");
		active.addItem( "Activas" );
		active.addItem( "Inactivas" );
		active.addItem(" Todas ");
		active.setSelectedIndex(2);
		active.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				onSearch();
			}
		});

		costCenter = new TextBox();
		costCenter.setVisibleLength(15);
		costCenter.setStyleName(AON.AON_CSS.aonInputText());
		costCenter.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				onSearch();
			}
		});

		tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonPanelGridSearch());
		tab.addStyleName(AON.AON_CSS.aonWidthAll());
		
		int row = 0;
		int col = 0;
		
		tab.setWidget(row, col, new Label(AON.MSG.code()));
		tab.getCellFormatter().setStyleName(row,col, AON.AON_CSS.aonPanelGridOdd());
		col++;
		tab.setWidget(row, col, code);
		tab.getCellFormatter().setStyleName(row,col, AON.AON_CSS.aonPanelGridEven());
		col++;
		
		tab.setWidget(row, col, new Label(AON.MSG.description()));
		tab.getCellFormatter().setStyleName(row,col, AON.AON_CSS.aonPanelGridOdd());
		col++;
		tab.setWidget(row, col, description);
		tab.getCellFormatter().setStyleName(row,col, AON.AON_CSS.aonPanelGridEven());
		col++;
		
		tab.setWidget(row, col, new Label(AON.MSG.aliasAbbr()));
		tab.getCellFormatter().setStyleName(row,col, AON.AON_CSS.aonPanelGridOdd());
		col++;
		tab.setWidget(row, col, alias);
		tab.getCellFormatter().setStyleName(row,col, AON.AON_CSS.aonPanelGridEven());
		col++;

		tab.setWidget(row, col, new Label(AON.MSG.level()));
		tab.getCellFormatter().setStyleName(row,col, AON.AON_CSS.aonPanelGridOdd());
		col++;
		tab.setWidget(row, col, level);
		tab.getCellFormatter().setStyleName(row,col, AON.AON_CSS.aonPanelGridEven());
		col++;

		tab.setWidget(row, col, new Label(AON.MSG.costCenter()));
		tab.getCellFormatter().setStyleName(row,col, AON.AON_CSS.aonPanelGridOdd());
		col++;
		tab.setWidget(row, col, costCenter);
		tab.getCellFormatter().setStyleName(row,col, AON.AON_CSS.aonPanelGridEven());
		col++;

		tab.setWidget(row, col, active);
		tab.getCellFormatter().setStyleName(row,col, AON.AON_CSS.aonPanelGridEven());
		col++;

		cleanButton = new Button();
		cleanButton.setTitle(AON.MSG.clean());
		cleanButton.setStyleName(AON.AON_CSS.aonIconDelete());
		cleanButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		cleanButton.addStyleName(AON.AON_CSS.aonMarginLeft());
		cleanButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
		cleanButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				id.setValue(null,false);
				code.setValue(null,false);
				description.setValue(null,false);
				alias.setValue(null,false);
				active.setSelectedIndex(2);
				level.setSelectedIndex(0);
				costCenter.setValue(null,false);
				code.setFocus(true);
				onSearch();
			}
		});

		refreshButton = new Button();
		refreshButton.setTitle(AON.MSG.refresh());
		refreshButton.setStyleName(AON.AON_CSS.aonIconRefresh());
		refreshButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
		refreshButton.addStyleName(AON.AON_CSS.aonMarginLeft());
		refreshButton.addStyleName(AON.AON_CSS.aonMarginLeft5());
		refreshButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSearch();
			}
		});

		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.add( cleanButton );
		buttonsPanel.add( refreshButton );
		tab.setWidget(row, col, buttonsPanel);
		tab.getCellFormatter().setStyleName(row,col, AON.AON_CSS.aonPanelGridEven());
		col++;
		
		for (int i = 0; i < col; i++) {
			tab.getColumnFormatter().setWidth(i, "1%");
		}
		tab.getColumnFormatter().setWidth(col-1, "auto");
		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		scrollPanel.setWidget(tab);
		northPanel.setWidget(scrollPanel);

		addNorth(northPanel, 60);
		centerPanel = new SimpleLayoutPanel();
		add(centerPanel);
		onSearch();
	}
	
	public String getCurrentDomainName() {
		return currentDomainName;
	}
	public Integer getCurrentDomainId() {
		return currentDomainId;
	}
	public String getCurrentUser() {
		return currentUser;
	}

	@Override
	public HandlerRegistration addSelectionHandler(AccountEntrySelectionHandler handler) {
		return super.addHandler(handler, AccountEntrySelectionEvent.getType());
	}

	@Override
	public int getTabIndex() {
		return code.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		code.setAccessKey(key);;
	}

	@Override
	public void setFocus(boolean focused) {
		code.setFocus(true);
	}

	@Override
	public void setTabIndex(int index) {
		code.setTabIndex(index);
	}
	
	private void onSearch() {
		AccountParams params = getWidgetParams();
		AccountPanel accountPanel = new AccountPanel(params);
//		accountPanel.addSelectionHandler(new AccountEntrySelectionHandler() {
//			
//			@Override
//			public void onSelection(AccountSelectionEvent event) {
//				AccountEntrySelectionEvent.fire(AccountModulePanel.this, event.getSelectedItem(), event.getCallback() );
//			}
//
//		});
		centerPanel.setWidget(accountPanel);
	}

	public AccountParams getWidgetParams() {
		return new AccountParams()
			.setDomainName(this.currentDomainName)
			.setDomain(this.currentDomainId)
			.setUser(this.currentUser)
			.setId(id.getValue())
			.setCode(code.getValue())
			.setDescription(description.getValue())
			.setAlias(alias.getValue())
			.setActive(active.getSelectedIndex()==2?null: active.getSelectedIndex() == 0)
			.setLevel(level.getSelectedIndex()==0?null:((byte) level.getSelectedIndex()))
			.setCostCenter(costCenter.getValue())
			;
	}
	
}
