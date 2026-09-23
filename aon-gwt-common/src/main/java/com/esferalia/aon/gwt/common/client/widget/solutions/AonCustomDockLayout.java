package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.OnSearchEvent;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AonCustomDockLayout extends DockLayoutPanel {
	
	private static final String CENTER_STYLE = "aon-center-panel";

	// Toolbar
	private AonToolbar toolbar;
	
	private SearchFilterComponent searchFilterComponent;
	private AonToolbarSearchBox aonToolbarSearchBox;
	
	protected AonCustomDockLayout(String title) {
		super(Unit.PX);
		if(AonStringUtils.isNotBlank(title))
			createToolbar(title, true);
	}
	
	protected AonCustomDockLayout(String title, boolean searchFilter) {
		super(Unit.PX);
		if(AonStringUtils.isNotBlank(title))
			createToolbar(title, searchFilter);
	}
	
	// Toolbar
	
	private void createToolbar(String title, boolean searchFilter) {
		toolbar = new AonToolbar(title);
		toolbar.addStyleName(AON.CSS.aonNoBorderToolbar());
			
		if(searchFilter) {
			searchFilterComponent = new SearchFilterComponent() {

				@Override
				protected void fireClearFilter() {
					onClearFilter();
				}
		
			};
		
			toolbar.showSearchPanel(searchFilterComponent);
		}
		
		addNorth(toolbar, AonToolbar.HEIGTH);
	}
	
	@Override
	public void add(Widget widget) {
	    super.add(widget);

	    // GWT envuelve el widget en un contenedor interno
	    if (widget != null && widget.getElement() != null) {
	        com.google.gwt.dom.client.Element parent = widget.getElement().getParentElement();
	        if (parent != null) {
	            parent.addClassName(CENTER_STYLE);
	        }
	    }
	}
	
	public AonToolbar getToolbar() {
		return toolbar;
	}
	
	public void setToolbarTitle(String title) {
		getToolbar().setTitle(title);
	}
	
	public void addToolbarButton(Widget widget) {
		getToolbar().add(widget);
	}
	
	public void removeToolbarButton(Widget widget) {
		getToolbar().remove(widget);
	}
	
	public void addToolbarButtonStart(Widget widget) {
		getToolbar().addStart(widget);
	}

	public int getToolbarButtonCount() {
		return getToolbar().getButtonContainer().getWidgetCount();
	}
	
	public FlowPanel getToolbarButtonPanel() {
		return getToolbar().getButtonContainer();
	}
	
	public void setSearchZIndex(Integer zIndex) {
		searchFilterComponent.setSearchZIndex(zIndex);
	}
	
	public void setPopupHeight(String height) {
		searchFilterComponent.setPopupHeight(height);
	}
	
	public TextBox getSearchTextBox() {
		return searchFilterComponent.getSearchTextBox();
	}
	
	public void setSearchPlaceholder(String placeholder) {
		searchFilterComponent.setPlaceholder(placeholder);
	}
	
	public void addKeyUpHandler(KeyUpHandler handler) {
		searchFilterComponent.addKeyUpHandler(handler);
	}
	
	public void addOnSearchHandler(OnSearchEvent.Handler handler) {
		searchFilterComponent.addOnSearchHandler(handler);
	}
	
	public void hideSearchWidget() {
		searchFilterComponent.hideSearchWidget();
	}
	
	public void hideFilterButton() {
		searchFilterComponent.getFilterButton().setVisible(false);
	}
	
	// Show only search button, hide text input
	public void showSeachButton() {
		searchFilterComponent.showSeachButton();
	}
	
	public void insertWidgetAfterSearchButton(Widget widget) {
		searchFilterComponent.insertWidgetAfterSearchButton(widget);
	}
	
	public void insertWidgetAfterSearchButton(Widget widget, boolean showSearchBox) {
		searchFilterComponent.insertWidgetAfterSearchButton(widget, showSearchBox);
	}
	
	public void addFilterWidget(Widget widget) {
		searchFilterComponent.addFilterWidget(widget);
	}
	
	public void addSortWidget(Widget widget) {
		searchFilterComponent.addSortWidget(widget);
	}
	
	public void showOrder() {
		searchFilterComponent.showOrder();
	}
	
	public void hideOrder() {
		searchFilterComponent.hiderder();
	}
	
	public void addUtilityOption(Button button, String text) {
		searchFilterComponent.addUtilityOption(button, text);
	}
	
	public void addToolbarSearchBox(Widget advancedSearch, Consumer<String> onValueChangeFunction) {
		aonToolbarSearchBox = new AonToolbarSearchBox() {

			@Override
			public void onValueChange(String value) {
				onValueChangeFunction.accept(value);
			}
		};
		if(advancedSearch != null) aonToolbarSearchBox.setAdvancedSearch(advancedSearch);
		getToolbar().showSearchPanel(aonToolbarSearchBox);
	}

	public void hideToolbarFilterMessages() {
		toolbar.getMessagePanel().getElement().getStyle().setDisplay(Display.NONE);
		toolbar.getFilterPanel().getElement().getStyle().setDisplay(Display.NONE);
	}
	
	public void hideToolbar() {
		addNorth(new Label(), 0);
//		toolbar.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	public void closeFilterPopup() {
		searchFilterComponent.closeFilterPopup();
	}
	
	protected abstract void onClearFilter();

}
