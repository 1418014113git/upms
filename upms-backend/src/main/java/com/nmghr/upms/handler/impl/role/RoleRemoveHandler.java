/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.upms.handler.impl.role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractRemoveHandler;
import com.nmghr.upms.config.UpmsErrorEnum;
import com.nmghr.upms.config.UpmsGlobalException;

/**
 * 角色删除接口.
 *
 * @author wujin  
 * @date 2018年11月13日 下午3:22:16 
 * @version 1.0   
 */
@Service("roleRemoveHandler")
@SuppressWarnings("all")
public class RoleRemoveHandler extends AbstractRemoveHandler{

  public RoleRemoveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public void remove(String id) throws Exception {
    Map<String, Object> requestMap = new HashMap<String, Object>();
    requestMap.put("roleId", id);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ROLEGETUSER");// 根据角色ID查询用户信息
    List<Map<String, Object>> userList = (List<Map<String, Object>>) baseService.list(requestMap);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ROLEGETMENU");// 根据角色ID查询菜单信息
    List<Map<String, Object>> menuList = (List<Map<String, Object>>) baseService.list(requestMap);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ROLEGETBUTTON");// 根据角色ID查询按钮信息
    List<Map<String, Object>> buttonList = (List<Map<String, Object>>) baseService.list(requestMap);
    if (menuList.size() > 0 || buttonList.size() > 0 || userList.size() > 0) {
      String resMessage = "该角色关联信息较多，无法删除";
      throw new UpmsGlobalException(UpmsErrorEnum.UNDELETE.getCode(), resMessage);
    } else {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ROLE");// 根据角色ID删除角色信息
      baseService.remove(id);
    }
  }
}
