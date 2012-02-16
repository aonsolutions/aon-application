package com.esferalia.aon.gwt.employee.client;



import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class Reports extends ResizeComposite {
	

	interface Binder extends UiBinder<Widget, Reports> { }
	private static final Binder binder = GWT.create(Binder.class);

	@UiField Label 	text;
	@UiField Button first;
	@UiField Button next;
	@UiField Button previous;
	@UiField Button last;

	@UiField Button print;
	
	@UiField HTML 	container;
	
	
	private	IReportsModel<IReport> 		reports;
	

	public Reports() {
		initWidget(binder.createAndBindUi(this));
		
		first.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				reports.first();
				onReportChanged();
			}
		});
		previous.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				reports.previous();
				onReportChanged();
			}
		});
		next.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				reports.next();
				onReportChanged();
			}
		});
		last.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				reports.last();
				onReportChanged();
			}
		});
		print.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				IReport report = reports.current();
				report.print();
			}
		});
		
	}
	
	
	public void setReports ( IReportsModel<IReport> reports){
		this.reports = reports;
		onReportChanged();
	}
	

	private void onReportChanged(){
		
		
		setEnabled(first, reports.hasPrevious());
		setEnabled(previous, reports.hasPrevious());
		setEnabled(next, reports.hasNext());
		setEnabled(last, reports.hasNext());
		
		text.setText( ( reports.currentIndex() + 1 ) + " de " + reports.size() );
		
		IReport report = reports.current();
		
		report.getAsHTML(1.30f, new AsyncCallback<String>() {
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
