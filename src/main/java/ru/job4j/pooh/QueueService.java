package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import static ru.job4j.pooh.Requests.GET;
import static ru.job4j.pooh.Requests.POST;

public class QueueService implements Service {
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Resp>> store
            = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        Resp rsl = null;
        if (POST.getValue().equals(req.typeRequest())) {
            rsl = post(req);
        }
        if (GET.getValue().equals(req.typeRequest())) {
            rsl = get(req);
        }
        return rsl;
    }

    private Resp post(Req req) {
        String text = req.getText();
        Resp resp = new Resp(text, 200);
        String nameQueue = req.fetch();
        store.computeIfAbsent(nameQueue, key -> new ConcurrentLinkedQueue<Resp>());
        ConcurrentLinkedQueue<Resp> queue = store.get(nameQueue);
        queue.offer(resp);
        return resp;
    }

    private Resp get(Req req) {
        String nameQueue = req.fetch();
        store.computeIfAbsent(req.fetch(), key -> new ConcurrentLinkedQueue<Resp>());
        Resp resp = store.get(nameQueue).poll();
        if (resp == null) {
            return new Resp("Dear subscriber, PoohServer have not any message for you.", 200);
        }
        return resp;
    }
}