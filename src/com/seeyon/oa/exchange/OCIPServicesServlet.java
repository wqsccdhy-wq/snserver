package com.seeyon.oa.exchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.seeyon.ocip.common.IConstant;
import com.seeyon.ocip.common.exceptions.InterfaceException;
import com.seeyon.ocip.common.utils.LogUtils;
import com.seeyon.ocip.configuration.OcipConfiguration;
import com.seeyon.ocip.exchange.api.IBussinessService;
import com.seeyon.ocip.exchange.model.BIZContentType;
import com.seeyon.ocip.org.agent.BaseAgent;
import com.seeyon.ocip.org.agent.UnitAgent;
import com.seeyon.ocip.org.agent.UserAgent;
import com.seeyon.ocip.org.entity.OcipUnit;
import com.seeyon.ocip.org.entity.OcipUser;

import ocipexchangedemo.GovdocPushImpl;

public class OCIPServicesServlet extends HttpServlet {

	private static final long serialVersionUID = 3670842542658827130L;
	private static final String rootID = "-4730833917433371643";
	private static final String rootName = "遂宁市";
	public static String sendOrgName = "遂宁市生态环境局";
	public static String sendOrgLocalId = "4556842553182555622";
	//通达数据库id=100
	public static String sendMemName = "杨春燕";
	public static String sendMemLocalId = "-7227801111876620996";
	public static String recOrgName = "四川省生态环境厅";
	//-4987235428039243802
	public static String recOrgLocalId = "-4987235428039243802";

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		req.setCharacterEncoding("utf-8");

		StringBuffer sb = new StringBuffer();
		// 接收数据
		BufferedReader reader = req.getReader();

		String tmp = "";
		while ((tmp = reader.readLine()) != null) {
			sb.append(tmp);
		}
		reader.close();
		Map<String, Object> params = null;
		String acceptJson = sb.toString();
		LogUtils.info(GovdocPushImpl.class, "参数信息：--->>acceptJson：" + acceptJson);
		if (!"".equals(acceptJson)) {
			params = JSONObject.parseObject(acceptJson, new TypeReference<Map<String, Object>>() {
			});
		}

		JSONObject jsonobj = new JSONObject();
		if (params == null || params.isEmpty()) {
			jsonobj.put("msg", "参数为空");
			return;
		}

		Object object = params.get("type");
		if (object == null) {
			jsonobj.put("msg", "type参数为空");
			return;
		}
		String param = (String) object;
		PrintWriter outt = resp.getWriter();

		//jsonobj.put("msg", "success");
		// 上传组织机构
		if ("org".equals(param)) {
			boolean upLoadOrgIUnit = upLoadOrgIUnit();
			boolean upLoadOrgUser = upLoadOrgUser();
			if (upLoadOrgIUnit && upLoadOrgUser) {
				jsonobj.put("msg", "success");
			} else {
				jsonobj.put("msg", "error");
			}
		} else if ("send".equals(param)) {// 发送公文
			params.put("recOrgID", recOrgLocalId);// 公文接收单位名称
			params.put("recOrgName", recOrgName);// 公文接收单位ID
			try {
				getBussinessService().fireExchange(BIZContentType.OFC, params);
				jsonobj.put("msg", "success");
			} catch (Exception e) {
				jsonobj.put("msg", e.getMessage());
				//outt.println(jsonobj);
				// e.printStackTrace();
				LogUtils.error(OCIPServicesServlet.class, "发送公文异常", e);
			}

		}
		outt.println(jsonobj);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * 上报单位
	 * 
	 * @return
	 */
	private boolean upLoadOrgIUnit() {
		boolean result = true;
		List<OcipUnit> unitInfos = new ArrayList<OcipUnit>();
		// 根节点必须要上传
		OcipUnit root = new OcipUnit();
		root.setObjectId(rootID);// 单位id,注意：根节点ID在上传时不要用这个，请用代码生成一个随机的id，以免不同的单位出现相同根节点的问题
		root.setName(rootName);// 名称
		root.setForeignName("");
		root.setShortName(rootName);
		root.setAliasName("");
		root.setSortId(1);// 排序号
		root.setCode(rootID);// 单位id,注意：根节点ID在上传时不要用这个，请用代码生成一个随机的id，以免不同的单位出现相同根节点的问题
		root.setIsEnable(IConstant.ENABLE);
		root.setParentId("0");// 上级单位ID，根节点没有上级单位，设置为0
		// unitInfos.add(root);

		OcipUnit unit1 = new OcipUnit();
		// unit1.setObjectId("2556842553182670622");//单位id
		unit1.setObjectId(sendOrgLocalId);// 单位id
		unit1.setName(sendOrgName);// 名称
		unit1.setForeignName("");
		unit1.setShortName(sendOrgName);
		unit1.setAliasName("");
		unit1.setSortId(2);// 排序号
		unit1.setCode(sendOrgLocalId);// 单位id
		unit1.setIsEnable(IConstant.ENABLE);
		unit1.setParentId(rootID);// 上级单位ID

		unitInfos.add(root);
		unitInfos.add(unit1);
		JSONObject req = new JSONObject();
		req.put("units", unitInfos);
		//BaseAgent.getInstance(DepartmentAgent.class)
		try {
			// 上传单位
			String rtn = BaseAgent.getInstance(UnitAgent.class)
					.uploadFull(JSONObject.toJSONString(req, SerializerFeature.WriteMapNullValue));

			if (rtn == null || rtn.equals("")) {
				System.out.println("上报单位失败,代理或平台网络连接出错");
				result = false;
			}
			System.out.println("同步单位结果：" + rtn);
		} catch (InterfaceException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			LogUtils.error(OCIPServicesServlet.class, "同步单位异常", e);
			result = false;
		}

		return result;
	}

	/**
	 * 上报人员
	 * 
	 * @return
	 */
	private boolean upLoadOrgUser() {
		boolean result = true;
		List<OcipUser> ocipUsers = new ArrayList<OcipUser>();
		OcipUser user = new OcipUser();
		user.setObjectId(sendMemLocalId);// 人员ID
		user.setName(sendMemName);// 异构人员名称
		user.setCode(sendMemLocalId);// 设置为人员ID
		user.setLoginName("csry1");// 登陆名
		user.setPassword("12345");// 密码
		user.setSex(OcipUser.SEX_MALE);// 性别
		user.setBirthday(new Date());// 生日
		user.setNation("");
		user.setTelNumber("13541246000");// 手机号
		user.setSortId(1);// 排序号
		user.setIsEnable(IConstant.ENABLE);// 启用
		user.setLevelId("");
		user.setIsAdmin(1);// 设置为非单位管理员
		List<OcipUser.Relation> relations = new ArrayList<OcipUser.Relation>();
		OcipUser.Relation relation = new OcipUser.Relation();
		relation.setType(OcipUser.Relation.RELATION_TYPE_POST_MAIN);
		relation.setUnitId(sendOrgLocalId);// 单位ID
		relations.add(relation);
		user.setRelations(relations);
		ocipUsers.add(user);
		JSONObject req = new JSONObject();
		req.put("users", ocipUsers);

		try {
			// 上传人员
			String rtn = BaseAgent.getInstance(UserAgent.class)
					.uploadFull(JSONObject.toJSONString(req, SerializerFeature.WriteMapNullValue));

			if (rtn == null || rtn.equals("")) {
				System.out.println("上报人员失败,代理或平台网络连接出错");
				result = false;
			}
			System.out.println("同步人员结果：" + rtn);
		} catch (InterfaceException e) {
			LogUtils.error(OCIPServicesServlet.class, "同步部门异常", e);
			// e.printStackTrace();
			result = false;
		}

		return result;
	}

	private IBussinessService getBussinessService() {
		return OcipConfiguration.getInstance().getExchangeSpi().getBussinessService();
	}

}
