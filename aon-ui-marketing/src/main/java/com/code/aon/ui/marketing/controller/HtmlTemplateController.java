package com.code.aon.ui.marketing.controller;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.registry.controller.RegistryAttachController;

public class HtmlTemplateController extends RegistryAttachController {

	private Converter byteArrayConverter;	
	
	public Converter getByteArrayConverter() {
		if ( this.byteArrayConverter == null ) {
			this.byteArrayConverter = new Converter() {
				
				@Override
				public String getAsString(FacesContext context, UIComponent component, Object value) {
					if ( value != null ) {
						return new String( (byte[]) value );	
					}
					return null;
				}
				
				@Override
				public Object getAsObject(FacesContext context, UIComponent component, String value) {
					if ( value != null ) {
						return value.getBytes();	
					}
					return null;
				}
			};	
		}
		return this.byteArrayConverter;
	}

	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		AonFile aonFile = new AonFile();
		aonFile.setMimeType(MimeType.MIME_HTML);
		setAonFile(aonFile);
	}

	@Override
	public void select(ActionEvent event) {
		super.select(event);
		RegistryAttachment attach = (RegistryAttachment) getTo();
		AonFile aonFile = new AonFile();
		aonFile.setMimeType(MimeType.MIME_HTML);
		aonFile.setData( attach.getData() );
		setAonFile(aonFile);		
	}
	
}
