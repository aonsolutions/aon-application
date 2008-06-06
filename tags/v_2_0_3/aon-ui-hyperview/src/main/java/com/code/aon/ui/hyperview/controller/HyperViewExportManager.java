package com.code.aon.ui.hyperview.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.hyperview.HyperViewUtil;
import com.code.aon.hyperview.player.HyperViewPlayer;
import com.code.aon.hyperview.player.IHyperViewPlayerNode;
import com.code.aon.hyperview.renderer.HyperViewRenderFormat;
import com.code.aon.hyperview.renderer.HyperViewRendererException;
import com.code.aon.hyperview.renderer.HyperViewRendererFactory;
import com.code.aon.hyperview.renderer.IHyperViewRenderer;
import com.code.aon.ui.util.AonUtil;

public class HyperViewExportManager {
	private static String CONTENT_DISPOSITION_HEADER = "Content-Disposition";

	private MessageFormat contentDisposition = new MessageFormat("attachment; filename=\"{0}.{1}\";");

	private boolean pdf = true; // true--> PDF; false-> XLS

	private boolean exportChildNodes = true;

	private boolean download = false;

	private String fileName;

	private String contextRelativeLogoPath;

	public boolean isDownload() {
		return download;
	}

	public void setDownload(boolean download) {
		this.download = download;
	}

	public boolean isExportChildNodes() {
		return exportChildNodes;
	}

	public void setExportChildNodes(boolean exportChildNodes) {
		this.exportChildNodes = exportChildNodes;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isPdf() {
		return pdf;
	}

	public void setPdf(boolean pdf) {
		this.pdf = pdf;
	}

	public String export() {
		try {
			HyperViewRenderFormat f = isPdf() ? HyperViewRenderFormat.PDF : HyperViewRenderFormat.EXCEL;
			IHyperViewRenderer wnr = HyperViewRendererFactory.getRenderer(f);
			export(wnr);
			setFileName(null);
			return null;
		} catch (HyperViewRendererException e) {
			e.printStackTrace();
			AonUtil.addFatalMessage( e.getMessage() );
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private void export(IHyperViewRenderer wnr) throws HyperViewRendererException {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			wnr.setNode(getStartingNode());
			wnr.setHyperViewPlayer(getPlayer());
			wnr.setRecursive(exportChildNodes);

			FacesContext ctx = FacesContext.getCurrentInstance();
			Application app = ctx.getApplication();
			String baseName = app.getMessageBundle();
			Locale locale = ctx.getViewRoot().getLocale();
			ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
			ExternalContext ec = ctx.getExternalContext();
			wnr.setMessageBundle(bundle);
			wnr.setLogo(getLogo());
			wnr.run(out);
			out.flush();

			HttpServletResponse sr = (HttpServletResponse) ec.getResponse();
			sr.setContentType(wnr.getContentType());
			if (isDownload()) {
				if (fileName == null || ("").equals(fileName)) {
					fileName = getStartingNode().getDescription();
					fileName = HyperViewUtil.escapeCharacters(fileName);
				}
				String[] args = new String[] { fileName, wnr.getExtension() };
				String cd = contentDisposition.format(args);
				sr.setHeader(CONTENT_DISPOSITION_HEADER, cd);
			}

			OutputStream res = sr.getOutputStream();
			res.write(out.toByteArray());
			res.flush();
			ctx.responseComplete();
		} catch (IOException e) {
			e.printStackTrace();
			throw new HyperViewRendererException(e.getMessage());
		}
	}

	private IHyperViewPlayerNode getStartingNode() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Application app = ctx.getApplication();
		ValueBinding vb = app.createValueBinding("#{runnableTree}");
		HyperViewRunnableController tree = (HyperViewRunnableController) vb.getValue(ctx);
		return (IHyperViewPlayerNode) tree.getCurrentNode();
	}

	private HyperViewPlayer getPlayer() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Application app = ctx.getApplication();
		ValueBinding vb = app.createValueBinding("#{runnableTree}");
		HyperViewRunnableController tree = (HyperViewRunnableController) vb.getValue(ctx);
		HyperViewPlayer player = tree.getPlayer();
		return player;
	}

	public String getContextRelativeLogoPath() {
		return contextRelativeLogoPath;
	}

	public void setContextRelativeLogoPath(String contextRelativeLogoPath) {
		this.contextRelativeLogoPath = contextRelativeLogoPath;
	}

	private URL getLogo() {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			HttpServletRequest req = (HttpServletRequest) ctx.getExternalContext().getRequest();
			String protocol = req.getScheme();
			String host = req.getServerName();
			int port = req.getLocalPort();
			String context = req.getContextPath();
			return new URL(protocol, host, port, context + getContextRelativeLogoPath());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			AonUtil.addFatalMessage( e.getMessage() );
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
}
