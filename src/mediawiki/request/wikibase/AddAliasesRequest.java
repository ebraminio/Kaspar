package mediawiki.request.wikibase;

public class AddAliasesRequest extends ModifierAliasesRequest {

	public AddAliasesRequest(String base, String language, String summary, String...aliases){
		super(base,"add",language,summary, aliases);
	}
	
}
