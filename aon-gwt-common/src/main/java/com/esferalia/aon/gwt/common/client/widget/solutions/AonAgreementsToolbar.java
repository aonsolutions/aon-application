package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AonAgreementsToolbar extends Composite {

	public interface Listener {
		
		void onCollapseMenuButtonClick(ClickEvent event);

		void onShowMenuButtonClick(ClickEvent event);
		
		void onImportButtonClick(ClickEvent event);
		
		void onTrashListButtonClick(ClickEvent event);
		
		void onSettingsButtonClick(ClickEvent event);
	}
	
	private static AonOptionsToolbarUiBinder uiBinder = GWT.create(AonOptionsToolbarUiBinder.class);

	interface AonOptionsToolbarUiBinder extends	UiBinder<Widget, AonAgreementsToolbar> {}
	
	
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String title();
		String cmdBtn();
		String redColor();
	}
	
	@UiField
	HTMLPanel headerSection;
	
	@UiField
	HTMLPanel toolsSection;

	private List<Listener> listeners;
	
	private AonButton showMenuButton;
	
	private AonButton importButton;
	private AonButton settingsButton;
	private AonButtonBadge trashListButton;
	
	private boolean agreementTreeShowed = true;
	
	public AonAgreementsToolbar() {
		initWidget(uiBinder.createAndBindUi(this));
		createToolbar();
		this.listeners = new ArrayList<Listener>();
	}
	
	public void setVisibleImportButton(boolean visible) {
		importButton.setVisible(visible);
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	public void setTrashAgreementWarn(Boolean hasTrashAgreements) {
		if(hasTrashAgreements) trashListButton.addBagde();
		else trashListButton.removeBadge();
	}
	
	private void createToolbar() {
		
		showMenuButton = new AonToolbarButton("Ocultar", AON.CSS.aonIconMenu() );
		showMenuButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(agreementTreeShowed) {
					showMenuButton.setTitle("Mostrar");
					for(Listener listener : listeners)
						listener.onCollapseMenuButtonClick(event);
				} else {
					showMenuButton.setTitle("Ocultar");
					for(Listener listener : listeners)
						listener.onShowMenuButtonClick(event);
				}
				
				agreementTreeShowed = !agreementTreeShowed;
				
			}
		});
		
		headerSection.add(showMenuButton);
		
		Label title = new Label("Convenios");
		title.addStyleName(style.title());
		headerSection.add(title);
		
		importButton = new AonToolbarButton("Importar Convenio", AON.CSS.aonIconImport() );
		importButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onImportButtonClick(event);
			}
		});
		toolsSection.add(importButton);
		importButton.setVisible(false);
		
		settingsButton = new AonToolbarButton("Utilidades", AON.CSS.aonIconSettings() );
		settingsButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onSettingsButtonClick(event);
			}
		});
		toolsSection.add(settingsButton);
		
		trashListButton = new AonButtonBadge("Papelera Convenios", AON.CSS.aonIconTrashList(), false);
		trashListButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onTrashListButtonClick(event);
			}
		});
		toolsSection.add(trashListButton);

		importButton.ensureDebugId("importButton");
		trashListButton.ensureDebugId("trashListButton");
	}
	
}
