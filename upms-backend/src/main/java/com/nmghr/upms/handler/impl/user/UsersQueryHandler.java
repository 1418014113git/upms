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
import org.springframework.util.CollectionUtils;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.basic.core.util.ValidationUtils;

/**
 * 用户查询接口.
 *
 * @author wujin
 * @version 1.0
 * @date 2018年11月15日 上午10:14:27
 */
@Service("usersQueryHandler")
@SuppressWarnings("all")
public class UsersQueryHandler extends AbstractQueryHandler {
  private final static int ROLE_SUPER_ADMIN = 1;// 超级管理员
  private final static int ROLE_APP = 2;// 应用管理员
  private final static int ROLE_DEPART = 3;// 部门管理员
  private final static int ROLE_CHANNEL = 4;// 渠道管理员
  private final static String APP_CODE_UPMS = "upms";// UPMS应用编码

  public UsersQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object page(Map<String, Object> requestMap, int currentPage, int pageSize) throws Exception {
    Object userId = requestMap.get("userId");
    ValidationUtils.notNull(userId, "用户ID不能为空");
    Object appCode = requestMap.get("appCode");
    ValidationUtils.notNull(appCode, "应用编码不能为空");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERUPMSROLE");
    Map<String, Object> userMap = (Map<String, Object>) baseService.get(userId.toString());
    if (!CollectionUtils.isEmpty(userMap)) {
      Object roleId = userMap.get("roleId");
      int role = Integer.parseInt(roleId.toString());
      if (ROLE_SUPER_ADMIN == role) {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERS");// 超级管理员查询用户列表
        return baseService.page(requestMap, currentPage, pageSize);
      } else if (ROLE_APP == role) {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERGETAPPSTRS");// 获取用户授权的除UPMS应用
        Map<String, Object> appStrMap = (Map<String, Object>) baseService.get(userId.toString());
        if (CollectionUtils.isEmpty(appStrMap)) {
          requestMap.put("appStr", "");
        } else {
          requestMap.put("appStr", appStrMap.get("appStr"));
        }
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPUSERS");// 应用管理员查询用户列表
        return baseService.page(requestMap, currentPage, pageSize);
      } else if (ROLE_DEPART == role) {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERGETDEPARTSTRS");// 获取用户授权的部门
        Map<String, Object> departStrMap = (Map<String, Object>) baseService.get(userId.toString());
        if (CollectionUtils.isEmpty(departStrMap)) {
          requestMap.put("departStr", "");
        } else {
          requestMap.put("departStr", departStrMap.get("departStr"));
        }
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPARTUSERS");// 部门管理员查询用户列表
        return baseService.page(requestMap, currentPage, pageSize);
      } else if (ROLE_CHANNEL == role) {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CHANNELUSERS");// 渠道管理员查询用户列表
        return baseService.page(requestMap, currentPage, pageSize);
      } else {
        return null;
      }
    } else {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPCODE");// 通过用户APPCODE获取应用信息
      Map<String, Object> appMap = (Map<String, Object>) baseService.get(appCode.toString());
      requestMap.put("appStr", appMap.get("id"));
      Object appId = appMap.get("id");
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPUSERS");// 获取该应用下的用户
      return baseService.page(requestMap, currentPage, pageSize);
    }
  }

}
