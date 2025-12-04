package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.HashMap;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessage;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageType;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageType.ConsoleDomainMessageTypeVisitor;
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

public class AonConsoleLogger extends DockLayoutPanel implements IConsoleLogger {
	
	private static final Logger LOGGER = Logger.getLogger(AonConsoleLogger.class.getName());
	
	private final FlowPanel headerPanel; 
	private final ScrollPanel scrollPanel;
	private final FlowPanel topContainer;
	private final FlowPanel bottomContainer;
	private final InlineLabel title; 
	private final AonConsoleProgressPanel main;
	private final HashMap<String,AonConsoleProgressPanel> labels = new HashMap<>();
	private final Supplier<ConsoleDomainMessageTypeVisitor<? extends Widget>> visitorSupplier;
	
	public AonConsoleLogger( Supplier<ConsoleDomainMessageTypeVisitor<? extends Widget>> visitorSupplier ) {
		super(Unit.PX);
		
		this.visitorSupplier = visitorSupplier;
		headerPanel = new FlowPanel();
		headerPanel.setStyleName(AON.CSS.aonBorderBottom());
		headerPanel.addStyleName(AON.CSS.aonPadding());
		
		title = new InlineLabel();
		title.setStyleName(AON.CSS.aonFontLarger());
		title.addStyleName(AON.CSS.aonBold());
		title.addStyleName(AON.CSS.aonTextUnderline());
		title.addStyleName(AON.CSS.aonMarginBottom());
		title.addStyleName(AON.CSS.aonMarginRight());
		headerPanel.add(title);

		main = new AonConsoleProgressPanel();
		main.addStyleName(AON.CSS.aonWidthAlmostAll());
		main.addStyleName(AON.CSS.aonInline());
		main.setVisible(false);
		headerPanel.add(main);
		
		addNorth(headerPanel, 35);
		
		SimpleLayoutPanel slp = new SimpleLayoutPanel();
		scrollPanel = new ScrollPanel();
		setStyleName(AON.CSS.aonScrollArea());
		FlowPanel container = new FlowPanel();
		container.setStyleName(AON.CSS.aonPadding());
		scrollPanel.add(container);
		topContainer = new FlowPanel();
		container.add(topContainer);
		bottomContainer = new FlowPanel();
		container.add(bottomContainer);
		slp.setWidget(scrollPanel);
		add(slp);
		
	}

	public void reset() {
		title.setTitle(null);
		main.setVisible(false);
		main.setProgress(0,null);
		topContainer.clear();
		bottomContainer.clear();
	}
	
	public void setMainProgress(double percent, String msg) {
		main.setVisible(true);
		main.setProgress(percent,msg);
	}

	@Override
	public void log(JsConsoleMessage message) {
		if (message == null) return;
		message.getType().visit( new ConsoleMessageVisitor(this.visitorSupplier, message));
		scrollPanel.scrollToBottom();
	}
	
	protected void showWarning(String message) {
		Label messageLabel = new Label(message);
		messageLabel.setStyleName(AON.CSS.aonColorOrange());
		messageLabel.addStyleName(AON.CSS.aonBold());
		messageLabel.addStyleName(AON.CSS.aonMarginLeft());
		topContainer.add(messageLabel);
	}



	private final class ConsoleMessageVisitor implements Visitor {
		private final JsConsoleMessage message;
		private final Supplier<ConsoleDomainMessageTypeVisitor<? extends Widget>> visitorSupplier;
		
		private ConsoleMessageVisitor(Supplier<ConsoleDomainMessageTypeVisitor<? extends Widget>> visitorSupplier, JsConsoleMessage message) {
			this.message = message;
			this.visitorSupplier = visitorSupplier;
		}

		private AonConsoleProgressPanel getProgressPanel() {
			return labels.computeIfAbsent(message.getProcessId()
				, k -> {
					AonConsoleProgressPanel w = new AonConsoleProgressPanel();
					topContainer.add(w);
					return w;
			});
		}

		@Override
		public void visitTitle() {
			title.setText(message.getMessage());
		}
		
		@Override
		public void visitSubtitle() {
			Label messageLabel = new Label(message.getMessage());
			messageLabel.setStyleName(AON.CSS.aonMarginTop());
			messageLabel.addStyleName(AON.CSS.aonBold());
			topContainer.add(messageLabel);
		}
		@Override
		public void visitMessage() {
			Label label = new Label(message.getMessage());
			label.setStyleName(AON.CSS.aonMarginLeft());
			topContainer.add(label);
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
			messageLabel.addStyleName(AON.CSS.aonMarginLeft());
			topContainer.add(messageLabel);
		}

		@Override
		public void visitWarning() {
			AonConsoleLogger.this.showWarning( message.getMessage() );
		}

		@Override
		public void visitError() {
			Label messageLabel = new Label(message.getMessage());
			messageLabel.setStyleName(AON.CSS.aonColorRed());
			messageLabel.addStyleName(AON.CSS.aonBold());
			messageLabel.addStyleName(AON.CSS.aonMarginLeft());
			topContainer.add(messageLabel);
		}

		@Override
		public void visitConsoleMessage() {
			if (message.getConsoleDomainMessage() != null) {
				LOGGER.info("1 -  ConsoleDomainMessage: " + message.getConsoleDomainMessage() );
				JsConsoleDomainMessage domainMessage = message.getConsoleDomainMessage();
				LOGGER.info("2 - domainMessage NULL?: " + (domainMessage == null ? "SI" : "NO") );
				if (domainMessage != null) {
					LOGGER.info("3 - domainMessage.getMessage(): " + domainMessage.getMessage() );
					ConsoleDomainMessage cdm = domainMessage.getConsoleDomainMessage();
					LOGGER.info("4 - ConsoleDomainMessage.getMessage(): " + cdm.getMessage() );
					ConsoleDomainMessageType type = domainMessage.getType(); 
					Widget widget = type.visit( cdm, this.visitorSupplier.get() );
					if (widget != null)
						bottomContainer.add(widget);
				}
			}
		}

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
