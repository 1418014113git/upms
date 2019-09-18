/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.user;

import java.util.ArrayList;
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
 * 获取所有部门信息及用户已分配部门.
 *
 * @author wujin
 * @date 2018年11月12日 下午2:52:30
 * @version 1.0
 */
@Service("usergetdepartQueryHandler")
@SuppressWarnings("all")
public class UserGetDepartQueryHandler extends AbstractQueryHandler {
  private final static int DEPART_CHOOSE_TRUE = 1;// 部门已选中
  private final static int DEPART_CHOOSE_FALSE = 0;// 部门未选中
  private final static String PARENT_FIRST = "-1";// 父节点为根节点
  private final static int ROLE_SUPER_ADMIN = 1;// 超级管理员
  private final static int ROLE_APP = 2;// 应用管理员
  private final static int ROLE_DEPART = 3;// 部门管理员
  private final static int ROLE_CHANNEL = 4;// 渠道管理员

  public UserGetDepartQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    Object appId = requestMap.get("appId");// 应用id
    ValidationUtils.notNull(appId, "应用id不能为空");
    Object userId = requestMap.get("userId");// 用户id
    ValidationUtils.notNull(userId, "用户id不能为空");
    Object depId = requestMap.get("departId");// 部门id
    Object roleId = requestMap.get("roleId");// 角色id
    ValidationUtils.notNull(roleId, "角色id不能为空");
    requestMap.put("appId", appId);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPGETDEPART");// 获取应用下的所有部门
    List<Map<String, Object>> departList = (List<Map<String, Object>>) baseService.list(requestMap);
    int role = Integer.parseInt(roleId.toString());
    if (ROLE_DEPART == role) {
      ValidationUtils.notNull(depId, "该用户未绑定部门");
      Object parentId = findParent(departList, depId);
      departList = getLastList(departList, parentId);
    } else if (ROLE_CHANNEL == role) {
      return new ArrayList<>();
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERGETDEPARTSTR");// 用户获取所有的部门信息
    Map<String, Object> userMap = (Map<String, Object>) baseService.get(userId.toString());
    if (CollectionUtils.isEmpty(userMap)) {
      for (int i = 0; i < departList.size(); i++) {
        Map<String, Object> departMap = departList.get(i);
        departMap.put("isChoose", DEPART_CHOOSE_FALSE); // isChoose:是否选中:0:未选中 1:已选中
      }
    } else {
      Object departStr = userMap.get("departStr");
      if (StringUtils.isEmpty(departStr)) {
        for (int i = 0; i < departList.size(); i++) {
          Map<String, Object> departMap = departList.get(i);
          departMap.put("isChoose", DEPART_CHOOSE_FALSE); // isChoose:是否选中:0:未选中 1:已选中
        }
      } else {
        String[] depStrs = departStr.toString().split(",");
        for (int i = 0; i < departList.size(); i++) {
          Map<String, Object> departMap = departList.get(i);
          Object departId = departMap.get("id") + "";
          boolean bol = false;
          for (int j = 0; j < depStrs.length; j++) {
            String dId = depStrs[j];
            if (dId.equals(departId)) {
              bol = true;
            }
          }
          if (bol) {
            departMap.put("isChoose", DEPART_CHOOSE_TRUE);
          } else {
            departMap.put("isChoose", DEPART_CHOOSE_FALSE);
          }
        }
      }
    }
    return departList;
  }

  private List<Map<String, Object>> getLastList(List<Map<String, Object>> departList,
      Object depId) {
    List<Map<String, Object>> lastList = new ArrayList<>();
    for (int i = 0; i < departList.size(); i++) {
      Map<String, Object> departMap = departList.get(i);
      Object departId = departMap.get("id") + "";
      Object parentId = departMap.get("parentId") + "";
      if (PARENT_FIRST.equals(parentId) && !depId.equals(departId)) {
      } else {
        lastList.add(departMap);
      }
    }
    return lastList;
  }


  private Object findParent(List<Map<String, Object>> departList, Object depId) {
    for (int i = 0; i < departList.size(); i++) {
      Map<String, Object> departMap = departList.get(i);
      Object departId = departMap.get("id") + "";
      if (depId.equals(departId)) {
        Object parentId = departMap.get("parentId") + "";
        if (PARENT_FIRST.equals(parentId)) {
          return departId;
        } else {
          return findParent(departList, parentId);
        }
      }
    }
    return null;
  }

}
