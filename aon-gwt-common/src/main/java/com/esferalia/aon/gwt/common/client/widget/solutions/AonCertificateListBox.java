package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;

public class AonCertificateListBox extends ListBox implements HasSelectionHandlers<Integer> {

	protected static final AonSuggestionServiceAsync SERVICE;
	static {
		AonSuggestionServiceAsync serviceRaw = GWT.create(AonSuggestionService.class);
		SERVICE = new AonSuggestionServiceAsyncDecorator(serviceRaw);
	}
	
	private Integer certificateId;
	
	public AonCertificateListBox(AonModuleOptions<?> opts) {
		addItem( "------", "" );
		if ( opts != null && opts.getConfiguration() != null ) {
			SERVICE.getCertificates(opts.getOccam(),new AsyncCallback<LinkedList<Certificate>>() {

				@Override
				public void onSuccess(LinkedList<Certificate> result) {
					AonCollectionUtils.stream(result)
						.forEach( cert -> addItem( cert.getDescription(), cert.getId().toString() ));
				}
				
				@Override
				public void onFailure(Throwable caught) {
					AonMessageDialog.error("No se han podido recuperar los certificados. " + caught.getMessage());
				}
			});
		}
		addChangeHandler(e -> {
			setCertificateId( AonNumberUtils.toInteger( getSelectedValue() ));
		});
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public Optional<Integer> getCertificateId() {
		return Optional.ofNullable(certificateId);
	}
	
	private void setCertificateId(Integer certificateId) {
		setCertificateId(certificateId, true);
	}

	private void setCertificateId(Integer certificateId, boolean fireEvents) {
		this.certificateId = certificateId;
		if (fireEvents) {
			SelectionEvent.fire(this, this.certificateId);
		}		
	}
	
}
