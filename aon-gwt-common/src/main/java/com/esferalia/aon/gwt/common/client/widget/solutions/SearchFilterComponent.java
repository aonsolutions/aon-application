package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class SearchFilterComponent extends HTMLPanel {

    private AonTableButton searchButton = new AonTableButton("Buscar", AON.CSS.aonIconSearch());
    private TextBox searchTextBox = new TextBox();
    private AonTableButton filterButton = new AonTableButton("Filtros", AON.CSS.aonIconFilterList());
	
    private PopupPanel searchMenuPopup = new PopupPanel(true);
    
    private HTMLPanel filterMenu;
    private HTMLPanel filterBody = new HTMLPanel("");
    
    private HTMLPanel sortMenu;
    private HTMLPanel sortBody = new HTMLPanel("");
    
    private HTMLPanel utilitiesMenu;
    private HTMLPanel utilitiesBody = new HTMLPanel("");

    // Constructor
    public SearchFilterComponent() {
        super("");

        this.setStyleName(AON.CSS.aonSearchFilter());
        
        searchTextBox.setStyleName(AON.CSS.aonCustomTextBoxInputNoBorder());
		searchTextBox.getElement().getStyle().setProperty("min-width", "14rem");

        // Agrega los botones al contenedor
        this.add(searchButton);
        this.add(searchTextBox);
        this.add(filterButton);

        // Configura el comportamiento del filterButton
        configureFilterButtonBehavior();
    }

    private void configureFilterButtonBehavior() {
    	
        filterButton.addClickHandler(event -> {
        	int top = this.getAbsoluteTop() + this.getOffsetHeight();
            int left = this.getAbsoluteLeft();
            searchMenuPopup.setPopupPosition(left, top);
            searchMenuPopup.getElement().getStyle().setZIndex(3);
            searchMenuPopup.show(); // Muestra con animación
        });
        
        // Configuración del PopupPanel (menu de búsqueda)
        setupSearchMenuPopup();
    }
    
   private void setupSearchMenuPopup() {
	   	HTMLPanel popupContent = new HTMLPanel("");
        popupContent.setStyleName(AON.CSS.aonSearchFilterPopup());
        
        // Menus
 		HTMLPanel menus = new HTMLPanel("");
 		menus.addStyleName(AON.CSS.aonFlexColumn());
        
        // Filter
 		filterMenu = new HTMLPanel("");
 		filterMenu.addStyleName(AON.CSS.aonFlexColumn());
 		
        // Sort
 		sortMenu = new HTMLPanel("");
 		sortMenu.addStyleName(AON.CSS.aonFlexColumn());
 		
 		// Utilities
		utilitiesMenu = new HTMLPanel("");
		utilitiesMenu.addStyleName(AON.CSS.aonFlexColumn());
		
		ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setStyleName(AON.CSS.aonSearchFilterScroll());
        scrollPanel.getElement().getStyle().setProperty("padding-right", ".5rem");
        menus.add(filterMenu);
        menus.add(sortMenu);
        menus.add(utilitiesMenu);
		
        scrollPanel.setWidget(menus);
        popupContent.add(scrollPanel);
        
        // Buttons
        HTMLPanel buttonPanel = new HTMLPanel("");
        buttonPanel.setStyleName(AON.CSS.aonDisplayFlexEnd());
        
        AonCustomButton clearButton = new AonCustomButton(AON.CSS.aonIconClear(), "Limpiar");
        clearButton.addDomHandler(e -> fireClearFilter(), ClickEvent.getType()); 
        buttonPanel.add(clearButton);
        popupContent.add(buttonPanel);
		
        searchMenuPopup.setWidget(popupContent);
    }

	public void addFilterWidget(Widget widget) {
		
		if(filterMenu.getWidgetCount() == 0) {
	 		HTMLPanel filterToolbarMenu = new HTMLPanel("");
	 		filterToolbarMenu.addStyleName(AON.CSS.aonFlexBetween());
	 		
	 		Label filterToolbarTitle = new Label("Filtros");
	 		filterToolbarTitle.getElement().getStyle().setProperty("cursor", "pointer");
	 		filterToolbarTitle.getElement().getStyle().setProperty("width", "100%");
	 		filterToolbarTitle.getElement().getStyle().setProperty("font-size", "1rem");
	 		filterToolbarTitle.getElement().getStyle().setProperty("font-weight", "bold");
	 		filterToolbarMenu.add(filterToolbarTitle);
	 		
	 		filterMenu.add(filterToolbarMenu);
	 		
	 		// Filter body
	 		filterBody.addStyleName(AON.CSS.aonFlexColumn());
	 		filterBody.getElement().getStyle().setProperty("padding-left", "0.5rem");
	 		filterMenu.add(filterBody);
	 		
	 		filterToolbarTitle.addClickHandler(e -> {
	 			filterBody.setVisible(!filterBody.isVisible());
	 		});
		}
		
		filterBody.add(widget);
	}

	public void addSortWidget(Widget widget) {
		
		if(sortMenu.getWidgetCount() == 0) {
			HTMLPanel sortToolbarMenu = new HTMLPanel("");
	 		sortToolbarMenu.addStyleName(AON.CSS.aonFlexBetween());
	 		
	 		Label sortToolbarTitle = new Label("Ordenar");
	 		sortToolbarTitle.getElement().getStyle().setProperty("cursor", "pointer");
	 		sortToolbarTitle.getElement().getStyle().setProperty("width", "100%");
	 		sortToolbarTitle.getElement().getStyle().setProperty("font-size", "1rem");
	 		sortToolbarTitle.getElement().getStyle().setProperty("font-weight", "bold");
	 		sortToolbarMenu.add(sortToolbarTitle);
	 		
	 		sortMenu.add(sortToolbarMenu);
	 		
	 		// Filter body
	 		sortBody.addStyleName(AON.CSS.aonFlexColumn());
	 		sortBody.getElement().getStyle().setProperty("padding-left", "0.5rem");
	 		sortMenu.add(sortBody);
	 		
	 		sortToolbarTitle.addClickHandler(e -> {
	 			sortBody.setVisible(!sortBody.isVisible());
	 		});
		}
		
		sortBody.add(widget);
	}

	public void addUtilityOption(Button button, String text) {
		
		if(utilitiesMenu.getWidgetCount() == 0) {
			HTMLPanel utilitiesToolbarMenu = new HTMLPanel("");
			utilitiesToolbarMenu.addStyleName(AON.CSS.aonFlexBetween());
			
			Label utilitiesToolbarTitle = new Label("Utilidades");
			utilitiesToolbarTitle.getElement().getStyle().setProperty("cursor", "pointer");
			utilitiesToolbarTitle.getElement().getStyle().setProperty("width", "100%");
			utilitiesToolbarTitle.getElement().getStyle().setProperty("font-size", "1rem");
			utilitiesToolbarTitle.getElement().getStyle().setProperty("font-weight", "bold");
			utilitiesToolbarMenu.add(utilitiesToolbarTitle);
			
			utilitiesMenu.add(utilitiesToolbarMenu);
			
			// Filter body
			utilitiesBody.addStyleName(AON.CSS.aonFlexColumn());
			utilitiesBody.getElement().getStyle().setProperty("padding-left", "0.5rem");
			utilitiesMenu.add(utilitiesBody);
			
			utilitiesToolbarTitle.addClickHandler(e -> {
				utilitiesBody.setVisible(!utilitiesBody.isVisible());
			});
		}
		
		HTMLPanel utilityPanel = new HTMLPanel("");
		utilityPanel.addStyleName(AON.CSS.aonUtilityOption());
		
		if(null != button)
			utilityPanel.add(button);
		
		Label textLabel = new Label(text);
		utilityPanel.add(textLabel);
		
		utilitiesBody.add(utilityPanel);
	}
	
	public TextBox getSearchTextBox() {
		return searchTextBox;
	}
	
    public void setPlaceholder(String placeholder) {
    	searchTextBox.getElement().setPropertyString("placeholder", AonStringUtils.isBlank(placeholder) ? "Escriba para filtrar" : placeholder);
    }
	
	public void addKeyUpHandler(KeyUpHandler handler) {
		searchTextBox.addKeyUpHandler(handler);
	}
	
	public AonTableButton getFilterButton() {
		return filterButton;
	}

	public void hideSearchWidget() {
		this.setVisible(false);
	}

	protected abstract void fireClearFilter();
	
}
