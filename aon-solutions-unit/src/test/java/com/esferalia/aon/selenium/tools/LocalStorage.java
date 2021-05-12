package com.esferalia.aon.selenium.tools;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class LocalStorage {
  private JavascriptExecutor js;

  public LocalStorage(WebDriver webDriver) {
    this.js = (JavascriptExecutor) webDriver;
  }

  public void removeItem(String item) {
    js.executeScript(String.format(
        "window.localStorage.removeItem('%s');", item));
  }

  public boolean isItemPresent(String item) {
    return !(js.executeScript(String.format(
        "return window.localStorage.getItem('%s');", item)) == null);
  }

  public String getItem(String key) {
    return (String) js.executeScript(String.format(
        "return window.localStorage.getItem('%s');", key));
  }

  public String getKey(int key) {
    return (String) js.executeScript(String.format(
        "return window.localStorage.key('%s');", key));
  }

  public Long getLength() {
    return (Long) js.executeScript("return window.localStorage.length;");
  }

  public void setItem(String item, String value) {
    js.executeScript(String.format(
        "window.localStorage.setItem('%s','%s');", item, value));
  }

  public void clear() {
    js.executeScript(String.format("window.localStorage.clear();"));
  }
}