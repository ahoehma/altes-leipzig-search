/**
 *
 */
package com.mymita.al.ui.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author Andreas Höhmann
 */
public final class ConcurrentUtils {

  public static Runnable wrap(final Runnable r) {
    final RequestAttributes callerRequestAttributes = RequestContextHolder.getRequestAttributes();
    return new Runnable() {

      @Override
      public void run() {
        final RequestAttributes currentRequestAttributes = RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(callerRequestAttributes, true);
        try {
          r.run();
        } finally {
          RequestContextHolder.setRequestAttributes(currentRequestAttributes);
        }
      }
    };
  }
}
