///*
// * Copyright (C) 2018 @内蒙古慧瑞.
// *
// * Unless required by applicable law or agreed to in writing, software distributed under the License
// * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
// * or implied. See the License for the specific language governing permissions and limitations under
// * the License.
// */
//
//package com.nmghr.upms.controller;
//
//import com.nmghr.basic.common.Constant;
//import com.nmghr.basic.common.Result;
//import com.nmghr.basic.core.common.LocalThreadStorage;
//import com.nmghr.basic.core.service.IBaseService;
//import com.nmghr.upms.config.UpmsErrorEnum;
//import com.nmghr.upms.config.UpmsGlobalException;
//import com.nmghr.util.GetIpUtil;
//import org.dom4j.Element;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.regex.Pattern;
//
///**
// * UK登录测试
// *
// */
//@RestController
//@RequestMapping("/ukTest")
//public class UKLoginTestController {
//
//  Logger logger = LoggerFactory.getLogger(this.getClass());
//  @Autowired
//  @Qualifier("baseService")
//  private IBaseService baseService;
//
//  @GetMapping("/random")
//  @ResponseBody
//  public Object random() throws Exception {
//    String randNum = generateRandomNum();
//    return Result.ok(randNum);
//  }
//
//  @PostMapping("/login")
//  @ResponseBody
//  public Object login(HttpSession session, HttpServletRequest request, @RequestBody Map<String, Object> requestBody)
//      throws Exception {
//    Object obj = null;
//    Map<String, Object> result = new HashMap<>();
//    boolean isSuccess = true;
//    String errCode = "999999", errDesc = null;
//
//    try {
//
//
//
//      if (isSuccess) {
//        logger.info("身份认证成功！\n");
//        String idcard = String.valueOf(requestBody.get("sfhm"));
//        requestBody.put("uk", idcard);
//        // 查询UK对应的用户信息
//        String ipAddress = GetIpUtil.getIpAddr(request);
//        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "userextuk");
//        List<Map<String, Object>> mapList = (List<Map<String, Object>>) baseService.list(requestBody);
//        if (mapList == null || mapList.size() == 0 || mapList.get(0)==null) {
//          // 保存登录失败信息
//          Map<String, Object> saveParamMap = new HashMap<String, Object>();
//          saveParamMap.put("ipAddress", ipAddress);
//          saveParamMap.put("loginStatus", 0);
//          saveParamMap.put("loginType", "2");
//          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSLOGINLOG");
//          baseService.save(saveParamMap);
//          throw new Exception("UK没有对应用户");
//        }
//        Map<String, Object> userInfo = mapList.get(0);
//        String token = getToken();
//        result.put("userId", userInfo.get("id"));
//        result.put("userName", userInfo.get("user_name"));
//        result.put("userToken", token);
//        result.put("accessToken", token);
//        result.put("userKey", userInfo.get("user_key"));
//        result.put("userSecret", userInfo.get("user_secret"));
//        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERGETROLE");// 获取用户角色信息
//        List<Map<String, Object>> roles = (List<Map<String, Object>>) baseService.list(result);
//        if (roles.size() == 0) {
//          String resMessage = "该用户在当前应用下无角色无法登陆";
//          throw new UpmsGlobalException(UpmsErrorEnum.UNLOGIN.getCode(), resMessage);
//        }
//        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERTOKEN");// 修改用户token
//        baseService.update(mapList.get(0).get("id") + "", result);
//
//        Map<String, Object> saveParamMap = new HashMap<String, Object>();
//        saveParamMap.put("realName", userInfo.get("real_name"));
//        saveParamMap.put("userName", userInfo.get("user_name"));
//        saveParamMap.put("ipAddress", ipAddress);
//        saveParamMap.put("loginStatus", 1);
//        saveParamMap.put("loginType", "2");
//        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSLOGINLOG");
//        baseService.save(saveParamMap);
//      }
//    } catch (Exception e) {
//      errDesc = e.getMessage();
//      logger.error("认证失败！", e);
//      if (StringUtils.isEmpty(errCode)) {
//        errCode = "999999";
//      }
//      return Result.fail(errCode, errDesc);
//    }
//    if (!isSuccess) {
//      if (StringUtils.isEmpty(errCode)) {
//        errCode = "999999";
//      }
//      return Result.fail(errCode, errDesc);
//    } else {
//      logger.info("处理数据结束，一切正常！\n");
//      return Result.ok(result);
//    }
//  }
//
//  private String generateRandomNum() {
//    String num = "1234567890abcdefghijklmnopqrstopqrstuvwxyz";
//    int size = 6;
//    char[] charArray = num.toCharArray();
//    StringBuffer sb = new StringBuffer();
//    for (int i = 0; i < size; i++) {
//      sb.append(charArray[((int) (Math.random() * 10000) % charArray.length)]);
//    }
//    return sb.toString();
//  }
//
//  @SuppressWarnings("unchecked")
//  private String getToken() throws Exception {
//    String token = UUID.randomUUID().toString();
//    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERS");// 根据用户token获取用户信息
//    Map<String, Object> requestBody = new HashMap<>();
//    requestBody.put("userToken", token);
//    List<Map<String, Object>> userList = (List<Map<String, Object>>) baseService.list(requestBody);
//    if (userList.size() > 0) {
//      return getToken();
//    }
//    return token;
//  }
//
//
//}
