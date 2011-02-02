package com.code.aon.hyperview.renderer;

import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.ResourceBundle;

import com.code.aon.hyperview.model.HyperViewAttribute;
import com.code.aon.hyperview.player.HyperViewPlayer;
import com.code.aon.hyperview.player.IHyperViewPlayerNode;

public abstract class HyperViewAbstractRenderer implements IHyperViewRenderer {
	private IHyperViewPlayerNode startNode;

	private HyperViewPlayer player;

	private boolean recursive;

	private ResourceBundle bundle;

	private HyperViewRendererUtils rendererUtils;

	private URL logo;

	public void setMessageBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public void setHyperViewPlayer(HyperViewPlayer player) {
		this.player = player;
	}

	public HyperViewPlayer getHyperViewPlayer() {
		return this.player;
	}

	public void setNode(IHyperViewPlayerNode node) {
		this.startNode = node;
	}

	public HyperViewRendererUtils getHyperViewRendererUtils() {
		if (rendererUtils == null) {
			rendererUtils = new HyperViewRendererUtils(bundle);
		}
		return rendererUtils;
	}

	public IHyperViewPlayerNode getNode() {
		return this.startNode;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public void setLogo(URL url) {
		this.logo = url;
	}

	public URL getLogo() {
		return this.logo;
	}

	public void run(OutputStream out) throws HyperViewRendererException {

		start(out);

	}

	public DateFormat getDateFormatter() {
		return getHyperViewRendererUtils().getDateFormatter();
	}

	public NumberFormat getDecimalFormatter() {
		return getHyperViewRendererUtils().getDecimalFormatter();
	}

	public NumberFormat getIntegerFormatter() {
		return getHyperViewRendererUtils().getIntegerFormatter();
	}

	public DateFormat getTimeFormatter() {
		return getHyperViewRendererUtils().getTimeFormatter();
	}

	public DateFormat getTimestampFormatter() {
		return getHyperViewRendererUtils().getTimestampFormatter();
	}

	protected String getValue(HyperViewAttribute attr, Object value)
			throws HyperViewRendererException {
		return getHyperViewRendererUtils().getValue(attr, value);
	}

	protected Date getDate(Number number) throws HyperViewRendererException {
		return getHyperViewRendererUtils().getDate(number);
	}

	protected abstract void start(OutputStream out)
			throws HyperViewRendererException;

}
