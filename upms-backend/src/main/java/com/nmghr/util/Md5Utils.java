/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nmghr.util;

import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <功能描述/>
 *
 * @author zhanghr
 * @date 2018年11月7日 下午1:52:24
 * @version 1.0
 */
public class Md5Utils {

  private static final Logger LOGGER = LoggerFactory.getLogger(Md5Utils.class);

  private static final String SECRET = "$%ZXa@123";


  private static byte[] md5(String s) {
    MessageDigest algorithm;
    try {
      algorithm = MessageDigest.getInstance("MD5");
      algorithm.reset();
      algorithm.update(s.getBytes("UTF-8"));
      byte[] messageDigest = algorithm.digest();
      return messageDigest;
    } catch (Exception e) {
      LOGGER.error("MD5 Error...", e);
    }
    return null;
  }

  private static final String toHex(byte hash[]) {
    if (hash == null) {
      return null;
    }
    StringBuffer buf = new StringBuffer(hash.length * 2);
    int i;

    for (i = 0; i < hash.length; i++) {
      if ((hash[i] & 0xff) < 0x10) {
        buf.append("0");
      }
      buf.append(Long.toString(hash[i] & 0xff, 16));
    }
    return buf.toString();
  }

  public static String hash(String s) {
    try {
      return new String(toHex(md5(s)).getBytes("UTF-8"), "UTF-8");
    } catch (Exception e) {
      LOGGER.error("not supported charset...{}", e);
      return s;
    }
  }

  /**
   * 对密码按照用户名，密码，盐进行加密
   * 
   * @param userName 用户名
   * @param passWord 密码
   * @param salt 盐
   * @return
   */
  public static String encryptPassword(String userName, String passWord, String salt) {
    return Md5Utils.hash(String.format("%s.%s.%s.%s", SECRET, Md5Utils.hash(userName),
        encryptPassword(passWord), salt));
  }

  public static String encryptMd5Password(String userName, String md5PassWord, String salt) {
    return Md5Utils.hash(String.format("%s.%s.%s.%s", SECRET, Md5Utils.hash(userName), md5PassWord, salt));
  }

  /**
   * 对密码按照密码，盐进行加密
   * 
   * @param passWord 密码
   * @param salt 盐
   * @return
   */
  public static String encryptPassword(String passWord, String salt) {
    return Md5Utils.hash(String.format("%s.%s.%s", SECRET, passWord, salt));
  }

  public static String encryptPassword(String passWord) {
    return Md5Utils.hash(String.format("%s.%s", SECRET, passWord));
  }
}
