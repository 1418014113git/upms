/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.upms.handler.impl.api;

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
 * api设置分组接口.
 *
 * @author wujin  
 * @date 2018年11月7日 下午2:00:17 
 * @version 1.0   
 */
@Service("apisetgroupSaveHandler")
@SuppressWarnings("all")
public class ApiSetGroupSaveHandler extends AbstractSaveHandler{

  public ApiSetGroupSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    Object groupName=requestBody.get("groupName");
    Object id=requestBody.get("id");
    ValidationUtils.notNull(id, "apiId不能为空");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APIGROUPREL");//删除API绑定的API组
    baseService.remove(id.toString());
    if(!StringUtils.isEmpty(groupName)) {
      String groupNameStr=groupName.toString();
      String[] groupIds=groupNameStr.split(",");
      Map<String, Object> requestMap=new HashMap<>();
      requestMap.put("apiId", id);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APIGROUPREL");//API绑定API组
      for(int i=0;i<groupIds.length;i++) {
       requestMap.put("apiGroupId", groupIds[i]);
       baseService.save(requestMap);
      }
    }
    return "ok";
  }
}
