/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.upms.handler.impl.depart;

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
 * 部门删除接口.
 *
 * @author wujin  
 * @date 2018年11月12日 上午11:33:33 
 * @version 1.0   
 */
@Service("departRemoveHandler")
@SuppressWarnings("all")
public class DepartRemoveHandler extends AbstractRemoveHandler{

  public DepartRemoveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public void remove(String id) throws Exception {
    Map<String, Object> requestMap = new HashMap<String, Object>();
    requestMap.put("departId", id);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPARTGETUSER");// 根据部门ID查询用户信息
    List<Map<String, Object>> userList = (List<Map<String, Object>>) baseService.list(requestMap);
    if (userList.size() > 0) {
      String resMessage = "该部门关联信息较多，无法删除";
      throw new UpmsGlobalException(UpmsErrorEnum.UNDELETE.getCode(), resMessage);
    } else {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPART");// 根据应用ID删除应用信息
      baseService.remove(id);
    }
  }
}
