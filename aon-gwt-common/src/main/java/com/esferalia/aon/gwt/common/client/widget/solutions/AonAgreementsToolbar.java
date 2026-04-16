package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class AonAgreementsToolbar {

	public interface Listener {
		
		void onCollapseMenuButtonClick(ClickEvent event);

		void onShowMenuButtonClick(ClickEvent event);
		
		void onImportButtonClick(ClickEvent event);
		
		void onTrashListButtonClick(ClickEvent event);
		
		void onSettingsButtonClick(ClickEvent event);
	}

	private List<Listener> listeners;
	
	private AonButton showMenuButton;
	
	private AonButton importButton;
	private AonButton settingsButton;
	private AonButtonBadge trashListButton;
	
	private boolean agreementTreeShowed = true;
	
	private AonCustomDockLayout parentDockLayout;
	
	public AonAgreementsToolbar(AonCustomDockLayout aonCustomDockLayout) {
		this.parentDockLayout = aonCustomDockLayout;
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
		
		showMenuButton = new AonToolbarButton("Ocultar", AON.CSS.aonIconMenuCollapse() );
		showMenuButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(agreementTreeShowed) {
					showMenuButton.setTitle("Mostrar");
					showMenuButton.removeStyleName(AON.CSS.aonIconMenuCollapse());
					showMenuButton.addStyleName(AON.CSS.aonIconMenu());
					for(Listener listener : listeners)
						listener.onCollapseMenuButtonClick(event);
				} else {
					showMenuButton.setTitle("Ocultar");
					showMenuButton.removeStyleName(AON.CSS.aonIconMenu());
					showMenuButton.addStyleName(AON.CSS.aonIconMenuCollapse());
					for(Listener listener : listeners)
						listener.onShowMenuButtonClick(event);
				}
				
				agreementTreeShowed = !agreementTreeShowed;
				
			}
		});
		parentDockLayout.addToolbarButton(showMenuButton);
		
		importButton = new AonToolbarButton("Importar Convenio", AON.CSS.aonIconCloudImport() );
		importButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onImportButtonClick(event);
			}
		});
		parentDockLayout.addToolbarButton(importButton);
		importButton.setVisible(false);
		
		settingsButton = new AonToolbarButton("Utilidades", AON.CSS.aonIconSettings() );
		settingsButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onSettingsButtonClick(event);
			}
		});
		parentDockLayout.addToolbarButton(settingsButton);
		
		trashListButton = new AonButtonBadge("Papelera Convenios", AON.CSS.aonIconTrashList(), false);
		trashListButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onTrashListButtonClick(event);
			}
		});
		parentDockLayout.addToolbarButton(trashListButton);

		importButton.ensureDebugId("importButton");
		trashListButton.ensureDebugId("trashListButton");
	}
	
}
