/**
 * 
 */
package com.esferalia.aon.gwt.api.client;

import org.junit.Test;

import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class GWTApiTestCase extends GWTTestCase {

	protected static String USER = "anderibz";
	protected static String REPONAME = "githubapi";
	protected static String DESCRIPTION = "Repositorio de prueba para metodos de TEST";
	
	public String getModuleName() {                                         // <span style="color:black;">**(2)**</span>
	    return "com.esferalia.aon.gwt.api.Api";
	}
	
	@Test
	public void testGetOpenIssues() {
		Incidence i = new Incidence(AonUrlApi.GITHUB);
		i.getOpenIssues(USER, REPONAME, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				System.out.println("GET OPEN ISSUES");
				for(Integer i = 0; i < result.getData().length(); i++)
					System.out.println("	#" + i + " "+ result.getData().get(i).getTitle());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println("K.O.");
			    assertTrue(false);
			}
		});
	}
	
	@Test
	public void testGetClosedIssues() {
		Incidence i = new Incidence(AonUrlApi.GITHUB);
		i.getClosedIssues(USER, REPONAME, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				System.out.println("GET CLOSED ISSUES");
				for(Integer i = 0; i < result.getData().length(); i++)
					System.out.println("	#" + i + " "+ result.getData().get(i).getTitle());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println("K.O.");
			}
		});
	}
	
	@Test
	public void testGetAllIssues() {
		Incidence i = new Incidence(AonUrlApi.GITHUB);
		i.getAllIssues(USER, REPONAME, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				System.out.println("GET ALL ISSUES");
				for(Integer i = 0; i < result.getData().length(); i++)
					System.out.println("	#" + i + " "+ result.getData().get(i).getTitle());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println("K.O.");
			}
		});
	}
	
	
	
	
	@Test
	public void testCreateIssue() {
		String r= "{\"title\":\"aaaa\",\"body\":\"bbbb\",\"assignee\":\"anderibz\",\"labels\":[\"bug\"]}";
		Incidence i = new Incidence(AonUrlApi.GITHUB);
		i.createIssue(USER, REPONAME, r, new AsyncCallback<JsIssue>() {
			
			@Override
			public void onSuccess(JsIssue result) {
				System.out.println("oki doki");
			    System.out.println(result.getTitle());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println("K.O.");
			    assertTrue(false);
			}
		});
	}
	
	
	
}
