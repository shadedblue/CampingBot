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
	private int id;
	private String container;
	private String category;
	private List<String> values;

	public static final int DEFAULT_ID = 0;
	public static final String CONTAINER_NAME = "container";
	public static final String CATEGORY_NAME = "category";
	public static final String TABLE = "categoried";
	public static final String VALUES = "values";

//	@Transient
//	private ListEventListener<String> listChangeListener = new ListEventListener<String>() {
//		@Override
//		public void listChanged(ListEvent<String> listChanges) {
//			while (listChanges.next()) {
//				final int type = listChanges.getType();
//				final int index = listChanges.getIndex();
//				// handle insert, update or delete at index
//				if (type == ListEvent.INSERT) {
//					DatabaseConsumer.getInstance().updatePersistence(CategoriedPersistence.this);
//					final Object newValue = listChanges.getNewValue();
//					System.out.println("Inserted: " + newValue);
//				}
//			}
//		}
//	};

	public void setValues(List<String> values) {
		this.values = values;

//		int size = (values != null) ? values.size() + 3 : 3;
//		EventList<String> valuesEvents = new BasicEventList<String>(size);
//
//		if (values != null)
//			valuesEvents.addAll(values);
//		this.values = valuesEvents;
//		valuesEvents.addListEventListener(listChangeListener);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getId() {
		return id;
	}

	@Column(name = CategoriedPersistence.CONTAINER_NAME)
	public String getContainer() {
		return container;
	}

	@Column(name = CategoriedPersistence.CATEGORY_NAME)
	public String getCategory() {
		return category;
	}

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = TABLE + "_" + VALUES, joinColumns = @JoinColumn(name = "id"))
	@Column(name = VALUES)
	public List<String> getValues() {
		if (values == null)
			setValues(null);
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
