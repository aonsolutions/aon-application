
import com.thoughtworks.selenium.*;
import java.util.regex.Pattern;

public class LoginTestCase extends SeleneseTestCase {
	public void setUp() throws Exception {
		setUp("http://dev.esferalia.com/", "*chrome");
	}
	public void testLoginTestCase() throws Exception {
		selenium.open("/aon-desktop/");
		selenium.type("j_username_view", "aon-user");
		selenium.type("j_password", "GGhh%123");
		selenium.click("login_btn");
		selenium.waitForPageToLoad("30000");
		selenium.open("/aon-desktop/");
		selenium.click("//a[@id='j_id24:j_id27']/span");
		selenium.waitForPageToLoad("30000");
	}
}
