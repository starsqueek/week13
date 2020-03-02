package ui;

import counter.CounterException;
import counter.ThreadHashSet;
import counter.ThreadSet;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Paths;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * Display for counters.
 *
 * Undocumented.
 *
 * @author Hugh Osborne
 * @version January 2020
 */
public class CountersUI {

//    private final static CountersUI listener = new CountersUI();

    private static int MAX_DELAY_COUNT = 100;
    private static double MAX_DELAY_SECONDS = 5, INITIAL_DELAY_SECONDS = 2.5;

    private static int countersActive = 0;

    private static ThreadSet<Counter> predefinedCounters = new ThreadHashSet<Counter>();

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    private static JFileChooser fileChooser = new JFileChooser();
        private static File traceDirectory = null;
        private static String traceFileName = null;
        private static final String traceExt = "trc";
        static class TraceFilter extends javax.swing.filechooser.FileFilter {

            public String getDescription() {
                return "AbstractCounter traces";
            }

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() || getFileExtension(pathname).equals(traceExt);
            }
        }
        private static javax.swing.filechooser.FileFilter traceFilter = new TraceFilter();

        private static File configDirectory = null;
        private static String configFileName = null;
        private static final String configExt = "cnt";
        static class ConfigFilter extends javax.swing.filechooser.FileFilter {

            public String getDescription() {
                return "AbstractCounter configurations";
            }

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() || getFileExtension(pathname).equals(configExt);
            }
        }
        private static javax.swing.filechooser.FileFilter configFilter = new ConfigFilter();

    private static ThreadSet<Counter> counters = new ThreadHashSet<Counter>();

    private static JFrame frame = new JFrame("Counters");

    private static CounterDisplay line = new CounterDisplay();

    private static JMenuBar menubar = new JMenuBar();
        private static JMenu configMenu = new JMenu("Configuration");
            private static JMenu show = new JMenu("Show...");
                private static JMenu showRunSet = new JMenu("... run set");
                private static JMenu showPredefined = new JMenu("... predefined counters");
            private static JMenu addCounter = new JMenu("Add counter");
                private static JMenu predefined = new JMenu("Use predefined counter");
                private static JMenuItem newCounter = new JMenuItem("Create new counter");
            private static JMenu removeMenu = new JMenu("Remove counter...");
                private static JMenu removeFromRunSet = new JMenu("... from run set");
                    private static JMenuItem removeAllRunSet = new JMenuItem("Remove all");
                private static JMenu removeFromPredefineds = new JMenu("... from predefined counters");
                    private static JMenuItem removeAllPredefineds = new JMenuItem("Remove all");
            private static JMenuItem loadConfig = new JMenuItem("Load configuration");
            private static JMenuItem reloadConfig = new JMenuItem("Reload last configuration");
            private static JMenuItem saveConfig = new JMenuItem("Save configuration");
            private static JMenuItem saveConfigAs = new JMenuItem("Save configuration as");
        private static JMenu traceMenu = new JMenu("Trace");
            private static JRadioButton trace = new JRadioButton("Trace On/Off",true);
            private static JMenuItem saveTraceAs = new JMenuItem("Save trace as");
            private static JMenuItem saveTrace = new JMenuItem("Save trace");
        private static JButton run = new JButton("Run");
        private static JButton stop = new JButton("Stop");

    private static JPanel downers = new JPanel();
    private static JPanel uppers = new JPanel();

        protected static JTextArea traceArea = new JTextArea(20,50);
    private static JScrollPane traceScroll = new JScrollPane(traceArea);

    private static JSlider speed = new JSlider(0,MAX_DELAY_COUNT-1,delayToSliderValue(INITIAL_DELAY_SECONDS));
        private static double sliderValueToDelay(int sliderValue) {
            return (MAX_DELAY_COUNT - sliderValue)/MAX_DELAY_SECONDS;
        }
        private static int delayToSliderValue(double delay) {
            return (int) (MAX_DELAY_COUNT * (1 - (INITIAL_DELAY_SECONDS/MAX_DELAY_SECONDS)));
        }


    private static void createAndShowGUI() throws CounterException, InterruptedException {
        //Create and set up the window.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLocationRelativeTo(null);
        frame.setJMenuBar(menubar);

        menubar.add(configMenu);
            configMenu.add(show);
            show.setEnabled(false);
                show.add(showRunSet);
                showRunSet.setEnabled(false);
                show.add(showPredefined);
                showPredefined.setEnabled(false);
            configMenu.add(addCounter);
                addCounter.add(predefined);
                addCounter.add(newCounter);
                newCounter.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            Counter counter = new Counter();
                            String[] options = {"Add to run set","Add to predefined"};
                            int selection = JOptionPane.showOptionDialog(frame,"Add " + counter.getName() + " to run set, or to predefined counters?","New counter",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
                            if (selection == JOptionPane.YES_OPTION) {
                                addToRunSet(counter,true);
                            } else {
                                addToPredefineds(counter,true);
                            }
                        } catch (CounterException ex) {
                          JOptionPane.showMessageDialog(frame, "Error creating counter.\n" + ex.getMessage() + "\nAbstractCounter not created.", "AbstractCounter creation error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            configMenu.add(removeMenu);
            removeMenu.setEnabled(true);
                removeMenu.add(removeFromRunSet);
                removeFromRunSet.setEnabled(false);
                    removeFromRunSet.add(removeAllRunSet);
                        removeAllRunSet.setEnabled(false);
                        removeAllRunSet.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                removeAllFromRunSet("Please confirm that you wish to clear the current run set.");
                            }
                        });
                removeMenu.add(removeFromPredefineds);
                removeFromPredefineds.setEnabled(true);
                    removeFromPredefineds.add(removeAllPredefineds);
                        removeAllPredefineds.setEnabled(true);
                        removeAllPredefineds.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                removeAllFromPredefineds("Please confirm that you wish to clear all predefined counters");
                            }
                        });
                    for (Counter counter: predefinedCounters) {
                        addToPredefineds(counter,false);
                    }
            configMenu.add(loadConfig);
                loadConfig.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        readConfigFile(false);
                    }
                });
            configMenu.add(reloadConfig);
                reloadConfig.setEnabled(false);
                reloadConfig.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        readConfigFile(true);
                    }
                });
            configMenu.add(saveConfig);
                saveConfig.setEnabled(false);
                saveConfig.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveConfigFile(false);
                    }
                });
            configMenu.add(saveConfigAs);
                saveConfigAs.setEnabled(false);
                saveConfigAs.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveConfigFile(true);
                    }
                });

        menubar.add(traceMenu);
            Counter.traceOn();
            traceMenu.add(trace);
                trace.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Counter.toggleTrace();
                    }
                });
            traceMenu.add(saveTrace);
                saveTrace.setEnabled(false);
                saveTrace.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveTraceFile(false);
                    }
                });
            traceMenu.add(saveTraceAs);
                saveTraceAs.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveTraceFile(true);
                    }
                });

        menubar.add(run);
            run.setEnabled(false);
            run.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    traceArea.setText(null);
                    Counter.resetTraceLineNumber();
                    counters.startSet();
                }
            });
        menubar.add(stop);
            stop.setEnabled(false);
            stop.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    stop.setEnabled(false);
                    run.setEnabled(false);
                    for (Counter counter: counters) {
                        counter.stopCounter();
                    }
                }
            });

        JMenuBar valueBar = new JMenuBar();
        valueBar.add(new JLabel("Value"));
        valueBar.add(line);
            line.setPaintTicks(true);
            line.setPaintLabels(true);
            line.setPaintTrack(false);
        frame.getContentPane().add(valueBar, BorderLayout.NORTH);

        downers.setLayout(new GridLayout(0,1));
        frame.getContentPane().add(downers,BorderLayout.WEST);
        uppers.setLayout(new GridLayout(0,1));
        frame.getContentPane().add(uppers,BorderLayout.EAST);

        traceArea.setEditable(false);
            DefaultCaret caret = (DefaultCaret) traceArea.getCaret();
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        traceScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        traceScroll.setWheelScrollingEnabled(true);
        frame.add(traceScroll,BorderLayout.CENTER);

        JMenuBar speedBar = new JMenuBar();
        speedBar.add(new JLabel("Speed"));
        speedBar.add(speed);
            Counter.setDelay(sliderValueToDelay(speed.getValue()));
            Dictionary<Integer,JLabel> labels = new Hashtable<>();
            labels.put(0,new JLabel("Slow"));
            labels.put(MAX_DELAY_COUNT/2,new JLabel("Medium"));
            labels.put(MAX_DELAY_COUNT-1,new JLabel("Fast"));
            speed.setLabelTable(labels);
            speed.setPaintLabels(true);
            speed.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    try {
                        Counter.setDelay(sliderValueToDelay(speed.getValue()));
                    } catch (CounterException error) {}
                }
            });
        frame.getContentPane().add(speedBar,BorderLayout.SOUTH);


        //Display the window.
        frame.pack();
        frame.setVisible(true);

    }

    protected static JMenu getRemoveFromRunSet() {
        return removeFromRunSet;
    }

    protected static void addToPredefineds(Counter counter,boolean report) {
        if (predefinedCounters.add(counter)) {
            predefined.add(counter.getAddFromPredefined());
            predefined.setEnabled(true);
            showPredefined.add(counter.getShowInPredefined());
            showPredefined.setEnabled(true);
            show.setEnabled(true);
            removeFromPredefineds.add(counter.getRemoveFromPredefineds());
            removeMenu.setEnabled(true);
            removeFromPredefineds.setEnabled(true);
            removeAllPredefineds.setEnabled(true);
            if (report) JOptionPane.showMessageDialog(frame, "AbstractCounter \"" + counter.getName() + "\" added to predefined counters.", "Predefined counter added", JOptionPane.PLAIN_MESSAGE);
        }
    }

    protected static void addToRunSet(Counter counter, boolean report) {
        if (counters.add(counter)) {
            calculateRange();
            run.setEnabled(true);
            saveConfigAs.setEnabled(true);
            removeAllRunSet.setEnabled(true);
            removeMenu.setEnabled(true);
            removeFromRunSet.add(counter.getRemoveFromRunSet());
            removeFromRunSet.setEnabled(true);
            showRunSet.add(counter.getShowInRunSet());
            showRunSet.setEnabled(true);
            show.setEnabled(true);
            if (counter.getStep() < 0) {
                downers.add(counter.getIcon());
            } else {
                uppers.add(counter.getIcon());
            }
            if (report) JOptionPane.showMessageDialog(frame, "AbstractCounter \"" + counter.getName() + "\" added to run set.", "Run set counter added", JOptionPane.PLAIN_MESSAGE);
        }
    }

    protected static void removeFromPredefineds(Counter counter,boolean report) {
        if (predefinedCounters.remove(counter)) {
            predefined.remove(counter.getAddFromPredefined());
            removeFromPredefineds.remove(counter.getRemoveFromPredefineds());
            showPredefined.remove(counter.getShowInPredefined());
            if (predefinedCounters.size() == 0) {
                removeAllPredefineds.setEnabled(false);
                removeFromPredefineds.setEnabled(false);
                showPredefined.setEnabled(false);
                if (counters.size() == 0) {
                    removeMenu.setEnabled(false);
                }
            }
            if (report) JOptionPane.showMessageDialog(frame, "AbstractCounter \"" + counter.getName() + "\" removed from predefined counters.", "Predefined counter removed", JOptionPane.PLAIN_MESSAGE);
        }
    }

    protected static void removeFromRunSet(Counter counter, boolean report) {
        if (counters.remove(counter)) {
           calculateRange();
           if (counter.getStep() < 0) {
               downers.remove(counter.getIcon());
               downers.validate();
               downers.repaint();
           } else {
               uppers.remove(counter.getIcon());
               uppers.validate();
               uppers.repaint();
           }
           showRunSet.remove(counter.getShowInRunSet());
           removeFromRunSet.remove(counter.getRemoveFromRunSet());
           if (counters.size() == 0) {
              run.setEnabled(false);
              saveConfig.setEnabled(false);
              saveConfigAs.setEnabled(false);
              showRunSet.setEnabled(false);
              removeFromRunSet.setEnabled(false);
              removeAllRunSet.setEnabled(false);
              if (predefinedCounters.size() == 0) {
                  removeMenu.setEnabled(false);
                  show.setEnabled(false);
              }
           }
           if (report) JOptionPane.showMessageDialog(frame, "AbstractCounter \"" + counter.getName() + "\" removed from run set.", "Run set counter removed", JOptionPane.PLAIN_MESSAGE);
        }
    };

    private static void calculateRange() {
        int minValue, maxValue, minStep, maxStep;
        if (counters.size() == 0) {
            minValue = 0;
            maxValue = 0;
            minStep = 0;
            maxStep = 0;
        } else {
            minValue = Integer.MAX_VALUE;
            maxValue = Integer.MIN_VALUE;
            minStep = 0;
            maxStep = 0;
            for (Counter counter: counters) {
                if (counter.getFrom() < minValue) {
                    minValue = counter.getFrom();
                }
                if (counter.getLimit() < minValue) {
                    minValue = counter.getLimit();
                }
                if (counter.getFrom() > maxValue) {
                    maxValue = counter.getFrom();
                }
                if (counter.getLimit() > maxValue) {
                    maxValue = counter.getLimit();
                }
                if (counter.getStep() < minStep) {
                    minStep = counter.getStep();
                }
                if (counter.getStep() > maxStep) {
                    maxStep = counter.getStep();
                }
            }
        }
        line.rangeChanged(minValue+minStep,maxValue+maxStep);
    }

    private static void saveTraceFile(boolean saveAs) {
        try {
            fileChooser.removeChoosableFileFilter(configFilter);
            fileChooser.addChoosableFileFilter(traceFilter);
            File traceFile = getFile(traceDirectory,traceFileName,traceExt,saveAs,true);
            if (getFileExtension(traceFile).isEmpty()) {
                File withExt = new File(traceFile.getParent(),traceFile.getName()+"."+traceExt);
                String[] options = new String[2];
                options[0] = traceFile.getName();
                options[1] = withExt.getName();
                String selectedFile = (String) JOptionPane.showInputDialog(null,"Selected filename has no filename extension.\nDo you want to save to a file without or with filename extension?","Save trace",JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
                if (selectedFile != null && selectedFile.equals(withExt.getName())) {
                    traceFile = withExt;
                 }
            }
            saveFile(traceArea.getText(), traceFile);
            traceDirectory = traceFile.getParentFile();
            traceFileName = traceFile.getName();
            saveTrace.setEnabled(true);
            saveTraceAs.setEnabled(true);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(fileChooser, "Trace file not saved.\n" + exception.getMessage(), "Save trace file", JOptionPane.ERROR_MESSAGE);
        } catch (NullPointerException exception)  {
            JOptionPane.showMessageDialog(fileChooser, "Trace file not saved.\nNo file chosen.", "Save trace file", JOptionPane.ERROR_MESSAGE);
        }
    }

    private final static String PREDEFINED_TAG = "Predefined",
                                RUN_SET_TAG = "RunSet",
                                END_TAG = "End",
                                COUNTER_TAG = "Counter";

    private static void saveConfigFile(boolean saveAs) {
        try {
            fileChooser.removeChoosableFileFilter(traceFilter);
            fileChooser.addChoosableFileFilter(configFilter);
            File configFile = getFile(configDirectory,configFileName,configExt,saveAs,true);
            if (getFileExtension(configFile).isEmpty()) {
                File withExt = new File(configFile.getParent(),configFile.getName()+"."+configExt);
                String[] options = new String[2];
                options[0] = withExt.getName();
                options[1] = configFile.getName();
                String selectedFile = (String) JOptionPane.showInputDialog(null,"Selected filename has no filename extension.\nDo you want to save to a file without or with filename extension?","Save trace",JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
                if (selectedFile != null && selectedFile.equals(withExt.getName())) {
                    configFile = withExt;
                }
            }
            StringBuilder builder = new StringBuilder();
            builder.append(PREDEFINED_TAG + "\n");
            for (Counter counter: predefinedCounters) {
                builder.append("\t " + COUNTER_TAG + "\t " + counter.getFrom()+"\t "+counter.getLimit()+"\t "+counter.getStep()+"\t "+counter.getName()+"\n");
            }
            builder.append(RUN_SET_TAG + "\n");
            for (Counter counter: counters) {
                builder.append("\t " + COUNTER_TAG + "\t " + counter.getFrom()+"\t "+counter.getLimit()+"\t "+counter.getStep()+"\t "+counter.getName()+"\n");
            }
            builder.append(END_TAG + "\n");
            String config = builder.toString();
            saveFile(config,configFile);
            configDirectory = configFile.getParentFile();
            configFileName = configFile.getName();
            saveConfig.setEnabled(true);
            saveConfigAs.setEnabled(true);
            reloadConfig.setEnabled(true);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(fileChooser, "Configuration file not saved.\n" + exception.getMessage(), "Save configuration file", JOptionPane.ERROR_MESSAGE);
        } catch (NullPointerException exception)  {
            JOptionPane.showMessageDialog(fileChooser, "Configuration file not saved.\nNo file chosen.", "Save configuration file", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void readConfigFile(boolean reload) {
        try {
            fileChooser.removeChoosableFileFilter(traceFilter);
            fileChooser.addChoosableFileFilter(configFilter);
            File configFile = getFile(configDirectory,configFileName,configExt,!reload,false);
            if (counters.size() == 0 || removeAll("Loading a new configuration requires clearing the current configuration.\nDo you wish to do this?")) {
                Scanner scanner = null;
                scanner = new Scanner(configFile);
                String next = scanner.next();
                if (!next.equals(PREDEFINED_TAG)) {
                    JOptionPane.showMessageDialog(fileChooser, "Parse error loading configuration file.\nEncountered \"" + next + "\" expected \"" + PREDEFINED_TAG + "\"", "Load configuration", JOptionPane.PLAIN_MESSAGE);
                    removeAllFromPredefineds(null);
                    return;
                }
                next = scanner.next();
                while (next.equals(COUNTER_TAG)) {
                    int from = scanner.nextInt();
                    int to = scanner.nextInt();
                    int step = scanner.nextInt();
                    StringBuilder builder = new StringBuilder();
                    builder.append(scanner.next());
                    while (!(next = scanner.next()).equals(COUNTER_TAG) && !next.equals(RUN_SET_TAG)) {
                        builder.append(" " + next);
                    }
                    addToPredefineds(new Counter(builder.toString(),from,to,step),false);
                }
                if (!next.equals(RUN_SET_TAG)) {
                    JOptionPane.showMessageDialog(fileChooser, "Parse error loading configuration file.\nEncountered \"" + next + "\" expected \"" + RUN_SET_TAG + "\"", "Load configuration", JOptionPane.PLAIN_MESSAGE);
                    removeAllFromPredefineds(null);
                    return;
                }
                next = scanner.next();
                while (next.equals(COUNTER_TAG)) {
                    int from = scanner.nextInt();
                    int to = scanner.nextInt();
                    int step = scanner.nextInt();
                    StringBuilder builder = new StringBuilder();
                    builder.append(scanner.next());
                    while (!(next = scanner.next()).equals(COUNTER_TAG) && !next.equals(END_TAG)) {
                        builder.append(" " + next);
                    }
                    addToRunSet(new Counter(builder.toString(),from,to,step),false);
                }
                if (!next.equals(END_TAG)) {
                    JOptionPane.showMessageDialog(fileChooser, "Parse error loading configuration file.\nEncountered \"" + next + "\" expected \"" + END_TAG + "\"", "Load configuration", JOptionPane.PLAIN_MESSAGE);
                    removeAllFromPredefineds(null);
                    return;
                }
                configDirectory = configFile.getParentFile();
                configFileName = configFile.getName();
                saveConfig.setEnabled(true);
                saveConfigAs.setEnabled(true);
                reloadConfig.setEnabled(true);
                removeAllRunSet.setEnabled(true);
                JOptionPane.showMessageDialog(fileChooser, "Configuration loaded.\n", "Load configuration", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(fileChooser, "Configuration not loaded.\n", "Load configuration", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (FileNotFoundException error) {
            JOptionPane.showMessageDialog(fileChooser, "Could not load configuration.\n" + error.getMessage(), "Load configuration", JOptionPane.ERROR_MESSAGE);
        } catch (CounterException error) {
            JOptionPane.showMessageDialog(fileChooser, "Error loading configuration.\n" + error.getMessage(), "Load configuration", JOptionPane.ERROR_MESSAGE);
        } catch (NullPointerException exception)  {
            JOptionPane.showMessageDialog(fileChooser, "Configuration not loaded.\nNo configuration file chosen.", "Load configuration", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected static boolean removeAllFromPredefineds(String message) {
        boolean ok = message == null || JOptionPane.showConfirmDialog(fileChooser, message, "Clear predefined counters", JOptionPane.YES_NO_OPTION) == 0;
        if (ok) {
            predefinedCounters.clear();
            removeFromPredefineds.removeAll();
            removeFromPredefineds.setEnabled(false);
            removeAllPredefineds.setEnabled(false);
            if (counters.size() == 0) {
                removeMenu.setEnabled(false);
            }
            predefined.removeAll();
            predefined.setEnabled(false);
            if (message != null) JOptionPane.showMessageDialog(fileChooser,"Predefined counters cleared.","Clear predefined counters",JOptionPane.PLAIN_MESSAGE);
        }
        return ok;
    }

    protected static boolean removeAllFromRunSet(String message) {
        boolean ok = message == null || JOptionPane.showConfirmDialog(fileChooser, message, "Clear run set", JOptionPane.YES_NO_OPTION) == 0;
        if (ok) {
            counters.clear();
            downers.removeAll();
            downers.validate();
            downers.repaint();
            uppers.removeAll();
            uppers.validate();
            uppers.repaint();
            removeMenu.removeAll();
            showRunSet.removeAll();
            showRunSet.setEnabled(false);
            saveConfig.setEnabled(false);
            saveConfigAs.setEnabled(false);
            removeAllRunSet.setEnabled(false);
            removeFromRunSet.setEnabled(false);
            if (predefinedCounters.size() == 0) {
                removeMenu.setEnabled(false);
            }
            if (message != null) JOptionPane.showMessageDialog(fileChooser,"Run set cleared.","Clear run set",JOptionPane.PLAIN_MESSAGE);
        }
        return ok;
    }

    protected static boolean removeAll(String message) {
        boolean ok = message == null || JOptionPane.showConfirmDialog(fileChooser, message, "Clear configuration", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        if (ok) {
            removeAllFromRunSet(null);
            removeAllFromPredefineds(null);
        }
        return ok;
    }

    private static void saveFile(String text,File destination) throws IOException {
        try (
            BufferedReader fileReader = new BufferedReader(new StringReader(text));
            PrintWriter fileWriter = new PrintWriter(new FileWriter(destination));
        ) {
            fileReader.lines().forEach(line -> fileWriter.println(line));
        }
    }

    private static File getFile(File directory,String name,String extension,boolean saveAs,boolean save) {
        if (!saveAs) {
            if (directory != null || name != null) {
                return new File(directory, name);
            } else {
                JOptionPane.showMessageDialog(fileChooser, "Could not get file.\nNo file found.", "Get file", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        File file;
        if (directory == null) {
            directory = Paths.get(".").toAbsolutePath().toFile();
        }
        fileChooser.setCurrentDirectory(directory);
        if (name != null) {
            fileChooser.setSelectedFile(new File(directory, name));
        }
        int fileChooserResponse = (save ? fileChooser.showSaveDialog(null) : fileChooser.showOpenDialog(null));
        File traceFile;
        if (fileChooserResponse == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            if (save && file.exists()) {
                int saveConfirmed = JOptionPane.showConfirmDialog(fileChooser, "File already exists.\n Do you want to overwrite it?", "Save trace", JOptionPane.YES_NO_OPTION);
                if (saveConfirmed != 0) {
                    JOptionPane.showMessageDialog(fileChooser, "Could not save file. No file chosen.\n", "Get file", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            } else if (!save && !file.exists()) {
                JOptionPane.showMessageDialog(fileChooser, "Cannot open file. File does not exist.\n", "Get file", JOptionPane.ERROR_MESSAGE);
            }
            return file;
        }
        JOptionPane.showMessageDialog(fileChooser, "Could not get file.\nNo file was selected.", "Get file", JOptionPane.ERROR_MESSAGE);
        return null;
    }

    public static void setUpPanel() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    createAndShowGUI();
                } catch (CounterException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void counterStarted() {
        line.setValue(Counter.getCount());
        countersActive++;
        run.setEnabled(false);
        configMenu.setEnabled(false);
        traceMenu.setEnabled(false);
        stop.setEnabled(true);
    }

    public static void counterStepped() {
        line.setValue(Counter.getCount());
    }

    public static void counterChecked() {

    }

    public static void counterEnded() {
        countersActive--;
        if (countersActive == 0) {
            run.setEnabled(false);
            stop.setEnabled(false);
            configMenu.setEnabled(true);
            traceMenu.setEnabled(true);
            String[] options = {
                    "Reload and rerun current run configuration",
                    "Clear current configuration",
                    "Leave current (expired run set) configuration as it is"
            };
            int response = JOptionPane.showOptionDialog(frame,
            "Current run set's run has been completed, and cannot be rerun.\n"+
                    "For a new run a new run set must be created/loaded.\n" +
                    "Before doing so the current run set must be cleared.\n" +
                    "Do you wish to do so?",
                    "Run ended",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (response != 2) {
                removeAll(null);
                if (response == 0) {
                    readConfigFile(true);
                    traceArea.setText(null);
                    Counter.resetTraceLineNumber();
                    counters.startSet();
                }
            }

        }
    }
}
