package ca.hapke.campingbot.log;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 * @author Nathan Hapke
 */
@Entity
@Table(name = CategoriedPersistence.TABLE, schema = DatabaseConsumer.SCHEMA)
public class CategoriedPersistence {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = CategoriedPersistence.CONTAINER_NAME)
	private String container;

	@Column(name = CategoriedPersistence.CATEGORY_NAME)
	private String category;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = TABLE + "_" + VALUES, joinColumns = @JoinColumn(name = "id"))
	@Column(name = VALUES)
	private List<String> values;

	public static final String CONTAINER_NAME = "container";
	public static final String CATEGORY_NAME = "category";
	public static final String TABLE = "categoried";
	public static final String VALUES = "values";

	public void setValues(List<String> values) {
		this.values = values;
	}

	public int getId() {
		return id;
	}

	public String getContainer() {
		return container;
	}

	public String getCategory() {
		return category;
	}

	public List<String> getValues() {
		return values;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CategoriedPersistence [id=");
		builder.append(id);
		builder.append(" ");
		builder.append(container);
		builder.append(":");
		builder.append(category);
		builder.append("]");
		return builder.toString();
	}
}
