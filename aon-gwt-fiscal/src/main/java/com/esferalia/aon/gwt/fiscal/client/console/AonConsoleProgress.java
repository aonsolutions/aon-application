package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.HashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.console.ConsoleMessageType.Visitor;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class AonConsoleProgress extends DockLayoutPanel {
	
	private final ScrollPanel scrollPanel;
	private final FlowPanel container;
	private Label title; 
	private Label subtitle;
	private AonConsoleProgressPanel main;
	private final HashMap<String,AonConsoleProgressPanel> labels = new HashMap<>();
	
	AonConsoleProgress() {
		super(Unit.PX);
		
		FlowPanel headerPanel = new FlowPanel();
		headerPanel.setStyleName(AON.CSS.aonBorderBottom());
		headerPanel.addStyleName(AON.CSS.aonPadding());
		
		title = new Label();
		title.setStyleName(AON.CSS.aonFontLarger());
		title.addStyleName(AON.CSS.aonBold());
		title.addStyleName(AON.CSS.aonTextUnderline());
		title.addStyleName(AON.CSS.aonMarginBottom());
		headerPanel.add(title);
		
		subtitle = new Label();
		subtitle.setStyleName(AON.CSS.aonBold());
		subtitle.addStyleName(AON.CSS.aonMarginBottom());
		headerPanel.add(subtitle);

		main = new AonConsoleProgressPanel();
		main.addStyleName(AON.CSS.aonWidth600());
		headerPanel.add(main);
		addNorth(headerPanel, 90);
		

		SimpleLayoutPanel slp = new SimpleLayoutPanel();
		scrollPanel = new ScrollPanel();
		setStyleName(AON.CSS.aonScrollArea());
		container = new FlowPanel();
		container.setStyleName(AON.CSS.aonPadding());
		scrollPanel.add(container);
		slp.setWidget(scrollPanel);
		add(slp);
	}

	public void setMainProgress(double percent, String msg) {
		main.setProgress(percent,msg);
	}

	void log(JsConsoleMessage message) {
		
		if (message != null) {
			message.getType().visit( new Visitor() {
				
				private AonConsoleProgressPanel getProgressPanel() {
					return labels.computeIfAbsent(message.getProcessId()
						, k -> {
							AonConsoleProgressPanel w = new AonConsoleProgressPanel();
							w.addStyleName(AON.CSS.aonWidth600());
							container.add(w);
							return w;
					});
				}
				
				@Override
				public void visitTitle() {
					title.setText(message.getMessage());
				}
				
				@Override
				public void visitSubtitle() {
					subtitle.setText(message.getMessage());
				}
				
				@Override
				public void visitMessage() {
					container.add(new Label(message.getMessage()));
				}

				@Override
				public void visitMainProgress() {
					setMainProgress(message.getPercent(), message.getMessage());
				}

				@Override
				public void visitProgress() {
					getProgressPanel().setProgress(message.getPercent(), message.getMessage());
				}
				
				@Override
				public void visitOk() {
					Label messageLabel = new Label(message.getMessage());
					messageLabel.setStyleName(AON.CSS.aonColorGreen());
					messageLabel.addStyleName(AON.CSS.aonBold());
					container.add(messageLabel);
				}
				
				@Override
				public void visitWarning() {
					Label messageLabel = new Label(message.getMessage());
					messageLabel.setStyleName(AON.CSS.aonColorOrange());
					messageLabel.addStyleName(AON.CSS.aonBold());
					container.add(messageLabel);
				}

				@Override
				public void visitError() {
					Label messageLabel = new Label(message.getMessage());
					messageLabel.setStyleName(AON.CSS.aonColorRed());
					messageLabel.addStyleName(AON.CSS.aonBold());
					container.add(messageLabel);
				}
			});
		}
		scrollPanel.scrollToBottom();
	}
	
	static class AonConsoleProgressPanel extends FlowPanel {
		private InlineLabel messageLabel = new InlineLabel();
		private AonConsoleProgressBar bar = new AonConsoleProgressBar();
		
		AonConsoleProgressPanel() {
			setStyleName(AON.CSS.aonMargin());
			messageLabel.setStyleName(AON.CSS.aonItalic());
			messageLabel.addStyleName(AON.CSS.aonPaddingLeft());
			bar.setWidth("200px");
			add(bar);
			add(messageLabel);
		}
		
		void setMesssageLabel(String message) {
			messageLabel.setText(message);
		}
		
		public void setProgress(double percent, String msg) {
			bar.setProgress(percent);
			messageLabel.setText(" (" + AON.FMT.format(percent) +  "% ) " + msg);
		}
		
	}
	
	
	static class AonConsoleProgressBar extends Widget {
		private static final double MAX = 100;
	    private final Element progress;

	    public AonConsoleProgressBar() {
	    	this(0.0);
	    }
	    public AonConsoleProgressBar(double  percent) {
	        progress = DOM.createElement("progress");
	        progress.setAttribute("max", Double.toString(MAX));
	        progress.setAttribute("value", Double.toString(percent));
	        setElement(progress);
	    }

	    public void setProgress(double  percent) {
	        progress.setAttribute("value", Double.toString(percent));
	    }

	}
	
}
