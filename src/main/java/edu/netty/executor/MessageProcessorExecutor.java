package edu.netty.executor;

import com.mobius.software.common.dal.timers.CountableQueue;
import com.mobius.software.common.dal.timers.PeriodicQueuedTasks;
import com.mobius.software.common.dal.timers.Task;
import com.mobius.software.common.dal.timers.Timer;
import com.mobius.software.common.dal.timers.WorkerPool;
import edu.netty.task.MessageProcessingTask;

public class MessageProcessorExecutor implements ProcessorExecutor {
    private WorkerPool workerPool;
    private int workersNumber;

    public void start(int workersNumber, long taskInterval) {
        System.out.println("MessageProcessorExecutor started with " + workersNumber + " workers");

        this.workersNumber = workersNumber;
        workerPool = new WorkerPool(taskInterval);
        workerPool.start(workersNumber);
    }

    public void stop() {
        System.out.println("MessageProcessorExecutor stopped");
        workerPool.stop();
        workerPool = null;
    }

    public void addTaskFirst(MessageProcessingTask task) {
        CountableQueue<Task> queue = getQueue(task.getId());
        if (queue != null) {
            System.out.println("Adding task to begin of executor");
            queue.offerFirst(task);
            System.out.println("Queue size: " + queue.size());
        }
    }

    public void addTaskLast(MessageProcessingTask task) {
        CountableQueue<Task> queue = getQueue(task.getId());
        if (queue != null) {
            System.out.println("Adding task to last of executor");
            queue.offerLast(task);
            System.out.println("Queue size: " + queue.size());
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