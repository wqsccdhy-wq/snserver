package ocipexchangedemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.seeyon.oa.exchange.OCIPServicesServlet;
import com.seeyon.ocip.common.IConstant;
import com.seeyon.ocip.common.IConstant.AddressType;
import com.seeyon.ocip.common.entry.Address;
import com.seeyon.ocip.common.license.OcipKeyMananger;
import com.seeyon.ocip.common.org.OcipOrgMember;
import com.seeyon.ocip.common.org.OcipOrgRelation;
import com.seeyon.ocip.common.org.OcipOrgUnit;
import com.seeyon.ocip.common.org.OrgMember;
import com.seeyon.ocip.common.organization.IOrganizationManager;
import com.seeyon.ocip.exchange.api.IBussinessHandler;
import com.seeyon.ocip.exchange.exceptions.BussinessException;
import com.seeyon.ocip.exchange.model.BIZContentType;
import com.seeyon.ocip.exchange.model.BIZExchangeData;
import com.seeyon.ocip.exchange.model.BIZMessage;
import com.seeyon.ocip.exchange.model.BussinessResult;
import com.seeyon.ocip.exchange.model.Organization;
import com.seeyon.ocip.exchange.model.edoc.EdocOperation;
import com.seeyon.ocip.exchange.model.edoc.RETEdocObject;
/**
 * 公文操作数据封装参考DEMO
 * @author Administrator
 *
 */
public class EdocRETExchangeHandler implements IBussinessHandler{
	
	private IOrganizationManager organizationManager;

	@Override
	public List<BussinessResult> exchangeReceive(BIZExchangeData in) throws BussinessException {
		List<BussinessResult> bussinessResults = new ArrayList<BussinessResult>();
		BIZMessage bussnissMessage = in.getBussnissMessage();
		Organization organization = in.getSender();
		String groupId = in.getGroupId();//公文ID
		Address creater = organization.getIdentification();
		BIZContentType contentType = bussnissMessage.getContentType();
		List<Organization> reciList = in.getRecivers();
		for (Organization organization2 : reciList) {
			BussinessResult result = new BussinessResult();
			result.setCode("0");
			result.setIdentifier(in.getIdentifier());
			result.setOrganization(organization2);
			result.setMessage("公文回执消息接受成功");
			bussinessResults.add(result);
		}
		if(BIZContentType.RET.equals(contentType)){
			try {
				RETEdocObject retEdocObject = (RETEdocObject)bussnissMessage.getContent();
				EdocOperation operation = retEdocObject.getOperation();
				/**
				 * operation.name=Received表示对方系统收到数据，生成了代办公文
				 * operation.name=Accepted表示对方系统签收了公文
				 * operation.name=StepBack表示对方系统回退了公文
				 * operation.name=REVOKED表示对方系统 撤销了公文
				 */
				String name = creater.getName();
				String id = creater.getId();
				String type = creater.getType();
				String detailId = retEdocObject.getDetailId();//
				if(operation.equals(EdocOperation.RECEIVED)){
					/**
					 * TODO 注意
					 * 当异构系统发送公文到OCIP时，如果OCIP收到数据，OICO会返回originMessageId和detailId值，这两个值异构系统需要保持下来，异构系统撤销公文时需要传递指给OCIP
					 */
					
					/**
					 * exchangeStatus:
					 * “0”: 接受数据正常;
					 * “1”: 发送失败，收文单位未设置收文交换流程;
					 * “2”: 发送失败，收文单位的交换流程被停用或者删除;
					 * “3”: 发送失败，接收单位没有找到对应人员;
					 * “4”: 交换模板触发失败
					 */
					String exchangeStatus = retEdocObject.getExchangeStatus();
					String originMessageId = retEdocObject.getOriginMessageId();//公文关联交换号，需要保持下来
					System.out.println("公文关联交换号originMessageId:" + originMessageId);
					System.out.println("对方系统收到数据，生成了代办公文,公文ID为：" + groupId + " detailId为:" + detailId + " 收文单位为:" + name + " 单位ID：" + id);
				}else if (operation.equals(EdocOperation.ACCEPTED)) {
					String opinion = retEdocObject.getOpinion();
					/*OrgMember localMember = organizationManager.getLocalMember(id);
					String orgPlatformUserId = localMember.getOrgPlatformUserId();
					OcipOrgMember member = organizationManager.getMember(orgPlatformUserId);*/
					OcipOrgMember member = organizationManager.getMember(creater.getId(), creater.getResource());
					List<OcipOrgRelation> condition = member.getRelations();
					if (condition != null && !condition.isEmpty()) {
						OcipOrgRelation ocipOrgRelation = condition.get(0);
						String unitId = ocipOrgRelation.getUnitId();
						OcipOrgUnit account = organizationManager.getAccount(unitId);
						String unitName = account.getName();
					}
					System.out.println("对方系统签收了公文,公文ID为：" + groupId + " detailId为:" + detailId + " 签收人为:" + name + " 人员ID:" + id + " 签收回复:" + opinion);
				}else if (operation.equals(EdocOperation.STEPBACK)) {
					String opinion = retEdocObject.getOpinion();
					System.out.println("对方系统回退了公文,公文ID为：" + groupId + " detailId为:" + detailId +" 回退人为:" + name + " 人员ID:" + id + " 回退原因:" + opinion);
				}else if (operation.equals(EdocOperation.REVOKED)) {
					String opinion = retEdocObject.getOpinion();
					System.out.println("对方系统撤销了公文,公文ID为："  + groupId + " detailId为:" + detailId +" 撤销人为:" + name + " 人员ID:" + id + " 撤销原因:" + opinion);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				String msg = "ocip接受公文回执信息执行失败";
				for (BussinessResult bussinessResult : bussinessResults) {
					bussinessResult.setCode("2");
					bussinessResult.setMessage(msg);
				}
			}
		}
		return bussinessResults;
	}

	@Override
	public BIZExchangeData exchangeSend(Map<String, Object> params) throws BussinessException {
		BIZExchangeData bizData = new BIZExchangeData();
		bizData.setIdentifier(UUID.randomUUID().toString().replaceAll("-", ""));
		String exchangeStatus = params.get("exchangeStatus")!=null?(String)params.get("exchangeStatus"):"";
		String subject = (String) params.get("subject");
		bizData.setSubject(subject);
		Object object = params.get("detailId");
		String detailvar = String.valueOf(object);
		
		EdocOperation edocOperation = (EdocOperation) params.get("edocOperation");
		List<Organization> receivers = new ArrayList<Organization>();
		
		BIZMessage bizMessage = new BIZMessage();
		bizMessage.setContentType(BIZContentType.RET);
		RETEdocObject retEdocObject = new RETEdocObject();
		retEdocObject.setOperation(edocOperation);
		retEdocObject.setOriginMessageId(UUID.randomUUID().toString().replaceAll("-", ""));
		bizMessage.setContent(retEdocObject);
		
		//公文回执方法地址信息
		Address createUser = new Address();
		String localSystemCode = OcipKeyMananger.getSysCode();
		createUser.setResource(localSystemCode);// 本系统注册id值
		createUser.setType(IConstant.AddressType.system.name());
		createUser.setId(localSystemCode);
		createUser.setName(localSystemCode);// 本系统名称
		
		retEdocObject.setDetailId(detailvar);
		retEdocObject.setExchangeStatus(exchangeStatus);
		
		Address ocipsys = new Address();
		ocipsys.setId(createUser.getId());
		ocipsys.setName(createUser.getName());
		ocipsys.setResource(createUser.getResource());
		ocipsys.setType(createUser.getType());
		retEdocObject.setOcipSyscode(ocipsys.toString());
		
		//异构系统接收到公文后，需要给OCIP发送一个回执信息，告知OCIP异构系统收到的数据
		if (edocOperation == EdocOperation.RECEIVED) {
			String name  = OCIPServicesServlet.sendOrgName;//回执发起者单位名称
			String id = "";//回执发起者本地单位ID 
			Object sendUnitIdObj = params.get("sendUnitId");
			if (sendUnitIdObj != null) {
				id = String.valueOf(sendUnitIdObj);
			}
			createUser.setName(name);//撤销公文发起者名称
			createUser.setType(AddressType.account.name());
			createUser.setId(id);//撤销公文发起者ID
			Object relationId = params.get("relationId");
			Object groupId = params.get("groupId");
			Object detailId = params.get("detailId");
			bizData.setGroupId(String.valueOf(groupId));
			retEdocObject.setOriginMessageId(String.valueOf(relationId));
			retEdocObject.setDetailId(String.valueOf(detailId));
		}
		
		//签收公文
		if (edocOperation == EdocOperation.ACCEPTED) {
			retEdocObject.setRecNo("");
			String name = (String) params.get("name");//公文签收发起人员名称
			String id = (String) params.get("id");//公文签收发起人员ID
			createUser.setName(name);
			createUser.setType(AddressType.member.name());
			createUser.setId(id);
			Object groupId = params.get("groupId");
			Object detailId = params.get("detailId");
			Object exchNo = params.get("exchNo");
			bizData.setGroupId(String.valueOf(groupId));
			retEdocObject.setOriginMessageId(String.valueOf(exchNo));
			retEdocObject.setDetailId(String.valueOf(detailId));
			String comment = (String)params.get("comment");//签收回复
			if(comment != null && !comment.isEmpty()){
				retEdocObject.setOpinion(comment);
			}
		}
		
		//撤销公文
		if (edocOperation == EdocOperation.REVOKED) {
			String name = (String) params.get("name");
			String id = (String) params.get("id");
			createUser.setName(name);//撤销公文发起者名称
			createUser.setType(AddressType.member.name());
			createUser.setId(id);//撤销公文发起者ID
			String comment = (String)params.get("comment");//撤销原因
			if(comment != null && !comment.isEmpty()){
				retEdocObject.setOpinion(comment);
			}
			String groupId = String.valueOf(params.get("groupId"));// 签收回复
			bizData.setGroupId(groupId);
			Object originMessageIdObj = params.get("exchNo");
			retEdocObject.setOriginMessageId(String.valueOf(originMessageIdObj));
		}
		
		// 回退公文
		if (edocOperation == EdocOperation.STEPBACK) {
			//retEdocObject.setRecNo("");
			String name = (String) params.get("name");
			String id = (String) params.get("id");
			createUser.setName(name);//公文回退人员名称
			createUser.setType(AddressType.member.name());
			createUser.setId(id);//公文回退人员名称本地ID
			Object groupId = params.get("groupId");
			Object detailId = params.get("detailId");
			Object exchNo = params.get("exchNo");
			bizData.setGroupId(String.valueOf(groupId));
			retEdocObject.setOriginMessageId(String.valueOf(exchNo));
			retEdocObject.setDetailId(String.valueOf(detailId));
			String comment = (String)params.get("comment");//回退原因
			if(comment != null && !comment.isEmpty()){
				retEdocObject.setOpinion(comment);
			}
		}
		
		//接收者信息
		Organization reciverOrg = new Organization();
		Address recAdd = new Address();
		recAdd.setResource("0");
		//recUnitId
		String recAccountId = "";
		String recOrgName = "";//接收单位名称 
		// 撤销
		if (edocOperation == EdocOperation.REVOKED) {
			String accountId = OCIPServicesServlet.recOrgLocalId;//接单位本地单位ID
			//将接收单位的本地ID转换为OCIP对应的单位ID
			OcipOrgUnit account = organizationManager.getAccount(accountId);//OCIP单位实体
			recAccountId = account.getId();
			recOrgName = account.getName();//接收单位名称
		}else {
			/**
			 * 注意：实际开发中，recAccountId和recOrgName，应该从数据库中获取，
			 * demo为了方便用的map保存
			 */
			Map<String, String> recUnitMap = EdocOFCExchangeHandler.getRecUnitMap();
			for (Entry<String, String> entyr : recUnitMap.entrySet()) {
				recAccountId = entyr.getKey();
				recOrgName = entyr.getValue();
			}
		}
		
		recAdd.setId(recAccountId);//接单位对应的OCIP单位ID
		
		/**
		 * 注意：recOrgName的值不能为空，否则OCIP反序列化会失败
		 * 
		 */
		recAdd.setName(recOrgName);
		recAdd.setType("account");
		reciverOrg.setIdentification(recAdd);
		reciverOrg.setName(recOrgName);
		receivers.add(reciverOrg);
		
		//公文回执发送方
		Organization sender = new Organization();
		sender.setIdentification(createUser);
		sender.setName(createUser.getName());
		bizData.setSender(sender);
		
		// 发送源地址信息
		Address source = new Address();
		source.setId(localSystemCode);
		source.setName(localSystemCode);
		
		bizData.setSource(source);
		bizData.setBussnissMessage(bizMessage);
		bizData.setRecivers(receivers);
		
		//String jsonString = JSONObject.toJSONString(bizData, SerializerFeature.WriteMapNullValue);
		
		return bizData;
	}

	@Override
	public BIZContentType type() {
		return BIZContentType.RET;
	}

	public IOrganizationManager getOrganizationManager() {
		return organizationManager;
	}

	public void setOrganizationManager(IOrganizationManager organizationManager) {
		this.organizationManager = organizationManager;
	}

}
