package mediawiki.task.config;

public class TokenPositioner implements ACtWPPositioner {

	private String[] tokens;
	
	public TokenPositioner(String...tokens){
		this.tokens = tokens;
	}
	
	
	@Override
	public String insert(String content, String template) {
		boolean flag = true;
		for(String token : tokens){
			if(content.indexOf(token) > -1){
				content = content.substring(0,content.indexOf(token))+"{{"+template+"}}\n"+content.substring(content.indexOf(token));
				flag = false;
				break;
			}
		}
		if(flag){
			content += "\n{{"+template+"}}";
		}
		return content;
	}


	public String[] getTokens() {
		return tokens;
	}


	public void setTokens(String[] tokens) {
		this.tokens = tokens;
	}

}
