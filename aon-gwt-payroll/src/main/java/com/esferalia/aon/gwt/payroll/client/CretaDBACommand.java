package com.esferalia.aon.gwt.payroll.client;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MultiMap;

import com.esferalia.aon.gwt.payroll.shared.BankAccount;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CretaDBACommand implements ScheduledCommand, CretaService {

	private CretaDBADialog  dialog;
	
	public CretaDBACommand() {
		this.dialog = new CretaDBADialog(){
			
			@Override
			protected boolean onAccept() {
				return CretaDBACommand.this.onAccept();
			}
			
			@Override
			protected String getDescription(CCC ccc) {
				return CretaDBACommand.this.getDescription(ccc);
			}
		
		};
	}
	
	// ------------------------------------------------------- ScheduledCommand
	
	@Override
	public void execute() {
		dialog.center();
		dialog.show();
	}
	
	public void setData(List<CCC> cccs) {
		dialog.setData(cccs);
	}
	
	public void setHolder(String holder) {
		dialog.setHolder(holder);
	}

	public String getHolder() {
		return dialog.getHolder();
	}
	
	public void setDocument(String document) {
		dialog.setDocument(document);
	}

	public String getDocument() {
		return dialog.getDocument();
	}

	public void setDocumentType(String documentType) {
		dialog.setDocumentType(documentType);
	}

	public String getDocumentType() {
		return dialog.getDocumentType();
	}

	public void setSelectedData(List<CCC> cccs) {
		dialog.setSelectedData(cccs);
	}
	
	public void setBankAccounts(Collection<BankAccount> bankAccounts){
		dialog.setBankAccounts(bankAccounts);
	}
	
	
	// ------------------------------------------------------------------------
	
	

	protected void onSucces(String dba) {
	}
	
	protected void onFailure(Throwable caugth) {
	}
	
	protected String getDescription(CCC ccc) {
		return ccc.getCode();
	}
	// ------------------------------------------------------------------------
	
	private boolean onAccept() {
		// TODO: MultiMap ????
		Map<Parameter, Collection<String>> params = new HashMap<CretaService.Parameter, Collection<String>>();
		params.put(Parameter.AUTORIZADO, Collections.singleton(Long.toString(dialog.getAuthorized())));
		
		params.put(Parameter.TIPO_MOVIMIENTO, Collections.singleton(dialog.getMovementType()));
		params.put(Parameter.TIPO_ACCION, Collections.singleton(dialog.getActionType()));

		params.put(Parameter.IBAN, Collections.singleton(dialog.getIBAN()));
		params.put(Parameter.TITULAR, Collections.singleton(dialog.getHolder()));
		params.put(Parameter.DOCUMENTO, Collections.singleton(dialog.getDocument()));
		params.put(Parameter.TIPO_DOCUMENTO, Collections.singleton(dialog.getDocumentType()));
		
		List<String> cccs = new LinkedList<String>();
		for ( CCC ccc : dialog.getSelectedData()) 
			cccs.add(ccc.getRegime() + ccc.getCode() );
		params.put(Parameter.CCC, cccs );

		MainCreta.send(File.COMUNICACION_DATOS_BANCARIOS, 
		params, 
		new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				CretaDBACommand.this.onSucces(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				CretaDBACommand.this.onFailure(caught);
			}
		}) ;
		
		return true;
	}
	
	
	
	
}
