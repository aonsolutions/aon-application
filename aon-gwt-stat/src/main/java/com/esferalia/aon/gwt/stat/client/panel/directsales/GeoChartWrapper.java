package com.esferalia.aon.gwt.stat.client.panel.directsales;
import com.google.gwt.ajaxloader.client.ArrayHelper;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.events.Handler;
import com.google.gwt.visualization.client.events.RegionClickHandler;
import com.google.gwt.visualization.client.events.ZoomOutHandler;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;

/**
 * Simple wrapper for the GeoChart from google-visualization API.
 * 
 * @author L. Plotnicki <lukasz.plotnicki@med.uni-heidelberg.de> Created with
 *         IntelliJ IDEA. Date: 09.07.12 Time: 16:42
 */
public class GeoChartWrapper extends CoreChart {
	// extends Visualization<GeoChartWrapper.Options> {
	/**
	 * How to display data on the map.
	 */
	public static enum DisplayMode {
		/**
		 * Put markers on the map, changing size and color according to the
		 * number given.
		 */
		MARKERS,

		/**
		 * Color regions inside the map according to the number given.
		 */
		REGIONS
	}

	private static class GeoChartColorAxis extends JavaScriptObject {
		public static GeoChartColorAxis create() {
			return createObject().cast();
		}

		protected GeoChartColorAxis() {
		}

		public final native void setColors(JsArrayString colors) /*-{
			this.colors = colors;
		}-*/;

		public final native void setMaxValue(double maxValue) /*-{
			this.maxValue = maxValue;
		}-*/;

		public final native void setMinValue(double minValue) /*-{
			this.minValue = minValue;
		}-*/;

		public final native void setValues(JsArrayNumber values) /*-{
			this.values = values;
		}-*/;

	}

	private static class MagnifyingGlass extends JavaScriptObject {
		public static MagnifyingGlass create() {
			return createObject().cast();
		}

		protected MagnifyingGlass() {
		}

		public final native void setEnable(boolean enable) /*-{
			this.enable = enable;
		}-*/;

		public final native void setZoomFactor(double zoomFactor) /*-{
			this.zoomFactor = zoomFactor;
		}-*/;
	}

	/**
	 * An object with members to configure how values are associated with bubble
	 * size.
	 */
	private static class SizeAxis extends JavaScriptObject {
		public static SizeAxis create() {
			return createObject().cast();
		}

		protected SizeAxis() {
		}

		/**
		 * Sets the maximum radius of the largest possible bubble, in pixels.
		 * 
		 * @param maxSize
		 *            maximum radius of the largest possible bubble, in pixels
		 */
		public final native void setMaxSize(int maxSize) /*-{
			this.maxSize = maxSize;
		}-*/;

		/**
		 * Sets the size value (as appears in the chart data) to be mapped to
		 * sizeAxis.maxSize. Larger values will be cropped to this value.
		 * 
		 * @param maxValue
		 */
		public final native void setMaxValue(double maxValue) /*-{
			this.maxValue = maxValue;
		}-*/;

		/**
		 * Sets the minimum radius of the smallest possible bubble, in pixels.
		 * 
		 * @param minSize
		 *            minimum radius of the smallest possible bubble, in pixels
		 */
		public final native void setMinSize(int minSize) /*-{
			this.minSize = minSize;
		}-*/;

		/**
		 * Sets the size value (as appears in the chart data) to be mapped to
		 * sizeAxis.minSize. Smaller values will be cropped to this value.
		 * 
		 * @param minValue
		 */
		public final native void setMinValue(double minValue) /*-{
			this.minValue = minValue;
		}-*/;

	}

	/**
	 * Options for drawing the chart.
	 * 
	 */
	public static class Options extends com.google.gwt.visualization.client.visualizations.corechart.Options {
		public static Options create() {
			return JavaScriptObject.createObject().cast();
		}

		protected Options() {
		}

		public final void setColorAxis(String... colors) {
			GeoChartColorAxis a = GeoChartColorAxis.create();
			a.setColors(ArrayHelper.toJsArrayString(colors));
			setColorAxis(a);
		}

		private final native void setColorAxis(GeoChartColorAxis colorAxis) /*-{
			this.colorAxis = colorAxis;
		}-*/;

		public final void setDisplayMode(DisplayMode mode) {
			setDisplayMode(mode.name().toLowerCase());
		}

		private native void setDisplayMode(String mode) /*-{
			this.displayMode = mode;
		}-*/;

//		public final native void setBackgroundColor(String backgroundColor) /*-{
//			this.backgroundColor = backgroundColor;
//		}-*/;

		public final native void setDatalessRegionColor(
				String datalessRegionColor) /*-{
			this.datalessRegionColor = datalessRegionColor;
		}-*/;

		public final native void setEnableRegionInteractivity(
				boolean enableRegionInteractivity) /*-{
			this.enableRegionInteractivity = enableRegionInteractivity;
		}-*/;

		public final native void setKeepAspectRatio(boolean keepAspectRatio) /*-{
			this.keepAspectRatio = keepAspectRatio;
		}-*/;

		private final native void setMagnifyingGlass(
				MagnifyingGlass magnifyingGlass) /*-{
			this.magnifyingGlass = magnifyingGlass;
		}-*/;

		public final void setMagnifyingGlass(boolean enabled, double zoomFactor) {
			MagnifyingGlass mg = MagnifyingGlass.create();
			mg.setEnable(enabled);
			mg.setZoomFactor(zoomFactor);
			setMagnifyingGlass(mg);
		}

		public final native void setMarkerOpacity(double markerOpacity) /*-{
			this.markerOpacity = markerOpacity;
		}-*/;

		public final native void setRegion(String region) /*-{
			this.region = region;
		}-*/;

		/**
		 * @param maxSize
		 *            - Maximum radius of the largest possible bubble, in
		 *            pixels.
		 * @param minSize
		 *            - Mininum radius of the smallest possible bubble, in
		 *            pixels.
		 */
		public final void setSizeAxis(int maxSize, int minSize) {
			SizeAxis s = SizeAxis.create();
			s.setMaxSize(maxSize);
            s.setMinSize(minSize);
            setSizeAxis(s);
		}

		/**
		 * @param maxSize
		 *            - Maximum radius of the largest possible bubble, in
		 *            pixels.
		 * @param minSize
		 *            - Mininum radius of the smallest possible bubble, in
		 *            pixels.
		 * @param maxValue
		 *            - Maximum value of size column in chart data The size
		 *            value (as appears in the chart data) to be mapped to
		 *            sizeAxis.maxSize. Larger values will be cropped to this
		 *            value.
		 * @param minValue
		 *            - Minimum value of size column in chart data The size
		 *            value (as appears in the chart data) to be mapped to
		 *            sizeAxis.minSize. Smaller values will be cropped to this
		 *            value.
		 */
		public final void setSizeAxis(int maxSize, int minSize,
				double maxValue, double minValue) {
			SizeAxis s = SizeAxis.create();
			s.setMaxSize(maxSize);
			s.setMaxValue(maxValue);
			s.setMinSize(minSize);
			s.setMinValue(minValue);
			setSizeAxis(s);
		}

		private final native void setSizeAxis(SizeAxis sizeAxis) /*-{
			this.sizeAxis = sizeAxis;
		}-*/;

//        public final native void setHeight(int height) /*-{
//                this.height = height;
//        }-*/;
//
//        public final native void setWidth(int width) /*-{
//                this.width = width;
//        }-*/;
	}

	public static final String PACKAGE = "geochart";

	public GeoChartWrapper(AbstractDataTable data, Options options) {
		super(data, options);
	}

//	public void addReadyHandler(ReadyHandler handler) {
//		Handler.addHandler(this, "drawingDone", handler);
//	}

	public void addRegionClickHandler(RegionClickHandler handler) {
		Handler.addHandler(this, "regionClick", handler);
	}

//	public final void addSelectHandler(SelectHandler handler) {
//		Selection.addSelectHandler(this, handler);
//	}

	public void addZoomOutHandler(ZoomOutHandler handler) {
		Handler.addHandler(this, "zoomOut", handler);
	}

//	public final JsArray<Selection> getSelections() {
//		return Selection.getSelections(this);
//	}
//
//	public final void setSelections(JsArray<Selection> sel) {
//		Selection.setSelections(this, sel);
//	}

	@Override
	protected native JavaScriptObject createJso(Element parent) /*-{
		return new $wnd.google.visualization.GeoChart(parent);
	}-*/;
}