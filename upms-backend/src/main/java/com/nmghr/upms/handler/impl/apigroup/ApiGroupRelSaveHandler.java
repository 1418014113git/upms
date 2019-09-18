/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.upms.handler.impl.apigroup;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;

/**
 * API组绑定API接口.
 *
 * @author wujin  
 * @date 2018年11月7日 下午5:37:38 
 * @version 1.0   
 */
@Service("apigrouprelSaveHandler")
@SuppressWarnings("all")
public class ApiGroupRelSaveHandler extends AbstractSaveHandler{

  public ApiGroupRelSaveHandler(IBaseService baseService) {
    super(baseService);
  }
 
  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    Object apiId=requestBody.get("apiId");
    ValidationUtils.notNull(apiId, "apiId不能为空");
    Object apiGroupId=requestBody.get("apiGroupId");
    ValidationUtils.notNull(apiGroupId, "api组Id不能为空");
    String apiIds=apiId.toString();
    String [] aIdStr=apiIds.split(",");
    Map<String, Object> requestMap=new HashMap<>();
    requestMap.put("apiGroupId", apiGroupId);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APIGROUPREL");// API组绑定API
    for(int i=0;i<aIdStr.length;i++) {
      requestMap.put("apiId", aIdStr[i]);
      baseService.save(requestMap);
    }
    return "ok";
  }
  
}
