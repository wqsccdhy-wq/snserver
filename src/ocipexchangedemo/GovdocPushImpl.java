package ocipexchangedemo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.seeyon.oa.exchange.OCIPServicesServlet;
import com.seeyon.ocip.common.utils.LogUtils;
import com.seeyon.ocip.configuration.OcipConfiguration;
import com.seeyon.ocip.exchange.api.IBussinessService;
import com.seeyon.ocip.exchange.exceptions.ExchangeException;
import com.seeyon.ocip.exchange.model.BIZContentType;
import com.seeyon.ocip.exchange.model.edoc.EdocOperation;

import ocipexchangedemo.bo.GovdocInfo;

public class GovdocPushImpl implements IGovdocPush {
	
	private String wsdlUrl;
	
	private String method;
	
	private String signWsdlUrl;
	
	private String signMethod;

	@Override
	public void push(GovdocInfo info) {

		ExecutorService threadPool = Executors.newCachedThreadPool();

		threadPool.submit(new Runnable() {

			@Override
			public void run() {
				try {
					//String wsdlUrl = "http://10.194.96.112:12399/general/lgt/getNewWorkFlow.service.php?wsdl";// webservice地址
					//String method = "newWorkFlow"; // 调用的那个方法
					String jsonString = JSONObject.toJSONString(info, SerializerFeature.WriteMapNullValue);
					StringBuffer info = new StringBuffer();
					info.append("GovdocPushImpl--->>jsonString：" + jsonString);
					info.append("|wsdlUrl：" + wsdlUrl + "|method:" + method);
					LogUtils.info(GovdocPushImpl.class, info.toString());
					Object[] param = new Object[] { jsonString, "1" };// 传递的参数值
					// String namespaceUrl1 =
					// "http://service.digitalproduct.tkbs.com";// 命名空间
					String namespaceUrl = "";// 命名空间
					Class[] opReturnType = new Class[] { String[].class };// 返回值类型
					String result = axis2RPCInvoke(wsdlUrl, method, param, namespaceUrl, opReturnType);
					System.out.println(result);
					LogUtils.info(GovdocPushImpl.class, "推送数据到通达结果 ：" + result);
					try {
						Map<String, String> params = JSONObject.parseObject(result, new TypeReference<Map<String, String>>() {
						});
						String flag = params.get("flag");
						if ("0".equals(flag)) {
							//TODO 根据返回结果，调用OCIP签收接口
							String groupId = params.get("groupId");
							String title = params.get("subject");
							String detailId = params.get("detailId");
							String exchNo = params.get("exchNo");
							Map<String, Object> paramMap = new HashMap<String, Object>();
							paramMap.put("groupId", groupId);
							paramMap.put("subject", title);
							paramMap.put("detailId", detailId);
							paramMap.put("exchNo", exchNo);
							paramMap.put("exchangeStatus", "0");
							//paramMap.put("comment", comment);
							paramMap.put("name", OCIPServicesServlet.sendMemName);//公文签收人员名称
							paramMap.put("id", OCIPServicesServlet.sendMemLocalId);//公文签收人员本地ID
							paramMap.put("edocOperation",  EdocOperation.ACCEPTED);
							getBussinessService().fireExchange(BIZContentType.RET, paramMap);
						}
					} catch (ExchangeException e) {
						LogUtils.error(GovdocPushImpl.class, "签收公文异常", e);
					}
				} catch (Exception e) {
					e.printStackTrace();
					LogUtils.info(GovdocPushImpl.class, "推送数据到通达异常", e);
				}

			}
		});

		threadPool.shutdown();

	}

	/**
	 * https://www.cnblogs.com/demingblog/p/3264688.html
	 * 
	 * @param wsdlUrl
	 * @param methodName
	 * @param parameter
	 * @param namespaceURI
	 * @param returnType
	 * @return
	 */
	public String axis2RPCInvoke(String wsdlUrl, String methodName, Object[] parameter, String namespaceURI,
			Class[] returnType) {
		Object[] ret = null;
		RPCServiceClient serviceClient = null;
		try {
			/*
			 * 此处RPCServiceClient 对象实例建议定义成类中的static变量 ，否则多次调用会出现连接超时的错误。
			 */
			serviceClient = new RPCServiceClient();
			Options options = serviceClient.getOptions();
			options.setTimeOutInMilliSeconds(3000);
			EndpointReference targetEPR = new EndpointReference(wsdlUrl);
			options.setTo(targetEPR);
			QName opQName = new QName(methodName);
			ret = serviceClient.invokeBlocking(opQName, parameter, returnType);
			// System.out.println(((String[]) ret[0])[0]);
		} catch (AxisFault e) {
			e.printStackTrace();
			LogUtils.error(GovdocPushImpl.class, "调用推送数据到通达异常", e);
		}finally{
			if(serviceClient != null){
				try {
					serviceClient.cleanupTransport();
					serviceClient = null;
				} catch (AxisFault e) {
					serviceClient = null;
					//e.printStackTrace();
				}
			}
		}
		return ((String[]) ret[0])[0];
	}

	public String getWsdlUrl() {
		return wsdlUrl;
	}

	public void setWsdlUrl(String wsdlUrl) {
		this.wsdlUrl = wsdlUrl;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	private IBussinessService getBussinessService() {
		return OcipConfiguration.getInstance().getExchangeSpi().getBussinessService();
	}

	@Override
	public void signEdoc(String summaryId, String staus) {
		ExecutorService threadPool = Executors.newCachedThreadPool();
		
		threadPool.submit(new Runnable() {

			@Override
			public void run() {
				try {
					StringBuffer bufferInfo = new StringBuffer();
					bufferInfo.append("GovdocPushImpl推送签收状态--->>summaryId：" + summaryId + "|staus:" + staus);
					bufferInfo.append("|signWsdlUrl：" + signWsdlUrl + "|signMethod:" + signMethod);
					LogUtils.info(GovdocPushImpl.class, bufferInfo.toString());
					Object[] param = new Object[] { summaryId, staus };// 传递的参数值
					String namespaceUrl = "";// 命名空间
					Class[] opReturnType = new Class[] { String[].class };// 返回值类型
					String result = axis2RPCInvoke(signWsdlUrl, signMethod, param, namespaceUrl, opReturnType);
					System.out.println(result);
					LogUtils.info(GovdocPushImpl.class, "推送签收数据结果 ：" + result);
				} catch (Exception e) {
					LogUtils.error(GovdocPushImpl.class, "推送签收数据异常summaryId:" + summaryId, e);
				}

			}
		});

		threadPool.shutdown();
	}

	public String getSignWsdlUrl() {
		return signWsdlUrl;
	}

	public void setSignWsdlUrl(String signWsdlUrl) {
		this.signWsdlUrl = signWsdlUrl;
	}

	public String getSignMethod() {
		return signMethod;
	}

	public void setSignMethod(String signMethod) {
		this.signMethod = signMethod;
	}
	
	
	

}
