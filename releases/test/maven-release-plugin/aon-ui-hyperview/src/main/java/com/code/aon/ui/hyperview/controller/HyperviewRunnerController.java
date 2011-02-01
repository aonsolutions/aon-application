/*
 * Created on 28-mar-2006
 *
 */
package com.code.aon.ui.hyperview.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.code.aon.hyperview.model.HyperView;
import com.code.aon.hyperview.model.HyperViewParameter;
import com.code.aon.hyperview.model.HyperViewParser;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.hyperview.servlet.HyperViewIconServlet;
import com.code.aon.ui.util.AonUtil;

public class HyperviewRunnerController {

	static final String PROPERTIES_FILE_PARAM = "propertiesFile";

	static final String RUNNABLE_TREE = "#{runnableTree}";

	static final String DEFAULT_MODEL_FOLDER = "/hyperview";

	static final String FILENAME_PROPERTY = "aon.hyperview.file";

	static final String FILENAME_ATTR = "fileName";

	static final String RESOURCE_DIR_ATTR = "aon.hyperview.resource.dir";

	private HyperView xmlHyperView;

	public HyperviewRunnerController() {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			HttpServletRequest req = (HttpServletRequest) ctx
					.getExternalContext().getRequest();
			String propertiesFileParam = req
					.getParameter(PROPERTIES_FILE_PARAM);
			String fileName = null;
			Properties props = null;
			if (propertiesFileParam != null) {
				File propertiesFile = new File(propertiesFileParam);
				FileInputStream fis = new FileInputStream(propertiesFile);
				props = new Properties();
				props.load(fis);
				fileName = props.getProperty(FILENAME_PROPERTY);
				if (fileName != null) {
					String resourceDir = props.getProperty(RESOURCE_DIR_ATTR);
					HttpSession session = req.getSession();
					if (resourceDir == null) {
						session
								.removeAttribute(HyperViewIconServlet.RESOURCE_DIR_ATTR);
					} else {
						session.setAttribute(
								HyperViewIconServlet.RESOURCE_DIR_ATTR,
								resourceDir);
					}
				}
			} else {
				fileName = req.getParameter(FILENAME_ATTR);
			}
			if (fileName != null) {
				InputStream input = null;
				File file = new File(fileName);
				if (file.exists() && file.canRead()) {
					input = new FileInputStream(file);
				} else {
					String path = DEFAULT_MODEL_FOLDER + "/" + fileName;
					input = ctx.getExternalContext().getResourceAsStream(path);
				}
				ValueBinding vb = ctx.getApplication().createValueBinding(
						RUNNABLE_TREE);
				HyperViewRunnableController runnableTree = (HyperViewRunnableController) vb
						.getValue(ctx);
				HyperViewParser parser = new HyperViewParser();
				xmlHyperView = parser.parse(input);
				runnableTree.setConnectionProperties(props);
				runnableTree.setHyperView(xmlHyperView);
				DataModel model = runnableTree.getParametersModel();
				List list = (List) model.getWrappedData();
				Map parameters = req.getParameterMap();
				for (Object o : list) {
					HyperViewParameter param = (HyperViewParameter) o;
					String name = param.getName();
					if (parameters.containsKey(name)) {
						String[] value = (String[]) parameters.get(name);
						param.setValue(value == null ? null : value[0]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			AonUtil.addFatalMessage( e.getMessage() );
			throw new AbortProcessingException(e.getMessage(), e);
		} 
	}

	public void onLaunch(ActionEvent event) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ValueBinding vb = ctx.getApplication()
				.createValueBinding(RUNNABLE_TREE);
		HyperViewRunnableController runnableTree = (HyperViewRunnableController) vb
				.getValue(ctx);
		List<HyperViewParameter> parameters = runnableTree.getHyperView()
				.getParameters();
		if (parameters == null || parameters.isEmpty()) {
			runnableTree.onRun(event);
		}
	}

	public String launch() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ValueBinding vb = ctx.getApplication()
				.createValueBinding(RUNNABLE_TREE);
		HyperViewRunnableController runnableTree = (HyperViewRunnableController) vb
				.getValue(ctx);
		List<HyperViewParameter> parameters = runnableTree.getHyperView()
				.getParameters();
		if (parameters == null || parameters.isEmpty() || !thereIsPromptedParameters(parameters)) {
			runnableTree.onRun( null );
			return "hyperview_view";
		}
		return "parameters";

	}

	private boolean thereIsPromptedParameters(List<HyperViewParameter> parameters) {
		for (HyperViewParameter param: parameters ) {
			if ( param.isPrompt() ) {
				return true;
			}
		}
		return false;
	}

}
