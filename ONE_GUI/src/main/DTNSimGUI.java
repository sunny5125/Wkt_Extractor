/* 
 * Copyright 2008 TKK/ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.xml.crypto.dsig.keyinfo.PGPData;

import core.Coord;
import main.DTNSimUI;
import playfield.PlayField;
import main.*;
import map.MapNode;
import map.Path;
import map.SimMap;

/**
 * Graphical User Interface for simulator
 *
 */
public class DTNSimGUI extends DTNSimUI {
	public MainWindow main;
	private PlayField field;
	private GUIControls guiControls;
	// private EventLogPanel eventLogPanel;
	private InfoPanel infoPanel;

	List<DataMule> dataMules = new ArrayList<DataMule>();

	private void startGUI() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					try {
						initGUI();
					} catch (AssertionError e) {
						processAssertionError(e);
					}
				}
			});
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	}

	SimMap scenMap = null;

	/**
	 * Initializes the GUI
	 */
	private void initGUI() {

		this.field = new PlayField(world);

		this.field.addMouseListener(new PlayfieldMouseHandler2());
		this.field.addMouseWheelListener(new PlayfieldMouseHandler());
		this.field.addMouseMotionListener(new PlayfieldMouseHandler_move());

		this.guiControls = new GUIControls(this, this.field);
		// this.eventLogPanel = new EventLogPanel(this);
		// this.infoPanel = new InfoPanel(this);
		this.main = new MainWindow(this.scen.getName(), world, field,
				guiControls, infoPanel, this);

		// scen.addMessageListener(eventLogPanel);
		// scen.addConnectionListener(eventLogPanel);

		if (scen.getMap() != null) {
			field.setMap(scen.getMap());
		}

		// if user closes the main window, call closeSim()
		this.main.addWindowListener(new WindowAdapter() {
			private boolean closeAgain = false;

			public void windowClosing(WindowEvent e) {
				closeSim();
				if (closeAgain) {
					// if method is called again, force closing
					System.err.println("Forced close. "
							+ "Some reports may have not been finalized.");
					System.exit(-1);
				}
				closeAgain = true;
			}
		});

		this.main.setVisible(true);

		scenMap = scen.getMap();

		readInputFile();
	}

	
	void readInputFile() {
		File file = new File("gui_input.txt");
		if (!file.exists()) {
			System.out.println("'gui_input.txt' FILE NOT FOUND");
			return;
		}

		try {
			
			List<String> lines = (ArrayList<String>) Files.readAllLines(file
					.toPath());
					
			int i = 0;
			double maxY=Double.parseDouble(lines.get(i++));
			int dbs = Integer.parseInt(lines.get(i++));
			for (int n = i + dbs; i < n; i++) {
				String line[] = lines.get(i).split("\\s+");
				Coord coord = new Coord(Double.parseDouble(line[0]),
						maxY-Double.parseDouble(line[1]));
				System.out.println("db "+coord);
				MapNode node = scenMap.getClosestMapNode(coord);
				System.out.println(node.getLocation());
				//main.addIDB(coord);
				main.addIDB(node.getLocation());
			}
			int wifis = Integer.parseInt(lines.get(i++));
			String clusterString = lines.get(i++);
			int firstwifi=dbs*2;
			for(int j=0;j<wifis;j++) 
				field.groupCenters.add(new ArrayList<DBHost>());
			String clusterarray[]=clusterString.split(",");
			for(int j=0;j<clusterarray.length;j++) {
				int groupindex=Integer.parseInt(clusterarray[j])-firstwifi;
				field.groupCenters.get(groupindex).add(field.dbhosts.get(j));
			}
			main.clusterWifiField.setText(clusterString);
			int nrofgcs = Integer.parseInt(lines.get(i++));
			String gcs = lines.get(i++);
			main.groupcentersField.setText(gcs);
			for (String gc : gcs.split(",")) {
				int adbindex = Integer.parseInt(gc) - dbs;
				Coord coord = field.dbhosts.get(adbindex).centerCoord;
				MapNode node = scenMap.getClosestMapNode(coord);
				main.addWIFI(node.getLocation());
			}
			int dms = Integer.parseInt(lines.get(i++));
			for (int n = i + dms; i < n; i++) {
				String trajectory[] = lines.get(i).split(",");
				List<DBHost> dbHosts=new ArrayList<DBHost>();
				for (String dbindex : trajectory) {
					dbHosts.add(field.dbhosts.get(Integer.parseInt(dbindex)));
				}
				main.addDM((ArrayList<DBHost>)dbHosts);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void runSim() {
		// double simTime = SimClock.getTime();
		double endTime = scen.getEndTime();

		startGUI();

		// Startup DTN2Manager
		// XXX: Would be nice if this wasn't needed..
		// DTN2Manager.setup(world);

		/*
		 * while (simTime < endTime && !simCancelled) { if
		 * (guiControls.isPaused()) { wait(10); // release CPU resources when
		 * paused } else { try { world.update(); } catch (AssertionError e) { //
		 * handles both assertion errors and SimErrors processAssertionError(e);
		 * } simTime = SimClock.getTime(); } this.update(false); }
		 * 
		 * simDone = true; done(); this.update(true); // force final GUI update
		 * 
		 * if (!simCancelled) { // NOT cancelled -> leave the GUI running
		 * JOptionPane.showMessageDialog(getParentFrame(), "Simulation done"); }
		 * else { // was cancelled -> exit immediately System.exit(0); }
		 */
	}

	/**
	 * Processes assertion errors by showing a warning dialog to the user and
	 * pausing the simulation (if it's running)
	 * 
	 * @param e
	 *            The error that was thrown
	 */
	private void processAssertionError(AssertionError e) {
		String title = e.getClass().getSimpleName() + " (simulation paused)";
		String msg = e.getMessage();
		String txt = (msg != null ? msg : "")
				+ " at simtime "
				+ "\n\ncaught at:\n"
				+ e.getStackTrace()[0].toString()
				+ "\nNote that the simulation might be in inconsistent state, "
				+ "continue only with caution.\n\n Show rest of the stack trace?";
		// rest of the update cycle that caused the exception is skipped
		// so the user is warned about the consequences

		if (guiControls != null) {
			guiControls.setPaused(true);
		}

		int selection = JOptionPane.showOptionDialog(getParentFrame(), txt,
				title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
				null, null, null);

		if (selection == 0) {
			txt = "";
			for (StackTraceElement trace : e.getStackTrace()) {
				txt += trace.toString() + "\n";
			}
			JOptionPane.showMessageDialog(getParentFrame(), txt, "stack trace",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Closes the program if simulation is done or cancels it.
	 */
	public void closeSim() {
		if (simDone) {
			System.exit(0);
		}
		this.world.cancelSim();
		this.simCancelled = true;
	}

	/**
	 * Updates the GUI
	 */
	public void update(boolean forcedUpdate) {
		double guiUpdateInterval = guiControls.getUpdateInterval();

		// update only if long enough simTime has passed (and not forced)
		// if (!forcedUpdate && guiUpdateInterval > (SimClock.getTime() -
		// this.lastUpdate)) {
		// return;
		// }

		try {
			// run update in EDT, TODO: optimize threading
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					updateView();
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		// wait a while if we don't want to run simulation at full speed
		if (guiUpdateInterval < 0) {
			wait(100 * (int) (-guiUpdateInterval));
		}

	}

	/**
	 * Updates playfield and sim time field
	 *
	 */
	private void updateView() {
		// double simTime = SimClock.getTime();
		// this.lastUpdate = simTime;
		// guiControls.setSimTime(simTime); // update time to control panel

		this.field.updateField();
	}

	/**
	 * Sets the pause of the simulation on/off
	 * 
	 * @param paused
	 *            True if pause should be set on
	 */
	public void setPaused(boolean paused) {
		this.guiControls.setPaused(paused);
	}

	/**
	 * Sets a node's graphical presentation in the center of the playfield view
	 * 
	 * @param host
	 *            The node to center
	 */
	/*
	 * public void setFocus(DTNHost host) { centerViewAt(host.getLocation());
	 * infoPanel.showInfo(host); showPath(host.getPath()); // show path on the
	 * playfield }
	 */

	/**
	 * Shows a path on the playfield
	 * 
	 * @param path
	 *            The path to show
	 */
	public void showPath(Path path) {
		field.addPath(path);
	}

	/**
	 * Returns the world coordinates that are currently in the center of the
	 * viewport
	 * 
	 * @return The coordinates
	 */
	public Coord getCenterViewCoord() {
		JScrollPane sp = main.getPlayFieldScroll();
		double midX, midY;

		midX = sp.getHorizontalScrollBar().getValue()
				+ sp.getViewport().getWidth() / 2;
		midY = sp.getVerticalScrollBar().getValue()
				+ sp.getViewport().getHeight() / 2;

		return this.field.getWorldPosition(new Coord(midX, midY));
	}

	/**
	 * Sets certain location to be in the center of the playfield view
	 * 
	 * @param loc
	 *            The location to center
	 */
	public void centerViewAt(Coord loc) {
		JScrollPane sp = main.getPlayFieldScroll();
		Coord gLoc = this.field.getGraphicsPosition(loc);
		int midX, midY;

		updateView(); // update graphics to match the values

		midX = (int) gLoc.getX() - sp.getViewport().getWidth() / 2;
		midY = (int) gLoc.getY() - sp.getViewport().getHeight() / 2;

		sp.getHorizontalScrollBar().setValue(midX);
		sp.getVerticalScrollBar().setValue(midY);
	}

	/**
	 * Returns the info panel of the GUI
	 * 
	 * @return the info panel of the GUI
	 */
	public InfoPanel getInfoPanel() {
		return this.infoPanel;
	}

	/**
	 * Returns the parent frame (window) of the gui.
	 * 
	 * @return The parent frame
	 */
	public MainWindow getParentFrame() {
		return this.main;
	}

	/**
	 * Suspend thread for ms milliseconds
	 * 
	 * @param ms
	 *            The nrof milliseconds to wait
	 */
	private void wait(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// nothing to do here
		}
	}

	/**
	 * Handler for playfield's mouse clicks.
	 */
	private class PlayfieldMouseHandler extends MouseAdapter implements
			MouseWheelListener {
		/**
		 * If mouse button is clicked, centers view at that location.
		 */
		/*
		 * public void mouseClicked(MouseEvent e) {
		 * 
		 * java.awt.Point p = e.getPoint(); Coord
		 * worldCoord=field.getWorldPosition(new Coord(p.x, p.y));
		 * centerViewAt(worldCoord); }
		 */

		public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
			guiControls.changeZoom(e.getWheelRotation());
		}
	}

	private class PlayfieldMouseHandler2 implements MouseListener {

		public void mouseClicked(MouseEvent e) {

			java.awt.Point p = e.getPoint();
			Coord worldCoord = field.getWorldPosition(new Coord(p.x, p.y));

			if (SwingUtilities.isMiddleMouseButton(e)) {
				centerViewAt(worldCoord);
			} else {
				// code for DB selection for DM
				if (field.selectDBs) {
					for (DBHost host : field.dbhosts) {
						if (host.contains(worldCoord)) {
							field.addtoSelectedDBHosts(host);
							field.repaint();
							break;
						}
					}

				} else {
					MapNode dbNode = world.scenario.getMap().getClosestMapNode(
							worldCoord);
					main.addtoRightPanel(dbNode.getLocation());
					// main.addtoRightPanel(worldCoord);
					System.out.println("Mouse clicked: " + worldCoord.getX()
							+ " " + worldCoord.getY());
				}
			}

			/*
			 * try { Process p1 = Runtime.getRuntime().exec("cmd /c dir");
			 * p1.waitFor(); BufferedReader reader = new BufferedReader( new
			 * InputStreamReader(p1.getInputStream())); String line; while
			 * ((line = reader.readLine()) != null) { System.out.println(line);
			 * } } catch (IOException e1) { // TODO Auto-generated catch block
			 * e1.printStackTrace(); } catch (InterruptedException e1) { // TODO
			 * Auto-generated catch block e1.printStackTrace(); }
			 */

			field.repaint();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}
	}

	private class PlayfieldMouseHandler_move extends MouseAdapter implements
			MouseMotionListener {
		public void mouseMoved(MouseEvent e) {
			java.awt.Point p = e.getPoint();
			Coord worldCoord = field.getWorldPosition(new Coord(p.x, p.y));
			guiControls.mouseLocation.setText(String.format("%.2f",
					worldCoord.getX())
					+ ", " + String.format("%.2f", worldCoord.getY()));

			if (field.selectDBs) {
				field.hoveredHost = null;
				for (DBHost host : field.dbhosts) {
					if (host.contains(worldCoord)) {
						field.hoveredHost = host;
						break;
					}
				}
				field.repaint();
			}

			MapNode mouseMapNode = world.scenario.getMap().getClosestMapNode(
					worldCoord);
			// System.out.println(minMapNode.getLocation() + " " + worldCoord);
			field.mouseCoord = mouseMapNode.getLocation();
			field.repaint();
		}
	}

}
