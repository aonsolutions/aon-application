package com.code.aon.ui.webmail.controller;

import java.util.List;

import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import com.code.aon.ui.form.IController;

public interface ISignatureController extends IController {
	
	Converter getConverter();
	
	List<SelectItem> getSignatures();

}
