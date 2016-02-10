package com.esferalia.aon.gwt.viewer.server.html2Image;

public class Html2Image {
	private HtmlParser parser = new HtmlParserImpl();
	private ImageRenderer imageRenderer;

	public HtmlParser getParser() {
		return parser;
	}

	public ImageRenderer getImageRenderer() {
		if (imageRenderer == null) {
			imageRenderer = new ImageRendererImpl(parser);
		}
		return imageRenderer;
	}

	public static Html2Image fromHtml(String html) {
		final Html2Image html2Image = new Html2Image();
		html2Image.getParser().loadHtml(html);
		return html2Image;
	}
}
