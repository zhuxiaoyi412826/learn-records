package dijkstra;

import javax.swing.*;
import java.awt.*;

/**
 * Dijkstra算法动画演示程序入口
 *
 * 功能说明：
 * - 支持可视化编辑有向加权图（添加/删除节点和边）
 * - 支持设置起点和终点
 * - 逐步动画展示Dijkstra算法执行过程
 * - 显示距离表更新、当前步骤说明和执行日志
 * - 支持自动播放、单步执行、回退和重置
 */
public class Main {

    public static void main(String[] args) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 在事件调度线程中创建GUI
        SwingUtilities.invokeLater(() -> {
            DijkstraVisualizer visualizer = new DijkstraVisualizer();
            visualizer.setVisible(true);
        });
    }
}
