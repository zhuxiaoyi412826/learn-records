package dijkstra;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;

/**
 * 图中的边类
 */
public class Edge {
    private Node from;
    private Node to;
    private int weight;

    // 边状态
    public enum State {
        NORMAL(new Color(150, 150, 150), 2),      // 普通边
        HIGHLIGHTED(new Color(255, 215, 0), 4),   // 高亮（当前考察）
        IN_PATH(new Color(220, 20, 60), 4),       // 最短路径中的边
        RELAXED(new Color(50, 205, 50), 3);       // 已松弛

        private final Color color;
        private final int strokeWidth;

        State(Color color, int strokeWidth) {
            this.color = color;
            this.strokeWidth = strokeWidth;
        }

        public Color getColor() { return color; }
        public int getStrokeWidth() { return strokeWidth; }
    }

    private State state = State.NORMAL;

    public Edge(Node from, Node to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public void draw(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x1 = from.getX();
        int y1 = from.getY();
        int x2 = to.getX();
        int y2 = to.getY();

        // 计算方向向量
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);
        double unitX = dx / length;
        double unitY = dy / length;

        // 起点和终点偏移（不画进圆内）
        int startX = (int) (x1 + unitX * from.getRadius());
        int startY = (int) (y1 + unitY * from.getRadius());
        int endX = (int) (x2 - unitX * to.getRadius());
        int endY = (int) (y2 - unitY * to.getRadius());

        // 绘制边
        g2d.setColor(state.getColor());
        g2d.setStroke(new BasicStroke(state.getStrokeWidth(),
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(startX, startY, endX, endY);

        // 绘制箭头
        drawArrow(g2d, startX, startY, endX, endY);

        // 绘制权重标签背景
        int midX = (startX + endX) / 2;
        int midY = (startY + endY) / 2;

        String weightStr = String.valueOf(weight);
        g2d.setFont(new java.awt.Font("微软雅黑", java.awt.Font.BOLD, 12));
        java.awt.FontMetrics fm = g2d.getFontMetrics();
        int strWidth = fm.stringWidth(weightStr);
        int strHeight = fm.getHeight();

        // 白色背景
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.fillRoundRect(midX - strWidth / 2 - 4, midY - strHeight / 2 - 2,
                         strWidth + 8, strHeight + 4, 6, 6);

        // 权重文字
        g2d.setColor(Color.BLACK);
        g2d.drawString(weightStr, midX - strWidth / 2, midY + strHeight / 4);
    }

    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        double arrowLength = 12;
        double arrowAngle = Math.PI / 6;

        double angle = Math.atan2(y2 - y1, x2 - x1);

        int arrowX1 = (int) (x2 - arrowLength * Math.cos(angle - arrowAngle));
        int arrowY1 = (int) (y2 - arrowLength * Math.sin(angle - arrowAngle));
        int arrowX2 = (int) (x2 - arrowLength * Math.cos(angle + arrowAngle));
        int arrowY2 = (int) (y2 - arrowLength * Math.sin(angle + arrowAngle));

        java.awt.Polygon arrow = new java.awt.Polygon();
        arrow.addPoint(x2, y2);
        arrow.addPoint(arrowX1, arrowY1);
        arrow.addPoint(arrowX2, arrowY2);

        g2d.fill(arrow);
    }

    public boolean containsNode(Node node) {
        return from.equals(node) || to.equals(node);
    }

    public Node getOtherNode(Node node) {
        if (from.equals(node)) return to;
        if (to.equals(node)) return from;
        return null;
    }

    // Getters and Setters
    public Node getFrom() { return from; }
    public Node getTo() { return to; }
    public int getWeight() { return weight; }

    public State getState() { return state; }
    public void setState(State state) { this.state = state; }

    @Override
    public String toString() {
        return from.getName() + " -> " + to.getName() + " (" + weight + ")";
    }
}
