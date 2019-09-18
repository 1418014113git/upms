/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.api;

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
import com.nmghr.upms.handler.impl.channel.ChannelSaveHandler;
import com.nmghr.util.JwtUtils;

/**
 * 修改API信息接口.
 *
 * @author wujin
 * @date 2018年11月6日 下午2:54:21
 * @version 1.0
 */
@Service("apiUpdateHandler")
@SuppressWarnings("all")
public class ApiUpdateHandler extends AbstractUpdateHandler {
  private final static String API_TYPE_SERVICE = "0";// 服务注册的API

  public ApiUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    validation(requestBody);// 验证参数
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "api");
    return baseService.update(id, requestBody);
  }

  private static void validation(Map<String, Object> requestBody) {
    Object apiName = requestBody.get("apiName");
    ValidationUtils.notNull(apiName, "API名称不能为空");
    Object apiCode = requestBody.get("apiCode");
    ValidationUtils.notNull(apiCode, "API编码不能为空");
    Object apiUrl = requestBody.get("apiUrl");
    ValidationUtils.notNull(apiUrl, "API地址不能为空");
    Object apiType = requestBody.get("apiType");
    ValidationUtils.notNull(apiType, "API类型不能为空");
    if (API_TYPE_SERVICE.equals(apiType)) {
      Object apiServiceId = requestBody.get("apiServiceId");
      ValidationUtils.notNull(apiServiceId, "服务注册名不能为空");
    }
    Object apiMethod = requestBody.get("apiMethod");
    ValidationUtils.notNull(apiMethod, "API访问方法不能为空");
    Object apiSchema = requestBody.get("apiSchema");
    ValidationUtils.notNull(apiSchema, "API协议名不能为空");
    Object enabled = requestBody.get("enabled");
    ValidationUtils.notNull(enabled, "启用状态不能为空");
    Object version = requestBody.get("version");
    ValidationUtils.notNull(version, "版本号不能为空");
  }

}
