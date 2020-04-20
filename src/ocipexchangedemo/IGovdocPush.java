package ocipexchangedemo;

/**
 * 定义调用第三方接口
 *
 */
import ocipexchangedemo.bo.GovdocInfo;

public interface IGovdocPush {
	/**
	 * 
	 * @param info
	 */
	public void push(GovdocInfo info);

}
