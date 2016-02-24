package com.mymita.al.ui.test;

import static com.codeborne.selenide.Selenide.$;

import org.testng.annotations.Test;

import com.codeborne.selenide.Selenide;

public class AppTest {

  @Test(enabled = false)
  public void canClick() {
    // System.setProperty("remote", "http://" + ahoehma + ":" + authentication.getAccessKey() + "@ondemand.saucelabs.com:80/wd/hub");
    Selenide.open("http://localhost:8080/main/person");
    $("div[location=\"name\"] input").val("Wagner").pressEnter();
  }
}
