package com.seeyon.apps.ocip.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.seeyon.ocip.exchange.model.MessagePackageFile;

/**
 * 数据服务平台webservice服务端
 * <p>
 * Title: OcipWebService
 * <p>
 * Description: TODO
 * <p>
 * Copyright: Copyright (c) 2016
 * 
 * @author wxt.shenchunyou
 * @date 2016-12-14 上午11:34:44
 * @version TODO
 */
@WebService(name = "ocipWebService", targetNamespace = "http://ocip.seeyon.com/exchange/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface OcipWebService {

	@WebMethod(operationName = "getJsonDataStr", action = "urn:getJsonDataStr")
	public String getJsonDataStr(String data);

	@WebMethod(operationName = "reqWebService", action = "urn:reqWebService")
	public String reqWebService(String unitId, String linkCode, String seconds, String reqType);

	@WebMethod(operationName = "sendBaseXMLEsbWebService", action = "urn:sendBaseXMLEsbWebService")
	public String sendBaseXMLEsbWebService(String transCode, String message);

	@WebMethod(operationName = "sendBaseXMLEsbWebServiceV2", action = "urn:sendBaseXMLEsbWebServiceV2")
	public String sendBaseXMLEsbWebServiceV2(String transCode, MessagePackageFile messagePackageFile);

}
