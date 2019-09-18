/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.menu;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.basic.core.util.ValidationUtils;

/**
 * 修改按钮的接口
 * @author brook
 * 2019年3月1日 下午2:26:39
 */
@Service("buttonUpdateHandler")
@SuppressWarnings("all")
public class ButtonUpdateHandler extends AbstractUpdateHandler {
  private final static String BUTTON_TYPE_LINK = "2";// 按钮类型为超链接类型

  public ButtonUpdateHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @Transactional
  public Object update(String id, Map<String, Object> requestBody) throws Exception {
    validation(requestBody);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BUTTON");
    String menuId = requestBody.get("menuId").toString();
    String appId = requestBody.get("appId").toString();
    String buttonCode = requestBody.get("buttonCode").toString();
    // 检查唯一性
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BUTTON");
    HashMap map = (HashMap) baseService.get(requestBody);
    if (map != null && !String.valueOf(map.get("id")).equals(id)) {
      throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(),
          "当前系统下按钮编码已经存在,请重新设置!");
    } else {
      return baseService.update(id, requestBody);
    }

  }

  /**
   * 验证参数.
   * 
   * @param requestBody 请求体
   */
  public static void validation(Map<String, Object> requestBody) {
    Object menuId = requestBody.get("menuId");
    ValidationUtils.notNull(menuId, "所属菜单ID不能为空");
    Object appId = requestBody.get("appId");
    ValidationUtils.notNull(appId, "所属应用ID不能为空");
    Object buttonName = requestBody.get("buttonName");
    ValidationUtils.notNull(buttonName, "按钮名称不能为空");
    Object buttonType = requestBody.get("buttonType");
    ValidationUtils.notNull(buttonType, "按钮类型不能为空");
    Object enabled = requestBody.get("enabled");
    ValidationUtils.notNull(enabled, "是否启用不能为空");
    Object deleteable = requestBody.get("deleteable");
    ValidationUtils.notNull(deleteable, "是否可删除不能为空");
    if (BUTTON_TYPE_LINK.equals(buttonType)) {
      Object linkTo = requestBody.get("linkTo");
      ValidationUtils.notNull(linkTo, "链接地址不能为空");
    }
    Object buttonCode = requestBody.get("buttonCode");
    ValidationUtils.notNull(buttonCode, "按钮编码不能为空");
    ValidationUtils.length(buttonCode, 5, 50, "按钮编码长度为5-50长度");
  }
}
