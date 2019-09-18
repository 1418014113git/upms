/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.upms.handler.impl.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;

/**
 * 用户分配应用接口.
 *
 * @author wujin  
 * @date 2018年11月12日 下午3:46:04 
 * @version 1.0   
 */
@Service("userallotappSaveHandler")
@SuppressWarnings("all")
public class UserAllotAppSaveHandler extends AbstractSaveHandler{

  public UserAllotAppSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    Object appId=requestBody.get("appId");//应用id
    ValidationUtils.notNull(appId, "应用id不能为空");
    Object userId=requestBody.get("userId");//用户id
    ValidationUtils.notNull(userId, "用户id不能为空");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERAPPREL");//用户获取应用列表
    List<Map<String, Object>> appList=(List<Map<String, Object>>) baseService.list(requestBody);
    if(appList.size()==0) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERSETDEFAULTAPP");//用户设置默认应用
      Map<String, Object> requestMap=new HashMap<>();
      requestMap.put("defaultAppId", appId);
      baseService.update(userId.toString(), requestMap);
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERAPPREL");//用户分配应用
    return baseService.save(requestBody);
  }
}
