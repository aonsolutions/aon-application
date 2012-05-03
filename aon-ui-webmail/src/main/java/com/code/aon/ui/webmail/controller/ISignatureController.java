package com.code.aon.ui.webmail.controller;

import java.util.List;

import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import com.code.aon.ui.form.IController;
import com.code.aon.webmail.ISignature;

public interface ISignatureController extends IController {
	
	Converter getConverter();
	
	List<SelectItem> getSignatures();
	
	boolean isRemovable( ISignature signature );

}
