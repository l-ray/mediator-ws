package de.clubspot.mediator.templates;

import org.apache.cocoon.pipeline.ProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class WebHarvestTemplate implements SourceTemplate {

    private static final Logger LOG =
            LoggerFactory.getLogger(WebHarvestTemplate.class.getName());


	String sUrl;
	String sStartUrl;
	String sName;
	String sPattern;
	String sId;
	String sIcon;
	String sDateFormat;
	String sCountryCode;
    String sSubPattern;

	private Connection connection;

	public WebHarvestTemplate(Connection connection) {
		super();
		this.connection = connection;
	}

	public WebHarvestTemplate(String id, Connection connection) throws ProcessingException {
		this(connection);
		LOG.trace("ID:"+id);
		this.setId(id);
		this.loadFromDatabase();
	}

	public String getUrl() {
		return sUrl;
	}

	public void setUrl(String url) {
		sUrl = url;
	}

	public String getStartUrl() {
		return sStartUrl;
	}

	public void setStartUrl(String startUrl) {
		sStartUrl = startUrl;
	}

	public String getName() {
		return sName;
	}
	
	public String getNameWithoutWhitestripes() {
		return sName.replaceAll("[ ,]", "_");
	}

	public void setName(String name) {
		sName = name;
	}

	public String getPattern() {
		return sPattern;
	}

	public void setPattern(String pattern) {
		sPattern = pattern;
	}

	public String getId() {
		return sId;
	}

	public void setId(String id) {
		sId = id;
	}

	public String getIcon() {
		return sIcon;
	}

	public void setIcon(String icon) {
		sIcon = icon;
	}

	public String getDateFormat() {
		return sDateFormat;
	}

	public void setDateFormat(String dateFormat) {
		sDateFormat = dateFormat;
	}

	public String getCountryCode() {
		return sCountryCode;
	}

	public void setCountryCode(String countryCode) {
		this.sCountryCode = countryCode;
	}

    public String getSubPattern() {
        return sSubPattern;
    }

    public void setSubPattern(String subPattern) {
        this.sSubPattern = subPattern;
    }

    public void loadFromDatabase() throws ProcessingException {

		Connection instance = this.connection;

		Statement stmt;

		String sQuery = null;

		try {

			stmt = instance.createStatement();

			sQuery = "SELECT name, id, url, starturl, pattern, subpattern, icon, dateformat, countrycode"
					+ " FROM pattern AS n"
					+ " WHERE n.id="
					+ this.getId();

			//LOG.trace(sQuery);
			
			ResultSet rs = stmt.executeQuery(sQuery);

			rs.next();

			//System.out.printf("%s, %s %n", rs.getString(1), rs.getString(2));

			this.setName(rs.getString("name"));
			this.setUrl(rs.getString("url"));
			this.setStartUrl(rs.getString("starturl"));
			this.setPattern(rs.getString("pattern"));
			this.setIcon(rs.getString("icon"));
			this.setDateFormat(rs.getString("dateformat"));
            this.setSubPattern(rs.getString("subpattern"));
			this.setCountryCode(rs.getString("countryCode"));

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			LOG.trace(sQuery);
			throw new ProcessingException(e);
		}

	}

	public String getCompiledPattern() {
		LOG.trace(this.getNameWithoutWhitestripes());

		StringBuilder message = new StringBuilder()
            .append(this.getSubPattern() == null ? "":"\n"+this.getSubPattern())
            .append("\n<var-def name=\"result\">\n")
			/*.append("\n<![CDATA[<source>")
			.append("\n<link>").append(this.getUrl()).append("</link>")
			.append("\n<name>").append(this.getName()).append("</name>")
			.append("\n<icon>").append(this.getIcon()).append("</icon>")
			.append("\n</source>]]>\n")*/
            .append(this.getPattern())
			.append("\n</var-def>\n");
            LOG.trace(message.toString());
		return message.toString();
	}

}
