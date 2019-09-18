/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.api;

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
 * 查询API对应的API组.
 *
 * @author wujin
 * @date 2018年11月7日 上午11:13:25
 * @version 1.0
 */
@Service("apigroupsQueryHandler")
@SuppressWarnings("all")
public class ApiGroupQueryHandler extends AbstractQueryHandler {
  private final static int API_GROUP_CHOOSE_TRUE = 1;//API组已选中
  private final static int API_GROUP_CHOOSE_FALSE = 0;//API组未选中

  public ApiGroupQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    Object apiId = requestMap.get("apiId");
    ValidationUtils.notNull(apiId, "api的Id不能为空");
    Map<String, Object> requsetBody = new HashMap<>();
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APIGROUPLIST");// 获取API组信息
    List<Map<String, Object>> apiGroupList =
        (List<Map<String, Object>>) baseService.list(requsetBody);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APIGROUPSTR");// 获取当前API所在的API组
    Object apiGroup = baseService.get(requestMap.get("apiId").toString());
    if (!StringUtils.isEmpty(apiGroup)) {
      Map<String, Object> groupMap = (Map<String, Object>) apiGroup;
      String groupStr = groupMap.get("groupStr") + "";
      String[] group = groupStr.split(",");
      for (int i = 0; i < apiGroupList.size(); i++) {
        Map<String, Object> apiGroupMap = apiGroupList.get(i);
        String apiGroupId = apiGroupMap.get("id") + "";
        boolean bol = false;
        for (int j = 0; j < group.length; j++) {
          String groupId = group[j];
          if (groupId.equals(apiGroupId)) {
            bol = true;
          }
        }
        if (bol) {
          apiGroupMap.put("isChoose", API_GROUP_CHOOSE_TRUE);
        } else {
          apiGroupMap.put("isChoose", API_GROUP_CHOOSE_FALSE);
        }
      }
    } else {
      for (int i = 0; i < apiGroupList.size(); i++) {
        Map<String, Object> apiGroupMap = apiGroupList.get(i);
        String apiGroupId = apiGroupMap.get("id") + "";
        apiGroupMap.put("isChoose", API_GROUP_CHOOSE_FALSE);
      }
    }
    return apiGroupList;
  }

}
