package com.code.aon.ui.cms.util;

import java.util.Comparator;

import com.code.aon.cms.Image;

public class ImageComparator implements Comparator {
	  public int compare(Object obj1, Object obj2) {
		Image img1 = (Image) obj1;
		Image img2 = (Image) obj2;

	    int nameComp = img1.getName().compareToIgnoreCase(img2.getName());

	    return nameComp;
	  }
}
