/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.upms.config.UpmsErrorEnum;
import com.nmghr.upms.config.UpmsGlobalException;
import com.nmghr.upms.config.UpmsProperties;
import com.nmghr.util.Md5Utils;

/**
 * 用户重置密码接口.
 *
 * @author wujin
 * @date 2018年11月9日 上午11:22:46
 * @version 1.0
 */
@Service("userresetpwdUpdateHandler")
@SuppressWarnings("all")
public class UserResetPwdUpdateHandler extends AbstractUpdateHandler {

  @Autowired
  private UpmsProperties password;

  public UserResetPwdUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERPWD");// 获取用户信息
    Map<String, Object> userMap = (Map<String, Object>) baseService.get(id);
    Object pwd = requestBody.get("passWord");
    String salt = userMap.get("salt").toString();
    String userName = userMap.get("userName").toString();
    String pWd = userMap.get("passWord").toString();
    String passWord = "";
    if (StringUtils.isEmpty(pwd)) {
      passWord = password.getDefaultPwd();
    } else {
      Object oldpwd = requestBody.get("oldPassWord");
      ValidationUtils.notNull(oldpwd, "旧密码不能为空");
      String olePwd = Md5Utils.encryptMd5Password(userName, oldpwd.toString(), salt);
      if (olePwd.equals(pWd)) {
        passWord = pwd.toString();
      } else {
        String resMessage = "旧密码错误";
        throw new UpmsGlobalException(UpmsErrorEnum.UNLOGIN.getCode(), resMessage);
      }
    }
    String newPwd = Md5Utils.encryptMd5Password(userName, passWord, salt);
    requestBody.put("passWord", newPwd);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERRESETPWD");// 用户重置密码
    return baseService.update(id, requestBody);
  }
}
