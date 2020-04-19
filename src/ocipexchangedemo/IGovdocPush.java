package ocipexchangedemo;

/**
 * 调用第三方接口定义说明
 *
 */
import ocipexchangedemo.bo.GovdocInfo;

public interface IGovdocPush {
	
	public void push(GovdocInfo info);

}
