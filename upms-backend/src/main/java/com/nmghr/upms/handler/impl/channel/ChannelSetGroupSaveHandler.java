/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.upms.handler.impl.channel;

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
 * 渠道设置分组接口.
 *
 * @author wujin  
 * @date 2018年11月7日 上午11:53:04 
 * @version 1.0   
 */
@Service("channelsetgroupSaveHandler")
@SuppressWarnings("all")
public class ChannelSetGroupSaveHandler extends AbstractSaveHandler{

  public ChannelSetGroupSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    Object groupName=requestBody.get("groupName");
    Object id=requestBody.get("id");
    ValidationUtils.notNull(id, "渠道ID不能为空");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CHANNELGROUP");//删除渠道绑定的API组
    baseService.remove(id.toString());
    if(!StringUtils.isEmpty(groupName)) {
      String groupNameStr=groupName.toString();
      String[] groupIds=groupNameStr.split(",");
      Map<String, Object> requestMap=new HashMap<>();
      requestMap.put("channelId", id);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CHANNELGROUPREL");//渠道绑定API组
      for(int i=0;i<groupIds.length;i++) {
       requestMap.put("apiGroupId", groupIds[i]);
       baseService.save(requestMap);
      }
    }
    return "ok";
  }
  
}
