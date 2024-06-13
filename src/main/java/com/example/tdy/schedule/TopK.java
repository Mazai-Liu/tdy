package com.example.tdy.schedule;

import com.example.tdy.entity.HotVideo;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.PriorityQueue;


/**
 * @author Mazai-Liu
 * @time 2024/6/13
 */
@Data
public class TopK {
    private int k;
    private PriorityQueue<HotVideo> queue;

    public void add(HotVideo hotVideo) {
        queue.add(hotVideo);
        if (queue.size() > k) {
            queue.remove();
        }
    }

    public TopK(int k) {
        this.k = k;
        this.queue = new PriorityQueue<>(k, (o1, o2) -> {
            return (int)(o1.getHot() - o2.getHot());
        });
    }

    public HotVideo getMin() {
        return queue.peek();
    }
}
