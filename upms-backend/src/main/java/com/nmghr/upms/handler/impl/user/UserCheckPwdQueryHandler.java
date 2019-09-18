/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.user;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.util.Md5Utils;

/**
 * 用户校验密码.
 *
 * @author wujin
 * @date 2018年11月15日 下午5:44:57
 * @version 1.0
 */
@Service("usercheckpwdQueryHandler")
@SuppressWarnings("all")
public class UserCheckPwdQueryHandler extends AbstractQueryHandler {
private static final String PASS_CHECK_TRUE="true";//密码正确
private static final String PASS_CHECK_FALSE="false";//密码错误
  
  public UserCheckPwdQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    Object passWord = requestMap.get("passWord");
    ValidationUtils.notNull(passWord, "密码不能为空");
    Object userId = requestMap.get("userId");
    ValidationUtils.notNull(userId, "用户ID不能为空");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERPWD");// 获取用户密码
    Map<String, Object> userMap = (Map<String, Object>) baseService.get(userId.toString());
    String pwd = userMap.get("passWord").toString();
    String salt = userMap.get("salt").toString();
    String userName = userMap.get("userName").toString();
    String pWd = Md5Utils.encryptMd5Password(userName, passWord.toString(), salt);
    if (pwd.equals(pWd)) {
      return PASS_CHECK_TRUE;
    } else {
      return PASS_CHECK_FALSE;
    }
  }
}
