package com.vfers.dao;

import com.vfers.model.user.User;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

/**
 * Dao.a
 * 
 * @author Letuer
 */
@Repository("userDao")
public class UserDao extends SqlSessionDaoSupport {
  private static final String WORKSPACE = "User";

  /**
   * 查询用户数据.
   * 
   * @param name 用户名
   * @return 用户对象
   */
  public User select(String name) {
    try {
      return this.getSqlSession().selectOne(WORKSPACE + ".select", name);
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
    return null;
  }

}
