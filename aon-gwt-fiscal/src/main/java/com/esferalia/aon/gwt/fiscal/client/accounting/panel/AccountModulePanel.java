package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountModuleOptions;
import com.esferalia.aon.occam.api.model.AccountParams;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
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

	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
	}

	private SimpleLayoutPanel northPanel;
	private SimpleLayoutPanel centerPanel;
	
	private FlexTable tab;
	
	private IntegerBox id;
	private TextBox code;
	private TextBox description;
	private TextBox alias;
	private ListBox active;
	private ListBox level;
	private ListBox costCenter;

	private AonSearchPanelButton cleanButton;
	private AonSearchPanelButton refreshButton;
	

	public AccountModulePanel(AccountModuleOptions options) {
		super(Unit.PX);
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());
		northPanel = new SimpleLayoutPanel();
		
		id = new IntegerBox();
		id.setVisibleLength(8);
		id.addValueChangeHandler(event -> onSearch( options ));
		
		code= new TextBox();
		code.setVisibleLength(9);
		code.setMaxLength(9);
		code.setStyleName(AON.CSS.aonInputText());
		code.addValueChangeHandler(event -> onSearch( options ));
		
		description= new TextBox();
		description.setVisibleLength(20);
		description.setStyleName(AON.CSS.aonInputText());
		description.addValueChangeHandler(event -> onSearch( options ));
		
		alias = new TextBox();
		alias.setVisibleLength(10);
		alias.setStyleName(AON.CSS.aonInputText());
		alias.addValueChangeHandler(event -> onSearch( options ));
		
		level = new ListBox();
		level.setWidth("50px");
		level.addItem( "---" );
		level.addItem( "1 d\u00EDgito" );
		level.addItem( "2 d\u00EDgitos" );
		level.addItem( "3 d\u00EDgitos" );
		level.addItem( "4 d\u00EDgitos" );
		level.addItem( "9 d\u00EDgitos" );
		level.setSelectedIndex(0);
		level.addChangeHandler(event -> onSearch( options ));

		active = new ListBox();
		active.setWidth("100px");
		active.addItem( "Activas" );
		active.addItem( "Inactivas" );
		active.addItem(" Todas ");
		active.setSelectedIndex(2);
		active.addChangeHandler(event -> onSearch( options ));

		costCenter = new ListBox();
		costCenter.addItem("-", "");
		getCostCenters(options, costCenterStream -> costCenterStream.forEach(costCenterIt -> costCenter.addItem(costCenterIt.getValue(), costCenterIt.getValue())) );
		costCenter.setStyleName(AON.CSS.aonInputText());
		costCenter.addChangeHandler(event -> onSearch( options ));

		tab = new FlexTable();
		tab.setStyleName(AON.CSS.aonSearchPanel());
		tab.addStyleName(AON.CSS.aonMarginLeft());
		tab.addStyleName(AON.CSS.aonMarginRight());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		
		int row = 0;
		int col = 0;
		
		tab.setWidget(row, col, new Label(AON.MSG.code()));
		tab.getCellFormatter().setStyleName(row,col, AON.CSS.aonSearchPanelLabel());
		col++;
		tab.setWidget(row, col, code);
		col++;
		
		tab.setWidget(row, col, new Label(AON.MSG.description()));
		tab.getCellFormatter().setStyleName(row,col, AON.CSS.aonSearchPanelLabel());
		col++;
		tab.setWidget(row, col, description);
		col++;
		
		tab.setWidget(row, col, new Label(AON.MSG.aliasAbbr()));
		tab.getCellFormatter().setStyleName(row,col, AON.CSS.aonSearchPanelLabel());
		col++;
		tab.setWidget(row, col, alias);
		col++;

		tab.setWidget(row, col, new Label(AON.MSG.level()));
		tab.getCellFormatter().setStyleName(row,col, AON.CSS.aonSearchPanelLabel());
		col++;
		tab.setWidget(row, col, level);
		col++;

		tab.setWidget(row, col, new Label(AON.MSG.costCenter()));
		tab.getCellFormatter().setStyleName(row,col, AON.CSS.aonSearchPanelLabel());
		col++;
		tab.setWidget(row, col, costCenter);
		col++;

		tab.setWidget(row, col, active);
		col++;

		cleanButton = new AonSearchPanelButton( AON.MSG.clean(), AON.CSS.aonIconClear() );
		cleanButton.addClickHandler(event -> {
			id.setValue(null,false);
			code.setValue(null,false);
			description.setValue(null,false);
			alias.setValue(null,false);
			active.setSelectedIndex(2);
			level.setSelectedIndex(0);
			setSelectedValueLB(costCenter, "");
			code.setFocus(true);
			onSearch( options );
		});

		refreshButton = new AonSearchPanelButton( AON.MSG.refresh(), AON.CSS.aonIconRefresh() );
		refreshButton.addStyleName(AON.CSS.aonMarginLeft());
		refreshButton.addClickHandler(event -> onSearch( options ));

		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.add( cleanButton );
		buttonsPanel.add( refreshButton );
		tab.setWidget(row, col, buttonsPanel);
		col++;
		
		for (int i = 0; i < col; i++) {
			tab.getColumnFormatter().setWidth(i, "1%");
		}
		tab.getColumnFormatter().setWidth(col-1, "auto");
		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.CSS.aonWidthAll());
		scrollPanel.setWidget(tab);
		northPanel.setWidget(scrollPanel);

		addNorth(northPanel, 60);
		centerPanel = new SimpleLayoutPanel();
		add(centerPanel);
		onSearch( options );
	}
	
	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (lBox.getValue(i).equals(text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
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
		code.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		code.setFocus(true);
	}

	@Override
	public void setTabIndex(int index) {
		code.setTabIndex(index);
	}
	
	public void onSearch( AccountModuleOptions options ) {
		AccountParams params = getWidgetParams( options );
		AccountPanel accountPanel = new AccountPanel(params);
		centerPanel.setWidget(accountPanel);
	}

	public AccountParams getWidgetParams( AccountModuleOptions options) {
		return new AccountParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setId(id.getValue())
			.setCode(code.getValue())
			.setDescription(description.getValue())
			.setAlias(alias.getValue())
			.setActive(active.getSelectedIndex()==2?null: active.getSelectedIndex() == 0)
			.setLevel(level.getSelectedIndex()==0?null:((byte) level.getSelectedIndex()))
			.setCostCenter(costCenter.getSelectedValue())
			;
	}
	
	private void getCostCenters(AccountModuleOptions options, Consumer<List<ApplicationParameter>> success) {
		COMMON_SERVICE.getCostCenters(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<ApplicationParameter>>() {
			
			@Override
			public void onSuccess(List<ApplicationParameter> customer) {
				success.accept(customer);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
}
