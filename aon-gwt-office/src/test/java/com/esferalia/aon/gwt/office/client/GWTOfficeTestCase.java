/**
 * 
 */
package com.esferalia.aon.gwt.office.client;

import org.junit.BeforeClass;
import org.junit.Test;

import com.esferalia.aon.gwt.office.client.models.AJSON;
import com.esferalia.aon.gwt.office.client.models.JSON;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.esferalia.aon.gwt.office.client.models.repos.JsRepo;
import com.esferalia.aon.gwt.office.client.values.IssueCommentValue;
import com.esferalia.aon.gwt.office.client.values.LabelValue;
import com.esferalia.aon.gwt.office.client.values.RepoValue;
import com.esferalia.aon.gwt.office.client.values.issues.IssueValue;
import com.esferalia.aon.gwt.office.shared.GWTTestConstans;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author amtzdelagos
 *
 */

public class GWTOfficeTestCase extends GWTTestConstans {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
	 */
	
	protected static String USER = "amtzdelagos";
	protected static String REPONAME = "aon-repoPrueba";
	protected static String DESCRIPTION = "Repositorio de prueba para metodos de TEST";
	
	@BeforeClass
	public void testCreateRepository() {
		System.out.println("testCreateRepo() .....");
		System.out
				.println("======== >>> Creando repositorio de prueba .....");

		final RepoValue repo = new RepoValue();
		repo.setName(REPONAME);
		repo.setDescription(DESCRIPTION);
		repo.setHasDownload(true);
		repo.setHasIssues(true);
		repo.setHasWiki(false);
		repo.setPrivate(false);

		getAonHub().createRepository(repo, new AsyncCallback<JsRepo>() {

			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
				finishTest();
			}

			@Override
			public void onSuccess(JsRepo result) {
				System.out.println("OK! Repositorio " + REPONAME
						+ " creado correctamente");
			}
		});
	}
	
	@Test
	public void testGetRepository() {
		System.out.println("testGetRepo() .....");

		getAonHub().getRepo(USER, REPONAME,
				new AsyncCallback<AJSON<JsRepo>>() {

					@Override
					public void onFailure(Throwable caught) {
						fail(caught.getMessage());
						finishTest();
					}

					@Override
					public void onSuccess(AJSON<JsRepo> result) {
						if ( result == null)
							finishTest();
						if ( result.getData() == null) 
							finishTest();
						
						assertNotNull(result);
						assertNotNull(result.getData());
						JsRepo repo = result.getData();						
						assertNotNull(repo);
						
						assertEquals(REPONAME, repo.getName());
						assertEquals(DESCRIPTION, repo.getDescription());
						assertEquals(USER + "/" + REPONAME,
								repo.getFullName());
						assertEquals(false, repo.isPrivate());
						assertEquals(false, repo.hasWiki());
						assertEquals(USER, repo.getOwner().getLogin());
						System.out.println(
								" OK ===> Repositorio obtenido correctamente");
					}
				});
	}
	
	@Test
	public void testCreateIssues() {
		
		System.out.println("testCreateIssues() .....");
		
		for (int i = 0; i < MAX_ISSUES_COUNT; i++) {
			final IssueValue issue = new IssueValue();
			final String title = PRUEBA_TEST + " Title issue " + (i + 1);
			final String body = PRUEBA_TEST + " Body issue " + (i + 1);

			issue.setTitle(title);
			issue.setBody(body);
			issue.setState(OPEN_STATE_ISSUE);
			issue.setType("TICKET");
			issue.setPriority("LOW");			
			
			System.out.println("Creando objecto Title issue " + (i + 1));

			getAonHub().createIssue(USER, REPONAME, issue, new AsyncCallback<JsIssue>() {

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
	
	@Test
	public void testUpdateIssues() {
		System.out.println("testUpdateIssues() .... ");
		
		getAonHub().getOpenIssues(USER, REPONAME, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
				finishTest();
			}
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				
				if (result == null)
					finishTest();
				if (result.getData() == null)
					finishTest();				
				
				JsArray<JsIssue> issues = result.getData();				
				for ( int x = 0; x < issues.length(); x++)
					updateIssue(issues.get(x));
			}
		});
	}
	
	@Test
	public void testCloseIssues() {
		System.out.println("testCloseIssues() .... ");
		
		getAonHub().getOpenIssues(USER, REPONAME, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
				finishTest();
			}
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				assertNotNull(result);
				assertNotNull(result.getData());
				
				JsArray<JsIssue> issues = result.getData();
				//assertEquals(MAX_ISSUES_COUNT, issues.length());
				
				for ( int x = 0; x < issues.length(); x++)
					updateStateIssue(issues.get(x), CLOSE_STATE_ISSUE);
			}
		});
	}
	
	@Test
	public void testReOpenIssuesTestCase() {
		System.out.println("testReopenIssues() .... ");
		getAonHub().getClosedIssues(USER, REPONAME, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
				finishTest();
			}
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				assertNotNull(result);
				assertNotNull(result.getData());
				
				JsArray<JsIssue> issues = result.getData();
				//assertEquals(MAX_ISSUES_COUNT, issues.length());
				
				for ( int x = 0; x < issues.length(); x++)
					updateStateIssue(issues.get(x), OPEN_STATE_ISSUE);
			}
		});
	}
	
	@Test
	public void testCreateLabels() {
		System.out.println("testCreateLabels() .... ");
		
		for ( int x = 0 ; x < MAX_LABELS_COUNT; x++) {
			LabelValue label = new LabelValue();
			label.setName(LABEL_NAME + x);
			label.setColor(LABEL_COLOR);		
			
			getAonHub().createLabel(USER, REPONAME, label, new AsyncCallback<JsLabel>() {
				
				@Override
				public void onFailure(Throwable caught) {
					fail(caught.getMessage());
					finishTest();
				}
				
				@Override
				public void onSuccess(JsLabel result) {
					assertTrue(result.getName().contains(LABEL_NAME));
					assertEquals(LABEL_COLOR, result.getColor());
					System.out.println(LABEL_NAME + result.getName()
							+ " creada correctamente");
				}
			});
		}
	}
	
	@Test
	public void testUpdateLabels() {
		
		getAonHub().getLabels(USER, REPONAME, new AsyncCallback<JSON<JsLabel>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
				finishTest();
			}
			
			@Override
			public void onSuccess(JSON<JsLabel> result) {
				assertNotNull(result);
				assertNotNull(result.getData());
				JsArray<JsLabel> labels = result.getData();
				//assertTrue(labels.length() > MAX_LABELS_COUNT);	
				
				for ( int x = 0; x < labels.length(); x++ ) {
					if (labels.get(x).getName().contains(LABEL_NAME))
						modifyLabel(labels.get(x));
				}
			}
		});
	}

	/* Assign TAGs to Issues */
	public void testAssignLabels() {
	}

	public void testGetAssignLabels2Issues() {

	}
	
	@Test
	public void testDeleteLabels() {
		
		getAonHub().getLabels(USER, REPONAME, new AsyncCallback<JSON<JsLabel>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
				finishTest();
			}
			
			@Override
			public void onSuccess(JSON<JsLabel> result) {
				assertNotNull(result);
				assertNotNull(result.getData());
				
				JsArray<JsLabel> labels = result.getData();
				assertTrue(labels.length() > MAX_LABELS_COUNT);
				
				for  ( int x = 0; x < labels.length(); x++) {
					if ( labels.get(x).getName().contains(LABEL_NAME))
						deleteLabel(labels.get(x));
				}
			}
		});
	}

	public void testAssignIssues() {
	}

	/* Search by state, author ?, asignee, sort, Label/Tag */
	public void testFilterIssues() {
	}

	public void testCreateComments() {
		
		getAonHub().getOpenIssues(USER, REPONAME, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
				finishTest();
			}
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				assertNotNull(result);
				assertNotNull(result.getData());
				
				JsArray<JsIssue> issues = result.getData();
				assertEquals(MAX_ISSUES_COUNT, issues.length());
				for ( int x = 0 ; x < issues.length() ; x++)
					commentIssue(issues.get(x));
			}
		});
	}

	public void testUpdateComments() {
		
		getAonHub().getOpenIssues(USER, REPONAME, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
				finishTest();
			}
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				assertNotNull(result);
				assertNotNull(result.getData());
				
				JsArray<JsIssue> issues = result.getData();
				
				for ( int x = 0; x < issues.length() ; x++) {
					final JsIssue issue = issues.get(x);
					
					getAonHub().getIssueComments(USER, REPONAME, issue, new AsyncCallback<JSON<JsIssueComment>>() {
						
						@Override
						public void onFailure(Throwable caught) {
							fail(caught.getMessage());
							finishTest();
						}
						
						@Override
						public void onSuccess(JSON<JsIssueComment> result) {
							
							assertNotNull(result);
							assertNotNull(result.getData());
							JsArray<JsIssueComment> comments = result.getData();
							for ( int x = 0; x < comments.length() ; x++)
								editCommentIssue(issue, comments.get(x));
						}
					});
				}
			}
		});
	}

	public void testGetEvents() {
		// TODO:
	}

	public void testDeleteComments() {
		
		System.out.println("testDeleteComment() .... ");
		
		getAonHub().getOpenIssues(USER, REPONAME, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
				finishTest();
			}
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {				
				
				assertNotNull(result);
				assertNotNull(result.getData());
				
				JsArray<JsIssue> issues = result.getData();
				
				for ( int x = 0; x < issues.length() ; x++)
					deleteIssueComment(issues.get(x));
			}
		});
	}
	
	@Test
	public void testDeleteRepository() {
		
		System.out.println("Borrando repositorio ..."); 
		
		getAonHub().deleteRepository(new AsyncCallback<JsRepo>() {

			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
				finishTest();
			}

			@Override
			public void onSuccess(JsRepo result) {
				System.out.println("Repositorio ELIMINADO");
			}
		});
	}

	// ------------------------ Private Methods ------------------------------

	private void updateIssue(final JsIssue issue) {
		IssueValue edited = new IssueValue();
		edited.setTitle(issue.getTitle() + EDITED);
		edited.setBody(issue.getBody() + EDITED);		
		
		getAonHub().editIssue(USER, REPONAME, issue, edited,
				new AsyncCallback<JsIssue>() {

					@Override
					public void onFailure(Throwable caught) {
						fail(caught.getMessage());
					}

					@Override
					public void onSuccess(JsIssue result) {
						assertNotNull(result);
						assertEquals(issue.getId(), result.getId());
						
						System.out.println("Title: " + result.getTitle());
						System.out.println("Body: " + result.getBody());
						
//						assertEquals(issue.getTitle() + EDITED,
//								result.getTitle());
//						assertEquals(issue.getBody() + EDITED,
//								result.getBody());
//						assertEquals(issue.getState(), OPEN_STATE_ISSUE);
						System.out.println("Issue " + issue.getTitle() + " "
								+ issue.getNumber() + " editado correctamente");
					}
				});
	}

	private void updateStateIssue(final JsIssue issue,
			final String state) {

		IssueValue prop = new IssueValue();
		prop.setState(state);
		System.out.println("Modificando el stado de la Issue "
				+ issue.getTitle() + ". Estado a " + state);

		getAonHub().editIssue(USER, REPONAME, issue, prop, new AsyncCallback<JsIssue>() {

			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
			}

			@Override
			public void onSuccess(JsIssue result) {
				assertNotNull(result);
				assertEquals(issue.getId(), result.getId());
//				assertEquals(issue.getTitle(), issue.getTitle());
//				assertEquals(issue.getBody(), result.getBody());
//				assertNotSame(issue.getState(), result.getState());
//				System.out.println("Estado de la Issue " + result.getTitle()
//						+ " actualizada correctamente");
			}
		});
	}

	private void modifyLabel(final JsLabel label) {
		LabelValue prop = new LabelValue();
		prop.setName(label.getName() + LABEL_NAME_EDITED);
		prop.setColor(LABEL_COLOR_EDITED);

		getAonHub().saveLabel(USER, REPONAME, label, prop, new AsyncCallback<JsLabel>() {

			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
			}

			@Override
			public void onSuccess(JsLabel result) {
				assertNotNull(result);
//				assertEquals(label.getName() + LABEL_NAME_EDITED,
//						result.getName());
//				assertEquals(LABEL_COLOR_EDITED, result.getColor());
				System.out.println(LABEL_NAME + result.getName()
						+ " actualizada correctamente");
			}
		});
	}
	
	private void deleteLabel(final JsLabel label) {
		
		getAonHub().deleteLabel(USER, REPONAME, label.getName(), new AsyncCallback<JsLabel>() {
			
			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
				System.err.println(label.getName() + " no borrada");
			}
			
			@Override
			public void onSuccess(JsLabel result) {
				System.out.println(" OK ..... ");
			}
		});
	}
	
	private void commentIssue(final JsIssue issue) {
		
		for ( int x = 0; x < MAX_COMMENTS_COUNT; x++ ) {
			IssueCommentValue comment = new IssueCommentValue();
			comment.setBody(COMMENT);
			
			getAonHub().createIssueComment(USER, REPONAME, issue, comment, new AsyncCallback<JsIssueComment>() {

				@Override
				public void onFailure(Throwable caught) {
					fail(caught.getMessage());
					finishTest();
				}

				@Override
				public void onSuccess(JsIssueComment result) {
					assertNotNull(result);					
					System.out.println("Comentario creado correctamente" );
				}
			});			
		}
	}
	
	private void editCommentIssue(final JsIssue issue, final JsIssueComment comment) {
		
		IssueCommentValue editComment = new IssueCommentValue();
		editComment.setBody(comment.getBody() + EDITED);
		
		getAonHub().editIssueComment(USER, REPONAME, comment.getId(), editComment, new AsyncCallback<JsIssueComment>() {

			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
				finishTest();
			}

			@Override
			public void onSuccess(JsIssueComment result) {
				System.out.println(result.getBody());						
			}
		});
	}
	
	private void deleteIssueComment(final JsIssue issue) {
		
		getAonHub().getIssueComments(USER, REPONAME, issue, new AsyncCallback<JSON<JsIssueComment>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
				finishTest();
			}
			
			@Override
			public void onSuccess(JSON<JsIssueComment> result) {				
				assertNotNull(result);
				assertNotNull(result.getData());
				JsArray<JsIssueComment> comments = result.getData();
				
				for ( int x = 0; x < comments.length(); x++) {
					JsIssueComment comment = comments.get(x);
					System.out.println(comment.getBody());
					System.out.println(comment.getId());
				}
			}
		});
		
		
	}
}
