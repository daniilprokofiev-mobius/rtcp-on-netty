package edu.rtcp.server.executor;

import com.mobius.software.common.dal.timers.CountableQueue;
import com.mobius.software.common.dal.timers.PeriodicQueuedTasks;
import com.mobius.software.common.dal.timers.Task;
import com.mobius.software.common.dal.timers.Timer;
import com.mobius.software.common.dal.timers.WorkerPool;
import edu.rtcp.server.executor.tasks.MessageTask;

public class MessageExecutor {
    private WorkerPool workerPool;
    private int workersNumber;

    public void start(int workersNumber, long taskInterval) {
        System.out.println("[EXECUTOR] Started with " + workersNumber + " workers");

        this.workersNumber = workersNumber;
        workerPool = new WorkerPool(taskInterval);
        workerPool.start(workersNumber);
    }

    public void stop() {
        System.out.println("[EXECUTOR] Stopped");
        workerPool.stop();
        workerPool = null;
    }

    public void addTaskFirst(MessageTask task) {
        CountableQueue<Task> queue = getQueue(task.getId());
        if (queue != null) {
            queue.offerFirst(task);
            System.out.println("[EXECUTOR] Task " + task.getId() + " added to BEGIN [Q: " + queue.size() + "]");
        }
    }

    public void addTaskLast(MessageTask task) {
        CountableQueue<Task> queue = this.getQueue(task.getId());

        if (queue != null) {
            queue.offerLast(task);
            System.out.println("[EXECUTOR] Task " + task.getId() + " added to END [Q: " + queue.size() + "]");
        }
    }

    private CountableQueue<Task> getQueue(String id) {
        int index = findQueueIndex(id);

        return workerPool.getLocalQueue(index);
    }

    public int findQueueIndex(String id) {
        return Math.abs(id.hashCode()) % workersNumber;
    }

    public PeriodicQueuedTasks<Timer> getPeriodicQueue() {
        return workerPool.getPeriodicQueue();
    }
}