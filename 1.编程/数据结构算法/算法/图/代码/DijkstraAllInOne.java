import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Dijkstra Algorithm Animation - All in One
 * Compile: javac -encoding UTF-8 DijkstraAllInOne.java
 * Run:     java DijkstraAllInOne
 */
public class DijkstraAllInOne {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            DijkstraVisualizer visualizer = new DijkstraVisualizer();
            visualizer.setVisible(true);
        });
    }
}

/* ============================================================
 *  Node
 * ============================================================ */
class Node {
    private int id;
    private String name;
    private int x, y;
    private int radius = 25;

    public enum State {
        UNVISITED(new Color(100, 149, 237)),
        CURRENT(new Color(255, 165, 0)),
        VISITED(new Color(50, 205, 50)),
        START(new Color(220, 20, 60)),
        TARGET(new Color(128, 0, 128));

        private final Color color;
        State(Color color) { this.color = color; }
        public Color getColor() { return color; }
    }

    private State state = State.UNVISITED;
    private int distance = Integer.MAX_VALUE;
    private boolean isStart = false;
    private boolean isTarget = false;
    private boolean isHighlighted = false;

    public Node(int id, String name, int x, int y) {
        this.id = id; this.name = name; this.x = x; this.y = y;
    }

    public void draw(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillOval(x - radius + 3, y - radius + 3, radius * 2, radius * 2);
        Color baseColor = isStart ? State.START.color : isTarget ? State.TARGET.color : state.color;
        g2d.setColor(baseColor.brighter());
        g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        g2d.setColor(baseColor);
        g2d.fillOval(x - radius + 2, y - radius + 2, (radius - 2) * 2, (radius - 2) * 2);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(x - radius, y - radius, radius * 2, radius * 2);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(name, x - fm.stringWidth(name) / 2, y + fm.getHeight() / 4);
        if (distance != Integer.MAX_VALUE) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            fm = g2d.getFontMetrics();
            String distStr = String.valueOf(distance);
            g2d.drawString(distStr, x - fm.stringWidth(distStr) / 2, y - radius - 8);
        } else {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            fm = g2d.getFontMetrics();
            String infStr = "\u221e";
            g2d.drawString(infStr, x - fm.stringWidth(infStr) / 2, y - radius - 8);
        }
    }

    public boolean contains(Point p) { return contains(p.x, p.y); }
    public boolean contains(int px, int py) {
        int dx = px - x, dy = py - y;
        return dx * dx + dy * dy <= radius * radius;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public int getRadius() { return radius; }
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    public int getDistance() { return distance; }
    public void setDistance(int distance) { this.distance = distance; }
    public boolean isStart() { return isStart; }
    public void setStart(boolean start) { isStart = start; }
    public boolean isTarget() { return isTarget; }
    public void setTarget(boolean target) { isTarget = target; }
    public boolean isHighlighted() { return isHighlighted; }
    public void setHighlighted(boolean highlighted) { isHighlighted = highlighted; }
    @Override public String toString() { return name; }
}

/* ============================================================
 *  Edge
 * ============================================================ */
class Edge {
    private Node from, to;
    private int weight;

    public enum State {
        NORMAL(new Color(150, 150, 150), 2),
        HIGHLIGHTED(new Color(255, 215, 0), 4),
        IN_PATH(new Color(220, 20, 60), 4),
        RELAXED(new Color(50, 205, 50), 3);

        private final Color color;
        private final int strokeWidth;
        State(Color color, int strokeWidth) { this.color = color; this.strokeWidth = strokeWidth; }
        public Color getColor() { return color; }
        public int getStrokeWidth() { return strokeWidth; }
    }

    private State state = State.NORMAL;

    public Edge(Node from, Node to, int weight) {
        this.from = from; this.to = to; this.weight = weight;
    }

    public void draw(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x1 = from.getX(), y1 = from.getY(), x2 = to.getX(), y2 = to.getY();
        double dx = x2 - x1, dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);
        double unitX = dx / length, unitY = dy / length;
        int startX = (int)(x1 + unitX * from.getRadius());
        int startY = (int)(y1 + unitY * from.getRadius());
        int endX = (int)(x2 - unitX * to.getRadius());
        int endY = (int)(y2 - unitY * to.getRadius());
        g2d.setColor(state.getColor());
        g2d.setStroke(new BasicStroke(state.getStrokeWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(startX, startY, endX, endY);
        drawArrow(g2d, startX, startY, endX, endY);
        int midX = (startX + endX) / 2, midY = (startY + endY) / 2;
        String weightStr = String.valueOf(weight);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.fillRoundRect(midX - fm.stringWidth(weightStr)/2 - 4, midY - fm.getHeight()/2 - 2,
                         fm.stringWidth(weightStr) + 8, fm.getHeight() + 4, 6, 6);
        g2d.setColor(Color.BLACK);
        g2d.drawString(weightStr, midX - fm.stringWidth(weightStr)/2, midY + fm.getHeight()/4);
    }

    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        double arrowLength = 12, arrowAngle = Math.PI / 6;
        double angle = Math.atan2(y2 - y1, x2 - x1);
        Polygon arrow = new Polygon();
        arrow.addPoint(x2, y2);
        arrow.addPoint((int)(x2 - arrowLength * Math.cos(angle - arrowAngle)),
                       (int)(y2 - arrowLength * Math.sin(angle - arrowAngle)));
        arrow.addPoint((int)(x2 - arrowLength * Math.cos(angle + arrowAngle)),
                       (int)(y2 - arrowLength * Math.sin(angle + arrowAngle)));
        g2d.fill(arrow);
    }

    public boolean containsNode(Node node) { return from.equals(node) || to.equals(node); }
    public Node getOtherNode(Node node) { return from.equals(node) ? to : to.equals(node) ? from : null; }
    public Node getFrom() { return from; }
    public Node getTo() { return to; }
    public int getWeight() { return weight; }
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    @Override public String toString() { return from.getName() + " -> " + to.getName() + " (" + weight + ")"; }
}

/* ============================================================
 *  Graph
 * ============================================================ */
class Graph {
    private List<Node> nodes;
    private List<Edge> edges;
    private int nextNodeId = 0;

    public Graph() { nodes = new ArrayList<>(); edges = new ArrayList<>(); }

    public Node addNode(String name, int x, int y) {
        Node node = new Node(nextNodeId++, name, x, y);
        nodes.add(node);
        return node;
    }

    public Edge addEdge(Node from, Node to, int weight) {
        return addEdge(from, to, weight, false);
    }

    public Edge addEdge(Node from, Node to, int weight, boolean bidirectional) {
        for (Edge e : edges)
            if (e.getFrom().equals(from) && e.getTo().equals(to)) return null;
        Edge edge = new Edge(from, to, weight);
        edges.add(edge);
        if (bidirectional) {
            for (Edge e : edges)
                if (e.getFrom().equals(to) && e.getTo().equals(from)) return edge;
            edges.add(new Edge(to, from, weight));
        }
        return edge;
    }

    public void removeNode(Node node) { edges.removeIf(e -> e.containsNode(node)); nodes.remove(node); }
    public void removeEdge(Edge edge) { edges.remove(edge); }
    public void clear() { nodes.clear(); edges.clear(); nextNodeId = 0; }

    public List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        for (Edge e : edges) if (e.getFrom().equals(node)) neighbors.add(e.getTo());
        return neighbors;
    }

    public Edge getEdge(Node from, Node to) {
        for (Edge e : edges) if (e.getFrom().equals(from) && e.getTo().equals(to)) return e;
        return null;
    }

    public List<Edge> getEdgesFrom(Node node) {
        List<Edge> result = new ArrayList<>();
        for (Edge e : edges) if (e.getFrom().equals(node)) result.add(e);
        return result;
    }

    public void resetStates() {
        for (Node n : nodes) { n.setState(Node.State.UNVISITED); n.setDistance(Integer.MAX_VALUE); }
        for (Edge e : edges) e.setState(Edge.State.NORMAL);
    }

    public List<Node> getNodes() { return new ArrayList<>(nodes); }
    public List<Edge> getEdges() { return new ArrayList<>(edges); }
    public Node getNodeAt(int x, int y) {
        for (Node n : nodes) if (n.contains(x, y)) return n;
        return null;
    }
    public boolean isEmpty() { return nodes.isEmpty(); }
    public int getNodeCount() { return nodes.size(); }
}

/* ============================================================
 *  DijkstraAnimator
 * ============================================================ */
class DijkstraAnimator {
    public enum StepType {
        INIT("Init", "Set start distance to 0, others to infinity"),
        SELECT_NODE("Select Node", "Extract min-distance node from priority queue"),
        CHECK_EDGE("Check Edge", "Examine edge to neighbor"),
        RELAX("Relax", "Update neighbor's shortest distance"),
        MARK_VISITED("Mark Visited", "Current node's shortest path confirmed"),
        FINISH("Finish", "All reachable nodes processed");

        private final String title, description;
        StepType(String title, String description) { this.title = title; this.description = description; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
    }

    public static class Step {
        private StepType type;
        private Node currentNode;
        private Edge currentEdge;
        private Node neighborNode;
        private int oldDistance, newDistance;
        private String message;
        private Map<Node, Integer> distanceSnapshot;

        public Step(StepType type, Node currentNode, String message) {
            this.type = type; this.currentNode = currentNode; this.message = message;
            this.distanceSnapshot = new HashMap<>();
        }
        public StepType getType() { return type; }
        public Node getCurrentNode() { return currentNode; }
        public Edge getCurrentEdge() { return currentEdge; }
        public Node getNeighborNode() { return neighborNode; }
        public String getMessage() { return message; }
        public Map<Node, Integer> getDistanceSnapshot() { return distanceSnapshot; }
        public int getOldDistance() { return oldDistance; }
        public int getNewDistance() { return newDistance; }
        public void setCurrentEdge(Edge e) { this.currentEdge = e; }
        public void setNeighborNode(Node n) { this.neighborNode = n; }
        public void setOldDistance(int d) { this.oldDistance = d; }
        public void setNewDistance(int d) { this.newDistance = d; }
        public void setDistanceSnapshot(Map<Node, Integer> s) { this.distanceSnapshot = s; }
    }

    private Graph graph;
    private List<Step> steps;
    private int currentStepIndex = -1;
    private boolean isRunning = false;
    private Thread animationThread;
    private int delayMs = 1000;
    private List<AnimationListener> listeners = new ArrayList<>();

    public DijkstraAnimator(Graph graph) { this.graph = graph; this.steps = new ArrayList<>(); }

    public void prepareAnimation(Node startNode) {
        steps.clear(); currentStepIndex = -1; graph.resetStates();
        Map<Node, Integer> dist = new HashMap<>();
        Map<Node, Node> prev = new HashMap<>();
        for (Node n : graph.getNodes()) dist.put(n, Integer.MAX_VALUE);
        dist.put(startNode, 0);
        Step initStep = new Step(StepType.INIT, startNode, "Init: start " + startNode.getName() + " distance = 0");
        initStep.setDistanceSnapshot(new HashMap<>(dist));
        steps.add(initStep);

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));
        Set<Node> visited = new HashSet<>();
        for (Node n : graph.getNodes()) pq.offer(n);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (visited.contains(current) || dist.get(current) == Integer.MAX_VALUE) continue;
            Step selectStep = new Step(StepType.SELECT_NODE, current,
                "Select " + current.getName() + ", distance = " + dist.get(current));
            selectStep.setDistanceSnapshot(new HashMap<>(dist));
            steps.add(selectStep);

            for (Edge edge : graph.getEdgesFrom(current)) {
                Node neighbor = edge.getTo();
                if (visited.contains(neighbor)) continue;
                int newDist = dist.get(current) + edge.getWeight();
                Step checkStep = new Step(StepType.CHECK_EDGE, current,
                    "Check edge " + current.getName() + " -> " + neighbor.getName() + ", weight = " + edge.getWeight());
                checkStep.setCurrentEdge(edge); checkStep.setNeighborNode(neighbor);
                checkStep.setDistanceSnapshot(new HashMap<>(dist));
                steps.add(checkStep);

                if (newDist < dist.get(neighbor)) {
                    int oldDist = dist.get(neighbor);
                    dist.put(neighbor, newDist); prev.put(neighbor, current);
                    Step relaxStep = new Step(StepType.RELAX, current,
                        "Relax! Update " + neighbor.getName() + ": " +
                        (oldDist == Integer.MAX_VALUE ? "\u221e" : oldDist) + " -> " + newDist);
                    relaxStep.setCurrentEdge(edge); relaxStep.setNeighborNode(neighbor);
                    relaxStep.setOldDistance(oldDist); relaxStep.setNewDistance(newDist);
                    relaxStep.setDistanceSnapshot(new HashMap<>(dist));
                    steps.add(relaxStep);
                    pq.remove(neighbor); pq.offer(neighbor);
                }
            }
            visited.add(current);
            Step visitStep = new Step(StepType.MARK_VISITED, current,
                "Node " + current.getName() + " confirmed, distance = " + dist.get(current));
            visitStep.setDistanceSnapshot(new HashMap<>(dist));
            steps.add(visitStep);
        }
        Step finishStep = new Step(StepType.FINISH, null, "Algorithm finished!");
        finishStep.setDistanceSnapshot(new HashMap<>(dist));
        steps.add(finishStep);
    }

    public void stepForward() {
        if (currentStepIndex < steps.size() - 1) { currentStepIndex++; applyStep(steps.get(currentStepIndex)); notifyListeners(); }
    }
    public void stepBackward() {
        if (currentStepIndex > 0) {
            currentStepIndex--; graph.resetStates();
            for (int i = 0; i <= currentStepIndex; i++) applyStep(steps.get(i));
            notifyListeners();
        }
    }
    private void applyStep(Step step) {
        switch (step.getType()) {
            case INIT: if (step.getCurrentNode() != null) { step.getCurrentNode().setDistance(0); step.getCurrentNode().setStart(true); } break;
            case SELECT_NODE: if (step.getCurrentNode() != null) step.getCurrentNode().setState(Node.State.CURRENT); break;
            case CHECK_EDGE: if (step.getCurrentEdge() != null) step.getCurrentEdge().setState(Edge.State.HIGHLIGHTED); break;
            case RELAX:
                if (step.getCurrentEdge() != null) step.getCurrentEdge().setState(Edge.State.RELAXED);
                if (step.getNeighborNode() != null) step.getNeighborNode().setDistance(step.getNewDistance());
                break;
            case MARK_VISITED: if (step.getCurrentNode() != null) step.getCurrentNode().setState(Node.State.VISITED); break;
            case FINISH: break;
        }
    }
    public void startAnimation() {
        if (isRunning) return; isRunning = true;
        animationThread = new Thread(() -> {
            while (isRunning && currentStepIndex < steps.size() - 1) {
                stepForward();
                try { Thread.sleep(delayMs); } catch (InterruptedException e) { break; }
            }
            isRunning = false; notifyListeners();
        });
        animationThread.start();
    }
    public void pauseAnimation() { isRunning = false; if (animationThread != null) animationThread.interrupt(); }
    public void reset() { pauseAnimation(); currentStepIndex = -1; graph.resetStates(); notifyListeners(); }
    public boolean isRunning() { return isRunning; }
    public int getCurrentStepIndex() { return currentStepIndex; }
    public int getTotalSteps() { return steps.size(); }
    public Step getCurrentStep() { return currentStepIndex >= 0 && currentStepIndex < steps.size() ? steps.get(currentStepIndex) : null; }
    public void setDelay(int delayMs) { this.delayMs = Math.max(100, Math.min(5000, delayMs)); }
    public interface AnimationListener { void onStepChanged(DijkstraAnimator animator); }
    public void addListener(AnimationListener l) { listeners.add(l); }
    private void notifyListeners() { for (AnimationListener l : listeners) l.onStepChanged(this); }
}

/* ============================================================
 *  GraphPanel
 * ============================================================ */
class GraphPanel extends JPanel {
    private Graph graph;
    private DijkstraAnimator animator;
    private enum Mode { EDIT_NODE, ADD_EDGE, SET_START, SET_TARGET, ANIMATE }
    private Mode currentMode = Mode.EDIT_NODE;
    private Node selectedNode = null, edgeStartNode = null, draggedNode = null;
    private Point dragOffset = null, mousePos = new Point(0, 0), tempLineEnd = null;
    private boolean bidirectionalEdge = false;
    private StatusCallback statusCallback;

    public GraphPanel(Graph graph) {
        this.graph = graph;
        this.animator = new DijkstraAnimator(graph);
        setBackground(new Color(245, 245, 250));
        setPreferredSize(new Dimension(800, 600));
        setupMouseListeners();
        setupKeyboardListeners();
    }

    private void setupMouseListeners() {
        MouseAdapter ma = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (currentMode == Mode.ANIMATE) return;
                Node clicked = graph.getNodeAt(e.getX(), e.getY());
                if (SwingUtilities.isLeftMouseButton(e)) handleLeftPress(e, clicked);
                else if (SwingUtilities.isRightMouseButton(e)) handleRightPress(e, clicked);
                repaint();
            }
            public void mouseReleased(MouseEvent e) {
                draggedNode = null; dragOffset = null;
                if (currentMode == Mode.ADD_EDGE && edgeStartNode != null) {
                    Node endNode = graph.getNodeAt(e.getX(), e.getY());
                    if (endNode != null && endNode != edgeStartNode) {
                        String w = JOptionPane.showInputDialog(GraphPanel.this,
                            "Edge weight " + edgeStartNode.getName() + " -> " + endNode.getName() + ":", "1");
                        if (w != null) {
                            try {
                                int weight = Integer.parseInt(w);
                                if (weight > 0) {
                                    int opt = JOptionPane.showConfirmDialog(GraphPanel.this,
                                        "Bidirectional edge?", "Edge Type", JOptionPane.YES_NO_OPTION);
                                    boolean bidi = (opt == JOptionPane.YES_OPTION);
                                    graph.addEdge(edgeStartNode, endNode, weight, bidi);
                                    updateStatus("Added edge: " + edgeStartNode.getName() +
                                        (bidi ? " <-> " : " -> ") + endNode.getName() + " (w=" + weight + ")");
                                }
                            } catch (NumberFormatException ex) { updateStatus("Invalid input"); }
                        }
                    }
                    edgeStartNode = null; tempLineEnd = null;
                }
                repaint();
            }
            public void mouseDragged(MouseEvent e) {
                mousePos = e.getPoint();
                if (draggedNode != null) { draggedNode.setX(e.getX()-dragOffset.x); draggedNode.setY(e.getY()-dragOffset.y); repaint(); }
                else if (currentMode == Mode.ADD_EDGE && edgeStartNode != null) { tempLineEnd = e.getPoint(); repaint(); }
            }
            public void mouseMoved(MouseEvent e) {
                mousePos = e.getPoint();
                if (currentMode == Mode.ADD_EDGE && edgeStartNode != null) { tempLineEnd = e.getPoint(); repaint(); }
            }
        };
        addMouseListener(ma); addMouseMotionListener(ma);
    }

    private void handleLeftPress(MouseEvent e, Node clicked) {
        switch (currentMode) {
            case EDIT_NODE:
                if (clicked != null) { draggedNode = clicked; dragOffset = new Point(e.getX()-clicked.getX(), e.getY()-clicked.getY()); selectedNode = clicked; }
                else {
                    String name = JOptionPane.showInputDialog(this, "Node name:", String.valueOf((char)('A'+graph.getNodeCount())));
                    if (name != null && !name.trim().isEmpty()) { selectedNode = graph.addNode(name.trim(), e.getX(), e.getY()); updateStatus("Added: "+name); }
                } break;
            case ADD_EDGE: if (clicked != null && edgeStartNode == null) { edgeStartNode = clicked; updateStatus("From: "+clicked.getName()); } break;
            case SET_START:
                if (clicked != null) { for (Node n : graph.getNodes()) n.setStart(false); clicked.setStart(true); updateStatus("Start: "+clicked.getName()); } break;
            case SET_TARGET:
                if (clicked != null) { clicked.setTarget(!clicked.isTarget()); updateStatus(clicked.isTarget() ? "Target: "+clicked.getName() : "Removed target: "+clicked.getName()); } break;
        }
    }

    private void handleRightPress(MouseEvent e, Node clicked) {
        if (clicked != null) {
            if (JOptionPane.showConfirmDialog(this, "Delete "+clicked.getName()+"?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                { graph.removeNode(clicked); selectedNode = null; updateStatus("Deleted: "+clicked.getName()); }
        }
    }

    private void setupKeyboardListeners() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() { public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE && selectedNode != null) { graph.removeNode(selectedNode); selectedNode = null; repaint(); }
        }});
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g); Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(220,220,230));
        for (int x=0; x<getWidth(); x+=30) g2d.drawLine(x,0,x,getHeight());
        for (int y=0; y<getHeight(); y+=30) g2d.drawLine(0,y,getWidth(),y);
        if (currentMode==Mode.ADD_EDGE && edgeStartNode!=null && tempLineEnd!=null) {
            g2d.setColor(new Color(255,165,0,180)); g2d.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,0,new float[]{8,4},0));
            g2d.drawLine(edgeStartNode.getX(), edgeStartNode.getY(), tempLineEnd.x, tempLineEnd.y);
        }
        for (Edge e : graph.getEdges()) e.draw(g2d);
        for (Node n : graph.getNodes()) n.draw(g2d);
        if (selectedNode!=null && currentMode==Mode.EDIT_NODE) {
            g2d.setColor(new Color(255,215,0)); g2d.setStroke(new BasicStroke(2));
            int r = selectedNode.getRadius()+4;
            g2d.drawOval(selectedNode.getX()-r, selectedNode.getY()-r, r*2, r*2);
        }
    }

    public void setMode(String mode) {
        switch (mode) { case "node": currentMode=Mode.EDIT_NODE; break; case "edge": currentMode=Mode.ADD_EDGE; edgeStartNode=null; break;
            case "start": currentMode=Mode.SET_START; break; case "target": currentMode=Mode.SET_TARGET; break; case "animate": currentMode=Mode.ANIMATE; break; }
        repaint();
    }
    public DijkstraAnimator getAnimator() { return animator; }
    public Graph getGraph() { return graph; }
    public void setStatusCallback(StatusCallback cb) { this.statusCallback = cb; }
    private void updateStatus(String msg) { if (statusCallback!=null) statusCallback.onStatusUpdate(msg); }
    public interface StatusCallback { void onStatusUpdate(String message); }
}

/* ============================================================
 *  DijkstraVisualizer (Main Window)
 * ============================================================ */
class DijkstraVisualizer extends JFrame {
    private Graph graph;
    private GraphPanel graphPanel;
    private DijkstraAnimator animator;
    private JLabel statusLabel, stepLabel;
    private JSlider speedSlider;
    private JButton playBtn, pauseBtn, nextBtn, prevBtn, resetBtn;
    private JTable distanceTable;
    private DefaultTableModel tableModel;
    private JTextArea logArea;
    private JLabel stepTypeLabel, stepDescLabel;
    private JProgressBar progressBar;

    public DijkstraVisualizer() {
        setTitle("Dijkstra Algorithm Visualization");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        graph = new Graph();
        createSampleGraph();
        graphPanel = new GraphPanel(graph);
        animator = graphPanel.getAnimator();
        animator.addListener(a -> updateUI());
        graphPanel.setStatusCallback(msg -> statusLabel.setText(msg));
        add(createToolbar(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
        pack(); setLocationRelativeTo(null); setMinimumSize(new Dimension(1100, 700));
        updateUI();
    }

    private void createSampleGraph() { createGraphWithNodes(6); }

    private void createGraphWithNodes(int nodeCount) {
        graph.clear();
        int panelW = 800, panelH = 600;
        double centerX = panelW/2, centerY = panelH/2;
        double radius = Math.min(panelW, panelH)/2 - 80;
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            double angle = 2*Math.PI*i/nodeCount - Math.PI/2;
            nodes.add(graph.addNode(String.valueOf((char)('A'+i)),
                (int)(centerX+radius*Math.cos(angle)), (int)(centerY+radius*Math.sin(angle))));
        }
        Random rand = new Random(1);
        for (int i = 0; i < nodeCount+rand.nextInt(nodeCount); i++) {
            int fi = rand.nextInt(nodeCount), ti = rand.nextInt(nodeCount);
            if (fi!=ti && graph.getEdge(nodes.get(fi),nodes.get(ti))==null)
                graph.addEdge(nodes.get(fi), nodes.get(ti), 1+rand.nextInt(10));
        }
        for (int i = 0; i < nodeCount-1; i++)
            if (graph.getEdgesFrom(nodes.get(i)).isEmpty() && graph.getEdge(nodes.get(i),nodes.get((i+1)%nodeCount))==null)
                graph.addEdge(nodes.get(i), nodes.get((i+1)%nodeCount), 1+rand.nextInt(5));
        if (!nodes.isEmpty()) nodes.get(0).setStart(true);
        if (nodes.size()>1) nodes.get(nodes.size()-1).setTarget(true);
    }

    private JPanel createToolbar() {
        JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        tb.setBorder(new EmptyBorder(5,10,5,10)); tb.setBackground(new Color(240,240,245));
        JPanel ncp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ncp.add(new JLabel("Nodes:"));
        JSpinner sp = new JSpinner(new SpinnerNumberModel(6,1,26,1));
        sp.setPreferredSize(new Dimension(60,25)); ncp.add(sp);
        JButton applyBtn = new JButton("Apply");
        applyBtn.addActionListener(e -> { createGraphWithNodes((Integer)sp.getValue()); animator.reset(); graphPanel.repaint(); updateUI(); });
        ncp.add(applyBtn); tb.add(ncp);
        tb.add(new JSeparator(SwingConstants.VERTICAL));
        ButtonGroup mg = new ButtonGroup();
        JToggleButton nodeBtn=new JToggleButton("Edit"), edgeBtn=new JToggleButton("Add Edge"),
                       startBtn=new JToggleButton("Set Start"), targetBtn=new JToggleButton("Set Target");
        nodeBtn.setSelected(true); mg.add(nodeBtn); mg.add(edgeBtn); mg.add(startBtn); mg.add(targetBtn);
        nodeBtn.addActionListener(e->graphPanel.setMode("node"));
        edgeBtn.addActionListener(e->graphPanel.setMode("edge"));
        startBtn.addActionListener(e->graphPanel.setMode("start"));
        targetBtn.addActionListener(e->graphPanel.setMode("target"));
        tb.add(nodeBtn); tb.add(edgeBtn); tb.add(startBtn); tb.add(targetBtn);
        tb.add(new JSeparator(SwingConstants.VERTICAL));
        playBtn=ctrlBtn("Play",new Color(50,150,50));
        pauseBtn=ctrlBtn("Pause",new Color(200,150,50));
        nextBtn=ctrlBtn("Next",new Color(50,100,200));
        prevBtn=ctrlBtn("Prev",new Color(50,100,200));
        resetBtn=ctrlBtn("Reset",new Color(200,50,50));
        JButton srcBtn=ctrlBtn("Source",new Color(100,100,150));
        srcBtn.addActionListener(e->showSourceCodeDialog());
        tb.add(playBtn); tb.add(pauseBtn); tb.add(prevBtn); tb.add(nextBtn); tb.add(resetBtn); tb.add(srcBtn);
        tb.add(new JSeparator(SwingConstants.VERTICAL));
        tb.add(new JLabel("Speed:"));
        speedSlider=new JSlider(100,2000,1000); speedSlider.setInverted(true); speedSlider.setPreferredSize(new Dimension(120,25));
        speedSlider.addChangeListener(e->animator.setDelay(speedSlider.getValue())); tb.add(speedSlider);
        JButton randBtn=new JButton("Random"); randBtn.addActionListener(e->generateRandomGraph()); tb.add(randBtn);
        JButton clearBtn=new JButton("Clear"); clearBtn.addActionListener(e->{graph.clear();animator.reset();graphPanel.repaint();updateUI();}); tb.add(clearBtn);

        playBtn.addActionListener(e->{
            Node s=getStartNode(); if(s==null){JOptionPane.showMessageDialog(this,"Set a start node!");return;}
            List<Node> targets=getTargetNodes(); if(targets.isEmpty()){JOptionPane.showMessageDialog(this,"Set at least one target!");return;}
            Map<Node,Integer> d=runDijkstra(s);
            StringBuilder unreachable=new StringBuilder();
            for(Node t:targets) if(d.get(t)==Integer.MAX_VALUE) { if(unreachable.length()>0) unreachable.append(", "); unreachable.append(t.getName()); }
            if(unreachable.length()>0) JOptionPane.showMessageDialog(this,"Warning: "+s.getName()+" cannot reach: "+unreachable);
            if(animator.getCurrentStepIndex()==-1) animator.prepareAnimation(s);
            graphPanel.setMode("animate"); animator.startAnimation(); updateUI();
        });
        pauseBtn.addActionListener(e->{animator.pauseAnimation();updateUI();});
        nextBtn.addActionListener(e->{
            Node s=getStartNode(); if(s==null){JOptionPane.showMessageDialog(this,"Set a start node!");return;}
            if(getTargetNodes().isEmpty()){JOptionPane.showMessageDialog(this,"Set at least one target!");return;}
            if(animator.getCurrentStepIndex()==-1) animator.prepareAnimation(s);
            graphPanel.setMode("animate"); animator.stepForward(); updateUI();
        });
        prevBtn.addActionListener(e->{animator.stepBackward();updateUI();});
        resetBtn.addActionListener(e->{animator.reset();graphPanel.setMode("node");nodeBtn.setSelected(true);updateUI();});
        return tb;
    }

    private JButton ctrlBtn(String text, Color c) {
        JButton b=new JButton(text); b.setFocusPainted(false); b.setBackground(c); b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial",Font.BOLD,12)); return b;
    }

    private JPanel createMainPanel() {
        JPanel mp=new JPanel(new BorderLayout(10,10)); mp.setBorder(new EmptyBorder(0,10,0,10));
        JPanel gc=new JPanel(new BorderLayout());
        gc.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(),"Graph Editor",TitledBorder.LEFT,TitledBorder.TOP,new Font("Arial",Font.BOLD,14)));
        gc.add(graphPanel,BorderLayout.CENTER);
        JPanel info=new JPanel(new BorderLayout(5,5)); info.setPreferredSize(new Dimension(300,0));
        JPanel sip=new JPanel(new GridLayout(3,1,5,5));
        sip.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(),"Current Step",TitledBorder.LEFT,TitledBorder.TOP,new Font("Arial",Font.BOLD,14)));
        sip.setBackground(new Color(250,250,255));
        stepTypeLabel=new JLabel("Not Started",SwingConstants.CENTER); stepTypeLabel.setFont(new Font("Arial",Font.BOLD,16)); stepTypeLabel.setForeground(new Color(50,50,150));
        stepDescLabel=new JLabel("Click Play or Next to start",SwingConstants.CENTER); stepDescLabel.setFont(new Font("Arial",Font.PLAIN,12));
        progressBar=new JProgressBar(0,100); progressBar.setStringPainted(true);
        sip.add(stepTypeLabel); sip.add(stepDescLabel); sip.add(progressBar);

        String[] cols={"Node","Distance","State"};
        tableModel=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        distanceTable=new JTable(tableModel); distanceTable.setFont(new Font("Arial",Font.PLAIN,12)); distanceTable.setRowHeight(25);
        distanceTable.getTableHeader().setFont(new Font("Arial",Font.BOLD,12));
        distanceTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean f,int r,int c){
                Component comp=super.getTableCellRendererComponent(t,v,sel,f,r,c);
                if(!sel){ comp.setBackground(r%2==0?Color.WHITE:new Color(245,245,250));
                    if(c==0&&r<graph.getNodes().size()){ Node n=graph.getNodes().get(r);
                        if(n.isStart()){setForeground(new Color(220,20,60));setFont(getFont().deriveFont(Font.BOLD));}
                        else if(n.isTarget()){setForeground(Color.BLACK);setFont(getFont().deriveFont(Font.BOLD));}
                        else{setForeground(Color.BLACK);setFont(getFont().deriveFont(Font.PLAIN));}
                    } else{setForeground(Color.BLACK);setFont(getFont().deriveFont(Font.PLAIN));}
                } return comp;
            }
        });
        JScrollPane ts=new JScrollPane(distanceTable);
        ts.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(),"Distance Table",TitledBorder.LEFT,TitledBorder.TOP,new Font("Arial",Font.BOLD,14)));
        ts.setPreferredSize(new Dimension(0,250));
        logArea=new JTextArea(8,20); logArea.setFont(new Font("Arial",Font.PLAIN,11)); logArea.setEditable(false);
        logArea.setLineWrap(true); logArea.setWrapStyleWord(true); logArea.setBackground(new Color(250,250,250));
        JScrollPane ls=new JScrollPane(logArea);
        ls.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(),"Log",TitledBorder.LEFT,TitledBorder.TOP,new Font("Arial",Font.BOLD,14)));
        info.add(sip,BorderLayout.NORTH); info.add(ts,BorderLayout.CENTER); info.add(ls,BorderLayout.SOUTH);
        mp.add(gc,BorderLayout.CENTER); mp.add(info,BorderLayout.EAST);
        return mp;
    }

    private JPanel createStatusBar() {
        JPanel sb=new JPanel(new BorderLayout(10,0)); sb.setBorder(new EmptyBorder(5,10,5,10)); sb.setBackground(new Color(230,230,235));
        statusLabel=new JLabel("Ready"); statusLabel.setFont(new Font("Arial",Font.PLAIN,12));
        stepLabel=new JLabel("Step: 0/0"); stepLabel.setFont(new Font("Arial",Font.PLAIN,12));
        sb.add(statusLabel,BorderLayout.WEST); sb.add(stepLabel,BorderLayout.EAST);
        return sb;
    }

    private Node getStartNode(){for(Node n:graph.getNodes())if(n.isStart())return n;return null;}
    private List<Node> getTargetNodes(){List<Node> t=new ArrayList<>();for(Node n:graph.getNodes())if(n.isTarget())t.add(n);return t;}

    private Map<Node,Integer> runDijkstra(Node start){
        Map<Node,Integer> dist=new HashMap<>(); Set<Node> visited=new HashSet<>();
        PriorityQueue<Node> pq=new PriorityQueue<>(Comparator.comparingInt(dist::get));
        for(Node n:graph.getNodes()) dist.put(n,Integer.MAX_VALUE);
        dist.put(start,0); for(Node n:graph.getNodes()) pq.offer(n);
        while(!pq.isEmpty()){
            Node cur=pq.poll(); if(visited.contains(cur)||dist.get(cur)==Integer.MAX_VALUE) continue;
            visited.add(cur);
            for(Edge e:graph.getEdgesFrom(cur)){ Node nb=e.getTo(); if(visited.contains(nb)) continue;
                int nd=dist.get(cur)+e.getWeight(); if(nd<dist.get(nb)){dist.put(nb,nd);pq.remove(nb);pq.offer(nb);}
            }
        } return dist;
    }

    private void generateRandomGraph(){
        graph.clear(); animator.reset(); Random rand=new Random();
        int nc=5+rand.nextInt(4); int pw=graphPanel.getWidth()>0?graphPanel.getWidth():800, ph=graphPanel.getHeight()>0?graphPanel.getHeight():600;
        List<Node> nodes=new ArrayList<>();
        for(int i=0;i<nc;i++){ int x,y,att=0; boolean ok;
            do{x=80+rand.nextInt(pw-160);y=80+rand.nextInt(ph-160);ok=true;for(Node n:nodes)if((x-n.getX())*(x-n.getX())+(y-n.getY())*(y-n.getY())<10000){ok=false;break;}att++;}while(!ok&&att<50);
            nodes.add(graph.addNode(String.valueOf((char)('A'+i)),x,y));
        }
        for(int i=0;i<nc+rand.nextInt(nc);i++){Node f=nodes.get(rand.nextInt(nc)),t=nodes.get(rand.nextInt(nc));
            if(f!=t&&graph.getEdge(f,t)==null) graph.addEdge(f,t,1+rand.nextInt(15));}
        for(int i=0;i<nc-1;i++) if(graph.getEdgesFrom(nodes.get(i)).isEmpty()&&graph.getEdge(nodes.get(i),nodes.get(i+1))==null)
            graph.addEdge(nodes.get(i),nodes.get(i+1),1+rand.nextInt(10));
        nodes.get(0).setStart(true); if(nc>1) nodes.get(nc-1).setTarget(true);
        graphPanel.repaint(); updateUI();
    }

    private void updateUI(){SwingUtilities.invokeLater(()->{
        DijkstraAnimator.Step step=animator.getCurrentStep();
        if(step!=null){stepTypeLabel.setText(step.getType().getTitle());stepDescLabel.setText("<html><center>"+step.getMessage()+"</center></html>");
            if(animator.getTotalSteps()>0){int p=(int)((animator.getCurrentStepIndex()+1)*100.0/animator.getTotalSteps());progressBar.setValue(p);progressBar.setString((animator.getCurrentStepIndex()+1)+" / "+animator.getTotalSteps());}
            logArea.append("["+step.getType().getTitle()+"] "+step.getMessage()+"\n");logArea.setCaretPosition(logArea.getDocument().getLength());
        } else{stepTypeLabel.setText("Not Started");stepDescLabel.setText("Click Play or Next to start");progressBar.setValue(0);progressBar.setString("0/0");}
        stepLabel.setText("Step: "+(animator.getCurrentStepIndex()+1)+" / "+animator.getTotalSteps());
        updateDistanceTable(step);
        playBtn.setEnabled(!animator.isRunning());pauseBtn.setEnabled(animator.isRunning());
        nextBtn.setEnabled(!animator.isRunning());prevBtn.setEnabled(!animator.isRunning()&&animator.getCurrentStepIndex()>0);
        graphPanel.repaint();
    });}

    private void updateDistanceTable(DijkstraAnimator.Step step){
        tableModel.setRowCount(0);
        if(step==null||step.getDistanceSnapshot()==null){for(Node n:graph.getNodes()){
            String d=n.isStart()?"0":"\u221e"; String s=n.isStart()?"Start":n.isTarget()?"Target":"Unvisited";
            tableModel.addRow(new Object[]{n.getName(),d,s});} return;}
        Map<Node,Integer> snap=step.getDistanceSnapshot();
        for(Node n:graph.getNodes()){int d=snap.getOrDefault(n,Integer.MAX_VALUE);String ds=d==Integer.MAX_VALUE?"\u221e":String.valueOf(d);
            String s=""; if(n.isStart())s="Start"; else if(n.isTarget()){if(n.getState()==Node.State.VISITED)s="Target(Confirmed)";
                else if(n.getState()==Node.State.CURRENT)s="Target(Current)";else if(d!=Integer.MAX_VALUE)s="Target(Updated)";else s="Target";}
            else if(n.getState()==Node.State.VISITED)s="Confirmed";else if(n.getState()==Node.State.CURRENT)s="Current";
            else if(d!=Integer.MAX_VALUE)s="Updated";else s="Unvisited";
            tableModel.addRow(new Object[]{n.getName(),ds,s});}
    }

    private void showSourceCodeDialog(){
        String src="Dijkstra Algorithm Core Implementation\n"+"========================================\n\n"+
        "public void dijkstra(Node start) {\n"+"    // 1. Initialize distance array\n"+
        "    Map<Node, Integer> dist = new HashMap<>();\n"+"    Map<Node, Node> prev = new HashMap<>();\n"+
        "    \n"+"    for (Node node : nodes) {\n"+"        dist.put(node, Integer.MAX_VALUE);  // Init to infinity\n"+
        "        prev.put(node, null);\n"+"    }\n"+"    dist.put(start, 0);  // Start distance is 0\n"+
        "    \n"+"    // 2. Use priority queue (min-heap)\n"+
        "    PriorityQueue<Node> pq = new PriorityQueue<>(\n"+"        Comparator.comparingInt(dist::get));\n"+
        "    \n"+"    for (Node node : nodes) { pq.offer(node); }\n"+"    \n"+
        "    Set<Node> visited = new HashSet<>();\n"+"    \n"+"    // 3. Main loop\n"+
        "    while (!pq.isEmpty()) {\n"+"        Node u = pq.poll();\n"+
        "        // Skip visited or unreachable nodes\n"+
        "        if (visited.contains(u) || dist.get(u) == Integer.MAX_VALUE) continue;\n"+
        "        visited.add(u);\n"+"        \n"+"        // 4. Relaxation\n"+
        "        for (Edge edge : getEdgesFrom(u)) {\n"+"            Node v = edge.getTo();\n"+
        "            if (visited.contains(v)) continue;\n"+"            int newDist = dist.get(u) + edge.getWeight();\n"+
        "            if (newDist < dist.get(v)) {\n"+"                dist.put(v, newDist);\n"+"                prev.put(v, u);\n"+
        "                // Update priority queue\n"+"                pq.remove(v); pq.offer(v);\n"+
        "            }\n"+"        }\n"+"    }\n"+"}\n\n"+
        "Complexity Analysis\n"+"===================\n"+"- Time: O((V+E)logV)\n"+"- Space: O(V)\n\n"+
        "Constraints\n"+"===========\n"+"- All edge weights must be non-negative\n"+"- Works for directed & undirected graphs\n"+
        "- Single-source shortest path";
        JDialog dlg=new JDialog(this,"Dijkstra Algorithm Source Code",true);
        dlg.setSize(700,600); dlg.setLocationRelativeTo(this);
        JPanel p=new JPanel(new BorderLayout(10,10)); p.setBorder(new EmptyBorder(10,10,10,10));
        JTextArea ta=new JTextArea(src); ta.setFont(new Font("Consolas",Font.PLAIN,14)); ta.setEditable(false);
        JScrollPane sp=new JScrollPane(ta); sp.setPreferredSize(new Dimension(680,500));
        JPanel bp=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton copyBtn=new JButton("Copy"); copyBtn.setBackground(new Color(50,150,50)); copyBtn.setForeground(Color.WHITE); copyBtn.setFocusPainted(false);
        copyBtn.addActionListener(e->{ta.selectAll();ta.copy();JOptionPane.showMessageDialog(dlg,"Copied!");});
        JButton closeBtn=new JButton("Close"); closeBtn.setBackground(new Color(150,150,150)); closeBtn.setForeground(Color.WHITE); closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e->dlg.dispose());
        bp.add(copyBtn); bp.add(closeBtn);
        p.add(sp,BorderLayout.CENTER); p.add(bp,BorderLayout.SOUTH);
        dlg.add(p); dlg.setVisible(true);
    }
}
