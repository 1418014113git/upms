/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.channel;

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
 * 查询渠道对应的API组.
 *
 * @author wujin
 * @date 2018年11月7日 上午10:34:41
 * @version 1.0
 */
@Service("channelgroupQueryHandler")
@SuppressWarnings("all")
public class ChannelGroupQueryHandler extends AbstractQueryHandler {
  private final static int API_GROUP_CHOOSE_TRUE = 1;//API组已选
  private final static int API_GROUP_CHOOSE_FALSE = 0;//API组未选
  
  public ChannelGroupQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    Object channelId = requestMap.get("channelId");
    ValidationUtils.notNull(channelId, "渠道Id不能为空");
    Map<String, Object> requsetBody = new HashMap<>();
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APIGROUPLIST");// 获取API组信息
    List<Map<String, Object>> apiGroupList =
        (List<Map<String, Object>>) baseService.list(requsetBody);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CHANNELGROUPSTR");// 获取当前渠道已绑定的API组
    Object channelGroup = baseService.get(requestMap.get("channelId").toString());
    if (!StringUtils.isEmpty(channelGroup)) {
      Map<String, Object> channelGroupMap = (Map<String, Object>) channelGroup;
      String groupStr = channelGroupMap.get("groupStr") + "";
      String[] group = groupStr.split(",");
      for (int i = 0; i < apiGroupList.size(); i++) {
        boolean bol = false;
        Map<String, Object> apiGroupMap = apiGroupList.get(i);
        String apiGroupId = apiGroupMap.get("id") + "";
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
