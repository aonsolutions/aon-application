package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Model390ServiceAsyncDecorator implements Model390ServiceAsync {

	private Model390ServiceAsync fsa;

	public Model390ServiceAsyncDecorator(Model390ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void getMod390(Occam occam, Integer id, AsyncCallback<Mod390> callback) {
		AON.start();
		fsa.getMod390(occam,id,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMod390s(Occam occam, AsyncCallback<LinkedList<Mod390>> callback) {
		AON.start();
		fsa.getMod390s(occam,new AsyncCallbackWrapper<>(callback));
	}

//	@Override
//	public void create(Occam occam, Mod390 mod390, AsyncCallback<Mod390> callback) {
//		AON.start();
//		fsa.create(occam, mod390, new AsyncCallbackWrapper<>(callback));
//		
//	}

	@Override
	public void initialize(Occam occam, int year, AsyncCallback<Mod390> callback) {
		AON.start();
		fsa.initialize(occam, year, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveComments(Occam occam, Mod390 mod390, AsyncCallback<Mod390> callback) {
		AON.start();
		fsa.saveComments(occam, mod390, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void delete(Occam occam, Mod390 mod390, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod390, new AsyncCallbackWrapper<>(callback));
	}	
	
}
