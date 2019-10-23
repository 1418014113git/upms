package com.nmghr.upms.handler.impl.role;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.Result;
import com.nmghr.basic.common.exception.GlobalErrorEnum;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.basic.core.util.ValidationUtils;

@Service("userStateUpdateHandler")
public class UserStateUpdateHandler extends AbstractUpdateHandler {

	private static String ALIAS_USERSTATE = "USERSTATE";// 修改人员状态
	public UserStateUpdateHandler(IBaseService baseService) {
		super(baseService);
	}
	
	public Object update(String id, Map<String, Object> requestBody) throws Exception{
		validation(requestBody);
		try{
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_USERSTATE);
			baseService.update(id, requestBody);
			return ("人员状态设置成功！");

			}catch(Exception e1){
			throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "人员信息保存失败，请联系管理员！");

			}
		
	}

	private void validation(Map<String, Object> requestBody) {
		Object order = requestBody.get("order");
		ValidationUtils.notNull(order, "排列次序不能为空");
		Object userState = requestBody.get("userState");
		ValidationUtils.notNull(userState, "人员状态不能为空");
		Object lastId = requestBody.get("lastId");
		ValidationUtils.notNull(lastId, "最后修改人id不能为空");
		Object lastName = requestBody.get("lastName");
		ValidationUtils.notNull(lastName, "最后修改人姓名不能为空");
	}

}
