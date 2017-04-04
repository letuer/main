package com.vfers.utils.google.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

/**
 * Google身份认证器.
 */
public class GoogleAuthenticator {
  /**
   * 验证码长度.
   */
  public static final int AUTH_CODE_LENGTH = 6;

  /**
   * 备用码长度.
   */
  public static final int STANDBY_CODE_LENGTH = 8;

  /**
   * 备用码数量.
   */
  public static final int STANDBY_CODE_NUM = 6;

  /**
   * 时间前后偏移量. 用于防止客户端时间不精确导致生成的TOTP与服务器端的TOTP一直不一致 如果为0,当前时间为 10:10:15 则表明在 10:10:00-10:10:30
   * 之间生成的TOTP 能校验通过 如果为1,则表明在 10:09:30-10:10:00 10:10:00-10:10:30 10:10:30-10:11:00 之间生成的TOTP 能校验通过
   * 以此类推
   */
  private static final int TIME_EXCURSION = 1;

  /**
   * The number of bits of a secret key in binary form. Since the Base32 encoding with 8 bit
   * characters introduces an 160% overhead, we just need 80 bits (10 bytes) to generate a 16 bytes
   * Base32-encoded secret key.
   */
  private static final int SECRET_BITS = 80;

  /**
   * Number of scratch codes to generate during the key generation. We are using Google's default of
   * providing 5 scratch codes.
   */
  private static final int SCRATCH_CODES = 5;

  /**
   * Number of digits of a scratch code represented as a decimal integer.
   */
  private static final int SCRATCH_CODE_LENGTH = 8;

  /**
   * Length in bytes of each scratch code. We're using Google's default of using 4 bytes per scratch
   * code.
   */
  private static final int BYTES_PER_SCRATCH_CODE = 4;

  /**
   * 创建一个密钥.
   * 
   * @return 密钥
   */
  public static String createSecretKey() {
    SecureRandom random = new SecureRandom();
    byte[] buffer =
        new byte[SECRET_BITS / SCRATCH_CODE_LENGTH + SCRATCH_CODES * BYTES_PER_SCRATCH_CODE];

    byte[] bytes = Arrays.copyOf(buffer, SECRET_BITS / SCRATCH_CODE_LENGTH);

    random.nextBytes(bytes);
    Base32 base32 = new Base32();
    String secretKey = base32.encodeToString(bytes);
    return secretKey.toUpperCase();
  }

  /**
   * 生成备用码.
   * 
   * @return 备用码
   */
  public static Set<String> createStandbyCodes() {
    SecureRandom random = new SecureRandom();
    Base32 base32 = new Base32();

    byte[] bytes = new byte[10];

    Set<String> standbyCodes = new HashSet<>();

    for (int i = 0; i < STANDBY_CODE_NUM; i++) {
      random.nextBytes(bytes);
      String code = base32.encodeToString(bytes).toUpperCase();
      standbyCodes.add(code.substring(0, STANDBY_CODE_LENGTH));
    }

    return standbyCodes;
  }

  /**
   * 校验方法.
   * 
   * @param secretKey 密钥
   * @param code 用户输入的TOTP
   */
  public static boolean verify(String secretKey, String code) {
    long time = System.currentTimeMillis() / 1000 / 30;
    for (int i = -TIME_EXCURSION; i <= TIME_EXCURSION; i++) {
      String totp = getTotp(secretKey, time + i);
      if (code.equalsIgnoreCase(totp)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 根据密钥获取验证码 返回字符串是因为数值有可能以0开头
   * 
   * @param secretKey 密钥
   * @param time 第几个30秒 System.currentTimeMillis() / 1000 / 30
   */
  public static String getTotp(String secretKey, long time) {
    Base32 base32 = new Base32();
    byte[] bytes = base32.decode(secretKey.toUpperCase());
    String hexKey = Hex.encodeHexString(bytes);
    String hexTime = Long.toHexString(time);
    return Totp.generateTotp(hexKey, hexTime, String.valueOf(AUTH_CODE_LENGTH));
  }

  /**
   * 生成Google Authenticator二维码所需信息 Google Authenticator 约定的二维码信息格式 :
   * otpauth://totp/{issuer}:{account}?secret={secret}&issuer={issuer} 参数需要url编码 +号需要替换成%20
   * 
   * @param secret 密钥 使用createSecretKey方法生成
   * @param account 用户账户 如: example@domain.com 138XXXXXXXX
   * @param issuer 服务名称 如: Google Github 印象笔记
   */
  public static String createGoogleAuthQrCodeData(String secret, String account, String issuer) {
    String qrCodeData = "otpauth://totp/%s?secret=%s&issuer=%s";
    try {
      return String.format(qrCodeData,
          URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20"), URLEncoder
              .encode(secret, "UTF-8").replace("+", "%20"), URLEncoder.encode(issuer, "UTF-8")
              .replace("+", "%20"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return "";
  }

}
