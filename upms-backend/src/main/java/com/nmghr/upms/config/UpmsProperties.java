/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nmghr.upms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <功能描述/>
 *
 * @author zhanghr
 * @date 2018年11月9日 下午6:26:18
 * @version 1.0
 */
@ConfigurationProperties("user")
@Configuration
public class UpmsProperties {

  private String defaultPwd = "888888";
  private int loginFailIntervalMinutes = 5;// 5分钟内用户登录失败3次后，禁止登录一段时间
  private int loginFailInTimes = 3;// 5分钟内用户登录失败3次后，禁止登录一段时间
  private int loginBanIntervalMinutes = 30;//用户登录失败3次后，30分钟内禁止再登录

  public String getDefaultPwd() {
    return defaultPwd;
  }

  public void setDefaultPwd(String defaultPwd) {
    this.defaultPwd = defaultPwd;
  }

  public int getLoginFailIntervalMinutes() {
    return loginFailIntervalMinutes;
  }

  public void setLoginFailIntervalMinutes(int loginFailIntervalMinutes) {
    this.loginFailIntervalMinutes = loginFailIntervalMinutes;
  }

  public int getLoginFailInTimes() {
    return loginFailInTimes;
  }

  public void setLoginFailInTimes(int loginFailInTimes) {
    this.loginFailInTimes = loginFailInTimes;
  }

  public int getLoginBanIntervalMinutes() {
    return loginBanIntervalMinutes;
  }

  public void setLoginBanIntervalMinutes(int loginBanIntervalMinutes) {
    this.loginBanIntervalMinutes = loginBanIntervalMinutes;
  }
}
