/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.channel;

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
import com.nmghr.util.JwtUtils;

/**
 * 渠道信息修改接口.
 *
 * @author wujin
 * @date 2018年11月5日 下午2:54:50
 * @version 1.0
 */
@Service("channelUpdateHandler")
@SuppressWarnings("all")
public class ChannelUpdateHandler extends AbstractUpdateHandler {
  private final static String CHANNEL_VALID_TYPE_JWTIP = "1";// 渠道验证方式JWT+IP

  public ChannelUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    validation(requestBody);// 验证参数
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Object channelEndTime = requestBody.get("channelEndTime");
    Date endTime = dateFormat.parse(channelEndTime.toString());
    Object channelStartTime = requestBody.get("channelStartTime");
    Date startTime = dateFormat.parse(channelStartTime.toString());
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "channel");
    Object channelObj = baseService.get(id);
    Map<String, Object> channelMsg = (Map<String, Object>) channelObj;
    Date sTime = dateFormat.parse(channelMsg.get("channelStartTime").toString());
    Date eTime = dateFormat.parse(channelMsg.get("channelEndTime").toString());
    if (!(endTime.getTime() == eTime.getTime()) || !(startTime.getTime() == sTime.getTime())) {
      Object salt = channelMsg.get("salt");
      Object channelSecret = channelMsg.get("channelSecret");
      Object channelKey = channelMsg.get("channelKey");
      Map<String, Object> map = new HashMap<>();
      map.put("channelKey", channelKey);
//      map.put("channelSecret", channelSecret);
      String jwt = JwtUtils.generateJwt(map, salt.toString(), startTime, endTime);
      requestBody.put("channelJwt", jwt);
    }
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "channel");
    return baseService.update(id, requestBody);
  }

  /**
   * 验证参数.
   * 
   * @param requestBody 请求体
   */
  private static void validation(Map<String, Object> requestBody) {
    Object channelName = requestBody.get("channelName");
    ValidationUtils.notNull(channelName, "渠道名称不能为空");
    Object channelEndTime = requestBody.get("channelEndTime");
    ValidationUtils.notNull(channelEndTime, "有效期结束时间不能为空");
    Object channelStartTime = requestBody.get("channelStartTime");
    ValidationUtils.notNull(channelStartTime, "有效期开始时间不能为空");
    Object validType = requestBody.get("validType");
    ValidationUtils.notNull(validType, "验证方式不能为空");
    if (CHANNEL_VALID_TYPE_JWTIP.equals(validType)) {
      Object ipAddress = requestBody.get("ipAddress");
      ValidationUtils.notNull(ipAddress, "IP地址不能为空");
    }
  }
}
