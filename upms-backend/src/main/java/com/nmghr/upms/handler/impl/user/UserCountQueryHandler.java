/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.basic.core.util.ValidationUtils;

/**
 * 统计三十天的用户总数.
 *
 * @author wujin
 * @date 2018年11月26日 上午11:01:45
 * @version 1.0
 */
@Service("usercountQueryHandler")
@SuppressWarnings("all")
public class UserCountQueryHandler extends AbstractQueryHandler {
  private final static int ROLE_SUPER_ADMIN = 1;// 超级管理员
  private final static int ROLE_APP = 2;// 应用管理员
  private final static int ROLE_DEPART = 3;// 部门管理员
  private final static int ROLE_CHANNEL = 4;// 渠道管理员

  public UserCountQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
    Object userId = requestMap.get("userId");
    ValidationUtils.notNull(userId, "用户ID不能为空");
    Object roleId = requestMap.get("roleId");
    ValidationUtils.notNull(roleId, "角色ID不能为空");
    int role = Integer.parseInt(roleId.toString());
    if (ROLE_SUPER_ADMIN == role) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERCOUNT");// 超级管理员查询用户统计
      Map<String, Object> userMap = (Map<String, Object>) baseService.get("");
      List<Integer> lastList = getLastList(userMap);
      return lastList;
    } else if (ROLE_APP == role) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERGETAPPSTRS");// 获取用户授权的除UPMS应用
      Map<String, Object> appStrMap = (Map<String, Object>) baseService.get(userId.toString());
      if (CollectionUtils.isEmpty(appStrMap)) {
        requestMap.put("appStr", "");
      } else {
        requestMap.put("appStr", appStrMap.get("appStr"));
      }
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPUSERCOUNT");// 应用管理员查询用户统计
      List<Map<String, Object>> appList = (List<Map<String, Object>>) baseService.list(requestMap);
      if (appList.size() == 0) {
        return new ArrayList<Integer>();
      } else {
        Map<String, Object> appMap = appList.get(0);
        List<Integer> lastList = getLastList(appMap);
        return lastList;
      }
    } else if (ROLE_DEPART == role) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERGETDEPARTSTRS");// 获取用户授权的部门
      Map<String, Object> departStrMap = (Map<String, Object>) baseService.get(userId.toString());
      if (CollectionUtils.isEmpty(departStrMap)) {
        requestMap.put("departStr", "");
      } else {
        requestMap.put("departStr", departStrMap.get("departStr"));
      }
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPARTUSERCOUNT");// 部门管理员查询用户统计
      List<Map<String, Object>> departList =
          (List<Map<String, Object>>) baseService.list(requestMap);
      if (departList.size() == 0) {
        return new ArrayList<Integer>();
      } else {
        Map<String, Object> departMap = departList.get(0);
        List<Integer> lastList = getLastList(departMap);
        return lastList;
      }
    } else if (ROLE_CHANNEL == role) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "CHANNELUSERCOUNT");// 渠道管理员查询用户统计
      List<Map<String, Object>> channelList =
          (List<Map<String, Object>>) baseService.list(requestMap);
      if (channelList.size() == 0) {
        return new ArrayList<Integer>();
      } else {
        Map<String, Object> channelMap = channelList.get(0);
        List<Integer> lastList = getLastList(channelMap);
        return lastList;
      }
    } else {
      return null;
    }
  }

  private List<Integer> getLastList(Map<String, Object> userMap) {
    if (CollectionUtils.isEmpty(userMap)) {
      return new ArrayList<Integer>();
    } else {
      List<Integer> lastList = new ArrayList<Integer>();
      lastList.add(Integer.parseInt(userMap.get("count30").toString()));
      lastList.add(Integer.parseInt(userMap.get("count29").toString()));
      lastList.add(Integer.parseInt(userMap.get("count28").toString()));
      lastList.add(Integer.parseInt(userMap.get("count27").toString()));
      lastList.add(Integer.parseInt(userMap.get("count26").toString()));
      lastList.add(Integer.parseInt(userMap.get("count25").toString()));
      lastList.add(Integer.parseInt(userMap.get("count24").toString()));
      lastList.add(Integer.parseInt(userMap.get("count23").toString()));
      lastList.add(Integer.parseInt(userMap.get("count22").toString()));
      lastList.add(Integer.parseInt(userMap.get("count21").toString()));
      lastList.add(Integer.parseInt(userMap.get("count20").toString()));
      lastList.add(Integer.parseInt(userMap.get("count19").toString()));
      lastList.add(Integer.parseInt(userMap.get("count18").toString()));
      lastList.add(Integer.parseInt(userMap.get("count17").toString()));
      lastList.add(Integer.parseInt(userMap.get("count16").toString()));
      lastList.add(Integer.parseInt(userMap.get("count15").toString()));
      lastList.add(Integer.parseInt(userMap.get("count14").toString()));
      lastList.add(Integer.parseInt(userMap.get("count13").toString()));
      lastList.add(Integer.parseInt(userMap.get("count12").toString()));
      lastList.add(Integer.parseInt(userMap.get("count11").toString()));
      lastList.add(Integer.parseInt(userMap.get("count10").toString()));
      lastList.add(Integer.parseInt(userMap.get("count9").toString()));
      lastList.add(Integer.parseInt(userMap.get("count8").toString()));
      lastList.add(Integer.parseInt(userMap.get("count7").toString()));
      lastList.add(Integer.parseInt(userMap.get("count6").toString()));
      lastList.add(Integer.parseInt(userMap.get("count5").toString()));
      lastList.add(Integer.parseInt(userMap.get("count4").toString()));
      lastList.add(Integer.parseInt(userMap.get("count3").toString()));
      lastList.add(Integer.parseInt(userMap.get("count2").toString()));
      lastList.add(Integer.parseInt(userMap.get("count1").toString()));
      return lastList;
    }
  }
}
