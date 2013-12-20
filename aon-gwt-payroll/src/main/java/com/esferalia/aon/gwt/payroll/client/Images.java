package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.user.client.ui.Tree;

/**
 * Specifies the images that will be bundled for this Composite and specify
 * that tree's images should also be included in the same bundle.
 */
public interface Images extends ClientBundle, Tree.Resources {

	ImageResource aet();

	ImageResource draft();

	ImageResource calc();

	ImageResource data();

	ImageResource euro();

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
	
	ImageResource agreement_changed();

	ImageResource payment();
	
	ImageResource deduction();
	
	ImageResource segsocial();
	
	ImageResource warn();
	
}