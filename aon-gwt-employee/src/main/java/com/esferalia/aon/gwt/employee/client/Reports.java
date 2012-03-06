package com.esferalia.aon.gwt.employee.client;



import java.util.Arrays;

import com.aeat.jaxb.TipoRetenidoSalida2011.Reduccion;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class Reports extends ResizeComposite {
	
	
	private static final int ZOOM_STEP = 20;
	private static final int MIN_ZOOM =  25;
	private static final int MAX_ZOOM =  500;

	private static final int DEFAULT_ZOOM = 135;
	
	interface Binder extends UiBinder<Widget, Reports> { }
	private static final Binder binder = GWT.create(Binder.class);

	@UiField Label 	text;
	@UiField Button firstButton;
	@UiField Button nextButton;
	@UiField Button previousButton;
	@UiField Button lastButton;

	@UiField Button printButton;
	@UiField Button excelButton;
	
	@UiField MenuItem printMenuItem;
	@UiField MenuItem downloadMenuItem;
	
	@UiField MenuItem reduceMenuItem;
	@UiField MenuItem enlargeMenuItem;

	@UiField HTML 	container;
	
	
	private int zoom = DEFAULT_ZOOM;

	private	IReportsModel<IDocument> 		documents;
	
	public Reports() {
		initWidget(binder.createAndBindUi(this));
		
		firstButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				documents.first();
				onReportChanged();
			}
		});
		previousButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				documents.previous();
				onReportChanged();
			}
		});
		nextButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				documents.next();
				onReportChanged();
			}
		});
		lastButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				documents.last();
				onReportChanged();
			}
		});
		printButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				IDocument document = documents.current();
				document.print();
			}
		});
		
		excelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				IDocument document = documents.current();
				document.download("xls");
			}
		});

		printMenuItem.setCommand(new Command() {
			
			@Override
			public void execute() {
				IDocument document = documents.current();
				document.print();
			}
		});
		
		downloadMenuItem.setCommand(new Command() {
			
			@Override
			public void execute() {
				IDocument document = documents.current();
				document.download();
			}
		});

		
		reduceMenuItem.setCommand(new Command() {
			
			@Override
			public void execute() {
				zoom = Math.max(MIN_ZOOM, zoom - ZOOM_STEP);
				getAsHTML();
			}
		});

		enlargeMenuItem.setCommand(new Command() {
			
			@Override
			public void execute() {
				zoom = Math.min(MAX_ZOOM, zoom + ZOOM_STEP);
				getAsHTML();
			}
		});
	}
	
	
	public void setReports ( IReportsModel<IDocument> documents){
		this.documents = documents;
		onReportChanged();
	}
	

	private void onReportChanged(){
		
		
		setEnabled(firstButton, documents.hasPrevious());
		setEnabled(previousButton, documents.hasPrevious());
		setEnabled(nextButton, documents.hasNext());
		setEnabled(lastButton, documents.hasNext());
		
		IDocument document = documents.current();

		setVisible(excelButton, support(document, "xls"));
		
		text.setText( ( documents.currentIndex() + 1 ) + " de " + documents.size() );
		
		getAsHTML();
	}
	
	private void getAsHTML() {
		IDocument document = documents.current();
		
		document.getAsHTML(zoom, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String html) {
				container.setHTML(html);
			}
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				container.setHTML(caught.getLocalizedMessage());
			}
		});

	}
	
	private void setEnabled( Button button, boolean enabled ){
		
		if ( button.isEnabled() == enabled){
			return;
		}
		
		button.setEnabled(enabled);
		String styleName = button.getStyleName();
		
		String newStyleName = enabled ? 
				styleName.replace("-disabled", "") :
				styleName + "-disabled";	
		
		button.setStyleName(newStyleName);
	}
	
	private void setVisible( Button button, boolean visible ){
		
		if ( button.isVisible() == visible){
			return;
		}
		
		button.setVisible(visible);
	}

	private static boolean support ( IDocument document, String format) {
		String supportedFormats [] = document.getSupportedFormats();
		for (int i = 0; i < supportedFormats.length; i++) {
			if ( format.equals(supportedFormats[i])){
				return true;
			}
		}
		return false;
	}
	
}
