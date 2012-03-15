/*
 * OpenPixi - Open Particle-In-Cell (PIC) Simulator
 * Copyright (C) 2012  OpenPixi.org
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openpixi.pixi.ui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import javax.swing.event.*;

import org.openpixi.pixi.physics.Simulation;

/**
 * Displays the animation of particles.
 */
public class MainControlApplet extends JApplet {

	private JButton startButton;
	private JButton stopButton;
	private JButton resetButton;
	private JButton testButton;
	
	private JSlider speedSlider;
	private JSlider stepSlider;
	
	private JSlider  dragSlider;
	
	private JSlider efieldXSlider;
	private JSlider efieldYSlider;
	private JSlider bfieldZSlider;
	private JSlider gfieldXSlider;
	private JSlider gfieldYSlider;
	
	private JCheckBox framerateCheck;
	private JCheckBox currentgridCheck;
	private JCheckBox fieldsCheck;
	private JCheckBox writePositionCheck;
	
	private JTextField xboxentry;
	private JTextField yboxentry;
	
	private JTextField filename;
	private JTextField filedirectory;
	
	private JComboBox initComboBox;
	private JComboBox algorithmComboBox;
	private JCheckBox traceCheck;
	private Particle2DPanel particlePanel;

	private static final double speedSliderScaling = 0.07;
	private static final double stepSliderScaling = 0.01;
	private static final double dragSliderScaling = 0.01;
	private static final double exSliderScaling = 0.05;
	private static final double eySliderScaling = 0.05;
	private static final double bzSliderScaling = 0.005;
	private static final double gxSliderScaling = 0.01;
	private static final double gySliderScaling = 0.01;

	String[] initStrings = {
			"10 random particles",
			"100 random particles",
			"1000 random particles",
			"10000 random particles",
			"Single particle in gravity",
			"Single particle in el. Field",
			"3 part. in magnetic field",
			"Spring force test"};
	
	String[] solverString = {
			"Euler Richardson",
			"LeapFrog",
			"LeapFrog Damped",
			"LeapFrog Half Step",
			"Boris",
			"Boris Damped",
			"Semi Implicit Euler",
			"Euler"};

	/**
	 * Listener for slider.
	 */
	class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent eve) {
			JSlider source = (JSlider) eve.getSource();
			if(source.getValueIsAdjusting())
			{
				int delay = (int) (1000 * Math.exp(-source.getValue() * speedSliderScaling));
				particlePanel.timer.setDelay(delay);
			}
		}
	}
	
	class ComboBoxListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox) e.getSource();
			int id  = cb.getSelectedIndex();
			particlePanel.resetAnimation(id);
			particlePanel.resetAnimation(initComboBox.getSelectedIndex());
			setSlidersValue();
		}
	}
	
	class AlgorithmListener implements ActionListener {
		public void actionPerformed(ActionEvent eve) {
			JComboBox cbox = (JComboBox) eve.getSource();
			int id = cbox.getSelectedIndex();
			particlePanel.algorithmChange(id);
			//one can use this instead of the method, just need to change algorithm_change to public
			//particlePanel.algorithm_change = id;
		}
	}

	/**
	 * Listener for start button.
	 */
	class StartListener implements ActionListener {
		public void actionPerformed(ActionEvent eve) {
			particlePanel.startAnimation();
		}
	}

	/**
	 * Listener for stop button.
	 */
	class StopListener implements ActionListener {
		public void actionPerformed(ActionEvent eve) {
			particlePanel.stopAnimation();
		}
	}

	/**
	 * Listener for reset button.
	 */
	class ResetListener implements ActionListener {
		public void actionPerformed(ActionEvent eve) {
			particlePanel.resetAnimation(initComboBox.getSelectedIndex());
			setSlidersValue();
			testButton.setEnabled(true);
		}
	}
	
	class TestListener implements ActionListener {
		public void actionPerformed(ActionEvent eve) {
				testButton.setEnabled(false);
				particlePanel.testSolver();
		}
	}
	
	class CheckListener implements ItemListener {
		public void itemStateChanged(ItemEvent eve){
				particlePanel.checkTrace();
		}
	}
	
	class WritePosition implements ItemListener {
		public void itemStateChanged(ItemEvent eve){
			if(eve.getStateChange() == ItemEvent.SELECTED)
				filename.setEnabled(true);
				filename.setEditable(true);
				filedirectory.setEnabled(true);
				filedirectory.setEditable(true);
			if(eve.getStateChange() == ItemEvent.DESELECTED)
			{
				filename.setEditable(false);
				particlePanel.writePosition();
			}
		}
	}
	
	class WriteFilename implements ActionListener {
		public void actionPerformed(ActionEvent eve) {
			if(writePositionCheck.isSelected())
			{
				particlePanel.fileName = filename.getText();
				particlePanel.fileDirectory = filedirectory.getText();
				particlePanel.writePosition();
				filename.setEditable(false);
				filename.setEnabled(false);
				filedirectory.setEditable(false);
				filedirectory.setEnabled(false);
			}
		}
	}
	
	class DrawCurrentGridListener implements ItemListener {
		public void itemStateChanged(ItemEvent eve){
				particlePanel.drawCurrentGrid();
		}
	}
	
	class DrawFieldsListener implements ItemListener {
		public void itemStateChanged(ItemEvent eve){
				particlePanel.drawFields();
		}
	}
	
	class FrameListener implements ItemListener {
		public void itemStateChanged(ItemEvent eve) {
			if(eve.getStateChange() == ItemEvent.SELECTED) {
				particlePanel.showinfo = true;
			} else if(eve.getStateChange() == ItemEvent.DESELECTED) {
				particlePanel.showinfo = false;
			}
		}
	}
	
	
	class DragListener implements ChangeListener{
		public void stateChanged(ChangeEvent eve) {
			JSlider source = (JSlider) eve.getSource();
			if(source.getValueIsAdjusting())
			{
				double value = source.getValue() * dragSliderScaling;
				Simulation.f.drag = value;
			}
		}
	}
	
	class EFieldXListener implements ChangeListener{
		public void stateChanged(ChangeEvent eve) {
			JSlider source = (JSlider) eve.getSource();
			if(source.getValueIsAdjusting())
			{
				double value = source.getValue() * exSliderScaling;
				Simulation.f.ex = value;
			}
		}
	}
	
	class EFieldYListener implements ChangeListener{
		public void stateChanged(ChangeEvent eve) {
			JSlider source = (JSlider) eve.getSource();
			if(source.getValueIsAdjusting())
			{
				double value = source.getValue() * eySliderScaling;
				Simulation.f.ey = value;
			}
		}
	}
	
	class BFieldZListener implements ChangeListener{
		public void stateChanged(ChangeEvent eve) {
			JSlider source = (JSlider) eve.getSource();
			if(source.getValueIsAdjusting())
			{
				double value = source.getValue() * bzSliderScaling;
				Simulation.f.bz = value;
			}
		}
	}
	
	class GFieldXListener implements ChangeListener{
		public void stateChanged(ChangeEvent eve) {
			JSlider source = (JSlider) eve.getSource();
			if(source.getValueIsAdjusting())
			{
				double value = source.getValue() * gxSliderScaling;
				Simulation.f.gx = value;
			}
		}
	}
	
	class GFieldYListener implements ChangeListener{
		public void stateChanged(ChangeEvent eve) {
			JSlider source = (JSlider) eve.getSource();
			if(source.getValueIsAdjusting())
			{
				double value = source.getValue() * gySliderScaling;
				Simulation.f.gy = value;
			}
		}
	}
	
	class StepListener implements ChangeListener{
		public void stateChanged(ChangeEvent eve) {
			JSlider source = (JSlider) eve.getSource();
			if(source.getValueIsAdjusting())
			{
				double value = source.getValue() * stepSliderScaling;
				Simulation.tstep = value;
			}
		}
	}
	
	class BoxDimension implements ActionListener{
		public void actionPerformed(ActionEvent eve) {
			int xbox = Integer.parseInt(xboxentry.getText());
			int ybox = Integer.parseInt(yboxentry.getText());
			Simulation.currentGrid.changeDimension(xbox, ybox);
			Simulation.currentGrid.setGrid(particlePanel.getWidth(), particlePanel.getHeight());
		}
	}

	
	/**
	 * Constructor.
	 */
	public MainControlApplet() {
		particlePanel = new Particle2DPanel();

		this.setVisible(true);
		this.setSize(1000, 500);

		startButton = new JButton("start");
		stopButton = new JButton("stop");
		resetButton = new JButton("reset");
		testButton = new JButton("Test");
		testButton.setToolTipText("Click the reset button afterwards to exit the test");

		/**one can also write a constructor for a JSlider as:
		 * JSlider slider = new JSlider(int min, int max, int value);
		 * where min is the minimal value (the same as setMinimum(int min), 
		 * max is the maximal value (the same as setMinimum(int max),
		 * and value is the current value (the same as setValue(int value),
		 * and the code would be shorter,
		 * but they are written like this, so it is clearer and not so confusing
		 */
		
		speedSlider = new JSlider();
		speedSlider.addChangeListener(new SliderListener());
		speedSlider.setMinimum(0);
		speedSlider.setMaximum(100);
		speedSlider.setValue(30);
		speedSlider.setMajorTickSpacing(5);
		speedSlider.setMinorTickSpacing(1);
		speedSlider.setPaintTicks(true);
		JLabel speedLabel = new JLabel("Frame rate");
		Box speed = Box.createVerticalBox();
		speed.add(speedLabel);
		speed.add(speedSlider);
		
		stepSlider = new JSlider();
		stepSlider.addChangeListener(new StepListener());
		stepSlider.setMinimum(1);
		stepSlider.setMaximum(100);
		stepSlider.setValue((int) (100 * (Simulation.tstep = 0.5)));
		stepSlider.setMajorTickSpacing(10);
		stepSlider.setMinorTickSpacing(2);
		stepSlider.setPaintTicks(true);
		JLabel stepLabel = new JLabel("Size of time step");
		Box step = Box.createVerticalBox();
		step.add(stepLabel);
		step.add(stepSlider);
		
		dragSlider = new JSlider();
		dragSlider.addChangeListener(new DragListener());
		dragSlider.setMinimum(0);
		dragSlider.setMaximum(100);
		dragSlider.setValue((int) Simulation.f.drag);
		dragSlider.setMajorTickSpacing(10);
		dragSlider.setMinorTickSpacing(2);
		dragSlider.setPaintTicks(true);
		JLabel dragLabel = new JLabel("Drag coefficient");
		
		efieldXSlider = new JSlider();
		efieldXSlider.addChangeListener(new EFieldXListener());
		efieldXSlider.setMinimum(-100);
		efieldXSlider.setMaximum(100);
		efieldXSlider.setValue((int) Simulation.f.ex);
		efieldXSlider.setMajorTickSpacing(20);
		efieldXSlider.setMinorTickSpacing(5);
		efieldXSlider.setPaintTicks(true);
		
		efieldYSlider = new JSlider();
		efieldYSlider.addChangeListener(new EFieldYListener());
		efieldYSlider.setMinimum(-100);
		efieldYSlider.setMaximum(100);
		efieldYSlider.setValue((int) Simulation.f.ey);
		efieldYSlider.setMajorTickSpacing(20);
		efieldYSlider.setMinorTickSpacing(5);
		efieldYSlider.setPaintTicks(true);
		
		bfieldZSlider = new JSlider();
		bfieldZSlider.addChangeListener(new BFieldZListener());
		bfieldZSlider.setMinimum(-100);
		bfieldZSlider.setMaximum(100);
		bfieldZSlider.setValue((int) Simulation.f.bz);
		bfieldZSlider.setMajorTickSpacing(20);
		bfieldZSlider.setMinorTickSpacing(5);
		bfieldZSlider.setPaintTicks(true);
		
		gfieldXSlider = new JSlider();
		gfieldXSlider.addChangeListener(new GFieldXListener());
		gfieldXSlider.setMinimum(-100);
		gfieldXSlider.setMaximum(100);
		gfieldXSlider.setValue((int) Simulation.f.gx);
		gfieldXSlider.setMajorTickSpacing(20);
		gfieldXSlider.setMinorTickSpacing(5);
		gfieldXSlider.setPaintTicks(true);
		
		gfieldYSlider = new JSlider();
		gfieldYSlider.addChangeListener(new GFieldYListener());
		gfieldYSlider.setMinimum(-100);
		gfieldYSlider.setMaximum(100);
		gfieldYSlider.setValue((int) Simulation.f.gy);
		gfieldYSlider.setMajorTickSpacing(20);
		gfieldYSlider.setMinorTickSpacing(5);
		gfieldYSlider.setPaintTicks(true);
		
		initComboBox = new JComboBox(initStrings);
		initComboBox.setSelectedIndex(0);
		initComboBox.addActionListener(new ComboBoxListener());
		JLabel initComboBoxLabel = new JLabel("Initial conditions");
		Box initBox = Box.createVerticalBox();
		initBox.add(initComboBoxLabel);
		initBox.add(initComboBox);
		
		algorithmComboBox = new JComboBox(solverString);
		algorithmComboBox.setSelectedIndex(0);
		algorithmComboBox.addActionListener(new AlgorithmListener());
		JLabel algorithmLabel = new JLabel("Algorithm");
		Box algorithmBox = Box.createVerticalBox();
		algorithmBox.add(algorithmLabel);
		algorithmBox.add(algorithmComboBox);
		
		startButton.addActionListener(new StartListener());
		stopButton.addActionListener(new StopListener());
		resetButton.addActionListener(new ResetListener());
		testButton.addActionListener(new TestListener());
		
		traceCheck = new JCheckBox("Trace");
		traceCheck.addItemListener(new CheckListener());
		
		currentgridCheck = new JCheckBox("Current");
		currentgridCheck.addItemListener(new DrawCurrentGridListener());
		
		fieldsCheck = new JCheckBox("Fields");
		fieldsCheck.addItemListener(new DrawFieldsListener());
		
		framerateCheck = new JCheckBox("Info");
		framerateCheck.addItemListener(new FrameListener());
		
		writePositionCheck = new JCheckBox("Write Position");
		writePositionCheck.addItemListener(new WritePosition());
		
		xboxentry = new JTextField(2);
		xboxentry.setText("10");
		xboxentry.addActionListener(new BoxDimension());
		
		yboxentry = new JTextField(2);
		yboxentry.setText("10");
		yboxentry.addActionListener(new BoxDimension());
		
		filename = new JTextField(10);
		filename.setText("Filename");
		filename.setEnabled(false);
		filename.setEditable(false);
		//filename.addActionListener(new WriteFilename());
		
		filedirectory = new JTextField(10);
		filedirectory.setText("Dir., ex. C:\\Pixi");
		filedirectory.setEnabled(false);
		filedirectory.setEditable(false);
		filedirectory.addActionListener(new WriteFilename());
		filedirectory.setToolTipText("Please enter an existing directory");
		
		
		JLabel xboxentryLabel = new JLabel("Cell width");
		JLabel yboxentryLabel = new JLabel("Cell height");
		/*Box currentBox = Box.createHorizontalBox();
		currentBox.add(currentgridCheck);
		currentBox.add(xboxentryLabel);
		currentBox.add(xboxentry);
		currentBox.add(Box.createHorizontalGlue());
		currentBox.add(yboxentryLabel);
		currentBox.add(yboxentry);*/

		JPanel controlPanelUp = new JPanel();
		controlPanelUp.setLayout(new FlowLayout());
		controlPanelUp.add(startButton);
		controlPanelUp.add(stopButton);
		controlPanelUp.add(resetButton);
//		controlPanelUp.add(testButton);
		//controlPanelUp.add(traceCheck);
		//controlPanelUp.add(framerateCheck);
//		controlPanelUp.add(writePositionCheck);
//		controlPanelUp.add(filename);
//		controlPanelUp.add(filedirectory);
		controlPanelUp.add(Box.createHorizontalGlue());
		controlPanelUp.add(currentgridCheck);	
		controlPanelUp.add(fieldsCheck);	
		controlPanelUp.add(Box.createHorizontalGlue());
		//controlPanelUp.add(currentBox);
		controlPanelUp.add(xboxentryLabel);
		controlPanelUp.add(xboxentry);
		controlPanelUp.add(Box.createHorizontalGlue());
		controlPanelUp.add(yboxentryLabel);
		controlPanelUp.add(yboxentry);
		
		
		JPanel controlPanelDown = new JPanel();
		controlPanelDown.setLayout(new FlowLayout());
		controlPanelDown.add(traceCheck);
		controlPanelDown.add(framerateCheck);
		controlPanelDown.add(initBox);
		controlPanelDown.add(algorithmBox);
		controlPanelDown.add(speed);
		controlPanelDown.add(step);
		
		Box panelBox = Box.createVerticalBox();
		panelBox.add(controlPanelUp);
		panelBox.add(controlPanelDown);
		
		JLabel eFieldXLabel = new JLabel("Electric Field in x - direction");
		JLabel eFieldYLabel = new JLabel("Electric Field in y - direction");
		JLabel bFieldZLabel = new JLabel("Magnetic Field in z - direction");
		JLabel gFieldXLabel = new JLabel("Gravitation in x - direction Field");
		JLabel gFieldYLabel = new JLabel("Gravitation in y - direction Field");
		
		Box fieldsBox = Box.createVerticalBox();
		fieldsBox.add(eFieldXLabel);
		fieldsBox.add(efieldXSlider);
		fieldsBox.add(eFieldYLabel);
		fieldsBox.add(efieldYSlider);
		fieldsBox.add(Box.createVerticalGlue());
		fieldsBox.add(bFieldZLabel);
		fieldsBox.add(bfieldZSlider);
		fieldsBox.add(Box.createVerticalGlue());
		fieldsBox.add(gFieldXLabel);
		fieldsBox.add(gfieldXSlider);
		fieldsBox.add(gFieldYLabel);
		fieldsBox.add(gfieldYSlider);
		fieldsBox.add(Box.createVerticalGlue());
		fieldsBox.add(dragLabel);
		fieldsBox.add(dragSlider);
		fieldsBox.add(Box.createVerticalGlue());
		
		this.setLayout(new BorderLayout());
		this.add(panelBox, BorderLayout.SOUTH);
		this.add(particlePanel, BorderLayout.CENTER);
		this.add(fieldsBox, BorderLayout.EAST);

	}
	
	public void setText(JTextArea text, String str, boolean onoff)
	{
		if(onoff)
			text.insert(str, 0);
		else
			text.replaceRange(" ", 0, text.getDocument().getLength());
	}
	
	public void setSlidersValue()
	{
		Simulation.tstep = 0.5;
		stepSlider.setValue(50);
		efieldXSlider.setValue((int) (Simulation.f.ex / exSliderScaling));
		efieldYSlider.setValue((int) (Simulation.f.ey / eySliderScaling));
		bfieldZSlider.setValue((int) (Simulation.f.bz / bzSliderScaling));
		gfieldXSlider.setValue((int) (Simulation.f.gx / gxSliderScaling));
		gfieldYSlider.setValue((int) (Simulation.f.gy / gySliderScaling));
		dragSlider.setValue((int) (Simulation.f.drag / dragSliderScaling));
		//int delay = particlePanel.timer.getDelay();
		//speedSlider.setValue((int) (-Math.log(delay / 1000.) / speedSliderScaling));
		speedSlider.setValue(50);
		particlePanel.timer.setDelay((int) (1000 * Math.exp(-50 * speedSliderScaling)));
		xboxentry.setText("10");
		yboxentry.setText("10");
		Simulation.currentGrid.changeDimension(10, 10);
		Simulation.currentGrid.setGrid(particlePanel.getWidth(), particlePanel.getHeight());
		writePositionCheck.setSelected(false);
		filename.setEditable(false);
		filename.setEnabled(false);
		filename.setText("Filename");
		filedirectory.setEditable(false);
		filedirectory.setEnabled(false);
		filedirectory.setText("direc., ex. C:\\Pixi");
	}
	
	@Override
	public void init() {
		super.init();

		particlePanel.timer.start();
		setSlidersValue();
	}

	/**
	 * Entry point for java application.
	 */
	public static void main(String[] args) {

		JFrame web = new JFrame();

		web.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		web.setTitle("OpenPixi");
		MainControlApplet applet = new MainControlApplet();
		web.setContentPane(applet);

		web.pack();
		web.setVisible(true);
		web.setSize(1000, 500);

		applet.init();
	}

}
