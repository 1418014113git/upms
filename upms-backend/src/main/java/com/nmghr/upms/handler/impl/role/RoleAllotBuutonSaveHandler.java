/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.role;

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
 * 角色分配按钮.
 *
 * @author wujin
 * @date 2018年11月13日 下午5:39:16
 * @version 1.0
 */
@Service("roleallotbuttonSaveHandler")
@SuppressWarnings("all")
public class RoleAllotBuutonSaveHandler extends AbstractSaveHandler {

  public RoleAllotBuutonSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    Object appId = requestBody.get("appId");// 应用id
    ValidationUtils.notNull(appId, "应用id不能为空");
    Object roleId = requestBody.get("roleId");// 角色id
    ValidationUtils.notNull(roleId, "角色id不能为空");
    Object menuId = requestBody.get("menuId");// 菜单id
    ValidationUtils.notNull(menuId, "菜单id不能为空");
    Object buttonIds = requestBody.get("buttonId");// 按钮id
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ROLEBUTTONREL");// 刪除角色已分配按钮
    baseService.remove(requestBody);
    if (StringUtils.isEmpty(buttonIds)) {
      return "ok";
    }
    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put("appId", appId);
    requestMap.put("roleId", roleId);
    requestMap.put("menuId", menuId);
    String buttonId = buttonIds.toString();
    String[] buttons = buttonId.split(",");
    for (int i = 0; i < buttons.length; i++) {
      String bId = buttons[i];
      requestMap.put("buttonId", bId);
      baseService.save(requestMap);// 角色分配按钮
    }
    return "ok";
  }
}
