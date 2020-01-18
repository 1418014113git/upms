/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.upms.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


/**
 * <功能描述/>
 *
 * @author weber
 * @date 2019年4月18日 下午6:39:03
 * @version 1.0
 */
@Mapper
public interface UserIpMapper {
  /**
   * 查ip名单列表
   * @return
   */
  @Select("<script>SELECT id,ip_address as ipAddress,create_time as createTime,creator,type*1 as type,end_time as endTime FROM ip_control_list\n" +
          "WHERE 1=1\n" +
          "<if test=\"type !=null and type != ''\"> and type = #{type} </if>\n" +
          "<if test=\"checkEndTime == 1\"> and end_time >= NOW() </if>\n" +
          "<if test=\"ipAddress !=null and ipAddress !=''\"> and ip_address = #{ipAddress} </if></script>")
  List<Map<String, Object>> getIpList(@Param("type") String type,@Param("checkEndTime") String checkEndTime,@Param("ipAddress") String ipAddress );

  @Select("select `id`, `config_key` as configKey, `config_value` as configValue,  `config_group` as configGroup, `category`,`enable` from sys_config  where config_key = #{configKey}\n")
  Map<String,Object> getConfig(@Param("configKey") String configKey);

  @Insert("INSERT INTO `ip_control_list` (id,`ip_address`, `create_time`, `creator`, `end_time`, `type`) VALUES \n" +
          "(#{id},#{ipAddress}, NOW(), #{creator}, #{endTime}, #{type})")
  int saveToWhite(@Param("map") Map<String,Object> map);

}
