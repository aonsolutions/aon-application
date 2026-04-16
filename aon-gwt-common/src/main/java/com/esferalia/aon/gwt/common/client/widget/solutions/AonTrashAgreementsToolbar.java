package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.event.dom.client.ClickEvent;

public class AonTrashAgreementsToolbar {

	public interface Listener {
		
		void onCollapseTrashMenuButtonClick(ClickEvent event);
		
		void onShowTrashMenuButtonClick(ClickEvent event);
		
		void onBackButtonClick(ClickEvent event);
	}
	
	private List<Listener> listeners;
	
	private boolean agreementTreeShowed = true;
	
	private AonCustomDockLayout parentDockLayout;
	
	public AonTrashAgreementsToolbar(AonCustomDockLayout aonCustomDockLayout) {
		this.parentDockLayout = aonCustomDockLayout;
		createToolbar();
		this.listeners = new ArrayList<>();
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	private void createToolbar() {
		parentDockLayout.setToolbarTitle("Papelera Convenios");
		
		AonButton showMenuButton = new AonToolbarButton("Ocultar", AON.CSS.aonIconMenuCollapse() );
		showMenuButton.addClickHandler(e -> {
			if(agreementTreeShowed) {
				showMenuButton.setTitle("Mostrar");
				showMenuButton.removeStyleName(AON.CSS.aonIconMenuCollapse());
				showMenuButton.addStyleName(AON.CSS.aonIconMenu());
				for(Listener listener : listeners)
					listener.onCollapseTrashMenuButtonClick(e);
			} else {
				showMenuButton.setTitle("Ocultar");
				showMenuButton.removeStyleName(AON.CSS.aonIconMenu());
				showMenuButton.addStyleName(AON.CSS.aonIconMenuCollapse());
				for(Listener listener : listeners)
					listener.onShowTrashMenuButtonClick(e);
			}
			
			agreementTreeShowed = !agreementTreeShowed;
		});
		parentDockLayout.addToolbarButton(showMenuButton);
		
		AonButton backButton = new AonToolbarButton(AON.MSG.backAction() + " a Convenios", AON.CSS.aonIconBack() );
		backButton.addClickHandler(e -> {
			for(Listener listener : listeners)
				listener.onBackButtonClick(e);
		});
		parentDockLayout.addToolbarButton(backButton);
		
		backButton.ensureDebugId("backButton");
	}
	
}
