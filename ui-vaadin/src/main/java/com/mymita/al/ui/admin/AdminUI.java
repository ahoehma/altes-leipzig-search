package com.mymita.al.ui.admin;

import javax.servlet.annotation.WebServlet;

import com.mymita.al.ui.admin.view.ChristeningView;
import com.mymita.al.ui.admin.view.MarriageView;
import com.mymita.al.ui.admin.view.PersonView;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

@PreserveOnRefresh
@Theme(ValoTheme.THEME_NAME)
public class AdminUI extends UI {

  @WebServlet(value = "/admin/*", asyncSupported = true)
  @VaadinServletConfiguration(productionMode = false, ui = AdminUI.class)
  public static class Servlet extends VaadinServlet {
  }

  @Override
  protected void init(final VaadinRequest request) {
    getPage().setTitle("Altes Leipzig Suche - Administration");
    final PersonView personView = new PersonView();
    final MarriageView marriageView = new MarriageView();
    final ChristeningView christeningView = new ChristeningView();
    setContent(new TabSheet(personView, marriageView, christeningView));
  }

}