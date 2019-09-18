/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.app;

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
 * 删除应用接口.
 *
 * @author wujin
 * @date 2018年11月9日 下午5:44:17
 * @version 1.0
 */
@Service("appRemoveHandler")
@SuppressWarnings("all")
public class AppRemoveHandler extends AbstractRemoveHandler {

  public AppRemoveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public void remove(String id) throws Exception {
    Map<String, Object> requestMap = new HashMap<String, Object>();
    requestMap.put("appId", id);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPART");// 根据应用ID查询部门信息
    List<Map<String, Object>> departList = (List<Map<String, Object>>) baseService.list(requestMap);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ROLE");// 根据应用ID查询部门信息
    List<Map<String, Object>> roleList = (List<Map<String, Object>>) baseService.list(requestMap);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERGETAPP");// 根据应用ID查询用户信息
    List<Map<String, Object>> userList = (List<Map<String, Object>>) baseService.list(requestMap);
    if (departList.size() > 0 || roleList.size() > 0 || userList.size() > 0) {
      String resMessage = "该应用关联信息较多，无法删除";
      throw new UpmsGlobalException(UpmsErrorEnum.UNDELETE.getCode(), resMessage);
    } else {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APP");// 根据应用ID删除应用信息
      baseService.remove(id);
    }
  }
}
