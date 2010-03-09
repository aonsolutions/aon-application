
import com.thoughtworks.selenium.*;
import java.util.regex.Pattern;

public class ProductsTestCase extends SeleneseTestCase {
	public void setUp() throws Exception {
		setUp("http://dev.esferalia.com/", "*chrome");
	}

	public void testProductsTestCase() throws Exception {
		selenium.setSpeed("1000");
		selenium.open("/aon-finance/");
		selenium.type("j_username_view", "aon-user");
		selenium.type("j_password", "GGhh%123");
		selenium.click("login_btn");
		selenium.waitForPageToLoad("30000");
		selenium.click("aonContent:j_id28:sales");
		selenium.click("aonContent:salesMenu:salesItem");
		selenium.click("aonContent:itemSearch:aonToolbar-reset");
		selenium.type("aonContent:itemForm:Item_product_code", "666");
		selenium.type("aonContent:itemForm:Item_product_name", "ProductsTestCase");
		selenium.type("aonContent:itemForm:Item_product_detail", "ProductsTestCase for Selenium");
		selenium.click("aonContent:itemForm:aonToolbar-save");
		selenium.click("aonContent:itemForm:aonToolbar-back");
		selenium.click("aonContent:itemList:itemData:0:itemData-selectButton");
		selenium.click("aonContent:itemForm:aonToolbar-remove");
		selenium.click("cb_aonToolbar-remove-yes");
		selenium.click("aonContent:j_id583:sales");
		selenium.click("//a[@id='j_id21:j_id24']/span");
		selenium.waitForPageToLoad("30000");
	}
}
