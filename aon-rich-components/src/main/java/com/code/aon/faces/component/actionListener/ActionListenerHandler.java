package com.code.aon.faces.component.actionListener;

import java.io.IOException;
import java.io.Serializable;

import javax.el.ELException;
import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.component.ActionSource;
import javax.faces.component.ActionSource2;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.MethodExpressionActionListener;

import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.el.LegacyMethodBinding;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;
import com.sun.facelets.util.FacesAPI;

public class ActionListenerHandler extends TagHandler implements IRichFacesTags {

	private final TagAttribute method;

	public ActionListenerHandler(TagConfig config) {
		super(config);
		this.method = this.getRequiredAttribute(METHOD);
	}

	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {
		if (parent instanceof ActionSource) {
			ActionSource src = (ActionSource) parent;
			if (ComponentSupport.isNew(parent)) {
				MethodExpression methodExpr = this.method.getMethodExpression(
						ctx, null, FaceletUtil.ACTION_LISTENER_SIG);

				ActionListener listener;

				if (FacesAPI.getVersion() >= 12 && src instanceof ActionSource2) {
					listener = new MethodExpressionActionListener(
                            this.method.getMethodExpression(ctx, null, FaceletUtil.ACTION_LISTENER_SIG));
				} else {
					listener = new LegacyMethodActionListener(
							new LegacyMethodBinding(methodExpr));
				}

				src.addActionListener(listener);
			}
		} else {
			throw new TagException(this.tag,
					"Parent is not of type ActionSource, type is: " + parent);
		}
	}

	private static class LegacyMethodActionListener implements ActionListener,
			Serializable {

		private MethodBinding method;

		public LegacyMethodActionListener() {
		};

		public LegacyMethodActionListener(MethodBinding method) {
			this.method = method;
		}

		public void processAction(ActionEvent evt)
				throws AbortProcessingException {
			FacesContext facescontext = FacesContext.getCurrentInstance();
			this.method.invoke(facescontext, new Object[] { evt });
		}

	}

}
