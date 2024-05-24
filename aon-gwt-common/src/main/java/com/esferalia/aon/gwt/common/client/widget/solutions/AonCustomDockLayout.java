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
	private HTMLPanel toolbarButtonsRight = new HTMLPanel(EMPTY_STRING);
	private TextBox searchTextBox = new TextBox();
	private AonToolbarButton filterButton = new AonToolbarButton("Filtros", AON.CSS.aonIconFilterList());
	
	// Right Menu
	private ScrollPanel rightMenuScroll;
	private boolean isRightMenuShown = false;
	private HTMLPanel rightMenu = new HTMLPanel(EMPTY_STRING);
	
	private HTMLPanel filterToolbarButtonsMenu = new HTMLPanel(EMPTY_STRING);
	
	private boolean isFilterShown = true;
	private HTMLPanel filterBody = new HTMLPanel(EMPTY_STRING);
	
	private boolean isUtilitiesShown = true;
	private HTMLPanel utilitiesBody = new HTMLPanel(EMPTY_STRING);
	
	private boolean isSortShown = true;
	private HTMLPanel sortBody = new HTMLPanel(EMPTY_STRING);
	
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
		
		Label titleLabel = new Label(title);
		titleLabel.getElement().getStyle().setProperty("font-size", "1.1rem");
		toolbarLeft.add(titleLabel);
		
		toolbar.add(toolbarLeft);
		
		// Right
		HTMLPanel toolbarRight = new HTMLPanel(EMPTY_STRING);
		toolbarRight.addStyleName(AON.CSS.aonItemFlex());
		
		HTMLPanel searchPanel = new HTMLPanel(EMPTY_STRING);
		searchPanel.setStyleName(AON.CSS.aonCustomSearchBox());
		
		AonTableButton searchButton = new AonTableButton("Buscar", AON.CSS.aonIconSearch());
		
		searchTextBox.setStyleName(AON.CSS.aonCustomTextBoxInputNoBorder());
		searchTextBox.getElement().setPropertyString("placeholder", "Filtrar por documento, nombre o alias");
		searchTextBox.getElement().getStyle().setProperty("min-width", "14rem");
		
		searchPanel.add(searchButton);
		searchPanel.add(searchTextBox);
		
		filterButton.addClickHandler(e -> {
			isRightMenuShown = !isRightMenuShown;
			setWidgetSize(rightMenuScroll, isRightMenuShown ? 350.00 : 0.00);
			animate(500);
			
			filterButton.removeStyleName(isRightMenuShown ? AON.CSS.aonIconFilterList() : AON.CSS.aonIconFilterListOff());
			filterButton.addStyleName(isRightMenuShown ? AON.CSS.aonIconFilterListOff() : AON.CSS.aonIconFilterList());
		});
		
		toolbarRight.add(toolbarButtonsRight);
		toolbarRight.add(searchPanel);
		toolbarRight.add(filterButton);
		
		toolbar.add(toolbarRight);
		
		addNorth(toolbar, 50.00);
	}
	
	public void addToolbarButton(Widget widget) {
		toolbarButtonsRight.add(widget);
	}
	
	public TextBox getSearchTextBox() {
		return searchTextBox;
	}
	
	// Right menu
	
	public void addFilterMenu() {
		// Right menu
		rightMenu.addStyleName(AON.CSS.aonFlexColumn());
		rightMenu.getElement().getStyle().setProperty("padding", "1rem");
		rightMenu.getElement().getStyle().setProperty("gap", "2rem");
		
		// Filter
		HTMLPanel filterMenu = new HTMLPanel(EMPTY_STRING);
		filterMenu.addStyleName(AON.CSS.aonFlexColumn());
		
		// Filter toolbar
		HTMLPanel filterToolbarMenu = new HTMLPanel(EMPTY_STRING);
		filterToolbarMenu.addStyleName(AON.CSS.aonFlexBetween());
		
		Label filterToolbarTitle = new Label("Filtros");
		filterToolbarTitle.getElement().getStyle().setProperty("cursor", "pointer");
		filterToolbarTitle.getElement().getStyle().setProperty("width", "100%");
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
		
		rightMenu.add(filterMenu);
		
		rightMenuScroll = new ScrollPanel(rightMenu);
		
		addEast(rightMenuScroll, 0.00);
	}
	
	public void addFilterToolbarButton(Button button) {
		filterToolbarButtonsMenu.add(button);
	}
	
	public void addFilterWidget(Widget widget) {
		filterBody.add(widget);
	}
	
	public void addSortMenu() {
		// Utilities
		HTMLPanel sortMenu = new HTMLPanel(EMPTY_STRING);
		sortMenu.addStyleName(AON.CSS.aonFlexColumn());
		
		// Utilities toolbar
		HTMLPanel sortToolbarMenu = new HTMLPanel(EMPTY_STRING);
		sortToolbarMenu.addStyleName(AON.CSS.aonFlexBetween());
		
		Label sortToolbarTitle = new Label("Ordenar");
		sortToolbarTitle.getElement().getStyle().setProperty("cursor", "pointer");
		sortToolbarTitle.getElement().getStyle().setProperty("width", "100%");
		sortToolbarTitle.getElement().getStyle().setProperty("font-size", "1rem");
		sortToolbarMenu.add(sortToolbarTitle);
		
		sortMenu.add(sortToolbarMenu);
		
		// Filter body
		sortBody.addStyleName(AON.CSS.aonFlexColumn());
		sortBody.getElement().getStyle().setProperty("padding-left", "0.5rem");
		sortMenu.add(sortBody);
		
		sortToolbarTitle.addClickHandler(e -> {
			isSortShown = !isSortShown;
			sortBody.setVisible(isSortShown);
		});
		
		rightMenu.add(sortMenu);
	}
	
	public void addSortWidget(Widget widget) {
		sortBody.add(widget);
	}
	
	public void addUtilitiesMenu() {
		// Utilities
		HTMLPanel utilitiesMenu = new HTMLPanel(EMPTY_STRING);
		utilitiesMenu.addStyleName(AON.CSS.aonFlexColumn());
		
		// Utilities toolbar
		HTMLPanel utilitiesToolbarMenu = new HTMLPanel(EMPTY_STRING);
		utilitiesToolbarMenu.addStyleName(AON.CSS.aonFlexBetween());
		
		Label utilitiesToolbarTitle = new Label("Utilidades");
		utilitiesToolbarTitle.getElement().getStyle().setProperty("cursor", "pointer");
		utilitiesToolbarTitle.getElement().getStyle().setProperty("width", "100%");
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
		
		rightMenu.add(utilitiesMenu);
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
