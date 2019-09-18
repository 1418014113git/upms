package com.nmghr.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeUtil {
//  public static void main(String[] args) {
//    List<Map<String, Object>> childs = new ArrayList<>();
//    Map<String, Object> tmpMap = new HashMap<>();
//    tmpMap.put("code","A");
//    tmpMap.put("parentCode","0");
//    tmpMap.put("name","A");
//    childs.add(tmpMap);
//    tmpMap = new HashMap<>();
//    tmpMap.put("code","B");
//    tmpMap.put("parentCode","0");
//    tmpMap.put("name","B");
//    childs.add(tmpMap);
//    tmpMap = new HashMap<>();
//    tmpMap.put("code","B1");
//    tmpMap.put("parentCode","B");
//    tmpMap.put("name","B1");
//    childs.add(tmpMap);
//    tmpMap = new HashMap<>();
//    tmpMap.put("code","A1");
//    tmpMap.put("parentCode","A");
//    tmpMap.put("name","A1");
//    childs.add(tmpMap);
//    tmpMap = new HashMap<>();
//    tmpMap.put("code","B11");
//    tmpMap.put("parentCode","B1");
//    tmpMap.put("name","B11");
//    childs.add(tmpMap);
//    tmpMap = new HashMap<>();
//    tmpMap.put("code","A11");
//    tmpMap.put("parentCode","A1");
//    tmpMap.put("name","A11");
//    childs.add(tmpMap);
//    tmpMap = new HashMap<>();
//    tmpMap.put("code","A12");
//    tmpMap.put("parentCode","A1");
//    tmpMap.put("name","A12");
//    childs.add(tmpMap);
//    System.out.println(JSONObject.toJSONString(getTree(childs, "code", "parentCode", "children")));
//  }

  /**
   * 获取树结构
   *
   * @param array
   * @param code
   * @param parentCode
   * @param children
   * @return
   */
  public static List<Map<String, Object>> getTree(List<Map<String, Object>> array, String code, String parentCode, String children) {
    List<Map<String, Object>> list = new ArrayList<>();
    int size = (int) Math.ceil(array.size() / 0.75)  + 1;
    Map<String, Object> tmpMap = new HashMap<>(size);
    for (Map<String, Object> tmp : array) {
      tmpMap.put(String.valueOf(tmp.get(code)), tmp);
    }

    for (Map<String, Object> tmp : array) {
      Map<String, Object> bean = (Map<String, Object>) tmpMap.get(tmp.get(parentCode));
      if (bean != null) {
        if (bean.get(children) == null) {
          List<Map<String, Object>> childs = new ArrayList<>();
          childs.add(tmp);
          bean.put(children, childs);
        } else {
          List<Map<String, Object>> childs = (List<Map<String, Object>>) bean.get(children);
          childs.add(tmp);
        }
      } else {
        list.add(tmp);
      }
    }
    return list;
  }

}
