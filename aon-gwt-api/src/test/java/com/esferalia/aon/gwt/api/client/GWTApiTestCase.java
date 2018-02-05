/**
 * 
 */
package com.esferalia.aon.gwt.api.client;

import org.junit.BeforeClass;
import org.junit.Test;

import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.IssueFilter;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.esferalia.aon.gwt.api.client.incidence.JsRepository;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GWTApiTestCase extends GWTTestCase {

	protected static String USER_NAME = "aibanez91";
	protected static String ORG_NAME = "aonPrueba";
	protected static String REPO_NAME = "repoPrueba";
	protected static String ACCESS_TOKEN = "d8aa641723e106b5d7c2d79d3cad963e0eb6e92d";
	protected static String DESCRIPTION = "Repositorio de prueba para metodos de TEST";
	protected static Integer DOMAIN_ID = 1;
	
	public String getModuleName() {                               
	    return "com.esferalia.aon.gwt.api.Api";
	}
	
	@BeforeClass
	public void testCreateRepository() {
		System.out.println("======== >>> Creando repositorio de prueba .....");
		
		String r= "{\"name\":\""+ REPO_NAME + "\",\"description\":\""+ DESCRIPTION +"\""
				+ ",\"private\":false,\"has_issues\":true,\"has_wiki\":false,\"has_downloads\":true}";

		Incidence i = new Incidence(AonUrlApi.GITHUB, ACCESS_TOKEN, USER_NAME, ORG_NAME, REPO_NAME, DOMAIN_ID);		
		i.createOrgRepository(r, new AsyncCallback<JsRepository>() {
			
			@Override
			public void onSuccess(JsRepository result) {
				result.getName();
				System.out.println("El repositorio "+ result.getName()+ " se ha creado correctament.");
				
				String r= "{\"title\":\"aaaa\",\"body\":\"aaaa\",\"assignee\":\"aibanez91\",\"labels\":[\"bug\"]}";
				testCreateIssue(r);

				String r1= "{\"title\":\"bbbb\",\"body\":\"bbbb\",\"assignee\":\"aibanez91\",\"labels\":[\"bug\"]}";
				testCreateIssue(r1);

				String r2= "{\"title\":\"cccc\",\"body\":\"cccc\",\"assignee\":\"aibanez91\",\"labels\":[\"bug\"]}";
				testCreateIssue(r2);

				String r3= "{\"title\":\"dddd\",\"body\":\"dddd\",\"assignee\":\"aibanez91\",\"labels\":[\"bug\"]}";
				testCreateIssue(r3);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println("ERROR FATAL");
				System.out.println(caught.getMessage());
			    assertTrue(false);
			}
		});
	}
	
	@Test
	public void testGetOpenIssues() {
		IssueFilter f = new IssueFilter().setState("open");
		Incidence i = new Incidence(AonUrlApi.GITHUB, ACCESS_TOKEN, USER_NAME, ORG_NAME, REPO_NAME, DOMAIN_ID);		
		i.getOrgIssues(f, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				System.out.println("GET OPEN ISSUES");
				for(Integer i = 0; i < result.getData().length(); i++)
					System.out.println("	#" + i + " "+ result.getData().get(i).getTitle());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println("ERROR FATAL");
				System.out.println(caught.getMessage());
			    assertTrue(false);
			}
		});
	}
	
	@Test
	public void testGetClosedIssues() {
		IssueFilter f = new IssueFilter().setState("closed");
		Incidence i = new Incidence(AonUrlApi.GITHUB, ACCESS_TOKEN, USER_NAME, ORG_NAME, REPO_NAME, DOMAIN_ID);		
		i.getOrgIssues(f, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				System.out.println("GET CLOSED ISSUES");
				for(Integer i = 0; i < result.getData().length(); i++)
					System.out.println("	#" + i + " "+ result.getData().get(i).getTitle());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println("ERROR FATAL");
				System.out.println(caught.getMessage());
			    assertTrue(false);
			}
		});
	}
	
	@Test
	public void testGetAllIssues() {
		IssueFilter f = new IssueFilter().setState("all");
		Incidence i = new Incidence(AonUrlApi.GITHUB, ACCESS_TOKEN, USER_NAME, ORG_NAME, REPO_NAME, DOMAIN_ID);		
		i.getOrgIssues(f, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				System.out.println("GET ALL ISSUES");
				for(Integer i = 0; i < result.getData().length(); i++)
					System.out.println("	#" + i + " "+ result.getData().get(i).getTitle());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println("ERROR FATAL");
				System.out.println(caught.getMessage());
			    assertTrue(false);
			}
		});
	}
	
	
	

	public void testCreateIssue(String requestData) {
		System.out.println("======== >>> Creando issue de prueba .....");

		Incidence i = new Incidence(AonUrlApi.GITHUB, ACCESS_TOKEN, USER_NAME, ORG_NAME, REPO_NAME, DOMAIN_ID);		
		i.createOrgIssue(requestData, new AsyncCallback<JsIssue>() {
			
			@Override
			public void onSuccess(JsIssue result) {
				System.out.println("La issue "+ result.getTitle()+ " se ha creado correctament.");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println("ERROR FATAL");
				System.out.println(caught.getMessage());
				assertTrue(false);
			}
		});
	}
	
	

	
}
