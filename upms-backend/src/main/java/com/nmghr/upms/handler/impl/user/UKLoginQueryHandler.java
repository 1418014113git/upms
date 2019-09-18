/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.nmghr.upms.handler.impl.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

/**
 * 用户登陆相关业务操作
 *
 * @author kaven
 * @date 2018年6月27日 下午2:46:09
 * @version 1.0
 */
@Service("ukloginQueryHandler")
public class UKLoginQueryHandler extends AbstractQueryHandler {

	public UKLoginQueryHandler(IBaseService baseService) {
		super(baseService);
	}

	/**
	 * 用户登录
	 * 
	 * @param requestMap
	 * @return
	 * @throws Exception
	 */
	@Override
	@SuppressWarnings("all")
	public Object list(Map<String, Object> requestMap) throws Exception {
		Map<String, Object> retMap = new HashMap<String, Object>();
		String resMessage = "";
		Object userObj = baseService.get(requestMap);
		if (!StringUtils.isEmpty(userObj)) {
			Map<String, Object> loginUser = (Map<String, Object>) userObj;
			String userSalt = (String) loginUser.get("salt");
			Integer userId = (Integer) loginUser.get("id");
			Map<String, Object> userMap = new HashMap<String, Object>();
			String userName = (String) loginUser.get("user_name");
			String userRefresh = (String) loginUser.get("user_refresh");
			String userToken = (String) loginUser.get("user_token");
			userMap.put("id", userId);
			userMap.put("userName", userName);
			userMap.put("userSalt", userSalt);
			userMap.put("userRefresh", userRefresh);
			userMap.put("userToken", userToken);
			userMap.put("accessToken", userToken);
			userMap.put("refreshToken", userToken);
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USER");
			Map<String, Object> map = (Map<String, Object>) baseService.get(String.valueOf(loginUser.get("id")));
			if (map != null) {
				userMap.put("realName", map.get("realName"));
			}
			retMap = userMap;
		} else {
			resMessage = "用户信息不存在";
			throw new GlobalErrorException(GlobalErrorEnum.UNLOGIN.getCode(), resMessage);
		}
		return retMap;
	}
}
