/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nmghr;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <功能描述/>
 *
 * @author zhanghr
 * @date 2018年11月2日 下午2:10:51
 * @version 1.0
 */
@RestController
public class PingController {
  /**
   * 测试连接.
   * 
   * @return
   */
  @GetMapping("/ping")
  public String ping() {
    return "PONG";
  }

  @PostMapping("/test/login")
  public String login() {
    return "知道了";
  }

}
