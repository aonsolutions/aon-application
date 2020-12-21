package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AonOptionsToolbar extends Composite {

	public interface Listener {
		
		void onNewButtonClick(ClickEvent event);
		
		void onPasteButtonClick(ClickEvent event);
		
		void onCopyButtonClick(ClickEvent event);
		
		void onDraftButtonClick(ClickEvent event);
				
		void onCollapseAllButtonClick(ClickEvent event);
		
		void onKeyUpSearchTextBox(KeyUpEvent event);

		void onCollapseMenuButtonClick(ClickEvent event);

		void onShowMenuButtonClick(ClickEvent event);

		void onImportButtonClick(ClickEvent event);
	}
	
	private static AonOptionsToolbarUiBinder uiBinder = GWT
			.create(AonOptionsToolbarUiBinder.class);

	interface AonOptionsToolbarUiBinder extends
			UiBinder<Widget, AonOptionsToolbar> {
	}
	
	@UiField
	HTMLPanel buttonBar;

	private List<Listener> listeners;
	
	private AonToolbarSmall toolbar;
	private AonButton viewButton;
	private AonButton pasteButton;
	private AonButton copyButton;
	private AonButton draftButton;
	private AonButton newButton;
	private AonButton importButton;
	private AonButton collapseAllButton;
	private AonButton collapseMenuButton;
	private AonButton showMenuButton;
	private TextBox searchTextBox;
	
	public AonOptionsToolbar() {
		initWidget(uiBinder.createAndBindUi(this));
		
		toolbar = getToolbarPanel();
		buttonBar.add(toolbar);
		
		this.listeners = new ArrayList<Listener>();
	}

	public void setVisibleSearchTextBox(boolean visible) {
		searchTextBox.setVisible(visible);
	}
	
	public void setVisiblePasteButton(boolean visible) {
		pasteButton.setVisible(visible);
	}
	
	public void setVisibleCopyButton(boolean visible) {
		copyButton.setVisible(visible);
	}
	
	public void setVisibleDraftButton(boolean visible) {
		draftButton.setVisible(visible);
	}
	
	public void setVisibleNewButton(boolean visible) {
		newButton.setVisible(visible);
	}
	
	public void setVisibleViewButton(boolean visible) {
		viewButton.setVisible(visible);
	}
	
	public void setVisibleCollapseButton(boolean visible) {
		collapseAllButton.setVisible(visible);
	}

	public void setEnabledPasteButton(boolean enabled) {
		pasteButton.setEnabled(enabled);
	}
	
	public void setEnabledCopyButton(boolean enabled) {
		copyButton.setEnabled(enabled);
	}
	
	public void setEnabledDraftButton(boolean enabled) {
		draftButton.setEnabled(enabled);
	}
	
	public void setEnabledNewButton(boolean enabled) {
		newButton.setEnabled(enabled);
	}
	
	public void setEnabledViewButton(boolean enabled) {
		viewButton.setEnabled(enabled);		
	}
	
	public void setEnabledCollapseButton(boolean enabled) {
		collapseAllButton.setEnabled(enabled);
	}
	
	public Button getViewButton() {
		return this.viewButton;
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
	
	private AonToolbarSmall getToolbarPanel() {
		
		AonToolbarSmall toolbar = new AonToolbarSmall();
		
		showMenuButton = new AonToolbarSmallButton("Mostrar", AON.CSS.aonIconMenu() );
		showMenuButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showMenuButton.setVisible(false);
				collapseMenuButton.setVisible(true);
				for(Listener listener : listeners)
					listener.onShowMenuButtonClick(event);
			}
		});
		toolbar.addLeftWidget(showMenuButton);
		showMenuButton.setVisible(false);
		
		collapseMenuButton = new AonToolbarSmallButton("Ocultar", AON.CSS.aonIconMenuCollapse() );
		collapseMenuButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				collapseMenuButton.setVisible(false);
				showMenuButton.setVisible(true);
				
				for(Listener listener : listeners)
					listener.onCollapseMenuButtonClick(event);
			}
		});
		toolbar.addLeftWidget(collapseMenuButton);
		
		searchTextBox = new TextBox();
		searchTextBox.getElement().getStyle().setWidth(80, Unit.PCT);
		searchTextBox.addKeyUpHandler(event -> {
			for(Listener listener : listeners)
				listener.onKeyUpSearchTextBox(event);
		});
		toolbar.setCenterWidget(searchTextBox);
		
		newButton = new AonToolbarSmallButton(AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		newButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onNewButtonClick(event);
			}
		});
		toolbar.add(newButton);
		
		importButton = new AonToolbarSmallButton("Importar Convenio", AON.CSS.aonIconCloudImport() );
		importButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onImportButtonClick(event);
			}
		});
		toolbar.add(importButton);
		
		draftButton = new AonToolbarSmallButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete() );
		draftButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onDraftButtonClick(event);
			}
		});
		toolbar.add(draftButton);
		
		viewButton = new AonToolbarSmallButton( "Ver", AON.CSS.aonIconShowPass() );
		viewButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
			}
		});
		toolbar.add(viewButton);
		viewButton.setVisible(false);
		
		copyButton = new AonToolbarSmallButton("Copiar", AON.CSS.aonIconCopy() );
		copyButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onCopyButtonClick(event);
			}
		});
		toolbar.add(copyButton);
		
		pasteButton = new AonToolbarSmallButton("Pegar", AON.CSS.aonIconPaste() );
		pasteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onPasteButtonClick(event);
			}
		});
		toolbar.add(pasteButton);
		
		collapseAllButton = new AonToolbarSmallButton("Mas", AON.CSS.aonIconDown() );
		collapseAllButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onCollapseAllButtonClick(event);
			}
		});
		toolbar.add(collapseAllButton);
		collapseAllButton.setVisible(false);
		
		newButton.ensureDebugId("newButton");
		copyButton.ensureDebugId("copyButton");
		draftButton.ensureDebugId("draftButton");
		pasteButton.ensureDebugId("pasteButton");
		viewButton.ensureDebugId("viewButton");
		collapseAllButton.ensureDebugId("collapseAllButton");
		
		return toolbar;
	}
	
}
