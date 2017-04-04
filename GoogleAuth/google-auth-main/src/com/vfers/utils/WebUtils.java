package com.vfers.utils;

import com.vfers.constant.WebConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * UI工具栏.
 * 
 * @author Letuer
 */
public class WebUtils {

  /**
   * 获取用户名.
   * 
   * @param request request
   * @return 用户名
   */
  public static String getUserName(HttpServletRequest request) {
    Object username = request.getSession().getAttribute(WebConstants.SESSION_KEY_USERNAME);
    return username == null ? null : String.valueOf(username);
  }

  /**
   * 用户是否已登录.
   * 
   * @param request request
   * @return boolean
   */
  public static boolean hasLogin(HttpServletRequest request) {
    Object username = request.getSession().getAttribute(WebConstants.SESSION_KEY_USERNAME);
    return username != null && !"".equals(username);
  }

  /**
   * 登录，向session中写入用户.
   * 
   * @param username 用户名
   * @param request request
   */
  public static void login(String username, HttpServletRequest request) {
    HttpSession oldSession = request.getSession(false);
    if (oldSession != null) {
      oldSession.invalidate();
    }
    HttpSession newSession = request.getSession(true);
    newSession.setAttribute(WebConstants.SESSION_KEY_USERNAME, username);
  }
}
