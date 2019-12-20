package com.nmghr.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sun.misc.BASE64Encoder;

/**
 * 旁路报文认证的网关工具类
 * 
 * @author weichang_ding
 * 
 */
@Service
public class JitGatewayUtil {

  private static final Logger logger = LoggerFactory.getLogger(JitGatewayUtil.class);
	
  
  @Value("${pki.authURL}")
  private String authURL;
  @Value("${pki.appId}")
  private String appId;
  @Value("${pki.randomFrom}")
  private int randomFrom;
  @Value("${pki.accessControl}")
  private String accessControl;
  @Value("${pki.qRCodeAuth}")
  private String qRCodeAuth;
	// 封装认证请求数据bean
	public JitGatewayUtilBean jitGatewayUtilBean;
	
	// 认证返回信息
	public AuthResult authResult;
	
	// 构造方法
	public JitGatewayUtil() {
		this.jitGatewayUtilBean = new JitGatewayUtilBean();
		this.authResult = new AuthResult();
	}
	

	
	/**
	 * 生成认证原文(根据randomFrom配置生成原文)
	 * 
	 * @return 认证原文
	 */
	public String generateRandomNum(int keyRandomFrom) {
		// randomFrom 1：调用应用服务器 2：调用网关
		String random = "";
		if(2==keyRandomFrom){
		  logger.info("调用网关生成原文");
		  random = generateRandomNumFromGagewayServer();
		}else
		{
			logger.info("调用应用服务器生成原文");
			random = generateRandomNumByApplication();
		}
		
		logger.info("生成原文结束，原文：" + random);
		return random;
	}
	
	/**
	 * 应用服务器产生认证原文(第二步 服务端产生认证原文)
	 * 
	 * @return 认证原文
	 */
	public String generateRandomNumByApplication() {
		String num = "1234567890abcdefghijklmnopqrstopqrstuvwxyz";
		int size = 6;
		char[] charArray = num.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size; i++) {
			sb.append(charArray[((int) (Math.random() * 10000) % charArray.length)]);
		}
		return sb.toString();
	}

	/**
	 * 连接网关服务器产生认证原文
	 * 
	 * @return 认证原文
	 * @throws DocumentException
	 */
	public String generateRandomNumFromGagewayServer(){
		String errCode = null;
		String errDesc = null;
		byte[] messagexml = null;
		
		// 组装认证原文请求报文数据
		logger.info("组装认证原文请求报文数据开始");
		Document reqDocument = DocumentHelper.createDocument();
		Element root = reqDocument.addElement(CommonConstant.MSG_ROOT);
		Element requestHeadElement = root.addElement(CommonConstant.MSG_HEAD);
		Element requestBodyElement = root.addElement(CommonConstant.MSG_BODY);
		
		// 组装报文头信息 
		requestHeadElement.addElement(CommonConstant.MSG_VSERSION).setText(CommonConstant.MSG_VSERSION_VALUE_10);
		requestHeadElement.addElement(CommonConstant.MSG_SERVICE_TYPE).setText(
				RandomConstant.MSG_SERVICE_TYPE_VALUE);

		// 组装报文体信息
		// 组装应用标识信息
		requestBodyElement.addElement(CommonConstant.MSG_APPID).
		//setText(configMap.get(ConfigConstant.KEY_APP_ID));
		// jdk1.4
		setText(appId);

		StringBuffer reqMessageData = new StringBuffer();
		try {
			// 将认证原文请求报文写入输出流 开始
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			XMLWriter writer = new XMLWriter(outStream);
			writer.write(reqDocument);
			messagexml = outStream.toByteArray();
			// 将认证原文请求报文写入输出流完毕

			reqMessageData.append("请求内容开始！\n");
			reqMessageData.append(outStream.toString() + "\n");
			reqMessageData.append("请求内容结束！\n");
			logger.info(reqMessageData.toString() + "\n");
		} catch (Exception e) {
			errDesc = "组装原文请求报文时出现异常";
			logger.info("组装原文请求报文时出现异常" + e.getMessage());
		}
		
		// 组装认证原文请求报文数据完毕
		logger.info("组装认证原文请求报文数据结束");

		// 创建与网关的HTTP连接，发送认证原文请求报文，并接收认证原文响应报文
		// 创建与网关的HTTP连接开始
		logger.info("创建与网关的HTTP连接，发送认证原文请求报文开始");
		String authURL = this.authURL;
		int statusCode = 500;
		HttpClient httpClient = new HttpClient();
		PostMethod postMethod = new PostMethod(authURL);
		postMethod.setRequestHeader("Connection", "close");

		// 设置报文传送的编码格式
		postMethod.setRequestHeader("Content-Type", "text/xml;charset=UTF-8");
		// 设置发送认证请求内容开始
		postMethod.setRequestBody(new ByteArrayInputStream(messagexml));
		// 设置发送认证请求内容结束
		
		// 执行postMethod
		try {
			// 发送原文请求报文与网关通讯
			URL url = protocol(authURL);
			statusCode = httpClient.executeMethod(postMethod);
			if(url != null && "https".equals(url.getProtocol())){
				Protocol.unregisterProtocol("https");
			}
		} catch (Exception e) {
			errCode = String.valueOf(statusCode);
			errDesc = e.getMessage();
			logger.info("发送原文请求报文与网关连接出现异常：" + errDesc);
			postMethod.releaseConnection();
			httpClient.getHttpConnectionManager().closeIdleConnections(0);
			httpClient = null;
			String errorJdkMess = errorJdkMess(e);
			if(errorJdkMess != null){
				errDesc = errorJdkMess;
			}
			e.printStackTrace();
			return null;
		}
		logger.info("创建与网关的HTTP连接，发送认证原文请求报文结束");

		// 网关返回认证原文响应
		StringBuffer respMessageData = new StringBuffer();
		String respMessageXml = null;
		// 当返回200或500状态时处理业务逻辑
		if (statusCode == HttpStatus.SC_OK || 
				statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
			try {
				// 接收通讯报文并处理开始
				byte[] inputstr = postMethod.getResponseBody();

				ByteArrayInputStream byteinputStream = new ByteArrayInputStream(inputstr);
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				int ch = 0;
				try {
					while ((ch = byteinputStream.read()) != -1) {
						int upperCh = (char) ch;
						outStream.write(upperCh);
					}
				} catch (Exception e) {
					errDesc = e.getMessage();
				}

				// 200 表示返回处理成功
				if (statusCode == HttpStatus.SC_OK) {
					respMessageData.append("响应内容开始！\n");
					respMessageData.append(new String(outStream.toByteArray(),"UTF-8") + "\n");
					respMessageData.append("响应内容开始！\n");
					respMessageXml = new String(outStream.toByteArray(),"UTF-8");
				} else {
					// 500 表示返回失败，发生异常
					respMessageData.append("响应500内容开始！\n");
					respMessageData.append(new String(outStream.toByteArray()) + "\n");
					respMessageData.append("响应500内容结束！\n");
					errCode = String.valueOf(statusCode);
					errDesc = new String(outStream.toByteArray());
				}
				logger.info("网关返回响应内容：" + respMessageData.toString());
			} catch (IOException e) {
				errCode = String.valueOf(statusCode);
				errDesc = e.getMessage();
				logger.info("读取原文请求响应报文出现异常：" + errCode + "," + errDesc);
			} finally {
				if (httpClient != null) {
					postMethod.releaseConnection();
					httpClient.getHttpConnectionManager().closeIdleConnections(0);
				}
			}
		}

		// 接收并解析网关服务器返回的原文请求响应报文
		Document respDocument = null;
		Element headElement = null;
		Element bodyElement = null;
		
		logger.info("解析网关服务器返回的原文请求响应报文开始");
		try {
			respDocument = DocumentHelper.parseText(respMessageXml);
		} catch (DocumentException e) {
			logger.info("解析认证返回信息异常：" + e.getMessage());
		}

		headElement = respDocument.getRootElement().element(CommonConstant.MSG_HEAD);
		bodyElement = respDocument.getRootElement().element(CommonConstant.MSG_BODY);

		// 解析报文头开始
		if (headElement != null) {
			boolean state = Boolean.valueOf(
				headElement.elementTextTrim(CommonConstant.MSG_MESSAGE_STATE));
			if (state) {
				errCode = headElement.elementTextTrim(CommonConstant.MSG_MESSAGE_CODE);
				errDesc = headElement.elementTextTrim(CommonConstant.MSG_MESSAGE_DESC);
				logger.info("向网关请求原文失败：" + errCode + "," + errDesc);
			}
		}

		// 解析原文
		Element originalElement = bodyElement.element(CommonConstant.MSG_ORIGINAL);
		String original = "";
		if (originalElement != null) {
			original = originalElement.getStringValue();
			logger.info("向网关请求原文成功，生成原文：" + original);
		}
		logger.info("解析网关服务器返回的原文请求响应报文结束");
		
		// 返回认证原文
		return original;
	}
	
	/**
	 * 身份认证
	 * @return
	 */
	public void auth(){
		// 成功标记
		boolean isSuccess = true;
		String errCode = null; 
		String errDesc = null;
		String original_data_base64 = null;
		
		// 认证地址、应用标识
		String authURL = this.authURL;
		//String appId = configMap.get(ConfigConstant.KEY_APP_ID);
		String appId = this.appId;
		
		// 校验应用标识或网关认证地址不可为空
		if (!isNotNull(appId) || !isNotNull(authURL)) {
			isSuccess = false;
			errDesc = "应用标识或网关认证地址不可为空";
			logger.info("应用标识或网关认证地址不可为空\n");
		}
		logger.info("应用标识及网关的认证地址读取成功！\n应用标识：" + appId + "\n认证地址：" + authURL + "\n");
		
		// 服务端验证认证原文
		if (isSuccess && !AuthConstant.MSG_AUTH_MODE_QRCODE_VALUE.equalsIgnoreCase(jitGatewayUtilBean.getAuthMode())) {
			logger.info("服务端验证认证原文开始");
			if (isNotNull(jitGatewayUtilBean.original_data) && 
				isNotNull(jitGatewayUtilBean.signed_data) && 
				isNotNull(jitGatewayUtilBean.original_jsp)) {
				// 服务端验证认证原文
				if (!jitGatewayUtilBean.original_data.equalsIgnoreCase(jitGatewayUtilBean.original_jsp)) {
					isSuccess = false;
					errDesc = "服务端验证认证原文失败:客户端提供的认证原文与服务端的不一致";
					logger.info(errDesc);
				} else {
					// 生成随机密钥 
					original_data_base64 = new BASE64Encoder().encode(jitGatewayUtilBean.original_jsp.getBytes());
					logger.info("服务端验证认证原文:服务端验证认证原文成功！\n认证原文：" + jitGatewayUtilBean.original_jsp
							+ "\n认证请求包：" + jitGatewayUtilBean.signed_data + "\n");
				}
			} else {
				isSuccess = false;
				errDesc = "服务端验证认证原文失败:证书认证数据不完整";
				logger.info("服务端验证认证原文失败:证书认证数据不完整！\n");
			}
			logger.info("服务端验证认证原文结束");
		}
		
		// 请求网关进行认证
		try{
			byte[] messagexml = null;
			if (isSuccess) {
				logger.info("组装认证请求报文开始");
				// 组装认证请求报文数据开始
				Document reqDocument = DocumentHelper.createDocument();
				Element root = reqDocument.addElement(CommonConstant.MSG_ROOT);
				Element requestHeadElement = root.addElement(CommonConstant.MSG_HEAD);
				Element requestBodyElement = root.addElement(CommonConstant.MSG_BODY);
				
				// 组装报文头信息 ,组装认证xml文件时，进行判断，如果配置调用应用服务器生成原文，则生成xml version版本为1.0，
				// 如果配置从网关生成原文，则生成xml version版本为1.1
				if(2 == randomFrom){
					requestHeadElement.addElement(CommonConstant.MSG_VSERSION).setText(
						CommonConstant.MSG_VSERSION_VALUE_11);
				}else{
					requestHeadElement.addElement(CommonConstant.MSG_VSERSION).setText(
						CommonConstant.MSG_VSERSION_VALUE_10);
				}
				
				// 服务类型
				requestHeadElement.addElement(CommonConstant.MSG_SERVICE_TYPE).setText(
						AuthConstant.MSG_SERVICE_TYPE_VALUE);

				// 组装报文体信息
				// 组装客户端信息
				Element clientInfoElement = requestBodyElement.addElement(AuthConstant.MSG_CLIENT_INFO);
				Element clientIPElement = clientInfoElement.addElement(AuthConstant.MSG_CLIENT_IP);
				clientIPElement.setText(jitGatewayUtilBean.remoteAddr);

				// 组装应用标识信息
				requestBodyElement.addElement(CommonConstant.MSG_APPID).setText(appId);
				Element authenElement = requestBodyElement.addElement(AuthConstant.MSG_AUTH);
				Element authCredentialElement = authenElement.addElement(AuthConstant.MSG_AUTHCREDENTIAL);

				// 组装证书认证信息
				if (AuthConstant.MSG_AUTH_MODE_CERT_VALUE.equalsIgnoreCase(jitGatewayUtilBean.authMode)){
					authCredentialElement.addAttribute(AuthConstant.MSG_AUTH_MODE,
							AuthConstant.MSG_AUTH_MODE_CERT_VALUE);
					authCredentialElement.addElement(AuthConstant.MSG_DETACH).setText(
							jitGatewayUtilBean.signed_data);
					authCredentialElement.addElement(CommonConstant.MSG_ORIGINAL).setText(
							original_data_base64);
				}
				else if (AuthConstant.MSG_AUTH_MODE_QRCODE_VALUE.equalsIgnoreCase(jitGatewayUtilBean.authMode)){
					authCredentialElement.addAttribute(AuthConstant.MSG_AUTH_MODE,
							AuthConstant.MSG_AUTH_MODE_QRCODE_VALUE);
					authCredentialElement.addElement(AuthConstant.MSG_TOKEN).setText(
							jitGatewayUtilBean.token);
					authCredentialElement.addElement(AuthConstant.MSG_QRCODE).setText(
							jitGatewayUtilBean.qrcode);
				}

				// 是否检查访问控制状态
				requestBodyElement.addElement(AuthConstant.MSG_ACCESS_CONTROL).
				//setText(configMap.get(ConfigConstant.KEY_ACCESS_CONTROL));
				setText(this.accessControl);

				// 组装属性查询列表信息
				Element attributesElement = requestBodyElement.addElement(AuthConstant.MSG_ATTRIBUTES);
				attributesElement.addAttribute(AuthConstant.MSG_ATTRIBUTE_TYPE, AuthConstant.MSG_ATTRIBUTE_TYPE_ALL);
				StringBuffer reqMessageData = new StringBuffer();
				try {
					ByteArrayOutputStream outStream = new ByteArrayOutputStream();
					XMLWriter writer = new XMLWriter(outStream);
					writer.write(reqDocument);
					messagexml = outStream.toByteArray();

					reqMessageData.append("请求内容开始！\n");
					reqMessageData.append(outStream.toString() + "\n");
					reqMessageData.append("请求内容结束！\n");
					logger.info(reqMessageData.toString() + "\n");
				} catch (Exception e) {
					isSuccess = false;
					errDesc = "组装认证请求报文出现异常";
					logger.info("组装认证请求报文出现异常" + e.getMessage());
				}
				logger.info("组装认证请求报文结束");
			}

			// 创建与网关的HTTP连接，发送认证请求报文，并接收认证响应报文
			// 创建与网关的HTTP连接 开始
			int statusCode = 500;
			HttpClient httpClient = null;
			PostMethod postMethod = null;
			if (isSuccess) {
				logger.info("向网关发送认证请求开始");
				// HTTPClient对象
				httpClient = new HttpClient();
				postMethod = new PostMethod(authURL);
				postMethod.setRequestHeader("Connection","close");
				

				// 设置报文传送的编码格式
				postMethod.setRequestHeader("Content-Type","text/xml;charset=UTF-8");
				// 设置发送认证请求内容开始
				postMethod.setRequestBody(new ByteArrayInputStream(messagexml));
				// 设置发送认证请求内容结束
				// 执行postMethod
				try {
					URL url = protocol(authURL);
					//  发送通讯报文与网关通讯
					statusCode = httpClient.executeMethod(postMethod);
					if(url != null && "https".equals(url.getProtocol())){
						Protocol.unregisterProtocol("https");
					}
				} catch (Exception e) {
					isSuccess = false;
					errCode = String.valueOf(statusCode);
					errDesc = e.getMessage();
					logger.info("向网关发送认证请求失败：与网关连接出现异常:" + errDesc);
					postMethod.releaseConnection();
					httpClient.getHttpConnectionManager().closeIdleConnections(0);
					httpClient = null ;
					String errorJdkMess = errorJdkMess(e);
					if(errorJdkMess != null){
						errDesc = errorJdkMess;
					}
					throw e;
				}
				logger.info("向网关发送认证请求结束");
			}
			
			// 读取网关返回的认证响应报文 
			StringBuffer respMessageData = new StringBuffer();
			String respMessageXml = null;
			if (isSuccess) {
				logger.info("读取网关返回的认证响应报文开始");
				// 当返回200或500状态时处理业务逻辑
				if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
					// 从头中取出转向的地址
					try {
						// 接收通讯报文并处理开始
						byte[] inputstr = postMethod.getResponseBody();

						ByteArrayInputStream ByteinputStream = new ByteArrayInputStream(inputstr);
						ByteArrayOutputStream outStream = new ByteArrayOutputStream();
						int ch = 0;
						try {
							while ((ch = ByteinputStream.read()) != -1) {
								int upperCh = (char) ch;
								outStream.write(upperCh);
							}
						} catch (Exception e) {
							isSuccess = false;
							errDesc = e.getMessage();
						}

						if (isSuccess) {
							// 200 表示返回处理成功
							if (statusCode == HttpStatus.SC_OK) {
								respMessageData.append("响应内容开始！\n");
								respMessageData.append(new String(outStream.toByteArray(), "UTF-8") + "\n");
								respMessageData.append("响应内容结束！\n");
								respMessageXml = new String(outStream.toByteArray(), "UTF-8");
							} else {
								// 500 表示返回失败，发生异常
								respMessageData.append("响应500内容开始！\n");
								respMessageData.append(new String(outStream.toByteArray()) + "\n");
								respMessageData.append("响应500内容结束！\n");
								isSuccess = false;
								errCode = String.valueOf(statusCode);
								errDesc = new String(outStream.toByteArray());
							}
							logger.info(respMessageData.toString());
						}
						// 接收通讯报文并处理结束
					} catch (IOException e) {
						isSuccess = false;
						errCode = String.valueOf(statusCode);
						errDesc = e.getMessage();
						logger.info("读取网关返回的认证响应报文出现异常:" + errDesc);
					} finally{
						if(httpClient != null){
							postMethod.releaseConnection();
							httpClient.getHttpConnectionManager().closeIdleConnections(0);
						}
					}
				}
				logger.info("读取网关返回的认证响应报文结束");
			}

			// 服务端处理(解析网关返回的认证响应报文)
			Document respDocument = null;
			Element headElement = null;
			Element bodyElement = null;
			if (isSuccess) {
				logger.info("解析网关返回的认证响应报文开始");
				// 特殊字符'&'处理
				respMessageXml = respMessageXml.replaceAll("&", "&amp;");
				respDocument = DocumentHelper.parseText(respMessageXml);

				headElement = respDocument.getRootElement().element(CommonConstant.MSG_HEAD);
				bodyElement = respDocument.getRootElement().element(CommonConstant.MSG_BODY);

				// 解析报文头
				if (headElement != null) {
					boolean state = Boolean.valueOf(
						headElement.elementTextTrim(CommonConstant.MSG_MESSAGE_STATE));
					if (state) {
						isSuccess = false;
						errCode = headElement.elementTextTrim(CommonConstant.MSG_MESSAGE_CODE);
						errDesc = headElement.elementTextTrim(CommonConstant.MSG_MESSAGE_DESC);
						logger.info("网关认证业务处理失败！\t" + errDesc + "\n");
					}
				}
			}

			if (isSuccess) {
				logger.info("解析报文头成功！\n");
				// 解析报文体
				// 解析认证结果集
				Element authResultElement = bodyElement.element(AuthConstant.MSG_AUTH_RESULT_SET)
						.element(AuthConstant.MSG_AUTH_RESULT);

				isSuccess = Boolean.valueOf(authResultElement.attributeValue(AuthConstant.MSG_SUCCESS));
				if (!isSuccess) {
					errCode = authResultElement.elementTextTrim(AuthConstant.MSG_AUTH_MESSSAGE_CODE);
					errDesc = authResultElement.elementTextTrim(AuthConstant.MSG_AUTH_MESSSAGE_DESC);
					logger.info("身份认证失败，失败原因：" + errDesc);
				}
			}

			if (isSuccess) {
				logger.info("身份认证成功！\n");
				String accessControlResult = bodyElement.elementTextTrim("accessControlResult");
				logger.info("网关根据规则对该用户计算的访问控制结果:" + accessControlResult);
				
				// 设置返回认证结果信息
				authResult.setAccessControlResult(accessControlResult);
				if("Deny".equals(accessControlResult)){
					isSuccess = false;
					errCode = "-1";
					errDesc = "该用户无权限访问此应用";
				}
				else{
					// 解析用户属性列表
					Element attrsElement = bodyElement.element(AuthConstant.MSG_ATTRIBUTES);
					if (attrsElement != null) {
						//List<Element> namespacesElements = (List<Element>)attrsElement
						List namespacesElements = (List)attrsElement
								.elements(AuthConstant.MSG_ATTRIBUTE);
						if (namespacesElements != null
								&& namespacesElements.size() > 0) {
							logger.info("属性个数：" + namespacesElements.size());
							for (int i = 0; i < namespacesElements.size(); i++) {
								//String arrs = namespacesElements.get(i).attributeValue(AuthConstant.MSG_NAMESPACE);
								String arrs = ((Element)namespacesElements.get(i)).attributeValue(AuthConstant.MSG_NAMESPACE);
								logger.info(arrs);
							}
							/*Map<String[],String> certAttributeNodeMap = new HashMap<String[],String>();
							Map<String[],String> childAttributeNodeMap = new HashMap<String[],String>();
							Map<String[],String> umsAttributeNodeMap = new HashMap<String[],String>();
							Map<String[],String> pmsAttributeNodeMap = new HashMap<String[],String>();*/
							Map certAttributeNodeMap = new HashMap();
							Map childAttributeNodeMap = new HashMap();
							Map umsAttributeNodeMap = new HashMap();
							Map pmsAttributeNodeMap = new HashMap();
							Map customAttributeNodeMap = new HashMap();
							String[] keyes = new String[2];
							if (attrsElement != null) {
								List attributeNodeList = attrsElement
										.elements(AuthConstant.MSG_ATTRIBUTE);
								for (int i = 0; i < attributeNodeList.size(); i++) {
									keyes = new String[2];
									Element userAttrNode = (Element) attributeNodeList.get(i);
									String msgParentName = userAttrNode
											.attributeValue(AuthConstant.MSG_PARENT_NAME);
									String name = userAttrNode
											.attributeValue(AuthConstant.MSG_NAME);
									String value = userAttrNode.getTextTrim();
									keyes[0] = name;
									childAttributeNodeMap.clear();
									//String arrs = namespacesElements.get(i).attributeValue(AuthConstant.MSG_NAMESPACE);
									String arrs = ((Element)namespacesElements.get(i)).attributeValue(AuthConstant.MSG_NAMESPACE);
									if (arrs.trim().equals(AuthConstant.KEY_NAMESPACE_CINAS)) {
										// 证书信息
										if (msgParentName != null
												&& !msgParentName.equals("")) {
											keyes[1] = msgParentName;
											if (value != null && value.length() > 0)
												childAttributeNodeMap.put(keyes,value);
										} else {
											if (value != null && value.length() > 0)
												certAttributeNodeMap.put(keyes,value);
										}
										certAttributeNodeMap.putAll(childAttributeNodeMap);
									} else if (arrs.trim().equals(AuthConstant.KEY_NAMESPACE_UMS)) {
										// UMS信息
										if (msgParentName != null
												&& !msgParentName.equals("")) {
											keyes[1] = msgParentName;
											if (value != null && value.length() > 0)
												childAttributeNodeMap.put(keyes,value);
										} else {
											if (value != null && value.length() > 0)
												umsAttributeNodeMap.put(keyes,value);
										}
										umsAttributeNodeMap.putAll(childAttributeNodeMap);
									//} else if (arrs.trim().contains(AuthConstant.KEY_NAMESPACE_PMS)) {
									} else if (arrs.trim().indexOf(AuthConstant.KEY_NAMESPACE_PMS) != -1) {
										// PMS信息
										if (msgParentName != null
												&& !msgParentName.equals("")) {
											keyes[1] = msgParentName;
											if (value != null && value.length() > 0)
												childAttributeNodeMap.put(keyes,value);
										} else {
											if (value != null && value.length() > 0)
												pmsAttributeNodeMap.put(keyes,value);
										}
										pmsAttributeNodeMap.putAll(childAttributeNodeMap);
									} else if (arrs.trim().indexOf(AuthConstant.KEY_NAMESPACE_CUSTOM) != -1) {
										// 自定义信息
										if (msgParentName != null
												&& !msgParentName.equals("")) {
											keyes[1] = msgParentName;
											if (value != null && value.length() > 0)
												childAttributeNodeMap.put(keyes,value);
										} else {
											if (value != null && value.length() > 0)
												customAttributeNodeMap.put(keyes,value);
										}
										customAttributeNodeMap.putAll(childAttributeNodeMap);
									} else {
										// 如果有其他的属性信息添加到证书信息里面
										if (msgParentName != null && !msgParentName.equals("")) {
											keyes[1] = msgParentName;
											if (value != null && value.length() > 0)
												childAttributeNodeMap.put(keyes,value);
										} else {
											if (value != null && value.length() > 0)
												certAttributeNodeMap.put(keyes,value);
										}
										certAttributeNodeMap.putAll(childAttributeNodeMap);
									}
								}
								// 设置返回认证结果信息
								authResult.setCertAttributeNodeMap(certAttributeNodeMap);
								authResult.setUmsAttributeNodeMap(umsAttributeNodeMap);
								authResult.setPmsAttributeNodeMap(pmsAttributeNodeMap);
								authResult.setCustomAttributeNodeMap(customAttributeNodeMap);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			isSuccess = false;
			e.printStackTrace();
			errDesc = e.getMessage();
		}
		logger.info("解析网关返回的认证响应报文结束");
		
		// 设置返回认证结果信息
		authResult.setSuccess(isSuccess);
		authResult.setErrCode(errCode);
		authResult.setErrDesc(errDesc);
	}

	/**
	 * 密文方式发送请求前调用
	 * @param authURL
	 * @return
	 */
	public static URL protocol(String authURL){
		// 发送原文请求报文与网关通讯
		URL url = getUrl(authURL);
		if(url != null && "https".equals(url.getProtocol())){
			int port = 443;
			if(url.getPort() != -1){
				port = url.getPort();
			}
			Protocol https = new Protocol("https", new HTTPSSecureProtocolSocketFactory(), port);
			Protocol.registerProtocol("https", https);
		}
		return url;
	}
	
	/**
	 * jdk版本支持校验
	 * @param e
	 * @return
	 */
	private static String errorJdkMess(Exception e){
		String errDesc = null; 
		//已测试jdk1.6和jdk1.7情况。
		if ("Received fatal alert: protocol_version"
				.equals(e.getMessage())
				|| "Remote host closed connection during handshake"
				.equals(e.getMessage())) {
			float weight = Float.parseFloat(getJavaVersion());
			
			if (weight <= 1.7f) {
				errDesc = "当前运行的服务器jdk版本过低，需要升级jdk到1.8版本以上；或将网关SSL级别调整为低级";
				System.err.println(errDesc);
			}
		}
		return errDesc;
	}
	
	/**
	 * 获取当前运行jdk版本返回前两位
	 * @return
	 */
	public static String getJavaVersion(){
		String javaVersion = System.getProperty("java.version");
		String[] array = javaVersion.split("\\.");
		String newVersion = "";
		for (int i = 0; i < 2; i++) {
			newVersion += array[i];
			if(i == 0){
				newVersion += ".";
			}
		}
		return newVersion;
	}
	
	/**
	 * 根据传入的url地址返回url对象
	 * @param urlAddress
	 * @return
	 */
	private static URL getUrl(String urlAddress){
		URL url = null;
		try {
			url = new URL(urlAddress);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	
	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return
	 */
	public boolean isNotNull(String str) {
		if (str == null || str.trim().equals(""))
			return false;
		else
			return true;
	}
	

	
	/**
	 * 配置常量
	 * @author weichang_ding
	 *
	 */
	public static class ConfigConstant{
		// 认证地址
		public static final String KEY_AUTH_URL = "authURL";

		// 应用标识
		public static final String KEY_APP_ID = "appId";

		// 是否开启开维码认证
		public static final String KEY_QRCODE_AUTH = "QRCodeAuth";
		
		// 调用应用服务器或网关生成原文
		public static final String KEY_RANDOM_FROM = "randomFrom";
		
		// 是否检查访问控制状态
		public static final String KEY_ACCESS_CONTROL = "accessControl";
		
		// 生成二维码地址
		public static final String KEY_GENERATEQRCODE_URL = "generateQRCodeURL";
		
		// 查询二维码状态地址
		public static final String KEY_QUERYQRCODESTATE_URL = "queryQRCodeStateURL";
		
		// 配置文件加载成功标记
		public static final String KEY_CONFIG_SUCCESS = "configSuccess";
	}
	
	/**
	 * 报文公共常量
	 * @author weichang_ding
	 *
	 */
	static class CommonConstant{
		// 报文根结点
		public static final String MSG_ROOT = "message";

		// 报文头结点
		public static final String MSG_HEAD = "head";

		// 报文体结点
		public static final String MSG_BODY = "body";

		// 服务版本号
		public static final String MSG_VSERSION = "version";

		// 服务版本值
		public static final String MSG_VSERSION_VALUE_10 = "1.0";
		public static final String MSG_VSERSION_VALUE_11 = "1.1";

		// 服务类型
		public static final String MSG_SERVICE_TYPE = "serviceType";

		// 报文体 应用ID
		public static final String MSG_APPID = "appId";

		// 响应报文状态
		public static final String MSG_MESSAGE_STATE = "messageState";

		// 错误代码
		public static final String MSG_MESSAGE_CODE = "messageCode";

		// 错误描述
		public static final String MSG_MESSAGE_DESC = "messageDesc";

		// 认证原文
		public static final String MSG_ORIGINAL = "original";
	}
	
	/**
	 * 随机数报文常量
	 * @author weichang_ding
	 *
	 */
	static class RandomConstant{
		// 服务类型值
		public static final String MSG_SERVICE_TYPE_VALUE = "OriginalService";
	}
	
	/**
	 * 认证报文常量
	 * @author weichang_ding
	 *
	 */
	public static class AuthConstant{
		// 服务类型值
		public static final String MSG_SERVICE_TYPE_VALUE = "AuthenService";
		
		// 报文体 认证方式
		public static final String MSG_AUTH_MODE = "authMode";

		// 报文体 证书认证方式
		public static final String MSG_AUTH_MODE_CERT_VALUE = "cert";

		// 报文体 口令认证方式
		public static final String MSG_AUTH_MODE_PASSWORD_VALUE = "password";
		
		// 报文体 二维码认证方式
		public static final String MSG_AUTH_MODE_QRCODE_VALUE = "qrcode";

		// 报文体 属性集
		public static final String MSG_ATTRIBUTES = "attributes";
		
		// 报文体 自定义属性集
		public static final String MSG_CUSTOM_ATTRIBUTES  = "customAttributes";

		// 报文体 属性
		public static final String MSG_ATTRIBUTE = "attr";

		// 报文体 属性名
		public static final String MSG_NAME = "name";

		// 报文父级节点
		public static final String MSG_PARENT_NAME = "parentName";

		// 报文体 属性空间
		public static final String MSG_NAMESPACE = "namespace";
		
		// 访问控制
		public static final String MSG_ACCESS_CONTROL = "accessControl";

		// 访问控制 ture
		public static final String MSG_ACCESS_CONTROL_TRUE = "true";

		// 访问控制 false
		public static final String MSG_ACCESS_CONTROL_FALSE = "false";

		// 报文体 认证结点
		public static final String MSG_AUTH = "authen";

		// 报文体 认证凭据
		public static final String MSG_AUTHCREDENTIAL = "authCredential";

		// 报文体 客户端结点
		public static final String MSG_CLIENT_INFO = "clientInfo";

		// 报文体 公钥证书
		public static final String MSG_CERT_INFO = "certInfo";

		// 报文体 客户端结点
		public static final String MSG_CLIENT_IP = "clientIP";

		// 报文体 detach认证请求包
		public static final String MSG_DETACH = "detach";
		
		// 报文体 证书类型，PM 证书为：PM
		public static final String MSG_CERTTYPE = "certType";

		// 报文体 用户名
		public static final String MSG_USERNAME = "username";

		// 报文体 口令
		public static final String MSG_PASSWORD = "password";
		
		// 报文体 Token
		public static final String MSG_TOKEN = "token";
		
		// QRCode
		public static final String MSG_QRCODE = "QRCode";

		// 报文体 属性类型
		public static final String MSG_ATTRIBUTE_TYPE = "attributeType";

		// 指定属性 portion
		public static final String MSG_ATTRIBUTE_TYPE_PORTION = "portion";

		// 指定属性 all
		public static final String MSG_ATTRIBUTE_TYPE_ALL = "all";
		
		// 指定属性列表控制项 attrType
		public static final String MSG_ATTR_TYPE = "attrType";

		// 报文体 认证结果集
		public static final String MSG_AUTH_RESULT_SET = "authResultSet";

		// 报文体 认证结果
		public static final String MSG_AUTH_RESULT = "authResult";

		// 报文体 认证结果状态
		public static final String MSG_SUCCESS = "success";

		// 报文体 认证错误码
		public static final String MSG_AUTH_MESSSAGE_CODE = "authMessageCode";

		// 报文体 认证错误描述
		public static final String MSG_AUTH_MESSSAGE_DESC = "authMessageDesc";
		
		// 二维码随机数 
		public static final String KEY_JIT_QRCODE = "jit_qrcode";
		
		// session中原文
		public static final String KEY_ORIGINAL_DATA = "original_data";

		// 客户端返回的认证原文，request中原文
		public static final String KEY_ORIGINAL = "original";

		// 签名结果
		public static final String KEY_SIGNED_DATA = "signed_data";
		
		// namespace 证书信息
		public static final String KEY_NAMESPACE_CINAS = "http://www.jit.com.cn/cinas/ias/ns/saml/saml11/X.509";
		
		// namespace ums信息
		public static final String KEY_NAMESPACE_UMS = "http://www.jit.com.cn/ums/ns/user";
		
		// namespace pms信息
		public static final String KEY_NAMESPACE_PMS = "http://www.jit.com.cn/pmi/pms";
		
		// namespace 自定义信息
		public static final String KEY_NAMESPACE_CUSTOM = "http://www.jit.com.cn/gw/custom/attribute";
	}
	
	/**
	 * 二维码相关常量
	 * @author weichang_ding
	 *
	 */
	public static class QRConstant{
		// jit_qrcode
		public static final String KEY_JIT_QRCODE = "jit_qrcode";
		
		// appFlag
		public static final String KEY_APP_FLAG = "appFlag";
		
		// 服务类型
		public static final String KEY_SERVICE_TYPE = "Service_Type";
		
		// 生成二维码
		public static final String KEY_QRCODE_GENERATE = "qrcode_generate";
		
	}
	
	/**
	 * 封装认证请求数据bean
	 * @author weichang_ding
	 *
	 */
	public class JitGatewayUtilBean {
		// 认证URL
//		private String authURL = "http://IPPort/MessageService";
//		private String authURL = "IPPort/MessageService";
		private String authURL;

		// 认证方式
		private String authMode;

		// token
		private String token;

		// 二维码随机数
		private String qrcode;

		// session中原文
		private String original_data;

		// 证书认证请求包
		private String signed_data;

		// 客户端返回的认证原文
		private String original_jsp;

		// 远程地址
		private String remoteAddr;
		
		// 生成二维码URL
//		private String generateQRCodeURL = "http://IPPort/qrcode_generate";
//		private String generateQRCodeURL = "IPPort/jit_qrcode_generate";
		private String generateQRCodeURL;
		
		// 查询二维码URL
//		private String queryQRCodeStateURL="http://IPPort/qrcode_poll?Service_Type=qrcode_poll&jit_qrcode=";
//		private String queryQRCodeStateURL="IPPort/jit_qrcode_poll?Service_Type=qrcode_poll&jit_qrcode=";
		private String queryQRCodeStateURL;
		
		public String getAuthMode() {
			return authMode;
		}

		public void setAuthMode(String authMode) {
			this.authMode = authMode;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getQrcode() {
			return qrcode;
		}

		public void setQrcode(String qrcode) {
			this.qrcode = qrcode;
		}

		public String getOriginal_data() {
			return original_data;
		}

		public void setOriginal_data(String original_data) {
			this.original_data = original_data;
		}

		public String getSigned_data() {
			return signed_data;
		}

		public void setSigned_data(String signed_data) {
			this.signed_data = signed_data;
		}

		public String getOriginal_jsp() {
			return original_jsp;
		}

		public void setOriginal_jsp(String original_jsp) {
			this.original_jsp = original_jsp;
		}

		public String getRemoteAddr() {
			return remoteAddr;
		}

		public void setRemoteAddr(String remoteAddr) {
			this.remoteAddr = remoteAddr;
		}

		
	}

	/**
	 * 认证返回信息
	 * @author weichang_ding
	 *
	 */
	public class AuthResult{
		// 认证成功标记
		private boolean isSuccess;
		
		// 错误编码
		private String errCode;
		
		// 错误描述
		private String errDesc;
		
		// 认证结果-认证属性
		//private Map<String[],String> certAttributeNodeMap;
		private Map certAttributeNodeMap;
		
		// 认证结果-UMS
		//private Map<String[],String> umsAttributeNodeMap;
		private Map umsAttributeNodeMap;
		
		// 认证结果-PMS
		//private Map<String[],String> pmsAttributeNodeMap;
		private Map pmsAttributeNodeMap;
		
		// 认证结果-自定义属性
		private Map customAttributeNodeMap;
		
		// 认证结果-自定义属性
		private String customAttrsElement;
		
		// 认证结果-访问控制
		private String accessControlResult;
		
		public boolean isSuccess() {
			return isSuccess;
		}
		private void setSuccess(boolean isSuccess) {
			this.isSuccess = isSuccess;
		}
		public String getErrCode() {
			return errCode;
		}
		private void setErrCode(String errCode) {
			this.errCode = errCode;
		}
		public String getErrDesc() {
			return errDesc;
		}
		private void setErrDesc(String errDesc) {
			this.errDesc = errDesc;
		}
		public Map getCertAttributeNodeMap() {
			return certAttributeNodeMap;
		}
		private void setCertAttributeNodeMap(Map certAttributeNodeMap) {
			this.certAttributeNodeMap = certAttributeNodeMap;
		}
		public Map getUmsAttributeNodeMap() {
			return umsAttributeNodeMap;
		}
		private void setUmsAttributeNodeMap(Map umsAttributeNodeMap) {
			this.umsAttributeNodeMap = umsAttributeNodeMap;
		}
		public Map getPmsAttributeNodeMap() {
			return pmsAttributeNodeMap;
		}
		private void setPmsAttributeNodeMap(Map pmsAttributeNodeMap) {
			this.pmsAttributeNodeMap = pmsAttributeNodeMap;
		}
		public String getCustomAttrsElement() {
			return customAttrsElement;
		}
		private void setCustomAttrsElement(String customAttrsElement) {
			this.customAttrsElement = customAttrsElement;
		}
		public Map getCustomAttributeNodeMap() {
			return customAttributeNodeMap;
		}
		public void setCustomAttributeNodeMap(Map customAttributeNodeMap) {
			this.customAttributeNodeMap = customAttributeNodeMap;
		}
		public String getAccessControlResult() {
			return accessControlResult;
		}
		public void setAccessControlResult(String accessControlResult) {
			this.accessControlResult = accessControlResult;
		}
		
	}
	
	/**
	 * 生成二维码结果数据
	 * @author weichang_ding
	 *
	 */
	public class QRCodeResult{
		// 成功标记
		private boolean isSuccess;
		
		// 二维码数据
		private byte[] bytes;
		
		// Cookie数据
		private Cookie[] cookies;

		public boolean isSuccess() {
			return isSuccess;
		}

		private void setSuccess(boolean isSuccess) {
			this.isSuccess = isSuccess;
		}

		public byte[] getBytes() {
			return bytes;
		}

		private void setBytes(byte[] bytes) {
			this.bytes = bytes;
		}

		public Cookie[] getCookies() {
			return cookies;
		}

		private void setCookies(Cookie[] cookies) {
			this.cookies = cookies;
		}
	}
	
}
