package com.esferalia.aon.gwt.marketing.client.marketing.panel;

import java.util.Arrays;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.marketing.client.marketing.MarketingModuleOptions;
import com.esferalia.aon.occam.api.model.QuestionParams;
import com.esferalia.aon.occam.api.model.registry.QuestionType;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;


public class QuestionModulePanel extends DockLayoutPanel {

	private SimpleLayoutPanel northPanel;
	private SimpleLayoutPanel centerPanel;
	
	private FlowPanel searchPanel;
	private FlowPanel filterPanel;
	
	private TextBox alias;
	private ListBox type;
	private ListBox active;
	
	private AonSearchPanelButton cleanButton;
	private AonSearchPanelButton refreshButton;
	
	public QuestionModulePanel(MarketingModuleOptions options) {
		super(Unit.PX);
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());
		northPanel = new SimpleLayoutPanel();
		
		alias = new TextBox();
		alias.setVisibleLength(50);
		alias.setStyleName(AON.CSS.aonInputText());
		alias.addValueChangeHandler(event -> onSearch( options ));
		
		type = new ListBox();
		type.addItem( "-", "");
		Arrays.stream(QuestionType.values()).forEach(qt -> type.addItem(qt.description(), qt.ordinal() + ""));
		type.setSelectedIndex(0);
		type.setStyleName(AON.CSS.aonInputText());
		type.addChangeHandler(event -> onSearch( options ));
		
		active = new ListBox();
		active.addItem( "Todas", "");
		active.addItem( "Inactivas", "0");
		active.addItem( "Activas", "1");
		active.setSelectedIndex(2);
		active.setStyleName(AON.CSS.aonInputText());
		active.addChangeHandler(event -> onSearch( options ));
		
		searchPanel = new FlowPanel();
		searchPanel.setStyleName(AON.CSS.aonSearchPanel());
		searchPanel.addStyleName(AON.CSS.aonFlexBetween());
		
		filterPanel = new FlowPanel();
		filterPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label aliasLabel = new Label("Alias");
		aliasLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		filterPanel.add(aliasLabel);
		filterPanel.add(alias);
		
		Label typeLabel = new Label(AON.MSG.type());
		typeLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		filterPanel.add(typeLabel);
		filterPanel.add(type);

		filterPanel.add(active);

		searchPanel.add(filterPanel);
		
		cleanButton = new AonSearchPanelButton( AON.MSG.clean(), AON.CSS.aonIconClear() );
		cleanButton.addClickHandler(event -> {
			alias.setValue(null,false);
			type.setSelectedIndex(0);
			
			onSearch( options );
		});

		refreshButton = new AonSearchPanelButton( AON.MSG.refresh(), AON.CSS.aonIconRefresh() );
		refreshButton.addStyleName(AON.CSS.aonMarginLeft());
		refreshButton.addClickHandler(event -> onSearch( options ));

		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.add( cleanButton );
		buttonsPanel.add( refreshButton );
		searchPanel.add(buttonsPanel);
		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.CSS.aonScrollArea());
		scrollPanel.setWidget(searchPanel);
		northPanel.setWidget(scrollPanel);

		addNorth(northPanel, 50);
		centerPanel = new SimpleLayoutPanel();
		add(centerPanel);
		onSearch( options );
	}
	
	public void onSearch( MarketingModuleOptions options ) {
		QuestionParams params = getWidgetParams( options );
		QuestionPanel investAssetPanel = new QuestionPanel(params);
		centerPanel.setWidget(investAssetPanel);
	}

	public QuestionParams getWidgetParams( MarketingModuleOptions options) {
		return new QuestionParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setAlias(alias.getValue())
			.setType(type.getSelectedIndex() == 0 ? null : Byte.parseByte(type.getSelectedValue()))
			.setActive(active.getSelectedIndex() == 0 ? null : Byte.parseByte(active.getSelectedValue()))
			;
	}
	
}
