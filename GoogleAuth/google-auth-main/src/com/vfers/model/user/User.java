package com.vfers.model.user;

import java.util.Set;

/**
 * 用户信息.
 * 
 * @author Letuer
 */
public class User {

  private String name;

  private String password;

  private String secretKey;

  private Set<String> standbyCodes;

  /**
   * 构造函数.
   * 
   * @param name 用户名
   * @param password 密码
   * @param secretKey 密钥
   * @param standbyCodes 备用码
   */
  public User(String name, String password, String secretKey, Set<String> standbyCodes) {
    this.name = name.toLowerCase();
    this.password = password;
    this.secretKey = secretKey;
    this.standbyCodes = standbyCodes;
  }

  /**
   * 消耗一个备用码.
   * 
   * @param code 备用码
   * @return 如果备用码存在，则移除并返回true，否则返回false
   */
  public boolean useStandbyCode(String code) {
    if (standbyCodes.contains(code)) {
      standbyCodes.remove(code);
      return true;
    }
    return false;
  }

  /**
   * 获取用户注册信息.
   * 
   * @return 用户注册信息
   */
  public User getUserRegistInfo() {
    return new User(name, null, secretKey, standbyCodes);
  }

  public String getName() {
    return name;
  }

  public String getPassword() {
    return password;
  }

  public String getSecretKey() {
    return secretKey;
  }
}
