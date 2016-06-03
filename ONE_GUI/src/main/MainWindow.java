/* 
 * Copyright 2008 TKK/ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package main;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.xml.crypto.dsig.keyinfo.PGPData;

import core.Coord;
import main.World;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import playfield.PlayField;

/**
 * Main window for the program. Takes care of layouting the main components in
 * the window.
 */
public class MainWindow extends JFrame {
	private static final String WINDOW_TITLE = "ONE";
	private static final int WIN_XSIZE = 900;
	private static final int WIN_YSIZE = 700;
	// log panel's initial weight in the split panel
	private static final double SPLIT_PANE_LOG_WEIGHT = 0.2;

	private JPanel right_panel;

	private JScrollPane playFieldScroll;
	private JPanel left_panel;

	private PlayField pf;

	private JPanel db_panel;
	private JPanel wifi_panel;
	JPanel dm_panel;

	private boolean isDBSelected = true;
	
	public JTextField clusterWifiField;
	public JTextField groupcentersField;

	DTNSimGUI gui;

	public MainWindow(String scenName, World world, PlayField field,
			GUIControls guiControls, InfoPanel infoPanel, DTNSimGUI gui) {
		super(WINDOW_TITLE + " - " + scenName);
		JFrame.setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		this.pf = field;
		this.gui = gui;

		JPanel containerPane = new JPanel();
		// leftPane.setLayout(new BoxLayout(leftPane,BoxLayout.Y_AXIS));
		containerPane.setLayout(new BorderLayout());
		JScrollPane hostListScroll;
		JSplitPane fieldLogSplit;
		JSplitPane logControlSplit;
		JSplitPane mainSplit;

		setLayout(new BorderLayout());
		setJMenuBar(new SimMenuBar(field));

		playFieldScroll = new JScrollPane(field);

		/*
		 * hostListScroll = new JScrollPane(new
		 * NodeChooser(world.getHosts(),gui));
		 * hostListScroll.setHorizontalScrollBarPolicy(
		 * JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		 * 
		 * logControlSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new
		 * JScrollPane(elp.getControls()),new JScrollPane(elp));
		 * logControlSplit.setResizeWeight(0.1);
		 * logControlSplit.setOneTouchExpandable(true);
		 * 
		 * fieldLogSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftPane,
		 * logControlSplit);
		 * fieldLogSplit.setResizeWeight(1-SPLIT_PANE_LOG_WEIGHT);
		 * fieldLogSplit.setOneTouchExpandable(true);
		 */

		setPreferredSize(new Dimension(WIN_XSIZE, WIN_YSIZE));

		containerPane.add(guiControls, BorderLayout.PAGE_START);
		containerPane.add(playFieldScroll, BorderLayout.CENTER);
		left_panel = new JPanel();
		left_panel.setLayout(new BoxLayout(left_panel, BoxLayout.Y_AXIS));
		containerPane.add(left_panel, BorderLayout.WEST);

		right_panel = new JPanel();
		right_panel.setLayout(new BoxLayout(right_panel, BoxLayout.Y_AXIS));
		JRadioButton db_RadioButton = new JRadioButton("DB");
		JRadioButton wifi_RadioButton = new JRadioButton("WIFI");
		ButtonGroup bG = new ButtonGroup();
		bG.add(db_RadioButton);
		bG.add(wifi_RadioButton);
		db_RadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				isDBSelected = db_RadioButton.isSelected();
			}
		});
		wifi_RadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isDBSelected = db_RadioButton.isSelected();
			}
		});
		db_RadioButton.setSelected(true);
		right_panel.add(db_RadioButton);
		right_panel.add(wifi_RadioButton);

		db_panel = new JPanel();
		db_panel.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "ADBs",
				TitledBorder.LEADING, TitledBorder.TOP, null,
				new Color(0, 0, 0)));
		db_panel.setLayout(new BoxLayout(db_panel, BoxLayout.Y_AXIS));
		right_panel.add(db_panel);
		wifi_panel = new JPanel();
		wifi_panel.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "WiFis",
				TitledBorder.LEADING, TitledBorder.TOP, null,
				new Color(0, 0, 0)));
		wifi_panel.setLayout(new BoxLayout(wifi_panel, BoxLayout.Y_AXIS));
		right_panel.add(wifi_panel);

		dm_panel = new JPanel();
		dm_panel.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "DMs", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(0, 0, 0)));
		dm_panel.setLayout(new BoxLayout(dm_panel, BoxLayout.Y_AXIS));

		JToggleButton addDM = new JToggleButton("ADD Data Mule");
		addDM.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (addDM.isSelected()) {
					pf.startDBselectionforDM();
					addDM.setText("FINISH");
				} else {
					pf.finishDBSelectionforDM();
					if (pf.selectedDbHosts.size() > 0) {
						addDM((ArrayList<DBHost>)pf.selectedDbHosts);
					}
					addDM.setText("ADD Data Mule");
				}
			}
		});

		dm_panel.add(addDM);
		right_panel.add(dm_panel);
		
		clusterWifiField=new JTextField("cluster wifi");
		right_panel.add(clusterWifiField);
		groupcentersField=new JTextField("group centers");
		right_panel.add(groupcentersField);
		

		containerPane.add(right_panel, BorderLayout.EAST);
		// leftPane.add(infoPanel);

		/*
		 * mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		 * fieldLogSplit, hostListScroll);
		 * mainSplit.setOneTouchExpandable(true);
		 * mainSplit.setResizeWeight(0.60);
		 */

		// this.getContentPane().add(mainSplit);
		this.getContentPane().add(containerPane);
		// this.getContentPane().add(playFieldScroll);

		pack();

		// guiControls.setSize(guiControls.getSize().width, 40);
	}
	
	void addDM(ArrayList<DBHost> dbhosts) {
		DataMule mule=new DataMule(dbhosts);
		gui.dataMules.add(mule);
		JButton dm_Button=new JButton("DM "+gui.dataMules.size());
		dm_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearLeftPanel();
				for (DBHost muleHost : mule.hosts) {
					left_panel.add(new JLabel(muleHost.name));
				}
				refreshLeftPanel();
			}
		});
		dm_panel.add(dm_Button);
		dm_panel.validate();
		dm_panel.repaint();
	}

	void clearLeftPanel() {
		left_panel.removeAll();
		left_panel.validate();
		left_panel.revalidate();
		left_panel.repaint();
	}
	void refreshLeftPanel() {
		left_panel.validate();
		left_panel.revalidate();
		left_panel.repaint();
	}
	void refreshRightPanel() {
		right_panel.validate();
		right_panel.revalidate();
		right_panel.repaint();
	}
	void addtoRightPanel(Coord coord) {
		if (isDBSelected)
			addIDB(coord);
		else
			addWIFI(coord);
	}

	void addIDB(Coord dbCoord) {
		DBHost host = new DBHost("DB " + (this.pf.dbhosts.size() + 1), dbCoord,
				200);
		this.pf.dbhosts.add(host);

		// String buttonString = "IDB @ " + dbCoord.toString();

		JButton button = new JButton(host.name);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clearLeftPanel();
				JButton removeButton = new JButton("remove " + host.name);
				left_panel.add(removeButton);
				removeButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						pf.dbhosts.remove(host);
						pf.repaint();
						left_panel.remove((JButton) e.getSource());
						db_panel.remove(button);
						refreshLeftPanel();
						refreshRightPanel();
					}
				});
				refreshLeftPanel();
			}
		});

		// idb_panel.add(button);
		db_panel.add(button);
		refreshRightPanel();
		
		pf.repaint();
		int nodecount = Integer.parseInt(JOptionPane.showInputDialog(this  ,"No. of Nodes in Group?"));
		host.setNodeCount(nodecount);
	}

	void addWIFI(Coord wifiCoord) {

		WifiHost host = new WifiHost("WIFI " + (this.pf.wifihosts.size() + 1),
				wifiCoord, 10, 100);

		// String buttonString = "WIFI @ " + wifiCoord.toString();

		this.pf.wifihosts.add(host);
		JButton button = new JButton(host.name);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				left_panel.removeAll();
				JButton removeButton = new JButton("remove " + host.name);
				left_panel.add(removeButton);
				removeButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// pf.dbCoords.remove(wifiCoord);
						pf.wifihosts.remove(host);
						pf.repaint();
						left_panel.remove((JButton) e.getSource());
						wifi_panel.remove(button);
						left_panel.revalidate();
						left_panel.repaint();
						right_panel.revalidate();
						right_panel.repaint();

					}
				});
				left_panel.validate();
				left_panel.revalidate();
				left_panel.repaint();

			}
		});

		// idb_panel.add(button);
		wifi_panel.add(button);
		right_panel.validate();
		right_panel.revalidate();
		right_panel.repaint();
	}

	/**
	 * Returns a reference of the play field scroll panel
	 * 
	 * @return a reference of the play field scroll panel
	 */
	public JScrollPane getPlayFieldScroll() {
		return this.playFieldScroll;
	}

}
