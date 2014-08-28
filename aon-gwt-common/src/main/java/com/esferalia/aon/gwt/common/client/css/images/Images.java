package com.esferalia.aon.gwt.common.client.css.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Tree;

/**
 * Specifies the images that will be bundled for this Composite and specify
 * that tree's images should also be included in the same bundle.
 */
public interface Images extends ClientBundle, Tree.Resources {


	ImageResource aet();

	ImageResource draft();

	ImageResource card();

	ImageResource calc();

	ImageResource data();

	ImageResource euro();

	ImageResource _error();

	ImageResource person();

	ImageResource family();

	ImageResource calendar();

	ImageResource enterprise();

	ImageResource workplace();

	ImageResource ine();

	ImageResource costs();

	ImageResource salaries();

	ImageResource employee();

	ImageResource oldemployee();
	
	ImageResource view();

	ImageResource preview();

	ImageResource minimize();
	
	ImageResource maximize();
	
	ImageResource restore();
	
	ImageResource blank();

	ImageResource clipboard();

	@Source("noimage.png")
	ImageResource treeLeaf();
	
	ImageResource agreement();
	
	ImageResource agreement_warn();

	ImageResource agreement_error();

	ImageResource agreement_changed();

	ImageResource agreement_changed_warn();

	ImageResource agreement_changed_error();

	ImageResource concept();

	ImageResource payment();
	
	ImageResource deduction();
	
	ImageResource segsocial();
	
	ImageResource warn();
	
	ImageResource changed();

	ImageResource x();
	
	ImageResource f();

	ImageResource fx();
	
	@Source("aon-icon-statistics.png")
	ImageResource statistics();
	
	@Source("aon-icon-it.png")
	ImageResource itDatas();
	
	ImageResource gps();
	
	ImageResource expand();

	ImageResource expandall();

	ImageResource collapse();

	ImageResource collapseall();
	
	ImageResource pdfPreview();
	
	ImageResource redo();
	
	ImageResource undo();

	ImageResource aon_icon_row_s();

	ImageResource aon_icon_row_changed();
	
	ImageResource aon_icon_row_c();
	
	@Source("rich-calendar-button.png")
	ImageResource rich_calendar_button();
	
	ImageResource tooltip_top ();

	ImageResource segsocial_small();
}