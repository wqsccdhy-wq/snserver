package ocipexchangedemo;

/**
 * 调用第三方接口
 */
import ocipexchangedemo.bo.GovdocInfo;

public interface IGovdocPush {
	
	public void push(GovdocInfo info);

}
