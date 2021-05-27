package com.esferalia.aon.ui.sepe.controller;

import java.io.Serializable;

import javax.faces.event.ActionEvent;

import com.esferalia.aon.payroll.ContrataBatch;
import com.esferalia.aon.ui.sepe.controller.handler.IContrataHandler;


public interface IContrataController extends IContrataHandler, ISepeHandler, Serializable{

	void initialize(ContrataBatch batch);

	IContrataHandler getHandler();
	
	void onContrataDataShow(ActionEvent event);
	
	void onContrataAccept(ActionEvent event);
	
}
