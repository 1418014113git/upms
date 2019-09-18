/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.user;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.basic.core.util.ValidationUtils;

/**
 * 获取应用下的角色以及用户已分配的角色.
 *
 * @author wujin
 * @date 2018年11月12日 下午3:10:01
 * @version 1.0
 */
@Service("usergetroleQueryHandler")
@SuppressWarnings("all")
public class UserGetRoleQueryHandler extends AbstractQueryHandler {
  private final static int ROLE_CHOOSE_TRUE = 1;// 角色已选中
  private final static int ROLE_CHOOSE_FALSE = 0;// 角色未选中

  public UserGetRoleQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    Object appId = requestMap.get("appId");// 应用id
    ValidationUtils.notNull(appId, "应用id不能为空");
    Object userId = requestMap.get("userId");// 用户id
    ValidationUtils.notNull(userId, "用户id不能为空");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPGETROLE");// 获取应用下的所有角色
    List<Map<String, Object>> roleList = (List<Map<String, Object>>) baseService.list(requestMap);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERGETROLESTR");// 用户获取所有的角色信息
    Map<String, Object> userMap = (Map<String, Object>) baseService.get(userId.toString());
    if (CollectionUtils.isEmpty(userMap)) {
      for (int i = 0; i < roleList.size(); i++) {
        Map<String, Object> roleMap = roleList.get(i);
        roleMap.put("isChoose", ROLE_CHOOSE_FALSE); // isChoose:是否选中:0:未选中 1:已选中
      }
    } else {
      Object roleStr = userMap.get("roleStr");
      if (StringUtils.isEmpty(roleStr)) {
        for (int i = 0; i < roleList.size(); i++) {
          Map<String, Object> roleMap = roleList.get(i);
          roleMap.put("isChoose", ROLE_CHOOSE_FALSE); // isChoose:是否选中:0:未选中 1:已选中
        }
      } else {
        String[] roleStrs = roleStr.toString().split(",");
        for (int i = 0; i < roleList.size(); i++) {
          boolean bol = false;
          Map<String, Object> roleMap = roleList.get(i);
          Object roleId = roleMap.get("id") + "";
          for (int j = 0; j < roleStrs.length; j++) {
            String rId = roleStrs[j];
            if (rId.equals(roleId)) {
              bol = true;
            }
          }
          if (bol) {
            roleMap.put("isChoose", ROLE_CHOOSE_TRUE);
          } else {
            roleMap.put("isChoose", ROLE_CHOOSE_FALSE);
          }
        }
      }
    }
    return roleList;
  }

}
