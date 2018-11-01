package lib;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.lettuce.core.api.sync.RedisCommands;

@Service
public class RedisService {
    @Autowired
    RedisCommands<String, String> syncCommands;

    public void addMessage(Message message) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(message);

        syncCommands.set("message:" + message.id, jsonString);

        Long epoch = Instant.parse(message.date).getEpochSecond();
        syncCommands.zadd(message.room, epoch.doubleValue(), message.id);
    }

    public List<Message> getMessages(String room) throws Exception {
        List<String> ids = syncCommands.zrange(room, 0, -1);
        List<Message> messages = new ArrayList<Message>(ids.size());
        for (String id : ids) {
            try {
                String jsonString = syncCommands.get("message:" + id);
                ObjectMapper objectMapper = new ObjectMapper();
                Message message = objectMapper.readValue(jsonString, Message.class);
                messages.add(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return messages;
    }
}
