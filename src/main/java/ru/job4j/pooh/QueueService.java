package ru.job4j.pooh;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Resp>> store
            = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        Resp rsl = null;
        if (req.typeRequest().equals("POST")) {
            rsl = whenRequestIsPOST(req);
        }
        if (req.typeRequest().equals("GET")) {
            rsl = whenRequestIsGET(req);
        }
        return rsl;
    }

    private Resp whenRequestIsPOST(Req req) {
        String text = req.getText();
        Resp resp = new Resp(text, 200);
        String nameQueue = req.getNameQueue();
        if (store.computeIfPresent(
                nameQueue,
                (key, value) -> {
                    ConcurrentLinkedQueue<Resp> queue = store.get(nameQueue);
                    queue.offer(resp);
                    return queue;
                }
        ) != null) {
            return resp;
        }
        ConcurrentLinkedQueue<Resp> queue = new ConcurrentLinkedQueue<>();
        store.put(nameQueue, queue);
        queue.offer(resp);
        return resp;
    }

    private Resp whenRequestIsGET(Req req) {
        String nameQueue = req.getNameQueue();
        store.computeIfAbsent(req.getNameQueue(), key -> new ConcurrentLinkedQueue<>());
        Resp resp = store.get(nameQueue).poll();
        if (resp == null) {
            return new Resp("Dear subscriber, PoohServer have not any message for you.", 200);
        }
        return resp;
    }
}