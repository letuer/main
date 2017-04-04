package com.vfers.utils;

/**
 * 字符串工具类.
 * 
 * @author Letuer
 */
public class StringUtils {

  /**
   * 字符串判空.
   * 
   * @param str str
   * @return null或字符串为空时返回true
   */
  public static boolean isEmpty(String str) {
    return str == null || str.isEmpty();
  }

  /**
   * 字符串判空.
   * 
   * @param str str
   * @return null或字符串为空或字符串只有空格时返回true
   */
  public static boolean isEmptyOrBlankOnly(String str) {
    return str == null || str.isEmpty() || str.trim().isEmpty();
  }
}
