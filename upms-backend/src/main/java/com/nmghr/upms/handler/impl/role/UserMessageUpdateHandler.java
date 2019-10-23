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

@Service("userMessageUpdateHandler")
public class UserMessageUpdateHandler extends AbstractUpdateHandler{

	private static String ALIAS_USERMESSAGE = "USERMESSAGE";// 修改人员信息
	public UserMessageUpdateHandler(IBaseService baseService) {
		super(baseService);
	}
	
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		validation(requestBody);

		try {
			LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_USERMESSAGE);
			baseService.update(id, requestBody);
			return ("人员信息保存成功！");
		} catch (Exception e) {
			throw new GlobalErrorException(GlobalErrorEnum.PARAM_NOT_VALID.getCode(), "人员信息保存失败，请联系管理员！");
		}
		
	}

	private void validation(Map<String, Object> requestBody) {
		Object realName = requestBody.get("realName");
		ValidationUtils.notNull(realName, "姓名不能为空");
		Object userIdNumber = requestBody.get("userIdNumber");
		ValidationUtils.notNull(userIdNumber, "身份证号不能为空");
		Object userSex = requestBody.get("userSex");
		ValidationUtils.notNull(userSex, "性别不能为空");
		Object birthTime = requestBody.get("birthTime");
		ValidationUtils.notNull(birthTime, "出生日期不能为空");
		Object age = requestBody.get("age");
		ValidationUtils.notNull(age, "年龄不能为空");
		Object nation = requestBody.get("nation");
		ValidationUtils.notNull(nation, "民族不能为空");
		Object politicsStatus = requestBody.get("politicsStatus");
		ValidationUtils.notNull(politicsStatus, "政治面貌不能为空");
		Object cultureDegree = requestBody.get("cultureDegree");
		ValidationUtils.notNull(cultureDegree, "文化程度不能为空");
		Object degree = requestBody.get("degree");
		ValidationUtils.notNull(degree, "获得学位不能为空");
		Object workerGrade = requestBody.get("workerGrade");
		ValidationUtils.notNull(workerGrade, "现任职级不能为空");
		Object workerDuty = requestBody.get("workerDuty");
		ValidationUtils.notNull(workerDuty, "现任职务不能为空");
		Object workerPost = requestBody.get("workerPost");
		ValidationUtils.notNull(workerPost, "工作岗位不能为空");
		Object joinPoliceTime = requestBody.get("joinPoliceTime");
		ValidationUtils.notNull(joinPoliceTime, "参加公安工作时间不能为空");
		Object joinHsyTime = requestBody.get("joinHsyTime");
		ValidationUtils.notNull(joinHsyTime, "参加环食药工作时间不能为空");
		Object workerPhone = requestBody.get("workerPhone");
		ValidationUtils.notNull(workerPhone, "办公电话不能为空");
		Object phone = requestBody.get("phone");
		ValidationUtils.notNull(phone, "手机电话不能为空");
		Object ip = requestBody.get("ip");
		ValidationUtils.notNull(ip, "电脑IP地址不能为空");
		Object lastId = requestBody.get("lastId");
		ValidationUtils.notNull(lastId, "最后修改人id不能为空");
		Object lastName = requestBody.get("lastName");
		ValidationUtils.notNull(lastName, "最后修改人姓名不能为空");
		
	}

}
