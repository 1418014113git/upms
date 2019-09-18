/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nmghr.util;

import java.util.Calendar;
/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2018年6月27日 下午6:19:26
 * @version 1.0
 */
import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * JWT生成工具
 *
 * @author zhanghr
 * @date 2018年10月25日 下午12:07:49
 * @version 1.0
 */
public class JwtUtils {

  private static int ACCESS_EXPIRATION_TIME = 10; // 成功token 单位(分)设置为10分钟 计算方式：10
  private static int REFRESH_EXPIRATION_TIME = 60 * 24; // 刷新token 单位(分)设置为 24小时 计算方式：60*24
  private static final String SECRET = "qwe!@#123";// 秘钥

  /**
   * 
   * @param claims 签名的数据
   * @param salt 秘钥盐
   * @param startTime 授权开始时间
   * @param offsetMinutes 授权有效期限 单位(分)
   * @return
   */
  public static String generateJwt(Map<String, Object> claims, String salt, Date startTime, int offsetMinutes) {

    Calendar c = Calendar.getInstance();
    c.setTime(startTime);
    c.add(Calendar.MINUTE, offsetMinutes);
    return generateJwt(claims, salt, startTime, c.getTime());
  }

  /**
   * 
   * @param claims 签名的数据
   * @param salt 秘钥盐
   * @param startTime 授权开始时间
   * @param endTime 授权结束时间
   * @return
   */
  public static String generateJwt(Map<String, Object> claims, String salt, Date startTime, Date endTime) {
    JwtBuilder builder = Jwts.builder().setClaims(claims);
    if (startTime != null) {
      builder.setNotBefore(startTime);
    } else {
      startTime = new Date();// 默认开始时间为当前时间
    }
    builder.setExpiration(endTime);
    builder.signWith(SignatureAlgorithm.HS256, String.format("%s.%s", SECRET, salt));
    return builder.compact();
  }

  /**
   * 
   * @param claims 签名的数据
   * @param salt 秘钥盐
   * @param offsetMinutes 授权有效期限 单位(分)
   * @return
   */
  public static String generateJwt(Map<String, Object> claims, String salt, int offsetMinutes) {
    return generateJwt(claims, salt, null, offsetMinutes);
  }

  public static String generateJwt(Map<String, Object> claims, String salt, Date endTime) {
    return generateJwt(claims, salt, null, endTime);
  }

  /**
   * 
   * @param claims 签名的数据
   * @param salt 秘钥盐
   * @return
   */
  public static String generateRefreshToken(Map<String, Object> claims, String salt) {
    return generateJwt(claims, salt, REFRESH_EXPIRATION_TIME);
  }

  /**
   * 
   * @param claims
   * @param salt
   * @return
   */
  public static String generateAccessToken(Map<String, Object> claims, String salt) {
    return generateJwt(claims, salt, ACCESS_EXPIRATION_TIME);
  }

}
