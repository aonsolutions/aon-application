package com.esferalia.aon.gwt.office.client;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.gwt.office.client.models.JSON;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GWTExtendsOfficeTestCase extends GWTOfficeTestCase {
	
	@Override
	public AonHub getAonHub() {
		AonHub aonHub = new AonHub("api/");
		USER = "user";
		REPONAME = "reponame";
		aonHub.setRepositoryUrl("api");
		return aonHub;
	}
	
	@Test
	public void testOpenNotices() {
		
//		delayTestFinish(3000 * 500);
		
		getAonHub().getOpenIssues(USER, REPONAME, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				fail("ERROR EN EL TEST");
				finishTest();
			}
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				assertNotNull(result);
				assertNotNull(result.getData());
				for (int x = 0; x < result.getData().length() ; x++) {
					System.out.println("ID: " + result.getData().get(x).getId()
							+ "\nTitle: " + result.getData().get(x).getTitle()
							+ "\nBody: " + result.getData().get(x).getBody());
				}
			}
		});		
	}
	
	@Override @Ignore
	public void testCreateRepository() {}

	@Override @Ignore	
	public void testGetRepository() {}
	
	@Override @Ignore 
	public void testCreateIssues() {
		//delayTestFinish(300*1000);
	}
	
	@Override @Ignore
	public void testUpdateIssues() {}
	
	@Override @Ignore
	public void testCloseIssues() {}
	
	@Override @Ignore
	public void testReOpenIssuesTestCase() {}
	
	@Override @Ignore
	public void testCreateLabels() {}
	
	@Override @Ignore
	public void testUpdateLabels() {}
	
	@Override @Ignore
	public void testAssignLabels() {}
	
	@Override @Ignore
	public void testGetAssignLabels2Issues() {}
	
	@Override @Ignore
	public void testDeleteLabels() {}
	
	@Override @Ignore
	public void testAssignIssues() {}
	
	@Override @Ignore
	public void testFilterIssues() {}
	
	@Override @Ignore
	public void testCreateComments() {}
	
	@Override @Ignore
	public void testUpdateComments() {}
	
	@Override @Ignore
	public void testGetEvents() {}
	
	@Override @Ignore
	public void testDeleteComments() {}
	
	@Override @Ignore
	public void testDeleteRepository() {}	
}
