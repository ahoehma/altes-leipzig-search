package com.mymita.al.ui.test;

import static com.codeborne.selenide.Selenide.$;

import org.testng.annotations.Test;

import com.codeborne.selenide.Selenide;

public class AppTest {

  @Test
  public void canClick() {
    Selenide.open("http://localhost:8080/main/person");
    $("div[location=\"name\"] input").val("Wagner").pressEnter();
  }
}
