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
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.basic.core.util.ValidationUtils;

/**
 * api组id获取api列表.
 *
 * @author wujin  
 * @date 2018年11月7日 下午5:00:31 
 * @version 1.0   
 */
@Service("apigroupgetapiQueryHandler")
@SuppressWarnings("all")
public class ApiGroupGetApiQueryHandler extends AbstractQueryHandler{
  private final static int API_CHOOSE_TRUE = 1;//API已选中
  private final static int API_CHOOSE_FALSE = 0;//API未选中

  public ApiGroupGetApiQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    Object apiGroupid = requestMap.get("apiGroupId");
    ValidationUtils.notNull(apiGroupid, "API组Id不能为空");
    Map<String, Object> requsetBody = new HashMap<>();
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APILIST");// 获取API信息
    List<Map<String, Object>> apiList =
        (List<Map<String, Object>>) baseService.list(requsetBody);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APISTR");// 获取当前API组已绑定的API
    Object groupApi = baseService.get(requestMap.get("apiGroupId").toString());
    if (!StringUtils.isEmpty(groupApi)) {
      Map<String, Object> groupMap = (Map<String, Object>) groupApi;
      String apiStr = groupMap.get("apiStr") + "";
      String[] api = apiStr.split(",");
      for (int i = 0; i < apiList.size(); i++) {
        boolean bol = false;
        Map<String, Object> apiMap = apiList.get(i);
        String apiGroupId = apiMap.get("id") + "";
        for (int j = 0; j < api.length; j++) {
          String groupId = api[j];
          if (groupId.equals(apiGroupId)) {
            bol = true;
          }
        }
        if (bol) {
          apiMap.put("isChoose", API_CHOOSE_TRUE);
        } else {
          apiMap.put("isChoose", API_CHOOSE_FALSE);
        }
      }
    } else {
      for (int i = 0; i < apiList.size(); i++) {
        Map<String, Object> apiGroupMap = apiList.get(i);
        String apiGroupId = apiGroupMap.get("id") + "";
        apiGroupMap.put("isChoose", API_CHOOSE_FALSE);
      }
    }
    return apiList;
  }
}
