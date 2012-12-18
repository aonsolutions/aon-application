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
	ImageResource draft();

	ImageResource data();

	ImageResource calendar();

	ImageResource enterprise();

	ImageResource workplace();

	ImageResource costs();

	ImageResource salaries();

	ImageResource employee();

	ImageResource oldemployee();
	
	ImageResource view();

	ImageResource minimize();
	
	ImageResource maximize();
	
	ImageResource restore();

	@Source("noimage.png")
	ImageResource treeLeaf();
}