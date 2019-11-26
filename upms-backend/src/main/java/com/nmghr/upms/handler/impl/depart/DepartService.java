package com.nmghr.upms.handler.impl.depart;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("departService")
public class DepartService {

  @Autowired
  private IBaseService baseService;

  /**
   * 查询下级直属单位
   * @param curType
   * @return
   * @throws Exception
   */
  public Object subordinate(String areaCode, int curType) throws Exception {
    curType++;
    if(curType>4){return null;}
    Map<String, Object> param = new HashMap<>();
    param.put("cityCode", areaCode);
    param.put("departType", curType);
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "DEPTBYCODETYPE");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(param);
    if (list == null || list.size()==0) {
      return subordinate(areaCode, curType);
    }
    return list;
  }

}
