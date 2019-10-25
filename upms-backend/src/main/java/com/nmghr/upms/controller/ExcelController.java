package com.nmghr.upms.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.sargeraswang.util.ExcelUtil.ExcelUtil;

@RestController
@RequestMapping("/excel")
public class ExcelController {
	private static final Logger log = LoggerFactory.getLogger(ExcelController.class);
	@Autowired
	@Qualifier("baseService")
	private IBaseService baseService;

	@GetMapping(value = "/exporFile/{alias}")
	@ResponseBody
	public void importFile(@PathVariable String alias, @RequestParam Map<String, Object> requestParam,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Map<String, String> headersMap = new LinkedHashMap<String, String>();
		Integer type = Integer.getInteger(String.valueOf(requestParam.get("type")));

		type = Integer.parseInt(request.getParameter("type"));

		Map<String, String[]> hm = request.getParameterMap();
		Iterator it = hm.keySet().iterator();

		Map<String, Object> param = new HashMap<String, Object>();
		while (it.hasNext()) {
			String key = it.next().toString();
			String[] values = (String[]) hm.get(key);
			if (values != null && values.length == 1) {
				param.put(key, values[0]);
			}
		}
		String fileName = "下载数据";
		if (type == 1) {
			fileName = "机构信息";
			headersMap.put("sequence", "序号");
			headersMap.put("departName", "机构全称");
			headersMap.put("departLevel", "机构级别");
			headersMap.put("realityNum", "实有人数");
			headersMap.put("areaName", "所属行政区划");
			headersMap.put("mainLeader", "主要负责人");
			headersMap.put("subofficeLeader", "分管局领导");
		}
		if (type == 2) {
			fileName = "人员信息";
			headersMap.put("sequence", "序号");
			headersMap.put("realName", "姓名");
			headersMap.put("userName", "警号");
			headersMap.put("userIdNumber", "身份证号");
			headersMap.put("userSex", "性别");
			headersMap.put("workerGrade", "现任职级");
			headersMap.put("workerDuty", "现任职务");
			headersMap.put("workerPhone", "办公电话");
			headersMap.put("phone", "手机号码");
			headersMap.put("userSort", "人员类别");
			headersMap.put("userState", "状态");
		}
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, alias.toUpperCase());
		alias = Constant.getHandlerBeanName(alias, Constant.OPERATOR_QUERY);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = (List<Map<String, Object>>) baseService.list(requestParam);
		if (type == 1) {
			list = tolist(list);
		}
		if (type == 2) {
			list = userlist(list);
		}
		Integer[] lockedArray = new Integer[] {15};// 锁定列
		ArrayList<Integer> lockedList = new ArrayList<Integer>(Arrays.asList(lockedArray));
		ExcelUtil.exportExcel(headersMap, list, os, lockedList);
		// 配置浏览器下载
		byte[] content = os.toByteArray();
		InputStream is = new ByteArrayInputStream(content);
		response.reset();
		response.setContentType("application/vnd.ms-excel;charset=utf-8");
		response.setHeader("Content-Disposition",
				"attachment;filename=" + new String((fileName + ".xlsx").getBytes(), "iso-8859-1"));
		ServletOutputStream out = response.getOutputStream();
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
		} catch (final IOException e) {
			throw e;
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}

	}

	private List<Map<String, Object>> tolist(List<Map<String, Object>> list) {
		int i = 1;
		for (Map<String, Object> map : list) {
			map.put("sequence", i + "");
			String departLevel = String.valueOf(map.get("departLevel"));
			if ("1".equals(departLevel)) {
				map.put("departLevel", "正厅级");
			} else if ("2".equals(departLevel)) {
				map.put("departLevel", "副厅级");
			} else if ("3".equals(departLevel)) {
				map.put("departLevel", "正处级");
			} else if ("4".equals(departLevel)) {
				map.put("departLevel", "副处级");
			} else if ("5".equals(departLevel)) {
				map.put("departLevel", "正科级");
			} else if ("6".equals(departLevel)) {
				map.put("departLevel", "副科级");
			} else if ("7".equals(departLevel)) {
				map.put("departLevel", "副科级以下");
			}
			i++;
		}
		return list;

	}
	private List<Map<String, Object>> userlist(List<Map<String, Object>> list) {
		int i = 1;
		for (Map<String, Object> map : list) {
			map.put("sequence", i + "");
			String userIdNumber = String.valueOf(map.get("userIdNumber"));
			String idCard = "";
			if (userIdNumber.length() == 15) {
				idCard = userIdNumber.replaceAll("(?<=\\w{9})\\w(?=\\w{0})", "*");
			} else if (userIdNumber.length() == 18) {
				idCard = userIdNumber.replaceAll("(?<=\\w{12})\\w(?=\\w{0})", "*");
			}

			map.put("userIdNumber", idCard);
			String userSex = String.valueOf(map.get("userSex"));
			if ("0".equals(userSex)) {
				map.put("userSex", "男");
			} else if ("1".equals(userSex)) {
				map.put("userSex", "女");
			} else if ("2".equals(userSex)) {
				map.put("userSex", "未知");
			}

			String workerGrade = String.valueOf(map.get("workerGrade"));
			if ("1".equals(workerGrade)) {
				map.put("workerGrade", "副厅级");
			} else if ("2".equals(workerGrade)) {
				map.put("workerGrade", "一级高级警长");
			} else if ("3".equals(workerGrade)) {
				map.put("workerGrade", "二级高级警长");
			} else if ("4".equals(workerGrade)) {
				map.put("workerGrade", "三级高级警长");
			} else if ("5".equals(workerGrade)) {
				map.put("workerGrade", "四级高级警长");
			} else if ("6".equals(workerGrade)) {
				map.put("workerGrade", "一级警长");
			} else if ("7".equals(workerGrade)) {
				map.put("workerGrade", "二级警长");
			} else if ("8".equals(workerGrade)) {
				map.put("workerGrade", "三级警长");
			} else if ("9".equals(workerGrade)) {
				map.put("workerGrade", "四级警长");
			} else if ("10".equals(workerGrade)) {
				map.put("workerGrade", "一级警员");
			} else if ("11".equals(workerGrade)) {
				map.put("workerGrade", "二级警员");
			} else if ("12".equals(workerGrade)) {
				map.put("workerGrade", "无");
			}

			String workerDuty = String.valueOf(map.get("workerDuty"));
			String departType = String.valueOf(map.get("departType"));
			if ("1".equals(departType)) {
				if ("1".equals(workerDuty)) {
					map.put("workerDuty", "总队长");
				} else if ("2".equals(workerDuty)) {
					map.put("workerDuty", "政委");
				} else if ("3".equals(workerDuty)) {
					map.put("workerDuty", "副总队长");
				} else if ("4".equals(workerDuty)) {
					map.put("workerDuty", "队（科）长");
				} else if ("5".equals(workerDuty)) {
					map.put("workerDuty", "副队（科）长");
				} else if ("6".equals(workerDuty)) {
					map.put("workerDuty", "无");
				}
			} else if ("2".equals(departType)) {
				if ("1".equals(workerDuty)) {
					map.put("workerDuty", "支队长");
				} else if ("2".equals(workerDuty)) {
					map.put("workerDuty", "政委");
				} else if ("3".equals(workerDuty)) {
					map.put("workerDuty", "副支队长");
				} else if ("4".equals(workerDuty)) {
					map.put("workerDuty", "大队（科）长");
				} else if ("5".equals(workerDuty)) {
					map.put("workerDuty", "副大队（科）长");
				} else if ("6".equals(workerDuty)) {
					map.put("workerDuty", "无");
				}
				
			} else if ("3".equals(departType)) {
				if ("1".equals(workerDuty)) {
					map.put("workerDuty", "大队长");
				} else if ("2".equals(workerDuty)) {
					map.put("workerDuty", "教导员");
				} else if ("3".equals(workerDuty)) {
					map.put("workerDuty", "副大队长");
				} else if ("4".equals(workerDuty)) {
					map.put("workerDuty", "无");
				}
				
			} else if ("4".equals(departType)) {
				if ("1".equals(workerDuty)) {
					map.put("workerDuty", "所长");
				} else if ("2".equals(workerDuty)) {
					map.put("workerDuty", "教导员");
				} else if ("3".equals(workerDuty)) {
					map.put("workerDuty", "副所长");
				} else if ("4".equals(workerDuty)) {
					map.put("workerDuty", "无");
				}
				
			}

			String userSort = String.valueOf(map.get("userSort"));
			if ("1".equals(userSort)) {
				map.put("userSort", "民警");
			} else if ("2".equals(userSort)) {
				map.put("userSort", "辅警");
			} else if ("3".equals(userSort)) {
				map.put("userSort", "工勤");
			}

			String userState = String.valueOf(map.get("userState"));
			if ("1".equals(userSort)) {
				if ("1".equals(userState)) {
					map.put("userState", "在职");
				} else if ("2".equals(userState)) {
					map.put("userState", "调离");
				} else if ("3".equals(userState)) {
					map.put("userState", "病休");
				} else if ("4".equals(userState)) {
					map.put("userState", "退休");
				} else if ("5".equals(userState)) {
					map.put("userState", "其他");
				}
				
			} else {
				if ("1".equals(userState)) {
					map.put("userState", "在职");
				} else if ("2".equals(userState)) {
					map.put("userState", "调离");
				} else if ("3".equals(userState)) {
					map.put("userState", "退休");
				} else if ("4".equals(userState)) {
					map.put("userState", "其他");
				}
				
			}
			i++;
		}
		return list;

	}
}
