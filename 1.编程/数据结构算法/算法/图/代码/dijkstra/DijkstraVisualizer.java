package dijkstra;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Dijkstra\u7b97\u6cd5\u53ef\u89c6\u5316\u4e3b\u7a97\u53e3
 */
public class DijkstraVisualizer extends JFrame {

    private Graph graph;
    private GraphPanel graphPanel;
    private DijkstraAnimator animator;

    // UI\u7ec4\u4ef6
    private JLabel statusLabel;
    private JLabel stepLabel;
    private JSlider speedSlider;
    private JSlider stepSlider;
    private JButton playBtn;
    private JButton pauseBtn;
    private JButton nextBtn;
    private JButton prevBtn;
    private JButton resetBtn;
    private JTable distanceTable;
    private DefaultTableModel tableModel;
    private JTextArea logArea;
    private JPanel stepInfoPanel;
    private JLabel stepTypeLabel;
    private JLabel stepDescLabel;
    private JProgressBar progressBar;

    public DijkstraVisualizer() {
        setTitle("Dijkstra\u7b97\u6cd5\u52a8\u753b\u6f14\u793a");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // \u521d\u59cb\u5316\u56fe\u6570\u636e
        graph = new Graph();
        createSampleGraph();

        // \u521b\u5efa\u4e3b\u9762\u677f
        graphPanel = new GraphPanel(graph);
        animator = graphPanel.getAnimator();
        animator.addListener(new DijkstraAnimator.AnimationListener() {
            @Override
            public void onStepChanged(DijkstraAnimator animator) {
                updateUI();
            }
        });

        graphPanel.setStatusCallback(new GraphPanel.StatusCallback() {
            @Override
            public void onStatusUpdate(String message) {
                statusLabel.setText(message);
            }
        });

        // \u5e03\u5c40
        add(createToolbar(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1100, 700));

        updateUI();
    }

    /**
     * \u521b\u5efa\u793a\u4f8b\u56fe
     */
    private void createSampleGraph() {
        createGraphWithNodes(6);
    }

    /**
     * \u521b\u5efa\u6307\u5b9a\u6570\u91cf\u8282\u70b9\u7684\u56fe
     */
    private void createGraphWithNodes(int nodeCount) {
        graph.clear();
        
        int panelW = 800;
        int panelH = 600;
        
        // \u8ba1\u7b97\u8282\u70b9\u5e03\u5c40\uff08\u5706\u5f62\u6392\u5217\uff09
        double centerX = panelW / 2;
        double centerY = panelH / 2;
        double radius = Math.min(panelW, panelH) / 2 - 80;
        
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            double angle = 2 * Math.PI * i / nodeCount - Math.PI / 2;
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            Node node = graph.addNode(String.valueOf((char) ('A' + i)), x, y);
            nodes.add(node);
        }

        // \u6dfb\u52a0\u4e00\u4e9b\u9ed8\u8ba4\u8fb9
        Random rand = new Random(1);
        int edgeCount = nodeCount + rand.nextInt(nodeCount);
        for (int i = 0; i < edgeCount; i++) {
            int fromIdx = rand.nextInt(nodeCount);
            int toIdx = rand.nextInt(nodeCount);
            if (fromIdx != toIdx && graph.getEdge(nodes.get(fromIdx), nodes.get(toIdx)) == null) {
                int weight = 1 + rand.nextInt(10);
                graph.addEdge(nodes.get(fromIdx), nodes.get(toIdx), weight);
            }
        }

        // \u786e\u4fdd\u56fe\u8fde\u901a
        for (int i = 0; i < nodeCount - 1; i++) {
            if (graph.getEdgesFrom(nodes.get(i)).isEmpty()) {
                Node to = nodes.get((i + 1) % nodeCount);
                int weight = 1 + rand.nextInt(5);
                if (graph.getEdge(nodes.get(i), to) == null) {
                    graph.addEdge(nodes.get(i), to, weight);
                }
            }
        }

        if (!nodes.isEmpty()) {
            nodes.get(0).setStart(true);
        }
        if (nodes.size() > 1) {
            nodes.get(nodes.size() - 1).setTarget(true);
        }
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBorder(new EmptyBorder(5, 10, 5, 10));
        toolbar.setBackground(new Color(240, 240, 245));

        // \u8282\u70b9\u6570\u91cf\u8bbe\u7f6e
        JPanel nodeCountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nodeCountPanel.add(new JLabel("\u8282\u70b9\u6570\u91cf:"));
        JSpinner nodeCountSpinner = new JSpinner(new SpinnerNumberModel(6, 1, 26, 1));
        nodeCountSpinner.setPreferredSize(new Dimension(60, 25));
        nodeCountPanel.add(nodeCountSpinner);
        JButton applyCountBtn = new JButton("\u5e94\u7528");
        applyCountBtn.addActionListener(e -> {
            int count = (Integer) nodeCountSpinner.getValue();
            createGraphWithNodes(count);
            animator.reset();
            graphPanel.repaint();
            updateUI();
        });
        nodeCountPanel.add(applyCountBtn);
        toolbar.add(nodeCountPanel);

        toolbar.add(new JSeparator(SwingConstants.VERTICAL));

        // \u6a21\u5f0f\u6309\u94ae\u7ec4
        ButtonGroup modeGroup = new ButtonGroup();

        JToggleButton nodeBtn = createModeButton("\u7f16\u8f91\u8282\u70b9", "node", modeGroup);
        JToggleButton edgeBtn = createModeButton("\u6dfb\u52a0\u8fb9", "edge", modeGroup);
        JToggleButton startBtn = createModeButton("\u8bbe\u7f6e\u8d77\u70b9", "start", modeGroup);
        JToggleButton targetBtn = createModeButton("\u8bbe\u7f6e\u7ec8\u70b9", "target", modeGroup);

        nodeBtn.setSelected(true);

        toolbar.add(new JLabel("\u7f16\u8f91\u6a21\u5f0f: "));
        toolbar.add(nodeBtn);
        toolbar.add(edgeBtn);
        toolbar.add(startBtn);
        toolbar.add(targetBtn);

        toolbar.add(new JSeparator(SwingConstants.VERTICAL));

        // \u52a8\u753b\u63a7\u5236\u6309\u94ae
        playBtn = createControlButton("\u25b6 \u64ad\u653e", new Color(50, 150, 50));
        pauseBtn = createControlButton("\u23f8 \u6682\u505c", new Color(200, 150, 50));
        nextBtn = createControlButton("\u23ed \u4e0b\u4e00\u6b65", new Color(50, 100, 200));
        prevBtn = createControlButton("\u23ee \u4e0a\u4e00\u6b65", new Color(50, 100, 200));
        resetBtn = createControlButton("\u23f9 \u91cd\u7f6e", new Color(200, 50, 50));

        toolbar.add(playBtn);
        toolbar.add(pauseBtn);
        toolbar.add(prevBtn);
        toolbar.add(nextBtn);
        toolbar.add(resetBtn);

        // \u6e90\u7801\u5c55\u793a\u6309\u94ae
        JButton sourceCodeBtn = createControlButton("\u1f4c4 \u6e90\u7801", new Color(100, 100, 150));
        sourceCodeBtn.addActionListener(e -> showSourceCodeDialog());
        toolbar.add(sourceCodeBtn);

        toolbar.add(new JSeparator(SwingConstants.VERTICAL));

        // \u901f\u5ea6\u63a7\u5236
        toolbar.add(new JLabel("\u901f\u5ea6:"));
        speedSlider = new JSlider(100, 2000, 1000);
        speedSlider.setInverted(true);
        speedSlider.setPreferredSize(new Dimension(120, 25));
        speedSlider.addChangeListener(e -> {
            animator.setDelay(speedSlider.getValue());
        });
        toolbar.add(speedSlider);

        // \u751f\u6210\u968f\u673a\u56fe\u6309\u94ae
        JButton randomBtn = new JButton("\u968f\u673a\u56fe");
        randomBtn.addActionListener(e -> generateRandomGraph());
        toolbar.add(randomBtn);

        // \u6e05\u7a7a\u6309\u94ae
        JButton clearBtn = new JButton("\u6e05\u7a7a");
        clearBtn.addActionListener(e -> {
            graph.clear();
            animator.reset();
            graphPanel.repaint();
            updateUI();
        });
        toolbar.add(clearBtn);

        // \u6309\u94ae\u4e8b\u4ef6
        playBtn.addActionListener(e -> {
            Node startNode = getStartNode();
            if (startNode == null) {
                JOptionPane.showMessageDialog(this, "\u8bf7\u5148\u8bbe\u7f6e\u8d77\u70b9\u8282\u70b9\uff01");
                return;
            }
            
            // \u68c0\u6d4b\u662f\u5426\u6709\u7ec8\u70b9
            List<Node> targetNodes = getTargetNodes();
            if (targetNodes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "\u8bf7\u5148\u8bbe\u7f6e\u81f3\u5c11\u4e00\u4e2a\u7ec8\u70b9\u8282\u70b9\uff01");
                return;
            }
            
            // \u68c0\u6d4b\u6700\u77ed\u8def\u5f84
            Map<Node, Integer> distances = runDijkstra(startNode);
            StringBuilder unreachableTargets = new StringBuilder();
            for (Node target : targetNodes) {
                if (distances.get(target) == Integer.MAX_VALUE) {
                    if (unreachableTargets.length() > 0) {
                        unreachableTargets.append(", ");
                    }
                    unreachableTargets.append(target.getName());
                }
            }
            
            if (unreachableTargets.length() > 0) {
                JOptionPane.showMessageDialog(this, 
                    "\u8b66\u544a\uff1a\u8d77\u70b9 " + startNode.getName() + " \u65e0\u6cd5\u5230\u8fbe\u7ec8\u70b9: " + unreachableTargets.toString());
            }
            
            if (animator.getCurrentStepIndex() == -1) {
                animator.prepareAnimation(startNode);
            }
            graphPanel.setMode("animate");
            animator.startAnimation();
            updateUI();
        });

        pauseBtn.addActionListener(e -> {
            animator.pauseAnimation();
            updateUI();
        });

        nextBtn.addActionListener(e -> {
            Node startNode = getStartNode();
            if (startNode == null) {
                JOptionPane.showMessageDialog(this, "\u8bf7\u5148\u8bbe\u7f6e\u8d77\u70b9\u8282\u70b9\uff01");
                return;
            }
            
            List<Node> targetNodes = getTargetNodes();
            if (targetNodes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "\u8bf7\u5148\u8bbe\u7f6e\u81f3\u5c11\u4e00\u4e2a\u7ec8\u70b9\u8282\u70b9\uff01");
                return;
            }
            
            if (animator.getCurrentStepIndex() == -1) {
                animator.prepareAnimation(startNode);
            }
            graphPanel.setMode("animate");
            animator.stepForward();
            updateUI();
        });

        prevBtn.addActionListener(e -> {
            animator.stepBackward();
            updateUI();
        });

        resetBtn.addActionListener(e -> {
            animator.reset();
            graphPanel.setMode("node");
            nodeBtn.setSelected(true);
            updateUI();
        });

        return toolbar;
    }

    private JToggleButton createModeButton(String text, String mode, ButtonGroup group) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFocusPainted(false);
        group.add(btn);
        btn.addActionListener(e -> {
            if (btn.isSelected()) {
                graphPanel.setMode(mode);
            }
        });
        return btn;
    }

    private JButton createControlButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.BOLD, 12));
        return btn;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(0, 10, 0, 10));

        // \u5de6\u4fa7\uff1a\u56fe\u9762\u677f
        JPanel graphContainer = new JPanel(new BorderLayout());
        graphContainer.setBorder(BorderFactory.createTitledBorder(
            new EtchedBorder(), "\u56fe\u7f16\u8f91\u5668", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.BOLD, 14)));
        graphContainer.add(graphPanel, BorderLayout.CENTER);

        // \u53f3\u4fa7\uff1a\u4fe1\u606f\u9762\u677f
        JPanel infoPanel = new JPanel(new BorderLayout(5, 5));
        infoPanel.setPreferredSize(new Dimension(300, 0));

        // \u6b65\u9aa4\u4fe1\u606f
        stepInfoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        stepInfoPanel.setBorder(BorderFactory.createTitledBorder(
            new EtchedBorder(), "\u5f53\u524d\u6b65\u9aa4", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.BOLD, 14)));
        stepInfoPanel.setBackground(new Color(250, 250, 255));

        stepTypeLabel = new JLabel("\u672a\u5f00\u59cb", SwingConstants.CENTER);
        stepTypeLabel.setFont(new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.BOLD, 16));
        stepTypeLabel.setForeground(new Color(50, 50, 150));

        stepDescLabel = new JLabel("\u70b9\u51fb\u64ad\u653e\u6216\u4e0b\u4e00\u6b65\u5f00\u59cb\u52a8\u753b", SwingConstants.CENTER);
        stepDescLabel.setFont(new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.PLAIN, 12));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.PLAIN, 11));

        stepInfoPanel.add(stepTypeLabel);
        stepInfoPanel.add(stepDescLabel);
        stepInfoPanel.add(progressBar);

        // \u8ddd\u79bb\u8868\u683c
        String[] columns = {"\u8282\u70b9", "\u6700\u77ed\u8ddd\u79bb", "\u72b6\u6001"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        distanceTable = new JTable(tableModel);
        distanceTable.setFont(new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.PLAIN, 12));
        distanceTable.setRowHeight(25);
        distanceTable.getTableHeader().setFont(new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.BOLD, 12));
        distanceTable.getTableHeader().setBackground(new Color(230, 230, 240));
        
        // \u8bbe\u7f6e\u81ea\u5b9a\u4e49\u5355\u5143\u683c\u6e32\u67d3\u5668
        distanceTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
                    
                    if (column == 0 && row < graph.getNodes().size()) {
                        Node node = graph.getNodes().get(row);
                        if (node.isStart()) {
                            setForeground(new Color(220, 20, 60));
                            setFont(getFont().deriveFont(Font.BOLD));
                        } else if (node.isTarget()) {
                            setForeground(Color.BLACK);
                            setFont(getFont().deriveFont(Font.BOLD));
                        } else {
                            setForeground(Color.BLACK);
                            setFont(getFont().deriveFont(Font.PLAIN));
                        }
                    } else {
                        setForeground(Color.BLACK);
                        setFont(getFont().deriveFont(Font.PLAIN));
                    }
                }
                
                return comp;
            }
        });

        JScrollPane tableScroll = new JScrollPane(distanceTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder(
            new EtchedBorder(), "\u8ddd\u79bb\u8868", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.BOLD, 14)));
        tableScroll.setPreferredSize(new Dimension(0, 250));

        // \u65e5\u5fd7\u533a\u57df
        logArea = new JTextArea(8, 20);
        logArea.setFont(new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.PLAIN, 11));
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBackground(new Color(250, 250, 250));

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder(
            new EtchedBorder(), "\u6267\u884c\u65e5\u5fd7", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.BOLD, 14)));

        infoPanel.add(stepInfoPanel, BorderLayout.NORTH);
        infoPanel.add(tableScroll, BorderLayout.CENTER);
        infoPanel.add(logScroll, BorderLayout.SOUTH);

        mainPanel.add(graphContainer, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.EAST);

        return mainPanel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout(10, 0));
        statusBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusBar.setBackground(new Color(230, 230, 235));

        statusLabel = new JLabel("\u5c31\u7eea - \u4f7f\u7528\u5de6\u952e\u6dfb\u52a0\u8282\u70b9\uff0c\u53f3\u952e\u5220\u9664");
        statusLabel.setFont(new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.PLAIN, 12));

        stepLabel = new JLabel("\u6b65\u9aa4: 0 / 0");
        stepLabel.setFont(new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.PLAIN, 12));

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(stepLabel, BorderLayout.EAST);

        return statusBar;
    }

    private Node getStartNode() {
        for (Node node : graph.getNodes()) {
            if (node.isStart()) return node;
        }
        return null;
    }

    private List<Node> getTargetNodes() {
        List<Node> targets = new ArrayList<>();
        for (Node node : graph.getNodes()) {
            if (node.isTarget()) {
                targets.add(node);
            }
        }
        return targets;
    }

    /**
     * \u6267\u884cDijkstra\u7b97\u6cd5\u68c0\u6d4b\u6700\u77ed\u8def\u5f84
     */
    private Map<Node, Integer> runDijkstra(Node startNode) {
        Map<Node, Integer> dist = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));
        Set<Node> visited = new HashSet<>();

        for (Node node : graph.getNodes()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(startNode, 0);

        for (Node node : graph.getNodes()) {
            pq.offer(node);
        }

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (visited.contains(current) || dist.get(current) == Integer.MAX_VALUE) {
                continue;
            }
            visited.add(current);

            for (Edge edge : graph.getEdgesFrom(current)) {
                Node neighbor = edge.getTo();
                if (visited.contains(neighbor)) continue;

                int newDist = dist.get(current) + edge.getWeight();
                if (newDist < dist.get(neighbor)) {
                    dist.put(neighbor, newDist);
                    pq.remove(neighbor);
                    pq.offer(neighbor);
                }
            }
        }

        return dist;
    }

    private void generateRandomGraph() {
        graph.clear();
        animator.reset();

        Random rand = new Random();
        int nodeCount = 5 + rand.nextInt(4); // 5-8\u4e2a\u8282\u70b9
        int panelW = graphPanel.getWidth() > 0 ? graphPanel.getWidth() : 800;
        int panelH = graphPanel.getHeight() > 0 ? graphPanel.getHeight() : 600;

        // \u751f\u6210\u8282\u70b9\uff0c\u907f\u514d\u91cd\u53e0
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            int x, y, attempts = 0;
            boolean valid;
            do {
                x = 80 + rand.nextInt(panelW - 160);
                y = 80 + rand.nextInt(panelH - 160);
                valid = true;
                for (Node n : nodes) {
                    int dx = x - n.getX();
                    int dy = y - n.getY();
                    if (dx * dx + dy * dy < 10000) { // \u6700\u5c0f\u95f4\u8ddd100
                        valid = false;
                        break;
                    }
                }
                attempts++;
            } while (!valid && attempts < 50);

            Node node = graph.addNode(String.valueOf((char) ('A' + i)), x, y);
            nodes.add(node);
        }

        // \u751f\u6210\u8fb9
        int edgeCount = nodeCount + rand.nextInt(nodeCount);
        for (int i = 0; i < edgeCount; i++) {
            Node from = nodes.get(rand.nextInt(nodeCount));
            Node to = nodes.get(rand.nextInt(nodeCount));
            if (from != to && graph.getEdge(from, to) == null) {
                int weight = 1 + rand.nextInt(15);
                graph.addEdge(from, to, weight);
            }
        }

        // \u786e\u4fdd\u56fe\u8fde\u901a\uff08\u6dfb\u52a0\u4e00\u4e9b\u8fb9\uff09
        for (int i = 0; i < nodeCount - 1; i++) {
            if (graph.getEdgesFrom(nodes.get(i)).isEmpty()) {
                Node to = nodes.get(i + 1);
                int weight = 1 + rand.nextInt(10);
                if (graph.getEdge(nodes.get(i), to) == null) {
                    graph.addEdge(nodes.get(i), to, weight);
                }
            }
        }

        nodes.get(0).setStart(true);
        if (nodeCount > 1) {
            nodes.get(nodeCount - 1).setTarget(true);
        }

        graphPanel.repaint();
        updateUI();
    }

    private void updateUI() {
        SwingUtilities.invokeLater(() -> {
            // \u66f4\u65b0\u6b65\u9aa4\u4fe1\u606f
            DijkstraAnimator.Step step = animator.getCurrentStep();
            if (step != null) {
                stepTypeLabel.setText(step.getType().getTitle());
                stepDescLabel.setText("<html><center>" + step.getMessage() + "</center></html>");

                if (animator.getTotalSteps() > 0) {
                    int progress = (int) ((animator.getCurrentStepIndex() + 1) * 100.0 / animator.getTotalSteps());
                    progressBar.setValue(progress);
                    progressBar.setString((animator.getCurrentStepIndex() + 1) + " / " + animator.getTotalSteps());
                }

                // \u6dfb\u52a0\u5230\u65e5\u5fd7
                logArea.append("[" + step.getType().getTitle() + "] " + step.getMessage() + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            } else {
                stepTypeLabel.setText("\u672a\u5f00\u59cb");
                stepDescLabel.setText("\u70b9\u51fb\u64ad\u653e\u6216\u4e0b\u4e00\u6b65\u5f00\u59cb\u52a8\u753b");
                progressBar.setValue(0);
                progressBar.setString("0 / 0");
            }

            stepLabel.setText("\u6b65\u9aa4: " + (animator.getCurrentStepIndex() + 1) + " / " + animator.getTotalSteps());

            // \u66f4\u65b0\u8ddd\u79bb\u8868
            updateDistanceTable(step);

            // \u66f4\u65b0\u6309\u94ae\u72b6\u6001
            playBtn.setEnabled(!animator.isRunning());
            pauseBtn.setEnabled(animator.isRunning());
            nextBtn.setEnabled(!animator.isRunning());
            prevBtn.setEnabled(!animator.isRunning() && animator.getCurrentStepIndex() > 0);

            // \u66f4\u65b0\u6b65\u9aa4\u6ed1\u5757
            if (stepSlider != null) {
                stepSlider.setMaximum(Math.max(0, animator.getTotalSteps() - 1));
                stepSlider.setValue(animator.getCurrentStepIndex());
            }

            graphPanel.repaint();
        });
    }

    private void updateDistanceTable(DijkstraAnimator.Step step) {
        tableModel.setRowCount(0);

        if (step == null || step.getDistanceSnapshot() == null) {
            for (Node node : graph.getNodes()) {
                String dist = node.isStart() ? "0" : "\u221e";
                String state = "";
                if (node.isStart()) {
                    state = "\u8d77\u70b9";
                } else if (node.isTarget()) {
                    state = "\u7ec8\u70b9";
                } else {
                    state = "\u672a\u8bbf\u95ee";
                }
                tableModel.addRow(new Object[]{node.getName(), dist, state});
            }
            return;
        }

        Map<Node, Integer> snapshot = step.getDistanceSnapshot();
        for (Node node : graph.getNodes()) {
            int dist = snapshot.getOrDefault(node, Integer.MAX_VALUE);
            String distStr = dist == Integer.MAX_VALUE ? "\u221e" : String.valueOf(dist);

            String state = "";
            if (node.isStart()) {
                state = "\u8d77\u70b9";
            } else if (node.isTarget()) {
                if (node.getState() == Node.State.VISITED) {
                    state = "\u7ec8\u70b9(\u5df2\u786e\u5b9a)";
                } else if (node.getState() == Node.State.CURRENT) {
                    state = "\u7ec8\u70b9(\u5f53\u524d)";
                } else if (dist != Integer.MAX_VALUE) {
                    state = "\u7ec8\u70b9(\u5df2\u66f4\u65b0)";
                } else {
                    state = "\u7ec8\u70b9";
                }
            } else if (node.getState() == Node.State.VISITED) {
                state = "\u5df2\u786e\u5b9a";
            } else if (node.getState() == Node.State.CURRENT) {
                state = "\u5f53\u524d";
            } else if (dist != Integer.MAX_VALUE) {
                state = "\u5df2\u66f4\u65b0";
            } else {
                state = "\u672a\u8bbf\u95ee";
            }

            tableModel.addRow(new Object[]{node.getName(), distStr, state});
        }
    }

    /**
     * \u663e\u793a\u7b97\u6cd5\u6e90\u7801\u5bf9\u8bdd\u6846
     */
    private void showSourceCodeDialog() {
        String sourceCode = "Dijkstra Algorithm Core Implementation\n" +
            "========================================\n\n" +
            "public void dijkstra(Node start) {\n" +
            "    // 1. Initialize distance array\n" +
            "    Map<Node, Integer> dist = new HashMap<>();\n" +
            "    Map<Node, Node> prev = new HashMap<>();\n" +
            "    \n" +
            "    for (Node node : nodes) {\n" +
            "        dist.put(node, Integer.MAX_VALUE);  // Init to infinity\n" +
            "        prev.put(node, null);\n" +
            "    }\n" +
            "    dist.put(start, 0);  // Start distance is 0\n" +
            "    \n" +
            "    // 2. Use priority queue (min-heap)\n" +
            "    PriorityQueue<Node> pq = new PriorityQueue<>(\n" +
            "        Comparator.comparingInt(dist::get));\n" +
            "    \n" +
            "    for (Node node : nodes) {\n" +
            "        pq.offer(node);\n" +
            "    }\n" +
            "    \n" +
            "    Set<Node> visited = new HashSet<>();\n" +
            "    \n" +
            "    // 3. Main loop\n" +
            "    while (!pq.isEmpty()) {\n" +
            "        Node u = pq.poll();\n" +
            "        \n" +
            "        // Skip visited or unreachable nodes\n" +
            "        if (visited.contains(u) || dist.get(u) == Integer.MAX_VALUE) {\n" +
            "            continue;\n" +
            "        }\n" +
            "        \n" +
            "        visited.add(u);\n" +
            "        \n" +
            "        // 4. Relaxation\n" +
            "        for (Edge edge : getEdgesFrom(u)) {\n" +
            "            Node v = edge.getTo();\n" +
            "            \n" +
            "            if (visited.contains(v)) {\n" +
            "                continue;\n" +
            "            }\n" +
            "            \n" +
            "            int newDist = dist.get(u) + edge.getWeight();\n" +
            "            \n" +
            "            if (newDist < dist.get(v)) {\n" +
            "                dist.put(v, newDist);\n" +
            "                prev.put(v, u);\n" +
            "                \n" +
            "                // Update priority queue\n" +
            "                pq.remove(v);\n" +
            "                pq.offer(v);\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}\n\n" +
            "Complexity Analysis\n" +
            "===================\n" +
            "- Time Complexity: O((V+E)logV)\n" +
            "  - V: Number of vertices\n" +
            "  - E: Number of edges\n" +
            "  - Each priority queue op is O(logV)\n" +
            "\n" +
            "- Space Complexity: O(V)\n" +
            "  - Store distances and predecessors\n" +
            "\n" +
            "Constraints\n" +
            "===========\n" +
            "- All edge weights must be non-negative\n" +
            "- Works for directed and undirected graphs\n" +
            "- Used for single-source shortest path";

        JDialog dialog = new JDialog(this, "Dijkstra Algorithm Source Code", true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        final JTextArea codeArea = new JTextArea(sourceCode);
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        codeArea.setEditable(false);
        codeArea.setLineWrap(false);
        codeArea.setWrapStyleWord(false);

        JScrollPane scrollPane = new JScrollPane(codeArea);
        scrollPane.setPreferredSize(new Dimension(680, 500));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton copyBtn = new JButton("Copy");
        copyBtn.setBackground(new Color(50, 150, 50));
        copyBtn.setForeground(Color.WHITE);
        copyBtn.setFocusPainted(false);
        copyBtn.setFont(new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.BOLD, 12));
        copyBtn.addActionListener(e -> {
            codeArea.selectAll();
            codeArea.copy();
            JOptionPane.showMessageDialog(dialog, "Code copied to clipboard!");
        });
        buttonPanel.add(copyBtn);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(150, 150, 150));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setFont(new Font("\u5fae\u8f6f\u96c5\u9ed1", Font.BOLD, 12));
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}
