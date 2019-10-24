package com.nmghr.upms.handler.impl.depart;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractUpdateHandler;
import com.nmghr.basic.core.util.ValidationUtils;

/**
 * 部门修改
 * 
 * @author heijiantao
 * @date 2019年10月22日
 * @version 1.0
 */
@Service("hsyzdepartUpdateHandler")
public class HsyzDepartUpdateHandler extends AbstractUpdateHandler {

	public HsyzDepartUpdateHandler(IBaseService baseService) {
		super(baseService);
		// TODO Auto-generated constructor stub
	}

	@Override
	@Transactional
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		validation(requestBody);// 验证参数
		requestBody.put("complete", 0);// 信息完善
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "hsyzdepart");// 修改部门信息
		return baseService.update(id, requestBody);
	}

	/**
	 * 验证参数.
	 * 
	 * @param requestBody
	 *            请求体
	 */
	private static void validation(Map<String, Object> requestBody) {
		Object departName = requestBody.get("departName");
		ValidationUtils.notNull(departName, "部门名称不能为空");
		ValidationUtils.length(departName, 0, 50, "机构名称最多可输入50个字");
		Object departLevel = requestBody.get("departLevel");
		ValidationUtils.notNull(departLevel, "机构级别不能为空");
		Object mainAssignment = requestBody.get("mainAssignment");
		ValidationUtils.notNull(mainAssignment, "主要职责任务不能为空");
		ValidationUtils.length(departName, 0, 100, "主要职责任务最多可输入100个字");
		Object provinceCode = requestBody.get("provinceCode");
		ValidationUtils.notNull(provinceCode, "所属行政区划不能为空");
		Object address = requestBody.get("address");
		ValidationUtils.notNull(address, "机构详细地址不能为空");
		ValidationUtils.length(departName, 0, 100, "详细地址最多可输入100个字");
		Object foundingTime = requestBody.get("foundingTime");
		ValidationUtils.notNull(foundingTime, "成立时间不能为空");
		Object postcode = requestBody.get("postcode");
		ValidationUtils.notNull(postcode, "邮编不能为空");
		Object linkmanPhone = requestBody.get("linkmanPhone");
		ValidationUtils.notNull(linkmanPhone, "联系电话不能为空");
		ValidationUtils.length(linkmanPhone, 0, 11, "请输入正确的电话");
		Object mainLeader = requestBody.get("mainLeader");
		ValidationUtils.notNull(mainLeader, "主要负责人不能为空");
		Object subofficeLeader = requestBody.get("subofficeLeader");
		ValidationUtils.notNull(subofficeLeader, "分管局领导不能为空");
		Object dailyLeader = requestBody.get("dailyLeader");
		ValidationUtils.notNull(dailyLeader, "日常联系人不能为空");
		Object designMode = requestBody.get("designMode");
		ValidationUtils.notNull(designMode, "机构设计模式不能为空");
	}

}
