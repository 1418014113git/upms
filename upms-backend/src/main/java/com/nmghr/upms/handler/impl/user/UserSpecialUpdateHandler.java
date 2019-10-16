package com.nmghr.upms.handler.impl.user;

import java.util.Map;
import java.util.Random;

import com.nmghr.util.Sms4Util;
import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.util.Md5Utils;
import com.nmghr.util.SaltUtils;

@Service("userspecialyUpdateHandler")
public class UserSpecialUpdateHandler extends AbstractUpdateHandler {
	public UserSpecialUpdateHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		if ("null".equals(String.valueOf(requestBody.get("userWord")))) {
			if ("2".equals(String.valueOf(requestBody.get("auditStatus")))) {
				String salt = SaltUtils.getSalt();
				String userName = String.valueOf(requestBody.get("userName"));
				String userWord = this.getStringRandom(8);
				String pwd = Sms4Util.Encryption(userWord, salt);
				requestBody.put("userWord", pwd);
				requestBody.put("salt", salt);
				requestBody.put("seeWord", userWord);
				requestBody.put("status", 1);
			} else {
				requestBody.put("auditStatus", 3);
			}

		} else {
			String salt = SaltUtils.getSalt();
			String userName = String.valueOf(requestBody.get("userName"));
			String userWord = String.valueOf(requestBody.get("userWord"));
			String pwd = Sms4Util.Encryption(userWord, salt);
			requestBody.put("userWord", pwd);
			requestBody.put("salt", salt);
			requestBody.put("seeWord", userWord);
		}
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "BATCHSIGNUPACCOUNT");
		return baseService.update(id, requestBody);
	}

	// 生成随机数字和字母,
	public String getStringRandom(int length) {
		final String str = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(36);
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}
}
