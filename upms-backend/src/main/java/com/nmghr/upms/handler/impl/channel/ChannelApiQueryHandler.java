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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

/**
 * 渠道获取API接口.
 *
 * @author wujin  
 * @date 2018年11月14日 下午5:50:17 
 * @version 1.0   
 */
@Service("channelapiQueryHandler")
@SuppressWarnings("all")
public class ChannelApiQueryHandler extends AbstractQueryHandler{

  public ChannelApiQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CHANNELLIST");// 获取渠道列表
    List<Map<String, Object>> channelList=(List<Map<String, Object>>) baseService.list(requestMap);
    for(int i=0;i<channelList.size();i++) {
      Map<String, Object> channelMap=channelList.get(i);
      Object channelId=channelMap.get("id");
      requestMap.put("channelId", channelId);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CHANNELGETAPIS");//渠道获取API
      List<Map<String,Object>> apis=(List<Map<String, Object>>) baseService.list(requestMap);
      List<Object> apiList=new ArrayList<>();
      for(int j=0;j<apis.size();j++) {
        Map<String,Object> apiMap=apis.get(j);
        Object id=apiMap.get("id");
        apiList.add(id);
      }
      channelMap.put("apis", apiList);
    }
    return channelList;
  }
}
