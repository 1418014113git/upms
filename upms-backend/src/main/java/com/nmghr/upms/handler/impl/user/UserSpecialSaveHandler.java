package com.nmghr.upms.handler.impl.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.nmghr.util.Sms4Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.util.Md5Utils;
import com.nmghr.util.SaltUtils;

@Service("userspecialSaveHandler")
public class UserSpecialSaveHandler extends AbstractSaveHandler {
	public UserSpecialSaveHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	private final static int USER_CREATE_UPMS = 1;// 是upms创建用

	@Transactional
	@Override
	public Object save(Map<String, Object> requestBody) throws Exception {

		requestBody.put("appCode", "ksjd");
		requestBody.put("deleteable", "0");
		requestBody.put("enabled", "0");
		requestBody.put("userType", "0");
		requestBody.put("isUpms", "1");
		requestBody.put("auditStatus", "0");
		requestBody.put("userIdNumber", String.valueOf(requestBody.get("cardNumber")));
		requestBody.put("realName", String.valueOf(requestBody.get("responsibleName")));
		requestBody.put("remark", "批量报名人员");
		requestBody.put("userSex", 2);
		requestBody.put("userEmail", requestBody.get("email"));
		requestBody.put("userName", String.valueOf(requestBody.get("responsiblePhone")));
		requestBody.put("departId", String.valueOf(requestBody.get("deptCode")));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERNAME");
		Map<String, Object> userMap = (Map<String, Object>) baseService.get(requestBody);
		if (userMap != null) {
			return Result.fail(99999, "负责人手机号已存在");
		}

		validation(requestBody);// 验证参数
		String userKey = getUserKey();
		String salt = SaltUtils.getSalt();
		String userName = String.valueOf(requestBody.get("userName"));
		String userWord = this.getStringRandom(8);
		String pwd = Sms4Util.Encryption(userWord, salt);
		requestBody.put("passWord", pwd);
		requestBody.put("salt", salt);
		requestBody.put("userKey", userKey);
		String userSecret = UUID.randomUUID().toString();
		requestBody.put("userSecret", userSecret);
		Object appCode = requestBody.get("appCode");
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USER");// 添加用户
		String id = baseService.save(requestBody).toString();
		requestBody.put("userId", id);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPCODE");// 通过appCode获取appId
		Map<String, Object> appMap = (Map<String, Object>) baseService.get(appCode.toString());
		requestBody.put("appId", appMap.get("id"));
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERAPPREL");// 用户绑定APP
		baseService.save(requestBody);
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERDEPARTREL");// 用户绑定部门
		baseService.save(requestBody);
		requestBody.put("menuCode", "1001034001");
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ROLEMENURELPATH");
		Map<String, Object> map = (Map<String, Object>) baseService.get(requestBody);
		if (map != null && map.size() > 0) {
			requestBody.put("roleId", map.get("id"));
		} else {
			Map<String, Object> roleMap = new HashMap<String, Object>();
			roleMap.put("roleName", "批量报名角色");
			roleMap.put("roleType", "1");
			roleMap.put("appId", "1002");
			roleMap.put("parentRoleId", "-1");
			roleMap.put("enabled", "1");
			roleMap.put("deleteable", "0");
			roleMap.put("roleDesc", "batchsignuprole");
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ROLE");
			Object roleId = baseService.save(roleMap);
			requestBody.put("roleId", roleId);
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "MENUBYCODE");
			List<Map<String, Object>> menuList = (List<Map<String, Object>>) baseService.list(requestBody);
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "ROLEMENUREL");
			if (menuList != null && menuList.size() > 0) {
				for (Map<String, Object> menu : menuList) {
					menu.put("roleId", roleId);
					menu.put("appId", 1002);
					baseService.save(menu);
				}
			}
		}
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERROLEREL");// 用户绑定角色
		return baseService.save(requestBody);

	}

	/**
	 * 获取用户Key.
	 * 
	 * @return 获取用户Key
	 * @throws Exception
	 */
	public String getUserKey() throws Exception {
		String userKey = UUID.randomUUID().toString();
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERS");// 根据用户key获取用户信息
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("userKey", userKey);
		List<Map<String, Object>> userList = (List<Map<String, Object>>) baseService.list(requestBody);
		if (userList.size() > 0) {
			return getUserKey();
		}
		return userKey;
	}

	/**
	 * 验证参数.
	 * 
	 * @param requestBody
	 *            请求体
	 */
	public static void validation(Map<String, Object> requestBody) {
		Object userName = requestBody.get("userName");
		ValidationUtils.notNull(userName, "用户名不能为空");
		Object realName = requestBody.get("responsibleName");
		ValidationUtils.notNull(realName, "真实姓名不能为空");
		Object deleteable = requestBody.get("deleteable");
		ValidationUtils.notNull(deleteable, "可否删除不能为空");
		Object enabled = requestBody.get("enabled");
		ValidationUtils.notNull(enabled, "是否启用不能为空");
		Object userType = requestBody.get("userType");
		ValidationUtils.notNull(userType, "用户类型不能为空");
		Object appCode = requestBody.get("appCode");
		ValidationUtils.notNull(appCode, "应用编码不能为空");
	}

	// 生成随机数字和字母,
	public String getStringRandom(int length) {
		String val = "";
		Random random = new Random();
		// 参数length，表示生成几位随机数
		for (int i = 0; i < length; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			// 输出字母还是数字
			if ("char".equalsIgnoreCase(charOrNum)) {
				// 输出是大写字母还是小写字母
				int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
				val += (char) (random.nextInt(26) + temp);
			} else if ("num".equalsIgnoreCase(charOrNum)) {
				val += String.valueOf(random.nextInt(10));
			}
		}
		return val;
	}

}
