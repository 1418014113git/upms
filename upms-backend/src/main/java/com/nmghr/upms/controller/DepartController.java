package com.nmghr.upms.controller;

import com.nmghr.basic.core.service.handler.ISaveHandler;
import com.nmghr.basic.core.service.handler.IUpdateHandler;
import com.nmghr.basic.core.util.SpringUtils;
import com.nmghr.upms.handler.impl.depart.DepartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 部门相关业务
 */
@RestController
@RequestMapping("/dept")
public class DepartController {

  @Autowired
  private DepartService departService;

  /**
   * 获取直属下级
   *
   * @return
   */
  @GetMapping(value = "/subordinate")
  public Object subordinate(String areaCode, int curType) throws Exception {
    return departService.subordinate(areaCode, curType);
  }


}
