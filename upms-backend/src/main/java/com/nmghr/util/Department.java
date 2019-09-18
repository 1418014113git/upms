/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.util;

import java.io.Serializable;
import java.util.List;

/**
 * 部门树BEAN
 *
 * @author weber  
 * @date 2018年7月4日 下午6:13:31 
 * @version 1.0   
 * @param <E>
 */
@SuppressWarnings("serial")
public class Department implements Serializable  {
  
  private List<Department> children;
  private String name;
  private String id;
  private String parentCode;
  private String code;
  private String menuIds;
  public List<Department> getChildren() {
    return children;
  }
  public void setChildren(List<Department> children) {
    this.children = children;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getParentCode() {
    return parentCode;
  }
  public void setParentCode(String parentCode) {
    this.parentCode = parentCode;
  }
  public String getCode() {
    return code;
  }
  public void setCode(String code) {
    this.code = code;
  }
  public String getMenuIds() {
    return menuIds;
  }
  public void setMenuIds(String menuIds) {
    this.menuIds = menuIds;
  }
  

  
}
