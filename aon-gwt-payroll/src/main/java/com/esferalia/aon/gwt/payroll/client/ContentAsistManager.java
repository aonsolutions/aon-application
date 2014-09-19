package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ComboBox.Resources;
import com.esferalia.aon.gwt.common.client.widget.ComboBox.Resources.DropDownListStyle;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.VariableDescriptor;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class ContentAsistManager {

	public static interface Proposal {

		void render(SafeHtmlBuilder builder);

	}

	public static interface Resources extends CellList.Resources {

		public static Resources INSTANCE = GWT.create(Resources.class);

		interface ContentAssistListStyle extends CellList.Style {

		}

		@Override
		@Source({ CellList.Style.DEFAULT_CSS, "contentAssistCellList.css" })
		ContentAssistListStyle cellListStyle();

	}

	public static class VariableProposal implements Proposal {

		private final String name;
		private final VariableDescriptor descriptor;

		public VariableProposal(String name, VariableDescriptor descriptor) {
			this.name = name;
			this.descriptor = descriptor;
		}

		@Override
		public void render(SafeHtmlBuilder builder) {
			String value = descriptor.getValue();
			builder.append(TEMPLATE.variable(SafeHtmlUtils.fromString(name),
					SafeHtmlUtils.fromString(StringUtils.isBlank(value) ? ""
							: value)));
		}
	}

	private static class NoProposals implements Proposal {

		private String message;

		public NoProposals(String message) {
			this.message = message;
		}

		@Override
		public void render(SafeHtmlBuilder builder) {
			builder.append(TEMPLATE.none(SafeHtmlUtils.fromString(message)));
		}
	}

	private static class CaretCoordinates {
		int width;
		int height;

		public CaretCoordinates(int width, int height) {
			this.width = width;
			this.height = height;
		}
	}

	static interface Template extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<div class=\"item\" style=\"white-space: nowrap;\"><span >{0}</span></div>")
		SafeHtml none(SafeHtml msg);

		@SafeHtmlTemplates.Template("<div class=\"item\" astyle=\"white-space: nowrap;\"><span class=\"aon-icon-f aon-padding-left\">{0}()</span></div>")
		SafeHtml function(SafeHtml name, SafeHtml value);

		@SafeHtmlTemplates.Template("<div class=\"item\" style=\"white-space: nowrap;\"><span class=\"aon-icon-x aon-padding-left\">{0}: </span><span class=\"aon-bold\" >{1}</span></div>")
		SafeHtml variable(SafeHtml name, SafeHtml value);
	}

	private static final Template TEMPLATE = GWT.create(Template.class);

	private static class ProposalCell extends AbstractCell<Proposal> {

		@Override
		public void render(Context context,
				ContentAsistManager.Proposal contentAsist, SafeHtmlBuilder sb) {
			contentAsist.render(sb);

		}

	}

	private static final List<NoProposals> NO_PROPOSALS = Collections
			.singletonList(new NoProposals("Ninguna Propuesta"));

	private final PopupPanel popup;
	private final Element contentsAsistTitle;

	private final CellList<Proposal> cellList;
	private final SingleSelectionModel<Proposal> singleSelectionModel;

	private final SortedMap<String, List<Proposal>> proposalsMap;

	public ContentAsistManager() {
		popup = createPopup();

		// Popup hides when the user clicks outside of it.
		ScrollPanel scrollPanel = new ScrollPanel();

		// Create a cell to render each insight.
		ProposalCell cell = new ProposalCell();

		// Create a CellList that uses the cell.
		cellList = new CellList<ContentAsistManager.Proposal>(cell, Resources.INSTANCE);
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		// Add a selection model to handle user selection.
		singleSelectionModel = new SingleSelectionModel<ContentAsistManager.Proposal>();
		cellList.setSelectionModel(singleSelectionModel);

		singleSelectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						Proposal selected = singleSelectionModel
								.getSelectedObject();
						if (selected != null) {
							//Window.alert("You selected: " + selected);
						}
					}
				});

		// Add it to the scroll panel.
		scrollPanel.add(cellList);
		popup.add(scrollPanel);

		proposalsMap = new TreeMap<String, List<ContentAsistManager.Proposal>>();

		// create content asist mark
		
		contentsAsistTitle = createContentAssistTitle("contentsAsistTitle");

	}

	public void addValueBox(ValueBoxBase<String> box) {
		addEventsToValueBox(box);
	}

	public void cleanAll() {
		proposalsMap.clear();
	}

	public void addInsight(String preffix, ContentAsistManager.Proposal insight) {
		List<ContentAsistManager.Proposal> insights = proposalsMap.get(preffix);
		if (insights == null) {
			insights = new ArrayList<ContentAsistManager.Proposal>();
			proposalsMap.put(preffix, insights);
		}
		insights.add(insight);
	}

	public void addAll(ContextDescriptor ctxDescriptor) {
		for (String var : ctxDescriptor.getVariables()) {
			VariableDescriptor varDescriptor = ctxDescriptor.get(var);
			addInsight(var, new VariableProposal(var, varDescriptor));
		}
	}

    // --------------------------------------------------------------------

	/**
     * Create the PopupPanel that will hold the list of suggestions.
     *
     * @return the popup panel
     */
    protected PopupPanel createPopup() {
      PopupPanel p = new PopupPanel(true, false);
      p.setStyleName("gwt-SuggestBoxPopup");
      p.setPreviewingAllNativeEvents(true);
      return p;
    }

    // --------------------------------------------------------------------
	private void hidePopup() {
		this.popup.hide();
	}

	private void showPopup(final ValueBoxBase<String> box) {

		final CaretCoordinates caret = getCaretCoordinates(box);

		this.popup.setHeight((caret.height * 10) + "px");

		// Set the position of the popup right before it is shown.
		popup.setPopupPositionAndShow(new PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				int top = box.getAbsoluteTop();
				int left = box.getAbsoluteLeft();

				int boxWidth = box.getElement().getClientWidth();
				left += caret.width % boxWidth;

				int boxHeight = box.getElement().getClientHeight();
				top += (((int) caret.width / boxWidth) + 1) * caret.height;

				ContentAsistManager.this.popup.setPopupPosition(left, top);
			}
		});
	}

	private void hideProposals() {
		hidePopup();
	}

	private void showProposals4(ValueBoxBase<String> box) {
		int end = box.getCursorPos();
		int start = getProposalStart(box.getText().substring(0, end));

		String preffix = box.getText().substring(start, end);
		List<? extends Proposal> proposals = getProposals(preffix);
		cellList.setRowData(proposals);
		// select first proposal, always will exist one at least.
		singleSelectionModel.setSelected(proposals.get(0), true);
		showPopup(box);

	}

	private void hideContentAsistTitle() {
		contentsAsistTitle.getStyle().setVisibility(Visibility.HIDDEN);
	}

	private void showContentAsistTitle4(ValueBoxBase<String> box) {
		int top = box.getAbsoluteTop() - contentsAsistTitle.getClientHeight();
		int left = box.getAbsoluteLeft() - contentsAsistTitle.getClientWidth();
		contentsAsistTitle.getStyle().setTop(top, Unit.PX);
		contentsAsistTitle.getStyle().setLeft(left, Unit.PX);
		contentsAsistTitle.getStyle().setVisibility(Visibility.VISIBLE);
	}

	private void addEventsToValueBox(final ValueBoxBase<String> box) {

		class ValueBoxEvents implements KeyDownHandler, FocusHandler,
				BlurHandler {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				switch (event.getNativeKeyCode()) {
				case KeyCodes.KEY_UP:
					moveSelectionUp();
					break;
				case KeyCodes.KEY_DOWN:
					moveSelectionDow();
					break;
				case KeyCodes.KEY_SPACE:
					if (event.isControlKeyDown())
						showProposals4(box);
					break;
				case KeyCodes.KEY_ENTER:
				case KeyCodes.KEY_ESCAPE:
					hideProposals();
					break;

				default:
					break;
				}
			}

			// ----------------------------------------------------------------

			@Override
			public void onFocus(FocusEvent event) {
				showContentAsistTitle4(box);
			}

			// ----------------------------------------------------------------

			@Override
			public void onBlur(BlurEvent event) {
				hideContentAsistTitle();
			}
		}

		ValueBoxEvents valueBoxEvents = new ValueBoxEvents();
		box.addBlurHandler(valueBoxEvents);
		box.addFocusHandler(valueBoxEvents);
		box.addKeyDownHandler(valueBoxEvents);
	}

	private void moveSelectionUp() {
		int selectedRow = getSelectedRow();
		setSelectedRow(selectedRow-1);
	}

	private void moveSelectionDow() {
		int selectedRow = getSelectedRow();
		setSelectedRow(selectedRow+1);
	}
	
	private void setSelectedRow(int row){
		if ( row < 0 )
			return;
		List<Proposal> proposals = cellList.getVisibleItems();
		if ( row >= proposals.size())
			return;
		singleSelectionModel.setSelected(proposals.get(row), true);
	}
	
	private int getSelectedRow(){
		List<Proposal> proposals = cellList.getVisibleItems();
		Proposal proposal = singleSelectionModel.getSelectedObject();
		return proposals.indexOf(proposal);
	}

	private int getProposalStart(String text) {
		RegExp regExp = RegExp.compile("[a-zA-Z_][a-zA-Z0-9_]*$");
		MatchResult result = regExp.exec(text);
		return result != null ? result.getIndex() : text.length();
	}

	private List<? extends Proposal> getProposals(String str) {
		List<Proposal> proposals = new ArrayList<ContentAsistManager.Proposal>();

		SortedMap<String, List<ContentAsistManager.Proposal>> tailMap = proposalsMap
				.tailMap(str);
		for (Entry<String, List<ContentAsistManager.Proposal>> entry : tailMap
				.entrySet())
			if (entry.getKey().startsWith(str))
				proposals.addAll(entry.getValue());
			else
				break;

		return proposals.isEmpty() ? NO_PROPOSALS: proposals;
	}

	private ContentAsistManager.CaretCoordinates getCaretCoordinates(
			ValueBoxBase<String> box) {
		SpanElement span = Document.get().createSpanElement();
		String text = box.getText().substring(0, box.getCursorPos());
		span.appendChild(Document.get().createTextNode(text));

		span.setClassName(box.getStyleName());
		span.getStyle().setProperty("width", "auto");
		span.getStyle().setProperty("height", "auto");
		span.getStyle().setProperty("minHeight", "0");
		span.getStyle().setVisibility(Visibility.HIDDEN);
		span.getStyle().setPadding(0, Unit.PX); // no padding
		span.getStyle().setMargin(0, Unit.PX); // no margin
		span.getStyle().setBorderWidth(0, Unit.PX); // no border

		Document.get().getBody().appendChild(span);

		int textWidth = span.getOffsetWidth();
		int textHeight = span.getOffsetHeight();

		span.removeFromParent();

		return new CaretCoordinates(textWidth, textHeight);
	}
	
	private static Element createContentAssistTitle(String id) {
		Element el = Document.get().getElementById(id);
		if ( el != null )
			return el;
		
		el = Document.get().createSpanElement();

		el.setId(id);
		el.setClassName(AON.AON_ICON_LAMP);
		el.getStyle().setZIndex(Byte.MAX_VALUE);
		el.getStyle().setPosition(Position.ABSOLUTE);
		el.getStyle().setVisibility(Visibility.HIDDEN);
		el.setTitle("Asistente Disponible (Ctrl+Espacio)");
		
		Document.get().getBody().appendChild(el);
		
		return el;
	}

}