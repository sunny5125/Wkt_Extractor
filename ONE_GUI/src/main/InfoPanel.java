/* 
 * Copyright 2008 TKK/ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Vector;

import javax.swing.*;

import map.Path;
import core.*;

/**
 * Information panel that shows data of selected messages and nodes.
 */
public class InfoPanel extends JPanel implements ActionListener{
	private JComboBox msgChooser;
	private JLabel info;
	private JButton infoButton;
	private JButton routingInfoButton;
	//private Message selectedMessage;
	private DTNSimGUI gui;
	
	public InfoPanel(DTNSimGUI gui) {
		this.gui = gui;
		reset();
	}
	
	private void reset() {
		this.removeAll();
		this.repaint();
		this.info = null;
		this.infoButton = null;
		//this.selectedMessage = null;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Show information about a message
	 * @param message Message to show the information of
	 */
	/*public void showInfo(Message message) {
		reset();
		this.add(new JLabel(message.toString()));
		setMessageInfo(message);
		this.revalidate();
	}

	private void setMessageInfo(Message m) {
		int ttl = m.getTtl();
		String txt = " [" + m.getFrom() + "->" + m.getTo() + "] " +   
				"size:" + m.getSize() + ", UI:" + m.getUniqueId() +  
				", received @ " + String.format("%.2f", m.getReceiveTime());
		if (ttl != Integer.MAX_VALUE) {
			txt += " TTL: " + ttl;
		}
		
		String butTxt = "path: " + (m.getHops().size()-1) + " hops";
		
		if (this.info == null) {
			this.info = new JLabel(txt);
			this.infoButton = new JButton(butTxt);
			this.add(info);
			this.add(infoButton);
			infoButton.addActionListener(this);
		}
		else {
			this.info.setText(txt);
			this.infoButton.setText(butTxt);
		}
		
		this.selectedMessage = m;
		infoButton.setToolTipText("path:" + m.getHops());
		
		this.revalidate();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == msgChooser) {
			if (msgChooser.getSelectedIndex() == 0) { // title text selected
				return; 
			}
			Message m = (Message)msgChooser.getSelectedItem();
			setMessageInfo(m);
		}
		else if (e.getSource() == this.infoButton) {
			Path p = new Path();
			for (DTNHost h : this.selectedMessage.getHops()) {
				p.addWaypoint(h.getLocation());
			}
				
			this.gui.showPath(p);
		}
		else if (e.getSource() ==  this.routingInfoButton) {
			new RoutingInfoWindow(this.selectedHost);
		}
	}*/
	
}