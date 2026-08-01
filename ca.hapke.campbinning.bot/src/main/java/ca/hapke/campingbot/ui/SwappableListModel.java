package ca.hapke.campingbot.ui;

import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

/**
 * @author Nathan Hapke
 */
public class SwappableListModel /* extends AbstractListModel<String> */ {
//	private static final long serialVersionUID = -7040710565220821520L;
	private List<String> list = Collections.EMPTY_LIST;
	private DefaultListModel<String> model = new DefaultListModel<String>();

	public ListModel<String> getModel() {
		return model;
	}

	public void setList(List<String> list) {
		this.list = list;
		model.removeAllElements();
		model.addAll(list);
	}

}
