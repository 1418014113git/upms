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
import org.springframework.util.StringUtils;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.basic.core.util.ValidationUtils;

/**
 * 角色获取菜单列表.
 *
 * @author wujin
 * @date 2018年11月13日 下午5:54:56
 * @version 1.0
 */
@Service("rolegetmenulistQueryHandler")
@SuppressWarnings("all")
public class RoleGetMenuListQueryHandler extends AbstractQueryHandler {
  private final static int MENU_CHOOSE_TRUE = 1;// 菜单已选中
  private final static int MENU_CHOOSE_FALSE = 0;// 菜单未选中
  private final static int ROLE_SUPER_ADMIN = 1;// 超级管理员
  private final static String APP_UPMS = "1";// 应用为upms


  public RoleGetMenuListQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    Object appId = requestMap.get("appId");
    ValidationUtils.notNull(appId, "应用id不能为空");
    Object roleId = requestMap.get("roleId");
    ValidationUtils.notNull(roleId, "角色id不能为空");
    Object upmsRoleId = requestMap.get("upmsRoleId");
    ValidationUtils.notNull(upmsRoleId, "upms角色id不能为空");
    Object userId = requestMap.get("userId");
    ValidationUtils.notNull(userId, "用户id不能为空");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPGETMENU");// 获取应用下的所有菜单
    List<Map<String, Object>> menuList = (List<Map<String, Object>>) baseService.list(requestMap);
    int roId = Integer.parseInt(upmsRoleId.toString());
    if (APP_UPMS.equals(appId)) {
      if (ROLE_SUPER_ADMIN != roId) {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERGETMENU");// 获取用户菜单信息
        List<Map<String, Object>> menus = (List<Map<String, Object>>) baseService.list(requestMap);
        if (menus.size() == 0) {
          menuList = new ArrayList<>();
        } else {
          // 将应用下不是用户的菜单剔除掉
          List<Map<String, Object>> list = new ArrayList<>();
          for (int i = 0; i < menuList.size(); i++) {
            Map<String, Object> menuMap = menuList.get(i);
            Object menuId = menuMap.get("id");
            boolean bol = false;
            for (int j = 0; j < menus.size(); j++) {
              Map<String, Object> menu = menus.get(j);
              Object id = menu.get("id");
              if (menuId.equals(id)) {
                bol = true;
              }
            }
            if (bol) {
              list.add(menuMap);
            }
          }
          menuList = list;
        }
      }
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ROLEGETMENU");// 角色获取所有的部门信息
    List<Map<String, Object>> roleMenuList =
        (List<Map<String, Object>>) baseService.list(requestMap);
    if (roleMenuList.size() == 0) {
      for (int i = 0; i < menuList.size(); i++) {
        Map<String, Object> menuMap = menuList.get(i);
        menuMap.put("isChoose", MENU_CHOOSE_FALSE); // isChoose:是否选中:0:未选中 1:已选中
      }
    } else {
      if (roleMenuList.size() == 0) {
        for (int i = 0; i < menuList.size(); i++) {
          Map<String, Object> menuMap = menuList.get(i);
          menuMap.put("isChoose", MENU_CHOOSE_FALSE);
        }
      } else {
        for (int i = 0; i < menuList.size(); i++) {
          Map<String, Object> menuMap = menuList.get(i);
          Object menuId = menuMap.get("id");
          boolean bol = false;
          for (int j = 0; j < roleMenuList.size(); j++) {
            Map<String, Object> roleMenuMap = roleMenuList.get(j);
            Object mId = roleMenuMap.get("id");
            if (menuId.equals(mId)) {
              bol = true;
            }
          }
          if (bol) {
            menuMap.put("isChoose", MENU_CHOOSE_TRUE);
          } else {
            menuMap.put("isChoose", MENU_CHOOSE_FALSE);
          }
        }
      }
    }
    return menuList;
  }
}
