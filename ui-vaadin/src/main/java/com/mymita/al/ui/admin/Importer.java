package com.mymita.al.ui.admin;

import java.io.File;

import com.vaadin.ui.ProgressBar;

interface Importer<T> {
  void importData(final File file, final ProgressBar progressBar);
}