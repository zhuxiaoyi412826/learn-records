package dijkstra;

import java.util.*;

/**
 * Dijkstra算法动画控制器
 * 将算法执行过程拆解为一系列可逐步展示的步骤
 */
public class DijkstraAnimator {

    /**
     * 算法步骤类型
     */
    public enum StepType {
        INIT("初始化", "设置起点距离为0，其他节点距离为∞"),
        SELECT_NODE("选择节点", "从优先队列中取出距离最小的节点"),
        CHECK_EDGE("检查边", "考察当前节点到邻居的边"),
        RELAX("松弛操作", "更新邻居的最短距离"),
        MARK_VISITED("标记已访问", "当前节点最短路径已确定"),
        FINISH("算法结束", "所有可达节点已处理完毕");

        private final String title;
        private final String description;

        StepType(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
    }

    /**
     * 单步记录
     */
    public static class Step {
        private StepType type;
        private Node currentNode;
        private Edge currentEdge;
        private Node neighborNode;
        private int oldDistance;
        private int newDistance;
        private String message;
        private Map<Node, Integer> distanceSnapshot;

        public Step(StepType type, Node currentNode, String message) {
            this.type = type;
            this.currentNode = currentNode;
            this.message = message;
            this.distanceSnapshot = new HashMap<>();
        }

        // Getters
        public StepType getType() { return type; }
        public Node getCurrentNode() { return currentNode; }
        public Edge getCurrentEdge() { return currentEdge; }
        public Node getNeighborNode() { return neighborNode; }
        public String getMessage() { return message; }
        public Map<Node, Integer> getDistanceSnapshot() { return distanceSnapshot; }
        public int getOldDistance() { return oldDistance; }
        public int getNewDistance() { return newDistance; }

        public void setCurrentEdge(Edge edge) { this.currentEdge = edge; }
        public void setNeighborNode(Node node) { this.neighborNode = node; }
        public void setOldDistance(int d) { this.oldDistance = d; }
        public void setNewDistance(int d) { this.newDistance = d; }
        public void setDistanceSnapshot(Map<Node, Integer> snapshot) { this.distanceSnapshot = snapshot; }
    }

    private Graph graph;
    private List<Step> steps;
    private int currentStepIndex = -1;
    private boolean isRunning = false;
    private Thread animationThread;
    private int delayMs = 1000; // 每步间隔毫秒
    private List<AnimationListener> listeners = new ArrayList<>();

    public DijkstraAnimator(Graph graph) {
        this.graph = graph;
        this.steps = new ArrayList<>();
    }

    /**
     * 预计算所有步骤
     */
    public void prepareAnimation(Node startNode) {
        steps.clear();
        currentStepIndex = -1;
        graph.resetStates();

        // 初始化距离
        Map<Node, Integer> dist = new HashMap<>();
        Map<Node, Node> prev = new HashMap<>();
        for (Node node : graph.getNodes()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(startNode, 0);

        // 创建初始化步骤
        Step initStep = new Step(StepType.INIT, startNode,
            "初始化：起点 " + startNode.getName() + " 的距离设为0");
        initStep.setDistanceSnapshot(new HashMap<>(dist));
        steps.add(initStep);

        // 优先队列
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));
        Set<Node> visited = new HashSet<>();

        for (Node node : graph.getNodes()) {
            pq.offer(node);
        }

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            // 跳过已访问或不可达的节点
            if (visited.contains(current) || dist.get(current) == Integer.MAX_VALUE) {
                continue;
            }

            // 选择节点步骤
            Step selectStep = new Step(StepType.SELECT_NODE, current,
                "选择节点 " + current.getName() + "，当前距离 = " + dist.get(current));
            selectStep.setDistanceSnapshot(new HashMap<>(dist));
            steps.add(selectStep);

            // 检查所有出边
            for (Edge edge : graph.getEdgesFrom(current)) {
                Node neighbor = edge.getTo();
                if (visited.contains(neighbor)) continue;

                int newDist = dist.get(current) + edge.getWeight();

                // 检查边步骤
                Step checkStep = new Step(StepType.CHECK_EDGE, current,
                    "检查边 " + current.getName() + " -> " + neighbor.getName() +
                    "，权重 = " + edge.getWeight());
                checkStep.setCurrentEdge(edge);
                checkStep.setNeighborNode(neighbor);
                checkStep.setDistanceSnapshot(new HashMap<>(dist));
                steps.add(checkStep);

                if (newDist < dist.get(neighbor)) {
                    int oldDist = dist.get(neighbor);
                    dist.put(neighbor, newDist);
                    prev.put(neighbor, current);

                    // 松弛步骤
                    Step relaxStep = new Step(StepType.RELAX, current,
                        "松弛成功！更新 " + neighbor.getName() + " 的距离：" +
                        (oldDist == Integer.MAX_VALUE ? "∞" : oldDist) + " -> " + newDist);
                    relaxStep.setCurrentEdge(edge);
                    relaxStep.setNeighborNode(neighbor);
                    relaxStep.setOldDistance(oldDist);
                    relaxStep.setNewDistance(newDist);
                    relaxStep.setDistanceSnapshot(new HashMap<>(dist));
                    steps.add(relaxStep);

                    // 更新优先队列
                    pq.remove(neighbor);
                    pq.offer(neighbor);
                }
            }

            visited.add(current);

            // 标记已访问步骤
            Step visitStep = new Step(StepType.MARK_VISITED, current,
                "节点 " + current.getName() + " 的最短路径已确定，距离 = " + dist.get(current));
            visitStep.setDistanceSnapshot(new HashMap<>(dist));
            steps.add(visitStep);
        }

        // 结束步骤
        Step finishStep = new Step(StepType.FINISH, null,
            "算法执行完毕！所有可达节点的最短路径已计算完成。");
        finishStep.setDistanceSnapshot(new HashMap<>(dist));
        steps.add(finishStep);
    }

    /**
     * 执行单步
     */
    public void stepForward() {
        if (currentStepIndex < steps.size() - 1) {
            currentStepIndex++;
            applyStep(steps.get(currentStepIndex));
            notifyListeners();
        }
    }

    /**
     * 回退一步
     */
    public void stepBackward() {
        if (currentStepIndex > 0) {
            // 重置并重新应用到前一步
            currentStepIndex--;
            graph.resetStates();
            for (int i = 0; i <= currentStepIndex; i++) {
                applyStep(steps.get(i));
            }
            notifyListeners();
        }
    }

    /**
     * 应用步骤到图形状态
     */
    private void applyStep(Step step) {
        switch (step.getType()) {
            case INIT:
                if (step.getCurrentNode() != null) {
                    step.getCurrentNode().setDistance(0);
                    step.getCurrentNode().setStart(true);
                }
                break;
            case SELECT_NODE:
                if (step.getCurrentNode() != null) {
                    step.getCurrentNode().setState(Node.State.CURRENT);
                }
                break;
            case CHECK_EDGE:
                if (step.getCurrentEdge() != null) {
                    step.getCurrentEdge().setState(Edge.State.HIGHLIGHTED);
                }
                break;
            case RELAX:
                if (step.getCurrentEdge() != null) {
                    step.getCurrentEdge().setState(Edge.State.RELAXED);
                }
                if (step.getNeighborNode() != null) {
                    step.getNeighborNode().setDistance(step.getNewDistance());
                }
                break;
            case MARK_VISITED:
                if (step.getCurrentNode() != null) {
                    step.getCurrentNode().setState(Node.State.VISITED);
                }
                break;
            case FINISH:
                break;
        }
    }

    /**
     * 开始自动播放动画
     */
    public void startAnimation() {
        if (isRunning) return;
        isRunning = true;
        animationThread = new Thread(() -> {
            while (isRunning && currentStepIndex < steps.size() - 1) {
                stepForward();
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    break;
                }
            }
            isRunning = false;
            notifyListeners();
        });
        animationThread.start();
    }

    /**
     * 暂停动画
     */
    public void pauseAnimation() {
        isRunning = false;
        if (animationThread != null) {
            animationThread.interrupt();
        }
    }

    /**
     * 重置动画
     */
    public void reset() {
        pauseAnimation();
        currentStepIndex = -1;
        graph.resetStates();
        notifyListeners();
    }

    /**
     * 跳转到指定步骤
     */
    public void jumpToStep(int index) {
        if (index < 0 || index >= steps.size()) return;
        pauseAnimation();
        graph.resetStates();
        currentStepIndex = index;
        for (int i = 0; i <= index; i++) {
            applyStep(steps.get(i));
        }
        notifyListeners();
    }

    // Getters
    public boolean isRunning() { return isRunning; }
    public int getCurrentStepIndex() { return currentStepIndex; }
    public int getTotalSteps() { return steps.size(); }
    public Step getCurrentStep() {
        if (currentStepIndex >= 0 && currentStepIndex < steps.size()) {
            return steps.get(currentStepIndex);
        }
        return null;
    }
    public List<Step> getSteps() { return new ArrayList<>(steps); }

    public void setDelay(int delayMs) {
        this.delayMs = Math.max(100, Math.min(5000, delayMs));
    }

    public int getDelay() { return delayMs; }

    // 监听器
    public interface AnimationListener {
        void onStepChanged(DijkstraAnimator animator);
    }

    public void addListener(AnimationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AnimationListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (AnimationListener listener : listeners) {
            listener.onStepChanged(this);
        }
    }
}
