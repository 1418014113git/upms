/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nmghr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * upms入口.
 *
 * @author zhanghr 
 * @date 2018年10月25日 下午5:11:59 
 * @version 1.0
 */
@SpringBootApplication
public class Application {

  /**
   * 入口方法.
   * 
   * @param args
   */
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
