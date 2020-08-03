/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.controller;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.upms.config.UpmsErrorEnum;
import com.nmghr.upms.config.UpmsGlobalException;
import com.nmghr.upms.config.UpmsProperties;
import com.nmghr.upms.service.IpControlService;
import com.nmghr.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.RenderedImage;
import java.util.*;


/**
 * 用户登录.
 *
 * @author yanjing
 * @version 1.0
 * @date 2018年12月18日 下午15:19:09
 */
@Controller
public class SyhzLoginController {
  private final static int USER_ENABLE_TRUE = 1;// 用户启用状态 默认值
  private final static int USER_ENABLE_FALSE = 0;// 用户禁用状态 默认值
  private final static int APP_ENABLE_TRUE = 1;// 应用启用状态 默认值
  private final static int USER_LOGINFAIL_FIRST = 1;// 登陆失败尝试第一次默认值
  private final static int PARENT_DEPART_ID = -1;// 登陆失败尝试第一次默认值

  private final static String ALAIS_USER_LOGIN_FAIL = "USERLOGINFAIL"; // 用户登录失败维护登录时间及失败次数
  private final static String ALAIS_INIT_USER_LOGIN_FAIL = "INITUSERLOGIN"; // 初始化登录失败时间记录及失败次数

  @Autowired
  private UpmsProperties upmsProperties;// 5分钟内用户登录失败3次后，禁止登录一段时间


  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  @Autowired
  private IpControlService ipControlService;

  @GetMapping("/login/verifycode1")
  public void code(HttpServletRequest request, HttpServletResponse response) throws Exception {


    Map<String, Object> veriCode = VerifyCodeUtils.generateCodeAndPic(); // 将验证码保存到Session中。
    HttpSession session = request.getSession();
    session.setAttribute("vcode", veriCode.get("code")); // 禁止图像缓存。
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
    response.setContentType("image/jpeg"); // 将图像输出到Servlet输出流中。
    ServletOutputStream sos = response.getOutputStream();
    ImageIO.write((RenderedImage) veriCode.get("img"), "jpeg", sos);
    sos.flush();
    sos.close();

  }

  @SuppressWarnings({"unchecked"})
  @PostMapping("/api/login")
  @ResponseBody
  public Object Login(@RequestBody Map<String, Object> requestBody, HttpServletRequest request)
      throws Exception {
//    HttpSession session = request.getSession();
//    String code = (String) session.getAttribute("vcode");
//    ValidationUtils.notNull(code, "验证码不对");
//    ValidationUtils.notNull(requestBody.get("vcode"), "验证码不能为空");

    ValidationUtils.notNull(requestBody.get("userName"), "用户名不能为空");
    ValidationUtils.notNull(requestBody.get("userPwd"), "密码不能为空");

//    String vcode = String.valueOf(requestBody.get("vcode"));// 用户名
    String userName = String.valueOf(requestBody.get("userName"));// 用户名
    String userPwd = String.valueOf(requestBody.get("userPwd"));// 密码

    Map<String, Object> saveParamMap = new HashMap<String, Object>();
    String ipAddress = GetIpUtil.getIpAddr(request);
//    saveParamMap.put("ipAddress", ipAddress);
//    saveParamMap.put("userName", userName);
////    if (!code.equalsIgnoreCase((String) vcode)) {
////      throw new UpmsGlobalException(UpmsErrorEnum.UNLOGIN.getCode(), "验证码不对");
////    }
//
//
//    Map<String,Object> ipCheckMap = new HashMap();
//    ipCheckMap.put("ip",ipAddress);
//    try {
//      boolean ipCheck = (boolean) ipControlService.get(ipCheckMap);
//      if (!ipCheck) {
//        return Result.fail(UpmsErrorEnum.UNLOGIN.getCode(), "IP受限,请联系系统管理员");
//      }
//    }catch (Exception e){
//      throw new UpmsGlobalException(UpmsErrorEnum.UNLOGIN.getCode(), "IP校验异常，请联系管理员");
//    }

    requestBody.put("passWord", userPwd);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "LOGIN");
    Object obj = baseService.get(requestBody);
    if (obj != null) {
      Map<String, Object> userMsg = (Map<String, Object>) obj;
      if ("1".equals(String.valueOf(userMsg.get("pwd_free")))) {
        return Result.fail(UpmsErrorEnum.UNLOGIN.getCode(), "此账号只允许PKI方式登录");
      }
      Object enabled = userMsg.get("enabled");
      saveParamMap.put("realName", userMsg.get("real_name"));
      if (!StringUtils.isEmpty(enabled)) {
        int enable = (int) enabled;
        if (USER_ENABLE_TRUE == enable) {
          Map<String, Object> result = executeLogin(requestBody, userName, userPwd, userMsg);
          if (result.containsKey("code") && UpmsErrorEnum.UNLOGIN.getCode().equals(result.get("code"))) {
            saveLoginLog(saveParamMap, 0);
            return Result.fail(UpmsErrorEnum.UNLOGIN.getCode(), String.valueOf(result.get("message")));
          } else {
            saveLoginLog(saveParamMap, 1);
            return result;
          }
        } else {
          saveLoginLog(saveParamMap, 0);
          return Result.fail(UpmsErrorEnum.UNLOGIN.getCode(), "用户状态异常,请联系系统管理员");
        }
      }
    }
    saveLoginLog(saveParamMap, 0);
    return Result.fail(UpmsErrorEnum.UNLOGIN.getCode(), "用户名或者密码不正确");

  }

  private void saveLoginLog(Map<String, Object> params, int status) throws Exception {
    params.put("loginType", "1");
    params.put("loginStatus", status);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSLOGINLOG");
    baseService.save(params);
  }

  /**
   * 主业务执行登录操作，及登录验证
   *
   * @param userName
   * @param userPwd
   * @param userMsg
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @Transactional
  private Map<String, Object> executeLogin(Map<String, Object> requestBody, String userName,
                                           String userPwd, Map<String, Object> userMsg) throws Exception {
    Map<String, Object> result = new HashMap<>();
    String resMessage = "";
    Object passWord = userMsg.get("pass_word");
    String defaultPwd = upmsProperties.getDefaultPwd();
    String salt = userMsg.get("salt").toString();
    Object tryLoginTime = userMsg.get("try_login_time");// 尝试登录失败时间
//    String pwd = Md5Utils.encryptMd5Password(userName, userPwd, salt);
    String pwd = Sms4Util.Encryption(userPwd, salt);
    String userId = String.valueOf(userMsg.get("id"));
    if (pwd.equals(passWord)) {
//      String dePwd = Md5Utils.encryptMd5Password(userName, defaultPwd, salt);
      String dePwd = Sms4Util.Encryption(defaultPwd, salt);
      if (pwd.equals(dePwd)) {
        /*
         * resMessage = "默认密码为123456，是否修改密码？"; throw new UpmsGlobalException(UpmsErrorEnum.UNLOGIN.getCode(), resMessage);
         */
      }
      if (!StringUtils.isEmpty(tryLoginTime)) {
        Date time = (Date) tryLoginTime;
        long s = (new Date().getTime() - time.getTime()) / (1000 * 60);
        if (s <= upmsProperties.getLoginBanIntervalMinutes()) {
          int loginFailCount = Integer.parseInt(userMsg.get("login_fail_count").toString());
          int loginFailInTime = upmsProperties.getLoginFailInTimes();
          if (loginFailInTime <= loginFailCount) {
            resMessage = "您的账号已锁定，如有问题请联系管理员";
            throw new UpmsGlobalException(UpmsErrorEnum.UNLOGIN.getCode(), resMessage);
          }
        }
      }
      result.put("userId", userId);
      String token = getToken();
      result.put("userToken", token);
      result.put("accessToken", token);
      result.put("userKey", userMsg.get("user_key"));
      result.put("userSecret", userMsg.get("user_secret"));
      result.put("salt", userMsg.get("salt"));
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALAIS_INIT_USER_LOGIN_FAIL);// 初始化登录失败时间记录及次数
      baseService.update(userId, new HashMap<>());
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERGETROLE");// 获取用户角色信息
      List<Map<String, Object>> roles = (List<Map<String, Object>>) baseService.list(result);
      if (roles.size() == 0) {
        resMessage = "该用户在当前应用下无角色无法登陆";
        throw new UpmsGlobalException(UpmsErrorEnum.UNLOGIN.getCode(), resMessage);
      }
      requestBody.put("userToken", token);
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERTOKEN");// 修改用户token
      baseService.update(userId, requestBody);
      return result;
    } else {
      return loginFail(tryLoginTime, userId, userMsg);
    }
  }

  @Transactional
  private Map<String, Object> loginFail(Object tryLoginTime, String userId,
                                        Map<String, Object> userMsg) throws Exception {
    String resMessage = "";
    String code = "";
    Map<String, Object> result = new HashMap<>();
    if (StringUtils.isEmpty(tryLoginTime)) {
      LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALAIS_USER_LOGIN_FAIL);// 修改用户尝试登录失败时间为当前时间
      baseService.update(userId, new HashMap<>());
      int times = upmsProperties.getLoginFailInTimes() - USER_LOGINFAIL_FIRST;
      resMessage = "密码错误，您还有" + times + "次尝试机会";
      code = UpmsErrorEnum.UNLOGIN.getCode();
    } else {
      int loginFailCount = 0;
      if (userMsg.containsKey("login_fail_count")) {
        loginFailCount = Integer.parseInt(String.valueOf(userMsg.get("login_fail_count")));
      }
      int loginFailInTime = upmsProperties.getLoginFailInTimes();
      if (loginFailInTime <= loginFailCount) {
        Date time = (Date) tryLoginTime;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.MINUTE, upmsProperties.getLoginBanIntervalMinutes());
        if (new Date().after(calendar.getTime())) {
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALAIS_INIT_USER_LOGIN_FAIL);// 初始化登录失败时间记录及次数
          baseService.update(userId, new HashMap<>());
          resMessage = "您的账号已锁定，请重新输入密码";
          code = UpmsErrorEnum.UNLOGIN.getCode();
        } else {
          resMessage = "您的账号已锁定，如有问题请联系管理员";
          code = UpmsErrorEnum.UNLOGIN.getCode();
        }
      } else {
        Date time = (Date) tryLoginTime;
        long s = (new Date().getTime() - time.getTime()) / (1000 * 60);
        if (s > upmsProperties.getLoginFailIntervalMinutes()) {
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALAIS_USER_LOGIN_FAIL);// 修改用户尝试登录失败时间为当前时间
          baseService.update(userId, new HashMap<>());
          int times = upmsProperties.getLoginFailInTimes() - USER_LOGINFAIL_FIRST;
          resMessage = "密码错误，您还有" + times + "次尝试机会";
          code = UpmsErrorEnum.UNLOGIN.getCode();
        } else {
          int loginCount = loginFailCount + 1;
          int times = upmsProperties.getLoginFailInTimes() - loginCount;
          if (times > 0) {
            resMessage = "密码错误，您还有" + times + "次尝试机会";
          } else {
            resMessage = "您的账号已锁定，如有问题请联系管理员";
          }
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALAIS_USER_LOGIN_FAIL);// 修改用户尝试登录失败时间为当前时间
          baseService.update(userId, new HashMap<>());
          code = UpmsErrorEnum.UNLOGIN.getCode();
        }
      }
    }
    result.put("code", code);
    result.put("message", resMessage);
    return result;
  }


  @SuppressWarnings("unchecked")
  @GetMapping("/api/user")
  @ResponseBody
  public Object LoginUser(@RequestParam Map<String, Object> requestBody) throws Exception {
    Object appCode = requestBody.get("appCode");
    Object token = requestBody.get("token");
    ValidationUtils.notNull(token, "token不能为空");
    Map<String, Object> requestMap = new HashMap<>();
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYHUSERTOKEN");// 获取用户信息
    Map<String, Object> user = (Map<String, Object>) baseService.get(token.toString());
    if (CollectionUtils.isEmpty(user)) {
      String resMessage = "token异常，请重新登陆";
      throw new UpmsGlobalException(UpmsErrorEnum.UNLOGIN.getCode(), resMessage);
    }
    Object userEnabled = user.get("enabled");
    if (!StringUtils.isEmpty(userEnabled)) {
      int userEnable = Integer.parseInt(userEnabled.toString());
      if (USER_ENABLE_FALSE == userEnable) {
        String resMessage = "用户状态异常,请联系系统管理员";
        throw new UpmsGlobalException(UpmsErrorEnum.UNLOGIN.getCode(), resMessage);
      }
    }
    requestMap.put("userId", user.get("id"));
    requestMap.put("appId", user.get("defaultAppId"));
    //获取用户部门信息
    //获取用户菜单信息
    //获取用户按钮信息
    //获取用户角色信息
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYHUSERGETDEPT");// 用户获取部门信息
    Object departs = baseService.list(requestMap);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYHUSERGETROLE");// 获取用户角色信息(启用状态下的)
    List<Map<String, Object>> roles = (List<Map<String, Object>>) baseService.list(requestMap);

    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYHUSERGETMENU");// 获取用户菜单信息
    Object menus = baseService.list(requestMap);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYHUSERGETBTN");// 获取用户按钮信息
    Object buttons = baseService.list(requestMap);
    List<Object> butList = new ArrayList<>();
    if (!StringUtils.isEmpty(buttons)) {
      List<Map<String, Object>> buttonList = (List<Map<String, Object>>) buttons;
      for (int i = 0; i < buttonList.size(); i++) {
        Map<String, Object> buttonMap = buttonList.get(i);
        Object buttonCode = buttonMap.get("buttonCode");
        butList.add(buttonCode);
      }
    } else {
      butList = new ArrayList<>();
    }

    Map<String, Object> resMap = new HashMap<>();
    resMap.put("user", user);
    resMap.put("deps", departs);
    resMap.put("roles", roles);
    resMap.put("menus", menus);
    resMap.put("resources", butList);
    return resMap;
  }

  @GetMapping("/api/logout")
  @ResponseBody
  public Object LoginOut(HttpServletRequest request) throws Exception {
    Object userId = request.getHeader("userId");
    ValidationUtils.notNull(userId, "用户id不能为空");
    return Result.ok("ok");
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getApps(Object departs, Object roles, Object apps) {
    List<Map<String, Object>> appsList = new ArrayList<Map<String, Object>>();
    if (StringUtils.isEmpty(apps)) {
      return appsList;
    }
    List<Map<String, Object>> appList = (List<Map<String, Object>>) apps;
    for (int i = 0; i < appList.size(); i++) {
      Map<String, Object> appMap = appList.get(i);
      Object appId = appMap.get("appId");
      List<Map<String, Object>> departList = (List<Map<String, Object>>) departs;
      if (departList.size() == 0) {
        appMap.put("departId", "");
        appMap.put("departName", "");
        appMap.put("parentDepartName", "");
        appMap.put("parentDepartType", "");
        appMap.put("departType", "");
        appMap.put("areaCode", "");
        appMap.put("parentAreaCode", "");
        appMap.put("parentDepartId", "");
        appMap.put("departCode", "");
      } else {
        for (int j = 0; j < departList.size(); j++) {
          Map<String, Object> departMap = departList.get(j);
          Object aId = departMap.get("appId");
          if (appId.equals(aId)) {
            appMap.put("departId", departMap.get("id"));
            appMap.put("departName", departMap.get("departName"));
            appMap.put("departType", departMap.get("departType"));
            appMap.put("areaCode", departMap.get("areaCode"));
            appMap.put("parentDepartId", departMap.get("parentDepartId"));
            appMap.put("departCode", departMap.get("departCode"));
            if (PARENT_DEPART_ID == (Integer
                .parseInt(departMap.get("parentDepartId").toString()))) {
              appMap.put("parentDepartName", "");
              appMap.put("parentDepartType", "");
              appMap.put("parentAreaCode", "");
            } else {
              appMap.put("parentDepartName", departMap.get("parentDepartName"));
              appMap.put("parentDepartType", departMap.get("parentDepartType"));
              appMap.put("parentAreaCode", departMap.get("parentAreaCode"));
            }
          }
        }
      }
      List<Map<String, Object>> rolesList = new ArrayList<Map<String, Object>>();
      List<Map<String, Object>> roleList = (List<Map<String, Object>>) roles;
      if (roleList.size() == 0) {
        appMap.put("roles", rolesList);
      } else {
        for (int j = 0; j < roleList.size(); j++) {
          Map<String, Object> roleMap = roleList.get(j);
          Object aId = roleMap.get("appId");
          if (appId.equals(aId)) {
            rolesList.add(roleMap);
          }
        }
        appMap.put("roles", rolesList);
      }
      appsList.add(appMap);
    }
    return appsList;
  }

  @SuppressWarnings("unchecked")
  private String getToken() throws Exception {
    String token = UUID.randomUUID().toString();
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERS");// 根据用户token获取用户信息
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("userToken", token);
    List<Map<String, Object>> userList = (List<Map<String, Object>>) baseService.list(requestBody);
    if (userList.size() > 0) {
      return getToken();
    }
    return token;
  }
}
