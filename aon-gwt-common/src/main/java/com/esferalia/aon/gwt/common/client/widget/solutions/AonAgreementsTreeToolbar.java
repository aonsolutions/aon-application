package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.animation.client.Animation;
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

public class AonAgreementsTreeToolbar extends Composite {

	public interface Listener {
		
		void onKeyUpSearchTextBox(KeyUpEvent event);
		
		void onNewButtonClick(ClickEvent event);
		
		void onDraftButtonClick(ClickEvent event);
		
//		void onPasteButtonClick(ClickEvent event);
		
//		void onCopyButtonClick(ClickEvent event);
		
		void onCollapseAllButtonClick(ClickEvent event);
	}
	
	private static AonOptionsToolbarUiBinder uiBinder = GWT.create(AonOptionsToolbarUiBinder.class);

	interface AonOptionsToolbarUiBinder extends	UiBinder<Widget, AonAgreementsTreeToolbar> {}
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {}
	
	@UiField
	HTMLPanel toolbar;

	private List<Listener> listeners;
	
	private AonButton searchButton;
	private TextBox searchTextBox;
	
	private AonButton newButton;
	private AonButton draftButton;
//	private AonButton copyButton;
//	private AonButton pasteButton;
	private AonButton collapseAllButton;
	
	private boolean searchTextBoxShowed = false;
	
	public AonAgreementsTreeToolbar() {
		initWidget(uiBinder.createAndBindUi(this));
		createToolbar();
		this.listeners = new ArrayList<Listener>();
	}
	
//	public void setVisiblePasteButton(boolean visible) {
//		pasteButton.setVisible(visible);
//	}
	
//	public void setVisibleCopyButton(boolean visible) {
//		copyButton.setVisible(visible);
//	}
	
	public void setVisibleDraftButton(boolean visible) {
		draftButton.setVisible(visible);
	}
	
	public void setVisibleNewButton(boolean visible) {
		newButton.setVisible(visible);
	}
	
//	public void setEnabledPasteButton(boolean enabled) {
//		pasteButton.setEnabled(enabled);
//	}
	
//	public void setEnabledCopyButton(boolean enabled) {
//		copyButton.setEnabled(enabled);
//	}
	
	public void setEnabledDraftButton(boolean enabled) {
		draftButton.setEnabled(enabled);
	}
	
	public void setEnabledNewButton(boolean enabled) {
		newButton.setEnabled(enabled);
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
		toolbar.add(searchButton);
		
		searchTextBox = new TextBox();
		searchTextBox.addKeyUpHandler(event -> {
			for(Listener listener : listeners)
				listener.onKeyUpSearchTextBox(event);
		});
		toolbar.add(searchTextBox);
		searchTextBox.getElement().getStyle().setOpacity(0);
		searchTextBox.getElement().getStyle().setWidth(100, Unit.PCT);
		
		newButton = new AonToolbarButton(AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		newButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onNewButtonClick(event);
			}
		});
		toolbar.add(newButton);
		
		draftButton = new AonToolbarButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete() );
		draftButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onDraftButtonClick(event);
			}
		});
		toolbar.add(draftButton);
		
//		copyButton = new AonToolbarButton("Copiar", AON.CSS.aonIconCopy() );
//		copyButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				for(Listener listener : listeners)
//					listener.onCopyButtonClick(event);
//			}
//		});
//		toolbar.add(copyButton);
//		
//		pasteButton = new AonToolbarButton("Pegar", AON.CSS.aonIconPaste() );
//		pasteButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				for(Listener listener : listeners)
//					listener.onPasteButtonClick(event);
//			}
//		});
//		toolbar.add(pasteButton);
		
		collapseAllButton = new AonToolbarButton("Mas", AON.CSS.aonIconMoreVertical() );
		collapseAllButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onCollapseAllButtonClick(event);
			}
		});
		toolbar.add(collapseAllButton);
		
		newButton.ensureDebugId("newButton");
		draftButton.ensureDebugId("draftButton");
//		copyButton.ensureDebugId("copyButton");
//		pasteButton.ensureDebugId("pasteButton");
		collapseAllButton.ensureDebugId("collapseAllButton");
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
