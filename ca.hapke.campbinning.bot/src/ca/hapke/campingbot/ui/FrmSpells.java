package ca.hapke.campingbot.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.hapke.campingbot.category.CategoriedItems;
import ca.hapke.campingbot.commands.spell.SpellCommand;
import ca.hapke.campingbot.commands.spell.SpellPacks;
import ca.hapke.util.ui.SwappableListModel;

/**
 * @author Nathan Hapke
 */
public class FrmSpells extends JFrame {

	private JPanel contentPane;
	private JList<String> lstPacks;
	private JTextField txtPack;
	private JTextField txtAlias;
	private JList<String> lstItems;
	private JList<String> lstAliases;
	private JList<String> lstExclamations;
	private JTextField txtExclamation;
	private JTextField txtItem;
	private SpellPacks packs;
	private SpellCommand spellCommand;
	private SwappableListModel aliasModel = new SwappableListModel();
	private SwappableListModel itemModel = new SwappableListModel();
	private SwappableListModel exclamationModel = new SwappableListModel();

	/**
	 * Create the frame.
	 */
	public FrmSpells(SpellCommand spellCmd) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.spellCommand = spellCmd;
		this.packs = spellCommand.getPacks();

		setTitle("Spell Pack Editor");
		setBounds(100, 100, 786, 573);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane sclPacks = new JScrollPane();
		sclPacks.setBounds(12, 40, 232, 189);
		contentPane.add(sclPacks);

		lstPacks = new JList<String>(packs.getPacksModel());
		lstPacks.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateDatas();
			}
		});
		sclPacks.setViewportView(lstPacks);
		updatePackList();

		JScrollPane sclAliases = new JScrollPane();
		sclAliases.setBounds(13, 300, 232, 189);
		contentPane.add(sclAliases);

		lstAliases = new JList<String>();
		lstAliases.setModel(aliasModel.getModel());
		sclAliases.setViewportView(lstAliases);

		JLabel lblAliases = new JLabel("Aliases");
		lblAliases.setVerticalAlignment(SwingConstants.BOTTOM);
		lblAliases.setBounds(14, 271, 168, 26);
		contentPane.add(lblAliases);

		JLabel lblPacks = new JLabel("Packs");
		lblPacks.setVerticalAlignment(SwingConstants.BOTTOM);
		lblPacks.setBounds(14, 11, 236, 26);
		contentPane.add(lblPacks);

		txtPack = new JTextField();
		txtPack.setBounds(12, 234, 155, 26);
		contentPane.add(txtPack);
		txtPack.setColumns(10);

		txtAlias = new JTextField();
		txtAlias.setColumns(10);
		txtAlias.setBounds(13, 494, 155, 26);
		contentPane.add(txtAlias);

		JButton btnAddPack = new JButton("Add");
		btnAddPack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = txtPack.getText();
//				if (name.length() > 0) {
//					packs.get(name, true);
//				}
				spellCommand.addPack(name);
				updatePackList();
				txtPack.setText("");
			}
		});
		btnAddPack.setBounds(173, 234, 71, 26);
		contentPane.add(btnAddPack);

		JButton btnAddAlias = new JButton("Add");
		btnAddAlias.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedPack = lstPacks.getSelectedValue();
				String alias = txtAlias.getText();
				packs.addAlias(selectedPack, alias);
				txtAlias.setText("");
				updateDatas();
			}
		});
		btnAddAlias.setBounds(173, 494, 71, 26);
		contentPane.add(btnAddAlias);

		JScrollPane sclItems = new JScrollPane();
		sclItems.setBounds(286, 40, 476, 189);
		contentPane.add(sclItems);

		lstItems = new JList<String>(itemModel.getModel());
		sclItems.setViewportView(lstItems);

		JLabel lblItems = new JLabel("Items");
		lblItems.setVerticalAlignment(SwingConstants.BOTTOM);
		lblItems.setBounds(288, 11, 236, 26);
		contentPane.add(lblItems);

		JScrollPane sclExclamations = new JScrollPane();
		sclExclamations.setBounds(286, 300, 476, 189);
		contentPane.add(sclExclamations);

		lstExclamations = new JList<String>(exclamationModel.getModel());
		sclExclamations.setViewportView(lstExclamations);

		JLabel lblExclamations = new JLabel("Exclamations");
		lblExclamations.setVerticalAlignment(SwingConstants.BOTTOM);
		lblExclamations.setBounds(288, 271, 236, 26);
		contentPane.add(lblExclamations);

		txtExclamation = new JTextField();
		txtExclamation.setColumns(10);
		txtExclamation.setBounds(287, 494, 400, 26);
		contentPane.add(txtExclamation);

		JButton btnAddExclamation = new JButton("Add");
		btnAddExclamation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedPack = lstPacks.getSelectedValue();
				String value = txtExclamation.getText();
				spellCommand.addSpellExclamation(selectedPack, value);
				updateDatas();
				txtExclamation.setText("");
			}
		});
		btnAddExclamation.setBounds(691, 494, 71, 26);
		contentPane.add(btnAddExclamation);

		txtItem = new JTextField();
		txtItem.setColumns(10);
		txtItem.setBounds(286, 234, 400, 26);
		contentPane.add(txtItem);

		JButton btnAddItem = new JButton("Add");
		btnAddItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedPack = lstPacks.getSelectedValue();
				String value = txtItem.getText();
				spellCommand.addSpellItem(selectedPack, value);
				updateDatas();
				txtItem.setText("");
			}
		});
		btnAddItem.setBounds(691, 234, 71, 26);
		contentPane.add(btnAddItem);
	}

	private void updateDatas() {
		String selectedPack = lstPacks.getSelectedValue();

		List<String> aliases = packs.getAliases(selectedPack);
		aliasModel.setList(aliases);

		CategoriedItems<String> pack = packs.get(selectedPack, false);
		List<String> excl = pack.getListView(SpellCommand.EXCLAMATION_CATEGORY);
		exclamationModel.setList(excl);

		List<String> items = pack.getListView(SpellCommand.ITEM_CATEGORY);
		itemModel.setList(items);
	}

	private void updatePackList() {
		lstPacks.updateUI();
	}
}
