package com.code.aon.hyperview.renderer;

import java.io.OutputStream;
import java.net.URL;
import java.util.ResourceBundle;

import com.code.aon.hyperview.player.HyperViewPlayer;
import com.code.aon.hyperview.player.IHyperViewPlayerNode;

public interface IHyperViewRenderer {

	void setNode(IHyperViewPlayerNode node);

	void setHyperViewPlayer(HyperViewPlayer player);

	String getContentType();

	String getExtension();

	void setRecursive(boolean recursive);

	boolean isRecursive();

	void setMessageBundle(ResourceBundle bundle);

	void setLogo(URL logoURL);

	void run(OutputStream out) throws HyperViewRendererException;
}
