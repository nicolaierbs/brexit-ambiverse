package eu.erbs.ambiverse.model;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class EntityRepresentation implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static String SEP = " @@@ ";

	private String id;
	private String name;
	private List<String> categories;
	private List<String> categoryNames;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<String> getCategories()
	{
		return categories;
	}

	public void setCategories(List<String> categories)
	{
		this.categories = categories;
	}

	public List<String> getCategoryNames()
	{
		return categoryNames;
	}

	public void setCategoryNames(List<String> categoryNames)
	{
		this.categoryNames = categoryNames;
	}

	@Override
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(getId());
		buffer.append(SEP);
		buffer.append(getName());
		buffer.append(SEP);
		buffer.append(StringUtils.join(getCategories(), SEP));
		return buffer.toString();
	}

}
