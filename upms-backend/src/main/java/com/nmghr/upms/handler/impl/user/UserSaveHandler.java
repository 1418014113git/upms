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
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractSaveHandler;
import com.nmghr.basic.core.util.ValidationUtils;
import com.nmghr.util.Md5Utils;
import com.nmghr.util.SaltUtils;

/**
 * 添加用户接口.
 *
 * @author wujin
 * @date 2018年11月9日 上午10:31:25
 * @version 1.0
 */
@Service("userSaveHandler")
@SuppressWarnings("all")
public class UserSaveHandler extends AbstractSaveHandler {
	private final static int USER_CREATE_UPMS = 1;// 是upms创建用户

	public UserSaveHandler(IBaseService baseService) {
		super(baseService);
	}

	@Override
	public Object save(Map<String, Object> requestBody) throws Exception {
		validation(requestBody);// 验证参数
		String salt = SaltUtils.getSalt();
		requestBody.put("salt", salt);
		String userKey = getUserKey();
		requestBody.put("userKey", userKey);
		String userSecret = UUID.randomUUID().toString();
		requestBody.put("userSecret", userSecret);
		String passWord = requestBody.get("passWord").toString();
		String userName = requestBody.get("userName").toString();
		String pwd = Md5Utils.encryptMd5Password(userName, passWord, salt);
		requestBody.put("passWord", pwd);
		Object appCode = requestBody.get("appCode");
		int isUpms = Integer.parseInt(requestBody.get("isUpms").toString());
		// 生成用户刷新 salt
		String userRefresh = getRandomString(6);
		requestBody.put("userRefresh", userRefresh);
		if (USER_CREATE_UPMS == isUpms) {
			requestBody.put("appCode", "");
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USER");// 添加用户
			return baseService.save(requestBody);
		} else {
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USER");// 添加用户
			Object userId = baseService.save(requestBody);
			requestBody.put("userId", userId);
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "APPCODE");// 通过appCode获取appId
			Map<String, Object> appMap = (Map<String, Object>) baseService.get(appCode.toString());
			requestBody.put("appId", appMap.get("id"));
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USERAPPREL");// 用户绑定APP
			baseService.save(requestBody);
			return userId;
		}
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

	public static String getRandomString(int length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(62);
			sb.append(str.charAt(number));
		}
		return sb.toString();
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
		Object passWord = requestBody.get("passWord");
		ValidationUtils.notNull(passWord, "密码不能为空");
		Object realName = requestBody.get("realName");
		ValidationUtils.notNull(realName, "真实姓名不能为空");
		Object deleteable = requestBody.get("deleteable");
		ValidationUtils.notNull(deleteable, "可否删除不能为空");
		Object enabled = requestBody.get("enabled");
		ValidationUtils.notNull(enabled, "是否启用不能为空");
		Object userType = requestBody.get("userType");
		ValidationUtils.notNull(userType, "用户类型不能为空");
		Object isUpms = requestBody.get("isUpms");
		ValidationUtils.notNull(isUpms, "是否为upms不能为空");
		Object appCode = requestBody.get("appCode");
		ValidationUtils.notNull(appCode, "应用编码不能为空");
	}

}
