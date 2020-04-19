package ocipexchangedemo;

import com.seeyon.ocip.common.utils.OcipService;
import com.seeyon.ocip.exchange.service.ExchMsgService;

public class ExchMsgServiceHandler implements ExchMsgService{
	
	public void initService() {
		OcipService.registerBean(ExchMsgService.class, this);
	}

	@Override
	public void handelExchMsg(String arg0) {
		// TODO Auto-generated method stub
		
	}

}
