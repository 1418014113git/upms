/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.config;

import com.nmghr.basic.common.exception.ErrorInfoInterface;
import com.nmghr.basic.common.exception.GlobalErrorException;

/**
 * <功能描述/>
 *
 * @author doeu
 * @date 2018年11月5日 下午5:11:56
 * @version 1.0
 */
public class UpmsGlobalException extends GlobalErrorException {


  /**
   * 
   */
  private static final long serialVersionUID = 1256174328566954269L;

  public UpmsGlobalException(ErrorInfoInterface errorInfo, Object... args) {
    this(errorInfo, null, args);
  }

  public UpmsGlobalException(ErrorInfoInterface errorInfo, Throwable cause, Object... args) {
    super(errorInfo, cause, args);
  }

  public UpmsGlobalException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }

  public UpmsGlobalException(String code, String message) {
    this(code, message, null);
  }
}
