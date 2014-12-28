package de.clubspot.mediator.templates;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.cocoon.ProcessingException;

public class WebHarvestTemplate implements SourceTemplate,
		Iterable<SourceTemplate> {
	String sUrl;
	String sStartUrl;
	String sName;
	String sPattern;
	String sId;
	String sIcon;
	String sDateFormat;
	String sCountryCode;
	int lft, rgt, level;
	private Connection connection;

	List<SourceTemplate> lChildren;

	public WebHarvestTemplate(Connection connection) {
		super();
		this.connection = connection;
	}

	public WebHarvestTemplate(String id, Connection connection) throws ProcessingException {
		this(connection);
		System.out.println("ID:"+id);
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

	public Iterator<SourceTemplate> iterator() {
		if (this.getChildren() == null) {
			this.loadChildrenFromDatabase();
		}
		return this.lChildren.iterator();
	}

	public int getLft() {
		return lft;
	}

	public void setLft(int lft) {
		this.lft = lft;
	}

	public int getRgt() {
		return rgt;
	}

	public void setRgt(int rft) {
		this.rgt = rft;
	}

	public String getCountryCode() {
		return sCountryCode;
	}

	public void setCountryCode(String countryCode) {
		this.sCountryCode = countryCode;
	}
	
	public void loadFromDatabase() throws ProcessingException {

		Connection instance = this.connection;

		Statement stmt;

		String sQuery = null;

		try {

			stmt = instance.createStatement();

			sQuery = "SELECT n.name, n.uid, n.url, n.starturl, n.pattern, n.icon, n.dateformat, n.countrycode, n.lft, n.rgt"
					+ " FROM tx_lrmediator_pattern AS n"
					+ " WHERE n.uid="
					+ this.getId();

			//System.out.println(sQuery);
			
			ResultSet rs = stmt.executeQuery(sQuery);

			rs.next();

			//System.out.printf("%s, %s %n", rs.getString(1), rs.getString(2));

			this.setName(rs.getString("name"));
			this.setUrl(rs.getString("url"));
			this.setStartUrl(rs.getString("starturl"));
			this.setPattern(rs.getString("pattern"));
			this.setIcon(rs.getString("icon"));
			this.setDateFormat(rs.getString("dateformat"));
			this.setRgt(rs.getInt("rgt"));
			this.setLft(rs.getInt("lft"));
			this.setLevel(0);
			this.setCountryCode(rs.getString("countryCode"));


			rs.close();
			stmt.close();

		} catch (SQLException e) {
			System.out.println(sQuery);
			throw new ProcessingException(e);
		}

	}

	public void loadChildrenFromDatabase() {

		Connection instance = this.connection;

		Statement stmt;

		this.lChildren = new ArrayList<SourceTemplate>();

		try {

			stmt = instance.createStatement();

			ResultSet rs = stmt
					.executeQuery("SELECT o.name, o.uid, o.url, o.starturl, o.pattern, o.icon, o.dateformat, o.countrycode, o.lft, o.rgt,"
							+ " COUNT(p.uid) AS level"
							+ " FROM tx_lrmediator_pattern AS n, tx_lrmediator_pattern AS p, tx_lrmediator_pattern AS o"
							+ " WHERE n.uid="
							+ this.getId()
							+ " AND o.uid != n.uid "
							+ " AND o.uid != p.uid "
							+ " AND o.lft BETWEEN p.lft AND p.rgt"
							+ " AND o.lft BETWEEN n.lft AND n.rgt"
							+ " GROUP BY o.lft, o.name, o.uid, o.url, o.starturl, o.pattern, o.icon, o.dateformat, o.countrycode ORDER BY o.lft;");

			while (rs.next()) {
				System.out
						.printf("%s, %s %n", rs.getString(1), rs.getString(2));

				WebHarvestTemplate tmpTemplate = new WebHarvestTemplate(this.connection);
				tmpTemplate.setId(rs.getString("uid"));
				tmpTemplate.setName(rs.getString("name"));
				tmpTemplate.setUrl(rs.getString("url"));
				tmpTemplate.setStartUrl(rs.getString("starturl"));
				tmpTemplate.setPattern(rs.getString("pattern"));
				tmpTemplate.setIcon(rs.getString("icon"));
				tmpTemplate.setDateFormat(rs.getString("dateformat"));
				tmpTemplate.setRgt(rs.getInt("rgt"));
				tmpTemplate.setLft(rs.getInt("lft"));
				tmpTemplate.setLevel(rs.getInt("level"));
				tmpTemplate.setCountryCode(rs.getString("countrycode"));

				this.lChildren.add(tmpTemplate);
			}

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getCompiledPattern() {
		System.out.println(this.getNameWithoutWhitestripes());
		
		Iterator<SourceTemplate> iter = this.iterator();
		
		StringBuffer subPattern = new StringBuffer();
		
		while (iter.hasNext()) {
			SourceTemplate subTemplate = iter.next();
			this.setPattern(
					this.getPattern().
						replaceAll("%uid:"+subTemplate.getId()+"%", subTemplate.getNameWithoutWhitestripes())
					);

			subPattern.append(subTemplate.getCompiledPattern());

		}

		System.out.println("actual Level:"+this.getLevel());
		
		StringBuffer message = new StringBuffer();
		
		if (this.getLevel() > 0) {
			message.append("\n<function name=\""+this.getNameWithoutWhitestripes()+"\">\n")
			.append("\t<return>\n")
			.append(this.getPattern())
			.append("\n\t</return>\n</function>");
		} else { }
		
		message.append(subPattern);
		
		if (this.getLevel() == 0) {
			message.append("\n<var-def name=\"result\">\n")
			.append("\n<![CDATA[<source>")
			.append("<link>"+this.getUrl()+"</link>")
			.append("<name>"+this.getName()+"</name>")
			.append("<icon>"+this.getIcon()+"</icon>")
			.append("</source>]]>")
			.append(this.getPattern())
			.append("\n</var-def>\n");
		}
		
		return message.toString();
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public List<SourceTemplate> getChildren() {
		return lChildren;
	}

	public void setChildren(List<SourceTemplate> children) {
		lChildren = children;
	}

}
