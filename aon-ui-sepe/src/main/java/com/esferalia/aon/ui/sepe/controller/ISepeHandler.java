package com.esferalia.aon.ui.sepe.controller;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.IAttachment;
import com.esferalia.aon.ui.sepe.utils.ISepeCommunicator;

public interface ISepeHandler {
	
	boolean isNevv();

	Boolean isBatchView();

	boolean isNewBatch();
	
	ISepeCommunicator getCommunicator();

	List<SelectItem> getPendingBatchList();

	String getCommunicationLogContent();
	
	IAttachment getGeneratedFile();
	IAttachment getCommunicationIdFile();
	IAttachment getResponseFile();
	
	boolean isShowLoginWindow();
	boolean isShowBatchWindow();
	boolean isShowCommunicationWindow();
	boolean isCommunicationIdReceived();
	boolean isCommunicationResponseReceived();
	boolean isCommunicationAccepted();
	boolean isCommunicationFinished();
	
	void onSendSepeFile(ActionEvent event);
	void onSepeDataQuery(ActionEvent event);
	void onResetBatch(ActionEvent event);
	void onBatchAccept(ActionEvent event);
	void onDownloadSepeXml(ActionEvent event);


	
	
}
