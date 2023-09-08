package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AonEmployeesTreeToolbar extends Composite {
	
	// ------------------------------------------ Listener

	public interface Listener {
		
		void onKeyUpSearchTextBox(KeyUpEvent event);
		
		void onCollapseAllButtonClick(ClickEvent event);
	}
	
	// ------------------------------------------ UiBinder
	
	private static AonOptionsToolbarUiBinder uiBinder = GWT.create(AonOptionsToolbarUiBinder.class);

	interface AonOptionsToolbarUiBinder extends	UiBinder<Widget, AonEmployeesTreeToolbar> {}
	
	// ------------------------------------------ UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String textBox();
		String loading();
	}
	
	@UiField
	HTMLPanel toolbar;
	
	// ------------------------------------------ Variables

	private List<Listener> listeners;
	
	private TextBox searchTextBox;
	private AonButton loadingButton;
	private AonButton collapseAllButton;
	
	// ------------------------------------------ Constructor
	
	public AonEmployeesTreeToolbar() {
		initWidget(uiBinder.createAndBindUi(this));
		createToolbar();
		this.listeners = new ArrayList<>();
	}
	
	// ------------------------------------------ Visibility
	
	public void setVisibleLoadingButton(boolean enabled) {
		loadingButton.setVisible(enabled);
	}
	
	// ------------------------------------------ Listener
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	// ------------------------------------------ Auxliar methods
	
	public TextBox getSearchTextBox() {
		return searchTextBox;
	}
	
	public AonButton getCollapseAllButton() {
		return collapseAllButton;
	}
	
	// ------------------------------------------ Toolbar
	
	private void createToolbar() {
		
		AonButton searchButton = new AonToolbarButton("Buscar", AON.CSS.aonIconSearch() );
		toolbar.add(searchButton);
		
		searchTextBox = new TextBox();
		searchTextBox.ensureDebugId("searchTextBox");
		searchTextBox.addStyleName(style.textBox());
		searchTextBox.addStyleName("aon-SearchTextBox");
		searchTextBox.getElement().setPropertyString("placeholder", "Empleados (Nombre, NIF, NAF)");
		searchTextBox.addKeyUpHandler(event -> {
			for(Listener listener : listeners)
				listener.onKeyUpSearchTextBox(event);
		});
		toolbar.add(searchTextBox);
		searchTextBox.getElement().getStyle().setWidth(100, Unit.PCT);
		
		loadingButton = new AonToolbarButton("Cargando trabajadores", AON.CSS.aonIconRenew());
		loadingButton.addStyleName(style.loading());
		toolbar.add(loadingButton);
		
		collapseAllButton = new AonToolbarButton("Mas", AON.CSS.aonIconMoreVertical() );
		collapseAllButton.addClickHandler(e -> {
			for(Listener listener : listeners)
				listener.onCollapseAllButtonClick(e);
		});
		toolbar.add(collapseAllButton);
		
		collapseAllButton.ensureDebugId("viewButton");
	}
	
}
