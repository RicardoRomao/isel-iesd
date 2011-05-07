package iesd.rmi.shat.server;

import iesd.rmi.shat.common.ITranslator;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class Translator implements ITranslator{

	private static final long serialVersionUID = 1L;

	@Override
	public String Translate(String text, String from, String to) throws Exception {
		Translate.setHttpReferrer("www.isel.pt");
		
		return Translate.execute(text, Language.fromString(from), Language.fromString(to));
	}

}
