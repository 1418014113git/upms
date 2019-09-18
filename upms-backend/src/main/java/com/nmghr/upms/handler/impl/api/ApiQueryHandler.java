/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.upms.handler.impl.api;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

/**
 * 通过id获取API信息.
 *
 * @author wujin  
 * @date 2018年11月5日 下午4:19:55 
 * @version 1.0   
 */
@Service("apiQueryHandler")
@SuppressWarnings("all")
public class ApiQueryHandler  extends AbstractQueryHandler{

  public ApiQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object get(String id) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "API");// 获取API信息
    Object obj=baseService.get(id);
    Map<String, Object> apiMsg=(Map<String, Object>) obj;
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APIGROUPREL");// 获取API的组信息
    Object apiGroup=baseService.get(id);
    if(StringUtils.isEmpty(apiGroup)) {
      apiMsg.put("apiGroupId", "");
      apiMsg.put("apiGroupName", "");
    }else {
      Map<String, Object>  apiGroupMsg=(Map<String, Object>) apiGroup;
      apiMsg.put("apiGroupId", apiGroupMsg.get("apiGroupId"));
      apiMsg.put("apiGroupName", apiGroupMsg.get("apiGroupName"));
    }
    return  apiMsg;
  }
}
