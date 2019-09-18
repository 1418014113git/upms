/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.user;

import java.util.HashMap;
import java.util.List;
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
 * 用户分配部门.
 *
 * @author wujn
 * @date 2018年11月12日 下午5:03:30
 * @version 1.0
 */
@Service("userallotdepartSaveHandler")
@SuppressWarnings("all")
public class UserAllotDepartSaveHandler extends AbstractSaveHandler {

  public UserAllotDepartSaveHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object save(Map<String, Object> requestBody) throws Exception {
    Object appId = requestBody.get("appId");// 应用id
    ValidationUtils.notNull(appId, "应用id不能为空");
    Object userId = requestBody.get("userId");// 用户id
    ValidationUtils.notNull(userId, "用户id不能为空");
    Object departId = requestBody.get("departId");// 部门id
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERDEPARTREL");// 刪除用戶已分配部门
    baseService.remove(requestBody);
    if (StringUtils.isEmpty(departId)) {
      return "ok";
    } else {
      return baseService.save(requestBody);
    }
  }
}
