package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.HashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.console.ConsoleMessageType.Visitor;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

class AonConsoleProgress extends ScrollPanel {
	
	private final FlowPanel container;
	private final HashMap<String,AonConsoleProgressPanel> labels = new HashMap<>();
	
	AonConsoleProgress() {
		setStyleName(AON.CSS.aonScrollArea());
		container = new FlowPanel();
		container.setStyleName(AON.CSS.aonPadding());
		setWidget(container);
	}

	void log(JsConsoleMessage message) {
		
		if (message != null) {
			final Label messageLabel = new Label();
			message.getType().visit( new Visitor() {
				
				private AonConsoleProgressPanel getProgressPanel() {
					return labels.computeIfAbsent(message.getProcessId()
						, k -> {
							AonConsoleProgressPanel w = new AonConsoleProgressPanel();
							container.add(w);
							return w;
					});
				}
				
				@Override
				public void visitTitle() {
					getProgressPanel().addTitleMessage(message.getMessage());
				}
				
				@Override
				public void visitSubtitle() {
					getProgressPanel().addSubtitleMessage(message.getMessage());
				}
				
				@Override
				public void visitMessage() {
					getProgressPanel().addMessage(message.getMessage());
				}

				@Override
				public void visitProgress() {
					getProgressPanel().setProgress(message.getPercent(), message.getMessage());
				}
				
				@Override
				public void visitOk() {
					getProgressPanel().addOK(message.getMessage());
				}
				
				@Override
				public void visitError() {
					getProgressPanel().addError(message.getMessage());
				}
			});
			container.add(messageLabel);
		}
		scrollToBottom();
	}
	
	static class AonConsoleProgressPanel extends FlowPanel {
		private AonConsoleProgressBar bar;
		private InlineLabel label;
		
		AonConsoleProgressPanel() {
			setStyleName(AON.CSS.aonMargin());
			addStyleName(AON.CSS.aonBorder());
			addStyleName(AON.CSS.aonMargin());
		}
		
		void addTitleMessage(String message) {
			Label messageLabel = new Label(message);
			messageLabel.setStyleName(AON.CSS.aonFontLarger());
			messageLabel.addStyleName(AON.CSS.aonBold());
			messageLabel.addStyleName(AON.CSS.aonTextUnderline());
			messageLabel.addStyleName(AON.CSS.aonMarginBottom());
			messageLabel.addStyleName(AON.CSS.aonPaddingLeft());
			add(messageLabel);
		}

		void addSubtitleMessage(String message) {
			Label messageLabel = new Label(message);
			messageLabel.setStyleName(AON.CSS.aonBold());
			messageLabel.addStyleName(AON.CSS.aonMarginBottom());
			messageLabel.addStyleName(AON.CSS.aonPaddingLeft());
			add(messageLabel);
		}
		
		void addMessage(String message) {
			Label messageLabel = new Label(message);
			messageLabel.setStyleName(AON.CSS.aonPaddingLeft());
			add(messageLabel);
		}
		
		void addOK(String message) {
			Label messageLabel = new Label(message);
			messageLabel.setStyleName(AON.CSS.aonColorGreen());
			messageLabel.addStyleName(AON.CSS.aonBold());
			messageLabel.addStyleName(AON.CSS.aonPaddingLeft());
			add(messageLabel);
		}
		void addError(String message) {
			Label messageLabel = new Label(message);
			messageLabel.setStyleName(AON.CSS.aonColorRed());
			messageLabel.addStyleName(AON.CSS.aonBold());
			messageLabel.addStyleName(AON.CSS.aonPaddingLeft());
			add(messageLabel);
		}
		
		public void setProgress(double percent, String msg) {
			if (bar == null) {
				bar = new AonConsoleProgressBar(percent);
				bar.getElement().getStyle().setWidth(30.0, Unit.PCT);
				label = new InlineLabel( msg );
				label.setStyleName(AON.CSS.aonMarginLeft());
				FlowPanel progressPanel = new FlowPanel();
				progressPanel.addStyleName(AON.CSS.aonPaddingLeft());
				progressPanel.add(bar);
				progressPanel.add(label);
				add(progressPanel);
			}
			setProgress(percent);
			label.setText(msg);
		}
		
		public void setProgress(double percent) {
			bar.setProgress(percent);
		}
	}
	
	static class AonConsoleProgressBar extends Widget {
		private static final double MAX = 100;
	    private final Element progress;
	    private final Element percentageLabel;

	    public AonConsoleProgressBar(double percent) {
	        progress = DOM.createElement("progress");
	        progress.setAttribute("max", Double.toString(MAX));
	        progress.setAttribute("value", Double.toString(percent));
	        percentageLabel = DOM.createElement("span");
	        percentageLabel.setInnerHTML(AON.FMT.format(percent));
	        progress.insertFirst(percentageLabel);
	        setElement(progress);
	        
	    }

	    public void setProgress(double percent) {
	        progress.setAttribute("value", Double.toString(percent));
	        percentageLabel.setInnerHTML(AON.FMT.format(percent));
	    }

	}
	
}
