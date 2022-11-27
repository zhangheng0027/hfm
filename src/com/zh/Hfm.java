package com.zh;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class Hfm {


    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < 8; i++) {
            map.put("" + i, i * i);
        }
        Map<String, Integer> result = buildHFMTree(map);

        System.out.println(result);

        result.forEach((k, v) -> {

            // 转成二进制输出
            System.out.println(k + " : " + Integer.toString(v, 2));

        });
    }

    /**
     * 构建并返回哈夫曼编码
     * @param map
     */
    public static Map<String, Integer> buildHFMTree(Map<String, Integer> map) {

        List<Node> ln = new ArrayList<>(map.size());

        map.forEach((k, v) -> ln.add(new Node(k, v)));

        // 排序
        ln.sort(Comparator.comparing(n -> n.count));

        Queue<Node> q1 = new ArrayBlockingQueue<>(ln.size());
        Queue<Node> q2 = new ArrayBlockingQueue<>(ln.size());

        // 加到队列中
        ln.forEach(e -> q1.add(e));
        _buildHFMTree(q1, q2, map.size());

        // 取出构建后的根节点
        Node root = pollMinNode(q1, q2);

        Map<String, Integer> result = new HashMap<>(map.size() << 1);
        printHFM(result, root, 1);

        return result;


    }


    /**
     * 将哈夫曼树转成编码，
     * 遵循一个原则，出现次数少的补 1， 出现次数多的 0。 原因是在传输或者存储的时候，1对应高电平，出现的越多，耗电越多。
     * 如果不考虑这个，实际上用 1 填充或者用 0 填充没有影响，我印象中书里好像没有提，当时就很疑惑。
     * @param map
     * @param root
     * @param a
     */
    public static void printHFM(Map<String, Integer> map, Node root, int a) {

        if (null == root.left) {
            map.put(root.context, a);
            return;
        }

        printHFM(map, root.left, (a << 1) + 1);
        printHFM(map, root.right, (a << 1));

    }

    /**
     * 构建哈夫曼树
     * @param q1 左边队列
     * @param q2 右边队列
     * @param a  队列总大小
     */
    public static void _buildHFMTree(Queue<Node> q1, Queue<Node> q2, int a) {
        if (a <= 1)
            return;

        // 左边的队列空了, 调换左右队列顺序
        if (q1.size() == 0) {
            _buildHFMTree(q2, q1, a);
            return;
        }

        // 先出队列的是出现次数少的
        Node n1 = pollMinNode(q1, q2);
        // 后出队列的时出现次数多的
        Node n2 = pollMinNode(q1, q2);

        // 构建父节点
        // 出现次数少的在父节点的左边，出现次数多的在父节点右边
        Node parentNode = new Node(n1, n2);

        // 将父节点加到右边队列
        q2.add(parentNode);

        // 重复操作
        _buildHFMTree(q1, q2, a - 1);
    }

    /**
     * 从两个队列中弹出小的节点
     * @param q1
     * @param q2
     * @return
     */
    public static Node pollMinNode(Queue<Node> q1, Queue<Node> q2) {
        if (q1.size() == 0)
            return q2.poll();
        if (q2.size() == 0)
            return q1.poll();

        return q1.peek().count > q2.peek().count ? q2.poll() : q1.poll();
    }


    static class Node {
        String context;
        Integer count;

        Node left;
        Node right;

        Node(String con, int cou) {
            this.context = con;
            this.count = cou;
        }


        Node(Node left, Node right) {
            this.left = left;
            this.right = right;
            // 父节点出现的次数是两个子节点出现次数之和
            this.count = left.count + right.count;
        }
    }

}
