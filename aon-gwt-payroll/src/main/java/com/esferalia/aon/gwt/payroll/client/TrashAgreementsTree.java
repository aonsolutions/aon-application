package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class TrashAgreementsTree extends Composite implements KeyDownHandler {
	
	public static class OverlayImagesImpl {

		interface Template extends SafeHtmlTemplates {
			@SafeHtmlTemplates.Template("<img onload='this.__gwtLastUnhandledEvent=\"load\";' src='{0}' "
					+ "style='{1}' border='0'>")
			SafeHtml image(SafeUri clearImage, SafeStyles style);
		}

		interface DraggableTemplate extends SafeHtmlTemplates {
			@SafeHtmlTemplates.Template("<img onload='this.__gwtLastUnhandledEvent=\"load\";' src='{0}' "
					+ "style='{1}' border='0' draggable='true'>")
			SafeHtml image(SafeUri clearImage, SafeStyles style);
		}

		private static final SafeUri CLEARIMAGE = UriUtils
				.fromTrustedString(GWT.getModuleBaseURL() + "clear.cache.gif");
		private static final Template TEMPLATE = GWT.create(Template.class);
		private static final DraggableTemplate DRAGGABLE_TEMPLATE = GWT
				.create(DraggableTemplate.class);

		public static SafeHtml getSafeHtml(int left, int top, int width,
				int height, SafeUri... uris) {
			return getSafeHtml(left, top, width, height, false, uris);
		}

		public static SafeHtml getSafeHtml(int left, int top, int width,
				int height, boolean isDraggable, SafeUri... uris) {

			StringBuffer background = new StringBuffer();
			for (SafeUri uri : uris) {
				if (background.length() > 0)
					background.append(", ");
				background.append("url(" + uri.asString() + ") " + "no-repeat "
						+ (-left + "px ") + (-top + "px"));
			}

			SafeStylesBuilder builder = new SafeStylesBuilder();

			builder.width(width, Unit.PX).height(height, Unit.PX)
					.trustedNameAndValue("background", background.toString());

			if (!isDraggable) {
				return TEMPLATE.image(CLEARIMAGE, SafeStylesUtils
						.fromTrustedString(builder.toSafeStyles().asString()));
			} else {
				return DRAGGABLE_TEMPLATE.image(CLEARIMAGE, SafeStylesUtils
						.fromTrustedString(builder.toSafeStyles().asString()));
			}
		}
	}
	
	interface Listener {
		
		void onAgreementDelete4Ever(Agreement agreement);
		
		void onAgreementRestore(Agreement agreement);
		
		void onTreeItemSelected(SelectionEvent<TreeItem> event);
		
		void getAgreements();
	}
	
	private static final Images IMAGES = GWT.create(Images.class);

	private static final ImageResource RESOURCES[][][] = {
			{ { IMAGES.agreement(), IMAGES.agreement_warn() },
				{ IMAGES.agreement_error(), IMAGES.agreement_error() } },
			{
					{ IMAGES.agreement_changed(),
							IMAGES.agreement_changed_warn() },
					{ IMAGES.agreement_changed_error(),
							IMAGES.agreement_changed_error() } } };

	private static AgreementsTreeUiBinder uiBinder = GWT.create(AgreementsTreeUiBinder.class);

	interface AgreementsTreeUiBinder extends UiBinder<Widget, TrashAgreementsTree> {}
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	Tree tree;
	
	private List<Listener> listeners;
	
	private DomainEmployeesServiceAsync employeesServiceAsync = DomainEmployeesServiceAsync.newInstance();
	private DomainEnterprisesServiceAsync enterpriseService = DomainEnterprisesServiceAsync.newInstance();

	public TrashAgreementsTree() {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.listeners = new ArrayList<Listener>();
		this.tree.addKeyDownHandler(this);
		
	}
	
	// ---------------------------------------------------- ContextMenu

	@Override
	public void onKeyDown(KeyDownEvent event) {
		int keyCode = event.getNativeKeyCode();
		Object object = tree.getSelectedItem().getUserObject();
		
		if(keyCode == KeyCodes.KEY_DELETE && object instanceof Agreement) {
			onAgreementDelete4Ever((Agreement) object);
		}
	}
	
	private void onAgreementDelete4Ever(Agreement agreement) {
		for(Listener listener : listeners)
			listener.onAgreementDelete4Ever(agreement);
	}
	
	// ---------------------------------------------------- UiHandler

	@UiHandler("tree")
	void onTreeItemSelected(SelectionEvent<TreeItem> event) {
		for(Listener listener : listeners)
			listener.onTreeItemSelected(event);
	}
	
	// ---------------------------------------------------- Listener
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	// ---------------------------------------------------- Auxiliar Methods (Getters)

	public DomainEnterprisesServiceAsync getEnterpriseService() {
		return enterpriseService;
	}
	
	public DomainEmployeesServiceAsync getEmployeesService() {
		return employeesServiceAsync;
	}
	
	public Tree getTree() {
		return tree;
	}
	
	public TreeItem getSelectedItem() {
		return tree.getSelectedItem();
	}
	
	public void clearTree() {
		tree.clear();
	}
	
	// ---------------------------------------------------- Images Resources

	public static ImageResource getImageResource(boolean changes,
			boolean errors, boolean warns) {
		return RESOURCES[changes ? 1 : 0][errors ? 1 : 0][warns ? 1 : 0];
	}

	static SafeHtml imageItemSafeHtml(ImageResource imageProto, String title) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(AbstractImagePrototype.create(imageProto).getSafeHtml());
		if (title != null)
			builder.appendEscaped(" " + title);
		return builder.toSafeHtml();
	}
	
	static SafeHtml imageItemSafeHtml(String title, ImageResource imageProto,
			ImageResource... imageMarks) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();

		List<SafeUri> uris = new ArrayList<SafeUri>();
		uris.add(imageProto.getSafeUri());
		for (ImageResource imageMark : imageMarks)
			uris.add(imageMark.getSafeUri());

		builder.append(OverlayImagesImpl.getSafeHtml(imageProto.getLeft(),
				imageProto.getTop(), imageProto.getWidth(),
				imageProto.getHeight(), uris.toArray(new SafeUri[uris.size()])));

		if (title != null)
			builder.appendEscaped(" " + title);
		
		return builder.toSafeHtml();
	}
	
	public static ImageResource getImageResource(Agreement agreement) {
		return agreement.getDomain() == 0 ?
				IMAGES.logo() :
				RESOURCES[0][0][agreement.hasLevelsWithoutCategories() ? 1 : 0];
	}
	
	public static ImageResource getImageResource(Agreement agreement, Integer actualDomain) {
		if (AonNumberUtils.equals(0, agreement.getDomain()))
			return IMAGES.aon_icon_row_s();
		else if(AonNumberUtils.notEquals(actualDomain, agreement.getDomain()))
			return IMAGES.aon_icon_row_c();
		else 
			return IMAGES.rowSelector();
	}

}
