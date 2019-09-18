/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.channel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.util.JwtUtils;
import com.nmghr.util.SaltUtils;



/**
 * 添加渠道接口.
 *
 * @author wujin
 * @date 2018年11月5日 上午11:04:22
 * @version 1.0
 */

@Service("channelSaveHandler")
@SuppressWarnings("all")
public class ChannelSaveHandler extends AbstractSaveHandler {
  private final static String CHANNEL_VALID_TYPE_JWTIP = "1";// 渠道验证方式JWT+IP
  private final static String ALIAS_CHANNEL = "channel";

  public ChannelSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    validation(requestBody);// 验证参数
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Object channelEndTime = requestBody.get("channelEndTime");
    Date endTime = dateFormat.parse(channelEndTime.toString());
    Object channelStartTime = requestBody.get("channelStartTime");
    Date startTime = dateFormat.parse(channelStartTime.toString());
    Object channelKey = getChannelKey();// 获取渠道key
    Object channelSecret = getChannelSecret(channelKey);// 获取渠道Secret
    String salt = SaltUtils.getSalt();
    Map<String, Object> map = new HashMap<>();
    map.put("channelKey", channelKey);
//    map.put("channelSecret", channelSecret);
    String jwt = JwtUtils.generateJwt(map, salt, startTime, endTime);
    requestBody.put("channelJwt", jwt);
    requestBody.put("channelKey", channelKey);
    requestBody.put("channelSecret", channelSecret);
    requestBody.put("salt", salt);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_CHANNEL);
    Object chanId = baseService.save(requestBody);
    return chanId;
  }

  /**
   * 获取渠道Key.
   * 
   * @return 获取渠道Key
   * @throws Exception
   */
  public Object getChannelKey() throws Exception {
    String channelKey = UUID.randomUUID().toString();
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_CHANNEL);
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("channelKey", channelKey);
    List<Map<String, Object>> channelList =
        (List<Map<String, Object>>) baseService.list(requestBody);
    if (channelList.size() > 0) {
      return getChannelKey();
    }
    return channelKey;
  }

  /**
   * 获取渠道secret.
   * 
   * @param 渠道salt
   * @return 渠道secret
   */
  public Object getChannelSecret(Object channelKey) {
    String channelSecret = UUID.randomUUID().toString();
    return channelSecret;
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
