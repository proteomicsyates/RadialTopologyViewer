// NetQuant is a graph viewer which allows interactive editing of networks
// (edges and nodes) and connects to databases and provides quantitative
// analysis.
// NetQuant is based on Medusa developed by Sean Hopper
// <http://www.bork.embl.de/medusa>
//
// Copyright (C) 2011 Diego Calzolari
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

package netQuant;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import netQuant.dataio.DataLoader;
import netQuant.display.EditableGraphPanel;
import netQuant.graph.Graph;
import netQuant.graph.Node;
import netQuant.graphedit.EditGraphDialog;
import netQuant.stringconnection.ConnectorManagerLite;
import netQuant.utils.AppVersion;

/**
 * The main holding and controlling frame for the graph displaying panels. This
 * tool is compendium of coPIT to show the radial interactions. The methods
 * involved are: radial(), setRadialLegendOrder();
 *
 * @author Diego Calzolari
 * @version 0.6
 */
public final class RadialTopologyViewer extends JFrame implements ItemListener, ComponentListener, ChangeListener {
	private static final String APP_PROPERTIES = "app.properties";

	/**
	 * to print label when exporting to EPS file
	 */
	public static boolean printLabelEPS = false;
	/**
	 * @serial
	 */
	private static final long serialVersionUID = -6468131725367360783L;
	/**
	 * debug level for output
	 */
	private static final int verbosity = 5;
	/**
	 * version number
	 */
	public static AppVersion versionApp;
	// this class should not directly require StringletPanel...
	// use reflection
	EditableGraphPanel stringletPanel;

	// test here
	// Object stringletPanel;

	NetQuantSettings stringSettings;
	EditGraphDialog egd;

	static final Color darkBackground = new Color(0.0f, 0.0f, 0.33f);
	// static final Color foreground = new Color(1.0f, 1.0f, 1.0f);
	static final Color foreground = Color.black;

	/**
	 * The main color of components
	 */
	public static final Color STRINGCOLOR = new Color(118, 186, 243);
	Color background = STRINGCOLOR;
	Border graphBorder = BorderFactory.createRaisedBevelBorder();

	// scroll able panel
	JScrollPane jScrollPane;
	// a holder frame for dialogs etc
	Frame f = new Frame();

	public RadialTopologyViewer() {
		super("RadialTopologyViewer " + getVersion().toString());
	}

	/**
	 * Initializes the main panel the default is 3/2 of the screen dimension on
	 * x and y
	 */
	private final void init() {

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		stringSettings = new NetQuantSettings();

		stringletPanel = new EditableGraphPanel(stringSettings);
		stringletPanel.setBasicEdgeColor(java.awt.Color.black);
		stringletPanel.setTimeFrameXY((int) (dim.getWidth() / 1.5), (int) (dim.getHeight() / 1.5));
		stringletPanel.setBorder(graphBorder);
		stringletPanel.setShowBorder(false);
		stringletPanel.setArrows(false);
		stringletPanel.setCool(true);

		jScrollPane = new JScrollPane(stringletPanel);

		egd = new EditGraphDialog(this, true);

		buildGUI();

	}

	JMenuBar menuBar = new JMenuBar();

	/**
	 * GUI container
	 */
	protected void buildGUI() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addComponentListener(this);
		Container content = getContentPane();
		content.setLayout(new BorderLayout());

		// add the menus
		menuBar.setBackground(background);

		populateFileMenu();
		menuBar.add(fileMenu);
		populateSelectMenu();
		menuBar.add(selectMenu);
		populateOptionMenu();
		menuBar.add(optionMenu);
		populateManipulateMenu();
		menuBar.add(manipulateMenu);
		populateInteractionMenu();
		// menuBar.add(interactionMenu);
		populateDisplayMenu();
		// menuBar.add(displayMenu);
		populateTopologyMenu();
		// menuBar.add(topologyMenu);
		populateHelpMenu();
		// menuBar.add(helpMenu);
		setJMenuBar(menuBar);

		// add the controls
		controlPanel = new JPanel();
		populateControlPanel();

		// set borders
		controlPanel.setBorder(graphBorder);

		// add components
		content.add("Center", jScrollPane);
		content.add("South", controlPanel);

		populateLegendPanel();
		populateScalerPanel(); // do I want to save it?

		content.add("East", scalerPanel);
		// add keyboard bindings
		// addKeyBindings();

	}

	/**
	 * Initializes data if it finds setting, otherwise it uses a default network
	 */
	void initData() {
		try {
			boolean load = loadSettings();
			defaultNet();
			if (stringletPanel == null)
				if (verbosity > 0)
					System.out.println("WARNING: stringletPanel is null");
			if (load) {
				// stringletPanel.loadingMessage();
				// stringletPanel.loadGraph(lastDir);
				if (verbosity > 4)
					System.out.println("waiting for loading net");
				waitForLoad(lastDir, DataLoader.LOAD_NET);
			} else {
				if (verbosity > 4)
					System.out.println("default network created");
			}
		} catch (IOException ie) {
			defaultNet();
		} catch (DataFormatException de) {
			JOptionPane.showMessageDialog(this, de.getMessage(), "Init Data Error", JOptionPane.ERROR_MESSAGE);
			defaultNet();
		}

	}

	public static netQuant.utils.AppVersion getVersion() {
		if (versionApp == null) {
			try {
				String tmp = netQuant.utils.PropertiesReader.getProperties(APP_PROPERTIES).getProperty("assembly.dir");
				if (tmp.contains("v")) {
					versionApp = new netQuant.utils.AppVersion(tmp.split("v")[1]);
				} else {
					versionApp = new netQuant.utils.AppVersion(tmp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return versionApp;

	}

	/**
	 * Default empty network
	 */
	public void defaultNet() {
	}

	/**
	 * main method
	 */
	public static void main(String[] args) {

		/**
		 * Set up look and feel
		 */
		if (verbosity > 4)
			System.out.println("Setting up look and feel");
		RadialTopologyViewer.setLnF();

		/**
		 * creating a new main frame
		 */
		if (verbosity > 4)
			System.out.println("initiliazing new main frame");
		RadialTopologyViewer stringletFrame = new RadialTopologyViewer();
		stringletFrame.init();

		try {
			/**
			 * check if any arg is present
			 */
			if (verbosity > 4)
				System.out.println("checking args...");
			stringletFrame.checkArgs(args);
		} catch (DataFormatException ex) {
			if (verbosity > 0)
				System.out.println("Data format error! Check your data.");
			ex.printStackTrace();
			System.exit(0);
		} catch (IOException ex) {
			if (verbosity > 0)
				System.out.println("File error! Check the integrity of your file.");
			ex.printStackTrace();
			System.exit(0);
		}

		/**
		 * show main frame
		 */
		if (verbosity > 4)
			System.out.println("creating main frame...");
		stringletFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		stringletFrame.pack();
		centerWindow(stringletFrame);
		stringletFrame.setVisible(true);
	}

	/**
	 * Check arguments from main method the format is "f" file "i" interaction
	 * the number of parameters can be 0 or 2,4,6....
	 *
	 * @param args
	 * @throws IOException
	 * @throws DataFormatException
	 */
	public void checkArgs(String[] args) throws IOException, DataFormatException {

		if (verbosity > 4)
			System.out.println("Checking arguments");

		int ln = args.length;
		// System.out.println(args.length);
		// should be 0, 2 or 4
		if (ln == 0) {
			if (verbosity > 4)
				System.out.println("No arguments. Proceeding normally");
			initData();
			return;
		} else {
			if (verbosity > 4)
				System.out.println("checking args number is even...");
			if (ln % 2 == 0) {

				if (verbosity > 4)
					System.out.println("checking arguments...");
				for (int i = 0; i < ln; i = i + 2) {
					checkArgPair(new String[] { args[i], args[i + 1] });
				}

			} else {
				if (verbosity > 0)
					System.out.println("the parameters are odd...");

				final Boolean printLabelsOnEPS = Boolean.valueOf(args[0]);
				if (printLabelsOnEPS != null) {
					RadialTopologyViewer.printLabelEPS = printLabelsOnEPS;
				}
			}
		}
	}

	/**
	 * load specified data
	 *
	 * @param args
	 * @throws IOException
	 * @throws DataFormatException
	 */
	private void checkArgPair(String[] args) throws IOException, DataFormatException {

		if (args[0].compareTo("f") == 0) {
			loadData(args[1], true);
		}
		if (args[0].compareTo("i") == 0) {
			loadInteractionSettings(args[1]);
		}
		if (args[0].compareTo("t") == 0) {
			loadData(args[1], false);
		}

	}

	/**
	 * Loads a data file without threading. Useful for loading graphs before
	 * start-up
	 *
	 * @param data
	 * @param formatted
	 * @throws java.io.IOException
	 * @throws netQuant.DataFormatException
	 */
	public void loadData(String data, boolean formatted) throws IOException, DataFormatException {

		dl = new DataLoader();
		Graph g = null;

		if (verbosity > 1)
			System.out.println("Loading " + data + ". Please wait");

		if (formatted) {
			if (verbosity > 4)
				System.out.println("Using normal load");
			g = dl.load(data);
		} else {
			if (verbosity > 4)
				System.out.println("Using simplest load");
			g = dl.loadSimplest(data);
		}

		if (verbosity > 4)
			System.out.println("Setting graph");
		stringletPanel.setGraph(g);

	}

	/**
	 * Load interactions
	 *
	 * @param interactionSettings
	 * @throws IOException
	 */
	private final void loadInteractionSettings(String interactionSettings) throws IOException {

		if (verbosity > 4)
			System.out.println("loading interactions");
		stringSettings.load(interactionSettings);

		if (verbosity > 4)
			System.out.println("Updating legend");
		updateLegend();

		if (verbosity > 4)
			System.out.println("Updating String settings");
		stringletPanel.updateStringSettings(stringSettings);
	}

	/**
	 * Set look and feel, it tries to set up one particular look and feel, if it
	 * fails, the system default is used.
	 */
	public static void setLnF() {

		try {
			if (verbosity > 4)
				System.out.println("setting L&F");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			if (verbosity > 4)
				System.out.println("using system default L&F");
			e.printStackTrace();
		} catch (InstantiationException e) {
			if (verbosity > 4)
				System.out.println("using system default L&F");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			if (verbosity > 4)
				System.out.println("using system default L&F");
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			if (verbosity > 4)
				System.out.println("using system default L&F");
			e.printStackTrace();
		}

	}

	/**
	 * create main window, and centers it in the middle of the screen
	 *
	 * @param win
	 */
	public static void centerWindow(java.awt.Window win) {

		if (verbosity > 4)
			System.out.println("centering frame");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = win.getSize();
		win.setLocation((screenSize.width - (frameSize.width)) / 2, (screenSize.height - (frameSize.height)) / 2);
	}

	// ----------------------------------------------------------------------------------------------------
	// Control Buttons
	// ----------------------------------------------------------------------------------------------------

	// controlPanel
	JPanel controlPanel;

	// FR button

	JButton loadButton = new JButton("Load");
	JButton inputButton = new JButton("Input");
	JButton controlButton = new JButton("Control");
	JButton expButton = new JButton("Experiment");
	JButton baitButton = new JButton("Bait");
	JButton dynamicButton = new JButton("Dynamic");
	JButton analysisButton = new JButton("Analysis");
	JButton resetButton = new JButton("Reset");

	JButton radialButton = new JButton("Radial Analysis");
	JButton radialorderButton = new JButton("Radial Order");

	JButton frButton = new JButton("Spring");
	JButton stringButton = new JButton("Data");
	JButton recalcButton = new JButton("Recalculate");
	JToggleButton relax = new JToggleButton("Relax", false);
	JToggleButton temperatureButton = new JToggleButton("Cooling", false);
	//
	/**
	 * Label to display the number of nodes and edges loaded
	 */
	JLabel fileLabel = new JLabel("<html>File: none<b></b></html>");
	JLabel infoLabel = new JLabel("<html><b>nodes: 0<br>edges: 0</b></html>");
	JLabel emptyLabel = new JLabel("  ");

	/**
	 * Populate elements of control panel
	 */
	private void populateControlPanel() {

		if (verbosity > 4)
			System.out.println("Populating Control Panel");

		// labels
		if (verbosity > 4)
			System.out.println("adding control labels...");

		// controlPanel.add(fileLabel);
		// controlPanel.add(infoLabel);
		// controlPanel.add(emptyLabel);

		// control buttons
		if (verbosity > 4)
			System.out.println("adding control buttons...");

		inputButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				loadInput(); // connection to STRING database....
			}
		});

		controlButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				loadControl();
			}
		});

		expButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				loadExp();
			}
		});

		baitButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				loadBaitData();
			}
		});

		dynamicButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				dynoAnalysis();
			}
		});

		loadButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				loadQuantData();
			}
		});

		analysisButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				analyze();
			}
		});

		resetButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				reset();
			}
		});

		radialButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				radial();
			}
		});
		radialorderButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				setRadialLegendOrder();
			}
		});

		controlPanel.add(radialButton);
		controlPanel.add(radialorderButton);
		// controlPanel.add(loadButton);
		// controlPanel.add(baitButton);
		// controlPanel.add(inputButton);
		// controlPanel.add(controlButton);
		// controlPanel.add(expButton);
		// controlPanel.add(dynamicButton);

		// controlPanel.add(analysisButton);
		// controlPanel.add(resetButton);

	}

	/**
	 * update the info label
	 */
	private void updateInfo() {

		if (verbosity > 4)
			System.out.println("updating nodes and edge number info...");
		String[] result = stringletPanel.getGraphData();
		String display = "<html><b>nodes: " + result[0] + "<br>edges: " + result[1] + "</b></html>";
		infoLabel.setText(display);
	}

	/**
	 * update the file info label
	 */
	private void updateFileInfo() {

		if (verbosity > 4)
			System.out.println("updating file info");
		fileLabel.setText(lastFile);

	}

	// -----------------------------------------------------------------------------------------------------
	// Scale Panel
	// -----------------------------------------------------------------------------------------------------

	/**
	 * Set up the legend and scaler panel
	 */
	JPanel scalerPanel = new JPanel();
	JLabel scalerLabel = new JLabel("  Scale");
	JSlider scalerSlider = new JSlider(JSlider.VERTICAL, 1, 1000, 100);

	/**
	 * Populate zoom scale panel
	 */
	private void populateScalerPanel() {

		if (verbosity > 4)
			System.out.println("Populate Scale Panel");

		scalerPanel.setLayout(new BoxLayout(scalerPanel, BoxLayout.Y_AXIS));
		scalerPanel.setOpaque(true);
		scalerPanel.setBackground(background);
		scalerPanel.setForeground(foreground);

		scalerSlider.setMajorTickSpacing(50);
		scalerSlider.setMinorTickSpacing(10);
		scalerSlider.setPaintTicks(true);
		scalerSlider.setPaintLabels(true);
		scalerSlider.setBackground(background);
		scalerSlider.setForeground(foreground);

		legendPanel.setAlignmentX(LEFT_ALIGNMENT);

		scalerSlider.setAlignmentX(LEFT_ALIGNMENT);

		scalerLabel.setAlignmentX(LEFT_ALIGNMENT);
		scalerLabel.setForeground(foreground);

		scalerPanel.add(legendPanel);
		scalerPanel.add(scalerLabel);
		scalerPanel.add(scalerSlider);

		scalerSlider.addChangeListener(this);

	}

	// -----------------------------------------------------------------------------------------------------
	// Legend Panel
	// -----------------------------------------------------------------------------------------------------

	JPanel legendPanel = new JPanel();
	JToggleButton[] toggleButton;

	JLabel legendTitleLabel;
	JPanel legendTitlePanel;
	JLabel logoLabel;
	JScrollPane legendScroller;

	/**
	 * Populate Legend Panel
	 */
	protected void populateLegendPanel() {

		if (verbosity > 4)
			System.out.println("Populate Legend Panel");

		legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
		legendPanel.setOpaque(true);
		legendPanel.setBackground(background);

		initLegendTitle();
		legendPanel.add(legendTitlePanel);

		initLegendScroller();
		// legendPanel.add(legendScroller);

	}

	/**
	 * update legend
	 */
	public void updateLegend() {

		if (verbosity > 4)
			System.out.println("update legend...");

		legendPanel.removeAll();

		initLegendScroller();

		legendPanel.add(legendTitlePanel);
		legendPanel.add(legendScroller);
		legendPanel.validate();
		scalerPanel.validate();
		scalerPanel.repaint();

	}

	/**
	 * Initialize Legend Title
	 */
	private void initLegendTitle() {

		if (verbosity > 4)
			System.out.println("init legend title");

		legendTitlePanel = new JPanel();
		legendTitlePanel.setLayout(new BoxLayout(legendTitlePanel, BoxLayout.Y_AXIS));

		legendTitlePanel.setOpaque(true);
		legendTitlePanel.setBackground(background);

		legendTitleLabel = new JLabel("Interactions");
		legendTitleLabel.setBackground(background);
		legendTitleLabel.setForeground(foreground);

		// load icon
		logoLabel = new JLabel();
		// java.net.URL imageURL = this.getClass().getClassLoader()
		// .getResource("/netQuant/images/medusa_logo_small.png");
		java.net.URL imageURL = RadialTopologyViewer.class.getClassLoader().getResource("medusa_logo_small.png");

		// TODO change the logo
		logoLabel.setIcon(new javax.swing.ImageIcon(imageURL));

		legendTitlePanel.add(logoLabel);
		legendTitlePanel.add(legendTitleLabel);

	}

	/**
	 * init legend scroller
	 */
	private void initLegendScroller() {

		// if (verbosity>4) System.out.println("init legend scroller");
		// Dimension legendSize=new Dimension(180,100);

		// ShowEdgeMultiPanel sp = new ShowEdgeMultiPanel(this,stringSettings);
		// sp.setBackground(background);

		// legendScroller = new JScrollPane(sp);
		// legendScroller.setPreferredSize(legendSize);
		// legendScroller.revalidate();
	}

	// ------------------------------------------------------------------------------------------------------
	// Options Menu
	// ------------------------------------------------------------------------------------------------------

	/**
	 * Option Menu
	 */
	JMenu optionMenu = new JMenu("Options");

	// elements of Menu
	JMenuItem searchMenuItem = new JMenuItem("Search");
	JMenuItem changeNodeSizeMenuItem = new JMenuItem("Change node size");
	JMenuItem changeNodesSizeMenuItem = new JMenuItem("Change nodes size");
	JMenuItem setEdgeLenMenuItem = new JMenuItem("Edge Length");

	// sub menu of Options
	JMenu colorMenu = new JMenu("Color");

	// elements of color
	JMenuItem changeColorMenuItem = new JMenuItem("Change node color");
	JMenuItem switchColorMenuItem = new JMenuItem("Switch channels");
	JMenuItem copyColorMenuItem = new JMenuItem("Copy channels");
	JMenuItem setBasicEdgeColorMenuItem = new JMenuItem("Base color");
	JMenuItem setBackgroundColorMenuItem = new JMenuItem("Background");

	// sub menu of Options
	JMenu fontMenu = new JMenu("Font");

	// elements of font
	JMenuItem setLabelColorMenuItem = new JMenuItem("Label");
	JMenuItem changeFontSizeMenuItem = new JMenuItem("Font size");

	// sub menu of color
	JMenu imageMenu = new JMenu("Image");

	// elements of image
	JMenuItem loadImageMenuItem = new JMenuItem("Load");
	JMenuItem clearImageMenuItem = new JMenuItem("Clear");

	/**
	 * Populate option menu
	 */
	private void populateOptionMenu() {

		if (verbosity > 4)
			System.out.println("Populate Option Menu");

		// set mnemonic/hot keys
		optionMenu.setMnemonic(KeyEvent.VK_O);
		colorMenu.setMnemonic(KeyEvent.VK_C);
		fontMenu.setMnemonic(KeyEvent.VK_F);
		imageMenu.setMnemonic(KeyEvent.VK_M);

		searchMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				searchNodeEvent();
			}
		});

		changeColorMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				changeColorEvent();
			}
		});

		switchColorMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				switchColorEvent();
			}
		});

		copyColorMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				copyColorEvent();
			}
		});

		xGradientColorMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				stringletPanel.addGradientX(2);
			}
		});

		changeNodeSizeMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				changeNodeSizeEvent();
			}
		});

		changeNodesSizeMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				changeNodesSizeEvent();
			}
		});

		setBasicEdgeColorMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				setBasicEdgeColorEvent();
			}
		});

		setLabelColorMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				setLabelColorEvent();
			}
		});

		setBackgroundColorMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				setBackgroundColorEvent();
			}
		});

		loadImageMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				loadImageEvent();
			}
		});
		clearImageMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				clearImageEvent();
			}
		});
		changeFontSizeMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				changeFontSizeEvent();
			}
		});

		setEdgeLenMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				setEdgeLenEvent();
			}
		});

		// populate sub menu color
		colorMenu.add(changeColorMenuItem);
		colorMenu.add(switchColorMenuItem);
		colorMenu.add(copyColorMenuItem);
		colorMenu.add(setBasicEdgeColorMenuItem);
		colorMenu.add(setBackgroundColorMenuItem);

		// populate sub menu font
		fontMenu.add(setLabelColorMenuItem);
		fontMenu.add(changeFontSizeMenuItem);

		// populate sub menu image
		imageMenu.add(loadImageMenuItem);
		imageMenu.add(clearImageMenuItem);

		// initialize color of option menu
		optionMenu.setBackground(background);
		optionMenu.setForeground(foreground);

		// populate option menu
		optionMenu.add(searchMenuItem);
		optionMenu.add(changeNodeSizeMenuItem);
		optionMenu.add(changeNodesSizeMenuItem);
		optionMenu.add(setEdgeLenMenuItem);
		optionMenu.add(colorMenu);
		optionMenu.add(fontMenu);
		optionMenu.add(imageMenu);

	}

	// -----------------------------------------------------------------------------------------------------
	// Topology Menu
	// -----------------------------------------------------------------------------------------------------

	/**
	 * Topology Menu
	 */
	JMenu topologyMenu = new JMenu("Topology");

	// elements of topology
	JMenuItem layoutMenuItem = new JMenuItem("FR Layout");
	JMenuItem complexLayoutMenuItem = new JMenuItem("Complex Layout");
	JMenuItem dbMenuItem = new JMenuItem("Data");
	JMenuItem recalcMenuItem = new JMenuItem("Reset");
	JCheckBoxMenuItem relaxMenuItem = new JCheckBoxMenuItem("Relax", false);
	JCheckBoxMenuItem temperatureMenuItem = new JCheckBoxMenuItem("Cooling", false);

	/**
	 * Populate topology menu
	 */
	private void populateTopologyMenu() {

		if (verbosity > 4)
			System.out.println("Populate Topology Menu");

		topologyMenu.setMnemonic(KeyEvent.VK_T);
		relaxMenuItem.setMnemonic(KeyEvent.VK_R);

		relaxMenuItem.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(java.awt.event.ItemEvent ex) {
				stringletPanel.setLabel(relaxMenuItem.isSelected());
				stringletPanel.repaint();
				if (relaxMenuItem.isSelected()) {
					stringletPanel.start();
				} else {
					stringletPanel.stop(); // TODO if I deselect it the labels
											// are disappering
				}
			}
		});

		temperatureMenuItem.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				stringletPanel.setCool(temperatureButton.isSelected());
				stringletPanel.repaint();
			}
		});

		dbMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				stringConnection(); // connection to STRING database....
			}
		});

		layoutMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				stopRelax();
				stringletPanel.energy(); // FR algorithm
			}
		});
		complexLayoutMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				stringletPanel.complexLayout(); // FR algorithm
			}
		});

		recalcMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				recalcEvent();

			}
		});

		topologyMenu.setBackground(background);
		topologyMenu.setForeground(foreground);

		topologyMenu.add(layoutMenuItem);
		topologyMenu.add(complexLayoutMenuItem);
		// topologyMenu.add(dbMenuItem);
		topologyMenu.add(recalcMenuItem);
		topologyMenu.add(relaxMenuItem);
		topologyMenu.add(temperatureMenuItem);

	}

	// -----------------------------------------------------------------------------------------------------
	// Select Menu
	// -----------------------------------------------------------------------------------------------------

	JMenu selectMenu = new JMenu("Select");

	JMenuItem selectAllMenuItem = new JMenuItem("All");
	JMenuItem selectNoneMenuItem = new JMenuItem("None");
	JMenuItem selectNodeFromFileMenuItem = new JMenuItem("From file");
	JMenuItem invertFixMenuItem = new JMenuItem("Invert");
	JMenuItem xGradientColorMenuItem = new JMenuItem("X gradient");
	JMenuItem selectNodeByRegExpMenuItem = new JMenuItem("By name");
	JMenuItem addNodeMenuItem = new JMenuItem("Add nodes");

	/**
	 * Populate select menu
	 */
	private void populateSelectMenu() {

		if (verbosity > 4)
			System.out.println("Populate Select Menu");

		selectMenu.setMnemonic(KeyEvent.VK_S);

		selectNodeByRegExpMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				searchNodeEvent();
			}
		});
		selectNodeFromFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				selectNodeFromFileEvent();
			}
		});
		selectAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				stringletPanel.setFix(true);
			}
		});

		selectNoneMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				stringletPanel.setFix(false);
			}
		});
		invertFixMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				stringletPanel.invertFix();
			}
		});

		selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));

		selectNoneMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));

		selectMenu.setBackground(background);
		selectMenu.setForeground(foreground);

		selectMenu.add(selectAllMenuItem);
		selectMenu.add(selectNoneMenuItem);
		selectMenu.add(selectNodeByRegExpMenuItem);
		selectMenu.add(selectNodeFromFileMenuItem);
		selectMenu.add(invertFixMenuItem);
	}

	// -----------------------------------------------------------------------------------------------------
	// Manipulate Menu
	// -----------------------------------------------------------------------------------------------------

	// manipulate data menu
	JMenu manipulateMenu = new JMenu("Manipulate");

	// manipulate items
	JMenuItem clearGraphMenuItem = new JMenuItem("Clear graph");
	JMenuItem changeNodeShapeMenuItem = new JMenuItem("Change node shape");
	JMenuItem deleteNodeMenuItem = new JMenuItem("Delete nodes");
	JMenuItem cropMenuItem = new JMenuItem("Crop graph");
	JMenuItem randomGraphMenuItem = new JMenuItem("Random graph");
	JMenuItem editGraphMenuItem = new JMenuItem("Edit graph");

	// rotate sub menu
	JMenu rotateMenu = new JMenu("Rotate");

	// elements of rotate
	JMenuItem rotateRightMenuItem = new JMenuItem("90d clockwise");
	JMenuItem rotateLeftMenuItem = new JMenuItem("90d anti-clockwise");

	// flip sub menu
	JMenu flipMenu = new JMenu("Flip");

	// elements of flip
	JMenuItem flipXItem = new JMenuItem("Vertical");
	JMenuItem flipYItem = new JMenuItem("Horizontal");

	/**
	 * Populate Manipulate menu
	 */
	private void populateManipulateMenu() {

		if (verbosity > 4)
			System.out.println("Populate Manipulate Menu");

		manipulateMenu.setMnemonic(KeyEvent.VK_M);

		changeNodeShapeMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				setShapeEvent();
			}
		});

		deleteNodeMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				stopRelax();
				stringletPanel.removeFixedNodes();
				// stringletPanel.updateNodes();
				updateInfo();
				stringletPanel.repaint();

			}
		});

		cropMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				String confidence = JOptionPane.showInputDialog("Choose minimum confidence level:" + "", "1.0");
				if (confidence != null) {
					stringletPanel.crop(Double.parseDouble(confidence));
					updateInfo();
					stringletPanel.repaint();
				}
			}
		});

		randomGraphMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getRandomGraphEvent();
			}
		});

		clearGraphMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				clearGraphEvent();
			}
		});

		rotateRightMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				rotateEvent(Math.PI / 2);
			}
		});

		rotateLeftMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				rotateEvent(-Math.PI / 2);
			}
		});

		flipXItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				flipX();
			}
		});

		flipYItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				flipY();
			}
		});

		editGraphMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				editGraph();
			}
		});

		manipulateMenu.setForeground(foreground);
		manipulateMenu.setBackground(background);

		manipulateMenu.add(clearGraphMenuItem);
		manipulateMenu.add(changeNodeShapeMenuItem);
		manipulateMenu.add(deleteNodeMenuItem);
		manipulateMenu.add(cropMenuItem);
		manipulateMenu.add(randomGraphMenuItem);
		manipulateMenu.add(editGraphMenuItem);

		rotateMenu.add(rotateRightMenuItem);
		rotateMenu.add(rotateLeftMenuItem);

		flipMenu.add(flipXItem);
		flipMenu.add(flipYItem);

		manipulateMenu.add(rotateMenu);
		manipulateMenu.add(flipMenu);

	}

	// -----------------------------------------------------------------------------------------------------
	// File Menu
	// -----------------------------------------------------------------------------------------------------

	// Set up the File menu
	JMenu fileMenu = new JMenu("File");

	// element of menu
	JMenuItem openMenuItem = new JMenuItem("Open");
	JMenuItem addMenuItem = new JMenuItem("Add");
	JMenuItem saveMenuItem = new JMenuItem("Save");
	JMenuItem quitMenuItem = new JMenuItem("Quit");

	// import sub menu
	JMenu importMenu = new JMenu("Import");

	// element of import sub menu
	JMenuItem importTabbedMenuItem = new JMenuItem("Simple tabbed");
	JMenuItem importFromWebMenuItem = new JMenuItem("Import from web service");
	JMenuItem createFromWebMenuItem = new JMenuItem("Create graph");

	// export sub menu
	JMenu exportMenu = new JMenu("Export");

	// elements of export sub menu
	JMenuItem exportPajekMenuItem = new JMenuItem("Pajek"); // TODO is helpful?
	JMenuItem saveHTMLParametersMenuItem = new JMenuItem("HTML Parameters");

	// image sub menu of export
	JMenu exportImageMenu = new JMenu("Image");

	// elements of image sub sub menu
	JMenuItem exportJPGMenuItem = new JMenuItem("JPG");
	JMenuItem exportPNGMenuItem = new JMenuItem("PNG");

	// postscrips sub menu of export
	JMenu exportPostscriptMenu = new JMenu("PostScript");

	// elements of postscrips sub sub menu
	JMenuItem exportPSMenuItem = new JMenuItem("PS");
	JMenuItem exportEPSMenuItem = new JMenuItem("EPS");

	/**
	 * Populate File Menu
	 */
	protected void populateFileMenu() {

		if (verbosity > 4)
			System.out.println("Populate File Menu");

		fileMenu.setMnemonic(KeyEvent.VK_F);

		quitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				quitApplication();
			}
		});
		openMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				loadEvent(DataLoader.LOAD_NET);
			}
		});
		addMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				addGraphEvent();
			}
		});
		saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				saveEvent();
			}
		});
		saveHTMLParametersMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				saveHTMLParametersEvent();
			}
		});
		exportJPGMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				exportImageEvent(false);
			}
		}

		);
		exportPNGMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				exportImageEvent(true);
			}
		});
		exportPSMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				exportPSEvent();
			}
		});
		exportEPSMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				exportEPSEvent();
			}
		});
		exportPajekMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				exportPajekEvent();
			}
		});
		importTabbedMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				loadEvent(DataLoader.LOAD_TABBED);
			}
		});
		importFromWebMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				loadWebEvent();
			}
		});

		fileMenu.setBackground(background);
		fileMenu.setForeground(foreground);
		fileMenu.add(openMenuItem);
		// fileMenu.add(addMenuItem);

		// selectMenu.setForeground(Color.black);
		// import sub menu

		// importMenu.add(importTabbedMenuItem);
		// importMenu.add(importFromWebMenuItem);

		// fileMenu.add(importMenu);

		// export sub menu

		exportImageMenu.add(exportJPGMenuItem);
		exportImageMenu.add(exportPNGMenuItem);

		exportPostscriptMenu.add(exportPSMenuItem);
		exportPostscriptMenu.add(exportEPSMenuItem);

		exportMenu.add(exportImageMenu);
		exportMenu.add(exportPostscriptMenu);
		// exportMenu.add(exportPajekMenuItem);
		// exportMenu.add(saveHTMLParametersMenuItem);

		fileMenu.add(exportMenu);

		// save & quit
		fileMenu.add(saveMenuItem);
		fileMenu.add(quitMenuItem);
	}

	// -----------------------------------------------------------------------------------------------------
	// Interactions Menu
	// -----------------------------------------------------------------------------------------------------

	// interaction menu
	JMenu interactionMenu = new JMenu("Interaction");

	// interaction elements
	JMenuItem loadInteractionMenuItem = new JMenuItem("Load");
	JMenuItem hideShowMenuItem = new JMenuItem("Hide");

	// STRING sub menu
	JMenu stringDefaultMenu = new JMenu("STRING default");

	// elements of string submenu
	JMenuItem stringProteinInteractionMenuItem = new JMenuItem("Protein");
	JMenuItem stringCOGInteractionMenuItem = new JMenuItem("COGS");

	/**
	 * Populate Interaction Menu
	 */
	protected void populateInteractionMenu() {

		if (verbosity > 4)
			System.out.println("Populate Interaction Menu");

		interactionMenu.setMnemonic(KeyEvent.VK_I);

		loadInteractionMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				interactionEvent();
			}
		});
		stringProteinInteractionMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				proteinInteractionEvent();
			}
		});
		stringCOGInteractionMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				cogInteractionEvent();
			}
		});
		hideShowMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				hideShowInteractionsEvent();
			}
		});

		interactionMenu.setBackground(background);
		interactionMenu.setForeground(foreground);

		interactionMenu.add(loadInteractionMenuItem);
		interactionMenu.add(hideShowMenuItem);

		stringDefaultMenu.add(stringProteinInteractionMenuItem);
		stringDefaultMenu.add(stringCOGInteractionMenuItem);

		interactionMenu.add(stringDefaultMenu);

	}

	// -----------------------------------------------------------------------------------------------------
	// Display Menu
	// -----------------------------------------------------------------------------------------------------

	// display menu
	JMenu displayMenu = new JMenu("Display");

	// elements of display
	JCheckBoxMenuItem confidenceCBMItem = new JCheckBoxMenuItem("Confidence", true);
	JCheckBoxMenuItem prettyCBMItem = new JCheckBoxMenuItem("Interactions", false);
	JCheckBoxMenuItem namesCBMItem = new JCheckBoxMenuItem("Labels", true);
	JCheckBoxMenuItem showNamesCBMItem = new JCheckBoxMenuItem("Show names", true);
	JCheckBoxMenuItem directedCBMItem = new JCheckBoxMenuItem("Directed", false);
	JCheckBoxMenuItem hideWhenMoveCBMItem = new JCheckBoxMenuItem("Hide when move", false);
	JCheckBoxMenuItem alphaCBMItem = new JCheckBoxMenuItem("Alpha conf", true);

	/**
	 * Populate display menu
	 */

	protected void populateDisplayMenu() {

		if (verbosity > 4)
			System.out.println("Populate Display Menu");

		displayMenu.setMnemonic(KeyEvent.VK_D);

		prettyCBMItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK));

		prettyCBMItem.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				stringletPanel.setPretty(prettyCBMItem.isSelected());
				stringletPanel.repaint();
			}
		});

		namesCBMItem.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				stringletPanel.setLabel(namesCBMItem.isSelected());
				stringletPanel.repaint();
			}
		});

		showNamesCBMItem.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				stringletPanel.setShowName(showNamesCBMItem.isSelected());
				stringletPanel.repaint();
			}
		});

		confidenceCBMItem.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				stringletPanel.setConfidence(confidenceCBMItem.isSelected());
				stringletPanel.repaint();
			}
		});

		directedCBMItem.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				stringletPanel.setArrows(directedCBMItem.isSelected());
				stringletPanel.repaint();
			}
		});

		hideWhenMoveCBMItem.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				stringletPanel.setHideWhenMove(hideWhenMoveCBMItem.isSelected());
				stringletPanel.repaint();
			}
		});

		alphaCBMItem.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				stringletPanel.setAlpha(alphaCBMItem.isSelected());
				stringletPanel.repaint();
			}
		});

		displayMenu.setBackground(background);
		displayMenu.setForeground(foreground);

		displayMenu.add(prettyCBMItem);
		displayMenu.add(namesCBMItem);
		displayMenu.add(showNamesCBMItem);
		displayMenu.add(directedCBMItem);
		displayMenu.add(confidenceCBMItem);
		displayMenu.add(alphaCBMItem);
		displayMenu.add(hideWhenMoveCBMItem);
	}

	// -----------------------------------------------------------------------------------------------------
	// Help Menu
	// -----------------------------------------------------------------------------------------------------

	/*
	 * Set up the help menu
	 */
	JMenu helpMenu = new JMenu("Help");

	JMenuItem aboutMenuItem = new JMenuItem("About RadialTopologyViewer");

	protected void populateHelpMenu() {

		if (verbosity > 4)
			System.out.println("Populate Help Menu");

		aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				aboutEvent();
			}
		});

		helpMenu.setBackground(background);
		helpMenu.setForeground(foreground);

		helpMenu.add(aboutMenuItem);

	}

	/**
	 * a yes/no dialogue
	 *
	 * @param message
	 * @return yes/no
	 */
	private int yes_no(String message) {
		int s = JOptionPane.showConfirmDialog(this, message, "Confirm", JOptionPane.YES_NO_OPTION);

		return s;
	}

	/**
	 * Clear a loaded graph
	 */
	private void clearGraphEvent() {
		stopAllRelax();
		if (yes_no("Your data will be lost. Are you sure?") == 0) {
			stringletPanel.clearGraph();
			stringletPanel.repaint();
			updateInfo();
		}
	}

	/**
	 * Reset all layout modification
	 */
	private void recalcEvent() {
		stopAllRelax();
		if (yes_no(
				"This will return edge length \nand orientation to default values\n Are you sure you want to do this?") == 0) {
			stringletPanel.autoFixOrientation();
			stringletPanel.calculateEdgeLength();
			stringletPanel.repaint();

		}
	}

	/**
	 * Rotate graph of 'degrees' degree
	 *
	 * @param degrees
	 */
	private void rotateEvent(double degrees) {
		stopAllRelax();
		stringletPanel.rotate(degrees);
		stringletPanel.repaint();
	}

	/**
	 * Flip x,y
	 *
	 * @param x
	 * @param y
	 */
	private void flipEvent(boolean x, boolean y) {
		stopAllRelax();
		stringletPanel.mirror(x, y);
		stringletPanel.repaint();
	}

	/**
	 * Flip X
	 */
	private void flipX() {
		flipEvent(true, false);
	}

	/**
	 * Flip Y
	 */
	private void flipY() {
		flipEvent(false, true);
	}

	/**
	 * Edit Graph
	 */
	private void editGraph() {
		stopAllRelax();
		egd = new EditGraphDialog(this, true);
		egd.setGraph(stringletPanel.getGraph());
		egd.setVisible(true);
		Graph g = egd.getGraph();
		if (g != null) {

			stringletPanel.setGraph(g);

			stringletPanel.repaint();
		}
	}

	// default data connection
	NetQuantDataConnection mc = new netQuant.stringconnection.Main();

	private void stringConnection() {
		// TODO check that the connection works
		String nodes = getFixedNodesAsString();

		Graph graph = mc.getGraph(nodes, this);
		stringletPanel.addGraph(graph);
		stringletPanel.copyNodeSettings(graph);

		stringletPanel.repaint();
		updateInfo();
	}

	/**
	 * Warning message for connection with String
	 */
	// private void stringConnection(){
	// JOptionPane.showConfirmDialog(this,"STRING requires a license and is
	// not\navailable to open source",
	// "Disabled",JOptionPane.PLAIN_MESSAGE,JOptionPane.INFORMATION_MESSAGE);
	// }

	/**
	 * Show the version number in dialog
	 */
	private void aboutEvent() {
		AboutDialog.showDialog(this, null, getVersion().toString());
	}

	/**
	 * Set a shape
	 */
	private void setShapeEvent() {
		Object[] shapes = { "circle", "rectangle", "triangle", "diamond" };
		int s = JOptionPane.showOptionDialog(this, "Choose a node shape", "Shape", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, shapes, "circle");

		stringletPanel.setShape(s);

		return;
	}

	/**
	 * load image
	 */
	public void loadImageEvent() {
		stringletPanel.stop();
		relax.setSelected(false);
		FileDialog fdLoad = new FileDialog(this, "Load image", FileDialog.LOAD);
		fdLoad.setVisible(true);
		String loadFile = fdLoad.getFile();
		String dir = fdLoad.getDirectory();
		if (loadFile != null) {
			try {
				stringletPanel.setImage(dir + loadFile);
			} catch (IOException ie) {
				JOptionPane.showMessageDialog(this, "The image could not be found or opened", "Load Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		stringletPanel.repaint();
	}

	/**
	 * Stop Relaxation
	 */
	private void stopAllRelax() {
		stringletPanel.stop();
		relax.setSelected(false);
		controlPanel.repaint();
	}

	/**
	 * Clear Image
	 */
	public void clearImageEvent() {
		stopAllRelax();
		stringletPanel.clearImage();
		stringletPanel.repaint();
	}

	/**
	 * Quit application and attempting to save the settings
	 */
	public void quitApplication() {
		stopAllRelax();
		System.out.println("Closing application");
		try {
			saveSettings();
			System.out.println("Current settings saved");
		} catch (IOException ie) {
			System.out.println("...settings not saved due to file problems");
		}
		remove(stringletPanel);
		System.exit(0);
	}

	/**
	 * Load graph
	 *
	 * @param loadType
	 */

	private void loadEvent(int loadType) {

		stopAllRelax();
		// scaleOut();
		FileDialog fdLoad = new FileDialog(this, "Load graph", FileDialog.LOAD);
		if (lastDir != null)
			fdLoad.setDirectory(lastDir);
		fdLoad.setFilenameFilter(new DataFileFilter());
		fdLoad.setVisible(true);
		String loadFile = fdLoad.getFile();
		String dir = fdLoad.getDirectory();
		if (loadFile != null) {
			try {
				waitForLoad(dir + loadFile, loadType);
				lastDir = dir;
				lastFile = loadFile;
			} catch (IOException ie) {
				ie.printStackTrace();
				JOptionPane.showMessageDialog(this, "The file could not be found or opened", "Load Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (DataFormatException dfe) {
				JOptionPane.showMessageDialog(this, dfe.getMessage(), "Data Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Creates a graph from web services
	 *
	 * @throws InterruptedException
	 */
	private void loadWebEvent() {

		ConnectorManagerLite comMlite;
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnval = fc.showOpenDialog(this);
		if (returnval == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			comMlite = new ConnectorManagerLite(file);
			stringletPanel.addGraph(comMlite.getGraph());
			updateInfo();
			stringletPanel.repaint();
			setCursor(null);
		}

	}

	/**
	 * Generate a random graph
	 */
	private void getRandomGraphEvent() {
		int s = yes_no("This will generate a random graph. Are you sure?");
		// System.out.println(s);
		if (s == 0) {
			stopAllRelax();
			stringletPanel.randomGraph(30, 60);
			updateInfo();
			stringletPanel.repaint();
		}
	}

	/**
	 * Add a graph
	 */
	private void addGraphEvent() {
		stopAllRelax();
		scaleOut();
		FileDialog fdLoad = new FileDialog(this, "Add graph", FileDialog.LOAD);
		if (lastDir != null)
			fdLoad.setFile(lastDir + lastFile);
		fdLoad.setFilenameFilter(new DataFileFilter());
		fdLoad.setVisible(true);
		String loadFile = fdLoad.getFile();
		String dir = fdLoad.getDirectory();
		if (loadFile != null) {
			try {

				stringletPanel.appendGraph(dir + loadFile);

				lastDir = dir + loadFile;
				lastFile = loadFile;
				updateInfo();
				stringletPanel.repaint();
			} catch (IOException ie) {
				JOptionPane.showMessageDialog(this, "The file could not be found or opened", "Load Error",
						JOptionPane.ERROR_MESSAGE);

			} catch (DataFormatException dfe) {

				JOptionPane.showMessageDialog(this, dfe.getMessage(), "Data Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Save session
	 */
	private void saveEvent() {
		FileDialog fdSave = new FileDialog(this, "Save as", FileDialog.SAVE);
		// fdSave.setDirectory(fdDirectory);
		fdSave.setFilenameFilter(new DataFileFilter());
		fdSave.setVisible(true);
		String saveFile = fdSave.getFile();
		String dir = fdSave.getDirectory();
		if (saveFile != null) {
			lastDir = dir;
			lastFile = saveFile;
			try {
				stringletPanel.saveGraph(dir + saveFile);
			} catch (IOException ie) {
				JOptionPane.showMessageDialog(this, "Error saving file.", "Save error", JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	/**
	 * Save as HTML
	 */
	private void saveHTMLParametersEvent() {
		FileDialog fdSave = new FileDialog(this, "Save applet parameters to", FileDialog.SAVE);
		fdSave.setVisible(true);
		String saveFile = fdSave.getFile();
		dl = new DataLoader();
		// System.out.println(stringletPanel.getGraph().report());
		if (saveFile != null) {
			try {
				dl.saveHTMLParameters(stringletPanel.getGraph(), saveFile);

			} catch (IOException ie) {
				JOptionPane.showMessageDialog(this, "Error saving file.", "Save error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Export as EPS
	 */
	private void exportEPSEvent() {

		FileDialog fdSave = new FileDialog(this, "Save as EPS", FileDialog.SAVE);
		fdSave.setVisible(true);
		String saveFile = fdSave.getFile();
		String dir = fdSave.getDirectory();
		if (saveFile != null) {
			try {
				stringletPanel.writeEPS(dir + saveFile);
				JOptionPane.showMessageDialog(this, "Panel exported to " + saveFile, "EPS saved",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException ie) {
				JOptionPane.showMessageDialog(this, "Error saving file.", "Save error", JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	/**
	 * Esport as PS
	 */
	private void exportPSEvent() {

		FileDialog fdSave = new FileDialog(this, "Save as PS", FileDialog.SAVE);
		fdSave.setVisible(true);
		String saveFile = fdSave.getFile();
		String dir = fdSave.getDirectory();
		if (saveFile != null) {
			// lastDir=dir+saveFile;
			try {
				stringletPanel.writePS(dir + saveFile);
				JOptionPane.showMessageDialog(this, "Panel exported to " + saveFile, "PS saved",
						JOptionPane.INFORMATION_MESSAGE);
				// System.out.println("weng MedusaFrame exportPSEvent");
			} catch (IOException ie) {
				JOptionPane.showMessageDialog(this, "Error saving file.", "Save error", JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	/**
	 * Export as pajek
	 */
	private void exportPajekEvent() {
		FileDialog fdSave = new FileDialog(this, "Save as pajek", FileDialog.SAVE);
		fdSave.setVisible(true);
		String saveFile = fdSave.getFile();
		String dir = fdSave.getDirectory();
		if (saveFile != null) {
			try {
				stringletPanel.writePajek(dir + saveFile);
				JOptionPane.showMessageDialog(this, "Exported to pajek format " + saveFile, "Pajek",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException ie) {
				JOptionPane.showMessageDialog(this, "Error saving file.", "Save error", JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	/**
	 * Load interaction file
	 */
	private void interactionEvent() {
		FileDialog fdInter = new FileDialog(this, "Load interaction file", FileDialog.LOAD);
		fdInter.setVisible(true);
		String loadFile = fdInter.getFile();
		String dir = fdInter.getDirectory();
		if (loadFile != null) {
			try {
				lastInter = dir + loadFile;
				stringSettings.load(dir + loadFile);
				updateLegend();
				stringletPanel.updateStringSettings(stringSettings);
				legendPanel.repaint();
				// stringSettings.report();
			} catch (IOException ie) {
				// ie.printStackTrace();
				JOptionPane.showMessageDialog(this,
						"Interaction settings could not be loaded.\n" + "Check if the file is valid.", "Load error",
						JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	/**
	 * Export image as png(true) or jpg(false)
	 *
	 * @param png
	 */
	private void exportImageEvent(boolean png) {
		String name = "network." + ((png) ? "png" : "jpg");
		FileDialog fdSave = new FileDialog(this, "Save as" + ((png) ? "png" : "jpg"), FileDialog.SAVE);
		fdSave.setFile(name);
		fdSave.setVisible(true);
		String saveFile = fdSave.getFile();
		String dir = fdSave.getDirectory();
		int type = (png) ? 1 : 0;

		stringletPanel.saveImage(dir + saveFile, type);
		JOptionPane.showMessageDialog(this, "Panel exported to " + saveFile, "Image saved",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * modify colors - switch
	 */
	private void switchColorEvent() {
		int[] colors = ColorJDialog.showDialog(f, null, "switch with");
		stringletPanel.manipulateColor(colors[0], colors[1], true);
		stringletPanel.repaint();

	}

	/**
	 * modify colors - copy to
	 */
	private void copyColorEvent() {
		int[] colors = ColorJDialog.showDialog(f, null, "copy to");
		stringletPanel.manipulateColor(colors[0], colors[1], false);
		stringletPanel.repaint();
	}

	/**
	 * modify colors - change node color
	 */
	private void changeColorEvent() {
		Color newColor = JColorChooser.showDialog(getRootPane(), "Choose node color", Color.red);
		if (newColor != null) {
			stringletPanel.changeNodeColor(newColor);

			stringletPanel.repaint();
		}
	}

	/**
	 * modify color - change label color
	 */
	private void setLabelColorEvent() {
		Color newColor = JColorChooser.showDialog(getRootPane(), "Choose label color", Color.black);
		if (newColor != null) {
			stringletPanel.setFontColor(newColor);

			stringletPanel.repaint();
		}
	}

	/**
	 * change background color
	 */
	private void setBackgroundColorEvent() {
		Color newColor = JColorChooser.showDialog(getRootPane(), "Choose label color", Color.white);
		if (newColor != null) {
			stringletPanel.setBackgroundColor(newColor);

			stringletPanel.repaint();
		}
	}

	/**
	 * set basic edge color
	 */
	private void setBasicEdgeColorEvent() {
		Color newColor = JColorChooser.showDialog(getRootPane(), "Choose label color", Color.gray);
		if (newColor != null) {
			stringletPanel.setBasicEdgeColor(newColor);

			stringletPanel.repaint();
		}
	}

	/**
	 * change node dimension
	 */
	private void changeNodeSizeEvent() {

		String n = JOptionPane.showInputDialog("Mark node matching regular expression:\n" + "e.g. \"mito.*\"", "");

		String s = JOptionPane.showInputDialog("Enter node size in pixels:\n" + "Default value is 10", "");
		int newSize = -1; // default if something goes wrong
		if ((s != null) && (s.length() > 0)) {
			try {
				newSize = Integer.parseInt(s);
				stringletPanel.setNodeSize(newSize, n);
			} catch (NumberFormatException ne) {
				JOptionPane.showMessageDialog(this, "Incorrect input", "An integer is required for this action",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		stringletPanel.repaint();
	}

	/**
	 * change nodes dimension
	 */
	private void changeNodesSizeEvent() {

		String s = JOptionPane.showInputDialog("Enter nodes size in pixels:\n" + "Default value is 10", "");
		int newSize = -1; // default if something goes wrong
		if ((s != null) && (s.length() > 0)) {
			try {
				newSize = Integer.parseInt(s);
				stringletPanel.setNodesSize(newSize);
			} catch (NumberFormatException ne) {
				JOptionPane.showMessageDialog(this, "Incorrect input", "An integer is required for this action",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		stringletPanel.repaint();
	}

	/**
	 * change font size
	 */
	private void changeFontSizeEvent() {
		String s = JOptionPane.showInputDialog("Enter font size:\n" + "Default value is 10", "");
		if ((s != null) && (s.length() > 0)) {
			try {
				int newSize = Integer.parseInt(s);
				stringletPanel.changeFont(newSize);
			} catch (NumberFormatException ne) {
				JOptionPane.showMessageDialog(this, "Incorrect input", "An integer is required for this action",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		stringletPanel.repaint();
	}

	/**
	 * search for a node
	 */
	private void searchNodeEvent() {
		String s = JOptionPane.showInputDialog("Mark node matching regular expression:\n" + "e.g. \"mito.*\"", "");

		if (s != null)
			if (!stringletPanel.findLabel(s))
				JOptionPane.showMessageDialog(this, "No match found", "No nodes matching your pattern were found",

						JOptionPane.ERROR_MESSAGE);
		stringletPanel.repaint();
	}

	/**
	 * select a node from a node list
	 */
	private void selectNodeFromFileEvent() {
		FileDialog fdSelect = new FileDialog(this, "Load node list", FileDialog.LOAD);
		if (lastDir != null)
			fdSelect.setDirectory(lastDir);
		fdSelect.setVisible(true);
		String loadFile = fdSelect.getFile();
		String dir = fdSelect.getDirectory();
		// lastDir=dir;

		try {
			stringletPanel.selectNodeFromFile(dir + loadFile);
			stringletPanel.repaint();
		} catch (IOException sNFFe) {
			JOptionPane.showMessageDialog(this, sNFFe.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);

		}
	}

	/**
	 * stop relax
	 */
	public void stopRelax() {
		stringletPanel.stop();
		if (relax.isSelected())
			relax.setSelected(false);

	}

	/**
	 * item state changed
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		// handleClassStateChange(e);
	}

	public void handleClassStateChange(ItemEvent e) {
		Object src = e.getSource();
		handleToggle(src);
	}

	public void handleToggle(Object src) {
		System.out.println("debug line 1522 event handled");
		ShowEdgePanel se = (ShowEdgePanel) src;
		System.out.println(se.getNumber());

	}

	public void handleEdgeEvent(int number, boolean selected) {

		stringletPanel.setShowEdge(number - 1, selected);
		stringletPanel.repaint();
	}

	/**
	 * handle component resize
	 */
	@Override
	public void componentResized(ComponentEvent e) {
	}

	double oldScale = 1.0;
	double scale = 1.0;

	/**
	 * handle state change
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider src = (JSlider) e.getSource();
		if (!src.getValueIsAdjusting()) {

			Dimension d = stringletPanel.getSize();
			// Dimension d = jScrollPane.getSize();
			double scale = (double) src.getValue() / 100;
			stringletPanel.setScale(scale / oldScale);
			// stringletPanel.setScale(scale);

			stringletPanel.setTimeFrameXY(d);
			oldScale = scale;

			stringletPanel.repaint();
			stringletPanel.revalidate();
			jScrollPane.revalidate();

		}
	}

	/**
	 * reset to minimal zoom
	 */
	public void scaleOut() {
		scalerSlider.setValue(0);
		double scale = 1.0;
		stringletPanel.setScale(scale / oldScale);
		Dimension d = stringletPanel.getSize();
		stringletPanel.setTimeFrameXY(d);
		oldScale = scale;

		stringletPanel.repaint();
		stringletPanel.revalidate();
		jScrollPane.revalidate();
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	/**
	 * save settings
	 */
	public void saveSettings() throws IOException {
		// System.out.println(lastDir);
		if (lastDir != null) {
			File saveFile = new File("NetViewSettings.ini");
			FileWriter out = new FileWriter(saveFile);
			out.write("LASTDIR=");
			out.write(lastDir + "\n");

			// write interaction file
			if (lastInter != null) {
				out.write("INTERACT=");
				out.write(lastInter + "\n");
			}
			out.close();
		}
	}

	String lastDir = null;
	String lastFile = null;
	String lastInter = null;

	/**
	 * load settings
	 *
	 * @return true if setting loaded
	 * @throws IOException
	 */
	private boolean loadSettings() throws IOException {
		File loadFile = new File("NetViewSettings.ini");
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(loadFile)));
			String inLine = in.readLine();
			if (inLine == null) {
				System.out.println("Error in NetViewSettings.ini");
				return false;
			}
			// get last dir
			Pattern dirPattern = Pattern.compile("LASTDIR=(.+)");
			Matcher matcher = dirPattern.matcher(inLine);
			// System.out.println(inLine);
			if (matcher.find()) {
				lastDir = matcher.group(1);
				// System.out.println("Reading line "+lastDir);

			} else {
				lastDir = System.getProperty("user.dir");
				// System.out.println("returning false from loadsettings");
				return false;
			}
			// get last interaction file
			inLine = in.readLine();
			if (inLine != null) {
				Pattern interPattern = Pattern.compile("INTERACT=(\\w+)");
				matcher = interPattern.matcher(inLine);
				if (matcher.find())
					lastInter = matcher.group(1);
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return true;
	}

	/**
	 * put nodes in a String for DataConnection
	 */
	private String getFixedNodesAsString() {
		StringBuffer sb = new StringBuffer();
		sb.append("");
		java.util.ArrayList<Node> nodeArray = stringletPanel.getFixed();
		for (java.util.Iterator<Node> i = nodeArray.iterator(); i.hasNext();) {
			Node n = i.next();
			sb.append(n.getLabel());
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * show protein interactions
	 */
	private void proteinInteractionEvent() {
		stringSettings.clear();
		stringSettings.init();
		updateLegend();
		stringletPanel.updateStringSettings(stringSettings);
		legendPanel.repaint();
	}

	/**
	 * show COG interactions
	 */
	private void cogInteractionEvent() {
		stringSettings.clear();
		stringSettings.initCOGS();
		updateLegend();
		stringletPanel.updateStringSettings(stringSettings);
		legendPanel.repaint();

	}

	/**
	 * Set edge length
	 */
	private void setEdgeLenEvent() {
		int len = (int) stringletPanel.getEdgeLen();
		String s = JOptionPane.showInputDialog("Enter desired edge length in pixels:\n" + "Set to 0 for default length",
				String.valueOf(len));
		if ((s != null) && (s.length() > 0)) {
			try {
				int newSize = Integer.parseInt(s);
				stringletPanel.setEdgeLen(newSize);
			} catch (NumberFormatException ne) {
				JOptionPane.showMessageDialog(this, "Incorrect input", "An integer is required for this action",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// testing here
	ProgressMonitor loader;
	Timer timer;
	DataLoader dl;

	/**
	 * Handle the loading wait
	 *
	 * @param fileName
	 * @param loadType
	 * @throws IOException
	 * @throws DataFormatException
	 */
	public void waitForLoad(String fileName, int loadType) throws IOException, DataFormatException {

		dl = new DataLoader(600, 600, fileName);
		dl.setLoadType(loadType);
		timer = new javax.swing.Timer(500, runLoad);

		dl.start();
		timer.start();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	/**
	 * manage the action listener
	 */
	private final ActionListener runLoad = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			infoLabel.setText(dl.getStatus() + " " + dl.getProgress());
			// loader.setProgress(0);
			// System.out.println(dl.getProgress());
			if (dl.isDone()) {
				timer.stop();
				dl.stop();
				// loader.close();
				// loader.setProgress(0);
				infoLabel.setText("Cleaning graph");
				setCursor(null);
				stringletPanel.setGraph(dl.getGraph());
				updateInfo();
				updateFileInfo();
				stringletPanel.repaint();
			}
		}
	};

	private boolean hideInteractions = false;

	/**
	 * hide/show interactions
	 */
	private void hideShowInteractionsEvent() {
		Container content = getContentPane();
		if (!hideInteractions) {
			content.remove(scalerPanel);
			hideShowMenuItem.setText("Show");
			hideInteractions = true;
		} else {
			content.add("East", scalerPanel);

			hideShowMenuItem.setText("Hide");
			hideInteractions = false;
		}
		validate();

	}

	private void analyze() {

		stringletPanel.analyzeGraph();
		stringletPanel.repaint();

	}

	/**
	 * this is the function to give the order to the nodes
	 */
	private void setRadialLegendOrder() {
		try {
			loadSettings();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		JFileChooser fc = new JFileChooser(lastDir);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnval = fc.showOpenDialog(this);
		if (returnval == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			BufferedReader dis = null;

			try {
				updateLastFile(file);
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				dis = new BufferedReader(new InputStreamReader(bis));
				Vector<String> data1 = new Vector<String>();

				data1.add(file.getName());

				String data = null;
				while ((data = dis.readLine()) != null) {

					data1.add(data);
				}
				stringletPanel.setProteinOnthology(data1);
				JOptionPane.showMessageDialog(this, "Radial order loaded properly", "Radial order",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, "File not found", "Data Error", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "The file could not be found or opened", "Load Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Some error occurred reading file", "Load Error",
						JOptionPane.ERROR_MESSAGE);
			} finally {
				if (dis != null) {
					try {
						dis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	/**
	 * this is the function that parse and load the information for the map
	 */
	private void radial() {

		try {
			loadSettings();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		JFileChooser fc = new JFileChooser(lastDir);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnval = fc.showOpenDialog(this);
		if (returnval == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			BufferedReader dis = null;
			try {
				updateLastFile(file);
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				dis = new BufferedReader(new InputStreamReader(bis));
				Vector<String> data1 = new Vector<String>();

				data1.add(file.getName());

				String data = null;
				while ((data = dis.readLine()) != null) {

					data1.add(data);
				}

				String root = stringletPanel.setRadialData(data1);
				stringletPanel.radial(getWidth(), getHeight(), root);
				stringletPanel.repaint();
				JOptionPane.showMessageDialog(this, "Input file loaded properly", "Input file loaded",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, "File not found", "Data Error", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "The file could not be found or opened", "Load Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Some error ocurred during reading input file:\n" + e.getMessage(),
						"Load Error", JOptionPane.ERROR_MESSAGE);
			} finally {
				if (dis != null) {
					try {
						dis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void updateLastFile(File file) throws IOException {
		if (file.isFile()) {
			lastFile = file.getAbsolutePath();
			lastDir = file.getParent();
			saveSettings();
		}

	}

	private void reset() {

		stringletPanel.resetGraph();
		stringletPanel.repaint();

	}

	private void loadQuantData() {

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnval = fc.showOpenDialog(this);
		if (returnval == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			BufferedReader dis = null;
			try {
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				dis = new BufferedReader(new InputStreamReader(bis));
				Vector<String> data1 = new Vector<String>();
				String data = null;
				while ((data = dis.readLine()) != null) {

					data1.add(data);
				}

				stringletPanel.setNodesQuantData(data1);
				JOptionPane.showMessageDialog(this, "Quant data set correctly", "Quant data loaded",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, "File not found", "Data Error", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "The file could not be found or opened", "Load Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error loading input file:\n" + e.getMessage(), "Load Error",
						JOptionPane.ERROR_MESSAGE);
			} finally {
				if (dis != null) {
					try {
						dis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void dynoAnalysis() {

		stringletPanel.computeDifferentialNetwork();
		stringletPanel.computeDifferentialEdges();
		stringletPanel.repaint();

	}

	private void loadBaitData() {

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnval = fc.showOpenDialog(this);
		if (returnval == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			BufferedReader dis = null;
			try {
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				dis = new BufferedReader(new InputStreamReader(bis));
				Vector<String> data1 = new Vector<String>();
				String data = null;
				while ((data = dis.readLine()) != null) {

					data1.add(data);
				}

				// stringletPanel.setBaitsQuantData(data1);

			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, "File not found", "Data Error", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "The file could not be found or opened", "Load Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error reading bait input file:\n" + e.getMessage(), "Load Error",
						JOptionPane.ERROR_MESSAGE);
			} finally {
				if (dis != null) {
					try {
						dis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

	private void loadExp() {
		stringletPanel.getExp();
		stringletPanel.repaint();

	}

	private void loadControl() {
		stringletPanel.getControl();
		stringletPanel.repaint();

	}

	private void loadInput() {
		stringletPanel.getInput();
		stringletPanel.repaint();

	}

}
