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
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AonAgreementsToolbar extends Composite {

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
	
	private static AonOptionsToolbarUiBinder uiBinder = GWT.create(AonOptionsToolbarUiBinder.class);

	interface AonOptionsToolbarUiBinder extends	UiBinder<Widget, AonAgreementsToolbar> {}
	
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
	
	private AonButton newButton;
	private AonButton importButton;
	private AonButton draftButton;
	private AonButton copyButton;
	private AonButton pasteButton;
	private AonButton viewButton;
	private AonButton collapseAllButton;
	
	private boolean agreementTreeShowed = true;
	private boolean searchTextBoxShowed = false;
	
	public AonAgreementsToolbar() {
		initWidget(uiBinder.createAndBindUi(this));
		createToolbar();
		this.listeners = new ArrayList<Listener>();
	}

//	public void setVisibleSearchTextBox(boolean visible) {
//		searchTextBox.getElement().getStyle().setOpacity(1);
//	}
	
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
		
		searchTextBox = new TextBox();
		searchTextBox.addKeyUpHandler(event -> {
			for(Listener listener : listeners)
				listener.onKeyUpSearchTextBox(event);
		});
		toolsSection.add(searchTextBox);
		searchTextBox.getElement().getStyle().setOpacity(0);
		
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
		
		newButton = new AonToolbarButton(AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		newButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onNewButtonClick(event);
			}
		});
		toolsSection.add(newButton);
		
		importButton = new AonToolbarButton("Importar Convenio", AON.CSS.aonIconCloudImport() );
		importButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onImportButtonClick(event);
			}
		});
		toolsSection.add(importButton);
		
		draftButton = new AonToolbarButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete() );
		draftButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onDraftButtonClick(event);
			}
		});
		toolsSection.add(draftButton);
		
		viewButton = new AonToolbarButton( "Ver", AON.CSS.aonIconShowPass() );
		viewButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
			}
		});
		toolsSection.add(viewButton);
		viewButton.setVisible(false);
		
		copyButton = new AonToolbarButton("Copiar", AON.CSS.aonIconCopy() );
		copyButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onCopyButtonClick(event);
			}
		});
		toolsSection.add(copyButton);
		
		pasteButton = new AonToolbarButton("Pegar", AON.CSS.aonIconPaste() );
		pasteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onPasteButtonClick(event);
			}
		});
		toolsSection.add(pasteButton);
		
		collapseAllButton = new AonToolbarButton("Mas", AON.CSS.aonIconDown() );
		collapseAllButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(Listener listener : listeners)
					listener.onCollapseAllButtonClick(event);
			}
		});
		toolsSection.add(collapseAllButton);
		collapseAllButton.setVisible(false);
		
		newButton.ensureDebugId("newButton");
		copyButton.ensureDebugId("copyButton");
		draftButton.ensureDebugId("draftButton");
		pasteButton.ensureDebugId("pasteButton");
		viewButton.ensureDebugId("viewButton");
		collapseAllButton.ensureDebugId("collapseAllButton");
	}
	
	private void showSearchTextBox(){
		final Element e = searchTextBox.getElement();

	    new Animation() {

	        @Override
	        protected void onUpdate( double progress ) {
	            e.getStyle().setOpacity( progress );
	        }

	        @Override
	        protected void onComplete() {
	        	 e.getStyle().setOpacity( 1.0 );
	        }
	    }.run( 500 );
	}
	
	private void hideSearchTextBox(){
		final Element e = searchTextBox.getElement();

	    new Animation() {

	        @Override
	        protected void onUpdate( double progress ) {
	            e.getStyle().setOpacity( 1.0 - progress );
	        }

	        @Override
	        protected void onComplete() {
	        	 e.getStyle().setOpacity( 0 );
	        }
	    }.run( 500 );
	}
	
}
