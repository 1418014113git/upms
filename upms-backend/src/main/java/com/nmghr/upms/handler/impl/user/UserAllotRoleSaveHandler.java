/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;

/**
 * 用户分配角色.
 *
 * @author wujin
 * @date 2018年11月12日 下午5:35:51
 * @version 1.0
 */
@Service("userallotroleSaveHandler")
@SuppressWarnings("all")
public class UserAllotRoleSaveHandler extends AbstractSaveHandler {

  public UserAllotRoleSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    Object appId = requestBody.get("appId");// 应用id
    ValidationUtils.notNull(appId, "应用id不能为空");
    Object userId = requestBody.get("userId");// 用户id
    ValidationUtils.notNull(userId, "用户id不能为空");
    Object roleIds = requestBody.get("roleId");// 角色id
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERROLEREL");// 刪除用戶已分配角色
    baseService.remove(requestBody);
    if (StringUtils.isEmpty(roleIds)) {
      return "ok";
    }
    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put("appId", appId);
    requestMap.put("userId", userId);
    String roleStr = roleIds.toString();
    String[] roles = roleStr.split(",");
    for (int i = 0; i < roles.length; i++) {
      String roleId = roles[i];
      requestMap.put("roleId", roleId);
      baseService.save(requestMap);// 用户分配角色
    }
    return "ok";
  }
}
