package com.nmghr.upms.handler.impl.depart;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;
import com.nmghr.util.TreeUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("departtreeQueryHandler")
public class DepartQueryHandler extends AbstractQueryHandler {
  public DepartQueryHandler(IBaseService baseService) {
    super(baseService);
  }

  @Override
  @SuppressWarnings("all")
  public Object list(Map<String, Object> requestMap) throws Exception {
    LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "SYHDEPTLIST");
    List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.list(requestMap);
    return TreeUtil.getTree(list, "dep_code", "super_dep_code", "children");
  }
}
