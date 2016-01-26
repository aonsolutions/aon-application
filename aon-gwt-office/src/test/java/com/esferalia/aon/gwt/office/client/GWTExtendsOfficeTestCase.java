package com.esferalia.aon.gwt.office.client;

import org.junit.Ignore;

import com.esferalia.aon.gwt.office.client.models.JSON;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.google.gwt.core.client.JsArray;
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
	
	@Override @Ignore
	public void testCreateRepository() {}

	@Override @Ignore	
	public void testGetRepository() {}
	
	@Override 
	public void testCreateIssues() {
		super.testCreateIssues();
	}
	
	@Override 
	public void testUpdateIssues() {
		super.testUpdateIssues();
	}
	
	@Override 
	public void testCloseIssues() {
		super.testCloseIssues();
	}
	
	@Override 
	public void testReOpenIssuesTestCase() {
		super.testReOpenIssuesTestCase();
	}
	
	@Override 
	public void testCreateLabels() {
		super.testCreateLabels();
	}
	
	@Override
	public void testUpdateLabels() {
		super.testUpdateLabels();
	}
	
	@Override @Ignore
	public void testAssignLabels() {}
	
	@Override @Ignore
	public void testGetAssignLabels2Issues() {}
	
	@Override 
	public void testDeleteLabels() {
		super.testDeleteLabels();
	}
	
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
	
	public void testDeleteNotice() {
		
		System.out.println("testDeleteNotices() ....... ");
		
		getAonHub().getOpenIssues(USER, REPONAME, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				fail("Error en testDeleteNotice --> getOpenIssues(): " + caught.getMessage());
				finishTest();
			}
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				assertNotNull(result);
				assertNotNull(result.getData());
				
				JsArray<JsIssue> issues = result.getData();
				assertTrue(issues.length() > 0);
				
				
				for ( int x = 0; x < issues.length(); x++ ) {
					final JsIssue issue = issues.get(x);
					if (issue.getTitle().startsWith(PRUEBA_TEST))
						deleteNotice(issue);
				}
			}
		});
	}
	
	private void deleteNotice(JsIssue issue) {
		
		getAonHub().deleteIssue(USER, REPONAME, issue, new AsyncCallback<JsIssue>() {
			
			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
				System.err.println("Notice no borrado ...");
				finishTest();
			}
			
			@Override
			public void onSuccess(JsIssue result) {
				System.out.println(" ..... OK ....");
			}
		});
	}
	
	@Override @Ignore
	public void testDeleteRepository() {}	
}
