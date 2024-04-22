package org.aonsolutions;

import java.nio.file.Paths;
import java.util.regex.Pattern;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;

public class App {             
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();
            page.navigate("http://ayudat.aonsolutions.net");
            page.getByPlaceholder("Usuario").fill("aon");
            page.getByPlaceholder("Contraseña").fill("t3st");
            page.locator("id=login_btn").click(); 
         
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("example.png")));
            
            page.locator("id=aonContent:mainForm:officeDomainEnterprise").click(); 
            
            System.out.println(page.title());
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("example2.png")));

        }
    }
}
