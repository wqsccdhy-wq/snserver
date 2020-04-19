package ocipexchangedemo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.seeyon.ocip.common.utils.LogUtils;

import ocipexchangedemo.bo.GovdocInfo;

public class GovdocPushImpl implements IGovdocPush {
	
	private String wsdlUrl;
	
	private String method;

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
					LogUtils.info(GovdocPushImpl.class, "GovdocPushImpl--->>jsonString：" + jsonString);
					LogUtils.info(GovdocPushImpl.class, "GovdocPushImpl--->>wsdlUrl：" + wsdlUrl + "|method:" + method);
					Object[] param = new Object[] { jsonString, "1" };// 传递的参数值
					// String namespaceUrl1 =
					// "http://service.digitalproduct.tkbs.com";// 命名空间
					String namespaceUrl = "";// 命名空间
					Class[] opReturnType = new Class[] { String[].class };// 返回值类型
					String result = axis2RPCInvoke(wsdlUrl, method, param, namespaceUrl, opReturnType);
					System.out.println(result);
					LogUtils.info(GovdocPushImpl.class, "推送数据到通达结果 ：" + result);
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
	
	

}
