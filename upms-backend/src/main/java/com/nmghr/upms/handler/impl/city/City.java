/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.handler.impl.city;

import java.io.Serializable;
import java.util.List;

/**
 * <功能描述/>
 *
 * @author weber
 * @date 2018年7月3日 下午4:51:18
 * @version 1.0
 */
@SuppressWarnings("serial")
public class City<E> implements Serializable {

  private List<E> children;
  private String cityName;
  private String cityCode;
  private String cityNick;
  private String parentId;
  private String proviceId;
  private String menuIds;


  public List<E> getChildren() {
    return children;
  }

  public void setChildren(List<E> children) {
    this.children = children;
  }

  public String getCityName() {
    return cityName;
  }

  public void setCityName(String cityName) {
    this.cityName = cityName;
  }

  public String getCityCode() {
    return cityCode;
  }

  public void setCityCode(String cityCode) {
    this.cityCode = cityCode;
  }

  public String getCityNick() {
    return cityNick;
  }

  public void setCityNick(String cityNick) {
    this.cityNick = cityNick;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getMenuIds() {
    return menuIds;
  }

  public void setMenuIds(String menuIds) {
    this.menuIds = menuIds;
  }

  public String getProviceId() {
    return proviceId;
  }

  public void setProviceId(String proviceId) {
    this.proviceId = proviceId;
  }



}
