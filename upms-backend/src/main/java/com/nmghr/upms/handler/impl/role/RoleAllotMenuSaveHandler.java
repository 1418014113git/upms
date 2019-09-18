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
 * 角色分配菜单.
 *
 * @author wujin
 * @date 2018年11月13日 下午5:16:27
 * @version 1.0
 */
@Service("roleallotmenuSaveHandler")
@SuppressWarnings("all")
public class RoleAllotMenuSaveHandler extends AbstractSaveHandler {

  public RoleAllotMenuSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    Object appId = requestBody.get("appId");// 应用id
    ValidationUtils.notNull(appId, "应用id不能为空");
    Object roleId = requestBody.get("roleId");// 角色id
    ValidationUtils.notNull(roleId, "角色id不能为空");
    Object menuIds = requestBody.get("menuId");// 菜单id
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ROLEMENUREL");// 刪除角色已分配菜单
    baseService.remove(requestBody);
    if (StringUtils.isEmpty(menuIds)) {
      return "ok";
    }
    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put("appId", appId);
    requestMap.put("roleId", roleId);
    String menuId = menuIds.toString();
    String[] menus = menuId.split(",");
    for (int i = 0; i < menus.length; i++) {
      String mId = menus[i];
      requestMap.put("menuId", mId);
      baseService.save(requestMap);// 角色分配菜单
    }
    return "ok";
  }
}
