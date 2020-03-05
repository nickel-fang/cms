package cn.people.cms.modules.file.message;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MessageReceiver {
    public static final int WAIT_SECONDS = 30;

    protected static final Map<String, Object> sLockObjMap = new HashMap<String, Object>();
    protected static Map<String, Boolean> sPollingMap = new ConcurrentHashMap<String, Boolean>();

    protected Object lockObj;
    protected String queueName;
    protected CloudQueue cloudQueue;
    protected int workerId;

    public MessageReceiver(int id, MNSClient mnsClient, String queue) {
        cloudQueue = mnsClient.getQueueRef(queue);
        queueName = queue;
        workerId = id;

        synchronized (sLockObjMap) {
            lockObj = sLockObjMap.get(queueName);
            if (lockObj == null) {
                lockObj = new Object();
                sLockObjMap.put(queueName, lockObj);
            }
        }
    }

    public boolean setPolling() {
        synchronized (lockObj) {
            Boolean ret = sPollingMap.get(queueName);
            if (ret == null || !ret) {
                sPollingMap.put(queueName, true);
                return true;
            }
            return false;
        }
    }

    public void clearPolling() {
        synchronized (lockObj) {
            sPollingMap.put(queueName, false);
            lockObj.notifyAll();
            log.info("Everyone WakeUp and Work!");
        }
    }

    public Message receiveMessage() {
        boolean polling = false;
        while (true) {
            synchronized (lockObj) {
                Boolean p = sPollingMap.get(queueName);
                if (p != null && p) {
                    try {
                        log.info("Thread" + workerId + " Have a nice sleep!");
                        polling = false;
                        lockObj.wait();
                    } catch (InterruptedException e) {
                        log.error("MessageReceiver Interrupted! QueueName is " + queueName);
                        return null;
                    }
                }
            }

            try {
                Message message = null;
                if (!polling) {
                    message = cloudQueue.popMessage();
                    if (message == null) {
                        polling = true;
                        continue;
                    }
                } else {
                    if (setPolling()) {
                        log.info("Thread" + workerId + " Polling!");
                    } else {
                        continue;
                    }
                    do {
                        log.info("Thread" + workerId + " KEEP Polling!");
                        try {
                            message = cloudQueue.popMessage(WAIT_SECONDS);
                        } catch(Exception e) {
                            log.error("Exception Happened when polling popMessage: " + e);
                        }
                    } while (message == null);
                    clearPolling();
                }
                return message;
            } catch (Exception e) {
                log.error("Exception Happened when popMessage: " + e);
            }
        }
    }
}
