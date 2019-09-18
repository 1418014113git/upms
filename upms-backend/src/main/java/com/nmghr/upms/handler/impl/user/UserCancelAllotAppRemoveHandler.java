/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractRemoveHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.upms.config.UpmsErrorEnum;
import com.nmghr.upms.config.UpmsGlobalException;

/**
 * 用户取消分配应用.
 *
 * @author wujin
 * @date 2018年11月12日 下午4:23:45
 * @version 1.0
 */
@Service("usercancelallotappRemoveHandler")
@SuppressWarnings("all")
public class UserCancelAllotAppRemoveHandler extends AbstractRemoveHandler {

  public UserCancelAllotAppRemoveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public void remove(Map<String, Object> requestMap) throws Exception {
    Object appId = requestMap.get("appId");// 应用id
    ValidationUtils.notNull(appId, "应用id不能为空");
    Object userId = requestMap.get("userId");// 用户id
    ValidationUtils.notNull(userId, "用户id不能为空");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERGETDEPARTLIST");// 用户获取当前应用下的部门
    List<Map<String, Object>> departList = (List<Map<String, Object>>) baseService.list(requestMap);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERGETROLELIST");// 用户获取当前应用下的角色
    List<Map<String, Object>> roleList = (List<Map<String, Object>>) baseService.list(requestMap);
    if (departList.size() > 0 || roleList.size() > 0) {
      String resMessage = "用户在该应用下有分配角色或部门，该应用无法删除";
      throw new UpmsGlobalException(UpmsErrorEnum.UNDELETE.getCode(), resMessage);
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERAPPREL");// 用户取消分配应用
    baseService.remove(requestMap);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERAPPREL");// 用户获取应用列表
    List<Map<String, Object>> appList = (List<Map<String, Object>>) baseService.list(requestMap);
    if (appList.size() == 0) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERSETDEFAULTAPP");// 用户设置默认应用
      Map<String, Object> requestBody = new HashMap<>();
      requestBody.put("defaultAppId", null);
      baseService.update(userId.toString(), requestBody);
    } else {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USER");// 获取用户信息
      Map<String, Object> userMap = (Map<String, Object>) baseService.get(userId.toString());
      Object defaultAppId = userMap.get("defaultAppId");
      if (defaultAppId.equals(appId)) {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERSETDEFAULTAPP");// 用户设置默认应用
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("defaultAppId", appList.get(0).get("appId"));
        baseService.update(userId.toString(), requestBody);
      }
    }
  }
}
