package de.clubspot.mediator.templates;

public interface SourceTemplate {
	public String getUrl();
	public String getStartUrl();	
	public String getName();
	public String getNameWithoutWhitestripes();
	public String getPattern();
	public String getCompiledPattern();
	public String getIcon();
	public String getId();
	public String getDateFormat();
	public int getLevel();
}
