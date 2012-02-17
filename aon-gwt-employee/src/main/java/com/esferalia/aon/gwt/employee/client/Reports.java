package com.esferalia.aon.gwt.employee.client;



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

	private static final int DEFAULT_ZOOM = 130;
	
	
	interface Binder extends UiBinder<Widget, Reports> { }
	private static final Binder binder = GWT.create(Binder.class);

	@UiField Label 	text;
	@UiField Button firstButton;
	@UiField Button nextButton;
	@UiField Button previousButton;
	@UiField Button lastButton;

	@UiField Button printButton;
	
	@UiField MenuItem printMenuItem;
	@UiField MenuItem downloadMenuItem;
	
	@UiField MenuItem reduceMenuItem;
	@UiField MenuItem enlargeMenuItem;

	@UiField HTML 	container;
	
	
	private int zoom = DEFAULT_ZOOM;

	private	IReportsModel<IReport> 		reports;
	
	public Reports() {
		initWidget(binder.createAndBindUi(this));
		
		firstButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				reports.first();
				onReportChanged();
			}
		});
		previousButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				reports.previous();
				onReportChanged();
			}
		});
		nextButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				reports.next();
				onReportChanged();
			}
		});
		lastButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				reports.last();
				onReportChanged();
			}
		});
		printButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				IReport report = reports.current();
				report.print();
			}
		});
		
		printMenuItem.setCommand(new Command() {
			
			@Override
			public void execute() {
				IReport report = reports.current();
				report.print();
			}
		});
		
		downloadMenuItem.setCommand(new Command() {
			
			@Override
			public void execute() {
				IReport report = reports.current();
				report.print();
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
	
	
	public void setReports ( IReportsModel<IReport> reports){
		this.reports = reports;
		onReportChanged();
	}
	

	private void onReportChanged(){
		
		
		setEnabled(firstButton, reports.hasPrevious());
		setEnabled(previousButton, reports.hasPrevious());
		setEnabled(nextButton, reports.hasNext());
		setEnabled(lastButton, reports.hasNext());
		
		text.setText( ( reports.currentIndex() + 1 ) + " de " + reports.size() );
		
		getAsHTML();
	}
	
	private void getAsHTML() {
		IReport report = reports.current();
		
		report.getAsHTML(zoom, new AsyncCallback<String>() {
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
	

}
