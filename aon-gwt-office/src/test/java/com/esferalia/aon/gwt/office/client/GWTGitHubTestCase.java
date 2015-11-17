/**
 * 
 */
package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.office.client.models.JSON;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.models.repos.JsRepo;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import junit.framework.Assert;

/**
 * @author rtrepiana
 *
 */
public class GWTGitHubTestCase extends GWTTestCase {

	/* (non-Javadoc)
	 * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "com.esferalia.aon.gwt.office.TestingOffice";
	}
	
	
	public void testCreateRepos() {
		//TODO:
	}
	
	
	public void testGetRepos() {
		
		GitHub gitHub = new GitHub();
		
		gitHub.getRepos( "amtzdelagos", new AsyncCallback<JSON<JsRepo>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				Assert.fail(caught.getMessage());
			}

			@Override
			public void onSuccess(JSON<JsRepo> result) {
				// TODO Auto-generated method stub
				JsArray<JsRepo> repos = result.getData();
				
				Assert.assertEquals(1, repos.length());
				
				Assert.assertEquals("amtzdelagos/aon-GwtOffice", repos.get(0).getFullName() );
				
			}
			
		});
		
	}
	
	public void testCreateIssues() {
	}
	
	public void testGetIssues() {
		
		GitHub gitHub = new GitHub();

		gitHub.getIssues(getUser(), "aon-GwtOffice", /* , State   */ new AsyncCallback<JSON<JsIssue>>() {
			@Override
			public void onFailure(Throwable caught) {
				Assert.fail(caught.getMessage());
			}
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				JsArray<JsIssue> issues = result.getData();
				
				Assert.assertEquals(5, issues.length());
				
				for ( int i = 0 ; i < issues.length() ; i++ ) {
					JsIssue issue = issues.get(i);
					Window.alert(issue.getTitle() +"\r\n" + issue.getBody());
				}
			}
		});
	}
	
	/* Update title & ¿attach? */
	public void testUpdateIssues() {
		
	}
	
	
	public void testCloseIssues() {
	}
	
	public void testReOpenIssues() {
	}
	
	/* Tags or Labels ??? */
	public void testCreateTags() {
	}
	
	public void testGetTags() {
	}
	
	public void testUpdateTags() {
	}
	
	/* Assign TAGs to Issues */
	public void testAssignTags() {
	}
	
	public void testDeleteTags() {
	}
	
	public void testAssignIssues() {
	}
	
	/* Search by state, author ?, asignee, sort, Label/Tag */
	public void testFilterIssues() {
	}

	public void testDeleteIssues() {
		//TODO:
	}

	public void testCreateComments() {
		//TODO:
	}

	public void testGetComments() {
		//TODO:
	}


	public void testUpdateComments() {
		//TODO:
	}
	
	
	public void testGetEvents() {
		//TODO:
	}

	public void testDeleteComments() {
		//TODO:
	}

	public void testDeleteRepos() {
		//TODO:
	}
	
	

	// ------------------------------------------------------------------------
	
	protected String getUser(){
		return "amtzdelagos";
	}
	
}
