package com.mymita.al.ui.admin.importer;

import java.io.File;

import com.vaadin.ui.ProgressBar;

public interface Importer<T> {
  void importData(File file, ProgressBar progressBar);
}