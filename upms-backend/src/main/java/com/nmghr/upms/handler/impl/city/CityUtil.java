/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.city;

import java.util.ArrayList;
import java.util.List;

/**
 * <功能描述/>
 *
 * @author brook
 * @date 2018年6月26日 下午1:48:03
 * @version 1.0
 */
public class CityUtil {

  /**
   * 解析树形数据
   * 
   * @param topId
   * @param entityList
   * @return
   * @author brook
   * @date 2018-6-26
   */
  public static <E extends City<E>> List<E> getTreeList(String topId, List<E> entityList) {
    List<E> resultList = new ArrayList<>();
    if(entityList==null||entityList.size()<1) {
      return resultList;
    }
    // 获取顶层元素集合
    String parentId;
    for (E entity : entityList) {
      parentId = entity.getParentId();
      if (parentId == null || topId.equals(parentId)) {
        resultList.add(entity);
      }
    }

    // 获取每个顶层元素的子数据集合
    for (E entity : resultList) {
      entity.setChildren(getSubList(entity.getCityCode(), entityList, entity.getCityCode()));
    }

    return resultList;
  }

  /**
   * 获取子数据集合
   * 
   * @param id
   * @param entityList
   * @return
   * @author brook
   * @date 2018-6-26
   */
  private static <E extends City<E>> List<E> getSubList(String id, List<E> entityList,
      String menuIds) {
    List<E> childList = new ArrayList<>();
    String parentId;

    // 子集的直接子对象
    int number = 0;
    for (E entity : entityList) {
      parentId = entity.getParentId();
      if (id.equals(parentId)) {
        childList.add(entity);
      }
    }


    // 子集的间接子对象
    for (E entity : childList) {
      entity.setMenuIds(menuIds);
      entity.setChildren((getSubList(entity.getCityCode(), entityList, menuIds + "," + entity.getCityCode())));
    }

    // 递归退出条件
    if (childList.size() == 0) {
      return null;
    }

    return childList;
  }
}
