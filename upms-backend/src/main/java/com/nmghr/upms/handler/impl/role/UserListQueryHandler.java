package com.nmghr.upms.handler.impl.role;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.core.service.handler.impl.AbstractQueryHandler;

@Service("userListQueryHandler")
public class UserListQueryHandler extends AbstractQueryHandler {

	public UserListQueryHandler(IBaseService baseService) {
		super(baseService);
	}
	
	public Object List(Map<String, Object> requestMap) throws Exception {
		return requestMap;
		
	}

}
