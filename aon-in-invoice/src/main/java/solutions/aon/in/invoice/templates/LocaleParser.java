package solutions.aon.in.invoice.templates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocaleParser {

	public static ParserContext getContext(String text) {
		ParserContext ret = null;
		for ( ParserContext ctx : ParserContext.values()) {
			String[] patterns = ctx.getAcceptPatterns();
			for (String pat : patterns) {
				Pattern pattern = Pattern.compile( pat, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(text);
				if (matcher.find()) {
					ret = ctx;
					break;
				};
			}
			if (ret != null) {
				break;
			}
		}
		return ret!=null?ret:ParserContext.getDefault();
	}

}
