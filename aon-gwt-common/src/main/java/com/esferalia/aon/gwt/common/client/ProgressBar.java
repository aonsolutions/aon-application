package com.esferalia.aon.gwt.common.client;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProgressBar extends VerticalPanel {

	public static final int SHOW_TEXT = 2;

	public static final int SHOW_TIME_REMAINING = 1;

	private long startTime = System.currentTimeMillis();

	private int elements = 20;

	private String secondsMessage = "Tiempo estimado: {0} Segundos";
	private String minutesMessage = "Tiempo estimado: {0} Minutos";
	private String hoursMessage = "Tiempo estimado: {0} Horas";

	private int progress = 0;

	private FlexTable barFrame = new FlexTable();

	private Grid elementGrid;

	private Label remainLabel = new Label();

	private Label textLabel = new Label();

	private boolean showRemaining = false;
	private boolean showText = false;

	public ProgressBar(int elements, int options) {
		if ((options & SHOW_TIME_REMAINING) == SHOW_TIME_REMAINING)
			showRemaining = true;
		if ((options & SHOW_TEXT) == SHOW_TEXT)
			showText = true;

		this.elements = elements;

		// Styling
		remainLabel.setStyleName(AON.AON_CSS.progressbarRemaining());
		textLabel.setStyleName(AON.AON_CSS.progressbarText());

		// Initialize the progress elements
		elementGrid = new Grid(1, elements);
		elementGrid.setStyleName(AON.AON_CSS.progressbarInner());
		elementGrid.setCellPadding(0);
		elementGrid.setCellSpacing(0);

		for (int loop = 0; loop < elements; loop++) {
			Grid elm = new Grid(1, 1);
			elm.setHTML(0, 0, "");
			elm.setStyleName(AON.AON_CSS.progressbarBlankBar());
			elm.addStyleName(AON.AON_CSS.progressbarBar());
			elementGrid.setWidget(0, loop, elm);
		}

		Grid containerGrid = new Grid(1, 1);
		containerGrid.setCellPadding(0);
		containerGrid.setCellSpacing(0);
		containerGrid.setWidget(0, 0, elementGrid);
		containerGrid.setStyleName(AON.AON_CSS.progressbaOuter());

		int row = 0;
		if (showText)
			barFrame.setWidget(row++, 0, textLabel);
		barFrame.setWidget(row++, 0, containerGrid);
		if (showRemaining)
			barFrame.setWidget(row++, 0, remainLabel);

		barFrame.setWidth("100%");

		this.add(barFrame);

		setProgress(0);
	}

	public ProgressBar(int elements) {
		this(elements, 0);
	}

	public void setProgress(int percentage) {

		if (percentage > 100)
			percentage = 100;
		if (percentage < 0)
			percentage = 0;

		// Set the internal variable
		progress = percentage;

		// Update the elements in the progress grid to
		// reflect the status
		int completed = elements * percentage / 100;
		for (int loop = 0; loop < elements; loop++) {
			Grid elm = (Grid) elementGrid.getWidget(0, loop);
			if (loop < completed) {
				elm.setStyleName(AON.AON_CSS.progressbarFullbar());
				elm.addStyleName(AON.AON_CSS.progressbarBar());
			} else {
				elm.setStyleName(AON.AON_CSS.progressbarBlankBar());
				elm.addStyleName(AON.AON_CSS.progressbarBar());
			}
		}

		if (percentage > 0) {
			// Calculate the new time remaining
			long soFar = (System.currentTimeMillis() - startTime) / 1000;
			long remaining = soFar * (100 - percentage) / percentage;
			// Select the best UOM
			String remainText = secondsMessage;
			if (remaining > 120) {
				remaining = remaining / 60;
				remainText = minutesMessage;
				if (remaining > 120) {
					remaining = remaining / 60;
					remainText = hoursMessage;
				}
			}
			// Locate the position to insert out time remaining
			int pos = remainText.indexOf("{0}");
			if (pos >= 0) {
				String trail = "";
				if (pos + 3 < remainText.length())
					trail = remainText.substring(pos + 3);
				remainText = remainText.substring(0, pos) + remaining + trail;
			}
			// Set the label
			remainLabel.setText(remainText);
		} else {
			// If progress is 0, reset the start time
			startTime = System.currentTimeMillis();
		}
	}

	public int getProgress() {
		return (progress);
	}

	public String getText() {
		return this.textLabel.getText();
	}

	public void setText(String text) {
		this.textLabel.setText(text);
	}

	public String getHoursMessage() {
		return hoursMessage;
	}

	public void setHoursMessage(String hoursMessage) {
		this.hoursMessage = hoursMessage;
	}

	public String getMinutesMessage() {
		return minutesMessage;
	}

	public void setMinutesMessage(String minutesMessage) {
		this.minutesMessage = minutesMessage;
	}

	public String getSecondsMessage() {
		return secondsMessage;
	}

	public void setSecondsMessage(String secondsMessage) {
		this.secondsMessage = secondsMessage;
	}
	public void setCompletedMessage(String text) {
		this.remainLabel.setText("");
		this.textLabel.setText(text);
		this.textLabel.setStyleName(AON.AON_CSS.progressbarRemaining());
		
		for (int x = 0; x < elements; x ++) {
			elementGrid.getWidget(0, x).addStyleName(AON.AON_CSS.progressbarCompleted());
			
		}
	}

}
