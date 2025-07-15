package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;

public class FiscalModelEmailUtils {
	
	public static final FiscalMSServiceAsync FISCAL_MS_SERVICE;
	
	static {
		FiscalMSServiceAsync serviceRaw = GWT.create(FiscalMSService.class);
		FISCAL_MS_SERVICE = new FiscalMSServiceAsyncDecorator(serviceRaw);
	}
	
	private FiscalModelEmailUtils() {
		
	}
	
	// Envio del email a la empresa cliente por parte del asesor (cuando se finaliza el modelo indicando Envio a Cliente)
	public static void sendEmail(IFiscalModelCallback<?, ?> modelCallback, IFiscalModel model) {
		
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add(new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		FISCAL_MS_SERVICE.sendEmail(modelCallback.getOptions().getOccam(), model, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				popup.hide();
			}

			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				modelCallback.showError("No se pudo enviar email: " + caught.getMessage());
			}
			
		});
		
	}

}


