/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.basic.core.util.ValidationUtils;

/**
 * 角色获取按钮.
 *
 * @author wujin
 * @date 2018年11月13日 下午6:12:38
 * @version 1.0
 */
@Service("rolegetbuttonlistQueryHandler")
@SuppressWarnings("all")
public class RoleGetButtonListQueryHandler extends AbstractQueryHandler {
  private final static int BUTTON_CHOOSE_TRUE = 1;// 按钮已选中
  private final static int BUTTON_CHOOSE_FALSE = 0;// 按钮未选中
  private final static int ROLE_SUPER_ADMIN = 1;// 超级管理员
  private final static String APP_UPMS = "1";// 应用为upms

  public RoleGetButtonListQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    Object appId = requestMap.get("appId");
    ValidationUtils.notNull(appId, "应用id不能为空");
    Object roleId = requestMap.get("roleId");
    ValidationUtils.notNull(roleId, "角色id不能为空");
    Object menuId = requestMap.get("menuId");
    ValidationUtils.notNull(menuId, "菜单id不能为空");
    Object upmsRoleId = requestMap.get("upmsRoleId");
    ValidationUtils.notNull(upmsRoleId, "upms角色id不能为空");
    Object userId = requestMap.get("userId");
    ValidationUtils.notNull(userId, "用户id不能为空");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPGETBUTTON");// 获取菜单下的所有按钮
    List<Map<String, Object>> buttonList = (List<Map<String, Object>>) baseService.list(requestMap);
    int roId = Integer.parseInt(upmsRoleId.toString());
    if (APP_UPMS.equals(appId) && ROLE_SUPER_ADMIN != roId) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERGETBUTTON");// 获取用户按钮信息
      List<Map<String, Object>> buttons = (List<Map<String, Object>>) baseService.list(requestMap);
      if (buttons.size() == 0) {
        buttonList = new ArrayList<>();
      } else {
        List<Map<String, Object>> list = new ArrayList<>();
        // 将菜单下用户没有权限的按钮剔除掉
        for (int i = 0; i < buttonList.size(); i++) {
          Map<String, Object> buttonMap = buttonList.get(i);
          Object buttonCode = buttonMap.get("buttonCode");
          boolean bol = false;
          for (int j = 0; j < buttons.size(); j++) {
            Map<String, Object> button = buttons.get(j);
            Object code = button.get("buttonCode");
            if (buttonCode.equals(code)) {
              bol = true;
            }
          }
          if (bol) {
            list.add(buttonMap);
          }
        }
        buttonList = list;
      }
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ROLEGETBUTTON");// 角色获取菜单下所有的按钮信息
    List<Map<String, Object>> roleButtonList =
        (List<Map<String, Object>>) baseService.list(requestMap);
    if (roleButtonList.size() == 0) {
      for (int i = 0; i < buttonList.size(); i++) {
        Map<String, Object> buttonMap = buttonList.get(i);
        buttonMap.put("isChoose", BUTTON_CHOOSE_FALSE); // isChoose:是否选中:0:未选中 1:已选中
      }
    } else {
      if (roleButtonList.size() == 0) {
        for (int i = 0; i < buttonList.size(); i++) {
          Map<String, Object> buttonMap = buttonList.get(i);
          buttonMap.put("isChoose", BUTTON_CHOOSE_FALSE);
        }
      } else {
        for (int i = 0; i < buttonList.size(); i++) {
          Map<String, Object> buttonMap = buttonList.get(i);
          Object buttonId = buttonMap.get("id");
          boolean bol = false;
          for (int j = 0; j < roleButtonList.size(); j++) {
            Map<String, Object> roleButtonMap = roleButtonList.get(j);
            Object bId = roleButtonMap.get("buttonId");
            if (buttonId.equals(bId)) {
              bol = true;
            }
          }
          if (bol) {
            buttonMap.put("isChoose", BUTTON_CHOOSE_TRUE);
          } else {
            buttonMap.put("isChoose", BUTTON_CHOOSE_FALSE);
          }
        }
      }
    }
    return buttonList;
  }
}
