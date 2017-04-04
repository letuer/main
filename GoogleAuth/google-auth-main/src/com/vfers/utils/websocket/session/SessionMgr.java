package com.vfers.utils.websocket.session;

import com.alibaba.fastjson.JSON;
import com.vfers.utils.ICallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * 会话管理类.
 * 
 * @author Letuer
 * 
 */
public class SessionMgr {

  /**
   * 会话属性Map中的会话类型标识key.
   */
  public static final String TYPE_KEY_IN_SESSION_ATTR = "__session_type";

  /**
   * 会话属性Map中标识该会话唯一性的业务id.
   * <ul>
   * <li>在服务端发送某一类型的消息时,会根据该标识判断该会话是否需要发送.比如:</li>
   * <li>系统通知类型,有的消息只能管理员能够接收,则该值应该为权限或用户标识.</li>
   * <li>如果该值不指定(拦截器中指定),则该消息为广播形式,所有该类型的会话都会收到消息.</li>
   * </ul>
   */
  public static final String BIZ_UUID_KEY_IN_SESSION_ATTR = "__session_biz_uuid";

  /**
   * 已注册会话类型.
   */
  private Set<EnumSessionType> registeredSessionTypes = new HashSet<>();

  /**
   * 会话集合:{会话标识符: 会话列表}.
   */
  private Map<EnumSessionType, List<WebSocketSession>> sessionsMap =
      new HashMap<EnumSessionType, List<WebSocketSession>>();

  /**
   * 单例,禁用构造函数创建对象.
   */
  private SessionMgr() {}

  /**
   * 静态内部类实现单例.
   */
  private static class LazyHolderSington {
    public static final SessionMgr INSTANCE = new SessionMgr();
  }

  /**
   * 获取单例对象.
   * 
   * @return 实例
   */
  public static SessionMgr getInstance() {
    return LazyHolderSington.INSTANCE;
  }

  /**
   * 会话类型注册.
   * 
   * @param sessionType 会话类型
   * @return 会话类型已存在时返回false
   */
  public final boolean regist(EnumSessionType sessionType) {
    if (registeredSessionTypes.contains(sessionType)) {
      return false;
    }
    registeredSessionTypes.add(sessionType);
    return true;
  }

  /**
   * 获取会话类别.
   * 
   * @param sessionType 会话类型
   * @return 非空List
   */
  public List<WebSocketSession> getSessions(EnumSessionType sessionType) {
    return sessionsMap.getOrDefault(sessionType, new ArrayList<WebSocketSession>());
  }

  /**
   * 在Session属性中增加会话标识和会话类型.
   * 
   * @param attributes 会话参数集合
   * @param sessionType 会话类型
   * @param request 请求参数
   */
  public void appendAttr(Map<String, Object> attributes, EnumSessionType sessionType,
      HttpServletRequest request) {
    attributes.put(TYPE_KEY_IN_SESSION_ATTR, sessionType.name());
    // request.getParameterValues()
  }

  /**
   * 增加会话
   * 
   * @param session 会话
   * @param callback 回调函数,成功时传参为会话类型,会话对象;会话类型未知时调用异常,传参为会话对象;会话类型未注册时调用失败,传参为会话类型,会话对象.
   */
  public void addSession(WebSocketSession session, ICallback callback) {
    EnumSessionType sessionType = getSessionType(session);
    if (sessionType != null) {
      if (registeredSessionTypes.contains(sessionType)) {
        List<WebSocketSession> list = sessionsMap.get(sessionType);
        if (list == null) {
          list = new ArrayList<>();
          sessionsMap.put(sessionType, list);
        }
        list.add(session);

        if (callback != null) {
          callback.success(sessionType, session);
        }
      } else {
        if (callback != null) {
          callback.failed(sessionType, session);
        }
      }
    } else {
      if (callback != null) {
        callback.exception(session);
      }
    }
  }

  /**
   * 移除会话
   * 
   * @param session 会话
   * @param callback 回调函数,成功时传参为会话类型,会话对象;会话类型未知时调用异常,传参为会话对象;无失败回调.
   */
  public void removeSession(WebSocketSession session, ICallback callback) {
    EnumSessionType sessionType = getSessionType(session);
    if (sessionType != null) {
      List<WebSocketSession> list = sessionsMap.get(sessionType);
      if (list != null) {
        list.remove(session);
        if (callback != null) {
          callback.success(sessionType, session);
        }
      }
    } else {
      if (callback != null) {
        callback.exception(session);
      }
    }
  }

  /**
   * 关闭会话
   * 
   * @param session 会话
   * @param callback 回调函数,成功时传参为会话类型,会话对象;会话类型未知时调用异常,传参为会话对象;会话关闭失败时调用异常,传参是会话对象,异常对象;无失败回调.
   */
  public void closeSession(WebSocketSession session, ICallback callback) {
    EnumSessionType sessionType = getSessionType(session);
    if (sessionType != null) {
      List<WebSocketSession> list = sessionsMap.get(sessionType);
      if (list != null) {
        list.remove(session);
        if (session.isOpen()) {
          try {
            session.close();
          } catch (IOException e) {
            if (callback != null) {
              callback.exception(session, e);
            }
            return;
          }
        }
        if (callback != null) {
          callback.success(sessionType, session);
        }
      }
    } else {
      if (callback != null) {
        callback.exception(session);
      }
    }
  }

  /**
   * 获取会话类型.
   */
  private EnumSessionType getSessionType(WebSocketSession session) {
    Map<String, Object> attributes = session.getAttributes();
    if (attributes.containsKey(TYPE_KEY_IN_SESSION_ATTR)) {
      return EnumSessionType.valueOf(String.valueOf(attributes.get(TYPE_KEY_IN_SESSION_ATTR)));
    }
    return null;
  }

  /**
   * 发送消息.
   * 
   * @param sessionType 会话类型
   * @param bizUuid 会话业务标识
   * @param message 消息
   * @throws IOException 会话发送异常
   */
  public void sendMessage(EnumSessionType sessionType, String bizUuid, Object message)
      throws IOException {
    List<WebSocketSession> list = getSessions(sessionType);
    if (list == null || list.isEmpty()) {
      return;
    }

    if (bizUuid == null) {
      for (WebSocketSession session : list) {
        session.sendMessage(createMessage(message));
      }
    } else {
      for (WebSocketSession session : list) {
        if (bizUuid.equals(session.getAttributes().get(BIZ_UUID_KEY_IN_SESSION_ATTR))) {
          session.sendMessage(createMessage(message));
        }
      }
    }
  }

  private TextMessage createMessage(Object message) {
    if (message instanceof CharSequence) {
      return new TextMessage((CharSequence) message);
    }

    try {
      String json = JSON.toJSONString(message);
      return new TextMessage(json);
    } catch (RuntimeException e) {
      return new TextMessage(String.valueOf(message));
    }
  }
}
