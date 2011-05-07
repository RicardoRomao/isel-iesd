package iesd.rmi.shat.common;

import java.io.Serializable;

public interface ITranslator extends Serializable{
	public String Translate(String text, String from, String to) throws Exception;
}
