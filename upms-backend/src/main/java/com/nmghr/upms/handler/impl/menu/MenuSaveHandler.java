/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.upms.config.UpmsErrorEnum;
import com.nmghr.upms.config.UpmsGlobalException;

/**
 * 添加菜单.
 *
 * @author wujin
 * @date 2018年11月26日 下午5:07:50
 * @version 1.0
 */
@Service("menuSaveHandler")
@SuppressWarnings("all")
public class MenuSaveHandler extends AbstractSaveHandler {
  private final static int MENU_TYPE_INHERIT = 1;// 菜单类型为继承菜单
  private final static int APP_ID_UPMS = 1;// 应用ID为UPMS

  public MenuSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object save(Map<String, Object> requestBody) throws Exception {
    validation(requestBody);
//    int menuType = Integer.parseInt(requestBody.get("menuType").toString());
//    if (MENU_TYPE_INHERIT == menuType) {
//      Object id = "";
//      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MENUPATH");// 校验菜单路由是否存在
//      List<Map<String, Object>> menuList =
//          (List<Map<String, Object>>) baseService.list(requestBody);
//      if (menuList.size() == 0) {
//        String resMessage = "请检查继承菜单路由是否正确";
//        throw new UpmsGlobalException(UpmsErrorEnum.UNCREATE.getCode(), resMessage);
//      } else {
//        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MENU");
//        id = baseService.save(requestBody);
//        Map<String, Object> menuMap = menuList.get(0);
//        Map<String, Object> requestMap = new HashMap<>();
//        requestMap.put("menuId", menuMap.get("id"));
//        requestMap.put("appId", APP_ID_UPMS);
//        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BUTTON");// 查询原菜单下的按钮
//        List<Map<String, Object>> buttonList =
//            (List<Map<String, Object>>) baseService.list(requestMap);
//        requestMap.put("appId", requestBody.get("appId"));
//        requestMap.put("menuId", id);
//        for (int i = 0; i < buttonList.size(); i++) {
//          Map<String, Object> buttonMap = buttonList.get(i);
//          requestMap.put("buttonName", buttonMap.get("buttonName"));
//          requestMap.put("buttonCode", buttonMap.get("buttonCode"));
//          requestMap.put("buttonType", buttonMap.get("buttonType"));
//          requestMap.put("linkTo", buttonMap.get("linkTo"));
//          requestMap.put("enabled", buttonMap.get("enabled"));
//          requestMap.put("deleteable", buttonMap.get("deleteable"));
//          baseService.save(requestMap);// 添加按钮
//        }
//      }
//      return id;
//    } else {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MENU");
      return baseService.save(requestBody);
//    }
  }

  /**
   * 验证参数.
   * 
   * @param requestBody 请求体
   */
  public static void validation(Map<String, Object> requestBody) {
    Object menuName = requestBody.get("menuName");
    ValidationUtils.notNull(menuName, "菜单名称不能为空");
    Object menuCode = requestBody.get("menuCode");
    ValidationUtils.notNull(menuCode, "菜单编码不能为空");
    Object menuPath = requestBody.get("menuPath");
    ValidationUtils.notNull(menuPath, "菜单路由不能为空");
    Object enabled = requestBody.get("enabled");
    ValidationUtils.notNull(enabled, "是否启用不能为空");
    Object sorted = requestBody.get("sorted");
    ValidationUtils.notNull(sorted, "排序不能为空");
    Object appId = requestBody.get("appId");
    ValidationUtils.notNull(appId, "所属应用ID");
    Object parentMenuId = requestBody.get("parentMenuId");
    ValidationUtils.notNull(parentMenuId, "父菜单ID不能为空");
    Object deleteable = requestBody.get("deleteable");
    ValidationUtils.notNull(deleteable, "是否可删除不能为空");
    Object menuType = requestBody.get("menuType");
    ValidationUtils.notNull(menuType, "菜单类型不能为空");
  }
}
