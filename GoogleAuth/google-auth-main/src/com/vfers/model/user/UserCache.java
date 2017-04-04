package com.vfers.model.user;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息缓存.
 * 
 * @author Letuer
 */
public class UserCache {
  private static Map<String, User> cache = new HashMap<>();

  /**
   * 缓存用户信息.
   * 
   * @param user 用户信息
   * @return 用户已存在时返回false
   */
  public static synchronized boolean cacheUser(User user) {
    String name = user.getName();
    if (cache.containsKey(name)) {
      return false;
    }

    cache.put(name, user);
    return true;
  }

  /**
   * 获取用户信息.
   * 
   * @param name 用户名
   * @return 用户信息
   */
  public static User getUser(String name) {
    return cache.get(name.trim().toLowerCase());
  }
}
