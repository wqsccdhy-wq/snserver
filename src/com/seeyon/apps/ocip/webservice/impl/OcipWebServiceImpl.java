
package com.seeyon.apps.ocip.webservice.impl;

import com.seeyon.apps.ocip.webservice.OcipWebService;
import com.seeyon.ocip.common.ExtIntfController;
import com.seeyon.ocip.configuration.OcipConfiguration;
import com.seeyon.ocip.exchange.api.ITransportService;
import com.seeyon.ocip.exchange.model.MessagePackageFile;

/**
 * 数据服务平台webservice服务端
 * @author wxt.touxin
 * @version 20170615
 *
 */
@javax.jws.WebService(
		endpointInterface = "com.seeyon.apps.ocip.webservice.OcipWebService",
		targetNamespace = "http://ocip.seeyon.com/exchange/",
		serviceName = "ocipWebService",
		portName = "ocipWebServicePort"
		,wsdlLocation = "com/seeyon/apps/ocip/webservice/OcipWebService.wsdl"
)
@javax.xml.ws.BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_MTOM_BINDING)
public class OcipWebServiceImpl implements OcipWebService {
	
	@Override
	public String getJsonDataStr(String data) {
		return ExtIntfController.main(data);
	}

	@Override
	public String reqWebService(String unitId, String linkCode, String seconds, String reqType) {
		return transportService().getResponse().responseTransportCode(unitId, linkCode, seconds, reqType);
	}
	
	
	private ITransportService transportService() {
		return OcipConfiguration.getInstance().getExchangeSpi().getTransportService();
	}

	@Override
	public String sendBaseXMLEsbWebService(String transCode, String message) {
		return transportService().getResponse().responseTransport(transCode, message);
	}

	@Override
	public String sendBaseXMLEsbWebServiceV2(String transCode, MessagePackageFile messagePackageFile) {
		return transportService().getResponse().responseTransport(transCode, messagePackageFile);
	}
}
