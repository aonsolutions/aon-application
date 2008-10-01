package com.code.aon.ui.form.listener;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import com.code.aon.ui.util.AonUtil;

/**
 * WindowCloseListener is used to capture the window closing event.
 */
public class WindowCloseListener implements ActionListener {

	/** The Constant WINDOW_CLOSE_NAME. */
	private static final String WINDOW_CLOSE_NAME = "windowClose";

	/** The Constant SCRIPT. */
	private static final String SCRIPT = "<script type='text/javascript'> self.close(); </script>";
	
	/** The close window. */
	private boolean closeWindow;	
	
	/**
	 * Process action.
	 * 
	 * @param actionEvent the action event
	 * 
	 * @throws AbortProcessingException the abort processing exception
	 */
	public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
		WindowCloseListener wcl = (WindowCloseListener) AonUtil.getRegisteredBean(WINDOW_CLOSE_NAME);
		wcl.setCloseWindow(true);
	}

	/**
	 * Checks if is close window.
	 * 
	 * @return true, if is close window
	 */
	public boolean isCloseWindow() {
		return this.closeWindow;
	}

	/**
	 * Sets the close window.
	 * 
	 * @param closeWindow the close window
	 */
	public void setCloseWindow(boolean closeWindow) {
		this.closeWindow = closeWindow;
	}

	/**
	 * Gets the script.
	 * 
	 * @return the script
	 */
	public String getScript() {
		this.closeWindow = false;
		return SCRIPT;
	}
	
}
