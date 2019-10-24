/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.city;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

/**
 * <功能描述/>
 *
 * @author weber
 * @date 2018年7月3日 下午4:46:21
 * @version 1.0
 */
@Service("citytreeQueryHandler")
public class CityTreeQueryHandler extends AbstractQueryHandler {
  
  private final String ALIAS_CITY_TREE = "citytree".toUpperCase();// 获取城市tree
  
  @Autowired
  public CityTreeQueryHandler(IBaseService baseService) {
    super(baseService);
  }


  /**
   * 查询所有城市
   * 
   * @param requestMap
   * @return
   * @throws Exception
   */
  @Override
  public Object list(Map<String, Object> requestMap) throws Exception {
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, ALIAS_CITY_TREE);
		LocalThreadStorage.put(Constant.CONTROLLER_PAGE, false);
		Object cityObj = baseService.list(requestMap);
		ArrayList<Object> objectList = (ArrayList<Object>) cityObj;
		List<City> list = null;
		if (!CollectionUtils.isEmpty(objectList)) {
			// 将数据整理成工具类需要的类型
			list = new ArrayList<>();
			for (Object object : objectList) {
				Map<?, ?> map = (HashMap<?, ?>) object;
				City<?> city = new City();
				city.setCityCode(String.valueOf(map.get("org_code")));
				city.setCityName(String.valueOf(map.get("org_name")));
				city.setCityNick("");
				city.setParentId(String.valueOf(map.get("org_p_code")));
				city.setProviceId("610000");
				list.add(city);
			}
		}
		String cityCode = (String) requestMap.get("cityCode");
		if ("null".equals(cityCode)) {
			cityCode = "000000";
		}
		List<City> menus = CityUtil.getTreeList("000000", list);
		// if (menus.size() >= 0 && menus.get(0).getChildren() != null) {
		// return menus.get(0);
		// }
		return menus;
  }

}
