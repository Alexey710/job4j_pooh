package ru.job4j.pooh;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {
    private final ConcurrentHashMap<String,
            ConcurrentHashMap<String, ConcurrentLinkedQueue<Resp>>> store
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
        String nameTopic = req.getNameQueue();
        if (store.computeIfPresent(
                nameTopic,
                (key, value) -> {
                    ConcurrentHashMap<String, ConcurrentLinkedQueue<Resp>> subMap
                            = store.get(nameTopic);
                    for (Map.Entry<String, ConcurrentLinkedQueue<Resp>> entry : subMap.entrySet()) {
                        ConcurrentLinkedQueue<Resp> queue = entry.getValue();
                        queue.offer(resp);
                    }
                    return value;
                }
        ) != null) {
            return resp;
        }
        ConcurrentHashMap<String, ConcurrentLinkedQueue<Resp>> map = new ConcurrentHashMap<>();
        store.put(nameTopic, map);
        return resp;
    }

    private Resp whenRequestIsGET(Req req) {
        String nameTopic = req.getNameQueue();
        ConcurrentHashMap<String, ConcurrentLinkedQueue<Resp>> subMap =
        store.computeIfAbsent(nameTopic, key -> new ConcurrentHashMap<String, ConcurrentLinkedQueue<Resp>>());
        /*subscribe2*/
        subMap.computeIfAbsent(req.getID(), key -> new ConcurrentLinkedQueue<Resp>());
        Resp rsl = store.get(nameTopic).get(req.getID()).poll();
        if (rsl == null) {
            String message = String.format(
                    "Dear subscriber ID=%s, PoohServer have not any message for you.", req.getID());
            return new Resp(message, 200);
        }
        return rsl;
    }
}
