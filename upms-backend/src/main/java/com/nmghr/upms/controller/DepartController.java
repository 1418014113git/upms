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
  /**
   * 队伍信息统计
   *
   * @return
   */
  @GetMapping(value = "/statistics")
  public Object statistics(String provinceCode, String cityCode, String departCode, String reginCode, String userSort) throws Exception {
    return departService.statisticsList(provinceCode, cityCode, reginCode, departCode, userSort);
  }
  /**
   * 队伍信息统计 图形统计
   *
   * @return
   */
  @GetMapping(value = "/statisticsChart")
  public Object statisticsChart(String provinceCode, String cityCode, String departCode, String reginCode, String userSort) throws Exception {
    return departService.statisticsEchart(provinceCode, cityCode, reginCode, departCode, userSort);
  }

  /**
   * 查询某队伍详细信息
   * @return
   * @throws Exception
   */
  @GetMapping(value = "/statisticsDetail")
  public Object statisticsDetail(String provinceCode, String cityCode, String departCode, String reginCode, String userSort) throws Exception {
    return departService.statisticsDetail(provinceCode, cityCode, reginCode, departCode, userSort);
  }
}
