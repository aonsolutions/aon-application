/**
 * 
 */
package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.office.client.models.AJSON;
import com.esferalia.aon.gwt.office.client.models.JSON;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.models.repos.JsRepo;
import com.esferalia.aon.gwt.office.client.values.RepoValue;
import com.esferalia.aon.gwt.office.client.values.issues.IssueValue;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author amtzdelagos
 *
 */

public class GWTGitHubTestCase extends GWTTestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
	 */

	private GitHub github = null;
	private JsRepo repository = null;

	@Override
	public String getModuleName() {
		return "com.esferalia.aon.gwt.office.TestingOffice";
	}	
	
	public void testCreateRepos() {

		final String description = "Prueba";

		RepoValue repo = new RepoValue();
		repo.setName(getRepoName());
		repo.setDescription(description);
		repo.setHasDownload(true);
		repo.setHasIssues(true);
		repo.setHasWiki(false);
		repo.setPrivate(false);

		getGitHub().createRepository(repo, new AsyncCallback<JsRepo>() {

			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
			}

			@Override
			public void onSuccess(JsRepo result) {
				assertNotNull(result);
				assertEquals(getRepoName(), result.getName());
				assertEquals(description, result.getDescription());
				assertEquals(getUser() + "/" + getRepoName(),
						result.getFullName());
				assertEquals(false, result.isPrivate());
				assertEquals(false, result.hasWiki());
				assertEquals(0, result.getSize());
				assertEquals(getUser(), result.getOwner().getLogin());
				System.out.println("Repositorio " + getRepoName()
						+ " creado correctamente");
			}
		});
	}

	 
	public void testGetRepos() {

		getGitHub().getRepo(getUser(), getRepoName(),
				new AsyncCallback<AJSON<JsRepo>>() {

					@Override
					public void onFailure(Throwable caught) {
						fail(caught.getMessage());
					}

					@Override
					public void onSuccess(AJSON<JsRepo> result) {
						JsRepo repo = result.getData();
						assertEquals(getRepoName(), repo.getName());
						assertEquals(getUser() + "/" + getRepoName(),
								repo.getFullName());
						assertEquals(getUser(), repo.getOwner().getLogin());
						assertEquals(getDescription(), repo.getDescription());
						assertEquals(false, repo.isPrivate());
						assertEquals(0, repo.openIssues());
						System.out.println("Repositorio " + repo.getName()
								+ "obtenido correctamente");
					}
				});
	}

	public void testCreateIssues() {

		getGitHub().getRepo(getUser(), getRepoName(),
				new AsyncCallback<AJSON<JsRepo>>() {

					@Override
					public void onFailure(Throwable caught) {
						fail(caught.getMessage() + " "
								+ caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(AJSON<JsRepo> result) {
						repository = result.getData();
						assertNotNull(repository);
						assertEquals(getRepoName(), repository.getName());
						System.out.println("Repositorio " + repository.getName()
								+ "obtenido correctamente");
					}
				});

		// ********* Issue 1 *********
		IssueValue issue1 = new IssueValue();
		issue1.setTitle(Issue1.getTitle());
		issue1.setBody(Issue1.getBody());
		issue1.setState(Issue1.getOpenState());
		System.out.println("Creando objeto ... " + Issue1.getTitle());
		// ***************************

		// ********* Issue 2 *********
		IssueValue issue2 = new IssueValue();
		issue2.setTitle(Issue2.getTitle());
		issue2.setBody(Issue2.getBody());
		issue2.setState(Issue2.getOpenState());
		System.out.println("Creando objeto ... " + Issue2.getTitle());
		// ***************************

		// ********* Issue 1 *********
		IssueValue issue3 = new IssueValue();
		issue3.setTitle(Issue3.getTitle());
		issue3.setBody(Issue3.getBody());
		issue3.setState(Issue3.getOpenState());
		System.out.println("Creando objeto ... " + Issue3.getTitle());
		// ***************************

		getGitHub().createIssue(repository, issue1,
				new AsyncCallback<JsIssue>() {

					@Override
					public void onFailure(Throwable caught) {
						fail(caught.getMessage());
					}

					@Override
					public void onSuccess(JsIssue result) {
						assertNotNull(result);
						assertEquals(Issue1.getTitle(), result.getTitle());
						assertEquals(Issue1.getBody(), result.getBody());
						assertEquals(Issue1.getOpenState(), result.getState());
						System.out.println("Issue " + Issue1.getTitle()
								+ " creado correctamente");
					}
				});

		getGitHub().createIssue(repository, issue2,
				new AsyncCallback<JsIssue>() {

					@Override
					public void onFailure(Throwable caught) {
						fail(caught.getMessage());
					}

					@Override
					public void onSuccess(JsIssue result) {
						assertNotNull(result);
						assertEquals(Issue2.getTitle(), result.getTitle());
						assertEquals(Issue2.getBody(), result.getBody());
						assertEquals(Issue2.getOpenState(), result.getState());
						System.out.println("Issue " + Issue2.getTitle()
								+ " creado correctamente");
					}
				});

		getGitHub().createIssue(repository, issue3,
				new AsyncCallback<JsIssue>() {

					@Override
					public void onFailure(Throwable caught) {
						fail(caught.getMessage());
					}

					@Override
					public void onSuccess(JsIssue result) {
						assertNotNull(result);
						assertEquals(Issue3.getTitle(), result.getTitle());
						assertEquals(Issue3.getBody(), result.getBody());
						assertEquals(Issue3.getOpenState(), result.getState());
						System.out.println("Issue " + Issue3.getTitle()
								+ " creado correctamente");
					}
				});
	}

	public void testGetIssues() {

		getGitHub().getRepo(getUser(), getRepoName(),
				new AsyncCallback<AJSON<JsRepo>>() {

					@Override
					public void onFailure(Throwable caught) {
						fail(caught.getMessage());
					}

					@Override
					public void onSuccess(AJSON<JsRepo> result) {
						repository = result.getData();
						assertNotNull(result);
						assertNotNull(repository);
						assertEquals(true, repository.hasIssues());
						assertEquals(3, repository.openIssues());
						System.out.println("Reposotorio " + repository.getName()
								+ " obtenido correctamente");
					}
				});

		getGitHub().getOpenIssues(getUser(), repository.getName(),
				new AsyncCallback<JSON<JsIssue>>() {

					@Override
					public void onFailure(Throwable caught) {
						fail(caught.getMessage());
					}

					@Override
					public void onSuccess(JSON<JsIssue> result) {
						JsArray<JsIssue> issues = result.getData();
						assertNotNull(issues);
						assertEquals(3, issues.length());

						for (int x = 0; x < issues.length(); x++) {

							switch (x) {
							case 0:
								assertEquals(Issue3.getTitle(),
										issues.get(x).getTitle());
								assertEquals(Issue3.getBody(),
										issues.get(x).getBody());
								assertEquals(Issue3.getOpenState(),
										issues.get(x).getState());
								System.out.println("Issue " + Issue3.getTitle()
										+ " obtenido correctamente");
								break;
							case 1:
								assertEquals(Issue2.getTitle(),
										issues.get(x).getTitle());
								assertEquals(Issue2.getBody(),
										issues.get(x).getBody());
								assertEquals(Issue2.getOpenState(),
										issues.get(x).getState());
								System.out.println("Issue " + Issue2.getTitle()
										+ " obtenido correctamente");
								break;
							case 2:
								assertEquals(Issue1.getTitle(),
										issues.get(x).getTitle());
								assertEquals(Issue1.getBody(),
										issues.get(x).getBody());
								assertEquals(Issue1.getOpenState(),
										issues.get(x).getState());
								System.out.println("Issue " + Issue1.getTitle()
										+ " obtenido correctamente");
								break;

							default:
								fail("Error en la iteracion de issues creados");
							}
						}
					}
				});
	}

	/* Update title & ¿attach? */
	public void testUpdateIssues() {

		getGitHub().getRepo(getUser(), getRepoName(),
				new AsyncCallback<AJSON<JsRepo>>() {

					@Override
					public void onFailure(Throwable caught) {
						fail(caught.getMessage());
					}

					@Override
					public void onSuccess(AJSON<JsRepo> result) {
						assertNotNull(result);
						repository = result.getData();
						assertNotNull(repository);
						assertEquals(getRepoName(), repository.getName());
						System.out.println("Repositorio " + repository.getName()
								+ " obtenido correctamente");
					}
				});

		getGitHub().getOpenIssues(getUser(), getRepoName(),
				new AsyncCallback<JSON<JsIssue>>() {

					@Override
					public void onFailure(Throwable caught) {
						fail(caught.getMessage());
					}

					@Override
					public void onSuccess(JSON<JsIssue> result) {
						assertNotNull(result);
						JsArray<JsIssue> issues = result.getData();
						assertNotNull(issues);

						for (int x = 0; x < issues.length(); x++)
							updateIssue(repository, result.getData().get(x));
					}
				});

	}

	public void testCloseIssues() {
		
		getGitHub().getRepo(getUser(), getRepoName(), new AsyncCallback<AJSON<JsRepo>>() {

			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
			}

			@Override
			public void onSuccess(AJSON<JsRepo> result) {
				assertNotNull(result);
				repository = result.getData();
				assertNotNull(repository);
				assertEquals(getUser(), repository.getOwner().getLogin());				
				assertEquals(3, repository.openIssues());
				System.out.println("Repositorio " + repository.getName() + " obtenido correctamente");
			}
		});
		
		getGitHub().getOpenIssues(getUser(), repository.getUrl(), new AsyncCallback<JSON<JsIssue>>() {

			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
			}

			@Override
			public void onSuccess(JSON<JsIssue> result) {
				assertNotNull(result);
				JsArray<JsIssue> issues = result.getData();
				assertNotNull(issues);
				assertTrue(issues.length() > 0);
				
				JsIssue issue = issues.get(0);
				updateStateIssue(repository, issue, Issue1.getCloseState());
			}
		});
		
		getGitHub().getOpenIssues(getUser(), repository.getUrl(), new AsyncCallback<JSON<JsIssue>>() {

			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
			}

			@Override
			public void onSuccess(JSON<JsIssue> result) {
				assertNotNull(result);
				JsArray<JsIssue> issues = result.getData();
				assertNotNull(issues);
				assertEquals(2, issues.length());
				System.out.println("Issues abiertas. Ok ..");
				
				for ( int x = 0; x < issues.length() ; x++)
					updateStateIssue(repository, issues.get(x), Issue1.getCloseState());
			}
		});
		
		getGitHub().getOpenIssues(getUser(), repository.getUrl(), new AsyncCallback<JSON<JsIssue>>() {

			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
			}

			@Override
			public void onSuccess(JSON<JsIssue> result) {
				assertNotNull(result);
				JsArray<JsIssue> issues = result.getData();
				assertNotNull(issues);
				
				assertEquals(0, issues.length());
				System.out.println("Lista issues abiertas vacia. Obteniendo lista issues cerradas ...");
			}
		});
		
		getGitHub().getClosedIssues(getUser(), repository.getUrl(), new AsyncCallback<JSON<JsIssue>>() {

			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
			}

			@Override
			public void onSuccess(JSON<JsIssue> result) {
				assertNotNull(result);
				JsArray<JsIssue> issues = result.getData();
				assertNotNull(issues);
				assertEquals(3, issues.length());			
				System.out.println("Todas las issues cerradas correctamente");
			}
		});
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
		// TODO:
	}

	public void testCreateComments() {
		// TODO:
	}

	public void testGetComments() {
		// TODO:
	}

	public void testUpdateComments() {
		// TODO:
	}

	public void testGetEvents() {
		// TODO:
	}

	public void testDeleteComments() {
		// TODO:
	}

	// public void testDeleteRepository() {
	//
	// getGitHub().getRepo(getUser(), getRepoName(),
	// new AsyncCallback<AJSON<JsRepo>>() {
	//
	// @Override
	// public void onFailure(Throwable caught) {
	// Assert.fail(caught.getMessage());
	// }
	//
	// @Override
	// public void onSuccess(AJSON<JsRepo> result) {
	//
	// JsRepo repo = result.getData();
	// assertNotNull(repo);
	// assertEquals(getRepoName(), repo.getName());
	//
	// getGitHub().deleteRepository(repo,
	// new AsyncCallback<JsRepo>() {
	//
	// @Override
	// public void onFailure(Throwable caught) {
	// fail(caught.getMessage());
	// }
	//
	// @Override
	// public void onSuccess(JsRepo result) {
	//
	// }
	// });
	// }
	// });
	// }
	
	// ------------------------ Private Methods ------------------------------

	private void updateIssue(JsRepo repo, final JsIssue issue) {
		IssueValue edited = new IssueValue();
		edited.setTitle(issue.getTitle() + Issue1.getEdited());
		edited.setBody(issue.getBody() + Issue1.getEdited());

		getGitHub().editIssue(repository, issue, edited,
				new AsyncCallback<JsIssue>() {

					@Override
					public void onFailure(Throwable caught) {
						fail(caught.getMessage());
					}

					@Override
					public void onSuccess(JsIssue result) {
						assertNotNull(result);
						assertEquals(issue.getId(), result.getId());
						assertEquals(issue.getTitle() + Issue1.getEdited(),
								result.getTitle());
						assertEquals(issue.getBody() + Issue1.getEdited(),
								result.getBody());
						assertEquals(issue.getState(), Issue1.getOpenState());
						System.out.println("Issue " + issue.getTitle() + " "
								+ issue.getNumber() + " editado correctamente");
					}
				});
	}
	
	private void updateStateIssue (JsRepo repo, final JsIssue issue, final String state) {
		
		IssueValue closed = new IssueValue();
		closed.setState(state);
		System.out.println("Cerrando Issue " + issue.getTitle() + " ...");
		
		getGitHub().editIssue(repository, issue, closed, new AsyncCallback<JsIssue>() {

			@Override
			public void onFailure(Throwable caught) {
				fail(caught.getMessage());
			}

			@Override
			public void onSuccess(JsIssue result) {
				assertNotNull(result);
				assertEquals(issue.getId(), result.getId());
				assertEquals(issue.getTitle(), issue.getTitle());
				assertEquals(issue.getBody(), result.getBody());
				assertEquals(Issue1.getCloseState(), result.getState());
				System.out.println("Issue " + result.getTitle() + " cerrado correctamente");
			}
		});
		
		
		
	
			

	}

	// ------------------------------------------------------------------------

	protected String getUser() {
		return "amtzdelagos";
	}

	protected String getRepoName() {
		return "aon-repoPrueba";
	}

	protected String getDescription() {
		return "Prueba";
	}

	protected GitHub getGitHub() {
		if (github == null) {
			github = new GitHub();
			github.setAccessToken("06a75ef8dfa037f188c2075333ed73574ffd1971");
		}
		return github;
	}

	static class Issue1 {

		static String getTitle() {
			return "Title Issue 1";
		}

		static String getBody() {
			return "Body Issue 1";
		}

		static String getEdited() {
			return " edited";
		}

		static String getOpenState() {
			return "open";
		}

		static String getCloseState() {
			return "closed";
		}
	}

	static class Issue2 {

		static String getTitle() {
			return "Title Issue 2";
		}

		static String getBody() {
			return "Body Issue 2";
		}

		static String getEdited() {
			return " edited";
		}

		static String getOpenState() {
			return "open";
		}

		static String getCloseState() {
			return "closed";
		}
	}

	static class Issue3 {

		static String getTitle() {
			return "Title Issue 3";
		}

		static String getBody() {
			return "Body Issue 3";
		}

		static String getEdited() {
			return " edited";
		}

		static String getOpenState() {
			return "open";
		}

		static String getCloseState() {
			return "closed";
		}
	}

}
