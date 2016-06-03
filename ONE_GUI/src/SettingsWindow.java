import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

import java.awt.Panel;

import javax.swing.JPanel;

import java.awt.FlowLayout;
import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.SwingConstants;

import java.awt.GridLayout;

import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;


public class SettingsWindow {

	private JFrame frame;
	private JTextField txtNitbcroytxt;
	private JTextField txtDefaultScenario;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField txtSimplebroadcasrinterface;
	private JTextField txtk;
	private JTextField textField_4;
	private JTextField textField;
	private JTextField txtk_1;
	private JTextField textField_5;
	private JTextField textField_1;
	private JTextField txtk_2;
	private JTextField textField_7;
	private JTextField textField_6;
	private JTextField txtk_3;
	private JTextField textField_9;
	private JTextField textField_8;
	private JTextField txtk_4;
	private JTextField textField_11;
	private JTextField textField_10;
	private JTextField txtk_5;
	private JTextField textField_13;
	private JTextField textField_12;
	private JTextField txtk_6;
	private JTextField textField_15;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SettingsWindow window = new SettingsWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SettingsWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 958, 609);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("Next");
		btnNewButton.setBounds(435, 536, 89, 23);
		frame.getContentPane().add(btnNewButton);
		
		JPanel panel_8 = new JPanel();
		panel_8.setBounds(0, 0, 942, 525);
		frame.getContentPane().add(panel_8);
		panel_8.setLayout(null);
		
		JLabel lblTextFileName = new JLabel("Text File Name:");
		lblTextFileName.setBounds(10, 11, 88, 14);
		panel_8.add(lblTextFileName);
		
		txtNitbcroytxt = new JTextField();
		txtNitbcroytxt.setBounds(298, 8, 248, 20);
		panel_8.add(txtNitbcroytxt);
		txtNitbcroytxt.setText("NIT_BCROY.txt");
		txtNitbcroytxt.setColumns(10);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 36, 456, 139);
		panel_8.add(panel);
		panel.setToolTipText("");
		panel.setLayout(new GridLayout(4, 2, 0, 0));
		panel.setBorder(BorderFactory.createTitledBorder("Scenario"));
		
		JLabel lblScenarioName = new JLabel("Name:");
		panel.add(lblScenarioName);
		
		txtDefaultScenario = new JTextField();
		panel.add(txtDefaultScenario);
		txtDefaultScenario.setText("default_scenario");
		txtDefaultScenario.setColumns(10);
		
		JLabel lblSimulateConnections = new JLabel("Simulate Connections:");
		panel.add(lblSimulateConnections);
		
		JCheckBox checkBox_1 = new JCheckBox("");
		checkBox_1.setSelected(true);
		panel.add(checkBox_1);
		
		JLabel lblUpdateInterval = new JLabel("Update Interval:");
		panel.add(lblUpdateInterval);
		
		textField_2 = new JTextField();
		textField_2.setText("0.1");
		textField_2.setColumns(10);
		panel.add(textField_2);
		
		JLabel label_2 = new JLabel("Scenario End Time:");
		panel.add(label_2);
		
		textField_3 = new JTextField();
		textField_3.setText("86000");
		textField_3.setColumns(10);
		panel.add(textField_3);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(10, 186, 456, 102);
		panel_8.add(panel_1);
		panel_1.setToolTipText("");
		panel_1.setBorder(new TitledBorder(null, "Bluetooth Interface", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setLayout(new GridLayout(3, 2, 0, 0));
		
		JLabel lblType = new JLabel("Type:");
		panel_1.add(lblType);
		
		txtSimplebroadcasrinterface = new JTextField();
		txtSimplebroadcasrinterface.setEditable(false);
		txtSimplebroadcasrinterface.setText("SimpleBroadcastInterface");
		txtSimplebroadcasrinterface.setColumns(10);
		panel_1.add(txtSimplebroadcasrinterface);
		
		JLabel lblTransmitSpeed = new JLabel("Transmit Speed:");
		panel_1.add(lblTransmitSpeed);
		
		txtk = new JTextField();
		txtk.setText("250K");
		txtk.setColumns(10);
		panel_1.add(txtk);
		
		JLabel lblTransmitRange = new JLabel("Transmit Range:");
		panel_1.add(lblTransmitRange);
		
		textField_4 = new JTextField();
		textField_4.setText("10");
		textField_4.setColumns(10);
		panel_1.add(textField_4);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(10, 299, 456, 102);
		panel_8.add(panel_2);
		panel_2.setToolTipText("");
		panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "WIFI Interface", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_2.setLayout(new GridLayout(3, 2, 0, 0));
		
		JLabel label = new JLabel("Type:");
		panel_2.add(label);
		
		textField = new JTextField();
		textField.setText("SimpleBroadcastInterface");
		textField.setEditable(false);
		textField.setColumns(10);
		panel_2.add(textField);
		
		JLabel label_1 = new JLabel("Transmit Speed:");
		panel_2.add(label_1);
		
		txtk_1 = new JTextField();
		txtk_1.setText("1024K");
		txtk_1.setColumns(10);
		panel_2.add(txtk_1);
		
		JLabel label_3 = new JLabel("Transmit Range:");
		panel_2.add(label_3);
		
		textField_5 = new JTextField();
		textField_5.setText("5000");
		textField_5.setColumns(10);
		panel_2.add(textField_5);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(10, 412, 456, 102);
		panel_8.add(panel_3);
		panel_3.setToolTipText("");
		panel_3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "DB Interface", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_3.setLayout(new GridLayout(3, 2, 0, 0));
		
		JLabel label_4 = new JLabel("Type:");
		panel_3.add(label_4);
		
		textField_1 = new JTextField();
		textField_1.setText("SimpleBroadcastInterface");
		textField_1.setEditable(false);
		textField_1.setColumns(10);
		panel_3.add(textField_1);
		
		JLabel label_5 = new JLabel("Transmit Speed:");
		panel_3.add(label_5);
		
		txtk_2 = new JTextField();
		txtk_2.setText("256K");
		txtk_2.setColumns(10);
		panel_3.add(txtk_2);
		
		JLabel label_6 = new JLabel("Transmit Range:");
		panel_3.add(label_6);
		
		textField_7 = new JTextField();
		textField_7.setText("300");
		textField_7.setColumns(10);
		panel_3.add(textField_7);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBounds(477, 73, 456, 102);
		panel_8.add(panel_5);
		panel_5.setToolTipText("");
		panel_5.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "ADB Interface", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_5.setLayout(new GridLayout(3, 2, 0, 0));
		
		JLabel label_10 = new JLabel("Type:");
		panel_5.add(label_10);
		
		textField_8 = new JTextField();
		textField_8.setText("SimpleBroadcastInterface");
		textField_8.setEditable(false);
		textField_8.setColumns(10);
		panel_5.add(textField_8);
		
		JLabel label_11 = new JLabel("Transmit Speed:");
		panel_5.add(label_11);
		
		txtk_4 = new JTextField();
		txtk_4.setText("2560K");
		txtk_4.setColumns(10);
		panel_5.add(txtk_4);
		
		JLabel label_12 = new JLabel("Transmit Range:");
		panel_5.add(label_12);
		
		textField_11 = new JTextField();
		textField_11.setText("20");
		textField_11.setColumns(10);
		panel_5.add(textField_11);
		
		JPanel panel_6 = new JPanel();
		panel_6.setBounds(476, 186, 456, 102);
		panel_8.add(panel_6);
		panel_6.setToolTipText("");
		panel_6.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "SAT Interface", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_6.setLayout(new GridLayout(3, 2, 0, 0));
		
		JLabel label_13 = new JLabel("Type:");
		panel_6.add(label_13);
		
		textField_10 = new JTextField();
		textField_10.setText("SimpleBroadcastInterface");
		textField_10.setEditable(false);
		textField_10.setColumns(10);
		panel_6.add(textField_10);
		
		JLabel label_14 = new JLabel("Transmit Speed:");
		panel_6.add(label_14);
		
		txtk_5 = new JTextField();
		txtk_5.setText("110K");
		txtk_5.setColumns(10);
		panel_6.add(txtk_5);
		
		JLabel label_15 = new JLabel("Transmit Range:");
		panel_6.add(label_15);
		
		textField_13 = new JTextField();
		textField_13.setText("20000");
		textField_13.setColumns(10);
		panel_6.add(textField_13);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBounds(477, 299, 456, 102);
		panel_8.add(panel_4);
		panel_4.setToolTipText("");
		panel_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "SAT CNInterface", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_4.setLayout(new GridLayout(3, 2, 0, 0));
		
		JLabel label_7 = new JLabel("Type:");
		panel_4.add(label_7);
		
		textField_6 = new JTextField();
		textField_6.setText("SimpleBroadcastInterface");
		textField_6.setEditable(false);
		textField_6.setColumns(10);
		panel_4.add(textField_6);
		
		JLabel label_8 = new JLabel("Transmit Speed:");
		panel_4.add(label_8);
		
		txtk_3 = new JTextField();
		txtk_3.setText("2500K");
		txtk_3.setColumns(10);
		panel_4.add(txtk_3);
		
		JLabel label_9 = new JLabel("Transmit Range:");
		panel_4.add(label_9);
		
		textField_9 = new JTextField();
		textField_9.setText("10");
		textField_9.setColumns(10);
		panel_4.add(textField_9);
		
		JPanel panel_7 = new JPanel();
		panel_7.setBounds(476, 412, 456, 102);
		panel_8.add(panel_7);
		panel_7.setToolTipText("");
		panel_7.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "SAT CNInterface", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_7.setLayout(new GridLayout(3, 2, 0, 0));
		
		JLabel label_16 = new JLabel("Type:");
		panel_7.add(label_16);
		
		textField_12 = new JTextField();
		textField_12.setText("SimpleBroadcastInterface");
		textField_12.setEditable(false);
		textField_12.setColumns(10);
		panel_7.add(textField_12);
		
		JLabel label_17 = new JLabel("Transmit Speed:");
		panel_7.add(label_17);
		
		txtk_6 = new JTextField();
		txtk_6.setText("2560K");
		txtk_6.setColumns(10);
		panel_7.add(txtk_6);
		
		JLabel label_18 = new JLabel("Transmit Range:");
		panel_7.add(label_18);
		
		textField_15 = new JTextField();
		textField_15.setText("10");
		textField_15.setColumns(10);
		panel_7.add(textField_15);
		
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getContentPane().remove(panel_8);
				frame.repaint();
			}
		});
	}
}
