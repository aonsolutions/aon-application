package com.esferalia.aon.tests;

import com.thoughtworks.selenium.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.regex.Pattern;

public class DesktopTestCase extends SeleneseTestCase {	

	@Before
	public void setUp() throws Exception {
		selenium = new AonSelenium("localhost", 4444, "*chrome", "http://test.esferalia.org/");
		selenium.start();

		selenium.open("/aon-desktop/");
		selenium.type("j_username_view", "test");
		selenium.type("j_password", "demo");
		selenium.click("login_btn");
		selenium.waitForPageToLoad("30000");
	}
	

	@Test
	public void testNotes() throws Exception {
		selenium.open("/aon-desktop/");
		selenium.click("//a[@id='aonContent:j_id31:j_id44']/img");
		selenium.click("aonContent:noteList:noteToolbar-reset");
		selenium.type("aonContent:noteForm:Note_subject", "Selenium Test");
		selenium.type("aonContent:noteForm:Note_note", "Esto es un test de selenium...\n\nSaludos,\n\n Grupo de desarrollo de esferalia NETWORKS S.A");
		selenium.click("aonContent:noteForm:noteToolbar-save");
		selenium.click("aonContent:noteForm:noteToolbar-search");
		selenium.type("aonContent:noteSearch:Note_subject", "Selenium*");
		selenium.click("search");
		selenium.click("aonContent:noteList:noteData:0:j_id804");
		selenium.click("aonContent:noteForm:noteToolbar-remove");
		selenium.click("cb_noteToolbar-remove-yes");
	}
	
	public void testFavorites() throws Exception {
		selenium.open("/aon-desktop/");
		selenium.click("//a[@id='aonContent:j_id31:j_id54']/img");
		selenium.click("aonContent:favoriteList:favorite_category");
		selenium.click("aonContent:favoriteCategoryList:favoriteCategoryData:favoriteCategoryData-reset");
		selenium.type("aonContent:favoriteCategoryList:favoriteCategoryData:j_id483", "Selenium Category");
		selenium.click("aonContent:favoriteCategoryList:favoriteCategoryData:favoriteCategoryData-new-save");
		selenium.click("aonContent:favoriteCategoryList:favorite_list");
		selenium.click("aonContent:favoriteList:favoriteToolbar-reset");
		selenium.type("aonContent:favoriteForm:Favorite_description", "Selenium Test");
		selenium.type("aonContent:favoriteForm:Favorite_url", "http://dev.esferalia.com");
		selenium.click("aonContent:favoriteForm:favoriteToolbar-save");
		selenium.click("aonContent:favoriteForm:favoriteToolbar-search");
		selenium.type("aonContent:favoriteSearch:Favorite_description", "Selenium*");
		selenium.click("search");
		selenium.click("aonContent:favoriteList:favoriteData:0:favoriteData-selectButton");
		selenium.click("aonContent:favoriteForm:favoriteToolbar-remove");
		selenium.click("cb_favoriteToolbar-remove-yes");
		selenium.click("aonContent:favoriteList:favorite_category");
		selenium.click("aonContent:favoriteCategoryList:favoriteCategoryData:0:favoriteCategoryData-selectColumn");
		selenium.click("aonContent:favoriteCategoryList:favoriteCategoryData:0:favoriteCategoryData-remove");
		selenium.click("aonContent:favoriteCategoryList:favoriteCategoryData:0:cb_favoriteCategoryData-remove-yes");
		selenium.click("aonContent:favoriteCategoryList:favorite_list");
	}

	public void testDocuments() throws Exception {
		selenium.open("/aon-desktop/");
		selenium.click("//a[@id='aonContent:j_id31:j_id64']/img");
		selenium.click("aonContent:identityForm:category");
		selenium.click("aonContent:categoryList:categoryData:categoryData-reset");
		selenium.type("aonContent:categoryList:categoryData:Category_name-New", "Selenium Category");
		selenium.click("aonContent:categoryList:categoryData:categoryData-new-save");
		selenium.click("aonContent:categoryList:categoryToolbar-back");
		/*
		selenium.click("aonContent:identityForm:corporateIdentityAttachData:corporateIdentityAttachData-reset");
		selenium.select("aonContent:identityForm:corporateIdentityAttachData:corporateIdentityAttach_scope-New", "label=GENERAL");
		selenium.select("aonContent:identityForm:corporateIdentityAttachData:corporateIdentityAttach_category-New", "label=Selenium Category");
		selenium.type("aonContent:identityForm:corporateIdentityAttachData:j_id748:file", "/etc/hosts");
		selenium.type("aonContent:identityForm:corporateIdentityAttachData:corporateIdentityAttach_description-New", "Selenium");
		selenium.click("aonContent:identityForm:corporateIdentityAttachData:corporateIdentityAttachData-new-save");
		selenium.click("aonContent:identityForm:corporateIdentityAttachToolbar-search");
		selenium.type("aonContent:corporateIdentityAttachSearch:RegistryAttachment_description", "Selenium");
		selenium.click("search");
		selenium.click("aonContent:identityForm:corporateIdentityAttachData:0:corporateIdentityAttachData-selectColumn");
		selenium.click("aonContent:identityForm:corporateIdentityAttachData:0:corporateIdentityAttachData-remove");
		selenium.click("aonContent:identityForm:corporateIdentityAttachData:0:cb_corporateIdentityAttachData-remove-yes");
		*/
		selenium.click("aonContent:identityForm:category");
		selenium.click("aonContent:categoryList:categoryData:0:categoryData-selectColumn");
		selenium.click("aonContent:categoryList:categoryData:0:categoryData-remove");
		selenium.click("aonContent:categoryList:categoryData:0:cb_categoryData-remove-yes");
		selenium.click("aonContent:categoryList:categoryToolbar-back");
	}
	
	@After
	public void tearDown() throws Exception {
		selenium.click("//a[@id='j_id24:j_id25']/span");
		selenium.waitForPageToLoad("30000");

		selenium.stop();
	}
}
