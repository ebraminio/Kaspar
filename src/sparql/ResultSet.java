package sparql;

import java.util.ArrayList;
import java.util.Iterator;

import javat.xml.Document;
import javat.xml.Element;

public class ResultSet implements Iterable<Result>{

	private ArrayList<String> variables;
	private boolean distinct;
	private boolean ordered;
	private ArrayList<Result> results;
	
	public static ResultSet parse(Document d) throws Exception{
		ResultSet r = new ResultSet();
		r.variables = new ArrayList<>();
		r.results  = new ArrayList<>();
		for(Element v : d.getRootElement().getChildren("head").get(0).getChildren("variable")){
			r.variables.add(v.getAttribute("name").getValue());
		}
		Element res = d.getRootElement().getChildren("results").get(0);
		r.distinct = Boolean.parseBoolean(res.getAttribute("distinct").getValue());
		r.ordered = Boolean.parseBoolean(res.getAttribute("ordered").getValue());
		for(Element rs : res.getChildren("result")){
			r.results.add(new Result(rs));
		}
		return r;
		
	}
	
	public ArrayList<String> getVariables() {
		return variables;
	}
	public boolean isDistinct() {
		return distinct;
	}
	public boolean isOrdered() {
		return ordered;
	}
	public ArrayList<Result> getResults() {
		return results;
	}
	public void setVariables(ArrayList<String> variables) {
		this.variables = variables;
	}
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}
	public void setOrdered(boolean ordered) {
		this.ordered = ordered;
	}
	public void setResults(ArrayList<Result> results) {
		this.results = results;
	}

	@Override
	public Iterator<Result> iterator() {
		return results.iterator();
	}

	public int size(){
		return results.size();
	}
}
