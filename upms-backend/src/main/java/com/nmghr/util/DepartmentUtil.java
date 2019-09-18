/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIDepartmentS OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.util;

import java.util.ArrayList;
import java.util.List;

/**
 * <功能描述/>
 *
 * @author weber  
 * @date 2018年7月4日 下午6:16:12 
 * @version 1.0   
 */
public class DepartmentUtil {
  /**
   * 解析树形数据
   */
  public static List<Department> getTreeList(String topId, List<Department> entityList) {
    List<Department> resultList = new ArrayList<>();
    if(entityList==null||entityList.size()<1) {
      return resultList;
    }
    // 获取顶层元素集合
    String parentId;
    for (Department entity : entityList) {
      parentId = entity.getParentCode();
      if (parentId == null || topId.equals(parentId)) {
        resultList.add(entity);
      }
    }

    // 获取每个顶层元素的子数据集合
    for (Department entity : resultList) {
      entity.setChildren(getSubList(entity.getCode(), entityList, entity.getCode()));
    }

    return resultList;
  }

  /**
   * 获取子数据集合
   */
  private static List<Department> getSubList(String id, List<Department> entityList,
      String menuIds) {
    List<Department> childList = new ArrayList<>();
    String parentId;

    // 子集的直接子对象
    for (Department entity : entityList) {
      parentId = entity.getParentCode();
      if (id.equals(parentId)) {
        childList.add(entity);
      }
    }

    // 子集的间接子对象
    for (Department entity : childList) {
      entity.setMenuIds(menuIds);
      entity.setChildren((getSubList(entity.getCode(), entityList, menuIds + "," + entity.getCode())));
    }

    // 递归退出条件
    if (childList.size() == 0) {
      return null;
    }

    return childList;
  }
}
