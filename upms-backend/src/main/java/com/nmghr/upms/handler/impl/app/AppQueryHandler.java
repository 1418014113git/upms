/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.upms.config.UpmsErrorEnum;
import com.nmghr.upms.config.UpmsGlobalException;

/**
 * 查询应用列表接口.
 *
 * @author wujin
 * @date 2018年11月8日 下午5:07:38
 * @version 1.0
 */
@Service("appQueryHandler")
@SuppressWarnings("all")
public class AppQueryHandler extends AbstractQueryHandler {
  private final static String GET_APP_TYPE_ALLOT = "allot";// 在分配处获取应用列表
  private final static int APP_DEFAULT_TRUE = 1;// 应用是默认
  private final static int APP_DEFAULT_FALSE = 0;// 应用不是默认
  private final static int APP_CHOOSE_FALSE = 0;// 应用没有选中
  private final static int APP_CHOOSE_TRUE = 1;// 应用选中
  private final static String APP_CODE_UPMS = "upms";// UPMS应用编码

  public AppQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    Object userId = requestMap.get("userId");
    ValidationUtils.notNull(userId, "用户id不能为空");
    Object appCode = requestMap.get("appCode");
    ValidationUtils.notNull(appCode, "应用编码不能为空");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USER");// 获取用户信息
    Map<String, Object> userMap = (Map<String, Object>) baseService.get(userId.toString());
    if (CollectionUtils.isEmpty(userMap)) {
      String resMessage = "当前登陆用户已不存在，如有问题请联系管理员";
      throw new UpmsGlobalException(UpmsErrorEnum.UNLOGIN.getCode(), resMessage);
    }
    Object appId = userMap.get("defaultAppId");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APP");// 获取应用信息
    List<Map<String, Object>> appList = (List<Map<String, Object>>) baseService.list(requestMap);
    if (!APP_CODE_UPMS.equals(appCode)) {
      List<Map<String, Object>> lastList = new ArrayList<>();
      for (int i = 0; i < appList.size(); i++) {
        Map<String, Object> appMap = appList.get(i);
        Object apCode = appMap.get("appCode");
        if (appCode.equals(apCode)) {
          lastList.add(appMap);
        }
      }
      appList = lastList;
    }
    if (StringUtils.isEmpty(appId)) {
      for (int i = 0; i < appList.size(); i++) {
        Map<String, Object> appMap = appList.get(i);
        appMap.put("isDefault", 0);
      }
    } else {
      for (int i = 0; i < appList.size(); i++) {
        Map<String, Object> appMap = appList.get(i);
        Object id = appMap.get("id");
        if (appId.equals(id)) {
          appMap.put("isDefault", APP_DEFAULT_TRUE);// 是否为默认应用：0：否1：是
        } else {
          appMap.put("isDefault", APP_DEFAULT_FALSE);
        }
      }
    }
    if (!APP_CODE_UPMS.equals(appCode)) {
      appList.get(0).put("isDefault", APP_DEFAULT_TRUE);
    }
    Object type = requestMap.get("type");
    if (!StringUtils.isEmpty(type)) {
      if (GET_APP_TYPE_ALLOT.equals(type)) {
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERGETAPPSTR");// 根据用户id获取应用信息
        Map<String, Object> appStrMap = (Map<String, Object>) baseService.get(userId.toString());
        if (StringUtils.isEmpty(appStrMap)) {
          for (int i = 0; i < appList.size(); i++) {
            Map<String, Object> appMap = appList.get(i);
            appMap.put("isChoose", APP_CHOOSE_FALSE);
          }
        } else {
          Object appStr = appStrMap.get("appStr");
          if (StringUtils.isEmpty(appStr)) {
            for (int i = 0; i < appList.size(); i++) {
              Map<String, Object> appMap = appList.get(i);
              appMap.put("isChoose", APP_CHOOSE_FALSE);
            }
          } else {
            String[] appStrs = appStr.toString().split(",");
            for (int i = 0; i < appList.size(); i++) {
              Map<String, Object> appMap = appList.get(i);
              Object id = appMap.get("id") + "";
              boolean bol = false;
              for (int j = 0; j < appStrs.length; j++) {
                String aId = appStrs[j];
                if (aId.equals(id)) {
                  bol = true;
                }
              }
              if (bol) {
                appMap.put("isChoose", APP_CHOOSE_TRUE);// 是否为选中应用：0：否1：是
              } else {
                appMap.put("isChoose", APP_CHOOSE_FALSE);
              }
            }
          }
        }
      }
    }
    return appList;
  }

}
