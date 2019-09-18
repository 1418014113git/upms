package com.nmghr.upms.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.service.handler.IUpdateHandler;
import com.nmghr.basic.core.util.SpringUtils;

/**
 * ----批量报名账号表 ----作者： hjt ----时间： 2019-04-11 17:26:05 ----
 */
@RestController
public class UserSpecialyController {

	/**
	 * 保存批量报名账号表
	 * 
	 * @param record
	 * @return
	 */
	@PostMapping(value = "/batchsignupaccountsave")

	public Object save(@RequestBody Map<String, Object> requestBody) throws Exception {
		ISaveHandler saveHandler = SpringUtils.getBean("userspecialSaveHandler", ISaveHandler.class);
		return saveHandler.save(requestBody);

	}



	/**
	 * 审核
	 * 
	 * @param record
	 * @return
	 */
	@PostMapping(value = "/accountAudit")

	public Object update(@RequestBody Map<String, Object> requestBody) throws Exception {
		String id = String.valueOf(requestBody.get("id"));
		IUpdateHandler updateHandler = SpringUtils.getBean("userspecialyUpdateHandler", IUpdateHandler.class);
		return updateHandler.update(id, requestBody);

	}



}
