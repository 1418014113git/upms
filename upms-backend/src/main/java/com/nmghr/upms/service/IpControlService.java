package com.nmghr.upms.service;

import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.rmdb.datasource.TargetDataSource;
import com.nmghr.upms.mapper.UserIpMapper;
import com.nmghr.util.IPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class IpControlService implements IBaseService {
    @Autowired
    private UserIpMapper userIpMapper;


    @Override
    public Object get(String id) throws Exception {
        return null;
    }

    @Override
    @TargetDataSource(value = "hsyz")
    public Object get(Map<String,Object> param) throws Exception {
        List<Map<String, Object>> whiteIpList = userIpMapper.getIpList("1", "1",null);
        List<Map<String, Object>> blackIpList = userIpMapper.getIpList("2", "1",null);
        boolean isInWhite = isInIpList(whiteIpList, String.valueOf(param.get("ip")));
        boolean isInBlack = isInIpList(blackIpList, String.valueOf(param.get("ip")));

        Map<String, Object> useIpControl = userIpMapper.getConfig("useIpControl");
        if (useIpControl != null) {
            //启用ip控制
            if ("1".equals(String.valueOf(useIpControl.get("enable")))) {
                Map<String, Object> useWhitelist = userIpMapper.getConfig("useWhitelist");
                if (useWhitelist != null) {
                    //启用白名单
                    if ("1".equals(String.valueOf(useWhitelist.get("enable"))))
                        //启用白名单
                        return (isInWhite && !isInBlack);
                        //  //未启用白名单
                    else {
                        return !isInBlack;
                    }
                } else return !isInBlack;

            } else return true;

        }
        //没配置ipcontrol
        else return true;
    }



    @Override
    public Object list(Map<String, Object> requestMap) throws Exception {
        return null;
    }

    @Override
    public Object page(Map<String, Object> requestMap, int currentPage, int pageSize) throws Exception {
        return null;
    }

    @Override
    public Object findAll() throws Exception {
        return null;
    }

    @Override
    public Object update(String id, Map<String, Object> requestBody) throws Exception {
        return null;
    }

    @Override
    public Object update(Map<String, Object> requestParam, Map<String, Object> requestBody) throws Exception {
        return null;
    }

    @Override
    public Object getSequence(String seqName) throws Exception {
        return null;
    }

    @Override
    public void remove(Map<String, Object> requestMap) throws Exception {

    }

    @Override
    public void remove(String id) throws Exception {

    }

    @Override
    public Object save(Map<String, Object> requestMap) throws Exception {
        return null;
    }




    /*
  判断IP是否在相应的名单或等于指定IP
  type 1 白名单
  type 2 黑名单
   */
    private boolean isInIpList(List<Map<String,Object>> ipList,String ipAddr) throws Exception {

        boolean flag = false;
        for (Map<String, Object> map : ipList) {
            String ipAddress = String.valueOf( map.get("ipAddress"));
            if(ipAddress.contains("~")){
                //判断IP是否在地址段
                String[] ip = ipAddress.split("~");
                if(IPUtil.ipExistsInRange(ipAddr, ip[0], ip[1])){
                    flag = true;
                    break;
                }
            }else{
                if(ipAddr.equals(ipAddress)){
                    flag = true;
                    break;
                }
            }
        }
        return  flag;
    }
}
