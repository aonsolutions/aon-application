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

public class AonAgreementsTreeToolbar extends Composite {
	
	// ------------------------------------------ Listener

	public interface Listener {
		
		void onKeyUpSearchTextBox(KeyUpEvent event);
		
		void onNewButtonClick(ClickEvent event);
		
		void onDraftButtonClick(ClickEvent event);
		
		void onViewAgreementsButtonClick(ClickEvent event, Boolean allAgreements);
		
		void onCollapseAllButtonClick(ClickEvent event);
	}
	
	// ------------------------------------------ UiBinder
	
	private static AonOptionsToolbarUiBinder uiBinder = GWT.create(AonOptionsToolbarUiBinder.class);

	interface AonOptionsToolbarUiBinder extends	UiBinder<Widget, AonAgreementsTreeToolbar> {}
	
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
	private AonButton newButton;
	private AonButton draftButton;
	private AonButton viewAgreementsButton;
	
	private boolean viewAgreements = false;
	
	// ------------------------------------------ Constructor
	
	public AonAgreementsTreeToolbar() {
		initWidget(uiBinder.createAndBindUi(this));
		createToolbar();
		this.listeners = new ArrayList<>();
	}
	
	// ------------------------------------------ Visibility
	
	public void setVisibleDraftButton(boolean visible) {
		draftButton.setVisible(visible);
	}
	
	public void setVisibleNewButton(boolean visible) {
		newButton.setVisible(visible);
	}
	
	public void setEnabledDraftButton(boolean enabled) {
		draftButton.setEnabled(enabled);
	}
	
	public void setEnabledNewButton(boolean enabled) {
		newButton.setEnabled(enabled);
	}
	
	public void setEnabledViewAgreementsButton(boolean enabled) {
		viewAgreementsButton.setEnabled(enabled);
	}
	
	public void setViewAgreements(boolean viewAgreementsIn) {
		viewAgreements = viewAgreementsIn;
	}
	
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
	
	public void resetTypeView() {
		viewAgreementsButton.setTitle("Mostrar todos los convenios");
		viewAgreementsButton.removeStyleName(AON.CSS.aonIconVisibilityOff());
		viewAgreementsButton.addStyleName(AON.CSS.aonIconVisibility());
	}
	
	// ------------------------------------------ Toolbar
	
	private void createToolbar() {
		
		AonButton searchButton = new AonToolbarButton("Buscar", AON.CSS.aonIconSearch() );
		toolbar.add(searchButton);
		
		searchTextBox = new TextBox();
		searchTextBox.addStyleName(style.textBox());
		searchTextBox.getElement().setPropertyString("placeholder", "Filtrar convenios");
		searchTextBox.addKeyUpHandler(event -> {
			for(Listener listener : listeners)
				listener.onKeyUpSearchTextBox(event);
		});
		toolbar.add(searchTextBox);
		searchTextBox.getElement().getStyle().setWidth(100, Unit.PCT);
		
		loadingButton = new AonToolbarButton("Cargando convenios", AON.CSS.aonIconRenew());
		loadingButton.addStyleName(style.loading());
		toolbar.add(loadingButton);
		
		newButton = new AonToolbarButton(AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		newButton.addClickHandler(e -> {
			for(Listener listener : listeners)
				listener.onNewButtonClick(e);
		});
		toolbar.add(newButton);
		
		viewAgreementsButton = new AonToolbarButton(
				viewAgreements ? "Mostrar convenios activos" : "Mostrar todos los convenios", 
				viewAgreements ? AON.CSS.aonIconVisibilityOff()  : AON.CSS.aonIconVisibility() );
		viewAgreementsButton.addClickHandler(e -> {
			viewAgreements = !viewAgreements;
			
			if(viewAgreements) {
				viewAgreementsButton.setTitle("Mostrar convenios activos");
				viewAgreementsButton.removeStyleName(AON.CSS.aonIconVisibility());
				viewAgreementsButton.addStyleName(AON.CSS.aonIconVisibilityOff());
			} else {
				viewAgreementsButton.setTitle("Mostrar todos los convenios");
				viewAgreementsButton.removeStyleName(AON.CSS.aonIconVisibilityOff());
				viewAgreementsButton.addStyleName(AON.CSS.aonIconVisibility());
			}
			
			for(Listener listener : listeners)
				listener.onViewAgreementsButtonClick(e, viewAgreements);
		});
		toolbar.add(viewAgreementsButton);
		
		
		draftButton = new AonToolbarButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete() );
		draftButton.addClickHandler(e -> {
			for(Listener listener : listeners)
				listener.onDraftButtonClick(e);
		});
		toolbar.add(draftButton);
		
		AonButton collapseAllButton = new AonToolbarButton("Mas", AON.CSS.aonIconMoreVertical() );
		collapseAllButton.addClickHandler(e -> {
			for(Listener listener : listeners)
				listener.onCollapseAllButtonClick(e);
		});
		toolbar.add(collapseAllButton);
		
		viewAgreementsButton.ensureDebugId("mostrarConvenios");
		newButton.ensureDebugId("newButton");
		draftButton.ensureDebugId("draftButton");
		collapseAllButton.ensureDebugId("collapseAllButton");
	}
	
}
