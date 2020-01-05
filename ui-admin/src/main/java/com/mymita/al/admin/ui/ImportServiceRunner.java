/*
 * Copyright (c) 2020 Siemens GS IT EB CONF. All rights reserved.
 * This software is the confidential and proprietary information of Siemens GS IT EB CONF.
 * This file is part of SPICE.
 */
package com.mymita.al.admin.ui;

import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.mymita.al.importer.ImportListener;
import com.mymita.al.importer.ImportService;

/**
 * @author Andreas Höhmann
 * @since 0.0.1
 */
@Service
public class ImportServiceRunner {

  @Async
  <T> void run(final ImportService<T> importService, final InputStreamResource resource, final ImportListener<T> importListener) {
    importService.importData(resource, importListener);
  }

}
