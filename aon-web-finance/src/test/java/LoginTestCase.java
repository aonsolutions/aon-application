
import com.thoughtworks.selenium.*;
import java.util.regex.Pattern;

public class LoginTestCase extends SeleneseTestCase {
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
		selenium.click("//a[@id='j_id21:j_id24']/span");
		selenium.waitForPageToLoad("30000");
	}
}
