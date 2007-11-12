package com.code.aon.cms.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.cms.Config;
import com.code.aon.cms.ConfigDetail;
import com.code.aon.cms.Footer;
import com.code.aon.cms.FooterDetail;
import com.code.aon.cms.GenericPage;
import com.code.aon.cms.GenericPageDetail;
import com.code.aon.cms.Header;
import com.code.aon.cms.HeaderDetail;
import com.code.aon.cms.Language;
import com.code.aon.cms.Link;
import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.LinkCategoryDetail;
import com.code.aon.cms.LinkDetail;
import com.code.aon.cms.Menu;
import com.code.aon.cms.MenuOption;
import com.code.aon.cms.MenuOptionDetail;
import com.code.aon.common.dao.AliasWriter;

public class CMSAliasWriter {

	public static void main(String[] args) throws IOException {

		File file = new File("/AON-PROJECT/aon-cms/src/main/java/com/code/aon/cms/dao/ICMSAlias.java");
		String[] classes = new String[16]; 
		classes[0] = Language.class.getName();
		classes[1] = Config.class.getName();
		classes[2] = ConfigDetail.class.getName();
		classes[3] = GenericPage.class.getName();
		classes[4] = GenericPageDetail.class.getName();
		classes[5] = Menu.class.getName();
		classes[6] = MenuOption.class.getName();
		classes[7] = MenuOptionDetail.class.getName();
		classes[8] = Header.class.getName();
		classes[9] = HeaderDetail.class.getName();
		classes[10] = Footer.class.getName();
		classes[11] = FooterDetail.class.getName();
		classes[12] = Link.class.getName();
		classes[13] = LinkDetail.class.getName();
		classes[14] = LinkCategory.class.getName();
		classes[15] = LinkCategoryDetail.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.cms.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
		
	}
}