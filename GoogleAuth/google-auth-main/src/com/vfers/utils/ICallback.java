package com.vfers.utils;

/**
 * 回调函数.
 * 
 * @author Letuer
 * 
 */
public interface ICallback {

  public void success(Object arg, Object... args);

  public void failed(Object arg, Object... args);

  public void exception(Object arg, Object... args);
}
