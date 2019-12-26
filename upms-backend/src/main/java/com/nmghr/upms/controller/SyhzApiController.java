package com.nmghr.upms.controller;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.util.TreeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SyhzApiController {

  @Autowired
  @Qualifier("baseService")
  private IBaseService baseService;

  /**
   * 获取所有列表
   * @param request
   * @param response
   * @throws Exception
   */
  @GetMapping("/api/department")
  @ResponseBody
  public Object deptlist(HttpServletRequest request, HttpServletResponse response) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYHDEPTLIST");
    return baseService.list(new HashMap<String, Object>());
  }
  /**
   * 获取所有列表
   * @param request
   * @param response
   * @throws Exception
   */
  @PostMapping("/api/userbydepcode")
  @ResponseBody
  public Object userByDepCode(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPCODEUSER");
    return baseService.list(params);
  }

  @PostMapping("/api/deptsuser")
  @ResponseBody
  public Object getDeptsUser(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPTSUSER");
    return baseService.list(params);
  }


  /**
   * 获取城市列表
   * @param request
   * @param response
   * @throws Exception
   */
  @GetMapping("/api/cityTree")
  @ResponseBody
  public Object cityTree(
      @RequestParam Map<String, Object> params,
      HttpServletRequest request, HttpServletResponse response) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYHCITYLIST");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(params);
    return TreeUtil.getTree(list, "cityCode", "parent", "children");
  }

}
