package dijkstra;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

/**
 * 图中的节点类
 */
public class Node {
    private int id;
    private String name;
    private int x, y;
    private int radius = 25;

    // 节点状态
    public enum State {
        UNVISITED(new Color(100, 149, 237)),   // 未访问 - 矢车菊蓝
        CURRENT(new Color(255, 165, 0)),        // 当前处理 - 橙色
        VISITED(new Color(50, 205, 50)),        // 已确定最短路径 - 酸橙绿
        START(new Color(220, 20, 60)),          // 起点 - 猩红
        TARGET(new Color(128, 0, 128));         // 终点 - 紫色

        private final Color color;

        State(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

    private State state = State.UNVISITED;
    private int distance = Integer.MAX_VALUE;
    private boolean isStart = false;
    private boolean isTarget = false;
    private boolean isHighlighted = false;

    public Node(int id, String name, int x, int y) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制阴影
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillOval(x - radius + 3, y - radius + 3, radius * 2, radius * 2);

        // 绘制节点主体
        Color baseColor = isStart ? State.START.color :
                         isTarget ? State.TARGET.color : state.color;

        // 外圈高光
        g2d.setColor(baseColor.brighter());
        g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        // 内圈主体
        g2d.setColor(baseColor);
        g2d.fillOval(x - radius + 2, y - radius + 2, (radius - 2) * 2, (radius - 2) * 2);

        // 边框
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new java.awt.BasicStroke(2));
        g2d.drawOval(x - radius, y - radius, radius * 2, radius * 2);

        // 绘制节点名称
        g2d.setColor(Color.WHITE);
        g2d.setFont(new java.awt.Font("微软雅黑", java.awt.Font.BOLD, 14));
        java.awt.FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(name);
        int textHeight = fm.getHeight();
        g2d.drawString(name, x - textWidth / 2, y + textHeight / 4);

        // 绘制距离值（如果不是无穷大）
        if (distance != Integer.MAX_VALUE) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new java.awt.Font("微软雅黑", java.awt.Font.BOLD, 11));
            fm = g2d.getFontMetrics();
            String distStr = String.valueOf(distance);
            int distWidth = fm.stringWidth(distStr);
            g2d.drawString(distStr, x - distWidth / 2, y - radius - 8);
        } else {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new java.awt.Font("微软雅黑", java.awt.Font.BOLD, 11));
            fm = g2d.getFontMetrics();
            String infStr = "∞";
            int infWidth = fm.stringWidth(infStr);
            g2d.drawString(infStr, x - infWidth / 2, y - radius - 8);
        }
    }

    public boolean contains(Point p) {
        int dx = p.x - x;
        int dy = p.y - y;
        return dx * dx + dy * dy <= radius * radius;
    }

    public boolean contains(int px, int py) {
        int dx = px - x;
        int dy = py - y;
        return dx * dx + dy * dy <= radius * radius;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return name;
    }
}
