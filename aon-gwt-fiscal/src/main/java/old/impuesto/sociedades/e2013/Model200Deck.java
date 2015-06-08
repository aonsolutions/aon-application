package old.impuesto.sociedades.e2013;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Model200Deck extends ResizeComposite {

	interface Model200DeckBinder extends UiBinder<Widget, Model200Deck> {
	}

	private static final Model200DeckBinder BINDER = GWT
			.create(Model200DeckBinder.class);

	@UiField
	DeckPanel pagesPanel;
	@UiField
	Page00 page00;
	@UiField
	Page01 page01;
	@UiField
	Page02 page02;
	@UiField
	Page03 page03;
	@UiField
	Page04 page04;
	@UiField
	Page05 page05;
	@UiField
	Page06 page06;
	@UiField
	Page07 page07;
	@UiField
	Page08 page08;
	@UiField
	Page09 page09;
	@UiField
	Page10 page10;
	@UiField
	Page11 page11;
	@UiField
	Page12 page12;
	@UiField
	Page13 page13;
	@UiField
	Page14 page14;
	
	@UiField
	ErrorPage errorPage;
	
	public Model200Deck() {
		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
	}


	public void dump(Mod200Object mod200) {
		page00.dump(mod200);
		page01.dump(mod200);
		page02.dump(mod200);
		page03.dump(mod200);
		page04.dump(mod200);
		page05.dump(mod200);
		page06.dump(mod200);
		page07.dump(mod200);
		page08.dump(mod200);
		page09.dump(mod200);
		page10.dump(mod200);
		page11.dump(mod200);
		page12.dump(mod200);
		page13.dump(mod200);
		page14.dump(mod200);
	}
	
	public Widget getPage(int i) {
		Widget[] pages = new Widget[]{page00,page01,page02,page03,page04,page05
				,page06,page07,page08,page09,page10,page11,page12,page13,page14}; 
		return pages[i];
	}
	
}
