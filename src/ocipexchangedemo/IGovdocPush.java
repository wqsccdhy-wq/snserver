package ocipexchangedemo;

/**
 * 定义调用第三方接口
 *
 */
import ocipexchangedemo.bo.GovdocInfo;

public interface IGovdocPush {
	/**
	 * 推送数据
	 * @param info
	 */
	public void push(GovdocInfo info);
	
	/**
	 * 签收公文
	 * @param summaryId
	 * @param staus
	 */
	public void signEdoc(String summaryId,String staus);

}
