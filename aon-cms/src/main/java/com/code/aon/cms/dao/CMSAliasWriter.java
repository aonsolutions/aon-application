package com.code.aon.cms.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.cms.Activity;
import com.code.aon.cms.ActivityConfig;
import com.code.aon.cms.ActivityDetail;
import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumCategory;
import com.code.aon.cms.AlbumCategoryDetail;
import com.code.aon.cms.AlbumConfig;
import com.code.aon.cms.AlbumDetail;
import com.code.aon.cms.AlbumImage;
import com.code.aon.cms.AlbumImageDetail;
import com.code.aon.cms.Article;
import com.code.aon.cms.ArticleCategory;
import com.code.aon.cms.ArticleCategoryDetail;
import com.code.aon.cms.ArticleConfig;
import com.code.aon.cms.ArticleDetail;
import com.code.aon.cms.ArticleDocument;
import com.code.aon.cms.ArticleDocumentDetail;
import com.code.aon.cms.ArticleRelated;
import com.code.aon.cms.Banner;
import com.code.aon.cms.BannerCategory;
import com.code.aon.cms.BannerCategoryDetail;
import com.code.aon.cms.BannerDetail;
import com.code.aon.cms.Brand;
import com.code.aon.cms.BrandDetail;
import com.code.aon.cms.Bulletin;
import com.code.aon.cms.BulletinArticle;
import com.code.aon.cms.BulletinDetail;
import com.code.aon.cms.BulletinEmail;
import com.code.aon.cms.Company;
import com.code.aon.cms.CompanyActivity;
import com.code.aon.cms.Config;
import com.code.aon.cms.ConfigDetail;
import com.code.aon.cms.Diary;
import com.code.aon.cms.DiaryDetail;
import com.code.aon.cms.DirectAccess;
import com.code.aon.cms.DirectAccessDetail;
import com.code.aon.cms.DirectAccessGroup;
import com.code.aon.cms.DirectAccessGroupDetail;
import com.code.aon.cms.Download;
import com.code.aon.cms.DownloadCategory;
import com.code.aon.cms.DownloadCategoryDetail;
import com.code.aon.cms.DownloadConfig;
import com.code.aon.cms.DownloadDetail;
import com.code.aon.cms.Faq;
import com.code.aon.cms.FaqCategory;
import com.code.aon.cms.FaqCategoryDetail;
import com.code.aon.cms.FaqConfig;
import com.code.aon.cms.FaqDetail;
import com.code.aon.cms.Footer;
import com.code.aon.cms.FooterBannerCategory;
import com.code.aon.cms.FooterDetail;
import com.code.aon.cms.GenericPage;
import com.code.aon.cms.GenericPageDetail;
import com.code.aon.cms.Header;
import com.code.aon.cms.HeaderDetail;
import com.code.aon.cms.HiruConfig;
import com.code.aon.cms.HiruCourse;
import com.code.aon.cms.HiruCourseDetail;
import com.code.aon.cms.HiruOrganizerCentre;
import com.code.aon.cms.Language;
import com.code.aon.cms.Link;
import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.LinkCategoryDetail;
import com.code.aon.cms.LinkConfig;
import com.code.aon.cms.LinkDetail;
import com.code.aon.cms.Menu;
import com.code.aon.cms.MenuOption;
import com.code.aon.cms.MenuOptionDetail;
import com.code.aon.cms.ModularPage;
import com.code.aon.cms.ModularPageDetail;
import com.code.aon.cms.ModularPageOption;
import com.code.aon.cms.ModularPageOptionDetail;
import com.code.aon.cms.Product;
import com.code.aon.cms.ProductCategory;
import com.code.aon.cms.ProductCategoryConfig;
import com.code.aon.cms.ProductCategoryDetail;
import com.code.aon.cms.ProductDetail;
import com.code.aon.cms.Section;
import com.code.aon.cms.Sidebar;
import com.code.aon.cms.SidebarOption;
import com.code.aon.cms.SidebarOptionDetail;
import com.code.aon.cms.SportCareerPath;
import com.code.aon.cms.SportCategory;
import com.code.aon.cms.SportCategoryDetail;
import com.code.aon.cms.SportClub;
import com.code.aon.cms.SportCoach;
import com.code.aon.cms.SportConfig;
import com.code.aon.cms.SportNationality;
import com.code.aon.cms.SportNationalityDetail;
import com.code.aon.cms.SportPlayer;
import com.code.aon.cms.SportPosition;
import com.code.aon.cms.SportPositionDetail;
import com.code.aon.cms.SportSeason;
import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.DAOConstantsWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;

public class CMSAliasWriter {

	public static void main(String[] args) throws IOException {

		File file = new File("/AON-PROJECT/aon-cms/src/main/java/com/code/aon/cms/dao/ICMSAlias.java");
		String[] classes = new String[] { 
			Language.class.getName(),
			Config.class.getName(),
			ConfigDetail.class.getName(),
			GenericPage.class.getName(),
			GenericPageDetail.class.getName(),
			Menu.class.getName(),
			MenuOption.class.getName(),
			MenuOptionDetail.class.getName(),
			Header.class.getName(),
			HeaderDetail.class.getName(),
			Footer.class.getName(),
			FooterDetail.class.getName(),
			Link.class.getName(),
			LinkDetail.class.getName(),
			LinkCategory.class.getName(),
			LinkCategoryDetail.class.getName(),
			Faq.class.getName(),
			FaqDetail.class.getName(),
			FaqCategory.class.getName(),
			FaqCategoryDetail.class.getName(),
			Brand.class.getName(),
			BrandDetail.class.getName(),
			ProductCategory.class.getName(),
			ProductCategoryDetail.class.getName(),
			Product.class.getName(),
			ProductDetail.class.getName(),
			Article.class.getName(),
			ArticleDetail.class.getName(),
			ArticleCategory.class.getName(),
			ArticleCategoryDetail.class.getName(),
			Banner.class.getName(),
			BannerDetail.class.getName(),
			BannerCategory.class.getName(),
			BannerCategoryDetail.class.getName(),
			Download.class.getName(),
			DownloadDetail.class.getName(),
			DownloadCategory.class.getName(),
			DownloadCategoryDetail.class.getName(),
			AlbumCategory.class.getName(),
			AlbumCategoryDetail.class.getName(),
			Album.class.getName(),
			AlbumDetail.class.getName(),
			AlbumImage.class.getName(),
			AlbumImageDetail.class.getName(),
			Sidebar.class.getName(),
			SidebarOption.class.getName(),
			SidebarOptionDetail.class.getName(),
			Section.class.getName(),		
			ModularPage.class.getName(),
			ModularPageOption.class.getName(),
			ModularPageOptionDetail.class.getName(),		
			DirectAccessGroup.class.getName(),
			DirectAccessGroupDetail.class.getName(),
			DirectAccess.class.getName(),
			DirectAccessDetail.class.getName(),
			FaqConfig.class.getName(),
			LinkConfig.class.getName(),
			AlbumConfig.class.getName(),
			ArticleConfig.class.getName(),
			ModularPageDetail.class.getName(),
			ArticleRelated.class.getName(),
			ArticleDocument.class.getName(),
			ArticleDocumentDetail.class.getName(),
			DownloadConfig.class.getName(),
			Company.class.getName(),
			Activity.class.getName(),
			ActivityDetail.class.getName(),
			CompanyActivity.class.getName(),
			Bulletin.class.getName(),
			BulletinDetail.class.getName(),
			BulletinArticle.class.getName(),
			BulletinEmail.class.getName(),
			FooterBannerCategory.class.getName(),
			Diary.class.getName(),
			DiaryDetail.class.getName(),
			HiruOrganizerCentre.class.getName(),
			HiruCourse.class.getName(),
			HiruCourseDetail.class.getName(),
			HiruConfig.class.getName(),
			ProductCategoryConfig.class.getName(),
			SportCategory.class.getName(),
			SportCategoryDetail.class.getName(),
			SportClub.class.getName(),
			SportPosition.class.getName(),
			SportPositionDetail.class.getName(),
			SportNationality.class.getName(),
			SportNationalityDetail.class.getName(),
			SportSeason.class.getName(),
			SportPlayer.class.getName(),
			SportCareerPath.class.getName(),
			SportCoach.class.getName(),
			SportConfig.class.getName(),
			ActivityConfig.class.getName() 
		};
		HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName());
		AliasWriter writer = new AliasWriter("com.code.aon.cms.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}