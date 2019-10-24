package com.nmghr.upms.controller;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.impl.BaseServiceImpl;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.upms.config.UpmsGlobalException;
import com.nmghr.upms.config.UpmsProperties;
import com.nmghr.util.MailUtils;
import com.nmghr.util.Md5Utils;
import com.nmghr.util.SaltUtils;
import com.nmghr.util.Sms4Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
public class ResetPwdController {
  @Autowired
  private BaseServiceImpl baseService;

  @Autowired
  private MailUtils mailUtils;

  @Autowired
  private UpmsProperties properties;

  @SuppressWarnings({"unchecked"})
  @PostMapping("/resetPwd")
  @ResponseBody
  public Object resetPwd(@RequestBody Map<String, Object> requestBody, HttpServletRequest request)
      throws Exception {
    ValidationUtils.notNull(requestBody.get("idCard"), "邮箱地址不能为空");
    ValidationUtils.notNull(requestBody.get("name"), "账户不能为空");
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("userName", requestBody.get("name"));
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "LOGIN");
    Map<String, Object> userMap = (Map<String, Object>) baseService.get(params);
    if (userMap == null) {
      throw new UpmsGlobalException("888889", "账户和身份证不匹配");
    }
    if (!requestBody.get("idCard").equals(userMap.get("user_id_number"))) {
      throw new UpmsGlobalException("888889", "账户和身份证不匹配");
    }
    // 保存默认密码
    String id = String.valueOf(userMap.get("id"));
//    String userName = String.valueOf(userMap.get("user_name"));
    String salt = SaltUtils.getSalt();
    String newPwd = getRandomString(8);
    String newPwdMd5 = Sms4Util.Encryption(newPwd, salt);
//    String newPwdMd5 = Md5Utils.encryptMd5Password(userName, newPwd, salt);
    requestBody.put("passWord", newPwdMd5);
    requestBody.put("salt", salt);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERRESETPWD");// 用户重置密码
    Object i = baseService.update(id, requestBody);
    if (Integer.parseInt(String.valueOf(i)) > 0) {
      return Result.ok(newPwd);
    }
    // 发送邮件
    // EmailUtils.testSendEmail(email, "重置密码", "您的密码已重置为" + DEF_PWD + "，请妥善保管");
    // mailUtils.sendEmail(email, "重置密码", "您的密码已重置为" + properties.getDefaultPwd() + "，请妥善保管。为了账户安全，请在登录后修改密码。");
    return Result.fail();
  }

  @ResponseBody
  @GetMapping("/sendEmail")
  public Object resetPwd(@RequestParam Map<String, Object> requestBody) throws Exception {
    String email = "522563907@qq.com";
    mailUtils.sendEmail(email, "重置密码",
        "您的密码已重置为" + properties.getDefaultPwd() + "，请妥善保管。为了账户安全，请在登录后修改密码。");
    return Result.ok(null);
  }
  
  
  final String str = "abcdefghijklmnopqrstuvwxyz0123456789";
  private String getRandomString(int length) {
    Random random = new Random();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < length; i++) {
      int number = random.nextInt(36);
      sb.append(str.charAt(number));
    }
    return sb.toString();
  }
  

}
