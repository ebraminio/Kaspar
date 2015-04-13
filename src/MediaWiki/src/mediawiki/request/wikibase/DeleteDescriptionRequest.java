package mediawiki.request.wikibase;

public class DeleteDescriptionRequest extends SetDescriptionRequest {

	public DeleteDescriptionRequest(String entity, String language) {
		super(entity, language, "");
	}

}
