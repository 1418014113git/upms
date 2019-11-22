/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nmghr.upms.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.upms.config.UpmsErrorEnum;
import com.nmghr.upms.config.UpmsGlobalException;
import com.nmghr.util.GetIpUtil;
import com.nmghr.util.JitGatewayUtil;

import sun.misc.BASE64Encoder;

/**
 * UK登录
 *
 * @author yanjing
 * @version 1.0
 * @date 2018年9月25日 下午3:36:40
 */
@RestController
@RequestMapping("/pki")
public class PKILoginController {
  
  private static final Logger logger = LoggerFactory.getLogger(PKILoginController.class);
  
  Pattern p = Pattern.compile("(\\d{17}[0-9Xx])");
  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;
  @Autowired
  private JitGatewayUtil jitGatewayUtil;
  
  @Value("${pki.authURL}")
  private String authURL;
  @Value("${pki.appId}")
  private String appId;
  @Value("${pki.randomFrom}")
  private int randomFrom;
  @Value("${pki.accessControl}")
  private boolean accessControl;
  @Value("${pki.qRCodeAuth}")
  private boolean qRCodeAuth;

  /**
   * PKI前端控件需要用到的随机参数
   * author wangpengwei
   * @return 获取到的随机数
   * @throws Exception
   */
  @GetMapping("/random")
  @ResponseBody
  public Object random(HttpSession session, HttpServletRequest request) throws Exception {
    String randNum = jitGatewayUtil.generateRandomNum(randomFrom);
 // 设置认证原文到session，用于程序向后传递，通讯报文中使用
    request.getSession().setAttribute("original_data", randNum);
    return Result.ok(randNum);
  }

  @SuppressWarnings("restriction")
  @PostMapping("/login")
  @ResponseBody
  public Object login(HttpSession session, HttpServletRequest request, @RequestBody Map<String, Object> requestBody)
      throws Exception {
      Map<String, Object> result = new HashMap<>();
      logger.info("身份认证开始！\n");

      // 设置认证方式、报文token、session中认证的随机密码、客户端认证原文、认证随机数、远程地址
      //认证方式
      //jitGatewayUtil.jitGatewayUtilBean.setAuthMode(request.getParameter(JitGatewayUtil.AuthConstant.MSG_AUTH_MODE));
      jitGatewayUtil.jitGatewayUtilBean.setAuthMode("cert");

      //网关的时候应该默认是有token，直接访问服务应该是null,这块是需要在生产内网环境测试验证
      jitGatewayUtil.jitGatewayUtilBean.setToken(request.getParameter(JitGatewayUtil.AuthConstant.MSG_TOKEN));
      //jitGatewayUtil.jitGatewayUtilBean.setToken(\);

      //保存在session中的随机数
      jitGatewayUtil.jitGatewayUtilBean.setOriginal_data(this.getProperties(request.getSession(),JitGatewayUtil.AuthConstant.KEY_ORIGINAL_DATA));
      
      //前端PKI传的original参数
      jitGatewayUtil.jitGatewayUtilBean.setOriginal_jsp(String.valueOf(requestBody.get("original")));
      //前端PKI传的signed_data参数
      jitGatewayUtil.jitGatewayUtilBean.setSigned_data(String.valueOf(requestBody.get("signed_data")));
      jitGatewayUtil.jitGatewayUtilBean.setRemoteAddr(request.getRemoteAddr());
      // 调用网关工具类方式进行身份认证
      jitGatewayUtil.auth();
     
      // 设置认证返回信息：isSuccess 认证是否成功,true成功/false失败;errCode 错误码;errDesc 错误描述
      session.setAttribute("isSuccess",jitGatewayUtil.authResult.isSuccess());
      if (!jitGatewayUtil.authResult.isSuccess()) {
          // 认证不通过
          if (jitGatewayUtil.isNotNull(jitGatewayUtil.authResult.getErrCode())) {
              session.setAttribute("errCode",jitGatewayUtil.authResult.getErrCode());
          }
          if (jitGatewayUtil.isNotNull(jitGatewayUtil.authResult.getErrDesc())) {
              session.setAttribute("errDesc",jitGatewayUtil.authResult.getErrDesc());
          }
          logger.info("身份认证失败，失败原因：" + jitGatewayUtil.authResult.getErrDesc());
      } else {
          // 认证通过（应用改造，保存至应用的会话中，后续使用）
          // 设置认证属性信息
          session.setAttribute("certAttributeNodeMap",
                  jitGatewayUtil.authResult.getCertAttributeNodeMap());
          // 设置UMS信息
          session.setAttribute("umsAttributeNodeMap",
                  jitGatewayUtil.authResult.getUmsAttributeNodeMap());
          // 设置PMS信息
          session.setAttribute("pmsAttributeNodeMap",
                  jitGatewayUtil.authResult.getPmsAttributeNodeMap());
          // 设置自定义信息
          session.setAttribute("customAttributeNodeMap",
                  jitGatewayUtil.authResult.getCustomAttributeNodeMap());
          logger.info("身份认证成功，认证信息正常返回！\n");
      }
      logger.info("身份认证结束！\n");
      
        //以下是获取到身份证以后，根据身份证获取用户的upms信息
      
   
     
     Map userInfoMap1 =  (Map)session.getAttribute("certAttributeNodeMap");
     Object uk = null;

    	for(Object key :userInfoMap1.keySet()) {
    	String[] k = (String[]) key;
    	if(k[0].equals("_saml_cert_subject") || k[0].equals("X509Certificate.SubjectDN")) {
    		uk = userInfoMap1.get(key);
    		break;
    	}
  
    	}
    	
    
        if (uk == null) {
          throw new Exception("uk参数为null");
        }
        Matcher m = p.matcher(uk.toString());
        m.find();
        String idcard = m.group(0);
        requestBody.put("uk", idcard);
        // 查询UK对应的用户信息
        String ipAddress = GetIpUtil.getIpAddr(request);
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "userextuk");
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) baseService.list(requestBody);
        if (mapList == null || mapList.size() == 0 || mapList.get(0)==null) {
          // 保存登录失败信息
          Map<String, Object> saveParamMap = new HashMap<String, Object>();
          saveParamMap.put("ipAddress", ipAddress);
          saveParamMap.put("loginStatus", 0);
          saveParamMap.put("loginType", "2");
          LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSLOGINLOG");
          baseService.save(saveParamMap);
          throw new Exception("UK没有对应用户");
        }
        Map<String, Object> userInfo = mapList.get(0);
        String token = getToken();
        result.put("userId", userInfo.get("id"));
        result.put("userName", userInfo.get("user_name"));
        result.put("userToken", token);
        result.put("accessToken", token);
        result.put("userKey", userInfo.get("user_key"));
        result.put("userSecret", userInfo.get("user_secret"));
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERGETROLE");// 获取用户角色信息
        List<Map<String, Object>> roles = (List<Map<String, Object>>) baseService.list(result);
        if (roles.size() == 0) {
          String resMessage = "该用户在当前应用下无角色无法登陆";
          throw new UpmsGlobalException(UpmsErrorEnum.UNLOGIN.getCode(), resMessage);
        }
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERTOKEN");// 修改用户token
        baseService.update(mapList.get(0).get("id") + "", result);

        Map<String, Object> saveParamMap = new HashMap<String, Object>();
        saveParamMap.put("realName", userInfo.get("real_name"));
        saveParamMap.put("userName", userInfo.get("user_name"));
        saveParamMap.put("ipAddress", ipAddress);
        saveParamMap.put("loginStatus", 1);
        saveParamMap.put("loginType", "2");
        LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYSLOGINLOG");
        baseService.save(saveParamMap);
      return Result.ok(result);
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


  /**
   * 获取session中的属性值
   * 
   * @param httpSession
   * @param key
   * @return
   */
  private String getProperties(HttpSession httpSession, String key) {
      return httpSession.getAttribute(key) == null ? null : httpSession
              .getAttribute(key).toString();
  }
}
