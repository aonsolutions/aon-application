package com.esferalia.aon.gwt.common.client.css.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Tree;

/**
 * Specifies the images that will be bundled for this Composite and specify
 * that tree's images should also be included in the same bundle.
 */
public interface Images extends ClientBundle, Tree.Resources {


	@Source("aon-icon-logo.png")
	ImageResource logo();

	ImageResource aet();

	ImageResource draft();

	ImageResource card();

	ImageResource calc();

	ImageResource data();

	ImageResource euro();

	ImageResource _error();

	@Source("aon-icon-info.png")
	ImageResource info();

	ImageResource person();

	ImageResource family();

	ImageResource calendar();

	ImageResource enterprise();
	
	ImageResource enterprises();

	@Source("aon-icon-paste.png")
	ImageResource aonIconPaste();
	
	@Source("laboral-calendar.png")
	ImageResource laboralCalendar();

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
	
	ImageResource parent();

	ImageResource agreement();
	
	ImageResource agreement_warn();

	ImageResource agreement_error();

	ImageResource agreement_parent();

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

	@Source("undo-all.png")
	ImageResource undo_all();

	ImageResource aon_icon_row_s();

	ImageResource aon_icon_row_parent();

	ImageResource aon_icon_row_changed();
	
	ImageResource aon_icon_row_c();
	
	@Source("aon-icon-calendar.png")
	ImageResource aon_icon_calendar();
	
	@Source("aon-icon-okwarning.png")
	ImageResource aon_icon_okwarning();

	ImageResource tooltip_top ();

	ImageResource segsocial_small();

	ImageResource cost();
	
	@Source("aon-icon-issue-closed.png")
	ImageResource aon_icon_issue_closed();

	@Source("aon-icon-issue-opened.png")
	ImageResource aon_icon_issue_opened();

	@Source("aon-icon-issue-opened-green.png")
	ImageResource aon_icon_issue_opened_green();

	@Source("aon-icon-issue-duplicated.png")
	ImageResource aon_icon_issue_duplicated();

	@Source("aon-icon-issue-reopened-break.png")
	ImageResource aon_icon_issue_reopened_break();
	
	@Source("aon-icon-issue-reopened-blue.png")
	ImageResource aon_icon_issue_reopened_blue();

	@Source("aon-icon-title.png")
	ImageResource aon_icon_issue_title();


}