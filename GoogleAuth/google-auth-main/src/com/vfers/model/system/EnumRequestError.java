package com.vfers.model.system;

/**
 * 请求错误类型.
 * 
 * @author Letuer
 */
public enum EnumRequestError {
  /**
   * 参数无效.
   */
  INVALID_PARAMS,

  /**
   * 用户不存在.
   */
  USER_NOT_EXIST,

  /**
   * 用户名或密码错误.
   */
  LOGIN_WRONG_NAME_OR_PASSWORD;
}
