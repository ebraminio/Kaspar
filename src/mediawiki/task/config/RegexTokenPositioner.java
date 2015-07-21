package mediawiki.task.config;

import java.util.regex.Pattern;

import util.Util;

public class RegexTokenPositioner extends TokenPositioner {

	public RegexTokenPositioner(String... tokens) {
		super(tokens);
	}
	
	private static final int FLAGS = Pattern.CASE_INSENSITIVE & Pattern.UNICODE_CASE & Pattern.UNICODE_CHARACTER_CLASS & Pattern.UNIX_LINES;
	

	@Override
	public String insert(String content, String template) {
		boolean flag = true;
		int index = Integer.MAX_VALUE;
		for(String token : getTokens()){
			int i = Util.indexOf(Pattern.compile(token,FLAGS), content);
			if(i > -1 && i < index) {
				index = i;
				flag = false;
			}
		}
		if(index != Integer.MAX_VALUE && flag == false) {
			content = content.substring(0,index)+"\n{{"+template+"}}\n"+content.substring(index);
		}else if(flag){
			content += "\n{{"+template+"}}";
		}
		return content;
	}

	

}
