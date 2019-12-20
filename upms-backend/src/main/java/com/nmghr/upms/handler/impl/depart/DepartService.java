package com.nmghr.upms.handler.impl.depart;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.common.exception.GlobalErrorException;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@SuppressWarnings("unchecked")
@Service("departService")
public class DepartService {

  @Autowired
  private IBaseService baseService;

  /**
   * 查询下级直属单位
   *
   * @param curType
   * @return
   * @throws Exception
   */
  public Object subordinate(String areaCode, int curType) throws Exception {
    curType++;
    if (curType > 4) {
      return null;
    }
    Map<String, Object> param = new HashMap<>();
    param.put("cityCode", areaCode);
    param.put("departType", curType);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPTBYCODETYPE");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(param);
    if (list == null || list.size() == 0) {
      return subordinate(areaCode, curType);
    }
    return list;
  }

  public Object statisticsList(String provinceCode, String cityCode, String reginCode, String departCode, String userSort) throws Exception {
    Map<String, Object> param = new HashMap<>();
    boolean flag = true;
    if (!StringUtils.isEmpty(reginCode)) {
      param.put("reginCode", reginCode);
      flag=false;
    } else {
      if (!StringUtils.isEmpty(cityCode)) {
        param.put("cityCode", cityCode);
        flag=false;
      } else {
        if (!StringUtils.isEmpty(provinceCode)) {
          param.put("provinceCode", provinceCode);
        }
      }
    }
    if (!StringUtils.isEmpty(userSort)) {
      param.put("userSort", userSort);
    }
    if(!StringUtils.isEmpty(departCode)){
      param.put("departCode", departCode);
      flag=false;
    }

    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "HSYZUSERTOTAL");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(param);
    if (list == null || list.size() == 0) {
      return new ArrayList<>();
    }
    List<Map<String, Object>> result = list;
    if(flag){
      Map<String, Object> city = new LinkedHashMap<>();
      for (Map m : list) {
        String cityName = String.valueOf(m.get("cityName"));
        if (city.get(cityName) != null) {
          Map<String, Object> c = (Map<String, Object>) city.get(cityName);
          c.put("sumAge", Integer.parseInt(String.valueOf(c.get("sumAge"))) + Integer.parseInt(String.valueOf(m.get("sumAge"))));
          c.put("sumAgeCount", Integer.parseInt(String.valueOf(c.get("sumAgeCount"))) + Integer.parseInt(String.valueOf(m.get("sumAgeCount"))));
          c.put("ryNum", Integer.parseInt(String.valueOf(c.get("ryNum"))) + Integer.parseInt(String.valueOf(m.get("ryNum"))));
          c.put("sex0", Integer.parseInt(String.valueOf(c.get("sex0"))) + Integer.parseInt(String.valueOf(m.get("sex0"))));
          c.put("sex1", Integer.parseInt(String.valueOf(c.get("sex1"))) + Integer.parseInt(String.valueOf(m.get("sex1"))));
          c.put("zzmm1", Integer.parseInt(String.valueOf(c.get("zzmm1"))) + Integer.parseInt(String.valueOf(m.get("zzmm1"))));
          c.put("zzmm2", Integer.parseInt(String.valueOf(c.get("zzmm2"))) + Integer.parseInt(String.valueOf(m.get("zzmm2"))));
          c.put("zzmm3", Integer.parseInt(String.valueOf(c.get("zzmm3"))) + Integer.parseInt(String.valueOf(m.get("zzmm3"))));
          c.put("zzmm4", Integer.parseInt(String.valueOf(c.get("zzmm4"))) + Integer.parseInt(String.valueOf(m.get("zzmm4"))));
          c.put("xw1", Integer.parseInt(String.valueOf(c.get("xw1"))) + Integer.parseInt(String.valueOf(m.get("xw1"))));
          c.put("xw2", Integer.parseInt(String.valueOf(c.get("xw2"))) + Integer.parseInt(String.valueOf(m.get("xw2"))));
          c.put("xw3", Integer.parseInt(String.valueOf(c.get("xw3"))) + Integer.parseInt(String.valueOf(m.get("xw3"))));
          c.put("xw4", Integer.parseInt(String.valueOf(c.get("xw4"))) + Integer.parseInt(String.valueOf(m.get("xw4"))));
          c.put("whcd1", Integer.parseInt(String.valueOf(c.get("whcd1"))) + Integer.parseInt(String.valueOf(m.get("whcd1"))));
          c.put("whcd2", Integer.parseInt(String.valueOf(c.get("whcd2"))) + Integer.parseInt(String.valueOf(m.get("whcd2"))));
          c.put("whcd3", Integer.parseInt(String.valueOf(c.get("whcd3"))) + Integer.parseInt(String.valueOf(m.get("whcd3"))));
          c.put("whcd4", Integer.parseInt(String.valueOf(c.get("whcd4"))) + Integer.parseInt(String.valueOf(m.get("whcd4"))));
          c.put("whcd5", Integer.parseInt(String.valueOf(c.get("whcd5"))) + Integer.parseInt(String.valueOf(m.get("whcd5"))));
          c.put("whcd6", Integer.parseInt(String.valueOf(c.get("whcd6"))) + Integer.parseInt(String.valueOf(m.get("whcd6"))));
          c.put("usort1", Integer.parseInt(String.valueOf(c.get("usort1"))) + Integer.parseInt(String.valueOf(m.get("usort1"))));
          c.put("usort2", Integer.parseInt(String.valueOf(c.get("usort2"))) + Integer.parseInt(String.valueOf(m.get("usort2"))));
          c.put("usort3", Integer.parseInt(String.valueOf(c.get("usort3"))) + Integer.parseInt(String.valueOf(m.get("usort3"))));
          List<Map<String, Object>> dArray = (List<Map<String, Object>>) c.get("list");
          Map<String, Object> cn = new HashMap<>();
          BigDecimal ageCount = new BigDecimal(String.valueOf(m.get("sumAgeCount")));
          if(ageCount.compareTo(BigDecimal.ZERO) > 0){
            m.put("avgAge", new BigDecimal(String.valueOf(m.get("sumAge"))).divide(ageCount, 0, RoundingMode.HALF_UP));
          } else {
            m.put("avgAge", "-");
          }
          cn.putAll(m);
          dArray.add(cn);
          c.put("list", dArray);
          city.put(cityName, c);
        } else {
          BigDecimal ageCount = new BigDecimal(String.valueOf(m.get("sumAgeCount")));
          if(ageCount.compareTo(BigDecimal.ZERO) > 0){
            m.put("avgAge", new BigDecimal(String.valueOf(m.get("sumAge"))).divide(ageCount, 0, RoundingMode.HALF_UP));
          } else {
            m.put("avgAge", "-");
          }
          Map<String, Object> c = new HashMap<>();
          c.putAll(m);
          List<Map<String, Object>> dArray = new ArrayList();
          Map<String, Object> cn = new HashMap<>();
          cn.putAll(m);
          dArray.add(cn);
          c.put("list", dArray);
          city.put(cityName, c);
        }
      }
      result = new ArrayList(city.values());
    }


    //计算合计
    int sumAge = 0, sumAgeCount = 0, ryNum = 0, sex0 = 0, sex1 = 0, zzmm1 = 0, zzmm2 = 0, zzmm3 = 0, zzmm4 = 0,
        xw1 = 0, xw2 = 0, xw3 = 0, xw4 = 0, whcd1 = 0, whcd2 = 0, whcd3 = 0, whcd4 = 0, whcd5 = 0, whcd6 = 0;
    for (Map m : result) {
      BigDecimal ageCount = new BigDecimal(String.valueOf(m.get("sumAgeCount")));
      if(ageCount.compareTo(BigDecimal.ZERO) > 0){
        m.put("avgAge", new BigDecimal(String.valueOf(m.get("sumAge"))).divide(ageCount, 0, RoundingMode.HALF_UP));
      } else {
        m.put("avgAge", "-");
      }
      sumAge += Integer.parseInt(String.valueOf(m.get("sumAge")));
      sumAgeCount += Integer.parseInt(String.valueOf(m.get("sumAgeCount")));
      ryNum += Integer.parseInt(String.valueOf(m.get("ryNum")));
      sex0 += Integer.parseInt(String.valueOf(m.get("sex0")));
      sex1 += Integer.parseInt(String.valueOf(m.get("sex1")));
      zzmm1 += Integer.parseInt(String.valueOf(m.get("zzmm1")));
      zzmm2 += Integer.parseInt(String.valueOf(m.get("zzmm2")));
      zzmm3 += Integer.parseInt(String.valueOf(m.get("zzmm3")));
      zzmm4 += Integer.parseInt(String.valueOf(m.get("zzmm4")));
      xw1 += Integer.parseInt(String.valueOf(m.get("xw1")));
      xw2 += Integer.parseInt(String.valueOf(m.get("xw2")));
      xw3 += Integer.parseInt(String.valueOf(m.get("xw3")));
      xw4 += Integer.parseInt(String.valueOf(m.get("xw4")));
      whcd1 += Integer.parseInt(String.valueOf(m.get("whcd1")));
      whcd2 += Integer.parseInt(String.valueOf(m.get("whcd2")));
      whcd3 += Integer.parseInt(String.valueOf(m.get("whcd3")));
      whcd4 += Integer.parseInt(String.valueOf(m.get("whcd4")));
      whcd5 += Integer.parseInt(String.valueOf(m.get("whcd5")));
      whcd6 += Integer.parseInt(String.valueOf(m.get("whcd6")));
    }
    BigDecimal ageCount = new BigDecimal(String.valueOf(sumAgeCount));
    Map<String, Object> c = new HashMap<>();
    if(ageCount.compareTo(BigDecimal.ZERO) > 0){
      c.put("avgAge", new BigDecimal(String.valueOf(sumAge)).divide(ageCount, 0, RoundingMode.HALF_UP));
    } else {
      c.put("avgAge", "-");
    }
    c.put("ryNum", ryNum);
    c.put("sex0", sex0);
    c.put("sex1", sex1);
    c.put("zzmm1", zzmm1);
    c.put("zzmm2", zzmm2);
    c.put("zzmm3", zzmm3);
    c.put("zzmm4", zzmm4);
    c.put("xw1", xw1);
    c.put("xw2", xw2);
    c.put("xw3", xw3);
    c.put("xw4", xw4);
    c.put("whcd1", whcd1);
    c.put("whcd2", whcd2);
    c.put("whcd3", whcd3);
    c.put("whcd4", whcd4);
    c.put("whcd5", whcd5);
    c.put("whcd6", whcd6);
    result.add(c);
    return result;
  }

  public Object statisticsEchart(String provinceCode, String cityCode, String reginCode, String departCode, String userSort) throws Exception {
    Map<String, Object> param = new HashMap<>();
    if (!StringUtils.isEmpty(reginCode)) {
      param.put("reginCode", reginCode);
    } else {
      if (!StringUtils.isEmpty(cityCode)) {
        param.put("cityCode", cityCode);
      } else {
        if (!StringUtils.isEmpty(provinceCode)) {
          param.put("provinceCode", provinceCode);
        }
      }
    }
    if (!StringUtils.isEmpty(userSort)) {
      param.put("userSort", userSort);
    }
    param.put("departCode", departCode);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "HSYZGUSERTOTAL");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(param);
    if (list == null || list.size() == 0) {
      return new ArrayList<>();
    }
    return list;
  }

  public Object statisticsDetail(String provinceCode, String cityCode, String reginCode, String departCode, String userSort) throws Exception {
    Map<String, Object> param = new HashMap<>();
    if (StringUtils.isEmpty(departCode)) {
      throw new GlobalErrorException("999668", "departCode 不能为空");
    }
    param.put("departCode", departCode);
    param.put("detail", "detail");
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "HSYZUSERTOTAL");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(param);
    if (list == null || list.size() == 0) {
      return new ArrayList<>();
    }
    if (list.size() > 2) {
      return list;
    }

    Map<String, Object> resultTemp = new LinkedHashMap<>();
    Map m1 = new HashMap();
    m1.put("usort", 1);
    m1.put("ryNum", 0);
    m1.put("sumAge", 0);
    m1.put("sumAgeCount", 0);
    m1.put("sex0", 0);
    m1.put("sex1", 0);
    m1.put("zzmm1", 0);
    m1.put("zzmm2", 0);
    m1.put("zzmm3", 0);
    m1.put("zzmm4", 0);
    m1.put("xw1", 0);
    m1.put("xw2", 0);
    m1.put("xw3", 0);
    m1.put("xw4", 0);
    m1.put("whcd1", 0);
    m1.put("whcd2", 0);
    m1.put("whcd3", 0);
    m1.put("whcd4", 0);
    m1.put("whcd5", 0);
    m1.put("whcd6", 0);
    resultTemp.put("1", m1);
    Map m2 = new HashMap();
    m2.putAll(m1);
    m2.put("usort", 2);
    Map m3 = new HashMap();
    m3.putAll(m1);
    m3.put("usort", 3);
    resultTemp.put("2", m2);
    resultTemp.put("3", m3);

    for (Map<String, Object> map : list) {
      Map<String, Object> m = (Map<String, Object>) resultTemp.get(String.valueOf(map.get("usort")));
      if (m != null) {
        m.putAll(map);
        resultTemp.put(String.valueOf(map.get("usort")), m);
      }
    }
    return resultTemp.values();
  }


}
