package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.CretaService.JsFile;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsTrabajadoresYTramos;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;

public abstract class JsFileColumn extends Column<JsFile, JsFile> {

	static class JsFileCell extends AbstractCell<JsFile> {

		static interface Handler {

			void onJsFileOut(JsFile jsFile, NativeEvent event);

			void onJsFileOver(JsFile jsFile, NativeEvent event);

			void onJsFileClick(JsFile jsFile, NativeEvent event);

			void onJsFileDblClick(JsFile jsFile, NativeEvent event);

			String getIconStyle(JsTrabajadoresYTramos jsTrabajadoresYTramos);

			String getDescription(JsTrabajadoresYTramos jsTrabajadoresYTramos);
		}

		static interface JsFileTemplate extends SafeHtmlTemplates {

			// onmouseout=\"__onJsFileOut()\"
			// onclick=\"__onJsFileClick(this.id,event.clientX,event.clientY)\"
			// onmouseover=\"__onJsFileOver(this.id,event.clientX,event.clientY)\"

			@Template("<div id=\"{4}\" class=\"aon-nowrap\" ><span class=\"{0}\" style=\"padding-left: 16px;\"></span><span class=\"aon-bold\" style=\"padding-left: 8px;\">{1}</span><span> ({2} {3})</span></div>")
			SafeHtml trabajadoresYTramos(String iconStyle, String description,
					String ccc, String month, String key);
		}

		private static final JsFileCell.JsFileTemplate JSFILE_TEMPLATE = GWT
				.create(JsFileCell.JsFileTemplate.class);

		private JsFileCell.Handler handler;

		public JsFileCell() {
			super(BrowserEvents.MOUSEOVER, BrowserEvents.MOUSEOUT,
					BrowserEvents.CLICK, BrowserEvents.DBLCLICK);
		}

		// ----------------------------------------------------------------
		@Override
		public void render(Context context, JsFile jsFile, SafeHtmlBuilder sb) {
			JsTrabajadoresYTramos t = (JsTrabajadoresYTramos) jsFile;
			sb.append(JSFILE_TEMPLATE.trabajadoresYTramos(
					handler.getIconStyle(t), handler.getDescription(t),
					t.getCCC().substring(6), t.getFrom(), t.getId())

			);
		}

		// ----------------------------------------------------------------
		@Override
		public void onBrowserEvent(Context context, Element parent,
				JsFile jsFile, NativeEvent event,
				ValueUpdater<JsFile> valueUpdater) {

			String type = event.getType();

			if (BrowserEvents.CLICK.equals(type))
				handler.onJsFileClick(jsFile, event);
			if (BrowserEvents.DBLCLICK.equals(type))
				handler.onJsFileDblClick(jsFile, event);
			else if (BrowserEvents.MOUSEOUT.equals(type))
				handler.onJsFileOut(jsFile, event);
			else if (BrowserEvents.MOUSEOVER.equals(type))
				handler.onJsFileOver(jsFile, event);
			else if (BrowserEvents.MOUSEWHEEL.equals(type))
				;
		}

		// ----------------------------------------------------------------

		private void setHandler(JsFileCell.Handler handler) {
			this.handler = handler;
		}

	}

	public JsFileColumn() {
		super(new JsFileCell());

		((JsFileColumn.JsFileCell) getCell())
				.setHandler(new JsFileCell.Handler() {
					@Override
					public void onJsFileOver(JsFile jsFile, NativeEvent event) {
						JsFileColumn.this.onJsFileOver(jsFile, event);
					}

					@Override
					public void onJsFileOut(JsFile jsFile, NativeEvent event) {
						JsFileColumn.this.onJsFileOut(jsFile, event);
					}

					@Override
					public void onJsFileClick(JsFile jsFile,
							NativeEvent event) {
						JsFileColumn.this.onJsFileClick(jsFile, event);
					}

					@Override
					public void onJsFileDblClick(JsFile jsFile,
							NativeEvent event) {
						JsFileColumn.this.onJsFileDblClick(jsFile, event);
					}

					@Override
					public String getIconStyle(
							JsTrabajadoresYTramos jsTrabajadoresYTramos) {
						return JsFileColumn.this
								.getIconStyle(jsTrabajadoresYTramos);
					}

					@Override
					public String getDescription(
							JsTrabajadoresYTramos jsTrabajadoresYTramos) {
						return JsFileColumn.this
								.getDescription(jsTrabajadoresYTramos);
					}
				});

	}

	// --------------------------------------------------------------------

	@Override
	public JsFile getValue(JsFile jsFile) {
		return jsFile;
	}

	// --------------------------------------------------------------------

	abstract void onJsFileOut(JsFile jsFile, NativeEvent event);

	abstract void onJsFileOver(JsFile jsFile, NativeEvent event);

	abstract void onJsFileClick(JsFile jsFile, NativeEvent event);

	abstract void onJsFileDblClick(JsFile jsFile, NativeEvent event);

	abstract String getIconStyle(JsTrabajadoresYTramos jsTrabajadoresYTramos);

	abstract String getDescription(JsTrabajadoresYTramos jsTrabajadoresYTramos);

	// --------------------------------------------------------------------

}