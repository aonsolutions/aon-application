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

public class AonTrashAgreementsToolbar extends Composite {

	public interface Listener {
		
		void onCollapseTrashMenuButtonClick(ClickEvent event);
		
		void onShowTrashMenuButtonClick(ClickEvent event);
		
		void onBackButtonClick(ClickEvent event);
	}
	
	private static AonOptionsToolbarUiBinder uiBinder = GWT.create(AonOptionsToolbarUiBinder.class);

	interface AonOptionsToolbarUiBinder extends	UiBinder<Widget, AonTrashAgreementsToolbar> {}
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String title();
	}
	
	@UiField
	HTMLPanel headerSection;
	
	@UiField
	HTMLPanel toolsSection;

	private List<Listener> listeners;
	
	private AonButton showMenuButton;
	
	private AonButton backButton;
	
	private boolean agreementTreeShowed = true;
	
	public AonTrashAgreementsToolbar() {
		initWidget(uiBinder.createAndBindUi(this));
		createToolbar();
		this.listeners = new ArrayList<Listener>();
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	private void createToolbar() {
		
		showMenuButton = new AonToolbarButton("Ocultar", AON.CSS.aonIconMenu() );
		showMenuButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(agreementTreeShowed) {
					showMenuButton.setTitle("Mostrar");
					for(Listener listener : listeners)
						listener.onCollapseTrashMenuButtonClick(event);
				} else {
					showMenuButton.setTitle("Ocultar");
					for(Listener listener : listeners)
						listener.onShowTrashMenuButtonClick(event);
				}
				
				agreementTreeShowed = !agreementTreeShowed;
				
			}
		});
		
		headerSection.add(showMenuButton);
		
		Label title = new Label("Papelera Convenios");
		title.addStyleName(style.title());
		headerSection.add(title);
		
		backButton = new AonToolbarButton(AON.MSG.backAction() + " a Convenios", AON.CSS.aonIconBack() );
		backButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onBackButtonClick(event);
			}
		});
		toolsSection.add(backButton);
		
		backButton.ensureDebugId("backButton");
	}
	
}
