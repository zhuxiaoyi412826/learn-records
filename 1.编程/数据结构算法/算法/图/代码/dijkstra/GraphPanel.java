package dijkstra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 图的可视化面板，支持拖拽节点、添加边等交互
 */
public class GraphPanel extends JPanel {

    private Graph graph;
    private DijkstraAnimator animator;

    // 交互状态
    private enum Mode {
        EDIT_NODE,      // 编辑节点（拖拽）
        ADD_EDGE,       // 添加边
        SET_START,      // 设置起点
        SET_TARGET,     // 设置终点
        ANIMATE         // 动画模式（只读）
    }

    private Mode currentMode = Mode.EDIT_NODE;
    private Node selectedNode = null;
    private Node edgeStartNode = null;
    private Node draggedNode = null;
    private Point dragOffset = null;
    private Point mousePos = new Point(0, 0);

    // 临时绘制线（添加边时）
    private Point tempLineEnd = null;

    // 边类型（单向/双向）
    private boolean bidirectionalEdge = false;

    // 状态回调
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
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentMode == Mode.ANIMATE) return;

                Node clickedNode = graph.getNodeAt(e.getX(), e.getY());

                if (SwingUtilities.isLeftMouseButton(e)) {
                    handleLeftPress(e, clickedNode);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    handleRightPress(e, clickedNode);
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedNode = null;
                dragOffset = null;

                if (currentMode == Mode.ADD_EDGE && edgeStartNode != null) {
                    Node endNode = graph.getNodeAt(e.getX(), e.getY());
                    if (endNode != null && endNode != edgeStartNode) {
                        // 弹出对话框输入权重和边类型
                        Window ancestor = SwingUtilities.getWindowAncestor(GraphPanel.this);
                        JDialog inputDialog;
                        if (ancestor instanceof Frame) {
                            inputDialog = new JDialog((Frame) ancestor, "边设置", true);
                        } else if (ancestor instanceof Dialog) {
                            inputDialog = new JDialog((Dialog) ancestor, "边设置", true);
                        } else {
                            inputDialog = new JDialog();
                            inputDialog.setTitle("边设置");
                            inputDialog.setModal(true);
                        }
                        inputDialog.setLayout(new BoxLayout(inputDialog.getContentPane(), BoxLayout.Y_AXIS));
                        inputDialog.setSize(300, 180);
                        inputDialog.setLocationRelativeTo(GraphPanel.this);

                        JPanel weightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        weightPanel.add(new JLabel("权重:"));
                        JTextField weightField = new JTextField(10);
                        weightField.setText("1");
                        weightPanel.add(weightField);

                        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        JCheckBox bidirectionalCheck = new JCheckBox("双向边");
                        bidirectionalCheck.setSelected(bidirectionalEdge);
                        typePanel.add(bidirectionalCheck);

                        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                        JButton okBtn = new JButton("确定");
                        JButton cancelBtn = new JButton("取消");
                        buttonPanel.add(okBtn);
                        buttonPanel.add(cancelBtn);

                        inputDialog.add(weightPanel);
                        inputDialog.add(typePanel);
                        inputDialog.add(buttonPanel);

                        final boolean[] confirmed = {false};
                        okBtn.addActionListener(ev -> {
                            confirmed[0] = true;
                            bidirectionalEdge = bidirectionalCheck.isSelected();
                            inputDialog.dispose();
                        });
                        cancelBtn.addActionListener(ev -> {
                            inputDialog.dispose();
                        });

                        inputDialog.setVisible(true);

                        if (confirmed[0]) {
                            try {
                                int weight = Integer.parseInt(weightField.getText());
                                if (weight > 0) {
                                    graph.addEdge(edgeStartNode, endNode, weight, bidirectionalEdge);
                                    String typeStr = bidirectionalEdge ? "双向" : "单向";
                                    updateStatus("已添加" + typeStr + "边: " + edgeStartNode.getName() + " " + (bidirectionalEdge ? "<->" : "->") + " " + endNode.getName() + " (权重: " + weight + ")");
                                }
                            } catch (NumberFormatException ex) {
                                updateStatus("输入无效，边未添加");
                            }
                        }
                    }
                    edgeStartNode = null;
                    tempLineEnd = null;
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mousePos = e.getPoint();

                if (draggedNode != null) {
                    draggedNode.setX(e.getX() - dragOffset.x);
                    draggedNode.setY(e.getY() - dragOffset.y);
                    repaint();
                } else if (currentMode == Mode.ADD_EDGE && edgeStartNode != null) {
                    tempLineEnd = e.getPoint();
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mousePos = e.getPoint();
                if (currentMode == Mode.ADD_EDGE && edgeStartNode != null) {
                    tempLineEnd = e.getPoint();
                    repaint();
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private void handleLeftPress(MouseEvent e, Node clickedNode) {
        switch (currentMode) {
            case EDIT_NODE:
                if (clickedNode != null) {
                    draggedNode = clickedNode;
                    dragOffset = new Point(
                        e.getX() - clickedNode.getX(),
                        e.getY() - clickedNode.getY()
                    );
                    selectedNode = clickedNode;
                } else {
                    // 添加新节点
                    String name = JOptionPane.showInputDialog(
                        this,
                        "请输入节点名称:",
                        String.valueOf((char) ('A' + graph.getNodeCount()))
                    );
                    if (name != null && !name.trim().isEmpty()) {
                        Node newNode = graph.addNode(name.trim(), e.getX(), e.getY());
                        selectedNode = newNode;
                        updateStatus("已添加节点: " + name);
                    }
                }
                break;

            case ADD_EDGE:
                if (clickedNode != null) {
                    if (edgeStartNode == null) {
                        edgeStartNode = clickedNode;
                        updateStatus("选择起点: " + clickedNode.getName() + "，请点击终点节点");
                    }
                }
                break;

            case SET_START:
                if (clickedNode != null) {
                    // 清除之前的起点
                    for (Node n : graph.getNodes()) {
                        n.setStart(false);
                    }
                    clickedNode.setStart(true);
                    updateStatus("设置起点: " + clickedNode.getName());
                }
                break;

            case SET_TARGET:
                if (clickedNode != null) {
                    // 切换终点状态（支持多个终点）
                    clickedNode.setTarget(!clickedNode.isTarget());
                    if (clickedNode.isTarget()) {
                        updateStatus("已添加终点: " + clickedNode.getName());
                    } else {
                        updateStatus("已移除终点: " + clickedNode.getName());
                    }
                }
                break;
        }
    }

    private void handleRightPress(MouseEvent e, Node clickedNode) {
        if (clickedNode != null) {
            // 右键删除节点
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要删除节点 " + clickedNode.getName() + " 吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                graph.removeNode(clickedNode);
                selectedNode = null;
                updateStatus("已删除节点: " + clickedNode.getName());
            }
        } else {
            // 检查是否点击了边（简化：显示所有边菜单）
            showEdgeContextMenu(e);
        }
    }

    private void showEdgeContextMenu(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem deleteEdgeItem = new JMenuItem("删除边...");
        deleteEdgeItem.addActionListener(ev -> {
            List<Edge> edges = graph.getEdges();
            if (edges.isEmpty()) {
                JOptionPane.showMessageDialog(this, "图中没有边");
                return;
            }
            Edge selected = (Edge) JOptionPane.showInputDialog(
                this,
                "选择要删除的边:",
                "删除边",
                JOptionPane.QUESTION_MESSAGE,
                null,
                edges.toArray(),
                edges.get(0)
            );
            if (selected != null) {
                graph.removeEdge(selected);
                updateStatus("已删除边: " + selected);
                repaint();
            }
        });
        menu.add(deleteEdgeItem);
        menu.show(this, e.getX(), e.getY());
    }

    private void setupKeyboardListeners() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE && selectedNode != null) {
                    graph.removeNode(selectedNode);
                    selectedNode = null;
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制网格背景
        drawGrid(g2d);

        // 绘制临时边（添加边时的预览）
        if (currentMode == Mode.ADD_EDGE && edgeStartNode != null && tempLineEnd != null) {
            g2d.setColor(new Color(255, 165, 0, 180));
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    0, new float[]{8, 4}, 0));
            g2d.drawLine(edgeStartNode.getX(), edgeStartNode.getY(), tempLineEnd.x, tempLineEnd.y);
        }

        // 绘制边
        for (Edge edge : graph.getEdges()) {
            edge.draw(g2d);
        }

        // 绘制节点
        for (Node node : graph.getNodes()) {
            node.draw(g2d);
        }

        // 绘制选中高亮
        if (selectedNode != null && currentMode == Mode.EDIT_NODE) {
            g2d.setColor(new Color(255, 215, 0));
            g2d.setStroke(new BasicStroke(2));
            int r = selectedNode.getRadius() + 4;
            g2d.drawOval(selectedNode.getX() - r, selectedNode.getY() - r, r * 2, r * 2);
        }

        // 绘制模式提示
        drawModeHint(g2d);
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(new Color(220, 220, 230));
        int gridSize = 30;
        for (int x = 0; x < getWidth(); x += gridSize) {
            g2d.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += gridSize) {
            g2d.drawLine(0, y, getWidth(), y);
        }
    }

    private void drawModeHint(Graphics2D g2d) {
        g2d.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        g2d.setColor(new Color(100, 100, 100));
        String hint = "当前模式: ";
        switch (currentMode) {
            case EDIT_NODE: hint += "编辑节点 (左键添加/拖拽，右键删除)"; break;
            case ADD_EDGE: hint += "添加边 (左键选择起点和终点)"; break;
            case SET_START: hint += "设置起点 (左键点击节点)"; break;
            case SET_TARGET: hint += "设置终点 (左键点击节点)"; break;
            case ANIMATE: hint += "动画演示 (只读模式)"; break;
        }
        g2d.drawString(hint, 10, getHeight() - 10);
    }

    public void setMode(String mode) {
        switch (mode) {
            case "node": currentMode = Mode.EDIT_NODE; break;
            case "edge": currentMode = Mode.ADD_EDGE; edgeStartNode = null; break;
            case "start": currentMode = Mode.SET_START; break;
            case "target": currentMode = Mode.SET_TARGET; break;
            case "animate": currentMode = Mode.ANIMATE; break;
        }
        repaint();
    }

    public DijkstraAnimator getAnimator() {
        return animator;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setStatusCallback(StatusCallback callback) {
        this.statusCallback = callback;
    }

    private void updateStatus(String message) {
        if (statusCallback != null) {
            statusCallback.onStatusUpdate(message);
        }
    }

    public interface StatusCallback {
        void onStatusUpdate(String message);
    }
}
