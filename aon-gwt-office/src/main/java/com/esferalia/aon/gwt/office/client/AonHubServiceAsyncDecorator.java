package com.esferalia.aon.gwt.office.client;

/**
 * 
 * @author amtzdelagos
 *
 */

public class AonHubServiceAsyncDecorator implements AonHubServiceAsync {
	
	private AonHubServiceAsync aonHubServiceAsync;
	
	public AonHubServiceAsyncDecorator(AonHubServiceAsync aonHubServiceAsync) {
		this.aonHubServiceAsync = aonHubServiceAsync;
	}

}
