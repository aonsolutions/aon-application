package com.esferalia.aon.gwt.office.shared;

import com.esferalia.aon.gwt.office.client.AonHub;
import com.google.gwt.junit.client.GWTTestCase;

public class GWTTestConstans extends GWTTestCase {

	protected final static int MAX_ISSUES_COUNT = 10;
	protected final static int MAX_LABELS_COUNT = 20;
	protected final static int MAX_COMMENTS_COUNT = 2;

	protected final static String EDITED = " --------- editado";
	protected final static String OPEN_STATE_ISSUE = "open";
	protected final static String PRUEBA_TEST = "PRUEBA PARA TEST";
	protected final static String CLOSE_STATE_ISSUE = "closed";
	protected final static String LABEL_NAME = "Etiqueta ";
	protected final static String LABEL_NAME_EDITED = " -------- editada";
	protected final static String LABEL_COLOR = "f29513";
	protected final static String COMMENT = "Comentario creado para prueba de TEST";
	protected final static String LABEL_COLOR_EDITED = "ff0000";
	
	protected AonHub aonHub = null;
	
	@Override
	public String getModuleName() {
		return "com.esferalia.aon.gwt.office.TestingOffice";
	}
	
	public AonHub getAonHub() {
		if (aonHub == null) {
			aonHub = new AonHub("https://api.github.com/");
			aonHub.setRepositoryUrl("https://api.github.com/repos/amtzdelagos/aon-repoPrueba");			
			aonHub.setAccessToken("06a75ef8dfa037f188c2075333ed73574ffd1971");
		}
		return aonHub;
	}
}
