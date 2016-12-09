package com.esferalia.aon.gwt.dump.client;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.paper.widget.PaperProgress;

public class ProgressInfo extends Composite {
	
	interface Binder extends UiBinder<Widget, ProgressInfo> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Button eraseInfoButton;

	@UiField
	Button eraseDownload;

	@UiField
	Button cancelDownload;

	@UiField
	FlexTable infoTab;

	@UiField
	Label progressTab;

	@UiField
	PaperProgress progressBar;

	@UiField
	ScrollPanel scrollInfo;
	
    


	private ConnectServiceAsyncDecorator connectServiceAsync;

	private Integer idTask;

	public ProgressInfo() {

		idTask = 0;

		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();

		initWidget(binder.createAndBindUi(this));
	
		this.eraseDownload.setTitle("Eliminar Descarga");
		this.cancelDownload.setTitle("Cancelar Descarga");
		this.eraseInfoButton.setTitle("Limpiar Pantalla");
		
		

		ConnectServiceAsync connectServiceRaw = GWT.create(ConnectService.class);
		connectServiceAsync = new ConnectServiceAsyncDecorator(connectServiceRaw);

	}

	public Integer getIdTask() {
		return idTask;
	}

	public void setIdTask(Integer idTask) {
		this.idTask = idTask;

	}


	@UiHandler("eraseInfoButton")
	void onClickEraseButton(ClickEvent event) {
		infoTab.removeAllRows();
	}

	@UiHandler("cancelDownload")
	void onClickCancelDownload(ClickEvent event) {

		connectServiceAsync.cancelDownload(idTask, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					progressTab.setText("Proceso descarga: cancelando descarga.");
					MessageBoxDialog.showFailDialog("Descarga cancelada.");
				} else
					Window.alert("No se ha podido cancelar");
			}
		});
	}

	@UiHandler("eraseDownload")
	void onClickEraseDownload(ClickEvent event) {
		
		connectServiceAsync.eraseDownload(idTask, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Boolean result) {
				if (result){
					MessageBoxDialog.showTrashDialog("Tarea eliminada.");
				}
			}
		});		
		
		EraseEvent.fire(this);
		
	}

	public HandlerRegistration addEraseHandler(EraseHandlder handler) {
		return addDomHandler(handler, EraseEvent.getType());
	}
	
	
}
