/* 
 * Copyright 2008 TKK/ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package main;

import playfield.PlayField;

import javax.swing.*;
import javax.xml.crypto.dsig.keyinfo.PGPData;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.*;

import core.Coord;

/**
 * GUI's control panel
 *
 */
public class GUIControls extends JPanel implements ActionListener {
	private static final String PATH_GRAPHICS = "buttonGraphics/";
	private static final String ICON_PAUSE = "Pause16.gif";
	private static final String ICON_PLAY = "Play16.gif";
	private static final String ICON_ZOOM = "Zoom24.gif";
	private static final String ICON_STEP = "StepForward16.gif";
	private static final String ICON_FFW = "FastForward16.gif";

	private static final String TEXT_PAUSE = "pause simulation";
	private static final String TEXT_PLAY = "play simulation";
	private static final String TEXT_PLAY_UNTIL = "play simulation until sim time...";
	private static final String TEXT_STEP = "step forward one interval";
	private static final String TEXT_FFW = "enable/disable fast forward";
	private static final String TEXT_UP_CHOOSER = "GUI update:";
	private static final String TEXT_SCREEN_SHOT = "screen shot";
	private static final String TEXT_SIMTIME = "Simulation time - click to force update";
	private static final String TEXT_SEPS = "simulated seconds per second";

	// "simulated events per second" averaging time (milliseconds)
	private static final int EPS_AVG_TIME = 2000;
	private static final String SCREENSHOT_FILE_TYPE = "png";
	private static final String SCREENSHOT_FILE = "screenshot";

	private JTextField simTimeField;
	private JLabel sepsField; // simulated events per second field
	private JButton playButton;
	private JButton playUntilButton;
	private boolean paused;
	private JButton stepButton;
	private boolean step;
	private JButton ffwButton;
	private boolean isFfw;
	private int oldSpeedIndex; // what speed was selected before FFW

	private JButton screenShotButton;
	private JComboBox guiUpdateChooser;
	// JOHEV ADDED
	public JLabel mouseLocation;

	/**
	 * GUI update speeds. Negative values -> how many 1/10 seconds to wait
	 * between updates. Positive values -> show every Nth update
	 */
	public static final String[] UP_SPEEDS = { "-10", "-1", "0.1", "1", "10",
			"100", "1000", "10000", "100000" };

	/** index of intial update speed setting */
	public static final int INITIAL_SPEED_SELECTION = 3;
	/** index of FFW speed setting */
	public static final int FFW_SPEED_INDEX = 7;

	private double guiUpdateInterval;
	private JComboBox zoomChooser;

	/** Zoom levels for GUI */
	public static final String[] ZOOM_LEVELS = { "0.01", "0.2", "0.3", "0.4",
			"0.5", "0.8", "1.0", "1.3", "1.7", "2.0", "2.5", "3.0" };
	private final int INITIAL_ZOOM_SELECTION = 2; // index of initial zoom

	private PlayField pf;
	private DTNSimGUI gui;

	private long lastUpdate;
	private double lastSimTime;
	private double playUntilTime;

	public GUIControls(DTNSimGUI gui, PlayField pf) {
		this.pf = pf;
		this.gui = gui;
		this.lastUpdate = System.currentTimeMillis();
		this.lastSimTime = 0;
		this.paused = true;
		this.isFfw = false;
		this.playUntilTime = Double.MAX_VALUE;
		initPanel();
	}

	/**
	 * Creates panel's components and initializes them
	 */
	private void initPanel() {
		this.setLayout(new FlowLayout());
		this.simTimeField = new JTextField("sim time");
		this.simTimeField.setColumns(5);
		this.simTimeField.setEditable(false);
		this.simTimeField.setToolTipText(TEXT_SIMTIME);
		this.simTimeField.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// setSimTime(SimClock.getTime());
			}
		});

		this.sepsField = new JLabel("0.00");
		this.sepsField.setToolTipText(TEXT_SEPS);

		this.screenShotButton = new JButton(TEXT_SCREEN_SHOT);
		this.guiUpdateChooser = new JComboBox(UP_SPEEDS);
		this.zoomChooser = new JComboBox(ZOOM_LEVELS);

		JButton genSettingsButton = new JButton("Generate Settings");
		genSettingsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateSettings(true);
			}
		});
		this.add(genSettingsButton);

		JButton startonesimButton = new JButton("Simulate");
		startonesimButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String batchlocation = System.getProperty("user.dir")
							+ "/Eone_1.4.1";
					System.out.println(batchlocation);
					Process process = Runtime.getRuntime().exec(
							"cmd.exe /c start one.bat", null,
							new File(batchlocation));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		this.add(startonesimButton);

		this.add(simTimeField);
		this.add(sepsField);
		playButton = addButton(paused ? ICON_PLAY : ICON_PAUSE,
				paused ? TEXT_PLAY : TEXT_PAUSE);
		stepButton = addButton(ICON_STEP, TEXT_STEP);
		ffwButton = addButton(ICON_FFW, TEXT_FFW);
		playUntilButton = addButton(ICON_PLAY, TEXT_PLAY_UNTIL);
		playUntilButton.setText("...");

		this.add(new JLabel(TEXT_UP_CHOOSER));
		this.add(this.guiUpdateChooser);
		this.guiUpdateChooser.setSelectedIndex(INITIAL_SPEED_SELECTION);
		this.updateUpdateInterval();

		this.add(new JLabel(createImageIcon(ICON_ZOOM)));
		this.zoomChooser.setSelectedIndex(this.INITIAL_ZOOM_SELECTION);
		this.updateZoomScale(false);

		this.add(this.zoomChooser);
		this.add(this.screenShotButton);

		// JOHEV ADDED
		this.mouseLocation = new JLabel("(x,y)");
		this.add(mouseLocation);
		// ===============

		guiUpdateChooser.addActionListener(this);
		zoomChooser.addActionListener(this);
		this.screenShotButton.addActionListener(this);
	}

	void generateSettings(boolean fileread) {
		int db = pf.dbhosts.size();
		int adb = db;
		int wifi = pf.wifihosts.size();
		int cd = gui.dataMules.size();
		int noofhosts = 4 + db;
		int firstADB = db;
		int firstWIFI = firstADB + adb;
		int mcs = firstWIFI;
		int firstCD = firstWIFI + wifi;
		int firstBT = firstCD + cd;
		String clusterWifi = gui.main.clusterWifiField.getText();
		String groupcenters = gui.main.groupcentersField.getText();

		// PrintWriter writer;
		try {
			File file = new File("sample_settings.txt");
			List<String> lines = (ArrayList<String>) Files.readAllLines(file
					.toPath());
			// lines.remove(42);
			lines.set(42, "Scenario.nrofHostGroups = " + noofhosts);
			lines.set(44, "ExternalMovement.file = new_ext_movement.txt");

			int nodesPerCluster = 3;
			int totalnodes = 0;
			for (DBHost host : pf.dbhosts) {
				totalnodes += host.nodecount;
			}

			if (fileread) {
				String hosts = firstBT + "," + (firstBT + totalnodes);
				String cw[] = clusterWifi.split(",");
				int value = 0;
				for (int i = 0; i < cw.length; i++) {
					if (Integer.parseInt(cw[i]) == mcs)
						value = firstADB + i;
				}
				String tohosts = value + "," + (value + 1);

				lines.set(150, "Events1.hosts = " + hosts);
				lines.set(151, "Events1.tohosts = " + tohosts);

				lines.set(159, "Events2.hosts = " + tohosts);
				lines.set(160, "Events2.tohosts = " + hosts);
			}

			lines.set(63, "Group.mcs_address = " + mcs);
			lines.set(64, "Group.firstADB = " + firstADB);
			lines.set(65, "Group.firstWIFI = " + firstWIFI);
			lines.set(66, "Group.firstCD = " + firstCD);
			lines.set(67, "Group.firstBT = " + firstBT);
			lines.set(69, "Group.clusterWifi = " + clusterWifi);
			lines.set(71, "Group.DMS = " + cd);

			lines.set(91, "Group.group_centers = " + groupcenters);
			lines.set(99, "Group1.nrofHosts = " + db);
			lines.set(106, "Group2.nrofHosts = " + adb);
			lines.set(117, "Group3.nrofHosts = " + wifi);
			lines.set(126, "Group4.nrofHosts = " + cd);

			int offset = 0;
			int clusterstart = 134;
			for (int i = 0, n = pf.dbhosts.size(); i < n; i++) {
				DBHost host = pf.dbhosts.get(i);
				lines.add(clusterstart + offset, "Group" + (5 + i)
						+ ".clusterCenter=" + (int) host.centerCoord.getX()
						+ "," + (int) host.centerCoord.getY());
				lines.add(clusterstart + 1 + offset, "Group" + (5 + i)
						+ ".dblocation=" + (int) host.centerCoord.getX() + ","
						+ (int) host.centerCoord.getY());
				ArrayList<String> strs = new ArrayList<String>();
				strs.add("Group" + (5 + i) + ".groupID = dtn"+(i+1)+"_");
				strs.add("Group" + (5 + i) + ".nrofHosts = " + host.nodecount);
				strs.add("Group" + (5 + i)
						+ ".movementModel = VolunteerMovement");
				strs.add("Group" + (5 + i) + ".clusterStops = 3,3");
				strs.add("Group" + (5 + i) + ".nrofInterfaces = 1");
				strs.add("Group" + (5 + i) + ".interface1 = btInterface");
				strs.add("Group" + (5 + i) + ".activeTimes = 0,133200");
				strs.add("");
				strs.add("");
				lines.addAll(clusterstart + 2 + offset, strs);
				offset += 2 + strs.size();
			}

			for (int i = 0, n = gui.dataMules.size(); i < n; i++) {
				ArrayList<String> dbsequence = new ArrayList<String>();
				ArrayList<String> waypoints = new ArrayList<String>();
				for (DBHost muleHost : gui.dataMules.get(i).hosts) {
					dbsequence.add(String.valueOf(Integer
							.parseInt(muleHost.name.split(" ")[1]) - 1));
					waypoints.add(String.valueOf((int) muleHost.centerCoord
							.getX()));
					waypoints.add(String.valueOf((int) muleHost.centerCoord
							.getY()));
				}
				lines.add(
						73 + i * 2,
						"Group.CD" + (i + 1) + " = "
								+ String.join(",", dbsequence));
				lines.add(73 + i * 2 + 1, "Group.waypoints" + (i + 1) + " = "
						+ String.join(",", waypoints));
			}
			File newFile = new File("Eone_1.4.1/new_settings.txt");
			Files.write(newFile.toPath(), lines);
			System.out.println("\nnew_settings.txt SETTINGS FILE WRITEN");

			file = new File("ext_movement.txt");
			int index = 0;
			lines = (ArrayList<String>) Files.readAllLines(file.toPath());
			List<String> adbsList = new ArrayList<String>();
			for (int i = 0; i < db; i++) {
				DBHost dbHost = pf.dbhosts.get(i);
				String line = "0 " + index + " "
						+ (int) dbHost.centerCoord.getX() + " "
						+ (int) dbHost.centerCoord.getY();
				String line2 = "0 " + (db+i+1) + " "
						+ (int) dbHost.centerCoord.getX() + " "
						+ (int) dbHost.centerCoord.getY();
				adbsList.add(line2);
				lines.add(line);
				index+=1;
			}
			lines.addAll(adbsList);
			for (int i = 0; i < wifi; i++) {
				WifiHost wifiHost = pf.wifihosts.get(i);
				String line = "0 " + index + " "
						+ (int) wifiHost.coord.getX() + " "
						+ (int) wifiHost.coord.getY();
				lines.add(line);
				index+=1;
			}
			for (int i = 0; i < cd; i++) {
				DataMule mule = gui.dataMules.get(i);
				DBHost firstHost = mule.hosts.get(0);
				String line = "0 " + index + " "
						+ (int) firstHost.centerCoord.getX() + " "
						+ (int) firstHost.centerCoord.getY();
				lines.add(line);
				index+=1;
			}
			newFile = new File("Eone_1.4.1/new_ext_movement.txt");
			Files.write(newFile.toPath(), lines);

			System.out
					.println("\nnew_ext_movement.txt EXTERNAL MOVEMENT FILE WRITEN");

			/*
			 * writer = new PrintWriter(new BufferedWriter(new
			 * FileWriter("sample_settings.txt")));
			 * writer.println("The first line");
			 * writer.println("The second line"); writer.close();
			 */
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = getClass().getResource(PATH_GRAPHICS + path);
		return new ImageIcon(imgURL);
	}

	private JButton addButton(String iconPath, String tooltip) {
		JButton button = new JButton(createImageIcon(iconPath));
		button.setToolTipText(tooltip);
		button.addActionListener(this);
		this.add(button);
		return button;
	}

	/**
	 * Sets the simulation time that control panel shows
	 * 
	 * @param time
	 *            The time to show
	 */
	public void setSimTime(double time) {
		long timeSinceUpdate = System.currentTimeMillis() - this.lastUpdate;

		if (timeSinceUpdate > EPS_AVG_TIME) {
			double val = ((time - this.lastSimTime) * 1000) / timeSinceUpdate;
			String sepsValue = String.format("%.2f 1/s", val);

			this.sepsField.setText(sepsValue);
			this.lastSimTime = time;
			this.lastUpdate = System.currentTimeMillis();
		}

		this.simTimeField.setText(String.format("%.1f", time));
	}

	/**
	 * Sets simulation to pause or play.
	 * 
	 * @param paused
	 *            If true, simulation is put to pause
	 */
	public void setPaused(boolean paused) {
		if (!paused) {
			this.playButton.setIcon(createImageIcon(ICON_PAUSE));
			this.playButton.setToolTipText(TEXT_PAUSE);
			this.paused = false;
			/*
			 * if (SimClock.getTime() >= this.playUntilTime) { // playUntilTime
			 * passed -> disable it this.playUntilTime = Double.MAX_VALUE; }
			 */
		} else {
			this.playButton.setIcon(createImageIcon(ICON_PLAY));
			this.playButton.setToolTipText(TEXT_PLAY);
			this.paused = true;
			// this.setSimTime(SimClock.getTime());
			this.pf.updateField();
		}
	}

	private void switchFfw() {
		if (isFfw) {
			this.isFfw = false; // set to normal play
			this.ffwButton.setIcon(createImageIcon(ICON_FFW));
			this.guiUpdateChooser.setSelectedIndex(oldSpeedIndex);
			this.ffwButton.setSelected(false);
		} else {
			this.oldSpeedIndex = this.guiUpdateChooser.getSelectedIndex();
			this.guiUpdateChooser.setSelectedIndex(FFW_SPEED_INDEX);
			this.isFfw = true; // set to FFW
			this.ffwButton.setIcon(createImageIcon(ICON_PLAY));
		}
	}

	/**
	 * Has user requested the simulation to be paused
	 * 
	 * @return True if pause is requested
	 */
	public boolean isPaused() {
		if (step) { // if we want to step, return false once and reset stepping
			step = false;
			return false;
		}
		/*
		 * if (SimClock.getTime() >= this.playUntilTime) { this.setPaused(true);
		 * }
		 */
		return this.paused;
	}

	/**
	 * Is fast forward turned on
	 * 
	 * @return True if FFW is on, false if not
	 */
	public boolean isFfw() {
		return this.isFfw;
	}

	/**
	 * Returns the selected update interval of GUI
	 * 
	 * @return The update interval (seconds)
	 */
	public double getUpdateInterval() {
		return this.guiUpdateInterval;
	}

	/**
	 * Changes the zoom level
	 * 
	 * @param delta
	 *            How much to change the current level (can be negative or
	 *            positive)
	 */
	public void changeZoom(int delta) {
		int newIndex = zoomChooser.getSelectedIndex() + delta;
		if (newIndex < 1) {
			newIndex = 1; // max zoom level is not accessible trough this
		}
		if (newIndex >= ZOOM_LEVELS.length) {
			newIndex = ZOOM_LEVELS.length - 1;
		}

		this.zoomChooser.setSelectedIndex(newIndex);
		this.updateZoomScale(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.playButton) {
			setPaused(!this.paused); // switch pause/play
		} else if (e.getSource() == this.stepButton) {
			setPaused(true);
			this.step = true;
		} else if (e.getSource() == this.ffwButton) {
			switchFfw();
		} else if (e.getSource() == this.playUntilButton) {
			setPlayUntil();
		} else if (e.getSource() == this.guiUpdateChooser) {
			updateUpdateInterval();
		} else if (e.getSource() == this.zoomChooser) {
			updateZoomScale(true);
		} else if (e.getSource() == this.screenShotButton) {
			takeScreenShot();
		}
	}

	private void setPlayUntil() {
		setPaused(true);
		String value = JOptionPane.showInputDialog(TEXT_PLAY_UNTIL);
		if (value == null) {
			return;
		}
		try {
			this.playUntilTime = Double.parseDouble(value);
			setPaused(false);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(gui.getParentFrame(),
					"Invalid number '" + value + "'", "error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateUpdateInterval() {
		String selString = (String) this.guiUpdateChooser.getSelectedItem();
		this.guiUpdateInterval = Double.parseDouble(selString);
	}

	/**
	 * Updates zoom scale to the one selected by zoom chooser
	 * 
	 * @param centerView
	 *            If true, the center of the viewport should remain the same
	 */
	private void updateZoomScale(boolean centerView) {
		String selString = this.zoomChooser.getSelectedItem().toString();
		double scale = Double.parseDouble(selString);

		if (centerView) {
			// Coord center = gui.getCenterViewCoord();
			this.pf.setScale(scale);
			// gui.centerViewAt(center);
		} else {
			this.pf.setScale(scale);
		}
	}

	private void takeScreenShot() {
		try {
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFile(new File(SCREENSHOT_FILE + "."
					+ SCREENSHOT_FILE_TYPE));
			int retVal = fc.showSaveDialog(this);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				BufferedImage i = new BufferedImage(this.pf.getWidth(),
						this.pf.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D g2 = i.createGraphics();

				this.pf.paint(g2); // paint playfield to buffered image
				ImageIO.write(i, SCREENSHOT_FILE_TYPE, file);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(gui.getParentFrame(),
					"screenshot failed (problems with output file?)",
					"Exception", JOptionPane.ERROR_MESSAGE);
		}
	}

}
