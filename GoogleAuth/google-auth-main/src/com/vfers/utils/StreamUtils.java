package com.vfers.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * IO流工具类.
 * 
 * @author Letuer
 */
public class StreamUtils {

  /**
   * 关闭流.
   * 
   * @param stream IO流
   */
  public static void closeStream(Closeable stream) {
    if (stream != null) {
      try {
        stream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
