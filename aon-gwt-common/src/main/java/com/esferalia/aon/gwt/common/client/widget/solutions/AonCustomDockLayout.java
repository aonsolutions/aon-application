package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AonCustomDockLayout extends DockLayoutPanel {

	private static final String EMPTY_STRING = "";

	// Toolbar
	private AonToolbarButton filterButton = new AonToolbarButton("Filtrar", AON.CSS.aonIconFilterList());
	private HTMLPanel toolbarRight = new HTMLPanel(EMPTY_STRING);
	private TextBox searchTextBox = new TextBox();
	private boolean isSearchTextBoxShown = true;
	
	// Left Menu
	private ScrollPanel leftMenuScroll;
	private boolean isLefMenuShown = false;
	private HTMLPanel leftMenu = new HTMLPanel(EMPTY_STRING);
	
	private HTMLPanel filterToolbarButtonsMenu = new HTMLPanel(EMPTY_STRING);
	
	private boolean isFilterShown = true;
	private HTMLPanel filterBody = new HTMLPanel(EMPTY_STRING);
	
	private boolean isUtilitiesShown = true;
	private HTMLPanel utilitiesBody = new HTMLPanel(EMPTY_STRING);
	
	public AonCustomDockLayout(String title) {
		super(Unit.PX);
		
		if(AonStringUtils.isNotBlank(title))
			createToolbar(title);
	}
	
	// Toolbar
	
	private void createToolbar(String title) {
		HTMLPanel toolbar = new HTMLPanel(EMPTY_STRING);
		toolbar.addStyleName(AON.CSS.aonFlexBetween());
		toolbar.getElement().getStyle().setProperty("padding", "0 1rem");
		
		// Left
		HTMLPanel toolbarLeft = new HTMLPanel(EMPTY_STRING);
		toolbarLeft.addStyleName(AON.CSS.aonItemFlex());
		filterButton.setVisible(false);
		toolbarLeft.add(filterButton);
		
		Label titleLabel = new Label(title.toUpperCase());
		titleLabel.getElement().getStyle().setProperty("font-weight", "700");
		titleLabel.getElement().getStyle().setProperty("font-size", "1.2rem");
		toolbarLeft.add(titleLabel);
		
		toolbar.add(toolbarLeft);
		
		// Right
		toolbarRight.addStyleName(AON.CSS.aonItemFlex());
		
		HTMLPanel searchPanel = new HTMLPanel(EMPTY_STRING);
		searchPanel.addStyleName(AON.CSS.aonItemFlex());
		searchPanel.getElement().getStyle().setProperty("border-bottom", "1px solid #b9b8b8");
		
		AonToolbarButton searchButton = new AonToolbarButton("Buscar", AON.CSS.aonIconSearch());
		searchButton.addClickHandler(e -> {
			isSearchTextBoxShown = !isSearchTextBoxShown;
			searchTextBox.setVisible(isSearchTextBoxShown);
			
			if(isSearchTextBoxShown)
				searchPanel.getElement().getStyle().setProperty("border-bottom", "1px solid #b9b8b8");
			else
				searchPanel.getElement().getStyle().clearProperty("border-bottom");
		});
		
		searchTextBox.setStyleName(AON.CSS.aonCustomTextBoxInputNoBorder());
		searchTextBox.getElement().setPropertyString("placeholder", "Filtrar por documento, nombre o alias");
		searchTextBox.getElement().getStyle().setProperty("min-width", "14rem");
		
		searchPanel.add(searchButton);
		searchPanel.add(searchTextBox);
		
		toolbarRight.add(searchPanel);
		
		toolbar.add(toolbarRight);
		
		addNorth(toolbar, 50.00);
	}
	
	public void addToolbarButton(Widget widget) {
		toolbarRight.add(widget);
	}
	
	public TextBox getSearchTextBox() {
		return searchTextBox;
	}
	
	// Left menu
	
	public void addFilterMenu() {
		filterButton.setVisible(true);
		filterButton.addClickHandler(e -> {
			isLefMenuShown = !isLefMenuShown;
			setWidgetSize(leftMenuScroll, isLefMenuShown ? 350.00 : 0.00);
			animate(500);
			
			filterButton.removeStyleName(isLefMenuShown ? AON.CSS.aonIconFilterList() : AON.CSS.aonIconFilterListOff());
			filterButton.addStyleName(isLefMenuShown ? AON.CSS.aonIconFilterListOff() : AON.CSS.aonIconFilterList());
		});
		
		// Left menu
		leftMenu.addStyleName(AON.CSS.aonFlexColumn());
		leftMenu.getElement().getStyle().setProperty("padding", "1rem");
		leftMenu.getElement().getStyle().setProperty("gap", "2rem");
		
		// Filter
		HTMLPanel filterMenu = new HTMLPanel(EMPTY_STRING);
		filterMenu.addStyleName(AON.CSS.aonFlexColumn());
		
		// Filter toolbar
		HTMLPanel filterToolbarMenu = new HTMLPanel(EMPTY_STRING);
		filterToolbarMenu.addStyleName(AON.CSS.aonFlexBetween());
		
		Label filterToolbarTitle = new Label("FILTRAR");
		filterToolbarTitle.getElement().getStyle().setProperty("cursor", "pointer");
		filterToolbarTitle.getElement().getStyle().setProperty("width", "100%");
		filterToolbarTitle.getElement().getStyle().setProperty("font-weight", "700");
		filterToolbarTitle.getElement().getStyle().setProperty("font-size", "1rem");
		filterToolbarMenu.add(filterToolbarTitle);
		
		filterToolbarButtonsMenu.addStyleName(AON.CSS.aonItemFlex());
		filterToolbarButtonsMenu.getElement().getStyle().setProperty("gap", "0");
		
		filterToolbarMenu.add(filterToolbarButtonsMenu);
		filterMenu.add(filterToolbarMenu);
		
		// Filter body
		filterBody.addStyleName(AON.CSS.aonFlexColumn());
		filterBody.getElement().getStyle().setProperty("padding-left", "0.5rem");
		filterMenu.add(filterBody);
		
		filterToolbarTitle.addClickHandler(e -> {
			isFilterShown = !isFilterShown;
			filterBody.setVisible(isFilterShown);
		});
		
		leftMenu.add(filterMenu);
		
		leftMenuScroll = new ScrollPanel(leftMenu);
		
		addWest(leftMenuScroll, 0.00);
	}
	
	public void addFilterToolbarButton(Button button) {
		filterToolbarButtonsMenu.add(button);
	}
	
	public void addFilterWidget(Widget widget) {
		filterBody.add(widget);
	}
	
	public void addUtilitiesMenu() {
		// Utilities
		HTMLPanel utilitiesMenu = new HTMLPanel(EMPTY_STRING);
		utilitiesMenu.addStyleName(AON.CSS.aonFlexColumn());
		
		// Utilities toolbar
		HTMLPanel utilitiesToolbarMenu = new HTMLPanel(EMPTY_STRING);
		utilitiesToolbarMenu.addStyleName(AON.CSS.aonFlexBetween());
		
		Label utilitiesToolbarTitle = new Label("UTILIDADES");
		utilitiesToolbarTitle.getElement().getStyle().setProperty("cursor", "pointer");
		utilitiesToolbarTitle.getElement().getStyle().setProperty("width", "100%");
		utilitiesToolbarTitle.getElement().getStyle().setProperty("font-weight", "700");
		utilitiesToolbarTitle.getElement().getStyle().setProperty("font-size", "1rem");
		utilitiesToolbarMenu.add(utilitiesToolbarTitle);
		
		utilitiesMenu.add(utilitiesToolbarMenu);
		
		// Filter body
		utilitiesBody.addStyleName(AON.CSS.aonFlexColumn());
		utilitiesBody.getElement().getStyle().setProperty("padding-left", "0.5rem");
		utilitiesMenu.add(utilitiesBody);
		
		utilitiesToolbarTitle.addClickHandler(e -> {
			isUtilitiesShown = !isUtilitiesShown;
			utilitiesBody.setVisible(isUtilitiesShown);
		});
		
		leftMenu.add(utilitiesMenu);
	}
	
	public void addUtilityOption(Button button, String text) {
		HTMLPanel utilityPanel = new HTMLPanel(EMPTY_STRING);
		utilityPanel.addStyleName(AON.CSS.aonUtilityOption());
		
		if(null != button)
			utilityPanel.add(button);
		
		Label textLabel = new Label(text);
		utilityPanel.add(textLabel);
		
		utilitiesBody.add(utilityPanel);
	}

}
