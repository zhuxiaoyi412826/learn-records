package dijkstra;

import java.util.*;

/**
 * 图的数据结构
 */
public class Graph {
    private List<Node> nodes;
    private List<Edge> edges;
    private int nextNodeId = 0;

    public Graph() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public Node addNode(String name, int x, int y) {
        Node node = new Node(nextNodeId++, name, x, y);
        nodes.add(node);
        return node;
    }

    public Edge addEdge(Node from, Node to, int weight) {
        return addEdge(from, to, weight, false);
    }

    public Edge addEdge(Node from, Node to, int weight, boolean bidirectional) {
        // 检查是否已存在相同方向的边
        for (Edge e : edges) {
            if (e.getFrom().equals(from) && e.getTo().equals(to)) {
                return null;
            }
        }
        Edge edge = new Edge(from, to, weight);
        edges.add(edge);
        
        if (bidirectional) {
            // 添加反向边
            for (Edge e : edges) {
                if (e.getFrom().equals(to) && e.getTo().equals(from)) {
                    return edge; // 反向边已存在
                }
            }
            Edge reverseEdge = new Edge(to, from, weight);
            edges.add(reverseEdge);
        }
        
        return edge;
    }

    public void removeNode(Node node) {
        // 移除关联的边
        edges.removeIf(e -> e.containsNode(node));
        nodes.remove(node);
    }

    public void removeEdge(Edge edge) {
        edges.remove(edge);
    }

    public void clear() {
        nodes.clear();
        edges.clear();
        nextNodeId = 0;
    }

    public List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getFrom().equals(node)) {
                neighbors.add(edge.getTo());
            }
        }
        return neighbors;
    }

    public Edge getEdge(Node from, Node to) {
        for (Edge edge : edges) {
            if (edge.getFrom().equals(from) && edge.getTo().equals(to)) {
                return edge;
            }
        }
        return null;
    }

    public List<Edge> getEdgesFrom(Node node) {
        List<Edge> result = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getFrom().equals(node)) {
                result.add(edge);
            }
        }
        return result;
    }

    public void resetStates() {
        for (Node node : nodes) {
            node.setState(Node.State.UNVISITED);
            node.setDistance(Integer.MAX_VALUE);
        }
        for (Edge edge : edges) {
            edge.setState(Edge.State.NORMAL);
        }
    }

    // Getters
    public List<Node> getNodes() { return new ArrayList<>(nodes); }
    public List<Edge> getEdges() { return new ArrayList<>(edges); }

    public Node getNodeAt(int x, int y) {
        for (Node node : nodes) {
            if (node.contains(x, y)) {
                return node;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public int getNodeCount() {
        return nodes.size();
    }
}
