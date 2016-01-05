package com.esferalia.aon.gwt.office.client;

import org.junit.Ignore;

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
	
	/*@Test
	public void testCreateNotices() {
		System.out.println("testCreateIssues() .....");
		
		for (int i = 0; i < MAX_ISSUES_COUNT; i++) {
			final IssueValue issue = new IssueValue();
			final String title = "Title issue " + (i + 1);
			final String body = "Body issue " + (i + 1);

			issue.setTitle(title);
			issue.setBody(body);
			issue.setState(OPEN_STATE_ISSUE);
			issue.setType("TICKET");
			issue.setPriority("LOW");
			issue.setLabels(new String[]{"LABORAL","FISCAL"});
			
			System.out.println("Creando objecto Title issue " + (i + 1));

			getAonHub().createIssue(issue, new AsyncCallback<JsIssue>() {

				@Override
				public void onFailure(Throwable caught) {
					fail(caught.getMessage());
					finishTest();
				}

				@Override
				public void onSuccess(JsIssue result) {
					assertNotNull(result);
					assertEquals(title, result.getTitle());
					assertEquals(body, result.getBody());
					assertEquals(OPEN_STATE_ISSUE.toUpperCase(), result.getState());
					
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
	}*/
	
	@Override @Ignore
	public void testCreateIssues() {
		//delayTestFinish(300*1000);
	}
	
	@Override
	public void testUpdateIssues() {
		super.testUpdateIssues();
	}
	
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
