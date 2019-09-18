/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.config;

import com.nmghr.basic.common.exception.ErrorInfoInterface;

/**
 * <功能描述/>
 *
 * @author doeu
 * @date 2018年11月5日 下午5:06:57
 * @version 1.0
 */
public enum UpmsErrorEnum implements ErrorInfoInterface {

  UNLOGIN("888001","Login error"),
  UNDELETE("888002","Delete error"),
  NOTENABLED("888003","Enabled error"),
  UNCREATE("888004","Create error"),
  UNLOCK("888005","Lock error"),;
  

  private String code;

  private String message;

  UpmsErrorEnum(String code, String message) {
    this.code = code;
    this.message = message;
  }

  @Override
  public String getCode() {
    return this.code;
  }

  @Override
  public String getMessage() {
    return this.message;
  }

}
