package factory.ui;

import Skeleton.*;
import factory.Main;
import factory.StatisticObserver;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import javax.swing.Timer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Modern Swing UI to visualize the Smart Factory simulation with real-time updates
 * and interactive controls.
 */
public class FactoryUI implements StatisticObserver {
    // UI Components
    private final JFrame frame;
    private DefaultTableModel statsTableModel;
    private JTable statsTable;
    private final Map<String, Integer> rowIndexByKey = new HashMap<>();
    private JLabel statusLabel;
    private JProgressBar simulationProgress;
    private JPanel factoryVisualization;
    private final Timer animationTimer;

    // Visualization maps
    private final Map<String, JPanel> robotPanels = new HashMap<>();
    private final Map<String, JPanel> beltPanels = new HashMap<>();
    private JPanel storagePanel;

    // Controls
    private JSpinner timeSpinner;
    private JSpinner actionsPerSecSpinner;
    private JSpinner numRobotsSpinner;
    private JSpinner numBeltsSpinner;
    private JButton startButton;
    private JButton pauseButton;

    // State
    private final AtomicBoolean simulationRunning = new AtomicBoolean(false);
    private long simulationStartTime;
    private long simulationDuration;

    // Colors
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 245);
    private static final Color HEADER_COLOR = new Color(60, 90, 120);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Color IDLE_COLOR = new Color(200, 200, 200);
    private static final Color ACTIVE_COLOR = new Color(100, 200, 100);
    private static final Color TABLE_ALT_COLOR = new Color(245, 245, 250);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);

    public FactoryUI() {
        // System look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        frame = new JFrame("Smart Factory Automation Dashboard");
        frame.setSize(1200, 800);
        frame.setMinimumSize(new Dimension(1000, 700));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        frame.setLayout(new BorderLayout(10, 10));

        // Header
        frame.add(createHeaderPanel(), BorderLayout.NORTH);

        // Split pane: visualization | stats+controls
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(650);
        split.setResizeWeight(0.6);
        split.setBorder(null);
        split.setLeftComponent(createVisualizationPanel());

        JPanel right = new JPanel(new BorderLayout(5,5));
        right.setBackground(BACKGROUND_COLOR);
        right.add(createStatsPanel(), BorderLayout.CENTER);
        right.add(createControlPanel(), BorderLayout.SOUTH);
        split.setRightComponent(right);

        frame.add(split, BorderLayout.CENTER);
        frame.add(createStatusPanel(), BorderLayout.SOUTH);

        Statistic.registerObserver(this);
        animationTimer = new Timer(50, e -> updateVisualization());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(HEADER_COLOR);
        p.setBorder(new EmptyBorder(15,15,15,15));
        JLabel title = new JLabel("Masoud Rafiee Smart Factory Automation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        p.add(title, BorderLayout.WEST);

        statusLabel = new JLabel("Ready to Start");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(Color.BLACK);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(statusLabel);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private JPanel createVisualizationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR),
                "Factory Visualization",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                ACCENT_COLOR));

        JSplitPane vsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        vsplit.setDividerLocation(400);
        vsplit.setResizeWeight(0.7);
        vsplit.setBorder(null);

        factoryVisualization = new JPanel(null);
        factoryVisualization.setBackground(Color.GRAY);
        factoryVisualization.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        storagePanel = createComponentPanel("Storage", new Color(200,230,200));
        storagePanel.setBounds(450,250,150,100);
        factoryVisualization.add(storagePanel);

        vsplit.setTopComponent(factoryVisualization);
        vsplit.setBottomComponent(createMonitoringPanel());
        panel.add(vsplit, BorderLayout.CENTER);

        simulationProgress = new JProgressBar(0,100);
        simulationProgress.setStringPainted(true);
        simulationProgress.setForeground(ACCENT_COLOR);
        panel.add(simulationProgress, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMonitoringPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BACKGROUND_COLOR);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150,150,180)),
                "Performance Monitoring",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(100,100,130)));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN,12));
        tabs.addTab("Production Chart", createDummyChartPanel());
        tabs.addTab("System Metrics", new JPanel());
        p.add(tabs, BorderLayout.CENTER);
        return p;
    }

    // A placeholder chart panel—you can swap in your real chart later
    private JPanel createDummyChartPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawString("Chart Area", 20,20);
            }
        };
        return p;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR),
                "Real-time Statistics",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD,14),
                ACCENT_COLOR));

        statsTableModel = new DefaultTableModel(new String[]{"Component","Metric","Value"},0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        statsTable = new JTable(statsTableModel);
        styleTable(statsTable);

        JScrollPane scroll = new JScrollPane(statsTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void styleTable(JTable t) {
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0,0));
        t.setRowHeight(25);
        t.setFillsViewportHeight(true);
        JTableHeader h = t.getTableHeader();
        h.setBackground(ACCENT_COLOR);
        h.setForeground(Color.BLACK);
        h.setFont(new Font("Segoe UI", Font.BOLD,12));
        DefaultTableCellRenderer rr = new DefaultTableCellRenderer() {
            DecimalFormat df=new DecimalFormat("#,##0.00");
            @Override public Component getTableCellRendererComponent(JTable table,Object val,boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(table,val,sel,foc,row,col);
                if (!sel) setBackground(row%2==0?Color.BLACK:TABLE_ALT_COLOR);
                setForeground(TEXT_COLOR);
                if (col==2 && val instanceof Number) {
                    setText(df.format(((Number)val).doubleValue()));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    setHorizontalAlignment(col==0?SwingConstants.LEFT:SwingConstants.CENTER);
                }
                return this;
            }
        };
        t.setDefaultRenderer(Object.class, rr);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR),
                "Controls",
                TitledBorder.RIGHT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD,14),
                ACCENT_COLOR));

        JPanel fields = new JPanel(new GridLayout(2,4,10,10));
        fields.setBackground(BACKGROUND_COLOR);
        fields.setBorder(new EmptyBorder(10,10,10,10));

        fields.add(new JLabel("Duration (s):"));
        timeSpinner = new JSpinner(new SpinnerNumberModel(10,1,60,1));
        styleSpinner(timeSpinner);
        fields.add(timeSpinner);

        fields.add(new JLabel("Actions/s:"));
        actionsPerSecSpinner = new JSpinner(new SpinnerNumberModel(2,1,10,1));
        styleSpinner(actionsPerSecSpinner);
        fields.add(actionsPerSecSpinner);

        fields.add(new JLabel("Robots:"));
        numRobotsSpinner = new JSpinner(new SpinnerNumberModel(3,1,10,1));
        styleSpinner(numRobotsSpinner);
        fields.add(numRobotsSpinner);

        fields.add(new JLabel("Belts:"));
        numBeltsSpinner = new JSpinner(new SpinnerNumberModel(2,1,5,1));
        styleSpinner(numBeltsSpinner);
        fields.add(numBeltsSpinner);

        panel.add(fields, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));
        btns.setBackground(BACKGROUND_COLOR);
        startButton = createStyledButton("Start", ACCENT_COLOR);
        startButton.addActionListener(e -> startSimulation());
        btns.add(startButton);

        pauseButton = createStyledButton("Pause", Color.GRAY);
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(e -> togglePause());
        btns.add(pauseButton);

        JButton reset = createStyledButton("Reset", new Color(240,100,100));
        reset.addActionListener(e -> resetSimulation());
        btns.add(reset);

        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private void styleSpinner(JSpinner s) {
        s.setFont(new Font("Segoe UI",Font.PLAIN,12));
        ((JSpinner.DefaultEditor)s.getEditor()).getTextField()
                .setHorizontalAlignment(SwingConstants.CENTER);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.BLACK);
        b.setFont(new Font("Segoe UI",Font.BOLD,12));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8,15,8,15));
        b.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseEntered(java.awt.event.MouseEvent e){b.setBackground(bg.brighter());}
            public void mouseExited(java.awt.event.MouseEvent e){b.setBackground(bg);}
        });
        return b;
    }

    private JPanel createStatusPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(230,230,230));
        p.setBorder(new EmptyBorder(5,10,5,10));
        p.add(new JLabel("© CS321 Smart Factory"), BorderLayout.WEST);
        return p;
    }

    private JPanel createComponentPanel(String name, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(color);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker()),
                new EmptyBorder(5,5,5,5)
        ));
        JLabel lbl = new JLabel(name, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI",Font.BOLD,12));
        p.add(lbl, BorderLayout.NORTH);
        JPanel inner = new JPanel(new GridLayout(0,1,2,2));
        inner.setOpaque(false);
        p.add(inner, BorderLayout.CENTER);
        return p;
    }

    private void startSimulation() {
        resetVisualization();
        clearStatistics();
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        simulationRunning.set(true);

        int duration = (Integer)timeSpinner.getValue();
        int aps = (Integer)actionsPerSecSpinner.getValue();
        int r = (Integer)numRobotsSpinner.getValue();
        int b = (Integer)numBeltsSpinner.getValue();

        simulationDuration = duration * 1000L;
        simulationStartTime = System.currentTimeMillis();
        animationTimer.start();
        statusLabel.setText("Running…");

        new Thread(() -> {
            SimulationInput in = new SimulationInput();
            in.addInput("Time", List.of(String.valueOf(duration)));
            in.addInput("ActionsPerSecond", List.of(String.valueOf(aps)));
            in.addInput("NumRobots", List.of(String.valueOf(r)));
            in.addInput("NumBelts", List.of(String.valueOf(b)));

            // Initialize visuals
            SwingUtilities.invokeLater(() -> initializeFactoryComponents(r,b));
            StatisticsContainer stats = Main.runTest(in);

            SwingUtilities.invokeLater(() -> {
                animationTimer.stop();
                simulationRunning.set(false);
                startButton.setEnabled(true);
                pauseButton.setEnabled(false);
                statusLabel.setText("Completed");
                simulationProgress.setValue(100);
                showSimulationResults(stats);
            });
        }, "SimThread").start();
    }

    private void initializeFactoryComponents(int nr, int nb) {
        factoryVisualization.removeAll();
        robotPanels.clear();
        beltPanels.clear();
        storagePanel = createComponentPanel("Storage", new Color(200,230,200));
        storagePanel.setBounds(450,250,150,100);
        factoryVisualization.add(storagePanel);

        // robots in circle
        double angle=0, step=2*Math.PI/nr; int cx=525, cy=300, r=200;
        for(int i=0;i<nr;i++){
            int x=(int)(cx + r*Math.cos(angle)-50);
            int y=(int)(cy + r*Math.sin(angle)-50);
            angle+=step;
            JPanel rob = createComponentPanel("Robot-"+(i+1), IDLE_COLOR);
            rob.setBounds(x,y,100,80);
            factoryVisualization.add(rob);
            robotPanels.put("Robot-"+(i+1),rob);
        }
        // belts
        int bw=80, bh=400, sx=50;
        int space=(factoryVisualization.getWidth()-2*sx-bw*nb)/(nb+1);
        for(int i=0;i<nb;i++){
            int x=sx+(space+bw)*i;
            JPanel belt = createComponentPanel("Belt-"+(i+1), new Color(180,180,220));
            belt.setBounds(x,50,bw,bh);
            factoryVisualization.add(belt);
            beltPanels.put("Belt-"+(i+1),belt);
        }
        factoryVisualization.repaint();
    }

    private void updateVisualization() {
        if(!simulationRunning.get()) return;
        long elapsed=System.currentTimeMillis()-simulationStartTime;
        simulationProgress.setValue((int)Math.min(100, elapsed*100/simulationDuration));
        long anim=System.currentTimeMillis()%1000;

        // animate robots
        for(var e:robotPanels.entrySet()){
            JPanel rob=e.getValue();
            boolean active=(anim/500)% (robotPanels.size()*2)==
                    (Integer.parseInt(e.getKey().split("-")[1])-1)*2;
            rob.setBackground(active?ACTIVE_COLOR:IDLE_COLOR);
        }
        factoryVisualization.repaint();
    }

    private void togglePause() {
        if(simulationRunning.get()){
            simulationRunning.set(false);
            animationTimer.stop();
            pauseButton.setText("Resume");
            statusLabel.setText("Paused");
        } else {
            simulationRunning.set(true);
            animationTimer.start();
            pauseButton.setText("Pause");
            statusLabel.setText("Running…");
        }
    }

    private void resetSimulation(){
        simulationRunning.set(false);
        animationTimer.stop();
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        simulationProgress.setValue(0);
        statusLabel.setText("Ready");
        clearStatistics();
        resetVisualization();
    }

    private void clearStatistics(){
        statsTableModel.setRowCount(0);
        rowIndexByKey.clear();
    }

    private void resetVisualization(){
        factoryVisualization.removeAll();
        robotPanels.clear();
        beltPanels.clear();
        storagePanel = createComponentPanel("Storage", new Color(200,230,200));
        storagePanel.setBounds(450,250,150,100);
        factoryVisualization.add(storagePanel);
        factoryVisualization.repaint();
    }
    @Override
    public void onStatisticUpdated(String comp, String stat, float val) {
        // Add debug logging
        System.out.println("Received update: " + comp + " | " + stat + " | " + val);

        SwingUtilities.invokeLater(() -> {
            // Check simulation running state but continue for debugging
            if(!simulationRunning.get()) {
                System.out.println("Warning: Update received while simulation not running: " + comp + " | " + stat + " | " + val);
                // Don't return - let the update go through for debugging
            }

            String key = comp + "|" + stat;

            // Get latest value from the container if possible
            float tot = val; // Default to the passed value
            try {
                // Try to get from container but handle null gracefully
                StatisticsContainer container = StatisticsContainer.getInstance();
                if (container != null) {
                    Statistics component = container.getComponent(comp);
                    if (component != null) {
                        Statistic statistic = component.getStatistic(stat);
                        if (statistic != null) {
                            tot = statistic.summarize();
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error getting statistic: " + e.getMessage());
                // Continue with the value we received
            }

            if(rowIndexByKey.containsKey(key)) {
                statsTableModel.setValueAt(tot, rowIndexByKey.get(key), 2);
            } else {
                int row = statsTableModel.getRowCount();
                statsTableModel.addRow(new Object[]{comp, stat, tot});
                rowIndexByKey.put(key, row);
            }

            updateComponentStats(comp, stat, tot);
        });
    }

    private void updateComponentStats(String comp, String stat, float v){
        JPanel p = comp.startsWith("Robot")?robotPanels.get(comp):
                comp.startsWith("Belt")?beltPanels.get(comp):
                        comp.equals("Storage")?storagePanel:null;
        if(p==null) return;
        JPanel inner=(JPanel)((BorderLayout)p.getLayout())
                .getLayoutComponent(BorderLayout.CENTER);
        boolean found=false;
        for(Component c:inner.getComponents()){
            if(c instanceof JLabel && ((JLabel)c).getText().startsWith(stat+":")){
                ((JLabel)c).setText(stat+": "+new DecimalFormat("#0.00").format(v));
                found=true; break;
            }
        }
        if(!found){
            JLabel lbl=new JLabel(stat+": "+new DecimalFormat("#0.00").format(v));
            lbl.setFont(new Font("Segoe UI",Font.PLAIN,10));
            inner.add(lbl);
            p.revalidate();
        }
        if(stat.equals("ItemsProcessed") && v>0) p.setBackground(ACTIVE_COLOR);
    }

    private void showSimulationResults(StatisticsContainer stats){
        JDialog d=new JDialog(frame,"Results",true);
        d.setLayout(new BorderLayout(10,10));
        d.setSize(500,400);
        JPanel cp=new JPanel(new BorderLayout(5,5));
        cp.setBorder(new EmptyBorder(10,10,10,10));
        DefaultTableModel rm=new DefaultTableModel(new String[]{"Comp","Metric","Total"},0);
        JTable rt=new JTable(rm);
        styleTable(rt);
        for(String c:stats.getComponentNames()){
            var m=stats.getComponent(c);
            for(String s:m.getStatisticNames()){
                rm.addRow(new Object[]{c,s,m.getStatistic(s).summarize()});
            }
        }
        cp.add(new JScrollPane(rt),BorderLayout.CENTER);
        JButton close=createStyledButton("Close",ACCENT_COLOR);
        close.addActionListener(e->d.dispose());
        JPanel bp=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bp.add(close);
        cp.add(bp,BorderLayout.SOUTH);
        d.add(cp);
        d.setLocationRelativeTo(frame);
        d.setVisible(true);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new FactoryUI();
        });
    }
}