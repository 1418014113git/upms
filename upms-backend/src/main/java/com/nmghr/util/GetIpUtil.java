/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.util;

import javax.servlet.http.HttpServletRequest;


/**
 * <功能描述/>
 *
 * @author weber
 * @date 2019年1月8日 下午6:51:11
 * @version 1.0
 */
public class GetIpUtil {
  /**
   * 获取用户真实IP地址，不使用request.getRemoteAddr()的原因是有可能用户使用了代理软件方式避免真实IP地址, 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值
   * 
   * @return ip
   */
  public static String getIpAddr(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
      // 多次反向代理后会有多个ip值，第一个ip才是真实ip
      if (ip.indexOf(",") != -1) {
        ip = ip.split(",")[0];
      }
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-Real-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }
}
