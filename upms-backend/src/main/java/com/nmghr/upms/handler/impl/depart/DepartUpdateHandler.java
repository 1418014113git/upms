/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.depart;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.upms.handler.impl.channel.ChannelSaveHandler;
import com.nmghr.util.JwtUtils;

/**
 * 部门信息修改接口.
 *
 * @author wujin
 * @date 2018年11月12日 上午11:27:12
 * @version 1.0
 */
@Service("departUpdateHandler")
@SuppressWarnings("all")
public class DepartUpdateHandler extends AbstractUpdateHandler {

  public DepartUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    validation(requestBody);//验证参数
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "depart");// 根据id获取部门信息
    Map<String, Object> departMap = (Map<String, Object>) baseService.get(id);
    String departName = departMap.get("departName").toString();
    requestBody.put("departLastName", departName);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "depart");// 修改部门信息
    return baseService.update(id, requestBody);
  }

  /**
   * 验证参数.
   * 
   * @param requestBody 请求体
   */
  private static void validation(Map<String, Object> requestBody) {
    Object departName = requestBody.get("departName");
    ValidationUtils.notNull(departName, "部门显示名称不能为空");
    Object departFullname = requestBody.get("departFullname");
    ValidationUtils.notNull(departFullname, "部门全称不能为空");
    Object sorted = requestBody.get("sorted");
    ValidationUtils.notNull(sorted, "排序不能为空");
    Object enabled = requestBody.get("enabled");
    ValidationUtils.notNull(enabled, "是否启用不能为空");
    Object deleteable = requestBody.get("deleteable");
    ValidationUtils.notNull(deleteable, "是否可删除不能为空");
  }
  
}
