package de.clubspot.mediator.templates;

public interface SourceTemplate {
	String getUrl();
	String getStartUrl();
	String getName();
	String getNameWithoutWhitestripes();
	String getPattern();
	String getCompiledPattern();
	String getIcon();
	String getId();
	String getDateFormat();

    String toXML();
}
