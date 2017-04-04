package com.vfers.controller;

import com.google.zxing.WriterException;
import com.vfers.constant.WebConstants;
import com.vfers.model.system.EnumRequestError;
import com.vfers.model.user.User;
import com.vfers.model.user.UserCache;
import com.vfers.utils.StreamUtils;
import com.vfers.utils.StringUtils;
import com.vfers.utils.WebUtils;
import com.vfers.utils.google.auth.GoogleAuthenticator;
import com.vfers.utils.google.auth.QrCodeUtils;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户Controller.
 * 
 * @author Letuer
 */
@Controller
@RequestMapping("/user")
public class UserController {

  /**
   * index.
   * 
   * @param request request
   */
  @RequestMapping("")
  @ResponseBody
  public void index(HttpServletRequest request) {
    
  }

  /**
   * 用户注册.
   * 
   * @param name 用户名
   * @param password 密码
   * @param request request
   * @return 是否注册成功
   */
  @RequestMapping("regist")
  @ResponseBody
  public boolean regist(String name, String password, HttpServletRequest request) {
    name = name.trim();
    String secretKey = GoogleAuthenticator.createSecretKey();
    Set<String> standbyCodes = GoogleAuthenticator.createStandbyCodes();

    User user = new User(name, password, secretKey, standbyCodes);
    UserCache.cacheUser(user);

    WebUtils.login(user.getName(), request);
    return true;
  }

  /**
   * 获取用户注册信息.
   * 
   * @param request request
   * @param response response
   * @return 用户注册信息
   * @throws IOException 登录跳转异常
   */
  @RequestMapping("getUserRegistInfo")
  @ResponseBody
  public User getUserRegistInfo(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String userName = WebUtils.getUserName(request);
    User user = UserCache.getUser(userName);

    if (user == null) {
      String path = request.getContextPath();
      response.sendRedirect(path + "/license");
      return null;
    }

    return user.getUserRegistInfo();
  }

  /**
   * 获取用户身份认证二维码.
   * 
   * @param request request
   * @param response response
   * @throws IOException 登录跳转异常
   */
  @RequestMapping("getGoogleAuthQrCode")
  public void getGoogleAuthQrCode(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    User user = getUserRegistInfo(request, response);
    String googleAuthQrCodeData =
        GoogleAuthenticator.createGoogleAuthQrCodeData(user.getSecretKey(), user.getName(),
            WebConstants.SYSTEM_NAME);

    ServletOutputStream outputStream = null;
    try {
      outputStream = response.getOutputStream();
      QrCodeUtils.writeToStream(googleAuthQrCodeData, outputStream);
    } catch (RuntimeException | WriterException e) {
      e.printStackTrace();
    } finally {
      StreamUtils.closeStream(outputStream);
    }
  }

  /**
   * 登录校验.
   * 
   * @param name 用户名
   * @param password 密码
   * @param code 动态身份验证码
   * @return 校验结果，成功时返回null
   */
  public EnumRequestError loginVerify(String name, String password, String code) {
    if (StringUtils.isEmptyOrBlankOnly(name) || StringUtils.isEmpty(password)
        || StringUtils.isEmptyOrBlankOnly(code)) {
      return EnumRequestError.INVALID_PARAMS;
    }

    User user = UserCache.getUser(name);
    if (user == null) {
      return EnumRequestError.USER_NOT_EXIST;
    }

    if (!user.getPassword().equals(password)) {
      return EnumRequestError.LOGIN_WRONG_NAME_OR_PASSWORD;
    }

    code = code.trim();
    int length = code.length();
    if (length == GoogleAuthenticator.AUTH_CODE_LENGTH) {
      return GoogleAuthenticator.verify(user.getSecretKey(), code)
          ? null
          : EnumRequestError.LOGIN_WRONG_NAME_OR_PASSWORD;
    } else if (length == GoogleAuthenticator.STANDBY_CODE_LENGTH) {
      return user.useStandbyCode(code) ? null : EnumRequestError.LOGIN_WRONG_NAME_OR_PASSWORD;
    }
    return EnumRequestError.INVALID_PARAMS;
  }
}
