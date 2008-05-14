package com.code.aon.hyperview.renderer;

import com.code.aon.hyperview.renderer.pdf.HyperViewPDFRenderer;
import com.code.aon.hyperview.renderer.xls.HyperViewXLSRenderer;

public class HyperViewRendererFactory {

	public static IHyperViewRenderer getRenderer(HyperViewRenderFormat format)
			throws HyperViewRendererException {

		if (format == HyperViewRenderFormat.EXCEL) {
			return new HyperViewXLSRenderer();
		} else if (format == HyperViewRenderFormat.PDF) {
			return new HyperViewPDFRenderer();
		}

		throw new HyperViewRendererException("No suitable Render for " + format
				+ " formatter!");
	}

}
