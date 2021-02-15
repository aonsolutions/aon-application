package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AonTrashAgreementsToolbar extends Composite {

	public interface Listener {
		
		void onCollapseTrashMenuButtonClick(ClickEvent event);
		
		void onShowTrashMenuButtonClick(ClickEvent event);
		
		void onKeyUpSearchTrashTextBox(KeyUpEvent event);
		
		void onBackButtonClick(ClickEvent event);
		
		void onDelete4EverButtonClick(ClickEvent event);
		
		void onRestoreButtonClick(ClickEvent event);
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
	
	private AonButton searchButton;
	private TextBox searchTextBox;
	
	private AonButton backButton;
	private AonButton delete4EverButton;
	private AonButton restoreButton;
	
	private boolean agreementTreeShowed = true;
	private boolean searchTextBoxShowed = false;
	
	public AonTrashAgreementsToolbar() {
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
		
		searchTextBox = new TextBox();
		searchTextBox.addKeyUpHandler(event -> {
			for(Listener listener : listeners)
				listener.onKeyUpSearchTrashTextBox(event);
		});
		toolsSection.add(searchTextBox);
		searchTextBox.getElement().getStyle().setOpacity(0);
		
		backButton = new AonToolbarButton(AON.MSG.backAction() + " a Convenios", AON.CSS.aonIconBack() );
		backButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onBackButtonClick(event);
			}
		});
		toolsSection.add(backButton);
		
		searchButton = new AonToolbarButton("Buscar", AON.CSS.aonIconSearch() );
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(searchTextBoxShowed) {
					hideSearchTextBox();
				} else {
					showSearchTextBox();
				}
				
				searchTextBoxShowed = !searchTextBoxShowed;
			}
		});
		toolsSection.add(searchButton);
		
		delete4EverButton = new AonToolbarButton("Eliminar definitivamente", AON.CSS.aonIconDeleteForever() );
		delete4EverButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onDelete4EverButtonClick(event);
			}
		});
		toolsSection.add(delete4EverButton);
		
		restoreButton = new AonToolbarButton(AON.MSG.restoreAction(), AON.CSS.aonIconRestoreDeleted() );
		restoreButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onRestoreButtonClick(event);
			}
		});
		toolsSection.add(restoreButton);
		
		backButton.ensureDebugId("backButton");
		delete4EverButton.ensureDebugId("delete4EverButton");
		restoreButton.ensureDebugId("restoreButton");
	}
	
	private void showSearchTextBox(){
		new Animation() {

	        @Override
	        protected void onUpdate( double progress ) {
	        	searchTextBox.getElement().getStyle().setOpacity( progress );
	        }

	        @Override
	        protected void onComplete() {
	        	searchTextBox.getElement().getStyle().setOpacity( 1.0 );
	        }
	    }.run( 500 );
	}
	
	private void hideSearchTextBox(){
		new Animation() {

	        @Override
	        protected void onUpdate( double progress ) {
	        	searchTextBox.getElement().getStyle().setOpacity( 1.0 - progress );
	        }

	        @Override
	        protected void onComplete() {
	        	searchTextBox.getElement().getStyle().setOpacity( 0 );
	        }
	    }.run( 500 );
	}
	
}
