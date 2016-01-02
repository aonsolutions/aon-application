package com.esferalia.aon.gwt.office.client;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.values.issues.IssueValue;
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
	
//	@Test
//	public void testOpenNotices() {
//
//		getAonHub().getOpenIssues(USER, REPONAME, new AsyncCallback<JSON<JsIssue>>() {
//			
//			@Override
//			public void onFailure(Throwable caught) {
//				fail("ERROR EN EL TEST");
//				finishTest();
//			}
//			
//			@Override
//			public void onSuccess(JSON<JsIssue> result) {
//				System.out.println("SIZE: " + result.getData().length());
//				
//				assertNotNull(result);
//				assertNotNull(result.getData());
//				
//				//assertNotNull(result.getData());
//				for (int x = 0; x < result.getData().length() ; x++) {
//					System.out.println("ID: " + result.getData().get(x).getId()
//							+ "\nTitle: " + result.getData().get(x).getTitle()
//							+ "\nBody: " + result.getData().get(x).getBody());
//				}
//			}
//		});		
//	}
	
	@Override @Ignore
	public void testCreateRepository() {}

	@Override @Ignore	
	public void testGetRepository() {}
	
	@Test
	public void testIssuesCreate() {
		System.out.println("testCreateIssues() .....");
		
		for (int i = 0; i < MAX_ISSUES_COUNT; i++) {
			final IssueValue issue = new IssueValue();
			final String title = "Title issue " + (i + 1);
			final String body = "Body issue " + (i + 1);

			issue.setTitle(title);
			issue.setBody(body);
			issue.setState(OPEN_STATE_ISSUE);
			
			System.out.println("Creando objecto Title issue " + (i + 1));

			getAonHub().createIssue(issue, new AsyncCallback<JsIssue>() {

				@Override
				public void onFailure(Throwable caught) {
					fail(caught.getMessage());
					finishTest();
				}

				@Override
				public void onSuccess(JsIssue result) {
					System.out.println(
							"==============================================");
					System.out.println(result.getTitle());
					System.out.println(result.getBody());
					System.out.println(result.getState());
					System.out.println(" == >> OK! Issue " + result.getTitle()
							+ " creada correctamente.... =====");
				}
			});
		}		

	}
	
	@Override
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
