package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * GWT wrapper for the &lt;aon-chat&gt; web component defined in aon-web-aio.
 *
 * <p>Usage:
 * <pre>
 *   AonChat chat = new AonChat();
 *   chat.setDescription("Descripción del workflow");
 *   chat.setWorkflowsJson("[{\"user\":\"Ander\",\"comment\":\"Mensaje\",\"date\":\"01/01/2025\"}]");
 *   chat.onComment((user, comment, date) -> save(user, comment, date));
 *   panel.add(chat);
 * </pre>
 *
 * <p>To add messages after the widget is rendered, use {@link #addMessage(String, String, String)}.
 */
public class AonChat extends HTMLPanel {

    public interface CommentHandler {
        void onComment(String user, String comment, String date);
    }

    private final Element chatElement;

    public AonChat() {
        super("");
        chatElement = createCustomElement("aon-chat");
        getElement().getStyle().setProperty("display", "flex");
        getElement().getStyle().setProperty("flex-direction", "column");
        getElement().getStyle().setProperty("width", "100%");
    }

    public AonChat(boolean readonly) {
        this();
        setReadonly(readonly);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (chatElement.getParentElement() == null) {
            getElement().appendChild(chatElement);
        }
    }

    public void setReadonly(boolean readonly) {
        chatElement.setPropertyBoolean("readonly", readonly);
    }

    public void setDescription(String description) {
        chatElement.setPropertyString("description", description);
    }

    /**
     * Sets the initial workflow list from a JSON array string.
     * Each element may include: user, comment, date, and an optional action object
     * with title, color and icon fields.
     *
     * <p>Must be called before the widget is added to a panel.
     */
    public void setWorkflowsJson(String workflowsJson) {
        setWorkflowsNative(chatElement, workflowsJson);
    }

    /**
     * Registers a handler that fires when the user submits a new comment.
     * Can be called before or after the widget is added to a panel.
     */
    public void onComment(CommentHandler handler) {
        addCommentListener(chatElement, handler);
    }

    /**
     * Appends a plain message to the chat at runtime (after the widget is rendered).
     */
    public void addMessage(String user, String comment, String date) {
        addMessageNative(chatElement, user, comment, date);
    }

    /**
     * Appends a workflow action entry to the chat at runtime (after the widget is rendered).
     */
    public void addAction(String user, String date, String actionTitle, String actionColor, String actionIcon) {
        addActionNative(chatElement, user, date, actionTitle, actionColor, actionIcon);
    }

    private static native Element createCustomElement(String tag) /*-{
        return $doc.createElement(tag);
    }-*/;

    private static native void setWorkflowsNative(Element element, String workflowsJson) /*-{
        try {
            element.workflows = JSON.parse(workflowsJson);
        } catch (e) {
            element.workflows = [];
        }
    }-*/;

    private static native void addMessageNative(Element element, String user, String comment, String date) /*-{
        var workflow = { user: user, comment: comment, date: date };
        if (typeof element.buildWorkflow === 'function') {
            element.buildWorkflow(workflow);
        }
    }-*/;

    private static native void addActionNative(Element element, String user, String date,
            String actionTitle, String actionColor, String actionIcon) /*-{
        var workflow = {
            user: user,
            date: date,
            action: { title: actionTitle, color: actionColor, icon: actionIcon }
        };
        if (typeof element.buildWorkflow === 'function') {
            element.buildWorkflow(workflow);
        }
    }-*/;

    private static native void addCommentListener(Element element, CommentHandler handler) /*-{
        element.addEventListener('comment', function(event) {
            var detail = event.detail || {};
            handler.@com.esferalia.aon.gwt.common.client.widget.solutions.AonChat.CommentHandler::onComment(*)(
                detail.user   || '',
                detail.comment || '',
                detail.date   || ''
            );
        });
    }-*/;
}
