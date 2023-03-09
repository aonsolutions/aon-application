package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AonTrashAgreementsTreeToolbar extends Composite {

	public interface Listener {
		
		void onKeyUpSearchTrashTextBox(KeyUpEvent event);
		
		void onDelete4EverButtonClick(ClickEvent event);
		
		void onRestoreButtonClick(ClickEvent event);
		
	}
	
	private static AonOptionsToolbarUiBinder uiBinder = GWT.create(AonOptionsToolbarUiBinder.class);

	interface AonOptionsToolbarUiBinder extends	UiBinder<Widget, AonTrashAgreementsTreeToolbar> {}
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String textBox();
	}
	
	@UiField
	HTMLPanel toolbar;

	private List<Listener> listeners;
	
	private AonButton searchButton;
	private TextBox searchTextBox;
	
	private AonButton delete4EverButton;
	private AonButton restoreButton;
	
	public AonTrashAgreementsTreeToolbar() {
		initWidget(uiBinder.createAndBindUi(this));
		createToolbar();
		this.listeners = new ArrayList<Listener>();
	}
	
	public TextBox getSearchTextBox() {
		return searchTextBox;
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	private void createToolbar() {
		
		searchButton = new AonToolbarButton("Buscar", AON.CSS.aonIconSearch() );
		toolbar.add(searchButton);
		
		searchTextBox = new TextBox();
		searchTextBox.addStyleName(style.textBox());
		searchTextBox.addStyleName("aon-SearchTextBox");
		searchTextBox.getElement().setPropertyString("placeholder", "Filtrar convenios");
		searchTextBox.addKeyUpHandler(event -> {
			for(Listener listener : listeners)
				listener.onKeyUpSearchTrashTextBox(event);
		});
		toolbar.add(searchTextBox);
		searchTextBox.getElement().getStyle().setWidth(100, Unit.PCT);
		
		delete4EverButton = new AonToolbarButton("Eliminar definitivamente", AON.CSS.aonIconDeleteForever() );
		delete4EverButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onDelete4EverButtonClick(event);
			}
		});
		toolbar.add(delete4EverButton);
		
		restoreButton = new AonToolbarButton(AON.MSG.restoreAction(), AON.CSS.aonIconRestoreDeleted() );
		restoreButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onRestoreButtonClick(event);
			}
		});
		toolbar.add(restoreButton);
		
		delete4EverButton.ensureDebugId("delete4EverButton");
		restoreButton.ensureDebugId("restoreButton");
	}

	public void setVisibleDraft4EverButton(boolean visible) {
		this.delete4EverButton.setVisible(visible);
	}

	public void setVisibleRestoreButton(boolean visible) {
		this.restoreButton.setVisible(visible);
	}
	
}
