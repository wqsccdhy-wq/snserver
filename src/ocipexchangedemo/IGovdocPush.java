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

}
