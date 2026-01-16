package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.event.AonResetEvent;
import com.esferalia.aon.gwt.common.client.widget.event.AonResetHandler;
import com.esferalia.aon.gwt.common.client.widget.event.AonSearchEvent;
import com.esferalia.aon.gwt.common.client.widget.event.AonSearchHandler;
import com.esferalia.aon.gwt.common.client.widget.event.HasAonResetHandlers;
import com.esferalia.aon.gwt.common.client.widget.event.HasAonSearchHandlers;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AonSearchFilter extends FlowPanel implements HasAonSearchHandlers, HasAonResetHandlers {

	private TextBox searchTextBox;
	private Timer searchTimer;
    private AonTableButton searchButton;
    
    private AonTableButton filterButton = new AonTableButton(AON.MSG.filter(), AON.CSS.aonIconFilterList());
	
    private PopupPanel searchMenuPopup = new PopupPanel(true);
    private FlowPanel popupContent;
    
    private FlowPanel filterMenu;
    private FlowPanel filterBody;
    
    private FlowPanel sortMenu;
    private FlowPanel sortBody;
    
    private Integer zIndex = 3;
    
    protected AonSearchFilter() {
    	this(true,true);
    }
    
    protected AonSearchFilter(boolean showSearchTextBox, boolean showFilterPanel) {
        this.setStyleName(AON.CSS.aonSearchFilter());
    
        if (showSearchTextBox) {
        	searchTextBox = new TextBox();
        	searchTextBox.setStyleName(AON.CSS.aonCustomTextBoxInputNoBorder());
        	searchTextBox.getElement().getStyle().setProperty("min-width", "14rem");

        	searchTextBox.addKeyUpHandler(e -> {
        	    String value = searchTextBox.getValue().trim();
        	    if (searchTimer != null) searchTimer.cancel();
        	    searchTimer = new Timer() {
        	        @Override
        	        public void run() {
        	            if (AonStringUtils.isBlank(value) || value.length() >= 3) {
        	                fireSearch();
        	            }
        	        }
        	    };        	
        	    searchTimer.schedule(300);	// Espera 300 ms antes de ejecutar fireSearch()
        	});        	
        	searchButton = new AonTableButton(AON.MSG.searchAction(), AON.CSS.aonIconSearch());
        	this.add(searchButton);
        	this.add(searchTextBox);
        }
        
        if (showFilterPanel) {
        	this.add(filterButton);
        	filterButton.setAccessKey('S');
            filterButton.addClickHandler(event -> {
            	int top = this.getAbsoluteTop() + this.getOffsetHeight();
                int left = this.getAbsoluteLeft();
                searchMenuPopup.setPopupPosition(left, top);
                searchMenuPopup.getElement().getStyle().setZIndex(zIndex);
                searchMenuPopup.show(); // Muestra con animación
            });
            
    	   	popupContent = new FlowPanel();
            popupContent.setStyleName(AON.CSS.aonSearchFilterPopup());
            
            // Menus
            FlowPanel menus = new FlowPanel();
     		menus.addStyleName(AON.CSS.aonFlexColumn());
            
            // Filter
     		filterMenu = new FlowPanel();
     		filterMenu.addStyleName(AON.CSS.aonFlexColumn());
     		
            // Sort
     		sortMenu = new FlowPanel();
     		sortMenu.addStyleName(AON.CSS.aonFlexColumn());
     		
    		ScrollPanel scrollPanel = new ScrollPanel();
            scrollPanel.setStyleName(AON.CSS.aonSearchFilterScroll());
            scrollPanel.getElement().getStyle().setProperty("padding-right", ".5rem");
            menus.add(filterMenu);
            menus.add(sortMenu);
    		
            scrollPanel.setWidget(menus);
            popupContent.add(scrollPanel);
            
            // Buttons
            HTMLPanel buttonPanel = new HTMLPanel("");
            buttonPanel.setStyleName(AON.CSS.aonDisplayFlexEnd());
            buttonPanel.getElement().getStyle().setProperty("margin-top", "1rem");
            
            AonCustomButton closeButton = new AonCustomButton(AON.CSS.aonIconClose(), "Cerrar");
            closeButton.addDomHandler(e -> searchMenuPopup.hide(), ClickEvent.getType()); 
            buttonPanel.add(closeButton);
            
            AonCustomButton clearButton = new AonCustomButton(AON.CSS.aonIconClear(), "Limpiar");
            clearButton.addDomHandler(e -> fireReset(), ClickEvent.getType()); 
            buttonPanel.add(clearButton);
            popupContent.add(buttonPanel);
    		
            searchMenuPopup.setWidget(popupContent);
            
            if ( this instanceof Focusable ) {
            	Scheduler.get().scheduleDeferred(() -> ((Focusable) this).setFocus(true));
            }
        }
    }
	public Optional<TextBox> getSearchTextBox() {
		return Optional.ofNullable(searchTextBox);
	}
	public Optional<FlowPanel> getFilterMenu() {
		return Optional.ofNullable(filterMenu);
	}
	public Optional<FlowPanel> getSortMenu() {
		return Optional.ofNullable(sortMenu);
	}
    
	public void addFilterWidget(Widget widget) {
		getFilterMenu().ifPresent(fm -> {
			if (fm.getWidgetCount() == 0) {
				FlowPanel filterToolbarMenu = new FlowPanel();
				filterToolbarMenu.addStyleName(AON.CSS.aonFlexBetween());
				
				Label filterToolbarTitle = new Label( AON.MSG.filter());
				filterToolbarTitle.setStyleName(AON.CSS.aonToolbarLabel());
				filterToolbarMenu.add(filterToolbarTitle);
				
				fm.add(filterToolbarMenu);
				
				filterBody = new FlowPanel();
				filterBody.addStyleName(AON.CSS.aonFlexColumn());
				filterBody.getElement().getStyle().setProperty("padding-left", "0.5rem");
				fm.add(filterBody);
				
				filterToolbarTitle.addClickHandler(e -> {
					filterBody.setVisible(!filterBody.isVisible());
				});
			}
			filterBody.add(widget);
		});
	}

	public void addSortWidget(Widget widget) {
		getSortMenu().ifPresent(sm -> {
			if (sm.getWidgetCount() == 0) {
				HTMLPanel sortToolbarMenu = new HTMLPanel("");
				sortToolbarMenu.addStyleName(AON.CSS.aonFlexBetween());
				
				Label sortToolbarTitle = new Label("Ordenar");
				sortToolbarTitle.setStyleName(AON.CSS.aonToolbarLabel());
				sortToolbarMenu.add(sortToolbarTitle);
				
				sm.add(sortToolbarMenu);
				
				// Filter body
				sortBody = new FlowPanel();
				sortBody.addStyleName(AON.CSS.aonFlexColumn());
				sortBody.getElement().getStyle().setProperty("padding-left", "0.5rem");
				sm.add(sortBody);
				
				sortToolbarTitle.addClickHandler(e -> {
					sortBody.setVisible(!sortBody.isVisible());
				});
			}
			
			sortBody.add(widget);
		});
		
	}
	
    public void setPlaceholder(String placeholder) {
    	if (AonStringUtils.isNotBlank(placeholder)) {
    		getSearchTextBox()
    			.ifPresent(tb -> tb.getElement().setPropertyString("placeholder", placeholder));
    	}
    }
	
	public void setPopupHeight(String height) {
        if (AonStringUtils.isNotBlank(height)) {
        	popupContent.setHeight(height);
        }
	}
	
	@Override
	public HandlerRegistration addAonSearchHandler(AonSearchHandler handler) {
		return super.addHandler(handler, AonSearchEvent.getType());
	}
	protected void fireSearch() {
		AonSearchEvent.fire( AonSearchFilter.this ); 
	}

	@Override
	public HandlerRegistration addAonResetHandler(AonResetHandler handler) {
		return super.addHandler(handler, AonResetEvent.getType());
	}
	protected void fireReset() {
		AonResetEvent.fire( AonSearchFilter.this ); 
	}
}
